@wip
Feature: Data Browser
As a dataprowler user, I want to be able to see lists of objects in a simple view, and click on a single object to get a detailed view of just that object 
 
Scenario: Go to Data Browser when authenticated SLI
 
Given I have an open web browser
And I am authenticated to SLI IDP as user "jdoe" with pass "jdoe1234"
When I navigate to the Data Browser Home URL
Then I should be redirected to the Data Browser home page
And I should see my available links labeled
 
 @wip
Scenario: Logout 

Given I have an open web browser
And I am authenticated to SLI IDP as user "jdoe" with pass "jdoe1234"
And I have navigated to any page of the Data Browser
When I click on the Logout link
Then I am redirected to a page that informs me that I have signed out
And I am no longer authenticated to SLI

Scenario: Navigate to home page from any page

Given I have an open web browser
And I am authenticated to SLI IDP as user "jdoe" with pass "jdoe1234"
And I have navigated to the "My Schools" page of the Data Browser
When I click on the Home link
Then I am redirected to the home page
 
Scenario: Associations List - Simple View

Given I have an open web browser
And I am authenticated to SLI IDP as user "jdoe" with pass "jdoe1234"
When I navigate to the Data Browser Home URL
And I click on an "Teacher to Section List" associations link
Then I am redirected to the associations list page
And I see a table displaying the associations in a list
And those names include the IDs of both "TeacherId" and "SectionId" in the association
 
Scenario: Associations List - Expand/Collapse between Simple View and Detail View

Given I have an open web browser
And I am authenticated to SLI IDP as user "jdoe" with pass "jdoe1234"
And I have navigated to the "My Sections" page of the Data Browser
When I click on a particular row
Then the row expands below listing the rest of the attributes for the item
When I click on the expanded row
Then the row collapses hiding the additional attributes

Scenario: Entity Detail View

Given I have an open web browser
And I am authenticated to SLI IDP as user "jdoe" with pass "jdoe1234"
And I have navigated to the "My Sections" page of the Data Browser
When I click on a particular row
And I click on the "Me" of any of the associating entities
Then I am redirected to a page
And that page lists all of the entity's fields (entity Detail View)
 
Scenario: Click on Available Links associations

Given I have an open web browser
And I am authenticated to SLI IDP as user "jdoe" with pass "jdoe1234"
And I have navigated to an entity or associations Detail View
When I click on an associations link
Then I am redirected to the particular associations Simple View
 
Scenario: Click on Available Links entities
Given I have an open web browser
And I am authenticated to SLI IDP as user "jdoe" with pass "jdoe1234"
And I have navigated to an entity or associations Detail View
When I click on an entity link
Then I am redirected to the particular entity Detail View
 
 @wip
Scenario: Click on an entity ID in Simple View (same for Detail View)

Given I have an open web browser
And I am authenticated to SLI IDP as user "jdoe" with pass "jdoe1234"
And I have navigated to a (associations) Simple View
When I click on any of the two entity IDs
Then I am redirected to the particular entity Detail View
