Feature: Dashboard Builder Configuration

As a admin, I'm able to configure dashboard

Background:
Given I have an open web browser
Given that dashboard has been authorized for all ed orgs
When I navigate to the Dashboard home page

@integration @RALLY_US2276 @RALLY_US3376 @RALLY_US3480
Scenario: Add a Page
# STATE IT admin logs in
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
 Then I should be redirected to the Dashboard landing page
 When I navigate to the Dashboard Builder page

And I click on "School" Profile Builder
And I add a Page named "School Tab"
And I add an available panel named "sectionList"
And I add an available panel named "teacherList"
And I click the Publish Layout button
And in "School Tab" Page, it has the following panels: "sectionList;teacherList"

And I click on "Section" Profile Builder
And I add a Page named "LOS 2"
And I add an available panel named "listOfStudents"
And I click the Publish Layout button
And in "Section Tab" Page, it has the following panels: "listOfStudents"
