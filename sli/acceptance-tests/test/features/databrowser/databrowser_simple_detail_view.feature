@RALLY_US215
Feature: Data Browser
As a Data Browser user, I want to be able to traverse all of the data I have access to so that I can investigate/troubleshoot issues as they come up
Background:
  Given that databrowser has been authorized for all ed orgs

Scenario: Go to Data Browser when authenticated SLI

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jwashington" "jwashington1234" for the "Simple" login page
Then I should be redirected to the Data Browser home page
# DS-1034 removes databrowser homepage links.
# And I should see my available links labeled

Scenario: Logout

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jwashington" "jwashington1234" for the "Simple" login page
Then I should be redirected to the Data Browser home page
When I click on the Logout link
And I am forced to reauthenticate to access the databrowser

@smoke
Scenario: Navigate to home page from any page

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
When I have navigated to the <Page> of the Data Browser as user "rrogers" with edorg "Illinois State Board of Education"
	|Page|
	|Staff Education Organization Associations|
	|Staff Program Associations|
	|Me|
Then I should click on the Home link and be redirected back

Scenario: Entity Detail Order - Order of Entity Properties should match configuration

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
And I click on the "Illinois State Board of Education" link
Then I am redirected to the educationOrganization page
And I see the properties in the following <Order>
| Order |
| Id |
| OrganizationCategories |
| StateOrganizationId |
| NameOfInstitution |
| Address |
And I see "Associated Entities" last

Scenario: Links List Order - Order of the list of Links in an Entity

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
And I click on the "Illinois State Board of Education" link
Then I am redirected to the educationOrganization page
And I see the list of "Associated Entities" in alphabetical order

Scenario: Associations List - Simple View

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
And I click on the "Illinois State Board of Education" link
And I click on the "Staff" link
When I click on the row containing "rrogers"
And I click on the "Staff Program Associations" link
Then I am redirected to the associations list page
And I see a table displaying the associations in a list
And those names include the IDs of both "ProgramId" and "StaffId" in the association

Scenario: Associations List - Expand/Collapse between Simple View and Detail View

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
And I click on the "Illinois State Board of Education" link
And I click on the "Staff" link
When I click on the row containing "rrogers"
And I click on the "Staff Cohort Associations" link
When I click on the row containing "b408635d-8fd5-11e1-86ec-0021701f543f_id"
Then the row expands below listing the rest of the attributes for the item
When I click on the row containing "b408635d-8fd5-11e1-86ec-0021701f543f_id"
Then the row collapses hiding the additional attributes

@wip
Scenario Outline: Entity Detail View

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
When I have navigated to the <Page> page of the Data Browser as user <User> with edorg <EdOrg>
When I click on the row containing <Text>
And I click on the <Link> of any of the associating entities
Then I am redirected to a page that page lists all of the <Entity> entity's fields
 Examples:
| Page                                      | User | EdOrg | Text                                       | Link         | Entity                                 |
| "Staff Program Associations"             | "rrogers" |"Illinois State Board of Education"| "9b8c3aab-8fd5-11e1-86ec-0021701f543f_id"  | "Me"         | "9bf906cc-8fd5-11e1-86ec-0021701f5432" |
| "Staff Cohort Associations" | "rrogers" |"Illinois State Board of Education"| "85585b27-5368-4f10-a331-3abcaf3a3f4c"| "Cohort" | "District-wide academic intervention cohort for Social Studies" |
| "Staff Cohort Associations" | "rrogers" |"Illinois State Board of Education"| "85585b27-5368-4f10-a331-3abcaf3a3f4c"| "Staff" | "rrogers"        |

Scenario: Click on Available Links associations

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jwashington" "jwashington1234" for the "Simple" login page
And I click on the "Sunset School District 4526" link
And I click on the "Staff" link
When I click on the row containing "jwashington"
And I click on the "Me" link
When I click on the "Staff Cohort Associations" link
Then I am redirected to the particular associations Simple View

Scenario: Click on Available Links entities
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jwashington" "jwashington1234" for the "Simple" login page
And I click on the "Sunset School District 4526" link
And I click on the "Staff" link
When I click on the row containing "jwashington"
And I click on the "Me" link
Then I am redirected to the particular entity Detail View

Scenario: Get a Forbidden message when we access something that is forbidden
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jwashington" "jwashington1234" for the "Simple" login page
And I click on the "Sunset School District 4526" link
And I click on the "Staff" link
When I click on the row containing "jwashington"
And I click on the "My Schools" link
#And I have navigated to the "Schools" listing of the Data Browser
When I should navigate to "/entities/schools/a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"
And I click on the "Teachers" link
Then I see a "You do not have access to view this." alert box
And I click the X
Then the error is dismissed


Scenario: Traverse Charter School, Multiple Parents School and Education Service Center
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I choose realm "Illinois Daybreak Charter School" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "swood" "swood1234" for the "Simple" login page
And I navigate to myself as user "swood" of edorg "Daybreak Charter School"
When I click on the "Education Organizations" link
And I click on the "Staff Education Organization Associations" link
And I have navigated to the "Schools" listing of the Data Browser
Then I should navigate to "/entities/schools/62d6d5a0-852c-45f4-906a-912752831662"
And I have navigated to the "EducationOrganizations" listing of the Data Browser
Then I should navigate to "/entities/educationOrganizations/62d6d5a0-852c-45f4-906a-912752831662"

Scenario: Traverse Multiple Parents School and Education Service Center
Given I change the isAdminRole flag for role "Educator" to in the realm "Daybreak" to be "true"
And I have an open web browser
And I navigated to the Data Browser Home URL
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
Then I should be redirected to the Data Browser home page
And I navigate to myself as user "e9ca4497-e1e5-4fc4-ac7b-24bad1f2998b" of edorg "Sunset Central High School"
When I click on the "Education Organizations" link
When I click on the row containing "92d6d5a0-852c-45f4-907a-912752831772"
Then the row expands below listing the rest of the attributes for the item
When I click on the "Parent Education Organization[1]" link
Then I click on the "Feeder Education Organizations" link
Then I click on the row containing "92d6d5a0-852c-45f4-907a-912752831772"
Then the row expands below listing the rest of the attributes for the item
When I click on the "Parent Education Organization" link
Then I click on the "Feeder Education Organizations" link
And I have navigated to the "EducationOrganizations" listing of the Data Browser
Then I should navigate to "/entities/educationOrganizations/bd086bae-ee82-4cf2-baf9-221a9407ea07"
Then I change the isAdminRole flag for role "Educator" to in the realm "Daybreak" to be "false"


@DE1948
Scenario: Traverse Edorg Hiearchy from SEA down to LEA
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
Then I should be redirected to the Data Browser home page
And I navigate to myself as user "rrogers" of edorg "Illinois State Board of Education"
When I click on the "Education Organizations" link
Then I should be on the detailed page for an SEA
When I click on the "Feeder Education Organizations" link
Then I should be on the detailed page for an LEA

@wip
Scenario: Click on an entity ID in Simple View (same for Detail View)

Given I have an open web browser
And I am authenticated to SLI IDP as user "jwashington" with pass "jwashington1234"
And I have navigated to the "Teacher to Section List" page of the Data Browser
When I click on any of the entity IDs
Then I am redirected to the particular entity Detail View


Scenario Outline: EducationOrganization table should be displayed and displayed only on the EducationOrganization pages 

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm <Realm> in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials <User> <Password> for the "Simple" login page
And I navigate to myself as user <User> of edorg <EdOrg>
Then I should go to the <Page> page and look for the EdOrg table with a <Result> result

Examples:
  | Realm                                    | User           | Password           | Page                           | Result | EdOrg                          |
  | "Illinois Daybreak School District 4529" | "jstevenson"   | "jstevenson1234"   | "Education Organizations"     | "Pass" | "Daybreak School District 4529" |
  | "Illinois Daybreak School District 4529" | "jstevenson"   | "jstevenson1234"   | "Programs"                    | "Fail" | "Daybreak School District 4529" |
  | "Illinois Daybreak School District 4529" | "jstevenson"   | "jstevenson1234"   | "Cohorts"                     | "Fail" | "Daybreak School District 4529" | 

Scenario: EducationOrganization table should have the following counts in the table

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
And I click on the "Daybreak School District 4529" link
And I click on the "Feeder Education Organizations" link
Then I should see a count of <Total> for id <ID> staff total and <Current> for current

  | ID						            | Total	| Current |
  |a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb	| 4	    | 4	      |
  |8cc0a1ac-ccb5-dffc-1d74-32964722179b	| 0	    | 0       |
  |ec2e4218-6483-4e9c-8954-0aecccfd4731 | 1     | 1       |

Scenario: Pagination should work appropriately in the Databrowser

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
Then I should navigate to "/entities/educationOrganizations/ec2e4218-6483-4e9c-8954-0aecccfd4731/studentSchoolAssociations/students"
And I should see a row containing "414106a9-6156-1003-a477-4bd4dda7e21a_id"
And I should NOT see a row containing "414106a9-6156-1011-a477-4bd4dda7e21a_id"
When I click on the "Next" link
And I should see a row containing "414106a9-6156-1020-a477-4bd4dda7e21a_id"
When I click on the "Last" link
And I should see a row containing "ffee781b-22b1-4015-81ff-3289ceb2c113_id"
When I click on the "Prev" link
And I should see a row containing "5dd72fa0-98fe-4017-ab32-0bd33dc03a81_id"
When I click on the "First" link
And I should see a row containing "414106a9-6156-1003-a477-4bd4dda7e21a_id"
When I click on the "50" link
And I should see a row containing "414106a9-6156-1020-a477-4bd4dda7e21a_id"
When I click on the "25" link
Then I should navigate to "/entities/educationOrganizations/a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb/studentSchoolAssociations/students"
When I click on the "Next" link
And I should see a row containing "fff656b2-5031-4897-b6b8-7b0f5769b482_id"


Scenario: Validate that the headers are correct for students, parents, staff and teachers
    Given I change the isAdminRole flag for role "Educator" to in the realm "Daybreak" to be "true"
    And I have an open web browser
    And I navigated to the Data Browser Home URL
    And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
    Then I should be redirected to the Data Browser home page
    Then I should navigate to <Link> and see columns in the correct order

    | Link                 |
    | "/entities/parents"  |
    | "/entities/staff"    |
    | "/entities/teachers" |
    | "/entities/students" |

    Given I change the isAdminRole flag for role "Educator" to in the realm "Daybreak" to be "false"

Scenario: There should be a pound sign next to the links that should be clickable and show the counts.

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
And I click on the "Daybreak School District 4529" link
Then I should click on the <Number> link pound and get <Text> returned

    | Number | Text      |
    | 4      | 1         |
    | 8      | 13        |
    | 16     | 1         |
    | 19     | 4 / 4     |
    | 21     | 0 / 0     |
    | 25     | N/A       |
