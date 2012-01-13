@wip
Feature: Test resolution of targets link for different entities

Background: None


Scenario: Check targets resolution after reading an assessment by ID
  Given format "application/json"
      When I navigate to GET "/assessments/<'Writing Advanced Placement Test' ID>"
      Then I should receive a return code of 200
          And I should receive a link named "getStudents" with URI "/student-assessment-associations/<'Writing Advanced Placement Test' ID>/targets"
      When I navigate to GET "/student-assessment-associations/<'Writing Advanced Placement Test' ID>/targets"
      Then I should receive a return code of 200
        And I should receive a collection of 5 student links
        And after resolution, I should receive a "Student" with ID "<'Alfonso' ID>"
        And after resolution, I should receive a "Student" with ID "<'Gil' ID>"
        And after resolution, I should receive a "Student" with ID "<'Sybill' ID>"
        And after resolution, I should receive a "Student" with ID "<'Mary' ID>"
        And after resolution, I should receive a "Student" with ID "<'John' ID>"

Scenario: Check targets resolution after reading a teacher by ID
   Given format "application/json"
   When I navigate to GET "/teachers/<'Macey' ID>"
   Then I should receive a return code of 200
      And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Macey' ID>/targets"
      And I should receive a link named "getSchools" with URI "/teacher-school-associations/<'Macey' ID>/targets"
   When I navigate to GET "/teacher-section-associations/<'Macey' ID>/targets"
      Then I should receive a return code of 200
        And I should receive a collection of 3 Section links
        And after resolution, I should receive a "Section" with ID "<'Algebra I' ID>"
        And after resolution, I should receive a "Section" with ID "<'Algebra II' ID>"
        And after resolution, I should receive a "Section" with ID "<'Advanced Algebra' ID>"
   When I navigate to GET "/teacher-school-associations/<'Macey' ID>/targets"
      Then I should receive a return code of 200
        And I should receive a collection of 2 School links
        And after resolution, I should receive a "School" with ID "<'Apple Alternative Elementary School' ID>"

Scenario: Check targets resolution after reading a student by ID
   Given format "application/json"
   When I navigate to GET "/students/<'Alfonzo' ID>"
   Then I should receive a return code of 200
      And I should receive a link named "getAssessments" with URI "/student-assessment-associations/<'Alfonzo' ID>/targets"
      When I navigate to GET "/student-assessment-associations/<'Alfonzo' ID>/targets"
      Then I should receive a return code of 200
        And I should receive a collection of 2 Assessment links
        And after resolution, I should receive a "Assessment" with ID "<'Writing Advanced Placement Test' ID>"
        And after resolution, I should receive a "Assessment" with ID "<'ISAT Mathematics' ID>"

Scenario: Check targets resolution after reading a school by ID
   Given format "application/json"
   When I navigate to GET "/schools/<'Apple Alternative Elementary School' ID>"
   Then I should receive a return code of 200
      And I should receive a link named "getTeachers" with URI "/teacher-school-associations/<'Apple Alternative Elementary School' ID>/targets"
      When I navigate to GET "/teacher-school-associations/<'Apple Alternative Elementary School' ID>/targets"
      Then I should receive a return code of 200
        And I should receive a collection of 3 Teacher links
        And after resolution, I should receive a "Teacher" with ID "<'Ms. Jones' ID>"
        And after resolution, I should receive a "Teacher" with ID "<'Mr. Smith' ID>"
        And after resolution, I should receive a "Teacher" with ID "<'Mr. Green' ID>"

