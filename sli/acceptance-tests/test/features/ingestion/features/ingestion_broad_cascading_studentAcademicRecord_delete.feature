@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Student Academic Record with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I should see child entities of entityType "studentAcademicRecord" with id "861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id" in the "Midgar" database	
    And I post "BroadStudentAcademicRecordDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BroadStudentAcademicRecordDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id" in the "Midgar" database
    And I should not see any entity mandatorily referring to "861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id" in the "Midgar" database
	And I should see entities optionally referring to "861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id" be updated in the "Midgar" database

