@RALLY_US149
Feature:  Dashboard Tests For 3 Sample Students

Background:
Given I have an open web browser
Given that dashboard has been authorized for all ed orgs
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go

@integration @RALLY_US200 @RALLY_US147 @RALLY_US198 @RALLY_US2253 @RALLY_US196 @RALLY_US2254
Scenario: View Matt Sollars
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
And I see a header on the page
And I see a footer on the page
And the title of the page is "inBloom Dashboard"
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
And I click on the go button
And I view the School Profile
And the school name is "East Daybreak Junior High"
And the school address is 
"""
111 Ave B
Chicago, IL 10112
"""
And the school phone number is "(917)-555-3312"
And the grades served is "6, 7, 8"
And I see the following subjects:
|Subject                          |
|Mathematics                      |
|English Language and Literature  |
And I click on subject "Mathematics"
And I see the following courses:
|Course|
|6th Grade Math     |
|7th Grade Math     |
And I click on subject "English Language and Literature"
And I see the following courses:
|Course             |
|6th Grade English  |
|8th Grade English  |
|7th Grade English  |
|6th Grade Math     |
|7th Grade Math     |
And I click on course "8th Grade English"
And I click on course "7th Grade English"
And I click on course "6th Grade English"
And I click on course "7th Grade Math"
And I see the following sections:
|Section                    |
|8th Grade English - Sec 6  |
|6th Grade English - Sec 4  |
|7th Grade English - Sec 5  |
|7th Grade Math - Sec 2     |
And I click on section "8th Grade English - Sec 6"
Then I see a list of 28 students
And I check "Student" column is sorted as "string" column
And I click on "Absence Count" header to sort a "integer" column in "ascending" order
And I click on "Student" header to sort a "string" column in "descending" order
And I click on "StateTest Reading Performance Level" header to sort an integer column in "ascending" order based on "title"
And I click on "StateTest Writing Performance Level" header to sort an integer column in "descending" order based on "title"
And I click on "Unit Test 1" header to sort a "LetterGrade" column in "ascending" order
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
And the grades teardrop color widgets for "previousSemester;twoSemestersAgo" are mapped correctly:
 |grade|teardrop           |
 |A+   |teardrop-darkgreen |
 |A-   |teardrop-darkgreen |
 |A    |teardrop-darkgreen |
 |B+   |teardrop-lightgreen|
 |B-   |teardrop-lightgreen|
 |B    |teardrop-lightgreen|
 |C+   |teardrop-yellow    |
 |C-   |teardrop-yellow    |
 |C    |teardrop-yellow    |
 |D+   |teardrop-orange    |
 |D-   |teardrop-orange    |
 |D    |teardrop-orange    |
 |F+   |teardrop-red       |
 |F-   |teardrop-red       |
 |F    |teardrop-red       |
And I click on student "Matt Sollars"
And I see a header on the page
And I see a footer on the page
And the title of the page is "inBloom - Student Profile"
And I view its student profile
And their name shown in profile is "Matt Joseph Sollars Jr"
And their id shown in proflie is "800000025"
And their grade is "8"
#And the teacher is "Mrs Linda Kim"
And the class is "8th Grade English - Sec 6"
And the lozenges count is "1"
And the lozenges include "ELL"
And there are "4" Tabs
And Tab has a title named "Middle School Overview"
And Tab has a title named "Attendance and Discipline"
And Tab has a title named "Assessments"
And Tab has a title named "Grades and Credits"
And in "Middle School Overview" tab, there are "2" Panels
And in "Attendance and Discipline" tab, there are "2" Panels
And in "Assessments" tab, there are "2" Panels
And in "Grades and Credits" tab, there are "2" Panels
And I look at the panel "Contact Information"
And I look at "Student" Contact Info
And there are "1" email addresses
And the list of email address includes "m.sollars@gmail.com"
And there are "1" phone numbers
And the list of phone number includes "309-555-2449"
And the phone number "309-555-2449" is of type "Home"
And there are "0" addresses
And I look at "Parent 1" Contact Info
And parent "Marsha Jones" is his "Grandparent"
And there are "2" phone numbers
And the order of the phone numbers is "3095550000;3095550001"
And there are "0" email addresses
And there are "1" addresses
And the list of address includes
"""
34 Eastside Ave
245
Daybreak, IL 75229
"""
And I look at "Parent 2" Contact Info
And parent "Matthew Sollars" is his "Father"
And there are "1" phone numbers
And the list of phone number includes "3095550000"
And there are "1" addresses
And the list of address includes
"""
82 EastCrest St, A11
Daybreak, IL 75229
"""
And there are "1" email addresses
And the list of email address includes "m.sollars@aol.com"
And I look at "Parent 3" Contact Info
And parent "Aaron Smith" is his "Agency representative"
And there are "1" phone numbers
And the list of phone number includes "3095550000"
And the phone number "3095550000" is of type "Work"
And there are "1" email addresses
And the list of email address includes "a.smith@acs.daybreak.gov"
And there are "1" addresses
And the list of address includes
"""
18 East Main Street, Ste 200
Daybreak, IL 75229
"""
And I look at "Parent 4" Contact Info
And parent "Marsha Sollars-Jones" is his "Mother"
And there are "0" phone numbers
And there are "0" addresses
And there are "0" email addresses
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
And the Attendance column "Excused" is of style "color-column-blue"
And the Attendance column "Unexcused" is of style "color-column-red"
And the Attendance column "Tardy" is of style "color-column-orange"
And I click on the browser back button
Then I see a list of 28 students

@integration @RALLY_US200 @RALLY_US147 @RALLY_US198
Scenario: View Mi-Ha Tran
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "rbraverman" "rbraverman1234" for the "Simple" login page
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
And I select course "1st Grade Homeroom"
And I select section "Mrs. Braverman's Homeroom #38"
Then I see a list of 25 students
Then I should only see one view named "Early Literacy View"
And the list includes: "Mi-Ha Tran"
And the following students have "ELL" lozenges: "Malcolm Haehn;Dara Nemecek;Lauretta Seip"
And I see a header on the page
And I see a footer on the page
# Early Literacy View
And the count for id "attendances.absenceCount" for student "Mi-Ha Tran" is "1"
And the class for id "attendances.absenceCount" for student "Mi-Ha Tran" is "color-widget-green"
And the count for id "attendances.tardyCount" for student "Mi-Ha Tran" is "0"
And the class for id "attendances.tardyCount" for student "Mi-Ha Tran" is "color-widget-darkgreen"
# Absence Count: 0
And the count for id "attendances.absenceCount" for student "Smouse Zhou" is "0"
And the class for id "attendances.absenceCount" for student "Smouse Zhou" is "color-widget-darkgreen"
# Absence Count: 6 - 10
And the count for id "attendances.absenceCount" for student "Rudolph Sennett" is "3"
And the class for id "attendances.absenceCount" for student "Rudolph Sennett" is "color-widget-green"
# TODO Absence Count:  > 11
# Tardy Count: 0
And the count for id "attendances.tardyCount" for student "Maria Werner" is "0"
And the class for id "attendances.tardyCount" for student "Maria Werner" is "color-widget-darkgreen"
# Tardy Count: 6-10
And the count for id "attendances.tardyCount" for student "Rudolph Sennett" is "4"
And the class for id "attendances.tardyCount" for student "Rudolph Sennett" is "color-widget-green"
# Tardy Count: > 11
And the count for id "attendances.tardyCount" for student "Garry Kinsel" is "7"
And the class for id "attendances.tardyCount" for student "Garry Kinsel" is "color-widget-yellow"
And I click on student "Mi-Ha Tran"
And I view its student profile
And their name shown in profile is "Mi-Ha Tran"
And their id shown in proflie is "100000017"
And their grade is "1"
#And the teacher is "Ms Rebecca Braverman"
And the class is "Mrs. Braverman's Homeroom #38"
And the lozenges count is "0"
And there are "4" Tabs
And Tab has a title named "Elementary School Overview"
And Tab has a title named "Attendance and Discipline"
And Tab has a title named "Assessments"
And Tab has a title named "Grades and Credits"
And in "Elementary School Overview" tab, there are "2" Panels
And in "Grades and Credits" tab, there are "2" Panels
And I look at the panel "Contact Information"
And there are "3" email addresses
And the list of email address includes "ti.troung@gmail.com"
And the list of email address includes "ti.troung@unicef.com"
And the list of email address includes "ti.troung@yahoo.com"
And the order of the email addresses is "ti.troung@gmail.com;ti.troung@unicef.com;ti.troung@yahoo.com"
And there are "2" phone numbers
And the list of phone number includes "309-555-5210"
And the list of phone number includes "309-555-5341"
And the phone number "309-555-5210" is of type "Unlisted"
And the phone number "309-555-5341" is of type "Unlisted"
And the order of the phone numbers is "309-555-5210;309-555-5341"
And there are "2" addresses
And the list of address includes
"""
34 Northshore Ave, Apt 9B
Daybreak, IL 75229
"""
And the list of address includes
"""
82 N. Central Pkwy., Suite B
Daybreak, IL 75229
"""
And the order of the addressess is "34 Northshore Ave;82 N. Central Pkwy."
And I look at "Parent 1" Contact Info
And parent "Ti Troung" is his "Mother"
And there are "2" phone numbers
And the list of phone number includes "3095550000"
And the list of phone number includes "3095555341"
And the order of the phone numbers is "3095550000;3095555341"
And the phone number "3095550000" is of type "Home"
And there are "3" email addresses
And the list of email address includes "ti.troung@gmail.com"
And the list of email address includes "ti.troung@unicef.org"
And the list of email address includes "ti.troung@yahoo.com"
And the order of the email addresses is "ti.troung@gmail.com;ti.troung@unicef.org;ti.troung@yahoo.com"
And there are "1" addresses
And the list of address includes
"""
34 Northshore Ave, Apt 9B
Daybreak, IL 75229
"""
And I look at "Parent 2" Contact Info
# Defect on character set
And parent "Richard Lê" is his "Father"
And there are "1" phone numbers
And the list of phone number includes "+33 (1) 45.23.35.48.12"
And there are "1" addresses
And the list of address includes
"""
23 Rue Lecourbe
Paris, IL 75229
"""
And Student Enrollment History has the following entries:
|Year      |School                     |Gr  |Entry Date |Entry Type                                 |Transfer |Withdraw Date  |Withdraw Type      |
|<empty>   |South Daybreak Elementary  |1   |2011-09-05 |Next year school                           |<empty>  |<empty>        |<empty>            |
|<empty>   |South Daybreak Elementary  |K   |2010-09-03 |Original entry into a United States school |<empty>  |2011-05-11     |End of school year |
And I click on the browser back button
Then I see a list of 25 students

@integration @RALLY_US200 @RALLY_US147 @RALLY_US198
Scenario: View Carmen Ortiz
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "American Literature"
And I select section "Sec 145"
Then I see a list of 25 students
And the list includes: "Carmen Ortiz"
And the following students have "ELL" lozenges: "Randolph Vanhooser;Kelvin Zahm;Johnathan Zenz"
And I see a header on the page
And I see a footer on the page
Then I should only see one view named "College Ready ELA View"
And the count for id "attendances.absenceCount" for student "Carmen Ortiz" is "1"
And the class for id "attendances.absenceCount" for student "Carmen Ortiz" is "color-widget-green"
And the count for id "attendances.attendanceRate" for student "Carmen Ortiz" is "99"
And the class for id "attendances.attendanceRate" for student "Carmen Ortiz" is "color-widget-darkgreen"
And the count for id "attendances.tardyCount" for student "Carmen Ortiz" is "0"
And the class for id "attendances.tardyCount" for student "Carmen Ortiz" is "color-widget-darkgreen"
And the count for id "attendances.tardyRate" for student "Carmen Ortiz" is "0"
And the class for id "attendances.tardyRate" for student "Carmen Ortiz" is "color-widget-darkgreen"
# Absence count: > 11
And the count for id "attendances.absenceCount" for student "Geoffrey Pillard" is "10"
And the class for id "attendances.absenceCount" for student "Geoffrey Pillard" is "color-widget-yellow"
# Absense count 1-5
And the count for id "attendances.absenceCount" for student "Maya Cun" is "1"
And the class for id "attendances.absenceCount" for student "Maya Cun" is "color-widget-green"
# attendance rate 98-99
And the count for id "attendances.attendanceRate" for student "Samatha Twining" is "99"
And the class for id "attendances.attendanceRate" for student "Samatha Twining" is "color-widget-darkgreen"
# Attendance rate 90-97
And the count for id "attendances.attendanceRate" for student "Garry Mcconnaughy" is "93"
And the class for id "attendances.attendanceRate" for student "Garry Mcconnaughy" is "color-widget-yellow"
# Attendance rate < 90
And the count for id "attendances.attendanceRate" for student "Oma Bevington" is "85"
And the class for id "attendances.attendanceRate" for student "Oma Bevington" is "color-widget-red"
And the grades teardrop color widgets for "previousSemester;twoSemestersAgo" are mapped correctly:
 |grade|teardrop           |
 |A+   |teardrop-darkgreen |
 |A-   |teardrop-darkgreen |
 |A    |teardrop-darkgreen |
 |B+   |teardrop-lightgreen|
 |B-   |teardrop-lightgreen|
 |B    |teardrop-lightgreen|
 |C+   |teardrop-yellow    |
 |C-   |teardrop-yellow    |
 |C    |teardrop-yellow    |
 |D+   |teardrop-orange    |
 |D-   |teardrop-orange    |
 |D    |teardrop-orange    |
 |F+   |teardrop-red       |
 |F-   |teardrop-red       |
 |F    |teardrop-red       |
And I click on student "Carmen Ortiz"
And I view its student profile
And their name shown in profile is "Carmen Daniella Ortiz"
And their id shown in proflie is "900000016"
And their grade is "11"
#And the teacher is "!"
#And the class is "American Literature"
And the lozenges count is "0"
And there are "4" Tabs
And Tab has a title named "High School Overview"
And Tab has a title named "Attendance and Discipline"
And Tab has a title named "Assessments"
And Tab has a title named "Grades and Credits"
And in "High School Overview" tab, there are "2" Panels
And in "Grades and Credits" tab, there are "2" Panels
And I look at the panel "Contact Information"
And I look at "Student" Contact Info
And there are "1" email addresses
And the list of email address includes "c.ortiz@gmail.com"
And there are "1" phone numbers
And the list of phone number includes "309-555-9372"
And the phone number "309-555-9372" is of type "Other"
And there are "0" addresses
And I look at "Parent 1" Contact Info
And parent "Ignatio Ortiz" is his "Father"
And there are "4" phone numbers
And the list of phone number includes "3095550001"
And the order of the phone numbers is "3095550001;3095550004;3095550003;3095550002"
And there are "1" email addresses
And the list of email address includes "c.ortiz@gmail.com"
And there are "1" addresses
And the list of address includes
"""
213 Central Ave
Daybreak, IL 75229
"""
And I look at "Parent 2" Contact Info
And parent "Felizia Ortiz" is his "Mother"
And there are "1" phone numbers
And the list of phone number includes "3095550000"
And Student Enrollment History has the following entries:
|Year   |School                     |Gr|Entry Date |Entry Type                                                                               |Transfer     |Withdraw Date  |Withdraw Type      |
|<empty>|Daybreak Central High      |11|2011-09-08 |Next year school                                                                         |<empty>      |<empty>        |<empty>            |
|<empty>|Daybreak Central High      |10|2010-09-08 |Next year school                                                                         |<empty>      |2011-05-11     |End of school year |
|<empty>|Daybreak Central High      |9 |2009-09-08 |Transfer from a public school in the same local education agency                         |<empty>      |2010-05-11     |End of school year |
|<empty>|East Daybreak Junior High  |8 |2008-09-11 |Transfer from a public school in the same local education agency                         |<empty>      |2009-05-08     |Exited             |
|<empty>|East Daybreak Junior High  |7 |2007-09-14 |Next year school                                                                         |<empty>      |2008-02-12     |Student is in a different public school in the same local education agency|
|<empty>|East Daybreak Junior High  |7 |2007-09-14 |Next year school                                                                         |<empty>      |2008-02-12     |Student is in a different public school in the same local education agency|
|<empty>|East Daybreak Junior High  |6 |2006-09-11 |Next year school                                                                         |<empty>      |2007-05-14     |End of school year |
|<empty>|South Daybreak Elementary  |5 |2005-09-09 |Transfer from a private, religiously-affiliated school in the same local education agency|<empty>      |2006-05-15     |Exited|
|<empty>|South Daybreak Elementary  |2 |2002-09-12 |Transfer from a school outside of the country                                            |<empty>      |2003-04-12     |Expelled or involuntarily withdrawn|
And I see a header on the page
When I click on "Assessment" Tab
And Assessment History includes results for:
|Test       |
|AP English |
And the Assessment History for "AP English" has the following entries:
|Date         |Grade  |Assessment Name                     |Perf Level |
|2011-05-01   |12     |English Literature and Composition  |3          |
|2011-05-01   |12     |English Language and Composition    |2          |
And I click on the browser back button
Then I see a list of 25 students
