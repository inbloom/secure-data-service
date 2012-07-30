@RALLY_US3047
Feature: SIF LEAInfo Test

Scenario: Post a LEAInfo message: Clean Database
Given I want to POST a(n) "sifEvent_LEAInfo_add" SIF message
And the following collections are clean in datastore:
     | collectionName        |
     | educationOrganization |
When I POST the message to the ZIS
And I wait for "10" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 2     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                   | searchType |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak School District 4530 | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                            | string     |


Scenario: Post a LEAInfo message: Populated Database
Given I want to POST a(n) "sifEvent_LEAInfo_change" SIF message
When I POST the message to the ZIS
#And "10" seconds have elapsed
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName        | count |
#     | educationOrganization | 4     |
#   And I check to find if record is in collection:
#     | collectionName        | expectedRecordCount | searchParameter          | searchValue    | searchType |
#     | educationOrganization | 0                   | body.stateOrganizationId | Tatooine PS IV | string     |
#     | educationOrganization | 1                   | body.stateOrganizationId | Hoth PS V      | string     |