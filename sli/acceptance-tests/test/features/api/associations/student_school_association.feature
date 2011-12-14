@wip
Feature: <US63> In order to manage students and schools 
    As a client application using SLI
    I want to know what students are currently enrolled at a particular school, 
		and I want to know what schools a student currently attends.  I also want
		to be able to manage these relationships by enrolling and un-enrolling students.
		This means we should be able to CREATE, READ, UPDATE, and DELETE student enrollment associations.


Background: Logged in as a super-user and using the small data set
	Given I am logged in using "demo" "demo1234"
	Given the SLI_SMALL dataset is loaded
	Given I have access to all students and schools


Scenario: School resource provides a link to it's student associations
	Given format "application/json"
	When I navigate to GET "/schools/eb3b8c35-f582-df23-e406-6947249a19f2"
	Then I should receive a return code of 200
		And I should receive a link where rel is "getSchoolEnrollments" and href ends with "/school-enrollments/eb3b8c35-f582-df23-e406-6947249a19f2"
	
Scenario: See all student associations for a school
	Given format "application/json"
	When I navigate to GET "/school-enrollments/eb3b8c35-f582-df23-e406-6947249a19f2"
	Then I should receive a return code of 200
		And I should receive a collection with "3" elements
		And the collection should contain a link where rel is "self" and href ends with "/school-enrollments/122a340e-e237-4766-98e3-4d2d67786572"
		And the collection should contain a link where rel is "self" and href ends with "/school-enrollments/53ec02fe-570b-4f05-a351-f8206b6d552a"
		And the collection should contain a link where rel is "self" and href ends with "/school-enrollments/4ef1498f-5dfc-4604-83c3-95e81146b59a"

Scenario: Student resource provides a link to it's school associations
	Given format "application/json"
	When I navigate to GET "/students/714c1304-8a04-4e23-b043-4ad80eb60992"
	Then I should receive a return code of 200
		And I should receive a link where rel is "getStudentEnrollments" and href ends with "/student-enrollments/714c1304-8a04-4e23-b043-4ad80eb60992"

Scenario: See all school associations for a student
	Given format "application/json"
	When I navigate to GET "/student-enrollments/714c1304-8a04-4e23-b043-4ad80eb60992"
	Then I should receive a return code of 200
		And I should receive a collection with "2" elements
		And the collection should contain a link where rel is "self" and href ends with "/student-enrollments/122a340e-e237-4766-98e3-4d2d67786572"
		And the collection should contain a link where rel is "self" and href ends with "/student-enrollments/6de7d3b6-54d7-48f0-92ad-0914fe229016"

Scenario: Read a student association by id
	Given format "application/json"
	When I navigate to GET "/school-enrollments/122a340e-e237-4766-98e3-4d2d67786572"
	Then I should receive a return code of 200
		And "id" should equal "122a340e-e237-4766-98e3-4d2d67786572"
		And "studentId" should equal "714c1304-8a04-4e23-b043-4ad80eb60992"
		And "schoolId" should equal "eb3b8c35-f582-df23-e406-6947249a19f2"
		And "entryGradeLevel" should equal "First grade"
		And I should receive a link where rel is "self" and href ends with "/school-enrollments/122a340e-e237-4766-98e3-4d2d67786572"
		And I should receive a link where rel is "getSchool" and href ends with "/schools/eb3b8c35-f582-df23-e406-6947249a19f2"
		And I should receive a link where rel is "getStudent" and href ends with "/students/714c1304-8a04-4e23-b043-4ad80eb60992"
	When I navigate to GET "/student-enrollments/122a340e-e237-4766-98e3-4d2d67786572"
	Then I should receive a return code of 200
		And "id" should equal "122a340e-e237-4766-98e3-4d2d67786572"
		And "studentId" should equal "714c1304-8a04-4e23-b043-4ad80eb60992"
		And "schoolId" should equal "eb3b8c35-f582-df23-e406-6947249a19f2"
		And "entryGradeLevel" should equal "First grade"
		And I should receive a link where rel is "self" and href ends with "/student-enrollments/122a340e-e237-4766-98e3-4d2d67786572"
		And I should receive a link where rel is "getSchool" and href ends with "/schools/eb3b8c35-f582-df23-e406-6947249a19f2"
		And I should receive a link where rel is "getStudent" and href ends with "/students/714c1304-8a04-4e23-b043-4ad80eb60992"

Scenario: Update an existing student-school-association
	Given format "application/json"
		And "entryGradeLevel" is "Second grade"
	When I navigate to PUT "/school-enrollments/122a340e-e237-4766-98e3-4d2d67786572"
	Then I should receive a return code of 204
	When I navigate to GET "/school-enrollments/122a340e-e237-4766-98e3-4d2d67786572"
	Then "entryGradeLevel" should equal "Second grade"
	
Scenario: Create a new student-school-association
	Given format "application/json"
		And "entryGradeLevel" is "Tenth grade"
		And "schoolId" is "eb3b8c35-f582-df23-e406-6947249a19f2"
		And "studentId" is "714c1304-8a04-4e23-b043-4ad80eb60992"
		And "entryDate" is "2010-01-01T00:00:00.00Z"
	When I navigate to POST "/school-enrollments"
	Then I should receive a return code of 201
		And I should receive a ID for the newly created student-school-association
	When I navigate to POST "/student-enrollments"
	Then I should receive a return code of 201
		And I should receive a ID for the newly created student-school-association

Scenario: Delete a student-school-association
	Given format "application/json"
	When I navigate to DELETE "/school-enrollments/122a340e-e237-4766-98e3-4d2d67786572"
	Then I should receive a return code of 204
	When I navigate to GET "/school-enrollments/122a340e-e237-4766-98e3-4d2d67786572"
	Then I should receive a return code of 404
	When I navigate to GET "/student-enrollments/122a340e-e237-4766-98e3-4d2d67786572"
	Then I should receive a return code of 404


### Error handling
Scenario: Attempt to read the base resource with no GUID
	Given format "application/json"
	When I navigate to GET "/school-enrollments"
	Then I should receive a return code of 405
	When I navigate to GET "/student-enrollments"
	Then I should receive a return code of 405


Scenario: Attempt to read a non-existent resource
	Given format "application/json"
	When I navigate to GET "/school-enrollments/11111111-1111-1111-1111-111111111111"
	Then I should receive a return code of 404
	When I navigate to GET "/student-enrollments/11111111-1111-1111-1111-111111111111"
	Then I should receive a return code of 404
	
Scenario: Attempt to delete a non-existent resource
	Given format "application/json"
	When I navigate to DELETE "/school-enrollments/11111111-1111-1111-1111-111111111111"
	Then I should receive a return code of 404
	When I navigate to DELETE "/student-enrollments/11111111-1111-1111-1111-111111111111"
	Then I should receive a return code of 404
		
Scenario: Update a non-existing student-school-association
    Given format "application/json"
    When I attempt to update a non-existing association "/student-enrollments/11111111-1111-1111-1111-111111111111"
    Then I should receive a return code of 404
	When I attempt to update a non-existing association "/school-enrollments/11111111-1111-1111-1111-111111111111"
    Then I should receive a return code of 404
