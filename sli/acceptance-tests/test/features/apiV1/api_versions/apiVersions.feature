@RALLY_US5029
Feature: As an API user, I want to be able to make requests to different versions of the 
  API and receive appropriate responses.

Background: Nothing yet
    Given format "application/vnd.slc+json"

Scenario Outline: Validate all links returned by the API are versioned based on input
    Given I am logged in using "<user name>" "<password>" to realm "<realm>"
     When I navigate to GET "/<version>/home"
     Then I should receive a return code of 200
      And all returned links should be version "<version>"
Examples:
| user name | password      | realm | version |
| rrogers   | rrogers1234   | IL    | v1      |
| cgray     | cgray1234     | IL    | v1      |
#| rrogers   | rrogers1234   | IL    | v1.0    |
#| cgray     | cgray1234     | IL    | v1.0    |
#| rrogers   | rrogers1234   | IL    | v1.1    |
#| cgray     | cgray1234     | IL    | v1.1    |
#| rrogers   | rrogers1234   | IL    | v2.0    |
#| cgray     | cgray1234     | IL    | v2.0    |
    
