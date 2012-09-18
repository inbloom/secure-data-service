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
package org.slc.sli.validation.schema;

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
import org.slc.sli.validation.SchemaRepository;

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
    public void validateNaturalKeyValidationExceptionFromMissingFields() {
        // setup
        Entity mockEntity = Mockito.mock(Entity.class);
        NaturalKeyValidationException e = new NaturalKeyValidationException(null, null);
        
        when(mockEntity.getType()).thenReturn("SomeEntityType");
        
        when(mockSchemaRepository.getSchema("SomeEntityType")).thenReturn(null);
        
        when(mockNaturalKeyExtractor.getNaturalKeyFields(mockEntity)).thenThrow(e);
        
        // test
        boolean result = apiNeutralSchemaValidator.validate(mockEntity);
        
        // validate
        Assert.assertEquals(false, result);
        
    }
}
