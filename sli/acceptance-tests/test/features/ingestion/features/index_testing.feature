Feature: Ingestion Index Test

Scenario: Create Indexes
Given the following collections are empty in datastore:
     | collectionName               |
     | student                      |
     | section                      |
     | studentSectionAssociation    |
And I create collections and add index

Then I should see following map of indexes in the corresponding collections:
     | collectionName               | index                                                                |
     | student                      | _id_                                                                 |
     | student                      | metaData.tenantId_1_metaData.externalId_1                            |
     | section                      | metaData.tenantId_1_metaData.externalId_1                            |
     | section                      | body.schoolId_1_metaData.tenantId_1_metaData.externalId_1            |
     | studentSectionAssociation    | body.studentId_1_metaData.tenantId_1_body.sectionId_1                |