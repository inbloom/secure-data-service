@RALLY_US3047 @RALLY_US3370 @RALLY_DE1527 @RALLY_US3178 @RALLY_US3369
Feature: SIF Integrated Test

Background: Set my data store
Given the data store is "data_integrated"

Scenario: Add an LEA
Given the data store is "data_LEAInfo"
And I wait for "10" seconds
And I want to POST a(n) "sifEvent_LEAInfo_add" SIF message
And the following collections are clean and bootstrapped in datastore:
     | collectionName           |
     | educationOrganization    |
     | student                  |
     | studentSchoolAssociation |
     | custom_entities          |
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName        | count |
     | educationOrganization | 2     |
   And I check to find if record is in collection:
     | collectionName        | expectedRecordCount | searchParameter                     | searchValue                                 | searchType |
     | educationOrganization | 1                   | body.stateOrganizationId            | Daybreak School District 4530               | string     |
     | educationOrganization | 1                   | body.stateOrganizationId            | IL                                          | string     |
     | educationOrganization | 1                   | body.parentEducationAgencyReference | 2012at-6dc60eb7-dcc5-11e1-95f6-0021701f543f | string     |
   And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile   |
     | educationOrganization | body.stateOrganizationId | Daybreak School District 4530 | string     | expected_LEAInfo_add |

Scenario: Update an LEA
Given the data store is "data_LEAInfo"
And I want to POST a(n) "sifEvent_LEAInfo_change_1" SIF message
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
Given the data store is "data_SchoolInfo"
And I want to POST a(n) "sifEvent_SchoolInfo_add" SIF message
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
   And I check that ID fields resolved correctly:
     | collectionName        | searchParameter          | searchValue        | searchType | idResolutionField                   | targetCollectionName  | targetSearchParameter    | targetSearchValue             | targetSearchType |
     | educationOrganization | body.stateOrganizationId | Daybreak West High | string     | body.parentEducationAgencyReference | educationOrganization | body.stateOrganizationId | Daybreak School District 4530 | string           |
   And I check that the record contains all of the expected values:
     | collectionName        | searchParameter          | searchValue                   | searchType | expectedValuesFile      |
     | educationOrganization | body.stateOrganizationId | Daybreak West High            | string     | expected_SchoolInfo_add |

Scenario: Update a School
Given the data store is "data_SchoolInfo"
And I want to POST a(n) "sifEvent_SchoolInfo_change_1" SIF message
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

Scenario: Add a Student
Given the data store is "data_StudentPersonal"
And I want to POST a(n) "sifEvent_StudentPersonal_add" SIF message
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
Given the data store is "data_StudentPersonal"
And I want to POST a(n) "sifEvent_StudentPersonal_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName   | count |
     | student          | 1     |
   And I check to find if record is in collection:
     | collectionName   | expectedRecordCount | searchParameter           | searchValue  | searchType |
     | student          | 1                   | body.studentUniqueStateId | WB0025       | string     |
   And I check that the record contains all of the expected values:
     | collectionName   | searchParameter           | searchValue  | searchType | expectedValuesFile              |
     | student          | body.studentUniqueStateId | WB0025       | string     | expected_StudentPersonal_change |

Scenario: Add a StudentLEARelationship
Given the data store is "data_StudentLEARelationship"
And I want to POST a(n) "sifEvent_StudentLEARelationship_add" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 1     |
   And I check to find if record is in collection:
     | collectionName           | expectedRecordCount | searchParameter      | searchValue                                  | searchType |
     | studentSchoolAssociation | 1                   | body.schoolYear      | 2011-2012                                    | string     |
     | studentSchoolAssociation | 1                   | body.entryGradeLevel | Tenth grade                                  | string     |
     | studentSchoolAssociation | 1                   | body.entryDate       | 2012-09-01                                   | string     |
   And I check that ID fields resolved correctly:
     | collectionName           | searchParameter | searchValue | searchType | idResolutionField | targetCollectionName  | targetSearchParameter     | targetSearchValue             | targetSearchType |
     | studentSchoolAssociation | body.schoolYear | 2011-2012   | string     | body.studentId    | student               | body.studentUniqueStateId | WB0025                        | string           |
     | studentSchoolAssociation | body.schoolYear | 2011-2012   | string     | body.schoolId     | educationOrganization | body.stateOrganizationId  | Daybreak School District 4530 | string           |

Scenario: Update a StudentLEARelationship
Given the data store is "data_StudentLEARelationship"
And I want to POST a(n) "sifEvent_StudentLEARelationship_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 1     |
   And I check to find if record is in collection:
     | collectionName           | expectedRecordCount | searchParameter      | searchValue                                  | searchType |
     | studentSchoolAssociation | 1                   | body.schoolYear      | 2013-2014                                    | string     |
     | studentSchoolAssociation | 1                   | body.entryGradeLevel | Ninth grade                                  | string     |
     | studentSchoolAssociation | 1                   | body.entryDate       | 2012-09-01                                   | string     |
   And I check that ID fields resolved correctly:
     | collectionName           | searchParameter | searchValue | searchType | idResolutionField | targetCollectionName  | targetSearchParameter     | targetSearchValue             | targetSearchType |
     | studentSchoolAssociation | body.schoolYear | 2013-2014   | string     | body.studentId    | student               | body.studentUniqueStateId | WB0025                        | string           |
     | studentSchoolAssociation | body.schoolYear | 2013-2014   | string     | body.schoolId     | educationOrganization | body.stateOrganizationId  | Daybreak School District 4530 | string           |

Scenario: Add a StudentSchoolEnrollment
Given the data store is "data_StudentSchoolEnrollment"
And I want to POST a(n) "sifEvent_StudentSchoolEnrollment_add" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 2     |
   And I check to find if record is in collection:
     | collectionName           | expectedRecordCount | searchParameter      | searchValue                                  | searchType |
     | studentSchoolAssociation | 1                   | body.schoolYear      | 2013-2014                                    | string     |
     | studentSchoolAssociation | 1                   | body.entryGradeLevel | Ninth grade                                  | string     |
     | studentSchoolAssociation | 1                   | body.entryDate       | 2012-09-01                                   | string     |
     | studentSchoolAssociation | 1                   | body.schoolYear      | 2011-2012                                    | string     |
     | studentSchoolAssociation | 1                   | body.entryGradeLevel | Tenth grade                                  | string     |
   And I check that ID fields resolved correctly:
     | collectionName           | searchParameter | searchValue | searchType | idResolutionField | targetCollectionName  | targetSearchParameter     | targetSearchValue             | targetSearchType |
     | studentSchoolAssociation | body.schoolYear | 2011-2012   | string     | body.studentId    | student               | body.studentUniqueStateId | WB0025                        | string           |
     | studentSchoolAssociation | body.schoolYear | 2011-2012   | string     | body.schoolId     | educationOrganization | body.stateOrganizationId  | Daybreak West High            | string           |

Scenario: Update a StudentSchoolEnrollment
Given the data store is "data_StudentSchoolEnrollment"
And I want to POST a(n) "sifEvent_StudentSchoolEnrollment_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName           | count |
     | studentSchoolAssociation | 2     |
   And I check to find if record is in collection:
     | collectionName           | expectedRecordCount | searchParameter      | searchValue                                  | searchType |
     | studentSchoolAssociation | 1                   | body.schoolYear      | 2013-2014                                    | string     |
     | studentSchoolAssociation | 1                   | body.entryGradeLevel | Ninth grade                                  | string     |
     | studentSchoolAssociation | 1                   | body.entryDate       | 2012-09-01                                   | string     |
     | studentSchoolAssociation | 1                   | body.schoolYear      | 2012-2013                                    | string     |
     | studentSchoolAssociation | 1                   | body.entryGradeLevel | Eleventh grade                               | string     |
   And I check that ID fields resolved correctly:
     | collectionName           | searchParameter | searchValue | searchType | idResolutionField | targetCollectionName  | targetSearchParameter     | targetSearchValue             | targetSearchType |
     | studentSchoolAssociation | body.schoolYear | 2012-2013   | string     | body.studentId    | student               | body.studentUniqueStateId | WB0025                        | string           |
     | studentSchoolAssociation | body.schoolYear | 2012-2013   | string     | body.schoolId     | educationOrganization | body.stateOrganizationId  | Daybreak West High            | string           |

Scenario: Add an Employee
Given the following collections are clean and bootstrapped in datastore:
     | collectionName    |
     | staff             |
And the data store is "data_EmployeePersonal"
And I want to POST a(n) "sifEvent_EmployeePersonal_add" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName | count |
     | staff          | 2     |
   And I check to find if record is in collection:
     | collectionName | expectedRecordCount | searchParameter         | searchValue  | searchType |
     | staff          | 1                   | body.staffUniqueStateId | C2345681     | string     |
   And I check that the record contains all of the expected values:
     | collectionName | searchParameter         | searchValue  | searchType | expectedValuesFile            |
     | staff          | body.staffUniqueStateId | C2345681     | string     | expected_EmployeePersonal_add |

Scenario: Update an Employee
Given the data store is "data_EmployeePersonal"
And I want to POST a(n) "sifEvent_EmployeePersonal_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName | count |
     | staff          | 2     |
   And I check to find if record is in collection:
     | collectionName | expectedRecordCount | searchParameter         | searchValue  | searchType |
     | staff          | 1                   | body.staffUniqueStateId | C2345681     | string     |
   And I check that the record contains all of the expected values:
     | collectionName | searchParameter         | searchValue  | searchType | expectedValuesFile               |
     | staff          | body.staffUniqueStateId | C2345681     | string     | expected_EmployeePersonal_change |

 Scenario: Add an Staff with existing employee record
Given the data store is "data_StaffPersonal"
And I want to POST a(n) "sifEvent_StaffPersonal_add" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName | count |
     | staff          | 2     |
   And I check to find if record is in collection:
     | collectionName | expectedRecordCount | searchParameter         | searchValue  | searchType |
     | staff          | 1                   | body.staffUniqueStateId | C2345681     | string     |
   And I check that the record contains all of the expected values:
     | collectionName | searchParameter         | searchValue  | searchType | expectedValuesFile               |
     | staff          | body.staffUniqueStateId | C2345681     | string     | expected_StaffPersonal_add_exist |

Scenario: Change a Staff record
Given the data store is "data_StaffPersonal"
And I want to POST a(n) "sifEvent_StaffPersonal_change" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName | count |
     | staff          | 2     |
   And I check to find if record is in collection:
     | collectionName | expectedRecordCount | searchParameter         | searchValue  | searchType |
     | staff          | 1                   | body.staffUniqueStateId | C2345681     | string     |
   And I check that the record contains all of the expected values:
     | collectionName | searchParameter         | searchValue  | searchType | expectedValuesFile            |
     | staff          | body.staffUniqueStateId | C2345681     | string     | expected_StaffPersonal_change |

Scenario: Add an EmploymentRecord for a staff
Given the data store is "data_EmploymentRecord"
And I want to POST a(n) "sifEvent_EmploymentRecord_add_staff_educationOrganization" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 2     |
     | teacherSchoolAssociation              | 0     |
   And I check that the record contains all of the expected values:
     | collectionName                        | searchParameter    | searchValue  | searchType | expectedValuesFile                                        |
     | staffEducationOrganizationAssociation | body.positionTitle | Senior Staff | string     | expected_EmploymentRecord_add_staff_educationOrganization |
   And I check that ID fields resolved correctly:
     | collectionName                        | searchParameter    | searchValue  | searchType | idResolutionField                   | targetCollectionName  | targetSearchParameter    | targetSearchValue             | targetSearchType |
     | staffEducationOrganizationAssociation | body.positionTitle | Senior Staff | string     | body.staffReference                 | staff                 | body.staffUniqueStateId  | C2345681                      | string           |
     | staffEducationOrganizationAssociation | body.positionTitle | Senior Staff | string     | body.educationOrganizationReference | educationOrganization | body.stateOrganizationId | Daybreak School District 4530 | string           |

Scenario: Update an EmploymentRecord for a staff
Given the data store is "data_EmploymentRecord"
And I want to POST a(n) "sifEvent_EmploymentRecord_change_staff_educationOrganization" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 2     |
     | teacherSchoolAssociation              | 0     |
   And I check that the record contains all of the expected values:
     | collectionName                        | searchParameter    | searchValue        | searchType | expectedValuesFile                                           |
     | staffEducationOrganizationAssociation | body.positionTitle | Super Senior Staff | string     | expected_EmploymentRecord_change_staff_educationOrganization |
   And I check that ID fields resolved correctly:
     | collectionName                        | searchParameter    | searchValue        | searchType | idResolutionField                   | targetCollectionName  | targetSearchParameter    | targetSearchValue             | targetSearchType |
     | staffEducationOrganizationAssociation | body.positionTitle | Super Senior Staff | string     | body.staffReference                 | staff                 | body.staffUniqueStateId  | C2345681                      | string           |
     | staffEducationOrganizationAssociation | body.positionTitle | Super Senior Staff | string     | body.educationOrganizationReference | educationOrganization | body.stateOrganizationId | Daybreak School District 4530 | string           |

Scenario: Add a StaffAssignment for a staff
Given the data store is "data_StaffAssignment"
And I want to POST a(n) "sifEvent_StaffAssignment_add_staff" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 3     |
     | teacherSchoolAssociation              | 0     |
   And I check that the record contains all of the expected values:
     | collectionName                        | searchParameter    | searchValue        | searchType | expectedValuesFile                  |
     | staffEducationOrganizationAssociation | body.endDate       | 2018-03-11         | string     | expected_StaffAssignment_add_staff  |
   And I check that ID fields resolved correctly:
     | collectionName                        | searchParameter    | searchValue        | searchType | idResolutionField                   | targetCollectionName  | targetSearchParameter    | targetSearchValue             | targetSearchType |
     | staffEducationOrganizationAssociation | body.endDate       | 2018-03-11         | string     | body.staffReference                 | staff                 | body.staffUniqueStateId  | C2345681                      | string           |
     | staffEducationOrganizationAssociation | body.endDate       | 2018-03-11         | string     | body.educationOrganizationReference | educationOrganization | body.stateOrganizationId | Daybreak West High            | string           |

Scenario: Update a StaffAssignment for a staff
Given the data store is "data_StaffAssignment"
And I want to POST a(n) "sifEvent_StaffAssignment_change_staff" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 3     |
     | teacherSchoolAssociation              | 0     |
   And I check that the record contains all of the expected values:
     | collectionName                        | searchParameter    | searchValue        | searchType | expectedValuesFile                     |
     | staffEducationOrganizationAssociation | body.endDate       | 2013-09-15         | string     | expected_StaffAssignment_change_staff  |
   And I check that ID fields resolved correctly:
     | collectionName                        | searchParameter    | searchValue        | searchType | idResolutionField                   | targetCollectionName  | targetSearchParameter    | targetSearchValue             | targetSearchType |
     | staffEducationOrganizationAssociation | body.endDate       | 2013-09-15         | string     | body.staffReference                 | staff                 | body.staffUniqueStateId  | C2345681                      | string           |
     | staffEducationOrganizationAssociation | body.endDate       | 2013-09-15         | string     | body.educationOrganizationReference | educationOrganization | body.stateOrganizationId | Daybreak West High            | string           |
#ecole 10/12/12 updates to natural key fields no longer allowed, these tests will be fixed in the future