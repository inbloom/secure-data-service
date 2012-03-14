package org.slc.sli.ingestion.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
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
public class EntityPersistHandlerTest {

    @Autowired
    private EntityPersistHandler entityPersistHandler;

    private MongoEntityRepository mockedEntityRepository;

    private Validator<NeutralRecordEntity> mockedValidator;

    private static final String STUDENT_ID = "765432";
    private static final String SCHOOL_ID = "654321";
    private static final String INTERNAL_STUDENT_ID = "0x" + STUDENT_ID;
    private static final String INTERNAL_SCHOOL_ID = "0x" + SCHOOL_ID;
    private static final String BAD_STUDENT_ID = "234567";
    private static final String REGION_ID = "https://devapp1.slidev.org:443/sp";
    private static final String METADATA_BLOCK = "metaData";
    private static final String REGION_ID_FIELD = "idNamespace";
    private static final String EXTERNAL_ID_FIELD = "externalId";

    private Map<String, String> schoolFilterFields = new HashMap<String, String>();
    private Map<String, String> studentSchoolAssociationFilterFields = new HashMap<String, String>();
    private LinkedList<Entity> studentList = new LinkedList<Entity>();
    private Iterable<Entity> studentFound = studentList;
    private LinkedList<Entity> schoolList = new LinkedList<Entity>();
    private Iterable<Entity> schoolFound = schoolList;
    private LinkedList<Entity> studentSchoolAssociationList = new LinkedList<Entity>();
    private Iterable<Entity> studentSchoolAssociationFound = studentSchoolAssociationList;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        mockedEntityRepository = mock(MongoEntityRepository.class);
        entityPersistHandler.setEntityRepository(mockedEntityRepository);
        when(mockedEntityRepository.findByPaths("student", new HashMap<String, String>())).thenReturn(studentFound);

        // School search.
        schoolFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        schoolFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, SCHOOL_ID);
        when(mockedEntityRepository.findByPaths("school", schoolFilterFields)).thenReturn(schoolFound);

        // Student-School Association search.
        studentSchoolAssociationFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentSchoolAssociationFilterFields.put("body.studentId", INTERNAL_STUDENT_ID);
        studentSchoolAssociationFilterFields.put("body.schoolId", INTERNAL_SCHOOL_ID);
        when(mockedEntityRepository.findByPaths("studentSchoolAssociation", studentSchoolAssociationFilterFields))
                .thenReturn(studentSchoolAssociationFound);

        mockedValidator = mock(Validator.class);
        entityPersistHandler.setPreValidator(mockedValidator);
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
        when(entityRepository.findByPaths("student", studentFilterFields)).thenReturn(Collections.<Entity>emptyList());

        // Create a new student entity, and test creating it in the data store.
        NeutralRecordEntity studentEntity = createStudentEntity();

        entityPersistHandler.setEntityRepository(entityRepository);
        studentEntity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), REGION_ID);
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
        when(entityRepository.findByPaths("student", studentFilterFields)).thenReturn(
                Arrays.asList((Entity) existingStudentEntity));

        when(entityRepository.update("student", studentEntity)).thenReturn(true);

        entityPersistHandler.setEntityRepository(entityRepository);
        studentEntity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), REGION_ID);
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
        when(entityRepository.findByPaths("student", studentFilterFields)).thenReturn(
                Arrays.asList((Entity) existingStudentEntity));

        ValidationError error = new ValidationError(ErrorType.REQUIRED_FIELD_MISSING, "field", null,
                new String[] { "String" });
        when(entityRepository.update("student", studentEntity)).thenThrow(
                new EntityValidationException(existingStudentEntity.getEntityId(), "student", Arrays.asList(error)));

        entityPersistHandler.setEntityRepository(entityRepository);
        studentEntity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), REGION_ID);
        entityPersistHandler.doHandling(studentEntity, fr);

        Assert.assertTrue("Error report should contain errors", fr.hasErrors());
        Assert.assertEquals("Entity student - Record 0: Missing or empty field <field>", fr.getFaults().get(0).getMessage());
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
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
        when(entityRepository.findByPaths("student", studentFilterFields)).thenReturn(studentList);

        // School search.
        NeutralRecordEntity foundSchool = new NeutralRecordEntity(null);
        foundSchool.setEntityId(INTERNAL_SCHOOL_ID);

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findByPaths("school", schoolFilterFields)).thenReturn(schoolList);

        NeutralRecordEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(STUDENT_ID);
        entityPersistHandler.setEntityRepository(entityRepository);
        studentSchoolAssociationEntity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), REGION_ID);
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
        when(entityRepository.findByPaths("student", studentFilterFields)).thenReturn(studentList);

        // School search.
        NeutralRecordEntity foundSchool = new NeutralRecordEntity(null);
        foundSchool.setEntityId(INTERNAL_SCHOOL_ID);

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findByPaths("school", schoolFilterFields)).thenReturn(schoolList);

        NeutralRecordEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(STUDENT_ID);
        NeutralRecordEntity existingStudentSchoolAssociationEntity = createStudentSchoolAssociationEntity(STUDENT_ID);

        existingStudentSchoolAssociationEntity.setEntityId(UUID.randomUUID().toString());

        when(entityRepository.findByPaths("studentSchoolAssociation", studentSchoolAssociationFilterFields))
                .thenReturn(Arrays.asList((Entity) existingStudentSchoolAssociationEntity));

        when(entityRepository.update("studentSchoolAssociation", studentSchoolAssociationEntity)).thenReturn(true);

        entityPersistHandler.setEntityRepository(entityRepository);
        studentSchoolAssociationEntity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), REGION_ID);
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
        when(entityRepository.findByPaths("student", studentFilterFields)).thenReturn(Collections.<Entity>emptyList());

        // School search.
        NeutralRecordEntity foundSchool = new NeutralRecordEntity(null);
        foundSchool.setEntityId(INTERNAL_SCHOOL_ID);

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findByPaths("school", schoolFilterFields)).thenReturn(schoolList);

        NeutralRecordEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(BAD_STUDENT_ID);
        when(entityRepository.findByPaths("studentSchoolAssociation", studentSchoolAssociationFilterFields))
                .thenReturn(Arrays.asList((Entity) studentSchoolAssociationEntity));

        when(entityRepository.update("studentSchoolAssociation", studentSchoolAssociationEntity)).thenReturn(true);

        entityPersistHandler.setEntityRepository(entityRepository);
        studentSchoolAssociationEntity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), REGION_ID);
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
        studentSchoolAssociationEntity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), REGION_ID);
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
        testEntity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), nameSpace);

        //create an entity to reference
        NeutralRecordEntity refEntity = new NeutralRecordEntity(new NeutralRecord());
        refEntity.setEntityId(internalId);
        LinkedList<Entity> refResults = new LinkedList<Entity>();
        refResults.add(refEntity);

        //mock the db lookup
        Map<String, String> testFilterFields = new HashMap<String, String>();
        testFilterFields.put(METADATA_BLOCK + "." + EntityMetadataKey.ID_NAMESPACE.getKey(), nameSpace);
        testFilterFields.put(METADATA_BLOCK + "." + EntityMetadataKey.EXTERNAL_ID.getKey(), externalId);

        when(mockedEntityRepository.findByPaths(collectionName, testFilterFields))
                .thenReturn((Iterable<Entity>) refResults);

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
        when(entityRepository.findByPaths("teacher", teacherFilterFields)).thenReturn(teacherList);

        // School search.
        NeutralRecordEntity foundSchool = new NeutralRecordEntity(null);
        foundSchool.setEntityId(INTERNAL_SCHOOL_ID);

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findByPaths("school", schoolFilterFields)).thenReturn(schoolList);

        NeutralRecordEntity teacherSchoolAssociationEntity = createTeacherSchoolAssociationEntity(STUDENT_ID);
        entityPersistHandler.setEntityRepository(entityRepository);
        teacherSchoolAssociationEntity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), REGION_ID);
        entityPersistHandler.doHandling(teacherSchoolAssociationEntity, fr);
        verify(entityRepository).create(teacherSchoolAssociationEntity.getType(),
                teacherSchoolAssociationEntity.getBody(), teacherSchoolAssociationEntity.getMetaData(),
                teacherSchoolAssociationEntity.getType());
        Assert.assertFalse("Error report should not contain errors", fr.hasErrors());
    }

}
