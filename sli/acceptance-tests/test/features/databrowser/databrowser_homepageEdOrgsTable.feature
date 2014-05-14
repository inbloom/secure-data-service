@JIRA_DS991

Feature: As a Data Browser User, I want to view the EdOrgs table in the homepage.
Background:
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page



Scenario: Seeing the EdOrgs table on the homepage as a staff member
When I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
When I navigated to the Data Browser Home URL
Then I should see a table for EdOrgs 
And I should see the values for EdOrgs Table
      |EdOrgsName	|EdOrgsId	|EdOrgsType| EdOrgsParent|     
      |Daybreak School District 4529    |bd086bae-ee82-4cf2-baf9-221a9407ea07   |educationOrganization    |  Illinois State Board of Education     |


Scenario: Seeing EdOrgs on the homepage for multiple Parents School and Education Service Center
Given I change the isAdminRole flag for role "Educator" to in the realm "Daybreak" to be "true"

And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
Then I should be redirected to the Data Browser home page

Then I should see a table for EdOrgs 
And I should see the values for EdOrgs Table
      |EdOrgsName	|EdOrgsId	|EdOrgsType| EdOrgsParent|     
      |Sunset Central High School	|6756e2b9-aba1-4336-80b8-4a5dde3c63fe	|educationOrganization	 |Sunset School District 4526|
      |Daybreak Central High	        |92d6d5a0-852c-45f4-907a-912752831772	|educationOrganization	 | Daybreak School District 4529|

Then I change the isAdminRole flag for role "Educator" to in the realm "Daybreak" to be "false"





