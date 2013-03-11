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

package util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

/**
 * Zip File writing class.
 *
 * @author tshewchuk
 *
 */
public class OutstreamZipFile {

    // private final Logger LOG =
    // LoggerFactory.getLogger(OutstreamZipFile.class);
    private final String TMP = "_tmp";

    // private FileOutputStream fos;
    private ZipArchiveOutputStream zos;
    private File tempZipFile;
    private ArchiveEntry zipEntry = null;
    private File zipFile;

    /**
     * Don't allow construction without a zip file.
     *
     */
    @SuppressWarnings("unused")
    private OutstreamZipFile() {
    }

    /**
     * Creates a new temporary ZIP file in the target folder.
     *
     * @param parentDirName
     *            Parent directory of new zip file
     * @param zipFileName
     *            New zip file to be created
     *
     * @throws IOException
     */
    public OutstreamZipFile(String parentDirName, String zipFileName) throws IOException {
        File parentDir = new File(parentDirName + "/");
        if (parentDir.isDirectory()) {
            tempZipFile = new File(parentDir, zipFileName + TMP + ".zip");
            tempZipFile.createNewFile();
            if (tempZipFile.canWrite()) {
                zos = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(
                        tempZipFile)));
                zipFile = new File(parentDir, zipFileName + ".zip");
            }
        }
    }

    /**
     * Creates a new archive entry in the ZIP archive.
     *
     * @param fileEntryName
     *            Name of the new file entry to add to the zip file
     *
     * @throws IOException
     */
    public void createArchiveEntry(String fileEntryName) throws IOException {
        if (zipEntry != null) {
            zos.closeArchiveEntry();
        }
        zipEntry = zos.createArchiveEntry(tempZipFile, fileEntryName);
        zos.putArchiveEntry(zipEntry);
    }

    /**
     * Returns a stream for a file stored in the ZIP archive.
     *
     * @param zipFile
     *            ZIP archive
     * @param data
     *            Data to add to the zip file
     *
     * @throws IOException
     */
    public int writeData(String data) throws IOException {
        int length = data.length();

        // Write data to output stream.
        zos.write(data.getBytes(), 0, length);
        zos.write('\n');

        return length;
    }

    /**
     * Rename the temp zip file to its permanent name.
     *
     * @throws IOException
     */
    public boolean renameTempZipFile() throws IOException {
        if (zipEntry != null) {
            zos.closeArchiveEntry();
        }
        zos.finish();
        IOUtils.closeQuietly(zos);
        boolean renamed = tempZipFile.renameTo(zipFile);
        return renamed;
    }

    public File getZipFile() {
        return zipFile;
    }

}
