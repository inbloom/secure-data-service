Feature: Orphaned Education Organizations in Application Authorization
  As an application authorizer
  I want to see any ed orgs who are orphaned from their hierarchy
  So that I can call an administrator to correct the invalid data

Scenario: Orphaned edorgs show up under their own category and user should still be able to enable/authorize
Given I have an open web browser
When I update edorg "bd086bae-ee82-4cf2-baf9-221a9407ea07" for tenant "Midgar" and update the parentEducationAgencyReference to a reference of "b1bd3db6-d020-4651-b1b8-a8dba688d9e1-xxx"
  And I hit the Admin Application Authorization Tool
  And I select "inBloom" from the dropdown and click go
  And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
  And I see an application "SDK Sample" in the table
  And I click on the "Edit Authorizations" button next to it
  And I am redirected to the Admin Application Authorization Edit Page
Then the edorg "bd086bae-ee82-4cf2-baf9-221a9407ea07" is present in the list of orphans
  And the edorg "bd086bae-ee82-4cf2-baf9-221a9407ea07" is not present in the tree
  And I update edorg "bd086bae-ee82-4cf2-baf9-221a9407ea07" for tenant "Midgar" and update the parentEducationAgencyReference to a reference of "b1bd3db6-d020-4651-b1b8-a8dba688d9e1"
