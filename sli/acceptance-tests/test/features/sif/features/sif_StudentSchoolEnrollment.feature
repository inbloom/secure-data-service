@RALLY_US3048
Feature: SIF StudentSchoolEnrollment Test

Scenario: Add a StudentSchoolEnrollment
Given the following collections are clean and bootstrapped in datastore:
     | collectionName           |
     | educationOrganization    |
     | studentSchoolAssociation |
     | student                  |
And the fixture data "sif_educationOrganization_fixture" has been imported into collection "educationOrganization"
And the fixture data "sif_student_fixture" has been imported into collection "student"
And I want to POST a(n) "sifEvent_StudentSchoolEnrollment_add" SIF message
When I POST the message to the ZIS
And I wait for "10" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 1     |
   And I check to find if record is in collection:
     | collectionName           | expectedRecordCount | searchParameter  | searchValue                                  | searchType |
     | studentSchoolAssociation | 1                   | body.studentId   | 2012vy-6dc32885-dcc5-11e1-95f6-0021701f543f  | string     |
   And I check that the record contains all of the expected values:
     | collectionName           | searchParameter          | searchValue                                  | searchType | expectedValuesFile                   |
     | studentSchoolAssociation | body.studentId           | 2012vy-6dc32885-dcc5-11e1-95f6-0021701f543f  | string     | expected_StudentSchoolEnrollment_add |

@wip
Scenario: Update a School 1
Given I want to POST a(n) "sifEvent_SchoolInfo_change_1" SIF message
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
  And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile           |
     | educationOrganization | body.stateOrganizationId | Daybreak West High            | string     | expected_SchoolInfo_change_1 |

Scenario: Update a School 2
Given I want to POST a(n) "sifEvent_SchoolInfo_change_2" SIF message
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
  And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile           |
     | educationOrganization | body.stateOrganizationId | Daybreak West High            | string     | expected_SchoolInfo_change_2 |

@wip
Scenario: Negative Testing - Add a School which is missing SLI required fields
Given the following collections are clean and bootstrapped in datastore:
     | collectionName        |
     | educationOrganization |
And the fixture data "sif_lea_fixture" has been imported into collection "educationOrganization"
And I want to POST a(n) "sifEvent_SchoolInfo_add_missing_SLI_required_fields" SIF message
When I POST the message to the ZIS
And I wait for "10" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 2     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                   | searchType |
     | educationOrganization | 0                   | body.stateOrganizationId | Daybreak West High            | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak School District 4530 | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                            | string     |

@wip
Scenario: Negative Testing - Update a School which doesn't exist
Given the following collections are clean and bootstrapped in datastore:
     | collectionName        |
     | educationOrganization |
And the fixture data "sif_lea_fixture" has been imported into collection "educationOrganization"
And I want to POST a(n) "sifEvent_SchoolInfo_change_1" SIF message
When I POST the message to the ZIS
And I wait for "10" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 2     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                   | searchType |
     | educationOrganization | 0                   | body.stateOrganizationId | Daybreak West High            | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak School District 4530 | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                            | string     |