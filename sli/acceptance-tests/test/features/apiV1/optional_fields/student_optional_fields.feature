Feature: As an SLI application, I want to be able to apply optional fields to student entities.
  This means I want to be able to expand endpoints by applying optional fields to the url.

  Background: Nothing yet
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And format "application/vnd.slc+json"

  Scenario: Applying optional fields
    Given optional field "attendances"
    And optional field "assessments"
    And optional field "gradebook"
    When I navigate to GET "/v1/sections/<SECTION ID>/studentSectionAssociations/students"
    Then I should receive a return code of 200

    # Attendaces
    And I should find "attendances" in "attendances"
    And I should see "In Attendance" is "200" in it
    And I should see "Excused Absence" is "22" in it
    And I should see "Total" is "222" in it

    #Assessments
    And I should find "1" "studentAssessmentAssociations"
    And I should find "assessments" in each of them
    And I look at the first one
    And I should see "administrationDate" is "2011-05-10" in it
    And inside "assessments"
    And I should see "academicSubject" is "Reading" in it
    And I should see "assessmentTitle" is "SAT" in it
    And I should see "entityType" is "assessment" in it

    #Gradebook Entries
    And I should find "3" "studentSectionGradebookEntries"
    And I should find "gradebookEntries" in each of them
    And I look at the first one
    And I should see "letterGradeEarned" is "A" in it
    And inside "gradebookEntries"
    And I should see "dateAssigned" is "2012-01-31" in it
    And I should see "gradebookEntryType" is "Quiz" in it
    And I should see "entityType" is "gradebookEntry" in it