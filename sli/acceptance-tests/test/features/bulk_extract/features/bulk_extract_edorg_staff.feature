Feature: A bulk extract is triggered and educationOrganization, school, staff, and teacher entities are verified

Scenario Outline: Verify educationOrganization, school, staff, and teacher entities were correctly extracted
   When I retrieve the path to the extract file for the tenant "<tenant>"
   And a "<entity>" extract file exists
   And a the correct number of "<entity>" was extracted from the database
   And a "<entity>" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "<user>", a "<role>" for "<realm>" in tenant "<tenant>", that lasts for "300" seconds
   Then a "<entity>" was extracted in the same format as the api
   
	Examples:
    | entity                                | user       | role             | realm       | tenant |
	| educationOrganization                 | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| school                                | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| staff                                 | jstevenson | IT Administrator | IL-Daybreak | Midgar |
	| teacher                               | jstevenson | IT Administrator | IL-Daybreak | Midgar |