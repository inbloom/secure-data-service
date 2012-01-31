package org.slc.sli.validation.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import org.slc.sli.validation.schema.NeutralSchema;
import org.slc.sli.validation.NeutralSchemaFactory;

/**
 * JUnit for XsdToNeturalSchema
 * 
 * @author nbrown
 * 
 */
public class XsdToNeutralSchemaTest {
    
    @Test
    public void testSchema() throws IOException {
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("src/test/resources/testSchemas",
                new NeutralSchemaFactory());
        NeutralSchema schema = repo.getSchema("TestComplexType");
        assertNotNull(schema);
        assertEquals("TestComplexType", schema.getType());
        NeutralSchema testStringSchema = (NeutralSchema) schema.getFields().get("testString");
        assertNotNull(testStringSchema);
        assertEquals("string", testStringSchema.getType());
        NeutralSchema testSimpleReferenceSchema = (NeutralSchema) schema.getFields().get("testSimpleReference");
        assertNotNull(testSimpleReferenceSchema);
        assertEquals("SimpleType", testSimpleReferenceSchema.getType());
        NeutralSchema testDateSchema = (NeutralSchema) schema.getFields().get("testDate");
        assertNotNull(testDateSchema);
        assertEquals("date", testDateSchema.getType());
    }
    
}
