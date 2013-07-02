@RALLY_US5627
Feature: Ingestion SuperDoc Deletion

Scenario: Ingestion of StudentAssessment-Orphans(Entities referring to missing StudentAssessments) conforms to out-of-order ingestion

    #Ingest data set with the lone student commented out. aka out of order data set. Then take snapshot.
    Given the "Midgar" tenant db is empty
    When I ingest "SuperStudentAssessment_NoAssessment.zip"
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "assessmentHollowCount"
    |field                                                |value                                                                   |
    |_id                                                  |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id                             |
    |studentAssessmentItem.body.studentAssessmentId       |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id                             |
    |studentObjectiveAssessment._id                       |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_ide82adaec5b649ec175fad108f36ca030f69437c7_id   |
    |studentObjectiveAssessment.body.objectiveAssessmentId |9f1440a2fe484703700393ee657fed36f4a0be90_idfd73c5577774ee14b01038ea578b7dc83a90671d_id   | 
    Then there exist "0" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "assessmentNonHollowCount"
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
    |body.administrationLanguage.language                 |English                                    |
    And I read the following entity in "Midgar" tenant and save it as "hollowStudentAssessment"
    | collection                                          | field                       |      value                                  |
    | studentAssessment                                    | _id                        |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id   |
    And the "Midgar" tenant db is empty
    And I re-execute saved query "assessmentHollowCount" to get "0" records
    And I re-execute saved query "assessmentNonHollowCount" to get "0" records

    #Ingest data but with assessment
    When I ingest "SuperStudentAssessment_All.zip"
    And I re-execute saved query "assessmentHollowCount" to get "1" records
    And I re-execute saved query "assessmentNonHollowCount" to get "1" records
    And I save the collection counts in "Midgar" tenant
    And I read the following entity in "Midgar" tenant and save it as "FullStudentAssessment"
    | collection                                          | field                       |      value                                  |
    | studentAssessment                                    | _id                        |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id   |

    #Delete student
    Given I post "SuperStudentAssessment_Delete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperStudentAssessment_Delete.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And the only errors I want to see in the resulting warning log file for "InterchangeStudentAssessment.xml" are below
        | code    |
        | CORE_0066|
    And I re-execute saved query "assessmentHollowCount" to get "1" records
    And I re-execute saved query "assessmentNonHollowCount" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |studentAssessment          |    0|
        |recordHash                 |   -1|
        |studentAssessment<hollow>  |    1|
    And I read again the entity tagged "hollowStudentAssessment" from the "Midgar" tenant and confirm that it is the same
    And I save the collection counts in "Midgar" tenant

    #Reingest Assessment
    When I ingest "SuperStudentAssessment_AssessmentOnly.zip"
    And I re-execute saved query "assessmentHollowCount" to get "1" records
    And I re-execute saved query "assessmentNonHollowCount" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |studentAssessment          |    0|
        |recordHash                 |    1|
        |studentAssessment<hollow>  |   -1|
    And I read again the entity tagged "FullStudentAssessment" from the "Midgar" tenant and confirm that it is the same
