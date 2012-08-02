@RALLY_US3047
Feature: SIF SchoolInfo Test

Scenario: Post a SchoolInfo message: Clean Database
Given I want to POST a(n) "sifEvent_LEAInfo_add" SIF message
And the following collections are clean in datastore:
     | collectionName        |
     | educationOrganization |
When I POST the message to the ZIS
And I wait for "10" seconds
Given I want to POST a(n) "sifEvent_SchoolInfo_add" SIF message
When I POST the message to the ZIS
And I wait for "10" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 3     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                   | searchType |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak West High            | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak School District 4530 | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                            | string     |
Given I want to POST a(n) "sifEvent_SchoolInfo_change" SIF message
When I POST the message to the ZIS
And I wait for "10" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 3     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                | searchType |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                         | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak West High         | string     |
     | educationOrganization | 0                   | body.nameOfInstitution   | Daybreak West High         | string     |
     | educationOrganization | 1                   | body.nameOfInstitution   | UPDATED Daybreak West High | string     |
Given I want to POST a(n) "sifEvent_SchoolInfo_change2" SIF message
When I POST the message to the ZIS
And I wait for "10" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 3   |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue             | searchType |
     | educationOrganization | 1                   | body.gradesOffered       | Eighth grade            | string     |
     | educationOrganization | 1                   | body.gradesOffered       | Seventh grade           | string     |
     | educationOrganization | 0                   | body.gradesOffered       | Ninth grade             | string     |
