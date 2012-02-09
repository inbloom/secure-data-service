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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.Validator;

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

    private ErrorReport mockedErrorReport = mock(ErrorReport.class);

    private static final String STUDENT_ID = "765432";
    private static final String SCHOOL_ID = "654321";
    private static final String BAD_STUDENT_ID = "234567";
    private static final String REGION_ID = "123456";
    private static final String METADATA_BLOCK = "metadata";
    private static final String REGION_ID_FIELD = "regionId";
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
        studentSchoolAssociationFilterFields.put("body.studentId", STUDENT_ID);
        studentSchoolAssociationFilterFields.put("body.schoolId", SCHOOL_ID);
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

        // Student search.
        Map<String, String> studentFilterFields = new HashMap<String, String>();
        studentFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, STUDENT_ID);
        when(entityRepository.findByPaths("student", studentFilterFields)).thenReturn(Collections.<Entity>emptyList());

        // Create a new student entity, and test creating it in the data store.
        NeutralRecordEntity studentEntity = createStudentEntity();

        entityPersistHandler.setEntityRepository(entityRepository);
        entityPersistHandler.doHandling(studentEntity, mockedErrorReport);

        verify(entityRepository).create(studentEntity.getType(), studentEntity.getBody(),
                studentEntity.getMetaData(), "student");
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    public void testUpdateStudentEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);

        NeutralRecordEntity studentEntity = createStudentEntity();

        // Student search.
        Map<String, String> studentFilterFields = new HashMap<String, String>();
        studentFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, STUDENT_ID);
        when(entityRepository.findByPaths("student", studentFilterFields)).thenReturn(Arrays.asList((Entity) studentEntity));

        when(entityRepository.update("student", studentEntity)).thenReturn(true);

        entityPersistHandler.setEntityRepository(entityRepository);
        entityPersistHandler.doHandling(studentEntity, mockedErrorReport);

        verify(entityRepository).update("student", studentEntity);
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    public void testCreateStudentSchoolAssociationEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);

        // Create a new student-school association entity, and test creating it in the data store.
        NeutralRecordEntity foundStudent = new NeutralRecordEntity(null);
        foundStudent.setEntityId("0x765432");

        LinkedList<Entity> studentList = new LinkedList<Entity>();
        studentList.add(foundStudent);

        // Student search.
        Map<String, String> studentFilterFields = new HashMap<String, String>();
        studentFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, STUDENT_ID);
        when(entityRepository.findByPaths("student", studentFilterFields)).thenReturn(studentList);

        // School search.
        NeutralRecordEntity foundSchool = new NeutralRecordEntity(null);
        foundSchool.setEntityId("0x654321");

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findByPaths("school", schoolFilterFields)).thenReturn(schoolList);

        NeutralRecordEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(STUDENT_ID);
        entityPersistHandler.setEntityRepository(entityRepository);
        entityPersistHandler.doHandling(studentSchoolAssociationEntity, mockedErrorReport);
        verify(entityRepository).create(studentSchoolAssociationEntity.getType(),
                studentSchoolAssociationEntity.getBody(), studentSchoolAssociationEntity.getMetaData(),
                studentSchoolAssociationEntity.getType());
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    public void testUpdateStudentSchoolAssociationEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);

        // Create a new student-school association entity, and test creating it in the data store.
        NeutralRecordEntity foundStudent = new NeutralRecordEntity(null);
        foundStudent.setEntityId("0x765432");

        LinkedList<Entity> studentList = new LinkedList<Entity>();
        studentList.add(foundStudent);

        // Student search.
        Map<String, String> studentFilterFields = new HashMap<String, String>();
        studentFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, STUDENT_ID);
        when(entityRepository.findByPaths("student", studentFilterFields)).thenReturn(studentList);

        // School search.
        NeutralRecordEntity foundSchool = new NeutralRecordEntity(null);
        foundSchool.setEntityId("0x654321");

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findByPaths("school", schoolFilterFields)).thenReturn(schoolList);

        NeutralRecordEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(STUDENT_ID);
        when(entityRepository.findByPaths("studentSchoolAssociation", studentSchoolAssociationFilterFields))
                .thenReturn(Arrays.asList((Entity) studentSchoolAssociationEntity));

        when(entityRepository.update("studentSchoolAssociation", studentSchoolAssociationEntity)).thenReturn(true);

        entityPersistHandler.setEntityRepository(entityRepository);
        entityPersistHandler.doHandling(studentSchoolAssociationEntity, mockedErrorReport);

        verify(entityRepository).update("studentSchoolAssociation", studentSchoolAssociationEntity);
    }

    @Test
    public void testInvalidUpdateStudentSchoolAssociationEntity() {
        MongoEntityRepository entityRepository = mock(MongoEntityRepository.class);

        // Create a new student-school association entity, and test creating it in the data store.
        NeutralRecordEntity foundStudent = new NeutralRecordEntity(null);
        foundStudent.setEntityId("0x765432");

        LinkedList<Entity> studentList = new LinkedList<Entity>();
        studentList.add(foundStudent);

        // Student search.
        Map<String, String> studentFilterFields = new HashMap<String, String>();
        studentFilterFields.put(METADATA_BLOCK + "." + REGION_ID_FIELD, REGION_ID);
        studentFilterFields.put(METADATA_BLOCK + "." + EXTERNAL_ID_FIELD, BAD_STUDENT_ID);
        when(entityRepository.findByPaths("student", studentFilterFields)).thenReturn(Collections.<Entity>emptyList());

        // School search.
        NeutralRecordEntity foundSchool = new NeutralRecordEntity(null);
        foundSchool.setEntityId("0x654321");

        LinkedList<Entity> schoolList = new LinkedList<Entity>();
        schoolList.add(foundSchool);
        when(entityRepository.findByPaths("school", schoolFilterFields)).thenReturn(schoolList);

        NeutralRecordEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(BAD_STUDENT_ID);
        when(entityRepository.findByPaths("studentSchoolAssociation", studentSchoolAssociationFilterFields))
                .thenReturn(Arrays.asList((Entity) studentSchoolAssociationEntity));

        when(entityRepository.update("studentSchoolAssociation", studentSchoolAssociationEntity)).thenReturn(true);

        entityPersistHandler.setEntityRepository(entityRepository);
        entityPersistHandler.doHandling(studentSchoolAssociationEntity, mockedErrorReport);

        verify(entityRepository, never()).update("studentSchoolAssociation", studentSchoolAssociationEntity);
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     *         Added testing of record DB lookup and update, and support for association entities.
     */
    @Test
    public void testInvalidateStudentSchoolAssociationEntity() {
        NeutralRecordEntity studentSchoolAssociationEntity = createStudentSchoolAssociationEntity(BAD_STUDENT_ID);
        studentSchoolAssociationEntity.setAttributeField("studentId", BAD_STUDENT_ID);

        entityPersistHandler.doHandling(studentSchoolAssociationEntity, mockedErrorReport);
        verify(mockedEntityRepository, never()).create(studentSchoolAssociationEntity.getType(),
                studentSchoolAssociationEntity.getBody(), studentSchoolAssociationEntity.getMetaData(),
                studentSchoolAssociationEntity.getType());
        verify(mockedEntityRepository, never()).update(studentSchoolAssociationEntity.getType(),
                studentSchoolAssociationEntity);
    }

    @Test
    public void testHandlePassedValidation() {
        /*
         * when validation passes for an entity, we should try to persist
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

        when(mockedErrorReport.hasErrors()).thenReturn(false);

        entityPersistHandler.handle(mockedEntity, mockedErrorReport);

        verify(mockedEntityRepository).create(expectedType, expectedMap, expectedMetaData, expectedType);
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

}
