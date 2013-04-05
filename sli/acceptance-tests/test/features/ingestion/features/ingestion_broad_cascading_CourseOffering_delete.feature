@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete Course Offering with cascade
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
    And I post "BroadCourseOfferingDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadCourseOfferingDelete.zip" is completed in database
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
    And I re-execute saved query "section" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |courseOffering|   -1|
        |section       |   -1|
        |gradebookEntry |  -3|
        |studentGradebookEntry|-3|
        |studentCompetency| -5|
        |grade        |    -1|
        |teacherSectionAssociation|-1|
        |studentSectionAssociation|-1|
        #|recordHash  |    -1|
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "644c4b7a7ffa3863ec22af9aeef918ef962f6d9c_id" in the "Midgar" database
