Feature: Dashboard Sad Path Tests

Test Sad Paths and Display Error Messages

Background:
Given I have an open web browser
Given that dashboard has been authorized for all ed orgs
When I navigate to the Dashboard home page
Then I should be redirected to the Realm page

Scenario: Teacher's school has no data (no courses)
When I select "New York Realm" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "mario.sanchez" "mario.sanchez1234" for the "Simple" login page
 When I select ed org "New York Parker District School System"
 When I select school "Parker Elementary School"
Then I am informed that "There is no data available for your request. Please contact your IT administrator."

Scenario: Staff's edorg has no data (no schools)
When I select "Illinois Sunset School District 4526" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "jwashington" "jwashington1234" for the "Simple" login page
Then I am informed that "There is no data available for your request. Please contact your IT administrator."

@DE1112
Scenario: Teacher without associations to anything (orphaned)
When I select "Illinois Sunset School District 4526" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "manthony" "manthony1234" for the "Simple" login page
Then I get an error message "Forbidden"
#Then I get an error message "We're sorry, your district has disallowed use of the Dashboard." #DE1112 should enable this step 

@DE1112
Scenario: School User whose district has disallowed the Dashboard for their district
Given the district "NY-Dusk" has dissallowed use of the dashboard
When I select "New York Realm" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "agibbs" "agibbs1234" for the "Simple" login page
Then I get an error message "The page you are requesting is not available."
#Then I get an error message "We're sorry, your district has disallowed use of the Dashboard." #DE1112 should enable this step 

@DE1112
Scenario: User accessing Dashboard does not resolve to anyone in database
When I select "Illinois Sunset School District 4526" and click go
 And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jdoe" "jdoe1234" for the "Simple" login page
Then I get an error message "Forbidden"
#Then I get an error message "We're sorry, your district has disallowed use of the Dashboard." #DE1112 should enable this step 

Scenario: Upload Config
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
When I enter the Configuration Area
Then I am authorized to the Configuration Area
And I paste Valid json config into the text box
And click Save
Then I should be shown a success message

# Need to get a user that this is valid for now that we have realm enforcement
Scenario: No sections
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
When I submit the credentials "mgonzales" "mgonzales1234" for the "Simple" login page
When I look in the ed org drop-down
Then I only see "Daybreak School District 4529"
When I look in the school drop-down
Then I only see "East Daybreak Junior High"
Then I am informed that "There is no data available for your request. Please contact your IT administrator."

@wip
Scenario:  Check empty student values
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
When I look in the ed org drop-down
Then I only see "Daybreak School District 4529"
When I look in the school drop-down
Then I only see "Daybreak Central High"
When I look in the course drop-down
Then I only see "American Literature"
And I select section "Sec 145"
Then I see a list of 4 students
And "Carmen Daniella Ortiz" has no "SAT Reading.x"
And "Carmen Daniella Ortiz" has no "SAT Reading.percentile"
And "Carmen Daniella Ortiz" has no "SAT Writing.x"
And "Carmen Daniella Ortiz" has no "SAT Writing.percentile"

Scenario: Check empty StateTest assessments
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
Then I am informed that "There is no data available for your request. Please contact your IT administrator."
When I select course "8th Grade Math"
When I select section "8th Grade Math - Sec 1"
 And I see a list of 5 students
And "Alton Ausiello" has no "StateTest Reading.perfLevel"
And "Alton Ausiello" has no "StateTest Reading.Scale score"
And "Alton Ausiello" has no "StateTest Reading.Other"
And "Alton Ausiello" has no "StateTest Writing.perfLevel"
And "Alton Ausiello" has no "StateTest Writing.Scale score"
And "Alton Ausiello" has no "currentSession-0"
And "Alton Ausiello" has no "previousSemester"
And "Alton Ausiello" has no "twoSemestersAgo"
And "Alton Ausiello" has no "absenceCount"
And "Alton Ausiello" has no "attendanceRate"
And "Alton Ausiello" has no "tardyCount"
And "Alton Ausiello" has no "tardyRate"

Scenario: Section without Student grades
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "rbraverman" "rbraverman1234" for the "Simple" login page
When I select section "Grade 1"
Then I am informed that "There is no data available for your request. Please contact your IT administrator."
