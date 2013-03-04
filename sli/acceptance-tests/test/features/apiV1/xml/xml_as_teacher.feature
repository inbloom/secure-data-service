@RALLY_DE87
@RALLY_US209
@RALLY_US210
Feature: As an SLI application, I want to be able to support XML.
  This means the API should support POST, PUT and GET with XML format.

Background: Nothing yet
  Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
  And format "application/xml"

  Scenario Outline: Getting response from GET - Read
    When I navigate to GET "/v1/<ENTITY URI>/<ENTITY ID>"
    Then I should receive a return code of 200
    And I should receive an XML document
    And I should see "<entityType>" is "<ENTITY TYPE>"

  Examples:
    | ENTITY URI                  | ENTITY TYPE                | ENTITY ID                               |
    | assessments                 | assessment                 | 2108c0c84ca6998eb157e1efd4d894746e1fdf8b_id    |
    | schools                     | school                     | 92d6d5a0-852c-45f4-907a-912752831772    |
    | students                    | student                    | 74cf790e-84c4-4322-84b8-fca7206f1085_id |
    | studentSectionAssociations  | studentSectionAssociation  | 706ee3be-0dae-4e98-9525-f564e05aa388_idbac890d6-b580-4d9d-a0d4-8bce4e8d351a_id |
    | courseOfferings             | courseOffering             | dc7df8c7-a5f1-48df-9e51-4c45afcc149f    |

  Scenario Outline: Getting response from GET - Read all
  
	#ES bug, set this back to zero after bug fix
    Given parameter "limit" is "250"
    When I navigate to GET "/v1/<ENTITY URI>"
    Then I should receive a return code of 200
    And I should receive an XML document
    And I should receive <ENTITY COUNT> entities
    And I should see each entity's "<entityType>" is "<ENTITY TYPE>"

  Examples:
    | ENTITY URI                  | ENTITY TYPE                | ENTITY COUNT    |
    | assessments                 | assessment                 | 17              |
    | schools                     | school                     | 1               |
    | students                    | student                    | 31              |
    | studentSectionAssociations  | studentSectionAssociation  | 31              |
    | courseOfferings             | courseOffering             | 138             |

  Scenario: Getting response from POST - Create (school)
    Given a valid XML document for a new school entity
    When I POST the entity to "/v1/schools"
    Then I should receive a return code of 403

  Scenario: Getting response from PUT - Update (school)
    When I navigate to GET "/v1/schools/<SCHOOL ENTITY TO BE UPDATED>"
    Then I should see "<nameOfInstitution>" is "Sunset Central High School"
    When I change the name to "Updated School Name"
    And I try to PUT the entity to "/v1/schools/<SCHOOL ENTITY TO BE UPDATED>"
    Then I should receive a return code of 403


Scenario: Applying optional fields
  Given optional field "attendances"
  And optional field "assessments"
  And optional field "gradebook"
  And optional field "transcript"
  And parameter "limit" is "0"
  When I navigate to GET "/v1/sections/<LINDA KIM SECTION ID>/studentSectionAssociations/students"
  Then I should receive an XML document
  And I should receive a return code of 200
  And I should receive 1 records
  Then when I look at the student "Marvin" "Miller"

  # attendances
  Then I should find 181 "<attendances>" under "<attendances>"
  And I should see "<date>" is "2011-09-07" for one of them
  And I should see "<event>" is "In Attendance" for it

  # assessments
  Then I should find 1 "<studentAssessments>"
  And I should see "<entityType>" is "studentAssessment" for one of them
  And I should see "<studentId>" is "<MARVIN MILLER STUDENT ID>" for it
  And I should find "<assessments>" under it
  And I should see "<entityType>" is "assessment" for it
  And I should see "<gradeLevelAssessed>" is "Twelfth grade" for it
  And I should find 3 "<objectiveAssessment>" under it
  Then I should find 1 "<studentAssessments>"
  And I should see "<studentId>" is "<MARVIN MILLER STUDENT ID>" for one of them
  And I should find 3 "<studentObjectiveAssessments>" under it
  And I should see "<objectiveAssessment><identificationCode>" is "SAT-Writing" for one of them
  And I should find 2 "<scoreResults>" under it
  And I should see "<result>" is "80" for one of them

  # gradebook
  Then I should find 3 "<studentGradebookEntries>"
  And I should see "<dateFulfilled>" is "2012-01-31" for one of them
  And I should see "<entityType>" is "studentGradebookEntry" for it
  And I should see "<letterGradeEarned>" is "A" for it
  And I should find "<gradebookEntries>" under it
  And I should see "<entityType>" is "gradebookEntry" for it
  And I should see "<dateAssigned>" is "2012-01-31" for it

  # transcript
  Then I should find 1 "<courseTranscripts>" under "<transcript>"
  And I should see "<studentId>" is "<MARVIN MILLER STUDENT ID>" for one of them
  And I should see "<entityType>" is "courseTranscript" for it
  And I should see "<finalLetterGradeEarned>" is "B" for it
  And I should find 1 "<studentSectionAssociations>" under "<transcript>"
  And I should see "<sectionId>" is "<LINDA KIM SECTION ID>" for one of them
  And I should find "<sections>" under it
  And I should see "<entityType>" is "section" for it
  And I should find "<sessions>" under it
  And I should see "<entityType>" is "session" for it
  Then I should find 1 "<studentSectionAssociations>" under "<transcript>"
  And I should see "<sectionId>" is "<LINDA KIM SECTION ID>" for one of them
  And I should find "<sections>" under it
  And I should find "<courses>" under it
  And I should see "<entityType>" is "course" for it

Scenario: Applying optional fields - single student view
  Given optional field "attendances"
  And optional field "assessments"
  And optional field "gradebook"
  And optional field "transcript"
  And parameter "limit" is "0"
  When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
  Then I should receive an XML document
  And I should receive a return code of 200

  # attendances
  Then I should find 181 "<attendances>" under "<attendances>"
  And I should see "<date>" is "2011-09-07" for one of them
  And I should see "<event>" is "In Attendance" for it

  # assessments
  Then I should find 1 "<studentAssessments>"
  And I should see "<entityType>" is "studentAssessment" for one of them
  And I should see "<studentId>" is "<MARVIN MILLER STUDENT ID>" for it
  And I should find "<assessments>" under it
  And I should see "<entityType>" is "assessment" for it
  And I should see "<gradeLevelAssessed>" is "Twelfth grade" for it
  And I should find 3 "<objectiveAssessment>" under it
  Then I should find 1 "<studentAssessments>"
  And I should see "<studentId>" is "<MARVIN MILLER STUDENT ID>" for one of them
  And I should find 3 "<studentObjectiveAssessments>" under it
  And I should see "<objectiveAssessment><identificationCode>" is "SAT-Writing" for one of them
  And I should find 2 "<scoreResults>" under it
  And I should see "<result>" is "80" for one of them

  # gradebook
  Then I should find 3 "<studentGradebookEntries>"
  And I should see "<dateFulfilled>" is "2012-01-31" for one of them
  And I should see "<entityType>" is "studentGradebookEntry" for it
  And I should see "<letterGradeEarned>" is "A" for it
  And I should find "<gradebookEntries>" under it
  And I should see "<entityType>" is "gradebookEntry" for it
  And I should see "<dateAssigned>" is "2012-01-31" for it

  # transcript
  Then I should find 1 "<courseTranscripts>" under "<transcript>"
  And I should see "<studentId>" is "<MARVIN MILLER STUDENT ID>" for one of them
  And I should see "<entityType>" is "courseTranscript" for it
  And I should see "<finalLetterGradeEarned>" is "B" for it
  And I should find 1 "<studentSectionAssociations>" under "<transcript>"
  And I should see "<sectionId>" is "<LINDA KIM SECTION ID>" for one of them
  And I should find "<sections>" under it
  And I should see "<entityType>" is "section" for it
  And I should find "<sessions>" under it
  And I should see "<entityType>" is "session" for it
  Then I should find 1 "<studentSectionAssociations>" under "<transcript>"
  And I should see "<sectionId>" is "<LINDA KIM SECTION ID>" for one of them
  And I should find "<sections>" under it
  And I should find "<courses>" under it
  And I should see "<entityType>" is "course" for it
