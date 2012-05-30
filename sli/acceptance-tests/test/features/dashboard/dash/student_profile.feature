Feature:  Student Profile View

As a teacher in a school district, I want to click on a student and be directed to their profile page.

Background:
Given I have an open web browser

@RALLY_US200 @RALLY_US147 @RALLY_US198
Scenario: View student's profile 
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
#Lozenges check
And the following students have "ELL" lozenges: "Matt Sollars;Odysseus Merrill;Hoyt Hicks;Brielle Klein;Patricia Harper"
# Lozenges check on LOS
Then there is no lozenges for student "Jeanette Graves"
#And I see a header on the page that has the text "Logout"
#And I see a footer on the page that has the text "Copyright"
And I click on student "Kimberley Pennington"
And I view its student profile
And their name shown in profile is "Kimberley Yuli Pennington Jr"
And their id shown in proflie is "437680177"
And their grade is "8"
And the teacher is "Mrs Linda Kim"
And the class is "8th Grade English - Sec 6"
And the lozenges count is "1"
And the lozenges include "ELL"
#And I see a header on the page that has the text "Logout"
#And I see a footer on the page that has the text "Copyright"
#Display hide tabs based on grades
And there are "4" Tabs
And Tab has a title named "Middle School Overview"
And Tab has a title named "Attendance and Discipline"
And Tab has a title named "Assessments"
And Tab has a title named "Grades and Credits"
#Check Enrollment
And Student Enrollment History has the following entries:
|Year   |School                     |Gr|Entry Date |Entry Type     |Transfer |Withdraw Date|Withdraw Type    |
|<empty>|East Daybreak Junior High  |8 |2012-01-01 |<empty>        |<empty>  |<empty>      |<empty>          |
|<empty>|<empty>                    |8 |2011-01-01 |<empty>        |<empty>  |2011-12-31   |<empty>          |
|<empty>|<empty>                    |8 |2011-01-01 |<empty>        |<empty>  |2011-12-31   |<empty>          |

@RALLY_US200 @RALLY_US147 @RALLY_US198
Scenario: Student with no grade level
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
And I click on student "Marvin Miller"
And I view its student profile
And their name shown in profile is "Marvin Miller Jr"
And their id shown in proflie is "453827070"
And their grade is "O"
And the teacher is "Mrs Linda Kim"
And the class is "8th Grade English - Sec 6"
And the lozenges count is "0"
#Display hide tabs based on grades
#And there are "8" Tabs
And Tab has a title named "Elementary School Overview"
And Tab has a title named "Middle School Overview"
And Tab has a title named "High School Overview"
#Check Enrollment
And Student Enrollment History has the following entries:
|Year   |School                     |Gr|Entry Date |Entry Type     |Transfer |Withdraw Date|Withdraw Type    |
|<empty>|East Daybreak Junior High  |O |2012-01-01 |<empty>        |<empty>  |2012-06-02   |<empty>          |
|<empty>|<empty>                    |O |2011-01-01 |<empty>        |<empty>  |2011-12-31   |<empty>          |
	
