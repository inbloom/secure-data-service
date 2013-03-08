@US4464
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
	| "/courses/@id/courseTranscripts"       | "0002f3f2-cf56-425a-ba24-56f805331743" | "/schools/@context/studentSchoolAssociations/students/studentAcademicRecords/courseTranscripts?courseId=@id"       |
	| "/gradingPeriods/@id/reportCards"      | "b40a7eb5-dd74-4666-a5b9-5c3f4425f130" | "/schools/@context/studentSchoolAssociations/students/reportCards?gradingPeriodId=@id"        |
	| "/gradingPeriods/@id/grades"           | "b40a7eb5-dd74-4666-a5b9-5c3f4425f130" | "/schools/@context/sections/studentSectionAssociations/grades?gradingPeriodId=@id"           |
    | "/sessions/@id/studentAcademicRecords" | "0410354d-dbcb-0214-250a-404401060c93" | "/schools/@context/studentSchoolAssociations/students/studentAcademicRecords?sessionId=@id" |
    | "/sessions/@id/sections"               | "0410354d-dbcb-0214-250a-404401060c93" | "/schools/@context/sections?sessionId=@id"                                                  |
    | "/assessments/@id/studentAssessments"  | "c757f9f2dc788924ce0715334c7e86735c5e1327_id" | "/schools/@context/studentSchoolAssociations/students/studentAssessments?assessmentId=@id"  |
    | "/courseOfferings/@id/sections"        | "01709b45-d323-4101-9918-34788dd77306" | "/schools/@context/sections?courseOfferingId=@id"                                           |


Scenario Outline: Teacher making calls to disallowed URI paths and being rewritten
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    And my contextual access is defined by table:
    | Context  | Ids                                                                          |
    | teachers | e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b                                         |
    | sections | 15ab6363-5509-470c-8b59-4f289c224107_id,47b5adbf-6fd0-4f07-ba5e-39612da2e234_id |
    When I call <Called Path> using ID <ID>
    Then I should receive a return code of 200
    And the executed URI should be <Rewritten Path>
    
Examples:
	| Called Path                           | ID                                     | Rewritten Path                                                                                |
	| "/courses/@id/courseTranscripts"      | "5841cf31-16a6-4b4d-abe1-3909d86b4fc3" | "/sections/@context/studentSectionAssociations/students/studentAcademicRecords/courseTranscripts?courseId=@id"       |
	| "/gradingPeriods/@id/reportCards"     | "8940897f-0e4b-4e88-9aa3-30cf040c0ebf" | "/sections/@context/studentSectionAssociations/students/reportCards?gradingPeriodId=@id"        |
	| "/gradingPeriods/@id/grades"          | "8940897f-0e4b-4e88-9aa3-30cf040c0ebf" | "/sections/@context/studentSectionAssociations/grades?gradingPeriodId=@id"           |
    | "/sessions/@id/studentAcademicRecords"| "0410354d-dbcb-0214-250a-404401060c93" | "/sections/@context/studentSectionAssociations/students/studentAcademicRecords?sessionId=@id" |
    | "/sessions/@id/sections"              | "0410354d-dbcb-0214-250a-404401060c93" | "/sections/@context/?sessionId=@id"                        |
    | "/assessments/@id/studentAssessments" | "b94b5194d45cd707465627c0cd6c4f68f3558600_id" | "/sections/@context/studentSectionAssociations/students/studentAssessments?assessmentId=@id"  |
    | "/courseOfferings/@id/sections"       | "88ddb0c4-1787-4ed8-884e-96aa774e6d42" | "/sections/@context/?courseOfferingId=@id"                 |


Scenario Outline: Teacher making calls to disallowed URI paths speficic to teachers and being rewritten
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    And my contextual access is defined by table:
    | Context  | Ids                                                                          |
    | teachers | e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b                                         |
    | sections | 15ab6363-5509-470c-8b59-4f289c224107_id,47b5adbf-6fd0-4f07-ba5e-39612da2e234_id |
    When I call <Called Path> using ID <ID>
    Then I should receive a return code of 200
    And the executed URI should be <Rewritten Path>
Examples:
	| Called Path                                                                       | ID | Rewritten Path |
    |"/schools/@id/teacherSchoolAssociations/teachers/teacherSectionAssociations"       | "92d6d5a0-852c-45f4-907a-912752831772" |"/teachers/@context/teacherSectionAssociations"|
    |"/schools/@id/studentSchoolAssociations"                                           | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations/students/studentSchoolAssociations"|
    |"/schools/@id/studentSchoolAssociations/students"                                  | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations/students"|
    |"/schools/@id/studentSchoolAssociations/students/attendances"                      | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations/students/attendances"|
    |"/schools/@id/studentSchoolAssociations/students/courseTranscripts"                | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations/students/studentAcademicRecords/courseTranscripts"|
    |"/schools/@id/studentSchoolAssociations/students/reportCards"                      | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations/students/reportCards"|
    |"/schools/@id/studentSchoolAssociations/students/studentAcademicRecords"           | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations/students/studentAcademicRecords"|
    |"/schools/@id/studentSchoolAssociations/students/studentAssessments"               | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations/students/studentAssessments"|
    |"/schools/@id/studentSchoolAssociations/students/studentGradebookEntries"          | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations/students/studentGradebookEntries"|
    |"/schools/@id/studentSchoolAssociations/students/studentParentAssociations"        | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations/students/studentParentAssociations"|
    |"/schools/@id/studentSchoolAssociations/students/studentParentAssociations/parents"| "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations/students/studentParentAssociations/parents"|
    |"/schools/@id/sections"                                                            | "92d6d5a0-852c-45f4-907a-912752831772" |"/teachers/@context/teacherSectionAssociations/sections"|
    |"/schools/@id/sections/gradebookEntries"                                           | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/gradebookEntries"|
    |"/schools/@id/sections/studentSectionAssociations"                                 | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations"|
    |"/schools/@id/sections/studentSectionAssociations/grades"                          | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations/grades"|
    |"/schools/@id/sections/studentSectionAssociations/studentCompetencies"             | "92d6d5a0-852c-45f4-907a-912752831772" |"/sections/@context/studentSectionAssociations/studentCompetencies"|
 
 Scenario Outline: Staff making calls to URIs through transitive relationships and being denied

    Given I am logged in using "akopel" "akopel1234" to realm "IL"
    When I call <Base Path> using ID <Direct ID>
    Then I should receive a return code of 200
    When I call <Extended Path> using ID <Direct ID>
    Then I should receive a return code of 200
    When I call <Base Path> using ID <Transitive ID>
    Then I should receive a return code of 200
    When I call <Extended Path> using ID <Transitive ID>
    Then I should receive a return code of 403
Examples:
	| Base Path                   | Extended Path                                                             | Direct ID                            | Transitive ID |
	|"/staff/@id"                 |"/staff/@id/disciplineActions"                                             |"cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4"|"4a39f944-c238-4787-965a-50f22f3a2d9c"|
	|"/staff/@id"                 |"/staff/@id/disciplineIncidents"                                           |"cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4"|"4a39f944-c238-4787-965a-50f22f3a2d9c"|
	|"/staff/@id"                 |"/staff/@id/disciplineIncidents/studentDisciplineIncidentAssociations"     |"cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4"|"4a39f944-c238-4787-965a-50f22f3a2d9c"|
	|"/staff/@id"                 |"/staff/@id/staffCohortAssociations"                                       |"cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4"|"4a39f944-c238-4787-965a-50f22f3a2d9c"|
	|"/staff/@id"                 |"/staff/@id/staffCohortAssociations/cohorts"                               |"cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4"|"4a39f944-c238-4787-965a-50f22f3a2d9c"|
	|"/staff/@id"                 |"/staff/@id/staffCohortAssociations/cohorts/studentCohortAssociations"     |"cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4"|"4a39f944-c238-4787-965a-50f22f3a2d9c"|
	|"/staff/@id"                 |"/staff/@id/staffEducationOrgAssignmentAssociations"                       |"cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4"|"4a39f944-c238-4787-965a-50f22f3a2d9c"|
	|"/staff/@id"                 |"/staff/@id/staffEducationOrgAssignmentAssociations/educationOrganizations"|"cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4"|"4a39f944-c238-4787-965a-50f22f3a2d9c"|
	|"/staff/@id"                 |"/staff/@id/staffProgramAssociations"                                      |"cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4"|"4a39f944-c238-4787-965a-50f22f3a2d9c"|
	|"/staff/@id"                 |"/staff/@id/staffProgramAssociations/programs"                             |"cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4"|"4a39f944-c238-4787-965a-50f22f3a2d9c"|
	|"/staff/@id"                 |"/staff/@id/staffProgramAssociations/programs/studentProgramAssociations"  |"cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4"|"4a39f944-c238-4787-965a-50f22f3a2d9c"|
	|"/educationOrganizations/@id"|"/educationOrganizations/@id/staffEducationOrgAssignmentAssociations"      |"a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"|"6756e2b9-aba1-4336-80b8-4a5dde3c63fe"|
	|"/educationOrganizations/@id"|"/educationOrganizations/@id/staffEducationOrgAssignmentAssociations/staff"|"a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"|"6756e2b9-aba1-4336-80b8-4a5dde3c63fe"|
	|"/educationOrganizations/@id"|"/educationOrganizations/@id/cohorts"                                      |"a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"|"6756e2b9-aba1-4336-80b8-4a5dde3c63fe"|

@wip
Scenario Outline: Teacher making calls to URIs through transitive relationships and being denied

    Given I am logged in using "rbraverman" "rbraverman1234" to realm "IL"
    When I call <Base Path> using ID <Direct ID>
    Then I should receive a return code of 200
    When I call <Extended Path> using ID <Direct ID>
    Then I should receive a return code of 200
    When I call <Base Path> using ID <Transitive ID>
    Then I should receive a return code of 200
    When I call <Extended Path> using ID <Transitive ID>
    Then I should receive a return code of 403
Examples:
	| Base Path                   | Extended Path                                                              | Direct ID | Transitive ID |
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

Scenario Outline: Denied Paths for Teacher
	Given I am logged in using "manthony" "manthony1234" to realm "IL"
	When I call <Allowed Path> using ID <ID>
	Then I should receive a return code of 200
	When I call <Denied Path> using ID <ID>
	Then I should receive a return code of 404
Examples:
    | Allowed Path           | Denied Path                                    | ID |
    | "/courses/@id"         | "/courses/@id/courseTranscripts/students"      | "e31f7583-417e-4c42-bd55-0bbe7518edf8" |
    | "/assessments/@id"     | "/assessments/@id/studentAssessments/students" | "b94b5194d45cd707465627c0cd6c4f68f3558600_id" |
    | "/programs/@id"        | "/programs/@id/cohorts"                        | "f24e5725-c1e4-48db-9f62-381ab434c0ec_id" |
	
Scenario Outline: Denied Paths for Staff
	Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
	When I call <Allowed Path> using ID <ID>
	Then I should receive a return code of 200
	When I call <Denied Path> using ID <ID>
	Then I should receive a return code of 404
Examples:
    | Allowed Path           | Denied Path                                    | ID |
    | "/courses/@id"         | "/courses/@id/courseTranscripts/students"      | "e31f7583-417e-4c42-bd55-0bbe7518edf8" |
    | "/assessments/@id"     | "/assessments/@id/studentAssessments/students" | "b94b5194d45cd707465627c0cd6c4f68f3558600_id" |
    | "/programs/@id"        | "/programs/@id/cohorts"                        | "9b8c3aab-8fd5-11e1-86ec-0021701f543f_id" |
	
Scenario Outline: Deny multiple IDs in URI if those IDs are rewritten to query params
#NOTE: This test will need to be reworked if the API is ever made to support multiple ids in query params
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
     When I call <Path> using ID <IDs>
     Then I should receive a return code of 413

Examples:
  | Path                                   | IDs                                    |
  | "/courses/@id/courseTranscripts"       | "0002f3f2-cf56-425a-ba24-56f805331743,2aa3aa59-cd8f-4ac7-811c-48d9618bc114" |
  | "/gradingPeriods/@id/reportCards"      | "b40a7eb5-dd74-4666-a5b9-5c3f4425f130,ef72b883-90fa-40fa-afc2-4cb1ae17623b" |
  | "/gradingPeriods/@id/grades"           | "b40a7eb5-dd74-4666-a5b9-5c3f4425f130,ef72b883-90fa-40fa-afc2-4cb1ae17623b" |
  | "/sessions/@id/studentAcademicRecords" | "0410354d-dbcb-0214-250a-404401060c93,abcff7ae-1f01-46bc-8cc7-cf409819bbce" |
  | "/sessions/@id/sections"               | "0410354d-dbcb-0214-250a-404401060c93,abcff7ae-1f01-46bc-8cc7-cf409819bbce" |
  | "/assessments/@id/studentAssessments"  | "c757f9f2dc788924ce0715334c7e86735c5e1327_id,7b2e6133-4224-4890-ac02-73962eb09645" |
  | "/courseOfferings/@id/sections"        | "01709b45-d323-4101-9918-34788dd77306,149fa66a-4a9c-4cca-a371-96fae55aaa8f" |
