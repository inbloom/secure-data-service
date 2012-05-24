@RALLY_US209
@RALLY_US210
Feature: As a teacher I want to see all my students in 3rd period Algebra II class and view StateTest Math 2011 assessment scores

This is the data I am assuming for these tests
SectionName:  Algebra II
ClassPeriod = 3
Teacher = Ms. Jones
Assume that Teacher, Student, Section, Assessment entity and associations are available

Background: Logged in as a teacher and using the small data set
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    Given format "application/json"
#    Given I have access to all students, assessments, and sections

Scenario: As a teacher I want to see all my students in 3rd period Algebra II class and view StateTest Math 2011 assessment scores
    When I navigate to GET "/v1/teachers/<'Linda Kim' ID>"
    Then I should receive a link named "getTeacherSectionAssociations" with URI "/v1/teachers/<'Linda Kim' ID>/teacherSectionAssociations"
        And I should receive a link named "getSections" with URI "/v1/teachers/<'Linda Kim' ID>/teacherSectionAssociations/sections"
        And I should receive a link named "self" with URI "/teachers/<'Linda Kim' ID>"
    
    When I navigate to "getSections" with URI "/v1/teachers/<'Linda Kim' ID>/teacherSectionAssociations/sections"
    Then I should have a list of 4 "section" entities
        And I should have an entity with ID "<'8th Grade English - Sec 5' ID>"
        And I should have an entity with ID "<'8th Grade English - Sec 6' ID>"
        And I should have an entity with ID "<'Science 7A - Sec 5f10' ID>"

    When I navigate to "getSections" with URI "/v1/teachers/<'Linda Kim' ID>/teacherSectionAssociations/sections" and filter by uniqueSectionCode is "8th Grade English - Sec 6"
    Then I should have a list of 1 "section" entities
        And I should have an entity with ID "<'8th Grade English - Sec 6' ID>"

    When I navigate to "getStudents" with URI "/v1/sections/<'8th Grade English - Sec 6' ID>/studentSectionAssociations/students"
    Then I should have a list of 28 "student" entities
        And I should have an entity with ID "<'Preston Muchow' ID>"
        And I should have an entity with ID "<'Mayme Borc' ID>"
        And I should have an entity with ID "<'Malcolm Costillo' ID>"
        And I should have an entity with ID "<'Tomasa Cleaveland' ID>"
        And I should have an entity with ID "<'Merry Mccanse' ID>"
        And I should have an entity with ID "<'Samantha Scorzelli' ID>"
        And I should have an entity with ID "<'Matt Sollars' ID>"
        And I should have an entity with ID "<'Dominic Brisendine' ID>"
        And I should have an entity with ID "<'Lashawn Taite' ID>"
        And I should have an entity with ID "<'Oralia Merryweather' ID>"
        And I should have an entity with ID "<'Dominic Bavinon' ID>"
        And I should have an entity with ID "<'Rudy Bedoya' ID>"
        And I should have an entity with ID "<'Verda Herriman' ID>"
        And I should have an entity with ID "<'Alton Maultsby' ID>"
        And I should have an entity with ID "<'Felipe Cianciolo' ID>"
        And I should have an entity with ID "<'Lyn Consla' ID>"
        And I should have an entity with ID "<'Felipe Wierzbicki' ID>"
        And I should have an entity with ID "<'Gerardo Giaquinto' ID>"
        And I should have an entity with ID "<'Holloran Franz' ID>"
        And I should have an entity with ID "<'Oralia Simmer' ID>"
        And I should have an entity with ID "<'Lettie Hose' ID>"
        And I should have an entity with ID "<'Gerardo Saltazor' ID>"
        And I should have an entity with ID "<'Lashawn Aldama' ID>"
        And I should have an entity with ID "<'Alton Ausiello' ID>"
        And I should have an entity with ID "<'Marco Daughenbaugh' ID>"
        And I should have an entity with ID "<'Karrie Rudesill' ID>"
        And I should have an entity with ID "<'Damon Iskra' ID>"
        And I should have an entity with ID "<'Gerardo Rounsaville' ID>"
    
    When I navigate to GET "/v1/students/<'Matt Sollars' ID>"
    Then I should receive a link named "getStudentAssessments" with URI "/v1/students/<'Matt Sollars' ID>/studentAssessments"
        And I should receive a link named "getAssessments" with URI "/v1/students/<'Matt Sollars' ID>/studentAssessments/assessments"
    
    When I navigate to "getAssessments" with URI "/v1/students/<'Matt Sollars' ID>/studentAssessments/assessments" and filter by assessmentTitle is "SAT"
    Then I should have a list of 1 "assessment" entities
        And I should have an entity with ID "<'SAT READING' ID>"

    When I navigate to "getStudentAssessments" with URI "/v1/studentAssessments" to filter
        And filter by administrationDate is between "2011-01-01" and "2011-12-31"
        And filter by studentId is <'Matt Sollars' ID>
    Then I should find a ScoreResult is 2060
        #And I should find a performanceLevelDescriptors is "At or Above Benchmark"