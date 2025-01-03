package dev.flandia.android.util

import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.tinylog.kotlin.Logger
import java.io.File
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.outputStream

internal object BinResources {

    private const val pkg = "dev.flandia.android.bin"
    private const val dst = "bin"

    init {
        Logger.debug("Extracting resources...")
        val ref = Reflections(pkg, Scanners.Resources)
        val res = ref.getResources(".*")
        res.forEach { name ->
            (ClassLoader.getSystemResourceAsStream(name) ?: throw NoSuchFileException(name)).use { istream ->
                Logger.debug("Extracting resource: $name...")
                val dst = Path.of(dst).resolve(Path.of(pkg.replace('.', '/')).relativize(Path.of(name)))
                dst.parent.createDirectories()
                dst.outputStream().use { ostream ->
                    istream.copyTo(ostream)
                }
            }
        }
        Runtime.getRuntime().addShutdownHook(Thread {
            File(dst).deleteRecursively()
        })
    }

    fun init() {
    }

}