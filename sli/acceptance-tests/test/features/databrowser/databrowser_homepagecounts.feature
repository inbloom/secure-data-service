@JIRA_DS1005
Feature: As a Data Browser User, I want to view the count of all students, staff, and teachers that have ever had an association with my EdOrgs and all the EdOrgs below them, and the number currently associated, so that I can use these counts to ensure my data imports loaded the correct number of records. 
Background:
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page


Scenario: Seeing the counts table on the homepage as a staff member
  When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
  When I navigated to the Data Browser Home URL
  Then I should see a table for counts
    And my counts for <type> are <ever> and <current>
    |type		|ever	|current|
    |Staff		|12	|12	|
    |Students		|124	|124	|
    |Teachers		|4	|4	|		
    |Non-Teachers	|8	|8	|




Scenario: Not seeing the counts table on the homepage as a student
  When I submit the credentials "carmen.ortiz" "carmen.ortiz1234" for the "Simple" login page
  When I navigated to the Data Browser Home URL
  Then I should not see a table for counts

