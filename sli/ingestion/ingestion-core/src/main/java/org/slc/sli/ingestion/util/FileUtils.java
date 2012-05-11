package org.slc.sli.ingestion.util;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for dealing with files
 *
 * @author nbrown
 *
 */
@Component
public class FileUtils {

    @Value("${landingzone.inbounddir}")
    private String lzDirectory;

    /**
     * Create a temporary file
     *
     * @return a
     * @throws IOException
     */
    public File createTempFile() throws IOException {
        File landingZone = new File(lzDirectory);
        File outputFile = landingZone.exists() ? File.createTempFile("ingestion_", ".tmp", landingZone) : File
                .createTempFile("ingestion_", ".tmp");
        return outputFile;
    }

    /**
     * Renames a file
     * @param source
     * @param dest
     * @return boolean value whether the renaming was successful or not.
     */
    public static boolean renameFile(File source, File dest) {
        dest.delete();
        return source.renameTo(dest);
    }
}
