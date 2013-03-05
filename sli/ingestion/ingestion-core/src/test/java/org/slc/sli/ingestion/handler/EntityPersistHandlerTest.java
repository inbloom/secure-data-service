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

package org.slc.sli.ingestion.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.DummyMessageReport;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.transformation.SimpleEntity;
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
public class EntityPersistHandlerTest {

    @Autowired
    private EntityPersistHandler entityPersistHandler;

    private MongoEntityRepository mockedEntityRepository;

    private static final String STUDENT_ID = "765432";
    private static final String STAGED_STUDENT_UUID = "2012js-df7cbeb6-f11f-11e1-8f42-406c8f3fb40c";
    private static final String STAGED_STUDENT_SCHOOL_UUID = "2012ab-df7cbeb6-f11f-11e1-8f42-406c8f3fb40c";
    private static final String STAGED_TEACHER_SCHOOL_UUID = "2012cd-df7cbeb6-f11f-11e1-8f42-406c8f3fb40c";
    private static final String SCHOOL_ID = "654321";
    private static final String INTERNAL_STUDENT_ID = "0x" + STUDENT_ID;
    private static final String INTERNAL_SCHOOL_ID = "0x" + SCHOOL_ID;
    private static final String REGION_ID = "SLI";
    private static final String METADATA_BLOCK = "metaData";
    private static final String REGION_ID_FIELD = "tenantId";
    private static final String EXTERNAL_ID_FIELD = "externalId";

    private final LinkedList<Entity> studentList = new LinkedList<Entity>();
    private final Iterable<Entity> studentFound = studentList;
    private final LinkedList<Entity> schoolList = new LinkedList<Entity>();
    private final Iterable<Entity> schoolFound = schoolList;
    private final LinkedList<Entity> studentSchoolAssociationList = new LinkedList<Entity>();
    private final Iterable<Entity> studentSchoolAssociationFound = studentSchoolAssociationList;

    private NeutralQuery regionIdStudentIdQuery = null;
    private NeutralQuery ssaQuery = null;

    @Value("${sli.ingestion.totalRetries}")
    private int totalRetries;

    @Before
    public void setup() {
        mockedEntityRepository = mock(MongoEntityRepository.class);
        entityPersistHandler.setEntityRepository(mockedEntityRepository);

        when(mockedEntityRepository.findAll(eq("student"), any(NeutralQuery.class))).thenReturn(studentFound);

        // School search.
        regionIdStudentIdQuery = new NeutralQuery();
        regionIdStudentIdQuery.addCriteria(new NeutralCriteria(METADATA_BLOCK + "." + REGION_ID_FIELD,
                NeutralCriteria.OPERATOR_EQUAL, REGION_ID, false));
        regionIdStudentIdQuery.addCriteria(new NeutralCriteria(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD,
                NeutralCriteria.OPERATOR_EQUAL, STUDENT_ID, false));
        when(mockedEntityRepository.findAll(eq("school"), eq(regionIdStudentIdQuery))).thenReturn(schoolFound);

        // Student-School Association search.
        ssaQuery = new NeutralQuery();
        ssaQuery.addCriteria(new NeutralCriteria(METADATA_BLOCK + "." + REGION_ID_FIELD,
                NeutralCriteria.OPERATOR_EQUAL, REGION_ID, false));
        ssaQuery.addCriteria(new NeutralCriteria("body.studentId", NeutralCriteria.OPERATOR_EQUAL, INTERNAL_STUDENT_ID,
                false));
        ssaQuery.addCriteria(new NeutralCriteria("body.schoolId", NeutralCriteria.OPERATOR_EQUAL, INTERNAL_SCHOOL_ID,
                false));
        when(mockedEntityRepository.findAll(eq("studentSchoolAssociation"), eq(ssaQuery))).thenReturn(
                studentSchoolAssociationFound);

    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     * @author tke 3/15/2012, modified be consistent with the new IdNormalization strategy.
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    public void testCreateStudentEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);

        // Student search.
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria(METADATA_BLOCK + "." + REGION_ID_FIELD, NeutralCriteria.OPERATOR_EQUAL,
                REGION_ID, false));
        query.addCriteria(new NeutralCriteria(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, NeutralCriteria.OPERATOR_EQUAL,
                STUDENT_ID, false));
        // Create a new student entity with entity ID, and test creating it in the data store.
        SimpleEntity studentEntity = createStudentEntity(true);

        List<Entity> le = new ArrayList<Entity>();
        le.add(studentEntity);
        when(entityRepository.findAll(eq("student"), any(NeutralQuery.class))).thenReturn(le);
        when(entityRepository.updateWithRetries(studentEntity.getType(), studentEntity, totalRetries)).thenReturn(true);

        entityPersistHandler.setEntityRepository(entityRepository);
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();
        entityPersistHandler.handle(studentEntity, errorReport, reportStats);

        verify(entityRepository).updateWithRetries(studentEntity.getType(), studentEntity, totalRetries);

        // Test student entity without entity ID, so that repository will create a new one
        le.clear();
        SimpleEntity studentEntity2 = createStudentEntity(false);
        le.add(studentEntity2);

        entityPersistHandler.handle(studentEntity2, errorReport, reportStats);

        verify(entityRepository).createWithRetries(studentEntity.getType(), null, studentEntity.getBody(),
                studentEntity.getMetaData(), "student", totalRetries);

        Assert.assertFalse("Error report should not contain errors", reportStats.hasErrors());
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    public void testUpdateStudentEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        SimpleEntity studentEntity = createStudentEntity(true);
        SimpleEntity existingStudentEntity = createStudentEntity(true);

        existingStudentEntity.setEntityId(UUID.randomUUID().toString());

        // Student search.
        when(entityRepository.findAll(eq("student"), eq(regionIdStudentIdQuery))).thenReturn(
                Arrays.asList((Entity) existingStudentEntity));
        when(entityRepository.updateWithRetries("student", studentEntity, totalRetries)).thenReturn(true);

        entityPersistHandler.setEntityRepository(entityRepository);
        studentEntity.getMetaData().put(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.doHandling(studentEntity, errorReport, reportStats);

        verify(entityRepository).updateWithRetries("student", studentEntity, totalRetries);
        Assert.assertFalse("Error report should not contain errors", reportStats.hasErrors());
    }

    @Test
    public void testPersistanceExceptionHandling() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);
        DummyMessageReport errorReport = mock(DummyMessageReport.class);
        Mockito.doCallRealMethod().when(errorReport)
                .error(Mockito.any(ReportStats.class), Matchers.any(Source.class),
                        Mockito.any(CoreMessageCode.class), Mockito.anyString()
                        , Mockito.anyString(), Mockito.anyString(), Mockito.anyObject(), Mockito.any());

        ReportStats reportStats = new SimpleReportStats();

        SimpleEntity studentEntity = createStudentEntity(true);
        SimpleEntity existingStudentEntity = createStudentEntity(true);

        existingStudentEntity.setEntityId(UUID.randomUUID().toString());

        // Student search.
        when(entityRepository.findAll(eq("student"), eq(regionIdStudentIdQuery))).thenReturn(
                Arrays.asList((Entity) existingStudentEntity));
        ValidationError error = new ValidationError(ErrorType.REQUIRED_FIELD_MISSING, "field", null,
                new String[] { "String" });
        when(entityRepository.updateWithRetries("student", studentEntity, totalRetries)).thenThrow(
                new EntityValidationException(existingStudentEntity.getEntityId(), "student", Arrays.asList(error)));

        entityPersistHandler.setEntityRepository(entityRepository);
        studentEntity.getMetaData().put(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.doHandling(studentEntity, errorReport, reportStats);

        Assert.assertTrue("Error report should contain errors", reportStats.hasErrors());
        Mockito.verify(errorReport, Mockito.times(1)).error(Matchers.any(ReportStats.class),
                Matchers.any(Source.class), Matchers.eq(CoreMessageCode.CORE_0006), Matchers.anyString(),
                Matchers.anyString(), Matchers.anyString(), Matchers.anyObject(), Matchers.any());
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    public void testCreateStudentSchoolAssociationEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        // Create a new student-school association entity, and test creating it in the data store.
        SimpleEntity foundStudent = new SimpleEntity();
        foundStudent.setEntityId(INTERNAL_STUDENT_ID);

        LinkedList<Entity> studentList = new LinkedList<Entity>();
        studentList.add(foundStudent);

        // Student search.
        when(entityRepository.findAll(eq("student"), eq(regionIdStudentIdQuery))).thenReturn(studentList);

        // School search.
        SimpleEntity foundSchool = new SimpleEntity();
        foundSchool.setEntityId(INTERNAL_SCHOOL_ID);

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findAll(eq("school"), any(NeutralQuery.class))).thenReturn(schoolList);

        SimpleEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(STUDENT_ID, false);
        entityPersistHandler.setEntityRepository(entityRepository);
        studentSchoolAssociationEntity.getMetaData().put(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.doHandling(studentSchoolAssociationEntity, errorReport, reportStats);
        verify(entityRepository).createWithRetries(studentSchoolAssociationEntity.getType(), null,
                studentSchoolAssociationEntity.getBody(), studentSchoolAssociationEntity.getMetaData(),
                studentSchoolAssociationEntity.getType(), totalRetries);
        Assert.assertFalse("Error report should not contain errors", reportStats.hasErrors());
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    public void testUpdateStudentSchoolAssociationEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        // Create a new student-school association entity, and test creating it in the data store.
        NeutralRecordEntity foundStudent = new NeutralRecordEntity(null);

        LinkedList<Entity> studentList = new LinkedList<Entity>();
        studentList.add(foundStudent);

        // Student search.
        when(entityRepository.findAll(eq("student"), eq(regionIdStudentIdQuery))).thenReturn(studentList);

        // School search.
        NeutralRecordEntity foundSchool = new NeutralRecordEntity(null);

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findAll(eq("school"), eq(regionIdStudentIdQuery))).thenReturn(schoolList);

        SimpleEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(STUDENT_ID, true);
        SimpleEntity existingStudentSchoolAssociationEntity = createStudentSchoolAssociationEntity(STUDENT_ID, true);

        existingStudentSchoolAssociationEntity.setEntityId(UUID.randomUUID().toString());

        when(entityRepository.findAll(eq("studentSchoolAssociation"), eq(ssaQuery))).thenReturn(
                Arrays.asList((Entity) existingStudentSchoolAssociationEntity));

        when(
                entityRepository.updateWithRetries("studentSchoolAssociation", studentSchoolAssociationEntity,
                        totalRetries)).thenReturn(true);

        entityPersistHandler.setEntityRepository(entityRepository);
        studentSchoolAssociationEntity.getMetaData().put(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.doHandling(studentSchoolAssociationEntity, errorReport, reportStats);

        verify(entityRepository).updateWithRetries("studentSchoolAssociation", studentSchoolAssociationEntity,
                totalRetries);
        Assert.assertFalse("Error report should not contain errors", reportStats.hasErrors());
    }

    @Test
    public void testHandleFailedValidation() {
        /*
         * when validation fails for an entity, we should not try to persist
         */

        AbstractMessageReport report = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        SimpleEntity mockedEntity = mock(SimpleEntity.class);

        String expectedType = "student";
        when(mockedEntity.getType()).thenReturn(expectedType);

        Map<String, Object> expectedMap = new HashMap<String, Object>();
        when(mockedEntity.getBody()).thenReturn(expectedMap);

        Map<String, Object> expectedMetaData = new HashMap<String, Object>();
        expectedMetaData.put(REGION_ID_FIELD, REGION_ID);
        expectedMetaData.put(EXTERNAL_ID_FIELD, STUDENT_ID);
        when(mockedEntity.getMetaData()).thenReturn(expectedMetaData);

        entityPersistHandler.handle(mockedEntity, report, reportStats);

        verify(mockedEntityRepository, never()).create(expectedType, expectedMap, expectedMetaData, expectedType);
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     * @author tke 3/15/2012 modified to test the new id normalization strategy
     * @param setId
     *            : set entity ID if it is true.
     *            Added testing of record DB lookup and update, and support for association
     *            entities.
     */
    public SimpleEntity createStudentEntity(boolean setId) {
        SimpleEntity entity = new SimpleEntity();

        if (setId) {
            entity.setEntityId(STUDENT_ID);
        }

        entity.setStagedEntityId(STAGED_STUDENT_UUID);
        entity.setType("student");

        Map<String, Object> field = new HashMap<String, Object>();
        field.put("studentUniqueStateId", STUDENT_ID);
        field.put("Sex", "Male");

        entity.setBody(field);
        entity.setMetaData(new HashMap<String, Object>());

        return entity;
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    public SimpleEntity createSchoolEntity() {
        // Create neutral record for entity.
        SimpleEntity entity = new SimpleEntity();
        entity.setEntityId(SCHOOL_ID);
        entity.setType("school");

        // Create new entity from neutral record.
        return entity;
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     * @author tke 3/15/2012, modified to be consistent with the new ID normalization strategy
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    public SimpleEntity createStudentSchoolAssociationEntity(String studentId, boolean setId) {
        SimpleEntity entity = new SimpleEntity();

        if (setId) {
            entity.setEntityId(studentId);
        }

        entity.setStagedEntityId(STAGED_STUDENT_SCHOOL_UUID);
        entity.setType("studentSchoolAssociation");
        Map<String, Object> localParentIds = new HashMap<String, Object>();
        localParentIds.put("Student", studentId);
        localParentIds.put("School", SCHOOL_ID);
        entity.setMetaData(localParentIds);
        Map<String, Object> field = new HashMap<String, Object>();
        field.put("studentId", studentId);
        field.put("schoolId", SCHOOL_ID);
        field.put("ClassOf", "2014");
        entity.setBody(field);

        // Create and return new entity from neutral record.
        return entity;
    }

    public SimpleEntity createTeacherSchoolAssociationEntity(String teacherId, boolean setId) {
        // Create neutral record for entity.
        SimpleEntity entity = new SimpleEntity();
        if (setId) {
            entity.setEntityId(teacherId);
        }

        entity.setStagedEntityId(STAGED_TEACHER_SCHOOL_UUID);
        entity.setType("teacherSchoolAssociation");
        Map<String, Object> field = new HashMap<String, Object>();
        field.put("teacherId", teacherId);
        field.put("schoolId", SCHOOL_ID);
        entity.setBody(field);
        entity.setMetaData(new HashMap<String, Object>());

        // Create and return new entity from neutral record.
        return entity;
    }

    @Test
    public void testCreateTeacherSchoolAssociationEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        // Create a new student-school association entity, and test creating it in the data store.
        SimpleEntity foundTeacher = new SimpleEntity();
        foundTeacher.setEntityId(INTERNAL_STUDENT_ID);

        LinkedList<Entity> teacherList = new LinkedList<Entity>();
        teacherList.add(foundTeacher);

        // Teacher search.
        when(entityRepository.findAll(eq("teacher"), eq(regionIdStudentIdQuery))).thenReturn(teacherList);

        // School search.
        SimpleEntity foundSchool = new SimpleEntity();
        foundSchool.setEntityId(INTERNAL_SCHOOL_ID);

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findAll(eq("school"), eq(regionIdStudentIdQuery))).thenReturn(schoolList);

        SimpleEntity teacherSchoolAssociationEntity = createTeacherSchoolAssociationEntity(STUDENT_ID, false);
        entityPersistHandler.setEntityRepository(entityRepository);
        teacherSchoolAssociationEntity.getMetaData().put(EntityMetadataKey.TENANT_ID.getKey(), REGION_ID);
        entityPersistHandler.doHandling(teacherSchoolAssociationEntity, errorReport, reportStats);
        verify(entityRepository).createWithRetries(teacherSchoolAssociationEntity.getType(), null,
                teacherSchoolAssociationEntity.getBody(), teacherSchoolAssociationEntity.getMetaData(),
                teacherSchoolAssociationEntity.getType(), totalRetries);
        Assert.assertFalse("Error report should not contain errors", reportStats.hasErrors());
    }
}
