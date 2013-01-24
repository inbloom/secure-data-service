@RALLY_US209
Feature: As an SLI application, I want to present links between an association and its end points.
  That means I should present the corrects links in an API response.

  Background: Logged in as IT Admin Rick Rogers
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/vnd.slc+json"
    And parameter "limit" is "0"

    Scenario Outline: Links to/from end points - single members
    When I navigate to GET "/<ASSOC URI>/<ASSOC ID>"
    Then I should receive a return code of 200
    And "entityType" should be "<ASSOC TYPE>"
    And I should receive a link named "<ENDPOINT1 LINK>" with URI "/<ENDPOINT1 URI>/<ENDPOINT1 ID>"
    And I should receive a link named "<ENDPOINT2 LINK>" with URI "/<ENDPOINT2 URI>/<ENDPOINT2 ID>"
    # End point 1
    When I navigate to GET "/<ASSOC URI>/<ASSOC ID>/<ENDPOINT1 URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "1" entities
    And each entity's "id" should be "<ENDPOINT1 ID>"
    And in each entity, I should receive a link named "<ASSOC LINK>" with URI "/<ENDPOINT1 URI>/<ENDPOINT1 ID>/<ASSOC URI>"
    And in each entity, I should receive a link named "<ENDPOINT2 RESOLUTION>" with URI "/<ENDPOINT1 URI>/<ENDPOINT1 ID>/<ASSOC URI>/<ENDPOINT2 URI>"
    # End point 2
    When I navigate to GET "/<ASSOC URI>/<ASSOC ID>/<ENDPOINT2 URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "1" entities
    And each entity's "id" should be "<ENDPOINT2 ID>"
    And in each entity, I should receive a link named "<ASSOC LINK>" with URI "/<ENDPOINT2 URI>/<ENDPOINT2 ID>/<ASSOC URI>"
    And in each entity, I should receive a link named "<ENDPOINT1 RESOLUTION>" with URI "/<ENDPOINT2 URI>/<ENDPOINT2 ID>/<ASSOC URI>/<ENDPOINT1 URI>"
    # Associations for end point 1
    When I navigate to GET "/<ENDPOINT1 URI>/<ENDPOINT1 ID>/<ASSOC URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "<COUNT 1>" entities
    And each entity's "entityType" should be "<ASSOC TYPE>"
    And each entity's "<ENDPOINT1 FIELD>" should be "<ENDPOINT1 ID>"
    # Associations for end point 2
    When I navigate to GET "/<ENDPOINT2 URI>/<ENDPOINT2 ID>/<ASSOC URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "<COUNT 2>" entities
    And each entity's "entityType" should be "<ASSOC TYPE>"
    And each entity's "<ENDPOINT2 FIELD>" should be "<ENDPOINT2 ID>"

    Examples:
    | ASSOC TYPE                             | ASSOC URI                                | ASSOC ID                             | ASSOC LINK                                 | ENDPOINT1 LINK     | ENDPOINT2 LINK           | ENDPOINT1 RESOLUTION   | ENDPOINT2 RESOLUTION      | ENDPOINT1 URI     | ENDPOINT2 URI          | ENDPOINT1 ID                         | ENDPOINT2 ID                            | ENDPOINT1 FIELD    | ENDPOINT2 FIELD                | COUNT 1 | COUNT 2 |
    | courseOffering                         | courseOfferings                          | b360e8e8-54d1-4b00-952a-b817f91035ed | getCourseOfferings                         | getSession         | getCourse                | getSessions            | getCourses                | sessions          | courses                | d23ebfc4-5192-4e6c-a52b-81cee2319072 | 52038025-1f18-456a-884e-d2e63f9a02f4    | sessionId          | courseId                       | 6       | 1       |
    | staffEducationOrganizationAssociation  | staffEducationOrgAssignmentAssociations  | c4d5d31b-001d-4573-b282-7e688a4676f9 | getStaffEducationOrgAssignmentAssociations | getStaff           | getEducationOrganization | getStaff               | getEducationOrganizations | staff             | educationOrganizations | 85585b27-5368-4f10-a331-3abcaf3a3f4c | b1bd3db6-d020-4651-b1b8-a8dba688d9e1    | staffReference     | educationOrganizationReference | 1       | 4       |
    | studentCohortAssociation               | studentCohortAssociations                | b408635d-8fd5-11e1-86ec-0021701f543f_idb40c5b02-8fd5-11e1-86ec-0021701f543f_id | getStudentCohortAssociations               | getStudent         | getCohort                | getStudents            | getCohorts                | students          | cohorts                | 41df2791-b33c-4b10-8de6-a24963bbd3dd_id | b408635d-8fd5-11e1-86ec-0021701f543f_id    | studentId          | cohortId                       | 1       | 3       |
    | studentDisciplineIncidentAssociation   | studentDisciplineIncidentAssociations    | 0fb8e0b4-8f84-48a4-b3f0-9ba7b0513dba_id0e26de6c-225b-9f67-9625-5113ad50a03b_id | getStudentDisciplineIncidentAssociations   | getStudent         | getDisciplineIncident    | getStudents            | getDisciplineIncidents    | students          | disciplineIncidents    | 0fb8e0b4-8f84-48a4-b3f0-9ba7b0513dba_id | 0e26de79-222a-5e67-9201-5113ad50a03b    | studentId          | disciplineIncidentId           | 1       | 2       |
    | studentParentAssociation               | studentParentAssociations                | 74cf790e-84c4-4322-84b8-fca7206f1085_iddd69083f-a053-4819-a3cd-a162cdc627d7_id | getStudentParentAssociations               | getStudent         | getParent                | getStudents            | getParents                | students          | parents                | 74cf790e-84c4-4322-84b8-fca7206f1085_id | eb4d7e1b-7bed-890a-cddf-cdb25a29fc2d | studentId          | parentId                       | 1       | 1       |
    | studentProgramAssociation              | studentProgramAssociations               | 9b8cafdc-8fd5-11e1-86ec-0021701f543f_idb3f68907-8fd5-11e1-86ec-0021701f543f_id | getStudentProgramAssociations              | getStudent         | getProgram               | getStudents            | getPrograms               | students          | programs               | 0f0d9bac-0081-4900-af7c-d17915e02378_id | 9b8cafdc-8fd5-11e1-86ec-0021701f543f_id    | studentId          | programId                      | 1       | 7       |
    | studentSchoolAssociation               | studentSchoolAssociations                | e7e0e926-874e-4d05-9177-9776d44c5fb5 | getStudentSchoolAssociations               | getStudent         | getSchool                | getStudents            | getSchools                | students          | schools                | 11d13fde-371c-4b58-b0b0-a6e2d955a947_id | a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb    | studentId          | schoolId                       | 2       | 62      |
    | studentSectionAssociation              | studentSectionAssociations               | 8ed12459-eae5-49bc-8b6b-6ebe1a56384f_id4ae72560-3518-4576-a35e-a9607668c9ad_id | getStudentSectionAssociations              | getStudent         | getSection               | getStudents            | getSections               | students          | sections               | 27fea52e-94ab-462c-b80f-7e868f6919d7_id | 8ed12459-eae5-49bc-8b6b-6ebe1a56384f_id    | studentId          | sectionId                      | 2       | 25      |
    | courseTranscript                       | courseTranscripts                        | f11a2a30-d4fd-4400-ae18-353c00d581a2 | getCourseTranscripts                       | getStudent         | getCourse                | getStudents            | getCourses                | students          | courses                | 74cf790e-84c4-4322-84b8-fca7206f1085_id | f917478f-a6f2-4f78-ad9d-bf5972b5567b | studentId          | courseId                       | 1       | 0       |
    | teacherSchoolAssociation               | teacherSchoolAssociations                | 26a4a0fc-fad4-45f4-a00d-285acd1f83eb | getTeacherSchoolAssociations               | getTeacher         | getSchool                | getTeachers            | getSchools                | teachers          | schools                | 67ed9078-431a-465e-adf7-c720d08ef512 | ec2e4218-6483-4e9c-8954-0aecccfd4731    | teacherId          | schoolId                       | 1       | 1       |
    | teacherSectionAssociation              | teacherSectionAssociations               | 15ab6363-5509-470c-8b59-4f289c224107_id32b86a2a-e55c-4689-aedf-4b676f3da3fc_id | getTeacherSectionAssociations | getTeacher | getSection | getTeachers     | getSections               | teachers          | sections               | e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b | 15ab6363-5509-470c-8b59-4f289c224107_id | teacherId          | sectionId                      | 2       | 1       |
    | staffCohortAssociation                 | staffCohortAssociations                  | 8fef446f-fc63-15f9-8606-0b85086c07d6 | getStaffCohortAssociations                 | getStaff           | getCohort                | getStaff               | getCohorts                | staff             | cohorts                | 85585b27-5368-4f10-a331-3abcaf3a3f4c | b408d88e-8fd5-11e1-86ec-0021701f543f_id    | staffId            | cohortId                       | 2       | 4       |
