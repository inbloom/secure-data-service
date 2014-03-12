@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Student with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported

    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "student"
        |field                                     |value                                                                                 |
        |_id                                       |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "7" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "8" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "grade"
        |field                                     |value                                                                                 |
        |grade.body.studentId                      |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "reportCard"
        |field                                     |value                                                                                 |
        |reportCard.body.studentId                 |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "studentAcademicRecord"
        |field                                     |value                                                                                 |
        |studentAcademicRecord.body.studentId      |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "4" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessmentItem"
        |field                                     |value                                                                                 |
        |studentAssessmentItem.body.studentAssessmentId |c278f2337a1cfc12b3d5ee3240b99c0457a96990_id                                      |        
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentCohortAssociation"
        |field                                     |value                                                                                 |
        |studentCohortAssociation._id              |908404e876dd56458385667fa383509035cd4312_ide3e74d25c695f1e27d2272bcbe12351cd02a78c1_id|
    Then there exist "6" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "studentCompetency"
        |field                                     |value                                                                                 |
        |body.studentSectionAssociationId          |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|  
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentDisciplineIncidentAssociation"
        |field                                     |value                                                                                 |
        |studentDisciplineIncidentAssociation.body.studentId  |908404e876dd56458385667fa383509035cd4312_id                                |
    Then there exist "139" "studentGradebookEntry" records like below in "Midgar" tenant. And I save this query as "studentGradebookEntry"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentObjectiveAssessment1"
        |field                                     |value                                                                                 |
        |studentObjectiveAssessment.body.studentAssessmentId |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id                                 |        
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentObjectiveAssessment2"
        |field                                     |value                                                                                 |
        |studentObjectiveAssessment.body.studentAssessmentId |c278f2337a1cfc12b3d5ee3240b99c0457a96990_id                                 |        
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentParentAssociation"
        |field                                     |value                                                                                 |
        |studentParentAssociation.body.studentId   |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
        |field                                     |value                                                                                 |
        |studentProgramAssociation.body.studentId  |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "studentSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "studentSchoolAssociation"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentSectionAssociation"
        |field                                     |value                                                                                 |
        |section._id                               |2c77a1e5896b8ea9504e91e324c199e95130878d_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentParent.xml"
       And the only errors I want to see in the resulting error log file for "InterchangeStudentParent.xml" are below
        | code    |
        | CORE_0066|
    And I should not see a warning log file created
    And I re-execute saved query "student" to get "1" records
    And I re-execute saved query "attendance" to get "2" records
    And I re-execute saved query "disciplineAction" to get "8" records
    And I re-execute saved query "courseTranscript" to get "7" records
    And I re-execute saved query "grade" to get "2" records
    And I re-execute saved query "reportCard" to get "2" records
    And I re-execute saved query "studentAcademicRecord" to get "2" records
    And I re-execute saved query "studentAssessment" to get "4" records
    And I re-execute saved query "studentAssessmentItem" to get "1" records    
    And I re-execute saved query "studentCohortAssociation" to get "1" records
    And I re-execute saved query "studentCompetency" to get "6" records
    And I re-execute saved query "studentDisciplineIncidentAssociation" to get "1" records
    And I re-execute saved query "studentGradebookEntry" to get "139" records
    And I re-execute saved query "studentObjectiveAssessment1" to get "1" records
    And I re-execute saved query "studentObjectiveAssessment2" to get "1" records
    And I re-execute saved query "studentParentAssociation" to get "1" records
    And I re-execute saved query "studentProgramAssociation" to get "1" records
    And I re-execute saved query "studentSchoolAssociation" to get "2" records
    And I re-execute saved query "studentSectionAssociation" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | student                                   |         0|        


Scenario: Delete Orphan Student with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "student"
        |field                                     |value                                                                                 |
        |_id                                       |d054b5e51b007508752e8038f073ebf3000b6cdb_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStudentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanStudentDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "student" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | student                                   |        -1|
        | recordHash                                |        -1|


Scenario: Delete Orphan Student Reference with cascade = false, attempt subsequent delete with body and then re-ingest and delete again
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "student"
        |field                                     |value                                                                                 |
        |_id                                       |d054b5e51b007508752e8038f073ebf3000b6cdb_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStudentRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanStudentRefDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "student" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | student                                   |        -1|
        | recordHash                                |        -1|
    When the landing zone is reinitialized
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStudentDelete.zip" file as the payload of the ingestion job
    And zip file is scp to ingestion landing zone with name "OrphanStudentDelete2.zip"
    And a batch job for file "OrphanStudentDelete2.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "CORE_0071" in the resulting error log file for "InterchangeStudentParent.xml"
    And the only errors I want to see in the resulting error log file for "InterchangeStudentParent.xml" are below
        | code    |
        | CORE_0071|
    And I should not see a warning log file created
    And I re-execute saved query "student" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | student                                   |        0|
        | recordHash                                |        0|
    When the landing zone is reinitialized
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStudent.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanStudent.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records ingested successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "student" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | student                                   |        +1|
        | recordHash                                |        +1|
    When the landing zone is reinitialized
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStudentDelete.zip" file as the payload of the ingestion job
    And zip file is scp to ingestion landing zone with name "OrphanStudentDelete3.zip"
    And a batch job for file "OrphanStudentDelete3.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "student" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | student                                   |        -1|
        | recordHash                                |        -1|
  
        
Scenario: Delete Student with cascade = false and force = true, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported

    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "student"
        |field                                     |value                                                                                 |
        |_id                                       |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "7" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "8" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "grade"
        |field                                     |value                                                                                 |
        |grade.body.studentId                      |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "reportCard"
        |field                                     |value                                                                                 |
        |reportCard.body.studentId                 |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "studentAcademicRecord"
        |field                                     |value                                                                                 |
        |studentAcademicRecord.body.studentId      |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "4" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessmentItem"
        |field                                     |value                                                                                 |
        |studentAssessmentItem.body.studentAssessmentId |c278f2337a1cfc12b3d5ee3240b99c0457a96990_id                                      |        
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentCohortAssociation"
        |field                                     |value                                                                                 |
        |studentCohortAssociation._id              |908404e876dd56458385667fa383509035cd4312_ide3e74d25c695f1e27d2272bcbe12351cd02a78c1_id|
    Then there exist "6" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "studentCompetency"
        |field                                     |value                                                                                 |
        |body.studentSectionAssociationId          |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|  
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentDisciplineIncidentAssociation"
        |field                                     |value                                                                                 |
        |studentDisciplineIncidentAssociation.body.studentId  |908404e876dd56458385667fa383509035cd4312_id                                |
    Then there exist "139" "studentGradebookEntry" records like below in "Midgar" tenant. And I save this query as "studentGradebookEntry"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentObjectiveAssessment1"
        |field                                     |value                                                                                 |
        |studentObjectiveAssessment.body.studentAssessmentId |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id                                 |        
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentObjectiveAssessment2"
        |field                                     |value                                                                                 |
        |studentObjectiveAssessment.body.studentAssessmentId |c278f2337a1cfc12b3d5ee3240b99c0457a96990_id                                 |        
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentParentAssociation"
        |field                                     |value                                                                                 |
        |studentParentAssociation.body.studentId   |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
        |field                                     |value                                                                                 |
        |studentProgramAssociation.body.studentId  |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "studentSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "studentSchoolAssociation"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentSectionAssociation"
        |field                                     |value                                                                                 |
        |section._id                               |2c77a1e5896b8ea9504e91e324c199e95130878d_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentParent.xml"
    And the only errors I want to see in the resulting warning log file for "InterchangeStudentParent.xml" are below
        | code    |
        | CORE_0066|
    And I should see "Child reference of entity type attendance" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentSchoolAssociation" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type grade" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentAcademicRecord" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type reportCard" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentAssessment" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentGradebookEntry" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentSectionAssociation" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type disciplineAction" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type courseTranscript" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentDisciplineIncidentAssociation" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentCohortAssociation" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentParentAssociation" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentProgramAssociation" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should not see an error log file created
    And I re-execute saved query "student" to get "1" records
    And I re-execute saved query "attendance" to get "2" records
    And I re-execute saved query "disciplineAction" to get "8" records
    And I re-execute saved query "courseTranscript" to get "7" records
    And I re-execute saved query "grade" to get "2" records
    And I re-execute saved query "reportCard" to get "2" records
    And I re-execute saved query "studentAcademicRecord" to get "2" records
    And I re-execute saved query "studentAssessment" to get "4" records
    And I re-execute saved query "studentAssessmentItem" to get "1" records    
    And I re-execute saved query "studentCohortAssociation" to get "1" records
    And I re-execute saved query "studentCompetency" to get "6" records
    And I re-execute saved query "studentDisciplineIncidentAssociation" to get "1" records
    And I re-execute saved query "studentGradebookEntry" to get "139" records
    And I re-execute saved query "studentObjectiveAssessment1" to get "1" records
    And I re-execute saved query "studentObjectiveAssessment2" to get "1" records
    And I re-execute saved query "studentParentAssociation" to get "1" records
    And I re-execute saved query "studentProgramAssociation" to get "1" records
    And I re-execute saved query "studentSchoolAssociation" to get "2" records
    And I re-execute saved query "studentSectionAssociation" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | recordHash                                |        -1|  
        | student<hollow>                           |         1|

Scenario: Delete Student Ref with cascade = false and force = true, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported

    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "student"
        |field                                     |value                                                                                 |
        |_id                                       |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "7" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "8" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "grade"
        |field                                     |value                                                                                 |
        |grade.body.studentId                      |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "reportCard"
        |field                                     |value                                                                                 |
        |reportCard.body.studentId                 |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "studentAcademicRecord"
        |field                                     |value                                                                                 |
        |studentAcademicRecord.body.studentId      |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "4" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessmentItem"
        |field                                     |value                                                                                 |
        |studentAssessmentItem.body.studentAssessmentId |c278f2337a1cfc12b3d5ee3240b99c0457a96990_id                                      |        
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentCohortAssociation"
        |field                                     |value                                                                                 |
        |studentCohortAssociation._id              |908404e876dd56458385667fa383509035cd4312_ide3e74d25c695f1e27d2272bcbe12351cd02a78c1_id|
    Then there exist "6" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "studentCompetency"
        |field                                     |value                                                                                 |
        |body.studentSectionAssociationId          |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|  
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentDisciplineIncidentAssociation"
        |field                                     |value                                                                                 |
        |studentDisciplineIncidentAssociation.body.studentId  |908404e876dd56458385667fa383509035cd4312_id                                |
    Then there exist "139" "studentGradebookEntry" records like below in "Midgar" tenant. And I save this query as "studentGradebookEntry"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentObjectiveAssessment1"
        |field                                     |value                                                                                 |
        |studentObjectiveAssessment.body.studentAssessmentId |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id                                 |        
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentObjectiveAssessment2"
        |field                                     |value                                                                                 |
        |studentObjectiveAssessment.body.studentAssessmentId |c278f2337a1cfc12b3d5ee3240b99c0457a96990_id                                 |        
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentParentAssociation"
        |field                                     |value                                                                                 |
        |studentParentAssociation.body.studentId   |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
        |field                                     |value                                                                                 |
        |studentProgramAssociation.body.studentId  |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "studentSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "studentSchoolAssociation"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentSectionAssociation"
        |field                                     |value                                                                                 |
        |section._id                               |2c77a1e5896b8ea9504e91e324c199e95130878d_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentRefDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentParent.xml"
     And the only errors I want to see in the resulting warning log file for "InterchangeStudentParent.xml" are below
        | code    |
        | CORE_0066|
    And I should see "Child reference of entity type attendance" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentSchoolAssociation" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type grade" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentAcademicRecord" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type reportCard" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentAssessment" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentGradebookEntry" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentSectionAssociation" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type disciplineAction" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type courseTranscript" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentDisciplineIncidentAssociation" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentCohortAssociation" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentParentAssociation" in the resulting warning log file for "InterchangeStudentParent.xml"
    And I should see "Child reference of entity type studentProgramAssociation" in the resulting warning log file for "InterchangeStudentParent.xml"
     And I should not see an error log file created
    And I re-execute saved query "student" to get "1" records
    And I re-execute saved query "attendance" to get "2" records
    And I re-execute saved query "disciplineAction" to get "8" records
    And I re-execute saved query "courseTranscript" to get "7" records
    And I re-execute saved query "grade" to get "2" records
    And I re-execute saved query "reportCard" to get "2" records
    And I re-execute saved query "studentAcademicRecord" to get "2" records
    And I re-execute saved query "studentAssessment" to get "4" records
    And I re-execute saved query "studentAssessmentItem" to get "1" records    
    And I re-execute saved query "studentCohortAssociation" to get "1" records
    And I re-execute saved query "studentCompetency" to get "6" records
    And I re-execute saved query "studentDisciplineIncidentAssociation" to get "1" records
    And I re-execute saved query "studentGradebookEntry" to get "139" records
    And I re-execute saved query "studentObjectiveAssessment1" to get "1" records
    And I re-execute saved query "studentObjectiveAssessment2" to get "1" records
    And I re-execute saved query "studentParentAssociation" to get "1" records
    And I re-execute saved query "studentProgramAssociation" to get "1" records
    And I re-execute saved query "studentSchoolAssociation" to get "2" records
    And I re-execute saved query "studentSectionAssociation" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | recordHash                                |        -1|          
        | student<hollow>                           |         1|


