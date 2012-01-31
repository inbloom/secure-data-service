package org.slc.sli.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.validation.schema.NeutralSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;

/**
 * JUnits for NeutralSchemaRegistry
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class NeutralSchemaRegistryTest {
    
    @Autowired
    NeutralSchemaRegistry schemaRegistry;
    
    @Test
    public void testInitialize() throws IOException {
        
        assertTrue("Schema Registry is empty", (schemaRegistry.size() > 0));
        
        String schemaPath = NeutralSchemaRegistry.SCHEMA_PATH;
        String schemaRepresentation = NeutralSchemaRegistry.JSON;
        URL schemaResourcesUrl = ResourceUtils.getURL(schemaPath);
        File schemaResourcesDir = FileUtils.toFile(schemaResourcesUrl);
        List<File> schemaFiles = new ArrayList<File>(FileUtils.listFiles(schemaResourcesDir,
                new String[] { schemaRepresentation }, true));
        
        for (File schemaFile : schemaFiles) {
            String schemaFileName = schemaFile.getName();
            int suffixIndex = schemaFileName.lastIndexOf(schemaRegistry.getSchemaRepresentation());
            schemaFileName = schemaFileName.substring(0, suffixIndex - 1);
            NeutralSchema schema = schemaRegistry.getSchemaMap().get(schemaFileName);
            
            assertNotNull("Schema Registry entry not found for schema file: " + schemaFile.getName(), schema);
            assertEquals("Schema Registry entry does not match for schema file: " + schemaFile.getName(),
                    schema.getType(), schemaFileName);
        }
        
    }
    
}
