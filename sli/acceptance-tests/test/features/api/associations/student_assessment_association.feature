@wip
Feature:As a SLI application I want to see analysis or scoring of a student's response on an assessment.
  I must be able to perform CRUD functions on student-assessment association.
  This represents the analysis or scoring of a student's response on an assessment.
  The analysis results in a value that represents a students performance on a set of items on a test.

This is the data I am assuming for these tests
AssessmentTitle: Mathematics Achievement Assessment Test, Writing Advanced Placement Test
AcademicSubject: Mathematics, Writing, Foreign Language
ContentStandard: School Standard
AssessmentCategory: Achievement  Test,  Advanced Placement Test
Student: Jane Doe

Background: Nothing yet

Scenario: Create a student-assessment-association
Given format "application/json"
	And Assessment is <'Mathematics Achievement Assessment Test' ID>
	And Student is <'Jane Doe' ID>
	And AdministrationDate is "12/01/2011"
	And ScoreResults is "85"
	And PerformanceLevel is 3
When I navigate to POST "/student-assessment-associations"
Then I should receive a return code of 201
	And I should receive a ID for the newly created student-assessment-association
When I navigate to GET /teacher-section-associations/<'newly created student-assessment-association' ID>
Then the AdministrationDate is "12/01/2011"
    And the ScoreResult is "85"
    And the PerformanceLevel is 3

Scenario: Read a student-assessment-association
Given format "application/json"
When I navigate to GET student-assessment-association for <'Student "Jane Doe" and AssessmentTitle "Writing Achievement Assessment Test" '  ID>
Then I should receive a  return code of 200
	And I should receive 1 student-assessment-association
    # the link name is not plural because an assessment only has one link going to each of the associated entity
	And I should receive a link named "getStudent" with URI /students/<'Jane Doe' ID>
	And I should receive a link named "getAssessment" with URI /assessments/<'Mathematics Advanced Placement Test' ID>
    And I should receive a link named "self" with URI /student-assessment-association/<'self' ID>
	And the AdministrationDate should be "09/15/2011"
	And the AdministrationEnd date should be "12/15/2011"
	And the RetestIndicator should be 1
	And the ScoreResults should be 85
	And the PerformanceLevel should be 3


Scenario: Reading a student-assessment-association for a student
Given format "application/json"
When I navigate to GET student-assessment-associations for the Student <'Jane Doe' ID>
Then I should receive a return code of 200
    # the line below appears incomplete, but is as intended
	And I should receive a collection of 2 student-assessment-associations links that resolve to
	And I should receive a link named "getStudents" with URI /students/<'Jane Doe' ID>
	And I should receive a link named "getAssessments" with URI /assessments/<'Mathematics Advanced Placement Test' ID>
	And I should receive a link named "getAssessments" with URI /assessments/<'Writing Advanced Placement Test' ID>

Scenario: Reading a student-assessment-association for a assessment
Given  format "application/json"
When I navigate to GET student-assessment-associations for the Assessment <'Mathematics Achievement Assessment Test' ID>
Then I should receive a return code of 200
	And I should receive a collection of 3 student-assessment-associations links that resolve to
	And I should receive a link named "getStudents" with URI /students/<'Jane Doe' ID>
	And I should receive a link named "getStudents" with URI /students/<'Albert Wright' ID>
	And I should receive a link named "getStudents" with URI /students/<'Kevin Smith' ID>
	And I should receive a link named "getAssessments" with URI /assessments/<'Mathematics Achievement Assessment Test' ID>

Scenario: Update a student-section-association
Given  format "application/vnd.slc+json"
And I navigate to GET Student Assessment Associations for the <'AssessmentTitle "Mathematics Achievement Assessment Test" and Student "Jane Doe"' ID>
	And ScoreResult is "85"
When I set the ScoreResult to "95"
	And I set the PerformanceLevel to 4
	And I navigate to PUT /student-assessment-associations/<the previous association ID>
Then I should receive a return code of 200
	And I navigate to GET /student-assessment-associations/<the previous association ID>
	And the ScoreResult should be "85"
	And the PerformanceLevel should be 4

Scenario: Delete a student-assessment-association
Given format "application/vnd.slc+json"
And I navigate to DELETE /student-assessment-associations/<'AssessmentTitle "French Advanced Placement" and Student "Joe Brown"' Id>
Then I should get a return code of 201
	And I navigate to GET /student-assessment-associations/<the previous association Id>
	And I should receive a return code of 404

Scenario: Delete a nonexistent student-assessment-association
Given format "application/vnd.slc+json"
And I navigate to DELETE /student-assessment-associations/<NonExistence Id>
Then I should get a return code of 404

