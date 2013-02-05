@RALLY_DE87
@RALLY_US209
@RALLY_US210
Feature: As an SLI application, I want to be able to support XML.
  This means the API should support POST, PUT and GET with XML format.

Background: Nothing yet
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
  And format "application/xml"

  Scenario Outline: Getting response from GET - Read
    When I navigate to GET "/v1/<ENTITY URI>/<ENTITY ID>"
    Then I should receive a return code of 200
    And I should receive an XML document
    And I should see "<entityType>" is "<ENTITY TYPE>"

  Examples:
    | ENTITY URI                  | ENTITY TYPE                | ENTITY ID                               |
    | assessments                 | assessment                 | dd916592-7d7e-5d27-a87d-dfc7fcb757f6    |
    | schools                     | school                     | 92d6d5a0-852c-45f4-907a-912752831772    |
    | students                    | student                    | 3a8860f9-ffb1-40f2-89b4-84f3ba369204_id    |
    | studentSectionAssociations  | studentSectionAssociation  | 8ed12459-eae5-49bc-8b6b-6ebe1a56384f_id4ae72560-3518-4576-a35e-a9607668c9ad_id    |
    | courseOfferings             | courseOffering             | c5b80f7d-93c5-11e1-adcc-101f74582c4c    |

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
    | schools                     | school                     | 0               |
    | students                    | student                    | 0               |
    | studentSectionAssociations  | studentSectionAssociation  | 0               |
    | courseOfferings             | courseOffering             | 138             |

Scenario: Getting response from POST - Create (school)
  Given a valid XML document for a new school entity
  When I POST the entity to "/v1/schools"
  Then I should receive a return code of 201
  And I should receive an ID for the newly created entity
  When I navigate to GET "/v1/schools/<NEWLY CREATED ENTITY ID>"
  Then I should receive a return code of 200
  And I should see "<nameOfInstitution>" is "Apple Alternative Elementary School"
  And I should see "<organizationCategories>" is "School"
  And I should find 1 "<address>"
  And I should see "<streetNumberName>" is "123 Main Street" for one of them
  And I should find 4 "<gradesOffered>"

Scenario: Getting response from PUT - Update (school)
  When I navigate to GET "/v1/schools/<SCHOOL ENTITY TO BE UPDATED>"
  Then I should see "<nameOfInstitution>" is "Sunset Central High School"
  When I change the name to "Updated School Name"
  And I PUT the entity to "/v1/schools/<SCHOOL ENTITY TO BE UPDATED>"
  Then I should receive a return code of 204
  When I navigate to GET "/v1/schools/<SCHOOL ENTITY TO BE UPDATED>"
  Then I should receive an XML document
  Then I should see "<nameOfInstitution>" is "Updated School Name"

  Scenario: Getting response from PUT - revert updated school name (school)
    When I navigate to GET "/v1/schools/<SCHOOL ENTITY TO BE UPDATED>"
    Then I should see "<nameOfInstitution>" is "Updated School Name"
    When I change the name to "Sunset Central High School"
    And I PUT the entity to "/v1/schools/<SCHOOL ENTITY TO BE UPDATED>"
    Then I should receive a return code of 204
    When I navigate to GET "/v1/schools/<SCHOOL ENTITY TO BE UPDATED>"
    Then I should receive an XML document
    Then I should see "<nameOfInstitution>" is "Sunset Central High School"

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
