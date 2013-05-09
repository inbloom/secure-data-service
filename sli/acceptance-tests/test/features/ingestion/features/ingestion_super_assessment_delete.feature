@RALLY_US5180
Feature: Assessment Super doc delete 

Scenario: Ingestion of Assessment-Orphans(Entities referring to missing Assessment) conforms to Assessment and Children ingestion followed by Assessment deletion

    #Ingest data set with the lone Assessment comment out. aka out of order data set. Then take snapshot.
    Given the "Midgar" tenant db is empty
    When I ingest "SuperAssessmentSubdocAndDenormOnly.zip"
    And I should not see a warning log file created
    Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "AssessmentHollowCount"
    |field       													  |value                                               |
    |_id         													  |458b6701422b88c1e3d01dd217bc7e76f77621a4_id         |
    |assessmentItem.body.assessmentId                          		  |458b6701422b88c1e3d01dd217bc7e76f77621a4_id         |
    |objectiveAssessment.body.assessmentId                            |458b6701422b88c1e3d01dd217bc7e76f77621a4_id         |
    Then there exist "0" "assessment" records like below in "Midgar" tenant. And I save this query as "AssessmentNonHollowCount"
    |field       													  |value                                               |
    |_id         													  |458b6701422b88c1e3d01dd217bc7e76f77621a4_id         |
    |body.gradeLevelAssessed										  |Third grade                                         |
    |body.assessmentFamilyReference  								  |1b37e1eb4516a453fece7f01b5af7a3fb86741a8_id         |
    |body.assessmentPeriodDescriptorId								  |8ee096782782740ca1b846b0ad4c269f032e8aa5_id         |
    |body.assessmentIdentificationCode.ID							  |2002-Third grade Assessment 1                       |
    |body.assessmentIdentificationCode.identificationSystem           |State                                               |
    |body.assessmentTitle                                             |2002-Third grade Assessment 1                       |
    And I read the following entity in "Midgar" tenant and save it as "hollowAssessment"
    | collection | field | value								      |
    | assessment    | _id   |458b6701422b88c1e3d01dd217bc7e76f77621a4_id |
    
    #Ingest same data set again. This time with the lone Assessment not commented out.
    Given the "Midgar" tenant db is empty
    When I ingest "SuperAssessmentAll.zip"
    And I re-execute saved query "AssessmentHollowCount" to get "1" records
    And I re-execute saved query "AssessmentNonHollowCount" to get "1" records
    And I read the following entity in "Midgar" tenant and save it as "nonHollowAssessment"
    | collection                                          | field                       |      value                                  |
    | assessment                                          | _id                         |458b6701422b88c1e3d01dd217bc7e76f77621a4_id  |
    And I save the collection counts in "Midgar" tenant

    #Delete Assessment   
    When I post "SuperAssessmentOnlyDelete.zip" file as the payload of the ingestion job
    And zip file is scp to ingestion landing zone
    Then a batch job for file "SuperAssessmentOnlyDelete.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should not see an error log file created
    And the only errors I want to see in the resulting warning log file for "InterchangeAssessmentMetadata.xml" are below
    | code    |
    | CORE_0066|
    And I see that collections counts have changed as follows in tenant "Midgar"
    | collection                                |     delta|
    | assessment<hollow>                        |         1|
    | recordHash                                |        -1|
    And I save the collection counts in "Midgar" tenant
    And I re-execute saved query "AssessmentHollowCount" to get "1" records
    And I re-execute saved query "AssessmentNonHollowCount" to get "0" records
    And I read again the entity tagged "hollowAssessment" from the "Midgar" tenant and confirm that it is the same
    
    #Reingest Assessment
    When I ingest "SuperAssessmentOnly.zip"
    And I re-execute saved query "AssessmentHollowCount" to get "1" records
    And I re-execute saved query "AssessmentNonHollowCount" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
    | collection                                |     delta|
    | assessment<hollow>                        |        -1|
    | recordHash                                |         1|
    And I read again the entity tagged "nonHollowAssessment" from the "Midgar" tenant and confirm that it is the same
	
