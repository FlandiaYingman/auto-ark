package tech.flandia_yingm.auto_fgo;

import lombok.val;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.newOutputStream;
import static org.apache.commons.io.FilenameUtils.getBaseName;

public class ResourcesHelper {

    public static Path copyResourceToFile(Class<?> clazz, String resourceName) throws IOException {
        val tempFile = createTempFile(
                getBaseName(resourceName),
                getExtension(resourceName)
        );
        try (val resourceInputStream = newResourceStream(clazz, resourceName);
             val fileOutputStream = newOutputStream(tempFile)) {
            IOUtils.copy(resourceInputStream, fileOutputStream);
        }
        return tempFile;
    }

    private static InputStream newResourceStream(Class<?> clazz, String resourceName) throws IOException {
        val resource = clazz.getResource(resourceName);
        if (resource == null) {
            throw new IllegalArgumentException(String.format("Resource %s of class %s not found", resourceName, clazz));
        }
        val connection = resource.openConnection();
        connection.setUseCaches(false);
        return connection.getInputStream();
    }

    private static String getExtension(String resourceName) {
        if (FilenameUtils.getExtension(resourceName).isEmpty()) {
            return null;
        }
        return String.format(".%s", FilenameUtils.getExtension(resourceName));
    }

}
