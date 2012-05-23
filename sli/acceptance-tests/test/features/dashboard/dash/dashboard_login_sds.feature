Feature: Dashboard User Login Authentication

As a SEA/LEA user, I want to use the SLI IDP Login to authenticate 
on SLI, so I could use the Dashboard application.

Background:
Given I have an open web browser
Given the server is in "live" mode

@integration
Scenario: Valid user login

#hitting static URL
When I access "/static/html/test.html" 
Then I can see "Static HTML page"
When I navigate to the Dashboard home page
Then I should be redirected to the Realm page
When I select "Illinois Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
Then I should be redirected to the Dashboard landing page
#hitting denied URL
When I access "/simon"
And I am informed that "the page that you were looking for could not be found"

@integration 
Scenario: Invalid user login

When I navigate to the Dashboard home page
And was redirected to the Realm page
When I select "New York Realm" and click go
And was redirected to the SLI-IDP login page
When I login as "InvalidJohnDoe" "demo1234"
Then I am informed that "Authentication failed"

@integration
Scenario: Login with cookie

When I navigate to the Dashboard home page
Then I should be redirected to the Realm page
Then I add a cookie for linda.kim
When I navigate to the Dashboard home page
Then I should be redirected to the Dashboard landing page

@integration @RALLY_US197 @RALLY_US200 @RALLY_US198 @RALLY_US147
Scenario: Login with District Level IT admin
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "jstevenson" "jstevenson1234"
Then I should be redirected to the Dashboard landing page
When I look in the ed org drop-down
Then I only see "Daybreak School District 4529"
When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I see these values in the drop-down: "South Daybreak Elementary;East Daybreak Junior High;Daybreak Central High"
When I select school "South Daybreak Elementary"
And I select course "1st Grade Homeroom"
And I select section "Mrs. Braverman's Homeroom #38"
Then I see a list of 25 students
And the count for id "attendances.absenceCount" for student "Mi-Ha Tran" is "1"
And the class for id "attendances.absenceCount" for student "Mi-Ha Tran" is "color-widget-green"
And the count for id "attendances.tardyCount" for student "Mi-Ha Tran" is "0"
And the class for id "attendances.tardyCount" for student "Mi-Ha Tran" is "color-widget-darkgreen"
And I copy my current URL
And I click on student "Mi-Ha Tran"
And I view its student profile
And their name shown in profile is "Mi-Ha Tran"
And their id shown in proflie is "100000017"
And their grade is "1"
And the teacher is "Ms Rebecca Braverman"
And the class is "Mrs. Braverman's Homeroom #38"
And I paste my copied URL
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
Then I see a list of 28 students
And the following students have "ELL" lozenges: "Matt Sollars;Alton Maultsby;Malcolm Costillo"
And the cutpoints for "StateTest Reading" is "120,180,231,278,364"
And the cutpoints for "StateTest Writing" is "6,15,21,28,33"
And the fuel gauge for "Matt Sollars" in "StateTest Reading" column "perfLevel" is "199"
And the fuel gauge for "Matt Sollars" in "StateTest Writing" column "perfLevel" is "1"
And the "current" grade for "Matt Sollars" is "B+"
And the "last semester" grade for "Matt Sollars" is "B+"
And the "2 semesters ago" grade for "Matt Sollars" is "A-"
And I click on student "Matt Sollars"
And I look at the panel "Contact Information"
And there are "1" email addresses
And the list of email address includes "m.sollars@gmail.com"
And there are "1" phone numbers
And the list of phone number includes "309-555-2449"
And the phone number "309-555-2449" is of type "Home"
And there are "0" addresses
And Student Enrollment History has the following entries:
|Year   |School                     |Gr|Entry Date |Entry Type                                                                 |Transfer |Withdraw Date|Withdraw Type      |
|<empty>|East Daybreak Junior High  |8 |2011-09-01 |<empty>                                                                    |<empty>  |<empty>      |<empty>            |
|<empty>|East Daybreak Junior High  |7 |2010-09-01 |Next year school                                                           |<empty>  |2011-05-11   |End of school year |
|<empty>|East Daybreak Junior High  |6 |2009-09-07 |Transfer from a public school in the same local education agency           |<empty>  |2010-05-11   |End of school year |
|<empty>|South Daybreak Elementary  |5 |2008-09-05 |Next year school                                                           |<empty>  |2009-05-11   |End of school year |
|<empty>|South Daybreak Elementary  |4 |2007-09-12 |Next year school                                                           |<empty>  |2008-05-10   |End of school year |
|<empty>|South Daybreak Elementary  |3 |2006-09-11 |Transfer from a private, religiously-affiliated school in a different state|<empty>  |2007-05-09   |Student is in a different public school in the same local education agency|
And I click on "Assessment" Tab
And Assessment History includes results for:
|Test         |
|StateTest Reading |
|StateTest Writing |
And the Assessment History for "StateTest Reading" has the following entries:
|Date         |Grade  |Assessment Name            |Scale score  |Other  |Percentile |Perf Level |
|2011-10-01   |8      |Grade 8 2011 StateTest Reading  |195          |642    |53         |195        |
|2011-09-01   |8      |Grade 8 2011 StateTest Reading  |199          |655    |55         |199        |
And the Assessment History for "StateTest Writing" has the following entries:
|Date         |Grade  |Assessment Name            |Perf Level|Scale score|
|2011-10-01   |8      |Grade 8 2011 StateTest Writing  |1         |1          |
|2011-09-01   |8      |Grade 8 2011 StateTest Writing  |25        |25         |
And I click on the browser back button
Then I see a list of 28 students
When I select school "Daybreak Central High"
And I select course "American Literature"
And I select section "Sec 145"
Then I see a list of 25 students
And the count for id "attendances.absenceCount" for student "Carmen Ortiz" is "1"
And the class for id "attendances.absenceCount" for student "Carmen Ortiz" is "color-widget-green"
And the count for id "attendances.attendanceRate" for student "Carmen Ortiz" is "99"
And the class for id "attendances.attendanceRate" for student "Carmen Ortiz" is "color-widget-darkgreen"
And the count for id "attendances.tardyCount" for student "Carmen Ortiz" is "0"
And the class for id "attendances.tardyCount" for student "Carmen Ortiz" is "color-widget-darkgreen"
And the count for id "attendances.tardyRate" for student "Carmen Ortiz" is "0"
And the class for id "attendances.tardyRate" for student "Carmen Ortiz" is "color-widget-darkgreen"
And I click on student "Carmen Ortiz"
When I enter "rudolph" into the "firstName" search box
And I click the search button
Then "2" results are returned in the page
And the search results include:
  |Student          |Grade    |School                     |
  |Rudolph Sennett  |1        |South Daybreak Elementary  |
  |Rudolph Krinsky  |12       |Daybreak Central High      |
  
 @integration @RALLY_US197 @RALLY_US200 @RALLY_US198 @RALLY_US147
 Scenario: Login with State Level IT Admin
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "rrogers" "rrogers1234"
When I look in the ed org drop-down
Then I see these values in the drop-down: "Daybreak School District 4529;Sunset School District 4526"
When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I see these values in the drop-down: "South Daybreak Elementary;East Daybreak Junior High;Daybreak Central High"
When I select ed org "Sunset School District 4526"
And I select school "Sunset Central High School"
And I select course "A.P. Calculus"
And I select section "A.P. Calculus Sec 201"
And I see a list of 3 students
When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "American Literature"
And I select section "Sec 145"
Then I see a list of 25 students
When I enter "Matt" into the "firstName" search box
And I click the search button
Then "50" results are returned in the page
And I select page size of "100"
And "54" results are returned in the page
And the search results include:
  |Student          |Grade    |School                     |
  |Matt Sollars     |8        |East Daybreak Junior High  |
  |Matt Forker      |11       |Sunset Central High School |
And I click on student "Matt Sollars"
And I view its student profile
And Student Enrollment History has the following entries:
|Year   |School                     |Gr|Entry Date |Entry Type                                                                 |Transfer |Withdraw Date|Withdraw Type      |
|<empty>|East Daybreak Junior High  |8 |2011-09-01 |<empty>                                                                    |<empty>  |<empty>      |<empty>            |
|<empty>|East Daybreak Junior High  |7 |2010-09-01 |Next year school                                                           |<empty>  |2011-05-11   |End of school year |
|<empty>|East Daybreak Junior High  |6 |2009-09-07 |Transfer from a public school in the same local education agency           |<empty>  |2010-05-11   |End of school year |
|<empty>|South Daybreak Elementary  |5 |2008-09-05 |Next year school                                                           |<empty>  |2009-05-11   |End of school year |
|<empty>|South Daybreak Elementary  |4 |2007-09-12 |Next year school                                                           |<empty>  |2008-05-10   |End of school year |
|<empty>|South Daybreak Elementary  |3 |2006-09-11 |Transfer from a private, religiously-affiliated school in a different state|<empty>  |2007-05-09   |Student is in a different public school in the same local education agency|

@integration @RALLY_US197 @RALLY_US200 @RALLY_US198 @RALLY_US147
Scenario: Login with District Leader
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "sbantu" "sbantu1234"
When I look in the ed org drop-down
Then I only see "Daybreak School District 4529"
When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I see these values in the drop-down: "South Daybreak Elementary;East Daybreak Junior High;Daybreak Central High"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
Then I see a list of 28 students
And the following students have "ELL" lozenges: "Matt Sollars;Alton Maultsby;Malcolm Costillo"
And the cutpoints for "StateTest Reading" is "120,180,231,278,364"
And the cutpoints for "StateTest Writing" is "6,15,21,28,33"
And the fuel gauge for "Matt Sollars" in "StateTest Reading" column "perfLevel" is "199"
And the fuel gauge for "Matt Sollars" in "StateTest Writing" column "perfLevel" is "1"
And the "current" grade for "Matt Sollars" is "B+"
And the "last semester" grade for "Matt Sollars" is "B+"
And the "2 semesters ago" grade for "Matt Sollars" is "A-"
When I select school "South Daybreak Elementary"
And I select course "1st Grade Homeroom"
And I select section "Mrs. Braverman's Homeroom #38"
Then I see a list of 25 students
And the count for id "attendances.absenceCount" for student "Mi-Ha Tran" is "1"
And the class for id "attendances.absenceCount" for student "Mi-Ha Tran" is "color-widget-green"
And the count for id "attendances.tardyCount" for student "Mi-Ha Tran" is "0"
And the class for id "attendances.tardyCount" for student "Mi-Ha Tran" is "color-widget-darkgreen"
When I select ed org "Daybreak School District 4529"
And I select school "Daybreak Central High"
And I select course "American Literature"
And I select section "Sec 145"
And I see a list of 25 students
When I enter "Matt" into the "firstName" search box
And I click the search button
Then "1" results are returned in the page
And the search results include:
  |Student          |Grade    |School                     |
  |Matt Sollars     |8        |East Daybreak Junior High  |
  
 @integration @RALLY_US200
 Scenario: Login with District level Agg. Viewer
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "jjackson" "jjackson1234"
When I look in the ed org drop-down
Then I only see "Daybreak School District 4529"
When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I see these values in the drop-down: "South Daybreak Elementary;East Daybreak Junior High;Daybreak Central High"
When I select school "East Daybreak Junior High"
Then I don't see a course selection

@integration @RALLY_US200
Scenario: Login with State Agg. Viewer
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "mjohnson" "mjohnson1234"
When I look in the ed org drop-down
Then I see these values in the drop-down: "Daybreak School District 4529;Sunset School District 4526"
When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I see these values in the drop-down: "South Daybreak Elementary;East Daybreak Junior High;Daybreak Central High"
When I select school "South Daybreak Elementary"
Then I don't see a course selection
When I select ed org "Sunset School District 4526"
When I look in the school drop-down
Then I see these values in the drop-down: "Sunset Central High School"
Then I don't see a course selection

@integration @RALLY_US200  @RALLY_US147 @RALLY_US198
Scenario: Login with State Leader
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "ckoch" "ckoch1234"
When I look in the ed org drop-down
Then I see these values in the drop-down: "Daybreak School District 4529;Sunset School District 4526"
When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I see these values in the drop-down: "South Daybreak Elementary;East Daybreak Junior High;Daybreak Central High"
And I select school "South Daybreak Elementary"
And I select course "Phys-Ed 4A"
And I look at the section drop-down
And I don't see these values in the drop-down: "Gym Class - 4;"
When I select ed org "Sunset School District 4526"
And I select school "Sunset Central High School"
And I select course "A.P. Calculus"
And I select section "A.P. Calculus Sec 201"
And I see a list of 3 students
When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "American Literature"
And I select section "Sec 145"
Then I see a list of 25 students
When I enter "Matt" into the "firstName" search box
And I click the search button
Then "50" results are returned in the page
And I select page size of "100"
And "54" results are returned in the page
And the search results include:
  |Student          |Grade    |School                     |
  |Matt Sollars     |8        |East Daybreak Junior High  |
  |Matt Forker      |11       |Sunset Central High School |
And I click on student "Matt Forker"
And I view its student profile
And their name shown in profile is "Matt Forker"
And their id shown in proflie is "1000000002"
And their grade is "11"
And the teacher is "Mr Mark Anthony"
And the class is "A.P. Calculus Sec 201"

@integration @RALLY_US197 @RALLY_US200
Scenario: Login with School Level Leader
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "mgonzales" "mgonzales1234"
When I look in the ed org drop-down
Then I only see "Daybreak School District 4529"
When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I only see "South Daybreak Elementary"
And I select school "South Daybreak Elementary"
And I select course "1st Grade Homeroom"
And I select section "Mrs. Braverman's Homeroom #38"
Then I see a list of 25 students
When I enter "Alton" into the "firstName" search box
And I click the search button
Then "0" results are returned in the page
And I click on the browser back button
Then I see a list of 25 students

@integration  @RALLY_US147 @RALLY_US198
Scenario: Login with School Level IT admin
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "akopel" "akopel1234"
When I look in the ed org drop-down
Then I only see "Daybreak School District 4529"
When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I only see "South Daybreak Elementary"
And I select school "South Daybreak Elementary"
And I select course "1st Grade Homeroom"
And I select section "Mrs. Braverman's Homeroom #38"
Then I see a list of 25 students
And I click on student "Mi-Ha Tran"
And I view its student profile
And their name shown in profile is "Mi-Ha Tran"
And their id shown in proflie is "100000017"
And their grade is "1"
And the teacher is "Ms Rebecca Braverman"
And the class is "Mrs. Braverman's Homeroom #38"
And the lozenges count is "0"

@integration @RALLY_US200
Scenario: Login with School Level aggr viewer
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "msmith" "msmith1234"
When I look in the ed org drop-down
Then I only see "Daybreak School District 4529"
When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I only see "South Daybreak Elementary"
And I select school "South Daybreak Elementary"
Then I don't see a course selection

@wip @integration
Scenario: user in IDP but not in mongo
#TODO there is a bug in the code right now, enable after bug fix
When I access "/static/html/test.html" 
Then I can see "Static HTML page"
When I navigate to the Dashboard home page
Then I should be redirected to the Realm page
When I select "Illinois Sunset School District 4526" and click go
When I login as "mario.sanchez" "mario.sanchez"
#TODO there is a bug in the code right now
Then I am informed that "Invalid User"
