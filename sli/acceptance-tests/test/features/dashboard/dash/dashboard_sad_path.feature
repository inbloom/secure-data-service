Feature: Dashboard Sad Path Tests

Test Sad Paths and Display Error Messages

Background:
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
Then I should be redirected to the Realm page

Scenario: User has no data
When I select "New York Realm" and click go
When I login as "mario.sanchez" "mario.sanchez1234"
Then I am informed that "No data is available for you to view."

Scenario:  User has no ed-org
When I select "Illinois Sunset School District 4526" and click go
When I login as "manthony" "manthony1234"
Then I am informed that "No data is available for you to view."

Scenario: User has no schools
When I select "Illinois Sunset School District 4526" and click go
When I login as "jdoe" "jdoe1234"
Then I am informed that "No data is available for you to view."

Scenario: User has org, no school
When I select "Illinois Sunset School District 4526" and click go
When I login as "ejane" "ejane1234"
#Then I am informed that "No data is available for you to view."
Then I am informed that "the page that you were looking for could not be found"

Scenario: No sections
When I select "Illinois Sunset School District 4526" and click go
When I login as "tbear" "tbear1234"
When I look in the ed org drop-down
Then I only see "Daybreak School District 4529"
When I look in the school drop-down
Then I only see "East Daybreak Junior High"
Then I am informed that "There is no data available for your request. Please contact your IT administrator."
#When I look in the course drop-down
#Then I only see "There is no data available for your request.  Please contact your IT administrator."

Scenario: No schools in district
When I select "Illinois Sunset School District 4526" and click go
When I login as "jwashington" "jwashington1234"
Then I am informed that "No data is available for you to view."

Scenario:  Check empty student values
When I select "Illinois Sunset School District 4526" and click go
When I login as "cgray" "cgray1234"
When I look in the ed org drop-down
Then I only see "Daybreak School District 4529"
When I look in the school drop-down
Then I only see "Daybreak Central High"
When I look in the course drop-down
Then I only see "American Literature"
And I select section "Sec 145"
Then I see a list of 4 students

Scenario: Check empty ISAT assessments
When I select "Illinois Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
Then I am informed that "There is no data available for your request. Please contact your IT administrator."
#Then I should have a dropdown selector named "viewSelect"
# Then I should only see one view named "There is no data available for your request.  Please contact your IT administrator."
When I select course "8th Grade Math"
When I select section "8th Grade Math - Sec 1"
And I see a list of 5 students

Scenario: Section without Student grades
When I select "Illinois Sunset School District 4526" and click go
When I login as "rbraverman" "rbraverman1234"
When I select section "Grade 1"
#Then I should have a dropdown selector named "viewSelect"
# Then I should only see one view named "There is no data available for your request. Please contact your IT administrator."
Then I am informed that "There is no data available for your request. Please contact your IT administrator."
