Feature:  Student Profile View

As a teacher in a school district, I want to click on a student and be directed to their profile page.

Background:
Given I have an open web browser
Given that dashboard has been authorized for all ed orgs
When I navigate to the Dashboard home page

@integration @RALLY_US200 @RALLY_US147 @RALLY_US198
Scenario: Student profile with no homeroom, teacher
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
And I click on student "Alton Maultsby"
And I view its student profile
And their name shown in profile is "Alton Maultsby Jr"
And their id shown in proflie is "800000016"
And their grade is "8"
And the class is "!"
And the lozenges count is "1"

@integration @RALLY_US200 @RALLY_US147 @RALLY_US198
Scenario: Student with a nickname and 1 studentSectionAssociation without homeroom
When I select "Illinois Sunset School District 4526" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "manthony" "manthony1234" for the "Simple" login page
When I select ed org "Sunset School District 4526"
When I select school "Sunset Central High School"
When I select course "A.P. Calculus"
When I select section "A.P. Calculus Sec 201"
And I click on student "Roberta Jones"
And I view its student profile
And their name shown in profile is "Roberta Jones (Robbie Jones)"
And their id shown in proflie is "1000000000"
And their grade is "11"
And the class is "A.P. Calculus Sec 201"

@integration @RALLY_US200 @RALLY_US147 @RALLY_US198
Scenario: Student with no grade level
When I select "Illinois Sunset School District 4526" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "manthony" "manthony1234" for the "Simple" login page
When I select ed org "Sunset School District 4526"
When I select school "Sunset Central High School"
When I select course "A.P. Calculus"
When I select section "A.P. Calculus Sec 201"
Then I should only see one view named "Default View"
And I click on student "Betty Davis"
And I view its student profile
And their grade is "!"



