Feature: A bulk extract is triggered and simple one-to-one entities are verified

Scenario Outline: Verify simple entities were correctly extracted
   When I retrieve the path to the extract file for the tenant "<tenant>"
   And a "<entity>" extract file exists
   And a the correct number of "<entity>" was extracted from the database
   And a "<entity>" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "<user>", a "<role>" for "<realm>" in tenant "<tenant>", that lasts for "300" seconds
   Then a "<entity>" was extracted in the same format as the api
   
	Examples:
    | entity                                | user       | role             | realm       | tenant |
	| competencyLevelDescriptor             | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| course                                | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| courseOffering                        | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| disciplineIncident                    | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| disciplineAction                      | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| gradingPeriod                         | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| graduationPlan                        | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| learningObjective                     | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| learningStandard                      | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| parent                                | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| session                               | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| staffEducationOrganizationAssociation | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| staffCohortAssociation                | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| staffProgramAssociation               | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| studentCompetency                     | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| studentCompetencyObjective            | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| studentSchoolAssociation              | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| teacherSchoolAssociation              | jstevenson | IT Administrator | IL-Daybreak | Midgar |