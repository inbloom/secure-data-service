@smoke
Feature: Authenticated Users can get an address to send support emails.

Scenario: Authenticated SLI user asks for support email

	Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
	When I make an API call to get the support email
	Then I receive JSON response that includes the address
	
Scenario: Unauthenticated user asks for support email

    When the sli securityEvent collection is empty
	And I GET the url "/v1/system/support/email" using a blank cookie
	Then I should receive a "401" response
	And I check to find if record is in sli db collection:
        | collectionName  | expectedRecordCount | searchParameter         | searchValue                               | searchType |
        | securityEvent   | 1                   | body.appId              | UNKNOWN                                   | string     |
        | securityEvent   | 1                   | body.className          | org.slc.sli.api.resources.SupportResource | string     |
        | securityEvent   | 1                   | body.logMessage         | Access Denied: User must be logged in     | string     |
        # user and targetEdOrgs are not known since no session was established
    And "1" security event with field "body.actionUri" matching "http.*/api/rest/v.*/system/support/email" should be in the sli db