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

db.runCommand( { enablesharding : database } );
db.runCommand( { shardcollection : database + ".assessment", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".attendance", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".calendarDate", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".cohort", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".competencyLevelDescriptor", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".course", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".courseOffering", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".courseSectionAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".disciplineAction", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".disciplineIncident", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".educationOrganization", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".educationOrganizationAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".educationOrganizationSchoolAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".grade", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".gradebookEntry", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".gradingPeriod", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".graduationPlan", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".learningObjective", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".learningStandard", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".parent", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".program", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".reportCard", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".section", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".sectionAssessmentAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".sectionSchoolAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".session", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".sessionCourseAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".staff", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".staffCohortAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".staffEducationOrganizationAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".staffProgramAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".student", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".studentAcademicRecord", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".studentAssessmentAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".studentCohortAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".studentCompetency", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".studentCompetencyObjective", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".studentDisciplineIncidentAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".studentParentAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".studentProgramAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".studentSectionAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".studentGradebookEntry", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".studentSchoolAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".studentTranscriptAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".teacherSchoolAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
db.runCommand( { shardcollection : database + ".teacherSectionAssociation", key : {"metaData.tenantId" : 1, "_id" : 1} } );
