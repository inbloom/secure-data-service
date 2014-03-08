@RALLY_US5180

Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Staff by Reference with cascade = false and force=false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staff" records like below in "Midgar" tenant. And I save this query as "Staff"
      |field                                        |value                                         |
      |_id                                          |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "DisciplineAction"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "1" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "DisciplineIncident"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "21" "staffCohortAssociation" records like below in "Midgar" tenant. And I save this query as "StaffCohortAssociation"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "13" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "StaffProgramAssociation"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
     Then there exist "1" "staffEducationOrganizationAssociation" records like below in "Midgar" tenant. And I save this query as "StaffEducationOrganizationAssociation"
      |field                                        |value                                         |
      |body.staffReference                          |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStaffRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStaffRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
  	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStaffAssociation.xml"
   	And I should not see a warning log file created
    And I re-execute saved query "Staff" to get "1" records
    And I re-execute saved query "DisciplineAction" to get "1" records
    And I re-execute saved query "DisciplineIncident" to get "1" records
    And I re-execute saved query "StaffCohortAssociation" to get "21" records
    And I re-execute saved query "StaffProgramAssociation" to get "13" records
    And I re-execute saved query "StaffEducationOrganizationAssociation" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta  |
      |staff                                  |    0  |
      |recordHash                             |    0  |  
   
Scenario: Delete Staff with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staff" records like below in "Midgar" tenant. And I save this query as "Staff"
      |field                                        |value                                         |
      |_id                                          |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "DisciplineAction"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "1" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "DisciplineIncident"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "21" "staffCohortAssociation" records like below in "Midgar" tenant. And I save this query as "StaffCohortAssociation"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "13" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "StaffProgramAssociation"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
     Then there exist "1" "staffEducationOrganizationAssociation" records like below in "Midgar" tenant. And I save this query as "StaffEducationOrganizationAssociation"
      |field                                        |value                                         |
      |body.staffReference                          |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStaffDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStaffDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
  	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStaffAssociation.xml"
   	And I should not see a warning log file created
    And I re-execute saved query "Staff" to get "1" records
    And I re-execute saved query "DisciplineAction" to get "1" records
    And I re-execute saved query "DisciplineIncident" to get "1" records
    And I re-execute saved query "StaffCohortAssociation" to get "21" records
    And I re-execute saved query "StaffProgramAssociation" to get "13" records
    And I re-execute saved query "StaffEducationOrganizationAssociation" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta  |
      |staff                                  |    0  |
      |recordHash                             |    0  |  
 

 Scenario: Delete Orphan Staff with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staff" records like below in "Midgar" tenant. And I save this query as "Staff"
      |field                                        |value                                         |
      |_id                                          |795162f2bb9e499df4eab7acbc761b4e9aaf8cb0_id   |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStaffDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanStaffDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file    
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "Staff" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta  |
      |staff                                  |   -1  |
      |recordHash                             |   -1  |
 

 Scenario: Delete Orphan Staff Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staff" records like below in "Midgar" tenant. And I save this query as "Staff"
      |field                                        |value                                         |
      |_id                                          |795162f2bb9e499df4eab7acbc761b4e9aaf8cb0_id   |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStaffRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanStaffRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file    
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "Staff" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta  |
      |staff                                  |   -1  |
      |recordHash                             |   -1  |
      

Scenario: Delete Staff with cascade = false, force = true, logviolation = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staff" records like below in "Midgar" tenant. And I save this query as "Staff"
      |field                                        |value                                         |
      |_id                                          |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "DisciplineAction"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "1" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "DisciplineIncident"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "21" "staffCohortAssociation" records like below in "Midgar" tenant. And I save this query as "StaffCohortAssociation"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "13" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "StaffProgramAssociation"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
     Then there exist "1" "staffEducationOrganizationAssociation" records like below in "Midgar" tenant. And I save this query as "StaffEducationOrganizationAssociation"
      |field                                        |value                                         |
      |body.staffReference                          |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStaffDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStaffDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStaffAssociation.xml"
   	And I should not see an error log file created
    And I re-execute saved query "Staff" to get "0" records
    And I re-execute saved query "DisciplineAction" to get "1" records
    And I re-execute saved query "DisciplineIncident" to get "1" records
    And I re-execute saved query "StaffCohortAssociation" to get "21" records
    And I re-execute saved query "StaffProgramAssociation" to get "13" records
    And I re-execute saved query "StaffEducationOrganizationAssociation" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta  |
      |staff                                  |   -1  |
      |recordHash                             |   -1  |    


Scenario: Delete Staff Reference with cascade = false, force = true, logviolation = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staff" records like below in "Midgar" tenant. And I save this query as "Staff"
      |field                                        |value                                         |
      |_id                                          |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "DisciplineAction"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "1" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "DisciplineIncident"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "21" "staffCohortAssociation" records like below in "Midgar" tenant. And I save this query as "StaffCohortAssociation"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "13" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "StaffProgramAssociation"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
     Then there exist "1" "staffEducationOrganizationAssociation" records like below in "Midgar" tenant. And I save this query as "StaffEducationOrganizationAssociation"
      |field                                        |value                                         |
      |body.staffReference                          |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStaffRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStaffRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStaffAssociation.xml"
   	And I should not see an error log file created
    And I re-execute saved query "Staff" to get "0" records
    And I re-execute saved query "DisciplineAction" to get "1" records
    And I re-execute saved query "DisciplineIncident" to get "1" records
    And I re-execute saved query "StaffCohortAssociation" to get "21" records
    And I re-execute saved query "StaffProgramAssociation" to get "13" records
    And I re-execute saved query "StaffEducationOrganizationAssociation" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta  |
      |staff                                  |   -1  |
      |recordHash                             |   -1  |    
