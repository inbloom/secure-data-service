/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
