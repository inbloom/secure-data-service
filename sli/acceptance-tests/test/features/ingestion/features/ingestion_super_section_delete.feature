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
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "sectionHCount"
    |field       													  |value                                               |
    |_id         													  |48fcd5a76d5c21262d625718ee26aca9ec0c058f_id         |
    |studentSectionAssociation.body.sectionId                         |48fcd5a76d5c21262d625718ee26aca9ec0c058f_id         |
    |teacherSectionAssociation.body.sectionId                         |48fcd5a76d5c21262d625718ee26aca9ec0c058f_id         |
    |gradebookEntry.body.sectionId                                    |48fcd5a76d5c21262d625718ee26aca9ec0c058f_id         |   
    Then there exist "0" "section" records like below in "Midgar" tenant. And I save this query as "sectionNHCount"
    |field       													  |value                                               |
    |_id         													  |48fcd5a76d5c21262d625718ee26aca9ec0c058f_id         |
    |studentSectionAssociation.body.sectionId                         |48fcd5a76d5c21262d625718ee26aca9ec0c058f_id         |
    |teacherSectionAssociation.body.sectionId                         |48fcd5a76d5c21262d625718ee26aca9ec0c058f_id         |
    |gradebookEntry.body.sectionId                                    |48fcd5a76d5c21262d625718ee26aca9ec0c058f_id         |   
    |body.uniqueSectionCode											  |12                                                  |
    |body.sessionId  												  |14e299a360cda49695ef9a0e0ed2f641b58828c2_id         |
    |body.schoolId													  |a13489364c2eb015c219172d561c62350f0453f3_id         |
    |body.courseOfferingId											  |76035e5149c1674bece4311f52d52b2dcc51165d_id         |
    |body.availableCredit.credit									  |int(2)												   |
    And I read the following entity in "Midgar" tenant and save it as "hollowSection"
    | collection | field | value								      |
    | section    | _id   |48fcd5a76d5c21262d625718ee26aca9ec0c058f_id |
    
    #Clear out the database
    And the "Midgar" tenant db is empty
    And I re-execute saved query "sectionHCount" to get "0" records
    
    #Ingest same data set again. This time with the lone student not commented out.
    And I post "SuperSectionAll.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperSectionAll.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I re-execute saved query "sectionHCount" to get "1" records
    And I re-execute saved query "sectionNHCount" to get "1" records

    #Delete section   
    And I post "SuperSectionOnlyDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperSectionOnlyDelete.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I re-execute saved query "sectionHCount" to get "1" records
    And I re-execute saved query "sectionNHCount" to get "0" records
    
    #Take new snapshot and compare with old snapshot
    And I read again the entity tagged "hollowSection" from the "Midgar" tenant and confirm that it is the same
    
    #Reingest section
    Given I post "SuperSectionOnly.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperSectionOnly.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I re-execute saved query "sectionHCount" to get "1" records
    And I re-execute saved query "sectionNHCount" to get "1" records
	

