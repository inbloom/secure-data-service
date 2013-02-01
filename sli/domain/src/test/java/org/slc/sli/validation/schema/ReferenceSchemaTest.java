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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.validation.DummyEntityRepository;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.ValidationError;

/**
 * Tests the ReferenceSchema methods
 *
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ReferenceSchemaTest {

    ReferenceSchema schema; // class under test

    @Autowired
    SchemaRepository schemaRepository;

    private ReferenceSchema spySchema;
    private DummyEntityRepository repo = new DummyEntityRepository();
    private static final String UUID = "123456789";
    private static final String REFERENCED_COLLECTION = "section";
    private static final String REFERENCE_FIELDNAME = "courseId";

    @Before
    public void setup() {
        schema = new ReferenceSchema("school", schemaRepository);

        Entity entity = mock(Entity.class);
        when(entity.getEntityId()).thenReturn(UUID);

        repo.addEntity(REFERENCED_COLLECTION, entity);

        AppInfo info = mock(AppInfo.class);
        when(info.getReferenceType()).thenReturn(REFERENCED_COLLECTION);

        spySchema = spy(schema);
        when(spySchema.getAppInfo()).thenReturn(info);
    }

    @Test
    public void testReferenceValidation() throws IllegalArgumentException {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        assertTrue("Reference entity validation failed", spySchema.validate(REFERENCE_FIELDNAME, UUID, errors, repo));
    }

    @Test
    public void testInvalidReferenceValidation() throws IllegalArgumentException {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        assertFalse("Invalid Reference entity validation failed",
                spySchema.validate(REFERENCE_FIELDNAME, "45679", errors, repo));
    }


    @Test
    public void testNullReferenceValidation() throws IllegalArgumentException {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        assertFalse("Null reference should not be valid",
                spySchema.validate(REFERENCE_FIELDNAME, null, errors, repo));
    }

    @Test
    public void testNonStringReferenceValidation() throws IllegalArgumentException {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        assertFalse("Non-string reference should not be valid",
                spySchema.validate(REFERENCE_FIELDNAME, new Integer(0), errors, repo));
    }

    @Test
    public void testEmptyStringReferenceValidation() throws IllegalArgumentException {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        assertFalse("Non-string reference should not be valid",
                spySchema.validate(REFERENCE_FIELDNAME, "", errors, repo));
    }
}
