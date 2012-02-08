Feature: Section Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone
    And I connect to "sli" database

Scenario: Post a zip file of 100 sections as a payload of the ingestion job
Given I post "validSectionsXML100.zip" as the payload of the ingestion job
    And the payload contains entities of type "section"
    And there are none of this type of entity in the DS
When zip file is scp to ingestion landing zone
    And "3" seconds have elapsed
Then I should see "100" entries in the corresponding collection
    And I should see "Processed 100 records." in the resulting batch job file

Scenario: Post a zip file of 100 sections as a payload of the ingestion job
Given I post "validSectionsCSV100.zip" as the payload of the ingestion job
    And the payload contains entities of type "section"
    And there are none of this type of entity in the DS
When zip file is scp to ingestion landing zone
    And "3" seconds have elapsed
Then I should see "100" entries in the corresponding collection
    And I should see "Processed 100 records." in the resulting batch job file
