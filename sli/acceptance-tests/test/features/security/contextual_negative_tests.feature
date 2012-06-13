Feature: Acceptance test cases proving access to student or no access to student determines access to related student data.

Scenario Outline: Validate access to student related data based on accessor's context.
	
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    And I navigate to GET <URI OF CONTEXT BASED ENTITY>
    Then I should receive a return code of 200
    Given I am logged in using "stweed" "token-only-no-password" to realm "IL"
    And I navigate to GET <URI OF CONTEXT BASED ENTITY>
    Then I should receive a return code of 403
    Examples:
    | URI OF CONTEXT BASED ENTITY                                               |
    | "/v1/studentAcademicRecords/56afc8d4-6c91-48f9-8a11-de527c1131b7"         |
    | "/v1/attendances/4beb72d4-0f76-4071-92b4-61982dba7a7b"                    |
    | "/v1/courseTranscripts/36aeeabf-ee9b-46e6-8039-13320bf15226"              |
    | "/v1/studentAssessments/e5e13e61-01aa-066b-efe0-710f7a011115"             |
	
