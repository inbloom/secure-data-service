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
	| "/v1/parents/45e42971-ce8e-4dff-98cf-66535887760a"             			|
	| "/v1/grades/ef42e2a2-9942-11e1-a8a9-68a86d21d918"            				|
	| "/v1/studentSectionGradebookEntries/ab3b4a69-5434-f906-e51b-d63ef970ef8f" |
	| "/v1/sections/a00c740c-8753-4190-90ad-9fc026a65d53" 						|
	| "/v1/studentSectionAssociations/82f7d7ff-a2b1-4e05-896a-48367f497b89" 	|
	| "/v1/courses/90d4743e-824b-49e2-8671-9d537c2ad0a1" 						|
	| "/v1/sessions/9499b9ef-640f-4103-8859-bb6d23b6b566" 						|
	
