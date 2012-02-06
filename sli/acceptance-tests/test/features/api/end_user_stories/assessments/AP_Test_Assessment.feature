@wip
Feature: Get AP Tests

Background: None

Scenario Outline:  As a leader I want to get the following attrubutes for all the students in my school, AP English Score, AP Calculus Score, AP US History Score
	Given  I am valid SEA/LEA end user <Username> with password <Password>
	And I have a Role attribute returned from the "SEA/LEA IDP"
	And the role attribute equals <AnyDefaultSLIRole>
	And I am authenticated on "SEA/LEA IDP"

	When I navigate to GET "/school/<'Apple Elementary School' ID>"
	Then I should receive a link named "getSessions" with URI "/school-session-associations/<'Apple Elementary School' ID>/targets"

		
	When I navigate to  "getSessions" with URI "/school-session-associations/<'Apple Elementary School' ID>/targets"
	Then I should receive a collection of 2 session links
		And after resolution, I should receive a "Session" with ID <'Spring 2011' ID>
		And after resolution, I should receive a "Session" with ID  <'Fall 2011' ID>
	
	When I navigate to each session
	Then I should recieve a link named "getSections" 	with URI "/session-section-associations/<'Spring 2011' ID>/targets"
	
	When I navigate to "getSections" 	with URI "/session-section-associations/<'Spring 2011' ID>/targets"
		Then I should recieve a collection of 4 sections links
		And after resolution, I should recieve a "Section" with ID <'Section 1' ID>
		And after resolution, I should recieve a "Section" with ID <'Section 2' ID>
		
	When I navigate to each section	
	Then I search for assessments with URI "/section-assessment-associations/<'Each Section' ID>/targets?AssessmentFamilyHierarchyName="AP"&AssessmentTile="AP English"&AssessmentSubject="English"
	     Then  I should receive 1 assessment link
	        And after resolution, I should receive an "Assessment" with ID "<'AP English' ID>"
	       
        
	When I navigate to  GET "/assessments/<'AP English' ID>"
	    Then the "AssessmentTitle" is "AP English"
		     And the "AssessmentCategory" is "Advanced Placement"
		     And the "AssessmentSubjectType" is "English"
		     And the "GradeLevelAssessed" is "Twelfth Grade"
		     And the "LowestGradeLevelAssessed" is "Tenth Grade"
		     And the "AssessmentPerformanceLevel" has the 5 levels
				     "PerformanceLevel"= "1"
				     "PerformanceLevel"= "2"
				     "PerformanceLevel"= "3"
				     "PerformanceLevel"= "4"
				     "PerformanceLevel"= "5"
		     And I should recieve a link to "getStudentAssessmentAssociations" with URI "/student-assessment-associations/<'AP English' ID>"
	  
	  When I navigate to GET "/student-section-association/<'Each Section' ID>/targets"
		Then I should receive a collection of 5 student links
	 
	 Given I loop through the collection of student links   
		 When I navigate to GET "/student-assessment-associations/<'AP English' ID>"
		     And filter by <'Current_student' ID> 
		     Then I get 1 student-assessment-association 
			    	 And the "AdministrationDate" is "2012/01/10"
			     And the "AdministrationEndDate" is "2012/01/15"
			     And the "ScoreResult" is a random number between {1,5}
			     
Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "administrator" | "administrator1234" | "IT Administrator" |
| "leader"        | "leader1234"        | "Leader"           |


Scenario Outline:  As a AggregateViewer and Teacher I should not get Access to all students in a school
	 Given  I am valid SEA/LEA end user <Username> with password <Password>
	And I have a Role attribute returned from the "SEA/LEA IDP"
	And the role attribute equals <AnyDefaultSLIRole>
	And I am authenticated on "SEA/LEA IDP"

	When I navigate to GET "/school/<'Apple Elementary School' ID>"
	Then I should not receive  a link named "getSessions" with URI "/school-session-associations/<'Apple Elementary School' ID>/"
		And I should not receive  a link named "getSessions" with URI "/school-session-associations/<'Apple Elementary School' ID>/targets"
		
Examples:
| Username         | Password             | AnyDefaultSLIRole  |
| "aggregateViewer"| "aggregate1234"      | "AggregateViewer"  |
| "educator"      | "educator1234"      | "Educator"         |