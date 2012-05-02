Feature: As an SLI application, I want to be able to perform CRUD operations on various resources
This means I want to be able to perform CRUD on all entities.
and verify that the correct links are made available.
  
Background: Nothing yet
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
      And format "application/vnd.slc+json"

	Scenario Outline: CRUD operations on an entity
		# Create
	   Given a valid entity json document for a <EntityType>
	    When I navigate to POST "/v1/<EntityURI>"
	    Then I should receive a return code of 201
	     And I should receive a new entity URI
		# Read
	    When I navigate to GET "/<EntityURI>/<NEWLY CREATED ENTITY URI>"
	    Then I should receive a return code of 200
	     And the response should contain the appropriate fields and values
	     And "entityType" should be <EntityType>
	     And I should receive a link named "self" with URI "/<EntityURI>/<NEWLY CREATED ENTITY URI>"
	     And the tenant ID of the entity should be "IL"
		# Update 
	    When I set the <UpdateField> to <UpdatedValue>
	     And I navigate to PUT "/<EntityURI>/<NEWLY CREATED ENTITY URI>"
	    Then I should receive a return code of 204
	     And I navigate to GET "/<EntityURI>/<NEWLY CREATED ENTITY URI>"
	     And <UpdateField> should be <UpdatedValue>
		# Delete
	    When I navigate to DELETE "/<EntityURI>/<NEWLY CREATED ENTITY URI>"
	    Then I should receive a return code of 204
	     And I navigate to GET "/<EntityURI>/<NEWLY CREATED ENTITY URI>"
	     And I should receive a return code of 404

Examples:
| EntityType                     | EntityURI               | UpdateField              | UpdatedValue                                 |
| "assessment"                   | assessments             | "assessmentTitle"        | "Advanced Placement Test - Subject: Writing" |
| "attendance"                   | attendances             | "studentId"              | "274f4c71-1984-4607-8c6f-0a91db2d240a"       |
| "cohort"                       | cohorts                 | "cohortDescription"      | "frisbee golf team"                          |
| "course"                       | courses                 | "courseDescription"      | "Advanced Linguistic Studies"                |
| "disciplineAction"             | disciplineActions       | "disciplineDate"         | "2012-03-18"                                 |
| "disciplineIncident"           | disciplineIncidents     | "incidentTime"           | "01:02:15"                                   |
| "educationOrganization"        | educationOrganizations  | "nameOfInstitution"      | "Bananas School District"                    |
| "gradebookEntry"               | gradebookEntries        | "gradebookEntryType"     | "Homework"                                   |
| "learningObjective"            | learningObjectives      | "academicSubject"        | "Mathematics"                                |
| "learningStandard"             | learningStandards       | "gradeLevel"             | "Ninth grade"                                |
| "parent"                       | parents                 | "parentUniqueStateId"    | "ParentID102"                                |
| "program"                      | programs                | "programSponsor"         | "State Education Agency"                     |
| "school"                       | schools                 | "nameOfInstitution"      | "Yellow Middle School"                       |
| "section"                      | sections                | "sequenceOfCourse"       | "2"                                          |
| "session"                      | sessions                | "totalInstructionalDays" | "43"                                         |
| "staff"                        | staff                   | "sex"                    | "Female"                                     |
| "student"                      | students                | "sex"                    | "Female"                                     |
| "studentAcademicRecord"        | studentAcademicRecords  | "sessionId"              | "67ce204b-9999-4a11-aacb-000000000003"       |
| "studentSectionGradebookEntry" | studentGradebookEntries | "diagnosticStatement"    | "Finished the quiz in 5 hours"               |
| "teacher"                      | teachers                | "highlyQualifiedTeacher" | "false"                                      |

	Scenario Outline: CRUD operations on invalid entities
	#Read invalid
    When I navigate to GET "/<EntityURI>/<INVALID REFERENCE>"
    Then I should receive a return code of 404
    #Update Invalid
     Given a valid entity json document for a <EntityType>
    When I set the <UpdateField> to <UpdatedValue>
    When I navigate to PUT "/<EntityURI>/<INVALID REFERENCE>"
    Then I should receive a return code of 404
    #Delete Invalid
    When I navigate to DELETE "/<EntityURI>/<INVALID REFERENCE>"
    Then I should receive a return code of 404
    
Examples:
| EntityType                     | EntityURI               | UpdateField              | UpdatedValue                                 |
| "assessment"                   | assessments             | "assessmentTitle"        | "Advanced Placement Test - Subject: Writing" |
| "attendance"                   | attendances             | "schoolYearAttendance"   | "[]"                                         |
| "cohort"                       | cohorts                 | "cohortDescription"      | "frisbee golf team"                          |
| "course"                       | courses                 | "courseDescription"      | "Advanced Linguistic Studies"                |
| "disciplineAction"             | disciplineActions       | "disciplineDate"         | "2012-03-18"                                 |
| "disciplineIncident"           | disciplineIncidents     | "incidentTime"           | "01:02:15"                                   |
| "educationOrganization"        | educationOrganizations  | "nameOfInstitution"      | "Bananas School District"                    |
| "gradebookEntry"               | gradebookEntries        | "gradebookEntryType"     | "Homework"                                   |
| "learningObjective"            | learningObjectives      | "academicSubject"        | "Mathematics"                                |
| "learningStandard"             | learningStandards       | "gradeLevel"             | "Ninth grade"                                |
| "parent"                       | parents                 | "parentUniqueStateId"    | "ParentID102"                                |
| "program"                      | programs                | "programSponsor"         | "State Education Agency"                     |
| "school"                       | schools                 | "nameOfInstitution"      | "Yellow Middle School"                       |
| "section"                      | sections                | "sequenceOfCourse"       | "2"                                          |
| "session"                      | sessions                | "totalInstructionalDays" | "43"                                         |
| "staff"                        | staff                   | "sex"                    | "Female"                                     |
| "student"                      | students                | "sex"                    | "Female"                                     |
| "studentAcademicRecord"        | studentAcademicRecords  | "sessionId"              | "67ce204b-9999-4a11-aacb-000000000003"       |
| "studentSectionGradebookEntry" | studentGradebookEntries | "diagnosticStatement"    | "Finished the quiz in 5 hours"               |
| "teacher"                      | teachers                | "highlyQualifiedTeacher" | "false"                                      |

	Scenario Outline: Get All Entities
	
    Given parameter "limit" is "0"
    When I navigate to GET "/v1/<EntityURI>"
    Then I should receive a return code of 200
     And I should receive a collection of "<EntityCount>" entities
     And each entity's "entityType" should be <EntityType>

Examples:
| EntityType                     | EntityURI               | EntityCount |
| "assessment"                   | assessments             | 1 |
| "attendance"                   | attendances             | 1 |
| "cohort"                       | cohorts                 | 3 |
| "course"                       | courses                 | 6 |
| "disciplineAction"             | disciplineActions       | 1 |
| "disciplineIncident"           | disciplineIncidents     | 1 |
| "educationOrganization"        | educationOrganizations  | 59 |
| "gradebookEntry"               | gradebookEntries        | 3 |
| "learningObjective"            | learningObjectives      | 5 |
| "learningStandard"             | learningStandards       | 14 |
| "parent"                       | parents                 | 1 |
| "program"                      | programs                | 2 |
| "school"                       | schools                 | 5 |
| "section"                      | sections                | 6 |
| "session"                      | sessions                | 5 |
| "staff"                        | staff                   | 11 |
| "student"                      | students                | 80 |
| "studentAcademicRecord"        | studentAcademicRecords  | 6 |
| "studentSectionGradebookEntry" | studentGradebookEntries | 3 |
| "teacher"                      | teachers                | 4 |
