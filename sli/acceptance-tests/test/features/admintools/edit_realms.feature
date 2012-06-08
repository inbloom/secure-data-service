Feature: Edit Realms Page
  As a realm admin I want to be able to create, edit, and delete realms
  
Background:
  Given I have an open web browser
   When I hit the realm editing URL
  And I was redirected to the "Simple" IDP Login page
  And I submit the credentials "fakerealmadmin" "fakerealmadmin1234" for the "Simple" login page

Scenario: Realm administrator editing an existing realm
    And I should see that I am on the "Fake Realm" edit page
    And I should enter "Edited Fake" into the Display Name field
    And I should click the "Save" button
    Then I should be redirected back to the edit page
    And I should receive a notice that the realm was successfully "updated"
    And I should see that I am on the "Edited Fake" edit page
  
Scenario: Realm Administrator deleting a existing realm
   And I should see that I am on the "Edited Fake" edit page
   And I should click the delete realm link
   Then I should be redirected to a new realm page
   And I should receive a notice that the realm was successfully "deleted"
   And I should see that I am on the new realm page
   And all of the input fields should be blank
   And I should hit the role mapping page
   And I should see that the page doesn't exist
   

Scenario: Realm Administrator creating a new realm
  And I should see that I am on the new realm page
  And all of the input fields should be blank
  When I enter valid data into all fields
  And I should click the "Save" button
  Then I should be redirected back to the edit page
  And I should receive a notice that the realm was successfully "created"
  And I should see that I am on the "Brand New Realm" edit page

Scenario: Realm creation/editing should have validation
  And I should see that I am on the "Brand New Realm" edit page
  And I should remove all of the fields
  And I should click the "Save" button
  Then I should get 4 errors
  When I enter valid data into all fields
  And I should click the "Save" button
  Then I should not see any errors
  And I should make the unique identifier not unique
  And I should click the "Save" button
  Then I should get 1 error