@RALLY_US3047
Feature: SIF Integrated Test

Background: Set my data store
Given the data store is "data_integrated"

Scenario: Add an LEA
Given I want to POST a(n) "sifEvent_LEAInfo_add" SIF message
And the following collections are clean and bootstrapped in datastore:
     | collectionName           |
     | educationOrganization    |
     | student                  |
     | studentSchoolAssociation |
And I wait for "10" seconds
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 2     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                   | searchType |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak School District 4530 | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                            | string     |
   And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile   |
     | educationOrganization | body.stateOrganizationId | Daybreak School District 4530 | string     | expected_LEAInfo_add |

Scenario: Update an LEA
Given I want to POST a(n) "sifEvent_LEAInfo_change_1" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 2     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                           | searchType |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                                    | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak School District 4530         | string     |
     | educationOrganization | 0                   | body.nameOfInstitution   | Daybreak School District 4530         | string     |
     | educationOrganization | 1                   | body.nameOfInstitution   | UPDATED Daybreak School District 4530 | string     |
     | educationOrganization | 1                   | body.address.city        | Salt Lake City                        | string     |
   And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile        |
     | educationOrganization | body.stateOrganizationId | Daybreak School District 4530 | string     | expected_LEAInfo_change_1 |

Scenario: Add a School
Given I want to POST a(n) "sifEvent_SchoolInfo_add" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 3     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter          | searchValue                   | searchType |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak West High            | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | Daybreak School District 4530 | string     |
     | educationOrganization | 1                   | body.stateOrganizationId | IL                            | string     |
   And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile      |
     | educationOrganization | body.stateOrganizationId | Daybreak West High            | string     | expected_SchoolInfo_add |

Scenario: Update a School
Given I want to POST a(n) "sifEvent_SchoolInfo_change_1" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
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

Scenario: Add a StudentLEARelationship
Given the fixture data "sif_student_fixture" has been imported into collection "student"
And I want to POST a(n) "sifEvent_StudentLEARelationship_add" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 1     |
   And I check to find if record is in collection:
     | collectionName           | expectedRecordCount | searchParameter      | searchValue                                  | searchType |
     | studentSchoolAssociation | 1                   | body.studentId       | 2012vy-6dc32885-dcc5-11e1-95f6-0021701f543f  | string     |
     | studentSchoolAssociation | 1                   | body.schoolYear      | 2011-2012                                    | string     |
     | studentSchoolAssociation | 1                   | body.entryGradeLevel | Tenth grade                                  | string     |
     | studentSchoolAssociation | 1                   | body.entryDate       | 2012-09-16                                   | string     |

Scenario: Update a StudentLEARelationship
Given I want to POST a(n) "sifEvent_StudentLEARelationship_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 1     |
   And I check to find if record is in collection:
     | collectionName           | expectedRecordCount | searchParameter      | searchValue                                  | searchType |
     | studentSchoolAssociation | 1                   | body.studentId       | 2012vy-6dc32885-dcc5-11e1-95f6-0021701f543f  | string     |
     | studentSchoolAssociation | 1                   | body.schoolYear      | 2013-2014                                    | string     |
     | studentSchoolAssociation | 1                   | body.entryGradeLevel | Ninth grade                                  | string     |
     | studentSchoolAssociation | 1                   | body.entryDate       | 2013-08-13                                   | string     |

Scenario: Add a StudentSchoolEnrollment
Given I want to POST a(n) "sifEvent_StudentSchoolEnrollment_add" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 2     |
   And I check to find if record is in collection:
     | collectionName           | expectedRecordCount | searchParameter      | searchValue                                  | searchType |
     | studentSchoolAssociation | 2                   | body.studentId       | 2012vy-6dc32885-dcc5-11e1-95f6-0021701f543f  | string     |
     | studentSchoolAssociation | 1                   | body.schoolYear      | 2013-2014                                    | string     |
     | studentSchoolAssociation | 1                   | body.entryGradeLevel | Ninth grade                                  | string     |
     | studentSchoolAssociation | 1                   | body.entryDate       | 2013-08-13                                   | string     |
     | studentSchoolAssociation | 1                   | body.schoolYear      | 2011-2012                                    | string     |
     | studentSchoolAssociation | 1                   | body.entryGradeLevel | Tenth grade                                  | string     |

Scenario: Update a StudentSchoolEnrollment
Given I want to POST a(n) "sifEvent_StudentSchoolEnrollment_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 2     |
   And I check to find if record is in collection:
     | collectionName           | expectedRecordCount | searchParameter      | searchValue                                  | searchType |
     | studentSchoolAssociation | 2                   | body.studentId       | 2012vy-6dc32885-dcc5-11e1-95f6-0021701f543f  | string     |
     | studentSchoolAssociation | 1                   | body.schoolYear      | 2013-2014                                    | string     |
     | studentSchoolAssociation | 1                   | body.entryGradeLevel | Ninth grade                                  | string     |
     | studentSchoolAssociation | 1                   | body.entryDate       | 2013-08-13                                   | string     |
     | studentSchoolAssociation | 1                   | body.schoolYear      | 2012-2013                                    | string     |
     | studentSchoolAssociation | 1                   | body.entryGradeLevel | Eleventh grade                               | string     |
