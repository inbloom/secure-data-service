@RALLY_US1564
@RALLY_US1889
Feature: As an SLI application, I want to be able to manage discipline incident entities
This means I want to be able to perform CRUD on all entities.
and verify that the correct links are made available.
  
Background: Nothing yet
    Given I am logged in using "demo" "demo1234" to realm "SLI"
      And format "application/vnd.slc+json"

Scenario: Create a valid entity
   Given a valid entity json document for a "<ENTITY TYPE>"
   Given format "application/json"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201
     And I should receive an ID for the newly created entity
    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200
     And the response should contain the appropriate fields and values

Scenario: Read all entities
    Given parameter "limit" is "0"
    When I navigate to GET "/<ENTITY URI>"
    Then I should receive a return code of 200
     And I should receive a collection of "<ENTITY COUNT>" entities
     And each entity's "entityType" should be "<ENTITY TYPE>"

Scenario: Read an entity and confirm presentation of links
    When I navigate to GET "/<ENTITY URI>/<ENTITY ID>"
    Then I should receive a return code of 200
     And "entityType" should be "<ENTITY TYPE>"
     And I should receive a link named "<SELF LINK NAME>" with URI "/<ENTITY URI>/<ENTITY ID>"

Scenario: Update entity
   Given format "application/json"
    When I navigate to GET "/<ENTITY URI>/<ENTITY ID FOR UPDATE>"
    Then "<UPDATE FIELD>" should be "<UPDATE FIELD EXPECTED VALUE>"
    When I set the "<UPDATE FIELD>" to "<UPDATE FIELD NEW VALID VALUE>"
     And I navigate to PUT "/<ENTITY URI>/<ENTITY ID FOR UPDATE>"
    Then I should receive a return code of 204
     And I navigate to GET "/<ENTITY URI>/<ENTITY ID FOR UPDATE>"
     And "<UPDATE FIELD>" should be "<UPDATE FIELD NEW VALID VALUE>"

Scenario: Delete entity
   Given format "application/json"
    When I navigate to DELETE "/<ENTITY URI>/<ENTITY ID FOR DELETE>"
    Then I should receive a return code of 204
     And I navigate to GET "/<ENTITY URI>/<ENTITY ID FOR DELETE>"
     And I should receive a return code of 404

Scenario: Non-happy path Attempt to create invalid entity
   Given an invalid entity json document for a "<ENTITY TYPE>"
   Given format "application/json"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400
     And the error message should indicate "<VALIDATION>"

Scenario: Non-happy path Attempt to read a non-existing entity
    When I navigate to GET "/<ENTITY URI>/<INVALID REFERENCE>"
    Then I should receive a return code of 404
 
Scenario: Non-happy path Attempt to update an entity to an invalid state
   Given format "application/json"
    When I navigate to GET "/<ENTITY URI>/<ENTITY ID FOR UPDATE>"
    When I set the "<REQUIRED FIELD>" to "<BLANK>"
     And I navigate to PUT "/<ENTITY URI>/<ENTITY ID FOR UPDATE>"
    Then I should receive a return code of 400
     And the error message should indicate "<VALIDATION>"

Scenario: Non-happy path Attempt to update a non-existing entity
   Given a valid entity json document for a "<ENTITY TYPE>"
    When I set the "<ENDPOINT2 FIELD>" to "<INVALID REFERENCE>"
    When I navigate to PUT "/<ENTITY URI>/<INVALID REFERENCE>"
    Then I should receive a return code of 404

Scenario: Non-happy path Attempt to delete a non-existing entity
    When I navigate to DELETE "/<ENTITY URI>/<INVALID REFERENCE>"
    Then I should receive a return code of 404

