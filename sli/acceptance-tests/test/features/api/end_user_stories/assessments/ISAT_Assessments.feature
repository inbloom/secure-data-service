@wip
Feature: As a teacher for my class I want to get the most recent values of the following attributes: ISAT Reading test PerformaceLevel, ScaleScore and Lexile

Background: None

Scenario Outline:  (sorting) As a teacher, for my class, I want to get the most recent DIBELS assessment
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
		And sort by AssessmentPeriodDescriptor.BeginDate, descending
		And set the page size to 1
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
				     "PerformanceLevel= "Academic Warning"
				     "MaximumScore" = "179"
				     "MinimumScore" = "120"
				     "PerformanceLevel"= "Below Standards"
				     "MaximumScore" = "230"
				     "MinimumScore" = "180"
				     "PerformanceLevel"= "Meets Standards"
				     "MaximumScore" = "277"
				     "MinimumScore" = "231"
				     "PerformanceLevel"= "Exceeds Standards"
				     "MaximumScore" = "364"
				     "MinimumScore" = "278"
		     And the "AssessmentFamilyHierarchyName" is "ISAT Reading for Grades 3-8"
		     And the "MaxRawScore" is "450"
		     And the "MinRawScore" is "120"
		     And the "AssessmentPeriodDescriptor.BeginDate" = "2012/05/01"
		     And the "AssessmentPeriodDescriptor.EndDate" = "2012/05/31"
	    
	 When I navigate to GET "/student-assessment-associations/<'ISAT Reading' ID>"
	     Then I get a collection of 20 student-assessment-associations links 
	     When I filter by studentId is <'Suzy Queue' ID>
	         Then I get 1 student-assessment-association
			    	 And the "AdministrationDate" is "2012/05/10"
			     And the "GradeLevelWhenAssessed" is "Seventh Grade"
			     And the "AssessmentFamily" is "ISAT Reading for Grade 8"
			     And the "PerformanceLevel" is "Meets Standards""
			     And the "ScaleScore" is "250"
			     And the "PercentileRank" is "92"
			     And the "LexileLevel" is "1205"
			     
Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "educator"      | "educator1234"      | "Educator"         |
| "administrator" | "administrator1234" | "IT Administrator" |
| "leader"        | "leader1234"        | "Leader"           |


# negative security case docuemented in another file.
