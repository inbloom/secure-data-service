Feature: Dashboard User Login Authentication

As a SEA/LEA user, I want to use the SLI IDP Login to authenticate
on SLI, so I could use the Dashboard application.

Background:
Given I have an open web browser
Given that dashboard has been authorized for all ed orgs

@integration @RALLY_US200 @RALLY_US5156
Scenario: Valid user login
Given the sli securityEvent collection is empty
#hitting static URL
When I access "/static/html/test.html"
Then I can see "Static HTML page"
When I navigate to the Dashboard home page
Then I should be redirected to the Realm page
When I select "Illinois Daybreak School District 4529" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
Then I should be redirected to the Dashboard landing page
And a security event matching "linda.kim from tenant Midgar logged successfully into inBloom Dashboards by slcdeveloper." should be in the sli db
And I check to find if record is in sli db collection:
     | collectionName      | expectedRecordCount | searchParameter       | searchValue  |
     | securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK  |
     | securityEvent       | 1                   | body.targetEdOrg      | IL-DAYBREAK  |   
     | securityEvent       | 1                   | body.targetEdOrgList  | IL-DAYBREAK  |
     
#hitting denied URL
When I access "/simon"
Then I am informed that "the page that you were looking for could not be found"

@RALLY_US200
Scenario: Invalid user login
When I navigate to the Dashboard home page
When I select "New York Realm" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "InvalidJohnDoe" "demo1234" for the "Simple" login page
Then I am informed that "Invalid User Name or password"

@wip
Scenario: Login with cookie
When I navigate to the Dashboard home page
Then I should be redirected to the Realm page
Then I add a cookie for linda.kim
When I navigate to the Dashboard home page
Then I should be redirected to the Dashboard landing page

@wip
Scenario: user in IDP but not in mongo
When I access "/static/html/test.html"
Then I can see "Static HTML page"
When I navigate to the Dashboard home page
Then I should be redirected to the Realm page
When I select "Sunset School District 4526" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "mario.sanchez" "mario.sanchez" for the "Simple" login page
#TODO there is a bug in the code right now
Then I am informed that "Invalid User"
