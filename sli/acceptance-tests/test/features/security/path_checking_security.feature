@US4464
@wip
Feature: Secure URI paths based on user type

Scenario Outline: Staff making calls to disallowed URI paths and being rewritten
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And my contextual access is defined by table:
    |Context  | Ids                                |
    |schools  |b1bd3db6-d020-4651-b1b8-a8dba688d9e1|
     When I call <Called Path> using ID <ID>
     Then I should receive a return code of 200
     And the executed URI should be <Rewritten Path>
Examples:
	| Called Path                          | ID | Rewritten Path |
	|"/courses/@id/courseTranscripts"      | "" |"/schools/@context/studentSchoolAssociations/students/courseTranscripts?courseId=@id"|
	|"/gradingPeriods/@id/reportCards"     | "" |"/schools/@context/studentSchoolAssociations/students/reportCards?gradingPeriod=@id"|
	|"/gradingPeriods/@id/grades"          | "" |"/schools/@context/studentSchoolAssociations/students/grades?gradingPeriodId=@id"|
    |"/sessions/@id/studentAcademicRecords"| "" |"/schools/@context/studentSchoolAssociations/students/studentAcademicRecords?sessionId=@id"|
    |"/sessions/@id/sections"              | "" |"/schools/@context/sections?sessionId=@id"|
    |"/assessments/@id/studentAssessments" | "" |"/schools/@context/studentSchoolAssociaitons/students/studentAssessments?assessmentId=@id"|
    |"/courseOfferings/@id/sections"       | "" |"/schools/@context/sections?courseOfferingId=@id"|

Scenario Outline: Teacher making calls to disallowed URI paths and being rewritten
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    And my contextual access is defined by table:
    |Context  | Ids                                |
    |teachers |e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b|
    |sections |15ab6363-5509-470c-8b59-4f289c224107,47b5adbf-6fd0-4f07-ba5e-39612da2e234|
    When I call <Called Path> using ID <ID>
    Then I should receive a return code of 200
    And the executed URI should be <Rewritten Path>
Examples:
	| Called Path                          | ID | Rewritten Path |
	|"/courses/@id/courseTranscripts"      | "" |"/sections/@context/studentSectionAssociations/students/courseTranscripts?courseId=@id"|
	|"/gradingPeriods/@id/reportCards"     | "" |"/sections/@context/studentSectionAssociations/students/reportCards?gradingPeriod=@id"|
	|"/gradingPeriods/@id/grades"          | "" |"/sections/@context/studentSectionAssociations/students/grades?gradingPeriodId=@id"|
    |"/sessions/@id/studentAcademicRecords"| "" |"/sections/@context/studentSectionAssociations/students/studentAcademicRecords?sessionId=@id"|
    |"/sessions/@id/sections"              | "" |"/teachers/@context/teacherSectionAssociations/sections?sessionId=@id"|
    |"/assessments/@id/studentAssessments" | "" |"/sections/@context/studentSectionAssociaitons/students/studentAssessments?assessmentId=@id"|
    |"/courseOfferings/@id/sections"       | "" |"/teachers/@context/teacherSectionAssociations/sections?courseOfferingId=@id"|

Scenario Outline: Teacher making calls to disallowed URI paths speficic to teachers and being rewritten
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    And my contextual access is defined by table:
    |Context  | Ids                                |
    |teachers |e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b|
    |sections |15ab6363-5509-470c-8b59-4f289c224107,47b5adbf-6fd0-4f07-ba5e-39612da2e234|
    When I call <Called Path> using ID <ID>
    Then I should receive a return code of 200
    And the executed URI should be <Rewritten Path>
Examples:
	| Called Path                                                                       | ID | Rewritten Path |
    |"/schools/@id/teacherSchoolAssociations/teachers/teacherSectionAssociations"       | "" |"/teachers/@context/teacherSectionAssociations"|
    |"/schools/@id/studentSchoolAssociations"                                           | "" |"/sections/@context/studentSectionAssociaitons/students/studentSchoolAssociations?schoolId=@id"|
    |"/schools/@id/studentSchoolAssociations/students"                                  | "" |"/sections/@context/studentSectionAssociaitons/students/"|
    |"/schools/@id/studentSchoolAssociations/students/attendances"                      | "" |"/sections/@context/studentSectionAssociaitons/students/attendances"|
    |"/schools/@id/studentSchoolAssociations/students/courseTranscripts"                | "" |"/sections/@context/studentSectionAssociaitons/students/courseTranscripts"|
    |"/schools/@id/studentSchoolAssociations/students/reportCards"                      | "" |"/sections/@context/studentSectionAssociaitons/students/reportCards"|
    |"/schools/@id/studentSchoolAssociations/students/studentAcademicRecords"           | "" |"/sections/@context/studentSectionAssociaitons/students/studentAcademicRecords"|
    |"/schools/@id/studentSchoolAssociations/students/studentAssessments"               | "" |"/sections/@context/studentSectionAssociaitons/students/studentAssessments"|
    |"/schools/@id/studentSchoolAssociations/students/studentGradebookEntries"          | "" |"/sections/@context/studentSectionAssociaitons/students/studentGradebookEntries"|
    |"/schools/@id/studentSchoolAssociations/students/studentParentAssociations"        | "" |"/sections/@context/studentSectionAssociaitons/students/studentParentAssociations"|
    |"/schools/@id/studentSchoolAssociations/students/studentParentAssociations/parents"| "" |"/sections/@context/studentSectionAssociaitons/students/studentParentAssociations/parents"|
    |"/schools/@id/sections"                                                            | "" |"/teachers/@context/teacherSectionAssociations/sections?schoolId=@id"|
    |"/schools/@id/sections/gradebookEntries"                                           | "" |"/teachers/@context/teacherSectionAssociations/sections/gradebookEntries"|
    |"/schools/@id/sections/studentSectionAssociations"                                 | "" |"/sections/@context/studentSectionAssociaitons"|
    |"/schools/@id/sections/studentSectionAssociations/grades"                          | "" |"/sections/@context/studentSectionAssociaitons/grades"|
    |"/schools/@id/sections/studentSectionAssociations/studentCompetencies"             | "" |"/sections/@context/studentSectionAssociaitons/studentCompetencies"|
 
@wip
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

@wip
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

Scenario Outline: Denied Paths
	Given I am logged in using "cgray" "cgray1234" to realm "IL"
	When I call <Allowed Path> using ID <ID>
	Then I should receive a return code of 200
	When I call <Denied Path> using ID <ID>
	Then I should receive a return code of 404
	Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
	When I call <Allowed Path> using ID <ID>
	Then I should receive a return code of 200
	When I call <Denied Path> using ID <ID>
	Then I should receive a return code of 404

Examples:
	| Allowed Path          | Denied Path                                  | ID |
	|"/courses/@id"         |"/courses/@id/courseTranscripts/students"     |""|
    |"/assessments/@id"     |"/assessments/@id/studentAssessments/students"|""|
	|"/programs/@id"        |"/programs/@id/cohorts"                       |""|