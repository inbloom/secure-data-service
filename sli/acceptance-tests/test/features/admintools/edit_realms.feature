@RALLY_US5215
Feature: Edit Realms Page
  As a realm admin I want to be able to create, edit, and delete realms
  
Scenario: Mega Multi Realm Test
  Given I have an open web browser
   When I hit the realm editing URL
  And I was redirected to the "Simple" IDP Login page
  And I submit the credentials "nyadmin" "nyadmin1234" for the "Simple" login page
#Scenario: Realm administrator editing an existing realm
    When I see the realms for "New York State Education System"
    And I click the "New York Realm" edit button
    Then I should see that I am on the "New York Realm" edit page
    And I should enter "Edited Fake" into the Display Name field
    And I should click the "Save" button
    Then I should be redirected back to the realm listing page
    And I should receive a notice that the realm was successfully "updated"
    And I see the realms for "New York State Education System"
    And the realm "Edited Fake" will exist
#Scenario: Realm Administrator deleting a existing realm
  When I click the "Edited Fake" delete button and confirm deletion
  Then I see the realms for "New York State Education System"
  And the realm "Fake Realm" will not exist
#Scenario: Realm Administrator creating a new realm with none existing
 When I hit the realm editing URL
  And I should see that I am on the new realm page
  And all of the input fields should be blank
  When I enter valid data into all fields
  And I should click the "Save" button
  Then I should be redirected back to the realm listing page
  And I should receive a notice that the realm was successfully "created"
  And I see the realms for "New York State Education System"
  And the realm "Brand New Realm" will exist
#Scenario: Realm creation/editing should have validation
  When I click the "Brand New Realm" edit button
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
  And I should make the IDP URL not unique
  And I should click the "Save" button
  Then I should get 1 error
  And I click on the Cancel button
#Scenario: Multi Realm creation
Given I see the realms for "New York State Education System"
And the realm "Brand New Realm" will exist
# Create realm #2
When I click on the Add new realm button
Then I should see that I am on the new realm page
And all of the input fields should be blank
When I enter data into all fields for realm "Chicken"
And I should click the "Save" button
Then I should be redirected back to the realm listing page
And I should receive a notice that the realm was successfully "created"
And I see the realms for "New York State Education System"
And the realm "Brand New Realm" will exist
And the realm "Chicken Realm" will exist
# Create realm #3
When I click on the Add new realm button
Then I should see that I am on the new realm page
And all of the input fields should be blank
When I enter data into all fields for realm "Pork"
And I should click the "Save" button
Then I should be redirected back to the realm listing page
And I should receive a notice that the realm was successfully "created"
And I see the realms for "New York State Education System"
And the realm "Brand New Realm" will exist
And the realm "Chicken Realm" will exist
And the realm "Pork Realm" will exist
# Check custom role endpoint goes to relam listing when more than one realm present
When I navigate to the Custom Role Mapping Page
Then I see the realms for "New York State Education System"
When I click the "Brand New Realm" custom roles button
Then I am shown the custom roles page for "Brand New Realm"
And the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves
# Do stuff to validate custom roles docs are being mapped to the proper realms
When I click on the Add Group button
And I type the name "Brand New Role" in the Group name textbox
When I add the right "READ_GENERAL" to the group "Brand New Role"   
And I add the role "Dummy" to the group "Brand New Role"
And I hit the save button
Then I am no longer in edit mode
And the group "Brand New Role" contains the roles "Dummy"
And the group "Brand New Role" contains the rights "Read General"
When I navigate back to the realm listing page
Then I see the realms for "New York State Education System"
When I click the "Chicken Realm" custom roles button
Then I am shown the custom roles page for "Chicken Realm"
And the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves
When I click on the Add Group button
And I type the name "Chicken Role" in the Group name textbox
When I add the right "READ_GENERAL" to the group "Chicken Role"   
And I add the role "Dummy" to the group "Chicken Role"
And I hit the save button
Then I am no longer in edit mode
And the group "Chicken Role" contains the roles "Dummy"
And the group "Chicken Role" contains the rights "Read General"
When I navigate back to the realm listing page
Then I see the realms for "New York State Education System"
When I click the "Chicken Realm" delete button and confirm deletion
And the realm "Brand New Realm" will exist
And the realm "Pork Realm" will exist
And the realm "Chicken Realm" will not exist
When I click the "Pork Realm" custom roles button
Then I am shown the custom roles page for "Pork Realm"
And the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves
When I navigate back to the realm listing page
Then I see the realms for "New York State Education System"
When I click the "Brand New Realm" custom roles button
Then I am shown the custom roles page for "Brand New Realm"
