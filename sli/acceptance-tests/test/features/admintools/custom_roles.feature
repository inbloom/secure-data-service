@no_ingestion_hooks
Feature: Custom role administration

  As a state or district-level administrator
  In order to manage role-based access
  I would like to be able to define custom roles

  Background:
    Given I have an open browser
      And I am a valid district-level administrator

  Scenario: Administrator navigates to the custom roles page
     When I attempt to go to the custom roles page
     Then I should be on the custom roles page

#  Scenario: Administrator resets role mappings to defaults
#    Given I go to the custom roles page
#     When I click on the "Reset to Defaults" button
#     Then I should be forced to confirm the reset with a popup

#    When I navigate to the Custom Role Mapping Page
#    And I select "inBloom" from the dropdown and click go
#    And I was redirected to the "Simple" IDP Login page
#    When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
#    Then I have navigated to my Custom Role Mapping Page
#    When I click on the Reset Mapping button
#    And I got a warning message saying "Are you sure you want to reset the mappings to factory defaults? This will remove any custom defined roles!"
#    When I click 'OK' on the warning message
#    Then I am no longer in edtit mode
#    Then the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves
#    And the Leader, Educator, Aggregate Viewer and IT Administrator role groups have the correct default role names
#    And the IT Administrator role is the only admin role

