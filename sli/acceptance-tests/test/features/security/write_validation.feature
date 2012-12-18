@RALLY_US4556
Feature: As an SEA/LEA end user I would like to be able to write only to data within my hierarchy through the API,
  so that, even though I can access data outside of it because I have association with particular student,
  I should not be writing into data that I don't own.

  Scenario Outline: Write operations requiring explicit associations on an entity as an IT Admin Teacher
    Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"
    And format "application/vnd.slc+json"
    Given entity URI <Entity Resource URI>
  # Bad Create
    Given a valid entity json document for a <Entity Type>
    When the entities referenced or associated edorg is out of my context
    When I post the entity
    Then I should receive a return code of 403
  # Create
    Given a valid entity json document for a <Entity Type>
    When the entities referenced or associated edorg is in my context
    When I post the entity
    Then I should receive a return code of 201
    And I should receive a new entity URI
  # Bad Update
    When I try to update the previously created entity with an invalid reference
    Then I should receive a return code of 403
  # Bad Delete
    When I try to delete an entity that is out of my write context
    Then I should receive a return code of 403

  Examples:
    | Entity Type                    | Entity Resource URI   |
    | "attendance"                   | "attendances"         |
    | "cohort"                       | "cohorts"             |
    | "course"                       | "courses"             |
    | "courseOffering"               | "courseOfferings"     |
    | "courseTranscript"             | "courseTranscripts"   |
    | "disciplineAction"             | "disciplineActions"   |
    | "disciplineIncident"           | "disciplineIncidents" |
    | "gradebookEntry"               | "gradebookEntries"    |
    | "graduationPlan"               | "graduationPlans"     |
    | "section"                      | "sections"            |
    | "session"                      | "sessions"            |
    | "studentGradebookEntry"        | "studentGradebookEntries"    |
    | "studentProgramAssociation"    | "studentProgramAssociations" |
    | "studentSectionAssociation"    | "studentSectionAssociations" |
    | "studentSchoolAssociation"     | "studentSchoolAssociations"  |
