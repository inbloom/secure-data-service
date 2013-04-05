@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion
#Type	Child Type	        Field	   minOccurs	maxOccurs	Child Collection	
#course	courseOffering	    courseId	1	        1	         courseOffering	
#course	courseTranscript	courseId	1	        1	         courseTranscript		missing!	

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete Course with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "course" records like below in "Midgar" tenant. And I save this query as "course"
	|field                                             |value                                                |
	|_id                                               |c818a2f609d4190166b96327d86086fe09f877ea_id          |
	Then there exist "2" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
	|field                                             |value                                                |
	|body.courseId                                     |c818a2f609d4190166b96327d86086fe09f877ea_id          |
	Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
	|field                                             |value                                                |
	|body.courseId                                     |c818a2f609d4190166b96327d86086fe09f877ea_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "BroadCourseDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadCourseDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file    
	And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "course" to get "0" records
	And I re-execute saved query "courseOffering" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        |collection |delta|
        |course      |   -1|
        |courseOffering| -2|
        |courseTranscript| -1|
        |section     |   -1|
        |studentCompetency|-5|
        |studentGradebookEntry|-3|
        |grade |-1|
        |gradebookEntry|-3|
        |teacherSectionAssociation|-1|
        |studentSectionAssociation|-1|
        #|recordHash  |  -18|
	And I should not see "c818a2f609d4190166b96327d86086fe09f877ea_id" in the "Midgar" database
	
@wip
Scenario: Delete Course with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "course" records like below in "Midgar" tenant. And I save this query as "course"
        |field                                     |value                                                                                 |
        |_id                                       |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
	  Then there exist "2" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
	      |field                                     |value                                                                                 |
	      |body.courseId                             |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
	  Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
	      |field                                     |value                                                                                 |
	      |body.courseId                             |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
        |field                                     |value                                                                                 |
        |body.schoolId                             |xxx                                           |    
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "studentSectionAssociation"
        |field                                     |value                                                                                 |
        |body.schoolId                             |xxx                                           |        
        |studentSectionAssociation.body.sectionId  |xxx                                           | 
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "teacherSectionAssociation"
        |field                                     |value                                                                                 |
        |body.schoolId                             |xxx                                           |        
        |teacherSectionAssociation.body.sectionId  |xxx                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "BroadCourseDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BroadCourseDelete.zip" is completed in database
    And a batch job log has been created
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	  And I should not see a warning log file created
    And I re-execute saved query "course" to get "0" records
    And I re-execute saved query "courseOffering" to get "0" records
    And I re-execute saved query "disciplineAction" to get "0" records
    And I re-execute saved query "disciplineAction2" to get "0" records
    And I re-execute saved query "disciplineIncident" to get "0" records
    And I re-execute saved query "section" to get "0" records
    And I re-execute saved query "studentSchoolAssociation" to get "0" records
    And I re-execute saved query "studentSectionAssociation" to get "0" records 
    And I re-execute saved query "teacherSchoolAssociation" to get "0" records   
    And I re-execute saved query "teacherSectionAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | course                                    |        -1| 
#        | recordHash                                |      	 -1|
        | courseOffering                            |      	 -2|
        | courseTranscript                          |      	 -1|
        | grade                                     |        -5| 
        | gradebookEntry                            |       -73|                
        | section                                   |       -11|
        | studentGradebookEntry                     |       -31|  
        | studentSectionAssociation                 |        -5| 
        | teacherSectionAssociation                 |       -11|                         
    And I should not see "352e8570bd1116d11a72755b987902440045d346_id" in the "Midgar" database
	
	Scenario: Delete Course with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "course" records like below in "Midgar" tenant. And I save this query as "course"
        |field                                     |value                                                                                 |
        |_id                                       |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
	  Then there exist "2" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
	      |field                                     |value                                                                                 |
	      |body.courseId                             |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
	  Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
	      |field                                     |value                                                                                 |
	      |body.courseId                             |c818a2f609d4190166b96327d86086fe09f877ea_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeCourseDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeCourseDelete.zip" is completed in database
    And a batch job log has been created
    And I should see "Processed 1 records." in the resulting batch job file
	  And I should see "records deleted successfully: 0" in the resulting batch job file
	  And I should see "records failed processing: 1" in the resulting batch job file
#    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "course" to get "1" records
    And I re-execute saved query "courseOffering" to get "2" records
    And I re-execute saved query "courseTranscript" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|    
        | course                                    |         0| 
#        | recordHash                                |      	 -1|

           
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
    And a batch job log has been created
    And I should see "Processed 1 records." in the resulting batch job file
	  And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
#    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "course" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|    
        | course                                    |        -1| 
#        | recordHash                                |      	 -1|
