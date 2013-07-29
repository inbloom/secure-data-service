Feature: Generic EdOrg Ingestion

  Scenario: Post Generic EdOrg Sample Data Set
    Given the "Midgar" tenant db is empty
    When I ingest "GenericEdOrg.zip"
    #basic SEA
    # basic LEA
    #basic school
    #nested LEA
    #school off nested LEA
    #school with two LEA parents at same level
    #school with LEA parents at different levels
    #school off muliple level and same levels (combining above two scenarios)
    #school/LEA off SEA

  Scenario: Post Generic EdOrg Sample Data Set
    Given the "Midgar" tenant db is empty
    When I ingest "GenericEdOrg.zip"
    And I should see following map of entry counts in the corresponding collections:
      | collectionName                           |              count|
      | recordHash                               |                  8|
      | educationOrganization                    |                  8|
    # clear out the ingested entities to ensure we aren't upserting the 2nd time
    And the following collections are empty in datastore:
      | collectionName                            |
      | educationOrganization                     |
    And zip file is scp to ingestion landing zone with name "Reingest-GenericEdOrg.zip"
    And a batch job for file "Reingest-GenericEdOrg.zip" is completed in database
    And I should see "InterchangeEducationOrganization.xml educationOrganization 8 deltas" in the resulting batch job file
    And I should not see a warning log file created
    And I should see following map of entry counts in the corresponding collections:
      | collectionName                           |              count|
      | recordHash                               |                  8|
      | educationOrganization                    |                  0|

