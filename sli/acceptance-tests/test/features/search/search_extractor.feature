@RALLY_US4006
Feature:  Search Indexer: Extractor
#search_indexer needs to have the following parameters set
#mvn -Dsli.search.indexer.extract.schedule="0 * * * * ?" -Dsli.search.indexer.extract.runOnStartup=true test

Background:
Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"

Scenario:  Data Extraction
Given I will wait up to "80" seconds for the extractor
Given Indexer should have "118" entities
And I search in Elastic Search for "matt"
And "1" hit is returned
And I see the following fields:
 |Field                     |Value                                    |
 |_id                       |2a49acfb-0da9-4983-8a20-4462584f59c7     |
 |_type                     |student                                  |
 |_source.name.firstName    |Matt                                     |
 |_source.name.lastSurname  |Sollars                                  |
 |_source.name.middleName   |D                                        |

