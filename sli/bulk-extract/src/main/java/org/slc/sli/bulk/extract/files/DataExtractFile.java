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
package org.slc.sli.bulk.extract.files;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;

/**
 * Extract's Data File Class.
 * @author npandey
 *
 */
public class DataExtractFile implements Closeable{

    private GzipCompressorOutputStream outputStream;
    private File file;

    private static final String FILE_EXTENSION = ".json.gz";

    /**
     * Parameterized constructor.
     * @param parentDirName
     *          Name of the parent directory of the extract files
     * @param fileName
     *          Name of the data file
     * @throws FileNotFoundException
     *          if data file is not found
     * @throws IOException
     *          if an I/O error occurred
     */
    public DataExtractFile(String parentDirName, String fileName)
            throws FileNotFoundException, IOException {

        File parentDir = new File(parentDirName + "/");
        if (parentDir.isDirectory()) {
            file = new File(parentDir, fileName + FILE_EXTENSION);
            file.createNewFile();
            if (file.canWrite()) {
                outputStream = new GzipCompressorOutputStream(new FileOutputStream(file));
            }
        }
    }

    /**
     * Getter for the output stream.
     * @return
     *      returns an Outputstream object
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(outputStream);

    }

    /**
     * Getter for data file's File object.
     * @return
     *      returns the File object
     */
    public File getFile() {
        return file;
    }

}
