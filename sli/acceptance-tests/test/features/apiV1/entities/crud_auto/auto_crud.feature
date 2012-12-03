@smoke @RALLY_US209 @RALLY_DE87
Feature: As an SLI application, I want to be able to perform CRUD operations on various resources
This means I want to be able to perform CRUD on all entities.
and verify that the correct links are made available.

Background: Nothing yet
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
      And format "application/vnd.slc+json"


        Scenario: CRUD operations on an entity
        Then I perform CRUD for each resource available




        Scenario: CRUD operations on invalid entities
        #Read invalid
        Then I navigate to GET with invalid id for each resource available
        #Update Invalid
        Then I navigate to PUT with invalid id for each resource available
        #Delete Invalid
        Then I navigate to DELETE with invalid id for each resource available


    Scenario: Get All Entities as State Staff
    Given my contextual access is defined by table:
    |Context                | Ids                                |
    |schools                |b1bd3db6-d020-4651-b1b8-a8dba688d9e1|
    |educationOrganizations |b1bd3db6-d020-4651-b1b8-a8dba688d9e1|
    |staff                  |85585b27-5368-4f10-a331-3abcaf3a3f4c|
    Given the expected rewrite results are defined by table:
    | Entity Type                              | Entity Resource URI                      | Count | Rewrite URI|
    | assessment                              | assessments                              | 17    |/assessments|
    | attendance                              | attendances                              | 0     |/schools/@ids/studentSchoolAssociations/students/attendances|
    | cohort                                  | cohorts                                  | 2     |/staff/@ids/staffCohortAssociations/cohorts|
    | course                                  | courses                                  | 0     |/schools/@ids/courses|
    | disciplineAction                        | disciplineActions                        | 2     |/staff/@ids/disciplineActions|
    | disciplineIncident                      | disciplineIncidents                      | 0     |/staff/@ids/disciplineIncidents|
    | educationOrganization                   | educationOrganizations                   | 1     |/staff/@ids/staffEducationOrgAssignmentAssociations/educationOrganizations|
    | gradebookEntry                          | gradebookEntries                         | 0     |/schools/@ids/sections/gradebookEntries|
    | learningObjective                       | learningObjectives                       | 5     |/learningObjectives|
    | learningStandard                        | learningStandards                        | 14    |/learningStandards|
    | parent                                  | parents                                  | 0     |/schools/@ids/studentSchoolAssociations/students/studentParentAssociations/parents|
    | program                                 | programs                                 | 2     |/staff/@ids/staffProgramAssociations/programs|
    | studentProgramAssociation               | studentProgramAssociations               | 10    |/staff/@ids/staffProgramAssociations/programs/studentProgramAssociations|
    | courseTranscript                        | courseTranscripts                        | 0     |/schools/@ids/studentSchoolAssociations/students/courseTranscripts|
    | staffEducationOrganizationAssociation   | staffEducationOrgAssignmentAssociations  | 1     |/staff/@ids/staffEducationOrgAssignmentAssociations|
    | studentCohortAssociation                | studentCohortAssociations                | 6     |/staff/@ids/staffCohortAssociations/cohorts/studentCohortAssociations|
    | teacherSectionAssociation               | teacherSectionAssociations               | 0     |/schools/@ids/teacherSchoolAssociations/teachers/teacherSectionAssociations|
    | studentSchoolAssociation                | studentSchoolAssociations                | 0     |/schools/@ids/studentSchoolAssociations|
    | teacherSchoolAssociation                | teacherSchoolAssociations                | 0     |/schools/@ids/teacherSchoolAssociations|
    | studentSectionAssociation               | studentSectionAssociations               | 0     |/schools/@ids/sections/studentSectionAssociations|
    | staffCohortAssociation                  | staffCohortAssociations                  | 2     |/staff/@ids/staffCohortAssociations|
    | studentAssessment                       | studentAssessments                       | 0     |/schools/@ids/studentSchoolAssociations/students/studentAssessments|
    | competencyLevelDescriptor               | competencyLevelDescriptor                | 1     |/competencyLevelDescriptor|
    | staffProgramAssociation                 | staffProgramAssociations                 | 3     |/staff/@ids/staffProgramAssociations|
    | studentDisciplineIncidentAssociation    | studentDisciplineIncidentAssociations    | 0     |/staff/@ids/disciplineIncidents/studentDisciplineIncidentAssociations|
    | studentParentAssociation                | studentParentAssociations                | 0     |/schools/@ids/studentSchoolAssociations/students/studentParentAssociations|
    | courseOffering                          | courseOfferings                          | 0     |/schools/@ids/courseOfferings|
    | graduationPlan                          | graduationPlans                          | 5     |/graduationPlans|
    | school                                  | schools                                  | 0     |/staff/@ids/staffEducationOrgAssignmentAssociations/schools|
    | section                                 | sections                                 | 0     |/schools/@ids/sections|
    | session                                 | sessions                                 | 0     |/educationOrganizations/@ids/sessions|
    | staff                                   | staff                                    | 4     |/educationOrganizations/@ids/staffEducationOrgAssignmentAssociations/staff|
    | student                                 | students                                 | 0     |/schools/@ids/studentSchoolAssociations/students|
    | studentAcademicRecord                   | studentAcademicRecords                   | 0     |/schools/@ids/studentSchoolAssociations/students/studentAcademicRecords|
    | studentGradebookEntry                   | studentGradebookEntries                  | 0     |/schools/@ids/studentSchoolAssociations/students/studentGradebookEntries|
    | teacher                                 | teachers                                 | 0     |/schools/@ids/teacherSchoolAssociations/teachers|
    | grade                                   | grades                                   | 0     |/schools/@ids/sections/studentSectionAssociations/grades|
    | studentCompetencie                      | studentCompetencies                      | 0     |/schools/@ids/sections/studentSectionAssociations/studentCompetencies|
    | gradingPeriod                           | gradingPeriods                           | 0     |/schools/@ids/sessions/gradingPeriods|
    | reportCard                              | reportCards                              | 0     |/schools/@ids/studentSchoolAssociations/students/reportCards|
    | studentCompetencyObjective              | studentCompetencyObjectives              | 0     |/educationOrganizations/@ids/studentCompetencyObjectives    |    
    Then the staff queries and rewrite rules work

#
#    Scenario Outline: CRUD operations on an entity as an IT Admin Teacher
#    Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"
#      And format "application/vnd.slc+json"
#       Given entity URI <Entity Resource URI>
#        # Create
#       Given a valid entity json document for a <Entity Type>
#        When I navigate to POST "/<ENTITY URI>"
#        Then I should receive a return code of 201
#         And I should receive a new entity URI
#        # Read
#        When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
#        Then I should receive a return code of 200
#         And the response should contain the appropriate fields and values
#         And "entityType" should be <Entity Type>
#         And I should receive a link named "self" with URI "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
#        # Update
#        When I set the <Update Field> to <Updated Value>
#         And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
#        Then I should receive a return code of 204
#         And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
#         And <Update Field> should be <Updated Value>
#        # Delete
#        When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
#        Then I should receive a return code of 204
#         And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
#         And I should receive a return code of 404
#
#Examples:
#| Entity Type                    | Entity Resource URI       | Update Field             | Updated Value                                |
#| "disciplineAction"             | "disciplineActions"       | "disciplineDate"         | "2012-03-18"                                 |
#| "educationOrganization"        | "educationOrganizations"  | "nameOfInstitution"      | "Bananas School District"                    |
#| "learningObjective"            | "learningObjectives"      | "academicSubject"        | "Mathematics"                                |
#| "learningStandard"             | "learningStandards"       | "gradeLevel"             | "Ninth grade"                                |
#| "school"                       | "schools"                 | "nameOfInstitution"      | "Yellow Middle School"                       |
#| "studentGradebookEntry"        | "studentGradebookEntries" | "diagnosticStatement"    | "Finished the quiz in 5 hours"               |
#| "grade"                        | "grades"                  | "gradeType"              | "Mid-Term Grade"                             |
#| "studentCompetency"            | "studentCompetencies"     | "diagnosticStatement"    | "advanced nuclear thermodynamics"            |
#| "graduationPlan"               | "graduationPlans"         | "individualPlan"         | "true"                                       |
#| "reportCard"                   | "reportCards"             | "numberOfDaysAbsent"     | "17"                                         |
#
#    Scenario Outline: CRUD operations on an entity as an IT Admin Teacher can't update natural keys
#    Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"
#      And format "application/vnd.slc+json"
#       Given entity URI <Entity Resource URI>
#        # Create
#       Given a valid entity json document for a <Entity Type>
#        When I navigate to POST "/<ENTITY URI>"
#        Then I should receive a return code of 201
#         And I should receive a new entity URI
#        # Read
#        When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
#        Then I should receive a return code of 200
#         And the response should contain the appropriate fields and values
#         And "entityType" should be <Entity Type>
#         And I should receive a link named "self" with URI "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
#        # Update
#        When I set the <Update Field> to <Updated Value>
#         And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
#        Then I should receive a return code of 409
#        # Delete
#        When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
#        Then I should receive a return code of 204
#         And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
#         And I should receive a return code of 404
#
#Examples:
#| Entity Type                    | Entity Resource URI       | Update Field             | Updated Value                                |
#| "assessment"                   | "assessments"             | "assessmentTitle"        | "Advanced Placement Test - Subject: Writing" |
#| "attendance"                   | "attendances"             | "studentId"              | "2fab099f-47d5-4099-addf-69120db3b53b"       |
#| "gradebookEntry"               | "gradebookEntries"        | "gradebookEntryType"     | "Homework"                                   |
#| "studentAcademicRecord"        | "studentAcademicRecords"  | "sessionId"              | "abcff7ae-1f01-46bc-8cc7-cf409819bbce"       |
    Scenario: CRUD operations requiring explicit associations on an entity as an IT Admin Teacher
    Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"
      And format "application/vnd.slc+json"
      Then I perform CRUD for each resource available



    Scenario: Get All Entities as School Teacher
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
     And format "application/vnd.slc+json"
    And my contextual access is defined by table:
    | Context                | Ids                                                                          |
    | schools                | 92d6d5a0-852c-45f4-907a-912752831772,6756e2b9-aba1-4336-80b8-4a5dde3c63fe    |
    | educationOrganizations | 92d6d5a0-852c-45f4-907a-912752831772,6756e2b9-aba1-4336-80b8-4a5dde3c63fe    |
    | staff                  | e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b                                         |
    | teachers               | e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b                                         |
    | sections               | 15ab6363-5509-470c-8b59-4f289c224107_id,47b5adbf-6fd0-4f07-ba5e-39612da2e234 |
    Given the expected rewrite results are defined by table:
    | Entity Type                              | Entity Resource URI                      | Count | Rewrite URI|
    | assessment                              | assessments                              | 17    |/assessments|
    | attendance                              | attendances                              | 4     |/sections/@ids/studentSectionAssociations/students/attendances|
    | cohort                                  | cohorts                                  | 1     |/staff/@ids/staffCohortAssociations/cohorts|
    | course                                  | courses                                  | 26    |/schools/@ids/courses|
    | disciplineAction                        | disciplineActions                        | 0     |/staff/@ids/disciplineActions|
    | disciplineIncident                      | disciplineIncidents                      | 0     |/staff/@ids/disciplineIncidents|
    | school                                  | educationOrganizations                   | 2     |/teachers/@ids/teacherSchoolAssociations/schools|
    | gradebookEntry                          | gradebookEntries                         | 1     |/sections/@ids/gradebookEntries|
    | learningObjective                       | learningObjectives                       | 5     |/learningObjectives|
    | learningStandard                        | learningStandards                        | 15    |/learningStandards|
    | parent                                  | parents                                  | 2     |/sections/@ids/studentSectionAssociations/students/studentParentAssociations/parents|
    | program                                 | programs                                 | 0     |/staff/@ids/staffProgramAssociations/programs|
    | studentProgramAssociation               | studentProgramAssociations               | 0     |/staff/@ids/staffProgramAssociations/programs/studentProgramAssociations    |
    | courseTranscript                        | courseTranscripts                        | 2     |/sections/@ids/studentSectionAssociations/students/courseTranscripts    |
    | staffEducationOrganizationAssociation   | staffEducationOrgAssignmentAssociations  | 3     |/educationOrganizations/@ids/staffEducationOrgAssignmentAssociations    |
    | studentCohortAssociation                | studentCohortAssociations                | 1     |/staff/@ids/staffCohortAssociations/cohorts/studentCohortAssociations    |
    | teacherSectionAssociation               | teacherSectionAssociations               | 2     |/teachers/@ids/teacherSectionAssociations    |
    | studentSchoolAssociation                | studentSchoolAssociations                | 59    |/sections/@ids/studentSectionAssociations/students/studentSchoolAssociations    |
    | teacherSchoolAssociation                | teacherSchoolAssociations                | 2     |/teachers/@ids/teacherSchoolAssociations    |
    | studentSectionAssociation               | studentSectionAssociations               | 25    |/sections/@ids/studentSectionAssociations    |
    | staffCohortAssociation                  | staffCohortAssociations                  | 1     |/staff/@ids/staffCohortAssociations    |
    | studentAssessment                       | studentAssessments                       | 1     |/sections/@ids/studentSectionAssociations/students/studentAssessments    |
    | competencyLevelDescriptor               | competencyLevelDescriptor                | 0     |/competencyLevelDescriptor    |
    | staffProgramAssociation                 | staffProgramAssociations                 | 0     |/staff/@ids/staffProgramAssociations    |
    | studentDisciplineIncidentAssociation    | studentDisciplineIncidentAssociations    | 0     |/staff/@ids/disciplineIncidents/studentDisciplineIncidentAssociations    |
    | studentParentAssociation                | studentParentAssociations                | 2     |/sections/@ids/studentSectionAssociations/students/studentParentAssociations    |
    | courseOffering                          | courseOfferings                          | 26    |/schools/@ids/courseOfferings    |
    | graduationPlan                          | graduationPlans                          | 5     |/graduationPlans    |
    | school                                  | schools                                  | 2     |/teachers/@ids/teacherSchoolAssociations/schools|
    | section                                 | sections                                 | 2     |/teachers/@ids/teacherSectionAssociations/sections|
    | session                                 | sessions                                 | 6     |/educationOrganizations/@ids/sessions|
    | staff                                   | staff                                    | 3     |/educationOrganizations/@ids/staffEducationOrgAssignmentAssociations/staff|
    | student                                 | students                                 | 25    |/sections/@ids/studentSectionAssociations/students|
    | studentAcademicRecord                   | studentAcademicRecords                   | 3     |/sections/@ids/studentSectionAssociations/students/studentAcademicRecords|
    | studentGradebookEntry                   | studentGradebookEntries                  | 2     |/sections/@ids/studentSectionAssociations/students/studentGradebookEntries|
    | teacher                                 | teachers                                 | 3     |/schools/@ids/teacherSchoolAssociations/teachers|
    | grade                                   | grades                                   | 1     |/sections/@ids/studentSectionAssociations/grades|
    | studentCompetency                       | studentCompetencies                      | 2     |/sections/@ids/studentSectionAssociations/studentCompetencies|
    | gradingPeriod                           | gradingPeriods                           | 2     |/schools/@ids/sessions/gradingPeriods|
    | reportCard                              | reportCards                              | 3     |/sections/@ids/studentSectionAssociations/students/reportCards|
    | studentCompetencyObjective              | studentCompetencyObjectives              | 0     |/educationOrganizations/@ids/studentCompetencyObjectives    |




