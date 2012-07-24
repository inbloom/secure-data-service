@RALLY_US3047
Feature: SIF LEAInfo Test

Scenario: Post an LEAInfo message: Clean Database
Given this is a new entity
#And the following collections are clean in datastore:
#     | collectionName        |
#     | educationOrganization |
When I POST a(n) "LEAInfo" SIF message
#And "10" seconds have elapsed
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName        | count |
#     | educationOrganization | 4     |
#   And I check to find if record is in collection:
#     | collectionName        | expectedRecordCount | searchParameter          | searchValue    | searchType |
#     | educationOrganization | 1                   | body.stateOrganizationId | Tatooine PS IV | string     |
#     | educationOrganization | 0                   | body.stateOrganizationId | Hoth PS V      | string     |


Scenario: Post an LEAInfo message: Populated Database
Given this is an update to an existing entity
When I POST a(n) "LEAInfo" SIF message
#And "10" seconds have elapsed
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName        | count |
#     | educationOrganization | 4     |
#   And I check to find if record is in collection:
#     | collectionName        | expectedRecordCount | searchParameter          | searchValue    | searchType |
#     | educationOrganization | 0                   | body.stateOrganizationId | Tatooine PS IV | string     |
#     | educationOrganization | 1                   | body.stateOrganizationId | Hoth PS V      | string     |