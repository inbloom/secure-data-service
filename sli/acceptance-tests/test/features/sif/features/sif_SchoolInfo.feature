@RALLY_US3047
Feature: SIF SchoolInfo Test

Scenario: Post a SchoolInfo message: Clean Database
Given the following collections are clean in datastore:
     | collectionName        |
     | educationOrganization |
When I POST a(n) "sifEvent_SchoolInfo_add" SIF message
#And "10" seconds have elapsed
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName        | count |
#     | educationOrganization | 4     |
#   And I check to find if record is in collection:
#     | collectionName        | expectedRecordCount | searchParameter          | searchValue    | searchType |
#     | educationOrganization | 1                   | body.stateOrganizationId | Tatooine PS IV | string     |
#     | educationOrganization | 0                   | body.stateOrganizationId | Hoth PS V      | string     |


Scenario: Post a SchoolInfo message: Populated Database
# Given ???
When I POST a(n) "sifEvent_SchoolInfo_change" SIF message
#And "10" seconds have elapsed
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName        | count |
#     | educationOrganization | 4     |
#   And I check to find if record is in collection:
#     | collectionName        | expectedRecordCount | searchParameter          | searchValue    | searchType |
#     | educationOrganization | 0                   | body.stateOrganizationId | Tatooine PS IV | string     |
#     | educationOrganization | 1                   | body.stateOrganizationId | Hoth PS V      | string     |