@RALLY_US510 @RALLY_US646
Feature: Get StateTest Reading Scores
Background: None

@wip
Scenario Outline:  As a teacher for my class I want to get the most recent values of the following attributes: StateTest Reading test PerformaceLevel, ScaleScore and Lexile
	Given  I am valid SEA/LEA end user <Username> with password <Password>
	And I have a Role attribute returned from the "SLI"
	And the role attribute equals <AnyDefaultSLIRole>
	And I am authenticated on "SLI"

	When I navigate to GET "/teachers/<'Ms. Jones' ID>"
	Then I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'Ms. Jones' ID>"
		And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
		And I should receive a link named "self" with URI "/teachers/<'Ms. Jones' ID>"
		
	When I navigate to "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
	Then I should receive a collection of 2 section links 
		And I should find section with uniqueSectionCode is "Section I" 
		And I should find section with uniqueSectionCode is "Section II"  with <'ImportantSection' ID>
				
	When I navigate to "getAssessments" with URI "/section-assessment-associations/<'ImportantSection' ID>/targets" 
		And filter by  "AssessmentFamilyHierarchyName" = "StateTest Reading for Grades 3-8" 
	     And "sort_by" ="AssessmentPeriodDescriptor.BeginDatee"
		 And "sort_order"="descending" 
		 And set the "page_size"  = "1" 
		 And get the "page"="1"
	     Then  I should receive a collection of 1 assessment link
	        And after resolution, I should receive an "Assessment" with ID "<'Most recent StateTest Reading' ID>"
        
	When I navigate to  GET "/assessments/<'Most recent StateTest Reading' ID>"
	    Then I should receive 1 assessment  
		     And  the "AssessmentTitle" is "StateTest Reading"
		     And the "AssessmentCategory" is "Achievement Test"
		     And the "AssessmentSubjectType" is "Reading"
		     And the "GradeLevelAssessed" is "Eight Grade"
		     And the "LowestGradeLevelAssessed" is "Third Grade"
		     And the "AssessmentPerformanceLevel" has the 4 levels
			 And the "AssessmentPerformanceLevel" levels contain a "Description" value of "Academic Warning"
             And the "AssessmentPerformanceLevel" levels contain a "Code" value of "AW"
             And the "AssessmentPerformanceLevel" levels contain a "MaximumScore" value of "179"
             And the "AssessmentPerformanceLevel" levels contain a "MinimumScore" value of "120"
             And the "AssessmentPerformanceLevel" levels contain a "PerformanceLevel.Description" value of "Below Standards"
             And the "AssessmentPerformanceLevel" levels contain a "PerformanceLevel.Code" value of "BS"
             And the "AssessmentPerformanceLevel" levels contain a "MaximumScore" value of "230"
             And the "AssessmentPerformanceLevel" levels contain a "MinimumScore" value of "180"
             And the "AssessmentPerformanceLevel" levels contain a "PerformanceLevel.Description" value of "Meets Standards"
             And the "AssessmentPerformanceLevel" levels contain a "PerformanceLevel.Code" value of "MS"
             And the "AssessmentPerformanceLevel" levels contain a "MaximumScore" value of "277"
             And the "AssessmentPerformanceLevel" levels contain a "MinimumScore" value of "231"
             And the "AssessmentPerformanceLevel" levels contain a "PerformanceLevel.Description" value of "Exceeds Standards"
             And the "AssessmentPerformanceLevel" levels contain a "PerformanceLevel.Code" value of "ES"
             And the "AssessmentPerformanceLevel" levels contain a "MaximumScore" value of "364"
             And the "AssessmentPerformanceLevel" levels contain a "MinimumScore" value of "278"
		     And the "AssessmentFamilyHierarchyName" is "StateTest Reading for Grades 3-8"
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
	 When I navigate to GET "/student-assessment-associations/<'StateTest Reading' ID>" 
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
