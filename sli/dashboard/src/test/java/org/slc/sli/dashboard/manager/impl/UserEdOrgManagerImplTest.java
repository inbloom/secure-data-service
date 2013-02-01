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

/**
 *
 */
package org.slc.sli.dashboard.manager.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.slc.sli.dashboard.client.APIClient;
import org.slc.sli.dashboard.client.SDKAPIClient;
import org.slc.sli.dashboard.entity.ConfigMap;
import org.slc.sli.dashboard.entity.EdOrgKey;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.JsonConverter;

/**
 * @author tosako
 *
 */
public class UserEdOrgManagerImplTest {
    UserEdOrgManagerImpl testInstitutionalHierarchyManagerImpl = null;
    APIClient apiClient = null;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        apiClient = new SDKAPIClient() {

            private String customConfigJson = "{}";

            @Override
            public ConfigMap getEdOrgCustomData(String token, String id) {
                return new GsonBuilder().create().fromJson(customConfigJson, ConfigMap.class);
            }

            public void putEdOrgCustomData(String token, String id, String customJson) {
                customConfigJson = customJson;
            }

            @Override
            public List<GenericEntity> getSchools(String token, List<String> schoolIds) {
                List<GenericEntity> schools = new ArrayList<GenericEntity>();
                schools.add(new GenericEntity()); // dummy GenericEntity
                return schools;
            }

            @Override
            public GenericEntity getParentEducationalOrganization(final String token, GenericEntity edOrg) {
                GenericEntity entity = null;
                if (token.equals("")) {
                    String json = "{\"id\":\"2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\",\"accountabilityRatings\":[],\"organizationCategories\":[\"Local Education Agency\"],\"address\":[{\"postalCode\":\"00000\",\"streetNumberName\":\"123 Street\",\"stateAbbreviation\":\"IL\",\"city\":\"Chicago\"}],\"educationOrgIdentificationCode\":[],\"programReference\":[],\"LEACategory\":\"Independent\",\"parentEducationAgencyReference\":\"2012ny-09327920-e000-11e1-9f3b-3c07546832b4\",\"links\":[{\"linkName\":\"self\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"custom\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4/custom\"},{\"linkName\":\"getParentEducationOrganization\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getFeederSchools\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/schools?parentEducationAgencyReference\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getStaffEducationOrgAssignmentAssociations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations\"},{\"linkName\":\"getStaff\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations/staff\"},{\"linkName\":\"getCourses\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/courses?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getFeederEducationOrganizations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations?parentEducationAgencyReference\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getSections\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sections?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getAttendances\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/attendances?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getCohorts\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/cohorts?educationOrgId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getSessions\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/sessions?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getDisciplineIncidents\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/disciplineIncidents?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"}],\"stateOrganizationId\":\"IL-DAYBREAK\",\"telephone\":[],\"nameOfInstitution\":\"Daybreak School District 4529\"}";
                    entity = JsonConverter.fromJson(json, GenericEntity.class);
                }
                return entity;
            }

            @Override
            public String getId(String token) {
                String id = "";
                if (token.equals(Constants.STATE_EDUCATION_AGENCY)) {
                    id = "1";
                } else if (token.equals(Constants.LOCAL_EDUCATION_AGENCY)) {
                    id = "2";
                } else if (token.equals("Teacher")) {
                    id = "3";
                }
                return id;
            }

            @Override
            public GenericEntity getStaffWithEducationOrganization(String token, String id, String organizationCategory) {
                GenericEntity entity = null;
                if (token.equals(Constants.STATE_EDUCATION_AGENCY) && id.equals("1")) {
                    String json = "{\"loginId\":\"a\",\"otherName\":[],\"sex\":\"Male\",\"staffUniqueStateId\":\"rrogers\",\"hispanicLatinoEthnicity\":false,\"links\":[{\"linkName\":\"self\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012wj-067afa2f-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"custom\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012wj-067afa2f-e000-11e1-9f3b-3c07546832b4/custom\"},{\"linkName\":\"getStaffProgramAssociations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012wj-067afa2f-e000-11e1-9f3b-3c07546832b4/staffProgramAssociations\"},{\"linkName\":\"getPrograms\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012wj-067afa2f-e000-11e1-9f3b-3c07546832b4/staffProgramAssociations/programs\"},{\"linkName\":\"getStaffEducationOrgAssignmentAssociations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012wj-067afa2f-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations\"},{\"linkName\":\"getEducationOrganizations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012wj-067afa2f-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations/educationOrganizations\"},{\"linkName\":\"getStaffCohortAssociations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012wj-067afa2f-e000-11e1-9f3b-3c07546832b4/staffCohortAssociations\"},{\"linkName\":\"getCohorts\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012wj-067afa2f-e000-11e1-9f3b-3c07546832b4/staffCohortAssociations/cohorts\"},{\"linkName\":\"getDisciplineActions\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/disciplineActions?staffId\u003d2012wj-067afa2f-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getDisciplineIncidents\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/disciplineIncidents?staffId\u003d2012wj-067afa2f-e000-11e1-9f3b-3c07546832b4\"}],\"yearsOfPriorTeachingExperience\":0,\"race\":[\"Asian\"],\"id\":\"2012wj-067afa2f-e000-11e1-9f3b-3c07546832b4\",\"yearsOfPriorProfessionalExperience\":20,\"address\":[{\"apartmentRoomSuiteNumber\":\"7B\",\"postalCode\":\"99999\",\"streetNumberName\":\"123 Sesame Street\",\"stateAbbreviation\":\"IL\",\"addressType\":\"Work\",\"city\":\"Chicago\"}],\"name\":{\"verification\":\"Life insurance policy\",\"lastSurname\":\"Rogers\",\"personalTitlePrefix\":\"Mr\",\"firstName\":\"Rick\"},\"electronicMail\":[{\"emailAddress\":\"junk@junk.com\",\"emailAddressType\":\"Organization\"}],\"highestLevelOfEducationCompleted\":\"No Degree\",\"credentials\":[{\"credentialField\":[{\"codeValue\":\"IT Admin\"}],\"level\":\"All Level (Grade Level PK-12)\",\"teachingCredentialType\":\"Standard\",\"credentialType\":\"Certification\",\"credentialIssuanceDate\":\"2000-01-01\"}],\"birthDate\":\"1980-02-01\",\"telephone\":[{\"telephoneNumber\":\"a\",\"primaryTelephoneNumberIndicator\":true,\"telephoneNumberType\":\"Fax\"}],\"staffIdentificationCode\":[{\"identificationSystem\":\"Selective Service\",\"ID\":\"a\",\"assigningOrganizationCode\":\"a\"}],\"edOrgSliId\":\"2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"}";
                    String edOrg = "{\"id\":\"2012ny-09327920-e000-11e1-9f3b-3c07546832b4\",\"accountabilityRatings\":[],\"organizationCategories\":[\"State Education Agency\"],\"staffEducationOrganizationAssociation\":[{\"id\":\"2012ug-0b171744-e000-11e1-9f3b-3c07546832b4\",\"staffClassification\":\"LEA System Administrator\",\"educationOrganizationReference\":\"2012ny-09327920-e000-11e1-9f3b-3c07546832b4\",\"positionTitle\":\"IT Support\",\"endDate\":\"2013-08-13\",\"staffReference\":\"2012wj-067afa2f-e000-11e1-9f3b-3c07546832b4\",\"beginDate\":\"1967-08-13\",\"entityType\":\"staffEducationOrganizationAssociation\"}],\"address\":[{\"postalCode\":\"00000\",\"streetNumberName\":\"123 Street\",\"stateAbbreviation\":\"IL\",\"city\":\"Chicago\"}],\"educationOrgIdentificationCode\":[],\"stateOrganizationId\":\"IL\",\"programReference\":[],\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012ny-09327920-e000-11e1-9f3b-3c07546832b4/custom\"},{\"rel\":\"getFeederSchools\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools?parentEducationAgencyReference\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getStaffEducationOrgAssignmentAssociations\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012ny-09327920-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations\"},{\"rel\":\"getStaff\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012ny-09327920-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations/staff\"},{\"rel\":\"getCourses\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/courses?schoolId\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getFeederEducationOrganizations\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations?parentEducationAgencyReference\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getSections\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/sections?schoolId\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getAttendances\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/attendances?schoolId\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getCohorts\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/cohorts?educationOrgId\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getSessions\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/sessions?schoolId\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getDisciplineIncidents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineIncidents?schoolId\u003d2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"}],\"telephone\":[],\"nameOfInstitution\":\"Illinois State Board of Education\"}";
                    entity = JsonConverter.fromJson(json, GenericEntity.class);
                    entity.put(Constants.ATTR_ED_ORG, JsonConverter.fromJson(edOrg, GenericEntity.class));
                } else if (token.equals(Constants.LOCAL_EDUCATION_AGENCY) && id.equals("2")) {
                    String json = "{\"loginId\":\"a\",\"otherName\":[],\"sex\":\"Male\",\"staffUniqueStateId\":\"jstevenson\",\"hispanicLatinoEthnicity\":false,\"links\":[{\"linkName\":\"self\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012es-067db968-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"custom\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012es-067db968-e000-11e1-9f3b-3c07546832b4/custom\"},{\"linkName\":\"getStaffProgramAssociations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012es-067db968-e000-11e1-9f3b-3c07546832b4/staffProgramAssociations\"},{\"linkName\":\"getPrograms\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012es-067db968-e000-11e1-9f3b-3c07546832b4/staffProgramAssociations/programs\"},{\"linkName\":\"getStaffEducationOrgAssignmentAssociations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012es-067db968-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations\"},{\"linkName\":\"getEducationOrganizations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012es-067db968-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations/educationOrganizations\"},{\"linkName\":\"getStaffCohortAssociations\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012es-067db968-e000-11e1-9f3b-3c07546832b4/staffCohortAssociations\"},{\"linkName\":\"getCohorts\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/staff/2012es-067db968-e000-11e1-9f3b-3c07546832b4/staffCohortAssociations/cohorts\"},{\"linkName\":\"getDisciplineActions\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/disciplineActions?staffId\u003d2012es-067db968-e000-11e1-9f3b-3c07546832b4\"},{\"linkName\":\"getDisciplineIncidents\",\"resource\":\"http://local.slidev.org:8080/api/rest/v1/disciplineIncidents?staffId\u003d2012es-067db968-e000-11e1-9f3b-3c07546832b4\"}],\"yearsOfPriorTeachingExperience\":0,\"race\":[\"Asian\"],\"id\":\"2012es-067db968-e000-11e1-9f3b-3c07546832b4\",\"yearsOfPriorProfessionalExperience\":20,\"address\":[{\"apartmentRoomSuiteNumber\":\"7B\",\"postalCode\":\"99999\",\"streetNumberName\":\"123 Wall Street\",\"stateAbbreviation\":\"IL\",\"addressType\":\"Work\",\"city\":\"Chicago\"}],\"name\":{\"verification\":\"Life insurance policy\",\"lastSurname\":\"Stevenson\",\"personalTitlePrefix\":\"Mr\",\"firstName\":\"James\"},\"electronicMail\":[{\"emailAddress\":\"junk@junk.com\",\"emailAddressType\":\"Organization\"}],\"highestLevelOfEducationCompleted\":\"No Degree\",\"credentials\":[{\"credentialField\":[{\"codeValue\":\"IT Admin\"}],\"level\":\"All Level (Grade Level PK-12)\",\"teachingCredentialType\":\"Standard\",\"credentialType\":\"Certification\",\"credentialIssuanceDate\":\"2000-01-01\"}],\"birthDate\":\"1980-02-01\",\"telephone\":[{\"telephoneNumber\":\"a\",\"primaryTelephoneNumberIndicator\":true,\"telephoneNumberType\":\"Fax\"}],\"staffIdentificationCode\":[{\"identificationSystem\":\"Selective Service\",\"ID\":\"a\",\"assigningOrganizationCode\":\"a\"}],\"edOrgSliId\":\"2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"}";
                    String edOrg = "{\"educationOrgIdentificationCode\":[],\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4/custom\"},{\"rel\":\"getParentEducationOrganization\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012ny-09327920-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getFeederSchools\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools?parentEducationAgencyReference\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getStaffEducationOrgAssignmentAssociations\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations\"},{\"rel\":\"getStaff\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4/staffEducationOrgAssignmentAssociations/staff\"},{\"rel\":\"getCourses\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/courses?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getFeederEducationOrganizations\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations?parentEducationAgencyReference\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getSections\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/sections?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getAttendances\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/attendances?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getCohorts\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/cohorts?educationOrgId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getSessions\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/sessions?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getDisciplineIncidents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineIncidents?schoolId\u003d2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"}],\"stateOrganizationId\":\"IL-DAYBREAK\",\"nameOfInstitution\":\"Daybreak School District 4529\",\"id\":\"2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\",\"accountabilityRatings\":[],\"organizationCategories\":[\"Local Education Agency\"],\"staffEducationOrganizationAssociation\":[{\"id\":\"2012hv-0b17da9b-e000-11e1-9f3b-3c07546832b4\",\"staffClassification\":\"LEA System Administrator\",\"educationOrganizationReference\":\"2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\",\"positionTitle\":\"IT Support\",\"endDate\":\"2013-08-13\",\"staffReference\":\"2012es-067db968-e000-11e1-9f3b-3c07546832b4\",\"beginDate\":\"1967-08-13\",\"entityType\":\"staffEducationOrganizationAssociation\"}],\"address\":[{\"postalCode\":\"00000\",\"streetNumberName\":\"123 Street\",\"stateAbbreviation\":\"IL\",\"city\":\"Chicago\"}],\"parentEducationAgencyReference\":\"2012ny-09327920-e000-11e1-9f3b-3c07546832b4\",\"LEACategory\":\"Independent\",\"programReference\":[],\"telephone\":[]}";
                    entity = JsonConverter.fromJson(json, GenericEntity.class);
                    entity.put(Constants.ATTR_ED_ORG, JsonConverter.fromJson(edOrg, GenericEntity.class));
                }
                return entity;
            }

            @Override
            public List<GenericEntity> getMySchools(String token) {
                List<GenericEntity> list = new ArrayList<GenericEntity>();
                if (token.equals("Teacher")) {
                    String json = "{\"teacherSchoolAssociation\":[{\"id\":\"2012pu-0b1c6e95-e000-11e1-9f3b-3c07546832b4\",\"academicSubjects\":[\"Computer and Information Sciences\",\"English Language and Literature\",\"Mathematics\"],\"schoolId\":\"2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\",\"programAssignment\":\"Regular Education\",\"entityType\":\"teacherSchoolAssociation\",\"teacherId\":\"2012ek-06548639-e000-11e1-9f3b-3c07546832b4\",\"instructionalGradeLevels\":[\"Sixth grade\",\"Seventh grade\",\"Eighth grade\",\"Ninth grade\"]}],\"educationOrgIdentificationCode\":[{\"identificationSystem\":\"School\",\"ID\":\"East Daybreak Junior High\"}],\"links\":[{\"rel\":\"self\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"custom\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4/custom\"},{\"rel\":\"getParentEducationOrganization\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/educationOrganizations/2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getCourses\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/courses?schoolId\u003d2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getStudentSchoolAssociations\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4/studentSchoolAssociations\"},{\"rel\":\"getStudents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4/studentSchoolAssociations/students\"},{\"rel\":\"getSections\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/sections?schoolId\u003d2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getAttendances\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/attendances?schoolId\u003d2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getCohorts\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/cohorts?educationOrgId\u003d2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getDisciplineActionsAsResponsibleSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineActions?responsibilitySchoolId\u003d2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getDisciplineActionsAsAssignedSchool\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineActions?assignmentSchoolId\u003d2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getSessions\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/sessions?schoolId\u003d2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\"},{\"rel\":\"getTeacherSchoolAssociations\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4/teacherSchoolAssociations\"},{\"rel\":\"getTeachers\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/schools/2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4/teacherSchoolAssociations/teachers\"},{\"rel\":\"getDisciplineIncidents\",\"href\":\"http://local.slidev.org:8080/api/rest/v1/disciplineIncidents?schoolId\u003d2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\"}],\"stateOrganizationId\":\"East Daybreak Junior High\",\"nameOfInstitution\":\"East Daybreak Junior High\",\"schoolCategories\":[\"Junior High School\"],\"id\":\"2012ye-0b0a45f5-e000-11e1-9f3b-3c07546832b4\",\"accountabilityRatings\":[],\"gradesOffered\":[\"Sixth grade\",\"Seventh grade\",\"Eighth grade\"],\"organizationCategories\":[\"School\"],\"address\":[{\"nameOfCounty\":\"Wake\",\"postalCode\":\"10112\",\"streetNumberName\":\"111 Ave B\",\"stateAbbreviation\":\"IL\",\"addressType\":\"Physical\",\"city\":\"Chicago\"}],\"parentEducationAgencyReference\":\"2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4\",\"programReference\":[],\"telephone\":[{\"institutionTelephoneNumberType\":\"Main\",\"telephoneNumber\":\"(917)-555-3312\"}]}";
                    GenericEntity entity = JsonConverter.fromJson(json, GenericEntity.class);
                    list.add(entity);
                }
                return list;
            }

        };

    }

    /**
     * Test method for
     * {@link org.slc.sli.dashboard.manager.impl.UserEdOrgManagerImpl#getUserEdOrg(java.lang.String)}
     * .
     */
    @Test
    @Ignore
    public void testGetUserDistrictId() {
        // EdOrgKey key = this.testInstitutionalHierarchyManagerImpl.getUserEdOrg("fakeToken");
        // Assert.assertEquals("my test district name", key.getDistrictId());
    }

    @Test
    public void testGetUserEdOrg() {
        this.testInstitutionalHierarchyManagerImpl = new UserEdOrgManagerImpl() {
            @Override
            public String getToken() {
                return "";
            }

            @Override
            protected boolean isEducator() {
                return false;
            }
        };
        this.testInstitutionalHierarchyManagerImpl.setApiClient(apiClient);
        EdOrgKey edOrgKey1 = this.testInstitutionalHierarchyManagerImpl.getUserEdOrg(Constants.STATE_EDUCATION_AGENCY);
        Assert.assertEquals("2012ny-09327920-e000-11e1-9f3b-3c07546832b4", edOrgKey1.getSliId());

        EdOrgKey edOrgKey2 = this.testInstitutionalHierarchyManagerImpl.getUserEdOrg(Constants.LOCAL_EDUCATION_AGENCY);
        Assert.assertEquals("2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4", edOrgKey2.getSliId());

        this.testInstitutionalHierarchyManagerImpl = new UserEdOrgManagerImpl() {
            @Override
            public String getToken() {
                return "";
            }

            @Override
            protected boolean isEducator() {
                return true;
            }
        };
        this.testInstitutionalHierarchyManagerImpl.setApiClient(apiClient);
        EdOrgKey edOrgKey3 = this.testInstitutionalHierarchyManagerImpl.getUserEdOrg("Teacher");
        Assert.assertEquals("2012zj-0b0711a4-e000-11e1-9f3b-3c07546832b4", edOrgKey3.getSliId());

    }

}
