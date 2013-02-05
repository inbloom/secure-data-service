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
package org.slc.sli.validation.schema;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.NaturalKeyValidationException;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.SchemaRepository;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * TODO: add class javadoc
 *
 */
public class ApiNeutralSchemaValidatorTest {

    @InjectMocks
    ApiNeutralSchemaValidator apiNeutralSchemaValidator = new ApiNeutralSchemaValidator();

    @Mock
    INaturalKeyExtractor mockNaturalKeyExtractor;

    @Mock
    SchemaRepository mockSchemaRepository;

    @Mock
    Repository<Entity> mockRepository;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void validateNaturalKeyValidationExceptionFromMissingFields() throws NoNaturalKeysDefinedException {
        // setup
        Entity mockEntity = Mockito.mock(Entity.class);
        NaturalKeyValidationException e = new NaturalKeyValidationException(null, null);
        
        String mockEntityType = "MockEntityType";
        
        when(mockEntity.getType()).thenReturn(mockEntityType);

        when(mockSchemaRepository.getSchema(mockEntityType)).thenReturn(null);

        when(mockNaturalKeyExtractor.getNaturalKeyFields(mockEntity)).thenThrow(e);

        // test
        boolean result = apiNeutralSchemaValidator.validate(mockEntity);

        // validate
        Assert.assertEquals(true, result);

    }

    @Test
    public void testGetValueOneLevel() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("key1", "value1");
        data.put("key2", "value2");
        data.put("key3", "value3");

        assertEquals("Should match", "value1", apiNeutralSchemaValidator.getValue("key1", data));
        assertEquals("Should match", "value2", apiNeutralSchemaValidator.getValue("key2", data));
        assertEquals("Should match", "value3", apiNeutralSchemaValidator.getValue("key3", data));
        assertEquals("Should match", null, apiNeutralSchemaValidator.getValue("key4", data));
    }

    @Test
    public void testGetValueMultiLevel() {
        Map<String, Object> inner2 = new HashMap<String, Object>();
        inner2.put("key7", "value7");
        inner2.put("key8", "value8");

        Map<String, Object> inner1 = new HashMap<String, Object>();
        inner1.put("key4", "value4");
        inner1.put("key5", "value5");
        inner1.put("key6", inner2);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("key1", "value1");
        data.put("key2", "value2");
        data.put("key3", inner1);

        assertEquals("Should match", "value1", apiNeutralSchemaValidator.getValue("key1", data));
        assertEquals("Should match", "value2", apiNeutralSchemaValidator.getValue("key2", data));

        assertEquals("Should match", inner1, apiNeutralSchemaValidator.getValue("key3", data));
        assertEquals("Should match", "value4", apiNeutralSchemaValidator.getValue("key3.key4", data));
        assertEquals("Should match", "value5", apiNeutralSchemaValidator.getValue("key3.key5", data));

        assertEquals("Should match", inner2, apiNeutralSchemaValidator.getValue("key3.key6", data));
        assertEquals("Should match", "value7", apiNeutralSchemaValidator.getValue("key3.key6.key7", data));
        assertEquals("Should match", "value8", apiNeutralSchemaValidator.getValue("key3.key6.key8", data));
    }
}
