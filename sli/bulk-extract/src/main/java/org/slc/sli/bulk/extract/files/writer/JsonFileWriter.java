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
package org.slc.sli.bulk.extract.files.writer;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

import org.slc.sli.domain.Entity;

/**
 * Extract's Data File Class.
 * @author npandey
 *
 */
public class JsonFileWriter implements Closeable {
    private static final JsonFactory JSON_FACTORY = new JsonFactory();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private File file;
    private GzipCompressorOutputStream outputStream;
    private JsonGenerator jsonGenerator;

    private static final String FILE_EXTENSION = ".json.gz";

    /**
     * Parameterized constructor.
     * @param parentDir
     *          Name of the parent directory of the extract files
     * @param filePrefix
     *          the prefix string to be used in file name generationName of the data file
     */
    public JsonFileWriter(File parentDir, String filePrefix) {
        file = new File(parentDir, filePrefix + FILE_EXTENSION);
    }

    /**
     * Getter for the output stream.
     * @return
     *      returns an OutputStream object
     * @throws IOException
     *          if an I/O error occurred
     * @throws FileNotFoundException
     *          if data file is not found
     */
    private OutputStream getOutputStream() throws FileNotFoundException, IOException {
        if(outputStream == null) {
            outputStream = new GzipCompressorOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        }
        return outputStream;
    }

    private JsonGenerator getJsonGenerator() throws IOException {
        if (jsonGenerator == null) {
            jsonGenerator = JSON_FACTORY.createJsonGenerator(getOutputStream());
            jsonGenerator.setCodec(MAPPER);
            jsonGenerator.writeStartArray();
        }

        return jsonGenerator;
    }

    /**
     * Writes an entity to a json file.
     * @param entity entity
     * @throws IOException IOException
     */
    public void write(Entity entity) throws IOException {
        getJsonGenerator().writeObject(entity.getBody());
    }

    @Override
    public void close() {
        try {
            if (jsonGenerator != null) {
                jsonGenerator.writeEndArray();
                jsonGenerator.flush();
                jsonGenerator.close();
            }
        } catch (IOException e) {
            // eat the exception
            e = null;
        }
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
