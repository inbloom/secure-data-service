@wip
Feature: Edit Realms Page
  As a realm admin I want to be able to create, edit, and delete realms
  
Background:
  Given I have an open web browser


Scenario: Go to edit realms page when having a role other than Realm Administrator
  Given I am authenticated to SLI IDP as user "leader" with pass "leader1234"
  When I hit the realm editing URL
   Then I should get a message that I am not authorized to access the page

Scenario: Realm administrator editing an existing realm
  Given I am authenticated to SLI IDP as user "sunsetrealmadmin" with pass "sunsetrealmadmin1234"
   When I hit the realm editing URL
    And I should see that I am on the "Illinois Sunset School District 4526" edit page
    And I should enter "Edited Sunset" into the Display Name field
    And I should click the "Update Realm" button
    Then I should be redirected back to the edit page
    And I should receive a notice that the realm was successfully updated
    And I should see that I am on the "Edited Sunset" edit page
  
  
Scenario: Realm Administrator deleting a realm
  Given I am authenticated to SLI IDP as user "sunsetrealmadmin" with pass "sunsetrealmadmin1234"
  When I hit the realm editing URL

Scenario: Realm Administrator creating a new realm
  Given I am authenticated to SLI IDP as user "sunsetrealmadmin" with pass "sunsetrealmadmin1234"
  When I hit the realm editing URL
  
#Scenario: Realm Administrator deleting a realm
#
