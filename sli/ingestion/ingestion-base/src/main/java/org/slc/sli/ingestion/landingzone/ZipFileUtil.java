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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZIP File utility class.
 *
 */
public final class ZipFileUtil {

    static final Logger LOG = LoggerFactory.getLogger(ZipFileUtil.class);

    private ZipFileUtil() {}

    /**
     * Extracts content of the ZIP file to the target folder.
     *
     * @param zipFile ZIP archive
     * @param targetDir Directory to extract files to
     * @param mkdirs Allow creating missing directories on the file paths
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void extract(File zipFile, File targetDir, boolean mkdirs) throws IOException {
        FileInputStream fis = null;
        ZipArchiveInputStream zis = null;

        try {
            // Create input stream
            fis = new FileInputStream(zipFile);
            zis = new ZipArchiveInputStream(new BufferedInputStream(fis));

            ArchiveEntry entry;

            // Extract files
            while ((entry = zis.getNextEntry()) != null) {

                if (!entry.isDirectory()) {
                    File targetFile = new File(targetDir, entry.getName());

                    if (mkdirs) {
                        targetFile.getParentFile().mkdirs();
                    }

                    copyInputStreamToFile(zis, targetFile);
                }
            }
        } finally {
            IOUtils.closeQuietly(zis);
            IOUtils.closeQuietly(fis);
        }
    }

    /**
     * Returns a stream for a file stored in the ZIP archive.
     *
     * @param zipFile ZIP archive
     * @param fileName Name of the file to get stream for
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static InputStream getInputStreamForFile(File zipFile, String fileName) throws IOException {
        ZipArchiveInputStream zis = null;
        InputStream fileInputStream = null;

        try {
            // Create input stream
            zis = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile)));

            ArchiveEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().equals(fileName)) {
                    fileInputStream = zis;
                    break;
                }
            }
        } finally {
            if (fileInputStream == null) {
                IOUtils.closeQuietly(zis);
            }
        }

        if (fileInputStream == null) {
            String msg = MessageFormat.format("No file entry is found for {0} withing the {0} archive", fileName, zipFile);
            throw new FileNotFoundException(msg);
        }

        return fileInputStream;
    }

    /**
     * Retrieves a name of the first .ctl file found in the zip archive.
     *
     * @param zipFile ZIP file to scan
     */
    public static String getControlFileName(File zipFile) throws IOException {
        ZipArchiveInputStream zis = null;

        try {
            // Create input stream
            zis = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile)));

            ArchiveEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith(".ctl")) {
                    return entry.getName();
                }
            }
        } finally {
            IOUtils.closeQuietly(zis);
        }

        return null;
    }

    /**
     * This is a copy of the Apache Commons IO {@link FileUtils.copyInputStreamToFile},
     * except it does not close the input stream at the end.
     *
     * @param source Source stream
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
