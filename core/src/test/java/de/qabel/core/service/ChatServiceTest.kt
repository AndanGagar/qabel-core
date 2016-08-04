package de.qabel.core.service

import de.qabel.core.config.Contact
import de.qabel.core.config.Identity
import de.qabel.core.config.factory.DropUrlGenerator
import de.qabel.core.crypto.QblECKeyPair
import de.qabel.core.drop.DropURL
import de.qabel.core.http.MainDropConnector
import de.qabel.core.http.MockDropServer
import de.qabel.core.repository.entities.ChatDropMessage
import de.qabel.core.repository.inmemory.InMemoryChatDropMessageRepository
import de.qabel.core.repository.inmemory.InMemoryContactRepository
import de.qabel.core.repository.inmemory.InMemoryDropStateRepository
import de.qabel.core.repository.inmemory.InMemoryIdentityRepository
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class ChatServiceTest {

    val dropGenerator = DropUrlGenerator("http://localhost:5000")
    val dropUrl = DropURL("http://192.168.50.42:5000/abcdefghijklmnopqrstuvwxyzabcdefgworkingUrl")
    lateinit var identityA: Identity
    lateinit var contactA: Contact
    lateinit var identityB: Identity
    lateinit var contactB: Contact

    private fun createTextPayload(text: String) = "{\"msg\": \"$text\"}"

    val identityRepository = InMemoryIdentityRepository()
    val contactRepository = InMemoryContactRepository()
    val chatDropRepo = InMemoryChatDropMessageRepository()
    val chatService = MainChatService(MainDropConnector(MockDropServer()),
        identityRepository, contactRepository,
        chatDropRepo, InMemoryDropStateRepository())

    @Before
    fun setUp() {
        identityA = Identity("IdentityA", listOf(dropGenerator.generateUrl()), QblECKeyPair())
        contactA = identityA.toContact()
        identityB = Identity("IdentityB", listOf(dropUrl), QblECKeyPair())
        contactB = identityB.toContact()

        identityRepository.save(identityA)
        identityRepository.save(identityB)

        contactRepository.save(contactA, identityB)
        contactRepository.save(contactB, identityA)
    }

    private fun createMessage(identity: Identity, contact: Contact, text: String) =
        ChatDropMessage(contact.id,
            identity.id,
            ChatDropMessage.Direction.OUTGOING,
            ChatDropMessage.Status.PENDING,
            ChatDropMessage.MessageType.BOX_MESSAGE,
            createTextPayload(text), System.currentTimeMillis())

    @Test
    fun testSend() {
        val message = createMessage(identityA, contactB, "Blub blub")

        chatService.sendMessage(message)

        assertThat(message.status, equalTo(ChatDropMessage.Status.SENT))

        val result = chatService.refreshMessages()
        assertThat(result.keys, hasSize(1))
        assertThat(result.keys.first().keyIdentifier, equalTo(identityB.keyIdentifier))

        assertThat(result.values.first(), hasSize(1))
        val received = result.values.first().first();
        assertThat(received.contactId, equalTo(identityA.id))
        assertThat(ChatDropMessage.MessagePayload.encode(received.messageType, received.payload),
            equalTo(ChatDropMessage.MessagePayload.encode(message.messageType, message.payload)))
        assertThat(received.messageType, equalTo(message.messageType))
        assertThat(received.status, equalTo(ChatDropMessage.Status.NEW))
    }

    @Test
    fun testReceiveMessages() {
        val message = createMessage(identityA, contactB, "Blub blub blubb")
        val messages = listOf(createMessage(identityA, contactB, "Blub blub"), message,
            createMessage(identityA, contactB, "Blub blub blubb blubb"));

        val stored = message.copy(contactId = contactA.id, identityId = contactB.id,
            direction = ChatDropMessage.Direction.INCOMING, status = ChatDropMessage.Status.NEW)

        chatDropRepo.persist(stored)
        messages.forEach { chatService.sendMessage(it) }

        val result = chatService.refreshMessages()
        assertThat(result.keys, hasSize(1))
        assertThat(result.keys.first().keyIdentifier, equalTo(identityB.keyIdentifier))

        assertThat(result.values.first(), hasSize(2))
    }
}
