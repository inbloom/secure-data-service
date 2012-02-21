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
		And sort-by is "entryGradeLevel"
		And sort-order is "ascending"
	When I navigate to GET "/student-school-associations/<'Krypton Middle School' ID>"
	Then I should receive a collection of student association links
		And the link at index 0 should point to an entity with id "094b9681-4fbc-4d59-a05d-87f6f5d0b759"
		And the link at index 1 should point to an entity with id "7a238370-415e-4fc0-9c5e-565cb8394d13"
		And the link at index 2 should point to an entity with id "4e044247-4cc0-49fa-900d-80064614060c"


Scenario: Sorting a collection of student school associations by entryGradeLevel, descending
	Given format "application/json"
		And sort-by is "entryGradeLevel"
		And sort-order is "descending"
	When I navigate to GET "/student-school-associations/<'Krypton Middle School' ID>"
	Then I should receive a collection of student association links
		And the link at index 0 should point to an entity with id "4e044247-4cc0-49fa-900d-80064614060c"
		And the link at index 1 should point to an entity with id "7a238370-415e-4fc0-9c5e-565cb8394d13"
		And the link at index 2 should point to an entity with id "094b9681-4fbc-4d59-a05d-87f6f5d0b759"

# 
# Scenario: When using a application/vndslcfull+json media type, I get association objects for the school instead of association links
# 	Given format "application/vnd.slc.full+json"
# 	When I navigate to GET "/student-school-associations/<'Apple Alternative Elementary School' ID>"
# 	Then I should receive a collection of student-school-association objects
# 		And I should not receive a collection of student-school-association links
# 
# Scenario: When using a application/vndslcfull+json media type, I get school objects instead of school links
# 	Given format "application/vnd.slc.full+json"
# 	When I navigate to GET "/student-school-associations/<'Alfonso' ID>/targets"
# 	Then I should receive a collection of school objects
# 		And I should not receive a collection of school links
# 
# Scenario: When using a application/vndslcfull+json media type, I get association objects for the student instead of association links
# 	Given format "application/vnd.slc.full+json"
# 	When I navigate to GET "/student-school-associations/<'Alfonso' ID>"
# 	Then I should receive a collection of student-school-association objects
# 		And I should not receive a collection of student-school-association links
# 	

	
	