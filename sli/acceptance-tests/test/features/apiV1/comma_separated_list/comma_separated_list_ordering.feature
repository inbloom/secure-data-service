@RALLY_DE955
Feature: As an SLI application, I want to return the right order of entities.
  That means when I do a GET on comma-separated entities, the order of the entities should be preserved.

  Background: Format is JSON
    Given format "application/json"

  Scenario Outline: Doing a GET request on 3 comma-separated, all valid student IDs
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    Given the order of students I want is <STUDENT IDs ORDER>
    When I navigate to GET "/v1/students/<STUDENT IDs LIST>"
    Then I should receive a return code of 200
    And the response at position 0 should include the information <STRING 1>
    And the response at position 1 should include the information <STRING 2>
    And the response at position 2 should include the information <STRING 3>

  Examples:
    | STUDENT IDs ORDER                                         | STRING 1           | STRING 2           | STRING 3           |
    | <MATT SOLLARS ID>,<CARMEN ORTIZ ID>,<MARVIN MILLER ID>    | <MATT SOLLARS ID>  | <CARMEN ORTIZ ID>  | <MARVIN MILLER ID> |
    | <MATT SOLLARS ID>,<MARVIN MILLER ID>,<CARMEN ORTIZ ID>    | <MATT SOLLARS ID>  | <MARVIN MILLER ID> | <CARMEN ORTIZ ID>  |
    | <CARMEN ORTIZ ID>,<MATT SOLLARS ID>,<MARVIN MILLER ID>    | <CARMEN ORTIZ ID>  | <MATT SOLLARS ID>  | <MARVIN MILLER ID> |
    | <CARMEN ORTIZ ID>,<MARVIN MILLER ID>,<MATT SOLLARS ID>    | <CARMEN ORTIZ ID>  | <MARVIN MILLER ID> | <MATT SOLLARS ID>  |
    | <MARVIN MILLER ID>,<CARMEN ORTIZ ID>,<MATT SOLLARS ID>    | <MARVIN MILLER ID> | <CARMEN ORTIZ ID>  | <MATT SOLLARS ID>  |
    | <MARVIN MILLER ID>,<MATT SOLLARS ID>,<CARMEN ORTIZ ID>    | <MARVIN MILLER ID> | <MATT SOLLARS ID>  | <CARMEN ORTIZ ID>  |

  Scenario Outline: Doing a GET request on 3 comma-separated, some invalid student IDs, some I cannot see
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    Given the order of students I want is <STUDENT IDs ORDER>
    When I navigate to GET "/v1/students/<STUDENT IDs LIST>"
    Then I should receive a return code of <CODE>

# Current behavior:
# If any of the IDs doesn't exist, return a 404
# Otherwise if any of the IDs aren't accessable, return 403
# Otherwise 200
  Examples:
    | STUDENT IDs ORDER                                         | CODE |
    | <MARVIN MILLER ID>,<MARVIN MILLER ID>,<MARVIN MILLER ID>  | 200  |
    | <INVALID ID>,<MARVIN MILLER ID>,<MARVIN MILLER ID>        | 404  |
    | <MARVIN MILLER ID>,<MARVIN MILLER ID>,<INACCESSABLE ID>   | 403  |
    | <MARVIN MILLER ID>,<INVALID ID>,<INACCESSABLE ID>         | 404  |

 Scenario Outline: Validate CSL for each endpoint
 	    Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
 	    When I navigate to GET "/v1/<ENDPOINT>/<ID1>"
 	    Then I should receive a return code of 200
 	    When I navigate to GET "/v1/<ENDPOINT>/<ID2>"
 	    Then I should receive a return code of 200
 	    When I navigate to GET "/v1/<ENDPOINT>/<ID1>,<ID2>"
 	    Then I should receive a return code of 200
 	    And I should see a total of 2 entities
 	    Examples:
 	    | ENDPOINT                  | ID1                                | ID2                                |
 	    |assessments                |b94b5194d45cd707465627c0cd6c4f68f3558600_id|c607aa848f00b4efa6bfb95fbe78a00338377f16_id|
 	    |attendances                |530f0704-c240-4ed9-0a64-55c0308f91ee|4beb72d4-0f76-4071-92b4-61982dba7a7b|
 	    |cohorts                    |b40926af-8fd5-11e1-86ec-0021701f543f_id|b408d88e-8fd5-11e1-86ec-0021701f543f_id|
 	    |courses                    |f9d960e4-682b-4ebe-96d8-c4c2fc803435|43ee8275-de7b-4a02-8ecb-21d25a45db36|
 	    |disciplineActions          |db7f1d4b-9689-b2f4-9281-d88d65999423|9e26de6c-225b-9f67-9201-8113ad50a03b|
 	    |disciplineIncidents        |0e26de79-7efa-5e67-9201-5113ad50a03b|0e26de79-22ea-5d67-9201-5113ad50a03b|
 	    |educationOrganizations     |a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb|92d6d5a0-852c-45f4-907a-912752831772|
 	    |gradebookEntries           |0dbb262b-8a3e-4a7b-82f9-72de95903d91_id20120613-56b6-4d17-847b-2997b7227686_id|706ee3be-0dae-4e98-9525-f564e05aa388_id008fd89d-88a2-43aa-8af1-74ac16a29380_id|
 	    |grades                     |708c4e08-9942-11e1-a8a9-68a86d21d918|ef42e2a2-9942-11e1-a8a9-68a86d21d918|
 	    |gradingPeriods             |ef72b883-90fa-40fa-afc2-4cb1ae17623b|b40a7eb5-dd74-4666-a5b9-5c3f4425f130|
 	    |learningObjectives         |df9165f2-653e-df27-a86c-bfc5f4b7577d|df9165f2-65fe-de27-a82c-bfc5f4b7577c|
 	    |learningStandards          |dd9165f2-65fe-7d27-a8ec-bdc5f77757f7|dd9165f2-65be-6e27-a8ac-bec5f4a757ba|
 	    |parents                    |eb4d7e1b-7bed-890a-cddf-cdb25a29fc2d|38ba6ea7-7e73-47db-99e7-d0956f83d7e9|
 	    |programs                   |9b8cafdc-8fd5-11e1-86ec-0021701f543f_id|9b8c3aab-8fd5-11e1-86ec-0021701f543f_id|
 	    |reportCards                |cf0ca1c6-a9db-4180-bf23-8276c4e2624c|8770da5b-dca5-4ced-bf3b-5fa17bc0001d|
 	    |schools                    |a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb|92d6d5a0-852c-45f4-907a-912752831772|
 	    |sections                   |a00c740c-8753-4190-90ad-9fc026a65d53_id|14c68439-62c1-461a-a178-ad8ac9404f95_id|
 	    |staff                      |e59d9991-9d8f-48ab-8790-59df9bcf9bc7|cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4|
 	    |students                   |766519bf-31f2-4140-97ec-295297bc045e_id|fb71442f-1023-4c55-a675-92ad8c393c82_id|
 	    |studentAcademicRecords     |56afc8d4-6c91-48f9-8a11-de527c1131b7|16afc8d4-6c91-48f9-8a51-de527c1131b7|
 	    |studentGradebookEntries    |2713b97a-5632-44a5-8e04-031074bcb326|0f5e6f78-5434-f906-e51b-d63ef970ef8f|
 	    |studentCompetencies        |b57643e4-9acf-11e1-89a7-68a86d21d918|3a2ea9f8-9acf-11e1-add5-68a86d83461b|
 	    #|studentCompetencyObjectives|_id|_id|
 	    |teachers                   |bcfcc33f-f4a6-488f-baee-b92fbd062e8d|e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b|
 	    # Associations
 	    |courseOfferings                        |01ba881f-ae39-4b76-920e-42bc7e8769d7|33ca7b78-737c-448c-8f68-4eb7e6aea415|
 	    |courseTranscripts                      |f11a2a30-d4fd-4400-ae18-353c00d581a2|36aeeabf-ee9b-46e6-8039-13320bf12346|
 	    |staffCohortAssociations                |b41338da-8fd5-11e1-86ec-0021701f543f|b4e31b1a-8e55-8803-722c-14d8087c0712|
 	    |staffEducationOrgAssignmentAssociations|b1c40ccc-b466-8f3b-b3c7-7e13c2bc4d5a|2e2c3dd7-d8d6-4966-82dc-cdc598aa7d2c|
 	    |staffProgramAssociations               |9bf7591b-8fd5-11e1-86ec-0021701f543f|9bfab47d-8fd5-11e1-86ec-0021701f5432|
 	    |studentAssessments                     |e5e13e61-01aa-066b-efe0-710f7a011115_id|87fb8da5-e1aa-a6d9-efc7-b0eb091cd695_id|
 	    |studentCohortAssociations              |b40926af-8fd5-11e1-86ec-0021701f543f_idb40ca923-8fd5-11e1-86ec-0021701f543f_id|b408d88e-8fd5-11e1-86ec-0021701f543f_idb40d6c75-8fd5-11e1-86ec-0021701f543f_id|
 	    |studentDisciplineIncidentAssociations  |0c2756fd-6a30-4010-af79-488d6ef2735a_id20120613-8d5a-c796-76e3-d77d5d497e6c_id|1563ec1d-924d-4c02-8099-3a0e314ef1d4_id3792acf6-8d5a-c796-76e3-d77d5d497e6c_id|
 	    |studentParentAssociations              |74cf790e-84c4-4322-84b8-fca7206f1085_iddd69083f-a053-4819-a3cd-a162cdc627d7_id|5738d251-dd0b-4734-9ea6-417ac9320a15_idc5aa1969-492a-5150-8479-71bfc4d57f1e_id|
 	    |studentProgramAssociations             |9b8c3aab-8fd5-11e1-86ec-0021701f543f_idb3f63ae6-8fd5-11e1-86ec-0021701f543f_id|9b8cafdc-8fd5-11e1-86ec-0021701f543f_idb3f55084-8fd5-11e1-86ec-0021701f543f_id|
 	    |studentSchoolAssociations              |f4cd9ac2-8f68-42a7-a886-977e4a194c0c|db49239e-4813-44d6-98b1-da29eba0f47f|
 	    |teacherSchoolAssociations              |9d4e4031-3a5d-4965-98b9-257ff887a774|26a4a0fc-fad4-45f4-a00d-285acd1f83eb|
 	    |teacherSectionAssociations             |706ee3be-0dae-4e98-9525-f564e05aa388_id29d58f86-5fab-4926-a9e2-e4076fe27bb3_id|15ab6363-5509-470c-8b59-4f289c224107_id32b86a2a-e55c-4689-aedf-4b676f3da3fc_id|
 
 Scenario Outline: Validate CSL where staff has access to one ID but not two
      Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
      When I navigate to GET "/v1/<ENDPOINT>/<GOOD_ID>"
      Then I should receive a return code of 200
      When I navigate to GET "/v1/<ENDPOINT>/<BAD_ID>"
      Then I should receive a return code of 403
      When I navigate to GET "/v1/<ENDPOINT>/<GOOD_ID>,<BAD_ID>"
      Then I should receive a return code of 403
      And I should see a total of 0 entities
      Examples:
      | ENDPOINT                  | GOOD_ID                            | BAD_ID                             |
      |attendances                |530f0704-c240-4ed9-0a64-55c0308f91ee|9953166a-9722-447c-094a-bfcce701c2c9|
      |cohorts                    |b40926af-8fd5-11e1-86ec-0021701f543f_id|7e9915ed-ea6f-4e6b-b8b0-aeae20a25826_id|
      |disciplineActions          |db7f1d4b-9689-b2f4-9281-d88d65999423|0e26de6c-225b-9f67-9281-7213ad50a03b|
      |disciplineIncidents        |0e26de79-7efa-5e67-9201-5113ad50a03b|0e26de79-226a-5d67-9201-5113ad50a03b|
#      |gradebookEntries           |0dbb262b-8a3e-4a7b-82f9-72de95903d91_id20120613-56b6-4d17-847b-2997b7227686_id|706ee3be-0dae-4e98-9525-f564e05aa388_id008fd89d-88a2-43aa-8af1-74ac16a29380_id|
#      |grades                     |708c4e08-9942-11e1-a8a9-68a86d21d918|ef42e2a2-9942-11e1-a8a9-68a86d21d918|
      |parents                    |eb4d7e1b-7bed-890a-cddf-cdb25a29fc2d|047e428a-336a-43c2-8944-d57204cabcd7|
#      |reportCards                |cf0ca1c6-a9db-4180-bf23-8276c4e2624c|8770da5b-dca5-4ced-bf3b-5fa17bc0001d| - Bad data
      |staff                      |e59d9991-9d8f-48ab-8790-59df9bcf9bc7|04f708bc-928b-420d-a440-f1592a5d1073|
      |students                   |766519bf-31f2-4140-97ec-295297bc045e_id|034e6e7f-9da2-454a-b67c-b95bd9f36433_id|
      |studentAcademicRecords     |56afc8d4-6c91-48f9-8a11-de527c1131b7|3a0cc576-fe7f-40bd-b86c-ca861244db12|
#      |studentGradebookEntries    |2713b97a-5632-44a5-8e04-031074bcb326|0f5e6f78-5434-f906-e51b-d63ef970ef8f| - Bad data
#      |studentCompetencies        |b57643e4-9acf-11e1-89a7-68a86d21d918|3a2ea9f8-9acf-11e1-add5-68a86d83461b| - Bad data
      |teachers                   |bcfcc33f-f4a6-488f-baee-b92fbd062e8d|eb4d7e1b-7bed-890a-d574-1d729a37fd2d| 
      # Associations
      |courseTranscripts                      |f11a2a30-d4fd-4400-ae18-353c00d581a2|09eced61-edd9-4826-a7bc-137ffecda877|
      |staffCohortAssociations                |b41338da-8fd5-11e1-86ec-0021701f543f|235b88ea-bfea-42ce-8b06-542143e19909|
      |staffEducationOrgAssignmentAssociations|b1c40ccc-b466-8f3b-b3c7-7e13c2bc4d5a|05e3de47-9e41-c048-a572-3eb4c7ee9095|
      |staffProgramAssociations               |9bf7591b-8fd5-11e1-86ec-0021701f543f|04223945-b773-425c-8173-af090a960603|
      |studentAssessments                     |e5e13e61-01aa-066b-efe0-710f7a011115_id|c8672d3b-0953-4ad7-a1b5-d5395bc0150a_id|
      |studentCohortAssociations              |b40926af-8fd5-11e1-86ec-0021701f543f_idb40ca923-8fd5-11e1-86ec-0021701f543f_id|a50121a2-c566-401b-99a5-71eb5cab5f4f_id32b4b1f1-f7c6-40c2-8de6-37e34f8051de_id|
      |studentDisciplineIncidentAssociations  |0c2756fd-6a30-4010-af79-488d6ef2735a_id20120613-8d5a-c796-76e3-d77d5d497e6c_id|714c1304-8a04-4e23-b043-4ad80eb60992_id0e26de6c-225b-9f67-8621-5113ad50a03b_id|
#      |studentParentAssociations              |74cf790e-84c4-4322-84b8-fca7206f1085_iddd69083f-a053-4819-a3cd-a162cdc627d7_id|5738d251-dd0b-4734-9ea6-417ac9320a15_idc5aa1969-492a-5150-8479-71bfc4d57f1e_id| - Get a 404 when accessing a parent where you get a 403 for accessing their student
      |studentProgramAssociations             |9b8c3aab-8fd5-11e1-86ec-0021701f543f_idb3f63ae6-8fd5-11e1-86ec-0021701f543f_id|f24e5725-c1e4-48db-9f62-381ab434c0ec_id052f9397-db72-4c3d-bb6d-0626c00a3694_id|
      |studentSchoolAssociations              |f4cd9ac2-8f68-42a7-a886-977e4a194c0c|03af9c21-43c0-4d2d-bac6-96cf3290a6f4| 
      |teacherSchoolAssociations              |9d4e4031-3a5d-4965-98b9-257ff887a774|1a72521b-7bed-890a-d574-1d729a379528|
      |teacherSectionAssociations             |706ee3be-0dae-4e98-9525-f564e05aa388_id29d58f86-5fab-4926-a9e2-e4076fe27bb3_id|58c9ef19-c172-4798-8e6e-c73e68ffb5a3_id12f25c0f-75d7-4e45-8f36-af1bcc342871_id|

Scenario Outline: Validate CSL where teacher has access to one ID but not two
      Given I am logged in using "cgray" "cgray1234" to realm "IL"
      When I navigate to GET "/v1/<ENDPOINT>/<GOOD_ID>"
      Then I should receive a return code of 200
      When I navigate to GET "/v1/<ENDPOINT>/<BAD_ID>"
      Then I should receive a return code of 403
      When I navigate to GET "/v1/<ENDPOINT>/<GOOD_ID>,<BAD_ID>"
      Then I should receive a return code of 403
      And I should see a total of 0 entities
      Examples:
      | ENDPOINT                  | GOOD_ID                            | BAD_ID                             |
      |attendances                |4beb72d4-0f76-4071-92b4-61982dba7a7b|530f0704-c240-4ed9-0a64-55c0308f91ee|
      |cohorts                    |9ac7ad37-80aa-42ab-9d63-e48cc70a7863_id|b40926af-8fd5-11e1-86ec-0021701f543f_id|
#      |disciplineActions          |db7f1d4b-9689-b2f4-9281-d88d65999423|0e26de6c-225b-9f67-9281-7213ad50a03b|
      |disciplineIncidents        |0e26de79-7efa-5e67-9201-5113ad50a03b|0e26de79-22ea-5d67-9201-5113ad50a03b|
      |gradebookEntries           |15ab6363-5509-470c-8b59-4f289c224107_ide49dc00c-182d-4f22-7919-201211130004_id|706ee3be-0dae-4e98-9525-f564e05aa388_id008fd89d-88a2-43aa-8af1-74ac16a29380_id|
      |grades                     |ef42e2a2-9942-11e1-7919-201211130001|708c4e08-9942-11e1-a8a9-68a86d21d918|
      |parents                    |9b8f7237-ce8e-4dff-7919-201211130040|056dce8e-ec68-4df6-add0-a4243bddca9a|
      |reportCards                |8770da5b-dca5-4ced-bf3b-5fa17bc0001d|cf0ca1c6-a9db-4180-bf23-8276c4e2624c|
      |staff                      |04f708bc-928b-420d-a440-f1592a5d1073|e59d9991-9d8f-48ab-8790-59df9bcf9bc7|
      |students                   |0c2756fd-6a30-4010-af79-488d6ef2735a_id|766519bf-31f2-4140-97ec-295297bc045e_id|
      |studentAcademicRecords     |56afc8d4-6c91-48f9-8a11-de527c1131b7|3a0cc576-fe7f-40bd-b86c-ca861244db12|
      |studentGradebookEntries    |20120613-5434-f906-e51b-d63ef970ef8f|2713b97a-5632-44a5-8e04-031074bcb326|
      |studentCompetencies        |b57643e4-9acf-11e1-7919-201211130002|b57643e4-9acf-11e1-89a7-68a86d21d918|
      |teachers                   |e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b|bcfcc33f-f4a6-488f-baee-b92fbd062e8d| 
      # Associations
      |courseTranscripts                      |36aeeabf-ee9b-46e6-7919-201311130015|f11a2a30-d4fd-4400-ae18-353c00d581a2|
      |staffCohortAssociations                |2e1b42be-ff75-4e74-b9f1-cdcd9164b183|b41338da-8fd5-11e1-86ec-0021701f543f|
      |staffEducationOrgAssignmentAssociations|a29e3113-316d-bfd1-4b00-b9121b8fdfd3|b1c40ccc-b466-8f3b-b3c7-7e13c2bc4d5a|
      |staffProgramAssociations               |971638e0-03a8-43df-b4d3-a577fa5ff59c|9bf7591b-8fd5-11e1-86ec-0021701f543f|
      |studentAssessments                     |e5e13e61-01aa-066b-efe0-710f7a011115_id|c8672d3b-0953-4ad7-a1b5-d5395bc0150a_id|
      |studentCohortAssociations              |9ac7ad37-80aa-42ab-9d63-e48cc70a7863_id9f916af2-c178-49ca-b9c4-e52fc2f629ed_id|b40926af-8fd5-11e1-86ec-0021701f543f_idb40ca923-8fd5-11e1-86ec-0021701f543f_id|
      |studentDisciplineIncidentAssociations  |0c2756fd-6a30-4010-af79-488d6ef2735a_id20120613-8d5a-c796-76e3-d77d5d497e6c_id|714c1304-8a04-4e23-b043-4ad80eb60992_id0e26de6c-225b-9f67-8621-5113ad50a03b_id|
      |studentParentAssociations              |0c2756fd-6a30-4010-af79-488d6ef2735a_idc5aa1969-492a-5150-8479-71bfc4d87984_id|74cf790e-84c4-4322-84b8-fca7206f1085_iddd69083f-a053-4819-a3cd-a162cdc627d7_id|
      |studentProgramAssociations             |9b8cafdc-8fd5-11e1-86ec-0021701f543f_idb3f4db53-8fd5-11e1-86ec-0021701f543f_id|9b8c3aab-8fd5-11e1-86ec-0021701f543f_idb3f63ae6-8fd5-11e1-86ec-0021701f543f_id|
      |studentSchoolAssociations              |f4cd9ac2-8f68-42a7-a886-977e4a194c0c|03af9c21-43c0-4d2d-bac6-96cf3290a6f4|
      |teacherSchoolAssociations              |9d4e4031-3a5d-4965-98b9-257ff887a774|1a72521b-7bed-890a-d574-1d729a379528|
      |teacherSectionAssociations             |15ab6363-5509-470c-8b59-4f289c224107_id32b86a2a-e55c-4689-aedf-4b676f3da3fc_id|706ee3be-0dae-4e98-9525-f564e05aa388_id29d58f86-5fab-4926-a9e2-e4076fe27bb3_id|
