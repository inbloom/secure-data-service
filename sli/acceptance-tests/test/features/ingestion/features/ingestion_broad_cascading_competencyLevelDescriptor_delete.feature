@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Competency Level Descriptor with cascade = false and force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "competencyLevelDescriptor" records like below in "Midgar" tenant. And I save this query as "competencyLevelDescriptor"
	   |field      |value                                           |
	   |_id        |d82250f49dbe4facb59af2f88fe746f70948405d_id     |
    And I save the collection counts in "Midgar" tenant   
    And I post "SafeCompetencyLevelDescriptorDelete.zip" file as the payload of the ingestion job
	  When zip file is scp to ingestion landing zone
    And a batch job for file "SafeCompetencyLevelDescriptorDelete.zip" is completed in database
	  And I should see "records considered for processing: 1" in the resulting batch job file
	  And I should see "records ingested successfully: 0" in the resulting batch job file
	  And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
	  And I should see "records not considered for processing: 0" in the resulting batch job file
	  And I should see "All records processed successfully." in the resulting batch job file
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	  And I re-execute saved query "competencyLevelDescriptor" to get "0" records
	  And I see that collections counts have changed as follows in tenant "Midgar"
	   |collection                        |delta     |
	   |competencyLevelDescriptor         |        -1|
	   |recordHash                        |        -1|

	
Scenario: Delete Competency Level Descriptor with cascade = false and force = true, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "competencyLevelDescriptor" records like below in "Midgar" tenant. And I save this query as "competencyLevelDescriptor"
	   |field      |value                                           |
	   |_id        |d82250f49dbe4facb59af2f88fe746f70948405d_id     |
    And I save the collection counts in "Midgar" tenant   
    And I post "ForceCompetencyLevelDescriptorDelete.zip" file as the payload of the ingestion job
	  When zip file is scp to ingestion landing zone
    And a batch job for file "ForceCompetencyLevelDescriptorDelete.zip" is completed in database
	  And I should see "records considered for processing: 1" in the resulting batch job file
	  And I should see "records ingested successfully: 0" in the resulting batch job file
	  And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
	  And I should see "records not considered for processing: 0" in the resulting batch job file
	  And I should see "All records processed successfully." in the resulting batch job file
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	  And I re-execute saved query "competencyLevelDescriptor" to get "0" records
	  And I see that collections counts have changed as follows in tenant "Midgar"
	   |collection                        |delta     |
	   |competencyLevelDescriptor         |        -1|
	   |recordHash                        |        -1|


Scenario: Delete Competency Level Descriptor Reference with cascade = false and force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "competencyLevelDescriptor" records like below in "Midgar" tenant. And I save this query as "competencyLevelDescriptor"
	   |field      |value                                           |
	   |_id        |d82250f49dbe4facb59af2f88fe746f70948405d_id     |
    And I save the collection counts in "Midgar" tenant   
    And I post "SafeCompetencyLevelDescriptorRefDelete.zip" file as the payload of the ingestion job
	  When zip file is scp to ingestion landing zone
    And a batch job for file "SafeCompetencyLevelDescriptorRefDelete.zip" is completed in database
	  And I should see "records considered for processing: 1" in the resulting batch job file
	  And I should see "records ingested successfully: 0" in the resulting batch job file
	  And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
	  And I should see "records not considered for processing: 0" in the resulting batch job file
	  And I should see "All records processed successfully." in the resulting batch job file
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	  And I re-execute saved query "competencyLevelDescriptor" to get "0" records
	  And I see that collections counts have changed as follows in tenant "Midgar"
	   |collection                        |delta     |
	   |competencyLevelDescriptor         |        -1|
	   |recordHash                        |        -1|

	
Scenario: Delete Competency Level Descriptor Reference with cascade = false and force = true, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "competencyLevelDescriptor" records like below in "Midgar" tenant. And I save this query as "competencyLevelDescriptor"
	   |field      |value                                           |
	   |_id        |d82250f49dbe4facb59af2f88fe746f70948405d_id     |
    And I save the collection counts in "Midgar" tenant   
    And I post "ForceCompetencyLevelDescriptorRefDelete.zip" file as the payload of the ingestion job
	  When zip file is scp to ingestion landing zone
    And a batch job for file "ForceCompetencyLevelDescriptorRefDelete.zip" is completed in database
	  And I should see "records considered for processing: 1" in the resulting batch job file
	  And I should see "records ingested successfully: 0" in the resulting batch job file
	  And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
	  And I should see "records not considered for processing: 0" in the resulting batch job file
	  And I should see "All records processed successfully." in the resulting batch job file
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	  And I re-execute saved query "competencyLevelDescriptor" to get "0" records
	  And I see that collections counts have changed as follows in tenant "Midgar"
	   |collection                        |delta     |
	   |competencyLevelDescriptor         |        -1|
	   |recordHash                        |        -1|

 
	
	
	
