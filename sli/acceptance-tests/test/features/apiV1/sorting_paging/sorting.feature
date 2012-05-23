Feature: Sort and page API results

Scenario: Check default limit of 50
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
  Given format "application/json"
  When I navigate to GET "/v1/students"
  Then I should receive a collection with 50 elements

Scenario: Sorting a collection of student school association links by entryGradeLevel, ascending
	Given I am logged in using "jpratt" "jpratt1234" to realm "NY"
		And format "application/json"
		And parameter "sortBy" is "entryGradeLevel"
		And parameter "sortOrder" is "ascending"
	When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations"
	Then I should receive a collection
		And the link at index 0 should point to an entity with id "455a39aa-f950-4dca-a3df-9a87f990054b"
		And the link at index 1 should point to an entity with id "f05029c8-c4e8-427e-b2a4-cc89c74650a1"
		And the link at index 2 should point to an entity with id "8f423e3d-a206-4bbe-a3ab-aa37e95ba3e5"
		And the link at index 3 should point to an entity with id "69b49ecc-86f6-44b4-be99-43e9041dee2e"

Scenario: Sorting a collection of entities obtained via a hop using a (nested) field, descending
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    Given format "application/json"
    And parameter "sortBy" is "name.firstName"
    And parameter "sortOrder" is "descending"
    When I navigate to GET "/v1/educationOrganizations/<'Illinois State Ed-org' ID>/staffEducationOrgAssignmentAssociations/staff"
    Then I should receive a return code of 200
    And I should receive a collection
    And the link at index 0 should have "name.firstName" equal to "Rick"
    And the link at index 1 should have "name.firstName" equal to "Michael"
    And the link at index 2 should have "name.firstName" equal to "Christopher"

Scenario: Sorting a collection of full student school association entities
	Given I am logged in using "jpratt" "jpratt1234" to realm "NY"
	Given format "application/json"
		And parameter "sortBy" is "entryGradeLevel"
		And parameter "sortOrder" is "descending"
	When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations"
	Then I should receive a collection
		And the link at index 0 should have "entryGradeLevel" equal to "Tenth grade"
		And the link at index 1 should have "entryGradeLevel" equal to "Sixth grade"
		And the link at index 2 should have "entryGradeLevel" equal to "Seventh grade"
		And the link at index 3 should have "entryGradeLevel" equal to "Eighth grade"

Scenario: Validate PII cannot be sorted against
  Given I am logged in using "jpratt" "jpratt1234" to realm "NY"
    Given format "application/json"
    And parameter "sortBy" is "name.firstName"
    And parameter "sortOrder" is "ascending"
    When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations/students"
    Then I should receive a return code of 400

Scenario: Sorting a collection of staff through a hop (from an ed-org)
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    Given format "application/json"
    And parameter "sortBy" is "name.firstName"
    And parameter "sortOrder" is "ascending"
    When I navigate to GET "/v1/educationOrganizations/<'Illinois State Ed-org' ID>/staffEducationOrgAssignmentAssociations/staff"
    Then I should receive a return code of 200
    And I should receive a collection
    And the link at index 0 should have "name.firstName" equal to "Christopher"
    And the link at index 1 should have "name.firstName" equal to "Michael"
    And the link at index 2 should have "name.firstName" equal to "Rick"

Scenario: Paging request the first two results from an API request
  Given I am logged in using "jpratt" "jpratt1234" to realm "NY"
    Given format "application/json"
		And parameter "sortBy" is "entryGradeLevel"
		And parameter "sortOrder" is "ascending"
	When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations"
	Then I should receive a collection with 4 elements
 	Given parameter "offset" is "0"
		And parameter "limit" is "2"
	When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations"
	Then I should receive a collection with 2 elements
		And the link at index 0 should point to an entity with id "455a39aa-f950-4dca-a3df-9a87f990054b"
		And the link at index 1 should point to an entity with id "f05029c8-c4e8-427e-b2a4-cc89c74650a1"
		And the header "TotalCount" equals 8
		And the a next link exists with offset equal to 2 and limit equal to 2
		And the a previous link should not exist

Scenario: Paging request the first two results from an API request via a hop
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    Given format "application/json"
		And parameter "sortBy" is "name.firstName"
		And parameter "sortOrder" is "ascending"
	When I navigate to GET "/v1/educationOrganizations/<'Illinois State Ed-org' ID>/staffEducationOrgAssignmentAssociations/staff"
	Then I should receive a collection with 3 elements
 	Given parameter "offset" is "0"
		And parameter "limit" is "1"
	When I navigate to GET "/v1/educationOrganizations/<'Illinois State Ed-org' ID>/staffEducationOrgAssignmentAssociations/staff"
	Then I should receive a collection with 1 elements
		And the link at index 0 should point to an entity with id "b4c2a73f-336d-4c47-9b47-2d24871eef96"
		And the header "TotalCount" equals 3
		And the a next link exists with offset equal to 1 and limit equal to 1
		And the a previous link should not exist

Scenario: Request the last and middle page of results from a API request
  Given I am logged in using "jpratt" "jpratt1234" to realm "NY"
    Given format "application/json"
		And parameter "sortBy" is "entryGradeLevel"
		And parameter "sortOrder" is "ascending"
		And parameter "offset" is "2"
		And parameter "limit" is "2"
	When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations"
	Then I should receive a collection with 2 elements
		And the link at index 0 should point to an entity with id "8f423e3d-a206-4bbe-a3ab-aa37e95ba3e5"
		And the link at index 1 should point to an entity with id "69b49ecc-86f6-44b4-be99-43e9041dee2e"
		And the header "TotalCount" equals 8
		And the a previous link exists with offset equal to 0 and limit equal to 2
	Given parameter "offset" is "1"
		And parameter "limit" is "2"
	When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations"
	Then I should receive a collection with 2 elements
			And the link at index 0 should point to an entity with id "f05029c8-c4e8-427e-b2a4-cc89c74650a1"
			And the link at index 1 should point to an entity with id "8f423e3d-a206-4bbe-a3ab-aa37e95ba3e5"
			And the header "TotalCount" equals 8
			And the a previous link exists with offset equal to 0 and limit equal to 2
			And the a next link exists with offset equal to 3 and limit equal to 2
            
Scenario Outline: Confirm ability to use different operators with numbers
  Given I am logged in using "jpratt" "jpratt1234" to realm "NY"
    Given format "application/json"
    And parameter "limit" is "0"
      And parameter "sequenceOfCourse" <operator> "5"
     When I navigate to GET "/v1/sections"
     Then I should receive a return code of 200
      And I should receive a collection with <entities returned> elements
    Examples:
        | operator  | entities returned |
        | "<="      | 12                |
        | ">"       | 4                  |
        | "<"       | 10                |
        | ">="      | 6                 |
        | "!="      | 14                |
        | "="       | 2                 |
