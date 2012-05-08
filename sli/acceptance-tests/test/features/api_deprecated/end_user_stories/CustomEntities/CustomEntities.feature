@wip
Feature: Test CRUD fuctionality of Custom Entities per application 
Application is authorized using OAuth (there is an client_id and client_secret).  Custom entities are partitioned by application. Access control inherited from parent.


Background: None

Scenario:  As an IT Admin, I want to add custom entitiy to a core entity belonging to my application
    Given  I am a valid SEA/LEA end user "demo" with password "demo1234"
    And the clientID is "demoClient"
    And a valid entity json object for a "educationOrganizations"
    And I add a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>ISAT Reading Results</DisplayName>" to the object
	When I navigate to POST "/educationOrganizations/{educationOrganizationId}/custom"
	Then I should receive a return code of 200
	
	Given  I am a valid SEA/LEA end user "demo" with password "demo1234"
    And the clientID is "SampleApplication"
    And a valid entity json object for a "educationOrganizations"
    And I add a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>SAT Scores</DisplayName>" to the object
	When I navigate to POST "/educationOrganizations/{educationOrganizationId}/custom"
	Then I should receive a return code of 200
	
	Given  I am a valid SEA/LEA end user "demo" with password "demo1234"
    And the clientID is "demoClient"
	When I navigate to GET "/educationOrganizations/{educationOrganizationId}/custom"
	Then I should recieve the educationOrganizations object with educationOrganizationId
	And I should receive a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>ISAT Reading Results</DisplayName>" in the result
		
Scenario: As an IT Admin, I want to update custom entity associated with any core entity belonging to my application
	Given  I am a valid SEA/LEA end user "demo" with password "demo1234"
    And the clientID is "demoClient"
    And a valid entity json object for a "educationOrganizations"
    And I add a key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>ISAT Writing Results</DisplayName>" to the object
	When I navigate to PUT "/educationOrganizations/{educationOrganizationId}/custom"
	Then I should receive a return code of 200
	
	Given  I am a valid SEA/LEA end user "demo" with password "demo1234"
    And the clientID is "SampleApplication"
    And a valid entity json object for a "educationOrganizations"
    And I add a key value pair "ColumnConfig" : "<?xml version=1.0?><DisplayName>SAT Scores</DisplayName>" to the object
	When I navigate to POST "/educationOrganizations/{educationOrganizationId}/custom"
	Then I should receive a return code of 200
	
	When I navigate to GET "/educationOrganizations/{educationOrganizationId}"
	Then I should recieve the "educationOrganizations" object with "educationOrganizationId"
	And I should receive key value pair "CustomConfig" : "<?xml version=1.0?><DisplayName>ISAT Writing Results</DisplayName>" in the result
	
	Given  I am a valid SEA/LEA end user "demo" with password "demo1234"
    And the clientID is "SampleApplication"
	When I navigate to GET "/educationOrganizations/{educationOrganizationId}/custom"
	Then I should recieve the "educationOrganizations" object with "educationOrganizationId"
	And I should receive a key value pair "ColumnConfig": "<?xml version=1.0?><DisplayName>SAT Scores</DisplayName>" in the result


Scenario Outline: As an educator or leader, I want to read a custom entity associated with any core entity belonging to my application 
	    Given  I am a valid SEA/LEA end user "demo" with password "demo1234"
	    And the clientID is "demoClient"
	    And a valid entity json object for a "students" with "studentId"
	    And I add a key value pair "Drives" : "True" to the object
		When I navigate to POST "/students/{studentId}/custom"
		Then I should receive a return code of "200"
		
		Given  I am a valid SEA/LEA end user <Username> with password <Password>
	    And the clientID is <ClientID>
		When I navigate to GET "/students/{studentId}/custom"
		Then I should receive a return code of <Code>
		And I should recieve the "students" object with "studentsId"
		And I should receive a key value pair <Key>: <Value> in the result
		And there is no other custom data returned
		
	Examples:
	| Username        | Password            | AnyDefaultSLIRole  | ClientID     | Key            | Value    | Code  |
	| "linda.kim"     | "linda.kim1234"     | "Educator"         | "demoClient" | "Drives"       | "True"   | "200" |
	| "mdorgan"       | "mdorgan1234"       | "AggregateViewer"  | "demoClient" | ""             | ""       | "403" |

Scenario Outline: As an user, I want to delete a custom entity associated with any core entity belonging to my application
		Given  I am a valid SEA/LEA end user "demo" with password "demo1234"
	    And the clientID is "demoClient"
	    And a valid entity json object for a "students" with "studentId"
	    And I add a key value pair "Drives" : "True" to the object
		When I navigate to POST "/students/{studentId}/custom"
		Then I should receive a return code of "200"
		
		Given  I am a valid SEA/LEA end user <Username> with password <Password>
	    And the clientID is <ClientID>
		When I navigate to DELETE "/students/{studentsId}/custom"
		Then I should receive a return code of <DelCode>
		
		When I navigate to GET "/students/{studentsId}/custom"
		Then I should receive a return code of <ReadCode>
		And I should receive a key value pair <Key>: <Value> in the result

		
	Examples:
	| Username        | Password            | AnyDefaultSLIRole  | ClientID     | Key            | Value    | DelCode   | ReadCode |
	| "linda.kim"     | "linda.kim1234"     | "Educator"         | "demoClient" | "Drives"       | "True"   | "403"     | "200"    |
	| "mdorgan"       | "mdorgan1234"       | "AggregateViewer"  | "demoClient" | ""             | ""       | "403"     | "403"    |
	| "demo"          | "demo1234"          | "ITAdmin"          | "demoClient" | ""             | ""       | "200"     | "403"    |

Scenario Outline: As an user, I want to update  a custom entity associated with any core entity belonging to my application 
		Given  I am a valid SEA/LEA end user <Username> with password <Password>
	    And the clientID is <ClientID>
	    And a valid entity json object for a "students" with "studentId"
	    And I add a key value pair <Key> : <Value> to the object
		When I navigate to PUT "/students/{studentId}/custom"
		Then I should receive a return code of <Code>

	Examples:
	| Username        | Password            | AnyDefaultSLIRole  | ClientID     | Key            | Value    | Code   | 
	| "linda.kim"     | "linda.kim1234"     | "Educator"         | "demoClient" | "Drives"       | "True"   | "403"  |
	| "mdorgan"       | "mdorgan1234"       | "AggregateViewer"  | "demoClient" | "Drives"       | "True"   | "403"  |
	| "demo"          | "demo1234"          | "ITAdmin"          | "demoClient" | "Drives"       | "True"   | "200"  |

