@RALLY_US5627
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store


Scenario: Blended Deletes with Force = flase and Force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
                                                              #Full Body Deletes#
    ############################                              Non Orphan,Force False                          #######################################
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "FFstudent"
        |field                                                |value                                                                                 |
        |_id                                                  |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "attendance" records like below in "Midgar" tenant. And I save this query as "FFattendance"
        |field                                                |value                                                                                 |
        |body.studentId                                       |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "7" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "FFcourseTranscript"
        |field                                                |value                                                                                 |
        |body.studentId                                       |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "8" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "FFdisciplineAction"
        |field                                                |value                                                                                 |
        |body.studentId                                       |908404e876dd56458385667fa383509035cd4312_id                                           |
    ##################################################################################################################################################

    ############################                              Non Orphan,Force True                            #######################################
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "FTstudent"
        |field                                                |value                                                                                 |
        |_id                                                  |db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id                                           |
    Then there exist "2" "attendance" records like below in "Midgar" tenant. And I save this query as "FTattendance"
        |field                                                |value                                                                                 |
        |body.studentId                                       |db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id                                           |
    Then there exist "4" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "FTcourseTranscript"
        |field                                                |value                                                                                 |
        |body.studentId                                       |db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id                                           |
    Then there exist "2" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "FTdisciplineAction"
        |field                                                |value                                                                                 |
        |body.studentId                                       |db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id                                           |
    ##################################################################################################################################################

    ############################                                   Orphan,Force False                          #######################################
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "OFFstudent"
        |field                                                |value                                                                                 |
        |_id                                                  |d054b5e51b007508752e8038f073ebf3000b6cdb_id                                           |
    Then there exist "0" "attendance" records like below in "Midgar" tenant. And I save this query as "OFFattendance"
        |field                                                |value                                                                                 |
        |body.studentId                                       |d054b5e51b007508752e8038f073ebf3000b6cdb_id                                           |
    Then there exist "0" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "OFFcourseTranscript"
        |field                                                |value                                                                                 |
        |body.studentId                                       |d054b5e51b007508752e8038f073ebf3000b6cdb_id                                           |
    Then there exist "0" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "OFFdisciplineAction"
        |field                                                |value                                                                                 |
        |body.studentId                                       |d054b5e51b007508752e8038f073ebf3000b6cdb_id                                           |
    ##################################################################################################################################################

    ############################                                   Orphan,Force True                          #######################################
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "OFTstudent"
        |field                                                |value                                                                                 |
        |_id                                                  |7f3fdb6e7200794ae5bddcc4c5c46f56f07984f8_id                                           |
    Then there exist "0" "attendance" records like below in "Midgar" tenant. And I save this query as "OFTattendance"
        |field                                                |value                                                                                 |
        |body.studentId                                       |7f3fdb6e7200794ae5bddcc4c5c46f56f07984f8_id                                           |
    Then there exist "0" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "OFTcourseTranscript"
        |field                                                |value                                                                                 |
        |body.studentId                                       |7f3fdb6e7200794ae5bddcc4c5c46f56f07984f8_id                                           |
    Then there exist "0" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "OFTdisciplineAction"
        |field                                                |value                                                                                 |
        |body.studentId                                       |7f3fdb6e7200794ae5bddcc4c5c46f56f07984f8_id                                           |
    ##################################################################################################################################################

                                                       #Reference Deletes#

    ############################                              Non Orphan,Force False                          #######################################
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "RNOFFstudent"
        |field                                                |value                                                                                 |
        |_id                                                  |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "2" "attendance" records like below in "Midgar" tenant. And I save this query as "RNOFFattendance"
        |field                                                |value                                                                                 |
        |body.studentId                                       |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "7" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "RNOFFcourseTranscript"
        |field                                                |value                                                                                 |
        |body.studentId                                       |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "8" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "RNOFFdisciplineAction"
        |field                                                |value                                                                                 |
        |body.studentId                                       |908404e876dd56458385667fa383509035cd4312_id                                           |
    ##################################################################################################################################################

    ############################                              Non Orphan,Force True                          #######################################
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "RNOFTstudent"
        |field                                                |value                                                                                 |
        |_id                                                  |2a83c3be5091796b40601b1215c62af421da6f8d_id                                           |
    Then there exist "2" "attendance" records like below in "Midgar" tenant. And I save this query as "RNOFTattendance"
        |field                                                |value                                                                                 |
        |body.studentId                                       |2a83c3be5091796b40601b1215c62af421da6f8d_id                                           |
    Then there exist "2" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "RNOFTcourseTranscript"
        |field                                                |value                                                                                 |
        |body.studentId                                       |2a83c3be5091796b40601b1215c62af421da6f8d_id                                           |
     Then there exist "3" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "RNOFTdisciplineAction"
        |field                                                |value                                                                                 |
        |body.studentId                                       |2a83c3be5091796b40601b1215c62af421da6f8d_id                                           |
    ##################################################################################################################################################
    And I save the collection counts in "Midgar" tenant
    And I post "BlendedDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BlendedDelete.zip" is completed in database

	And I should see "Processed 6 records." in the resulting batch job file
    And I should see "records deleted successfully: 4" in the resulting batch job file
	And I should see "records failed processing: 2" in the resulting batch job file
    #Error file is created for Force="false" Non Orphan Record
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentParent.xml"
    #Warning file is created for Force="true" Non Orphan Record
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentParent.xml"

    #Non Orphan Force = "false" fails
    And I re-execute saved query "FFstudent" to get "1" records
    And I re-execute saved query "FFattendance" to get "2" records
    And I re-execute saved query "FFcourseTranscript" to get "7" records
    And I re-execute saved query "FFdisciplineAction" to get "8" records

    #Non Orphan Force = "true" passes
    And I re-execute saved query "FTstudent" to get "1" records
    And I re-execute saved query "FTattendance" to get "2" records
    And I re-execute saved query "FTcourseTranscript" to get "4" records
    And I re-execute saved query "FTdisciplineAction" to get "2" records

    #Orphan Force = "false" passes
    And I re-execute saved query "OFFstudent" to get "0" records
    And I re-execute saved query "OFFattendance" to get "0" records
    And I re-execute saved query "OFFcourseTranscript" to get "0" records
    And I re-execute saved query "OFFdisciplineAction" to get "0" records

    #Orphan Force = "true" passes
    And I re-execute saved query "OFTstudent" to get "0" records
    And I re-execute saved query "OFTattendance" to get "0" records
    And I re-execute saved query "OFTcourseTranscript" to get "0" records
    And I re-execute saved query "OFTdisciplineAction" to get "0" records

    #Non Orphan Reference Force = "false" fails
    And I re-execute saved query "RNOFFstudent" to get "1" records
    And I re-execute saved query "RNOFFattendance" to get "2" records
    And I re-execute saved query "RNOFFcourseTranscript" to get "7" records
    And I re-execute saved query "RNOFFdisciplineAction" to get "8" records

    #Non Orphan Reference Force = "true" passes
    And I re-execute saved query "RNOFTstudent" to get "1" records
    And I re-execute saved query "RNOFTattendance" to get "2" records
    And I re-execute saved query "RNOFTcourseTranscript" to get "2" records
    And I re-execute saved query "RNOFTdisciplineAction" to get "3" records

#    And I see that collections counts have changed as follows in tenant "Midgar"
#        | collection                                |     delta|
#        | student                                   |        -3|
#        | recordHash                                |        -3|

  

