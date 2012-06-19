package org.slc.sli.ingestion.util;

import java.io.File;

import org.springframework.stereotype.Component;

/**
 * Utility class for dealing with files
 *
 * @author nbrown
 *
 */
@Component
public class FileUtils {

    /**
     * Get or create a sub-directory withing the provided parent directory
     *
     * @param parentDir
     * @param subDirName
     * @return
     */
    public static File getOrCreateSubDir(final File parentDir, final String subDirName) {
        String parentDirAbsPath = parentDir.getAbsolutePath();
        if (!parentDirAbsPath.endsWith("/")) {
            parentDirAbsPath += "/";
        }
        File subDir = new File(parentDirAbsPath + subDirName);
        if (!subDir.exists()) {
            subDir.mkdir();
        }
        return subDir;
    }

    /**
     * Renames a file
     *
     * @param source
     * @param dest
     * @return boolean value whether the renaming was successful or not.
     */
    public static boolean renameFile(File source, File dest) {
        dest.delete();
        return source.renameTo(dest);
    }
}
