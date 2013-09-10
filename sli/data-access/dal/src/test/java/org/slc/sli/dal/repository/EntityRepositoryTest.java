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

package org.slc.sli.dal.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.*;

import javax.annotation.Resource;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.AccessibilityCheck;
import org.slc.sli.domain.CascadeResult;
import org.slc.sli.domain.CascadeResultError;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * JUnits for DAL
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class EntityRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        TenantContext.setTenantId("SLIUnitTest");
    }

    @Resource(name = "mongoEntityRepository")
    private Repository<Entity> repository;

    boolean schoolLineageIs(String schoolId, Set<String> expectedEdOrgs) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, schoolId));
        Entity school = repository.findOne(EntityNames.EDUCATION_ORGANIZATION, neutralQuery);
        ArrayList<String> edOrgs = (ArrayList<String>) school.getMetaData().get("edOrgs");
        if (edOrgs == null) {
            return expectedEdOrgs.isEmpty();
        } else if (!expectedEdOrgs.equals(new HashSet<String>(edOrgs))) {
            System.out.println("School edOrg lineage incorrect. Expected " + expectedEdOrgs + ", got " + edOrgs);
            return false;
        }
        return true;
    }

    @Test
    public void testSchoolLineage() {
        NeutralQuery query = null;

        repository.deleteAll(EntityNames.EDUCATION_ORGANIZATION, null);

        DBObject indexKeys = new BasicDBObject("body." + ParameterConstants.STATE_ORGANIZATION_ID, 1);
        mongoTemplate.getCollection(EntityNames.EDUCATION_ORGANIZATION).ensureIndex(indexKeys);
        mongoTemplate.getCollection(EntityNames.EDUCATION_ORGANIZATION).ensureIndex(new BasicDBObject("metaData.edOrgs", 1), new BasicDBObject("sparse", true));

        // Add a school
        Entity school = createEducationOrganizationEntity("school1", "school", "School", null);
        Set<String> expectedEdOrgs = new HashSet();
        expectedEdOrgs.add(school.getEntityId());
        assertTrue("School not found in lineage.", schoolLineageIs(school.getEntityId(), expectedEdOrgs));

        // Add an SEA
        Entity sea = createEducationOrganizationEntity("sea1", "stateEducationAgency", "State Education Agency", null);
        assertTrue("After adding SEA expected edOrgs not found in lineage.", schoolLineageIs(school.getEntityId(), expectedEdOrgs));

        // Add an LEA
        List<String> parentRefs = new ArrayList<String>(Arrays.asList(sea.getEntityId()));
        Entity lea = createEducationOrganizationEntity("lea1", "localEducationAgency", "Local Education Agency", parentRefs);
        assertTrue("After adding LEA expected edOrgs not found in lineage.", schoolLineageIs(school.getEntityId(), expectedEdOrgs));

        // doUpdate School parent ref to LEA
        List<String> parentRefsUpdate = new ArrayList<String>(Arrays.asList(lea.getEntityId()));
        query = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, school.getEntityId()));
        Update update = new Update().set("body." + ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE, parentRefsUpdate);
        repository.doUpdate(EntityNames.EDUCATION_ORGANIZATION, query, update);
        expectedEdOrgs.add(lea.getEntityId());
        expectedEdOrgs.add(sea.getEntityId());
        assertTrue("After updating school parent ref expected edOrgs not found in lineage.", schoolLineageIs(school.getEntityId(), expectedEdOrgs));

        // Patch LEA parent ref to an undefined id
        List<String> parentRefsPatch = new ArrayList<String>(Arrays.asList("undefinedId"));
        Map<String, Object> newValues = new HashMap<String, Object>();
        newValues.put(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE, parentRefsPatch);
        repository.patch("localEducationEntity", EntityNames.EDUCATION_ORGANIZATION, lea.getEntityId(), newValues);
        expectedEdOrgs.remove(sea.getEntityId());
        assertTrue("After updating school parent ref expected edOrgs not found in lineage.", schoolLineageIs(school.getEntityId(), expectedEdOrgs));

        // Update LEA to set parent ref back to SEA
        repository.update(EntityNames.EDUCATION_ORGANIZATION, lea, false);
        expectedEdOrgs.add(sea.getEntityId());
        assertTrue("After updating school parent ref expected edOrgs not found in lineage.", schoolLineageIs(school.getEntityId(), expectedEdOrgs));

        // Delete LEA - lineage should be recalculated
        repository.delete(EntityNames.EDUCATION_ORGANIZATION, lea.getEntityId());
        expectedEdOrgs.remove(lea.getEntityId());
        expectedEdOrgs.remove(sea.getEntityId());
        assertTrue("After deleting lea expected edOrgs not found in lineage.", schoolLineageIs(school.getEntityId(), expectedEdOrgs));

        // Insert LEA with no parent ref to SEA
        lea.getBody().remove(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE);
        Entity insertedLea = ((MongoEntityRepository)repository).insert(lea, EntityNames.EDUCATION_ORGANIZATION);
        expectedEdOrgs.add(lea.getEntityId());
        assertTrue("After re-adding LEA with no parent ref expected edOrgs not found in lineage.", schoolLineageIs(school.getEntityId(), expectedEdOrgs));

        // findAndUpdate School parent ref to SEA
        List<String> parentRefsSEA = new ArrayList<String>(Arrays.asList(sea.getEntityId()));
        query = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, school.getEntityId()));
        update = new Update().set("body." + ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE, parentRefsSEA);
        repository.findAndUpdate(EntityNames.EDUCATION_ORGANIZATION, query, update);
        expectedEdOrgs.remove(lea.getEntityId());
        expectedEdOrgs.add(sea.getEntityId());
        assertTrue("After updating school parent ref to SEA, expected edOrgs not found in lineage.", schoolLineageIs(school.getEntityId(), expectedEdOrgs));

        // Clear lineage for negative tests
        clearSchoolLineage(school);

        // Create an unrelated entity type and make sure school lineage isn't recalculated
        repository.create("student", buildTestStudentEntity());
        assertTrue("After adding a student lineage school lineage should not change.", schoolLineageIs(school.getEntityId(), new HashSet<String>()));

        // Patch an edOrg non-parent-ref and make sure school lineage isn't recalculated
        newValues = new HashMap<String, Object>();
        newValues.put("body.nameOfInstitution", "updatedName");
        repository.patch("school", EntityNames.EDUCATION_ORGANIZATION, school.getEntityId(), newValues);
        assertTrue("Updating a school non-parent ref should not change school lineage.", schoolLineageIs(school.getEntityId(), new HashSet<String>()));

        mongoTemplate.getCollection(EntityNames.EDUCATION_ORGANIZATION).drop();

    }

    private void clearSchoolLineage(Entity school) {
        school.getMetaData().remove("edOrgs");
        repository.update(EntityNames.EDUCATION_ORGANIZATION, school, false);
    }

    private Entity createEducationOrganizationEntity(String stateOrgId, String type, String organizationCategory, List<String> parentRefs) {
        Random rand = new Random();
        Map<String, Object> body = new HashMap<String, Object>();
        List<Map<String, String>> addresses = new ArrayList<Map<String, String>>();
        Map<String, String> address = new HashMap<String, String>();
        List<String> organizationCategories = new ArrayList<String>();

        /*
     	    <xs:enumeration value="State Education Agency" />
			<xs:enumeration value="Education Service Center" />
			<xs:enumeration value="Local Education Agency" />
			<xs:enumeration value="School" />
         */
        organizationCategories.add(organizationCategory);
        body.put(ParameterConstants.ORGANIZATION_CATEGORIES, organizationCategories);

        address.put("streetNumberName", rand.nextInt(100) + " Hill Street");
        address.put("city", "My City");
        address.put("stateAbbreviation", "IL");
        address.put("postalCode", "1235");
        addresses.add(address);
        body.put("address", addresses);

        if (parentRefs != null && !parentRefs.isEmpty()) {
            body.put(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE, parentRefs);
        }
        body.put("nameOfInstitution", stateOrgId + "Name");
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, stateOrgId);
        return repository.create(type, body, EntityNames.EDUCATION_ORGANIZATION);
    }

    @Test
    public void testDeleteAll() {
        repository.deleteAll("student", null);

        DBObject indexKeys = new BasicDBObject("body.firstName", 1);
        mongoTemplate.getCollection("student").ensureIndex(indexKeys);

        Map<String, Object> studentMap = buildTestStudentEntity();
        studentMap.put("firstName", "John");
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", studentMap);
        assertEquals(5, repository.count("student", new NeutralQuery()));
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("firstName=John"));
        repository.deleteAll("student", neutralQuery);
        assertEquals(4, repository.count("student", new NeutralQuery()));

        repository.deleteAll("student", null);
        mongoTemplate.getCollection("student").dropIndex(indexKeys);
    }

    private AccessibilityCheck access = new AccessibilityCheck() {
        // grant access to all entities
        // TODO exercise access denied logic
        @Override
        public boolean accessibilityCheck(String id) {
            return true;
        }
    };

    private AccessibilityCheck accessDenied = new AccessibilityCheck() {
        int count = 0;

        // grant access to no entities
        // TODO exercise access denied logic
        @Override
        public boolean accessibilityCheck(String id) {
            count++;
            if (count > 1) {
                // deny access after the first check
                return false;
            }
            return true;
        }
    };

    @Test
    public void testSafeDelete() {
//        testSafeDeleteHelper(collectionName, overridingId, cascade, dryrun, forced, logViolations,maxObjects, access, leafDataOnly,
//                leafDataOnly, expectedNObjects, expectedDepth, expectedStatus, expectedErrorType, expectedWarningType)

        // Test leaf node delete success : cascade=false and dryrun=false
        testSafeDeleteHelper("gradingPeriod", null, false, false, false, false, null, access, true, 1, MongoEntityRepository.DELETE_BASE_DEPTH, CascadeResult.Status.SUCCESS, null, null);

        // Test leaf node delete failure : cascade=false and dryrun=false
        testSafeDeleteHelper("gradingPeriod", null, false, false, false, false, null, access, false, 0, MongoEntityRepository.DELETE_BASE_DEPTH, CascadeResult.Status.ERROR, CascadeResultError.ErrorType.CHILD_DATA_EXISTS, null);

        // Test cascade=false and dryrun=true
        testSafeDeleteHelper("gradingPeriod", null, false, true, false, false, null, access, false, 0, MongoEntityRepository.DELETE_BASE_DEPTH, CascadeResult.Status.ERROR, CascadeResultError.ErrorType.CHILD_DATA_EXISTS, null);

        // Test cascade=true and dryrun=true
        testSafeDeleteHelper("gradingPeriod", null, true, true, false, false, null, access, false, 4, MongoEntityRepository.DELETE_BASE_DEPTH+1, CascadeResult.Status.SUCCESS, null, null);

        // Test cascade=true and dryrun=false
        testSafeDeleteHelper("gradingPeriod", null, true, false, false, false, null, access, false, 4, MongoEntityRepository.DELETE_BASE_DEPTH+1, CascadeResult.Status.SUCCESS, null, null);

        // Test maxobjects
        testSafeDeleteHelper("gradingPeriod", null, true, false, false, false, 2, access, false, 4, MongoEntityRepository.DELETE_BASE_DEPTH+1, CascadeResult.Status.MAX_OBJECTS_EXCEEDED, null, null);

        // Test access denied
        testSafeDeleteHelper("gradingPeriod", null, true, false, false, false, null, accessDenied, false, 0, MongoEntityRepository.DELETE_BASE_DEPTH, CascadeResult.Status.ERROR, CascadeResultError.ErrorType.ACCESS_DENIED, null);

        // Test deletion from a non-existent collection
        testSafeDeleteHelper("nonexistentCollection", null, true, false, false, false, null, access, false, 0, MongoEntityRepository.DELETE_BASE_DEPTH, CascadeResult.Status.ERROR, CascadeResultError.ErrorType.DELETE_ERROR, null);

        // Test deletion of a non-existent id
        testSafeDeleteHelper("gradingPeriod", "noMatchId", true, false, false, false, null, access, false, 0, MongoEntityRepository.DELETE_BASE_DEPTH, CascadeResult.Status.ERROR, CascadeResultError.ErrorType.DELETE_ERROR, null);

        // Test leaf forced delete
        testSafeDeleteHelper("gradingPeriod", null, false, false, true, true, null, access, true, 1, MongoEntityRepository.DELETE_BASE_DEPTH, CascadeResult.Status.SUCCESS, null, null);

        // Test forced delete with children
        testSafeDeleteHelper("gradingPeriod", null, false, false, true, false, null, access, false, 1, MongoEntityRepository.DELETE_BASE_DEPTH, CascadeResult.Status.SUCCESS, null, null);

        // Test forced delete with logViolations
        testSafeDeleteHelper("gradingPeriod", null, false, false, true, true, null, access, false, 1, MongoEntityRepository.DELETE_BASE_DEPTH, CascadeResult.Status.SUCCESS, null, CascadeResultError.ErrorType.CHILD_DATA_EXISTS);
    }

    private void testSafeDeleteHelper(String entityType, String overridingId,
            boolean cascade, boolean dryrun, boolean forced, boolean logViolations,
            Integer maxObjects, AccessibilityCheck access,
            boolean leafDataOnly,int expectedNObjects, int expectedDepth,
            CascadeResult.Status expectedStatus,
            CascadeResultError.ErrorType expectedErrorType, CascadeResultError.ErrorType expectedWarningType) {
//        System.out.println("Testing safeDelete: ");
//        System.out.println("   entity type             : " + entityType);
//        System.out.println("   override id             : " + overridingId);
//        System.out.println("   cascade                 : " + cascade);
//        System.out.println("   dryrun                  : " + dryrun);
//        System.out.println("   maxObjects              : " + maxObjects);
//        System.out.println("   leaf data only          : " + leafDataOnly);
//        System.out.println("   expected affected count : " + expectedNObjects);
//        System.out.println("   expected depth          : " + expectedDepth);

        CascadeResult result = null;
        String idToDelete = prepareSafeDeleteGradingPeriodData(leafDataOnly);

        // used to test bad id scenario
        if (overridingId != null) {
            idToDelete = overridingId;
        }

        result = repository.safeDelete(entityType, idToDelete, cascade, dryrun, forced, logViolations, maxObjects, access);

        // check for at least one instance of the expected error type
        boolean errorMatchFound = false;
        List<CascadeResultError> errors = result.getErrors();
        if (expectedErrorType == null && errors != null && errors.size() == 0) {
            errorMatchFound = true;
        } else {
            for (CascadeResultError error : errors) {
                if (error.getErrorType() == expectedErrorType) {
                    errorMatchFound = true;
                    break;
                }
            }
        }

//        for (CascadeResultError error : result.getErrors()) {
//            System.out.println(error);
//        }

        // check for at least one instance of the expected warning type
        boolean warningMatchFound = false;
        List<CascadeResultError> warnings = result.getWarnings();
        if (expectedWarningType == null && warnings != null && warnings.size() == 0) {
            warningMatchFound = true;
        } else {
            for(CascadeResultError warning : warnings) {
                if (warning.getErrorType() == expectedWarningType) {
                    warningMatchFound = true;
                    break;
                }
            }
        }

//        for(CascadeResultError warning : result.getWarnings()) {
//            System.out.println(warning);
//        }

        //   verify expected results
        assertEquals(expectedNObjects, result.getnObjects());
        assertEquals(expectedDepth, result.getDepth());
        assertEquals(expectedStatus, result.getStatus());
        assertTrue(errorMatchFound);
        assertTrue(warningMatchFound);
    }

    private String prepareSafeDeleteGradingPeriodData(boolean justLeaf) {
        clearSafeDeleteGradingPeriodData();

        // populate the leaf grading period entity to be deleted
        String idToDelete = prepareSafeDeleteGradingPeriodLeafData();

        if (!justLeaf) {
            prepareSafeDeleteGradingPeriodReferenceData(idToDelete);
        }

        return idToDelete;
    }

    private void clearSafeDeleteGradingPeriodData() {
        repository.deleteAll("gradingPeriod", null);
        repository.deleteAll("session", null);
        repository.deleteAll(MongoRepository.CUSTOM_ENTITY_COLLECTION, null);
        repository.deleteAll("yearlyTranscript", null); // actual mongo
                                                        // collection for grade
                                                        // and reportCard
    }

    private String prepareSafeDeleteGradingPeriodLeafData() {
        DBObject indexKeys = new BasicDBObject("body.beginDate", 1);
        mongoTemplate.getCollection("gradingPeriod").ensureIndex(indexKeys);

        // create a minimal gradingPeriod document
        Map<String, Object> gradingPeriodIdentity = new HashMap<String, Object>();
        gradingPeriodIdentity.put("gradingPeriod", "gradingPeriod1");
        gradingPeriodIdentity.put("schoolYear", "2011-2012");
        gradingPeriodIdentity.put("schoolId", "schoolId1");
        Map<String, Object> gradingPeriodBody = new HashMap<String, Object>();
        gradingPeriodBody.put("gradingPeriodIdentity", gradingPeriodIdentity);
        gradingPeriodBody.put("beginDate", "beginDate1");
        repository.create("gradingPeriod", gradingPeriodBody);

        // get the db id of the gradingPeriod - there is only one
        NeutralQuery neutralQuery = new NeutralQuery();
        Entity gradingPeriod1 = repository.findOne("gradingPeriod", neutralQuery);
        return gradingPeriod1.getEntityId();
    }

    private void prepareSafeDeleteGradingPeriodReferenceData(String idToDelete) {
        DBObject indexKeys = new BasicDBObject("body.gradingPeriodId", 1);
        mongoTemplate.getCollection("grade").ensureIndex(indexKeys);
        mongoTemplate.getCollection("gradeBookEntry").ensureIndex(indexKeys);
        mongoTemplate.getCollection("reportCard").ensureIndex(indexKeys);
        mongoTemplate.getCollection(MongoRepository.CUSTOM_ENTITY_COLLECTION).ensureIndex(
                "metaData." + MongoRepository.CUSTOM_ENTITY_ENTITY_ID);
        DBObject indexKeysList = new BasicDBObject("body.gradingPeriodReference", 1);
        mongoTemplate.getCollection("session").ensureIndex(indexKeysList);

        // add a custom entity referencing the entity to be deleted
        Map<String, Object> customEntityMetaData = new HashMap<String, Object>();
        customEntityMetaData.put(MongoRepository.CUSTOM_ENTITY_ENTITY_ID, idToDelete);
        Map<String, Object> customEntityBody = new HashMap<String, Object>();
        customEntityBody.put("customBodyData", "customData1");
        repository.create(MongoRepository.CUSTOM_ENTITY_COLLECTION, customEntityBody,
                customEntityMetaData, MongoRepository.CUSTOM_ENTITY_COLLECTION);
        customEntityMetaData.put(MongoRepository.CUSTOM_ENTITY_ENTITY_ID, "nonMatchingId");
        customEntityBody.put("customBodyData", "customData2");
        repository.create(MongoRepository.CUSTOM_ENTITY_COLLECTION, customEntityBody,
                customEntityMetaData, MongoRepository.CUSTOM_ENTITY_COLLECTION);

        // add referencing grade entities
        Map<String, Object> gradeMap = new HashMap<String, Object>();
        gradeMap.put("studentId", "studentId1");
        gradeMap.put("sectionId", "sectionId1");
        gradeMap.put("schoolYear", "2011-2012");
        gradeMap.put("gradingPeriodId", "noMatch");
        repository.create("grade", gradeMap); // add one non-matching document
        gradeMap.put("studentId", "studentId2");
        gradeMap.put("sectionId", "sectionId2");
        gradeMap.put("schoolYear", "2011-2012");
        gradeMap.put("gradingPeriodId", idToDelete);
        repository.create("grade", gradeMap); // add matching document

        // // add referencing gradeBookEntry entities - excluded since the
        // reference type is the same as grade
        // Map<String, Object> gradeBookEntryMap = new HashMap<String,
        // Object>();
        // gradeBookEntryMap.put("gradebookEntryType", "gradebookEntryType1");
        // gradeBookEntryMap.put("sectionId", "sectionId1");
        // gradeBookEntryMap.put("gradingPeriodId", idToDelete);
        // repository.create("gradeBookEntry", gradeBookEntryMap); // add one
        // non-matching document
        // gradeBookEntryMap.put("gradebookEntryType", "gradebookEntryType2");
        // gradeBookEntryMap.put("sectionId", "sectionId2");
        // gradeBookEntryMap.put("gradingPeriodId", "noMatch");
        // repository.create("gradeBookEntry", gradeBookEntryMap); // add
        // matching document

        // add referencing resportCard entities
        Map<String, Object> reportCardMap = new HashMap<String, Object>();
        reportCardMap.put("schoolYear", "2011-2012");
        reportCardMap.put("studentId", "studentId1");
        reportCardMap.put("gradingPeriodId", "noMatch");
        repository.create("reportCard", reportCardMap); // add one non-matching
                                                        // document
        reportCardMap.put("schoolYear", "2011-2012");
        reportCardMap.put("studentId", "studentId2");
        reportCardMap.put("gradingPeriodId", idToDelete);
        repository.create("reportCard", reportCardMap); // add matching document

        // create a minimal session document
        Map<String, Object> sessionMap = new HashMap<String, Object>();
        sessionMap.put("sessionName", "session1");
        sessionMap.put("schoolId", "schoolId1");
        List<String> gradingPeriodRefArray = new ArrayList<String>();
        gradingPeriodRefArray.add("dog");
        sessionMap.put("gradingPeriodReference", gradingPeriodRefArray);
        repository.create("session", sessionMap);
        sessionMap.put("sessionName", "session2");
        sessionMap.put("schoolId", "schoolId2");
        gradingPeriodRefArray.add(idToDelete);
        gradingPeriodRefArray.add("mousearama");
        sessionMap.put("gradingPeriodReference", gradingPeriodRefArray);
        repository.create("session", sessionMap);
    }

    @Test
    public void testDeleteAttendanceEventWithBogusId() {
        // Result is error, and Attendance record should remain unchanged in DB.
        String attendanceRecordId = insertAttendanceEventData(false);
        String bogusAttendanceRecordId = "123-abc-789-def-e1e10";
        Entity bogusDeleteAssessment = createDeleteAttendanceEntity("Excused: sick",
                "Excused Absence", "2012-04-18", bogusAttendanceRecordId);
        CascadeResult bogusDeleteResult = repository.safeDelete(bogusDeleteAssessment,
                bogusAttendanceRecordId, false, false, false, false, 1, null);
        List<Map<String, String>> attendanceEvents = getAttendanceEvents(false);
        Entity attendanceRecord = getAttendanceRecord(attendanceRecordId);
        Assert.assertEquals("Delete result should be error", CascadeResult.Status.ERROR,
                bogusDeleteResult.getStatus());
        Assert.assertNotNull("Attendance record should exist in DB", attendanceRecord);
        Assert.assertEquals("Field attendanceEvent mismatch", attendanceEvents, attendanceRecord
                .getBody().get("attendanceEvent"));
    }

    @Test
    public void testDeleteAttendanceEventWithBogusField() {
        // Result is success, but Attendance record should remain unchanged in DB.
        String attendanceRecordId = insertAttendanceEventData(false);
        Entity bogusDeleteAssessment = createDeleteAttendanceEntity("Excused: dead",
                "Excused Absence", "2012-04-18", attendanceRecordId);
        CascadeResult bogusDeleteResult = repository.safeDelete(bogusDeleteAssessment,
                attendanceRecordId, false, false, false, false, 1, null);
        List<Map<String, String>> attendanceEvents = getAttendanceEvents(false);
        Entity attendanceRecord = getAttendanceRecord(attendanceRecordId);
        Assert.assertEquals("Delete result should be success", CascadeResult.Status.SUCCESS,
                bogusDeleteResult.getStatus());
        Assert.assertNotNull("Attendance record should exist in DB", attendanceRecord);
        Assert.assertEquals("Field attendanceEvent mismatch", attendanceEvents, attendanceRecord
                .getBody().get("attendanceEvent"));
    }

    @Test
    public void testDeleteNextToLastAttendanceEvent() {
        String attendanceRecordId = insertAttendanceEventData(false);
        Entity deleteAssessment = createDeleteAttendanceEntity("Excused: sick", "Excused Absence",
                "2012-04-18", attendanceRecordId);
        CascadeResult deleteResult1 = repository.safeDelete(deleteAssessment,
                attendanceRecordId, false, false, false, false, 1, null);
        List<Map<String, String>> attendanceEvents = getAttendanceEvents(true);
        Entity attendanceRecord = getAttendanceRecord(attendanceRecordId);
        Assert.assertEquals("Delete result should be success", CascadeResult.Status.SUCCESS,
                deleteResult1.getStatus());
        Assert.assertNotNull("Attendance record should exist in DB", attendanceRecord);
        Assert.assertEquals("Field attendanceEvent mismatch", attendanceEvents, attendanceRecord
                .getBody().get("attendanceEvent"));
    }

    @Test
    public void testDeleteLastAttendanceEvent() {
        String attendanceRecordId = insertAttendanceEventData(true);
        Entity deleteAssessment = createDeleteAttendanceEntity("Missed school bus", "Tardy",
                "2011-10-26", attendanceRecordId);
        CascadeResult deleteResult2 = repository.safeDelete(deleteAssessment,
                attendanceRecordId, false, false, false, false, 1, null);
        Entity attendanceRecord = getAttendanceRecord(attendanceRecordId);
        Assert.assertEquals("Delete result should be success", CascadeResult.Status.SUCCESS,
                deleteResult2.getStatus());
        Assert.assertNull("Attendance record should not exist in DB", attendanceRecord);
    }

    private String insertAttendanceEventData(boolean skipFirst) {
        // Clear attendance collection.
        repository.deleteAll("attendance", null);

        // Populate the attendance record to be deleted, and add it to the db.
        Map<String, Object> attendanceMap = new HashMap<String, Object>();
        List<Map<String, String>> attendanceEvents = getAttendanceEvents(skipFirst);
        attendanceMap.put("attendanceEvent", attendanceEvents);
        attendanceMap.put("schoolId", "schoolId1");
        attendanceMap.put("schoolYear", "2011-2012");
        attendanceMap.put("studentId", "studentId1");
        repository.create("attendance", attendanceMap);

        // Get the db id of the attendance record; there is only one.
        NeutralQuery neutralQuery = new NeutralQuery();
        Entity attendance = repository.findOne("attendance", neutralQuery);
        return attendance.getEntityId();
    }

    private Entity getAttendanceRecord(String id) {
        return repository.findById("attendance", id);
    }

    private List<Map<String, String>> getAttendanceEvents(boolean skipFirst) {
        List<Map<String, String>> attendanceEvents = new ArrayList<Map<String, String>>();
        if (!skipFirst) {
            Map<String, String> attendanceEvent1 = new HashMap<String, String>();
            attendanceEvent1.put("reason", "Excused: sick");
            attendanceEvent1.put("event", "Excused Absence");
            attendanceEvent1.put("date", "2012-04-18");
            attendanceEvents.add(attendanceEvent1);
        }
        Map<String, String> attendanceEvent2 = new HashMap<String, String>();
        attendanceEvent2.put("reason", "Missed school bus");
        attendanceEvent2.put("event", "Tardy");
        attendanceEvent2.put("date", "2011-10-26");
        attendanceEvents.add(attendanceEvent2);

        return attendanceEvents;
    }

    private Entity createDeleteAttendanceEntity(String reason, String event, String date, String id) {
        Map<String, Object> attendanceBody = new HashMap<String, Object>();
        List<Map<String, String>> attendanceEvents = new ArrayList<Map<String, String>>();
        Map<String, String> attendanceEvent = new HashMap<String, String>();
        attendanceEvent.put("reason", reason);
        attendanceEvent.put("event", event);
        attendanceEvent.put("date", date);
        attendanceEvents.add(attendanceEvent);
        attendanceBody.put("attendanceEvent", attendanceEvents);
        attendanceBody.put("schoolId", "schoolId1");
        attendanceBody.put("schoolYear", "2011-2012");
        attendanceBody.put("studentId", "studentId1");
        return new MongoEntity("attendance", id, attendanceBody, new HashMap<String, Object>());
    }

    @Test
    public void testCRUDEntityRepository() {

        // clean up the existing student data
        repository.deleteAll("student", null);

        // create new student entity
        Map<String, Object> student = buildTestStudentEntity();

        // test save
        Entity saved = repository.create("student", student);
        String id = saved.getEntityId();
        assertTrue(!id.equals(""));

        // test findAll
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(20);
        Iterable<Entity> entities = repository.findAll("student", neutralQuery);
        assertNotNull(entities);
        Entity found = entities.iterator().next();
        assertEquals(found.getBody().get("birthDate"), student.get("birthDate"));
        assertEquals((found.getBody()).get("firstName"), "Jane");
        assertEquals((found.getBody()).get("lastName"), "Doe");

        // test find by id
        Entity foundOne = repository.findById("student", saved.getEntityId());
        assertNotNull(foundOne);
        assertEquals(foundOne.getBody().get("birthDate"), student.get("birthDate"));
        assertEquals((found.getBody()).get("firstName"), "Jane");

        // test update
        found.getBody().put("firstName", "Mandy");
        assertTrue(repository.update("student", found, false));
        entities = repository.findAll("student", neutralQuery);
        assertNotNull(entities);
        Entity updated = entities.iterator().next();
        assertEquals(updated.getBody().get("firstName"), "Mandy");

        // test delete by id
        Map<String, Object> student2Body = buildTestStudentEntity();
        Entity student2 = repository.create("student", student2Body);
        entities = repository.findAll("student", neutralQuery);
        assertNotNull(entities.iterator().next());
        repository.delete("student", student2.getEntityId());
        Entity zombieStudent = repository.findById("student", student2.getEntityId());
        assertNull(zombieStudent);

        assertFalse(repository.delete("student", student2.getEntityId()));

        // test deleteAll by entity type
        repository.deleteAll("student", null);
        entities = repository.findAll("student", neutralQuery);
        assertFalse(entities.iterator().hasNext());
    }

    @Test
    public void testNeedsId() {

        Entity e = new MongoEntity("student", buildTestStudentEntity());
        assertFalse(repository.update("student", e, false));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSort() {

        // clean up the existing student data
        repository.deleteAll("student", null);

        // create new student entity
        Map<String, Object> body1 = buildTestStudentEntity();
        Map<String, Object> body2 = buildTestStudentEntity();
        Map<String, Object> body3 = buildTestStudentEntity();
        Map<String, Object> body4 = buildTestStudentEntity();

        body1.put("firstName", "Austin");
        body2.put("firstName", "Jane");
        body3.put("firstName", "Mary");
        body4.put("firstName", "Suzy");

        body1.put("performanceLevels", new String[] { "1" });
        body2.put("performanceLevels", new String[] { "2" });
        body3.put("performanceLevels", new String[] { "3" });
        body4.put("performanceLevels", new String[] { "4" });

        // save entities
        repository.create("student", body1);
        repository.create("student", body2);
        repository.create("student", body3);
        repository.create("student", body4);

        // sort entities by firstName with ascending order
        NeutralQuery sortQuery1 = new NeutralQuery();
        sortQuery1.setSortBy("firstName");
        sortQuery1.setSortOrder(NeutralQuery.SortOrder.ascending);
        sortQuery1.setOffset(0);
        sortQuery1.setLimit(100);

        Iterable<Entity> entities = repository.findAll("student", sortQuery1);
        assertNotNull(entities);
        Iterator<Entity> it = entities.iterator();
        assertEquals("Austin", it.next().getBody().get("firstName"));
        assertEquals("Jane", it.next().getBody().get("firstName"));
        assertEquals("Mary", it.next().getBody().get("firstName"));
        assertEquals("Suzy", it.next().getBody().get("firstName"));

        // sort entities by firstName with descending order
        NeutralQuery sortQuery2 = new NeutralQuery();
        sortQuery2.setSortBy("firstName");
        sortQuery2.setSortOrder(NeutralQuery.SortOrder.descending);
        sortQuery2.setOffset(0);
        sortQuery2.setLimit(100);
        entities = repository.findAll("student", sortQuery2);
        assertNotNull(entities);
        it = entities.iterator();
        assertEquals("Suzy", it.next().getBody().get("firstName"));
        assertEquals("Mary", it.next().getBody().get("firstName"));
        assertEquals("Jane", it.next().getBody().get("firstName"));
        assertEquals("Austin", it.next().getBody().get("firstName"));

        // sort entities by performanceLevels which is an array with ascending
        // order
        NeutralQuery sortQuery3 = new NeutralQuery();
        sortQuery3.setSortBy("performanceLevels");
        sortQuery3.setSortOrder(NeutralQuery.SortOrder.ascending);
        sortQuery3.setOffset(0);
        sortQuery3.setLimit(100);
        entities = repository.findAll("student", sortQuery3);
        assertNotNull(entities);
        it = entities.iterator();
        assertEquals("1", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
        assertEquals("2", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
        assertEquals("3", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
        assertEquals("4", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));

        // sort entities by performanceLevels which is an array with descending
        // order
        NeutralQuery sortQuery4 = new NeutralQuery();
        sortQuery4.setSortBy("performanceLevels");
        sortQuery4.setSortOrder(NeutralQuery.SortOrder.descending);
        sortQuery4.setOffset(0);
        sortQuery4.setLimit(100);
        entities = repository.findAll("student", sortQuery4);
        assertNotNull(entities);
        it = entities.iterator();
        assertEquals("4", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
        assertEquals("3", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
        assertEquals("2", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
        assertEquals("1", ((List<String>) (it.next().getBody().get("performanceLevels"))).get(0));
    }

    @Test
    public void testCount() {
        repository.deleteAll("student", null);

        DBObject indexKeys = new BasicDBObject("body.cityOfBirth", 1);
        mongoTemplate.getCollection("student").ensureIndex(indexKeys);

        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        Map<String, Object> oddStudent = buildTestStudentEntity();
        oddStudent.put("cityOfBirth", "Nantucket");
        repository.create("student", oddStudent);
        assertEquals(5, repository.count("student", new NeutralQuery()));
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("cityOfBirth=Nantucket"));
        assertEquals(1, repository.count("student", neutralQuery));

        repository.deleteAll("student", null);
        mongoTemplate.getCollection("student").dropIndex(indexKeys);
    }

    private Map<String, Object> buildTestStudentEntity() {

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("firstName", "Jane");
        body.put("lastName", "Doe");
        body.put("studentUniqueStateId", UUID.randomUUID().toString());
        // Date birthDate = new Timestamp(23234000);
        body.put("birthDate", "2000-01-01");
        body.put("cityOfBirth", "Chicago");
        body.put("CountyOfBirth", "US");
        body.put("dateEnteredUs", "2011-01-01");
        body.put("displacementStatus", "some");
        body.put("economicDisadvantaged", true);
        body.put("generationCodeSuffix", "Z");
        body.put("hispanicLatinoEthnicity", true);
        body.put("limitedEnglishProficiency", "Yes");
        body.put("maidenName", "Smith");
        body.put("middleName", "Patricia");
        body.put("multipleBirthStatus", true);
        body.put("oldEthnicity", "AMERICAN_INDIAN_OR_ALASKAN_NATIVE");
        body.put("personalInformationVerification", "verified");
        body.put("personalTitlePrefix", "Miss");
        body.put("profileThumbnail", "doej23.png");
        body.put("schoolFoodServicesEligibility", "REDUCED_PRICE");
        body.put("sex", "Female");
        body.put("stateOfBirthAbbreviation", "IL");
        body.put("studentSchoolId", "DOE-JANE-222");
        return body;
    }

    @Test
    public void testTimestamps() throws Exception {

        // clean up the existing student data
        repository.deleteAll("student", null);

        // create new student entity
        Map<String, Object> student = buildTestStudentEntity();

        // test save
        Entity saved = repository.create("student", student);

        DateTime created = new DateTime(saved.getMetaData().get(EntityMetadataKey.CREATED.getKey()));
        DateTime updated = new DateTime(saved.getMetaData().get(EntityMetadataKey.UPDATED.getKey()));

        assertEquals(created, updated);

        saved.getBody().put("cityOfBirth", "Evanston");

        // Needs to be here to prevent cases where code execution is so fast,
        // there
        // is no difference between create/update times
        Thread.sleep(2);

        repository.update("student", saved, false);

        updated = new DateTime(saved.getMetaData().get(EntityMetadataKey.UPDATED.getKey()));

        assertTrue(updated.isAfter(created));

    }

    @Test
    public void testFindIdsByQuery() {
        repository.deleteAll("student", null);
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        repository.create("student", buildTestStudentEntity());
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(100);

        Iterable<String> ids = repository.findAllIds("student", neutralQuery);
        List<String> idList = new ArrayList<String>();
        for (String id : ids) {
            idList.add(id);
        }

        assertEquals(5, idList.size());
    }

    @Test
    public void findOneTest() {
        repository.deleteAll("student", null);
        DBObject indexKeys = new BasicDBObject("body.firstName", 1);
        mongoTemplate.getCollection("student").ensureIndex(indexKeys);

        Map<String, Object> student = buildTestStudentEntity();
        student.put("firstName", "Jadwiga");

        this.repository.create("student", student);
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("firstName=Jadwiga"));

        assertNotNull(this.repository.findOne("student", neutralQuery));

        repository.deleteAll("student", null);
        mongoTemplate.getCollection("student").dropIndex(indexKeys);
    }

    @Test
    public void findOneMultipleMatches() {
        repository.deleteAll("student", null);
        DBObject indexKeys = new BasicDBObject("body.firstName", 1);
        mongoTemplate.getCollection("student").ensureIndex(indexKeys);

        Map<String, Object> student = buildTestStudentEntity();
        student.put("firstName", "Jadwiga");
        this.repository.create("student", student);

        student = buildTestStudentEntity();
        student.put("firstName", "Jadwiga");
        this.repository.create("student", student);

        student = buildTestStudentEntity();
        student.put("firstName", "Jadwiga");
        this.repository.create("student", student);

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("firstName=Jadwiga"));
        assertNotNull(this.repository.findOne("student", neutralQuery));

        repository.deleteAll("student", null);
        mongoTemplate.getCollection("student").dropIndex(indexKeys);
    }

    @Test
    public void findOneTestNegative() {
        repository.deleteAll("student", null);
        DBObject indexKeys = new BasicDBObject("body.firstName", 1);
        mongoTemplate.getCollection("student").ensureIndex(indexKeys);

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("firstName=Jadwiga"));

        assertNull(this.repository.findOne("student", neutralQuery));

        repository.deleteAll("student", null);
        mongoTemplate.getCollection("student").dropIndex(indexKeys);
    }

    @Test
    public void testUpdateRetry() {
        TenantContext.setTenantId("SLIUnitTest");
        repository.deleteAll("student", null);

        DBObject indexKeys = new BasicDBObject("body.cityOfBirth", 1);
        mongoTemplate.getCollection("student").ensureIndex(indexKeys);

        repository.create("student", buildTestStudentEntity());

        Entity entity = repository.findOne("student", new NeutralQuery());
        Map<String, Object> studentBody = entity.getBody();
        studentBody.put("cityOfBirth", "ABC");

        Entity studentEntity = new MongoEntity("student", entity.getEntityId(), studentBody,
                entity.getMetaData());
        repository.updateWithRetries("student", studentEntity, 5);

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("cityOfBirth=ABC"));
        assertEquals(1, repository.count("student", neutralQuery));

        repository.deleteAll("student", null);
        mongoTemplate.getCollection("student").dropIndex(indexKeys);
    }

    @Test
    public void testCreateWithMetadata() {
        repository.deleteAll("student", null);
        Map<String, Object> studentBody = buildTestStudentEntity();
        Map<String, Object> studentMetaData = new HashMap<String, Object>();
        repository.create("student", studentBody, studentMetaData, "student");
        assertEquals(1, repository.count("student", new NeutralQuery()));
    }

    @Test
    public void testCreateRetryWithError() {
        Repository<Entity> mockRepo = Mockito.spy(repository);
        Map<String, Object> studentBody = buildTestStudentEntity();
        Map<String, Object> studentMetaData = new HashMap<String, Object>();
        int noOfRetries = 5;

        Mockito.doThrow(new MongoException("Test Exception"))
                .when(((MongoEntityRepository) mockRepo))
                .internalCreate("student", null, studentBody, studentMetaData, "student");
        Mockito.doCallRealMethod()
                .when(mockRepo)
                .createWithRetries("student", null, studentBody, studentMetaData, "student",
                        noOfRetries);

        try {
            mockRepo.createWithRetries("student", null, studentBody, studentMetaData, "student",
                    noOfRetries);
        } catch (MongoException ex) {
            assertEquals(ex.getMessage(), "Test Exception");
        }

        Mockito.verify((MongoEntityRepository) mockRepo, Mockito.times(noOfRetries))
                .internalCreate("student", null, studentBody, studentMetaData, "student");
    }

    @Test
    public void testUpdateRetryWithError() {
        Repository<Entity> mockRepo = Mockito.spy(repository);
        Map<String, Object> studentBody = buildTestStudentEntity();
        Map<String, Object> studentMetaData = new HashMap<String, Object>();
        Entity entity = new MongoEntity("student", null, studentBody, studentMetaData);
        int noOfRetries = 3;

        Mockito.doThrow(new InvalidDataAccessApiUsageException("Test Exception")).when(mockRepo)
                .update("student", entity, false);
        Mockito.doCallRealMethod().when(mockRepo).updateWithRetries("student", entity, noOfRetries);

        try {
            mockRepo.updateWithRetries("student", entity, noOfRetries);
        } catch (InvalidDataAccessApiUsageException ex) {
            assertEquals(ex.getMessage(), "Test Exception");
        }

        Mockito.verify(mockRepo, Mockito.times(noOfRetries)).update("student", entity, false);
    }
}
