@RALLY_US5180
Feature: Assessment Super doc delete 

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Ingest Assessment and Sub Docs, delete Assessment, An empty body of Assessment and all Sub Docs should still be there
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    
    #Ingest Super Doc and Sub Docs
	And I post "SuperAssessmentAll.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SuperAssessmentAll.zip" is completed in database		
    Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                                                           |value                                                                                          |
	|_id                                                             |458b6701422b88c1e3d01dd217bc7e76f77621a4_id                                                    |	
    Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentGradeLevelAssessed"
	|field                                                           |value                                                                                          |
	|body.gradeLevelAssessed                                         |Third grade                                                   								 |	
    Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentFamilyRef"
	|field                                                           |value                                                                                          |
	|body.assessmentFamilyReference                                  |1b37e1eb4516a453fece7f01b5af7a3fb86741a8_id                                                    |	
    Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentPeriodDesp"
	|field                                                           |value                                                                                          |
	|body.assessmentPeriodDescriptorId                               |8ee096782782740ca1b846b0ad4c269f032e8aa5_id                                                    |	
    Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentIdentificationCode"
	|field                                                           |value                                                                                          |
	|body.assessmentIdentificationCode.ID                            |2002-Third grade Assessment 1                                                                  |	
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentItem"
	|field                                                           |value                                                                                          |
	|assessmentItem._id                                              |458b6701422b88c1e3d01dd217bc7e76f77621a4_id6d4739bad4415d4032f3f75cada26ff265a4972f_id         |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessment"
	|field                                                           |value                                                                                          |
	|objectiveAssessment._id                                         |458b6701422b88c1e3d01dd217bc7e76f77621a4_id444f99dc71f20039278ffa4354e1d0541e9cd986_id         |					   
    And I save the collection counts in "Midgar" tenant
	#Delete Super Doc   
    And I post "SuperAssessmentOnlyDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SuperAssessmentOnlyDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	#And I should not see a warning log file created
    And I re-execute saved query "assessment" to get "0" records
    And I re-execute saved query "assessmentGradeLevelAssessed" to get "0" records
    And I re-execute saved query "assessmentFamilyRef" to get "0" records
    And I re-execute saved query "assessmentPeriodDesp" to get "0" records
    And I re-execute saved query "assessmentIdentificationCode" to get "0" records
    And I re-execute saved query "assessmentItem" to get "1" records
    And I re-execute saved query "objectiveAssessment" to get "1" records	
	#Check to see the empty body of Super Doc and Sub Docs are still there
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentItem"
	|field                                                           |value                                                                                          |
	|assessmentItem._id                                              |458b6701422b88c1e3d01dd217bc7e76f77621a4_id6d4739bad4415d4032f3f75cada26ff265a4972f_id         |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessment"
	|field                                                           |value                                                                                          |
	|objectiveAssessment._id                                         |458b6701422b88c1e3d01dd217bc7e76f77621a4_id444f99dc71f20039278ffa4354e1d0541e9cd986_id         |
		