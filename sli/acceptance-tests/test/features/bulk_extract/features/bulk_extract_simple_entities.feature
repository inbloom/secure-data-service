Feature: A bulk extract is triggered and simple one-to-one entities are verified

Scenario: Verify a cohort was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "cohort" extract file exists
   And a the correct number of "cohort" was extracted from the database
   And a "cohort" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "sbantu", a "Leader" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "cohort" was extracted in the same format as the api

Scenario: Verify a competencyLevelDescriptor was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "competencyLevelDescriptor" extract file exists
   And a the correct number of "competencyLevelDescriptor" was extracted from the database
   And a "competencyLevelDescriptor" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "competencyLevelDescriptor" was extracted in the same format as the api

Scenario: Verify a course was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "course" extract file exists
   And a the correct number of "course" was extracted from the database
   And a "course" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "course" was extracted in the same format as the api

Scenario: Verify a courseOffering was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "courseOffering" extract file exists
   And a the correct number of "courseOffering" was extracted from the database
   And a "courseOffering" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "courseOffering" was extracted in the same format as the api

Scenario: Verify a disciplineIncident was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "disciplineIncident" extract file exists
   And a the correct number of "disciplineIncident" was extracted from the database
   And a "disciplineIncident" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "disciplineIncident" was extracted in the same format as the api

Scenario: Verify a disciplineAction was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "disciplineAction" extract file exists
   And a the correct number of "disciplineAction" was extracted from the database
   And a "disciplineAction" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "disciplineAction" was extracted in the same format as the api

Scenario: Verify a gradingPeriod was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "gradingPeriod" extract file exists
   And a the correct number of "gradingPeriod" was extracted from the database
   And a "gradingPeriod" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "gradingPeriod" was extracted in the same format as the api

Scenario: Verify a graduationPlan was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "graduationPlan" extract file exists
   And a the correct number of "graduationPlan" was extracted from the database
   And a "graduationPlan" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "graduationPlan" was extracted in the same format as the api

Scenario: Verify a learningObjective was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "learningObjective" extract file exists
   And a the correct number of "learningObjective" was extracted from the database
   And a "learningObjective" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "learningObjective" was extracted in the same format as the api

Scenario: Verify a learningStandard was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "learningStandard" extract file exists
   And a the correct number of "learningStandard" was extracted from the database
   And a "learningStandard" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "learningStandard" was extracted in the same format as the api

Scenario: Verify a parent was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "parent" extract file exists
   And a the correct number of "parent" was extracted from the database
   And a "parent" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "rrogers", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "parent" was extracted in the same format as the api

Scenario: Verify a session was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "session" extract file exists
   And a the correct number of "session" was extracted from the database
   And a "session" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "session" was extracted in the same format as the api

Scenario: Verify a staffEducationOrganizationAssociation was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "staffEducationOrganizationAssociation" extract file exists
   And a the correct number of "staffEducationOrganizationAssociation" was extracted from the database
   And a "staffEducationOrganizationAssociation" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "staffEducationOrganizationAssociation" was extracted in the same format as the api

Scenario: Verify a staffProgramAssociation was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "staffProgramAssociation" extract file exists
   And a the correct number of "staffProgramAssociation" was extracted from the database
   And a "staffProgramAssociation" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "staffProgramAssociation" was extracted in the same format as the api

Scenario: Verify a studentCompetency was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "studentCompetency" extract file exists
   And a the correct number of "studentCompetency" was extracted from the database
   And a "studentCompetency" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "studentCompetency" was extracted in the same format as the api

Scenario: Verify a studentCompetencyObjective was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "studentCompetencyObjective" extract file exists
   And a the correct number of "studentCompetencyObjective" was extracted from the database
   And a "studentCompetencyObjective" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "studentCompetencyObjective" was extracted in the same format as the api

Scenario: Verify a studentSchoolAssociation was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "studentSchoolAssociation" extract file exists
   And a the correct number of "studentSchoolAssociation" was extracted from the database
   And a "studentSchoolAssociation" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "studentSchoolAssociation" was extracted in the same format as the api

Scenario: Verify a teacherSchoolAssociation was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "teacherSchoolAssociation" extract file exists
   And a the correct number of "teacherSchoolAssociation" was extracted from the database
   And a "teacherSchoolAssociation" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "teacherSchoolAssociation" was extracted in the same format as the api



