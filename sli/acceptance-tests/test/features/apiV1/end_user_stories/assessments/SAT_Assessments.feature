@RALLY_US510 @RALLY_US632
Feature: SAT Scores 

Background: None

@wip
Scenario Outline:  As a teacher for my class I want to get the most recent values SAT including Critical Reading, Writing, Mathematics
	Given  I am a valid SEA/LEA end user <Username> with password <Password>
    And I have a Role attribute returned from the "SLI"
    And the role attribute equals <AnyDefaultSLIRole>
    And I am authenticated on "SLI"
    
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
		And filter by  "assessmentFamilyHierarchyName" = "SAT" 
		And "sort-by" = "assessmentPeriodDescriptor.beginDate"
		 And "sort-order" = "descending" 
		 And "start-index" = "0" 
		 And "max-results" = "1"
	     Then  I should receive a collection of 1 assessment link
	        And I should find Assessment with "<'Most recent SAT' ID>"
        
	When I navigate to GET "/assessments/<'Most recent SAT' ID>"
	    Then I should receive 1 assessment  
		     And  the "assessmentTitle" is "SAT"
		     And the "assessmentCategory" is "College Addmission Test"
		     And the "gradeLevelAssessed" is "Twelfth grade"
		     And the "lowestGradeLevelAssessed" is "Eleventh grade"
		     And the "assessmentFamilyHierarchyName" is "SAT"
		     And the "maxRawScore" is 2400
		     And the "minRawScore" is 600
		     And the assessment has an array of 3 objectiveAssessments
		     And the first one is "objectiveAssessment.identificationCode" = "SAT-Writing"
		     And the first one is "objectiveAssessment.percentOfAssessment" = 33
		     And the first one is "objectiveAssessment.maxRawScore" = 800
		     And the second one is "objectiveAssessment.identificationCode" = "SAT-Math"
		     And the second one is "objectiveAssessment.percentOfAssessment" = 33
		     And the second one is "objectiveAssessment.maxRawScore" = 800
		     And the third one is "objectiveAssessment.identificationCode" = "SAT-Critical Reading"
		     And the third one is "objectiveAssessment.percentOfAssessment" = 33
		     And the third one is "objectiveAssessment.maxRawScore" = 800
	  
	When I navigate to "getStudents" with URI "/student-section-associations/<'ImportantSection' ID>/targets"
	Then I should receive a collection of 5 student links
	 
	 When I navigate to GET "/student-assessment-associations/<'Most recent SAT' ID>"
	     Then I get a collection of 20 student-assessment-associations links 
	     When I navigate to GET "/student-assessment-associations/<'Most recent SAT' ID>" and filter by studentId is "<'Current_student' ID>"
	         Then I get a collection of 1 student-assessment-associations links 
	         And  I should receive a "studentAssessmentAssociation" with ID "<'Most Recent Assessment Association' ID>"
	         
	     When I navigate to URI /student-assessment-associations/"<'Most Recent Assessment Association' ID>"
	     Then I get 1 student-assessment-association
			    	 And the "administrationDate" is "2011-05-10"
			     And the "gradeLevelWhenAssessed" is "Twelfth grade"
			     And the "scoreResults" has the 2 entries
			     And the first one is "scoreResults.assessmentReportingMethod" = "Scale score"	
			     And the first one is "scoreResults.result" = "2060" 
			     And the second one is "scoreResults.assessmentReportingMethod" = "Percentile"	
			     And the second one is "scoreResults.result" = "92" 
				And the "studentObjectiveAssessments" has the 3 entries
				 And the first one is "studentObjectiveAssessments.objectiveAssessment.identificationCode" = "SAT-Writing"
				 And the first one is "studentObjectiveAssessments.scoreResults.assessmentReportingMethod" = "Scale score"	
			     And the first one is "studentObjectiveAssessments.scoreResults.result" = "680"
			     And the first one is "studentObjectiveAssessments.scoreResults.assessmentReportingMethod" = "Percentile"	
			     And the first one is "studentObjectiveAssessments.scoreResults.result" = "80"
			     And the second one is "studentObjectiveAssessments.objectiveAssessment.identificationCode" = "SAT-Math"
				 And the second one is "studentObjectiveAssessments.scoreResults.assessmentReportingMethod" = "Scale score"	
			     And the second one is "studentObjectiveAssessments.scoreResults.result" = "680"
			     And the second one is "studentObjectiveAssessments.scoreResults.assessmentReportingMethod" = "Percentile"	
			     And the second one is "studentObjectiveAssessments.scoreResults.result" = "80"
			     And the third one is "studentObjectiveAssessments.objectiveAssessment.identificationCode" = "SAT-Reading"
				 And the third one is "studentObjectiveAssessments.scoreResults.assessmentReportingMethod" = "Scale score"	
			     And the third one is "studentObjectiveAssessments.scoreResults.result" = "680"
			     And the third one is "studentObjectiveAssessments.scoreResults.assessmentReportingMethod" = "Percentile"	
			     And the third one is "studentObjectiveAssessments.scoreResults.result" = "80"

Examples:
| Username        | Password            | AnyDefaultSLIRole  |
#| "educator"      | "educator1234"      | "Educator"         |
#| "administrator" | "administrator1234" | "IT Administrator" |
#| "leader"        | "leader1234"        | "Leader"           |


# negative security case docuemented in another file.
