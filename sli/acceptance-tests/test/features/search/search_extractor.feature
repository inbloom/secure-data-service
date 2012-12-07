@RALLY_US4006 @clearIndexer
Feature:  Search Indexer: Extractor

Scenario:  Data Extraction
Given I send a command to start the extractor to extract now
Given Indexer should have "147" entities
And I flush the Indexer
And I search in Elastic Search for "matt" in tenant "Midgar"
And "1" hit is returned
And I see the following fields:
 |Field                     |Value                                    |
 |_id                       |2a49acfb-0da9-4983-8a20-4462584f59c7     |
 |_type                     |student                                  |
 |_source.name.firstName    |Matt                                     |
 |_source.name.lastSurname  |Sollars                                  |
 |_source.name.middleName   |D                                        |
 And I search in Elastic Search for "geometry" in tenant "Midgar"
And "1" hit is returned
And I see the following fields:
 |Field                     |Value                                    |
 |_type                     |learningObjective                        |
 |_source.objective         |Geometry                                 |
 And I search in Elastic Search for "congruence" in tenant "Midgar"
And "1" hit is returned
And I see the following fields:
 |Field                     |Value                                    |
 |_type                     |learningStandard                         |
 |_id                       | dd9165f2-653e-6f27-a82c-bfc5f4c757bc    |	
And I search in Elastic Search for "PSAT%20Mathematics" in tenant "Midgar"
And "19" hit is returned