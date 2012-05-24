@RALLY_US209
@RALLY_US1804
@RALLY_US1961
Feature: As an SLI application, I want to be able to manage staff cohort associations.
This means I want to be able to perform CRUD on all associations.
and verify that the correct links are made available.
  
Background: Nothing yet
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
      And format "application/vnd.slc+json"

Scenario: Create a valid association
   Given a valid association json document for a "<ASSOCIATION TYPE>"
    When I navigate to POST "/<ASSOCIATION URI>"
    Then I should receive a return code of 201
     And I should receive an ID for the newly created association
    When I navigate to GET "/<ASSOCIATION URI>/<NEWLY CREATED ASSOCIATION ID>"
    Then I should receive a return code of 200
     And the response should contain the appropriate fields and values

Scenario: Read all associations
    Given parameter "limit" is "0"
    When I navigate to GET "/<ASSOCIATION URI>"
    Then I should receive a return code of 200
     And I should receive a collection of "<ASSOCIATION COUNT>" entities
     And each entity's "entityType" should be "<ASSOCIATION TYPE>"

Scenario: Read an association and confirm presentation of links
    When I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID - SINGLE>"
    Then I should receive a return code of 200
     And "entityType" should be "<ASSOCIATION TYPE>"
     And "<ENDPOINT1 FIELD>" should be "<ENDPOINT1 FIELD - SINGLE - EXPECTED VALUE>"
     And "<ENDPOINT2 FIELD>" should be "<ENDPOINT2 FIELD - SINGLE - EXPECTED VALUE>"
     And I should receive a link named "<SELF LINK NAME>" with URI "/<ASSOCIATION URI>/<ASSOCIATION ID - SINGLE>"
     And I should receive a link named "<ENDPOINT1 LINK NAME>" with URI "/<ENDPOINT1 URI>/<ENDPOINT1 ID - SINGLE>"
     And I should receive a link named "<ENDPOINT2 LINK NAME>" with URI "/<ENDPOINT2 URI>/<ENDPOINT2 ID - SINGLE>"

Scenario: Read endpoint1 of an association with a single member and confirm presentation of links
    When I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID - SINGLE>/<ENDPOINT1 URI>"
    Then I should receive a return code of 200
     And I should receive a collection of "1" entities
     And each entity's "entityType" should be "<ENDPOINT1 TYPE>"
     And each entity's "id" should be "<ENDPOINT1 ID - SINGLE>"
     And in each entity, I should receive a link named "<ASSOCIATION LINK NAME>" with URI "/<ENDPOINT1 URI>/<ENDPOINT1 ID - SINGLE>/<ASSOCIATION URI>"
     And in each entity, I should receive a link named "<ENDPOINT2 RESOLUTION LINK NAME>" with URI "/<ENDPOINT1 URI>/<ENDPOINT1 ID - SINGLE>/<ASSOCIATION URI>/<ENDPOINT2 URI>"

Scenario: Read endpoint2 of an association with a single member and confirm presentation of links
    When I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID - SINGLE>/<ENDPOINT2 URI>"
    Then I should receive a return code of 200
     And I should receive a collection of "1" entities
     And each entity's "entityType" should be "<ENDPOINT2 TYPE>"
     And each entity's "id" should be "<ENDPOINT2 ID - SINGLE>"
     And in each entity, I should receive a link named "<ASSOCIATION LINK NAME>" with URI "/<ENDPOINT2 URI>/<ENDPOINT2 ID - SINGLE>/<ASSOCIATION URI>"
     And in each entity, I should receive a link named "<ENDPOINT1 RESOLUTION LINK NAME>" with URI "/<ENDPOINT2 URI>/<ENDPOINT2 ID - SINGLE>/<ASSOCIATION URI>/<ENDPOINT1 URI>"

Scenario: Read endpoint1 of an association with a multiple members and confirm presentation of links
    Given parameter "limit" is "0"
    When I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID - MULTIPLE>/<ENDPOINT1 URI>"
    Then I should receive a return code of 200
     And I should receive a collection of "<ASSOCIATION - MULTIPLE - ENDPOINT1 COUNT>" entities
     And each entity's "entityType" should be "<ENDPOINT1 TYPE>"
     And each entity's "id" should be in the array "<ENDPOINT1 FIELD - MULTIPLE - EXPECTED VALUE>"
     And in each entity, I should receive a link named "<ASSOCIATION LINK NAME>" for a value from array "<ENDPOINT1 FIELD - MULTIPLE - EXPECTED VALUE>" with URI prefix "/<ENDPOINT1 URI>" and URI suffix "<ASSOCIATION URI>"
     And in each entity, I should receive a link named "<ENDPOINT2 RESOLUTION LINK NAME>" for a value from array "<ENDPOINT1 FIELD - MULTIPLE - EXPECTED VALUE>" with URI prefix "/<ENDPOINT1 URI>" and URI suffix "<ASSOCIATION URI>" and "<ENDPOINT2 URI>"

Scenario: Read endpoint2 of an association with multiple members and confirm presentation of links
    Given parameter "limit" is "0"
    When I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID - MULTIPLE>/<ENDPOINT2 URI>"
    Then I should receive a return code of 200
     And I should receive a collection of "<ASSOCIATION - MULTIPLE - ENDPOINT2 COUNT>" entities
     And each entity's "entityType" should be "<ENDPOINT2 TYPE>"
     And each entity's "id" should be in the array "<ENDPOINT2 FIELD - MULTIPLE - EXPECTED VALUE>"
     And in each entity, I should receive a link named "<ASSOCIATION LINK NAME>" for a value from array "<ENDPOINT2 FIELD - MULTIPLE - EXPECTED VALUE>" with URI prefix "/<ENDPOINT2 URI>" and URI suffix "<ASSOCIATION URI>"
     And in each entity, I should receive a link named "<ENDPOINT1 RESOLUTION LINK NAME>" for a value from array "<ENDPOINT2 FIELD - MULTIPLE - EXPECTED VALUE>" with URI prefix "/<ENDPOINT2 URI>" and URI suffix "<ASSOCIATION URI>" and "<ENDPOINT1 URI>"

Scenario: Read associations for endpoint1
    Given parameter "limit" is "0"
    When I navigate to GET "/<ENDPOINT1 URI>/<ENDPOINT1 ID - SINGLE>/<ASSOCIATION URI>"
    Then I should receive a return code of 200
     And I should receive a collection of "<ASSOCIATION COUNT FOR ENDPOINT 1>" entities
     And each entity's "entityType" should be "<ASSOCIATION TYPE>"
     And each entity's "<ENDPOINT1 FIELD>" should contain "<ENDPOINT1 ID - SINGLE>"

Scenario: Read associations for endpoint2
    Given parameter "limit" is "0"
    When I navigate to GET "/<ENDPOINT2 URI>/<ENDPOINT2 ID - SINGLE>/<ASSOCIATION URI>"
    Then I should receive a return code of 200
     And I should receive a collection of "<ASSOCIATION COUNT FOR ENDPOINT 2>" entities
     And each entity's "entityType" should be "<ASSOCIATION TYPE>"
     And each entity's "<ENDPOINT2 FIELD>" should contain "<ENDPOINT2 ID - SINGLE>"

Scenario: Read entities associated to endpoint1
    Given parameter "limit" is "0"
    When I navigate to GET "/<ENDPOINT1 URI>/<ENDPOINT1 ID - SINGLE>/<ASSOCIATION URI>/<ENDPOINT2 URI>"
    Then I should receive a return code of 200
     And I should receive a collection of "<RESOLUTION COUNT FOR ENDPOINT 1>" entities
     And each entity's "entityType" should be "<ENDPOINT2 TYPE>"

Scenario: Read entities associated to endpoint2
    Given parameter "limit" is "0"
    When I navigate to GET "/<ENDPOINT2 URI>/<ENDPOINT2 ID - SINGLE>/<ASSOCIATION URI>/<ENDPOINT1 URI>"
    Then I should receive a return code of 200
     And I should receive a collection of "<RESOLUTION COUNT FOR ENDPOINT 2>" entities
     And each entity's "entityType" should be "<ENDPOINT1 TYPE>"

Scenario: Update association
   Given format "application/json"
    When I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID FOR UPDATE>"
    Then "<UPDATE FIELD>" should be "<UPDATE FIELD EXPECTED VALUE>"
    When I set the "<UPDATE FIELD>" to "<UPDATE FIELD NEW VALID VALUE>"
     And I navigate to PUT "/<ASSOCIATION URI>/<ASSOCIATION ID FOR UPDATE>"
    Then I should receive a return code of 204
     And I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID FOR UPDATE>"
     And "<UPDATE FIELD>" should be "<UPDATE FIELD NEW VALID VALUE>"

Scenario: Non-happy path: Attempt to create association with invalid reference for endpoint1
   Given a valid association json document for a "<ASSOCIATION TYPE>"
    When I set the "<ENDPOINT1 FIELD>" to "<INVALID REFERENCE>"
    When I navigate to POST "/<ASSOCIATION URI>"
    Then I should receive a return code of 400

Scenario: Non-happy path: Attempt to create association with invalid reference for endpoint2
   Given a valid association json document for a "<ASSOCIATION TYPE>"
    When I set the "<ENDPOINT2 FIELD>" to "<INVALID REFERENCE>"
    When I navigate to POST "/<ASSOCIATION URI>"
    Then I should receive a return code of 400

Scenario: Non-happy path: Attempt to read a non-existing association
    When I navigate to GET "/<ASSOCIATION URI>/<INVALID REFERENCE>"
    Then I should receive a return code of 404

Scenario: Non-happy path: Attempt to update a non-existing association
   Given a valid association json document for a "<ASSOCIATION TYPE>"
    When I set the "<ENDPOINT2 FIELD>" to "<INVALID REFERENCE>"
    When I navigate to PUT "/<ASSOCIATION URI>/<INVALID REFERENCE>"
    Then I should receive a return code of 404

Scenario: Non-happy path: Attempt to update endpoint1 to an invalid reference
    When I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID FOR UPDATE>"
     And I set the "<ENDPOINT1 FIELD>" to "<INVALID REFERENCE>"
     And I navigate to PUT "/<ASSOCIATION URI>/<ASSOCIATION ID FOR UPDATE>"
    Then I should receive a return code of 400
     And the error message should indicate "<VALIDATION>"

Scenario: Non-happy path: Attempt to update endpoint2 to an invalid reference
    When I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID FOR UPDATE>"
     And I set the "<ENDPOINT2 FIELD>" to "<INVALID REFERENCE>"
     And I navigate to PUT "/<ASSOCIATION URI>/<ASSOCIATION ID FOR UPDATE>"
    Then I should receive a return code of 400
     And the error message should indicate "<VALIDATION>"

Scenario: Non-happy path: Attempt to delete a non-existing association
    When I navigate to DELETE "/<ASSOCIATION URI>/<INVALID REFERENCE>"
    Then I should receive a return code of 404

Scenario: Delete association
   Given format "application/json"
    When I navigate to DELETE "/<ASSOCIATION URI>/<ASSOCIATION ID FOR DELETE>"
    Then I should receive a return code of 204
     And I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID FOR DELETE>"
     And I should receive a return code of 404
