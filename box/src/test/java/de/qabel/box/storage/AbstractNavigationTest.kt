package de.qabel.box.storage

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import de.qabel.box.storage.command.DeleteFileChange
import de.qabel.box.storage.command.UpdateFileChange
import de.qabel.box.storage.dto.DMChangeNotification
import de.qabel.box.storage.hash.QabelBoxDigestProvider
import de.qabel.box.storage.jdbc.JdbcDirectoryMetadataFactory
import de.qabel.core.crypto.CryptoUtils
import de.qabel.core.crypto.QblECKeyPair
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.spongycastle.crypto.params.KeyParameter
import rx.observers.TestSubscriber
import java.io.*
import java.nio.file.Files
import java.security.Security
import java.util.*

abstract class AbstractNavigationTest {
    init {
        Security.addProvider(QabelBoxDigestProvider())
    }

    val tmpDir = Files.createTempDirectory("qbl").toFile()
    val deviceId = "deviceId".toByteArray()
    val dmFactory = JdbcDirectoryMetadataFactory(tmpDir, deviceId)
    val dm = dmFactory.create()
    val keyPair = QblECKeyPair()
    val key = keyPair.privateKey
    val readBackend = StubReadBackend()
    val writeBackend = StubWriteBackend()
    val volumeConfig = BoxVolumeConfig(
        "prefix",
        deviceId,
        readBackend,
        writeBackend,
        "Blake2b",
        tmpDir
    )
    abstract val nav: AbstractNavigation
    var subscriber = TestSubscriber<DMChangeNotification>()

    @Before
    open fun setUp() {
        nav.changes.subscribe(subscriber)
    }

    @Test
    fun notifiesAboutFileCreates() {
        nav.upload("test", ByteArrayInputStream("content".toByteArray()), 7)
        assertChange(UpdateFileChange::class.java) {
            assertEquals("test", newFile.name)
            assertNull(expectedFile)
        }
    }

    @Test
    fun notifiesAboutFileDeletes() {
        val file = nav.upload("test", ByteArrayInputStream("content".toByteArray()), 7)
        resubscribe()
        nav.delete(file)

        assertChange(DeleteFileChange::class.java) {
            assertEquals("test", DeleteFileChange@this.file.name)
        }
    }

    @Test
    fun resubscribe() {
        subscriber = TestSubscriber<DMChangeNotification>()
        nav.changes.subscribe(subscriber)
    }

    fun <T> assertChange(clazz: Class<T>, block: T.() -> Unit) : T {
        val changes = subscriber.onNextEvents
        assertThat(changes, hasSize(equalTo(1)))

        val change = changes.first().change
        if (!clazz.isInstance(change)) {
            fail(change.toString() + " is not of type " + clazz.name)
        }
        block(change as T)
        return change
    }

    @Test
    fun catchesDMConflictsWhileUploadingDm() {
        nav.setAutocommit(false)
        nav.upload("testfile", "content".byteInputStream(), 7L)
        val cPath = setupConflictingDM() { it.insertFile(someFile("anotherFile")) }

        var dmWasUploaded = false
        // on commit, AbstractNavigation will check if the dm has changed remotely, let's assume it has not
        readBackend.respond(dm.fileName) { throw UnmodifiedException("dm not modified") }

        // but before our new dm is uploaded, somebody else finished a commit remotely
        writeBackend.respond(dm.fileName) { throw ModifiedException("dm was modified")}

        // after detecting the conflict, the new download should happen
        readBackend.respond(dm.fileName) { StorageDownload(encryptAndStream(cPath), "another hash", cPath.length()) }

        // and then we can cleanly re-upload the dm
        writeBackend.respond(dm.fileName) {
            dmWasUploaded = true
            StorageWriteBackend.UploadResult(Date(), "new etag")
        }

        nav.commit()

        assertTrue(nav.hasFile("testfile"))
        assertTrue(nav.hasFile("anotherFile"))
        assertTrue(dmWasUploaded)
    }

    private fun setupConflictingDM(action: ((DirectoryMetadata) -> Unit)? = null): File {
        val conflictingDM = dmFactory.create()
        action?.invoke(conflictingDM)
        conflictingDM.commit()
        val cPath = conflictingDM.path
        return cPath
    }

    internal open fun encryptAndStream(cPath: File): InputStream? {
        val baos = ByteArrayOutputStream()
        CryptoUtils().encryptStreamAuthenticatedSymmetric(FileInputStream(cPath), baos, KeyParameter(key), null)
        return ByteArrayInputStream(baos.toByteArray())
    }

    private fun someFile(name: String = "file") = BoxFile("a", "b", name, 10L, 0L, "test".toByteArray())

    private fun anyUploadResult() = StorageWriteBackend.UploadResult(Date(), "etag")
}
