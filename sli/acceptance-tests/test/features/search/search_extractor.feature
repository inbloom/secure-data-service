@RALLY_US4006 @clearIndexer
Feature:  Search Indexer: Extractor

Scenario:  Data Extraction
Given I import into tenant collection
Given I send a command to start the extractor to extract "Midgar" now
Given I send a command to start the extractor to extract "Hyrule" now
Given Indexer should have "111" entities
And I flush the Indexer
And I search in Elastic Search for "matt" in tenant "Midgar"
And "1" hit is returned
And I see the following fields:
 |Field                     |Value                                    |
 |_id                       |2a49acfb-0da9-4983-8a20-4462584f59c7_id     |
 |_type                     |student                                  |
 |_source.name.firstName    |Matt                                     |
 |_source.name.lastSurname  |Sollars                                  |
 |_source.name.middleName   |D                                        |
And I search in Elastic Search for "PSAT%20Mathematics" in tenant "Midgar"
And "5" hit is returned