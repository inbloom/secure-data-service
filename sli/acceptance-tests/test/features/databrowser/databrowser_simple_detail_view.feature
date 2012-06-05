@RALLY_US215
Feature: Data Browser
As a Data Browser user, I want to be able to traverse all of the data I have access to so that I can investigate/troubleshoot issues as they come up
 
Scenario: Go to Data Browser when authenticated SLI
 
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jdoe" "jdoe1234" for the "Simple" login page
Then I should be redirected to the Data Browser home page
And I should see my available links labeled
 
Scenario: Logout 

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jdoe" "jdoe1234" for the "Simple" login page
Then I should be redirected to the Data Browser home page
When I click on the Logout link
Then I am redirected to a page that informs me that I have signed out
And I am forced to reauthenticate to access the databrowser

Scenario Outline: Navigate to home page from any page

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jdoe" "jdoe1234" for the "Simple" login page
And I have navigated to the <Page> page of the Data Browser
When I click on the "Home" link
Then I should be redirected to the Data Browser home page
 Examples:
 | Page                      |
 | "My Schools"              |
 | "Teacher to Section List" |
 | "Me"                      |
	
Scenario: Associations List - Simple View

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jdoe" "jdoe1234" for the "Simple" login page
And I click on the "Teacher to Section List" link
Then I am redirected to the associations list page
And I see a table displaying the associations in a list
And those names include the IDs of both "TeacherId" and "SectionId" in the association
 
Scenario: Associations List - Expand/Collapse between Simple View and Detail View

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jdoe" "jdoe1234" for the "Simple" login page
And I have navigated to the "My Sections" page of the Data Browser
When I click on the row containing "FHS-Science101"
Then the row expands below listing the rest of the attributes for the item
When I click on the row containing "FHS-Science101"
Then the row collapses hiding the additional attributes

Scenario Outline: Entity Detail View

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jdoe" "jdoe1234" for the "Simple" login page
And I have navigated to the <Page> page of the Data Browser
When I click on the row containing <Text>
And I click on the <Link> of any of the associating entities
Then I am redirected to a page that page lists all of the <Entity> entity's fields
 Examples:
| Page                      | Text                                  | Link         | Entity        |
| "My Sections"             | "FHS-Math101"                         | "Me"         | "FHS-Math101" |
| "Teacher to Section List" | "eb4d7e1b-7bed-890a-d574-cdb25a29fc2d"| "GetSection" | "FHS-Math101" |
| "Teacher to Section List" | "eb4d7e1b-7bed-890a-d574-cdb25a29fc2d"| "GetTeacher" | "Doe"        |
	
Scenario: Click on Available Links associations

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jdoe" "jdoe1234" for the "Simple" login page
And I have navigated to the "My Schools" page of the Data Browser
When I click on the "Teacher to School List" link
Then I am redirected to the particular associations Simple View
 
Scenario: Click on Available Links entities
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jdoe" "jdoe1234" for the "Simple" login page
And I have navigated to the "My Schools" page of the Data Browser
When I click on the "Me" link
Then I am redirected to the particular entity Detail View
 
 @wip
Scenario: Click on an entity ID in Simple View (same for Detail View)

Given I have an open web browser
And I am authenticated to SLI IDP as user "jdoe" with pass "jdoe1234"
And I have navigated to the "Teacher to Section List" page of the Data Browser
When I click on any of the entity IDs
Then I am redirected to the particular entity Detail View
