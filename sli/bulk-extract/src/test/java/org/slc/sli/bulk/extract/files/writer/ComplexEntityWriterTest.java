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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.files.JsonExtractFile;
import org.slc.sli.domain.Entity;

/**
 * Test bulk extraction into zip files.
 *
 * @author ablum
 *
 */
public class ComplexEntityWriterTest {

    private ComplexEntityWriter writer = new ComplexEntityWriter();
    JsonEntityWriter jsonWriter1 = Mockito.mock(JsonEntityWriter.class);
    JsonEntityWriter jsonWriter2 = Mockito.mock(JsonEntityWriter.class);

    /**
     * Initialize the ComplexEntityWriter.
     */
    @Before
    public void init() {

        Map<String, JsonEntityWriter> writers = new HashMap<String, JsonEntityWriter>();
        writers.put("teacher", jsonWriter1);
        writers.put("staff", jsonWriter2);
        writer.setWriters(writers);

        Map<String, String> multi = new HashMap<String, String>();
        multi.put("teacher", "staff");

        writer.setmultiFileEntities(multi);
    }

    /**
     * Test the write method.
     */
    @Test
    public void testWrite() {
        ExtractFile archiveFile = Mockito.mock(ExtractFile.class);
        JsonExtractFile jsonFile = Mockito.mock(JsonExtractFile.class);
        Mockito.when(archiveFile.getDataFileEntry(Mockito.anyString())).thenReturn(jsonFile);
        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("teacher");

        writer.write(entity, archiveFile);

        Mockito.verify(jsonWriter1, Mockito.times(1)).write(Mockito.any(Entity.class), Mockito.any(JsonExtractFile.class));
        Mockito.verify(jsonWriter2, Mockito.times(1)).write(Mockito.any(Entity.class), Mockito.any(JsonExtractFile.class));

    }
}
