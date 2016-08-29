package de.qabel.chat.repository.sqlite.schemas

import de.qabel.core.repository.EntityManager
import de.qabel.chat.repository.entities.ChatDropMessage
import de.qabel.chat.repository.entities.ChatDropMessage.*
import de.qabel.core.repository.framework.DBField
import de.qabel.core.repository.framework.DBRelation
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp

object ChatDropMessageDB : DBRelation<ChatDropMessage> {

    override val TABLE_NAME = "chat_drop_message"
    override val TABLE_ALIAS = "cdm"

    override val ID: DBField = field("id")
    val IDENTITY_ID: DBField = field("identity_id")
    val CONTACT_ID = field("contact_id")

    val DIRECTION = field("direction")
    val STATUS = field("status")

    val PAYLOAD_TYPE = field("payload_type")
    val PAYLOAD = field("payload")

    val CREATED_ON = field("created_on")

    override val ENTITY_FIELDS = listOf(CONTACT_ID, IDENTITY_ID, DIRECTION, STATUS,
        PAYLOAD_TYPE, PAYLOAD, CREATED_ON)

    override val ENTITY_CLASS: Class<ChatDropMessage> = ChatDropMessage::class.java

    override fun applyValues(startIndex: Int, statement: PreparedStatement, model: ChatDropMessage): Int =
        with(statement) {
            var i = startIndex
            setInt(i++, model.contactId)
            setInt(i++, model.identityId)
            setByte(i++, model.direction.type)
            setInt(i++, model.status.type)
            setString(i++, model.messageType.type)
            setString(i++, model.payload.toString())
            setTimestamp(i++, Timestamp(model.createdOn))
            return i
        }

    override fun hydrateOne(resultSet: ResultSet, entityManager: EntityManager): ChatDropMessage {
        val id = resultSet.getInt(ID.alias())
        if (entityManager.contains(ENTITY_CLASS, id)) {
            return entityManager.get(ENTITY_CLASS, id)
        }
        val payloadType = toEnum(MessageType.values(), resultSet.getString(PAYLOAD_TYPE.alias())!!, { it.type })
        val payloadString = resultSet.getString(PAYLOAD.alias())
        val payload = MessagePayload.fromString(payloadType, payloadString)
        if(payload is MessagePayload.ShareMessage){
            payload.shareData = ChatShareDB.hydrateOne(resultSet, entityManager)
        }
        return ChatDropMessage(resultSet.getInt(CONTACT_ID.alias()),
            resultSet.getInt(IDENTITY_ID.alias()),
            toEnum(Direction.values(), resultSet.getByte(DIRECTION.alias()), { it.type }),
            toEnum(Status.values(), resultSet.getInt(STATUS.alias()), { it.type }),
            payloadType,
            payload,
            resultSet.getTimestamp(CREATED_ON.alias()).time,
            id)
    }

    fun <X : Enum<X>, S : Any> toEnum(enum: Array<X>, value: S, extract: (enum: X) -> S) =
        enum.find {
            extract(it).equals(value)
        } ?: throw RuntimeException("Invalid enum value found")

}
