package de.qabel.box.storage.jdbc.migration

import de.qabel.core.repository.sqlite.migration.AbstractMigration
import java.sql.Connection

class FMMigration1468173861Init(connection: Connection) : AbstractMigration(connection) {
    override val version = 1468173861L

    override fun up() {
        execute("CREATE TABLE spec_version ( version INTEGER PRIMARY KEY )")
        execute("""CREATE TABLE file (
            owner BLOB NOT NULL,
            prefix VARCHAR(255) NOT NULL,
            block VARCHAR(255) NOT NULL,
            name VARCHAR(255) NULL PRIMARY KEY,
            size LONG NOT NULL,
            mtime LONG NOT NULL,
            key BLOB NOT NULL )""")
        execute("INSERT INTO spec_version (version) VALUES(0)")
    }

    override fun down() {
        execute("DROP TABLE file")
        execute("DROP TABLE spec_version")
    }
}
