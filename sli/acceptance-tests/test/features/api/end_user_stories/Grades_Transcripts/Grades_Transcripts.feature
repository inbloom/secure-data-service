@wip
Feature: As a fourth grade teacher, for my class, I want to get the final grade for Session I in reading

Background: None

Scenario Outline:  As a fourth grade teacher, for my class, I want to get the final grade for Session I in reading
	Given  I am valid SEA/LEA end user <Username> with password <Password>
	And I have a Role attribute returned from the "SEA/LEA IDP"
	And the role attribute equals <AnyDefaultSLIRole>
	And I am authenticated on "SEA/LEA IDP"

	When I navigate to GET "/teachers/<'Ms. Jones' ID>"
	Then I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'Ms. Jones' ID>"
		And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
		And I should receive a link named "self" with URI "/teachers/<'Ms. Jones' ID>"
		
	When I navigate to "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
	Then I should receive a collection of 1 section links 
		And I should find section with uniqueSectionCode is "Section II"  with <'ImportantSection' ID>
		
	When I navigate to section with URI "/sections/<'ImportantSection' ID>"  
	And I navigate to GET "/student-section-association/<'Important_Section' ID>/targets"
		Then I should receive a collection of 1 student link
		And after resolution, I should receive a "Student" with ID <'John Doe' ID>
		
	#getting the final grade, enabling chaining 
	When I navigate to /sections/<'ImportantSection' ID>/sessions/<'Fall 2011' ID>
	Then I should recieve 1 session link
	And after resolution, I should receive a "Session" with ID <'Fall 2011' ID>  
	
	When I navigate to GET "/sessions/<'Fall 2011' ID>/courses"
	Then I should receive a collection of 1 course links
		And after resolution, I should receive a "Course" with ID <'Reading I' ID>
		And after resolution, I should receive a "Course" with ID  <'Writing I' ID>
		And after resolution, I should receive a "Course" with ID  <'Math I' ID>
		
	When I navigate to GET URI "/courses/<'Reading I' ID>"
	Then I should receive a return code of 200
	And "courseTitle" is "Reading 101"
	And "courseCode" is "R403"
	And "gradesOffered" is "Fourth Grade"
	And "subjectArea" is "Critical Reading"
	And "courseDescription" is "Critical Reading course for 4th graders"
	And "courseGPAApplicability" is "1"
	And I should receive a link named "getStudentTranscriptAssociations" with URI "/student-transcript-association/<'Reading I' ID>"
	
	When I navigate to "getCourseTranscriptAssociations" with URI "/student-transcript-associations/<'Reading I' ID>" 
	Then I should receive a collection of 7 studentTranscriptAssoc 
	
	When I navigate to GET "/student-transcript-association/<'Reading I' ID>"  And filter by studentId is  <'John Doe' ID> 
	Then  I should receive a collection of 1 student-transcript-association link 
	And after resolution, I should receive a "student-transcript-association" with ID "<'Most Recent CourseTranscript Association' ID>"
		  
	When I navigate to URI "/student-transcript-associations/<'Most Recent CourseTranscript Association' ID>"
		Then I get 1 student-transcript-association
		And "courseAttempedResult" is "Pass"
		And "creditsEarned" is "3.0"
		And "methodCreditEarned" is "Classroom"
		And "finalLetterGradeEarned" is "B"
		And "finalNumericGradeEarned" is "85"
		And "gradeType" is "Final"
			
	When I naviagte to GET "sessions/<'Fall 2011' ID>/studentAcademicRecordsAssociations/" and filter by studentId is  <'John Doe' ID>   
		Then I should receive 1 StudentAcademicRecordsAssociation link
		And after resolution, I should receive a "StudentAcademicRecordsAssociation" with ID <'SAR Fall 2011' ID>
		And the "GPAGivenGradingPeriod" is "3.2"
		And the "GPACumulative" is "3.5"
		And the "cumulativeCreditsEarned" is "120"
		And the "cumulativeGradePointAverage" is "3.5"
		And the "academicHonors.recognitiionType" is "Athletic awards"
	  
Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "educator"      | "educator1234"      | "Educator"         |
| "administrator" | "administrator1234" | "IT Administrator" |
| "leader"        | "leader1234"        | "Leader"           |


Scenario Outline:  As a AggregateViewer I should not see assessment data
	Given  I am valid SEA/LEA end user <Username> with password <Password>
	And I have a Role attribute returned from the "SEA/LEA IDP"
	And the role attribute equals <AnyDefaultSLIRole>
	And I am authenticated on "SEA/LEA IDP"

	When I navigate to GET "/teachers/<'Ms. Jones' ID>"
	Then I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'Ms. Jones' ID>"
		And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
		And I should receive a link named "self" with URI "/teachers/<'Ms. Jones' ID>"
		
	When I navigate to "getSections" with URI "/teacher-section-associations/<'Ms. Jones' ID>/targets"
	Then I should receive a collection of 1 section links 
		And I should find section with sectionName is "Section II"  with <'ImportantSection' ID>
				
	When I navigate to "getStudents" with URI "/student-section-associations/<'ImportantSection' ID>/targets"
	Then I should receive a collection of 1 student link
		And after resolution, I should receive a "Student" with ID <'John Doe' ID>
		And there is no link to sessions 
		And there is no link to courses
		And there is no link to studentAcademicRecordsAssociations

Examples:
| Username         | Password             | AnyDefaultSLIRole  |
| "aggregateViewer"| "aggregate1234"      | "AggregateViewer"         |
	 
