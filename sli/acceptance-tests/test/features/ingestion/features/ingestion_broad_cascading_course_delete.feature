@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Course with cascade = false (negative case)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "course" records like below in "Midgar" tenant. And I save this query as "course"
        |field                                     |value                                                                                 |
        |_id                                       |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
	And there exist "2" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
        |field                                     |value                                                                                 |
        |body.courseId                             |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
    And there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
        |field                                     |value                                                                                 |
        |body.courseId                             |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeCourseDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeCourseDelete.zip" is completed in database
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeEducationOrganization.xml"
   	And I should not see a warning log file created
    And I re-execute saved query "course" to get "1" records
    And I re-execute saved query "courseOffering" to get "2" records
    And I re-execute saved query "courseTranscript" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|    
        | course                                    |         0| 
        | recordHash                                |      	  0|

Scenario: Delete Orphan Course with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "course" records like below in "Midgar" tenant. And I save this query as "course"
        |field                                     |value                                                                                 |
        |_id                                       |594075bffc6a74e387aec970f5db5ee65d28238d_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanCourseDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanCourseDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "course" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | course                                    |        -1|
        | recordHash                                |      	 -1|

Scenario: Delete Orphan Course Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "course" records like below in "Midgar" tenant. And I save this query as "course"
        |field                                     |value                                                                                 |
        |_id                                       |594075bffc6a74e387aec970f5db5ee65d28238d_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanCourseRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanCourseRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "course" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | course                                    |        -1|
        | recordHash                                |      	 -1|

Scenario: Delete Course with default settings (Confirm that by default cascade = false, force = true and log violations = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "course" records like below in "Midgar" tenant. And I save this query as "course"
        |field                                     |value                                                                                 |
        |_id                                       |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
	And there exist "2" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
        |field                                     |value                                                                                 |
        |body.courseId                             |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
    And there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
        |field                                     |value                                                                                 |
        |body.courseId                             |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceCourseDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceCourseDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrganization.xml"
    And I re-execute saved query "course" to get "0" records
    And I re-execute saved query "courseOffering" to get "2" records
    And I re-execute saved query "courseTranscript" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | course                                    |        -1|
        | recordHash                                |      	 -1|

Scenario: Delete Course Ref with cascade = false and default settings (Confirm that by default force = true and log violations = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "course" records like below in "Midgar" tenant. And I save this query as "course"
        |field                                     |value                                                                                 |
        |_id                                       |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
	And there exist "2" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
        |field                                     |value                                                                                 |
        |body.courseId                             |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
    And there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
        |field                                     |value                                                                                 |
        |body.courseId                             |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceCourseRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceCourseRefDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrganization.xml"
    And I re-execute saved query "course" to get "0" records
    And I re-execute saved query "courseOffering" to get "2" records
    And I re-execute saved query "courseTranscript" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | course                                    |        -1|
        | recordHash                                |      	 -1|

