package org.slc.sli.validation;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

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
        Entity school = new Entity() {
            public String getEntityId() {
                return "";
            }
            
            public String getType() {
                return "school";
            }
            
            public Map<String, Object> getBody() {
                return null;
            }
        };
        assertNotNull(schemaRegistry.findSchemaForType(school));

    }

}
