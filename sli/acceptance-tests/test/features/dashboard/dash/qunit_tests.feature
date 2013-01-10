Feature: Run qunit tests and ensure no errors

Background:
  Given I have an open web browser
 
 Scenario: Check qunit html file.
 	When I navigate to qunit results page
 	Then I check to see if there are any qunit failures
