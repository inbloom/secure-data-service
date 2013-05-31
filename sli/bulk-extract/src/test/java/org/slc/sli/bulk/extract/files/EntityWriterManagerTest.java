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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.bulk.extract.files.metadata.ErrorFile;
import org.slc.sli.bulk.extract.files.writer.JsonFileWriter;
import org.slc.sli.bulk.extract.files.writer.EntityWriter;
import org.slc.sli.bulk.extract.util.DefaultHashMap;
import org.slc.sli.domain.Entity;

/**
 * Test bulk extraction into zip files.
 *
 * @author ablum
 *
 */
public class EntityWriterManagerTest {

    private EntityWriterManager writer = new EntityWriterManager();
    EntityWriter defaultWriter = Mockito.mock(EntityWriter.class);
    EntityWriter jsonWriter1 = Mockito.mock(EntityWriter.class);
    EntityWriter jsonWriter2 = Mockito.mock(EntityWriter.class);

    /**
     * Initialize the ComplexEntityWriter.
     */
    @Before
    public void init() {

        Map<String, EntityWriter> writers = new HashMap<String, EntityWriter>();
        writers.put("teacher", jsonWriter1);
        writers.put("staff", jsonWriter2);

        DefaultHashMap<String, EntityWriter> defaulted = new DefaultHashMap<String, EntityWriter>(writers, defaultWriter);
        writer.setWriters(defaulted);

        Map<String, String> multi = new HashMap<String, String>();
        multi.put("teacher", "staff");

        writer.setMultiFileEntities(multi);

        Map<String, String> entities = new HashMap<String, String>();
        entities.put("teacher", "staff");
        entities.put("staff", "staff");
        entities.put("student", "student");
        writer.setEntities(entities);
    }

    /**
     * Test the write method.
     */
    @Test
    public void testWrite() {
        ExtractFile archiveFile = Mockito.mock(ExtractFile.class);
        JsonFileWriter jsonFile = Mockito.mock(JsonFileWriter.class);
        Mockito.when(archiveFile.getDataFileEntry(Mockito.anyString())).thenReturn(jsonFile);
        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("teacher");

        writer.write(entity, archiveFile);

        Mockito.verify(jsonWriter1, Mockito.times(1)).write(Mockito.any(Entity.class), Mockito.any(JsonFileWriter.class), Mockito.any(ErrorFile.class));
        Mockito.verify(jsonWriter2, Mockito.times(1)).write(Mockito.any(Entity.class), Mockito.any(JsonFileWriter.class), Mockito.any(ErrorFile.class));

    }

    /**
     * Test the write method.
     */
    @Test
    public void testWriteDefault() {
        ExtractFile archiveFile = Mockito.mock(ExtractFile.class);
        JsonFileWriter jsonFile = Mockito.mock(JsonFileWriter.class);
        Mockito.when(archiveFile.getDataFileEntry(Mockito.anyString())).thenReturn(jsonFile);
        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("student");

        writer.write(entity, archiveFile);
        Mockito.verify(defaultWriter, Mockito.times(1)).write(Mockito.any(Entity.class), Mockito.any(JsonFileWriter.class), Mockito.any(ErrorFile.class));
        Mockito.verify(jsonWriter1, Mockito.times(0)).write(Mockito.any(Entity.class), Mockito.any(JsonFileWriter.class), Mockito.any(ErrorFile.class));
        Mockito.verify(jsonWriter2, Mockito.times(0)).write(Mockito.any(Entity.class), Mockito.any(JsonFileWriter.class), Mockito.any(ErrorFile.class));

    }
}
