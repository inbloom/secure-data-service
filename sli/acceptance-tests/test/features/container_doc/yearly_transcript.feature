Feature: As an SLI API, I want to support a yearly transcript container document.
  This means Grade, ReportCard and StudentAcademicRecord entities are sub-doc'ed are sub-doc'ed in a yearlyTranscript
  container document bucketized by studentId and schoolYear.

Background: Logged in as IT administrator Rick Rogers
  Given I am logged in using "rrogers" "somepassword" to realm "IL"
  And format "application/vnd.slc+json"

  Scenario: CRUD operations for Grade, ReportCard amd StudentAcademicRecord
    Given I have valid json documents for "grade,reportCard,studentAcademicRecord"
    Then I should be able to perform CRUD on them

  Scenario: Check if entities are sub-doc'ed correctly
    Given I have valid json documents for "grade,reportCard,studentAcademicRecord"
    And I POST them
    Then the documents should be sub-doc'ed in a "yearlyTranscript" document
    And I should DELETE them
