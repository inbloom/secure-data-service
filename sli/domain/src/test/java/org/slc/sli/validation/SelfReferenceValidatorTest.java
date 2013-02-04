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
package org.slc.sli.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.domain.Entity;
import org.slc.sli.validation.schema.INaturalKeyExtractor;


/**
 * @author ablum
 */
public class SelfReferenceValidatorTest {
	private static final String REFERENCE_FIELD = "parentEducationAgency";
	private static final String UUID = "3849403483783";

	@InjectMocks
	SelfReferenceValidator selfReferenceValidator = new SelfReferenceValidator();

	@Mock
	SelfReferenceExtractor selfReferenceExtractor;

    @Mock
    INaturalKeyExtractor naturalKeyExtractor;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(selfReferenceExtractor.getSelfReferenceFields(Mockito.any(Entity.class))).thenReturn(REFERENCE_FIELD);

    }

    @Test
    public void testValidate() {
        Entity entity = Mockito.mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(REFERENCE_FIELD, UUID);
        Mockito.when(entity.getBody()).thenReturn(body);
        Mockito.when(entity.getEntityId()).thenReturn(UUID);

        List<ValidationError> errors = new ArrayList<ValidationError>();

        boolean valid = selfReferenceValidator.validate(entity, errors);

        Assert.assertFalse(valid);

        Assert.assertEquals(1, errors.size());
    }


}
