package dev.flandia.android.util

import dev.flandia.android.util.TempFiles.LOCAL_TEMP_DIR
import dev.flandia.android.util.TempFiles.SYSTEM_TEMP_DIR
import dev.flandia.android.util.TempFiles.alloc
import dev.flandia.android.util.TempFiles.allocLocal
import dev.flandia.android.util.TempFiles.allocSystem
import dev.flandia.android.util.TempFiles.free
import dev.flandia.android.util.TempFiles.register
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.*

/**
 * A utility object providing functions related to temp files.
 *
 * In order to use this object, call [register] first. It registers
 * a temp directory for future use. The registered temp directory is
 * cleaned immediately (if it already exists). Then, call [alloc] with the
 * registered temp directory to obtain a temp file. Finally call [free] to
 * free the allocated temp file.
 *
 * There are also two pre-registered directories, which are
 * [SYSTEM_TEMP_DIR] and [LOCAL_TEMP_DIR]. Call [allocSystem] or
 * [allocLocal] to make use of them.
 *
 * Don't worry if there are some temp files which haven't been freed.
 * There's a shutdown hook, freeing all allocated temp files and cleaning
 * all registered temp directories. In the worst case, the JVM may halt
 * without calling the shutdown hook. However, [register] cleans the temp
 * directory while registering. So register the temp directory on program
 * startups is the best practice.
 */
object TempFiles {

    private data class TempDir(val path: Path) {
        init {
            check(path.normalize().toAbsolutePath() == path)
            if (path.exists()) check(path.isDirectory())
        }
    }

    private data class TempFile(val path: Path) {
        init {
            check(path.normalize().toAbsolutePath() == path)
            if (path.exists()) check(path.isRegularFile())
        }
    }

    private fun tempDirOf(path: Path) = TempDir(path.normalize().toAbsolutePath())

    private fun tempFileOf(path: Path) = TempFile(path.normalize().toAbsolutePath())


    /**
     * The default temp directory specified by the OS. It is based on the
     * JVM property `java.io.tmpdir`, resolving `top.anagke.kio_TempFiles`
     * directory.
     *
     * In Unix-like systems, the base temp directory is usually `/tmp` or
     * `/var/tmp`. In windows, the base temp directory is usually `%tmp%`.
     * However, this property could be changed by adding JVM option
     * `-Djava.io.tmpdir=<path>`
     */
    val SYSTEM_TEMP_DIR: Path = Paths.get(System.getProperty("java.io.tmpdir"), "top.anagke.kio_TempFiles")

    /**
     * The default temp directory which is OS independent. It is based on the
     * cwd resolving `top.anagke.kio_TempFiles`.
     */
    val LOCAL_TEMP_DIR: Path = Paths.get("./top.anagke.kio_TempFiles")

    private val tempDirs: MutableSet<TempDir> = ConcurrentHashMap.newKeySet()
    private val tempFiles: MutableSet<TempFile> = ConcurrentHashMap.newKeySet()

    init {
        Runtime.getRuntime().addShutdownHook(Thread { tempDirs.forEach(this::clean) })
        register(SYSTEM_TEMP_DIR)
        register(LOCAL_TEMP_DIR)
    }


    /**
     * Registers the temp directory.
     *
     * **WARNING**: This method cleans all files and directories in the temp
     * directory. Make sure there are no important files in the temp directory.
     * The temp directory will also be cleans before the JVM shutdowns.
     */
    fun register(tempDirPath: Path) {
        val tempDirObj = tempDirOf(tempDirPath)
        clean(tempDirObj)
        tempDirs.add(tempDirObj)
    }

    private fun clean(tempDir: TempDir) {
        if (Files.exists(tempDir.path)) {
            Files.walk(tempDir.path).use { walk ->
                walk.sorted(Comparator.reverseOrder())
                    .forEach { it.deleteExisting() }
            }
        }
    }


    /**
     * Allocates a temp file inside the given temp directory. To ensure all
     * temp files are cleaned after the JVM shutdowns, the temp directory must
     * be registered via [register].
     *
     * The name of the allocated temp file is generated by a random UUID
     * (type 4) without any extensions.
     *
     * @param tempDir the given temp directory
     * @return the allocated temp file, inside the given temp directory
     */
    fun alloc(tempDir: Path, ext: String): Path {
        val tempDirObj = tempDirOf(tempDir)
        if (!tempDirs.contains(tempDirObj)) throw TempDirNotFoundException(tempDir)
        val tempFile = tempDir.resolve(newTempFileName(ext))
        val tempFileObj = tempFileOf(tempFile)
        tempFiles.add(tempFileObj)
        tempDir.createDirectories()
        tempFile.createFile()
        return tempFile
    }

    /**
     * Frees the given temp file. To ensure not to delete any important file,
     * the given temp file must be allocated via [alloc].
     *
     * This method is representation-insensitive, which means it doesn't
     * distinguish relative path and absolute path.
     *
     * If the given temp file is not allocated via [alloc], throws a
     * [TempFileNotFoundException]. Freeing a same temp file multiple times
     * throws a [TempFileNotFoundException] too.
     *
     * @param tempFile the given temp file, should be allocated via [alloc]
     */
    fun free(tempFile: Path) {
        val tempFileObj = tempFileOf(tempFile)
        if (!tempFiles.remove(tempFileObj)) throw TempFileNotFoundException(tempFile)
        tempFile.deleteIfExists()
    }

    private fun newTempFileName(ext: String): String = "${UUID.randomUUID().toString()}.$ext"


    /**
     * Allocates a temp file inside [SYSTEM_TEMP_DIR].
     *
     * More details in [alloc].
     */
    fun allocSystem(ext: String) = alloc(SYSTEM_TEMP_DIR, ext)

    /**
     * Allocates a temp file inside [LOCAL_TEMP_DIR].
     *
     * More details in [alloc].
     */
    fun allocLocal(ext: String) = alloc(LOCAL_TEMP_DIR, ext)

}

/** Indicates that a temp directory can't be found. */
class TempDirNotFoundException(tempDir: Path) : Exception("temp dir $tempDir not found")

/** Indicates that a temp file can't be found. */
class TempFileNotFoundException(tempFile: Path) : Exception("temp file $tempFile not found")

/**
 * Executes the given block with a new allocated temp file and then free it
 * correctly whether an exception is thrown or not.
 *
 * @param tempDir the temp directory
 * @param block the given block
 */
fun <R> TempFiles.useTempFile(tempDir: Path, ext: String, block: (Path) -> R): R {
    val tempFile = alloc(tempDir, ext)
    try {
        return block(tempFile)
    } finally {
        free(tempFile)
    }
}

fun <R> TempFiles.useSystemTempFile(ext: String, block: (Path) -> R): R {
    return useTempFile(SYSTEM_TEMP_DIR, ext, block)
}
