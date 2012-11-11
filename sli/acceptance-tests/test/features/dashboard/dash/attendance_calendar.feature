Feature: Attendance Calendar

Background:
Given I have an open web browser
Given that dashboard has been authorized for all ed orgs
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go

@integration @RALLY_US200 @RALLY_US147 @RALLY_US198 @RALLY_US2253 @RALLY_US196 @RALLY_US2254
Scenario: View Matt Sollars
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
When I enter "Garry" into the "firstName" search box
And I click the search button
# US4437 -  Dashboard Temporary fix
And I select school "South Daybreak Elementary"
And I search by clicking on the go button
Then "1" results are returned in the page
#Then "2" results are returned in the page
And I click on student "Garry Kinsel"
When I click on "Attendance and Discipline" Tab
And I see the Attendance Calendar
And calendar month number "1" is "September" of "2011" 
And calendar month number "6" is "February" of "2012" 
And in calendar month number "1" in day "7" is "Unexcused Absence" with no events
And in calendar month number "2" in day "4" is "Tardy" with event "Missed school bus"
And in calendar month number "2" in day "21" is "Tardy" with event "Woke up Late"
And in calendar month number "2" in day "26" is "Excused Absence" with event "Absent excused"
And in calendar month number "3" in day "17" is "In Attendance" with no events
And in calendar month number "4" in day "19" is "Non School Day" with no events
And the following are the total attendance number shown in the calendar
|Attendance Category|Count  |
|Tardy              |7     |
|Unexcused Absence  |2      |
|Excused Absence    |3      |