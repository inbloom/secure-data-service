Feature: Sort and page API results

Background: 
	Given I am logged in using "demo" "demo1234"
	Given I have access to all students and schools

Scenario: Sorting a collection of student school association links by entryGradeLevel, ascending
	Given format "application/json"
		And parameter "sort-by" is "entryGradeLevel"
		And parameter "sort-order" is "ascending"
	When I navigate to GET "/student-school-associations/<'Krypton Middle School' ID>"
	Then I should receive a collection
		And the link at index 0 should point to an entity with id "094b9681-4fbc-4d59-a05d-87f6f5d0b759"
		And the link at index 1 should point to an entity with id "7a238370-415e-4fc0-9c5e-565cb8394d13"
		And the link at index 2 should point to an entity with id "4e044247-4cc0-49fa-900d-80064614060c"


Scenario: Sorting a collection of student entities links obtained via a hop by name.firstName, descending
	Given format "application/json"
		And parameter "sort-by" is "name.firstName"
		And parameter "sort-order" is "descending"
	When I navigate to GET "/student-school-associations/<'Krypton Middle School' ID>/targets"
	Then I should receive a collection
		And the link at index 0 should point to an entity with id "e0e99028-6360-4247-ae48-d3bb3ecb606a"
		And the link at index 1 should point to an entity with id "1aaad90e-02d0-4346-a3c4-a42747b9b050"

Scenario: Sorting a collection of full student school association entities
	Given format "application/json"
		And parameter "sort-by" is "entryGradeLevel"
		And parameter "sort-order" is "descending"
		And parameter "full-entities" is "true"
	When I navigate to GET "/student-school-associations/<'Krypton Middle School' ID>"
	Then I should receive a collection
		And the link at index 0 should have "entryGradeLevel" equal to "Third grade"
		And the link at index 1 should have "entryGradeLevel" equal to "Sixth grade"
		And the link at index 2 should have "entryGradeLevel" equal to "Second grade"

Scenario: Sorting a collection of full student entities obtained via a hop
	Given format "application/json"
		And parameter "sort-by" is "name.firstName"
		And parameter "sort-order" is "ascending"
		And parameter "full-entities" is "true"
	When I navigate to GET "/student-school-associations/<'Krypton Middle School' ID>/targets"
	Then I should receive a collection
		And the link at index 0 should have "name.firstName" equal to "Darkseid"
		And the link at index 1 should have "name.firstName" equal to "Sybill"


Scenario: Paging: request the first two results from an API request
    Given format "application/json"
		And parameter "sort-by" is "entryGradeLevel"
		And parameter "sort-order" is "ascending"
	When I navigate to GET "/student-school-associations/<'Krypton Middle School' ID>"
	Then I should receive a collection with 3 elements
 	Given parameter "start-index" is "0"
		And parameter "max-results" is "2"
	When I navigate to GET "/student-school-associations/<'Krypton Middle School' ID>"
	Then I should receive a collection with 2 elements
		And the link at index 0 should point to an entity with id "094b9681-4fbc-4d59-a05d-87f6f5d0b759"
		And the link at index 1 should point to an entity with id "7a238370-415e-4fc0-9c5e-565cb8394d13"
		And the header "TotalCount" equals 3

Scenario: Paging: request a page of results from a API request
    Given format "application/json"
		And parameter "sort-by" is "entryGradeLevel"
		And parameter "sort-order" is "ascending"
		And parameter "start-index" is "1"
		And parameter "max-results" is "2"
	When I navigate to GET "/student-school-associations/<'Krypton Middle School' ID>"
	Then I should receive a collection with 2 elements
		And the link at index 0 should point to an entity with id "7a238370-415e-4fc0-9c5e-565cb8394d13"
		And the link at index 1 should point to an entity with id "4e044247-4cc0-49fa-900d-80064614060c"
		And the header "TotalCount" equals 3

