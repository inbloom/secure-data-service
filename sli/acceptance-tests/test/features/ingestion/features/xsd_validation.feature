@RALLY_US1706
@RALLY_US1970
Feature: Ed-Fi XSD Verification

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone


Scenario: Student Ed-Fi XSD Validation

    Given I post "XsdValidation.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
        | recordHash                  |
    When zip file is scp to ingestion landing zone
    And a batch job for file "XsdValidation.zip" is completed in database
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

    And I should see "BASE_0017" in the resulting warning log file
    And I should see "InterchangeStudent.xml:line-56,column-28" in the resulting warning log file
    And I should see "cvc-complex-type.2.4.a: Invalid content was found starting with element 'EconomicDisadvantaged'. One of '{" in the resulting warning log file

    And I should see "BASE_0017" in the resulting warning log file
    And I should see "InterchangeStudent.xml:line-87,column-60" in the resulting warning log file
    And I should see "cvc-type.3.1.3: The value '' of element 'LimitedEnglishProficiency' is not valid." in the resulting warning log file

    And I should not see an error log file created

Scenario: InterchangeStudentGrade.xml Ed-Fi XSD Validation - <CompetencyLevel> under <StudentGradebookEntry>

    Given I post "StudentGradeXsdValidation.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
      | collectionName              |
      | student                     |
      | calendarDate                |
      | course                      |
	  | courseOffering              |
      | educationOrganization       |
      | gradebookEntry              |
      | section                     |
	  | gradingPeriod               |
      | session                     |
      | studentGradebookEntry       |
      | recordHash                  |
    When zip file is scp to ingestion landing zone
    And a batch job for file "StudentGradeXsdValidation.zip" is completed in database
    And a batch job log has been created

    Then I should see following map of entry counts in the corresponding collections:
      | collectionName              | count |
      | educationOrganization       | 3     |
      | student                     | 1     |
      | course                      | 1     |
      | gradingPeriod               | 3     |
      | gradebookEntry              | 1     |
      | session                     | 1     |
	  | courseOffering              | 1     |
      | section                     | 1     |
      | studentGradebookEntry       | 1     |
    And I check to find if record is in collection:
      | collectionName              | expectedRecordCount | searchParameter                    | searchValue           | searchType        |
      | student                     | 1                   | body.studentUniqueStateId          | 100000000             | string            |
      | student                     | 1                   | body.schoolFoodServicesEligibility | Reduced price         | string            |
      | studentGradebookEntry       | 1                   | body.dateFulfilled                 | 2011-09-16            | string            |

    Then I should see "Processed 292 records." in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records considered: 2" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 2" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records failed: 0" in the resulting batch job file

    And I should not see an error log file created
