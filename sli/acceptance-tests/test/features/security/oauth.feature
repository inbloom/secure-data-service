Feature: OAuth edge cases
 
Scenario: Make call to /token with invalid auth code

Given I make a call to /token with an invalid auth code
Then I get a "400" response code
And the response error is "invalid_grant"

Scenario: Make call to /token with invalid client_id

Given I make a call to /token with an invalid client_id
Then I get a "400" response code
And the response error is "invalid_client"

Scenario: Make call to /token with an expired auth code

Given I make a call to /token with an expired auth code
Then I get a "400" response code
And the response error is "invalid_grant"
