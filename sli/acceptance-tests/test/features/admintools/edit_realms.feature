
Feature: Edit Realms Page
  As a realm admin I want to be able to create, edit, and delete realms
  
Background:
  Given I have an open web browser
   When I hit the realm editing URL
  And I was redirected to the "Simple" IDP Login page
  And I submit the credentials "fakerealmadmin" "fakerealmadmin1234" for the "Simple" login page

Scenario: Realm administrator editing an existing realm
    When I see the realms for "NC-KRYPTON"
    And I click the "Fake Realm" edit button
    Then I should see that I am on the "Fake Realm" edit page
    And I should enter "Edited Fake" into the Display Name field
    And I should click the "Save" button
    Then I should be redirected back to the edit page
    And I should receive a notice that the realm was successfully "updated"
    And I see the realms for "NC-KRYPTON"
  
Scenario: Realm Administrator deleting a existing realm
  When I see the realms for "NC-KRYPTON"
  And I click the "Fake Realm" delete button and confirm deletion
  Then I see the realms for "NC-KRYPTON"
  And the realm "Fake Realm" will not exist

Scenario: Realm Administrator creating a new realm
  And I should see that I am on the new realm page
  And all of the input fields should be blank
  When I enter valid data into all fields
  And I should click the "Save" button
  Then I should be redirected back to the edit page
  And I should receive a notice that the realm was successfully "created"
  And I see the realms for "NC-KRYPTON"
  And the realm "Brand New Realm" will exist

Scenario: Realm creation/editing should have validation
  When I see the realms for "NC-KRYPTON"
  And I click the "Brand New Realm" edit button
  And I should see that I am on the "Brand New Realm" edit page
  And I should remove all of the fields
  And I should click the "Save" button
  Then I should get 5 errors
  When I enter valid data into all fields
  And I should click the "Save" button
  Then I should not see any errors
  When I click the "Brand New Realm" edit button
  And I should make the unique identifier not unique
  And I should click the "Save" button
  Then I should get 1 error
  And I should make the display name not unique
  And I should click the "Save" button
  Then I should get 1 error
