@RALLY_US209
@RALLY_US210
Feature: As a teacher I want to get DIBELS Composite Score and Reading Level

Background: None

    Scenario Outline:  (sorting) As a teacher, for my class, I want to get the most recent DIBELS assessment
    Given I am a valid SEA/LEA end user <Username> with password <Password>
    And I have a Role attribute returned from the "SLI"
    And the role attribute equals <AnyDefaultSLIRole>
    And I am authenticated on "SLI"

     Given format "application/json"
     When I navigate to GET "/<TEACHER URI>/<'Ms. Jones' ID>"
      Then I should receive a link named "getTeacherSectionAssociations" with URI "/<TEACHER URI>/<'Ms. Jones' ID>/<TEACHER SECTION ASSOC URI>"
        And I should receive a link named "getSections" with URI "/<TEACHER URI>/<'Ms. Jones' ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
        And I should receive a link named "self" with URI "/<TEACHER URI>/<'Ms. Jones' ID>"

     When I navigate to GET "/<TEACHER URI>/<'Ms. Jones' ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
      Then I should have a list of 2 "section" entities
        And I should have an entity with ID "<'Important Section' ID>"

     When I navigate to URI "/<SECTION URI>/<'Important Section' ID>/<SECTION ASSESSMENT ASSOC URI>/<ASSESSMENT URI>" with filter sorting and pagination
        And filter by "assessmentFamilyHierarchyName" = "DIBELS Next"
        And filter by "sortBy" = "assessmentPeriodDescriptor.beginDate"
        And filter by "sortOrder" = "descending"
        And filter by "offset" = "0"
        And filter by "limit" = "1"
        And I submit the sorting and pagination request
      Then I should have a list of 1 "assessment" entities
        And I should have an entity with ID "<'Grade 2 MOY DIBELS' ID>"

     When I navigate to GET "/<ASSESSMENT URI>/<'Grade 2 MOY DIBELS' ID>"
      Then I should have a list of 1 "assessment" entities
        And "assessmentTitle" should be "DIBELS-MOY"
        And "assessmentCategory" should be "Benchmark test"
        And "academicSubject" should be "Reading"
        And "gradeLevelAssessed" should be "Second grade"
        And "lowestGradeLevelAssessed" should be "Second grade"
        And "assessmentFamilyHierarchyName" should be "DIBELS Next"
        And "maxRawScore" should be "380"
        And "minRawScore" should be "13"
        And the field "assessmentPeriodDescriptor.beginDate" should be "2012-01-01"
        And the field "assessmentPeriodDescriptor.endDate" should be "2012-02-01"
        And there are "3" "assessmentPerformanceLevel"
        And for the level at position "0"
        And the key "minimumScore" has value "190"
        And the key "maximumScore" has value "380"
        And the key "assessmentReportingMethod" has value "Composite Score"
        And the key "performanceLevelDescriptor.0.codeValue" has value "Level 1"
        And the key "performanceLevelDescriptor.1.description" has value "At or Above Benchmark"
        And for the level at position "1"
        And the key "minimumScore" has value "145"
        And the key "maximumScore" has value "189"
        And the key "assessmentReportingMethod" has value "Composite Score"
        And the key "performanceLevelDescriptor.0.codeValue" has value "Level 2"
        And the key "performanceLevelDescriptor.1.description" has value "Below Benchmark"
        And for the level at position "2"
        And the key "minimumScore" has value "13"
        And the key "maximumScore" has value "144"
        And the key "assessmentReportingMethod" has value "Composite Score"
        And the key "performanceLevelDescriptor.0.codeValue" has value "Level 3"
        And the key "performanceLevelDescriptor.1.description" has value "Well Below Benchmark"

     When I navigate to GET "/<SECTION URI>/<'Important Section' ID>/<STUDENT SECTION ASSOC URI>/<STUDENT URI>"
      Then I should have a list of 5 "student" entities
        And I should have an entity with ID "<'John Doe' ID>"
        And I should have an entity with ID "<'Sean Deer' ID>"
        And I should have an entity with ID "<'Suzy Queue' ID>"
        And I should have an entity with ID "<'Mary Line' ID>"
        And I should have an entity with ID "<'Dong Steve' ID>"

     When I navigate to URI "/<ASSESSMENT URI>/<'Grade 2 MOY DIBELS' ID>/<STUDENT ASSESSMENT ASSOC URI>" with filter sorting and pagination
        And filter by "sortBy" = "administrationDate"
        And filter by "sortOrder" = "descending"
        And filter by "offset" = "0"
        And filter by "limit" = "1"
        And I submit the sorting and pagination request
      Then I should have a list of 1 "studentAssessmentAssociation" entities
        And I should have an entity with ID "<'Most Recent Student Assessment Association' ID>"

     When I navigate to GET "/<STUDENT ASSESSMENT ASSOC URI>/<'Most Recent Student Assessment Association' ID>"
      Then I should have a list of 1 "studentAssessmentAssociation" entities
        And "administrationDate" should be "2012-01-10"
        And "administrationEndDate" should be "2012-01-15"
        And "gradeLevelWhenAssessed" should be "Second grade"
        And "retestIndicator" should be "1st Retest"
        And the field "performanceLevelDescriptors.0.1.description" should be "Below Benchmark"
        And the field "scoreResults.0.assessmentReportingMethod" should be "Scale score"
        And the field "scoreResults.0.result" should be "120"

Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "administrator" | "administrator1234" | "IT Administrator" |
| "leader"        | "leader1234"        | "Leader"           |

    Scenario Outline:  (paging/sorting) As a teacher, for my class, I want to get the most recent values of the following attributes: DIBELSCompositeScore, ReadingInstructionalLevel, PerformanceLevel
    Given I am a valid SEA/LEA end user <Username> with password <Password>
    And I have a Role attribute returned from the "SLI"
    And the role attribute equals <AnyDefaultSLIRole>
    And I am authenticated on "SLI"

    Given format "application/json"
     When I navigate to GET "/<TEACHER URI>/<'Ms. Jones' ID>"
      Then I should receive a link named "getTeacherSectionAssociations" with URI "/<TEACHER URI>/<'Ms. Jones' ID>/<TEACHER SECTION ASSOC URI>"
        And I should receive a link named "getSections" with URI "/<TEACHER URI>/<'Ms. Jones' ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
        And I should receive a link named "self" with URI "/<TEACHER URI>/<'Ms. Jones' ID>"

    When I navigate to GET "/<TEACHER URI>/<'Ms. Jones' ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
    Then I should have a list of 2 "section" entities
        And I should have an entity with ID "<'Important Section' ID>"

    When I navigate to GET "/<SECTION URI>/<'Important Section' ID>/<STUDENT SECTION ASSOC URI>/<STUDENT URI>"
      Then I should have a list of 5 "student" entities
        And I should have an entity with ID "<'John Doe' ID>"
        And I should have an entity with ID "<'Sean Deer' ID>"
        And I should have an entity with ID "<'Suzy Queue' ID>"
        And I should have an entity with ID "<'Mary Line' ID>"
        And I should have an entity with ID "<'Dong Steve' ID>"

     When I navigate to URI "/<STUDENT URI>/<'John Doe' ID>/<STUDENT ASSESSMENT ASSOC URI>/<ASSESSMENT URI>" with filter sorting and pagination
        And filter by "assessmentTitle" = "DIBELS-MOY"
        And I submit the sorting and pagination request
      Then I should have a list of 1 "assessment" entities
        And I should have an entity with ID "<'Grade 2 MOY DIBELS' ID>"

     When I navigate to GET "/<ASSESSMENT URI>/<'Grade 2 MOY DIBELS' ID>"
      Then I should have a list of 1 "assessment" entities
        And "assessmentTitle" should be "DIBELS-MOY"
        And "assessmentCategory" should be "Benchmark test"
        And "academicSubject" should be "Reading"
        And "gradeLevelAssessed" should be "Second grade"
        And "lowestGradeLevelAssessed" should be "Second grade"
        And "assessmentFamilyHierarchyName" should be "DIBELS Next"
        And "maxRawScore" should be "380"
        And "minRawScore" should be "13"
        And the field "assessmentPeriodDescriptor.beginDate" should be "2012-01-01"
        And the field "assessmentPeriodDescriptor.endDate" should be "2012-02-01"
        And there are "3" "assessmentPerformanceLevel"
        And for the level at position "0"
        And the key "minimumScore" has value "190"
        And the key "maximumScore" has value "380"
        And the key "assessmentReportingMethod" has value "Composite Score"
        And the key "performanceLevelDescriptor.0.codeValue" has value "Level 1"
        And the key "performanceLevelDescriptor.1.description" has value "At or Above Benchmark"
        And for the level at position "1"
        And the key "minimumScore" has value "145"
        And the key "maximumScore" has value "189"
        And the key "assessmentReportingMethod" has value "Composite Score"
        And the key "performanceLevelDescriptor.0.codeValue" has value "Level 2"
        And the key "performanceLevelDescriptor.1.description" has value "Below Benchmark"
        And for the level at position "2"
        And the key "minimumScore" has value "13"
        And the key "maximumScore" has value "144"
        And the key "assessmentReportingMethod" has value "Composite Score"
        And the key "performanceLevelDescriptor.0.codeValue" has value "Level 3"
        And the key "performanceLevelDescriptor.1.description" has value "Well Below Benchmark"

     When I navigate to URI "/<ASSESSMENT URI>/<'Grade 2 MOY DIBELS' ID>/<STUDENT ASSESSMENT ASSOC URI>" with filter sorting and pagination
        And filter by "sortBy" = "administrationDate"
        And filter by "sortOrder" = "descending"
        And filter by "offset" = "0"
        And filter by "limit" = "1"
        And I submit the sorting and pagination request
      Then I should have a list of 1 "studentAssessmentAssociation" entities
        And I should have an entity with ID "<'Most Recent Student Assessment Association' ID>"

     When I navigate to GET "/<STUDENT ASSESSMENT ASSOC URI>/<'Most Recent Student Assessment Association' ID>"
      Then I should have a list of 1 "studentAssessmentAssociation" entities
        And "administrationDate" should be "2012-01-10"
        And "administrationEndDate" should be "2012-01-15"
        And "gradeLevelWhenAssessed" should be "Second grade"
        And "retestIndicator" should be "1st Retest"
        And the field "performanceLevelDescriptors.0.1.description" should be "Below Benchmark"
        And the field "scoreResults.0.assessmentReportingMethod" should be "Scale score"
        And the field "scoreResults.0.result" should be "120"

Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "administrator" | "administrator1234" | "IT Administrator" |
| "leader"        | "leader1234"        | "Leader"           |

Scenario Outline:  As a AggregateViewer I should not see personally identifiable information data
    Given I am a valid SEA/LEA end user <Username> with password <Password>
    And I have a Role attribute returned from the "SLI"
    And the role attribute equals <AnyDefaultSLIRole>
    And I am authenticated on "SLI"

    When I navigate to GET "/<TEACHER URI>/<'Ms. Smith' ID>"
    Then I should receive a return code of 403

    When I navigate to GET "/<TEACHER SECTION ASSOC URI>/<'Teacher Ms. Jones and Section Algebra II' ID>/<TEACHER URI>"
    Then I should receive a return code of 403

    When I navigate to GET "/<STUDENT SECTION ASSOC URI>/<'Algebra II' ID>/<STUDENT URI>"
    Then I should receive a return code of 403

    When I navigate to GET "/<STUDENT URI>/<'Jane Doe' ID>"      
    Then I should receive a return code of 403

    When I navigate to GET "/<SECTION URI>/<'Algebra II' ID>"      
    Then I should receive a return code of 403

    When I navigate to GET "/<ASSESSMENT URI>/<'Grade 2 BOY DIBELS' ID>"
    Then I should receive a return code of 403

Examples:
| Username         | Password             | AnyDefaultSLIRole  |
| "aggregator"     | "aggregator1234"     | "AggregateViewer"  |