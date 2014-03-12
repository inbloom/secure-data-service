@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Teacher with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staff" records like below in "Midgar" tenant. And I save this query as "StaffQ"
      |field                                        |value                                         |
      |_id                                          |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "DisciplineActionQ"
      |field                                        |value                                         |
      |body.staffId                                 |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "4" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "DisciplineIncidentQ"
      |field                                        |value                                         |
      |body.staffId                                 |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "staffCohortAssociation" records like below in "Midgar" tenant. And I save this query as "StaffCohortAssociationQ"
      |field                                        |value                                         |
      |body.staffId                                 |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "StaffProgramAssociationQ"
     |field                                        |value                                         |
     |body.staffId                                 |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
     Then there exist "1" "teacherSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "teacherSchoolAssociationQ"
      |field                                        |value                                         |
      |body.teacherId                               |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
     Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "teacherSectionAssociationQ"
      |field                                        |value                                         |
      |teacherSectionAssociation.body.teacherId     |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeTeacherDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeTeacherDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see a warning log file created
    And I re-execute saved query "StaffQ" to get "1" records
    And I re-execute saved query "DisciplineActionQ" to get "1" records
    And I re-execute saved query "DisciplineIncidentQ" to get "4" records
    And I re-execute saved query "StaffCohortAssociationQ" to get "1" records
    And I re-execute saved query "StaffProgramAssociationQ" to get "1" records
    And I re-execute saved query "teacherSchoolAssociationQ" to get "1" records
    And I re-execute saved query "teacherSectionAssociationQ" to get "1" records

    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta  |
      |staff                                  |   0  |


	Scenario: Delete Orphan Teacher with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staff" records like below in "Midgar" tenant. And I save this query as "staff"
        |field                                     |value                                                                                 |
        |_id                                       |31c87f975b69371f2172bbdf5fd8fac612014ba0_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanTeacherDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanTeacherDelete.zip" is completed in database
	  And I should see "Processed 1 records." in the resulting batch job file
		And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	  And I should not see a warning log file created
    And I re-execute saved query "staff" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | staff                                     |        -1|       
        | recordHash                                |        -1|


  Scenario: Delete Orphan Teacher Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staff" records like below in "Midgar" tenant. And I save this query as "staff"
        |field                                     |value                                                                                 |
        |_id                                       |31c87f975b69371f2172bbdf5fd8fac612014ba0_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanTeacherRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanTeacherRefDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "staff" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | staff                                     |        -1|
        | recordHash                                |        -1|

  Scenario: Delete Teacher with cascade = false, force = "true"
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staff" records like below in "Midgar" tenant. And I save this query as "StaffQ"
      |field                                        |value                                         |
      |_id                                          |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "DisciplineActionQ"
      |field                                        |value                                         |
      |body.staffId                                 |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "4" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "DisciplineIncidentQ"
      |field                                        |value                                         |
      |body.staffId                                 |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "staffCohortAssociation" records like below in "Midgar" tenant. And I save this query as "StaffCohortAssociationQ"
      |field                                        |value                                         |
      |body.staffId                                 |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "StaffProgramAssociationQ"
      |field                                        |value                                         |
      |body.staffId                                 |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "teacherSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "teacherSchoolAssociationQ"
      |field                                        |value                                         |
      |body.teacherId                               |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "teacherSectionAssociationQ"
      |field                                        |value                                         |
      |teacherSectionAssociation.body.teacherId     |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceTeacherDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceTeacherDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStaffAssociation.xml"
    And I re-execute saved query "StaffQ" to get "0" records
    And I re-execute saved query "DisciplineActionQ" to get "1" records
    And I re-execute saved query "DisciplineIncidentQ" to get "4" records
    And I re-execute saved query "StaffCohortAssociationQ" to get "1" records
    And I re-execute saved query "StaffProgramAssociationQ" to get "1" records
    And I re-execute saved query "teacherSchoolAssociationQ" to get "1" records
    And I re-execute saved query "teacherSectionAssociationQ" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta|
      |staff                                  |   -1|
      |recordHash                             |   -1|

  Scenario: Delete Teacher Reference with cascade = false, force = "true"
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staff" records like below in "Midgar" tenant. And I save this query as "StaffQ"
      |field                                        |value                                         |
      |_id                                          |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "DisciplineActionQ"
      |field                                        |value                                         |
      |body.staffId                                 |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "4" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "DisciplineIncidentQ"
      |field                                        |value                                         |
      |body.staffId                                 |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "staffCohortAssociation" records like below in "Midgar" tenant. And I save this query as "StaffCohortAssociationQ"
      |field                                        |value                                         |
      |body.staffId                                 |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "StaffProgramAssociationQ"
      |field                                        |value                                         |
      |body.staffId                                 |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "teacherSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "teacherSchoolAssociationQ"
      |field                                        |value                                         |
      |body.teacherId                               |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "teacherSectionAssociationQ"
      |field                                        |value                                         |
      |teacherSectionAssociation.body.teacherId     |4c9cc1f4f35e2e1917c6a27a2dfcf69be47b22bd_id   |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceTeacherRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceTeacherRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStaffAssociation.xml"
    And I re-execute saved query "StaffQ" to get "0" records
    And I re-execute saved query "DisciplineActionQ" to get "1" records
    And I re-execute saved query "DisciplineIncidentQ" to get "4" records
    And I re-execute saved query "StaffCohortAssociationQ" to get "1" records
    And I re-execute saved query "StaffProgramAssociationQ" to get "1" records
    And I re-execute saved query "teacherSchoolAssociationQ" to get "1" records
    And I re-execute saved query "teacherSectionAssociationQ" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta|
      |staff                                  |   -1|
      |recordHash                             |   -1|
