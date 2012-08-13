@RALLY_US3077
Feature: SIF StudentPersonal Test

Background: Set my data store
Given the data store is "data_StudentPersonal"

Scenario: Add a Student
Given the following collections are clean and bootstrapped in datastore:
     | collectionName    |
     | student           |
And I want to POST a(n) "sifEvent_StudentPersonal_add" SIF message
And I wait for "10" seconds
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName   | count |
     | student          | 1     |
   And I check to find if record is in collection:
     | collectionName   | expectedRecordCount | searchParameter           | searchValue  | searchType |
     | student          | 1                   | body.studentUniqueStateId | WB0025       | string     |
   And I check that the record contains all of the expected values:
     | collectionName   | searchParameter           | searchValue  | searchType | expectedValuesFile           |
     | student          | body.studentUniqueStateId | WB0025       | string     | expected_StudentPersonal_add |

Scenario: Update a student
Given I want to POST a(n) "sifEvent_StudentPersonal_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName   | count |
#     | student          | 1     |
#   And I check to find if record is in collection:
#     | collectionName   | expectedRecordCount | searchParameter           | searchValue  | searchType |
#     | student          | 1                   | body.studentUniqueStateId | WB0025       | string     |
#   And I check that the record contains all of the expected values:
#     | collectionName   | searchParameter           | searchValue  | searchType | expectedValuesFile              |
#     | student          | body.studentUniqueStateId | WB0025       | string     | expected_StudentPersonal_change |

Scenario: Negative Testing - Add a Student which is missing SLI required fields
Given the following collections are clean and bootstrapped in datastore:
     | collectionName  |
     | student         |
And I want to POST a(n) "sifEvent_StudentPersonal_add_missing_SLI_required_fields" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName   | count |
#     | student          | 0     |
#   And I check to find if record is in collection:
#     | collectionName   | expectedRecordCount | searchParameter           | searchValue  | searchType |
#     | student          | 0                   | body.studentUniqueStateId | WB0025       | string     |

Scenario: Negative Testing - Update a Student which doesn't exist
Given the following collections are clean and bootstrapped in datastore:
     | collectionName  |
     | student         |
And the fixture data "sif_lea_fixture" has been imported into collection "educationOrganization"
And I want to POST a(n) "sifEvent_StudentPersonal_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName  | count |
#     | student         | 0     |
#   And I check to find if record is in collection:
#     | collectionName   | expectedRecordCount | searchParameter           | searchValue  | searchType |
#     | student          | 0                   | body.studentUniqueStateId | WB0025       | string     |
