@RALLY_US5074

Feature: List-Attendance endpoint should return a list of all student attendances

  Scenario Outline: Check that I can get all attendances for a school
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    When I navigate to <url> with <id>
    Then the response status should be 200 OK
    And I can see all the attendances in a school
  Examples:
    | url                                                                         | id                                     |
    | "/schools/#{id}/studentSchoolAssociations/students/attendances"             | "92d6d5a0-852c-45f4-907a-912752831772" |
    | "/schools/#{id}/studentSchoolAssociations/students/attendances?offset=2"   | "92d6d5a0-852c-45f4-907a-912752831772" |
