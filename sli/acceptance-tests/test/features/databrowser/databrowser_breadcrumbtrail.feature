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
  When I click on the link "Staff Education Organization Associations"
  Then I should see a breadcrumbtrail of "home > staffEducationOrgAssignmentAssociations"
  When I click on the link "home"
  Then I should see a breadcrumbtrail of "home"
  When I click on the link "Education Organizations"
  When I click on the link "Staff Education Organization Associations"
  Then I should see a breadcrumbtrail of "home > educationOrganizations > staffEducationOrgAssignmentAssociations"

