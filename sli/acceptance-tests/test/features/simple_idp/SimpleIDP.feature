@sandbox
@RALLY_US2212
Feature: User authenticates against the Simple Sandbox IDP
 
 Background: Realm selector is set up to reflect one sandbox tenancies and database has redirect links
Given I have an open web browser

Scenario: Use Mock IDP to log in as IL Educator
Given I navigate to databrowser home page
Then I will be redirected to realm selector web page
When I select the "SimpleIDP for Sandbox" realm
Then I was redirected to the "Simple" IDP Login page
When I enter the credentials "testdeveloper" "testdeveloper1234" for the Simple IDP
And I want to imitate the user "cgray" who is a "Educator"
And I click Login
And I wait for 5 second
Then I should be redirected to the databrowser web page
Then I should see the name "Charles Gray" on the page

Scenario: Deny logging in to non-sandbox NY Realm
Given I navigate to databrowser home page
Then I will be redirected to realm selector web page
When I select the "SimpleIDP for Sandbox" realm
Then I was redirected to the "Simple" IDP Login page
When I enter the credentials "testdeveloper" "testdeveloper1234" for the Simple IDP
And I want to imitate the user "eengland" who is a "Educator"
And I click Login
Then I am denied from accessing the databrowser
