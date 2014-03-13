@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete CourseOffering with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
	|field                                                           |value                                                |
	|_id                                                             |644c4b7a7ffa3863ec22af9aeef918ef962f6d9c_id          |
	Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
	|field                                                           |value                                                |
	|body.courseOfferingId                                           |644c4b7a7ffa3863ec22af9aeef918ef962f6d9c_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "SafeCourseOfferingDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeCourseOfferingDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeMasterSchedule.xml"
	And I should not see a warning log file created
    And I re-execute saved query "courseOffering" to get "1" records
    And I re-execute saved query "section" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |courseOffering|   0|
 

Scenario: Delete Orphan CourseOffering with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
	|field                                                           |value                                                |
	|_id                                                             |4335480b5fd712894dc0fb52f4e64d262d548c08_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "OrphanCourseOfferingDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanCourseOfferingDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "courseOffering" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection   |delta|
        |courseOffering|   -1|
        |recordHash    |   -1|


Scenario: Delete Orphan CourseOffering Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
	|field                                                           |value                                                |
	|_id                                                             |4335480b5fd712894dc0fb52f4e64d262d548c08_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "OrphanCourseOfferingRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanCourseOfferingRefDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "courseOffering" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |courseOffering|  -1|  
        |recordHash    |  -1|


 Scenario: Delete CourseOffering with cascade = false, force = true and log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
	|field                                                           |value                                                |
	|_id                                                             |644c4b7a7ffa3863ec22af9aeef918ef962f6d9c_id          |
	Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
	|field                                                           |value                                                |
	|body.courseOfferingId                                           |644c4b7a7ffa3863ec22af9aeef918ef962f6d9c_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "ForceCourseOfferingDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceCourseOfferingDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeMasterSchedule.xml"	
    And I re-execute saved query "courseOffering" to get "0" records
    And I re-execute saved query "section" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |courseOffering|   -1|
        |recordHash  |    -1|

 Scenario: Delete CourseOffering Reference with cascade = false, force = true and log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
	|field                                                           |value                                                |
	|_id                                                             |644c4b7a7ffa3863ec22af9aeef918ef962f6d9c_id          |
	Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
	|field                                                           |value                                                |
	|body.courseOfferingId                                           |644c4b7a7ffa3863ec22af9aeef918ef962f6d9c_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "ForceCourseOfferingRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceCourseOfferingRefDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeMasterSchedule.xml"	
    And I re-execute saved query "courseOffering" to get "0" records
    And I re-execute saved query "section" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |courseOffering|   -1|
        |recordHash  |    -1|
