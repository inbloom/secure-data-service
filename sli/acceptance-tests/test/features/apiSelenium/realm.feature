
Feature: <US398> Choose Identity Provider Realm
 
As a SEA/LEA user
I want a realm chooser page
So that I could be redirected on my State/District IDP login page
 
Scenario: Go to Realm page
 
Given I have an open web browser
When I navigate to the Realm page URL
Then I should be directed to the Realm page
 
Scenario: Go to Realm Login page with a chosen realm
 
Given I see the Realm page
When I choose realm "Shared Learning Initiative" in the drop-down list
And I click on the page Go button
Then I should be redirected to "SLI" Realm Login page
 
 @wip
Scenario: Go to Realm Login page without a chosen realm
 
Given I see the Realm page
And a realm in the drop-down list is not (pre)selected
When I click on the page Go button
Then I should be notified that I must choose a realm
 
 @wip
Scenario: Go to Realm Login page with switching to  an empty realm
 
Given I see the Realm page
When I choose NC in the realm drop-down list
And I choose an empty item in the drop-down list
When I click on the page Go button
Then I should be notified that I must choose a realm
 
 @wip
Scenario: Change realm
 
Given I was redirected to a State/District login page
And it is not my State/District login page
When I click on the browser Back button
Then I should be redirected to the Realm page