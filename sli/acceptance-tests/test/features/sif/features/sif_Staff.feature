@RALLY_US3178
Feature: SIF Staff Test

Background: Set my data store
Given the data store is "data_Staff"

Scenario: Add an Employee
Given the following collections are clean and bootstrapped in datastore:
     | collectionName    |
     | staff             |
And I want to POST a(n) "sifEvent_EmployeePersonal_add" SIF message
And I wait for "10" seconds
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName | count |
#     | staff          | 2     |
#   And I check to find if record is in collection:
#     | collectionName | expectedRecordCount | searchParameter         | searchValue  | searchType |
#     | staff          | 1                   | body.staffUniqueStateId | C2345681     | string     |
#   And I check that the record contains all of the expected values:
#     | collectionName | searchParameter         | searchValue  | searchType | expectedValuesFile            |
#     | staff          | body.staffUniqueStateId | C2345681     | string     | expected_EmployeePersonal_add |

Scenario: Update an Employee
Given I want to POST a(n) "sifEvent_EmployeePersonal_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName | count |
#     | staff          | 2     |
#   And I check to find if record is in collection:
#     | collectionName | expectedRecordCount | searchParameter         | searchValue  | searchType |
#     | staff          | 1                   | body.staffUniqueStateId | C2345681     | string     |
#   And I check that the record contains all of the expected values:
#     | collectionName | searchParameter         | searchValue  | searchType | expectedValuesFile               |
#     | staff          | body.staffUniqueStateId | C2345681     | string     | expected_EmployeePersonal_change |

Scenario: Add an Staff with existing employee record
And I want to POST a(n) "sifEvent_StaffPersonal_add" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName | count |
#     | staff          | 2     |
#   And I check to find if record is in collection:
#     | collectionName | expectedRecordCount | searchParameter         | searchValue  | searchType |
#     | staff          | 1                   | body.staffUniqueStateId | C2345681     | string     |
#   And I check that the record contains all of the expected values:
#     | collectionName | searchParameter         | searchValue  | searchType | expectedValuesFile            |
#     | staff          | body.staffUniqueStateId | C2345681     | string     | expected_StaffPersonal_add |

Scenario: Add an Staff with existing employee record
And I want to POST a(n) "sifEvent_StaffPersonal_add" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName | count |
#     | staff          | 2     |
#   And I check to find if record is in collection:
#     | collectionName | expectedRecordCount | searchParameter         | searchValue  | searchType |
#     | staff          | 1                   | body.staffUniqueStateId | C2345681     | string     |
#   And I check that the record contains all of the expected values:
#     | collectionName | searchParameter         | searchValue  | searchType | expectedValuesFile               |
#     | staff          | body.staffUniqueStateId | C2345681     | string     | expected_StaffPersonal_add_exist |

Scenario: Change a Staff record
And I want to POST a(n) "sifEvent_StaffPersonal_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName | count |
#     | staff          | 2     |
#   And I check to find if record is in collection:
#     | collectionName | expectedRecordCount | searchParameter         | searchValue  | searchType |
#     | staff          | 1                   | body.staffUniqueStateId | C2345681     | string     |
#   And I check that the record contains all of the expected values:
#     | collectionName | searchParameter         | searchValue  | searchType | expectedValuesFile            |
#     | staff          | body.staffUniqueStateId | C2345681     | string     | expected_StaffPersonal_change |


Scenario: Add an Staff with no existing employee record
Given the following collections are clean and bootstrapped in datastore:
     | collectionName  |
     | staff           |
And I want to POST a(n) "sifEvent_StaffPersonal_add" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName | count |
#     | staff          | 2     |
#   And I check to find if record is in collection:
#     | collectionName | expectedRecordCount | searchParameter         | searchValue  | searchType |
#     | staff          | 1                   | body.staffUniqueStateId | C2345681     | string     |
#   And I check that the record contains all of the expected values:
#     | collectionName | searchParameter         | searchValue  | searchType | expectedValuesFile               |
#     | staff          | body.staffUniqueStateId | C2345681     | string     | expected_StaffPersonal_add_clean |


Scenario: Negative Testing - Add a Employee which is missing SLI required fields
Given the following collections are clean and bootstrapped in datastore:
     | collectionName  |
     | staff           |
And I want to POST a(n) "sifEvent_EmployeePersonal_add_missing_SLI_required_fields" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName | count |
#     | staff          | 1     |

Scenario: Negative Testing - Add a Staff which is missing SLI required fields
Given the following collections are clean and bootstrapped in datastore:
     | collectionName  |
     | staff           |
And I want to POST a(n) "sifEvent_StaffPersonal_add_missing_SLI_required_fields" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName   | count |
#     | staff            | 1     |

Scenario: Negative Testing - Update a Employee which doesn't exist
Given the following collections are clean and bootstrapped in datastore:
     | collectionName  |
     | staff           |
And I want to POST a(n) "sifEvent_EmployeePersonal_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName   | count |
#     | staff            | 1     |

Scenario: Negative Testing - Update a Staff which doesn't exist
Given the following collections are clean and bootstrapped in datastore:
     | collectionName  |
     | staff           |
And I want to POST a(n) "sifEvent_StaffPersonal_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
#Then I should see following map of entry counts in the corresponding collections:
#     | collectionName   | count |
#     | staff            | 1     |


