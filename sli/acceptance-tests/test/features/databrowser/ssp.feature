Feature: As a user I want to be able to search for entities by their unique IDs, sort on columns, and page new data
Background:
Given I have an open web browser
    And I navigated to the Data Browser Home URL
    And I was redirected to the Realm page
    And I choose realm "Illinois Sunset School District 4526" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the SLI IDP Login page
  When I enter "rrogers" in the username text field
    And I enter "rrogers1234" in the password text field
    And I click the IDP page Go button
  Then I should be redirected to the Data Browser home page

Scenario: Searching for a entities
  
  Then I can search for <Type> with a <Field> and get a <Result>
|Type                  |Field     | Result |
|students              |900000006 | Pass   |
|students              |waffles   | Fail   |
|teachers              |a         | Pass   |
|teachers              |waffles   | Fail   |
|parents               |6231066736| Pass   |
|parents               |waffles   | Fail   |
|educationOrganizations|IL-SUNSET | Pass   |
|educationOrganizations|waffles   | Fail   |
|staff                 |wgoodman  | Pass   |
|staff                 |waffles   | Fail   |

@wip
Scenario: Sorting in ascending/descending
  When I go to the students page
    And I click on the First Name column
  Then the order of the contents should change
  When I click again
  Then the contents should reverse

@wip
Scenario: Paging data
  When I go to the students page
  Then I should see 50 students
  When I scroll to the bottom
    And wait for 5 seconds
  Then I should see more students