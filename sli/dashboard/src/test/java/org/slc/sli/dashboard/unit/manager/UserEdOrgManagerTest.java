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

package org.slc.sli.dashboard.unit.manager;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.dashboard.client.SDKAPIClient;
import org.slc.sli.dashboard.entity.EdOrgKey;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.impl.UserEdOrgManagerImpl;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.JsonConverter;

/**
 * test for UserEdOrgManager
 *
 * @author agrebneva
 *
 */
public class UserEdOrgManagerTest {
    UserEdOrgManagerImpl userEdOrgManager;

    @Before
    public void setup() {
        userEdOrgManager = new UserEdOrgManagerImpl() {
            @Override
            protected boolean isEducator() {
                return true;
            }
        };
        userEdOrgManager.setApiClient(new SDKAPIClient() {
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
            public String getId(String token) {
                if (token.equals(Constants.STATE_EDUCATION_AGENCY)) {
                    return "1";
                } else if (token.equals(Constants.LOCAL_EDUCATION_AGENCY)) {
                    return "2";
                }
                return "3";
            }
        });
    }

    @Test
    public void testGetStaffInfo() {
        GenericEntity entity = userEdOrgManager.getStaffInfo(Constants.LOCAL_EDUCATION_AGENCY);
        GenericEntity edOrg = (GenericEntity) entity.get(Constants.ATTR_ED_ORG);
        List<String> organizationCategories = (List<String>) edOrg.get(Constants.ATTR_ORG_CATEGORIES);
        Assert.assertNotNull(organizationCategories);
        Assert.assertEquals(1, organizationCategories.size());
        Assert.assertEquals(Constants.LOCAL_EDUCATION_AGENCY, organizationCategories.get(0));
    }

    public void testUserEdOrg() {
        EdOrgKey edOrgKey = userEdOrgManager.getUserEdOrg("1");
        Assert.assertNotNull(edOrgKey);
    }
}
