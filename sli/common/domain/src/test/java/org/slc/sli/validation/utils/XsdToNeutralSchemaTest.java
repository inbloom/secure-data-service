package org.slc.sli.validation.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.validation.NeutralSchemaFactory;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.ComplexSchema;
import org.slc.sli.validation.schema.DateSchema;
import org.slc.sli.validation.schema.DoubleSchema;
import org.slc.sli.validation.schema.IntegerSchema;
import org.slc.sli.validation.schema.NeutralSchema;
import org.slc.sli.validation.schema.Restriction;
import org.slc.sli.validation.schema.StringSchema;

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
    
    ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
            new String[] { "spring/applicationContext-test.xml" });
    
    @Test
    public void testSimpleType() throws IOException {
        
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("classpath:testSchemas", new NeutralSchemaFactory());
        repo.setApplicationContext(appContext);
        
        NeutralSchema baseSimpleType = repo.getSchema("BaseSimpleType");
        assertNotNull(baseSimpleType);
        assertEquals("BaseSimpleType", baseSimpleType.getType());
    }
    
    @Test
    public void testSchemaDocumentation() throws IOException {
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("classpath:testSchemas", new NeutralSchemaFactory());
        repo.setApplicationContext(appContext);
        
        NeutralSchema schema = repo.getSchema("TestPersonallyIdentifiableInfoSimple");
        assertNotNull(schema);
        
        schema = repo.getSchema("TestSecuritySimple");
        assertNotNull(schema);
    }
    
    @Test
    public void testSchema() throws IOException {
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("testSchemas", new NeutralSchemaFactory());
        Resource[] resources = { new FileSystemResource("src/test/resources/testSchemas/TestXMLSchema.xsd") };
        repo.generateSchemas(resources);
        NeutralSchema schema = repo.getSchema("TestComplexType");
        assertNotNull(schema);
        assertEquals("TestComplexType", schema.getType());
        assertEquals(ComplexSchema.class.getCanonicalName(), schema.getValidatorClass());
        
        NeutralSchema testStringSchema = (NeutralSchema) schema.getFields().get("testString");
        assertNotNull(testStringSchema);
        assertEquals(NeutralSchemaType.STRING.getName(), testStringSchema.getType());
        assertEquals(StringSchema.class.getCanonicalName(), testStringSchema.getValidatorClass());
        
        NeutralSchema testSimpleReferenceSchema = (NeutralSchema) schema.getFields().get("testSimpleReference");
        assertNotNull(testSimpleReferenceSchema);
        assertEquals("BaseSimpleType", testSimpleReferenceSchema.getType());
        assertEquals(StringSchema.class.getCanonicalName(), testSimpleReferenceSchema.getValidatorClass());
        assertEquals("1", testSimpleReferenceSchema.getProperties().get(Restriction.MIN_LENGTH.getValue()));
        assertEquals("30", testSimpleReferenceSchema.getProperties().get(Restriction.MAX_LENGTH.getValue()));
        
        NeutralSchema testDateSchema = (NeutralSchema) schema.getFields().get("testDate");
        assertNotNull(testDateSchema);
        assertEquals("date", testDateSchema.getType());
        assertEquals(DateSchema.class.getCanonicalName(), testDateSchema.getValidatorClass());
        
        NeutralSchema anonSchema = (NeutralSchema) schema.getFields().get("testAnonymousSimpleType");
        assertNotNull(anonSchema);
        assertEquals("testAnonymousSimpleType", anonSchema.getType());
        assertEquals(IntegerSchema.class.getCanonicalName(), anonSchema.getValidatorClass());
        assertEquals("1", anonSchema.getProperties().get(Restriction.MIN_INCLUSIVE.getValue()));
        assertEquals("2", anonSchema.getProperties().get(Restriction.MAX_INCLUSIVE.getValue()));
        
        NeutralSchema extSchema = (NeutralSchema) schema.getFields().get("testComplexContentExtension");
        assertNotNull(extSchema);
        assertEquals("testComplexContentExtension", extSchema.getType());
        assertEquals(ComplexSchema.class.getCanonicalName(), extSchema.getValidatorClass());
        NeutralSchema extField1 = (NeutralSchema) extSchema.getFields().get("stringElement");
        assertNotNull(extField1);
        assertEquals("string", extField1.getType());
        assertEquals(StringSchema.class.getCanonicalName(), extField1.getValidatorClass());
        NeutralSchema extField2 = (NeutralSchema) extSchema.getFields().get("extension1");
        assertNotNull(extField2);
        assertEquals("complexType2", extField2.getType());
        assertEquals(ComplexSchema.class.getCanonicalName(), extField2.getValidatorClass());
        assertNotNull(extField2.getFields().get("intElement"));
        assertEquals(IntegerSchema.class.getCanonicalName(),
                ((NeutralSchema) extField2.getFields().get("intElement")).getValidatorClass());
        NeutralSchema extField3 = (NeutralSchema) extSchema.getFields().get("extension2");
        assertNotNull(extField3);
        assertEquals("BaseSimpleType", extField3.getType());
        assertEquals(StringSchema.class.getCanonicalName(), extField3.getValidatorClass());
        
        NeutralSchema cycleSchema = (NeutralSchema) schema.getFields().get("WeeksInCycle");
        assertNotNull(cycleSchema);
        assertEquals("WeeksInCycle", cycleSchema.getType());
        assertEquals(IntegerSchema.class.getCanonicalName(), cycleSchema.getValidatorClass());
        assertEquals("1", cycleSchema.getProperties().get(Restriction.MIN_INCLUSIVE.getValue()));
        assertEquals("52", cycleSchema.getProperties().get(Restriction.MAX_INCLUSIVE.getValue()));
        
        NeutralSchema schema2 = repo.getSchema("TestComplexType2");
        assertNotNull(schema2);
        NeutralSchema cycle2Schema = (NeutralSchema) schema2.getFields().get("WeeksInCycle");
        assertNotNull(schema2);
        assertEquals("WeeksInCycle2", cycle2Schema.getType());
        assertEquals(IntegerSchema.class.getCanonicalName(), cycle2Schema.getValidatorClass());
        assertEquals("1", cycle2Schema.getProperties().get(Restriction.MIN_INCLUSIVE.getValue()));
        assertEquals("99", cycle2Schema.getProperties().get(Restriction.MAX_INCLUSIVE.getValue()));
        NeutralSchema testDoubleSchema = (NeutralSchema) schema.getFields().get("testDouble");
        assertNotNull(testDoubleSchema);
        assertEquals("double", testDoubleSchema.getType());
        assertEquals(DoubleSchema.class.getCanonicalName(), testDoubleSchema.getValidatorClass());
        
        NeutralSchema testFloatSchema = (NeutralSchema) schema.getFields().get("testFloat");
        assertNotNull(testFloatSchema);
        assertEquals("float", testFloatSchema.getType());
        assertEquals(DoubleSchema.class.getCanonicalName(), testFloatSchema.getValidatorClass());
        
    }
    
    @Test
    public void testSliXsdSchema() throws IOException {
        assertNotNull(schemaRepo);
        assertNull(schemaRepo.getSchema("non-exist-schema"));
        // TODO add schemas to this as they are finalized
        String[] testSchemas = {};
        for (String testSchema : testSchemas) {
            assertNotNull("cant find schema: " + testSchema, schemaRepo.getSchema(testSchema));
            assertEquals(schemaRepo.getSchema(testSchema).getType(), testSchema);
            assertEquals(schemaRepo.getSchema(testSchema).getSchemaType(), NeutralSchemaType.COMPLEX);
        }
    }
    
    
    
}
