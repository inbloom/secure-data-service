@RALLY_US4328
Feature: As an SLI platform, I want to denormalize data correctly when posting data to SuperDocs.

  Background: Logged in as Illinois Daybreak IT Admin James Stevenson
    Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
    And format "application/json"

  Scenario Outline: Check whether entity references are populated correctly in SuperDocs
    # Pre-POST check
    When I look at "<Reference 1 ID>" in the "<Referencing Collection>"
    Then I should not find "<Reference 2 ID>" in "<Reference Field>"
    # POST
    Given a valid json document for a <Entity Type>
    When I navigate to POST "/<Entity URI>"
    Then I should receive a return code of 201
    And I should receive a new ID
    # Read
    When I navigate to GET "/<Entity URI>/<NEW ID>"
    Then I should receive a return code of 200
    And "<Reference 1>" should be "<Reference 1 ID>"
    And "<Reference 2>" should be "<Reference 2 ID>"
    # Reference should be populated
    When I look at "<Reference 1 ID>" in the "<Referencing Collection>"
    Then I should find "<Reference 2 ID>" in "<Reference Field>"
    # DELETE
    When I navigate to DELETE "/<Entity URI>/<NEW ID>"
    Then I navigate to GET "/<Entity URI>/<NEW ID>"
    And I should receive a return code of 404
    When I look at "<Reference 1 ID>" in the "<Referencing Collection>"
    Then I should not find "<Reference 2 ID>" in "<Reference Field>"

     Examples:
       | Entity Type               | Entity URI                 | Reference 1 | Reference 1 ID  | Reference 2 | Reference 2 ID            | Referencing Collection | Reference Field |
       | studentSectionAssociation | studentSectionAssociations | studentId   | <MARVIN MILLER> | sectionId   | <8TH GRADE ENGLISH SEC 6> | student                | section         |
