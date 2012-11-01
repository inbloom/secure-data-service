@RALLY_US2194 @DE609 @DE1997
Feature: Blacklist validation

Background:
    Given format "application/vnd.slc+json"

Scenario: Create entities which contain invalid characters - staff
Given I am logged in using "rrogers" "rrogers1234" to realm "IL"

   Given an entity with a \u0000 character for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with a \u0001 character for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with a \u0010 character for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with a \u003c character for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with a '>' character for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with a '<' character for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

Scenario: Create entities which contain invalid words - staff
Given I am logged in using "rrogers" "rrogers1234" to realm "IL"

   Given an entity with the text 'onload' for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with the text 'onunload' for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with the text '<iframe' for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

Scenario: Create entities which contain invalid text in relaxed validation - staff
Given I am logged in using "rrogers" "rrogers1234" to realm "IL"

   Given an entity with the text '<iframe' in the description field for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with the text ' <script' in the description field for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

Scenario: Create entities which contain valid text in relaxed validation - staff
Given I am logged in using "rrogers" "rrogers1234" to realm "IL"

   Given an entity with the text '<' in the description field for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201

   Given an entity with the text '>' in the description field for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201

Scenario: Go to Create URLs which contain null byte attack characters - staff
Given I am logged in using "rrogers" "rrogers1234" to realm "IL"

  When I navigate to GET "/<NULL QUERY PARAMS>"
  Then I should receive a return code of 400

  When I navigate to GET "/<NULL QUERY NULL VALUE>"
  Then I should receive a return code of 400

  When I navigate to GET "/<NULL QUERY VALUE NULL>"
  Then I should receive a return code of 400

Scenario: Create entities which contain invalid characters - admin teacher
Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"

   Given an entity with a \u0000 character for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with a \u0001 character for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with a \u0010 character for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with a \u003c character for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with a '>' character for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with a '<' character for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

Scenario: Create entities which contain invalid words - admin teacher
Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"

   Given an entity with the text 'onload' for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with the text 'onunload' for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with the text '<iframe' for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

Scenario: Create entities which contain invalid text in relaxed validation - admin teacher
Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"

   Given an entity with the text '<iframe' in the description field for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

   Given an entity with the text ' <script' in the description field for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 400

Scenario: Create entities which contain valid text in relaxed validation - admin teacher
Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"

   Given an entity with the text '<' in the description field for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201

   Given an entity with the text '>' in the description field for a "<ENTITY TYPE>"
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201

Scenario: Go to Create URLs which contain null byte attack characters - admin teacher
Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"

  When I navigate to GET "/<NULL QUERY PARAMS>"
  Then I should receive a return code of 400

  When I navigate to GET "/<NULL QUERY NULL VALUE>"
  Then I should receive a return code of 400

  When I navigate to GET "/<NULL QUERY VALUE NULL>"
  Then I should receive a return code of 400

Scenario: Go to Create URLs which contain null byte attack characters - non-admin teacher
Given I am logged in using "cgray" "cgray1234" to realm "IL"

  When I navigate to GET "/<NULL QUERY PARAMS>"
  Then I should receive a return code of 400

  When I navigate to GET "/<NULL QUERY NULL VALUE>"
  Then I should receive a return code of 400

  When I navigate to GET "/<NULL QUERY VALUE NULL>"
  Then I should receive a return code of 400


