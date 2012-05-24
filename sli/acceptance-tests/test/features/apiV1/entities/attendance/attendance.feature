@crud
@RALLY_US209
Feature: As an SLI application, I want to be able to perform CRUD on attendance

Background: Logged in as a super-user and using the small data set
  Given I am logged in using "demo" "demo1234" to realm "SLI"
  Given format "application/json"
  Given I am testing the 'v1attendance' entity

@create
Scenario: Create a new entity
  When I POST a new entity
  Then I should receive a new entity ID
  When I GET the new entity
  Then the response should match the POSTed entity

@read
Scenario: Read an entity by id
  When I GET an existing entity
  Then the response should contain the expected entity

@update
Scenario: Update an existing entity
  When I GET an existing entity
  Then the response should contain the expected entity
  When I update fields in the existing entity
  And I PUT the updated entity
  Then I should receive a return code of 204
  When I GET the existing entity
  Then the response should contain the updated fields

@delete
Scenario: Delete an existing entity
  When I DELETE an existing entity
  Then I should receive a return code of 204
  When I GET the existing entity
  Then I should receive a return code of 404

# negative tests TODO
# make test with bad references, should fail to post and put
# make entity with bad fields, should fail to post and put
# get an entitiy that doesn't exist
# update an entity that doesn't exist
# access incorrect root, expect 404

