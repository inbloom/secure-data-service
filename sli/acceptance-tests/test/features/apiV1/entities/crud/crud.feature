@RALLY_US209
Feature: As an SLI application, I want to be able to perform CRUD operations on various resources
This means I want to be able to perform CRUD on all entities.
and verify that the correct links are made available.

Background: Nothing yet
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
      And format "application/vnd.slc+json"

    Scenario Outline: CRUD operations on an entity
       Given entity URI <Entity Resource URI>
        # Create
       Given a valid entity json document for a <Entity Type>
        When I navigate to POST "/<ENTITY URI>"
        Then I should receive a return code of 201
         And I should receive a new entity URI
        # Read
        When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
        Then I should receive a return code of 200
         And the response should contain the appropriate fields and values
         And "entityType" should be <Entity Type>
         And I should receive a link named "self" with URI "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
         And the tenant ID of the entity should be "IL"
        # Update
        When I set the <Update Field> to <Updated Value>
         And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
        Then I should receive a return code of 204
         And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
         And <Update Field> should be <Updated Value>
        # Delete
        When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
        Then I should receive a return code of 204
         And I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
         And I should receive a return code of 404

Examples:
| Entity Type                    | Entity Resource URI       | Update Field             | Updated Value                                |
| "assessment"                   | "assessments"             | "assessmentTitle"        | "Advanced Placement Test - Subject: Writing" |
| "attendance"                   | "attendances"             | "studentId"              | "274f4c71-1984-4607-8c6f-0a91db2d240a"       |
| "cohort"                       | "cohorts"                 | "cohortDescription"      | "frisbee golf team"                          |
| "course"                       | "courses"                 | "courseDescription"      | "Advanced Linguistic Studies"                |
| "disciplineAction"             | "disciplineActions"       | "disciplineDate"         | "2012-03-18"                                 |
| "disciplineIncident"           | "disciplineIncidents"     | "incidentTime"           | "01:02:15"                                   |
| "educationOrganization"        | "educationOrganizations"  | "nameOfInstitution"      | "Bananas School District"                    |
| "gradebookEntry"               | "gradebookEntries"        | "gradebookEntryType"     | "Homework"                                   |
| "learningObjective"            | "learningObjectives"      | "academicSubject"        | "Mathematics"                                |
| "learningStandard"             | "learningStandards"       | "gradeLevel"             | "Ninth grade"                                |
| "parent"                       | "parents"                 | "parentUniqueStateId"    | "ParentID102"                                |
| "program"                      | "programs"                | "programSponsor"         | "State Education Agency"                     |
| "school"                       | "schools"                 | "nameOfInstitution"      | "Yellow Middle School"                       |
| "section"                      | "sections"                | "sequenceOfCourse"       | "2"                                          |
| "session"                      | "sessions"                | "totalInstructionalDays" | "43"                                         |
| "staff"                        | "staff"                   | "sex"                    | "Female"                                     |
| "student"                      | "students"                | "sex"                    | "Female"                                     |
| "studentAcademicRecord"        | "studentAcademicRecords"  | "sessionId"              | "62101257-592f-4cbe-bcd5-b8cd24a06f73"       |
| "studentSectionGradebookEntry" | "studentGradebookEntries" | "diagnosticStatement"    | "Finished the quiz in 5 hours"               |
| "teacher"                      | "teachers"                | "highlyQualifiedTeacher" | "false"                                      |
| "grade"                        | "grades"                  | "gradeType"              | "Mid-Term Grade"                             |
| "studentCompetency"            | "studentCompetencies"     | "diagnosticStatement"    | "advanced nuclear thermodynamics"            |
| "gradingPeriod"                | "gradingPeriods"          | "endDate"                | "2015-10-15"                                 |
| "reportCard"                   | "reportCards"             | "numberOfDaysAbsent"     | "17"                                         |

    Scenario Outline: CRUD operations on invalid entities
    Given entity URI <Entity Resource URI>
    #Read invalid
     When I navigate to GET "/<ENTITY URI>/<INVALID REFERENCE>"
     Then I should receive a return code of 404
    #Update Invalid
    Given a valid entity json document for a <Entity Type>
     When I set the <Update Field> to <Updated Value>
     When I navigate to PUT "/<ENTITY URI>/<INVALID REFERENCE>"
     Then I should receive a return code of 404
    #Delete Invalid
     When I navigate to DELETE "/<ENTITY URI>/<INVALID REFERENCE>"
     Then I should receive a return code of 404

Examples:
| Entity Type                    | Entity Resource URI       | Update Field             | Updated Value                                |
| "assessment"                   | "assessments"             | "assessmentTitle"        | "Advanced Placement Test - Subject: Writing" |
| "attendance"                   | "attendances"             | "schoolYearAttendance"   | "[]"                                         |
| "cohort"                       | "cohorts"                 | "cohortDescription"      | "frisbee golf team"                          |
| "course"                       | "courses"                 | "courseDescription"      | "Advanced Linguistic Studies"                |
| "disciplineAction"             | "disciplineActions"       | "disciplineDate"         | "2012-03-18"                                 |
| "disciplineIncident"           | "disciplineIncidents"     | "incidentTime"           | "01:02:15"                                   |
| "educationOrganization"        | "educationOrganizations"  | "nameOfInstitution"      | "Bananas School District"                    |
| "gradebookEntry"               | "gradebookEntries"        | "gradebookEntryType"     | "Homework"                                   |
| "learningObjective"            | "learningObjectives"      | "academicSubject"        | "Mathematics"                                |
| "learningStandard"             | "learningStandards"       | "gradeLevel"             | "Ninth grade"                                |
| "parent"                       | "parents"                 | "parentUniqueStateId"    | "ParentID102"                                |
| "program"                      | "programs"                | "programSponsor"         | "State Education Agency"                     |
| "school"                       | "schools"                 | "nameOfInstitution"      | "Yellow Middle School"                       |
| "section"                      | "sections"                | "sequenceOfCourse"       | "2"                                          |
| "session"                      | "sessions"                | "totalInstructionalDays" | "43"                                         |
| "staff"                        | "staff"                   | "sex"                    | "Female"                                     |
| "student"                      | "students"                | "sex"                    | "Female"                                     |
| "studentAcademicRecord"        | "studentAcademicRecords"  | "sessionId"              | "67ce204b-9999-4a11-aacb-000000000003"       |
| "studentSectionGradebookEntry" | "studentGradebookEntries" | "diagnosticStatement"    | "Finished the quiz in 5 hours"               |
| "teacher"                      | "teachers"                | "highlyQualifiedTeacher" | "false"                                      |
| "grade"                        | "grades"                  | "gradeType"              | "Mid-Term Grade"                             |
| "studentCompetency"            | "studentCompetencies"     | "diagnosticStatement"    | "advanced nuclear thermodynamics"            |
| "gradingPeriod"                | "gradingPeriods"          | "endDate"                | "2015-10-15"                                 |
| "reportCard"                   | "reportCards"             | "numberOfDaysAbsent"     | "17"                                         |

    Scenario Outline: Get All Entities
    Given entity URI <Entity Resource URI>
    Given parameter "limit" is "0"
     When I navigate to GET "/<ENTITY URI>"
     Then I should receive a return code of 200
      And I should receive a collection of "<Entity Count>" entities
      And each entity's "entityType" should be <Entity Type>

Examples:
| Entity Type                    | Entity Resource URI       | Entity Count |
| "assessment"                   | "assessments"             | 6 |
| "attendance"                   | "attendances"             | 2 |
| "cohort"                       | "cohorts"                 | 7 |
| "course"                       | "courses"                 | 91 |
| "disciplineAction"             | "disciplineActions"       | 3 |
| "disciplineIncident"           | "disciplineIncidents"     | 3 |
| "educationOrganization"        | "educationOrganizations"  | 59 |
| "gradebookEntry"               | "gradebookEntries"        | 4 |
| "learningObjective"            | "learningObjectives"      | 5 |
| "learningStandard"             | "learningStandards"       | 14 |
| "parent"                       | "parents"                 | 3 |
| "program"                      | "programs"                | 5 |
| "school"                       | "schools"                 | 5 |
| "section"                      | "sections"                | 93 |
| "session"                      | "sessions"                | 22 |
| "staff"                        | "staff"                   | 22 |
| "student"                      | "students"                | 83 |
| "studentAcademicRecord"        | "studentAcademicRecords"  | 2 |
| "studentSectionGradebookEntry" | "studentGradebookEntries" | 5 |
| "teacher"                      | "teachers"                | 5 |
| "grade"                        | "grades"                  | 2 |
| "studentCompetency"            | "studentCompetencies"     | 3 |
| "gradingPeriod"                | "gradingPeriods"          | 2 |
| "reportCard"                   | "reportCards"             | 3 |
