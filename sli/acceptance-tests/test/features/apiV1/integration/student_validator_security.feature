@wip @student_validator
Feature: Verify integrety of Student Validation Logic
  I want to make damn sure that student validation logic is working as intended, and that others do not break it.

Scenario: Validators return proper return codes on multi-ID requests
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "carmen.ortiz" with password "carmen.ortiz1234"
  And format "application/json"
  When I make API calls for multiple IDs in accordance to the following table:
    | Path                             | GoodId                                                                                 | BadId                                                                                  |
    | /attendances/@ids                | 69a338d4c77f47dbb0edb12878c751bde7622505_id                                            |                                                                                        |
    | /cohorts/@ids                    | 271a8d3a18ae2d80599dc55a0abaaeb8527ff10f_id                                            |                                                                                        |
    | /courseTranscripts/@ids          | a2cc843e5bc38898c50960008110e029ece8e609_id                                            | 59f8b2b15cb04b2cd47db666d46802aca5836c1e_id                                            |
    | /gradebookEntries/@ids           | 527f07a98f7f05c56c17a07cbbeac7eb1fa1d4db_id9de343d4f87b71ca48f226aafb77601f59fb95c3_id | ac4aede7e0113d1c003f3da487fc079e124f129d_id26c51914b1974dfd8104dad40ee78f4c10324851_id |
    | /grades/@ids                     | e1ddd4b5c0c531a734135ecd461b33cab842c18c_id071ab91f682dde1c9eb3e4b241e26285cac6ad0a_id |                                             |
    | /graduationPlans/@ids            | 7f6e03f2a01f0f74258a1b0d8796be5eaf289f0a_id                                            |                                             |
    | /parents/@ids                    | 2d6638adf22232b9af30b03ce9e84e707f4cf501_id                                            |                                             |
    | /reportCards/@ids                | e1ddd4b5c0c531a734135ecd461b33cab842c18c_id4775da5abb4e0b99e6f6f57239c5739b6f4299e4_id |                                             |
    | /staff/@ids                      | e19386ee6534460293d35cc7ab9b8547e145205a_id                                            |                                             |
    | /studentAcademicRecords/@ids     |                                                                                        |                                             |
    | /studentAssessments/@ids         |                                                                                        |                                             |
    | /studentCohortAssociations/@ids  | fdd8ee3ee44133f489e47d2cae109e886b041382_idb8b99293f159daade6bf392d41c604d7ebcf88fd_id |                                             |
    | /studentCompetencies/@ids        |                                                                                        |                                             |
    | /studentGradebookEntries/@ids    |                                                                                        |                                             |
    | /studentParentAssociations/@ids  | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_idf2f0a1ddc141a58169b3c586e78ae6ba8d44e8ee_id |                                             |
    | /studentProgramAssociations/@ids | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_ida5c2d783a3c6787ca248e524744e16c85abc141e_id |                                             |
    | /students/@ids                   | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                            | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id |
    | /studentSchoolAssociations/@ids  |                                                                                        |                                             |
    | /studentSectionAssociations/@ids |                                                                                        |                                             |
    | /teachers/@ids                   |                                                                                        |                                             |
    | /teacherSchoolAssociations/@ids  |                                                                                        |                                             |
    | /teacherSectionAssociations/@ids |                                                                                        |                                             |
    | /yearlyAttendances/@ids          |                                                                                        |                                             |
  When I request the Good ID, I should be allowed
  When I request the Bad ID, I should be denied
  When I request both IDs, I should be denied
