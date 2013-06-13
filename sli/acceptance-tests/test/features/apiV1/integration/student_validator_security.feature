@wip @student_validator
Feature: Verify integrety of Student Validation Logic
  I want to make damn sure that student validation logic is working as intended, and that others do not break it.

Scenario: Validators return proper return codes on multi-ID requests
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "carmen.ortiz" with password "carmen.ortiz1234"
  And format "application/json"
  When I make API calls for multiple IDs in accordance to the following table:
    | Path                             | GoodId                                                                                 | BadId                                                                                  |
    | /attendances/@ids                | 69a338d4c77f47dbb0edb12878c751bde7622505_id                                            | cd14890af69207e6d9433f0962107eb0c96a1748_id                                            |
    | /cohorts/@ids                    | 271a8d3a18ae2d80599dc55a0abaaeb8527ff10f_id                                            |                                                                                        |
    | /courseTranscripts/@ids          | a2cc843e5bc38898c50960008110e029ece8e609_id                                            | 59f8b2b15cb04b2cd47db666d46802aca5836c1e_id                                            |
    | /gradebookEntries/@ids           | 527f07a98f7f05c56c17a07cbbeac7eb1fa1d4db_id9de343d4f87b71ca48f226aafb77601f59fb95c3_id | ac4aede7e0113d1c003f3da487fc079e124f129d_id26c51914b1974dfd8104dad40ee78f4c10324851_id |
    | /grades/@ids                     | e1ddd4b5c0c531a734135ecd461b33cab842c18c_id071ab91f682dde1c9eb3e4b241e26285cac6ad0a_id |                                             |
    | /graduationPlans/@ids            | 7f6e03f2a01f0f74258a1b0d8796be5eaf289f0a_id                                            |                                             |
    | /parents/@ids                    | 2d6638adf22232b9af30b03ce9e84e707f4cf501_id                                            |                                             |
    | /reportCards/@ids                | e1ddd4b5c0c531a734135ecd461b33cab842c18c_id4775da5abb4e0b99e6f6f57239c5739b6f4299e4_id |                                             |
    | /staff/@ids                      | e19386ee6534460293d35cc7ab9b8547e145205a_id                                            |                                             |
    | /studentAcademicRecords/@ids     | e1ddd4b5c0c531a734135ecd461b33cab842c18c_id074f8af9afa35d4bb10ea7cd17794174563c7509_id | 2f38a01d3ce555cfcdc637aa02d3596de1e27574_id51a05e3fc152d6180c97370b1cdc4de367b1dce7_id |
    | /studentAssessments/@ids         | 87e0d1641f5a3e89a97a7ae3e13be346b321ee0a_id                                            | b42851a6c1fc6ed59ee1ef050cea20691a4d3267_id                                            |
    | /studentCohortAssociations/@ids  | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id8f048847deda45718db8abba32518b55f0006ec1_id | fdd8ee3ee44133f489e47d2cae109e886b041382_idb8b99293f159daade6bf392d41c604d7ebcf88fd_id |
    | /studentCompetencies/@ids        | b1274fe30c860d6f4a14f4586b1f6fe607525abd_id                                            | 844dbacd1d091e31428bc03fe6427186a093bf8f_id                                            |
    | /studentGradebookEntries/@ids    | 549754559ca84c5abcbacdc5f205f33ec8b21e00_id                                            | 27c40760959ddc79be9643c441dadff2e86c37bb_id                                            |
    | /studentParentAssociations/@ids  | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idc72d8118cb267b19c3716bf36e0d00a6f8649853_id | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_idf2f0a1ddc141a58169b3c586e78ae6ba8d44e8ee_id |
    | /studentProgramAssociations/@ids | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id4cb8ced05aef691b18a8c60c90f2c102acdef2b2_id | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_ida5c2d783a3c6787ca248e524744e16c85abc141e_id |
    | /students/@ids                   | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                            | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                            |
    | /studentSchoolAssociations/@ids  | 31c5fd4550afbc370682674ecccb569604d7673f_id                                            | a7e4e3a047a37499173d70b552ee27f305208ea7_id                                            |
    | /studentSectionAssociations/@ids | 527f07a98f7f05c56c17a07cbbeac7eb1fa1d4db_id0342942167328984401ea4a8619a6f1718f1fa66_id | 6b687d24b9a2b10c664e2248bd8e689a482e47e2_id2de59b0cfdaaccf05ddf3e859f27ddb6bdc9cce3_id |
#    | /teachers/@ids                   | fe472294f0e40fd428b1a67b9765360004562bab_id                                            | 6757c28005c30748f3bbda02882bf59bc81e0d71_id                                            |
#    | /teacherSchoolAssociations/@ids  | 185f8333b893edd803f880463a2a193d60715743_id                                            | 93a4133d17303788f99e3b229b9649d46de5f42e_id                                            |
#    | /teacherSectionAssociations/@ids | 527f07a98f7f05c56c17a07cbbeac7eb1fa1d4db_id1e0c3bfe230357bd09c3d1a19a29b17489eeea68_id | 6b687d24b9a2b10c664e2248bd8e689a482e47e2_idfe800a3044200c3b3ca6875b2449d581cc0521b7_id |
    | /yearlyAttendances/@ids          | 69a338d4c77f47dbb0edb12878c751bde7622505_id                                            | cd14890af69207e6d9433f0962107eb0c96a1748_id                                            |
  When I request the Good ID, I should be allowed
  When I request the Bad ID, I should be denied
  When I request both IDs, I should be denied
