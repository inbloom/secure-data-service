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

db.runCommand( { enablesharding : "is" } );
db.runCommand( { shardcollection : "is.assessment", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.assessment_transformed", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.attendance", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.attendance_transformed", key : {"batchJobId" : 1, "_id" : 1, "studentId" : 1, "schoolId" : 1} } );
db.runCommand( { shardcollection : "is.calendarDate", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.cohort", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.competencyLevelDescriptor", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.course", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.courseOffering", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.courseSectionAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
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
db.runCommand( { shardcollection : "is.parent", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.program", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.reportCard", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.school", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.section", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.sectionAssessmentAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.sectionSchoolAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.session", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.sessionCourseAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.staff", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.staffCohortAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.staffEducationOrganizationAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.staffProgramAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.stateEducationAgency", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.student", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentAcademicRecord", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentAssessmentAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentAssessmentAssociation_transformed", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentCohortAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentCompetency", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentCompetencyObjective", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentDisciplineIncidentAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentParentAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentProgramAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentSectionAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentSectionGradebookEntry", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentSchoolAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentTranscriptAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.studentTranscriptAssociation_transformed", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.teacher", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.teacherSchoolAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : "is.teacherSectionAssociation", key : {"batchJobId" : 1, "_id" : 1} } );
