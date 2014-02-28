@track_entities
Feature: Realm Management
  As a realm administrator
  In order to manage security realms for my SDS instance
  I can add new realms, edit realms, and delete realms

Background:
  Given I have an open browser

Scenario: A realm administrator can create a realm
  Given I am a valid realm administrator
    And I am managing my realms
   When I add a new realm
   Then I see the new realm listed

Scenario: A realm administrator can edit a realm
  Given I am a valid realm administrator
    And I am managing my realms
    And I add a new realm
   When I edit that realm
    And I modify the realm name
    And I save the changes
   Then I see the edited realm listed

Scenario: A realm administrator can delete a realm
  Given I am a valid realm administrator
    And I am managing my realms
    And I add a new realm
   When I delete that realm
   Then I do not see the realm listed
