Feature: Roles

  As a SLI Administrator
  I want to view and manage my roles
  So that we can easily manage new users and roles

  Scenario: Viewing Roles
  Given I have authenticated as "demo" and "demo1234"
  When I go to "/roles"
  Then I should get a "200"
  
