//
// Copyright 2012-2013 inBloom, Inc. and its affiliates.
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



//custom entities
db["custom_entities"].ensureIndex({"metaData.entityId":1,"metaData.clientId":1});


//_id indexes for embedded entities
//studentAssessment embedded into student
db["student"].ensureIndex({"studentAssessment._id":1});
//studentSectionAssociation embedded into section
db["section"].ensureIndex({"studentSectionAssociation._id":1});


//direct references - index on each direct reference
db["attendance"].ensureIndex({"body.schoolId":1});
db["attendance"].ensureIndex({"body.studentId":1});
db["cohort"].ensureIndex({"body.educationOrgId":1});
db["cohort"].ensureIndex({"body.programId":1});
db["course"].ensureIndex({"body.schoolId":1});
db["courseOffering"].ensureIndex({"body.courseId":1});
db["courseOffering"].ensureIndex({"body.schoolId":1});
db["courseOffering"].ensureIndex({"body.sessionId":1});
db["disciplineAction"].ensureIndex({"body.assignmentSchoolId":1});
db["disciplineAction"].ensureIndex({"body.disciplineIncidentId":1});
db["disciplineAction"].ensureIndex({"body.responsibilitySchoolId":1});
db["disciplineAction"].ensureIndex({"body.staffId":1});
db["disciplineAction"].ensureIndex({"body.studentId":1});
db["disciplineIncident"].ensureIndex({"body.schoolId":1});
db["disciplineIncident"].ensureIndex({"body.staffId":1});
db["educationOrganization"].ensureIndex({"body.parentEducationAgencyReference":1});
db["grade"].ensureIndex({"body.gradingPeriodId":1});
db["grade"].ensureIndex({"body.studentSectionAssociationId":1});
db["gradebookEntry"].ensureIndex({"body.sectionId":1});
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
db["student"].ensureIndex({"studentAssessment.assessmentId":1});
db["student"].ensureIndex({"studentAssessment.studentId":1});  // do we need this?
db["studentCohortAssociation"].ensureIndex({"body.cohortId":1});
db["studentCohortAssociation"].ensureIndex({"body.studentId":1});
db["studentCompetency"].ensureIndex({"body.objectiveId.learningObjectiveId":1});
db["studentCompetency"].ensureIndex({"body.objectiveId.studentCompetencyObjectiveId":1});
db["studentCompetency"].ensureIndex({"body.studentSectionAssociationId":1});
db["studentCompetencyObjective"].ensureIndex({"body.educationOrganizationId":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"body.disciplineIncidentId":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"body.studentId":1});
db["studentGradebookEntry"].ensureIndex({"body.gradebookEntryId":1});
db["studentGradebookEntry"].ensureIndex({"body.sectionId":1});
db["studentGradebookEntry"].ensureIndex({"body.studentId":1});
db["studentGradebookEntry"].ensureIndex({"body.studentSectionAssociationId":1});
db["studentParentAssociation"].ensureIndex({"body.parentId":1});
db["studentParentAssociation"].ensureIndex({"body.studentId":1});
db["studentProgramAssociation"].ensureIndex({"body.educationOrganizationId":1});
db["studentProgramAssociation"].ensureIndex({"body.programId":1});
db["studentProgramAssociation"].ensureIndex({"body.studentId":1});
db["studentSchoolAssociation"].ensureIndex({"body.graduationPlanId":1});
db["studentSchoolAssociation"].ensureIndex({"body.schoolId":1});
db["studentSchoolAssociation"].ensureIndex({"body.studentId":1});
//studentSectionAssociation is embedded into section
db["section"].ensureIndex({"studentSectionAssociation.sectionId":1});  // do we need this?
db["section"].ensureIndex({"studentSectionAssociation.studentId":1});
db["courseTranscript"].ensureIndex({"body.courseId":1});
db["courseTranscript"].ensureIndex({"body.studentAcademicRecordId":1});
db["courseTranscript"].ensureIndex({"body.studentId":1});
db["teacherSchoolAssociation"].ensureIndex({"body.schoolId":1});
db["teacherSchoolAssociation"].ensureIndex({"body.teacherId":1});
db["teacherSectionAssociation"].ensureIndex({"body.sectionId":1});
db["teacherSectionAssociation"].ensureIndex({"body.teacherId":1});


//staff context resolver access - stamped edOrgs
//TODO this section can be removed when staff stamper goes away
db["assessment"].ensureIndex({"metaData.edOrgs":1});
db["attendance"].ensureIndex({"metaData.edOrgs":1});
db["calendarDate"].ensureIndex({"metaData.edOrgs":1});
db["cohort"].ensureIndex({"metaData.edOrgs":1});
db["competencyLevelDescriptor"].ensureIndex({"metaData.edOrgs":1});
db["course"].ensureIndex({"metaData.edOrgs":1});
db["courseOffering"].ensureIndex({"metaData.edOrgs":1});
db["courseSectionAssociation"].ensureIndex({"metaData.edOrgs":1});
db["disciplineAction"].ensureIndex({"metaData.edOrgs":1});
db["disciplineIncident"].ensureIndex({"metaData.edOrgs":1});
db["educationOrganization"].ensureIndex({"metaData.edOrgs":1});
db["educationOrganizationAssociation"].ensureIndex({"metaData.edOrgs":1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"metaData.edOrgs":1});
db["grade"].ensureIndex({"metaData.edOrgs":1});
db["gradebookEntry"].ensureIndex({"metaData.edOrgs":1});
db["gradingPeriod"].ensureIndex({"metaData.edOrgs":1});
db["graduationPlan"].ensureIndex({"metaData.edOrgs":1});
db["learningObjective"].ensureIndex({"metaData.edOrgs":1});
db["learningStandard"].ensureIndex({"metaData.edOrgs":1});
db["parent"].ensureIndex({"metaData.edOrgs":1});
db["program"].ensureIndex({"metaData.edOrgs":1});
db["reportCard"].ensureIndex({"metaData.edOrgs":1});
db["section"].ensureIndex({"metaData.edOrgs":1});
db["session"].ensureIndex({"metaData.edOrgs":1});
db["staff"].ensureIndex({"metaData.edOrgs":1});
db["staffCohortAssociation"].ensureIndex({"metaData.edOrgs":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"metaData.edOrgs":1});
db["staffProgramAssociation"].ensureIndex({"metaData.edOrgs":1});
db["student"].ensureIndex({"metaData.edOrgs":1});
db["studentAcademicRecord"].ensureIndex({"metaData.edOrgs":1});
db["studentCohortAssociation"].ensureIndex({"metaData.edOrgs":1});
db["studentCompetency"].ensureIndex({"metaData.edOrgs":1});
db["studentCompetencyObjective"].ensureIndex({"metaData.edOrgs":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"metaData.edOrgs":1});
db["studentGradebookEntry"].ensureIndex({"metaData.edOrgs":1});
db["studentParentAssociation"].ensureIndex({"metaData.edOrgs":1});
db["studentProgramAssociation"].ensureIndex({"metaData.edOrgs":1});
db["studentSchoolAssociation"].ensureIndex({"metaData.edOrgs":1});
db["courseTranscript"].ensureIndex({"metaData.edOrgs":1});
db["teacherSchoolAssociation"].ensureIndex({"metaData.edOrgs":1});
db["teacherSectionAssociation"].ensureIndex({"metaData.edOrgs":1});


//profiled - ingestion
db["educationOrganization"].ensureIndex({"body.stateOrganizationId":1});
db["section"].ensureIndex({"studentSectionAssociation._id":1});
db["student"].ensureIndex({"body.studentUniqueStateId":1});


//oprhan detection - this should be removed when done in API
db["educationOrganization"].ensureIndex({"metaData.isOrphaned":1});


//profiled
db["educationOrganization"].ensureIndex({"body.parentEducationAgencyReference":1,"type":1});
db["educationOrganization"].ensureIndex({"metaData.isOrphaned":1,"metaData.createdBy":1});
db["educationOrganization"].ensureIndex({"type":1,"body.nameOfInstitution":1});
db["gradingPeriod"].ensureIndex({"body.beginDate":1,"metaData.edOrgs":1});
db["staff"].ensureIndex({"body.staffUniqueStateId":1});
db["staff"].ensureIndex({"type":1});
