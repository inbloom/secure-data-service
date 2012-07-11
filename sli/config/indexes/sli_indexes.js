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

//auth, realm, application
db["application"].ensureIndex({"body.authorized_ed_orgs":1});
db["application"].ensureIndex({"body.bootstrap":1});
db["application"].ensureIndex({"body.client_id":1,"body.client_secret":1});
db["application"].ensureIndex({"body.client_secret":1,"body.client_id":1});
db["applicationAuthorization"].ensureIndex({"body.authId":1,"body.authType":1});
db["realm"].ensureIndex({"body.idp.id":1});
db["realm"].ensureIndex({"body.uniqueIdentifier":1});
db["tenant"].ensureIndex({"body.landingZone.ingestionServer":1});
db["tenant"].ensureIndex({"body.landingZone.path":1});
db["tenant"].ensureIndex({"body.tenantId":1,"body.landingZone.educationOrganization":1});
db["userSession"].ensureIndex({"body.appSession.code.expiration":1,"body.appSession.clientId":1,"body.appSession.verified":1,"body.appSession.code.value":1}, {"name":"codeExpiration_clientId_verified_codeValue"});
db["userSession"].ensureIndex({"body.appSession.samlId":1});
db["userSession"].ensureIndex({"body.appSession.token":1});
db["userSession"].ensureIndex({"body.principal.externalId":1});
db["userSession"].ensureIndex({"body.expiration":1,"body.hardLogout":1,"body.appSession.token":1});

//direct references - 3 indexes for each one (minus parallel arrays and redundant indexes)
//ingestion lookup
//teacher context resolver
//staff context resolver
db["attendance"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.schoolId":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.schoolId":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentId":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"body.educationOrgId":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.educationOrgId":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.educationOrgId":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"body.programId":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.programId":1});
db["course"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1});
db["course"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.schoolId":1});
db["course"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.schoolId":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"body.courseId":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.courseId":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.courseId":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.sessionId":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.sessionId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.assignmentSchoolId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.assignmentSchoolId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.assignmentSchoolId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.disciplineIncidentId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.disciplineIncidentId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.responsibilitySchoolId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.responsibilitySchoolId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.responsibilitySchoolId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.staffId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.staffId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.schoolId":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.schoolId":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"body.staffId":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.staffId":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.staffId":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"body.parentEducationAgencyReference":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.parentEducationAgencyReference":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.parentEducationAgencyReference":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodId":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.gradingPeriodId":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.gradingPeriodId":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"body.studentSectionAssociationId":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentSectionAssociationId":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentSectionAssociationId":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.sectionId":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.sectionId":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"body.learningStandards":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.learningStandards":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"body.parentLearningObjective":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.parentLearningObjective":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.parentLearningObjective":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"body.grades":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.grades":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodId":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.gradingPeriodId":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.gradingPeriodId":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"body.studentCompetencyId":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentCompetencyId":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentId":1});
db["schoolSessionAssociation"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1});
db["schoolSessionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.schoolId":1});
db["schoolSessionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.schoolId":1});
db["schoolSessionAssociation"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1});
db["schoolSessionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.sessionId":1});
db["schoolSessionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.sessionId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.courseId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.courseId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.courseId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.programReference":1});
db["section"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.programReference":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.schoolId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.schoolId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.sessionId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.sessionId":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"body.assessmentId":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.assessmentId":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.assessmentId":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.sectionId":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.sectionId":1});
db["session"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodReference":1});
db["session"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.gradingPeriodReference":1});
db["session"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1});
db["session"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.schoolId":1});
db["session"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.schoolId":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.cohortId":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.cohortId":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.staffId":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.staffId":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"body.educationOrganizationReference":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.educationOrganizationReference":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.educationOrganizationReference":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"body.staffReference":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.staffReference":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.staffReference":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.programId":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.programId":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.staffId":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.staffId":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"body.sessionId":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.sessionId":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.sessionId":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentId":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"body.assessmentId":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.assessmentId":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.assessmentId":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentId":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.cohortId":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.cohortId":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.cohortId":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentId":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"body.learningObjectiveId":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.learningObjectiveId":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.learningObjectiveId":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"body.studentSectionAssociationId":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentSectionAssociationId":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentSectionAssociationId":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"body.disciplineIncidentId":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.disciplineIncidentId":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.disciplineIncidentId":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentId":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"body.parentId":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.parentId":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.parentId":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.educationOrganizationId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.educationOrganizationId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.educationOrganizationId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.programId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.programId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.programId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentId":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.schoolId":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.schoolId":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentId":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.sectionId":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.sectionId":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentId":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.gradebookEntryId":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.gradebookEntryId":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.gradebookEntryId":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.sectionId":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.sectionId":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.courseId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.courseId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.courseId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentAcademicRecordId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentAcademicRecordId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentAcademicRecordId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.studentId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.studentId":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.schoolId":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.schoolId":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.teacherId":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.teacherId":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.teacherId":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.sectionId":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.sectionId":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.teacherId":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"_id":1,"body.teacherId":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1,"body.teacherId":1});

// staff security resolver -- commented are in direct refs above
db["assessment"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["attendance"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["calendarDate"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["competencyLevelDescriptor"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["course"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["courseOffering"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["courseSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["educationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["educationOrganizationSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["grade"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["graduationPlan"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["learningObjective"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["learningStandard"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["parent"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["program"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["schoolSessionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["section"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["sectionSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["session"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["sessionCourseAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["student"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentCompetencyObjective"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});
//db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.edOrgs":1});

// security resolver: createdBy and orphaned
db["assessment"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["calendarDate"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["competencyLevelDescriptor"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["course"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["courseOffering"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["courseSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["educationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["graduationPlan"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["learningStandard"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["parent"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["program"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["reportCard"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["schoolSessionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["section"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["sectionSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["session"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["sessionCourseAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["student"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentCompetencyObjective"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.isOrphaned":1,"metaData.createdBy":1});

//sharding --> sharded on { metaData.tenantId, _id }
//most of these are redundant, but REQUIRED for sharding
db["assessment"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["attendance"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["calendarDate"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["cohort"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["competencyLevelDescriptor"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["course"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["courseOffering"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["courseSectionAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["disciplineAction"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["educationOrganization"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["educationOrganizationAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["grade"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["graduationPlan"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["learningObjective"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["learningStandard"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["parent"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["program"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["reportCard"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["section"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["sectionAssessmentAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["sectionSchoolAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["session"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["sessionCourseAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["staff"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["staffCohortAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["staffProgramAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["student"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["studentAcademicRecord"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["studentCompetency"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["studentCompetencyObjective"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId" : 1, "_id" : 1});

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
db["schoolSessionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
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
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1});

// profiled
db["assessment"].ensureIndex({"metaData.tenantId":1,"body.assessmentIdentificationCode":1});
db["attendance"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"body.studentId":1});
db["calendarDate"].ensureIndex({"metaData.tenantId":1,"body.date":1,"body.calendarEvent":1});
db["calendarDate"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"body.date":1,"body.calendarEvent":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"body.cohortIdentifier":1,"body.educationOrgId":1});
db["cohort"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"body.educationOrgId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.staffId":1,"body.stateOrganizationId":1});
db["disciplineAction"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"body.stateOrganizationId":1});
db["disciplineIncident"].ensureIndex({"metaData.tenantId":1,"body.incidentIdentifier":1,"body.schoolId":1});
db["educationOrganization"].ensureIndex({"metaData.tenantId":1,"body.stateOrganizationId":1});
db["grade"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"body.studentSectionAssociationId":1});
db["gradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.gradebookEntryType":1,"body.dateAssigned":1,"body.sectionId":1});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodIdentity.gradingPeriod":1,"body.gradingPeriodIdentity.schoolYear":1,"body.gradingPeriodIdentity.stateOrganizationId":1},{"name":"tenantId_gradingPeriod_schoolYear_stateOrgId"});
db["gradingPeriod"].ensureIndex({"metaData.tenantId":1,"body.gradingPeriodIdentity.schoolYear":1,"body.gradingPeriodIdentity.gradingPeriod":1,"body.gradingPeriodIdentity.educationalOrgIdentity":1},{"name":"tenantId_schoolYear_gradingPeriod_edOrgIdentity"});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"body.learningObjectiveId.identificationCode":1});
db["learningObjective"].ensureIndex({"metaData.tenantId":1,"body.objective":1,"body.academicSubject":1,"body.objectiveGradeLevel":1});
db["learningStandard"].ensureIndex({"metaData.tenantId":1,"body.learningStandardId.identificationCode":1});
db["parent"].ensureIndex({"metaData.tenantId":1,"body.parentUniqueStateId":1});
db["program"].ensureIndex({"metaData.tenantId":1,"body.programId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"body.uniqueSectionCode":1,"body.schoolId":1,"body.sessionId":1,"body.courseId":1});
db["section"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"body.schoolId":1});
db["session"].ensureIndex({"metaData.tenantId":1,"body.sessionName":1,"body.schoolYear":1,"body.term":1,"body.schoolId":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"_id":1,"type":1});
db["staff"].ensureIndex({"metaData.tenantId":1,"body.staffUniqueStateId":1});
db["student"].ensureIndex({"metaData.tenantId":1,"body.studentUniqueStateId":1});
db["studentAssessmentAssociation"].ensureIndex({"metaData.tenantId":1,"body.assessmentId":1,"body.studentId":1,"body.administrationDate":1});
db["studentCohortAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"body.cohortId":1,"body.beginDate":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"body.studentSectionAssociationId":1});
db["studentCompetency"].ensureIndex({"metaData.tenantId":1,"metaData.externalId":1,"metaData.studentUniqueStateId":1,"metaData.uniqueSectionCode":1},{"name":"tenantId_externalId_studentUniqueStateId_uniqueSectionCode"});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"body.disciplineIncidentId":1});
db["studentParentAssociation"].ensureIndex({"metaData.tenantId":1,"body.parentId":1,"body.studentId":1});
db["studentProgramAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"body.programId":1,"body.beginDate":1});
db["studentSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.studentId":1,"body.sectionId":1});
db["studentSectionGradebookEntry"].ensureIndex({"metaData.tenantId":1,"body.gradebookEntryId":1,"body.studentId":1,"body.sectionId":1});
db["studentTranscriptAssociation"].ensureIndex({"metaData.tenantId":1,"body.courseId":1,"body.studentAcademicRecordId":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.tenantId":1,"body.schoolId":1,"body.teacherId":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.tenantId":1,"body.sectionId":1,"body.teacherId":1});
