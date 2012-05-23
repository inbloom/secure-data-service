@RALLY_US209
Feature: As an SLI application, I want to be able to perform CRUD operations on various resources in RC
This means I want to be able to perform CRUD on all entities.
and verify that the correct links are made available.
  
Background: Nothing yet
    Given I am logged in using "demo" "demo1234" to realm "SLI"
      And format "application/vnd.slc+json"

	Scenario Outline: CRUD operations on an entity in RC
		# Create
	   Given a valid entity json document for a <EntityType>
	    When I navigate to POST <EntityURI>
	    Then I should receive a return code of 201
	     And I should receive a new entity URI
		# Read
	    When I navigate to GET "/<NEWLY CREATED ENTITY URI>"
	    Then I should receive a return code of 200
	     And the response should contain the appropriate fields and values
	     And "entityType" should be <EntityType>
	     And I should receive a link named "self" with URI "/<NEWLY CREATED ENTITY URI>"
		# Update 
	    When I set the <UpdateField> to <UpdatedValue>
	     And I navigate to PUT "/<NEWLY CREATED ENTITY URI>"
	    Then I should receive a return code of 204
	     And I navigate to GET "/<NEWLY CREATED ENTITY URI>"
	     And <UpdateField> should be <UpdatedValue>
		# Delete
	    When I navigate to DELETE "/<NEWLY CREATED ENTITY URI>"
	    Then I should receive a return code of 204
	     And I navigate to GET "/<NEWLY CREATED ENTITY URI>"
	     And I should receive a return code of 404

Examples:
| EntityType        | EntityURI            | UpdateField              | UpdatedValue                                 |
| "student"         | "/students"          | "sex"                    | "Female"                                     |
| "assessment"      | "/assessments"       | "assessmentTitle"        | "Advanced Placement Test - Subject: Writing" |
| "parent"          | "/parents"           | "parentUniqueStateId"    | "ParentID102"                                |
| "school"          | "/schools"           | "nameOfInstitution"      | "Yellow Middle School"                       |
| "teacher"         | "/teachers"          | "highlyQualifiedTeacher" | "false"                                      |
