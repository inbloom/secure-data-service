@smoke
Feature: Smoked Dashboard Tests
As a developer, I want a smoke test for the dashboard component that finishes in a few minutes, using hickory wood chips for the smoke flavor.

Scenario: MEGA SHMOKE - IT Admin 
#Upload valid config file
Given I have an open web browser
Given that dashboard has been authorized for all ed orgs
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
When I enter the Configuration Area
Then I am authorized to the Configuration Area
And the title of the page is "SLC"
And I paste Valid json config into the text box
And click Save
Then I should be shown a success message
#Login with District Level IT admin
When I navigate to the Dashboard home page
When I look in the ed org drop-down
Then I only see "Daybreak School District 4529"
When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I see these values in the drop-down: "Daybreak Central High;East Daybreak Junior High;South Daybreak Elementary"
When I select school "South Daybreak Elementary"
And I select course "1st Grade Homeroom"
And I select section "Mrs. Braverman's Homeroom #38"
Then I see a list of 25 students
And the following students have "504" lozenges: "Archie Forker"
And the count for id "attendances.absenceCount" for student "Mi-Ha Tran" is "1"
And the class for id "attendances.absenceCount" for student "Mi-Ha Tran" is "color-widget-green"
And the count for id "attendances.tardyCount" for student "Mi-Ha Tran" is "0"
And the class for id "attendances.tardyCount" for student "Mi-Ha Tran" is "color-widget-darkgreen"

Scenario: MEGA SHMOKE - Teacher
#Selecting classes on LOS
Given I have an open web browser
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
When I look in the ed org drop-down
Then I see these values in the drop-down: "Daybreak School District 4529"

When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I only see "East Daybreak Junior High"

When I select school "East Daybreak Junior High"
And I look in the course drop-down
Then I see these values in the drop-down: "6th Grade English;6th Grade Math;7th Grade English;7th Grade Math;8th Grade English"

When I select course "8th Grade English"
Then I see these values in the drop-down: "8th Grade English - Sec 6"
When I select section "8th Grade English - Sec 6"
Then I see a list of 28 students

Then I should have a dropdown selector named "viewSelect"
And I should have a selectable view named "Middle School ELA View"

#Scenario: Monster attendance test
And I select view "Middle School ELA View"
Then the table includes header "Attendance"
And I should see a table heading "Tardy Count"
And the count for id "attendances.tardyCount" for student "Rudy Bedoya" is "0"
And the class for id "attendances.tardyCount" for student "Rudy Bedoya" is "color-widget-darkgreen"
And I should see a table heading "Tardy Rate %"
And the count for id "attendances.tardyRate" for student "Samantha Scorzelli" is "0"
And the class for id "attendances.tardyRate" for student "Samantha Scorzelli" is "color-widget-darkgreen"
And I should see a table heading "Attendance Rate %"
And the count for id "attendances.attendanceRate" for student "Dominic Brisendine" is "100"
And the class for id "attendances.attendanceRate" for student "Dominic Brisendine" is "color-widget-darkgreen"
And the count for id "attendances.attendanceRate" for student "Lashawn Aldama" is "99"
And the class for id "attendances.attendanceRate" for student "Lashawn Aldama" is "color-widget-darkgreen"
And the count for id "attendances.attendanceRate" for student "Karrie Rudesill" is "100"
And the class for id "attendances.attendanceRate" for student "Karrie Rudesill" is "color-widget-darkgreen"
And the count for id "attendances.attendanceRate" for student "Lashawn Taite" is "86"
And the class for id "attendances.attendanceRate" for student "Lashawn Taite" is "color-widget-red"
And I should see a table heading "Absence Count"
And the count for id "attendances.absenceCount" for student "Dominic Brisendine" is "0"
And the class for id "attendances.absenceCount" for student "Dominic Brisendine" is "color-widget-darkgreen"
And the count for id "attendances.absenceCount" for student "Felipe Cianciolo" is "6"
And the class for id "attendances.absenceCount" for student "Felipe Cianciolo" is "color-widget-yellow"
And the count for id "attendances.absenceCount" for student "Merry Mccanse" is "5"
And the class for id "attendances.absenceCount" for student "Merry Mccanse" is "color-widget-green"

#Lozenges check
And the following students have "ELL" lozenges: "Matt Sollars;Malcolm Costillo;Felipe Cianciolo"
Then there is no lozenges for student "Tomasa Cleaveland"

#Student Profile
And I click on student "Alton Maultsby"
And I view its student profile
And their name shown in profile is "Alton Maultsby Jr"
And their id shown in proflie is "800000016"
And their grade is "8"
And the class is "!"
And the lozenges count is "1"

#Display hide tabs based on grades
And there are "4" Tabs
And Tab has a title named "Middle School Overview"
And Tab has a title named "Attendance and Discipline"
And Tab has a title named "Assessments"
And Tab has a title named "Grades and Credits"
And in "Middle School Overview" tab, there are "2" Panels
And in "Attendance and Discipline" tab, there are "2" Panels
And in "Assessments" tab, there are "2" Panels
And in "Grades and Credits" tab, there are "2" Panels
