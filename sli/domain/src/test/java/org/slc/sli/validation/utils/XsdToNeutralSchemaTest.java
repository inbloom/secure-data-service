/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.validation.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.enums.Right;
import org.slc.sli.validation.NeutralSchemaFactory;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.ChoiceSchema;
import org.slc.sli.validation.schema.ComplexSchema;
import org.slc.sli.validation.schema.DateSchema;
import org.slc.sli.validation.schema.Documentation;
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
        Documentation doc = simpleDoc.getDocumentation();
        assertEquals("Test documentation.", doc.toString());

        NeutralSchema complexDoc = repo.getSchema("TestDocumentationComplex");
        assertNotNull(complexDoc);
        doc = complexDoc.getDocumentation();
        assertEquals("Test complex documentation.", doc.toString());

        Map<String, NeutralSchema> fields = complexDoc.getFields();
        for (Map.Entry<String, NeutralSchema> entry : fields.entrySet()) {

            // base1 has no documentation
            if (entry.getKey().equals("base1")) {
                assertNull(entry.getValue().getDocumentation());
            }

            // simple does
            if (entry.getKey().equals("simple")) {
                doc = entry.getValue().getDocumentation();
                assertNotNull(doc);
                assertEquals("Test documentation.", doc.toString());
            }
        }
    }

    /**
    * Test non-RelaxedBlacklist annotations within a simple string documentation.
    *
    *
    */
    @Test
    public void testNonRelaxedBlacklistStringType() throws IOException {

        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("classpath:testSchemas", new NeutralSchemaFactory());
        repo.setApplicationContext(appContext);

        NeutralSchema simpleDoc = repo.getSchema("TestNonRelaxedBlacklistString");
        assertNotNull(simpleDoc);
        assertEquals("TestNonRelaxedBlacklistString", simpleDoc.getType());
        assertTrue("TestNonRelaxedBlacklistString should NOT be RelaxedBlacklist", !simpleDoc.isRelaxedBlacklisted());
    }

    /**
    * Test RelaxedBlacklist annotations within a simple string documentation.
    *
    *
    */
    @Test
    public void testRelaxedBlacklistStringType() throws IOException {

        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("classpath:testSchemas", new NeutralSchemaFactory());
        repo.setApplicationContext(appContext);

        NeutralSchema simpleDoc = repo.getSchema("TestRelaxedBlacklistString");
        assertNotNull(simpleDoc);
        assertEquals("TestRelaxedBlacklistString", simpleDoc.getType());
        assertTrue("TestRelaxedBlacklistString should be RelaxedBlacklist", simpleDoc.isRelaxedBlacklisted());
    }

    /**
    * Test RelaxedBlacklist annotations within a complex string documentation sequence.
    *
    */
    @Test
    public void testTestRelaxedBlacklistSequenceComplex() throws IOException {

        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("classpath:testSchemas", new NeutralSchemaFactory());
        repo.setApplicationContext(appContext);

        NeutralSchema complexDoc = repo.getSchema("TestRelaxedBlacklistSequenceComplex");
        assertNotNull(complexDoc);

        Map<String, NeutralSchema> fields = complexDoc.getFields();
        for (Map.Entry<String, NeutralSchema> entry : fields.entrySet()) {

            // base1 has TestRelaxedBlacklistString documentation
            if (entry.getKey().equals("white")) {
                assertEquals("TestRelaxedBlacklistString", entry.getValue().getType());
                assertTrue("TestRelaxedBlacklistString should be RelaxedBlacklist", entry.getValue().isRelaxedBlacklisted());
            }

            // simple has TestNonRelaxedBlacklistString documentation
            if (entry.getKey().equals("nonwhite")) {
                assertEquals("TestNonRelaxedBlacklistString", entry.getValue().getType());
                assertTrue("TestNonRelaxedBlacklistString should NOT be RelaxedBlacklist", !entry.getValue().isRelaxedBlacklisted());
            }
        }
    }

    /**
    * Test RelaxedBlacklist annotations within a complex string documentation choice.
    *
    */
    @Test
    public void testTestRelaxedBlacklistChoiceComplex() throws IOException {

        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("classpath:testSchemas", new NeutralSchemaFactory());
        repo.setApplicationContext(appContext);

        NeutralSchema complexDoc = repo.getSchema("TestRelaxedBlacklistChoiceComplex");
        assertNotNull(complexDoc);

        Map<String, NeutralSchema> fields = complexDoc.getFields();
        for (Map.Entry<String, NeutralSchema> entry : fields.entrySet()) {

            // base1 has TestRelaxedBlacklistString documentation
            if (entry.getKey().equals("white")) {
                assertEquals("TestRelaxedBlacklistString", entry.getValue().getType());
                assertTrue("TestRelaxedBlacklistString should be RelaxedBlacklist", entry.getValue().isRelaxedBlacklisted());
            }

            // simple has TestNonRelaxedBlacklistString documentation
            if (entry.getKey().equals("nonwhite")) {
                assertEquals("TestNonRelaxedBlacklistString", entry.getValue().getType());
                assertTrue("TestNonRelaxedBlacklistString should NOT be RelaxedBlacklist", !entry.getValue().isRelaxedBlacklisted());
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
        AppInfo appInfo = simpleDoc.getAppInfo();
        assertNotNull(appInfo);

        // Simple type marked PII
        simpleDoc = repo.getSchema("TestPersonallyIdentifiableInfoSimple");
        assertNotNull(simpleDoc);
        appInfo = simpleDoc.getAppInfo();
        assertNotNull(appInfo);
        assertTrue(appInfo.isPersonallyIdentifiableInfo());

        // All fields in a complex type marked PII are marked as PII unless explicitly set.
        NeutralSchema complexDoc = repo.getSchema("TestPersonallyIdentifiableInfoComplex");
        assertNotNull(complexDoc);
        appInfo = complexDoc.getAppInfo();
        assertTrue(appInfo.isPersonallyIdentifiableInfo());
        Map<String, NeutralSchema> fields = complexDoc.getFields();
        for (Map.Entry<String, NeutralSchema> entry : fields.entrySet()) {
            appInfo = entry.getValue().getAppInfo();
            assertTrue(appInfo.isPersonallyIdentifiableInfo());
        }

        // Fields in a complex type not marked PII use their type annotations.
        complexDoc = repo.getSchema("TestNotPersonallyIdentifiableInfoComplex");
        assertNotNull(complexDoc);
        appInfo = complexDoc.getAppInfo();
        assertNull(appInfo);
        fields = complexDoc.getFields();

        // expecting only one field
        assertTrue(fields.size() == 1);
        for (Map.Entry<String, NeutralSchema> entry : fields.entrySet()) {
            appInfo = entry.getValue().getAppInfo();
            assertTrue(appInfo.isPersonallyIdentifiableInfo());
        }

        simpleDoc = repo.getSchema("TestSecuritySimple");
        assertNotNull(simpleDoc);
        appInfo = simpleDoc.getAppInfo();
        assertNotNull(appInfo.getReadAuthorities());
        assertTrue(appInfo.getReadAuthorities().contains(Right.ADMIN_ACCESS));

        complexDoc = repo.getSchema("TestSecurityComplex");
        assertNotNull(complexDoc);
        appInfo = complexDoc.getAppInfo();
        assertTrue(appInfo.getReadAuthorities().size() == 3);
        assertTrue(appInfo.getReadAuthorities().contains(Right.READ_RESTRICTED));
        assertTrue(appInfo.getReadAuthorities().contains(Right.READ_GENERAL));
        assertTrue(appInfo.getReadAuthorities().contains(Right.READ_PUBLIC));

        // attributes with more restrictive rights should maintain those rights.
        fields = complexDoc.getFields();

        for (Map.Entry<String, NeutralSchema> entry : fields.entrySet()) {
            appInfo = entry.getValue().getAppInfo();

            if (entry.getKey().equals("security")) {
                assertTrue(appInfo.getReadAuthorities().contains(Right.ADMIN_ACCESS));

            } else {
                assertTrue(appInfo.getReadAuthorities().contains(Right.READ_RESTRICTED));
            }
        }
    }


    /**
     * Certain annotations with restrictedForLogging flag
     */
    @Test
    public void testRestrictedForLogging() throws IOException {
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("classpath:testSchemas", new NeutralSchemaFactory());
        repo.setApplicationContext(appContext);

        // no restrictedForLogging flag
        NeutralSchema complexDoc = repo.getSchema("TestNotRestrictedForLoggingComplex");
        assertNotNull(complexDoc);
        AppInfo appInfo = complexDoc.getAppInfo();
        assertNotNull(appInfo);
        assertFalse(appInfo.isRestrictedFieldForLogging());

        // has restrictedForLogging flag set
        complexDoc = repo.getSchema("TestRestrictedForLoggingComplex");
        assertNotNull(complexDoc);
        appInfo = complexDoc.getAppInfo();
        assertNotNull(appInfo);
        assertTrue(appInfo.isRestrictedFieldForLogging());
    }

    @Test
    public void testChoiceSchema() throws IOException {
        XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("classpath:testSchemas", new NeutralSchemaFactory());
        repo.setApplicationContext(appContext);

        ChoiceSchema choiceSchema = (ChoiceSchema) repo.getSchema("TestChoiceNoMinOccurs");
        assertNotNull(choiceSchema);

        assertTrue(choiceSchema.getMinOccurs() == 0);
        assertTrue(choiceSchema.getMaxOccurs() == ChoiceSchema.UNBOUNDED);
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
        assertEquals("testAnonymousSimpleType1", anonSchema.getType());
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
        assertEquals(IntegerSchema.class.getCanonicalName(), extField2.getFields().get("intElement")
                .getValidatorClass());
        NeutralSchema extField3 = extSchema.getFields().get("extension2");
        assertNotNull(extField3);
        assertEquals("BaseSimpleType", extField3.getType());
        assertEquals(StringSchema.class.getCanonicalName(), extField3.getValidatorClass());

        NeutralSchema cycleSchema = schema.getFields().get("WeeksInCycle");
        assertNotNull(cycleSchema);
        assertEquals("WeeksInCycle1", cycleSchema.getType());
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

        NeutralSchema schema3 = repo.getSchema("TestComplexType3");
        assertNotNull(schema3);
        NeutralSchema simpleRestriction = schema3.getFields().get("SimpleTypeRestriction");
        assertNotNull(simpleRestriction);
        assertEquals("SimpleTypeRestriction1", simpleRestriction.getType());
        assertEquals(StringSchema.class.getCanonicalName(), simpleRestriction.getValidatorClass());
        assertEquals("40", simpleRestriction.getProperties().get(Restriction.MAX_LENGTH.getValue()));
        assertEquals("1", simpleRestriction.getProperties().get(Restriction.MIN_LENGTH.getValue()));
    }

    @Test
    public void testInvalidSchemas() throws IOException {
        try {
            XsdToNeutralSchemaRepo repo = new XsdToNeutralSchemaRepo("classpath:testUnsupportedSchemas/choice",
                    new NeutralSchemaFactory());
            repo.setApplicationContext(appContext);
            fail();
        } catch (RuntimeException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testEqualsHashCode() {
        // If someone were to override equals/hashCode, it could break the XSD parser.
        ChoiceSchema choice1 = new ChoiceSchema(0, 1);
        ChoiceSchema choice2 = new ChoiceSchema(0, 1);
        assertFalse(choice1.equals(choice2));
    }
}
