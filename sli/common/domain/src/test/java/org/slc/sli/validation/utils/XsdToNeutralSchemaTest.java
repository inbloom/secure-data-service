package org.slc.sli.validation.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.slc.sli.validation.NeutralSchemaFactory;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * JUnit for XsdToNeturalSchema
 * 
 * @author nbrown
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class XsdToNeutralSchemaTest {
    @Autowired
    SchemaRepository schemaRepo;
    
    @Test
    public void testSchema() throws IOException {
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("testSchemas",
                new NeutralSchemaFactory());
        Resource[] resources = {new FileSystemResource("src/test/resources/testSchemas/TestXMLSchema.xsd")};
        repo.generateSchemas(resources);
        NeutralSchema schema = repo.getSchema("TestComplexType");
        assertNotNull(schema);
        assertEquals("TestComplexType", schema.getType());
        NeutralSchema testStringSchema = (NeutralSchema) schema.getFields().get("testString");
        assertNotNull(testStringSchema);
        assertEquals("string", testStringSchema.getType());
        NeutralSchema testSimpleReferenceSchema = (NeutralSchema) schema.getFields().get("testSimpleReference");
        assertNotNull(testSimpleReferenceSchema);
        assertEquals("BaseSimpleType", testSimpleReferenceSchema.getType());
        NeutralSchema testDateSchema = (NeutralSchema) schema.getFields().get("testDate");
        assertNotNull(testDateSchema);
        assertEquals("date", testDateSchema.getType());
    }
    
    @Test
    public void testSliXsdSchema() throws IOException {
        assertNotNull(schemaRepo);
        assertNull(schemaRepo.getSchema("non-exist-schema"));
        // TODO add schemas to this as they are finalized
        String[] testSchemas = { };
        for (String testSchema : testSchemas) {
            assertNotNull("cant find schema: " + testSchema, schemaRepo.getSchema(testSchema));
            assertEquals(schemaRepo.getSchema(testSchema).getType(), testSchema);
            assertEquals(schemaRepo.getSchema(testSchema).getSchemaType(), NeutralSchemaType.COMPLEX);
        }
    }
}
