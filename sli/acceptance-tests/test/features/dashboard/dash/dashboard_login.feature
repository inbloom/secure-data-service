Feature: Dashboard User Login Authentication

As a SEA/LEA user, I want to use the SLI IDP Login to authenticate 
on SLI, so I could use the Dashboard application.


Scenario: Go to Dashboard page when not authenticated to SLI

Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
Then I should be redirected to the Realm page

Scenario: Go to Dashboard page when authenticated to SLI

Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
And I wait for "1" seconds
When I login as "linda.kim" "linda.kim1234"
And I wait for "2" seconds
Then I should be redirected to the Dashboard landing page

Scenario: Valid user login

Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "New York Realm " and click go
And I wait for "1" seconds
When I login as "mario.sanchez" "mario.sanchez1234"
Then I should be redirected to the Dashboard landing page
#And I clicked the Submit button
#And I wait for "2" seconds
Then I should be redirected to the Dashboard landing page

Scenario: Invalid user login

Given I have an open web browser
Given the server is in "live" mode
And I navigate to the Dashboard home page
And was redirected to the Realm page
When I select "New York Realm" and click go
And was redirected to the SLI-IDP login page
And I wait for "1" seconds
When I login as "InvalidJohnDoe" "demo1234"
And I wait for "2" seconds
Then I am informed that "Authentication failed"


Scenario: hitting denied URL

Given I have an open web browser
Given the server is in "live" mode
And I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
And I wait for "1" seconds
When I login as "linda.kim" "linda.kim1234"
When I access "/simon"
And I wait for "2" seconds
Then I am informed that "HTTP Status 403 - Access is denied"

Scenario: hitting static URL

Given I have an open web browser
Given the server is in "live" mode
    When I access "/static/html/test.html" 
Then I can see "Static HTML page"
