@RALLY_US5594

Feature: As an API user, I want to be able to get a list authorized LEAs.

Background: An authorized bulk extract user logs in and gets the information for the extract from a HEAD call
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT

Scenario: Validate the return the app is authorized
    When I make a call retrieve the header for the bulk extract end point "/v1.1/bulk/extract/list"
    Then I get back a response code of "200"
