@RALLY_US5627
Feature: Ingestion SuperDoc Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Ingestion of StudentAssessment-Orphans(Entities referring to missing StudentAssessments) conforms to out-of-order ingestion
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty

    #Ingest data set with the lone student commented out. aka out of order data set. Then take snapshot.
    Given I post "SuperStudentAssessment_NoAssessment.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperStudentAssessment_NoAssessment.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "assessmentHCount"
    |field                                                |value                                                                   |
    |_id                                                  |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id                             |
    |studentAssessmentItem.body.studentAssessmentId       |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id                             |
    |studentObjectiveAssessment._id                       |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_ide82adaec5b649ec175fad108f36ca030f69437c7_id   |
    |studentObjectiveAssessment.body.objectiveAssessmentId |9f1440a2fe484703700393ee657fed36f4a0be90_idfd73c5577774ee14b01038ea578b7dc83a90671d_id   | 
                            
    Then there exist "0" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "assessmentNHCount"
    |field                                                |value                                                                      |
    |_id                                                  |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id                  |
    |studentAssessmentItem.body.studentAssessmentId       |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id                  |
    |studentObjectiveAssessment._id                       |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_ide82adaec5b649ec175fad108f36ca030f69437c7_id   |
    |studentObjectiveAssessment.body.objectiveAssessmentId |9f1440a2fe484703700393ee657fed36f4a0be90_idfd73c5577774ee14b01038ea578b7dc83a90671d_id   | 
    |body.serialNumber                                    |1 code                                                                     |
    |body.studentId                                       |908404e876dd56458385667fa383509035cd4312_id|
    |body.assessmentId                                    |9f1440a2fe484703700393ee657fed36f4a0be90_id|
    |body.administrationDate                              |2001-09-04                                 |
    |body.administrationEndDate                           |2010-07-22                                 |
    |body.gradeLevelWhenAssessed                          |Grade 13                                   |
    |body.administrationEnvironment                       |Classroom                                  |
    |body.reasonNotTested                                 |Absent                                     |
    |body.administrationLanguage                          |English                                    |


    And I read the following entity in "Midgar" tenant and save it as "hollowStudentAssessment"
    | collection                                          | field                       |      value                                  |
    | studentAssessment                                    | _id                        |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id   |

    #Clear out the database
    And the "Midgar" tenant db is empty
    And I re-execute saved query "assessmentHCount" to get "0" records
    And I re-execute saved query "assessmentNHCount" to get "0" records

    #Ingest same data set again. This time with the lone student not commented out.
    Given I post "SuperStudentAssessment_All.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperStudentAssessment_All.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I re-execute saved query "assessmentHCount" to get "1" records
    And I re-execute saved query "assessmentNHCount" to get "1" records
    And I save the collection counts in "Midgar" tenant
    
      And I read the following entity in "Midgar" tenant and save it as "FullStudentAssessment"
    | collection                                          | field                       |      value                                  |
    | studentAssessment                                    | _id                        |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id   |


    #Delete student
    Given I post "SuperStudentAssessment_Delete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperStudentAssessment_Delete.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I re-execute saved query "assessmentHCount" to get "1" records
    And I re-execute saved query "assessmentNHCount" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |studentAssessment   |   0|
        |recordHash          |   0|
        |studentAssessment<hollow>|    1|

    #Take new snapshot and compare with old snapshot
    And I read again the entity tagged "hollowStudentAssessment" from the "Midgar" tenant and confirm that it is the same
    And I save the collection counts in "Midgar" tenant

    #Reingest Assessment
    Given I post "SuperStudentAssessment_AssessmentOnly.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperStudentAssessment_AssessmentOnly.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I re-execute saved query "assessmentHCount" to get "1" records
    And I re-execute saved query "assessmentNHCount" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |studentAssessment   |   0|
        |recordHash          |   0|
        |studentAssessment<hollow>|    -1|
    
    #Take new snapshot and compare with old snapshot
    And I read again the entity tagged "FullStudentAssessment" from the "Midgar" tenant and confirm that it is the same
