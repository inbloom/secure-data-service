package org.slc.sli.validation.utils;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import org.slc.sli.validation.NeutralSchema;
import org.slc.sli.validation.NeutralSchemaFactory;

/**
 * JUnit for XsdToNeturalSchema
 * 
 * @author nbrown
 * 
 */
public class XsdToNeutralSchemaTest {
    
    @Test
    public void test() throws IOException {
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("src/test/resources/testSchemas",
                new NeutralSchemaFactory());
        NeutralSchema schema = repo.getSchema("TestComplexType");
        assertNotNull(schema);
    }
    
}
