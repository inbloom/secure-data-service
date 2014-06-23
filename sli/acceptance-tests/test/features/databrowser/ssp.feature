@RALLY_US215
Feature: As a user I want to be able to search for entities by their unique IDs, sort on columns, and page new data
Background:
Given I have an open web browser
Given I ping Simple IDP
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  And that databrowser has been authorized for all ed orgs

Scenario Outline: Searching for a entities
  When I can search for <Type> with a <Field>
  Then I get result <Result>
  Examples:
    |Type                  |Field      | Result |
    |students              |900000006  | Pass   |
    |students              |waffles    | Fail   |
    |parents               |6231066736 | Pass   |
    |parents               |ozzy       | Fail   |
    |educationOrganizations|SAGITTARON | Pass   |
    |educationOrganizations|ozzy       | Fail   |
    |staff                 |wgoodman   | Pass   |
    |staff                 |waffles    | Fail   |
    |staff                 |           | Fail   |
 # 2014-05-20 TAF :  It Admins are unable to view staff/student data when not using natural key (related to previously reported defect).  So commenting out these tests.
 #   |studentByName         |Brisendine | Pass   |
 #   |studentByName         |           | Fail   |
 #   |staffByName           |Goodman    | Pass   |
 #   |staffByName           |ozzy       | Fail   |
 #   |staffByName           |           | Fail   |
    |edOrgByName           |Sagittaron District Schools   | Pass   |
    |edOrgByName           |ozzy       | Fail   |
    |edOrgByName           |           | Fail   |


