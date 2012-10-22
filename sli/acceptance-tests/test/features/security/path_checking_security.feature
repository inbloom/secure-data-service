@US4464
@wip
Feature: Secure URI paths based on user type

Scenario Outline: Staff making calls to not allowed URI paths and being denied

	Given something
	When something
	Then something
Examples:
	| Allowed Path            | Denied Path                                  | ID |
	|"/courses/@id"           |"/courses/@id/courseTranscripts"              |""|
	|"/courses/@id"           |"/courses/@id/courseTranscripts/students"     |""|
	|"/gradingPeriods/@id"    |"/gradingPeriods/@id/reportCards"             |""|
	|"/gradingPeriods/@id"    |"/gradingPeriods/@id/grades"                  |""|
    |"/sessions/@id"          |"	/sessions/@id/studentAcademicRecords"        |""|
    |"/assessments/@id"       |"/assessments/@id/studentAssessments"         |""|
    |"/assessments/@id"       |"/assessments/@id/studentAssessments/students"|""|
    |"/learningObjectives/@id"|"	/learningObjectives/@id/studentCompetencies" |""|
	
Scenario Outline: Teacher making calls to not allowed URI paths and being denied

	Given something
	When something
	Then something
Examples:
	| Allowed Path            | Denied Path                                  | ID |
	|"/courses/@id"           |"/courses/@id/courseTranscripts"              |""|
	|"/courses/@id"           |"/courses/@id/courseTranscripts/students"     |""|
	|"/gradingPeriods/@id"    |"/gradingPeriods/@id/reportCards"             |""|
	|"/gradingPeriods/@id"    |"/gradingPeriods/@id/grades"                  |""|
    |"/sessions/@id"          |"	/sessions/@id/studentAcademicRecords"        |""|
    |"/assessments/@id"       |"/assessments/@id/studentAssessments"         |""|
    |"/assessments/@id"       |"/assessments/@id/studentAssessments/students"|""|
    |"/learningObjectives/@id"|"	/learningObjectives/@id/studentCompetencies" |""|
    #Teacher Specific denied paths
    |"/schools/@id"|"/schools/@id/teacherSchoolAssociations/teachers/teacherSectionAssociations"       |""|
    |"/schools/@id"|"/schools/@id/studentSchoolAssociations"                                           |""|
    |"/schools/@id"|"/schools/@id/studentSchoolAssociations/students"                                  |""|
    |"/schools/@id"|"/schools/@id/studentSchoolAssociations/students/attendances"                      |""|
    |"/schools/@id"|"/schools/@id/studentSchoolAssociations/students/courseTranscripts"                |""|
    |"/schools/@id"|"/schools/@id/studentSchoolAssociations/students/reportCards"                      |""|
    |"/schools/@id"|"/schools/@id/studentSchoolAssociations/students/studentAcademicRecords"           |""|
    |"/schools/@id"|"/schools/@id/studentSchoolAssociations/students/studentAssessments"               |""|
    |"/schools/@id"|"/schools/@id/studentSchoolAssociations/students/studentGradebookEntries"          |""|
    |"/schools/@id"|"/schools/@id/studentSchoolAssociations/students/studentParentAssociations"        |""|
    |"/schools/@id"|"/schools/@id/studentSchoolAssociations/students/studentParentAssociations/parents"|""|
    |"/schools/@id"|"/schools/@id/sections/gradebookEntries"                                           |""|
    |"/schools/@id"|"/schools/@id/sections/studentSectionAssociations"                                 |""|
    |"/schools/@id"|"/schools/@id/sections/studentSectionAssociations/grades"                          |""|
    |"/schools/@id"|"/schools/@id/sections/studentSectionAssociations/studentCompetencies"             |""|

Scenario: Staff making calls to URIs through transitive relationships and being denied

	Given something
	When something
	Then something

Scenario: Teacher making calls to URIs through transitive relationships and being denied

	Given something
	When something
	Then something
 
 