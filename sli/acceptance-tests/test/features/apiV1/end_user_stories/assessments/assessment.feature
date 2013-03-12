@RALLY_US209
@RALLY_US210
Feature: As a teacher I want to get DIBELS Composite Score and Reading Level

Background: None

   Scenario Outline:  (sorting) As a staff, for my edOrgs, I want to get the most recent Math assessment
     Given I am a valid SEA/LEA end user <Username> with password <Password>
        And I have a Role attribute returned from the "SLI"
        And the role attribute equals <AnyDefaultSLIRole>
        And I am authenticated on "IL"

     Given format "application/json"
     When I navigate to GET "/<TEACHER URI>/<'Linda Kim' ID>"
      Then I should receive a link named "getTeacherSectionAssociations" with URI "/<TEACHER URI>/<'Linda Kim' ID>/<TEACHER SECTION ASSOC URI>"
        And I should receive a link named "getSections" with URI "/<TEACHER URI>/<'Linda Kim' ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
        And I should receive a link named "self" with URI "/<TEACHER URI>/<'Linda Kim' ID>"

     When I navigate to GET "/<TEACHER URI>/<'Linda Kim' ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
      Then I should have a list of "section" entities
        And I should have an entity with ID "<'8th Grade English - Sec 6' ID>"
#new
     When I navigate to GET "/<ASSESSMENT URI>"
      Then I should receive a return code of 200
      And I should have a list of 17 "assessment" entities
#/new     
     When I navigate to GET "/<ASSESSMENT URI>/<'Math Assessment' ID>"
      Then I should have a list of 1 "assessment" entities
        And "assessmentTitle" should be "Mathematics Achievement Assessment Test"
        And "assessmentCategory" should be "Advanced Placement"
        And "academicSubject" should be "Mathematics"
        And "gradeLevelAssessed" should be "Eighth grade"
#new
     When I navigate to GET "/<SCHOOL URI>/<STUDENT SCHOOL URI>/<STUDENT SCHOOL ASSOC URI>/<STUDENT URI>/<STUDENT ASSESSMENT ASSOC URI>"
      Then I should have a list of 7 "studentAssessment" entities
        And occurrence 2 of entity "assessmentId" should be "<'SAT' ID>"
        And occurrence 2 of entity "gradeLevelWhenAssessed" should be "Twelfth grade"
        And occurrence 2 of entity "retestIndicator" should be "1st Retest"
        And in occurrence 2 I should receive a link named "self" with URI "/<STUDENT ASSESSMENT ASSOC URI>/<'SAT Student Assessment Association' ID>"
        And in occurrence 2 I should receive a link named "custom" with URI "/<STUDENT ASSESSMENT ASSOC URI>/<'SAT Student Assessment Association' ID>/<CUSTOM URI>"
        And in occurrence 2 I should receive a link named "getStudent" with URI "/<STUDENT URI>/<'Matt DERP' ID>"
        And in occurrence 2 I should receive a link named "getStudents" with URI "/<STUDENT ASSESSMENT ASSOC URI>/<'SAT Student Assessment Association' ID>/<STUDENT URI>"
        And in occurrence 2 I should receive a link named "getAssessment" with URI "/<ASSESSMENT URI>/<'SAT' ID>"

     When I navigate to GET "/<ASSESSMENT URI>/<'SAT' ID>/<STUDENT ASSESSMENT ASSOC URI>"
      Then I should have a list of 0 "assessment" entities
#/new

     When I navigate to GET "/<SECTION URI>/<'8th Grade English - Sec 6' ID>/<STUDENT SECTION ASSOC URI>/<STUDENT URI>"
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

     When I navigate to URI "/<SECTION URI>/<'8th Grade English - Sec 6' ID>/<STUDENT SECTION ASSOC URI>/<STUDENT URI>/<STUDENT ASSESSMENT ASSOC URI>" with filter sorting and pagination
        And filter by "sortBy" = "administrationDate"
        And filter by "sortOrder" = "descending"
        And filter by "offset" = "0"
        And I submit the sorting and pagination request
        Then I should have a list of "studentAssessment" entities
        And I should have an entity with ID "<'Most Recent Math Student Assessment Association' ID>"

     When I navigate to GET "/<STUDENT ASSESSMENT ASSOC URI>/<'Most Recent Math Student Assessment Association' ID>"
      Then I should have a list of 1 "studentAssessment" entities
        And "administrationDate" should be "2011-09-15"
        And "administrationEndDate" should be "2011-12-15"
        And "retestIndicator" should be "Primary Administration"

Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "rrogers"       | "rrogers1234"       | "IT Administrator" |
| "sbantu"        | "sbantu1234"        | "Leader"           |


   Scenario Outline:  (sorting) As a teacher, for my classes, I want to get the most recent Math assessment
     Given I am a valid SEA/LEA end user <Username> with password <Password>
        And I have a Role attribute returned from the "SLI"
        And the role attribute equals <AnyDefaultSLIRole>
        And I am authenticated on "IL"

     Given format "application/json"
     When I navigate to GET "/<TEACHER URI>/<'Linda Kim' ID>"
      Then I should receive a link named "getTeacherSectionAssociations" with URI "/<TEACHER URI>/<'Linda Kim' ID>/<TEACHER SECTION ASSOC URI>"
        And I should receive a link named "getSections" with URI "/<TEACHER URI>/<'Linda Kim' ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
        And I should receive a link named "self" with URI "/<TEACHER URI>/<'Linda Kim' ID>"

     When I navigate to GET "/<TEACHER URI>/<'Linda Kim' ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
      Then I should have a list of "section" entities
        And I should have an entity with ID "<'8th Grade English - Sec 6' ID>"
#new
     When I navigate to GET "/<ASSESSMENT URI>"
      Then I should receive a return code of 404
      #Then I should have a list of 17 "assessment" entities
#/new     
     When I navigate to GET "/<ASSESSMENT URI>/<'Math Assessment' ID>"
      Then I should have a list of 1 "assessment" entities
        And "assessmentTitle" should be "Mathematics Achievement Assessment Test"
        And "assessmentCategory" should be "Advanced Placement"
        And "academicSubject" should be "Mathematics"
        And "gradeLevelAssessed" should be "Eighth grade"
#new
     When I navigate to GET "/<SCHOOL URI>/<STUDENT SCHOOL URI>/<STUDENT SCHOOL ASSOC URI>/<STUDENT URI>/<STUDENT ASSESSMENT ASSOC URI>"
      Then I should have a list of 3 "studentAssessment" entities
        And occurrence 2 of entity "assessmentId" should be "<'SAT' ID>"
        And occurrence 2 of entity "gradeLevelWhenAssessed" should be "Twelfth grade"
        And occurrence 2 of entity "retestIndicator" should be "1st Retest"
        And in occurrence 2 I should receive a link named "self" with URI "/<STUDENT ASSESSMENT ASSOC URI>/<'SAT Student Assessment Association' ID>"
        And in occurrence 2 I should receive a link named "custom" with URI "/<STUDENT ASSESSMENT ASSOC URI>/<'SAT Student Assessment Association' ID>/<CUSTOM URI>"
        And in occurrence 2 I should receive a link named "getStudent" with URI "/<STUDENT URI>/<'Matt DERP' ID>"
        And in occurrence 2 I should receive a link named "getStudents" with URI "/<STUDENT ASSESSMENT ASSOC URI>/<'SAT Student Assessment Association' ID>/<STUDENT URI>"
        And in occurrence 2 I should receive a link named "getAssessment" with URI "/<ASSESSMENT URI>/<'SAT' ID>"

     When I navigate to GET "/<ASSESSMENT URI>/<'SAT' ID>/<STUDENT ASSESSMENT ASSOC URI>"
      Then I should have a list of 0 "assessment" entities
#/new

     When I navigate to GET "/<SECTION URI>/<'8th Grade English - Sec 6' ID>/<STUDENT SECTION ASSOC URI>/<STUDENT URI>"
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

     When I navigate to URI "/<SECTION URI>/<'8th Grade English - Sec 6' ID>/<STUDENT SECTION ASSOC URI>/<STUDENT URI>/<STUDENT ASSESSMENT ASSOC URI>" with filter sorting and pagination
        And filter by "sortBy" = "administrationDate"
        And filter by "sortOrder" = "descending"
        And filter by "offset" = "0"
        And I submit the sorting and pagination request
        Then I should have a list of "studentAssessment" entities
        And I should have an entity with ID "<'Most Recent Math Student Assessment Association' ID>"

     When I navigate to GET "/<STUDENT ASSESSMENT ASSOC URI>/<'Most Recent Math Student Assessment Association' ID>"
      Then I should have a list of 1 "studentAssessment" entities
        And "administrationDate" should be "2011-09-15"
        And "administrationEndDate" should be "2011-12-15"
        And "retestIndicator" should be "Primary Administration"

Examples:
| "linda.kim"     | "linda.kim1234"     | "Educator"         |

    Scenario Outline:  (paging/sorting) As a teacher, for my class, I want to get the most recent values of the following attributes: DIBELSCompositeScore, ReadingInstructionalLevel, PerformanceLevel
    Given I am a valid SEA/LEA end user <Username> with password <Password>
    And I have a Role attribute returned from the "SLI"
    And the role attribute equals <AnyDefaultSLIRole>
    And I am authenticated on "IL"

    Given format "application/json"
     When I navigate to GET "/<TEACHER URI>/<'Linda Kim' ID>"
      Then I should receive a link named "getTeacherSectionAssociations" with URI "/<TEACHER URI>/<'Linda Kim' ID>/<TEACHER SECTION ASSOC URI>"
        And I should receive a link named "getSections" with URI "/<TEACHER URI>/<'Linda Kim' ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
        And I should receive a link named "self" with URI "/<TEACHER URI>/<'Linda Kim' ID>"

    When I navigate to GET "/<TEACHER URI>/<'Linda Kim' ID>/<TEACHER SECTION ASSOC URI>/<SECTION URI>"
    Then I should have a list of "section" entities
        And I should have an entity with ID "<'8th Grade English - Sec 6' ID>"

    When I navigate to GET "/<SECTION URI>/<'8th Grade English - Sec 6' ID>/<STUDENT SECTION ASSOC URI>/<STUDENT URI>"
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

     When I navigate to URI "/<STUDENT URI>/<'Matt Sollars' ID>/<STUDENT ASSESSMENT ASSOC URI>/<ASSESSMENT URI>" with filter sorting and pagination
        And filter by "assessmentTitle" = "SAT 2"
        And I submit the sorting and pagination request
      Then I should have a list of "assessment" entities
        And I should have an entity with ID "<'SAT' ID>"

     When I navigate to GET "/<ASSESSMENT URI>/<'SAT' ID>"
      Then I should have a list of 1 "assessment" entities
        And "assessmentTitle" should be "SAT 2"
        And "assessmentCategory" should be "College Addmission Test"
        And "academicSubject" should be "Reading"
        And "gradeLevelAssessed" should be "Twelfth grade"
        And "lowestGradeLevelAssessed" should be "Eleventh grade"
        And "assessmentFamilyHierarchyName" should be "SAT"
        And "maxRawScore" should be "2400"
        And "minRawScore" should be "600"
        And the field "assessmentPeriodDescriptor.beginDate" should be "2011-01-01"
        And the field "assessmentPeriodDescriptor.endDate" should be "2011-02-01"
        And there are "0" "assessmentPerformanceLevel"

     When I navigate to URI "/<STUDENT URI>/<'Matt Sollars' ID>/<STUDENT ASSESSMENT ASSOC URI>" with filter sorting and pagination
        And filter by "sortBy" = "administrationDate"
        And filter by "sortOrder" = "descending"
        And filter by "offset" = "0"
        #And filter by "limit" = "1"
        And I submit the sorting and pagination request
      Then I should have a list of "studentAssessment" entities
        And I should have an entity with ID "<'Most Recent SAT Student Assessment Association' ID>"

     When I navigate to GET "/<STUDENT ASSESSMENT ASSOC URI>/<'Most Recent SAT Student Assessment Association' ID>"
      Then I should have a list of 1 "studentAssessment" entities
        And "administrationDate" should be "2011-05-10"
        And "administrationEndDate" should be "2011-06-15"
        And "gradeLevelWhenAssessed" should be "Twelfth grade"
        And "retestIndicator" should be "1st Retest"
        And the field "scoreResults.0.assessmentReportingMethod" should be "Scale score"
        And the field "scoreResults.0.result" should be "2060"
#new
     When I navigate to GET "/<STUDENT ASSESSMENT ASSOC URI>/<'Most Recent SAT Student Assessment Association' ID>/<STUDENT URI>"
      Then I should have a list of 1 "student" entities

     When I navigate to GET "/<STUDENT ASSESSMENT ASSOC URI>/<'Most Recent SAT Student Assessment Association' ID>/<ASSESSMENT URI>"
      Then I should have a list of 1 "assessment" entities
#/new
Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "rrogers"       | "rrogers1234"       | "IT Administrator" |
| "sbantu"        | "sbantu1234"        | "Leader"           |
| "linda.kim"     | "linda.kim1234"     | "Educator"         |

@derp
Scenario Outline:  As a AggregateViewer I should not see personally identifiable information data
    Given I am a valid SEA/LEA end user <Username> with password <Password>
    And I have a Role attribute returned from the "SLI"
    And the role attribute equals <AnyDefaultSLIRole>
    And I am authenticated on "IL"
    And the sli securityEvent collection is empty
    When I navigate to GET "/<TEACHER URI>/<'Ms. Smith' ID>"
    Then I should receive a return code of 403
    And a security event matching "^Access Denied" should be in the sli db
     And I check to find if record is in sli db collection:
       | collectionName      | expectedRecordCount | searchParameter       | searchValue                           |
       | securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK                           |
       | securityEvent       | 1                   | body.targetEdOrgList  | IL-DAYBREAK                           |

    When the sli securityEvent collection is empty
    And I navigate to GET "/<TEACHER SECTION ASSOC URI>/<'Teacher Ms. Jones and Section Algebra II' ID>/<TEACHER URI>"
    Then I should receive a return code of 403
    And a security event matching "^Access Denied" should be in the sli db
     And I check to find if record is in sli db collection:
       | collectionName      | expectedRecordCount | searchParameter       | searchValue                           |
       | securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK                           |
       | securityEvent       | 1                   | body.targetEdOrgList  | IL-DAYBREAK                           |

    When the sli securityEvent collection is empty
    And I navigate to GET "/<SECTION URI>/<'Track and Field - Sec 6s10' ID>/<STUDENT SECTION ASSOC URI>/<STUDENT URI>"
    Then I should receive a return code of 403
    And a security event matching "^Access Denied" should be in the sli db
     And I check to find if record is in sli db collection:
       | collectionName      | expectedRecordCount | searchParameter       | searchValue                           |
       | securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK                           |
       | securityEvent       | 1                   | body.targetEdOrgList  | IL-DAYBREAK                           |

    When the sli securityEvent collection is empty
    And I navigate to GET "/<STUDENT URI>/<'Matt Sollars' ID>"
    Then I should receive a return code of 403
    And a security event matching "^Access Denied" should be in the sli db
     And I check to find if record is in sli db collection:
       | collectionName      | expectedRecordCount | searchParameter       | searchValue                           |
       | securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK                           |
       | securityEvent       | 1                   | body.targetEdOrgList  | IL-DAYBREAK                           |

#Sections are now public and global
    When I navigate to GET "/<SECTION URI>/<'Algebra II' ID>"      
    Then I should receive a return code of 200 

#Assessments are now public and global
    When I navigate to GET "/<ASSESSMENT URI>/<'Grade 2 BOY DIBELS' ID>"
    Then I should receive a return code of 200

Examples:
| Username         | Password             | AnyDefaultSLIRole  |
| "msmith"         | "msmith1234"         | "AggregateViewer"  |
