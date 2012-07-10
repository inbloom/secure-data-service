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
// put 'creationTime' index on all entities (including staged-only entities)
//
db["assessment"].ensureIndex({"creationTime":1});
db["assessment_transformed"].ensureIndex({"creationTime":1});
db["assessmentFamily"].ensureIndex({"creationTime":1});
db["assessmentItem"].ensureIndex({"creationTime":1});
db["assessmentPeriodDescriptor"].ensureIndex({"creationTime":1});
db["attendance"].ensureIndex({"creationTime":1});
db["attendance_transformed"].ensureIndex({"creationTime":1});
db["calendarDate"].ensureIndex({"creationTime":1});
db["cohort"].ensureIndex({"creationTime":1});
db["competencyLevelDescriptor"].ensureIndex({"creationTime":1});
db["course"].ensureIndex({"creationTime":1});
db["courseOffering"].ensureIndex({"creationTime":1});
db["disciplineAction"].ensureIndex({"creationTime":1});
db["disciplineIncident"].ensureIndex({"creationTime":1});
db["educationOrganization"].ensureIndex({"creationTime":1});
db["educationOrganizationAssociation"].ensureIndex({"creationTime":1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"creationTime":1});
db["grade"].ensureIndex({"creationTime":1});
db["gradebookEntry"].ensureIndex({"creationTime":1});
db["gradingPeriod"].ensureIndex({"creationTime":1});
db["graduationPlan"].ensureIndex({"creationTime":1});
db["learningObjective"].ensureIndex({"creationTime":1});
db["learningObjective_transformed"].ensureIndex({"creationTime":1});
db["learningStandard"].ensureIndex({"creationTime":1});
db["objectiveAssessment"].ensureIndex({"creationTime":1});
db["parent"].ensureIndex({"creationTime":1});
db["program"].ensureIndex({"creationTime":1});
db["reportCard"].ensureIndex({"creationTime":1});
db["school"].ensureIndex({"creationTime":1});
db["section"].ensureIndex({"creationTime":1});
db["session"].ensureIndex({"creationTime":1});
db["staff"].ensureIndex({"creationTime":1});
db["staffCohortAssociation"].ensureIndex({"creationTime":1});
db["staffEducationOrganizationAssociation"].ensureIndex({"creationTime":1});
db["staffProgramAssociation"].ensureIndex({"creationTime":1});
db["student"].ensureIndex({"creationTime":1});
db["studentAcademicRecord"].ensureIndex({"creationTime":1});
db["studentAssessmentAssociation"].ensureIndex({"creationTime":1});
db["studentAssessmentAssociation_transformed"].ensureIndex({"creationTime":1});
db["studentAssessmentItem"].ensureIndex({"creationTime":1});
db["studentCohortAssociation"].ensureIndex({"creationTime":1});
db["studentCompetency"].ensureIndex({"creationTime":1});
db["studentCompetencyObjective"].ensureIndex({"creationTime":1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"creationTime":1});
db["studentObjectiveAssessment"].ensureIndex({"creationTime":1});
db["studentParentAssociation"].ensureIndex({"creationTime":1});
db["studentProgramAssociation"].ensureIndex({"creationTime":1});
db["studentSectionAssociation"].ensureIndex({"creationTime":1});
db["studentSectionGradebookEntry"].ensureIndex({"creationTime":1});
db["studentSchoolAssociation"].ensureIndex({"creationTime":1});
db["studentTranscriptAssociation"].ensureIndex({"creationTime":1});
db["studentTranscriptAssociation_transformed"].ensureIndex({"creationTime":1});
db["teacher"].ensureIndex({"creationTime":1});
db["teacherSchoolAssociation"].ensureIndex({"creationTime":1});
db["teacherSectionAssociation"].ensureIndex({"creationTime":1});

//
// put indexes on shard keys (both in sharded and un-sharded configurations)
//
db["assessment"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["assessment_transformed"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["attendance"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["attendance_transformed"].ensureIndex({"batchJobId" : 1, "body.studentId" : 1, "body.schoolId" : 1}, {unique : true});
db["calendarDate"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["cohort"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["competencyLevelDescriptor"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["course"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["courseOffering"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["disciplineAction"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["disciplineIncident"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["educationOrganization"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["educationOrganizationAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["educationOrganizationSchoolAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["grade"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["gradebookEntry"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["gradingPeriod"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["graduationPlan"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["learningObjective"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["learningObjective_transformed"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["learningStandard"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["parent"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["program"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["reportCard"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["school"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["section"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["sectionAssessmentAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["sectionSchoolAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["session"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["sessionCourseAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["staff"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["staffCohortAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["staffEducationOrganizationAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["staffProgramAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["student"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentAcademicRecord"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentAssessmentAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentAssessmentAssociation_transformed"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentCohortAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentCompetency"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentCompetencyObjective"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentDisciplineIncidentAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentParentAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentProgramAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentSectionAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentSectionGradebookEntry"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentSchoolAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentTranscriptAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["studentTranscriptAssociation_transformed"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["teacher"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["teacherSchoolAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["teacherSectionAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});

// 
// other indexes required for responsiveness of staging database during transformations
//
db["assessmentFamily"].ensureIndex({"body.AssessmentFamilyIdentificationCode.ID":1});
db["assessmentItem"].ensureIndex({"body.identificationCode":1});
db["assessmentItem"].ensureIndex({"localId":1});
db["assessmentPeriodDescriptor"].ensureIndex({"body.codeValue":1});
db["assessmentPeriodDescriptor"].ensureIndex({"body.codeValue":1});
db["disciplineIncident"].ensureIndex({"body.id":1});
db["school"].ensureIndex({"body.stateOrganizationId":1});
db["session"].ensureIndex({"body.schoolId":1});
db["studentAssessmentItem"].ensureIndex({"localParentIds.studentResultRef":1});
db["studentObjectiveAssessment"].ensureIndex({"body.studentAssessmentRef":1});
db["studentSchoolAssociation"].ensureIndex({"body.studentId":1});
