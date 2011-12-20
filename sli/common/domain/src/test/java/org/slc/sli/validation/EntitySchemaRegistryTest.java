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
        Entity school = mock(Entity.class);
        when(school.getType()).thenReturn("school");
        Schema schoolSchema = schemaRegistry.findSchemaForType(school);
        assertNotNull(schoolSchema);
        assertEquals(schoolSchema.getName(), "School");

        Entity student = mock(Entity.class);
        when(student.getType()).thenReturn("student");
        Schema studentSchema = schemaRegistry.findSchemaForType(student);
        assertNotNull(studentSchema);
        assertEquals(studentSchema.getName(), "Student");
        
        Entity studentSchoolAssociation = mock(Entity.class);
        when(studentSchoolAssociation.getType()).thenReturn("studentSchoolAssociation");
        Schema studentSchoolAssociationSchema = schemaRegistry.findSchemaForType(studentSchoolAssociation);
        assertNotNull(studentSchoolAssociationSchema);
        assertEquals(studentSchoolAssociationSchema.getName(), "StudentSchoolAssociation");

    }

}
