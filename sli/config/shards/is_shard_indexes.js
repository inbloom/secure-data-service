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

db["assessment"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["assessment_transformed"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["attendance"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["attendance_transformed"].ensureIndex({"batchJobId" : 1, "_id" : 1, "studentId" : 1, "schoolId" : 1});
db["calendarDate"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["cohort"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["competencyLevelDescriptor"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["course"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["courseOffering"].ensureIndex({"batchJobId" : 1, "_id" : 1});
db["courseSectionAssociation"].ensureIndex({"batchJobId" : 1, "_id" : 1});
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
