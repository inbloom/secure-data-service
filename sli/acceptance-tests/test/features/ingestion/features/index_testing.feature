Feature: Ingestion Index Test

Scenario: Create Indexes
Given the following collections are empty in datastore:
     | collectionName               |
     | student                      |
And I create collections and add index

Then I should see following map of indexes in the corresponding collections:
     | collectionName               | index                                                                |
     | student                      | _id_                                                                 |
     | student                      | metaData.tenantId_1                                                  |
     | student                      | metaData.tenantId_1_metaData.externalId_1                            |