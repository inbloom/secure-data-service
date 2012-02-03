@wip
Feature: SAT Scores 

Background: None

Scenario Outline:  As a teacher for my class I want to get the most recent values SAT including Critical Reading, Writing, Mathematics 
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
		And filter by  "AssessmentFamilyHierarchyName" = "SAT" 
		And "sort_by" ="AssessmentPeriodDescriptor.BeginDatee"
		And "sort_order"="descending" 
		And set the "page_size"  = "1" 
		And get the "page"="1"
	     Then  I should receive a collection of 1 assessment link
	        And after resolution, I should receive an "Assessment" with ID "<'Most recent SAT' ID>"
        
	When I navigate to  GET "/assessments/<'Most recent SAT' ID>"
	    Then I should receive 1 assessment  
		     And  the "AssessmentTitle" is "SAT"
		     And the "AssessmentCategory" is "College Addmission Test"
		     And the "GradeLevelAssessed" is "Twelfth Grade"
		     And the "LowestGradeLevelAssessed" is "Eleventh Grade"
		     And the "AssessmentFamilyHierarchyName" is "SAT"
		     And the "MaxRawScore" is "2400"
		     And the "MinRawScore" is "600"
	  
	 When I navigate to GET "/student-section-association/<'ImportantSection' ID>/targets"
		Then I should receive a collection of 5 student links
	 
	 Given I loop through the collection of student links      
	 When I navigate to GET "/student-assessment-associations/<'SAT' ID>"
	     Then I get a collection of 20 student-assessment-associations links 
	     When I filter by studentId is <'Current_student' ID>
	         Then I get 1 student-assessment-association
			    	 And the "AdministrationDate" is "2012/05/10"
			     And the "GradeLevelWhenAssessed" is "Twelfth Grade"
			     And the "AssessmentFamily" is "SAT"  
			     And the "ScaleScore" is "2060"
			     And the "PercentileRank" is "92"
			     # This might be a new field, maybe we can use ObjectiveAssessment.
			     And the "AssessmentSections" has the 3 sections
				     "AssessmentSection"= "Critical Reading"
				     "ScaleScore" = "680"
				     "PercentileRank" = "80"
				     "AssessmentSection"= "Mathematics"
				     "ScaleScore" = "780"
				     "PercentileRank" = "92"
				     "AssessmentSection"= "Writing"
				     "ScaleScore" = "600"
				     "PercentileRank" = "80"
Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "educator"      | "educator1234"      | "Educator"         |
| "administrator" | "administrator1234" | "IT Administrator" |
| "leader"        | "leader1234"        | "Leader"           |


# negative security case docuemented in another file.
