@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Student with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "student"
        |field                                     |value                                                                                 |
        |_id                                       |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "6" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
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
        |cohort._id                                |271a8d3a18ae2d80599dc55a0abaaeb8527ff10f_id                                           |
    Then there exist "5" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "studentCompetency"
        |field                                     |value                                                                                 |
        |body.studentSectionAssociationId          |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|  
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentDisciplineIncidentAssociation"
        |field                                     |value                                                                                 |
        |studentDisciplineIncidentAssociation.body.studentId  |908404e876dd56458385667fa383509035cd4312_id                                |
    Then there exist "139" "studentGradebookEntry" records like below in "Midgar" tenant. And I save this query as "studentGradebookEntry"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentParentAssociation"
        |field                                     |value                                                                                 |
        |studentParentAssociation.body.studentId   |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "3" "program" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
        |field                                     |value                                                                                 |
        |studentProgramAssociation.body.studentId  |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "studentSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "studentSchoolAssociation"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentSectionAssociation"
        |field                                     |value                                                                                 |
        |section._id                               |2c77a1e5896b8ea9504e91e324c199e95130878d_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "BroadStudentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BroadStudentDelete.zip" is completed in database
    And a batch job log has been created
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "student" to get "0" records
    And I re-execute saved query "attendance" to get "0" records
    And I re-execute saved query "disciplineAction" to get "0" records
    And I re-execute saved query "courseTranscript" to get "0" records
    And I re-execute saved query "grade" to get "0" records
    And I re-execute saved query "reportCard" to get "0" records
    And I re-execute saved query "studentAcademicRecord" to get "0" records
    And I re-execute saved query "studentAssessment" to get "0" records
    And I re-execute saved query "studentAssessmentItem" to get "0" records    
    And I re-execute saved query "studentCohortAssociation" to get "0" records
    And I re-execute saved query "studentCompetency" to get "0" records
    And I re-execute saved query "studentDisciplineIncidentAssociation" to get "0" records
    And I re-execute saved query "studentGradebookEntry" to get "0" records
    And I re-execute saved query "studentParentAssociation" to get "0" records
    And I re-execute saved query "studentProgramAssociation" to get "0" records
    And I re-execute saved query "studentSchoolAssociation" to get "0" records
    And I re-execute saved query "studentSectionAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | student                                   |        -1|
#        | recordHash                                |      	 -1|
        | attendance	                              |      	 -2|
        | courseTranscript                          |      	 -6|          
        | disciplineAction                          |        -8|   
        | grade                                     |        -6|
        | reportCard                                |				 -2|  
        | studentAcademicRecord                     |				 -2|        
        | studentAssessment                         |				 -4| 
        | studentAssessmentItem                     |				 -4|                        
        | studentCohortAssociation                  |        -1|
        | studentCompetency                         |       -30|                 
        | studentDisciplineIncidentAssociation      |				 -8|  
        | studentGradebookEntry                     |      -139|
        | studentObjectiveAssessment                |        -4|                
        | studentParentAssociation                  |				 -2|        
        | studentProgramAssociation                 |				 -5|        
        | studentSchoolAssociation                  |				 -2|        
        | studentSectionAssociation                 |				 -6| 
        | yearlyTranscript                          |				 -2|          
    And I should not see "908404e876dd56458385667fa383509035cd4312_id" in the "Midgar" database
