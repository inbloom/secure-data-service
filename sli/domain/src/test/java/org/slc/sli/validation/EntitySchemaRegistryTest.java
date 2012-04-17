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
                "studentAssessmentAssociation", "studentSchoolAssociation", "studentSectionAssociation", "teacher",
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
