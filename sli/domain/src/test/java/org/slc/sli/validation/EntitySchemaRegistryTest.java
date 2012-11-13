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


package org.slc.sli.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.avro.Schema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for EntitySchemaRegistry
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class EntitySchemaRegistryTest {
    @Autowired
    EntitySchemaRegistry schemaRegistry;

    @Test
    public void testFindSchemaForType() {
        String[] schemaNames = new String[] { "assessment", "student", "school", "section",
                "studentAssessment", "studentSchoolAssociation", "studentSectionAssociation", "teacher",
                "teacherSchoolAssociation", "teacherSectionAssociation" };
        for (String schemaName : schemaNames) {
            checkSchema(schemaName);
        }
    }

    private void checkSchema(String schemaName) {
        Entity entity = mock(Entity.class);
        when(entity.getType()).thenReturn(schemaName);
        Schema schema = schemaRegistry.findSchemaForType(entity);
        assertNotNull(schema);
        assertEquals(schema.getName(), schemaName.substring(0, 1).toUpperCase() + schemaName.substring(1));
    }
}
