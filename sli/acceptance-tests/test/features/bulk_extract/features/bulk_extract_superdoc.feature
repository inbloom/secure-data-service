Feature: A bulk extract is triggered and superdoc and subdoc entities are verified

Scenario Outline: Verify simple entities were correctly extracted
   When I retrieve the path to the extract file for the tenant "<tenant>"
   And a "<entity>" extract file exists
   And a the correct number of "<entity>" was extracted from the database
   And a "<entity>" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "<user>", a "<role>" for "<realm>" in tenant "<tenant>", that lasts for "300" seconds
   Then a "<entity>" was extracted in the same format as the api
   
	Examples:
    | entity                                | user       | role             | realm       | tenant |
	| student                               | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| studentParentAssociation              | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| studentDisciplineIncidentAssociation  | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| studentAssessment                     | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| studentAssessmentItem                 | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| studentObjectiveAssessment            | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| assessment                            | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| assessmentItem                        | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| objectiveAssessment                   | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| section                               | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| studentSectionAssociation             | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| gradebookEntry                        | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| teacherSectionAssociation             | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| program                               | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| studentProgramAssociation             | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| cohort                                | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| studentCohortAssociation              | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| yearlyTranscript                      | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| studentAcademicRecord                 | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| grade                                 | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| reportCard                            | jstevenson | IT Administrator | IL-Daybreak | Midgar |
