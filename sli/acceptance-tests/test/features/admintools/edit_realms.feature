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
    And I should receive a notice that the realm was successfully "updated"
    And I should see that I am on the "Edited Sunset" edit page
  
Scenario: Realm Administrator deleting a existing realm
  Given I am authenticated to SLI IDP as user "sunsetrealmadmin" with pass "sunsetrealmadmin1234"
  When I hit the realm editing URL
   And I should see that I am on the "Edited Sunset" edit page
   And I should click the delete realm link
   Then I should be redirected to a new realm page
   And I should receive a notice that the realm was successfully "deleted"
   And I should see that I am on the new realm page
   And all of the input fields should be blank
   And I should hit the role mapping page
   And I should see that the page doesn't exist'
   
Scenario: Realm Administrator creating a new realm
  Given I am authenticated to SLI IDP as user "sunsetrealmadmin" with pass "sunsetrealmadmin1234"
  When I hit the realm editing URL
  And I should see that I am on the new realm page
  And all of the input fields should be blank
  When I enter valid data into all fields
  And I should click the "Create Realm" button
  Then I should be redirected back to the edit page
  And I should receive a notice that the realm was successfully "created"
  And I should see that I am on the "Brand New Realm" edit page
