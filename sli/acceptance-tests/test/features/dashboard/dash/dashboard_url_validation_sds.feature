Feature: Dashboard URL Validation

As a secrutiy officer, I want the application to be pretected from attacks via PathVariable.

Background:
Given I have an open web browser

Scenario: Invalid componentId PathVariable accessing DataController

Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
Then I should be redirected to the Dashboard landing page
When I access "/service/data/h@ck"
Then I am informed that "the page that you were looking for could not be found"

Scenario: Invalid componentId and id PathVariables accessing PanelController

Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
Then I should be redirected to the Dashboard landing page
When I access "/service/component/h@ck/!d"
Then I am informed that "the page that you were looking for could not be found"