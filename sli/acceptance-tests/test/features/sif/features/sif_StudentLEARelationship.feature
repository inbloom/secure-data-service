@RALLY_US3048
Feature: SIF StudentLEARelationship Test

Background: Set my data store
Given the data store is "data_StudentLEARelationship"

Scenario: Add a StudentLEARelationship
Given the following collections are clean and bootstrapped in datastore:
     | collectionName           |
     | educationOrganization    |
     | studentSchoolAssociation |
     | student                  |
And the fixture data "sif_educationOrganization_fixture" has been imported into collection "educationOrganization"
And the fixture data "sif_student_fixture" has been imported into collection "student"
And I want to POST a(n) "sifEvent_StudentLEARelationship_add" SIF message
And I wait for "10" seconds
When I POST the message to the ZIS
And I wait for "10" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 1     |
   And I check to find if record is in collection:
     | collectionName           | expectedRecordCount | searchParameter  | searchValue                                  | searchType |
     | studentSchoolAssociation | 1                   | body.studentId   | 2012vy-6dc32885-dcc5-11e1-95f6-0021701f543f  | string     |
   And I check that the record contains all of the expected values:
     | collectionName           | searchParameter          | searchValue                                  | searchType | expectedValuesFile                  |
     | studentSchoolAssociation | body.studentId           | 2012vy-6dc32885-dcc5-11e1-95f6-0021701f543f  | string     | expected_StudentLEARelationship_add |

Scenario: Update a StudentLEARelationship
Given I want to POST a(n) "sifEvent_StudentLEARelationship_change" SIF message
When I POST the message to the ZIS
And I wait for "10" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 1     |
   And I check to find if record is in collection:
     | collectionName           | expectedRecordCount | searchParameter  | searchValue                                  | searchType |
     | studentSchoolAssociation | 1                   | body.studentId   | 2012vy-6dc32885-dcc5-11e1-95f6-0021701f543f  | string     |
  And I check that the record contains all of the expected values:
     | collectionName           | searchParameter          | searchValue                                  | searchType | expectedValuesFile                      |
     | studentSchoolAssociation | body.studentId           | 2012vy-6dc32885-dcc5-11e1-95f6-0021701f543f  | string     | expected_StudentLEARelationship_change |

Scenario: Negative Testing - Add a StudentLEARelationship which is missing SLI required fields
Given the following collections are clean and bootstrapped in datastore:
     | collectionName           |
     | educationOrganization    |
     | studentSchoolAssociation |
     | student                  |
And the fixture data "sif_educationOrganization_fixture" has been imported into collection "educationOrganization"
And the fixture data "sif_student_fixture" has been imported into collection "student"
And I want to POST a(n) "sifEvent_StudentLEARelationship_add_missing_SLI_required_fields" SIF message
When I POST the message to the ZIS
And I wait for "10" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 0     |

Scenario: Negative Testing - Update a StudentLEARelationship which doesn't exist
Given the following collections are clean and bootstrapped in datastore:
     | collectionName           |
     | educationOrganization    |
     | studentSchoolAssociation |
     | student                  |
And the fixture data "sif_educationOrganization_fixture" has been imported into collection "educationOrganization"
And the fixture data "sif_student_fixture" has been imported into collection "student"
And I want to POST a(n) "sifEvent_StudentLEARelationship_change" SIF message
When I POST the message to the ZIS
And I wait for "10" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 0     |