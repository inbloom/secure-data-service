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

package org.slc.sli.bulk.extract.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Zip File writing class.
 *
 * @author tshewchuk
 *
 */
public class OutstreamZipFile{

    private ArchiveOutputStream archiveStream;
    private ArchiveEntry archiveEntry;
    private File outputFile;
    private JsonFactory jsonFactory;
    private JsonGenerator jGenerator;

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
            outputFile = new File(parentDir, zipFileName + ".zip");
            outputFile.createNewFile();
            if (outputFile.canWrite()) {
                archiveStream = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(
                        outputFile)));
            }
            jsonFactory = new JsonFactory();
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
        if (archiveEntry != null) {
            archiveStream.closeArchiveEntry();
        }
        archiveEntry =archiveStream.createArchiveEntry(outputFile, fileEntryName);
        archiveStream.putArchiveEntry(archiveEntry);
        jGenerator = jsonFactory.createJsonGenerator(archiveStream);
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
        archiveStream.write('\n');
        archiveStream.write(data.getBytes(), 0, length);

        return length;
    }

    public void writeData(Map<String, Object> value) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Write data to output stream.
        mapper.writeValue(jGenerator, value);

    }

    public void writeEndArray() throws JsonGenerationException, IOException, InterruptedException {
        jGenerator.writeEndArray();
        jGenerator.flush();
    }

    public void writeStartArray() throws JsonGenerationException, IOException {
        jGenerator.writeStartArray();
    }

    /**
     *
     *
     * @throws IOException
     */
    public void closeZipFile() throws IOException {
        if (archiveEntry != null) {
            archiveStream.closeArchiveEntry();
        }
        archiveStream.finish();
        IOUtils.closeQuietly(archiveStream);
    }

    public File getOutputFile() {
        return outputFile;
    }

}
