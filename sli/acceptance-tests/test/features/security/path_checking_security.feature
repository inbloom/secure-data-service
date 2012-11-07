@US4464
@wip
Feature: Secure URI paths based on user type

Scenario Outline: Staff making calls to disallowed URI paths and being rewritten
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And my contextual access is defined by table:
    | Context  | Ids                                  |
    | schools  | b1bd3db6-d020-4651-b1b8-a8dba688d9e1 |
     When I call <Called Path> using ID <ID>
     Then I should receive a return code of 200
     And the executed URI should be <Rewritten Path>

Examples:
	| Called Path                            | ID                                     | Rewritten Path                                                                              |
	| "/courses/@id/courseTranscripts"       | "0002f3f2-cf56-425a-ba24-56f805331743" | "/schools/@context/studentSchoolAssociations/students/courseTranscripts?courseId=@id"       |
	| "/gradingPeriods/@id/reportCards"      | "b40a7eb5-dd74-4666-a5b9-5c3f4425f130" | "/schools/@context/studentSchoolAssociations/students/reportCards?gradingPeriod=@id"        |
	| "/gradingPeriods/@id/grades"           | "b40a7eb5-dd74-4666-a5b9-5c3f4425f130" | "/schools/@context/studentSchoolAssociations/students/grades?gradingPeriodId=@id"           |
    | "/sessions/@id/studentAcademicRecords" | "0410354d-dbcb-0214-250a-404401060c93" | "/schools/@context/studentSchoolAssociations/students/studentAcademicRecords?sessionId=@id" |
    | "/sessions/@id/sections"               | "0410354d-dbcb-0214-250a-404401060c93" | "/schools/@context/sections?sessionId=@id"                                                  |
    | "/assessments/@id/studentAssessments"  | "29f044bd-1449-4fb7-8e9a-5e2cf9ad252a" | "/schools/@context/studentSchoolAssociaitons/students/studentAssessments?assessmentId=@id"  |
    | "/courseOfferings/@id/sections"        | "01709b45-d323-4101-9918-34788dd77306" | "/schools/@context/sections?courseOfferingId=@id"                                           |


Scenario Outline: Teacher making calls to disallowed URI paths and being rewritten
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    And my contextual access is defined by table:
    | Context  | Ids                                                                          |
    | teachers | e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b                                         |
    | sections | 15ab6363-5509-470c-8b59-4f289c224107_id,47b5adbf-6fd0-4f07-ba5e-39612da2e234 |
    When I call <Called Path> using ID <ID>
    Then I should receive a return code of 200
    And the executed URI should be <Rewritten Path>
    
Examples:
	| Called Path                           | ID                                     | Rewritten Path                                                                                |
	| "/courses/@id/courseTranscripts"      | "5841cf31-16a6-4b4d-abe1-3909d86b4fc3" | "/sections/@context/studentSectionAssociations/students/courseTranscripts?courseId=@id"       |
	| "/gradingPeriods/@id/reportCards"     | "8940897f-0e4b-4e88-9aa3-30cf040c0ebf" | "/sections/@context/studentSectionAssociations/students/reportCards?gradingPeriod=@id"        |
	| "/gradingPeriods/@id/grades"          | "8940897f-0e4b-4e88-9aa3-30cf040c0ebf" | "/sections/@context/studentSectionAssociations/students/grades?gradingPeriodId=@id"           |
    | "/sessions/@id/studentAcademicRecords"| "0410354d-dbcb-0214-250a-404401060c93" | "/sections/@context/studentSectionAssociations/students/studentAcademicRecords?sessionId=@id" |
    | "/sessions/@id/sections"              | "0410354d-dbcb-0214-250a-404401060c93" | "/teachers/@context/teacherSectionAssociations/sections?sessionId=@id"                        |
    | "/assessments/@id/studentAssessments" | "dd916592-7d7e-5d27-a87d-dfc7fcb12346" | "/sections/@context/studentSectionAssociaitons/students/studentAssessments?assessmentId=@id"  |
    | "/courseOfferings/@id/sections"       | "88ddb0c4-1787-4ed8-884e-96aa774e6d42" | "/teachers/@context/teacherSectionAssociations/sections?courseOfferingId=@id"                 |


Scenario Outline: Teacher making calls to disallowed URI paths speficic to teachers and being rewritten
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    And my contextual access is defined by table:
    | Context  | Ids                                                                          |
    | teachers | e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b                                         |
    | sections | 15ab6363-5509-470c-8b59-4f289c224107_id,47b5adbf-6fd0-4f07-ba5e-39612da2e234 |
    When I call <Called Path> using ID <ID>
    Then I should receive a return code of 200
    And the executed URI should be <Rewritten Path>
Examples:
	| Called Path                                                                       | ID | Rewritten Path |
    |"/schools/@id/teacherSchoolAssociations/teachers/teacherSectionAssociations"       | "92d6d5a0-852c-45f4-907a-912752831772" |"/teachers/@context/teacherSectionAssociations"|
    |"/schools/@id/studentSchoolAssociations"                                           | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons/students/studentSchoolAssociations?schoolId=@id"|
    |"/schools/@id/studentSchoolAssociations/students"                                  | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons/students/"|
    |"/schools/@id/studentSchoolAssociations/students/attendances"                      | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons/students/attendances"|
    |"/schools/@id/studentSchoolAssociations/students/courseTranscripts"                | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons/students/courseTranscripts"|
    |"/schools/@id/studentSchoolAssociations/students/reportCards"                      | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons/students/reportCards"|
    |"/schools/@id/studentSchoolAssociations/students/studentAcademicRecords"           | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons/students/studentAcademicRecords"|
    |"/schools/@id/studentSchoolAssociations/students/studentAssessments"               | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons/students/studentAssessments"|
    |"/schools/@id/studentSchoolAssociations/students/studentGradebookEntries"          | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons/students/studentGradebookEntries"|
    |"/schools/@id/studentSchoolAssociations/students/studentParentAssociations"        | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons/students/studentParentAssociations"|
    |"/schools/@id/studentSchoolAssociations/students/studentParentAssociations/parents"| "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons/students/studentParentAssociations/parents"|
    |"/schools/@id/sections"                                                            | "92d6d5a0-852c-45f4-907a-912752831772" |"/teachers/@context/teacherSectionAssociations/sections?schoolId=@id"|
    |"/schools/@id/sections/gradebookEntries"                                           | "92d6d5a0-852c-45f4-907a-912752831772" |"/teachers/@context/teacherSectionAssociations/sections/gradebookEntries"|
    |"/schools/@id/sections/studentSectionAssociations"                                 | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons"|
    |"/schools/@id/sections/studentSectionAssociations/grades"                          | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons/grades"|
    |"/schools/@id/sections/studentSectionAssociations/studentCompetencies"             | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociaitons/studentCompetencies"|
 
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
	Given I am logged in using "manthony" "manthony1234" to realm "IL"
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
	| Allowed Path           | Denied Path                                    | ID |
	| "/courses/@id"         | "/courses/@id/courseTranscripts/students"      | "e31f7583-417e-4c42-bd55-0bbe7518edf8" |
    | "/assessments/@id"     | "/assessments/@id/studentAssessments/students" | "dd916592-7d7e-5d27-a87d-dfc7fcb12346" |
	| "/programs/@id"        | "/programs/@id/cohorts"                        | "f24e5725-c1e4-48db-9f62-381ab434c0ec" |
	
