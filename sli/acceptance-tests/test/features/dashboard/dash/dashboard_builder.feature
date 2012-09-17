Feature: Dashboard Builder Configuration

As a admin, I'm able to configure dashboard

Background:
Given I have an open web browser
Given that dashboard has been authorized for all ed orgs
When I navigate to the Dashboard home page

@integration @RALLY_US2276 @RALLY_US3376
Scenario: Add a Page
# STATE IT admin logs in
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
 Then I should be redirected to the Dashboard landing page
 When I navigate to the Dashboard Builder page
And I click on "School" Profile Builder
And I add a Page named "State Tab"
And I upload custom json for it
And in "Subjects and Courses" Page, it has the following panels: "sectionList"
And I logout
 ## STATE IT admin logs out
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
 Then I should be redirected to the Dashboard landing page
When I navigate to the Dashboard Builder page
And I click on "School" Profile Builder
And I add a Page named "School Tab"
And I add an available panel named "sectionList"
And I add an available panel named "teacherList"
And in "School Tab" Page, it has the following panels: "sectionList;teacherList"
And I delete an available panel named "teacherList"
And in "School Tab" Page, it has the following panels: "sectionList"
And I click on "Section" Profile Builder
And I add a Page named "LOS 2"
And I add an available panel named "listOfStudents"
When I navigate to the Dashboard home page
Then I should be redirected to the Dashboard landing page
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
And I click on the go button
And I see the following tab order "Subjects and Courses;Teachers;State Tab;School Tab"
And I view the School Profile
And there are "4" Tabs
And Tab has a title named "School Tab"
And I click on "School Tab" Tab
And I click on subject "English Language and Literature"
And I click on course "8th Grade English"
And I click on section "8th Grade English - Sec 6"
And I view its section profile
And there are "2" Tabs
And Tab has a title named "LOS 2"
And I click on "LOS 2" Tab
Then I see a list of 28 students
And I logout
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "manthony" "manthony1234" for the "Simple" login page
 Then I should be redirected to the Dashboard landing page
When I select ed org "Sunset School District 4526"
When I select school "Sunset Central High School"
When I select course "Choose One"
And I click on the go button
And I view the School Profile
And there are "3" Tabs
And I see the following tab order "Subjects and Courses;Teachers;State Tab"
And I click on "Subjects and Courses" Tab
And I click on subject "Miscellaneous"
And I click on course "A.P. Calculus"
And I click on section "A.P. Calculus Sec 201"
And I view its section profile
And there are "1" Tabs
And Tab has a title named "List of Students"
And I logout
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
 Then I should be redirected to the Dashboard landing page
#TODO : drag and drop not working on jenkins
#And I move Page "School Tab" to become Page Number "0"
#When I navigate to the Dashboard home page
#When I select ed org "Daybreak School District 4529"
#When I select school "East Daybreak Junior High"
#And I click on the go button
#And I see the following tab order "School Tab;Subjects and Courses"
#When I navigate to the Dashboard Builder page
#And I click on "School" Profile Builder
#And I see the following page order "School Tab;Subjects and Courses" in the builder
#end of TODO
When I enter the Configuration Area
Then I am authorized to the Configuration Area
And I paste Valid json config into the text box
And click Save
Then I should be shown a success message
And I logout
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
 Then I should be redirected to the Dashboard landing page
 When I navigate to the Dashboard Builder page
And I click on "School" Profile Builder
And I see the following page order "Subjects and Courses;Teachers;State Tab" in the builder
And I delete Page "State Tab"
 And I logout