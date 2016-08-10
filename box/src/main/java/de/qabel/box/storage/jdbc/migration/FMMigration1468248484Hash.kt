package de.qabel.box.storage.jdbc.migration

import de.qabel.core.repository.sqlite.migration.AbstractMigration
import java.sql.Connection

class FMMigration1468248484Hash(connection: Connection) : AbstractMigration(connection) {
    override val version = 1468248484L

    override fun up() {
        execute("ALTER TABLE file ADD COLUMN hash BLOB")
        execute("ALTER TABLE file ADD COLUMN hashAlgorithm TEXT")
    }

    override fun down() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
