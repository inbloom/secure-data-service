@RALLY_US209
@RALLY_US210
Feature: As an SLI application, I want to be able to view more student data in a single API call.
  This means I want to be able to apply optional fields to the url.

  Background: Nothing yet
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And format "application/vnd.slc+json"

  Scenario: Applying optional fields - attendances, assessments, gradebook
    Given optional field "attendances"
    And optional field "assessments"
    And optional field "gradebook"
    When I navigate to GET "/v1/students/<STUDENT_ID>"
    Then I should receive a return code of 200

  # Attendaces
    And I should find "1" "attendances"
    And I should find "181" "attendances" in it
    When I look at the first one
    Then I should see "date" is "2011-09-06" in it
    And I should see "event" is "In Attendance" in it

  # Assessments
    And I should find "1" "studentAssessments"
    And I should find "assessments" expanded in each of them
    When I look at the first one
    Then I should see "administrationDate" is "2011-05-10" in it
    And inside "assessments"
    And I should see "academicSubject" is "Reading" in it
    And I should see "assessmentTitle" is "SAT" in it
    And I should see "entityType" is "assessment" in it
    And I should find "3" "objectiveAssessment" in it
    When I look at the first one
    Then I should see "identificationCode" is "SAT-Writing" in it
    And I should see "maxRawScore" is "800" in it
    And I should see "percentOfAssessment" is "33" in it
    When I go back up one level
    Then I should find "3" "studentObjectiveAssessments" in it
    When I look at the first one
    Then I should find "2" "scoreResults" in it
    When I look at the first one
    Then I should see "assessmentReportingMethod" is "Scale score" in it
    And I should see "result" is "680" in it

  # Gradebook Entries
    And I should find "3" "studentGradebookEntries"
    And I should find "gradebookEntries" expanded in each of them
    When I look at the first one
    Then I should see "letterGradeEarned" is "A" in it
    And inside "gradebookEntries"
    And I should see "dateAssigned" is "2012-02-07" in it
    And I should see "gradebookEntryType" is "Quiz" in it
    And I should see "entityType" is "gradebookEntry" in it


  Scenario: Applying optional fields - transcript - studentSectionAssociations
    Given optional field "transcript"
    When I navigate to GET "/v1/students/<STUDENT_ID>"
    Then I should receive a return code of 200

  # Transcript
    And inside "transcript"
    And I should find "2" "studentSectionAssociations" in it
    And I should find "sections" expanded in each of them
    When I look at the first one
    Then I should see "id" is "<STUDENT SECTION ASSOC ID>" in it
    And inside "sections"
    And I should see "courseId" is "<COURSE ID>" in it
    And I should see "schoolId" is "<SCHOOL ID>" in it
    And I should see "uniqueSectionCode" is "algebraIIS11" in it
    And inside "sessions"
    And I should see "sessionName" is "Fall 2011" in it
    When I go back up one level
    Then inside "courses"
    And I should see "courseDescription" is "Intro to French" in it

  Scenario: Applying optional fields - transcript - courseTranscripts
    Given optional field "transcript"
    When I navigate to GET "/v1/students/<STUDENT_ID>"
    Then I should receive a return code of 200

    And inside "transcript"
    And I should find "1" "courseTranscripts" in it
    When I look at the first one
    And I should see "finalLetterGradeEarned" is "B" in it