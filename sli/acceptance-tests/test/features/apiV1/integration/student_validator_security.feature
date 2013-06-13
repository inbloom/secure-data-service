@wip @student_validator
Feature: Verify integrety of Student Validation Logic
  I want to make damn sure that student validation logic is working as intended, and that others do not break it.

Scenario: Validators return proper return codes on multi-ID requests
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "carmen.ortiz" with password "carmen.ortiz1234"
  And format "application/json"
  When I make API calls for multiple IDs in accordance to the following table:
    | Path                             | GoodId                                                                                 | BadId                                       |
    | /attendances/@ids                | 69a338d4c77f47dbb0edb12878c751bde7622505_id                                            |
    | /cohorts/@ids                    | 271a8d3a18ae2d80599dc55a0abaaeb8527ff10f_id                                            |                                             |
    | /courseTranscripts/@ids          | a2cc843e5bc38898c50960008110e029ece8e609_id                                            |
    | /gradebookEntries/@ids           |                                                                                         |                                             |
    | /grades/@ids                     |                                                                                        |                                             |
    | /graduationPlans/@ids            |                                                                                        |                                             |
    | /parents/@ids                    |                                                                                        |                                             |
    | /reportCards/@ids                |                                                                                        |                                             |
    | /staff/@ids                      |                                                                                        |                                             |
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
