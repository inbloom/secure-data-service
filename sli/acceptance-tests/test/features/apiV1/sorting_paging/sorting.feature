@wip
Feature: Sort and page API results

Background: 
	Given I am logged in using "demo" "demo1234" to realm "SLI"

Scenario: Sorting a collection of student school association links by entryGradeLevel, ascending
	Given format "application/json"
		And parameter "sortBy" is "entryGradeLevel"
		And parameter "sortOrder" is "ascending"
	When I navigate to GET "/v1/schools/<'Krypton Middle School' ID>/studentSchoolAssociations"
	Then I should receive a collection
		And the link at index 0 should point to an entity with id "094b9681-4fbc-4d59-a05d-87f6f5d0b759"
		And the link at index 1 should point to an entity with id "7a238370-415e-4fc0-9c5e-565cb8394d13"
		And the link at index 2 should point to an entity with id "4e044247-4cc0-49fa-900d-80064614060c"


Scenario: Sorting a collection of student entities links obtained via a hop by firstName, descending
	Given format "application/json"
		And parameter "sortBy" is "name.firstName"
		And parameter "sortOrder" is "descending"
	When I navigate to GET "/v1/schools/<'Krypton Middle School' ID>/studentSchoolAssociations/students"
	Then I should receive a collection
		And the link at index 0 should point to an entity with id "e0e99028-6360-4247-ae48-d3bb3ecb606a"
		And the link at index 1 should point to an entity with id "1aaad90e-02d0-4346-a3c4-a42747b9b050"

Scenario: Sorting a collection of full student school association entities
	Given format "application/json"
		And parameter "sortBy" is "entryGradeLevel"
		And parameter "sortOrder" is "descending"
	When I navigate to GET "/v1/schools/<'Krypton Middle School' ID>/studentSchoolAssociations"
	Then I should receive a collection
		And the link at index 0 should have "entryGradeLevel" equal to "Third grade"
		And the link at index 1 should have "entryGradeLevel" equal to "Sixth grade"
		And the link at index 2 should have "entryGradeLevel" equal to "Second grade"

Scenario: Sorting a collection of full student entities obtained via a hop
	Given format "application/json"
		And parameter "sortBy" is "name.firstName"
		And parameter "sortOrder" is "ascending"
	When I navigate to GET "/v1/schools/<'Krypton Middle School' ID>/studentSchoolAssociations/students"
	Then I should receive a collection
		And the link at index 0 should have "name.firstName" equal to "Darkseid"
		And the link at index 1 should have "name.firstName" equal to "Sybill"

Scenario: Paging request the first two results from an API request
    Given format "application/json"
		And parameter "sortBy" is "entryGradeLevel"
		And parameter "sortOrder" is "ascending"
	When I navigate to GET "/v1/schools/<'Krypton Middle School' ID>/studentSchoolAssociations"
	Then I should receive a collection with 3 elements
 	Given parameter "offset" is "0"
		And parameter "limit" is "2"
	When I navigate to GET "/v1/schools/<'Krypton Middle School' ID>/studentSchoolAssociations"
	Then I should receive a collection with 2 elements
		And the link at index 0 should point to an entity with id "094b9681-4fbc-4d59-a05d-87f6f5d0b759"
		And the link at index 1 should point to an entity with id "7a238370-415e-4fc0-9c5e-565cb8394d13"
		And the header "TotalCount" equals 3
		And the a next link exists with offset equal to 2 and limit equal to 2
		And the a previous link should not exist

Scenario: Paging request the first two results from an API request via a hop
    Given format "application/json"
		And parameter "sortBy" is "name.firstName"
		And parameter "sortOrder" is "ascending"
	When I navigate to GET "/v1/schools/<'Krypton Middle School' ID>/studentSchoolAssociations/students"
	Then I should receive a collection with 2 elements
 	Given parameter "offset" is "0"
		And parameter "limit" is "1"
	When I navigate to GET "/v1/schools/<'Krypton Middle School' ID>/studentSchoolAssociations/students"
	Then I should receive a collection with 1 elements
		And the link at index 0 should point to an entity with id "1aaad90e-02d0-4346-a3c4-a42747b9b050"
		And the header "TotalCount" equals 2
		And the a next link exists with offset equal to 1 and limit equal to 1
		And the a previous link should not exist

Scenario: Request the last and middle page of results from a API request
    Given format "application/json"
		And parameter "sortBy" is "entryGradeLevel"
		And parameter "sortOrder" is "ascending"
		And parameter "offset" is "1"
		And parameter "limit" is "2"
	When I navigate to GET "/v1/schools/<'Krypton Middle School' ID>/studentSchoolAssociations"
	Then I should receive a collection with 2 elements
		And the link at index 0 should point to an entity with id "7a238370-415e-4fc0-9c5e-565cb8394d13"
		And the link at index 1 should point to an entity with id "4e044247-4cc0-49fa-900d-80064614060c"
		And the header "TotalCount" equals 3
		And the a previous link exists with offset equal to 0 and limit equal to 2
		And the a next link should not exist
	Given parameter "offset" is "1"
		And parameter "limit" is "1"
	When I navigate to GET "/v1/schools/<'Krypton Middle School' ID>/studentSchoolAssociations"
	Then I should receive a collection with 1 elements
			And the link at index 0 should point to an entity with id "7a238370-415e-4fc0-9c5e-565cb8394d13"
			And the header "TotalCount" equals 3
			And the a previous link exists with offset equal to 0 and limit equal to 1
			And the a next link exists with offset equal to 2 and limit equal to 1

