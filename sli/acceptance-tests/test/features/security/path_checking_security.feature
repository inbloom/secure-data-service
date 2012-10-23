@US4464
@wip
Feature: Secure URI paths based on user type

Scenario Outline: Staff making calls to not allowed URI paths and being denied

	Given something
	When something
	Then something
Examples:
	| Allowed Path          | Denied Path                                  | ID |
	|"/courses/@id"         |"/courses/@id/courseTranscripts"              |""|
	|"/courses/@id"         |"/courses/@id/courseTranscripts/students"     |""|
	|"/gradingPeriods/@id"  |"/gradingPeriods/@id/reportCards"             |""|
	|"/gradingPeriods/@id"  |"/gradingPeriods/@id/grades"                  |""|
    |"/sessions/@id"        |"	/sessions/@id/studentAcademicRecords"        |""|
    |"/sessions/@id"        |"	/sessions/@id/sections"                      |""|
    |"/assessments/@id"     |"/assessments/@id/studentAssessments"         |""|
    |"/assessments/@id"     |"/assessments/@id/studentAssessments/students"|""|
    |"/programs/@id"        |"	/programs/@id/cohorts"                       |""|
    |"/courseOfferings/@id" |"	/courseOfferings/@id/sections"               |""|
	
Scenario Outline: Teacher making calls to not allowed URI paths and being denied

	Given something
	When something
	Then something
Examples:
	| Allowed Path          | Denied Path                                  | ID |
	|"/courses/@id"         |"/courses/@id/courseTranscripts"              |""|
	|"/courses/@id"         |"/courses/@id/courseTranscripts/students"     |""|
	|"/gradingPeriods/@id"  |"/gradingPeriods/@id/reportCards"             |""|
	|"/gradingPeriods/@id"  |"/gradingPeriods/@id/grades"                  |""|
    |"/sessions/@id"        |"	/sessions/@id/studentAcademicRecords"        |""|
    |"/sessions/@id"        |"	/sessions/@id/sections"                      |""|
    |"/assessments/@id"     |"/assessments/@id/studentAssessments"         |""|
    |"/assessments/@id"     |"/assessments/@id/studentAssessments/students"|""|
    |"/programs/@id"        |"	/programs/@id/cohorts"                       |""|
    |"/courseOfferings/@id" |"	/courseOfferings/@id/sections"               |""|
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
    |"/schools/@id"|"/schools/@id/sections"                                                            |""|
    |"/schools/@id"|"/schools/@id/sections/gradebookEntries"                                           |""|
    |"/schools/@id"|"/schools/@id/sections/studentSectionAssociations"                                 |""|
    |"/schools/@id"|"/schools/@id/sections/studentSectionAssociations/grades"                          |""|
    |"/schools/@id"|"/schools/@id/sections/studentSectionAssociations/studentCompetencies"             |""|

Scenario Outline: Staff making calls to URIs through transitive relationships and being denied

	Given something
	When something
	Then something
Examples:
	| Base Path                   | Extended Path                                                              | Direct ID | Transitive ID |
	|"/cohorts/@id"               |"/cohorts/@id/staffCohortAssociations"                                      |""|""|
	|"/cohorts/@id"               |"/cohorts/@id/staffCohortAssociations/staff"                                |""|""|
	|"/cohorts/@id"               |"/cohorts/@id/studentCohortAssociations"                                    |""|""|
	|"/cohorts/@id"               |"/cohorts/@id/studentCohortAssociations/students"                           |""|""|
	|"/programs/@id"              |"/programs/@id/staffProgramAssociations"                                    |""|""|
	|"/programs/@id"              |"/programs/@id/staffProgramAssociations/staff"                              |""|""|
	|"/programs/@id"              |"/programs/@id/studentProgramAssociations"                                  |""|""|
	|"/programs/@id"              |"/programs/@id/studentProgramAssociations/students"                         |""|""|
	|"/staff/@id"                 |"/staff/{id}/disciplineActions"                                             |""|""|
	|"/staff/@id"                 |"/staff/{id}/disciplineIncidents"                                           |""|""|
	|"/staff/@id"                 |"/staff/{id}/disciplineIncidents/studentDisciplineIncidentAssociations"     |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffCohortAssociations"                                       |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffCohortAssociations/cohorts"                               |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffCohortAssociations/cohorts/studentCohortAssociations"     |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffEducationOrgAssignmentAssociations"                       |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffEducationOrgAssignmentAssociations/educationOrganizations"|""|""|
	|"/staff/@id"                 |"/staff/{id}/staffProgramAssociations"                                      |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffProgramAssociations/programs"                             |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffProgramAssociations/programs/studentProgramAssociations"  |""|""|
	|"/educationOrganizations/@id"|"/educationOrganizations/{id}/staffEducationOrgAssignmentAssociations"      |""|""|
	|"/educationOrganizations/@id"|"/educationOrganizations/{id}/staffEducationOrgAssignmentAssociations/staff"|""|""|
	|"/educationOrganizations/@id"|"/educationOrganizations/{id}/graduationPlans"                              |""|""|
	|"/educationOrganizations/@id"|"/educationOrganizations/{id}/cohorts"                                      |""|""|

Scenario Outline: Teacher making calls to URIs through transitive relationships and being denied

	Given something
	When something
	Then something
Examples:
	| Base Path                   | Extended Path                                                              | Direct ID | Transitive ID |
	|"/cohorts/@id"               |"/cohorts/@id/staffCohortAssociations"                                      |""|""|
	|"/cohorts/@id"               |"/cohorts/@id/staffCohortAssociations/staff"                                |""|""|
	|"/cohorts/@id"               |"/cohorts/@id/studentCohortAssociations"                                    |""|""|
	|"/cohorts/@id"               |"/cohorts/@id/studentCohortAssociations/students"                           |""|""|
	|"/programs/@id"              |"/programs/@id/staffProgramAssociations"                                    |""|""|
	|"/programs/@id"              |"/programs/@id/staffProgramAssociations/staff"                              |""|""|
	|"/programs/@id"              |"/programs/@id/studentProgramAssociations"                                  |""|""|
	|"/programs/@id"              |"/programs/@id/studentProgramAssociations/students"                         |""|""|
	|"/staff/@id"                 |"/staff/{id}/disciplineActions"                                             |""|""|
	|"/staff/@id"                 |"/staff/{id}/disciplineIncidents"                                           |""|""|
	|"/staff/@id"                 |"/staff/{id}/disciplineIncidents/studentDisciplineIncidentAssociations"     |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffCohortAssociations"                                       |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffCohortAssociations/cohorts"                               |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffCohortAssociations/cohorts/studentCohortAssociations"     |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffEducationOrgAssignmentAssociations"                       |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffEducationOrgAssignmentAssociations/educationOrganizations"|""|""|
	|"/staff/@id"                 |"/staff/{id}/staffProgramAssociations"                                      |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffProgramAssociations/programs"                             |""|""|
	|"/staff/@id"                 |"/staff/{id}/staffProgramAssociations/programs/studentProgramAssociations"  |""|""|
	|"/educationOrganizations/@id"|"/educationOrganizations/{id}/staffEducationOrgAssignmentAssociations"      |""|""|
	|"/educationOrganizations/@id"|"/educationOrganizations/{id}/staffEducationOrgAssignmentAssociations/staff"|""|""|
	|"/educationOrganizations/@id"|"/educationOrganizations/{id}/graduationPlans"                              |""|""|
	|"/educationOrganizations/@id"|"/educationOrganizations/{id}/cohorts"                                      |""|""|
	#Teacher specific limitations
	|"/sections/@id"|"/sections/@id/gradebookEntries"|""|""|
	|"/sections/@id"|"/sections/@id/studentGradebookEntries"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations/grades"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations/studentCompetencies"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations/students"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations/students/attendances"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations/students/courseTranscripts"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations/students/reportCards"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations/students/studentAcademicRecords"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations/students/studentAssessments"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations/students/studentGradebookEntries"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations/students/studentParentAssociations"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations/students/studentParentAssociations/parents"|""|""|
	|"/sections/@id"|"/sections/@id/studentSectionAssociations/students/studentSchoolAssociations"|""|""|
	|"/sections/@id"|"/sections/@id/teacherSectionAssociations"|""|""|
	|"/sections/@id"|"/sections/@id/teacherSectionAssociations/teachers"|""|""|
	|"/teachers/@id"|"/teachers/{id}/teacherSchoolAssociations"|""|""|
	|"/teachers/@id"|"/teachers/{id}/teacherSectionAssociations"|""|""|
	|"/teachers/@id"|"/teachers/{id}/teacherSectionAssociations/sections"|""|""|
	|"/teachers/@id"|"/teachers/{id}/teacherSchoolAssociations/schools"|""|""|
	|"/teachers/@id"|"/teachers/{id}/disciplineActions"|""|""|
	|"/teachers/@id"|"/teachers/{id}/disciplineIncidents"|""|""|
	|"/teachers/@id"|"/teachers/{id}/disciplineIncidents/studentDisciplineIncidentAssociations"|""|""|
