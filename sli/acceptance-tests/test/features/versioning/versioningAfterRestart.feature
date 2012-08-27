@US3567
Feature: Entity versioning and migration strategies
I want to track versions of entities, detect when new versions have been created, and signal for asynchronous upgrades to existing entities.

@DB-MIGRATION-BEFORE-START
Scenario: Remove all records in the collection before we start
Given the following collections are empty in datastore:
	| collectionName                        |
    | metaData                              |
Then I should see following map of entry counts in the corresponding collections:
    | collectionName                        | count |
    | metaData                              | 0     |
        
@wip
@DB-MIGRATION-AFTER-START
Scenario: Check that after starting the API, documents exist in the collection

@wip
@DB-MIGRATION-AFTER-RESTART
Scenario: Check that the updated versions were detected on startup and thus SARJE has been signaled