package top.anagke.auto_android.util

import java.io.Closeable
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*
import kotlin.io.path.Path


class OsMutex
@Throws(MutexException::class)
constructor(lockName: String) : Closeable {

    private val lockChannel: FileChannel

    init {
        val tmpdir = System.getProperty("java.io.tmpdir")!!
        val lockFile = Path(tmpdir, "$lockName.lock")

        try {
            lockChannel = FileChannel.open(lockFile, WRITE, CREATE, TRUNCATE_EXISTING, DELETE_ON_CLOSE, SYNC)
            lockChannel.tryLock() ?: throw MutexException(lockName, lockFile)
        } catch (e: IOException) {
            throw MutexException(lockName, lockFile, e)
        }
    }

    override fun close() {
        lockChannel.close()
    }

}

class MutexException(lockName: String, lockFile: Path, cause: Throwable? = null) :
    Exception("lock $lockName ($lockFile) has already been acquired", cause)
