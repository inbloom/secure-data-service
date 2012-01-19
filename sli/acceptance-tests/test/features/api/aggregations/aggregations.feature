@wip
Feature: In order to provide base aggregation information
	As a client application using SLI
	I want to know what aggregations are available to a user based on their user type.
	This means all associations should be returned as links when accessing the AGGREGATION URI.

Background: Logged in as a leader and using the small data set
	Given I am logged in using "leader" "leader1234"
	Given I have access to all entities
	
Scenario: MOCK Home URI returns a aggregation start link
	Given format "application/json"
	When I navigate to GET "<home URI>"
	Then I should receive a return code of 200
		And I should receive a link where rel is "links" and href ends with "/aggregation"

Scenario: MOCK Aggregation URI returns a valid district link
	Given format "application/json"
		And mock district ID <mock ID>
	When I navigate to GET "<aggregation URI>"
	Then I should receive a return code of 200
		And I should receive a collection of association links
		And I should receive a link where rel is "links" and href ends with "/district/" and appropriate ID

Scenario: Aggregation district URI returns valid aggregations links
	Given format "application/json"
		And mock district ID <mock ID>		
	When I navigate to GET "<district URI>"
	Then I should receive a return code of 200
		And I should receive a collection of aggregations links
		And after resolution, I should receive an object with a link named "self" with URI "/aggregations/" and appropriate ID and with district ID "<mock ID>"
