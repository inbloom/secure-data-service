
Feature: In order to manage students and assessments
As a SLI application I want to see analysis or scoring of a student's response on an assessment. 
I must be able to perform CRUD functions on student-assessment association.

This is the data I am assuming for these tests
AssessmentTitle: Mathematics Achievement Assessment Test, Writing Advanced Placement Test
AssessmentSubject: Mathematics, Writing, Foreign Language
GradeLevelAssessed: Adult
ContentStandard: School Standard
AssessmentCategory: Achievement  Test,  Advanced Placement Test
Student: Jane Doe

Background: Logged in as a super-user and using the small data set
	Given I am logged in using "demo" "demo1234"
	Given I have access to all students and assessments


Scenario: Create a student-assessment-association
Given format "application/json"
	And "assessmentID" is <'Mathematics Achievement  Assessment Test' ID>
	And "studentID" is <'Jane Doe' ID>
	And "administrationDate" is "2011-12-01"
	And "scoreResults" is "85"
	And "performanceLevel" is "3"
When I navigate to POST "/student-assessment-associations"
Then I should receive a return code of 201
	And I should receive a ID for the newly created student-assessment-association
    And the "administrationDate" should be "2011-12-01"
	And "scoreResults" should be "85"
	And "performanceLevel" should be "3"


Scenario: Read a student-assessment-association
Given format "application/json"
When I navigate to GET "/student-assessment-associations/<Student 'Jane Doe' and AssessmentTitle 'Writing Achievement Assessment Test' ID>"
Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/student-assessment-associations/<Student 'Jane Doe' and AssessmentTitle 'Writing Achievement Assessment Test' ID>"
	And I should receive a link named "getStudents" with URI "/students/<'Jane Doe' ID>"
	And I should receive a link named "getAssessment" with URI "/assessments/<'Writing Achievement Assessment Test' ID>"
	And the "administrationDate" should be "2011-09-15"
	And the "administrationEndDate" should be "2011-12-15"
	And the "retestIndicator" should be "1"
	And the "scoreResults" should be "85"
	And the "performanceLevel" should be "3"
	
	
Scenario: Reading a student-assessment-association for a student
Given format "application/json"
When I navigate to GET "/student-assessment-associations/<'Jane Doe' ID>"
Then I should receive a return code of 200
	And I should receive a collection of 2 student-assessment-association links
	And after resolution, I should get a link named "getStudent" with URI "/students/<'Jane Doe' ID>"
	And after resolution, I should get a link named "getAssessment" with URI "/assessments/<'Mathematics Achievement Assessment Test' ID>"
	And after resolution, I should get a link named "getAssessment" with URI "/assessments/<'Writing Achievement Assessment Test' ID>"

Scenario: Reading a student-assessment-association for a assessment
Given  format "application/json"
When I navigate to GET "/student-assessment-associations/<'Mathematics Achievement Assessment Test' ID>"
Then I should receive a return code of 200
	And I should receive a collection of 3 student-assessment-association links
	And after resolution, I should get a link named "getStudent" with URI "/students/<'Jane Doe' ID>"
	And after resolution, I should get a link named "getStudent" with URI "/students/<'Albert Wright' ID>"
	And after resolution, I should get a link named "getStudent" with URI "/students/<'Kevin Smith' ID>"
	And after resolution, I should get a link named "getAssessment" with URI "/assessments/<'Mathematics Achievement Assessment Test' ID>"

Scenario: Update a student-assessment-association 
Given  format "application/json"
When I navigate to GET "/student-assessment-associations/<'Student "Jane Doe" and AssessmentTitle "Mathematics Achievement  Assessment Test"' ID>"
	Then  the "scoreResults" should be "85"
When I set the ScoreResult to "95" 
	And I set the PerformanceLevel to"4"
	And I navigate to PUT "/student-assessment-associations/<'Student "Jane Doe" and AssessmentTitle "Mathematics Achievement  Assessment Test"' ID>"
Then I should receive a return code of 204
When I navigate to GET "/student-assessment-associations/<'Student "Jane Doe" and AssessmentTitle "Mathematics Achievement  Assessment Test"' ID>"
	Then the "scoreResults" should be "95"
	And the "performanceLevel" should be "4"


Scenario: Delete a student-assessment-association
Given format "application/json"
And I navigate to DELETE "/student-assessment-associations/<AssessmentTitle 'French Advanced Placement' and Student 'Joe Brown' ID>"
Then I should receive a return code of 204
	And I navigate to GET "/student-assessment-associations/<AssessmentTitle 'French Advanced Placement' and Student 'Joe Brown' ID>"
	And I should receive a return code of 404

### Error Handling

Scenario: Attempt to read a non-existing student assessment association
	Given format "application/json"
	When I navigate to GET "/student-assessment-associations/<'NonExistence' ID>"
	Then I should receive a return code of 404
	
Scenario: Attempt to update a non-existing student assessment association
	Given format "application/json"
	When I navigate to PUT "/student-assessment-associations/<'NonExistence' ID>"
	Then I should receive a return code of 404
	
Scenario: Delete a nonexistent student-assessment-association
Given format "application/json"
And I navigate to DELETE "/student-assessment-associations/<'NonExistence' ID>"
Then I should receive a return code of 404

