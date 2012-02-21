Feature: Sort API results.


Background: 
	Given I am logged in using "demo" "demo1234"
	Given I have access to all students and schools

# Reference collections
# Full entities
# Hops - reference
# Hops - Full
# Accending, Descending
# complex objects


Scenario: Sorting a collection of student school associations by entryGradeLevel, ascending
	Given format "application/json"
		And parameter "sort-by" is "entryGradeLevel"
		And parameter "sort-order" is "ascending"
	When I navigate to GET "/student-school-associations/<'Krypton Middle School' ID>"
	Then I should receive a collection of links
		And the link at index 0 should point to an entity with id "094b9681-4fbc-4d59-a05d-87f6f5d0b759"
		And the link at index 1 should point to an entity with id "7a238370-415e-4fc0-9c5e-565cb8394d13"
		And the link at index 2 should point to an entity with id "4e044247-4cc0-49fa-900d-80064614060c"


Scenario: Sorting a collection of student school associations by entryGradeLevel, descending
	Given format "application/json"
		And parameter "sort-by" is "entryGradeLevel"
		And parameter "sort-order" is "descending"
	When I navigate to GET "/student-school-associations/<'Krypton Middle School' ID>"
	Then I should receive a collection of links
		And the link at index 0 should point to an entity with id "4e044247-4cc0-49fa-900d-80064614060c"
		And the link at index 1 should point to an entity with id "7a238370-415e-4fc0-9c5e-565cb8394d13"
		And the link at index 2 should point to an entity with id "094b9681-4fbc-4d59-a05d-87f6f5d0b759"

Scenario: Sorting a collection of students obtained via a single hop
	Given format "application/json"
		And parameter "sort-by" is "name.firstName"
		And parameter "sort-order" is "ascending"
	When I navigate to GET "/student-school-associations/<'Krypton Middle School' ID>/targets"
	Then I should receive a collection of links
		And the link at index 0 should point to an entity with id "1aaad90e-02d0-4346-a3c4-a42747b9b050"
		And the link at index 1 should point to an entity with id "e0e99028-6360-4247-ae48-d3bb3ecb606a"