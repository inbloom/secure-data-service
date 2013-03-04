@sandbox
@RALLY_US2212
Feature: User authenticates against the Simple Sandbox IDP
 
 Background: Realm selector is set up to reflect one sandbox tenancies and database has redirect links
Given I have an open web browser

Scenario: Use Sandbox IDP to log in as different users
  Given I navigate to databrowser home page
  Then I was redirected to the "Simple" IDP Login page
  When I submit the developer credentials "testdeveloper" "testdeveloper1234" for the impersonation login page
  Then I should be redirected to the impersonation page
    And I should see that I "testdeveloper" am logged in
    And I want to manually imitate the user "cgray" who is a "IT Administrator"
  Then I should be redirected to the databrowser web page
  Then I should see the name "Charles Gray" on the page
  # Verify role not blank if custom role not specified
When I navigate to databrowsers "/entities/system/session/debug" page
Then I should see my roles as "IT Administrator"
And I should see my rights include "READ_PUBLIC"
And I should see my rights include "READ_GENERAL"
And I should see my rights include "AGGREGATE_READ"
#logout and login as a different impersonated user
When I logout of the databrowser
Then I should see the logout message
Given I navigate to databrowser home page
  Then I should be redirected to the impersonation page
    And I want to select "akopel" from the "SmallDatasetUsers" in automatic mode
  Then I should be redirected to the databrowser web page
  Then I should see the name "Amy Kopel" on the page
  # Verify role not blank if custom role not specified
When I navigate to databrowsers "/entities/system/session/debug" page
Then I should see my roles as "IT Administrator"
And I should see my rights include "READ_PUBLIC"
And I should see my rights include "READ_GENERAL"
And I should see my rights include "AGGREGATE_READ"
#logout of the databrowser/api and then logout of the simple-idp
When I logout of the databrowser
Then I should see the logout message
Given I navigate to databrowser home page
  Then I should be redirected to the impersonation page
  When I click on the simple-idp logout link
  Then I was redirected to the "Simple" IDP Login page
  When I submit the developer credentials "developer-email@slidev.org" "test1234" for the impersonation login page
    And I should see that I "developer-email@slidev.org" am logged in
  Then I should be redirected to the impersonation page
    And I want to manually imitate the user "cgray" who is a "IT Administrator"
  Then I should be redirected to an API error page that says invalid user

Scenario: Use Mock IDP to log in as IL Aggregate Viewer
  Given I navigate to databrowser home page
  Then I was redirected to the "Simple" IDP Login page
  When I submit the developer credentials "testdeveloper" "testdeveloper1234" for the impersonation login page
  Then I should be redirected to the impersonation page
    And I want to manually imitate the user "cgray" who is a "Aggregate Viewer"
  Then I get message that I am not authorized to use the Databrowser
  Then I should see the name "Charles Gray" on the page

Scenario: Deny logging in to non-sandbox NY Realm
  Given I navigate to databrowser home page
  Then I was redirected to the "Simple" IDP Login page
  When I submit the developer credentials "testdeveloper" "testdeveloper1234" for the impersonation login page
  Then I should be redirected to the impersonation page
    And I want to manually imitate the user "eengland" who is a "Educator"
  Then I am denied from accessing the databrowser

Scenario: Use Sandbox IDP to log in to two apps as the same impersonation user without relogging in
  Given I navigate to databrowser home page
  Then I was redirected to the "Simple" IDP Login page
  When I submit the developer credentials "testdeveloper" "testdeveloper1234" for the impersonation login page
  Then I should be redirected to the impersonation page
    And I should see that I "testdeveloper" am logged in
    And I want to manually imitate the user "rrogers" who is a "IT Administrator"
  Then I should be redirected to the databrowser web page
  Then I should see the name "Rick Rogers" on the page
  When I navigate to the Sample App I should see the name "Rick Rogers" on the page
  When I logout of the databrowser
    Then I should see the logout message
  When I navigate to the Sample App it should crash and burn
  