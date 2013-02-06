@US4121
Feature: Database Index Validation

Background: I have a landing zone route configured
Given I am using local data store 

Scenario: Ingestion for existing tenant, both sli and ingestion_batch_job index validation pass
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak" 
And the tenant database for "Midgar" does not exist 
And the tenantIsReady flag for the tenant "Midgar" is reset 
And I post "tenant.zip" file as the payload of the ingestion job 
When zip file is scp to ingestion landing zone 
And a batch job for file "tenant.zip" is completed in database 
And I post "noRecord.zip" file as the payload of the ingestion job 
When zip file is scp to ingestion landing zone 
And a batch job for file "noRecord.zip" is completed in database 
And I should see "All records processed successfully." in the resulting batch job file

@wip
Scenario: Ingestion for existing tenant, sli index validation fail
Then the following collections are missing indexes in sli datastore
| collectionName              | indexes           |
| learningObjective           | body.learningObjectiveId  |
| learningObjective           | body.objective            |
| section                     | body.assessmentReferences |
| section                     | body.courseOfferingId     |
And I post "noRecord.zip" file as the payload of the ingestion job 
When zip file is scp to ingestion landing zone 
And a batch job for file "noRecord.zip" is completed in database 
And I should see "Index Validation Error" in the resulting error log file
Then the following collections rebuild indexes in sli datastore
| collectionName              | indexes           |
| learningObjective           | body.learningObjectiveId  |
| learningObjective           | body.objective            |
| section                     | body.assessmentReferences |
| section                     | body.courseOfferingId     |
And I post "tenant.zip" file as the payload of the ingestion job 
When zip file is scp to ingestion landing zone 
And a batch job for file "tenant.zip" is completed in database 
And I should see "All records processed successfully." in the resulting batch job file

@wip
Scenario: Ingestion for existing tenant, both sli and ingestion_batch_job index validation fail
Then the following collections are missing indexes in sli datastore
| collectionName              | indexes           |
| learningObjective           | body.learningObjectiveId  |
| learningObjective           | body.objective            |
| section                     | body.assessmentReferences |
| section                     | body.courseOfferingId     |
Then the following collections are missing indexes in ingestion_batch_job datastore
| collectionName              | indexes           |
| persistenceLatch            | syncStage                 |
| persistenceLatch            | jobId                     |
| transformationLatch         | syncStage                 |
| transformationLatch         | recordType                |
And I post "noRecord.zip" file as the payload of the ingestion job 
When zip file is scp to ingestion landing zone 
And a batch job for file "noRecord.zip" is completed in database 
And I should see "Index Validation Error" in the resulting error log file
Then the following collections rebuild indexes in sli datastore
| collectionName              | indexes           |
| learningObjective           | body.learningObjectiveId  |
| learningObjective           | body.objective            |
| section                     | body.assessmentReferences |
| section                     | body.courseOfferingId     |
Then the following collections rebuild indexes in ingestion_batch_job datastore
| collectionName              | indexes           |
| persistenceLatch            | syncStage                 |
| persistenceLatch            | jobId                     |
| transformationLatch         | syncStage                 |
| transformationLatch         | recordType                |
And I post "tenant.zip" file as the payload of the ingestion job 
When zip file is scp to ingestion landing zone 
And a batch job for file "tenant.zip" is completed in database 
And I should see "All records processed successfully." in the resulting batch job file

@wip
Scenario: Ingestion for existing tenant, ingestion_batch_job index validation fail
Then the following collections are missing indexes in ingestion_batch_job datastore
| collectionName              | indexes           |
| persistenceLatch            | syncStage                 |
| persistenceLatch            | jobId                     |
| transformationLatch         | syncStage                 |
| transformationLatch         | recordType                |
And I post "noRecord.zip" file as the payload of the ingestion job 
When zip file is scp to ingestion landing zone 
And a batch job for file "noRecord.zip" is completed in database 
And I should see "Index Validation Error" in the resulting error log file
Then the following collections rebuild indexes in ingestion_batch_job datastore
| collectionName              | indexes           |
| persistenceLatch            | syncStage                 |
| persistenceLatch            | jobId                     |
| transformationLatch         | syncStage                 |
| transformationLatch         | recordType                |
And I post "tenant.zip" file as the payload of the ingestion job 
When zip file is scp to ingestion landing zone 
And a batch job for file "tenant.zip" is completed in database 
And I should see "All records processed successfully." in the resulting batch job file