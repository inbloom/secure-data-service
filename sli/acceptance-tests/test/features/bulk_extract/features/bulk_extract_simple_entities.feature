Feature: A bulk extract is triggered and simple one-to-one entities are verified

Scenario Outline: Verify simple entities were correctly extracted
   When I get the path to the extract file for the tenant "<tenant>" and application with id "<appId>"
   And a "<entity>" extract file exists
   And a the correct number of "<entity>" was extracted from the database
   And a "<entity>" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "<user>", a "<role>" for "<realm>" in tenant "<tenant>", that lasts for "300" seconds
   Then a "<entity>" was extracted in the same format as the api
   
	Examples:
    | entity                                | user       | role             | realm       | tenant | appId                                |
    | attendance                            | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| competencyLevelDescriptor             | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| course                                | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| courseOffering                        | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| courseTranscript                      | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| disciplineIncident                    | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| disciplineAction                      | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| gradingPeriod                         | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| graduationPlan                        | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| learningObjective                     | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| learningStandard                      | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| parent                                | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| session                               | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| staffEducationOrganizationAssociation | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| staffCohortAssociation                | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| staffProgramAssociation               | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| studentCompetency                     | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
    | studentGradebookEntry                 | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| studentCompetencyObjective            | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| studentSchoolAssociation              | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
	| teacherSchoolAssociation              | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |