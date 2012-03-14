@wip
Feature: As a teacher I want to see all my students in 3rd period Algebra II class and view ISAT Math 2011 assessment scores

This is the data I am assuming for these tests
SectionName:  Algebra II
ClassPeriod = 3
Teacher = Ms. Jones
Assume that Teacher, Student, Section, Assessment entity and associations are available

Background: Logged in as a teacher and using the small data set
    Given I am logged in using "demo" "demo1234"
    Given format "application/json"
    Given I have access to all students, assessments, and sections

Scenario: As a teacher I want to see all my students in 3rd period Algebra II class and view ISAT Math 2011 assessment scores
    When I navigate to GET "/v1/teachers/<'Ms. Jones' ID>"
    Then I should receive a link named "getTeacherSectionAssociations" with URI "/v1/teachers/<'Ms. Jones' ID>/teacherSectionAssociations"
        And I should receive a link named "getSections" with URI "/v1/teachers/<'Ms. Jones' ID>/teacherSectionAssociations/sections"
        And I should receive a link named "self" with URI "/teachers/<'Ms. Jones' ID>"
    
    When I navigate to "getSections" with URI "/v1/teachers/<'Ms. Jones' ID>/teacherSectionAssociations/sections"
    Then I should have a list of 3 "section" entities
        And I should have an entity with ID "<'French period 5' ID>"
        And I should have an entity with ID "<'Algebra II period 3' ID>"
        And I should have an entity with ID "<'Algebra II period 1' ID>"

    When I navigate to "getSections" with URI "/v1/teachers/<'Ms. Jones' ID>/teacherSectionAssociations/sections" and filter by sectionName is "Algebra II" and classPeriod is "3"
    Then I should have a list of 1 "section" entities
        And I should have an entity with ID "<'Algebra II period 3' ID>"

    When I navigate to "getStudents" with URI "/v1/sections/<'Algebra II period 3' ID>/studentSectionAssociations/students"
    Then I should have a list of 5 "student" entities
        And I should have an entity with ID "<'John Doe' ID>"
        And I should have an entity with ID "<'Sean Deer' ID>"
        And I should have an entity with ID "<'Suzy Queue' ID>"
        And I should have an entity with ID "<'Mary Line' ID>"
        And I should have an entity with ID "<'Dong Steve' ID>"
    
    When I navigate to GET "/v1/students/<'Suzy Queue' ID>"
    Then I should receive a link named "getStudentAssessmentAssociations" with URI "/v1/students/<'Suzy Queue' ID>/studentAssessmentAssociations"
        And I should receive a link named "getAssessments" with URI "/v1/students/<'Suzy Queue' ID>/studentAssessmentAssociations/assessments"
    
    When I navigate to "getAssessments" with URI "/v1/students/<'Suzy Queue' ID>/studentAssessmentAssociations/assessments" and filter by assessmentTitle is "ISAT MATH"
    Then I should have a list of 1 "assessment" entities
        And I should have an entity with ID "<'ISAT MATH' ID>"

    When I navigate to "getStudentAssessmentAssociations" with URI "/v1/studentAssessmentAssociations" to filter
        And filter by administrationDate is between "2011-01-01" and "2011-12-31"
        And filter by studentId is <'Suzy Queue' ID>
    Then I should find a ScoreResult is 89
        And I should find a performanceLevelDescriptors is "At or Above Benchmark"


