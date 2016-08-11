package de.qabel.core.repository.sqlite.migration

import java.sql.Connection
import java.sql.SQLException

class Migration1460997040ChatDropMessage(connection: Connection) : AbstractMigration(connection) {

    override val version: Long
        get() = 1460997040L

    @Throws(SQLException::class)
    override fun up() {
        execute(
                "CREATE TABLE chat_drop_message (" +
                        "id INTEGER PRIMARY KEY," +
                        "contact_id INTEGER NOT NULL," +
                        "identity_id INTEGER NOT NULL," +
                        "status INTEGER NOT NULL," +
                        "direction INTEGER NOT NULL," +
                        "payload_type VARCHAR(255) NOT NULL," +
                        "payload TEXT," +
                        "created_on TIMESTAMP NOT NULL," +
                        "FOREIGN KEY (contact_id) REFERENCES contact (id) ON DELETE CASCADE," +
                        "FOREIGN KEY (identity_id) REFERENCES identity (id) ON DELETE CASCADE" +
                        ")")
    }

    @Throws(SQLException::class)
    override fun down() {
        execute("DROP TABLE chat_drop_message")
    }
}
