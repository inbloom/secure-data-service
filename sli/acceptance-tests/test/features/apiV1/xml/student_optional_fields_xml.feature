Feature: As an SLI application, I want to be able to apply optional fields to student entities.
  This means I want to be able to expand endpoints by applying optional fields to the url.

  Background: Nothing yet
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And format "application/xml"

  Scenario: Applying optional fields - attendances
    Given optional field "attendances"    
    And parameter "limit" is "0"
    When I navigate to GET "/v1/sections/<SECTION ID>/studentSectionAssociations/students"
    Then I should receive a return code of 200
    And I should receive an XML document

    # Attendaces
    And I should find "74" "attendances"
    And I should see the year "2011" in none of the attendance entries
    And I should see the year "2012" in some of the attendance entries
    When I look at the first one
    Then I should see "eventDate" is "2012-01-26" in it
    And I should see "attendanceEventType" is "Daily Attendance" in it
    And I should see "entityType" is "attendance" in it
    And I should see "studentId" is "<STUDENT_ID>" in it

   