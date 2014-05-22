@JIRA_DS891
Feature: As a user I want to see an indication of what pages I have visited to get to my current location, and be able to travel easily back to a previously visited page
Background:
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  And that databrowser has been authorized for all ed orgs

Scenario: Moving through breadcrumbs
  When I navigated to the Data Browser Home URL
  Then I should see a breadcrumbtrail of "home"
  And I navigate to myself as user "rrogers" of edorg "Illinois State Board of Education"
  When I click on the link "Staff Education Organization Associations"
  Then I should see a breadcrumbtrail of "home > staff > 85585b27-5368-4f10-a331-3abcaf3a3f4c > staffEducationOrgAssignmentAssociations"
  When I click on the link "home"
  Then I should see a breadcrumbtrail of "home"
  And I navigate to myself as user "rrogers" of edorg "Illinois State Board of Education"
  When I click on the link "Education Organizations"
  When I click on the link "Staff Education Organization Associations"
  Then I should see a breadcrumbtrail of "home > educationOrganizations > b1bd3db6-d020-4651-b1b8-a8dba688d9e1 > staffEducationOrgAssignmentAssociations"

@DS-1144
Scenario Outline: Displaying Search Breadcrumbs
  When I navigated to the Data Browser Home URL
  When I can search for <Type> with a <Field>
  Then I should see a breadcrumbtrail of <Result>
  Examples:
    |Type                  |Field     | Result            |
    |students              |900000006 | "home > search"   |
    |parents               |6231066736| "home > search"   |
