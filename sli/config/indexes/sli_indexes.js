//
// Copyright 2012 Shared Learning Collaborative, LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//


//
// Run the indexing script with data in collections to test for errors.
//
// Indexing Gotchas:
// - Long index names
// - Parallel indexes: creating an index key with more than one field that is an array
// - Redundant indexes: {a,b,c} makes {a,b}, {a} redundant
//
// Known problem fields for parallel indexes: (no index key with more
// than one of these)
// These can be found in ComplexTypex.xsd
// xpath=//xs:element[@type="reference"][@maxOccurs="unbounded"]
// - *:metaData.edOrgs
// - *:metaData.teacherContext
// - cohort:body.programId
// - disciplineAction:body.disciplineIncidentId
// - disciplineAction:body.staffId
// - disciplineAction:body.studentId
// - learningObjective:body.learningStandards
// - reportCard:body.grades
// - reportCard:body.studentCompetencyId
// - section:body.programReference
// - section:body.assessmentReference
// - session:body.gradingPeriodreference
// - staffCohortAssociation:body.cohortId
// - staffCohortAssociation:body.staffId
// - staffProgramAssociation:body.programId
// - staffProgramAssociation:body.staffId
//


//auth, realm, application, tenant
db["adminDelegation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["adminDelegation"].ensureIndex({"metaData.tenantId":1,"body.localEdOrgId":1,"body.appApprovalEnabled":1});

db["application"].ensureIndex({"body.allowed_for_all_edorgs":1});
db["application"].ensureIndex({"body.authorized_ed_orgs":1});
db["application"].ensureIndex({"body.authorized_for_all_edorgs":1});
db["application"].ensureIndex({"body.bootstrap":1});
db["application"].ensureIndex({"body.client_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["application"].ensureIndex({"body.client_secret":1,"body.client_id":1});
db["application"].ensureIndex({"body.name":1});

db["applicationAuthorization"].ensureIndex({"body.appIds":1});
db["applicationAuthorization"].ensureIndex({"body.authId":1});
db["applicationAuthorization"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["applicationAuthorization"].ensureIndex({"metaData.tenantId":1,"body.authId":1,"body.authType":1});

db["realm"].ensureIndex({"_id":1,"body.uniqueIdentifier":1});
db["realm"].ensureIndex({"body.idp.id":1});
db["realm"].ensureIndex({"body.uniqueIdentifier":1});

db["tenant"].ensureIndex({"body.landingZone.ingestionServer":1});
db["tenant"].ensureIndex({"body.landingZone.path":1});
db["tenant"].ensureIndex({"body.tenantId":1,"_id":1});
db["tenant"].ensureIndex({"body.tenantId":1,"body.landingZone.educationOrganization":1});
db["tenant"].ensureIndex({"body.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["tenant"].ensureIndex({"type":1});

db["userSession"].ensureIndex({"body.appSession.code.expiration":1,"body.appSession.clientId":1,"body.appSession.verified":1,"body.appSession.code.value":1}, {"name":"codeExpiration_clientId_verified_codeValue"});
db["userSession"].ensureIndex({"body.appSession.samlId":1});
db["userSession"].ensureIndex({"body.appSession.token":1});
db["userSession"].ensureIndex({"body.expiration":1,"body.hardLogout":1,"body.appSession.token":1});
db["userSession"].ensureIndex({"body.principal.externalId":1});
db["userSession"].ensureIndex({"body.principal.realm":1});


//custom entities
db["custom_entities"].ensureIndex({"metaData.entityId":1});
db["custom_entities"].ensureIndex({"metaData.tenantId":1,"metaData.entityId":1,"metaData.clientId":1});


//direct references - 3 indexes for each one (minus parallel arrays and redundant indexes)
//staff context resolver
//orphan
//teacher context resolver
db["assessment"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["assessment"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["assessment"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["assessment"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["assessment"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["assessment"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.edOrgs":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.teacherContext":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.edOrgs":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.teacherContext":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"body.educationOrgId":1,"metaData.edOrgs":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"body.educationOrgId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"body.educationOrgId":1,"metaData.teacherContext":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"body.programId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["course"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["course"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["course"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["course"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["course"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["course"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["course"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.edOrgs":1});
db["course"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["course"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.teacherContext":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"body.courseId":1,"metaData.edOrgs":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"body.courseId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"body.courseId":1,"metaData.teacherContext":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.edOrgs":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.teacherContext":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1,"metaData.edOrgs":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1,"metaData.teacherContext":1});
db["courseSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["courseSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["courseSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["courseSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["courseSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["courseSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.assignmentSchoolId":1,"metaData.edOrgs":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.assignmentSchoolId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.assignmentSchoolId":1,"metaData.teacherContext":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.disciplineIncidentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.responsibilitySchoolId":1,"metaData.edOrgs":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.responsibilitySchoolId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.responsibilitySchoolId":1,"metaData.teacherContext":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.staffId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.edOrgs":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.teacherContext":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"body.staffId":1,"metaData.edOrgs":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"body.staffId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"body.staffId":1,"metaData.teacherContext":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"body.parentEducationAgencyReference":1,"metaData.edOrgs":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"body.parentEducationAgencyReference":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"body.parentEducationAgencyReference":1,"metaData.teacherContext":1});
db["educationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["educationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["educationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["educationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["educationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["educationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodId":1,"metaData.edOrgs":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodId":1,"metaData.teacherContext":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"body.studentSectionAssociationId":1,"metaData.edOrgs":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"body.studentSectionAssociationId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"body.studentSectionAssociationId":1,"metaData.teacherContext":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"metaData.edOrgs":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"metaData.teacherContext":1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["graduationPlan"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["graduationPlan"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["graduationPlan"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["graduationPlan"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["graduationPlan"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["graduationPlan"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"body.learningStandards":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"body.parentLearningObjective":1,"metaData.edOrgs":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"body.parentLearningObjective":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"body.parentLearningObjective":1,"metaData.teacherContext":1});
db["learningStandard"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["learningStandard"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["learningStandard"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["learningStandard"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["learningStandard"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["learningStandard"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["parent"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["parent"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["parent"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["parent"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["parent"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["parent"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["program"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["program"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["program"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["program"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["program"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["program"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"body.grades":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodId":1,"metaData.edOrgs":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodId":1,"metaData.teacherContext":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"body.studentCompetencyId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.edOrgs":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.teacherContext":1});
db["section"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["section"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["section"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["section"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["section"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["section"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.assessmentReferences":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.courseOfferingId":1,"metaData.edOrgs":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.courseOfferingId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.courseOfferingId":1,"metaData.teacherContext":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.programReference":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.edOrgs":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.teacherContext":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1,"metaData.edOrgs":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1,"metaData.teacherContext":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["sectionSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["sectionSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["sectionSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["sectionSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["sectionSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["sectionSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["session"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["session"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["session"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["session"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["session"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["session"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["session"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodReference":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["session"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.edOrgs":1});
db["session"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["session"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.teacherContext":1});
db["sessionCourseAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["sessionCourseAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["sessionCourseAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["sessionCourseAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["sessionCourseAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["sessionCourseAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.cohortId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.staffId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"body.educationOrganizationReference":1,"metaData.edOrgs":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"body.educationOrganizationReference":1,"metaData.isOrphaned":1,"metaData.createdBy":1}, {"name":"tenantId_edOrgRef_isOrphaned_createdBy"});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"body.educationOrganizationReference":1,"metaData.teacherContext":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"body.staffReference":1,"metaData.edOrgs":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"body.staffReference":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"body.staffReference":1,"metaData.teacherContext":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.programId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.staffId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["student"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["student"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["student"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["student"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["student"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["student"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1,"metaData.edOrgs":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1,"metaData.teacherContext":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.edOrgs":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.teacherContext":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"body.assessmentId":1,"metaData.edOrgs":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"body.assessmentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"body.assessmentId":1,"metaData.teacherContext":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.edOrgs":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.teacherContext":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.cohortId":1,"metaData.edOrgs":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.cohortId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.cohortId":1,"metaData.teacherContext":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.edOrgs":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.teacherContext":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"body.learningObjectiveId":1,"metaData.edOrgs":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"body.learningObjectiveId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"body.learningObjectiveId":1,"metaData.teacherContext":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"body.studentSectionAssociationId":1,"metaData.edOrgs":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"body.studentSectionAssociationId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"body.studentSectionAssociationId":1,"metaData.teacherContext":1});
db["studentCompetencyObjective"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentCompetencyObjective"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCompetencyObjective"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["studentCompetencyObjective"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["studentCompetencyObjective"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCompetencyObjective"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"body.disciplineIncidentId":1,"metaData.edOrgs":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"body.disciplineIncidentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1}, {"name":"tenantId_disciplineIncidentId_isOrphaned_createdBy"});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"body.disciplineIncidentId":1,"metaData.teacherContext":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.edOrgs":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.teacherContext":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.gradebookEntryId":1,"metaData.edOrgs":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.gradebookEntryId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.gradebookEntryId":1,"metaData.teacherContext":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"metaData.edOrgs":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"metaData.teacherContext":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.edOrgs":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.teacherContext":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"body.parentId":1,"metaData.edOrgs":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"body.parentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"body.parentId":1,"metaData.teacherContext":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.edOrgs":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.teacherContext":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.educationOrganizationId":1,"metaData.edOrgs":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.educationOrganizationId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.educationOrganizationId":1,"metaData.teacherContext":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.programId":1,"metaData.edOrgs":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.programId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.programId":1,"metaData.teacherContext":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.edOrgs":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.teacherContext":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.edOrgs":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.teacherContext":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.edOrgs":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.teacherContext":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"metaData.edOrgs":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"metaData.teacherContext":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.edOrgs":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.teacherContext":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.courseId":1,"metaData.edOrgs":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.courseId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.courseId":1,"metaData.teacherContext":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentAcademicRecordId":1,"metaData.edOrgs":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentAcademicRecordId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentAcademicRecordId":1,"metaData.teacherContext":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.edOrgs":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"metaData.teacherContext":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.edOrgs":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"metaData.teacherContext":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.teacherId":1,"metaData.edOrgs":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.teacherId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.teacherId":1,"metaData.teacherContext":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.teacherContext":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.edOrgs":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"metaData.teacherContext":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"metaData.edOrgs":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"metaData.teacherContext":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.teacherId":1,"metaData.edOrgs":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.teacherId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.teacherId":1,"metaData.teacherContext":1});

//sharding --> sharded on { metaData.tenantId, _id }
//most of these are redundant, but REQUIRED for sharding
db["assessment"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["calendarDate"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["competencyLevelDescriptor"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["course"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["courseSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["educationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["graduationPlan"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["learningStandard"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["parent"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["program"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["section"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["sectionSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["session"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["sessionCourseAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["student"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["studentCompetencyObjective"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1});

//ingestion tenantId,externalId
db["assessment"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["calendarDate"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["competencyLevelDescriptor"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["course"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["courseSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["educationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["graduationPlan"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["learningStandard"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["parent"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["program"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["sectionSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["session"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["sessionCourseAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["student"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentCompetencyObjective"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});

//stamper - profiles
db["courseOffering"].ensureIndex({"body.sessionId":1});
db["educationOrganization"].ensureIndex({"type":1});
db["grade"].ensureIndex({"body.studentSectionAssociationId":1});
db["schoolSessionAssociation"].ensureIndex({"body.sessionId":1});
db["section"].ensureIndex({"body.sessionId":1});
db["sectionAssessmentAssociation"].ensureIndex({"body.sectionId":1});
db["staff"].ensureIndex({"type":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.cohortId":1,"body.studentRecordAccess":1,"body.endDate":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.programId":1,"body.studentRecordAccess":1,"body.endDate":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"body.endDate":1});
db["studentCompetency"].ensureIndex({"body.studentSectionAssociationId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"body.endDate":1});
db["studentSectionAssociation"].ensureIndex({"body.sectionId":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"body.endDate":1});
db["studentTranscriptAssociation"].ensureIndex({"body.studentId":1});
db["teacherSchoolAssociation"].ensureIndex({"body.endDate":1});
db["teacherSchoolAssociation"].ensureIndex({"body.schoolId":1,"body.endDate":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"body.endDate":1});

//profiled - ingestion
db["attendance"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"body.studentId":1});
db["calendarDate"].ensureIndex({"metaData.tenantId":1,"body.date":1,"body.calendarEvent":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"body.educationOrgId":1,"metaData.externalId":1});
db["course"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"body.courseTitle":1,"body.schoolId":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1,"body.localCourseCode":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"body.incidentIdentifier":1,"body.schoolId":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"body.stateOrganizationId":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"body.studentSectionAssociationId":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"body.gradebookEntryType":1,"body.sectionId":1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodIdentity.schoolYear":1,"body.gradingPeriodIdentity.gradingPeriod":1,"body.gradingPeriodIdentity.schoolId":1}, {"name":"tenantId_schoolYear_gradingPeriod_schoolId"});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"body.objective":1,"body.academicSubject":1,"body.objectiveGradeLevel":1,"body.learningObjectiveId.identificationCode":1}, {"name":"tenantId_objective_academicSubject_objGradeLvl_idCode"});
db["section"].ensureIndex({"metaData.tenantId":1,"body.uniqueSectionCode":1,"body.schoolId":1,"body.sessionId":1,"body.courseOfferingId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"body.schoolId":1});
db["session"].ensureIndex({"metaData.tenantId":1,"body.sessionName":1,"body.schoolYear":1,"body.term":1,"body.schoolId":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.staffId":1,"body.cohortId":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"body.studentId":1,"body.sessionId":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"body.assessmentId":1,"body.studentId":1,"body.administrationDate":1}, {"name":"tenantId_externalId_assessmentId_studentId_adminDate"});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"body.studentSectionAssociationId":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"body.disciplineIncidentId":1});
db["studentGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.gradebookEntryId":1,"body.studentId":1,"body.sectionId":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"body.parentId":1,"body.studentId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"body.programId":1,"body.beginDate":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"body.schoolId":1,"body.entryDate":1,"body.entryGradeLevel":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"body.sectionId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.courseId":1,"body.studentAcademicRecordId":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"body.teacherId":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"body.teacherId":1});

// profiled
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"_id":1,"type":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"_id":1,"type":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"body.staffUniqueStateId":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"body.endDate":1});
db["teacherSectionAssociation"].ensureIndex({"body.sectionId":1});
