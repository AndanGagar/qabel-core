package de.qabel.core.http

import de.qabel.core.config.Contact
import de.qabel.core.config.Identities
import de.qabel.core.config.Identity
import de.qabel.core.crypto.BinaryDropMessageV0
import de.qabel.core.drop.DefaultDropParser
import de.qabel.core.drop.DropMessage
import de.qabel.core.drop.DropParser
import de.qabel.core.drop.DropURL
import de.qabel.core.exceptions.QblDropInvalidMessageSizeException
import de.qabel.core.exceptions.QblSpoofedSenderException
import de.qabel.core.exceptions.QblVersionMismatchException
import de.qabel.core.http.DropServerHttp.DropServerResponse
import de.qabel.core.repository.entities.DropState
import org.slf4j.LoggerFactory

class MainDropConnector(val dropServer: DropServerHttp):
    DropConnector,
    DropParser by DefaultDropParser() {

    companion object {
        private val logger = LoggerFactory.getLogger(MainDropConnector::class.java)
    }

    override fun sendDropMessage(identity: Identity, contact: Contact,
                                 message: DropMessage, server: DropURL) {
        val messageBytes = BinaryDropMessageV0(message)
            .assembleMessageFor(contact, identity)
        dropServer.sendBytes(server.uri, messageBytes)
    }

    override fun receiveDropMessages(identity: Identity, dropUrl: DropURL, dropState: DropState): DropServerResponse<DropMessage> {
        val dropResult = dropServer.receiveMessageBytes(dropUrl.uri, dropState.eTag)
        if (!dropResult.second.isEmpty()) {
            dropState.eTag = dropResult.second
        }
        val receivers = Identities().apply { put(identity) }
        val messages = dropResult.third.map { byteMessage ->
            try {
                parse(byteMessage, receivers).second
            } catch (e: QblVersionMismatchException) {
                logger.warn("Received DropMessage with version mismatch")
                throw RuntimeException("Version mismatch should not happen", e);
            } catch (e: QblDropInvalidMessageSizeException) {
                // Invalid message uploads may happen with malicious intent
                // or by broken clients. Skip.
                logger.warn("Received DropMessage with invalid size")
                null
            } catch(ex: QblSpoofedSenderException) {
                logger.warn("QblSpoofedSenderException while disassembling message")
                null
            }
        }.filterNotNull()
        return DropServerResponse(dropResult.component1(), dropState, messages)
    }

}
