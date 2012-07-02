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


package org.slc.sli.ingestion.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.Validator;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 * Tests for EntityPersistHandler
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class NeutralRecordfEntityPersistHandlerTest {

    @Autowired
    private NeutralRecordEntityPersistHandler entityPersistHandler;

    private MongoEntityRepository mockedEntityRepository;

    private Validator<NeutralRecordEntity> mockedValidator;

    private static final String STUDENT_ID = "765432";
    private static final String SCHOOL_ID = "654321";
    private static final String INTERNAL_STUDENT_ID = "0x" + STUDENT_ID;
    private static final String INTERNAL_SCHOOL_ID = "0x" + SCHOOL_ID;
    private static final String BAD_STUDENT_ID = "234567";
    private static final String REGION_ID = "https://devapp1.slidev.org:443/sp";
    private static final String METADATA_BLOCK = "metaData";
    private static final String REGION_ID_FIELD = "tenantId";
    private static final String EXTERNAL_ID_FIELD = "externalId";

    private final Map<String, String> schoolFilterFields = new HashMap<String, String>();
    private final Map<String, String> studentSchoolAssociationFilterFields = new HashMap<String, String>();
    private final LinkedList<Entity> studentList = new LinkedList<Entity>();
    private final Iterable<Entity> studentFound = studentList;
    private final LinkedList<Entity> schoolList = new LinkedList<Entity>();
    private final Iterable<Entity> schoolFound = schoolList;
    private final LinkedList<Entity> studentSchoolAssociationList = new LinkedList<Entity>();
    private final Iterable<Entity> studentSchoolAssociationFound = studentSchoolAssociationList;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        mockedEntityRepository = mock(MongoEntityRepository.class);
        entityPersistHandler.setEntityRepository(mockedEntityRepository);
        when(mockedEntityRepository.findAllByPaths(eq("student"), eq(new HashMap<String, String>()), any(NeutralQuery.class))).thenReturn(studentFound);

        // School search.
        schoolFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        schoolFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, SCHOOL_ID);
        when(mockedEntityRepository.findAllByPaths(eq("school"), eq(schoolFilterFields), any(NeutralQuery.class))).thenReturn(schoolFound);

        // Student-School Association search.
        studentSchoolAssociationFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentSchoolAssociationFilterFields.put("body.studentId", INTERNAL_STUDENT_ID);
        studentSchoolAssociationFilterFields.put("body.schoolId", INTERNAL_SCHOOL_ID);
        when(mockedEntityRepository.findAllByPaths(eq("studentSchoolAssociation"), eq(studentSchoolAssociationFilterFields), any(NeutralQuery.class)))
                .thenReturn(studentSchoolAssociationFound);

        mockedValidator = mock(Validator.class);
        List<Validator<NeutralRecordEntity>> mockedValidators = new ArrayList<Validator<NeutralRecordEntity>>();
        mockedValidators.add(mockedValidator);
        entityPersistHandler.setPreValidators(mockedValidators);
    }

    /*
     * @Test
     * public void testDoHandling() {
     * Entity mockedEntity = mock(Entity.class);
     * ErrorReport mockedErrorReport = mock(ErrorReport.class);
     *
     * String expectedType = "test_entity";
     * when(mockedEntity.getType()).thenReturn(expectedType);
     *
     * Map<String, Object> expectedMap = new HashMap<String, Object>();
     * when(mockedEntity.getBody()).thenReturn(expectedMap);
     *
     * entityPersistHandler.doHandling((NeutralRecordEntity)mockedEntity, mockedErrorReport);
     *
     * verify(mockedEntityRepository).create(expectedType, expectedMap);
     * }
     */

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    public void testCreateStudentEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);
        FaultsReport fr = new FaultsReport();

        // Student search.
        Map<String, String> studentFilterFields = new HashMap<String, String>();
        studentFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, STUDENT_ID);
        when(entityRepository.findAllByPaths(eq("student"), eq(studentFilterFields), any(NeutralQuery.class))).thenReturn(Collections.<Entity>emptyList());

        // Create a new student entity, and test creating it in the data store.
        NeutralRecordEntity studentEntity = createStudentEntity();
        studentEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.setEntityRepository(entityRepository);
        entityPersistHandler.doHandling(studentEntity, fr);

        verify(entityRepository).create(studentEntity.getType(), studentEntity.getBody(), studentEntity.getMetaData(),
                "student");
        Assert.assertFalse("Error report should not contain errors", fr.hasErrors());
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    public void testUpdateStudentEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);
        FaultsReport fr = new FaultsReport();

        NeutralRecordEntity studentEntity = createStudentEntity();
        NeutralRecordEntity existingStudentEntity = createStudentEntity();

        existingStudentEntity.setEntityId(UUID.randomUUID().toString());

        // Student search.
        Map<String, String> studentFilterFields = new HashMap<String, String>();
        studentFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, STUDENT_ID);
        when(entityRepository.findAllByPaths(eq("student"), eq(studentFilterFields), any(NeutralQuery.class))).thenReturn(
                Arrays.asList((Entity) existingStudentEntity));

        when(entityRepository.update("student", studentEntity)).thenReturn(true);

        entityPersistHandler.setEntityRepository(entityRepository);
        studentEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.doHandling(studentEntity, fr);

        verify(entityRepository).update("student", studentEntity);
        Assert.assertFalse("Error report should not contain errors", fr.hasErrors());
    }

    @Test
    public void testPersistanceExceptionHandling() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);
        FaultsReport fr = new FaultsReport();

        NeutralRecordEntity studentEntity = createStudentEntity();
        NeutralRecordEntity existingStudentEntity = createStudentEntity();

        existingStudentEntity.setEntityId(UUID.randomUUID().toString());

        // Student search.
        Map<String, String> studentFilterFields = new HashMap<String, String>();
        studentFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, STUDENT_ID);
        when(entityRepository.findAllByPaths(eq("student"), eq(studentFilterFields), any(NeutralQuery.class))).thenReturn(
                Arrays.asList((Entity) existingStudentEntity));

        ValidationError error = new ValidationError(ErrorType.REQUIRED_FIELD_MISSING, "field", null,
                new String[] { "String" });
        when(entityRepository.update(eq("student"), eq(studentEntity))).thenThrow(
                new EntityValidationException(existingStudentEntity.getEntityId(), "student", Arrays.asList(error)));

        studentEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.setEntityRepository(entityRepository);
        entityPersistHandler.doHandling(studentEntity, fr);

        Assert.assertTrue("Error report should contain errors", fr.hasErrors());
                String message = "ERROR: There has been a data validation error when saving an entity\n"
                               + "       Error      REQUIRED_FIELD_MISSING\n"
                               + "       Entity     student\n"
                               + "       Instance   0\n"
                               + "       Field      field\n"
                               + "       Value      null\n"
                               + "       Expected   [String]\n";
        Assert.assertEquals(message, fr.getFaults().get(0).getMessage());
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    @Ignore
    public void testCreateStudentSchoolAssociationEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);
        FaultsReport fr = new FaultsReport();

        // Create a new student-school association entity, and test creating it in the data store.
        NeutralRecordEntity foundStudent = new NeutralRecordEntity(null);
        foundStudent.setEntityId(INTERNAL_STUDENT_ID);

        LinkedList<Entity> studentList = new LinkedList<Entity>();
        studentList.add(foundStudent);

        // Student search.
        Map<String, String> studentFilterFields = new HashMap<String, String>();
        studentFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, STUDENT_ID);
        when(entityRepository.findAllByPaths(eq("student"), eq(studentFilterFields), any(NeutralQuery.class))).thenReturn(studentList);

        // School search.
        NeutralRecordEntity foundSchool = new NeutralRecordEntity(null);
        foundSchool.setEntityId(INTERNAL_SCHOOL_ID);

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findAllByPaths(eq("school"), eq(schoolFilterFields), any(NeutralQuery.class))).thenReturn(schoolList);

        NeutralRecordEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(STUDENT_ID);
        studentSchoolAssociationEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.setEntityRepository(entityRepository);
        entityPersistHandler.doHandling(studentSchoolAssociationEntity, fr);
        verify(entityRepository).create(studentSchoolAssociationEntity.getType(),
                studentSchoolAssociationEntity.getBody(), studentSchoolAssociationEntity.getMetaData(),
                studentSchoolAssociationEntity.getType());
        Assert.assertFalse("Error report should not contain errors", fr.hasErrors());
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    @Ignore
    public void testUpdateStudentSchoolAssociationEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);
        FaultsReport fr = new FaultsReport();

        // Create a new student-school association entity, and test creating it in the data store.
        NeutralRecordEntity foundStudent = new NeutralRecordEntity(null);
        foundStudent.setEntityId(INTERNAL_STUDENT_ID);

        LinkedList<Entity> studentList = new LinkedList<Entity>();
        studentList.add(foundStudent);

        // Student search.
        Map<String, String> studentFilterFields = new HashMap<String, String>();
        studentFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, STUDENT_ID);
        when(entityRepository.findAllByPaths(eq("student"), eq(studentFilterFields), any(NeutralQuery.class))).thenReturn(studentList);

        // School search.
        NeutralRecordEntity foundSchool = new NeutralRecordEntity(null);
        foundSchool.setEntityId(INTERNAL_SCHOOL_ID);

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findAllByPaths(eq("school"), eq(schoolFilterFields), any(NeutralQuery.class))).thenReturn(schoolList);

        NeutralRecordEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(STUDENT_ID);
        NeutralRecordEntity existingStudentSchoolAssociationEntity = createStudentSchoolAssociationEntity(STUDENT_ID);

        existingStudentSchoolAssociationEntity.setEntityId(UUID.randomUUID().toString());

        when(entityRepository.findAllByPaths(eq("studentSchoolAssociation"), eq(studentSchoolAssociationFilterFields), any(NeutralQuery.class)))
                .thenReturn(Arrays.asList((Entity) existingStudentSchoolAssociationEntity));

        when(entityRepository.update(eq("studentSchoolAssociation"), eq(studentSchoolAssociationEntity))).thenReturn(true);

        studentSchoolAssociationEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.setEntityRepository(entityRepository);
        entityPersistHandler.doHandling(studentSchoolAssociationEntity, fr);

        verify(entityRepository).update("studentSchoolAssociation", studentSchoolAssociationEntity);
        Assert.assertFalse("Error report should not contain errors", fr.hasErrors());
    }

    @Test
    public void testInvalidUpdateStudentSchoolAssociationEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);
        FaultsReport fr = new FaultsReport();

        // Create a new student-school association entity, and test creating it in the data store.
        NeutralRecordEntity foundStudent = new NeutralRecordEntity(null);
        foundStudent.setEntityId(INTERNAL_STUDENT_ID);

        LinkedList<Entity> studentList = new LinkedList<Entity>();
        studentList.add(foundStudent);

        // Student search.
        Map<String, String> studentFilterFields = new HashMap<String, String>();
        studentFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, BAD_STUDENT_ID);
        when(entityRepository.findAllByPaths(eq("student"), eq(studentFilterFields), any(NeutralQuery.class))).thenReturn(Collections.<Entity>emptyList());

        // School search.
        NeutralRecordEntity foundSchool = new NeutralRecordEntity(null);
        foundSchool.setEntityId(INTERNAL_SCHOOL_ID);

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findAllByPaths(eq("school"), eq(schoolFilterFields), any(NeutralQuery.class))).thenReturn(schoolList);

        NeutralRecordEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(BAD_STUDENT_ID);
        when(entityRepository.findAllByPaths(eq("studentSchoolAssociation"), eq(studentSchoolAssociationFilterFields), any(NeutralQuery.class)))
                .thenReturn(Arrays.asList((Entity) studentSchoolAssociationEntity));

        when(entityRepository.update("studentSchoolAssociation", studentSchoolAssociationEntity)).thenReturn(true);

        entityPersistHandler.setEntityRepository(entityRepository);
        studentSchoolAssociationEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.doHandling(studentSchoolAssociationEntity, fr);

        verify(entityRepository, never()).update("studentSchoolAssociation", studentSchoolAssociationEntity);
        Assert.assertTrue("Error report should contain errors", fr.hasErrors());
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    public void testInvalidateStudentSchoolAssociationEntity() {
        NeutralRecordEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(BAD_STUDENT_ID);
        studentSchoolAssociationEntity.setAttributeField("studentId", BAD_STUDENT_ID);

        FaultsReport fr = new FaultsReport();
        studentSchoolAssociationEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.doHandling(studentSchoolAssociationEntity, fr);
        verify(mockedEntityRepository, never()).create(studentSchoolAssociationEntity.getType(),
                studentSchoolAssociationEntity.getBody(), studentSchoolAssociationEntity.getMetaData(),
                studentSchoolAssociationEntity.getType());
        verify(mockedEntityRepository, never()).update(studentSchoolAssociationEntity.getType(),
                studentSchoolAssociationEntity);
        Assert.assertTrue("Error report should contain errors", fr.hasErrors());
    }

    @Test
    public void testHandleFailedValidation() {
        /*
         * when validation fails for an entity, we should not try to persist
         */

        NeutralRecordEntity mockedEntity = mock(NeutralRecordEntity.class);
        ErrorReport mockedErrorReport = mock(ErrorReport.class);

        String expectedType = "student";
        when(mockedEntity.getType()).thenReturn(expectedType);

        Map<String, Object> expectedMap = new HashMap<String, Object>();
        when(mockedEntity.getBody()).thenReturn(expectedMap);

        Map<String, Object> expectedMetaData = new HashMap<String, Object>();
        expectedMetaData.put(REGION_ID_FIELD, REGION_ID);
        expectedMetaData.put(EXTERNAL_ID_FIELD, STUDENT_ID);
        when(mockedEntity.getMetaData()).thenReturn(expectedMetaData);

        when(mockedErrorReport.hasErrors()).thenReturn(true);

        entityPersistHandler.handle(mockedEntity, mockedErrorReport);

        verify(mockedEntityRepository, never()).create(expectedType, expectedMap, expectedMetaData, expectedType);
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    public NeutralRecordEntity createStudentEntity() {
        // Create neutral record for entity.
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setLocalId(STUDENT_ID);
        neutralRecord.setRecordType("student");
        Map<String, Object> field = new HashMap<String, Object>();
        field.put("studentUniqueStateId", STUDENT_ID);
        field.put("Sex", "Male");
        neutralRecord.setAttributes(field);

        // Create new entity from neutral record.
        return new NeutralRecordEntity(neutralRecord);
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    public NeutralRecordEntity createSchoolEntity() {
        // Create neutral record for entity.
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setLocalId(SCHOOL_ID);
        neutralRecord.setRecordType("school");

        // Create new entity from neutral record.
        return new NeutralRecordEntity(neutralRecord);
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    public NeutralRecordEntity createStudentSchoolAssociationEntity(String studentId) {
        // Create neutral record for entity.
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setAssociation(true);
        neutralRecord.setRecordType("studentSchoolAssociation");
        Map<String, Object> localParentIds = new HashMap<String, Object>();
        localParentIds.put("Student", studentId);
        localParentIds.put("School", SCHOOL_ID);
        neutralRecord.setLocalParentIds(localParentIds);
        Map<String, Object> field = new HashMap<String, Object>();
        field.put("studentId", studentId);
        field.put("schoolId", SCHOOL_ID);
        field.put("ClassOf", "2014");
        neutralRecord.setAttributes(field);

        // Create and return new entity from neutral record.
        return new NeutralRecordEntity(neutralRecord);
    }

    /**
     * @author jtully 2/28/2012
     * Test of resolveInternalIds for reference fields of arbitrary name
     */
    @Test
    @Ignore
    public void shouldNormalizeIdInNamedReferenceField() {
        //set up the variable names and values to test
        String fieldName = "fieldName";
        String collectionName = "collectionName";
        String externalId = "externalId";
        String internalId = "internalId";
        String nameSpace = "nameSpace";

        //create a test entity with these values
        Map<String, Object> localParentIds = new HashMap<String, Object>();
        localParentIds.put(collectionName + "#" + fieldName, externalId);

        NeutralRecord testNr = new NeutralRecord();
        testNr.setLocalParentIds(localParentIds);
        testNr.setAttributeField(fieldName, externalId);
        testNr.setLocalParentIds(localParentIds);

        NeutralRecordEntity testEntity = new NeutralRecordEntity(testNr);
        testEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), nameSpace);

        //create an entity to reference
        NeutralRecordEntity refEntity = new NeutralRecordEntity(new NeutralRecord());
        refEntity.setEntityId(internalId);
        LinkedList<Entity> refResults = new LinkedList<Entity>();
        refResults.add(refEntity);

        //mock the db lookup
        Map<String, String> testFilterFields = new HashMap<String, String>();
        testFilterFields.put(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey(), nameSpace);
        testFilterFields.put(METADATA_BLOCK + "." + EntityMetadataKey.EXTERNAL_ID.getKey(), externalId);

        when(mockedEntityRepository.findAllByPaths(eq(collectionName), eq(testFilterFields), any(NeutralQuery.class)))
                .thenReturn(refResults);

        //mock the errorReport
        ErrorReport mockedErrorReport = mock(ErrorReport.class);
        when(mockedErrorReport.hasErrors()).thenReturn(false);

        entityPersistHandler.resolveInternalIds(testEntity, mockedErrorReport);

        //assert that the Id normalization was performed correctly
        String idValue = (String) testEntity.getBody().get(fieldName);
        Assert.assertEquals("reference Id was not normalized correctly", internalId, idValue);
    }

    /**
     */
    public NeutralRecordEntity createTeacherSchoolAssociationEntity(String studentId) {
        // Create neutral record for entity.
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setAssociation(true);
        neutralRecord.setRecordType("teacherSchoolAssociation");
        Map<String, Object> localParentIds = new HashMap<String, Object>();
        localParentIds.put("Teacher", studentId);
        localParentIds.put("School", SCHOOL_ID);
        neutralRecord.setLocalParentIds(localParentIds);
        Map<String, Object> field = new HashMap<String, Object>();
        field.put("teacherId", studentId);
        field.put("schoolId", SCHOOL_ID);
        neutralRecord.setAttributes(field);

        // Create and return new entity from neutral record.
        return new NeutralRecordEntity(neutralRecord);
    }

    /**
     */
    @Test
    @Ignore
    public void testCreateTeacherSchoolAssociationEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);
        FaultsReport fr = new FaultsReport();

        // Create a new student-school association entity, and test creating it in the data store.
        NeutralRecordEntity foundTeacher = new NeutralRecordEntity(null);
        foundTeacher.setEntityId(INTERNAL_STUDENT_ID);

        LinkedList<Entity> teacherList = new LinkedList<Entity>();
        teacherList.add(foundTeacher);

        // Teacher search.
        Map<String, String> teacherFilterFields = new HashMap<String, String>();
        teacherFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        teacherFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, STUDENT_ID);
        when(entityRepository.findAllByPaths(eq("teacher"), eq(teacherFilterFields), any(NeutralQuery.class))).thenReturn(teacherList);

        // School search.
        NeutralRecordEntity foundSchool = new NeutralRecordEntity(null);
        foundSchool.setEntityId(INTERNAL_SCHOOL_ID);

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findAllByPaths(eq("school"), eq(schoolFilterFields), any(NeutralQuery.class))).thenReturn(schoolList);

        NeutralRecordEntity teacherSchoolAssociationEntity = createTeacherSchoolAssociationEntity(STUDENT_ID);
        teacherSchoolAssociationEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.setEntityRepository(entityRepository);
        entityPersistHandler.doHandling(teacherSchoolAssociationEntity, fr);
        verify(entityRepository).create(teacherSchoolAssociationEntity.getType(),
                teacherSchoolAssociationEntity.getBody(), teacherSchoolAssociationEntity.getMetaData(),
                teacherSchoolAssociationEntity.getType());
        Assert.assertFalse("Error report should not contain errors", fr.hasErrors());
    }

    /**
     * @author tke
     */
    @Test
    public void testCreateEntityLookupFilterByFields() {
        NeutralRecordEntity neutralRecordEntity = createStudentSchoolAssociationEntity(INTERNAL_STUDENT_ID);
        List<String> keyFields = new ArrayList<String>();
        keyFields.add(METADATA_BLOCK + ".localId");
        keyFields.add(METADATA_BLOCK + ".externalId");
        keyFields.add("body.studentId");
        neutralRecordEntity.setMetaDataField("localId", INTERNAL_STUDENT_ID);
        neutralRecordEntity.setMetaDataField("externalId", SCHOOL_ID);

        ErrorReport mockedErrorReport = mock(ErrorReport.class);
        when(mockedErrorReport.hasErrors()).thenReturn(false);

        Map<String, String> res = entityPersistHandler.createEntityLookupFilter(neutralRecordEntity, keyFields, mockedErrorReport);

        Assert.assertNotNull(res);
        Assert.assertEquals(INTERNAL_STUDENT_ID, res.get(METADATA_BLOCK + ".localId"));
        Assert.assertEquals(SCHOOL_ID, res.get(METADATA_BLOCK + ".externalId"));
        Assert.assertEquals(INTERNAL_STUDENT_ID, res.get("body.studentId"));
    }

    /**
     * @author tke
     */
    @Test
    public void testCreateEntityLookupFilterByFieldsFromJson() throws Exception {
        NeutralRecordEntity neutralRecordEntity = createStudentSchoolAssociationEntity(INTERNAL_STUDENT_ID);
        File jsonFile = new File("src/test/resources/TeacherSectionAssociation.json");
        ObjectMapper mapper = new ObjectMapper();
        EntityConfig teacherSectionAssociation = mapper.readValue(jsonFile, EntityConfig.class);

        neutralRecordEntity.setMetaDataField("localId", INTERNAL_STUDENT_ID);
        neutralRecordEntity.setMetaDataField("externalId", SCHOOL_ID);

        ErrorReport mockedErrorReport = mock(ErrorReport.class);
        when(mockedErrorReport.hasErrors()).thenReturn(false);

        Map<String, String> res = entityPersistHandler.createEntityLookupFilter(neutralRecordEntity, teacherSectionAssociation.getKeyFields(), mockedErrorReport);

        Assert.assertNotNull(res);
        Assert.assertEquals(INTERNAL_STUDENT_ID, res.get(METADATA_BLOCK + ".localId"));
        Assert.assertEquals(SCHOOL_ID, res.get(METADATA_BLOCK + ".externalId"));
        Assert.assertEquals(INTERNAL_STUDENT_ID, res.get("body.studentId"));

    }

}
