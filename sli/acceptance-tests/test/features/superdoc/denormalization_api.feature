#
# Note - this feature test is checking for the existing of entity references within superdocs.
#     Security context tests for generic CRUD operations on these entity types are defined in
#     the v1API CRUD tests and not repeated here.
#

@RALLY_US4328
Feature: As an SLI platform, I want to denormalize data to super-docs correctly when posting data.

  Background: Logged in as Illinois Daybreak IT Admin James Stevenson
    Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
    And format "application/json"

  Scenario Outline: Check whether entity references are populated correctly in SuperDocs

    # Pre-POST check
    When I look at "<Parent ID>" in the "<Parent Collection>"
    Then I should not find "<Reference ID>" in "<Reference Field>"

    # POST
    Given a valid json document for a <Entity Type>
    When I navigate to POST "/<Entity URI>"
    Then I should receive a return code of 201
    And I should receive a new ID

    # Read
    When I navigate to GET "/<Entity URI>/<NEW ID>"
    Then I should receive a return code of 200
    # Reference should be inserted
    When I look at "<Parent ID>" in the "<Parent Collection>"
    Then I should find "<Reference ID>" in "<Reference Field>"

    # DELETE and make sure reference is also deleted
    When I navigate to DELETE "/<Entity URI>/<NEW ID>"
    Then I should receive a return code of 204
    When I navigate to GET "/<Entity URI>/<NEW ID>"
    And I should receive a return code of 404
    When I look at "<Parent ID>" in the "<Parent Collection>"
    Then I should not find "<Reference ID>" in "<Reference Field>"

  Examples:
       | Entity Type               | Entity URI                 | Parent Collection        | Parent ID                     | Reference Field | Reference ID              |
       | studentSectionAssociation | studentSectionAssociations | student                  | <MARVIN MILLER>               | section         | <8TH GRADE ENGLISH SEC 6> |
       | studentProgramAssociation | studentProgramAssociations | student                  | <MARVIN MILLER>               | program         | <ACC TEST PROG 2>         |
       | studentAssessment         | studentAssessments         | student                  | <MARVIN MILLER>               |studentAssessment | <STUDENT ASSESSMENT ID>  |
#       | studentAcademicRecord     | studentAcademicRecords     | studentSchoolAssociation | <MARVIN MILLER EAST DB JR HI> | sessions        | <SPRING SEMESTER>               |
#       | attendance                | attendances                | studentSchoolAssociation | <MARVIN MILLER EAST DB JR HI> | session         | <FALL 2011>               |


  Scenario Outline: Check whether entity references are populated correctly in SuperDocs
  #Login as a different user who has access
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/json"
  # Pre-POST check
    When I look at "<Parent ID>" in the "<Parent Collection>"
    Then I should not find "<Reference ID>" in "<Reference Field>"

  # POST
    Given a valid json document for a <Entity Type>
    When I navigate to POST "/<Entity URI>"
    Then I should receive a return code of 201
    And I should receive a new ID

  # Read
    When I navigate to GET "/<Entity URI>/<NEW ID>"
    Then I should receive a return code of 200
  # Reference should be inserted
    When I look at "<Parent ID>" in the "<Parent Collection>"
    Then I should find "<Reference ID>" in "<Reference Field>"

  # DELETE and make sure reference is also deleted
    When I navigate to DELETE "/<Entity URI>/<NEW ID>"
    Then I should receive a return code of 204
    When I navigate to GET "/<Entity URI>/<NEW ID>"
    And I should receive a return code of 404
    When I look at "<Parent ID>" in the "<Parent Collection>"
    Then I should not find "<Reference ID>" in "<Reference Field>"

  Examples:
    | Entity Type               | Entity URI                 | Parent Collection        | Parent ID                     | Reference Field | Reference ID              |
    | studentCohortAssociation  | studentCohortAssociations  | student                  | <MARVIN MILLER>               | cohort         | <ACC-TEST-COH-2>               |
