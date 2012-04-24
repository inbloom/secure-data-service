Feature: As an SLI application, I want to be able to support XML.
  This means the API should support POST, PUT and GET with XML format.

Background: Nothing yet
  Given I am logged in using "demo" "demo1234" to realm "SLI"
  And format "application/xml"

Scenario Outline: Getting response from GET - Read
  When I navigate to GET "/v1/<ENTITY URI>/<ENTITY ID>"
  Then I should receive a return code of 200
  And I should receive an XML document
  And I should see "<entityType>" is "<ENTITY TYPE>"

Examples:
  | ENTITY URI                  | ENTITY TYPE                | ENTITY ID                               |
  | assessments                 | assessment                 | 6c572483-fe75-421c-9588-d82f1f5f3af5    |
  | schools                     | school                     | eb3b8c35-f582-df23-e406-6947249a19f2    |
  | students                    | student                    | 714c1304-8a04-4e23-b043-4ad80eb60992    |
  | studentSectionAssociations  | studentSectionAssociation  | 4efb4b14-bc49-f388-0000-0000c9355702    |
  | courseOfferings   			| sessionCourseAssociation   | 9ff65bb1-ef8b-4588-83af-d58f39c1bf68    |

Scenario Outline: Getting response from GET - Read all
  Given parameter "limit" is "0"
  When I navigate to GET "/v1/<ENTITY URI>"
  Then I should receive a return code of 200
  And I should receive an XML document
  And I should receive <ENTITY COUNT> entities
  And I should see each entity's "<entityType>" is "<ENTITY TYPE>"

Examples:
  | ENTITY URI                  | ENTITY TYPE                | ENTITY COUNT    |
  | assessments                 | assessment                 | 16              |
  | schools                     | school                     | 32              |
  | students                    | student                    | 223             |
  | studentSectionAssociations  | studentSectionAssociation  | 309             |
  | courseOfferings   			| sessionCourseAssociation   | 4               |

Scenario: Applying optional fields
  Given optional field "attendances"
  And optional field "assessments"
  And optional field "gradebook"
  And optional field "transcript"
  And parameter "limit" is "0"
  When I navigate to GET "/v1/sections/<LINDA KIM SECTION ID>/studentSectionAssociations/students"
  Then I should receive an XML document
  And I should receive a return code of 200

  # attendances
  Then I should find "<attendances>" under "<student>"
  And I should find 74 "<attendances>" under "<student><attendances>"
  And I should find 0 entries with "<eventDate>" including the string "2011"
  And I should see "<entityType>" is "attendance" for the one at position 2
  And I should see "<studentId>" is "<MARVIN MILLER STUDENT ID>" for the one at position 2
  And I should see "<attendanceEventCategory>" is "Excused Absence" for the one at position 2

  # assessments
  Then I should find "<studentAssessmentAssociations>" under "<student>"
  And I should see "<entityType>" is "studentAssessmentAssociation"
  And I should see "<studentId>" is "<MARVIN MILLER STUDENT ID>"
  And I should find "<assessments>" under "<student><studentAssessmentAssociations>"
  And I should see "<entityType>" is "assessment"
  And I should see "<gradeLevelAssessed>" is "Twelfth grade"
  And I should find 3 "<studentObjectiveAssessments>" under "<student><studentAssessmentAssociations>"
  And I should find 2 "<scoreResults>" under "<student><studentAssessmentAssociations><studentObjectiveAssessments>"
  And I should see "<result>" is "80" for the one at position 2

  # gradebook
  Then I should find 3 "<studentSectionGradebookEntries>" under "<student>"
  And I should see "<entityType>" is "studentSectionGradebookEntry" for the one at position 1
  And I should see "<letterGradeEarned>" is "A" for the one at position 1
  And I should see "<dateFulfilled>" is "2012-01-31" for the one at position 1
  And I should find 1 "<gradebookEntries>" under "<student><studentSectionGradebookEntries>"
  And I should see "<entityType>" is "gradebookEntry" for the one at position 1
  And I should see "<dateAssigned>" is "2012-01-31" for the one at position 1

  # transcript
  Then I should find "<transcript>" under "<student>"
  And I should find "<studentTranscriptAssociations>" under "<student><transcript>"
  And I should see "<entityType>" is "studentTranscriptAssociation"
  And I should see "<finalLetterGradeEarned>" is "B"
  And I should find 2 "<studentSectionAssociations>" under "<student><transcript>"
  And I should see "<entityType>" is "studentSectionAssociation" for the one at position 1
  And I should find "<sections>" under "<student><transcript><studentSectionAssociations>"
  And I should see "<entityType>" is "section"
  And I should find "<sessions>" under "<student><transcript><studentSectionAssociations><sections>"
  And I should see "<entityType>" is "session"
  And I should find "<courses>" under "<student><transcript><studentSectionAssociations><sections>"
  And I should see "<entityType>" is "course"

Scenario: Applying optional fields - attendances with year filter
  Given optional field "attendances.1"
  And parameter "limit" is "0"
  When I navigate to GET "/v1/sections/<LINDA KIM SECTION ID>/studentSectionAssociations/students"
  Then I should receive an XML document
  And I should receive a return code of 200

  Then I should find 161 "<attendances>" under "<student><attendances>"
  And I should find 87 entries with "<eventDate>" including the string "2011"