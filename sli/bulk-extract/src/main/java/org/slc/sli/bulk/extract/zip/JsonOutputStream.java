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
package org.slc.sli.bulk.extract.zip;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
/**
 * @author tke
 *
 */
public abstract class JsonOutputStream {
    protected ArchiveOutputStream archiveStream;
    protected ArchiveEntry archiveEntry;
    protected File outputFile;
    protected JsonFactory jsonFactory;
    protected JsonGenerator jGenerator;

    public abstract void createArchiveEntry(String fileEntryName) throws IOException;

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
        jGenerator.close();
       archiveStream.finish();
        IOUtils.closeQuietly(archiveStream);
    }
}
