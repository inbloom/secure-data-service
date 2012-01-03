##This was legacy gherkin
@wip
Feature: <US313> In order to manage teachers and schools 
    As a client application using SLI
    I want to know what teachers are currently employed at a particular school, 
		and I want to know what schools a teacher currently works at.  
		I also want to be able to manage these relationships by having teachers work and stop working at any school.
		This means we should be able to CREATE, READ, UPDATE, and DELETE teacher employment associations.


Background: Logged in as a super-user and using the small data set
	Given the SLI_SMALL dataset is loaded
	Given I am logged in using "jimi" "jimi"
	Given I have access to all teachers and schools


#### Reading 
Scenario: List all teachers currently employed in a school
    Given format "application/json"
    When I navigate to GET "/schools/152901001/teachers" 
    Then I should receive a return code of 200
        And I should see 5 teachers
	   And I should find the teacher "Belle Fernandez"
	   And I should not find the teacher "Bert Munoz"

Scenario: Find one school for a teacher
    When I navigate to GET "/teachers/725286880/schools"
    Then I should receive a return code of 200
		And I should see 1 schools
		And I should find the school "Orange Middle School"

#note, teacher 1234567890 is not in the test database currently is employed at 2 schools
Scenario: Find multiple schools for a teacher
    When I navigate to GET "/teachers/1234567890/schools"
    Then I should receive a return code of 200
		And I should see 2 schools
		And I should find the school "Orange Middle School"
		And I should find the school "Apple Alternative Elementary School"


#### Update
Scenario: A teacher switches schools
    Given "Bert Munoz" works at "Orange Middle School"
        And format "application/json"
    When I navigate to PUT "/schools/152901001/teachers/725286880" 
    Then I should receive a return code of 204
        And I should see the teacher "Bert Munoz" works at "Apple Alternative Elementary School"
        And I should see the teacher "Bert Munoz" does not work at "Orange Middle School"


#### Create
Scenario: A teacher starts to work at another school
    Given "Bert Munoz" works at "Orange Middle School"
    When I navigate to POST "/schools/152901002/teachers/725286880" 
        And format "application/json"
    Then I should receive a return code of 204
        And I should see the teacher "Bert Munoz" works at "Apple Alternative Elementary School"
        And I should see the teacher "Bert Munoz" works at "Orange Middle School"


### Delete
Scenario: A teacher stops working at a school
    Given format "application/json"
    When I navigate to DELETE "/schools/152901002/teachers/1234567890" 
    Then I should receive a return code of 200
        And I should see the teacher "John Adams" does not work at "Apple Alternative Elementary School"
        And I should see the teacher "John Adams" works at "Orange Middle School"


#### Errors 
#Scenario: Delete a school-teacher relationship that leaves a teacher not working at any schools
#    Given 
#    When 
#    Then 

#Scenario: ....
