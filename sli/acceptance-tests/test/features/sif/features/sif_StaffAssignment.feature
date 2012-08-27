@RALLY_US3369
Feature: SIF StaffAssignment Test

Background: Set my data store
Given the data store is "data_StaffAssignment"

Scenario: Add a StaffAssignment for a staff
Given I wait for "10" seconds
And the following collections are clean and bootstrapped in datastore:
     | collectionName                        |
     | educationOrganization                 |
     | staff                                 |
     | staffEducationOrganizationAssociation |
     | teacherSchoolAssociation              |
     | custom_entities                       |
And the fixture data "sif_educationOrganization_fixture" has been imported into collection "educationOrganization"
And the fixture data "sif_staff_fixture" has been imported into collection "staff"
And the fixture data "sif_bootstrap_custom_entity_fixture" has been imported into collection "custom_entities"
And I want to POST a(n) "sifEvent_StaffAssignment_add_staff" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 2     |
     | teacherSchoolAssociation              | 0     |
   And I check to find if record is in collection:
     | collectionName                        | expectedRecordCount | searchParameter                     | searchValue                                 | searchType |
     | staffEducationOrganizationAssociation | 1                   | body.staffReference                 | 2012fw-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     |
     | staffEducationOrganizationAssociation | 1                   | body.educationOrganizationReference | 2012av-6dcc2939-dcc5-11e1-95f6-0021701f543f | string     |
   And I check that the record contains all of the expected values:
     | collectionName                        | searchParameter     | searchValue                                 | searchType | expectedValuesFile                 |
     | staffEducationOrganizationAssociation | body.staffReference | 2012fw-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     | expected_StaffAssignment_add_staff |

Scenario: Update a StaffAssignment for a staff
Given I want to POST a(n) "sifEvent_StaffAssignment_change_staff" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 2     |
     | teacherSchoolAssociation              | 0     |
  And I check to find if record is in collection:
     | collectionName                        | expectedRecordCount | searchParameter                     | searchValue                                 | searchType |
     | staffEducationOrganizationAssociation | 1                   | body.staffReference                 | 2012fw-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     |
     | staffEducationOrganizationAssociation | 1                   | body.educationOrganizationReference | 2012av-6dcc2939-dcc5-11e1-95f6-0021701f543f | string     |
   And I check that the record contains all of the expected values:
     | collectionName                        | searchParameter     | searchValue                                 | searchType | expectedValuesFile                    |
     | staffEducationOrganizationAssociation | body.staffReference | 2012fw-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     | expected_StaffAssignment_change_staff |

Scenario: Add a StaffAssignment for a teacher
Given the following collections are clean and bootstrapped in datastore:
     | collectionName                        |
     | educationOrganization                 |
     | staff                                 |
     | staffEducationOrganizationAssociation |
     | teacherSchoolAssociation              |
     | custom_entities                       |
And the fixture data "sif_educationOrganization_fixture" has been imported into collection "educationOrganization"
And the fixture data "sif_teacher_fixture" has been imported into collection "staff"
And the fixture data "sif_bootstrap_custom_entity_fixture" has been imported into collection "custom_entities"
And I want to POST a(n) "sifEvent_StaffAssignment_add_teacher" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 2     |
     | teacherSchoolAssociation              | 1     |
   And I check to find if record is in collection:
     | collectionName                        | expectedRecordCount | searchParameter                     | searchValue                                 | searchType |
     | teacherSchoolAssociation              | 1                   | body.teacherId                      | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     |
     | teacherSchoolAssociation              | 1                   | body.schoolId                       | 2012av-6dcc2939-dcc5-11e1-95f6-0021701f543f | string     |
     | staffEducationOrganizationAssociation | 1                   | body.staffReference                 | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     |
     | staffEducationOrganizationAssociation | 1                   | body.educationOrganizationReference | 2012av-6dcc2939-dcc5-11e1-95f6-0021701f543f | string     |
   And I check that the record contains all of the expected values:
     | collectionName                        | searchParameter     | searchValue                                 | searchType | expectedValuesFile                                                         |
     | teacherSchoolAssociation              | body.teacherId      | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     | expected_StaffAssignment_add_teacher_teacherSchoolAssociation              |
     | staffEducationOrganizationAssociation | body.staffReference | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     | expected_StaffAssignment_add_teacher_staffEducationOrganizationAssociation |

Scenario: Update a StaffAssignment for a teacher
Given I want to POST a(n) "sifEvent_StaffAssignment_change_teacher" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 2     |
     | teacherSchoolAssociation              | 1     |
   And I check to find if record is in collection:
     | collectionName                        | expectedRecordCount | searchParameter                     | searchValue                                 | searchType |
     | teacherSchoolAssociation              | 1                   | body.teacherId                      | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     |
     | teacherSchoolAssociation              | 1                   | body.schoolId                       | 2012av-6dcc2939-dcc5-11e1-95f6-0021701f543f | string     |
     | staffEducationOrganizationAssociation | 1                   | body.staffReference                 | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     |
     | staffEducationOrganizationAssociation | 1                   | body.educationOrganizationReference | 2012av-6dcc2939-dcc5-11e1-95f6-0021701f543f | string     |
   And I check that the record contains all of the expected values:
     | collectionName                        | searchParameter     | searchValue                                 | searchType | expectedValuesFile                                                            |
     | teacherSchoolAssociation              | body.teacherId      | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     | expected_StaffAssignment_change_teacher_teacherSchoolAssociation              |
     | staffEducationOrganizationAssociation | body.staffReference | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     | expected_StaffAssignment_change_teacher_staffEducationOrganizationAssociation |

Scenario: Negative Testing - Add a StaffAssignment which is missing SLI required fields 1: missing required non-reference field for staffEducationOrganizationAssociation
Given the following collections are clean and bootstrapped in datastore:
     | collectionName                        |
     | educationOrganization                 |
     | staff                                 |
     | staffEducationOrganizationAssociation |
     | teacherSchoolAssociation              |
     | custom_entities                       |
And the fixture data "sif_educationOrganization_fixture" has been imported into collection "educationOrganization"
And the fixture data "sif_staff_fixture" has been imported into collection "staff"
And the fixture data "sif_teacher_fixture" has been imported into collection "staff"
And the fixture data "sif_bootstrap_custom_entity_fixture" has been imported into collection "custom_entities"
And I want to POST a(n) "sifEvent_StaffAssignment_add_staff_missing_SLI_required_fields" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 1     |
     | teacherSchoolAssociation              | 0     |

Scenario: Negative Testing - Update a StaffAssignment which doesn't exist - staff
Given the following collections are clean and bootstrapped in datastore:
     | collectionName                        |
     | educationOrganization                 |
     | staff                                 |
     | staffEducationOrganizationAssociation |
     | teacherSchoolAssociation              |
     | custom_entities                       |
And the fixture data "sif_educationOrganization_fixture" has been imported into collection "educationOrganization"
And the fixture data "sif_staff_fixture" has been imported into collection "staff"
And the fixture data "sif_teacher_fixture" has been imported into collection "staff"
And the fixture data "sif_bootstrap_custom_entity_fixture" has been imported into collection "custom_entities"
And I want to POST a(n) "sifEvent_StaffAssignment_change_staff" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 1     |
     | teacherSchoolAssociation              | 0     |
