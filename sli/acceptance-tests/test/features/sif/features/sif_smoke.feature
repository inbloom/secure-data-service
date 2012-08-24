@smoke @RALLY_US3047 @RALLY_US3370
Feature: SIF Smoke Test

Background: Set my data store
Given the data store is "data_integrated"

Scenario: Add an LEA, School, Student, StudentLEARelationship, and StudentSchoolEnrollment
Given the following collections are clean and bootstrapped in datastore:
     | collectionName           |
     | educationOrganization    |
     | student                  |
     | studentSchoolAssociation |
     | custom_entities          |
And I wait for "10" seconds
Given the data store is "data_LEAInfo"
When I POST a(n) "sifEvent_LEAInfo_add" SIF message to the ZIS
Given the data store is "data_integrated"
And I POST a(n) "sifEvent_SchoolInfo_add" SIF message to the ZIS
And I POST a(n) "sifEvent_StudentPersonal_add" SIF message to the ZIS
And I POST a(n) "sifEvent_StudentLEARelationship_add" SIF message to the ZIS
And I POST a(n) "sifEvent_StudentSchoolEnrollment_add" SIF message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | educationOrganization    | 3     |
     | studentSchoolAssociation | 2     |
     | student                  | 1     |
   And I check to find if record is in collection:
     | collectionName           | expectedRecordCount | searchParameter           | searchValue                                  | searchType |
     | educationOrganization    | 1                   | body.stateOrganizationId  | Daybreak School District 4530                | string     |
     | educationOrganization    | 1                   | body.stateOrganizationId  | IL                                           | string     |
     | educationOrganization    | 1                   | body.stateOrganizationId  | Daybreak West High                           | string     |
     | student                  | 1                   | body.studentUniqueStateId | WB0025                                       | string     |
     | studentSchoolAssociation | 2                   | body.schoolYear           | 2011-2012                                    | string     |
     | studentSchoolAssociation | 2                   | body.entryGradeLevel      | Tenth grade                                  | string     |
     | studentSchoolAssociation | 1                   | body.entryDate            | 2012-09-01                                   | string     |
     | studentSchoolAssociation | 1                   | body.entryDate            | 2012-09-16                                   | string     |
   And I check that the record contains all of the expected values:
     | collectionName        | searchParameter           | searchValue                   | searchType | expectedValuesFile           |
     | educationOrganization | body.stateOrganizationId  | Daybreak School District 4530 | string     | ../data_LEAInfo/expected_LEAInfo_add         |
     | educationOrganization | body.stateOrganizationId  | Daybreak West High            | string     | expected_SchoolInfo_add      |
     | student               | body.studentUniqueStateId | WB0025                        | string     | expected_StudentPersonal_add |
   And I check that ID fields resolved correctly:
     | collectionName           | searchParameter          | searchValue        | searchType | idResolutionField                   | targetCollectionName  | targetSearchParameter     | targetSearchValue             | targetSearchType |
     | educationOrganization    | body.stateOrganizationId | Daybreak West High | string     | body.parentEducationAgencyReference | educationOrganization | body.stateOrganizationId  | Daybreak School District 4530 | string           |
     | studentSchoolAssociation | body.entryDate           | 2012-09-01         | string     | body.studentId                      | student               | body.studentUniqueStateId | WB0025                        | string           |
     | studentSchoolAssociation | body.entryDate           | 2012-09-01         | string     | body.schoolId                       | educationOrganization | body.stateOrganizationId  | Daybreak School District 4530 | string           |
     | studentSchoolAssociation | body.entryDate           | 2012-09-16         | string     | body.studentId                      | student               | body.studentUniqueStateId | WB0025                        | string           |
     | studentSchoolAssociation | body.entryDate           | 2012-09-16         | string     | body.schoolId                       | educationOrganization | body.stateOrganizationId  | Daybreak West High            | string           |