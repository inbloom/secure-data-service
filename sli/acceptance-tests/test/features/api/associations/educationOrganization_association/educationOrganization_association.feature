Feature: As an SLI application, I want to be able to manage associations between Education Organizations.
  This means I want to be able to perform CRUD on educationOrganization-associations and verify correct links
  from the association to the appropriate entities.

Background: Logged in as a super-user and using the small data set
  Given I am logged in using "demo" "demo1234"
  Given I have access to all educationOrganizations and educationOrganization-associations

Scenario: Read an Education Organization Association by ID
  Given format "application/json"
  When I navigate to GET "/educationOrganization-associations/<'GALACTICA-CAPRICA' ID>"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/educationOrganization-associations/<'GALACTICA-CAPRICA' ID>"
    And I should receive a link named "getEducationOrganizationSource" with URI "/educationOrganizations/<'GALACTICA' ID>"
    And I should receive a link named "getEducationOrganizationTarget" with URI "/educationOrganizations/<'CAPRICA' ID>"

Scenario: Read an Education Organizations list of Education Organization Associations
  Given format "application/json"
  When I navigate to GET "/educationOrganization-associations/<'GALACTICA' ID>"
  Then I should receive a return code of 200
    And I should receive a collection of 3 links
    And I should have a link with ID "<'GALACTICA-CAPRICA' ID>"
    And I should have a link with ID "<'GALACTICA-PICON' ID>"
    And I should have a link with ID "<'GALACTICA-SAGITTARON' ID>"

Scenario: Create an association between Education Organizations
  Given format "application/json"
    And "educationOrganizationIdSource" is "<'GALACTICA' ID>"
    And "educationOrganizationIdTarget" is "<'VIRGON' ID>"
  When I navigate to POST "/educationOrganization-associations"
  Then I should receive a return code of 201
    And I should receive an ID for the newly created educationOrganizatino-association
  When I navigate to GET "/educationOrganization-associations/<newly created ID>"
  Then I should receive a return code of 200

