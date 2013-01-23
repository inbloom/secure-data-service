@smoke 
@RALLY_US4307
Feature: Data Browser: As a student, I want to login to SLI

Background:
  Given that databrowser has been authorized for all ed orgs

Scenario: Åccess home page for student using Data Browser, then logout
  Given I have an open web browser
  And I navigated to the Data Browser Home URL
  And I was redirected to the Realm page
  And I choose realm "Illinois Daybreak Students" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "cegray" "cegray1234" for the "Simple" login page
  Then I should be redirected to the Data Browser home page
  And I should see my available links labeled
  When I click on the Logout link
  And I am forced to reauthenticate to access the databrowser

@wip
Scenario: Check authentication credentials of student using Data Browser 
  Given I have an open web browser
  And I navigated to the Data Browser Home URL
  And I was redirected to the Realm page
  And I choose realm "Illinois Daybreak Students" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "cegray" "cegray1234" for the "Simple" login page
  Then I should be redirected to the Data Browser home page
  # make call to /system/session/debug
  # -> verify user name and user type

