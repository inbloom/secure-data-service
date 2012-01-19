
Feature: Test resolution of targets link for different entities

Background: None
	 Given I am logged in using "demo" "demo1234"
	 And I have access to all entities

Scenario: Check targets resolution after reading an assessment by ID
	  Given format "application/json"
      When I navigate to GET "/assessments/<'Writing Advanced Placement Test' ID>"
      Then I should receive a return code of 200
          And I should receive a link named "getStudents" with URI "/student-assessment-associations/<'Writing Advanced Placement Test' ID>/targets"
      When I navigate to GET "/student-assessment-associations/<'Writing Advanced Placement Test' ID>/targets"
      Then I should receive a return code of 200
        And I should receive a collection of 5 student links
        And after resolution, I should receive a "Student" with ID "<'Alfonso' ID>"
        And after resolution, I should receive a "Student" with ID "<'Priscilla' ID>"
        And after resolution, I should receive a "Student" with ID "<'Alden' ID>"
        And after resolution, I should receive a "Student" with ID "<'Donna' ID>"
        And after resolution, I should receive a "Student" with ID "<'Rachel' ID>"

Scenario: Check targets resolution after reading a teacher by ID
   Given format "application/json"
   When I navigate to GET "/teachers/<'Quemby' ID>"
   Then I should receive a return code of 200
      And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Quemby' ID>/targets"
      And I should receive a link named "getSchools" with URI "/teacher-school-associations/<'Quemby' ID>/targets"
   When I navigate to GET "/teacher-section-associations/<'Quemby' ID>/targets"
      Then I should receive a return code of 200
        And I should receive a collection of 2 Section links
        And after resolution, I should receive a "Section" with ID "<'Chemistry F11' ID>"
        And after resolution, I should receive a "Section" with ID "<'Physics S08' ID>"
   When I navigate to GET "/teacher-school-associations/<'Quemby' ID>/targets"
      Then I should receive a return code of 200
        And I should receive a collection of 1 School links
        And after resolution, I should receive a "School" with ID "<'Apple Alternative Elementary School' ID>"

Scenario: Check targets resolution after reading a student by ID
   Given format "application/json"
   When I navigate to GET "/students/<'Alfonso' ID>"
   Then I should receive a return code of 200
      And I should receive a link named "getAssessments" with URI "/student-assessment-associations/<'Alfonso' ID>/targets"
      When I navigate to GET "/student-assessment-associations/<'Alfonso' ID>/targets"
      Then I should receive a return code of 200
        And I should receive a collection of 2 Assessment links
        And after resolution, I should receive a "Assessment" with ID "<'Writing Advanced Placement Test' ID>"
        And after resolution, I should receive a "Assessment" with ID "<'Writing Achievement Assessment Test' ID>"

Scenario: Check targets resolution after reading a school by ID
   Given format "application/json"
   When I navigate to GET "/schools/<'Biology High School' ID>"
   Then I should receive a return code of 200
      And I should receive a link named "getTeachers" with URI "/teacher-school-associations/<'Biology High School' ID>/targets"
      When I navigate to GET "/teacher-school-associations/<'Biology High School' ID>/targets"
      Then I should receive a return code of 200
        And I should receive a collection of 3 Teacher links
        And after resolution, I should receive a "Teacher" with ID "<'Ms. Jones' ID>"
        And after resolution, I should receive a "Teacher" with ID "<'Mr. Smith' ID>"
        And after resolution, I should receive a "Teacher" with ID "<'Mrs. Solis' ID>"

