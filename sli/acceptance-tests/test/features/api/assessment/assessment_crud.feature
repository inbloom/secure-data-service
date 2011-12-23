@wip
Feature: In order to manage assessments
As a SLI Application I wan t to be able to manage the basic model of assessments. This includes performing CRUD on Assessment entity. 
Also verify the correct links from assessment to student-assessment associations and family-assessment associations.

This is the data I am assuming for these tests
AssessmentTitle: Mathematics Achievement Assessment Test, Writing Advanced Placement Test
AssessmentSubject: Mathematics, Writing, Foreign Language
GradeLevelAssessed: Adult
ContentStandard: School Standard
AssessmentCategory: Achievement  Test,  Advanced Placement Test

## JSON

Scenario: Create an assessment 
	Given format "application/vnd.slc+json"
		And AssessmentTitle is <'Mathematics Achievement Assessment Test' ID>
		And AssessmentSection is <Mathematics>
		And AssessmentCategory is <Achievement Test>
		And GradeLevelAssessed is <Adult>
		And ContentStandard is <School Standard>
	When I navigate to POST "/assessments"
	Then I should receive a return code of 201
		And I should receive an ID for a newly created assessment

Scenario: Read an assessment by ID
	Given format "application/vnd.slc+json"
	When I navigate to GET /assessments/<'Writing Advanced Placement Test' ID>
	Then I should receive a return code of 2xx
		And I should receive 1 Assessments
		And the AssessmentTitle should be "Writing Advanced Placement Test" 
		And the AssessmentSubject should be "Writing"
		And the AssessmentCategory should be "Advanced Placement Test"

Scenario: Update an assessment by ID
	Given format "application/vnd.slc+json"
		And I navigate to GET /assessments/<'Writing Advanced Placement Test' ID>
	When I set the AssessmentTitle to "Advanced Placement Test - Subject: Writing"
		And I navigate to PUT /assessments/<the previous assessment Id>
	Then I should get a return code of 2xx
		And I navigate to GET /assessments/<the previous assessment id>
		And the AssessmentTitle should be "Advanced Placement Test - Subject: Writing"

Scenario: Delete an assessment by ID
	Given format "application/vnd.slc+json"
		And I navigate to GET /assessments/<'Writing Advanced Placement Test' ID>
	When I navigate to DELETE /assessments/<the previous association Id>
	Then I should get a return code of 2xx
		And I navigate to PUT /assessments/<the previous assessment Id>
		And I should receive a return code of 404

## LINKS
Scenario: Assessment entity links to StudentAssessment association and AssessmentFamily association
   Given format "application/json"
   When I navigate to Assessment <'Mathematics Achievement Assessment Test' ID>
   Then I should receive a return code of 2XX
      And I should receive a link named "getStudentAssessmentAssociations" with URI /student-assessment-associations/<'Mathematics Achievement Assessment Test' ID>
	And I should receive a link named "getStudentAssessments" with URI /student-assessment-associations/<'Mathematics Achievement Assessment Test'' ID>/targets
	And I should receive a link named "getAssessmentFamily" with URI /assessment-family-associations/<'Mathematics Achievement Assessment Test' ID>

## ERROR HANDLING
Scenario: Attempt to read a non-existent assessment
   Given format "application/json"
   When I navigate to GET assessment NonExistentAssessment
   Then I should receive a return code of 4XX

Scenario: Attempt to update a non-existent assessment
   Given format "application/json"
   When I navigate to PUT assessment NonExistentAssessment
   Then I should receive a return code of 4XX

Scenario: Attempt to delete a non-existent assessment
   Given format "application/json"
   When I navigate to DELETE assessment NonExistentAssessment
   Then I should receive a return code of 4XX
		
