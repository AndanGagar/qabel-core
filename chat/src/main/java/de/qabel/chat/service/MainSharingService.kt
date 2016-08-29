package de.qabel.chat.service

import de.qabel.box.storage.*
import de.qabel.box.storage.exceptions.QblStorageException
import de.qabel.box.storage.exceptions.QblStorageNotFound
import de.qabel.chat.repository.ChatShareRepository
import de.qabel.chat.repository.entities.BoxFileChatShare
import de.qabel.chat.repository.entities.ChatDropMessage
import de.qabel.chat.repository.entities.ShareStatus
import de.qabel.core.config.Contact
import de.qabel.core.config.Identity
import de.qabel.core.config.SymmetricKey
import de.qabel.core.crypto.CryptoUtils
import de.qabel.core.repository.ContactRepository
import org.spongycastle.crypto.params.KeyParameter
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.security.InvalidKeyException

class MainSharingService(private val chatShareRepository: ChatShareRepository,
                         private val contactRepository: ContactRepository,
                         private val boxReadBackend: StorageReadBackend,
                         private val cryptoUtils: CryptoUtils = CryptoUtils()) : SharingService {

    override fun getOrCreateFileShare(identity: Identity, contact: Contact,
                                      boxFile: BoxFile, boxNavigation: BoxNavigation): BoxFileChatShare =
        (boxNavigation.getSharesOf(boxFile).find { it.recipient == contact.keyIdentifier }?.let {
            BoxExternalReference(
                false,
                boxReadBackend.getUrl(boxFile.meta),
                boxFile.name,
                identity.ecPublicKey,
                boxFile.metakey)
        } ?: boxNavigation.share(identity.ecPublicKey, boxFile, contact.keyIdentifier)).let {
            chatShareRepository.findByBoxReference(identity, it.url, it.key) ?:
                createNewBoxFileShare(it, boxFile, identity, ShareStatus.CREATED).apply {
                    chatShareRepository.persist(this)
                }
        }

    override fun receiveShare(identity: Identity, message: ChatDropMessage, payload: ChatDropMessage.MessagePayload.ShareMessage) {
        val share = chatShareRepository.findByBoxReference(identity, payload.shareData.metaUrl, payload.shareData.metaKey.byteList.toByteArray()) ?:
            payload.shareData.apply {
                chatShareRepository.persist(payload.shareData)
            }
        chatShareRepository.connectWithMessage(message, share)
        payload.shareData = share
    }

    override fun addMessageToShare(share: BoxFileChatShare, chatDropMessage: ChatDropMessage) =
        chatShareRepository.connectWithMessage(chatDropMessage, share)

    override fun markShareSent(share: BoxFileChatShare) = share.apply {
        if (share.status == ShareStatus.CREATED) {
            share.status = ShareStatus.SENT
            chatShareRepository.update(share)
        }
    }

    override fun revokeFileShare(contact: Contact, share: BoxFileChatShare,
                                 boxFile: BoxFile, boxNavigation: BoxNavigation) {
        boxNavigation.unshare(boxFile)
        share.status = ShareStatus.DELETED
        chatShareRepository.update(share)
    }

    override fun acceptShare(chatDropMessage: ChatDropMessage, boxNavigation: BoxNavigation): BoxExternalFile {
        val share = chatShareRepository.findByMessage(chatDropMessage)
        share.status = ShareStatus.ACCEPTED
        return refreshShare(share, boxNavigation)
    }

    @Throws(IOException::class, InvalidKeyException::class, QblStorageException::class)
    override fun downloadShare(share: BoxFileChatShare, targetFile: File, boxNavigation: BoxNavigation) {
        try {
            if (share.key == null) {
                refreshShare(share, boxNavigation)
            }
            val rootUri = URI(share.metaUrl)
            val url = rootUri.resolve("blocks/").resolve(share.block).toString()
            boxReadBackend.download(url, null).use { download ->
                cryptoUtils.decryptFileAuthenticatedSymmetricAndValidateTag(download.inputStream, targetFile,
                    KeyParameter(share.key!!.byteList.toByteArray()))
            }
        } catch (e: URISyntaxException) {
            throw QblStorageException("no valid uri: " + share.metaUrl)
        }
    }

    override fun refreshShare(share: BoxFileChatShare, boxNavigation: BoxNavigation): BoxExternalFile =
        tryRefreshShare(share, boxNavigation) ?: throw QblStorageException("ExternalRef not accessible")

    private fun tryRefreshShare(share: BoxFileChatShare, boxNavigation: BoxNavigation): BoxExternalFile? {
        val boxExternalFile = try {
            val fileMetadata = boxNavigation.getMetadataFile(Share(share.metaUrl, share.metaKey.byteList.toByteArray()))
            val fileRef = fileMetadata.file!! //TODO
            applyBoxFile(share, fileRef)
            fileRef
        } catch (deleted: QblStorageNotFound) {
            share.status = ShareStatus.DELETED
            null
        } catch (notReachable: QblStorageException) {
            share.status = ShareStatus.UNREACHABLE
            null
        }
        chatShareRepository.update(share)
        return boxExternalFile
    }

    private fun applyBoxFile(share: BoxFileChatShare, boxFile: BoxFile) {
        share.name = boxFile.name
        share.size = boxFile.size
        share.hashed = boxFile.hashed
        share.prefix = boxFile.prefix
        share.modifiedOn = boxFile.mtime
        share.key = SymmetricKey.Factory.fromBytes(boxFile.key)
        share.block = boxFile.block
    }

    private fun createNewBoxFileShare(boxFileRef: BoxExternalReference, boxFile: BoxFile,
                                      identity: Identity, status: ShareStatus): BoxFileChatShare =
        BoxFileChatShare(status, boxFileRef.name, boxFile.size, SymmetricKey.Factory.fromBytes(boxFileRef.key),
            boxFileRef.url, boxFile.hashed, boxFile.prefix, boxFile.mtime, SymmetricKey(boxFile.key.toList()),
            boxFile.block, contactRepository.findByKeyId(identity.keyIdentifier).id,
            identity.id)
}
