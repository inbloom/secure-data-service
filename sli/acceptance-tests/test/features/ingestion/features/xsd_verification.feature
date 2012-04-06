Feature: XSD Verification

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone


Scenario: Valid Student Verification

    Given I post "XsdValidationValid.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
    When zip file is scp to ingestion landing zone
    And I am willing to wait up to 5 seconds for ingestion to complete
    And a batch job log has been created

    Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 2     |
    And I check to find if record is in collection:
    | collectionName            | expectedRecordCount | searchParameter                    | searchValue           | searchType        |
    | student                   | 1                   | body.studentUniqueStateId          | 530425896             | string            |
    | student                   | 1                   | body.name.firstName                | Rhonda                | string            |
    | student                   | 1                   | body.name.middleName               | Shannon               | string            |
    | student                   | 1                   | body.name.lastName                 | Delgado               | string            |
    | student                   | 1                   | body.name.GenerationCodeSuffix     | Delgado               | string            |
    | student                   | 1                   | body.sex                           | Female                | string            |
    | student                   | 1                   | body.BirthData.BirthDate           | 2006-07-02            | string            |
    | student                   | 1                   | body.HispanicLatinoEthnicity       | false                 | string            |
    | student                   | 1                   | body.EconomicDisadvantaged         | false                 | string            |
    | student                   | 1                   | body.SchoolFoodServicesEligibility | false                 | string            |
    | student                   | 1                   | body.LimitedEnglishProficiency     | NotLimited            | string            |
    | student                   | 1                   | body.DisplacementStatus            | false                 | string            |

    And I should see a batch job file created
    And I should see "Processed 2 records." in the resulting batch job file
    And I should see "InterchangeStudentValid.xml records considered: 2" in the resulting batch job file
    And I should see "InterchangeStudentValid.xml records ingested successfully: 2" in the resulting batch job file
    And I should see "InterchangeStudentValid.xml records failed: 0" in the resulting batch job file
    And I should not see an error log file created
 

Scenario: Invalid Student Verification

    Given I post "XsdValidationInvalid.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
    When zip file is scp to ingestion landing zone
    And I am willing to wait up to 5 seconds for ingestion to complete
    And a batch job log has been created

    Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 0     |

    And I should see a batch job log file created
    And I should see "Processed 2 records." in the resulting batch job file
    And I should see "InterchangeStudentInvalid.xml records considered: 2" in the resulting batch job file
    And I should see "InterchangeStudentInvalid.xml records ingested successfully: 0" in the resulting batch job file
    And I should see "InterchangeStudentInvalid.xml records failed: 2" in the resulting batch job file
    And I should see an error log file created
    And I should see "cvc-datatype-valid.1.2.1: '2006-07-02-081' is not a valid value for 'date'." in the resulting error log file
    And I should see "cvc-type.3.1.3: The value '2006-07-02-081' of element 'BirthDate' is not valid." in the resulting error log file
    
