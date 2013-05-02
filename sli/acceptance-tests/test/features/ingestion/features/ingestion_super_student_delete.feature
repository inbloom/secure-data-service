@RALLY_US5627
Feature: Ingestion SuperDoc Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Ingestion of Student-Orphans(Entities referring to missing student) conforms to Student and Children ingestion followed by Student deletion
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty

    #Ingest data set with the lone student commented out. aka out of order data set. Then take snapshot.
    Given I post "SuperStudent_NoStudent.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperStudent_NoStudent.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I should see "All records processed successfully." in the resulting batch job file
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentHCount"
    |field                                                |value                                                                      |
    |_id                                                  |908404e876dd56458385667fa383509035cd4312_id                                |
    |studentCohortAssociation.body.cohortId               |271a8d3a18ae2d80599dc55a0abaaeb8527ff10f_id                                |
    |studentDisciplineIncidentAssociation.body.studentId  |908404e876dd56458385667fa383509035cd4312_id                                |
    |studentParentAssociation.body.studentId              |908404e876dd56458385667fa383509035cd4312_id                                |
    |studentProgramAssociation.body.programId             |0064dd5bb3bffd47e93b023585e6591c018ee697_id                                |
    |section._id                                          |2c77a1e5896b8ea9504e91e324c199e95130878d_id                                |
    Then there exist "0" "student" records like below in "Midgar" tenant. And I save this query as "studentNHCount"
    |field                                                |value                                                                      |
    |_id                                                  |908404e876dd56458385667fa383509035cd4312_id                                |
    |studentCohortAssociation.body.cohortId               |271a8d3a18ae2d80599dc55a0abaaeb8527ff10f_id                                |
    |studentDisciplineIncidentAssociation.body.studentId  |908404e876dd56458385667fa383509035cd4312_id                                |
    |studentParentAssociation.body.studentId              |908404e876dd56458385667fa383509035cd4312_id                                |
    |studentProgramAssociation.body.programId             |0064dd5bb3bffd47e93b023585e6591c018ee697_id                                |
    |section._id                                          |2c77a1e5896b8ea9504e91e324c199e95130878d_id                                |
    |body.studentUniqueStateId                            |1                                                                          |
    |body.languages                                       |Nepali                                                                     |
    |body.learningStyles.auditoryLearning                 |int(8)                                                                     |
    |body.learningStyles.tactileLearning                  |int(27)                                                                    |
    |body.learningStyles.visualLearning                   |int(24)                                                                    |
    |body.limitedEnglishProficiency                       |NotLimited                                                                 |

    And I read the following entity in "Midgar" tenant and save it as "hollowStudent"
    | collection                                          | field                       |      value                                  |
    | student                                             | _id                         |908404e876dd56458385667fa383509035cd4312_id  |

    #Clear out the database
    And the "Midgar" tenant db is empty
    And I re-execute saved query "studentHCount" to get "0" records
    And I re-execute saved query "studentNHCount" to get "0" records

    #Ingest same data set again. This time with the lone student not commented out.
    Given I post "SuperStudent_All.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperStudent_All.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I should see "All records processed successfully." in the resulting batch job file
    And I re-execute saved query "studentHCount" to get "1" records
    And I re-execute saved query "studentNHCount" to get "1" records
    #Take snapshot of full bodied student
    And I read the following entity in "Midgar" tenant and save it as "nonHollowStudent"
    | collection                                          | field                       |      value                                  |
    | student                                             | _id                         |908404e876dd56458385667fa383509035cd4312_id  |
    #Save collection counts for comparison later (Z1)
    And I save the collection counts in "Midgar" tenant

    #Delete student
    Given I post "SuperStudent_DeleteStudent.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperStudent_DeleteStudent.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentParent.xml"
    And the only errors I want to see in the resulting warning log file for "InterchangeStudentParent.xml" are below
    | code    |
    | CORE_0066|
    #Compare saved collection counts(Z1)
    And I see that collections counts have changed as follows in tenant "Midgar"
    | collection                                |     delta|
    | recordHash                                |        -1|
    | student<hollow>                           |         1|
    #Save collection counts for comparison later (Z2)
    And I save the collection counts in "Midgar" tenant

    And I re-execute saved query "studentHCount" to get "1" records
    And I re-execute saved query "studentNHCount" to get "0" records

    #Take new snapshot of hollow bodied student and compare with old snapshot
    And I read again the entity tagged "hollowStudent" from the "Midgar" tenant and confirm that it is the same

    #Reingest student
    Given I post "SuperStudent_OnlyStudent.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SuperStudent_OnlyStudent.zip" is completed in database
    And I should see "All records processed successfully." in the resulting batch job file
    And I should not see an error log file created
    And I re-execute saved query "studentHCount" to get "1" records
    And I re-execute saved query "studentNHCount" to get "1" records
    #Compare saved collection counts(Z2)
    And I see that collections counts have changed as follows in tenant "Midgar"
    | collection                                |     delta|
    | recordHash                                |         1|
    | student<hollow>                           |        -1|

    #Take new snapshot of full bodied student and compare with old snapshot
    And I read again the entity tagged "nonHollowStudent" from the "Midgar" tenant and confirm that it is the same