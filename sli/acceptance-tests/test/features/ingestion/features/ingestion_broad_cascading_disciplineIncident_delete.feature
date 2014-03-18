@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Safe Delete DisciplineIncident with Cascade = false, Force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "disciplineIncident"
      |field                                                                |value                                      |
      |_id                                                                  |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "student"
      |field                                                                |value                                      |
      |studentDisciplineIncidentAssociation.body.disciplineIncidentId       |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction"
      |field                                                                |value                                      |
      |body.disciplineIncidentId                                            |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeDisciplineIncidentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeDisciplineIncidentDelete.zip" is completed in database
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentDiscipline.xml"
   	And I should not see a warning log file created
    And I re-execute saved query "disciplineIncident" to get "1" records
    And I re-execute saved query "student" to get "1" records
    And I re-execute saved query "disciplineAction" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                                |     delta|
      | disciplineIncident                        |         0|
      | recordHash                                |         0|
  
      
Scenario: Safe Delete DisciplineIncident by Ref with Cascade = false, Force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "disciplineIncident"
      |field                                                                |value                                      |
      |_id                                                                  |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "student"
      |field                                                                |value                                      |
      |studentDisciplineIncidentAssociation.body.disciplineIncidentId       |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction"
      |field                                                                |value                                      |
      |body.disciplineIncidentId                                            |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeDisciplineIncidentRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeDisciplineIncidentRefDelete.zip" is completed in database
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentDiscipline.xml"
   	And I should not see a warning log file created
    And I re-execute saved query "disciplineIncident" to get "1" records
    And I re-execute saved query "student" to get "1" records
    And I re-execute saved query "disciplineAction" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                                |     delta|
      | disciplineIncident                        |         0|
      | recordHash                                |         0|

      
 Scenario: Delete DisciplineIncident with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "disciplineIncident"
      |field                                                                |value                                      |
      |_id                                                                  |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "student"
      |field                                                                |value                                      |
      |studentDisciplineIncidentAssociation.body.disciplineIncidentId       |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction"
      |field                                                                |value                                      |
      |body.disciplineIncidentId                                            |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceDisciplineIncidentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceDisciplineIncidentDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "All records processed successfully" in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentDiscipline.xml"
    And I re-execute saved query "disciplineIncident" to get "0" records
    And I re-execute saved query "student" to get "1" records
    And I re-execute saved query "disciplineAction" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                                |     delta|
      | disciplineIncident                        |        -1|
      | recordHash                                |        -1|
      
 
 Scenario: Delete DisciplineIncident with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "disciplineIncident"
      |field                                                                |value                                      |
      |_id                                                                  |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "student"
      |field                                                                |value                                      |
      |studentDisciplineIncidentAssociation.body.disciplineIncidentId       |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction"
      |field                                                                |value                                      |
      |body.disciplineIncidentId                                            |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceDisciplineIncidentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceDisciplineIncidentDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "All records processed successfully" in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentDiscipline.xml"
    And I re-execute saved query "disciplineIncident" to get "0" records
    And I re-execute saved query "student" to get "1" records
    And I re-execute saved query "disciplineAction" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                                |     delta|
      | disciplineIncident                        |        -1|
      | recordHash                                |        -1|


  Scenario: Delete DisciplineIncident Reference with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "disciplineIncident"
      |field                                                                |value                                      |
      |_id                                                                  |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "student"
      |field                                                                |value                                      |
      |studentDisciplineIncidentAssociation.body.disciplineIncidentId       |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction"
      |field                                                                |value                                      |
      |body.disciplineIncidentId                                            |e3b0557d742649d5c5ee547a6f9549c9cb80713b_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceDisciplineIncidentRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceDisciplineIncidentRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "All records processed successfully" in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentDiscipline.xml"
    And I re-execute saved query "disciplineIncident" to get "0" records
    And I re-execute saved query "student" to get "1" records
    And I re-execute saved query "disciplineAction" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                                |     delta|
      | disciplineIncident                        |        -1|
      | recordHash                                |        -1|
