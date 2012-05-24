Feature: Ed-Fi XSD Verification

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone


Scenario: Student Ed-Fi XSD Validation

    Given I post "XsdValidation.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
    When zip file is scp to ingestion landing zone
    And a batch job log has been created

    Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 4     |
    And I check to find if record is in collection:
    | collectionName            | expectedRecordCount | searchParameter                    | searchValue           | searchType        |
    | student                   | 1                   | body.studentUniqueStateId          | 530425896             | string            |
    | student                   | 1                   | body.studentUniqueStateId          | 814202099             | string            |
    | student                   | 1                   | body.studentUniqueStateId          | 162849670             | string            |
    | student                   | 1                   | body.studentUniqueStateId          | 489503728             | string            |
    | student                   | 2                   | body.schoolFoodServicesEligibility | Reduced price         | string            |
    | student                   | 2                   | body.schoolFoodServicesEligibility | Full price            | string            |
    | student                   | 3                   | body.limitedEnglishProficiency     | NotLimited            | string            |

    Then I should see "Processed 4 records." in the resulting batch job file
    And I should see "InterchangeStudent.xml records considered: 4" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 4" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file

    And I should see "File InterchangeStudent.xml, Line 56, Column 28:" in the resulting warning log file
    And I should see "cvc-complex-type.2.4.a: Invalid content was found starting with element 'EconomicDisadvantaged'." in the resulting warning log file
    And I should see "One of '{" in the resulting warning log file
    And I should see "http://ed-fi.org/0100" in the resulting warning log file
    And I should see ":BirthData}' is expected." in the resulting warning log file

    And I should see "File InterchangeStudent.xml, Line 87, Column 60:" in the resulting warning log file
    And I should see "cvc-type.3.1.3: The value '' of element 'LimitedEnglishProficiency' is not valid." in the resulting warning log file

    And I should not see an error log file created

Scenario: InterchangeStudentGrade.xml Ed-Fi XSD Validation - <CompetencyLevel> under <StudentGradebookEntry>

    Given I post "StudentGradeXsdValidation.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
      | collectionName              |
      | student                     |
      | course                      |
      | educationOrganization       |
      | gradebookEntry              |
      | schoolSessionAssociation    |
      | section                     |
      | session                     |
      | studentSectionGradebookEntry|
    When zip file is scp to ingestion landing zone
    And a batch job log has been created

    Then I should see following map of entry counts in the corresponding collections:
      | collectionName              | count |
      | student                     | 1     |
      | course                      | 1     |
      | educationOrganization       | 3     |
      | gradebookEntry              | 1     |
      | schoolSessionAssociation    | 0     |
      | section                     | 1     |
      | session                     | 1     |
      | studentSectionGradebookEntry| 1     |
    And I check to find if record is in collection:
      | collectionName              | expectedRecordCount | searchParameter                    | searchValue           | searchType        |
      | student                     | 1                   | body.studentUniqueStateId          | 100000000             | string            |
      | student                     | 1                   | body.schoolFoodServicesEligibility | Reduced price         | string            |
      | studentSectionGradebookEntry| 1                   | body.dateFulfilled                 | 2011-09-16            | string            |

    Then I should see "Processed 290 records." in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records considered: 2" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 2" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records failed: 0" in the resulting batch job file

    And I should not see an error log file created