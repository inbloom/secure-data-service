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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.bulk.extract.files.writer.JsonFileWriter;
import org.slc.sli.domain.Entity;

/** Unit test for JsonExtractFile.
 * @author tke
 *
 */
public class JsonFileWriterTest {

    /**
     * test case.
     */
    @Test
    public void test() {
        String tempParentDir = "./tempDir";
        String tempFileName = "tempFile";
        File gZipFile = null;
            try {

                File tempDir = new File(tempParentDir);
                tempDir.mkdirs();
                Entity entity = Mockito.mock(Entity.class);
                Map<String, Object> body = new HashMap<String, Object>();
                body.put("Student", "Student");
                Mockito.when(entity.getBody()).thenReturn(body);
                JsonFileWriter testDEF = new JsonFileWriter(tempDir, tempFileName);

                testDEF.write(entity);
                testDEF.close();

                gZipFile = new File(tempDir, tempFileName + ".json.gz");

                InputStream stream = new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(gZipFile)));

                StringWriter writer = new StringWriter();
                IOUtils.copy(stream, writer);
                String actual = writer.toString();
                //String entryString = entry.toString();

                ObjectMapper mapper = new ObjectMapper();
                String expected = mapper.writeValueAsString(body);
                Assert.assertTrue("Failed to find contents of first student", actual.contains(expected));
            } catch (Exception e) {
                Assert.fail("Test fail due to exception" + e.toString());
            } finally {
                FileUtils.deleteQuietly(gZipFile);
            }
    }

}
