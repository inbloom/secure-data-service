Feature: <US63> In order to manage students and schools 
    As a client application using SLI
    I want to know what students are currently enrolled at a particular school, 
		and I want to know what schools a student currently attends.  I also want
		to be able to manage these relationships by enrolling and un-enrolling students.
		This means we should be able to CREATE, READ, UPDATE, and DELETE student enrollment associations.


#Background probably eliminates for most of the Given clauses in the scenarios - revisit
Background: Logged in as a super-user and using the small data set
	Given I am logged in using "jimi" "jimi"
	Given the SLI_SMALL dataset is loaded
	Given I have access to all students and schools


#### Reading 
Scenario: List all students currently enrolled in a school
	Given the school "Orange Middle School"
    When I navigate to GET "/schools/#/students" 
        And format "application/json"
    Then I should receive a return code of 200
        And I should see 52 students
		And I should find the student "Bell Allegra Guerrero"
		And I should not find the student "Priscilla Halla Mcintyre"

Scenario: Find one school for a student
	Given the student "Bell Allegra Guerrero"
    When I navigate to GET "/students/#/schools"
         And format "application/json"
    Then I should receive a return code of 200
		And I should see 1 school
		And I should find the school "Orange Middle School"

Scenario: Find multiple schools for a student
	Given the student "Alfonso Ora Steele"
    When I navigate to GET "/students/#/schools"
         And format "application/json"
    Then I should receive a return code of 200
		And I should see 2 schools
		And I should find the school "Orange Middle School"
		And I should find the school "Apple Alternative Elementary School"


#### Update
Scenario: A student switches schools
    Given "Bell Allegra Guerrero" attends "Orange Middle School"
    When I navigate to PUT "/schools/#/students/#" 
        And format "application/json"
        And change the school to "Apple Alternative Elementary School"
    Then I should receive a return code of 204
        And I should see the student "Bell Allegra Guerrero" attends "Apple Alternative Elementary School"
        And I should see the student "Bell Allegra Guerrero" does not attend "Orange Middle School"


#### Create
Scenario: A student starts to attend another school
    Given "Yolanda Campos" attends "Apple Alternative Elementary School"
    When I navigate to POST "/schools/#/students/#" 
        And format "application/json"
    Then I should receive a return code of 204
        And I should see the student "Yolanda Campos" attends "Apple Alternative Elementary School"
        And I should see the student "Yolanda Campos" attends "Orange Middle School"


### Delete
Scenario: A student stops attending a school
	Given "Yolanda Campos" attends "Orange Middle School"
    When I navigate to DELETE "/schools/#/students/#" 
        And format "application/json"
    Then I should receive a return code of 204
        And I should see the student "Yolanda Campos" attends "Apple Alternative Elementary School"
        And I should see the student "Yolanda Campos" does not attend "Orange Middle School"


#### Errors 
#Scenario: Delete a school-student relationship that leaves a student un-enrolled
#    Given 
#    When 
#    Then 

#Scenario: ....
