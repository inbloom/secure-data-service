@RALLY_US4086 @clearIndexer
Feature:  Context security implementation for staff and teachers in ES

Scenario:  IT admins can search for students at their Ed Org Level only
# IT admin can search for student data within their Ed Org : Illinois Daybreak School District 4529
Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
#Search by Last Name
Given I search in API for "tran"
Then I should receive a return code of 200
And I see the following search results:
 |Field              |Value                                           |
 |id                 |92a33cae13e838176dbea9ca8b8c354d7420eaa8_id     |
 |type               |student                                         |
 |name.lastSurname   |Tran                                            |
 |name.firstName     |Mi-Ha                                           |
 
# IT admin cannot search for students outside their Ed Org
Given I search in API for "Jeremy"
#TO_DO: Update return code to 404 when the development is completed.
Then I should receive a return code of 200
And no search results are returned

@RALLY_US4156
Scenario:  State IT admin can search for students from all Ed Orgs within the state
Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
# Search by First Name
Given I search in API for "mi-ha"
Then I should receive a return code of 200
And I see the following search results:
 |Field              |Value                                           |
 |id                 |92a33cae13e838176dbea9ca8b8c354d7420eaa8_id     |
 |type               |student                                         |
 |name.lastSurname   |Tran                                            |
 |name.firstName     |Mi-Ha                                           |
 
 #Search by partial string
Given I search in API for "Jere"
#TO_DO: Update return code to 404 when the development is completed.
Then I should receive a return code of 200
And I see the following search results:
 |Field              |Value                                           |
 |id                 |44dcd2d31ccfecce30e206da107f4e3d0a302a91_id     |
 |type               |student                                         |
 |name.lastSurname   |Lin                                             |
 |name.firstName     |Jeremy                                          |
  
Scenario:  Teacher can search for students within their cohort/section/program only
Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
Given I search in API for "matt"
Then I should receive a return code of 200
And I see the following search results:
 |Field              |Value                                           |
 |id                 |067198fd6da91e1aa8d67e28e850f224d6851713_id     |
 |type               |student                                         |
 |name.middleName    |Joseph                                          | 
 |name.lastSurname   |Sollars                                         |
 |name.firstName     |Matt                                            |
 
#Search for student belonging to the same school but different cohort/section/program
Given I search in API for "Carmen"
#TO_DO: Update return code to 404 when the development is completed.
Then I should receive a return code of 200
And no search results are returned

# Test with another teacher
Given I am logged in using "cgray" "cgray1234" to realm "IL"
#Search by middle name
Given I search in API for "Daniella"
Then I should receive a return code of 200
And I see the following search results:
 |Field              |Value                                           |
 |id                 |2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id     |
 |type               |student                                         |
 |name.middleName    |Daniella                                        |
 |name.lastSurname   |Ortiz                                           |
 |name.firstName     |Carmen                                          |
 
Given I search in API for "matt"
#TO_DO: Update return code to 404 when the development is completed.
Then I should receive a return code of 200
And no search results are returned 
 
 
 
 