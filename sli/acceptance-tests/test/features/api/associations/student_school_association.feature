@wip
Feature: <US63> In order to manage students and schools 
    As a client application using SLI
    I want to know what students are currently enrolled at a particular school, 
		and I want to know what schools a student currently attends.  I also want
		to be able to manage these relationships by enrolling and un-enrolling students.
		This means we should be able to CREATE, READ, UPDATE, and DELETE student enrollment associations.


Background: Logged in as a super-user and using the small data set
	Given I am logged in using "jimi" "jimi"
	Given the SLI_SMALL dataset is loaded
	Given I have access to all students and schools


#### Reading 
Scenario: List all students currently enrolled in a school
    Given the school "Orange Middle School"
	   And format "application/json"
    # Fill in the hash below with a valid school id
    When I navigate to GET "/schools/#/students" 
    Then I should receive a return code of 200
        And I should see 52 students
	   And I should find the student "Bell Allegra Guerrero"
	   And I should not find the student "Priscilla Halla Mcintyre"
		
# Need to fix the hash below to a number
Scenario: Find one school for a student
   Given the student "Bell Allegra Guerrero"
       And format "application/json"
    When I navigate to GET "/students/#/schools"
    Then I should receive a return code of 200
	  And I should see 1 schools
	  And I should find the school "Orange Middle School"

Scenario: Find multiple schools for a student
    Given the student "Alfonso Ora Steele"
       And format "application/json"
    When I navigate to GET "/students/#/schools"
    Then I should receive a return code of 200
	  And I should see 2 schools
	  And I should find the school "Orange Middle School"
	  And I should find the school "Apple Alternative Elementary School"


#### Update
Scenario: A student switches schools
    Given "Bell Allegra Guerrero" attends "Orange Middle School"
        And format "application/json"
        And change the school to "Apple Alternative Elementary School"
    When I navigate to PUT "/schools/#/students/#" 
    Then I should receive a return code of 204
        And I should see the student "Bell Allegra Guerrero" attends "Apple Alternative Elementary School"
        And I should see the student "Bell Allegra Guerrero" does not attend "Orange Middle School"


#### Create
Scenario: A student starts to attend another school
    Given "Yolanda Campos" attends "Apple Alternative Elementary School"
        And format "application/json"
    When I navigate to POST "/schools/#/students/#" 
    Then I should receive a return code of 204
        And I should see the student "Yolanda Campos" attends "Apple Alternative Elementary School"
        And I should see the student "Yolanda Campos" attends "Orange Middle School"


### Delete
Scenario: A student stops attending a school
    Given "Yolanda Campos" attends "Orange Middle School"
       And format "application/json"
    When I navigate to DELETE "/schools/#/students/#" 
    Then I should receive a return code of 204
        And I should see the student "Yolanda Campos" attends "Apple Alternative Elementary School"
        And I should see the student "Yolanda Campos" does not attend "Orange Middle School"


#### Errors 
#Scenario: Delete a school-student relationship that leaves a student un-enrolled
#    Given 
#    When 
#    Then 

#Scenario: ....
