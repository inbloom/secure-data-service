@RALLY_US4835
@rc
@sandbox
Feature: As a Dashboard user, I want to be able to be able to traverse all of the data I have access to.

Background: None

Scenario: All-in-one scenario
    Given I have an open web browser
    When I navigate to the Portal home page
    And I was redirected to the "Simple" IDP Login page
    When I submit the developer credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the impersonation login page
    Then I should be redirected to the impersonation page
    And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in

	#Title: District Level IT Admin Logs into dashboard from portal
    And I want to select "jstevenson" from the "SmallDatasetUsers" in automatic mode
    Then I should be on Portal home page  
	Then I should see Admin link
	And I click on Admin
	Then I should be on the admin page
	And under Application Configuration, I click on "inBloom Dashboards"
	Then I am authorized to the Configuration Area
	And I paste Valid json config into the text box
	And click Save
	Then I should be shown a success message
   When I navigate to the Portal home page
   And under My Applications, I click on "inBloom Dashboards"
	When I select ed org "Daybreak School District 4529"
	When I select school "East Daybreak Junior High"
	When I select course "8th Grade English"
	When I select section "8th Grade English - Sec 6"
	And I view its section profile
	Then I see a list of 28 students
	When I enter "rudolph" into the student search box
	And I click the search button
	Then "2" results are returned in the page
	And the search results include:
	 |Student                   |Grade    |School                     |
	 |Rudolph Sennett           |1        |South Daybreak Elementary  |
	 |Rudolph Theodore Krinsky  |12       |Daybreak Central High      |
	And I click on log out
    Then I should be redirected to the impersonation page
    And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in

	#Title: Educator Logs into dashboard from portal 
    And I want to select "linda.kim" from the "SmallDatasetUsers" in automatic mode
    Then I should be on Portal home page  
	And under My Applications, I see the following apps: "inBloom Dashboards"
	And under My Applications, I click on "inBloom Dashboards" 
	Then I should be redirected to the Dashboard landing page
	When I select ed org "Daybreak School District 4529"
	When I select school "East Daybreak Junior High"
	And I click on the go button
	And I view the School Profile
	And I click on subject "English Language and Literature"
	And I click on course "8th Grade English"
	And I click on section "8th Grade English - Sec 6"
	Then I see a list of 28 students
	Then I should only see one view named "Middle School ELA View"
	And the list includes: "Matt Sollars"
	And the following students have "ELL" lozenges: "Matt Sollars;Alton Maultsby;Malcolm Costillo"
	And there is no lozenges for student "Lettie Hose"
	And the cutpoints for "StateTest Reading" is "120,180,231,278,364"
	And the cutpoints for "StateTest Writing" is "6,15,21,28,33"
	And the fuel gauge for "Matt Sollars" in "StateTest Reading" column "perfLevel" is "199"
	And the fuel gauge for "Matt Sollars" in "StateTest Writing" column "perfLevel" is "1"
	And the fuel gauge for "Oralia Merryweather" in "StateTest Reading" column "perfLevel" is "205"
	And the fuel gauge for "Oralia Merryweather" in "StateTest Writing" column "perfLevel" is "32"
	And the fuel gauge for "Gerardo Saltazor" in "StateTest Reading" column "perfLevel" is "309"
	And the fuel gauge for "Gerardo Saltazor" in "StateTest Writing" column "perfLevel" is "15"
	And the fuel gauge for "Karrie Rudesill" in "StateTest Reading" column "perfLevel" is "181"
	And the fuel gauge for "Karrie Rudesill" in "StateTest Writing" column "perfLevel" is "11"
	And the count for id "attendances.absenceCount" for student "Matt Sollars" is "4"
	And the class for id "attendances.absenceCount" for student "Matt Sollars" is "color-widget-green"
	And the count for id "attendances.attendanceRate" for student "Matt Sollars" is "95"
	And the class for id "attendances.attendanceRate" for student "Matt Sollars" is "color-widget-green"
	And the count for id "attendances.tardyCount" for student "Matt Sollars" is "0"
	And the class for id "attendances.tardyCount" for student "Matt Sollars" is "color-widget-darkgreen"
	And the count for id "attendances.tardyRate" for student "Matt Sollars" is "0"
	And the class for id "attendances.tardyRate" for student "Matt Sollars" is "color-widget-darkgreen"
	# AbsenceCount: 1
	And the count for id "attendances.absenceCount" for student "Dominic Brisendine" is "0"
	And the class for id "attendances.absenceCount" for student "Dominic Brisendine" is "color-widget-darkgreen"
	# AbsenceCount: more than 6 absence count
	And the count for id "attendances.absenceCount" for student "Alton Maultsby" is "5"
	And the class for id "attendances.absenceCount" for student "Alton Maultsby" is "color-widget-green"
	# AbsenceCount: more than 11 absense count
	And the count for id "attendances.absenceCount" for student "Felipe Cianciolo" is "6"
	And the class for id "attendances.absenceCount" for student "Felipe Cianciolo" is "color-widget-yellow"
	# AbsenceCount: less than 89% attendance rate
	And the count for id "attendances.attendanceRate" for student "Lashawn Taite" is "86"
	And the class for id "attendances.attendanceRate" for student "Lashawn Taite" is "color-widget-red"
	# AbsenceRate: between 90-94%
	And the count for id "attendances.attendanceRate" for student "Rudy Bedoya" is "95"
	And the class for id "attendances.attendanceRate" for student "Rudy Bedoya" is "color-widget-green"
	# AbsenceRate: between 95-98%
	And the count for id "attendances.attendanceRate" for student "Merry Mccanse" is "93"
	And the class for id "attendances.attendanceRate" for student "Merry Mccanse" is "color-widget-yellow"
	# AbsenceRate: between 99 - 100%
	And the count for id "attendances.attendanceRate" for student "Dominic Brisendine" is "100"
	And the class for id "attendances.attendanceRate" for student "Dominic Brisendine" is "color-widget-darkgreen"
	# TODO:  all TardyCount and rates are 0"
	And I click on student "Matt Sollars"
	And I see a header on the page
	And I see a footer on the page
	And the title of the page is "inBloom - Student Profile"
	And I view its student profile
	And their name shown in profile is "Matt Joseph Sollars Jr"
	And their id shown in proflie is "800000025"
	And their grade is "8"
	And the class is "8th Grade English - Sec 6"
	And the lozenges count is "1"
	And the lozenges include "ELL"
	And Student Enrollment History has the following entries:
	|Year   |School                     |Gr|Entry Date |Entry Type                                                                 |Transfer |Withdraw Date|Withdraw Type      |
	|<empty>|East Daybreak Junior High  |8 |2011-09-01 |<empty>                                                                    |<empty>  |<empty>      |<empty>            |
	|<empty>|East Daybreak Junior High  |7 |2010-09-01 |Next year school                                                           |<empty>  |2011-05-11   |End of school year |
	|<empty>|East Daybreak Junior High  |6 |2009-09-07 |Transfer from a public school in the same local education agency           |<empty>  |2010-05-11   |End of school year |
	|<empty>|South Daybreak Elementary  |5 |2008-09-05 |Next year school                                                           |<empty>  |2009-05-11   |End of school year |
	|<empty>|South Daybreak Elementary  |4 |2007-09-12 |Next year school                                                           |<empty>  |2008-05-10   |End of school year |
	|<empty>|South Daybreak Elementary  |3 |2006-09-11 |Transfer from a private, religiously-affiliated school in a different state|<empty>  |2007-05-09   |Student is in a different public school in the same local education agency|
	 When I click on "Assessment" Tab
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
	When I click on "Attendance and Discipline" Tab
	And the Attendance History in grid "1" has the following entries:
	|Term         |School                     |Grade Level  |% Present  |Total Absences |Excused  |Unexcused  |Tardy  |
	|2011-2012    |East Daybreak Junior High  |8            |0          |0              |0        |0          |0      |
	And the Attendance History in grid "2" has the following entries:
	|Term         |School                     |Grade Level  |% Present  |Total Absences |Excused  |Unexcused  |Tardy  |
	|2010-2011    |East Daybreak Junior High  |7            |0          |0              |0        |0          |0      |
	And I click on log out
    Then I should be redirected to the impersonation page
    And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in

	#Title: State Level IT Admin Logs into dashboard from portal
    And I want to select "rrogers" from the "SmallDatasetUsers" in automatic mode  
	Then I should be on Portal home page
	Then I should see Admin link
	And I click on Admin
	Then I should be on the admin page
	And under Application Configuration, I click on "inBloom Dashboards"
	Then I am authorized to the Configuration Area
	And I paste Valid json config into the text box
	And click Save
	Then I should be shown a success message
	When I navigate to the Dashboard home page
	When I look in the ed org drop-down
	Then I see these values in the drop-down: "Daybreak School District 4529"
	When I select ed org "Daybreak School District 4529"
	When I look in the school drop-down
	Then I see these values in the drop-down: "Daybreak Central High;East Daybreak Junior High;South Daybreak Elementary"
	When I select ed org "Daybreak School District 4529"
	When I select school "Daybreak Central High"
	And I select course "American Literature"
	And I select section "Sec 145"
	Then I see a list of 25 students
	When I enter "Matt" into the student search box
	And I click the search button
	And the search results include:
	 |Student                 |Grade    |School                     |
	 |Matt Joseph Sollars     |8        |East Daybreak Junior High  |
	And I click on student "Matt Joseph Sollars"
	And I view its student profile
	And Student Enrollment History has the following entries:
	|Year   |School                     |Gr|Entry Date |Entry Type                                                                 |Transfer |Withdraw Date|Withdraw Type      |
	|<empty>|East Daybreak Junior High  |8 |2011-09-01 |<empty>                                                                    |<empty>  |<empty>      |<empty>            |
	|<empty>|East Daybreak Junior High  |7 |2010-09-01 |Next year school                                                           |<empty>  |2011-05-11   |End of school year |
	|<empty>|East Daybreak Junior High  |6 |2009-09-07 |Transfer from a public school in the same local education agency           |<empty>  |2010-05-11   |End of school year |
	|<empty>|South Daybreak Elementary  |5 |2008-09-05 |Next year school                                                           |<empty>  |2009-05-11   |End of school year |
	|<empty>|South Daybreak Elementary  |4 |2007-09-12 |Next year school                                                           |<empty>  |2008-05-10   |End of school year |
	|<empty>|South Daybreak Elementary  |3 |2006-09-11 |Transfer from a private, religiously-affiliated school in a different state|<empty>  |2007-05-09   |Student is in a different public school in the same local education agency|
