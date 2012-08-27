@RALLY_US3567
Feature: Entity versioning and migration strategies
As a system that supports versions, I want to start tracking versions of entities.

Scenario: Remove all records in the metaData collection
Given the following collections are empty in datastore:
	| collectionName                        |
    | metaData                              |
Then I should see following map of entry counts in the corresponding collections:
    | collectionName                        | count |
    | metaData                              | 0     |
        