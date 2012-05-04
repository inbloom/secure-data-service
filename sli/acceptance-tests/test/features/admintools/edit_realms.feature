@wip
Feature: Edit Realms Page
  As a realm admin I want to be able to create, edit, and delete realms
  
Background:
  Given I have an open web browser


Scenario: Go to edit realms page when having a role other than Realm Administrator

Scenario: Realm Administrator creating a new realm
  Given I am authenticated to the SLI IDP as "realmadmin" "realmadmin1234"
  When I hit the realm editing URL
  
Scenario: Realm Administrator deleting a realm

Scenario: Realm administrator editing an existing realm