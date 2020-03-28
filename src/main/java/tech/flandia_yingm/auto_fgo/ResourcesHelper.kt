package tech.flandia_yingm.auto_fgo

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

object ResourcesHelper {

    @Throws(IOException::class)
    fun copyResourceToFile(clazz: Class<*>, resourceName: String): Path {
        val tempFile = Files.createTempFile(
                File(resourceName).nameWithoutExtension,
                ".${File(resourceName).extension}"
        )
        newResourceStream(clazz, resourceName).use { ris ->
            Files.newOutputStream(tempFile).use { fos ->
                ris.copyTo(fos)
            }
        }
        return tempFile
    }

    private fun newResourceStream(clazz: Class<*>, name: String): InputStream {
        val resource = clazz.getResource(name) ?: throw IllegalArgumentException("$name of $clazz not found")
        val connection = resource.openConnection()
        connection.useCaches = false
        return connection.getInputStream()
    }

}