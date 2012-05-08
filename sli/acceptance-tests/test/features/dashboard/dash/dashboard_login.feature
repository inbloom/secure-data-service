Feature: Dashboard User Login Authentication

As a SEA/LEA user, I want to use the SLI IDP Login to authenticate 
on SLI, so I could use the Dashboard application.

@wip
Scenario: Go to Dashboard page when not authenticated to SLI
#Moved to the valid user login test below
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
Then I should be redirected to the Realm page

@wip
Scenario: Go to Dashboard page when authenticated to SLI
#This login scenario is covered in student_profile tests
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
#hitting static URL
When I access "/static/html/test.html" 
Then I can see "Static HTML page"
When I navigate to the Dashboard home page
Then I should be redirected to the Realm page
When I select "Sunset School District 4526" and click go
And I wait for "1" seconds
When I login as "linda.kim" "linda.kim1234"
Then I should be redirected to the Dashboard landing page
#hitting denied URL
When I access "/simon"
Then I am informed that "Page Not Accessible"
And I am informed that "The page you are requesting is not available"

Scenario: Invalid user login

Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
And was redirected to the Realm page
When I select "New York Realm" and click go
And was redirected to the SLI-IDP login page
When I login as "InvalidJohnDoe" "demo1234"
Then I am informed that "Authentication failed"

@wip
Scenario: hitting denied URL
#covered in valid user login test 
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
When I access "/simon"
Then I am informed that "HTTP Status 403 - Access is denied"

@wip
Scenario: hitting static URL

Given I have an open web browser
Given the server is in "live" mode
    When I access "/static/html/test.html" 
Then I can see "Static HTML page"
