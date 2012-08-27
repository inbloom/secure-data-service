@RALLY_US3369
Feature: SIF EmployeeAssignment Test

Background: Set my data store
Given the data store is "data_EmployeeAssignment"

Scenario: Add an EmployeeAssignment for a teacher
Given I wait for "10" seconds
And the following collections are clean and bootstrapped in datastore:
     | collectionName                        |
     | educationOrganization                 |
     | staff                                 |
     | staffEducationOrganizationAssociation |
     | teacher                               |
     | teacherSchoolAssociation              |
     | custom_entities                       |
And the fixture data "sif_educationOrganization_fixture" has been imported into collection "educationOrganization"
And the fixture data "sif_teacher_fixture" has been imported into collection "staff"
And the fixture data "sif_bootstrap_custom_entity_fixture" has been imported into collection "custom_entities"
And I want to POST a(n) "sifEvent_EmployeeAssignment_add_teacher" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 2     |
     | teacherSchoolAssociation              | 1     |
   And I check to find if record is in collection:
     | collectionName                        | expectedRecordCount | searchParameter                     | searchValue                                 | searchType |
     | staffEducationOrganizationAssociation | 1                   | body.staffReference                 | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     |
     | staffEducationOrganizationAssociation | 2                   | body.educationOrganizationReference | 2012at-6dc60eb7-dcc5-11e1-95f6-0021701f543f | string     |
     | teacherSchoolAssociation 			 | 1                   | body.teacherId		                 | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     |
     | teacherSchoolAssociation				 | 1                   | body.schoolId						 | 2012at-6dc60eb7-dcc5-11e1-95f6-0021701f543f | string     |
   And I check that the record contains all of the expected values:
     | collectionName                        | searchParameter     | searchValue                                 | searchType | expectedValuesFile										|
     | staffEducationOrganizationAssociation | body.staffReference | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     | expected_EmployeeAssignment_add_teacher_staffEdOrg		|
	 | teacherSchoolAssociation				 | body.teacherId      | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     | expected_EmployeeAssignment_add_teacher_teacherSchool 	|

Scenario: Update an EmployeeAssignment for a teacher
Given I want to POST a(n) "sifEvent_EmployeeAssignment_change_teacher" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 2     |
     | teacherSchoolAssociation              | 1     |
  And I check to find if record is in collection:
     | collectionName                        | expectedRecordCount | searchParameter                     | searchValue                                 | searchType |
     | staffEducationOrganizationAssociation | 1                   | body.staffReference                 | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     |
     | staffEducationOrganizationAssociation | 2                   | body.educationOrganizationReference | 2012at-6dc60eb7-dcc5-11e1-95f6-0021701f543f | string     |
     | teacherSchoolAssociation 			 | 1                   | body.teacherId		                 | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     |
     | teacherSchoolAssociation				 | 1                   | body.schoolId						 | 2012at-6dc60eb7-dcc5-11e1-95f6-0021701f543f | string     |
 And I check that the record contains all of the expected values:
     | collectionName                        | searchParameter     | searchValue                                 | searchType | expectedValuesFile										|
     | staffEducationOrganizationAssociation | body.staffReference | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     | expected_EmployeeAssignment_change_teacher_staffEdOrg		|
	 | teacherSchoolAssociation				 | body.teacherId      | 2012vm-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     | expected_EmployeeAssignment_change_teacher_teacherSchool 	|

Scenario: Add an EmployeeAssignment for a staff
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
And I want to POST a(n) "sifEvent_EmployeeAssignment_add_staff" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 2     |
   And I check to find if record is in collection:
     | collectionName                        | expectedRecordCount | searchParameter                     | searchValue                                 | searchType |
     | staffEducationOrganizationAssociation | 1                   | body.staffReference                 | 2012fw-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     |
     | staffEducationOrganizationAssociation | 2                   | body.educationOrganizationReference | 2012at-6dc60eb7-dcc5-11e1-95f6-0021701f543f | string     |
 And I check that the record contains all of the expected values:
     | collectionName                        | searchParameter     | searchValue                                 | searchType | expectedValuesFile										|
     | staffEducationOrganizationAssociation | body.staffReference | 2012fw-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     | expected_EmployeeAssignment_add_staff_staffEdOrg		|

Scenario: Update an EmployeeAssignment for a teacher
Given I want to POST a(n) "sifEvent_EmployeeAssignment_change_staff" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 2     |
  And I check to find if record is in collection:
     | collectionName                        | expectedRecordCount | searchParameter                     | searchValue                                 | searchType |
     | staffEducationOrganizationAssociation | 1                   | body.staffReference                 | 2012fw-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     |
     | staffEducationOrganizationAssociation | 2                   | body.educationOrganizationReference | 2012at-6dc60eb7-dcc5-11e1-95f6-0021701f543f | string     |
 And I check that the record contains all of the expected values:
     | collectionName                        | searchParameter     | searchValue                                 | searchType | expectedValuesFile										|
     | staffEducationOrganizationAssociation | body.staffReference | 2012fw-e2efce80-eb85-11e1-b59f-406c8f06bd30 | string     | expected_EmployeeAssignment_change_staff_staffEdOrg		|

Scenario: Negative Testing - Add an EmploymentRecord which is missing SLI required fields 1: missing required non-reference field
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
And I want to POST a(n) "sifEvent_EmployeeAssignment_add_missing_SLI_required_fields" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 1     |
     | teacherSchoolAssociation              | 0     |

Scenario: Negative Testing - Add an EmployeeAssignment which is missing SLI required fields 2: missing reference
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
And I want to POST a(n) "sifEvent_EmployeeAssignment_add_missing_reference" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 1     |
     | teacherSchoolAssociation              | 0     |

Scenario: Negative Testing - Update an EmployeeAssignment which doesn't exist - teacher
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
And I want to POST a(n) "sifEvent_EmployeeAssignment_change_teacher" SIF message
When I POST the message to the ZIS
And I wait for "3" seconds
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                        | count |
     | staffEducationOrganizationAssociation | 1     |
     | teacherSchoolAssociation              | 0     |

