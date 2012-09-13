/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.landingzone.validation.TestErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.validation.SchemaRepository;

/**
 * Unit tests for DeterministicIdResolver
 *
 * @author jtully
 * @author vmcglaughlin
 *
 */
public class DeterministicIdResolverTest {
    @InjectMocks
    DeterministicIdResolver didResolver;

    @Mock
    private UUIDGeneratorStrategy didGenerator;

    @Mock
    private SchemaRepository schemaRepository;

    @Mock
    private DidEntityConfigFactory didEntityConfigs;

    @Mock
    private DidRefConfigFactory didRefConfigs;

    private static final String TENANT = "tenant";
    private static final String ENTITY_TYPE = "entity_type";
    private static final String SRC_KEY_FIELD = "key_field";
    private static final String SRC_KEY_VALUE = "key_value";
    private static final String DID_VALUE = "did_value";
    private static final String DID_TARGET_FIELD = "id_field";
    private static final String SRC_REF_FIELD = "ref_field";

    private static final String NON_DID_ENTITY_TYPE = "non_did_entity_type";

    @Before
    public void setup() {
        didResolver = new DeterministicIdResolver();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldResolveSimpleDid() throws IOException {
        Entity entity = createSourceEntity();

        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        Mockito.when(didRefConfigs.getDidRefConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(refConfig);
        Mockito.when(didEntityConfigs.getDidEntityConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(entityConfig);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put(SRC_KEY_FIELD, SRC_KEY_VALUE);
        String entityType = ENTITY_TYPE;
        String tenantId = TENANT;
        NaturalKeyDescriptor ndk = new NaturalKeyDescriptor(naturalKeys, tenantId, entityType);

        Mockito.when(didGenerator.generateId(ndk)).thenReturn(DID_VALUE);

        ErrorReport errorReport = new TestErrorReport();

        didResolver.resolveInternalIds(entity, TENANT, errorReport);

        Assert.assertEquals(DID_VALUE, entity.getBody().get(DID_TARGET_FIELD));
        Assert.assertFalse("no errors should be reported from reference resolution ", errorReport.hasErrors());
    }

    @Test
    public void shouldFailFastForNonDidEntities() {
        Entity entity = createSourceEntity();
        ErrorReport errorReport = new TestErrorReport();

        // test null entity config
        Mockito.when(didEntityConfigs.getDidEntityConfiguration(Mockito.eq(NON_DID_ENTITY_TYPE))).thenReturn(null);
        didResolver.resolveInternalIds(entity, TENANT, errorReport);
        Mockito.verifyZeroInteractions(didRefConfigs);

        // test null ref config list
        DidEntityConfig entityConfig = Mockito.mock(DidEntityConfig.class);
        Mockito.when(didEntityConfigs.getDidEntityConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(entityConfig);
        Mockito.when(entityConfig.getReferenceSources()).thenReturn(null);
        didResolver.resolveInternalIds(entity, TENANT, errorReport);
        Mockito.verifyZeroInteractions(didRefConfigs);

        // test empty ref config list
        Mockito.when(entityConfig.getReferenceSources()).thenReturn(new ArrayList<DidRefSource>());
        didResolver.resolveInternalIds(entity, TENANT, errorReport);
        Mockito.verifyZeroInteractions(didRefConfigs);
    }

    @Test
    public void shouldResolveNestedDid() throws IOException {

        // TODO get this test working correctly

        DidRefConfig refConfig = createRefConfig("test_DID_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("test_DID_entity_config.json");
        ErrorReport errorReport = new TestErrorReport();
        Entity entity = createSourceEntity();
        Mockito.when(didRefConfigs.getDidRefConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(refConfig);
        Mockito.when(didEntityConfigs.getDidEntityConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(entityConfig);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);
        Mockito.when(didGenerator.generateId(Mockito.any(NaturalKeyDescriptor.class))).thenReturn(DID_VALUE);
        didResolver.resolveInternalIds(entity, TENANT, errorReport);

    }

    @Test
    public void testErrorReportingOnRefConfigEntityTypeEmpty() throws IOException {
        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config_entityType_empty.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        testGenericErrorReporting(refConfig, entityConfig);
    }

    @Test
    public void testErrorReportingOnRefConfigEntityTypeMissing() throws IOException {
        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config_entityType_missing.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        testGenericErrorReporting(refConfig, entityConfig);
    }

    @Test
    public void testErrorReportingOnRefConfigKeyFieldNameEmpty() throws IOException {
        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config_keyFieldName_empty.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        testGenericErrorReporting(refConfig, entityConfig);
    }

    @Test
    public void testErrorReportingOnRefConfigKeyFieldNameMissing() throws IOException {
        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config_keyFieldName_missing.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        testGenericErrorReporting(refConfig, entityConfig);
    }

    @Test
    public void testErrorReportingOnRefConfigKeyFieldsEmpty() throws IOException {
        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config_keyFields_empty.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        testGenericErrorReporting(refConfig, entityConfig);
    }

    @Test
    public void testErrorReportingOnRefConfigKeyFieldsMissing() throws IOException {
        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config_keyFields_missing.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        testGenericErrorReporting(refConfig, entityConfig);
    }

    @Test
    public void testErrorReportingOnRefConfigValueSourceEmpty() throws IOException {
        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config_valueSource_empty.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        testGenericErrorReporting(refConfig, entityConfig);
    }

    @Test
    public void testErrorReportingOnRefConfigValueSourceMissing() throws IOException {
        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config_valueSource_missing.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        testGenericErrorReporting(refConfig, entityConfig);
    }

    @Test
    public void testErrorReportingOnEntityConfigDidFieldPathEmpty() throws IOException {
        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config_didFieldPath_empty.json");

        testGenericErrorReporting(refConfig, entityConfig);
    }

    private void testGenericErrorReporting(DidRefConfig refConfig, DidEntityConfig entityConfig) {
        ErrorReport errorReport = new TestErrorReport();
        Entity entity = createSourceEntity();
        Mockito.when(didRefConfigs.getDidRefConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(refConfig);
        Mockito.when(didEntityConfigs.getDidEntityConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(entityConfig);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);
        Mockito.when(didGenerator.generateId(Mockito.any(NaturalKeyDescriptor.class))).thenReturn(DID_VALUE);
        didResolver.resolveInternalIds(entity, TENANT, errorReport);

        Assert.assertNull("Id should not have been resolved", entity.getBody().get(DID_TARGET_FIELD));
        Assert.assertTrue("Errors should be reported from reference resolution ", errorReport.hasErrors());
    }

    @Test
    public void testErrorReportingOnEntityRefFieldMissing() throws IOException {
        // entity doesn't have property to get at sourceRefPath
        ErrorReport errorReport = new TestErrorReport();
        Entity entity = createSourceEntityMissingRefField();
        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        Mockito.when(didRefConfigs.getDidRefConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(refConfig);
        Mockito.when(didEntityConfigs.getDidEntityConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(entityConfig);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);
        didResolver.resolveInternalIds(entity, TENANT, errorReport);

        Assert.assertNull("Id should not have been resolved", entity.getBody().get(DID_TARGET_FIELD));
        Assert.assertTrue("Errors should be reported from reference resolution ", errorReport.hasErrors());
    }

    @Test
    public void testErrorReportingOnFactoryMissingRefConfigForEntityType() throws IOException {
        ErrorReport errorReport = new TestErrorReport();
        Entity entity = createSourceEntityMissingRefField();
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        Mockito.when(didRefConfigs.getDidRefConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(null);
        Mockito.when(didEntityConfigs.getDidEntityConfiguration(Mockito.eq(ENTITY_TYPE))).thenReturn(entityConfig);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);
        didResolver.resolveInternalIds(entity, TENANT, errorReport);

        Assert.assertNull("Id should not have been resolved", entity.getBody().get(DID_TARGET_FIELD));
        Assert.assertFalse("No errors should be reported from reference resolution ", errorReport.hasErrors());
        Mockito.verifyZeroInteractions(schemaRepository);
    }

    private DidEntityConfig createEntityConfig(String fileName) throws IOException {
        Resource jsonFile =  new ClassPathResource("DeterministicIdResolverConfigs/" + fileName);
        DidEntityConfig entityConfig  = DidEntityConfig.parse(jsonFile.getInputStream());
        return entityConfig;
    }

    private DidRefConfig createRefConfig(String fileName) throws IOException {
        Resource jsonFile =  new ClassPathResource("DeterministicIdResolverConfigs/" + fileName);
        DidRefConfig refConfig  = DidRefConfig.parse(jsonFile.getInputStream());
        return refConfig;
    }

    /**
     * Create an entity in which the Id has already been resolved
     */
    private Entity createSourceEntity() {

        Map<String, String> refObject = new HashMap<String, String>();
        refObject.put(SRC_KEY_FIELD, SRC_KEY_VALUE);

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(SRC_REF_FIELD, refObject);

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);
        nr.setRecordType(ENTITY_TYPE);

        Entity entity = new NeutralRecordEntity(nr);

        return entity;
    }

    private Entity createSourceEntityMissingRefField() {

        Map<String, Object> attributes = new HashMap<String, Object>();

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);
        nr.setRecordType(ENTITY_TYPE);

        Entity entity = new NeutralRecordEntity(nr);

        return entity;
    }


}
