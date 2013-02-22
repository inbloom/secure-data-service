@RALLY_US209
@RALLY_US210
@RALLY_DE87
Feature: As an SLI application, I want to be able to apply optional fields to student entities.
  This means I want to be able to expand endpoints by applying optional fields to the url.

  Scenario Outline: Applying optional fields - attendances, assessments, gradebook
    Given I am logged in using "<username>" "<password>" to realm "IL"
    And format "application/vnd.slc+json"
    Given optional field "assessments"
    And optional field "gradebook"
    And parameter "limit" is "0"
    When I navigate to GET "/v1/sections/<SECTION ID>/studentSectionAssociations/students"
    Then I should receive a return code of 200
    And I should receive a collection of "1" entities
    And I should see "id" is "<STUDENT_ID>" in one of them

    # Assessments
    And I should find "1" "studentAssessments"
    And I should find "assessments" expanded in each of them
    Then I should see "administrationDate" is "2011-05-10" in one of them
    And inside "assessments"
    And I should see "academicSubject" is "Reading" in it
    And I should see "assessmentTitle" is "SAT 2" in it
    And I should see "entityType" is "assessment" in it
    And I should find "3" "objectiveAssessment" in it
    Then I should see "identificationCode" is "SAT-Writing" in one of them
    And I should see "maxRawScore" is "800" in it
    And I should see "percentOfAssessment" is "33" in it
    When I go back up one level
    Then I should find "3" "studentObjectiveAssessments" in it
    Then I should see "objectiveAssessment.identificationCode" is "SAT-Writing" in one of them
    Then I should find "2" "scoreResults" in it
    Then I should see "assessmentReportingMethod" is "Scale score" in one of them
    And I should see "result" is "680" in it

    # Gradebook Entries
    And I should find "3" "studentGradebookEntries"
    And I should find "gradebookEntries" expanded in each of them
    Then I should see "dateFulfilled" is "2012-01-31" in one of them
    And inside "gradebookEntries"
    And I should see "dateAssigned" is "2012-01-31" in it
    And I should see "gradebookEntryType" is "Quiz" in it
    And I should see "entityType" is "gradebookEntry" in it
    Examples:
      | username               | password            |
      | linda.kim              | linda.kim1234       |
      | rrogers                | rrogers1234         |

  Scenario Outline: Applying optional fields - transcript - studentSectionAssociations
    Given I am logged in using "<username>" "<password>" to realm "IL"
    And format "application/vnd.slc+json"
    Given optional field "transcript"
    When I navigate to GET "/v1/sections/<SECTION ID>/studentSectionAssociations/students"
    Then I should receive a return code of 200
    And I should receive a collection of "1" entities
    And I should see "id" is "<STUDENT_ID>" in one of them

    # Transcript
    And inside "transcript"
    And I should find "1" "studentSectionAssociations" in it
    And I should find one with the property "id" having the value "<STUDENT SECTION ASSOC ID>"
    When I go into the item with the property "id" having the value "<STUDENT SECTION ASSOC ID>"
    Then I should find "sections" expanded in it
    And inside "sections"
    And I should see "courseOfferingId" is "<COURSE OFFERING ID>" in it
    And I should see "schoolId" is "<SCHOOL ID>" in it
    And I should see "uniqueSectionCode" is "8th Grade English - Sec 5" in it
    And inside "sessions"
    And I should see "sessionName" is "Spring 2012" in it
    When I go back up one level
    Then inside "courses"
    And I should see "courseDescription" is "Intro to Russian" in it
    Examples:
      | username               | password            |
      | linda.kim              | linda.kim1234       |
      | rrogers                | rrogers1234         |

  Scenario Outline: Applying optional fields - transcript - courseTranscripts
    Given I am logged in using "<username>" "<password>" to realm "IL"
    And format "application/vnd.slc+json"
    Given optional field "transcript"
    When I navigate to GET "/v1/sections/<SECTION ID>/studentSectionAssociations/students"
    Then I should receive a return code of 200
    And I should receive a collection of "1" entities
    And I should see "id" is "<STUDENT_ID>" in one of them
    
    And inside "transcript"
    And I should find "1" "courseTranscripts" in it
    And I should see "finalLetterGradeEarned" is "B" in one of them
    Examples:
      | username               | password            |
      | linda.kim              | linda.kim1234       |
      | rrogers                | rrogers1234         |
    
  Scenario Outline: Test that both optionalFields and views work equally
    Given I am logged in using "<username>" "<password>" to realm "IL"
    And format "application/vnd.slc+json"
    And parameter "optionalFields" is "transcript"
    When I navigate to GET "/v1/sections/<SECTION ID>/studentSectionAssociations/students"
    Then I should receive a return code of 200
    And I should receive a collection of "1" entities
    And I should see "id" is "<STUDENT_ID>" in one of them
    
    And inside "transcript"
    And I should find "1" "courseTranscripts" in it
    And I should see "finalLetterGradeEarned" is "B" in one of them
    Examples:
      | username               | password            |
      | linda.kim              | linda.kim1234       |
      | rrogers                | rrogers1234         |
