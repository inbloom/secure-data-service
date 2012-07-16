@RALLY_US149
@RALLY_US209
Feature: As an SLI application, I want to be able to manage student transcript (course) associations.
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
    When I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID>"
    Then I should receive a return code of 200
     And "entityType" should be "<ASSOCIATION TYPE>"
     And "<ENDPOINT1 FIELD>" should be "<ENDPOINT1 FIELD EXPECTED VALUE>"
     And "<ENDPOINT2 FIELD>" should be "<ENDPOINT2 FIELD EXPECTED VALUE>"
     And I should receive a link named "<SELF LINK NAME>" with URI "/<ASSOCIATION URI>/<ASSOCIATION ID>"
     And I should receive a link named "<ENDPOINT1 LINK NAME>" with URI "/<ENDPOINT1 URI>/<ENDPOINT1 ID>"
     And I should receive a link named "<ENDPOINT2 LINK NAME>" with URI "/<ENDPOINT2 URI>/<ENDPOINT2 ID>"

Scenario: Read endpoint1 of an association and confirm presentation of links
    When I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID>/<ENDPOINT1 URI>"
    Then I should receive a return code of 200
     And I should receive a collection of "1" entities
     And each entity's "entityType" should be "<ENDPOINT1 TYPE>"
     And each entity's "id" should be "<ENDPOINT1 ID>"
     And in each entity, I should receive a link named "<ASSOCIATION LINK NAME>" with URI "/<ENDPOINT1 URI>/<ENDPOINT1 ID>/<ASSOCIATION URI>"
     And in each entity, I should receive a link named "<ENDPOINT2 RESOLUTION LINK NAME>" with URI "/<ENDPOINT1 URI>/<ENDPOINT1 ID>/<ASSOCIATION URI>/<ENDPOINT2 URI>"

Scenario: Read endpoint2 of an association and confirm presentation of links
    When I navigate to GET "/<ASSOCIATION URI>/<ASSOCIATION ID>/<ENDPOINT2 URI>"
    Then I should receive a return code of 200
     And I should receive a collection of "1" entities
     And each entity's "entityType" should be "<ENDPOINT2 TYPE>"
     And each entity's "id" should be "<ENDPOINT2 ID>"
     And in each entity, I should receive a link named "<ASSOCIATION LINK NAME>" with URI "/<ENDPOINT2 URI>/<ENDPOINT2 ID>/<ASSOCIATION URI>"
     And in each entity, I should receive a link named "<ENDPOINT1 RESOLUTION LINK NAME>" with URI "/<ENDPOINT2 URI>/<ENDPOINT2 ID>/<ASSOCIATION URI>/<ENDPOINT1 URI>"
