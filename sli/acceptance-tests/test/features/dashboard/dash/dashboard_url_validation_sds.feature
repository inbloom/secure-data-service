Feature: Dashboard URL Validation

As a secrutiy officer, I want the application to be pretected from attacks via PathVariable.

Background:
Given I have an open web browser

@integration @RALLY_US200 @RALLY_US147 @RALLY_US198
Scenario: Valid componentId PathVariable accessing LayoutController

Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
Then I should be redirected to the Dashboard landing page
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
And I click on student "Matt Sollars"
Then I view its student profile

@integration
Scenario: Invalid componentId PathVariable accessing LayoutController

Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
Then I should be redirected to the Dashboard landing page
When I access "/service/layout/h@ck/29343103-22c7-4ec9-9983-aa22a6375ad4"
Then I am informed that "the page that you were looking for could not be found"

@integration
Scenario: Invalid componentId and id PathVariables accessing PanelController

Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
Then I should be redirected to the Dashboard landing page
When I access "/service/component/h@ck/!d"
Then I am informed that "the page that you were looking for could not be found"