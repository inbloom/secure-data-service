@wip
Feature: DIBELS Assessments

Background: None

Scenario Outline:  As a teacher I want to get DIBELS Composite Score and Reading Level
	Given  I am valid SEA/LEA end user <Username> with password <Password>
	And I have a Role attribute returned from the "SEA/LEA IDP"
	And the role attribute equals <AnyDefaultSLIRole>
	And I am authenticated on "SEA/LEA IDP"

	When I navigate to GET "/teachers/<'Ms. Jones' ID>"
	Then I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'Ms. Jones' ID>"
		And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
		And I should receive a link named "self" with URI "/teachers/<'Ms. Jones' ID>"
		
	When I navigate to "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
	Then I should receive a collection of 2 section links 
		And I should find section with sectionName is "Section I" 
		And I should find section with sectionName is "Section II"  with <'ImportantSection' ID>
				
	When I navigate to "getStudents" with URI "/student-section-associations/<'ImportantSection' ID>/targets"
	Then I should receive a collection of 5 student links
		And after resolution, I should receive a "Student"" with ID <'John Doe' ID>
		And after resolution, I should receive a "Student" with ID  <'Sean Deer' ID>
		And after resolution, I should receive a "Student" with ID  <'Suzy Queue' ID>
		And after resolution, I should receive a "Student" with ID  <'Mary Line' ID>
	 	And after resolution, I should receive a "Student" with ID  <'Dong Steve' ID>
	 	
	# this is to enable paging 	
	When I navigate to each student  	
		 Then I should receive a link named "getAssessments" with URI "/student-assessment-associations/<'Each Student' ID>/targets"
     
	     When I navigate to GET "/student-assessment-associations/<"Each Student ID">/targets"
	     Then  I should receive a collection of 3 assessment link
	        And after resolution, I should receive an "Assessment" with ID "<'Grade 2 BOY DIBELS' ID>"
	        And after resolution, I should receive an "Assessment" with ID "<'Grade 2 EOY DIBELS' ID>" and with 
	        And after resolution, I should receive an "Assessment" with ID "<'Grade 2 MOY DIBELS' ID>"
        
	     When I navigate to  GET "/assessments/<'Grade 2 MOY DIBELS' ID>"
	     Then the "AssessmentTitle" is "DIBELS-MOY"
		     And the "AssessmentCategory" is "Benchmark Test"
		     And the "AssessmentSubjectType" is "Reading"
		     And the "GradeLevelAssessed" is "Second Grade"
		     And the "LowestGradeLevelAssessed" is "Second Grade"
		     And the "AssessmentPerformanceLevel" is an array of ("At or Above Benchmark", "Below Benchmark", "Well Below Benchmark")
		     And the "AssessmentFamilyHierarchyName" is "DIBELS Next"
		     And the "MaxRawScore" is "380"
		     And I should recieve a link to "getStudentAssessmentAssociations" with URI "/student-assessment-associations/<'Grade 2 MOY DIBELS' ID>"
	    
	     When I navigate to GET "/student-assessment-associations/<'Grade 2 MOY DIBELS' ID>"
	     Then I get a list of "student-assessment-associations"
		     When I traverse each association
			     Then I find the latest association with
			    	 And the "AdministrationDate" is "2012/01/10"
			     And the "AdministrationEndDate" is "2012/01/15"
			     And the "GradeLevelWhenAssessed" is "Second Grade"
			     And the "AssessmentFamily" is "DIBELS Next Grade 2"
			     And the "PerformanceLevel" is "Below Benchmark"
			     And the "ScoreResult" is "120"

Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "educator"      | "educator1234"      | "Educator"         |
| "administrator" | "administrator1234" | "IT Administrator" |
| "leader"        | "leader1234"        | "Leader"           |


Scenario Outline:  As a AggregateViewer I should not get DIBELS Composite Score and Reading Level
	Given  I am valid SEA/LEA end user <Username> with password <Password>
	And I have a Role attribute returned from the "SEA/LEA IDP"
	And the role attribute equals <AnyDefaultSLIRole>
	And I am authenticated on "SEA/LEA IDP"

	When I navigate to GET "/teachers/<'Ms. Jones' ID>"
	Then I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'Ms. Jones' ID>"
		And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
		And I should receive a link named "self" with URI "/teachers/<'Ms. Jones' ID>"
		
	When I navigate to "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
	Then I should receive a collection of 2 section links 
		And I should find section with sectionName is "Section I" 
		And I should find section with sectionName is "Section II"  with <'ImportantSection' ID>
				
	When I navigate to "getStudents" with URI "/student-section-associations/<'ImportantSection' ID>/targets"
	Then I should receive a collection of 5 student links
		And after resolution, I should receive a "Student"" with ID <'John Doe' ID>
		And after resolution, I should receive a "Student" with ID  <'Sean Deer' ID>
		And after resolution, I should receive a "Student" with ID  <'Suzy Queue' ID>
		And after resolution, I should receive a "Student" with ID  <'Mary Line' ID>
	 	And after resolution, I should receive a "Student" with ID  <'Dong Steve' ID>
	 	
	# this is to enable paging 	
	When I navigate to each student  	
	Then I should not receive a link named "getAssessments" with URI "/student-assessment-associations/<'Each Student' ID>/targets"
Examples:
| Username         | Password             | AnyDefaultSLIRole  |
| "aggregateViewer"| "aggregate1234"      | "AggregateViewer"         |
	 
