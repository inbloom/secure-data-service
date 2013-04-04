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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.bulk.extract.files.JsonExtractFile;
import org.slc.sli.bulk.extract.treatment.Treatment;
import org.slc.sli.domain.Entity;

/**
 * Test bulk extraction into zip files.
 *
 * @author ablum
 *
 */
public class JsonEntityWriterTest {
    private JsonEntityWriter writer;
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
        writer = new JsonEntityWriter(treatment);
    }

    /**
     * Test write.
     */
    @Test
    public void testWrite() {
        JsonExtractFile file = Mockito.mock(JsonExtractFile.class);
        Entity entity = Mockito.mock(Entity.class);

        Entity check = writer.write(entity, file);

        Assert.assertEquals(treatedEntity, check);
        try {
            Mockito.verify(file, Mockito.times(1)).write(treatedEntity);
        } catch (IOException e) {
            Assert.fail();
        }
    }

}
