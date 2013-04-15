@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete LearningStandard with cascade = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "learningStandard" records like below in "Midgar" tenant. And I save this query as "learningStandard"
	|field                                             |value                                                |
	|_id                                               |b0e0b7466429d5d994f18da6982c3af25d1496c3_id          |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                                             |value                                                |
	|objectiveAssessment.body.learningObjectives       |1b0d13e233ef61ffafb613a8cc6930dfc0d29b92_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "BroadLearningStandardDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadLearningStandardDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "b0e0b7466429d5d994f18da6982c3af25d1496c3_id" in the "Midgar" database
	
Scenario: Delete LearningStandard with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "learningStandard" records like below in "Midgar" tenant. And I save this query as "learningStandard"
	|field                                             |value                                                |
	|_id                                               |b0e0b7466429d5d994f18da6982c3af25d1496c3_id          |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                                             |value                                                |
	|assessmentItem.body.learningStandards             |b0e0b7466429d5d994f18da6982c3af25d1496c3_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "SafeLearningStandardDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeLearningStandardDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "b0e0b7466429d5d994f18da6982c3af25d1496c3_id" in the "Midgar" database

Scenario: Delete Orphan LearningStandard with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "learningStandard" records like below in "Midgar" tenant. And I save this query as "learningStandard"
	|field                                             |value                                                |
	|_id                                               |b0e0b7466429d5d994f18da6982c3af25d1496c3_id          |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                                             |value                                                |
	|assessmentItem.body.learningStandards             |b0e0b7466429d5d994f18da6982c3af25d1496c3_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "SafeLearningStandardDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeLearningStandardDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "b0e0b7466429d5d994f18da6982c3af25d1496c3_id" in the "Midgar" database

Scenario: Delete Orphan LearningStandard Ref with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "learningStandard" records like below in "Midgar" tenant. And I save this query as "learningObjective"
	|field                                             |value                                                |
	|_id                                               |b0e0b7466429d5d994f18da6982c3af25d1496c3_id          |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                                             |value                                                |
	|assessmentItem.body.learningStandards             |b0e0b7466429d5d994f18da6982c3af25d1496c3_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "SafeLearningStandardDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeLearningStandardDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "b0e0b7466429d5d994f18da6982c3af25d1496c3_id" in the "Midgar" database