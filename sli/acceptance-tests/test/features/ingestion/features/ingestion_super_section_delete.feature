@RALLY_US5635
Feature: Super Section Deletion

Background: I have a landing zone route configured
Given I am using local data store


Scenario: Ingestion of Student-Orphans(Entities referring to missing student) conforms to Student and Children ingestion followed by Student deletion
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    
    #Ingest data set with the lone student comment out. aka out of order data set. Then take snapshot.
    And I post "SuperSectionSubdocAndDenormOnly.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperSectionSubdocAndDenormOnly.zip" is completed in database
    Then there exist "0" "section" records like below in "Midgar" tenant. And I save this query as "sectionCount"
    |field       |value                                               |
    |_id         |908404e876dd56458385667fa383509035cd4312_id         |   
    And I read the following entity in "Midgar" tenant and save it as "hollowSection"
    | collection | field | value								      |
    | section    | _id   |48fcd5a76d5c21262d625718ee26aca9ec0c058f_id |
    
    #Clear out the database
    And the "Midgar" tenant db is empty
    And I re-execute saved query "sectionCount" to get "0" records
    
    #Ingest same data set again. This time with the lone student not commented out.
    And I post "SuperSectionAll.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperSectionAll.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I re-execute saved query "sectionCount" to get "0" records

    #Delete student    
    And I post "SuperSectionOnlyDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperSectionOnlyDelete.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I re-execute saved query "sectionCount" to get "0" records
    
    #Take new snapshot and compare with old snapshot
    And I read again the entity tagged "hollowSection" from the "Midgar" tenant and confirm that it is the same
	

