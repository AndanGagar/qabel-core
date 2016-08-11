package de.qabel.box.storage

import de.qabel.box.storage.exceptions.QblStorageException

import java.io.InputStream

interface StorageWriteBackend {
    /**
     * Upload a file to the storage. Will overwrite if the file exists
     */
    @Throws(QblStorageException::class)
    fun upload(name: String, content: InputStream): Long

    /**
     * Delete a file on the storage. Will not fail if the file was not found
     */
    @Throws(QblStorageException::class)
    fun delete(name: String)
}
