package org.slc.sli.validation.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.enums.Right;
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
        
        NeutralSchema simpleDoc = repo.getSchema("TestDocumentationSimple");
        assertNotNull(simpleDoc);
        assertEquals("Test documentation.", simpleDoc.getDocumentation());
        
        NeutralSchema complexDoc = repo.getSchema("TestDocumentationComplex");
        assertNotNull(complexDoc);
        assertEquals("Test complex documentation.", complexDoc.getDocumentation());
        
        Map<String, NeutralSchema> fields = complexDoc.getFields();
        for (Map.Entry<String, NeutralSchema> entry : fields.entrySet()) {
            
            // base1 has no documentation
            if (entry.getKey().equals("base1")) {
                assertNull(entry.getValue().getDocumentation());
            }
            
            // simple does
            if (entry.getKey().equals("simple")) {
                assertNotNull(entry.getValue().getDocumentation());
                assertEquals("Test documentation.", entry.getValue().getDocumentation());
            }
        }
    }
    
    /**
     * Certain annotations are inheritable when the type is contained within another type.
     * For example, the annotation for personally identifiable information (PII) is inheritable.
     * If a complex type is marked as PII, all of its elements are automatically marked PII.
     * 
     * For security / restricted access annotations, we choose the most restrictive value.
     */
    @Test
    public void testInheritableAnnotations() throws IOException {
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("classpath:testSchemas", new NeutralSchemaFactory());
        repo.setApplicationContext(appContext);
        
        // Not marked PII
        NeutralSchema simpleDoc = repo.getSchema("TestDocumentationSimple");
        assertNotNull(simpleDoc);
        assertFalse(simpleDoc.isPersonallyIdentifiableInfo());
        
        // Simple type marked PII
        simpleDoc = repo.getSchema("TestPersonallyIdentifiableInfoSimple");
        assertNotNull(simpleDoc);
        assertTrue(simpleDoc.isPersonallyIdentifiableInfo());
        
        // All fields in a complex type marked PII are marked as PII unless explicitly set.
        NeutralSchema complexDoc = repo.getSchema("TestPersonallyIdentifiableInfoComplex");
        assertNotNull(complexDoc);
        assertTrue(complexDoc.isPersonallyIdentifiableInfo());
        Map<String, NeutralSchema> fields = complexDoc.getFields();
        for (Map.Entry<String, NeutralSchema> entry : fields.entrySet()) {
            assertTrue(entry.getValue().isPersonallyIdentifiableInfo());
        }
        
        // Fields in a complex type not marked PII use their type annotations.
        complexDoc = repo.getSchema("TestNotPersonallyIdentifiableInfoComplex");
        assertNotNull(complexDoc);
        assertFalse(complexDoc.isPersonallyIdentifiableInfo());
        fields = complexDoc.getFields();
        
        // expecting only one field
        assertTrue(fields.size() == 1);
        for (Map.Entry<String, NeutralSchema> entry : fields.entrySet()) {
            assertTrue(entry.getValue().isPersonallyIdentifiableInfo());
        }
        
        simpleDoc = repo.getSchema("TestSecuritySimple");
        assertNotNull(simpleDoc);
        assertNotNull(simpleDoc.getReadAuthority());
        assertTrue(simpleDoc.getReadAuthority() == Right.ADMIN_ACCESS);
        
        complexDoc = repo.getSchema("TestSecurityComplex");
        assertNotNull(complexDoc);
        assertTrue(complexDoc.getReadAuthority() == Right.READ_RESTRICTED);
        
        // attributes with more restrictive rights should maintain those rights.
        fields = complexDoc.getFields();
        
        for (Map.Entry<String, NeutralSchema> entry : fields.entrySet()) {
            if (entry.getKey().equals("security")) {
                assertTrue(entry.getValue().getReadAuthority() == Right.ADMIN_ACCESS);
                
            } else {
                assertTrue(entry.getValue().getReadAuthority() == Right.READ_RESTRICTED);
            }
        }
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
        
        NeutralSchema testStringSchema = schema.getFields().get("testString");
        assertNotNull(testStringSchema);
        assertEquals(NeutralSchemaType.STRING.getName(), testStringSchema.getType());
        assertEquals(StringSchema.class.getCanonicalName(), testStringSchema.getValidatorClass());
        
        NeutralSchema testSimpleReferenceSchema = schema.getFields().get("testSimpleReference");
        assertNotNull(testSimpleReferenceSchema);
        assertEquals("BaseSimpleType", testSimpleReferenceSchema.getType());
        assertEquals(StringSchema.class.getCanonicalName(), testSimpleReferenceSchema.getValidatorClass());
        assertEquals("1", testSimpleReferenceSchema.getProperties().get(Restriction.MIN_LENGTH.getValue()));
        assertEquals("30", testSimpleReferenceSchema.getProperties().get(Restriction.MAX_LENGTH.getValue()));
        
        NeutralSchema testDateSchema = schema.getFields().get("testDate");
        assertNotNull(testDateSchema);
        assertEquals("date", testDateSchema.getType());
        assertEquals(DateSchema.class.getCanonicalName(), testDateSchema.getValidatorClass());
        
        NeutralSchema anonSchema = schema.getFields().get("testAnonymousSimpleType");
        assertNotNull(anonSchema);
        assertEquals("testAnonymousSimpleType", anonSchema.getType());
        assertEquals(IntegerSchema.class.getCanonicalName(), anonSchema.getValidatorClass());
        assertEquals("1", anonSchema.getProperties().get(Restriction.MIN_INCLUSIVE.getValue()));
        assertEquals("2", anonSchema.getProperties().get(Restriction.MAX_INCLUSIVE.getValue()));
        
        NeutralSchema extSchema = schema.getFields().get("testComplexContentExtension");
        assertNotNull(extSchema);
        assertEquals("testComplexContentExtension", extSchema.getType());
        assertEquals(ComplexSchema.class.getCanonicalName(), extSchema.getValidatorClass());
        NeutralSchema extField1 = extSchema.getFields().get("stringElement");
        assertNotNull(extField1);
        assertEquals("string", extField1.getType());
        assertEquals(StringSchema.class.getCanonicalName(), extField1.getValidatorClass());
        NeutralSchema extField2 = extSchema.getFields().get("extension1");
        assertNotNull(extField2);
        assertEquals("complexType2", extField2.getType());
        assertEquals(ComplexSchema.class.getCanonicalName(), extField2.getValidatorClass());
        assertNotNull(extField2.getFields().get("intElement"));
        assertEquals(IntegerSchema.class.getCanonicalName(),
                extField2.getFields().get("intElement").getValidatorClass());
        NeutralSchema extField3 = extSchema.getFields().get("extension2");
        assertNotNull(extField3);
        assertEquals("BaseSimpleType", extField3.getType());
        assertEquals(StringSchema.class.getCanonicalName(), extField3.getValidatorClass());
        
        NeutralSchema cycleSchema = schema.getFields().get("WeeksInCycle");
        assertNotNull(cycleSchema);
        assertEquals("WeeksInCycle", cycleSchema.getType());
        assertEquals(IntegerSchema.class.getCanonicalName(), cycleSchema.getValidatorClass());
        assertEquals("1", cycleSchema.getProperties().get(Restriction.MIN_INCLUSIVE.getValue()));
        assertEquals("52", cycleSchema.getProperties().get(Restriction.MAX_INCLUSIVE.getValue()));
        
        NeutralSchema schema2 = repo.getSchema("TestComplexType2");
        assertNotNull(schema2);
        NeutralSchema cycle2Schema = schema2.getFields().get("WeeksInCycle");
        assertNotNull(schema2);
        assertEquals("WeeksInCycle2", cycle2Schema.getType());
        assertEquals(IntegerSchema.class.getCanonicalName(), cycle2Schema.getValidatorClass());
        assertEquals("1", cycle2Schema.getProperties().get(Restriction.MIN_INCLUSIVE.getValue()));
        assertEquals("99", cycle2Schema.getProperties().get(Restriction.MAX_INCLUSIVE.getValue()));
        NeutralSchema testDoubleSchema = schema.getFields().get("testDouble");
        assertNotNull(testDoubleSchema);
        assertEquals("double", testDoubleSchema.getType());
        assertEquals(DoubleSchema.class.getCanonicalName(), testDoubleSchema.getValidatorClass());
        
        NeutralSchema testFloatSchema = schema.getFields().get("testFloat");
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
