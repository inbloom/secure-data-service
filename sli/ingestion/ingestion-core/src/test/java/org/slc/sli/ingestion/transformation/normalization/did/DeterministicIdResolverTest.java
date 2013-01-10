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
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
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
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.DummyMessageReport;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;
import org.slc.sli.ingestion.transformation.normalization.Ref;
import org.slc.sli.ingestion.transformation.normalization.RefDef;
import org.slc.sli.validation.SchemaRepository;

/**
 * Unit tests for DeterministicIdResolver
 *
 * @author jtully
 * @author vmcglaughlin
 *
 */
public class DeterministicIdResolverTest {

    ObjectMapper MAPPER = new ObjectMapper();

    @InjectMocks
    DeterministicIdResolver didResolver;

    @Mock
    private UUIDGeneratorStrategy didGenerator;

    @Mock
    private SchemaRepository schemaRepository;

    @Mock
    private DidSchemaParser didSchemaParser;

    @Mock
    private DidEntityConfigReader didEntityConfigReader;

    @Mock
    private EntityConfigFactory entityConfigs;

    private static final String TENANT = "tenant";
    private static final String ENTITY_TYPE = "entity_type";
    private static final String SRC_KEY_FIELD = "key_field";
    private static final String SRC_KEY_VALUE = "key_value";
    private static final String DID_VALUE = "did_value";

    private static final String REF_FIELD = "ref_field";

    private static final String NESTED_SRC_KEY_FIELD = "nested_key_field";
    private static final String NESTED_ENTITY_TYPE = "nested_entity_type";
    private static final String NESTED_KEY_SOURCE = "nested_key_source";
    private static final String NESTED_SRC_KEY_VALUE = "nested_key_value";
    private static final String NESTED_REF = "nested_reference";
    private static final String NESTED_DID_VALUE = "nested_did_value";

    private static final String NON_DID_ENTITY_TYPE = "non_did_entity_type";

    private static final String SRC_KEY_VALUE_1 = "key_value_1";
    private static final String SRC_KEY_VALUE_2 = "key_value_2";
    private static final String DID_VALUE_1 = "did_value_1";
    private static final String DID_VALUE_2 = "did_value_2";

    private static final String EMBEDDED_LIST_FIELD = "embeddedList";

    @Before
    public void setup() {
        didResolver = new DeterministicIdResolver();
        entityConfigs = Mockito.mock(EntityConfigFactory.class);
        didEntityConfigReader = Mockito.mock(DidEntityConfigReader.class);
        Mockito.when(entityConfigs.getEntityConfiguration(Mockito.anyString())).thenReturn(null);

        MockitoAnnotations.initMocks(this);
    }

    private void mockRefConfig(DidRefConfig didRefConfig, String refType) {
        Map<String, DidRefConfig> refConfigMap = new HashMap<String, DidRefConfig>();
        refConfigMap.put(refType, didRefConfig);
        Mockito.when(didSchemaParser.getRefConfigs()).thenReturn(refConfigMap);
    }

    private void mockEntityConfig(DidEntityConfig didEntityConfig, String entityType) {
        Map<String, DidEntityConfig> entityConfigMap = new HashMap<String, DidEntityConfig>();
        entityConfigMap.put(entityType, didEntityConfig);
        Mockito.when(didSchemaParser.getEntityConfigs()).thenReturn(entityConfigMap);
    }

    @Test
    public void shouldAddAppropriateContextForReference() throws IOException {
        NeutralRecordEntity entity = createSourceEntity();

        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        mockRefConfig(refConfig, ENTITY_TYPE);
        mockEntityConfig(entityConfig, ENTITY_TYPE);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put(SRC_KEY_FIELD, SRC_KEY_VALUE);
        String entityType = ENTITY_TYPE;
        String tenantId = TENANT;
        NaturalKeyDescriptor ndk = new NaturalKeyDescriptor(naturalKeys, tenantId, entityType, null);

        Mockito.when(didGenerator.generateId(Mockito.eq(ndk))).thenReturn(DID_VALUE);

        AbstractMessageReport errorReport = new DummyMessageReport();

        EntityConfig oldEntityConfig = Mockito.mock(EntityConfig.class);
        Mockito.when(entityConfigs.getEntityConfiguration(Mockito.anyString())).thenReturn(oldEntityConfig);
        List<RefDef> references = new ArrayList<RefDef>();
        Mockito.when(oldEntityConfig.getReferences()).thenReturn(references);

        RefDef refDef1 = Mockito.mock(RefDef.class);
        Ref ref1 = Mockito.mock(Ref.class);
        Mockito.when(ref1.getEntityType()).thenReturn(ENTITY_TYPE);
        Mockito.when(refDef1.getRef()).thenReturn(ref1);
        references.add(refDef1);

        RefDef refDef2 = Mockito.mock(RefDef.class);
        Ref ref2 = Mockito.mock(Ref.class);
        Mockito.when(ref2.getEntityType()).thenReturn("wrongEntityType");
        Mockito.when(refDef2.getRef()).thenReturn(ref2);
        references.add(refDef2);

        ReportStats reportStats = new SimpleReportStats();
        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);

        Assert.assertEquals(DID_VALUE, entity.getBody().get(REF_FIELD));
        Assert.assertFalse("no errors should be reported from reference resolution ", reportStats.hasErrors());
    }

    @Test
    public void shouldResolveSimpleDid() throws IOException {
        NeutralRecordEntity entity = createSourceEntity();

        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        mockRefConfig(refConfig, ENTITY_TYPE);
        mockEntityConfig(entityConfig, ENTITY_TYPE);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put(SRC_KEY_FIELD, SRC_KEY_VALUE);
        String entityType = ENTITY_TYPE;
        String tenantId = TENANT;
        NaturalKeyDescriptor ndk = new NaturalKeyDescriptor(naturalKeys, tenantId, entityType, null);

        Mockito.when(didGenerator.generateId(Mockito.eq(ndk))).thenReturn(DID_VALUE);

        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);

        Assert.assertEquals(DID_VALUE, entity.getBody().get(REF_FIELD));
        Assert.assertFalse("no errors should be reported from reference resolution ", reportStats.hasErrors());
    }

    @Test
    public void shouldIgnoreOptionalEmptyRefs() throws IOException {
        NeutralRecordEntity entity = createSourceEntity();

        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("optional_DID_entity_config.json");

        mockRefConfig(refConfig, ENTITY_TYPE);
        mockEntityConfig(entityConfig, ENTITY_TYPE);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put(SRC_KEY_FIELD, SRC_KEY_VALUE);
        String entityType = ENTITY_TYPE;
        String tenantId = TENANT;
        NaturalKeyDescriptor ndk = new NaturalKeyDescriptor(naturalKeys, tenantId, entityType, null);

        Mockito.when(didGenerator.generateId(Mockito.eq(ndk))).thenReturn(DID_VALUE);

        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);

        // assert that the other reference is resolved and that no errors are reported
        Assert.assertEquals(DID_VALUE, entity.getBody().get(REF_FIELD));
        Assert.assertFalse("no errors should be reported from reference resolution ", reportStats.hasErrors());
    }

    @Test
    public void shouldResolveListOfReferences() throws IOException {
        NeutralRecordEntity entity = createSourceEntityWithRefList();

        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        mockRefConfig(refConfig, ENTITY_TYPE);
        mockEntityConfig(entityConfig, ENTITY_TYPE);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);

        Map<String, String> naturalKeys1 = new HashMap<String, String>();
        naturalKeys1.put(SRC_KEY_FIELD, SRC_KEY_VALUE_1);
        NaturalKeyDescriptor ndk1 = new NaturalKeyDescriptor(naturalKeys1, TENANT, ENTITY_TYPE, null);

        Map<String, String> naturalKeys2 = new HashMap<String, String>();
        naturalKeys2.put(SRC_KEY_FIELD, SRC_KEY_VALUE_2);
        NaturalKeyDescriptor ndk2 = new NaturalKeyDescriptor(naturalKeys2, TENANT, ENTITY_TYPE, null);

        Mockito.when(didGenerator.generateId(Mockito.eq(ndk1))).thenReturn(DID_VALUE_1);
        Mockito.when(didGenerator.generateId(Mockito.eq(ndk2))).thenReturn(DID_VALUE_2);

        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);

        @SuppressWarnings("unchecked")
        List<String> did_list = (List<String>) entity.getBody().get(REF_FIELD);

        Assert.assertNotNull(did_list);
        Assert.assertEquals(2, did_list.size());

        Assert.assertEquals(DID_VALUE_1, did_list.get(0));
        Assert.assertEquals(DID_VALUE_2, did_list.get(1));

        Assert.assertFalse("no errors should be reported from reference resolution ", reportStats.hasErrors());
    }

    @Test
    public void shouldFailFastForNonDidEntities() {
        NeutralRecordEntity entity = createSourceEntity();
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        // test null entity config
        mockEntityConfig(null, NON_DID_ENTITY_TYPE);

        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);
        Mockito.verify(didSchemaParser, Mockito.never()).getRefConfigs();

        // test null ref config list
        DidEntityConfig entityConfig = Mockito.mock(DidEntityConfig.class);
        mockEntityConfig(entityConfig, ENTITY_TYPE);
        Mockito.when(entityConfig.getReferenceSources()).thenReturn(null);
        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);
        Mockito.verify(didSchemaParser, Mockito.never()).getRefConfigs();

        // test empty ref config list
        Mockito.when(entityConfig.getReferenceSources()).thenReturn(new ArrayList<DidRefSource>());
        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);
        Mockito.verify(didSchemaParser, Mockito.never()).getRefConfigs();
    }

    @Test
    public void shouldResolveNestedDid() throws IOException {

        DidRefConfig refConfig = createRefConfig("nested_DID_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        NeutralRecordEntity entity = createNestedSourceEntity();

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put(SRC_KEY_FIELD, NESTED_DID_VALUE);
        String entityType = ENTITY_TYPE;
        String tenantId = TENANT;
        NaturalKeyDescriptor ndk = new NaturalKeyDescriptor(naturalKeys, tenantId, entityType, null);

        Map<String, String> nestedNaturalKeys = new HashMap<String, String>();
        nestedNaturalKeys.put(NESTED_SRC_KEY_FIELD, NESTED_SRC_KEY_VALUE);
        NaturalKeyDescriptor nestedNdk = new NaturalKeyDescriptor(nestedNaturalKeys, tenantId, NESTED_ENTITY_TYPE, null);

        mockRefConfig(refConfig, ENTITY_TYPE);
        mockEntityConfig(entityConfig, ENTITY_TYPE);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);
        Mockito.when(didGenerator.generateId(Mockito.eq(ndk))).thenReturn(DID_VALUE);
        Mockito.when(didGenerator.generateId(Mockito.eq(nestedNdk))).thenReturn(NESTED_DID_VALUE);
        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);

        Object resolvedId = entity.getBody().get(REF_FIELD);
        Assert.assertEquals(DID_VALUE, resolvedId);
        Assert.assertFalse("no errors should be reported from reference resolution ", reportStats.hasErrors());

    }

    @Test
    public void shouldResolveNestedDidWithOptionalNestedReference() throws IOException {

        NeutralRecordEntity entity = createEntity("NeutralRecord_StudentTranscriptAssoc_missingOptionalEdOrg.json");
        DidRefConfig refConfig = createRefConfig("StudentAcademicRecord_optional_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("StudentTranscriptAssoc_entity_config.json");
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        Map<String, String> naturalKeys = new HashMap<String, String>();
        naturalKeys.put("schoolId", "");
        naturalKeys.put("sessionName", "Spring 2011 East Daybreak Junior High");
        naturalKeys.put("schoolId", "");
        String tenantId = TENANT;
        NaturalKeyDescriptor sessionNKD = new NaturalKeyDescriptor(naturalKeys, tenantId, "session", null);
        Mockito.when(didGenerator.generateId(Mockito.eq(sessionNKD))).thenReturn("sessionDID");

        naturalKeys = new HashMap<String, String>();
        naturalKeys.put("sessionId", "sessionDID");
        NaturalKeyDescriptor studentAcademicRecordNKD = new NaturalKeyDescriptor(naturalKeys, tenantId,
                "studentAcademicRecord", null);
        Mockito.when(didGenerator.generateId(Mockito.eq(studentAcademicRecordNKD))).thenReturn(
                "studentAcademicRecordDID");

        mockRefConfig(refConfig, "studentAcademicRecord");
        mockEntityConfig(entityConfig, "studentTranscriptAssociation");
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);

        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);

        Object resolvedId = entity.getBody().get("StudentAcademicRecordReference");
        Assert.assertEquals("studentAcademicRecordDID", resolvedId);
        Assert.assertFalse("no errors should be reported from reference resolution ", reportStats.hasErrors());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldResolveDidsInEmbeddedList() throws IOException {
        NeutralRecordEntity entity = createSourceEmbeddedEntity();

        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("Embedded_DID_entity_config.json");

        mockRefConfig(refConfig, ENTITY_TYPE);
        mockEntityConfig(entityConfig, ENTITY_TYPE);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);

        Map<String, String> naturalKeys1 = new HashMap<String, String>();
        naturalKeys1.put(SRC_KEY_FIELD, SRC_KEY_VALUE_1);
        String entityType = ENTITY_TYPE;
        String tenantId = TENANT;
        NaturalKeyDescriptor ndk1 = new NaturalKeyDescriptor(naturalKeys1, tenantId, entityType, null);

        Map<String, String> naturalKeys2 = new HashMap<String, String>();
        naturalKeys2.put(SRC_KEY_FIELD, SRC_KEY_VALUE_2);
        NaturalKeyDescriptor ndk2 = new NaturalKeyDescriptor(naturalKeys2, tenantId, entityType, null);

        Mockito.when(didGenerator.generateId(Mockito.eq(ndk1))).thenReturn(DID_VALUE_1);
        Mockito.when(didGenerator.generateId(Mockito.eq(ndk2))).thenReturn(DID_VALUE_2);

        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);

        List<Object> embeddedList = (List<Object>) entity.getBody().get(EMBEDDED_LIST_FIELD);
        Assert.assertNotNull(embeddedList);
        Assert.assertEquals(2, embeddedList.size());

        Map<String, Object> subObj1 = (Map<String, Object>) embeddedList.get(0);
        Map<String, Object> subObj2 = (Map<String, Object>) embeddedList.get(1);

        Assert.assertEquals(DID_VALUE_1, subObj1.get(REF_FIELD));
        Assert.assertEquals(DID_VALUE_2, subObj2.get(REF_FIELD));
        Assert.assertFalse("no errors should be reported from reference resolution ", reportStats.hasErrors());
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

    private void testGenericErrorReporting(DidRefConfig refConfig, DidEntityConfig entityConfig) {
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        NeutralRecordEntity entity = createSourceEntity();
        Object refObj = entity.getBody().get(REF_FIELD);
        mockRefConfig(refConfig, ENTITY_TYPE);
        mockEntityConfig(entityConfig, ENTITY_TYPE);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);
        Mockito.when(didGenerator.generateId(Mockito.any(NaturalKeyDescriptor.class))).thenReturn(DID_VALUE);
        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);

        Assert.assertEquals("Id should not have been resolved; it should still be the refObject", refObj, entity
                .getBody().get(REF_FIELD));
        Assert.assertTrue("Errors should be reported from reference resolution ", reportStats.hasErrors());
    }

    @Test
    public void testErrorReportingOnEntityRefFieldMissing() throws IOException {
        // entity doesn't have property to get at sourceRefPath
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        NeutralRecordEntity entity = createSourceEntityMissingRefField();
        DidRefConfig refConfig = createRefConfig("Simple_DID_ref_config.json");
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        mockRefConfig(refConfig, ENTITY_TYPE);
        mockEntityConfig(entityConfig, ENTITY_TYPE);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);
        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);

        Assert.assertNull("Id should not have been resolved", entity.getBody().get(REF_FIELD));
        Assert.assertTrue("Errors should be reported from reference resolution ", reportStats.hasErrors());
    }

    @Test
    public void testErrorReportingOnFactoryMissingRefConfigForEntityType() throws IOException {
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        NeutralRecordEntity entity = createSourceEntityMissingRefField();
        DidEntityConfig entityConfig = createEntityConfig("Simple_DID_entity_config.json");

        mockRefConfig(null, ENTITY_TYPE);
        mockEntityConfig(entityConfig, ENTITY_TYPE);
        Mockito.when(schemaRepository.getSchema(ENTITY_TYPE)).thenReturn(null);
        didResolver.resolveInternalIds(entity, TENANT, errorReport, reportStats);

        Assert.assertNull("Id should not have been resolved", entity.getBody().get(REF_FIELD));
        Assert.assertFalse("No errors should be reported from reference resolution ", reportStats.hasErrors());
    }

    private DidEntityConfig createEntityConfig(String fileName) throws IOException {
        Resource jsonFile = new ClassPathResource("DeterministicIdResolverConfigs/" + fileName);
        DidEntityConfig entityConfig = DidEntityConfig.parse(jsonFile.getInputStream());
        return entityConfig;
    }

    private DidRefConfig createRefConfig(String fileName) throws IOException {
        Resource jsonFile = new ClassPathResource("DeterministicIdResolverConfigs/" + fileName);
        DidRefConfig refConfig = DidRefConfig.parse(jsonFile.getInputStream());
        return refConfig;
    }

    private NeutralRecordEntity createEntity(String fileName) throws IOException {
        Resource jsonFile = new ClassPathResource("DeterministicIdResolverConfigs/" + fileName);
        SimpleEntity entity = MAPPER.readValue(jsonFile.getInputStream(), SimpleEntity.class);
        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(entity.getBody());
        nr.setRecordType(entity.getType());
        return new NeutralRecordEntity(nr);
    }

    /**
     * Create an entity in which the Id has already been resolved
     */
    private NeutralRecordEntity createSourceEntity() {

        Map<String, String> refObject = new HashMap<String, String>();
        refObject.put(SRC_KEY_FIELD, SRC_KEY_VALUE);

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(REF_FIELD, refObject);

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);
        nr.setRecordType(ENTITY_TYPE);

        NeutralRecordEntity entity = new NeutralRecordEntity(nr);

        return entity;
    }

    private NeutralRecordEntity createNestedSourceEntity() {

        Map<String, Object> refObject = new HashMap<String, Object>();

        Map<String, Object> nestedRefObject = new HashMap<String, Object>();
        nestedRefObject.put(NESTED_KEY_SOURCE, NESTED_SRC_KEY_VALUE);

        refObject.put(NESTED_REF, nestedRefObject);

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(REF_FIELD, refObject);

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);
        nr.setRecordType(ENTITY_TYPE);

        NeutralRecordEntity entity = new NeutralRecordEntity(nr);

        return entity;
    }

    @SuppressWarnings("unused")
    private NeutralRecordEntity createOptionalNestedSourceEntity() {
        Map<String, Object> refObject = new HashMap<String, Object>();
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(REF_FIELD, refObject);

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);
        nr.setRecordType(ENTITY_TYPE);

        NeutralRecordEntity entity = new NeutralRecordEntity(nr);

        return entity;
    }

    private NeutralRecordEntity createSourceEntityWithRefList() {
        Map<String, String> refObject1 = new HashMap<String, String>();
        refObject1.put(SRC_KEY_FIELD, SRC_KEY_VALUE_1);

        Map<String, String> refObject2 = new HashMap<String, String>();
        refObject2.put(SRC_KEY_FIELD, SRC_KEY_VALUE_2);

        List<Object> refList = new ArrayList<Object>();
        refList.add(refObject1);
        refList.add(refObject2);

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(REF_FIELD, refList);

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);
        nr.setRecordType(ENTITY_TYPE);

        NeutralRecordEntity entity = new NeutralRecordEntity(nr);

        return entity;
    }

    private NeutralRecordEntity createSourceEntityMissingRefField() {

        Map<String, Object> attributes = new HashMap<String, Object>();

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);
        nr.setRecordType(ENTITY_TYPE);

        NeutralRecordEntity entity = new NeutralRecordEntity(nr);

        return entity;
    }

    /**
     * Create an entity with an embedded list of entities, within each of which there is a reference
     */
    private NeutralRecordEntity createSourceEmbeddedEntity() {

        Map<String, String> refObject1 = new HashMap<String, String>();
        refObject1.put(SRC_KEY_FIELD, SRC_KEY_VALUE_1);

        Map<String, String> refObject2 = new HashMap<String, String>();
        refObject2.put(SRC_KEY_FIELD, SRC_KEY_VALUE_2);

        Map<String, Object> subObject1 = new HashMap<String, Object>();
        subObject1.put(REF_FIELD, refObject1);

        Map<String, Object> subObject2 = new HashMap<String, Object>();
        subObject2.put(REF_FIELD, refObject2);

        List<Object> subObjectList = new ArrayList<Object>();
        subObjectList.add(subObject1);
        subObjectList.add(subObject2);

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(EMBEDDED_LIST_FIELD, subObjectList);

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);
        nr.setRecordType(ENTITY_TYPE);

        NeutralRecordEntity entity = new NeutralRecordEntity(nr);

        return entity;
    }

}
