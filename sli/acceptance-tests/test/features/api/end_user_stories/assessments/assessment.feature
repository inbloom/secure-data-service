Feature: As a teacher I want to get DIBELS Composite Score and Reading Level

Background: None

	Scenario Outline:  (sorting) As a teacher, for my class, I want to get the most recent DIBELS assessment
    Given  I am a valid SEA/LEA end user <Username> with password <Password>
    And I have a Role attribute returned from the "SEA/LEA IDP"
    And the role attribute equals <AnyDefaultSLIRole>
    And I am authenticated on "SEA/LEA IDP"

     Given format "application/json"
	When I navigate to GET "/teachers/<'Ms. Jones' ID>"
	Then I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'Ms. Jones' ID>"
		And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
		And I should receive a link named "self" with URI "/teachers/<'Ms. Jones' ID>"
		
	When I navigate to "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
	Then I should receive a collection of 2 section links 
		And I should find section with uniqueSectionCode is "Section I" 
		And I should find section with uniqueSectionCode is "Section II"  with <'ImportantSection' ID>
				
	When I navigate to "getAssessments" with URI "/section-assessment-associations/<'ImportantSection' ID>/targets" with filter sorting and pagination
		And filter by  "assessmentFamilyHierarchyName" = "DIBELS Next" 
		And "sort-by" = "assessmentPeriodDescriptor.beginDate"
		 And "sort-order" = "descending" 
		 And "start-index" = "0" 
		 And "max-results" = "1"
	     Then  I should receive a collection of 1 assessment link
	        And I should find Assessment with "<'Grade 2 MOY DIBELS' ID>"
        
	When I navigate to GET "/assessments/<'Grade 2 MOY DIBELS' ID>"
	    Then I should receive 1 assessment  
		     And  the "assessmentTitle" is "DIBELS-MOY"
		     And the "assessmentCategory" is "Benchmark test"
		     And the "academicSubject" is "Reading"
		     And the "gradeLevelAssessed" is "Second grade"
		     And the "lowestGradeLevelAssessed" is "Second grade"
		     And the "assessmentPerformanceLevel" has the 3 levels
		     And the "assessmentPerformanceLevel.performanceLevelDescriptor.codeValue" = "Level 1"
			And the "assessmentPerformanceLevel.performanceLevelDescriptor.description" = "At or Above Benchmark"
			And  the "assessmentPerformanceLevel.minimumScore" = 190
			And the "assessmentPerformanceLevel.maximumScore" = 380
			And the "assessmentPerformanceLevel.performanceLevelDescriptor.codeValue" = "Level 2"
			And the "assessmentPerformanceLevel.performanceLevelDescriptor.description" = "Below Benchmark"
		    And the "assessmentPerformanceLevel.maximumScore" = 189
		    And the "assessmentPerformanceLevel.minimumScore" = 145
		    And the "assessmentPerformanceLevel.performanceLevelDescriptor.codeValue" = "Level 3"
		    And the "assessmentPerformanceLevel.performanceLevelDescriptor.description" = "Well Below Benchmark"
			And the "assessmentPerformanceLevel.maximumScore" = 144
			And the "assessmentPerformanceLevel.minimumScore" = 13
		     And the "assessmentFamilyHierarchyName" is "DIBELS Next"
		     And the "maxRawScore" is 380
		     And the "minRawScore" is 13
		     And the "assessmentPeriodDescriptor.beginDate" is "2012-01-01"
		     And the "assessmentPeriodDescriptor.endDate" is "2012-02-01"
	    
	 When I navigate to GET "/student-section-associations/<'ImportantSection' ID>/targets"
		Then I should receive a collection of 5 student links
		And I should find Student with <'John Doe' ID>
		And I should find Student with <'Sean Deer' ID>
		And I should find Student with <'Suzy Queue' ID>
		And I should find Student with <'Mary Line' ID>
	 	And I should find Student with <'Dong Steve' ID>
	 
	 Given I loop through the collection of student links
	 When for each student, I navigate to GET "/student-assessment-associations/<'Grade 2 MOY DIBELS' ID>" with filter sorting and pagination
	 	 And for each student, filter by "studentId" = <'Current_student' ID> 
		 And for each student, "sort-by" = "administrationDate"
		 And for each student, "sort-order" = "descending"
		 And for each student, "start-index" = "0" 
		 And for each student, "max-results" = "1"
		 Then for each student, I should receive a collection of 1 studentAssessmentAssociation link 
		  And for each student, I should receive a "student-assessment-association" with ID "<'Most Recent Assessment Association' ID>"
	     
	  When for each student, I navigate to URI /student-assessment-associations/"<'Most Recent Assessment Association' ID>"
	     Then for each student, I get 1 student-assessment-association
			    	 And for each student, the "administrationDate" is "2012-01-10"
			     And for each student, the "administrationEndDate" is "2012-01-15"
			     And for each student, the "gradeLevelWhenAssessed" is "Second grade"
			   #  And for each student, the "assessmentFamily" is "DIBELS Next Grade 2"
			     And for each student, the "performanceLevelDescriptors.description" is "Below Benchmark"
			     And for each student, the "scoreResults.assessmentReportingMethod" is "Scale score"
			     And for each student, the "scoreResults.result" is "120"   
			     
Examples:
| Username        | Password            | AnyDefaultSLIRole  |
#| "educator"      | "educator1234"      | "Educator"         |
| "administrator" | "administrator1234" | "IT Administrator" |
| "leader"        | "leader1234"        | "Leader"           |


Scenario Outline:  (paging/sorting) As a teacher, for my class, I want to get the most recent values of the following attributes: DIBELSCompositeScore, ReadingInstructionalLevel, PerformanceLevel
     Given  I am a valid SEA/LEA end user <Username> with password <Password>
    And I have a Role attribute returned from the "SEA/LEA IDP"
    And the role attribute equals <AnyDefaultSLIRole>
    And I am authenticated on "SEA/LEA IDP"

     Given format "application/json"
	When I navigate to GET "/teachers/<'Ms. Jones' ID>"
	Then I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'Ms. Jones' ID>"
		And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
		And I should receive a link named "self" with URI "/teachers/<'Ms. Jones' ID>"
		
	When I navigate to "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
	Then I should receive a collection of 2 section links 
		And I should find section with uniqueSectionCode is "Section I" 
		And I should find section with uniqueSectionCode is "Section II"  with <'ImportantSection' ID>
				
	When I navigate to "getStudents" with URI "/student-section-associations/<'ImportantSection' ID>/targets"
	Then I should receive a collection of 5 student links
		And I should find Student with <'John Doe' ID>
		And I should find Student with <'Sean Deer' ID>
		And I should find Student with <'Suzy Queue' ID>
		And I should find Student with <'Mary Line' ID>
	 	And I should find Student with <'Dong Steve' ID>
	 	 	
	    When I navigate to "getAssessments" with URI "/student-assessment-associations/<'Each Student' ID>/targets" and  filter by "assessmentTitle" is "DIBELS-MOY"
	     Then  I should receive a collection of 1 assessment link
	        And I should find Assessment with "<'Grade 2 MOY DIBELS' ID>"
	        
	   When I navigate to GET "/assessments/<'Grade 2 MOY DIBELS' ID>"
	    Then I should receive 1 assessment  
		     And the "assessmentCategory" is "Benchmark test"
		     And the "academicSubject" is "Reading"
		     And the "gradeLevelAssessed" is "Second grade"
		     And the "lowestGradeLevelAssessed" is "Second grade"
		     And the "assessmentPerformanceLevel" has the 3 levels
			 And the "assessmentPerformanceLevel.performanceLevelDescriptor.codeValue" = "Level 1"
			And the "assessmentPerformanceLevel.performanceLevelDescriptor.description" = "At or Above Benchmark"
			And  the "assessmentPerformanceLevel.minimumScore" = 190
			And the "assessmentPerformanceLevel.maximumScore" = 380
			And the "assessmentPerformanceLevel.performanceLevelDescriptor.codeValue" = "Level 2"
			And the "assessmentPerformanceLevel.performanceLevelDescriptor.description" = "Below Benchmark"
		    And the "assessmentPerformanceLevel.maximumScore" = 189
		    And the "assessmentPerformanceLevel.minimumScore" = 145
		    And the "assessmentPerformanceLevel.performanceLevelDescriptor.codeValue" = "Level 3"
		    And the "assessmentPerformanceLevel.performanceLevelDescriptor.description" = "Well Below Benchmark"
			And the "assessmentPerformanceLevel.maximumScore" = 144
			And the "assessmentPerformanceLevel.minimumScore" = 13
			 And the "assessmentFamilyHierarchyName" is "DIBELS Next"
		      And the "maxRawScore" is 380
		     And the "minRawScore" is 13
		     And the "assessmentPeriodDescriptor.beginDate" is "2012-01-01"
		     And the "assessmentPeriodDescriptor.endDate" is "2012-02-01"
		     
	    When for each student, I navigate to GET "/student-assessment-associations/<'Grade 2 MOY DIBELS' ID>" with filter sorting and pagination
	 	 And for each student, filter by "studentId" = <'Current_student' ID> 
		 And for each student, "sort-by" = "administrationDate"
		 And for each student, "sort-order" = "descending"
		 And for each student, "start-index" = "0" 
		 And for each student, "max-results" = "1"
		 Then for each student, I should receive a collection of 1 studentAssessmentAssociation link 
		  And for each student, I should receive a "student-assessment-association" with ID "<'Most Recent Assessment Association' ID>"
	     
	  When for each student, I navigate to URI /student-assessment-associations/"<'Most Recent Assessment Association' ID>"
	     Then for each student, I get 1 student-assessment-association
			    	 And for each student, the "administrationDate" is "2012-01-10"
			     And for each student, the "administrationEndDate" is "2012-01-15"
			     And for each student, the "gradeLevelWhenAssessed" is "Second grade"
			   #  And for each student, the "assessmentFamily" is "DIBELS Next Grade 2"
			     And for each student, the "performanceLevelDescriptors.description" is "Below Benchmark"
			     And for each student, the "scoreResults.assessmentReportingMethod" is "Scale score"
			     And for each student, the "scoreResults.result" is "120"   

Examples:
| Username        | Password            | AnyDefaultSLIRole  |
#| "educator"      | "educator1234"      | "Educator"         |
| "administrator" | "administrator1234" | "IT Administrator" |
| "leader"        | "leader1234"        | "Leader"           |

Scenario Outline:  As a AggregateViewer I should not see personally identifiable information data
    Given I am a valid SEA/LEA end user <Username> with password <Password>
    And I have a Role attribute returned from the "SEA/LEA IDP"
    And the role attribute equals "Aggregate Viewer"
    And I am authenticated on "SLI Realm"

    When I navigate to GET "/teachers/<'Ms. Smith' ID>"
    Then I should receive a return code of 403
        
    When I navigate to GET "/teacher-section-associations/<'Teacher Ms. Jones and Section Algebra II' ID>/targets"
    Then I should receive a return code of 403

    When I navigate to GET "/student-section-associations/<'Algebra II' ID>/targets"
    Then I should receive a return code of 403
         
    When I navigate to GET "/students/<'Jane Doe' ID>"      
    Then I should receive a return code of 403
    
    When I navigate to GET "/sections/<'Algebra II' ID>"      
    Then I should receive a return code of 403
        
    When I navigate to GET "/assessments/<'Grade 2 BOY DIBELS' ID>"
    Then I should receive a return code of 403

Examples:
| Username         | Password             | AnyDefaultSLIRole  |
| "aggregator"     | "aggregator1234"     | "AggregateViewer"  |
     
