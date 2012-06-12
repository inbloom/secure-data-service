Feature: Acceptance test cases proving access to student or no access to student determines access to related student data.

Scenario Outline: Expected
	
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    And I navigate to GET <URI OF CONTEXT BASED ENTITY>
    Then I should receive a return code of 200
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And I navigate to GET <URI OF CONTEXT BASED ENTITY>
    Then I should receive a return code of 403
    Examples:
    | URI OF CONTEXT BASED ENTITY                                               |
#   | "/v1/studentSectionGradebookEntries/AAAAAAAA-BBBB-CCCC-DDDD-EEEEEEEEEEEE" |
	
