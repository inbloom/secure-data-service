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

package org.slc.sli.ingestion.transformation.normalization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.cache.NullCacheProvider;
import org.slc.sli.ingestion.landingzone.validation.TestErrorReport;
import org.slc.sli.ingestion.validation.DummyErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * ID Normalizer unit tests.
 *
 * @author okrook
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class IdNormalizationTest {

    @Autowired
    IdNormalizer idNorm;

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Test
    public void testComplexRefResolution() throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        String fieldPath = "body.courseId";
        String collectionName = "TestCollection";
        String path = "body.courseCode";
        String valueSource = "body.courseCode";
        List<String> complexFieldNames = new ArrayList<String>(3);
        complexFieldNames.add("ID");
        complexFieldNames.add("identificationSystem");
        complexFieldNames.add("assigningOrganizationCode");

        ComplexRefDef ref = new ComplexRefDef();
        ref.setFieldPath(fieldPath);
        ref.setPath(path);
        ref.setValueSource(valueSource);
        ref.setComplexFieldNames(complexFieldNames);
        DummyErrorReport errorReport = new DummyErrorReport();
        Repository<Entity> repo = Mockito.mock(Repository.class);

        // set input entity
        Entity entity = getTestComplexRefEntity(1, false);
        // set expected entity
        Entity expectedRecord = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord.getEntityId()).thenReturn("123");
        Mockito.when(expectedRecord.getType()).thenReturn(collectionName);
        // set repository
        Mockito.when(
                repo.findByQuery(Mockito.eq(collectionName), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(Arrays.asList(expectedRecord));

        idNorm.setEntityRepository(repo);
        idNorm.resolveReferenceWithComplexArray(entity, "someNamespace", ref.getValueSource(), ref.getFieldPath(),
                collectionName, ref.getPath(), ref.getComplexFieldNames(), errorReport);

        String foundValue = (String) PropertyUtils.getProperty(entity, fieldPath);
        Assert.assertEquals("123", foundValue);

        // set input entity
        entity = getTestComplexRefEntity(3, true);
        // set expected entity
        expectedRecord = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord.getEntityId()).thenReturn("125");
        Mockito.when(expectedRecord.getType()).thenReturn(collectionName);
        // set repository
        Mockito.when(
                repo.findByQuery(Mockito.eq(collectionName), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(Arrays.asList(expectedRecord));

        idNorm.setEntityRepository(repo);
        idNorm.resolveReferenceWithComplexArray(entity, "someNamespace", ref.getValueSource(), ref.getFieldPath(),
                collectionName, ref.getPath(), ref.getComplexFieldNames(), errorReport);

        foundValue = (String) PropertyUtils.getProperty(entity, fieldPath);
        Assert.assertEquals("125", foundValue);

        // set input entity
        entity = getTestComplexRefEntity(2, false);
        // set expected entity
        Entity expectedRecord1 = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord1.getEntityId()).thenReturn("123");
        Mockito.when(expectedRecord1.getType()).thenReturn(collectionName);
        Entity expectedRecord2 = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord2.getEntityId()).thenReturn("124");
        Mockito.when(expectedRecord2.getType()).thenReturn(collectionName);
        Entity[] expectedRecords = new Entity[2];
        expectedRecords[0] = expectedRecord1;
        expectedRecords[1] = expectedRecord2;
        // set repository
        Mockito.when(
                repo.findByQuery(Mockito.eq(collectionName), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(Arrays.asList(expectedRecords));

        idNorm.setEntityRepository(repo);
        idNorm.resolveReferenceWithComplexArray(entity, "someNamespace", ref.getValueSource(), ref.getFieldPath(),
                collectionName, ref.getPath(), ref.getComplexFieldNames(), errorReport);

        foundValue = (String) PropertyUtils.getProperty(entity, fieldPath);
        Assert.assertNull(foundValue);
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Test
    public void testRefResolution() {
        Ref myCollectionId = new Ref();
        myCollectionId.setEntityType("course");
        Field columnField = new Field();
        columnField.setPath("column");

        FieldValue columnValue = new FieldValue();
        columnValue.setValueSource("body.field");
        columnField.setValues(Arrays.asList(columnValue));

        List<Field> fields = Arrays.asList(columnField);
        List<List<Field>> choice = Arrays.asList(fields);
        myCollectionId.setChoiceOfFields(choice);

        idNorm.setCacheProvider(new NullCacheProvider());
        Repository<Entity> repo = Mockito.mock(Repository.class);
        Repository<Entity> repoNull = Mockito.mock(Repository.class);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("field", 5);

        MongoEntity entity = new MongoEntity("test", body);

        Entity expectedRecord = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord.getEntityId()).thenReturn("123");

        Mockito.when(repo.findByQuery(Mockito.eq("course"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(Arrays.asList(expectedRecord));
        Mockito.when(repoNull.findByQuery(Mockito.eq("course"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(null);

        idNorm.setEntityRepository(repo);

        String internalId = idNorm.resolveInternalId(entity, "someNamespace", myCollectionId, "someFieldPath",
                new DummyErrorReport(), "");

        Assert.assertEquals("123", internalId);

        idNorm.setEntityRepository(repoNull);

        // Testing findByQuery returns null
        internalId = idNorm.resolveInternalId(entity, "someNamespace", myCollectionId, "someFieldPath",
                new DummyErrorReport(), "");

        Assert.assertEquals(null, internalId);
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Test
    public void testRefResolution2() {
        Ref myCollectionId = new Ref();
        myCollectionId.setEntityType("course");
        Field columnField = new Field();
        columnField.setPath("column");

        List<RefDef> refDefs = new ArrayList<RefDef>();
        RefDef refDef = new RefDef();
        refDef.setRef(myCollectionId);
        refDef.setFieldPath("body.field");
        refDefs.add(refDef);
        EntityConfig entityConfig = new EntityConfig();
        entityConfig.setReferences(refDefs);
        EntityConfig entityConfig2 = new EntityConfig();
        entityConfig2.setReferences(null);

        FieldValue columnValue = new FieldValue();
        columnValue.setValueSource("body.field");
        columnField.setValues(Arrays.asList(columnValue));

        List<Field> fields = Arrays.asList(columnField);
        List<List<Field>> choice = Arrays.asList(fields);
        myCollectionId.setChoiceOfFields(choice);

        idNorm.setCacheProvider(new NullCacheProvider());
        Repository<Entity> repo = Mockito.mock(Repository.class);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("field", 5);

        MongoEntity entity = new MongoEntity("test", body);

        Entity expectedRecord = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord.getEntityId()).thenReturn("123");

        Mockito.when(repo.findByQuery(Mockito.eq("course"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(Arrays.asList(expectedRecord));

        idNorm.setEntityRepository(repo);

        idNorm.resolveInternalIds(entity, "someNamespace", entityConfig, new DummyErrorReport());

        Assert.assertEquals("123", entity.getBody().get("field"));

        // Testing entityConfig.getReference == null
        idNorm.resolveInternalIds(entity, "someNamespace", entityConfig2, new DummyErrorReport());
        Assert.assertEquals("123", entity.getBody().get("field"));
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Test
    public void testCollectionRefResolution() {
        Ref myCollectionId = new Ref();
        myCollectionId.setEntityType("course");
        Field columnField = new Field();
        columnField.setPath("column");

        FieldValue columnValue = new FieldValue();
        columnValue.setValueSource("body.field");
        columnField.setValues(Arrays.asList(columnValue));

        List<Field> fields = Arrays.asList(columnField);
        List<List<Field>> choice = Arrays.asList(fields);
        myCollectionId.setChoiceOfFields(choice);

        idNorm.setCacheProvider(new NullCacheProvider());
        Repository<Entity> repo = Mockito.mock(Repository.class);

        Map<String, Object> body = new HashMap<String, Object>();

        List<String> value = new ArrayList<String>();

        value.add("5");
        value.add("6");
        value.add("7");
        body.put("field", value);

        MongoEntity entity = new MongoEntity("test", body);

        Entity expectedRecord = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord.getEntityId()).thenReturn("123");

        Entity secondRecord = Mockito.mock(Entity.class);
        Mockito.when(secondRecord.getEntityId()).thenReturn("456");

        Entity thirdRecord = Mockito.mock(Entity.class);
        Mockito.when(thirdRecord.getEntityId()).thenReturn("789");

        ArrayList<Entity> records = new ArrayList<Entity>();
        records.add(expectedRecord);
        records.add(secondRecord);
        records.add(thirdRecord);

        Mockito.when(repo.findByQuery(Mockito.eq("course"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(records);

        idNorm.setEntityRepository(repo);

        String internalId = idNorm.resolveInternalId(entity, "someNamespace", myCollectionId, "someFieldPath",
                new DummyErrorReport(), "");

        Assert.assertEquals("123", internalId);
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Test
    public void testMultiRefResolution() {

        Ref secondCollection = new Ref();
        secondCollection.setEntityType("cohort");
        Field secondCollectionField = new Field();
        secondCollectionField.setPath("column");
        FieldValue fValue = new FieldValue();
        fValue.setValueSource("body.field");
        secondCollectionField.setValues(Arrays.asList(fValue));
        secondCollection.setChoiceOfFields(Arrays.asList(Arrays.asList(secondCollectionField)));

        Ref myCollectionId = new Ref();
        myCollectionId.setEntityType("course");
        Field columnField = new Field();
        columnField.setPath("body.secondCollectionId");

        FieldValue columnValue = new FieldValue();
        columnValue.setRef(secondCollection);
        columnField.setValues(Arrays.asList(columnValue));

        List<Field> fields = Arrays.asList(columnField);
        List<List<Field>> choice = Arrays.asList(fields);
        myCollectionId.setChoiceOfFields(choice);

        idNorm.setCacheProvider(new NullCacheProvider());
        Repository<Entity> repo = Mockito.mock(Repository.class);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("field", 5);

        MongoEntity entity = new MongoEntity("test", body);

        Entity expectedRecord = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord.getEntityId()).thenReturn("123");

        Mockito.when(repo.findByQuery(Mockito.eq("course"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(Arrays.asList(expectedRecord));

        Entity secondRecord = Mockito.mock(Entity.class);
        Mockito.when(secondRecord.getEntityId()).thenReturn("456");
        Mockito.when(repo.findByQuery(Mockito.eq("cohort"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(Arrays.asList(secondRecord));

        idNorm.setEntityRepository(repo);

        String internalId = idNorm.resolveInternalId(entity, "someNamespace", myCollectionId, "someFieldPath",
                new DummyErrorReport(), "");

        Assert.assertEquals("123", internalId);

        String secinternalId = idNorm.resolveInternalId(entity, "someNamespace", secondCollection, "someFieldPath",
                new DummyErrorReport(), "");

        Assert.assertEquals("456", secinternalId);
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    // @Test
    public void testResolveRefList() {
        final String collectionName = "course";

        // create a test refConfig
        Ref refConfig = new Ref();
        refConfig.setIsRefList(true);
        // the base path of the reference object
        refConfig.setRefObjectPath("body.refField");
        Field field = new Field();
        // the path to query
        field.setPath("body.refIds");
        FieldValue fValue = new FieldValue();
        // the source field for the query value
        fValue.setValueSource("body.refField.idField");
        field.setValues(Arrays.asList(fValue));
        refConfig.setChoiceOfFields(Arrays.asList(Arrays.asList(field)));

        // create an entity config
        List<RefDef> refDefs = new ArrayList<RefDef>();
        RefDef refDef = new RefDef();
        refDef.setRef(refConfig);
        // the field to be put the resolved internal ID into
        refDef.setFieldPath("body.refIds");
        refDefs.add(refDef);
        EntityConfig entityConfig = new EntityConfig();
        entityConfig.setReferences(refDefs);

        Entity entity = getTestRefListEntity();

        NeutralRecord nr1 = new NeutralRecord();
        NeutralRecordEntity expectedEntity1 = new NeutralRecordEntity(nr1);
        expectedEntity1.getBody().put("externalId", "externalId1");
        NeutralRecord nr2 = new NeutralRecord();
        NeutralRecordEntity expectedEntity2 = new NeutralRecordEntity(nr2);
        expectedEntity2.getBody().put("externalId", "externalId2");
        List<Entity> expectedEntityList = new ArrayList<Entity>();
        expectedEntityList.add(expectedEntity1);
        expectedEntityList.add(expectedEntity2);

        idNorm.setCacheProvider(new NullCacheProvider());
        Repository<Entity> repo = Mockito.mock(Repository.class);

        // mock the repo query note this doesn't test whether the query was constructed correctly
        Mockito.when(
                repo.findByQuery(Mockito.eq(collectionName), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(expectedEntityList);

        idNorm.setEntityRepository(repo);
        idNorm.resolveInternalIds(entity, "someNamespace", entityConfig, new DummyErrorReport());

        List<String> refIds = (List<String>) entity.getBody().get("refIds");

        assertNotNull("attribute refIds field should not be null", refIds);
        assertEquals("attribute refIds should have 2 elements", 2, refIds.size());
    }

    private Entity getTestRefListEntity() {
        Map<String, Object> firstRef = new HashMap<String, Object>();
        firstRef.put("idField", "externalId1");
        Map<String, Object> secondRef = new HashMap<String, Object>();
        secondRef.put("idField", "externalId2");
        // Added one more duplicate reference to test fix to DE564
        Map<String, Object> thirdRef = new HashMap<String, Object>();
        thirdRef.put("idField", "externalId2");

        List<Object> refList = new ArrayList<Object>();
        refList.add(firstRef);
        refList.add(secondRef);
        refList.add(thirdRef);
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("refField", refList);

        List<Object> idList = new ArrayList<Object>();
        idList.add("");
        idList.add("");
        attributes.put("refIds", idList);

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);
        Entity entity = new NeutralRecordEntity(nr);

        return entity;
    }

    @SuppressWarnings("deprecation")
    // @Test
    public void shouldResolveNestedRef() {
        EntityConfig entityConfig = createNestedRefConfig(true);

        @SuppressWarnings("unchecked")
        Repository<Entity> repo = Mockito.mock(Repository.class);
        // mock the repo query note this doesn't test whether the query was constructed correctly
        Mockito.when(
                repo.findByQuery(Mockito.eq("parentCollection"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(getParentTargetEntities());
        Mockito.when(
                repo.findByQuery(Mockito.eq("childCollection"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(getChildTargetEntities());

        Entity entity = createNestedSourceEntity(true);
        ErrorReport errorReport = new TestErrorReport();

        idNorm.setCacheProvider(new NullCacheProvider());

        idNorm.setEntityRepository(repo);
        idNorm.resolveInternalIds(entity, "SLI", entityConfig, errorReport);

        assertNotNull("attribute parentId should not be null", entity.getBody().get("parentId"));
        assertEquals("attribute parentId should be resolved to parent_guid", "parent_guid",
                entity.getBody().get("parentId"));
        assertFalse("no errors should be reported from reference resolution ", errorReport.hasErrors());
    }

    @SuppressWarnings("deprecation")
    // @Test
    public void shouldFailWithUnresolvedNonNullOptionalChildRef() {
        EntityConfig entityConfig = createNestedRefConfig(true);

        @SuppressWarnings("unchecked")
        Repository<Entity> repo = Mockito.mock(Repository.class);
        // mock the repo query note this doesn't test whether the query was constructed correctly
        Mockito.when(
                repo.findByQuery(Mockito.eq("parentCollection"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(getParentTargetEntities());
        Mockito.when(
                repo.findByQuery(Mockito.eq("childCollection"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(new ArrayList<Entity>());

        Entity entity = createNestedSourceEntity(true);
        ErrorReport errorReport = new TestErrorReport();

        idNorm.setCacheProvider(new NullCacheProvider());

        idNorm.setEntityRepository(repo);
        idNorm.resolveInternalIds(entity, "SLI", entityConfig, errorReport);

        assertNull("attribute parentId should be null", entity.getBody().get("parentId"));
        assertTrue("errors should be reported from failed reference resolution ", errorReport.hasErrors());
    }

    @SuppressWarnings("deprecation")
    // @Test
    public void shouldFailWithUnresolvedRequiredNullChildRef() {
        // TODO
        EntityConfig entityConfig = createNestedRefConfig(false);

        @SuppressWarnings("unchecked")
        Repository<Entity> repo = Mockito.mock(Repository.class);
        // mock the repo query note this doesn't test whether the query was constructed correctly
        Mockito.when(
                repo.findByQuery(Mockito.eq("parentCollection"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(getParentTargetEntities());
        Mockito.when(
                repo.findByQuery(Mockito.eq("childCollection"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(new ArrayList<Entity>());

        Entity entity = createNestedSourceEntity(false);
        ErrorReport errorReport = new TestErrorReport();

        idNorm.setCacheProvider(new NullCacheProvider());

        idNorm.setEntityRepository(repo);
        idNorm.resolveInternalIds(entity, "SLI", entityConfig, errorReport);

        assertNull("attribute parentId should be null", entity.getBody().get("parentId"));
        assertTrue("errors should be reported from failed reference resolution ", errorReport.hasErrors());
    }

    @SuppressWarnings("deprecation")
    // @Test
    public void shouldResolveWithUnresolvedOptionalNullChildRef() {
        EntityConfig entityConfig = createNestedRefConfig(true);

        @SuppressWarnings("unchecked")
        Repository<Entity> repo = Mockito.mock(Repository.class);
        // mock the repo query note this doesn't test whether the query was constructed correctly
        Mockito.when(
                repo.findByQuery(Mockito.eq("parentCollection"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(getParentTargetEntities());
        Mockito.when(
                repo.findByQuery(Mockito.eq("childCollection"), Mockito.any(Query.class), Mockito.eq(0), Mockito.eq(0)))
                .thenReturn(new ArrayList<Entity>());

        Entity entity = createNestedSourceEntity(false);
        ErrorReport errorReport = new TestErrorReport();

        idNorm.setCacheProvider(new NullCacheProvider());

        idNorm.setEntityRepository(repo);
        idNorm.resolveInternalIds(entity, "SLI", entityConfig, errorReport);

        assertNotNull("attribute parentId should not be null", entity.getBody().get("parentId"));
        assertEquals("attribute parentId should be resolved to parent_guid", "parent_guid",
                entity.getBody().get("parentId"));
        assertFalse("no errors should be reported from reference resolution ", errorReport.hasErrors());
    }

    // create nested ref
    private EntityConfig createNestedRefConfig(boolean optionalChildRef) {
        Resource jsonFile = null;
        if (optionalChildRef) {
            jsonFile = new ClassPathResource("idNormalizerTestConfigs/nestedRef_optionalChild.json");
        } else {
            jsonFile = new ClassPathResource("idNormalizerTestConfigs/nestedRef.json");
        }
        EntityConfig entityConfig = null;
        try {
            entityConfig = EntityConfig.parse(jsonFile.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entityConfig;
    }

    // create nested source Entity
    private Entity createNestedSourceEntity(boolean includeChildRefValue) {

        Map<String, Object> parentRef = new HashMap<String, Object>();
        parentRef.put("srcOtherField", "otherFieldVal");

        if (includeChildRefValue) {
            parentRef.put("srcChildRefField", "childFieldValue");
        }

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("parentRef", parentRef);

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);

        Entity entity = new NeutralRecordEntity(nr);

        return entity;
    }

    private List<Entity> getChildTargetEntities() {
        List<Entity> entities = new ArrayList<Entity>();
        entities.add(createChildTargetEntity());

        return entities;
    }

    private Entity createChildTargetEntity() {

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("childField", "childFieldValue");

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);

        NeutralRecordEntity entity = new NeutralRecordEntity(nr);
        return entity;
    }

    private List<Entity> getParentTargetEntities() {
        List<Entity> entities = new ArrayList<Entity>();
        entities.add(createParentTargetEntity());

        return entities;
    }

    private Entity createParentTargetEntity() {

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("childId", "child_guid");

        NeutralRecord nr = new NeutralRecord();
        nr.setAttributes(attributes);

        NeutralRecordEntity entity = new NeutralRecordEntity(nr);
        return entity;
    }

    private Entity getTestComplexRefEntity(int recordNum, boolean assigningOrganizationCodeUsed) {
        Map<String, Object> firstRef = new HashMap<String, Object>();
        firstRef.put("ID", "id_123");
        firstRef.put("identificationSystem", "identificationSystem_123");
        if (assigningOrganizationCodeUsed) {
            firstRef.put("assigningOrganizationCode", "assigningOrganizationCode_123");
        }
        Map<String, Object> secondRef = new HashMap<String, Object>();
        secondRef.put("ID", "id_124");
        secondRef.put("identificationSystem", "identificationSystem_124");
        if (assigningOrganizationCodeUsed) {
            secondRef.put("assigningOrganizationCode", "assigningOrganizationCode_124");
        }
        // Added one more duplicate reference to test fix to DE564
        Map<String, Object> thirdRef = new HashMap<String, Object>();
        thirdRef.put("ID", "id_125");
        thirdRef.put("identificationSystem", "identificationSystem_125");
        if (assigningOrganizationCodeUsed) {
            thirdRef.put("assigningOrganizationCode", "assigningOrganizationCode_125");
        }

        List<Object> courseCodes = new ArrayList<Object>();
        courseCodes.add(firstRef);
        if (recordNum >= 2) {
            courseCodes.add(secondRef);
        }
        if (recordNum >= 3) {
            courseCodes.add(thirdRef);
        }

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("courseCode", courseCodes);

        NeutralRecord nr = new NeutralRecord();
        nr.setSourceId("someNamespace");
        nr.setRecordType("studentTranscriptAssociation");
        nr.setAttributes(attributes);
        return new NeutralRecordEntity(nr);
    }
}
