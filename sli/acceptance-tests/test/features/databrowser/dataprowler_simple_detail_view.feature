@wip
Feature: Data Prowler
As a dataprowler user, I want to be able to see lists of objects in a simple view, and click on a single object to get a detailed view of just that object 
 
Scenario: Go to Data Prowler when authenticated SLI
 
Given I have an open web browser
And I am authenticated to SLI
When I navigate to the Data Prowler Home URL
Then I should be redirected to the Data Prowler home page
And I should see my available links labeled
 
 @wip
Scenario: Logout 

Given I have an open web browser
And I am authenticated to SLI
And I have navigated to any page of the Data Prowler
When I click on the Logout link
Then I am redirected to a page that informs me that I have signed out
And I am no longer authenticated to SLI

Scenario: Navigate to home page from any page

Given I have an open web browser
And I am authenticated to SLI
And I have navigated to any page of the Data Prowler
When I click on the Home link
Then I am redirected to the home page
 
Scenario: Associations List - Simple View

Given I have an open web browser
And I am authenticated to SLI
And I have navigated to any page of the Data Prowler
When I click on an (entity) associations link
Then I am redirected to the associations list page (a Simple View)
And I see a table displaying the associations in a list
And the table has 5 columns
And the first 4 columns of the first row of the table show the titles of the association's fields
And those names include the IDs of both of the entities of the association
And the fifth column has name Details
And the columns display the values for those fields
And the fifth column always displays Details
 
Scenario: Associations List - Expand/Collapse between Simple View and Detail View

Given I have an open web browser
And I am authenticated to SLI
And I have navigated to an (associations) Simple View
When I click on Details on a particular row
Then the row expands below (within the table frame)
And the row displays an additional frame listing the rest of the attributes for the item
When I click details on the expanded row
Then the row collapses

Scenario: Entity Detail View

Given I have an open web browser
And I am authenticated to SLI
And I have navigated to an (associations) View
When I click on the ID of any of the associating entities
Then I am redirected to a page
And that page lists all of the entity's fields (entity Detail View)
 
Scenario: Click on Available Links associations

Given I have an open web browser
And I am authenticated to SLI
And I have navigated to an entity or associations Detail View
When I click on an associations link
Then I am redirected to the particular associations Simple View
 
Scenario: Click on Available Links entities
Given I have an open web browser
And I am authenticated to SLI
And I have navigated to an entity or associations Detail View
When I click on an entity link
Then I am redirected to the particular entity Detail View
 
 @stretch
Scenario: Click on an entity ID in Simple View (same for Detail View)

Given I have an open web browser
And I am authenticated to SLI
And I have navigated to a (associations) Simple View
When I click on any of the two entity IDs
Then I am redirected to the particular entity Detail View
