package org.slc.sli.validation;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        assertNotNull(schemaRegistry.findSchemaForType(school));

        Entity student = mock(Entity.class);
        when(student.getType()).thenReturn("student");
        assertNotNull(schemaRegistry.findSchemaForType(student));
        
        Entity studentSchoolAssociation = mock(Entity.class);
        when(studentSchoolAssociation.getType()).thenReturn("studentSchoolAssociation");
        assertNotNull(schemaRegistry.findSchemaForType(studentSchoolAssociation));

    }

}
