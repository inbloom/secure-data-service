@smoke @RALLY_DE842
Feature: Acceptance test cases proving access to student or no access to student determines access to related student data.

Scenario Outline: Validate access to student related data based on accessor's context.
	
    Given I am logged in using "cgray" "cgray1234" to realm "IL"
    And I navigate to GET <URI OF CONTEXT BASED ENTITY>
    Then I should receive a return code of 200
    Given I am logged in using "stweed" "token-only-no-password" to realm "IL"
    And I navigate to GET <URI OF CONTEXT BASED ENTITY>
    Then I should receive a return code of 403
    Given I am logged in using "sbantu" "sbantu1234" to realm "IL"
    And I navigate to GET <URI OF CONTEXT BASED ENTITY>
    Then I should receive a return code of 200
	Given I am logged in using "akopel" "akopel1234" to realm "IL"
    And I navigate to GET <URI OF CONTEXT BASED ENTITY>
    Then I should receive a return code of 403
    Examples:
    | URI OF CONTEXT BASED ENTITY                                                       |
    | "/v1/studentAcademicRecords/7a70c01bf8d93d9b1f53ab45080777b0b49794fa_id56afc8d4-6c91-48f9-8a11-de527c1131b7"                 |
    | "/v1/attendances/4beb72d4-0f76-4071-92b4-61982dba7a7b"                            |
    | "/v1/courseTranscripts/36aeeabf-ee9b-46e6-8039-13320bf12346"                      |
    | "/v1/studentAssessments/e5e13e61-01aa-066b-efe0-710f7a011115_id"                     |
    | "/v1/disciplineActions/9e26de6c-225b-9f67-9201-8113ad50a03b"                      |
    | "/v1/studentDisciplineIncidentAssociations/0c2756fd-6a30-4010-af79-488d6ef2735a_id20120613-8d5a-c796-76e3-d77d5d497e6c_id"  |
    | "/v1/disciplineIncidents/0e26de79-7efa-5e67-9201-5113ad50a03b"                    |
    | "/v1/reportCards/7a70c01bf8d93d9b1f53ab45080777b0b49794fa_id20120613-fd6c-4d3a-8a84-97bd8dd829b7"                            |
    | "/v1/studentGradebookEntries/20120613-5434-f906-e51b-d63ef970ef8f"                |
    | "/v1/gradebookEntries/15ab6363-5509-470c-8b59-4f289c224107_ide49dc00c-182d-4f22-7919-201211130004_id"                       |
    | "/v1/studentParentAssociations/0c2756fd-6a30-4010-af79-488d6ef2735a_idc5aa1969-492a-5150-8479-71bfc4d87984_id"              |
    | "/v1/parents/9b8f7237-ce8e-4dff-98cf-66535880987b"                                |
    | "/v1/studentSchoolAssociations/9cd50bcb-b39a-4d8a-b866-8766c79d6965"              |
    | "/v1/studentCohortAssociations/9ac7ad37-80aa-42ab-9d63-e48cc70a7863_iddd5e5b41-30fb-40e5-a968-afe7ae32fce3_id"              |
    | "/v1/studentProgramAssociations/9b8cafdc-8fd5-11e1-86ec-0021701f543f_idb3f55084-8fd5-11e1-86ec-0021701f543f_id"             |
    | "/v1/studentCompetencies/3a2ea9f8-9acf-11e1-add5-68a86d83461b"                    |
    | "/v1/grades/78501562270ca2938d56f793547bc3fc2c0b56e6_idef42e2a2-9942-11e1-a8a9-68a86d21d918"                                 |
    | "/v1/studentSectionAssociations/a00c740c-8753-4190-90ad-9fc026a65d53_id82f7d7ff-a2b1-4e05-896a-48367f497b89_id"             |

