@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

#objectiveAssessment	studentAssessment 	objectiveAssessment	1	1	 missing!		


@wip
Scenario: Delete Objective Assessment From Student Assessment with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessment"
	|field                                                           |value                                                                                          |
	|objectiveAssessment._id                                         |58346902a070426a109f451129eeeb1268daed21_idd705e26a138eb9e608e23b4c82fd6257633b7244_id         |
#	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "subobjectiveAssessment"
#	|field                                                           |value                                                                                          |
#	|objectiveAssessment.body.subObjectiveAssessment                 |2001-First grade Assessment 1.OA-0                                                             |
#	Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
#	|field                                                           |value                                                                                          |
#	|studentObjectiveAssessment.body.objectiveAssessmentId           |58346902a070426a109f451129eeeb1268daed21_idd705e26a138eb9e608e23b4c82fd6257633b7244_id         |
   	And I save the collection counts in "Midgar" tenant
    And I post "BroadObjectiveAssessmentFromStudentAssessmentDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadObjectiveAssessmentFromStudentAssessmentDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "objectiveAssessment" to get "0" records
#	And I re-execute saved query "subobjectiveAssessment" to get "0" records
#	And I re-execute saved query "studentAssessment" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|objectiveAssessment                    |        -1|
	|recordHash                             |         0|
	And I should not see "58346902a070426a109f451129eeeb1268daed21_idd705e26a138eb9e608e23b4c82fd6257633b7244_id" in the "Midgar" database
    And I should not see any entity mandatorily referring to "58346902a070426a109f451129eeeb1268daed21_idd705e26a138eb9e608e23b4c82fd6257633b7244_id" in the "Midgar" database
	And I should see entities optionally referring to "58346902a070426a109f451129eeeb1268daed21_idd705e26a138eb9e608e23b4c82fd6257633b7244_id" be updated in the "Midgar" database

#objectiveAssessment  objectiveAssessment  subObjectiveAssessment relationship missing
#objectiveAssessment  studentAssessment    objectiveAssessmentId relationship missing		

@wip
Scenario: Delete Objective Assessment From Assessment Metadata with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessment"
	|field                                                           |value                                                                                          |
	|objectiveAssessment._id                                         |938404a8790b90361f61ad35e7aa82d1dc97c8e2_idcd64d9ea394bd362cd32c25e4953cd6549ee508d_id         |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "subobjectiveAssessment"
	|field                                                           |value                                                                                          |
	|objectiveAssessment.body.subObjectiveAssessment                 |2001-First grade Assessment 1.OA-0 Sub                                                         |
	Then there exist "2" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
	|field                                                           |value                                                                                          |
	|studentObjectiveAssessment.body.objectiveAssessmentId           |938404a8790b90361f61ad35e7aa82d1dc97c8e2_idcd64d9ea394bd362cd32c25e4953cd6549ee508d_id         |
	And I save the collection counts in "Midgar" tenant
	And I post "SafeObjectiveAssessmentFromStudentAssessmentDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeObjectiveAssessmentFromStudentAssessmentDelete.zip" is completed in database
    And a batch job log has been created
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeAssessmentMetadata.xml"
   	And I should not see a warning log file created
	And I re-execute saved query "objectiveAssessment" to get "1" records
	And I re-execute saved query "subobjectiveAssessment" to get "1" records
	And I re-execute saved query "studentAssessment" to get "2" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|objectiveAssessment                    |         0|
	|recordHash                             |         0|
	

Scenario: Delete Orphan Objective Assessment From Assessment Metadata with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported

	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessment"
	|field                                                           |value                                                                                          |
	|objectiveAssessment._id                                         |b840858f2c106a12f138fe1be69f5959257bc14a_ida863e2e45fbcc39ba32e02c06d32d3e9ac69d578_id         |
	And I save the collection counts in "Midgar" tenant
	And I post "OrphanObjectiveAssessmentFromStudentAssessmentDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanObjectiveAssessmentFromStudentAssessmentDelete.zip" is completed in database
    And a batch job log has been created
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
	And I re-execute saved query "objectiveAssessment" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|objectiveAssessment                    |        -1|
	|assessment                             |        -1|
	#|recordHash                             |        -1|
	
	
Scenario: Delete Orphan Objective Assessment Reference From Assessment Metadata with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessment"
	|field                                                           |value                                                                                          |
	|objectiveAssessment._id                                         |b840858f2c106a12f138fe1be69f5959257bc14a_ida863e2e45fbcc39ba32e02c06d32d3e9ac69d578_id         |
	And I save the collection counts in "Midgar" tenant
	And I post "OrphanObjectiveAssessmentFromStudentAssessmentRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanObjectiveAssessmentFromStudentAssessmentRefDelete.zip" is completed in database
    And a batch job log has been created
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
	And I re-execute saved query "objectiveAssessment" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|objectiveAssessment                    |        -1|
	|assessment                             |        -1|
	#|recordHash                             |        -1|
	
	
Scenario: Delete Objective Assessment From Assessment Metadata with default settings (Confirm that by default cascade = false, force = true and log violations = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessment"
	|field                                                           |value                                                                                          |
	|objectiveAssessment._id                                         |58346902a070426a109f451129eeeb1268daed21_iddadf8836650e994cb05e032558bf46391cb432d2_id         |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "subobjectiveAssessment"
	|field                                                           |value                                                                                          |
	|objectiveAssessment.body.subObjectiveAssessment                 |2001-First grade Assessment 1.OA-0 Sub                                                         |
	#Then there exist "2" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
	#|field                                                           |value                                                                                          |
	#|studentObjectiveAssessment.body.objectiveAssessmentId           |58346902a070426a109f451129eeeb1268daed21_iddadf8836650e994cb05e032558bf46391cb432d2_id         |
    And I post "ForceObjectiveAssessmentFromStudentAssessmentDelete.zip" file as the payload of the ingestion job
	And I save the collection counts in "Midgar" tenant
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceObjectiveAssessmentFromStudentAssessmentDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "objectiveAssessment" to get "0" records
	And I re-execute saved query "subobjectiveAssessment" to get "1" records
#	And I re-execute saved query "studentAssessment" to get "2" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|objectiveAssessment                    |        -1|
	|recordHash                             |         0|
	

Scenario: Delete Objective Assessment Reference From Assessment Metadata with default settings (Confirm that by default cascade = false, force = true and log violations = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessment"
	|field                                                           |value                                                                                          |
	|objectiveAssessment._id                                         |58346902a070426a109f451129eeeb1268daed21_iddadf8836650e994cb05e032558bf46391cb432d2_id         |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "subobjectiveAssessment"
	|field                                                           |value                                                                                          |
	|objectiveAssessment.body.subObjectiveAssessment                 |2001-First grade Assessment 1.OA-0 Sub                                                         |
	#Then there exist "2" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
	#|field                                                          |value                                                                                          |
	#|studentObjectiveAssessment.body.objectiveAssessmentId          |58346902a070426a109f451129eeeb1268daed21_iddadf8836650e994cb05e032558bf46391cb432d2_id         |
    And I post "ForceObjectiveAssessmentRefFromStudentAssessmentDelete.zip" file as the payload of the ingestion job
	And I save the collection counts in "Midgar" tenant
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceObjectiveAssessmentRefFromStudentAssessmentDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "objectiveAssessment" to get "0" records
	And I re-execute saved query "subobjectiveAssessment" to get "1" records
#	And I re-execute saved query "studentAssessment" to get "2" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|objectiveAssessment                    |        -1|
	|recordHash                             |         0|
