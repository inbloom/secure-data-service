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

///////////////////////////////////////////////////////////////////
// Low cardinality collections
///////////////////////////////////////////////////////////////////

// approved 10/30 by Tony/Billy
db["adminDelegation"].ensureIndex({"body.localEdOrgId":1,"body.appApprovalEnabled":1});  // admin rights to users

// approved 10/30 by Tony/Billy
db["applicationAuthorization"].ensureIndex({"body.appIds":1});  //app auth
// approved 10/30 by Tony/Billy
db["applicationAuthorization"].ensureIndex({"body.authId":1,"body.authType":1});  //app auth

// approved 10/30 by Tony/Billy
db["customRole"].ensureIndex({"body.realmId":1});  //api-every call
// approved 10/30 by Tony/Billy
db["customRole"].ensureIndex({"metaData.tenantId":1,"body.realmId":1});  // create custom role for realm

// approved 10/30 by Tony/Billy
db["custom_entities"].ensureIndex({"metaData.entityId":1,"metaData.clientId":1});

///////////////////////////////////////////////////////////////////
// _id indexes for embedded entities
///////////////////////////////////////////////////////////////////

// These should not be needed - we should be able to search on the _id of the container entity
// Billy is investigating these

//studentProgramAssociation is embedded into program
db["program"].ensureIndex({"studentProgramAssociation._id":1});
//studentAssessment embedded into student
db["student"].ensureIndex({"studentAssessment._id":1});
db["student"].ensureIndex({"studentParentAssociation._id":1});
db["student"].ensureIndex({"studentParentAssociation.body.parentId":1});
//studentSectionAssociation embedded into section
db["section"].ensureIndex({"studentSectionAssociation._id":1});
//teacherSectionAssociation embedded into section
db["section"].ensureIndex({"teacherSectionAssociation._id":1});
//gradebookEntry embedded into section
db["section"].ensureIndex({"gradebookEntry._id":1});

///////////////////////////////////////////////////////////////////
// direct references - index on each direct reference
///////////////////////////////////////////////////////////////////

// These indexes will be reviewed as part of US4658

db["attendance"].ensureIndex({"body.schoolId":1});
db["attendance"].ensureIndex({"body.studentId":1});
db["cohort"].ensureIndex({"body.educationOrgId":1});
db["cohort"].ensureIndex({"studentCohortAssociation.body.studentId":1});
db["cohort"].ensureIndex({"studentCohortAssociation._id":1});
db["cohort"].ensureIndex({"body.programId":1});
db["course"].ensureIndex({"body.schoolId":1});
db["courseOffering"].ensureIndex({"body.courseId":1});
db["courseOffering"].ensureIndex({"body.schoolId":1});
db["courseOffering"].ensureIndex({"body.sessionId":1});
db["courseTranscript"].ensureIndex({"body.courseId":1});
db["courseTranscript"].ensureIndex({"body.studentAcademicRecordId":1});
db["courseTranscript"].ensureIndex({"body.studentId":1});
db["disciplineAction"].ensureIndex({"body.assignmentSchoolId":1});
db["disciplineAction"].ensureIndex({"body.disciplineIncidentId":1});
db["disciplineAction"].ensureIndex({"body.responsibilitySchoolId":1});
db["disciplineAction"].ensureIndex({"body.staffId":1});
db["disciplineAction"].ensureIndex({"body.studentId":1});
db["disciplineIncident"].ensureIndex({"body.schoolId":1});
db["disciplineIncident"].ensureIndex({"body.staffId":1});
db["educationOrganization"].ensureIndex({"body.parentEducationAgencyReference":1});
db["educationOrganization"].ensureIndex({"body.programReference":1});
db["grade"].ensureIndex({"body.gradingPeriodId":1});
db["grade"].ensureIndex({"body.studentSectionAssociationId":1});
//gradebookEntry embedded into section
db["section"].ensureIndex({"gradebookEntry.body.sectionId":1});
db["graduationPlan"].ensureIndex({"body.educationOrganizationId":1});
db["learningObjective"].ensureIndex({"body.learningStandards":1});
db["learningObjective"].ensureIndex({"body.parentLearningObjective":1});
db["reportCard"].ensureIndex({"body.grades":1});
db["reportCard"].ensureIndex({"body.gradingPeriodId":1});
db["reportCard"].ensureIndex({"body.studentCompetencyId":1});
db["reportCard"].ensureIndex({"body.studentId":1});
db["section"].ensureIndex({"body.assessmentReferences":1});
db["section"].ensureIndex({"body.courseOfferingId":1});
db["section"].ensureIndex({"body.programReference":1});
db["section"].ensureIndex({"body.schoolId":1});
db["section"].ensureIndex({"body.sessionId":1});
db["session"].ensureIndex({"body.gradingPeriodReference":1});
db["session"].ensureIndex({"body.schoolId":1});
db["staffCohortAssociation"].ensureIndex({"body.cohortId":1});
db["staffCohortAssociation"].ensureIndex({"body.staffId":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"body.educationOrganizationReference":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"body.staffReference":1});
db["staffProgramAssociation"].ensureIndex({"body.programId":1});
db["staffProgramAssociation"].ensureIndex({"body.staffId":1});
db["studentAcademicRecord"].ensureIndex({"body.reportCards":1});
db["studentAcademicRecord"].ensureIndex({"body.sessionId":1});
db["studentAcademicRecord"].ensureIndex({"body.studentId":1});
//studentAssessment is embedded into student
db["student"].ensureIndex({"studentAssessment.body.assessmentId":1});
db["student"].ensureIndex({"studentAssessment.body.studentId":1});  // do we need this?
db["studentCohortAssociation"].ensureIndex({"body.cohortId":1});
db["studentCohortAssociation"].ensureIndex({"body.studentId":1});
db["studentCompetency"].ensureIndex({"body.objectiveId.learningObjectiveId":1});
db["studentCompetency"].ensureIndex({"body.objectiveId.studentCompetencyObjectiveId":1});
db["studentCompetency"].ensureIndex({"body.studentSectionAssociationId":1});
db["studentCompetencyObjective"].ensureIndex({"body.educationOrganizationId":1});
db["student"].ensureIndex({"studentDisciplineIncidentAssociation.body.disciplineIncidentId":1});
db["student"].ensureIndex({"studentDisciplineIncidentAssociation.body.studentId":1});
db["student"].ensureIndex({"studentDisciplineIncidentAssociation._id":1});
db["studentGradebookEntry"].ensureIndex({"body.gradebookEntryId":1});
db["studentGradebookEntry"].ensureIndex({"body.sectionId":1});
db["studentGradebookEntry"].ensureIndex({"body.studentId":1});
db["studentGradebookEntry"].ensureIndex({"body.studentSectionAssociationId":1});
db["studentParentAssociation"].ensureIndex({"body.parentId":1});
db["studentParentAssociation"].ensureIndex({"body.studentId":1});
//studentProgramAssociation is embedded into program
db["program"].ensureIndex({"studentProgramAssociation.body.educationOrganizationId":1});
db["program"].ensureIndex({"studentProgramAssociation.body.programId":1});
db["program"].ensureIndex({"studentProgramAssociation.body.studentId":1});
db["studentSchoolAssociation"].ensureIndex({"body.graduationPlanId":1});
db["studentSchoolAssociation"].ensureIndex({"body.schoolId":1});
db["studentSchoolAssociation"].ensureIndex({"body.studentId":1});
//studentSectionAssociation is embedded into section
db["section"].ensureIndex({"studentSectionAssociation.body.sectionId":1});  // do we need this?
db["section"].ensureIndex({"studentSectionAssociation.body.studentId":1});
db["teacherSchoolAssociation"].ensureIndex({"body.schoolId":1});
db["teacherSchoolAssociation"].ensureIndex({"body.teacherId":1});
//teacherSectionAssociation is embedded into section
db["section"].ensureIndex({"teacherSectionAssociation.body.sectionId":1});
db["section"].ensureIndex({"teacherSectionAssociation.body.teacherId":1});

///////////////////////////////////////////////////////////////////
// Ingestion
///////////////////////////////////////////////////////////////////

// These indexes are all ued by ingestion and should be removed as part of
// reference resolution work
// TONY - Need to tie these to reference resolution stories

db["assessment"].ensureIndex({"body.assessmentIdentificationCode":1});  // US4388
db["assessment"].ensureIndex({"body.assessmentPeriodDescriptor.codeValue":1});  // US4388
db["assessment"].ensureIndex({"body.assessmentTitle":1,"body.academicSubject":1,"body.gradeLevelAssessed":1});  // US4388
db["calendarDate"].ensureIndex({"body.date":1,"body.calendarEvent":1});
db["course"].ensureIndex({"body.courseCode.ID":1,"body.courseCode.identificationSystem":1});
db["disciplineIncident"].ensureIndex({"body.incidentIdentifier":1});
db["educationOrganization"].ensureIndex({"body.stateOrganizationId":1});
db["educationOrganization"].ensureIndex({"body.educationOrgIdentificationCode":1}); // needs to be combined with next
db["educationOrganization"].ensureIndex({"body.educationOrgIdentificationCode.ID":1});
db["gradingPeriod"].ensureIndex({"body.gradingPeriodIdentity.schoolYear":1});
db["graduationPlan"].ensureIndex({"body.graduationPlanType":1});
db["learningObjective"].ensureIndex({"body.learningObjectiveId":1});  // needs to combined with next
db["learningObjective"].ensureIndex({"body.learningObjectiveId.identificationCode":1});
db["learningObjective"].ensureIndex({"body.objective":1});
db["learningStandard"].ensureIndex({"body.learningStandardId.identificationCode":1});
db["parent"].ensureIndex({"body.parentUniqueStateId":1});
db["program"].ensureIndex({"body.programId":1});
db["section"].ensureIndex({"body.uniqueSectionCode":1});
db["session"].ensureIndex({"body.sessionName":1});
db["student"].ensureIndex({"body.studentUniqueStateId":1});
db["studentCompetency"].ensureIndex({"metaData.externalId":1,"metaData.studentUniqueStateId":1,"metaData.uniqueSectionCode":1},{"name":"studentCompetencyId_externalId_stateId_sectionCode"});
db["studentCompetencyObjective"].ensureIndex({"body.studentCompetencyObjectiveId":1});

///////////////////////////////////////////////////////////////////
// Profiled
///////////////////////////////////////////////////////////////////

//These indexes will be reviewed as part of US4658

db["educationOrganization"].ensureIndex({"body.parentEducationAgencyReference":1,"type":1});
db["educationOrganization"].ensureIndex({"type":1,"body.nameOfInstitution":1});
db["gradingPeriod"].ensureIndex({"body.beginDate":1});
db["staff"].ensureIndex({"body.staffUniqueStateId":1});
db["staff"].ensureIndex({"type":1});


// TODO: Index to make querying acceptance tests pass - this is really bad!!
db["reportCard"].ensureIndex({"body.gpaCumulative":1});

//US4365
db["student"].ensureIndex({"cohort._id" : 1});

//DE2091, checked with Billy
db["competencyLevelDescriptor"].ensureIndex({"body.codeValue":1});

// US4602, checked with Billy.
db["educationOrganization"].ensureIndex({"body.organizationCategories":1});
