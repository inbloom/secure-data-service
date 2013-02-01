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

db.runCommand( { enablesharding : "is" } );
db.runCommand( { shardcollection : "is.assessment", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.assessment_transformed", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.assessmentFamily", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.assessmentItem", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.assessmentPeriodDescriptor", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.attendance", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.attendance_transformed", key : {"batchJobId" : 1, "body.studentId" : 1, "body.schoolId" : 1}, unique : true } );
db.runCommand( { shardcollection : "is.calendarDate", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.cohort", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.competencyLevelDescriptor", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.course", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.courseOffering", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.disciplineAction", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.disciplineIncident", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.educationOrganization", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.educationOrganizationAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.educationOrganizationSchoolAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.grade", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.gradebookEntry", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.gradingPeriod", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.graduationPlan", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.learningObjective", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.learningObjective_transformed", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.learningStandard", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.localEducationAgency", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.objectiveAssessment", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.parent", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.program", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.reportCard", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.section", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.sectionAssessmentAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.sectionSchoolAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.session", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.staff", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.staffCohortAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.staffEducationOrganizationAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.staffProgramAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.stateEducationAgency", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.student", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentAcademicRecord", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentAssessment", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentAssessment_transformed", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentAssessmentItem", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentCohortAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentCompetency", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentCompetencyObjective", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentDisciplineIncidentAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentObjectiveAssessment", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentParentAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentProgramAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentSectionAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentGradebookEntry", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentSchoolAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.courseTranscript", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.courseTranscript_transformed", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.teacherSchoolAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.teacherSectionAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
