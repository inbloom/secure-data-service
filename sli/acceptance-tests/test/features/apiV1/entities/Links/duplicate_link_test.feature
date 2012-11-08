@RALLY_US209 @RALLY_DE843
Feature: As an SLI application, I want to be able to perform CRUD operations on various resources
  This means I want to be able to perform CRUD on all entities.
  and verify that the correct links are made available.

  Background: Nothing yet
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/vnd.slc+json"

  Scenario Outline: Get All Entities
    Given entity URI <Entity Resource URI>
    Given parameter "limit" is "1"
    When I navigate to GET "/<ENTITY URI>"
    Then I should receive a return code of 200
    And I should not have any duplicate links

  Examples:
    | Entity Resource URI       |
    | "assessments"             |
    | "attendances"             |
    | "cohorts"                 |
    | "courses"                 |
    | "disciplineActions"       |
    | "disciplineIncidents"     |
    | "educationOrganizations"  |
    | "gradebookEntries"        |
    | "learningObjectives"      |
    | "learningStandards"       |
    | "parents"                 |
    | "programs"                |
    | "schools"                 |
    | "sessions"                |
    | "sections"                |
    | "staff"                   |
    | "students"                |
    | "studentAcademicRecords"  |
    | "studentGradebookEntries" |
    | "teachers"                |
    | "grades"                  |
    | "studentCompetencies"     |
    | "gradingPeriods"          |
    | "reportCards"             |
