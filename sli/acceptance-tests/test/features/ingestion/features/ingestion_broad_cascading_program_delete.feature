@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete Program with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "localEducationAgency"
	|field                                                           |value                                                |
	|body.programReference                                                |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "1" "program" records like below in "Midgar" tenant. And I save this query as "program"
	|field                                                           |value                                                |
	|_id                                                             |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "104" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "staffProgramAssociation"
	|field                                                           |value                                                |
	|body.programId                                                  |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "1" "program" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
	|field                                                           |value                                                |
	|studentProgramAssociation.body.programId                        |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "9" "student" records like below in "Midgar" tenant. And I save this query as "student"
	|field                                                           |value                                                |
	|program._id                                                     |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	And I save the collection counts in "Midgar" tenant   
    And I post "BroadProgramDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadProgramDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "program" to get "0" records
	And I re-execute saved query "localEducationAgency" to get "0" records
	And I re-execute saved query "studentProgramAssociation" to get "0" records
	And I re-execute saved query "staffProgramAssociation" to get "0" records
	And I re-execute saved query "student" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|student                                |         0|
	|program                                |        -1|
	|staffProgramAssociation                |      -104|
	|recordHash                             |         0|
	|studentProgramAssociation              |       -14|
	And I should not see "0064dd5bb3bffd47e93b023585e6591c018ee697_id" in the "Midgar" database

Scenario: Delete Program with cascade = false, force = false
  Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And the "Midgar" tenant db is empty
  When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "program" records like below in "Midgar" tenant. And I save this query as "program"
	|field                                                           |value                                                |
	|_id                                                             |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "2" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	|field                                                           |value                                                |
	|type                                                            |educationOrganization                                 |
	|body.programReference                                           |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "105" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "staffProgramAssociation"
	|field                                                           |value                                                |
	|body.programId                                                  |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "9" "student" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
	|field                                                           |value                                                |
	|studentProgramAssociation.body.programId                        |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
	|field                                                           |value                                                |
	|body.programReference                                           |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |	
	Then there exist "1" "cohort" records like below in "Midgar" tenant. And I save this query as "cohort"
	|field                                                           |value                                                |
	|body.programId                                                  |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |		
	And I save the collection counts in "Midgar" tenant   
  And I post "SafeProgramDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
  And a batch job for file "SafeProgramDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
  And I should see "CORE_0066" in the resulting error log file for "InterchangeEducationOrganization.xml"
  And I should not see a warning log file created
	And I re-execute saved query "program" to get "1" records
	And I re-execute saved query "educationOrganization" to get "2" records
	And I re-execute saved query "staffProgramAssociation" to get "105" records
	And I re-execute saved query "studentProgramAssociation" to get "9" records
	And I re-execute saved query "section" to get "1" records
	And I re-execute saved query "cohort" to get "1" records	
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                             |delta     |
	|program                                |         0|
	
	
	Scenario: Delete Program Reference with cascade = false, force = false
  Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And the "Midgar" tenant db is empty
  When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "program" records like below in "Midgar" tenant. And I save this query as "program"
	|field                                                           |value                                                |
	|_id                                                             |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "2" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	|field                                                           |value                                                |
	|type                                                            |educationOrganization                                 |
	|body.programReference                                           |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "105" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "staffProgramAssociation"
	|field                                                           |value                                                |
	|body.programId                                                  |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "9" "student" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
	|field                                                           |value                                                |
	|studentProgramAssociation.body.programId                        |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
	|field                                                           |value                                                |
	|body.programReference                                           |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |	
	And I save the collection counts in "Midgar" tenant   
  And I post "SafeProgramRefDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
  And a batch job for file "SafeProgramRefDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
  And I should see "CORE_0066" in the resulting error log file for "InterchangeEducationOrganization.xml"
  And I should not see a warning log file created
	And I re-execute saved query "program" to get "1" records
	And I re-execute saved query "educationOrganization" to get "2" records
	And I re-execute saved query "staffProgramAssociation" to get "105" records
	And I re-execute saved query "studentProgramAssociation" to get "9" records
	And I re-execute saved query "section" to get "1" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                             |delta     |
	|program                                |         0|

	
Scenario: Orphan Delete Program with cascade = false and force = false, log violations = true
	Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported			
	  Then there exist "1" "program" records like below in "Midgar" tenant. And I save this query as "program"
        |field      |value                                               |
        |_id        |33488db7ec1b7c3ccfc46829fcf968d8fd8009f0_id         |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanProgramDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanProgramDelete.zip" is completed in database
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	  And I should not see a warning log file created
    And I re-execute saved query "program" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | program                                   |        -1|
        | recordHash                                |        -1|
        
 Scenario: Orphan Delete Program Ref with cascade = false and false = true, log violations = true
	Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported		
	  Then there exist "1" "program" records like below in "Midgar" tenant. And I save this query as "program"
        |field      |value                                               |
        |_id        |33488db7ec1b7c3ccfc46829fcf968d8fd8009f0_id         |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanProgramRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanProgramRefDelete.zip" is completed in database
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	  And I should not see a warning log file created
    And I re-execute saved query "program" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | program                                   |        -1|
        | recordHash                                |        -1|
        
        
Scenario: Delete Program with cascade = false and force = true, log violations = true
	Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported			
	  Then there exist "1" "program" records like below in "Midgar" tenant. And I save this query as "program"
        |field      |value                                               |
        |_id        |0064dd5bb3bffd47e93b023585e6591c018ee697_id         |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceProgramDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceProgramDelete.zip" is completed in database
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	  And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrganization.xml"
    And I re-execute saved query "program" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | program                                   |        -1|
        | recordHash                                |        -1|

        
 Scenario: Delete Program Ref with cascade = false and force = true, log violations = true
	Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	  Then there exist "1" "program" records like below in "Midgar" tenant. And I save this query as "program"  
        |field      |value                                               |
        |_id        |0064dd5bb3bffd47e93b023585e6591c018ee697_id         |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceProgramRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceProgramRefDelete.zip" is completed in database
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	  And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrganization.xml"
    And I re-execute saved query "program" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | program                                   |        -1|
        | recordHash                                |        -1|
