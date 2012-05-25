Feature: K-3 student list view

As a SEA/LEA user, I want to see the K-3 student list view
on SLI, so I could see elementary school students results

@integration @RALLY_US200 @RALLY_US147 @RALLY_US198
Scenario: Check K-3 Student Name
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "rbraverman" "rbraverman1234" for the "Simple" login page
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
And I select course "1st Grade Homeroom"
And I select section "Mrs. Braverman's Homeroom #38"
Then I should only see one view named "Early Literacy View"
Then I see a list of 25 students
And the list includes: "Mi-Ha Tran"
And I click on student "Bennie Cimmino"
And I view its student profile
And their grade is "1"
And Tab has a title named "Elementary School Overview"
 When I click on "Assessment" Tab
And Assessment History includes results for:
|Test         |
|READ 2.0  |
And the Assessment History for "READ 2.0" has the following entries:
|Date         |Grade  |Assessment Name            |Perf Level |
|2011-08-10   |1      |READ 2.0 Grade 1 BOY    |Level 2    |
|2012-03-01   |1      |READ 2.0 Grade 1 MOY    |Level 1    |