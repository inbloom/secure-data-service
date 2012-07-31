@RALLY_US3047
Feature: SIF LEAInfo Test

Scenario: Post a LEAInfo message: Clean Database
Given I want to POST a(n) "sifEvent_LEAInfo_add" SIF message
And the following collections are clean in datastore:
     | collectionName        |
     | educationOrganization |
When I POST the message to the ZIS
And I wait for "60" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 2     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                   | searchType |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak School District 4530 | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                            | string     |
Given I want to POST a(n) "sifEvent_LEAInfo_change" SIF message
When I POST the message to the ZIS
And I wait for "60" seconds
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
Given I want to POST a(n) "sifEvent_LEAInfo_change2" SIF message
When I POST the message to the ZIS
And I wait for "60" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 2     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue         | searchType |
     | educationOrganization | 1                   | body.address.addressType | Physical            | string     |
     | educationOrganization | 1                   | body.address.city        | Springfield         | string     |
     | educationOrganization | 0                   | body.address.city        | Salt Lake City      | string     |
