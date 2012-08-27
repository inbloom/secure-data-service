@RALLY_US3047 @RALLY_DE1527
Feature: SIF LEAInfo Test

Background: Set my data store
Given the data store is "data_LEAInfo"

Scenario: Add an LEA
Given I want to POST a(n) "sifEvent_LEAInfo_add" SIF message
And the following collections are clean and bootstrapped in datastore:
     | collectionName        |
     | educationOrganization |
     | custom_entities       |
And I wait for "10" seconds
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 2     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter                     | searchValue                                 | searchType |
     | educationOrganization | 1                   | body.stateOrganizationId            | Daybreak School District 4530               | string     |
     | educationOrganization | 1                   | body.stateOrganizationId            | IL                                          | string     |
     | educationOrganization | 1                   | body.parentEducationAgencyReference | 2012at-6dc60eb7-dcc5-11e1-95f6-0021701f543f | string     |
   And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile   |
     | educationOrganization | body.stateOrganizationId | Daybreak School District 4530 | string     | expected_LEAInfo_add |

Scenario: Update an LEA 1
Given I want to POST a(n) "sifEvent_LEAInfo_change_1" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 2     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                           | searchType |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                                    | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak School District 4530         | string     |
     | educationOrganization | 0                   | body.nameOfInstitution   | Daybreak School District 4530         | string     |
     | educationOrganization | 1                   | body.nameOfInstitution   | UPDATED Daybreak School District 4530 | string     |
     | educationOrganization | 1                   | body.address.city        | Salt Lake City                        | string     |
   And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile        |
     | educationOrganization | body.stateOrganizationId | Daybreak School District 4530 | string     | expected_LEAInfo_change_1 |

Scenario: Update an LEA 2
Given I want to POST a(n) "sifEvent_LEAInfo_change_2" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 2     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue         | searchType |
     | educationOrganization | 1                   | body.address.addressType | Physical            | string     |
     | educationOrganization | 1                   | body.address.city        | Springfield         | string     |
     | educationOrganization | 0                   | body.address.city        | Salt Lake City      | string     |
   And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile        |
     | educationOrganization | body.stateOrganizationId | Daybreak School District 4530 | string     | expected_LEAInfo_change_2 |

Scenario: Negative Testing - Add an LEA which is missing SLI required fields
Given I want to POST a(n) "sifEvent_LEAInfo_add_missing_SLI_required_fields" SIF message
And the following collections are clean and bootstrapped in datastore:
     | collectionName        |
     | educationOrganization |
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 1     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                   | searchType |
     | educationOrganization | 0                   | body.stateOrganizationId | Daybreak School District 4530 | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                            | string     |

Scenario: Negative Testing - Update an LEA which doesn't exist
Given I want to POST a(n) "sifEvent_LEAInfo_change_1" SIF message
And the following collections are clean and bootstrapped in datastore:
     | collectionName        |
     | educationOrganization |
     | custom_entities       |
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 1     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                   | searchType |
     | educationOrganization | 0                   | body.stateOrganizationId | Daybreak School District 4530 | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                            | string     |
