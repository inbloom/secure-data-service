@RALLY_US3047 @RALLY_DE1527
Feature: SIF SchoolInfo Test

Background: Set my data store
Given the data store is "data_SchoolInfo"

Scenario: Add a School
Given the following collections are clean and bootstrapped in datastore:
     | collectionName        |
     | educationOrganization |
     | custom_entities       |
And the fixture data "sif_lea_fixture" has been imported into collection "educationOrganization"
And the fixture data "sif_bootstrap_custom_entity_fixture" has been imported into collection "custom_entities"
And I want to POST a(n) "sifEvent_SchoolInfo_add" SIF message
And I wait for "10" seconds
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 3     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter                     | searchValue                                 | searchType |
     | educationOrganization | 1                   | body.stateOrganizationId            | Daybreak West High                          | string     |
     | educationOrganization | 1                   | body.stateOrganizationId            | IL                                          | string     |
     | educationOrganization | 1                   | body.stateOrganizationId            | Daybreak School District 4530               | string     |
     | educationOrganization | 1                   | body.parentEducationAgencyReference | 2012vv-cd444c5b-dcd7-11e1-992f-0021701f543f | string     |
   And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile      |
     | educationOrganization | body.stateOrganizationId | Daybreak West High            | string     | expected_SchoolInfo_add |

Scenario: Update a School 1
Given I want to POST a(n) "sifEvent_SchoolInfo_change_1" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 3     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                   | searchType |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                            | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak School District 4530 | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak West High            | string     |
     | educationOrganization | 0                   | body.nameOfInstitution   | Daybreak West High            | string     |
     | educationOrganization | 1                   | body.nameOfInstitution   | UPDATED Daybreak West High    | string     |
  And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile           |
     | educationOrganization | body.stateOrganizationId | Daybreak West High            | string     | expected_SchoolInfo_change_1 |

Scenario: Update a School 2
Given I want to POST a(n) "sifEvent_SchoolInfo_change_2" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 3   |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue             | searchType |
     | educationOrganization | 1                   | body.gradesOffered       | Eighth grade            | string     |
     | educationOrganization | 1                   | body.gradesOffered       | Seventh grade           | string     |
     | educationOrganization | 0                   | body.gradesOffered       | Ninth grade             | string     |
  And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile           |
     | educationOrganization | body.stateOrganizationId | Daybreak West High            | string     | expected_SchoolInfo_change_2 |

Scenario: Negative Testing - Add a School which is missing SLI required fields
Given the following collections are clean and bootstrapped in datastore:
     | collectionName        |
     | educationOrganization |
     | custom_entities       |
And I want to POST a(n) "sifEvent_SchoolInfo_add_missing_SLI_required_fields" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 1     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                   | searchType |
     | educationOrganization | 0                   | body.stateOrganizationId | Daybreak West High            | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                            | string     |

Scenario: Negative Testing - Update a School which doesn't exist
Given the following collections are clean and bootstrapped in datastore:
     | collectionName        |
     | educationOrganization |
And the fixture data "sif_lea_fixture" has been imported into collection "educationOrganization"
And I want to POST a(n) "sifEvent_SchoolInfo_change_1" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 2     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                   | searchType |
     | educationOrganization | 0                   | body.stateOrganizationId | Daybreak West High            | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                            | string     |