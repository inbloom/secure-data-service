@RALLY_DE87
@RALLY_US209
@RALLY_US1747
Feature: As an SLI application, I want to be able to delete an entity and trigger a cascade deletion
    This means any entity referencing the deleted entity should also be deleted

Background:
    Given I am logged in using "cgrayadmin" "cgrayadmin1234" to realm "IL"
    Given format "application/vnd.slc+json"
    
Scenario: Confirm cascade deletion involving arrays of references do NOT delete the entity containing the array, only the reference from the array
    When I navigate to GET "/<SECTION URI>/<SECTION ID 2 FOR ARRAY TEST>"
    Then I should receive a return code of 200
     And I should see 2 "assessmentReferences"
     And one value in "assessmentReferences" should be "<ASSESSMENT ID 1 FOR ARRAY TEST>"
     And one value in "assessmentReferences" should be "<ASSESSMENT ID 2 FOR ARRAY TEST>"
    When I navigate to DELETE "/<ASSESSMENT URI>/<ASSESSMENT ID 1 FOR ARRAY TEST>"
    Then I should receive a return code of 204
    When I navigate to GET "/<ASSESSMENT URI>/<ASSESSMENT ID 1 FOR ARRAY TEST>"
    Then I should receive a return code of 404
    When I navigate to GET "/<SECTION URI>/<SECTION ID 2 FOR ARRAY TEST>"
    Then I should receive a return code of 200
     And I should see 1 "assessmentReferences"
     And one value in "assessmentReferences" should be "<ASSESSMENT ID 2 FOR ARRAY TEST>"
    When I navigate to DELETE "/<ASSESSMENT URI>/<ASSESSMENT ID 2 FOR ARRAY TEST>"
    Then I should receive a return code of 204
    When I navigate to GET "/<ASSESSMENT URI>/<ASSESSMENT ID 2 FOR ARRAY TEST>"
    Then I should receive a return code of 404
    When I navigate to GET "/<SECTION URI>/<SECTION ID 2 FOR ARRAY TEST>"
    Then I should receive a return code of 200
     And I should see 0 "assessmentReferences"
     
    
    
Scenario: Delete a school and confirm deletion of related entities, associations, and their cascading entities and associations
    When I navigate to GET "/<SECTION URI>/<SECTION ID 2>"
    Then I should receive a return code of 200
     And I should receive a link named "getSchool" with URI "/<SCHOOL URI>/<SCHOOL ID 2>"
    When I navigate to GET "/<TEACHER SECTION ASSOCIATION URI>/<TEACHER SECTION ASSOCIATION ID 2>"
    Then I should receive a return code of 200
     And I should receive a link named "getSection" with URI "/<SECTION URI>/<SECTION ID 2>"
     And I should receive a link named "getTeacher" with URI "/<TEACHER URI>/<TEACHER ID 2>"
    When I navigate to DELETE "/<SCHOOL URI>/<SCHOOL ID 2>"
    Then I should receive a return code of 204
    When I navigate to GET "/<SCHOOL URI>/<SCHOOL ID 2>"
    Then I should receive a return code of 404
    When I navigate to GET "/<SECTION URI>/<SECTION ID 2>"
    Then I should receive a return code of 404
    When I navigate to GET "/<TEACHER SECTION ASSOCIATION URI>/<TEACHER SECTION ASSOCIATION ID 2>"
    Then I should receive a return code of 404
