@RALLY_US5074

Feature: List-Attendance endpoint should return a list of all student attendances

  Scenario Outline: Check that I can get all attendances for a school
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    When I navigate to <url> with <id>
    Then I should receive a valid return code
    And I can see all the attendances in a school
  Examples:
    | url                                                                         | id                                     |
    | "/schools/#{id}/studentSchoolAssociations/students/attendances"             | "92d6d5a0-852c-45f4-907a-912752831772" |
# TODO: Fix this - offset makes API throw out of bound exception.`
#    | "/schools/#{id}/studentSchoolAssociations/students/attendances?offset=50"   | "92d6d5a0-852c-45f4-907a-912752831772" |
