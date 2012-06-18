@security
Feature: SLI hosted admin authorization tests

Background:
Given I have an open web browser

Scenario: Valid SLC Operator tries to authenticate on Application Authorization Tool
Given I am a valid SLC Operator
When I try to authenticate on the Application Authorization Tool
Then I get message that I am not authorized

Scenario: Valid SLC Operator tries to authenticate on Role Mapping Tool
Given I am a valid SLC Operator
When I try to authenticate on the Role Mapping Tool
Then I get message that I am not authorized

Scenario: Valid SLC Operator access the Application Approval Tool
Given I am a valid SLC Operator
When I try to authenticate on the Application Approval Tool
Then I do not get message that I am not authorized

@wip
Scenario: Valid District Super Administrator tries to authenticate on Application Registration Tool
Given I am a valid Super Administrator
When I try to authenticate on the Application Registration Tool
Then I get message that I am not authorized

Scenario: Valid District Super Administrator tries to authenticate on Role Mapping Tool
Given I am a valid Super Administrator
When I try to authenticate on the Role Mapping Tool
Then I get message that I am not authorized
