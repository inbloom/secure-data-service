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

import java.io.IOException;

import junit.framework.Assert;

import org.codehaus.jackson.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.bulk.extract.files.metadata.ErrorFile;
import org.slc.sli.bulk.extract.treatment.Treatment;
import org.slc.sli.domain.Entity;

/**
 * Test bulk extraction into zip files.
 *
 * @author ablum
 *
 */
public class EntityWriterTest {
    private EntityWriter writer;
    Treatment treatment;
    Entity treatedEntity;

    /**
     * Initialize the JsonEntityWriter.
     */
    @Before
    public void init() {
        treatedEntity = Mockito.mock(Entity.class);
        treatment = Mockito.mock(Treatment.class);
        Mockito.when(treatment.apply(Mockito.any(Entity.class))).thenReturn(treatedEntity);
        writer = new EntityWriter(treatment);
    }

    /**
     * Test write.
     */
    @Test
    public void testWrite() {
        JsonFileWriter file = Mockito.mock(JsonFileWriter.class);
        ErrorFile errorFile = Mockito.mock(ErrorFile.class);
        Entity entity = Mockito.mock(Entity.class);

        Entity check = writer.write(entity, file, errorFile);

        Assert.assertEquals(treatedEntity, check);
        try {
            Mockito.verify(file, Mockito.times(1)).write(treatedEntity);
        } catch (IOException e) {
            Assert.fail();
        }
    }
    
    @Test
    public void testIOException() throws IOException {
        JsonFileWriter file = Mockito.mock(JsonFileWriter.class);
        Mockito.doThrow(new IOException("Mock IOException")).when(file).write(Mockito.any(Entity.class));
        ErrorFile errorFile = Mockito.mock(ErrorFile.class);
        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("MOCK_ENTITY_TYPE");

        writer.write(entity, file, errorFile);
        Mockito.verify(errorFile).logEntityError(entity);
        
        Mockito.doThrow(Mockito.mock(JsonProcessingException.class)).when(file).write(Mockito.any(Entity.class));
        writer.write(entity, file, errorFile);
        Mockito.verify(errorFile, Mockito.times(2)).logEntityError(entity); // twice because we're reusing the mocked logger.
    }

}
