@wip
Feature: Get ISAT Reading Scores
Background: None

Scenario Outline:  As a teacher for my class I want to get the most recent values of the following attributes: ISAT Reading test PerformaceLevel, ScaleScore and Lexile
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
				
	When I navigate to "getAssessments" with URI "/section-assessment-associations/<'ImportantSection' ID>/targets" 
		And filter by  "AssessmentFamilyHierarchyName" = "ISAT Reading for Grades 3-8" 
	     And "sort_by" ="AssessmentPeriodDescriptor.BeginDatee"
		 And "sort_order"="descending" 
		 And set the "page_size"  = "1" 
		 And get the "page"="1"
	     Then  I should receive a collection of 1 assessment link
	        And after resolution, I should receive an "Assessment" with ID "<'Most recent ISAT Reading' ID>"
        
	When I navigate to  GET "/assessments/<'Most recent ISAT Reading' ID>"
	    Then I should receive 1 assessment  
		     And  the "AssessmentTitle" is "ISAT Reading"
		     And the "AssessmentCategory" is "Achievement Test"
		     And the "AssessmentSubjectType" is "Reading"
		     And the "GradeLevelAssessed" is "Eight Grade"
		     And the "LowestGradeLevelAssessed" is "Third Grade"
		     And the "AssessmentPerformanceLevel" has the 4 levels
				     "PerformanceLevel.Description"= "Academic Warning"
				     "PerformanceLevel.Code"= "AW"
				     "MaximumScore" = "179"
				     "MinimumScore" = "120"
				     "PerformanceLevel.Description"= "Below Standards"
				     "PerformanceLevel.Code"= "BS"
				     "MaximumScore" = "230"
				     "MinimumScore" = "180"
				     "PerformanceLevel.Description"= "Meets Standards"
				     "PerformanceLevel.Code"= "MS"
				     "MaximumScore" = "277"
				     "MinimumScore" = "231"
				      "PerformanceLevel.Description"= "Exceeds Standards"
				     "PerformanceLevel.Code"= "ES"
				     "MaximumScore" = "364"
				     "MinimumScore" = "278"
		     And the "AssessmentFamilyHierarchyName" is "ISAT Reading for Grades 3-8"
		     And the "MaxRawScore" is "450"
		     And the "MinRawScore" is "120"
		     And the "AssessmentPeriodDescriptor.BeginDate" = "2012/05/01"
		     And the "AssessmentPeriodDescriptor.EndDate" = "2012/05/31"

    When I navigate to GET "/student-section-association/<'Important_Section' ID>/targets"
		Then I should receive a collection of 5 student links
		And after resolution, I should receive a "Student"" with ID <'John Doe' ID>
		And after resolution, I should receive a "Student" with ID  <'Sean Deer' ID>
		And after resolution, I should receive a "Student" with ID  <'Suzy Queue' ID>
		And after resolution, I should receive a "Student" with ID  <'Mary Line' ID>
	 	And after resolution, I should receive a "Student" with ID  <'Dong Steve' ID>
	 
	 Given I loop through the collection of student links
	 When I navigate to GET "/student-assessment-associations/<'ISAT Reading' ID>" 
	     When I filter by studentId is <'Current_student' ID>
	         Then I get 1 student-assessment-association
			    	 And the "AdministrationDate" is "2012/05/10"
			     And the "GradeLevelWhenAssessed" is "Seventh Grade"
			     And the "PerformanceLevelDescriptor.Description" is "Meets Standards"
			     And the "PerformanceLevelDescriptor.CodeValue" is "M"
			     And the "ScoreResults.AssessmentReportingResultType" is "PercentileRank"	
			     And the "ScoreResults.Result" is "92" 
			     And the "ScoreResults.AssessmentReportingResultType" is "ScaleScore"	
			     And the "ScoreResults.Result" is "250"    
			     And the "ScoreResults.AssessmentReportingResultType" is "LexlileLevel"	
			     And the "ScoreResults.Result" is "1205"     
			     
Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "educator"      | "educator1234"      | "Educator"         |
| "administrator" | "administrator1234" | "IT Administrator" |
| "leader"        | "leader1234"        | "Leader"           |


# negative security case docuemented in another file.
