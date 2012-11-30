@RALLY_US4006 @clearIndexer
Feature:  Search Indexer: Extractor

Background:
Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
Given I DELETE to clear the Indexer

Scenario:  Data Extraction
Given I send a command to start the extractor to extract now
Given Indexer should have "94" entities
And I search in Elastic Search for "matt"
And "1" hit is returned
And I see the following fields:
 |Field                     |Value                                    |
 |_id                       |2a49acfb-0da9-4983-8a20-4462584f59c7     |
 |_type                     |student                                  |
 |_source.name.firstName    |Matt                                     |
 |_source.name.lastSurname  |Sollars                                  |
 |_source.name.middleName   |D                                        |