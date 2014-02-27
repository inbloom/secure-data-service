@smoke @RALLY_US209 @RALLY_DE87
Feature: As an SLI application, I want to be able to perform CRUD operations on various resources
  This means I want to be able to perform CRUD on all entities.
  and verify that the correct links are made available.

  Background: Nothing yet
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/vnd.slc+json"

  @DE2943
  Scenario: Search on fields with insufficient rights returns bad request
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And format "application/vnd.slc+json"
    When I navigate to GET "/v1/students/0c2756fd-6a30-4010-af79-488d6ef2735a_id?economicDisadvantaged=false"
    Then I should receive a return code of 400
    When I navigate to GET "/v1/students/0c2756fd-6a30-4010-af79-488d6ef2735a_id?economicDisadvantaged=true"
    Then I should receive a return code of 400

  @DE2943
  Scenario: Search on inaccessible entities with fields returns acess denied
    Given I am logged in using "jvasquez" "jvasquez" to realm "IL"
    And format "application/vnd.slc+json"
    When I navigate to GET "/v1/students/414106a9-6156-1023-a477-4bd4dda7e21a_id?economicDisadvantaged=false"
    Then I should receive a return code of 403
    When I navigate to GET "/v1/students/414106a9-6156-1023-a477-4bd4dda7e21a_id?economicDisadvantaged=true"
    Then I should receive a return code of 403

  Scenario Outline: CRUD operations requiring explicit associations on an entity as staff
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/vnd.slc+json"
    Given entity URI <Entity Resource URI>
  # Create
    Given a valid entity json document for a <Entity Type>
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
  # Association
    When I create an association of type <Association Type>
    When I POST the association of type <Association Type>
    Then I should receive a return code of 201
  # Read
    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200
    And a valid entity json document for a <Entity Type>
    And the response should contain the appropriate fields and values
    And "entityType" should be <Entity Type>
    And I should receive a link named "self" with URI "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
  # Update
    When I set the <Update Field> to <Updated Value>
    And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    And <Update Field> should be <Updated Value>
  # Delete
    When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    And I should receive a return code of 404

  Examples:
    | Entity Type | Entity Resource URI | Association Type                         | Update Field             | Updated Value            |
    | "staff"     | "staff"             | "staffEducationOrganizationAssociation2" | "sex"                    | "Female"                 |
    | "teacher"   | "teachers"          | "teacherSchoolAssociation2"              | "highlyQualifiedTeacher" | "false"                  |
    | "program"   | "programs"          | "staffProgramAssociation"                | "programSponsor"         | "State Education Agency" |

  Scenario Outline: CRUD operations on an entity and can't update natural key
    Given entity URI <Entity Resource URI>
  # Create
    Given a valid entity json document for a <Entity Type>
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
  # Read
    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200
    And the response should contain the appropriate fields and values
    And "entityType" should be <Entity Type>
    And I should receive a link named "self" with URI "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
  # Update
    When I set the <Update Field> to <Updated Value>
    And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 409
  # Delete
    When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    And I should receive a return code of 404

  Examples:
    | Entity Type             | Entity Resource URI      | Update Field         | Updated Value                                |
    | "assessment"            | "assessments"            | "assessmentTitle"    | "Advanced Placement Test - Subject: Writing" |
    | "gradebookEntry"        | "gradebookEntries"       | "gradebookEntryType" | "Homework"                                   |
    | "studentAcademicRecord" | "studentAcademicRecords" | "sessionId"          | "abcff7ae-1f01-46bc-8cc7-cf409819bbce"       |
    | "grade"                 | "grades"                 | "schoolYear"         | "2008-2009"                                  |

  Scenario Outline: CRUD operations on an entity requiring explicit associations and can't update natural key
    Given entity URI <Entity Resource URI>
  # Create
    Given a valid entity json document for a <Entity Type>
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
  # Optional Association
    When I create an association of type <Association Type>
    When I POST the association of type <Association Type>
    Then I should receive a return code of 201
  # Read
    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200
    And a valid entity json document for a <Entity Type>
    And the response should contain the appropriate fields and values
    And "entityType" should be <Entity Type>
    And I should receive a link named "self" with URI "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
  # Update
    When I set the <Update Field> to <Updated Value>
    And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 409
  # Delete
    When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    And I should receive a return code of 404

  Examples:
    | Entity Type | Entity Resource URI | Association Type            | Update Field          | Updated Value |
    | "parent"    | "parents"           | "studentParentAssociation2" | "parentUniqueStateId" | "ParentID102" |

  Scenario Outline: CRUD operations requiring explicit associations on an entity as an IT Admin Teacher
    Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"
    And format "application/vnd.slc+json"
    Given entity URI <Entity Resource URI>
  # Create
    Given a valid entity json document for a <Entity Type>
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
  # Association
    When I create an association of type <Association Type>
    When I POST the association of type <Association Type>
    Then I should receive a return code of 201
  # Read
    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200
    And a valid entity json document for a <Entity Type>
    And the response should contain the appropriate fields and values
    And "entityType" should be <Entity Type>
    And I should receive a link named "self" with URI "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
  # Update
    When I set the <Update Field> to <Updated Value>
    And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    And <Update Field> should be <Updated Value>
  # Delete
    When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    And I should receive a return code of <Return Code>

  Examples:
    | Entity Type          | Entity Resource URI   | Association Type                        | Update Field             | Updated Value            | Return Code |
    | "cohort"             | "cohorts"             | "studentCohortAssocation"               | "cohortDescription"      | "frisbee golf team"      | 404         |
    | "disciplineIncident" | "disciplineIncidents" | "studentDisciplineIncidentAssociation"  | "incidentTime"           | "01:02:15"               | 404         |
    | "program"            | "programs"            | "studentProgramAssociation"             | "programSponsor"         | "State Education Agency" | 404         |
    | "section"            | "sections"            | "studentSectionAssociation"             | "sequenceOfCourse"       | "2"                      | 404         |
    | "staff"              | "staff"               | "staffEducationOrganizationAssociation" | "sex"                    | "Female"                 | 404         |
    | "student"            | "students"            | "studentSectionAssociation2"            | "sex"                    | "Female"                 | 404         |
    | "teacher"            | "teachers"            | "teacherSchoolAssociation"              | "highlyQualifiedTeacher" | "false"                  | 404         |

# Session and course require multiple levels of associations, e.g. course -> courseOffering -> section -> teacherSectionAssoc
#| "session"                      | "sessions"            | | |  "totalInstructionalDays" | "43"                                         |
#| "course"                       | "courses"             | "courseOffering"                       | "section"         | "courseDescription"      | "Advanced Linguistic Studies" |

  Scenario Outline: CRUD operations requiring explicit associations on an entity as an IT Admin Teacher and can't update natural keys
    Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"
    And format "application/vnd.slc+json"
    Given entity URI <Entity Resource URI>
  # Create
    Given a valid entity json document for a <Entity Type>
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
  # Association
    When I create an association of type <Association Type>
    When I POST the association of type <Association Type>
    Then I should receive a return code of 201
  # Read
    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200
    And a valid entity json document for a <Entity Type>
    And the response should contain the appropriate fields and values
    And "entityType" should be <Entity Type>
    And I should receive a link named "self" with URI "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
  # Update
    When I set the <Update Field> to <Updated Value>
    And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 409
  # Delete
    When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    And I should receive a return code of 404

  Examples:
    | Entity Type | Entity Resource URI | Association Type           | Update Field          | Updated Value |
    | "parent"    | "parents"           | "studentParentAssociation" | "parentUniqueStateId" | "ParentID102" |

  Scenario Outline: Get All Entities as School Teacher

    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    And format "application/vnd.slc+json"
    And my contextual access is defined by table:
      | Context                | Ids                                                                             |
      | schools                | 92d6d5a0-852c-45f4-907a-912752831772,6756e2b9-aba1-4336-80b8-4a5dde3c63fe       |
      | educationOrganizations | 92d6d5a0-852c-45f4-907a-912752831772,6756e2b9-aba1-4336-80b8-4a5dde3c63fe       |
      | staff                  | e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b                                            |
      | teachers               | e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b                                            |
      | sections               | 15ab6363-5509-470c-8b59-4f289c224107_id,47b5adbf-6fd0-4f07-ba5e-39612da2e234_id |
    Given entity URI <Entity Resource URI>
  #TODO BUG for the 6 entities routed to ES, revert back to "0" when bug is fixed
    Given parameter "limit" is "250"
    When I navigate to GET "/<ENTITY URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count>" entities
    And each entity's "entityType" should be <Entity Type>
    And uri was rewritten to "<Rewrite URI>"

  Examples:
    | Entity Type                  | Entity Resource URI           | Count | Rewrite URI                                                                          |
    | "assessment"                 | "assessments"                 | 18    | /search/assessments                                                                  |
    | "attendance"                 | "attendances"                 | 4     | /sections/@ids/studentSectionAssociations/students/attendances                       |
    | "cohort"                     | "cohorts"                     | 4     | /staff/@ids/staffCohortAssociations/cohorts                                          |
    | "course"                     | "courses"                     | 92    | /search/courses                                                                      |
    | "disciplineAction"           | "disciplineActions"           | 0     | /staff/@ids/disciplineActions                                                        |
    | "disciplineIncident"         | "disciplineIncidents"         | 0     | /staff/@ids/disciplineIncidents                                                      |
    | "educationOrganization"      | "educationOrganizations"      | 2     | /teachers/@ids/teacherSchoolAssociations/schools                                     |
    | "gradebookEntry"             | "gradebookEntries"            | 3     | /sections/@ids/gradebookEntries                                                      |
    | "learningObjective"          | "learningObjectives"          | 5     | /search/learningObjectives                                                           |
    | "learningStandard"           | "learningStandards"           | 14    | /search/learningStandards                                                            |
    | "parent"                     | "parents"                     | 3     | /sections/@ids/studentSectionAssociations/students/studentParentAssociations/parents |
    | "program"                    | "programs"                    | 1     | /staff/@ids/staffProgramAssociations/programs                                        |
    | "educationOrganization"      | "schools"                     | 2     | /teachers/@ids/teacherSchoolAssociations/schools                                     |
    | "section"                    | "sections"                    | 2     | /teachers/@ids/teacherSectionAssociations/sections                                   |
    | "session"                    | "sessions"                    | 29    | /search/sessions                                                                     |
    | "staff"                      | "staff"                       | 6     | /educationOrganizations/@ids/staffEducationOrgAssignmentAssociations/staff           |
    | "student"                    | "students"                    | 25    | /sections/@ids/studentSectionAssociations/students                                   |
    | "studentAcademicRecord"      | "studentAcademicRecords"      | 2     | /sections/@ids/studentSectionAssociations/students/studentAcademicRecords            |
    | "studentGradebookEntry"      | "studentGradebookEntries"     | 2     | /sections/@ids/studentSectionAssociations/students/studentGradebookEntries           |
    | "teacher"                    | "teachers"                    | 3     | /schools/@ids/teacherSchoolAssociations/teachers                                     |
    | "grade"                      | "grades"                      | 1     | /sections/@ids/studentSectionAssociations/grades                                     |
    | "studentCompetency"          | "studentCompetencies"         | 2     | /sections/@ids/studentSectionAssociations/studentCompetencies                        |
    | "gradingPeriod"              | "gradingPeriods"              | 3     | /search/gradingPeriods                                                               |
    | "reportCard"                 | "reportCards"                 | 3     | /sections/@ids/studentSectionAssociations/students/reportCards                       |

    #ds-917: list endpoint for public data no longer shows entities from edorgs not directly associated with the user
    #these entities can still be read by accessing them by id.
    #| "studentCompetencyObjective" | "studentCompetencyObjectives" | 1     | /search/studentCompetencyObjectives                                                  |

  @DE1825
  Scenario: Invalid data parsing fails gracefully
    When I navigate to GET "/v1/staffEducationOrgAssignmentAssociations?endDate=blah"
    Then I should receive a return code of 400
    When I create an association of type "studentSectionAssociation"
    And field "beginDate" is removed from the json document
    When I navigate to POST "/v1/studentSectionAssociations"
    Then I should receive a return code of 400



#all staff types (it admins, educators) should be able to see all public entities

  @tagPublicEntities
  Scenario Outline: Ensure Public Entities Are Visible
    Given I am logged in using <User> <Password> to realm "IL"
    And entity URI <Entity>
    And parameter "limit" is "0"
    When I navigate to GET "/<ENTITY URI>"
    Then I should receive a return code of 200
  #generic step that sets global variable for current entity
    And I should see all entities

  Examples:
    | User         | Password         | Entity            |
    | "linda.kim"  | "linda.kim1234"  | "sessions"        |
    | "linda.kim"  | "linda.kim1234"  | "gradingPeriods"  |
    | "linda.kim"  | "linda.kim1234"  | "courseOfferings" |
    | "linda.kim"  | "linda.kim1234"  | "courses"         |
    | "jstevenson" | "jstevenson1234" | "sessions"        |
    | "jstevenson" | "jstevenson1234" | "gradingPeriods"  |
    | "jstevenson" | "jstevenson1234" | "courseOfferings" |
    | "jstevenson" | "jstevenson1234" | "courses"         |


  #crud assessment / studentAssessment and verify in mongo it's superdoc'ed
  @US5365 @AssmtTest

  Scenario: crud on super assessment and super studentAssessment
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And entity URI "/v1/assessments"
    And format "application/vnd.slc+json"
    And a valid entity json document for a "base_super_assessment"
    When I navigate to POST "<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    And a valid entity json document for a "super_assessment"
    When I navigate to PUT "/assessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I verify "objectiveAssessment" and "assessmentItem" should be subdoc'ed in mongo for this new "assessment"
    And I verify there are "1" "assessmentPeriodDescriptor" with "codeValue=codeGreen" in mongo
    When I navigate to GET "/assessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200
    And I verify "objectiveAssessment, assessmentPeriodDescriptor" and "assessmentItem" is collapsed in response body
    And "objectiveAssessment" is hierachical with children at "objectiveAssessments"
    When I set the "lowestGradeLevelAssessed" to "Sixth grade"
    And I set the "codeValue" to "codeRed" in "assessmentPeriodDescriptor"
    And I navigate to PUT "/assessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I verify "objectiveAssessment" and "assessmentItem" should be subdoc'ed in mongo for this new "assessment"
  #assessmentPeriodDecriptor is read
    And I verify there are "1" "assessmentPeriodDescriptor" with "codeValue=codeRed" in mongo
    And I verify there are "1" "assessmentPeriodDescriptor" with "codeValue=codeGreen" in mongo
    And I navigate to GET "/assessments/<NEWLY CREATED ENTITY ID>"
    And "lowestGradeLevelAssessed" should be "Sixth grade"
    And I verify "objectiveAssessment, assessmentPeriodDescriptor" and "assessmentItem" is collapsed in response body
    And I verify "codeValue" is "codeRed" inside "assessmentPeriodDescriptor"
  # the corresponding studentAssessment
    Given entity URI "/v1/studentAssessments"
    And a valid entity json document for a "studentAssessment"
    When I navigate to POST "<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    And I verify "studentObjectiveAssessment" and "studentAssessmentItem" should be subdoc'ed in mongo for this new "studentAssessment"
    When I navigate to GET "/studentAssessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200
  # verifies DID and associations between studentAssessment and assessment
    And I verify "studentObjectiveAssessments" and "studentAssessmentItems" is collapsed in response body
    And I verify there are "2" "studentObjectiveAssessments" in response body
    When I set the "administrationEnvironment" to "School"
    And I navigate to PUT "/studentAssessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I verify "studentObjectiveAssessment" and "studentAssessmentItem" should be subdoc'ed in mongo for this new "studentAssessment"
    When I navigate to GET "/studentAssessments/<NEWLY CREATED ENTITY ID>"
    And "administrationEnvironment" should be "School"
    Then I delete both studentAssessment and Assessment

  Scenario: crud on assessmentItem
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And entity URI "/v1/assessments"
    And format "application/vnd.slc+json"
    And a valid entity json document for a "base_super_assessment"
    When I navigate to POST "<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    And a valid entity json document for a "assessment_item"
    When I navigate to PUT "/assessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    When I navigate to GET "/assessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200
    And I set the "correctResponse" to "true" in the first "assessmentItem" subdoc
    And I navigate to PUT "/assessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I remove the "correctResponse" field in the first "assessmentItem" subdoc
    And I navigate to PUT "/assessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    When I navigate to DELETE "/assessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
#yearlyAttendance CRUD

  Scenario: Validate AssessmentItem, ObjectiveAssessment, StudentAssessmentItem, StudentObjectiveAssessment are validated correctly
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And entity URI "/v1/assessments"
    And format "application/vnd.slc+json"
    And a valid entity json document for a "base_super_assessment"
    When I navigate to POST "<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI

    And a valid entity json document for a "assessment_item"
    And I remove the "identificationCode" field in the first "assessmentItem" subdoc
    When I navigate to PUT "/assessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 400
    #Invalid Reference error
    And a valid entity json document for a "assessment_item"
    And I set the "assessmentId" to "IncorrectAssessmentId" in the first "assessmentItem" subdoc
    And I navigate to PUT "/assessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 400

    Given a valid entity json document for a "invalid_nested_objective_assessment"
    And I navigate to PATCH "/assessments/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 400

    Given entity URI "/v1/studentAssessments"
    And a valid entity json document for a "missing_req_field_studentOA"
    When I navigate to POST "<ENTITY URI>"
    Then I should receive a return code of 400

  Scenario Outline: CRUD operations till we unwip auto_crud
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/vnd.slc+json"
    Given entity URI <Entity Resource URI>
  # Create
    Given a valid entity json document for a <Entity Type>
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
  # Read
    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200
    And a valid entity json document for a <Entity Type>
    And the response should contain the appropriate fields and values
    And "entityType" should be <Entity Type>
    And I should receive a link named "self" with URI "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
  # Update
    When I set the <Update Field> to <Updated Value>
    And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    And <Update Field> should be <Updated Value>
  # Delete
    When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    And I should receive a return code of 404

  Examples:
    | Entity Type      | Entity Resource URI | Update Field  | Updated Value         |
    | "gradebookEntry" | "gradebookEntries"  | "description" | "Updated description" |


  Scenario Outline: Get modified grade, reportCard and AcademicRecord entities

    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    And format "application/vnd.slc+json"
    And my contextual access is defined by table:
      | Context                | Ids                                                                             |
      | schools                | 92d6d5a0-852c-45f4-907a-912752831772,6756e2b9-aba1-4336-80b8-4a5dde3c63fe       |
      | educationOrganizations | 92d6d5a0-852c-45f4-907a-912752831772,6756e2b9-aba1-4336-80b8-4a5dde3c63fe       |
      | staff                  | e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b                                            |
      | teachers               | e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b                                            |
      | sections               | 15ab6363-5509-470c-8b59-4f289c224107_id,47b5adbf-6fd0-4f07-ba5e-39612da2e234_id |
    Given entity URI <Entity Resource URI>
    Given parameter "limit" is "250"
    When I navigate to GET "/<ENTITY URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count>" entities
    And each entity's "entityType" should be <Entity Type>
  #And each entity's "schoolyear" value should be <school year>
    And uri was rewritten to "<Rewrite URI>"
    And the response should contain the "<school year>" field
  Examples:
    | Entity Type             | Entity Resource URI      | Count | Rewrite URI                                                               |  | school year |
    | "studentAcademicRecord" | "studentAcademicRecords" | 2     | /sections/@ids/studentSectionAssociations/students/studentAcademicRecords |  | 2010-2011   |
    | "grade"                 | "grades"                 | 1     | /sections/@ids/studentSectionAssociations/grades                          |  | 2010-2011   |
    | "reportCard"            | "reportCards"            | 3     | /sections/@ids/studentSectionAssociations/students/reportCards            |  | 2010-2011   |

  @student
  Scenario Outline: Rewrites for student work
    Given I am logged in using "carmen.ortiz" "carmen.ortiz1234" to realm "IL-Daybreak-Students"
    And format "application/vnd.slc+json"
    And my contextual access is defined by table:
      | Context                | Ids                                                                       |
      | schools                | 92d6d5a0-852c-45f4-907a-912752831772,ec2e4218-6483-4e9c-8954-0aecccfd4731 |
      | educationOrganizations | 92d6d5a0-852c-45f4-907a-912752831772,ec2e4218-6483-4e9c-8954-0aecccfd4731 |
      | students               | 11e51fc3-2e4a-4ef0-bfe7-c8c29d1a798b_id                                   |
    When I navigate to GET "/v1.5/<Entity>"
    Then I should receive a return code of 200
    And uri was rewritten to "<Rewrite>"
  Examples:
    | Entity                      | Rewrite                                                  |
    | courses                     | /schools/@ids/courses                                    |
    | schools                     | /schools/@ids                                            |
    | educationOrganizations      | /schools/@ids                                            |
    #| programs                    | /students/@ids/studentProgramAssociations/programs                            |
    | courseOfferings             | /schools/@ids/courseOfferings                            |
    | sessions                    | /schools/@ids/sessions                                   |
    #| sections                    | /students/@ids/studentSectionAssociations/sections |
    | gradingPeriods              | /schools/@ids/sessions/gradingPeriods                    |
    | graduationPlans             | /schools/@ids/graduationPlans                            |
    #| assessments                 | /student/@ids/studentAssessments/assessments      |
    | learningObjectives          | /search/learningObjectives                               |
    | learningStandards           | /search/learningStandards                                |
    | studentCompetencyObjectives | /educationOrganizations/@ids/studentCompetencyObjectives |

  Scenario: Sub docs are preserved when a super doc is deleted
  # Create
    Given entity URI "parents"
    And format "application/vnd.slc+json"
    And a valid entity json document for a "parent"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
  # Read
    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200
    And the response should contain the appropriate fields and values
    And "entityType" should be "parent"
  # Create Association
    When I create an association of type "student_studentParentAssociation"
    When I POST the association of type "student_studentParentAssociation"
    Then I should receive a return code of 201
    And I should receive a new entity URI
  # Read Association
    Given entity URI "studentParentAssociations"
    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200
  #And a valid entity json document for a <Entity Type>
    And the response should contain the appropriate fields and values
    And "entityType" should be "studentParentAssociation"

  # Delete Superdoc
    When I delete the superdoc "students" of "<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204
    Given entity URI "studentParentAssociations"
    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200