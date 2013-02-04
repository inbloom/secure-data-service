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

package org.slc.sli.ingestion.transformation.normalization.did;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * unit tests for DidSchemaParser
 *
 * @author jtully
 *
 */
public class DidSchemaParserTest {

    DidSchemaParser didSchemaParser;

    private static final String SECTION_TYPE = "section";
    private static final String EDORG_TYPE = "educationOrganization";
    private static final String GRADEBOOKENTRY_TYPE = "gradebookEntry";
    private static final String OPTIONAL_REF_TYPE = "optionalRef";
    private static final String SECTION_KEY_FIELD = "uniqueSectionCode";
    private static final String SECTION_SCHOOL_KEYFIELD = "schoolId";
    private static final String SCHOOL_KEYFIELD = "stateOrganizationId";
    private static final String BASE_ENTITY_TYPE = "baseEntity";
    private static final String EXTENDED_ENTITY_TYPE = "extendedEntity";

    @Before
    public void setup() {
        didSchemaParser = new DidSchemaParser();
        didSchemaParser.setResourceLoader(new DefaultResourceLoader());
        didSchemaParser.setXsdLocation("classpath:test-schema/Ed-Fi-Core.xsd");
        didSchemaParser.setExtensionXsdLocation("classpath:test-schema/SLI-Ed-Fi-Core.xsd");
        didSchemaParser.setXsdParentLocation("classpath:test-schema");
        didSchemaParser.setExtensionXsdParentLocation("classpath:test-schema");
        didSchemaParser.setup();
    }

    @Test
    public void shouldExtractSimpleRefConfigs() {
        Map<String, DidRefConfig> refConfigs = didSchemaParser.getRefConfigs();
        Assert.assertEquals("Should extract 2 ref configs for the SLC section and edOrg referenceTypes", 2, refConfigs.size());
        Assert.assertTrue(refConfigs.containsKey(SECTION_TYPE));
        Assert.assertTrue(refConfigs.containsKey(EDORG_TYPE));

        DidRefConfig schoolRefConfig = refConfigs.get(EDORG_TYPE);
        Assert.assertNotNull(schoolRefConfig);
        Assert.assertEquals(EDORG_TYPE, schoolRefConfig.getEntityType());
        Assert.assertNotNull(schoolRefConfig.getKeyFields());
        Assert.assertEquals("nested schoolId ref should contain 1 key field", 1, schoolRefConfig.getKeyFields().size());

        KeyFieldDef schoolStateOrgId = schoolRefConfig.getKeyFields().get(0);
        Assert.assertNotNull(schoolStateOrgId);
        Assert.assertEquals(SCHOOL_KEYFIELD, schoolStateOrgId.getKeyFieldName());
        Assert.assertEquals("EducationalOrgIdentity.StateOrganizationId", schoolStateOrgId.getValueSource());
        Assert.assertNull("school stateOrgId should not contain a nested reference", schoolStateOrgId.getRefConfig());
    }

    @Test
    public void shouldExtractNestedRefConfigs() {
        Map<String, DidRefConfig> refConfigs = didSchemaParser.getRefConfigs();
        Assert.assertEquals("Should extract 2 ref configs for the SLC section and edOrg referenceTypes", 2, refConfigs.size());
        Assert.assertTrue(refConfigs.containsKey(SECTION_TYPE));
        Assert.assertTrue(refConfigs.containsKey(EDORG_TYPE));

        DidRefConfig sectionRefConfig = refConfigs.get(SECTION_TYPE);
        Assert.assertNotNull(sectionRefConfig);
        Assert.assertEquals(sectionRefConfig.getEntityType(), SECTION_TYPE);

        Assert.assertNotNull(sectionRefConfig.getKeyFields());
        List<KeyFieldDef> keyFields = sectionRefConfig.getKeyFields();
        Assert.assertEquals("keyFields list should contain 2 keyfields", 2, keyFields.size());

        //create a map from the list because we don't care about the order of key fields
        Map<String, KeyFieldDef> keyFieldMap = new HashMap<String, KeyFieldDef>();
        for (KeyFieldDef keyField : keyFields) {
            keyFieldMap.put(keyField.getKeyFieldName(), keyField);
        }

        Assert.assertTrue(keyFieldMap.containsKey(SECTION_KEY_FIELD));
        KeyFieldDef uniqSectionCode = keyFieldMap.get(SECTION_KEY_FIELD);
        Assert.assertNotNull(uniqSectionCode);
        Assert.assertEquals(SECTION_KEY_FIELD, uniqSectionCode.getKeyFieldName());
        Assert.assertNull("uniqueSectionCode should not have a nested DID", uniqSectionCode.getRefConfig());
        Assert.assertEquals("SectionIdentity.UniqueSectionCode", uniqSectionCode.getValueSource());

        Assert.assertTrue(keyFieldMap.containsKey(SECTION_SCHOOL_KEYFIELD));
        KeyFieldDef schoolId = keyFieldMap.get(SECTION_SCHOOL_KEYFIELD);

        Assert.assertEquals(SECTION_SCHOOL_KEYFIELD, schoolId.getKeyFieldName());
        Assert.assertNotNull("schoolId should have a nested DID", schoolId.getRefConfig());
        Assert.assertNotNull("schoolId should  have a value source", schoolId.getValueSource());
        Assert.assertNotNull("SectionIdentity.EducationalOrgReference", schoolId.getValueSource());

        DidRefConfig nestedRefConfig = schoolId.getRefConfig();
        Assert.assertEquals(EDORG_TYPE, nestedRefConfig.getEntityType());
        Assert.assertNotNull(nestedRefConfig.getKeyFields());
        Assert.assertEquals("nested schoolId ref should contain 1 key field", 1, nestedRefConfig.getKeyFields().size());

        KeyFieldDef stateOrgId = nestedRefConfig.getKeyFields().get(0);
        Assert.assertNotNull(stateOrgId);
        Assert.assertEquals(SCHOOL_KEYFIELD, stateOrgId.getKeyFieldName());
        Assert.assertEquals("EducationalOrgIdentity.StateOrganizationId", stateOrgId.getValueSource());
        Assert.assertNull("nested stateOrgId should not contain a nested reference", stateOrgId.getRefConfig());
    }

    @Test
    public void shouldExtractEntityConfigs() {
        Map<String, DidEntityConfig> entityConfigs = didSchemaParser.getEntityConfigs();

        Assert.assertEquals("Should extract 1 entity config for the 1 complexType containing a sectionReference (SLC-GradebookEntry)", 1, entityConfigs.size());

        //check the entity configs extracted are for the correct types
        Assert.assertTrue(entityConfigs.containsKey(GRADEBOOKENTRY_TYPE));

        //test the entityConfig for StudentSectionAssociation
        DidEntityConfig gbeConfig = entityConfigs.get(GRADEBOOKENTRY_TYPE);
        Assert.assertNotNull(gbeConfig);
        Assert.assertNotNull(gbeConfig.getReferenceSources());

        List<DidRefSource> refSources = gbeConfig.getReferenceSources();
        Assert.assertEquals("entity config should contain a single DidRefSource (section)", 1, refSources.size());
        DidRefSource refSource = refSources.get(0);
        Assert.assertNotNull(refSource);
        Assert.assertEquals(SECTION_TYPE, refSource.getEntityType());
        Assert.assertEquals("body.SectionReference", refSource.getSourceRefPath());
    }

    @Test
    public void shouldExtractEntityConfigsWithOptionalRefs() {
        //change to the OptionalRef xsd
        didSchemaParser.setExtensionXsdLocation("classpath:test-schema/OptionalRef-Extension.xsd");
        didSchemaParser.setup();

        Map<String, DidEntityConfig> entityConfigs = didSchemaParser.getEntityConfigs();

        Assert.assertEquals("Should extract 1 entity config for the 1 complexType containing a did ref (OptionalRefExample)", 1, entityConfigs.size());

        //check the entity configs extracted are for the correct types
        Assert.assertTrue(entityConfigs.containsKey(OPTIONAL_REF_TYPE));

        //test the entityConfig for StudentSectionAssociation
        DidEntityConfig gbeConfig = entityConfigs.get(OPTIONAL_REF_TYPE);
        Assert.assertNotNull(gbeConfig);
        Assert.assertNotNull(gbeConfig.getReferenceSources());

        List<DidRefSource> refSources = gbeConfig.getReferenceSources();
        Assert.assertEquals("entity config should contain a single DidRefSource (edOrg)", 1, refSources.size());
        DidRefSource refSource = refSources.get(0);
        Assert.assertNotNull(refSource);
        Assert.assertEquals(EDORG_TYPE, refSource.getEntityType());
        Assert.assertEquals(true, refSource.isOptional());
        Assert.assertEquals("body.OptionalSchoolRef", refSource.getSourceRefPath());
    }

    @Test
    public void shouldExtractEntityConfigsWithOptionalRefsFromChoice() {
        //change to the OptionalRef xsd
        didSchemaParser.setExtensionXsdLocation("classpath:test-schema/OptionalRefChoice-Extension.xsd");
        didSchemaParser.setup();

        Map<String, DidEntityConfig> entityConfigs = didSchemaParser.getEntityConfigs();

        Assert.assertEquals("Should extract 1 entity config for the 1 complexType containing a did ref (OptionalRefExample)", 1, entityConfigs.size());

        //check the entity configs extracted are for the correct types
        Assert.assertTrue(entityConfigs.containsKey(OPTIONAL_REF_TYPE));

        //test the entityConfig for StudentSectionAssociation
        DidEntityConfig gbeConfig = entityConfigs.get(OPTIONAL_REF_TYPE);
        Assert.assertNotNull(gbeConfig);
        Assert.assertNotNull(gbeConfig.getReferenceSources());

        List<DidRefSource> refSources = gbeConfig.getReferenceSources();
        Assert.assertEquals("entity config should contain 2 DidRefSource (edOrg)", 2, refSources.size());

        //put the refSources into a map since order doesn't matter
        Map<String, DidRefSource> refSourceMap = new HashMap<String, DidRefSource>();
        for (DidRefSource refSource : refSources) {
            refSourceMap.put(refSource.getSourceRefPath(), refSource);
        }

        Assert.assertTrue(refSourceMap.containsKey("body.OptionalSchoolRefA"));
        DidRefSource refSourceA = refSourceMap.get("body.OptionalSchoolRefA");
        Assert.assertNotNull(refSourceA);
        Assert.assertEquals(EDORG_TYPE, refSourceA.getEntityType());
        Assert.assertEquals(true, refSourceA.isOptional());
        Assert.assertEquals("body.OptionalSchoolRefA", refSourceA.getSourceRefPath());

        Assert.assertTrue(refSourceMap.containsKey("body.OptionalSchoolRefB"));
        DidRefSource refSourceB = refSourceMap.get("body.OptionalSchoolRefB");
        Assert.assertNotNull(refSourceB);
        Assert.assertEquals(EDORG_TYPE, refSourceB.getEntityType());
        Assert.assertEquals(true, refSourceB.isOptional());
        Assert.assertEquals("body.OptionalSchoolRefB", refSourceB.getSourceRefPath());
    }

    @Test
    public void shouldExtractEntityConfigsWithOptionalKeyFields() {
        //change to the OptionalRef xsd
        didSchemaParser.setExtensionXsdLocation("classpath:test-schema/OptionalKeyField-Extension.xsd");
        didSchemaParser.setup();

        Map<String, DidRefConfig> refConfigs = didSchemaParser.getRefConfigs();

        Assert.assertEquals("Should extract 1 ref config", 1, refConfigs.size());

        //check the entity configs extracted are for the correct types
        Assert.assertTrue(refConfigs.containsKey(EDORG_TYPE));

        //test the entityConfig for StudentSectionAssociation
        DidRefConfig edOrgConfig = refConfigs.get(EDORG_TYPE);
        Assert.assertNotNull(edOrgConfig);
        Assert.assertNotNull(edOrgConfig.getKeyFields());

        List<KeyFieldDef> keyFields = edOrgConfig.getKeyFields();
        Assert.assertEquals("entity config should contain 1 keyfield", 1, keyFields.size());

        Assert.assertNotNull(keyFields.get(0));
        KeyFieldDef keyField = keyFields.get(0);
        Assert.assertEquals("stateOrganizationId", keyField.getKeyFieldName());
        Assert.assertTrue(keyField.isOptional());
    }

    @Test
    public void shouldExtractEntityConfigsWithChoiceKeyFields() {
        //change to the OptionalRef xsd
        didSchemaParser.setExtensionXsdLocation("classpath:test-schema/OptionalChoiceKeyField-Extension.xsd");
        didSchemaParser.setup();

        Map<String, DidRefConfig> refConfigs = didSchemaParser.getRefConfigs();

        Assert.assertEquals("Should extract 1 ref config", 1, refConfigs.size());

        //check the entity configs extracted are for the correct types
        Assert.assertTrue(refConfigs.containsKey(EDORG_TYPE));

        //test the entityConfig for StudentSectionAssociation
        DidRefConfig edOrgConfig = refConfigs.get(EDORG_TYPE);
        Assert.assertNotNull(edOrgConfig);
        Assert.assertNotNull(edOrgConfig.getKeyFields());

        List<KeyFieldDef> keyFields = edOrgConfig.getKeyFields();
        Assert.assertEquals("entity config should contain 2 keyfield", 2, keyFields.size());

        //order doesn't matter so put them into a map
        Map<String, KeyFieldDef> keyFieldMap = new HashMap<String, KeyFieldDef>();
        for (KeyFieldDef keyField : keyFields) {
            keyFieldMap.put(keyField.getKeyFieldName(), keyField);
        }

        Assert.assertTrue(keyFieldMap.containsKey("keyFieldA"));
        Assert.assertTrue(keyFieldMap.containsKey("keyFieldB"));

        Assert.assertNotNull(keyFieldMap.get("keyFieldA"));
        KeyFieldDef keyFieldA = keyFieldMap.get("keyFieldA");
        Assert.assertEquals("keyFieldA", keyFieldA.getKeyFieldName());
        Assert.assertTrue(keyFieldA.isOptional());

        Assert.assertNotNull(keyFieldMap.get("keyFieldB"));
        KeyFieldDef keyFieldB = keyFieldMap.get("keyFieldB");
        Assert.assertEquals("keyFieldB", keyFieldB.getKeyFieldName());
        Assert.assertTrue(keyFieldB.isOptional());
    }

    @Test
    public void shouldExtractEntityConfigsWithRefsInBaseType() {
        //change to the OptionalRef xsd
        didSchemaParser.setExtensionXsdLocation("classpath:test-schema/RefInBaseEntity.xsd");
        didSchemaParser.setup();

        Map<String, DidEntityConfig> entityConfigs = didSchemaParser.getEntityConfigs();

        Assert.assertEquals("Should extract 2 entity configs for the base and extended type", 2, entityConfigs.size());

        //check the entity configs extracted are for the correct types
        Assert.assertTrue(entityConfigs.containsKey(BASE_ENTITY_TYPE));
        Assert.assertTrue(entityConfigs.containsKey(EXTENDED_ENTITY_TYPE));


        /*
        //test the entityConfig for StudentSectionAssociation
        DidEntityConfig gbeConfig = entityConfigs.get(OPTIONAL_REF_TYPE);
        Assert.assertNotNull(gbeConfig);
        Assert.assertNotNull(gbeConfig.getReferenceSources());

        List<DidRefSource> refSources = gbeConfig.getReferenceSources();
        Assert.assertEquals("entity config should contain a single DidRefSource (edOrg)", 1, refSources.size());
        DidRefSource refSource = refSources.get(0);
        Assert.assertNotNull(refSource);
        Assert.assertEquals(EDORG_TYPE, refSource.getEntityType());
        Assert.assertEquals(true, refSource.isOptional());
        Assert.assertEquals("body.OptionalSchoolRef", refSource.getSourceRefPath());
        */
    }

}
