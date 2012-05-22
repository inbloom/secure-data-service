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
And I click on student "Alton Maultsby"
And I view its student profile
And their name shown in profile is "Alton Maultsby Jr"
And their id shown in proflie is "800000016"
And their grade is "8"
And the teacher is "!"
And the class is "!"
And the lozenges count is "1"
#Display hide tabs based on grades
And there are "4" Tabs
And Tab has a title named "Middle School Overview"
And Tab has a title named "Attendance and Discipline"
And Tab has a title named "Assessments"
And Tab has a title named "Grades and Credits"
And in "Middle School Overview" tab, there are "2" Panels
And in "Attendance and Discipline" tab, there are "1" Panels
And in "Assessments" tab, there are "2" Panels
And in "Grades and Credits" tab, there are "1" Panels

@integration
Scenario: Student with a nickname and 1 studentSectionAssociation without homeroom
When I login as "manthony" "manthony1234"
When I select ed org "Sunset School District 4526"
When I select school "Sunset Central High School"
When I select course "A.P. Calculus"
When I select section "A.P. Calculus Sec 201"
And I click on student "Roberta Jones"
And I view its student profile
And their name shown in profile is "Roberta Jones (Robbie Jones)"
And their id shown in proflie is "1000000000"
And their grade is "11"
And the teacher is "Mr Mark Anthony"
And the class is "A.P. Calculus Sec 201"
And there are "4" Tabs

@integration 
Scenario: Student with no grade level
When I login as "manthony" "manthony1234"
When I select ed org "Sunset School District 4526"
When I select school "Sunset Central High School"
When I select course "A.P. Calculus"
When I select section "A.P. Calculus Sec 201"
Then I should only see one view named "Default View"
And I click on student "Betty Davis"
And I view its student profile
And their grade is "!"


	
