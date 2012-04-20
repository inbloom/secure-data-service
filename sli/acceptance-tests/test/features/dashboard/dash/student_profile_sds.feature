Feature:  Student Profile View

As a teacher in a school district, I want to click on a student and be directed to their profile page.

Background:
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go

@integration
Scenario: Student profile with no homeroom, teacher
When I login as "linda.kim" "linda.kim1234"
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
#Lozenges check
And the following students have "ELL" lozenges: "Matt Sollars;Malcolm Costillo;Felipe Cianciolo"
# Lozenges check on LOS
Then there is no lozenges for student "Tomasa Cleaveland"
And I see a header on the page that has the text "Logout"
And I see a footer on the page that has the text "Copyright"
And I click on student "Alton Maultsby"
And I view its student profile
And their name shown in profile is "Alton Maultsby Jr"
And their id shown in proflie is "800000016"
And their grade is "8"
And the teacher is "!"
And the class is "!"
And the lozenges count is "1"
And I see a header on the page that has the text "Logout"
And I see a footer on the page that has the text "Copyright"
#Display hide tabs based on grades
And there are "7" Tabs
And Tab has a title named "Middle School Overview"
And Tab has a title named "Attendance and Discipline"
And Tab has a title named "Assessments"
And Tab has a title named "Grades and Credits"
And Tab has a title named "Advanced Academics"
And Tab has a title named "ELL"
#Check the District tab
And Tab has a title named "Daybreak District"
And in "Middle School Overview" tab, there are "2" Panels
And in "Attendance and Discipline" tab, there are "1" Panels
And in "Assessments" tab, there are "0" Panels
And in "Grades and Credits" tab, there are "0" Panels
And in "Advanced Academics" tab, there are "0" Panels
And in "ELL" tab, there are "0" Panels

@wip @integration
Scenario: Student with no grade level
When I login as "cgray" "cgray1234"
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
And I click on student "Marvin Miller"
And I view its student profile
And their name shown in profile is "Marvin Miller Jr"
And their id shown in proflie is "453827070"
And their grade is "!"
And the teacher is "Mrs Linda Kim"
And the class is "8th Grade English - Sec 6"
And the lozenges count is "0"
#Display hide tabs based on grades
And there are "8" Tabs
And Tab has a title named "Elementary School Overview"
And Tab has a title named "Middle School Overview"
And Tab has a title named "High School Overview"

@wip
Scenario: View a student with other name
Given I am authenticated to SLI as "lkim" password "lkim"
When I click on the Dashboard page

	
