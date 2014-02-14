@smoke @javascript
Feature:
  The administration tool can be used:
    - by a developer to register a new application
    - by an operator to approve a developer application
    - by a developer to enable her application for education organizations
    - by IT administrators to authorize or deauthorize applications
    - by realm administrators to modify realm information

Background:
  Given I have an open web browser
    And I have an open browser

Scenario: A developer registers an application
  Given I am a valid inBloom developer
    And I am managing my applications
   When I submit a new application for registration
   Then the application should get registered
    And the application status should be pending

Scenario: An operator approves an application
  Given I am a valid inBloom operator
    And I am managing my applications
    And I see a pending application
   When I approve the pending application
   Then the application status should be approved

Scenario: A developer enables education organizations for her application
  Given I am a valid inBloom developer
    And I am managing my applications
    And I have an in-progress application
   When I edit the in-progress application
    And enable the application for an education organization
   Then the application should be ready

Scenario: A tenant-level administrator authorizes an application for education organizations
  Given I am a valid tenant-level administrator
    And I am managing my application authorizations
   When I edit the authorizations for an application
    And authorize the application for all education organizations
   Then the application should be approved for all education organizations

Scenario: A district-level administrator de-authorizes an application for education organizations
  Given I am a valid district-level administrator
    And I am managing my application authorizations
   When I edit the authorizations for an application
    And de-authorize the application for all education organizations
   Then the application should not be approved

#Scenario: Realm administrator operations
#Given I am a valid realm administrator
#When I authenticate on the realm editing tool
#When I see the realms for "Sunset School District 4526 (IL-SUNSET)"
#And I click the "Illinois Sunset School District 4526" edit button
#And I should see that I am on the "Illinois Sunset School District 4526" edit page
#And I should enter "Smoke" into the Display Name field
#And I should click the "Save" button
#Then I see the realms for "Sunset School District 4526 (IL-SUNSET)"
#And the realm "Smoke" will exist
#And I should receive a notice that the realm was successfully "updated"
#And I click the "Smoke" edit button
#And I should see that I am on the "Smoke" edit page
#And I should enter "Illinois Sunset School District 4526" into the Display Name field
#And I should click the "Save" button
#Then I see the realms for "Sunset School District 4526 (IL-SUNSET)"
#And the realm "Illinois Sunset School District 4526" will exist
