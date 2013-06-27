/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.ingestion.landingzone;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZIP File utility class.
 */
public final class ZipFileUtil {

    static final Logger LOG = LoggerFactory.getLogger(ZipFileUtil.class);

    private ZipFileUtil() {
    }

    /**
     * Extracts content of the ZIP file to the target folder.
     *
     * @param zipFile   ZIP archive
     * @param targetDir Directory to extract files to
     * @param mkdirs    Allow creating missing directories on the file paths
     * @throws IOException           IO Exception
     * @throws FileNotFoundException
     */
    public static void extract(File zipFile, File targetDir, boolean mkdirs) throws IOException {
        ZipFile zf = null;

        try {
            zf = new ZipFile(zipFile);

            Enumeration<ZipArchiveEntry> zes = zf.getEntries();

            while (zes.hasMoreElements()) {
                ZipArchiveEntry entry = zes.nextElement();

                if (!entry.isDirectory()) {
                    File targetFile = new File(targetDir, entry.getName());

                    if (mkdirs) {
                        targetFile.getParentFile().mkdirs();
                    }

                    InputStream is = null;
                    try {
                        is = zf.getInputStream(entry);
                        copyInputStreamToFile(is, targetFile);
                    } finally {
                        IOUtils.closeQuietly(is);
                    }
                }
            }
        } finally {
            ZipFile.closeQuietly(zf);
        }
    }

    /**
     * Returns a stream for a file stored in the ZIP archive.
     *
     * @param zipFile  ZIP archive
     * @param fileName Name of the file to get stream for
     * @return Input Stream for the requested file entry
     * @throws IOException           IO Exception
     * @throws FileNotFoundException
     */
    public static InputStream getInputStreamForFile(File zipFile, String fileName) throws IOException {
        ZipFile zf = null;
        InputStream fileInputStream = null;

        try {
            zf = new ZipFile(zipFile);

            ZipArchiveEntry entry = zf.getEntry(fileName);

            if (entry == null || entry.isDirectory()) {
                String msg = MessageFormat.format("No file entry is found for {0} withing the {0} archive", fileName, zipFile);
                throw new FileNotFoundException(msg);
            }

            final ZipFile fzf = zf;
            fileInputStream = new BufferedInputStream(zf.getInputStream(entry)) {
                @Override
                public void close() throws IOException {
                    super.close();
                    ZipFile.closeQuietly(fzf);
                }
            };
        } finally {
            if (fileInputStream == null) {
                ZipFile.closeQuietly(zf);
            }
        }

        return fileInputStream;
    }

    /**
     * Returns whether or not a file is in the ZIP archive.
     *
     * @param zipFile  ZIP archive
     * @param fileName Name of the file to get stream for
     * @return whether or not the fileName is in the zipFile
     */
    public static boolean isInZipFile(File zipFile, String fileName) {
        ZipFile zf = null;

        try {
            zf = new ZipFile(zipFile);

            Enumeration<ZipArchiveEntry> zipFileEntries = zf.getEntries();

            while (zipFileEntries.hasMoreElements()) {
                if (zipFileEntries.nextElement().getName().equals(fileName)) {
                    return true;
                }
            }
        } catch (IOException exception) {
            return false;
        } finally {
            ZipFile.closeQuietly(zf);
        }

        return false;
    }

    public static boolean isInZipFileEntries(String fileName, Set<String> zipFileEntries) {
        if (zipFileEntries == null) {
            return false;
        }

        return zipFileEntries.contains(fileName);
    }

    /**
     * Get the entries in the ZIP file.
     *
     * @param zipFileName  ZIP file name
     * @return the entries in the ZIP file
     */
    public static Set<String> getZipFileEntries(String zipFileName) throws IOException {
        Enumeration<ZipArchiveEntry> zipFileEntries = null;
        Set<String> filesInZip = new HashSet<String>();

        ZipFile zf = null;

        if (zipFileName == null) {
            return null;
        }

        try {
            zf = new ZipFile(zipFileName);

            zipFileEntries = zf.getEntries();
            while (zipFileEntries.hasMoreElements()) {
                filesInZip.add(zipFileEntries.nextElement().getName());
            }

        } finally {
            if (zf != null) {
                ZipFile.closeQuietly(zf);
            }
        }

        return filesInZip;
    }

    /**
     * Retrieves a name of the first .ctl file found in the zip archive.
     *
     * @param zipFile ZIP file to scan
     * @return A filename representing the control file.
     * @throws IOException IO Exception
     */
    public static String getControlFileName(File zipFile) throws IOException {
        ZipFile zf = null;

        try {
            zf = new ZipFile(zipFile);

            Enumeration<ZipArchiveEntry> zes = zf.getEntries();

            while (zes.hasMoreElements()) {
                ZipArchiveEntry entry = zes.nextElement();

                if (!entry.isDirectory() && entry.getName().endsWith(".ctl")) {
                    return entry.getName();
                }
            }
        } finally {
            ZipFile.closeQuietly(zf);
        }

        return null;
    }

    /**
     * This is a copy of the Apache Commons IO {@link FileUtils.copyInputStreamToFile},
     * except it does not close the input stream at the end.
     *
     * @param source      Source stream
     * @param destination Destination file
     * @throws IOException
     */
    private static void copyInputStreamToFile(InputStream source, File destination) throws IOException {
        OutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(destination));

            IOUtils.copy(source, output);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

}
