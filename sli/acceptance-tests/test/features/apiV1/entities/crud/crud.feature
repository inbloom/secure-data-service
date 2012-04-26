Feature: As an SLI application, I want to be able to perform CRUD operations on various resources
This means I want to be able to perform CRUD on all entities.
and verify that the correct links are made available.
  
Background: Nothing yet
    Given I am logged in using "demo" "demo1234" to realm "SLI"
      And format "application/vnd.slc+json"

	Scenario Outline: CRUD operations on an entity
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
	     And the tenant ID of the entity should be "SLI"
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
| EntityType                     | EntityURI                  | UpdateField              | UpdatedValue                                 |
| "assessment"                   | "/assessments"             | "assessmentTitle"        | "Advanced Placement Test - Subject: Writing" |
| "attendance"                   | "/attendances"             | "schoolYearAttendance"   | "[]"                                         |
| "cohort"                       | "/cohorts"                 | "cohortDescription"      | "frisbee golf team"                          |
| "course"                       | "/courses"                 | "courseDescription"      | "Advanced Linguistic Studies"                |
| "disciplineAction"             | "/disciplineActions"       | "disciplineDate"         | "2012-03-18"                                 |
| "disciplineIncident"           | "/disciplineIncidents"     | "incidentTime"           | "01:02:15"                                   |
| "educationOrganization"        | "/educationOrganizations"  | "nameOfInstitution"      | "Bananas School District"                    |
| "gradebookEntry"               | "/gradebookEntries"        | "gradebookEntryType"     | "Homework"                                   |
| "learningObjective"            | "/learningObjectives"      | "academicSubject"        | "Mathematics"                                |
| "learningStandard"             | "/learningStandards"       | "gradeLevel"             | "Ninth grade"                                |
| "parent"                       | "/parents"                 | "parentUniqueStateId"    | "ParentID102"                                |
| "program"                      | "/programs"                | "programSponsor"         | "State Education Agency"                     |
| "school"                       | "/schools"                 | "nameOfInstitution"      | "Yellow Middle School"                       |
| "section"                      | "/sections"                | "sequenceOfCourse"       | "2"                                          |
| "session"                      | "/sessions"                | "totalInstructionalDays" | "43"                                         |
| "staff"                        | "/staff"                   | "sex"                    | "Female"                                     |
| "student"                      | "/students"                | "sex"                    | "Female"                                     |
| "studentSectionGradebookEntry" | "/studentGradebookEntries" | "diagnosticStatement"    | "Finished the quiz in 5 hours"               |
| "teacher"                      | "/teachers"                | "highlyQualifiedTeacher" | "false"                                      |
