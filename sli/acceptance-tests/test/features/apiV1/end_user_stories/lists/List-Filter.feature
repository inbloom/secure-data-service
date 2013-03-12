@RALLY_US209
@RALLY_US210
Feature: As a teacher or leader I want to see all my students in 3rd period Algebra II class and view StateTest Math 2011 assessment scores

This is the data I am assuming for these tests
SectionName:  Algebra II
ClassPeriod = 3
Teacher = Linda Kim
Assume that Teacher, Student, Section, Assessment entity and associations are available

Background: Using the small data set

Scenario Outline: As a teacher or leader I want to see all my students in 3rd period Algebra II class and view StateTest Math 2011 assessment scores
    Given I am logged in using <Username> <Password> to realm "IL"
    Given format "application/json"

    When I navigate to GET "/v1/teachers/<'Linda Kim' ID>"
    Then I should receive a return code of <RC>
    Then I should receive a link named "getTeacherSectionAssociations" with URI "/v1.1/teachers/<'Linda Kim' ID>/teacherSectionAssociations"
        And I should receive a link named "getSections" with URI "/v1.1/teachers/<'Linda Kim' ID>/teacherSectionAssociations/sections"
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
    Then I should receive a link named "getStudentAssessments" with URI "/v1.1/students/<'Matt Sollars' ID>/studentAssessments"
        And I should receive a link named "getAssessments" with URI "/v1.1/students/<'Matt Sollars' ID>/studentAssessments/assessments"

    When I navigate to "getAssessments" with URI "/v1/students/<'Matt Sollars' ID>/studentAssessments/assessments" and filter by assessmentTitle is "SAT 2"
    Then I should have a list of 1 "assessment" entities
        And I should have an entity with ID "<'SAT READING' ID>"

    When I navigate to "getStudentAssessments" with URI "/v1/students/<'Matt Sollars' ID>/studentAssessments" and filter by administrationDate is between "2011-01-01" and "2011-06-31"
    Then I should have a list of 1 "studentAssessment" entities
        And I should have an entity with ID "<'Matt Sollars Assessment' ID>"

	Examples:
	| Username     | Password        | RC  | Role       | EdOrg                      |
	| "linda.kim"  | "linda.kim1234" | 200 | "Educator" | "Daybreak Middle School"   |
	| "sbantu"     | "sbantu1234"    | 200 | "Leader"   | "DayBreak School District" |


Scenario Outline: As teacher or leader in the Daybreak district but outside Daybreak Middle School, I should not have access to Linda Kim's students
    Given I am logged in using <Username> <Password> to realm "IL"
     And format "application/json"
     And the sli securityEvent collection is empty
    When I navigate to GET "/v1/teachers/<'Linda Kim' ID>"
    Then I should receive a return code of <RC>
     And a security event matching "^Access Denied" should be in the sli db
     And I check to find if record is in sli db collection:
       | collectionName      | expectedRecordCount | searchParameter       | searchValue                           |
       | securityEvent       | 1                   | body.userEdOrg        | <userEdOrg>                           |
       | securityEvent       | 1                   | body.targetEdOrgList  | <targetEdOrg>                         |

	Examples:
	| Username     | Password         | RC  | Role       | EdOrg                       | userEdOrg                             | targetEdOrg                           |
	| "cgray"      | "cgray1234"      | 403 | "Educator" | "Daybreak Central High"     | IL-SUNSET                             | IL-SUNSET                             |
	| "rbraverman" | "rbraverman1234" | 403 | "Educator" | "South Daybreak Elementary" | IL-DAYBREAK                           | IL-DAYBREAK                           |
	| "mgonzales"  | "mgonzales234"   | 403 | "Leader"   | "South Daybreak Elementary" | IL-DAYBREAK                           | IL-DAYBREAK                           |


Scenario Outline: As a teacher or leader in another district I cannot see any of Linda Kim's students in her 3rd period Algebra II class
    Given I am logged in using <Username> <Password> to realm "IL"
     And format "application/json"
     And the sli securityEvent collection is empty
    When I navigate to GET "/v1/teachers/<'Linda Kim' ID>"
    Then I should receive a return code of <RC>
     And a security event matching "^Access Denied" should be in the sli db
     #And I check to find if record is in sli db collection:
       #| collectionName      | expectedRecordCount | searchParameter       | searchValue                           |
       #| securityEvent       | 1                   | body.userEdOrg        | <userEdOrg>                           |
       #| securityEvent       | 1                   | body.targetEdOrgList  | <targetEdOrg>                         |

	Examples:
	| Username   | Password       | RC  | Role       | EdOrg                    |userEdOrg                             | targetEdOrg                           |
	| "llogan"   | "llogan1234"   | 403 | "Leader"   | "Sunset School District" |IL-DAYBREAK                           | IL-DAYBREAK                           |
	| "manthony" | "manthony1234" | 403 | "Educator" | "Sunset Central High"    |IL-DAYBREAK                           | IL-DAYBREAK                           |
