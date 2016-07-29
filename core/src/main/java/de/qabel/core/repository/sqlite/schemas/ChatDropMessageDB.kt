package de.qabel.core.repository.sqlite.schemas

import de.qabel.core.repository.EntityManager
import de.qabel.core.repository.entities.ChatDropMessage
import de.qabel.core.repository.entities.ChatDropMessage.*
import de.qabel.core.repository.framework.DBField
import de.qabel.core.repository.framework.DBRelation
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp

object ChatDropMessageDB : DBRelation<ChatDropMessage> {

    override val TABLE_NAME = "chat_drop_message"
    override val TABLE_ALIAS = "cdm";

    override val ID: DBField = DBField("id", TABLE_NAME, TABLE_ALIAS);
    val IDENTITY_ID: DBField = DBField("identity_id", TABLE_NAME, TABLE_ALIAS);
    val CONTACT_ID = DBField("contact_id", TABLE_NAME, TABLE_ALIAS);

    val DIRECTION = DBField("direction", TABLE_NAME, TABLE_ALIAS)
    val STATUS = DBField("status", TABLE_NAME, TABLE_ALIAS);

    val PAYLOAD_TYPE = DBField("payload_type", TABLE_NAME, TABLE_ALIAS);
    val PAYLOAD = DBField("payload", TABLE_NAME, TABLE_ALIAS);

    val CREATED_ON = DBField("created_on", TABLE_NAME, TABLE_ALIAS);

    override val ENTITY_FIELDS = listOf(CONTACT_ID, IDENTITY_ID, DIRECTION, STATUS,
        PAYLOAD_TYPE, PAYLOAD, CREATED_ON)

    override val ENTITY_CLASS: Class<ChatDropMessage> = ChatDropMessage::class.java

    override fun applyValues(startIndex: Int, statement: PreparedStatement, model: ChatDropMessage) : Int =
        with(statement) {
            var i = startIndex;
            setInt(i++, model.contactId)
            setInt(i++, model.identityId)
            setByte(i++, model.direction.type)
            setInt(i++, model.status.type)
            setString(i++, model.messageType.type)
            setString(i++, MessagePayload.encode(model.messageType, model.payload))
            setTimestamp(i++, Timestamp(model.createdOn))
            return i;
        }

    override fun hydrateOne(resultSet: ResultSet, entityManager: EntityManager): ChatDropMessage {
        val id = resultSet.getInt(ID.alias())
        if (entityManager.contains(ENTITY_CLASS, id)) {
            return entityManager.get(ENTITY_CLASS, id)
        }
        return ChatDropMessage(resultSet.getInt(CONTACT_ID.alias()),
            resultSet.getInt(IDENTITY_ID.alias()),
            toEnum(Direction.values(), resultSet.getByte(DIRECTION.alias()), { it.type }),
            toEnum(Status.values(), resultSet.getInt(STATUS.alias()), { it.type }),
            toEnum(MessageType.values(), resultSet.getString(PAYLOAD_TYPE.alias()), { it.type }),
            resultSet.getString(PAYLOAD.alias()),
            resultSet.getLong(CREATED_ON.alias()),
            id);
    }

    fun <X : Enum<X>, S : Any> toEnum(enum: Array<X>, value: S, extract: (enum: X) -> S) =
        enum.find {
            extract(it).equals(value)
        } ?: throw RuntimeException("Invalid enum value found")

}
