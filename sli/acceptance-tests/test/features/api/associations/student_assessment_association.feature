@wip
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

Background: Nothing yet

Scenario: Create a student-assessment-association
Given format "application/vnd.slc+json"
	And AssessmentTitle is "Mathematics Achievement Assessment Test"
	And AssessmentSubject is "Mathematics"
	And Student is <'Jane Doe' ID>
When I navigate to POST "/student-assessment-associations"
Then I should receive a return code of 201
	And I should receive a ID for the newly created student-assessment-association

Scenario: Read a student-assessment-association
Given format "application/vnd.slc+json"
When I navigate to GET Student Assessment Association for Student "Jane Doe" and AssessmentTitle "Mathematics Achievement Assessment Test"
Then I should receive a  return code of 2xx
	And I should receive 1 student-assessment-assoications
	And I should receive a link named "getStudent" with URI /students/<'Jane Doe' ID>
	And I should receive a link named "getAssessment" with URI /assessments/<'Mathematics Achievement Assessment Test' ID>
	And the administration date should be "09/15/2011"
	And the administration end date should be "12/15/2011"
	And the retest indicator should be 0
	And the Score Results should be 85

Scenario: Reading a student-assessment-association for a student
Given format "application/vnd.slc+json"
When I navigate to GET Student Assessment Associations for the Student "Jane Doe"
Then I should receive a return code of 2xx
	And I should receive 2 student-assessment-associations
	And I should receive a link named "getStudent" with URI /students/<'Jane Doe' ID>
	And I should receive a link named "getAssessment" with URI /assessments/<'Mathematics Achievement Assessment Test' ID>
	And I should receive a link named "getAssessment" with URI /assessments/<'Writing Advanced Placement Test' ID>

Scenario: Reading a student-assessment-association for a assessment
Given  format "application/vnd.slc+json"
When I navigate to GET Student Section Associations for the Assessment "Mathematics Achievement Assessment Test"
Then I should receive a return code of 2xx
	And I should 	receive 3 student-assessment-associations
	And I should receive a link named "getStudent" with URI /students/<'Jane Doe' ID>
	And I should receive a link named "getStudent" with URI /students/<'Albert Wright' ID>
	And I should receive a link named "getStudent" with URI /students/<'Kevin Smith' ID>
	And I should receive a link named "getAssessment" with URI /assessments/<'Mathematics Achievement Assessment Test' ID>

Scenario: Update a student-section-association 
Given  format "application/vnd.slc+json"
And I navigate to GET Student Assessment Associations for the AssessmentTitle "Mathematics Achievement Assessment Test" and Student "Jane Doe"
	And Assessment Subject is "Mathematics Level I"
When I set the Assessment Subject to "Mathematics Level II"
	And I navigate to PUT /student-assessment-associations/<the previous association ID>
Then I should get a return code of 2xx
	And I navigate to GET /student-assessment-associations/<the previous association ID>
	And the Assessment Subject should be "Mathematics Level II"

Scenario: Delete a student-assessment-association
Given format "application/vnd.slc+json"
And I navigate to DELETE /student-assessment-associations/<the previous association Id>
Then I should get a return code of 2xx
	And I navigate to GET /student-assessment-associations/<the previous association Id>
	And I should receive a return code of 404

