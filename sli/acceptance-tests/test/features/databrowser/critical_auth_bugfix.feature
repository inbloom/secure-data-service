Feature: Validate critical bugs do not resurface after being sqashed
I want to detect the ressurection of 'zombie bugs' for critical defects fixed previously

Scenario: Zombie Bug 1: Gaining access as the previously authenticated user when given no credentials

	Given Another user has authenticated to SLI previously
	When I access the API resource "/system/session/debug" with no authorization headers present
	Then I should receive a return code of 401
	When I access the API resource "/v1/students" with no authorization headers present
	Then I should receive a return code of 401