Feature: User Session Tests
Checking that sessions work as they should

Scenario: Granting access to valid user with valid, previously obtained access token

	Given the user has previously authenticated as "cgray" with password "cgray1234" in "IL"
	And received a "valid" access token 
     When I successfully access resource "/system/session/debug" and record expiration
	Then current session's expiration is in the future
     When I successfully access resource "/system/session/debug" and record expiration
	Then current session's expiration has been extended
     When I successfully access resource "/system/session/debug" and record expiration
	Then current session's expiration has been extended
