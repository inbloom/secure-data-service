@RALLY_US4086 @integration
Feature:  Context security implementation for staff and teachers in ES

Scenario:  IT admins can search for students at their Ed Org Level only
# IT admin can search for student data within their Ed Org : Illinois Daybreak School District 4529
Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
#Search by Last Name
Given I search in API for "tran"
Then I should receive a return code of 200
And I see the following search results at index 0:
 |Field              |Value                                           |
 |id                 |92a33cae13e838176dbea9ca8b8c354d7420eaa8_id     |
 |entityType         |student                                         |
 |name.lastSurname   |Tran                                            |
 |name.firstName     |Mi-Ha                                           |
 
# IT admin cannot search for students outside their Ed Org
Given I search in API for "Jeremy"
#TO_DO: Update return code to 404 when the development is completed.
Then I should receive a return code of 200
And no search results are returned
Given I search in API for "rudolp"
Then I should receive a return code of 200
Then I should receive a collection with 2 elements
And I see the following search results at index 0:
 |Field              |Value                                           |
 |name.firstName     |Rudolph                                         |
 And I see the following search results at index 1:
 |Field              |Value                                           |
 |name.firstName     |Rudolph                                         |

@RALLY_US4156
Scenario:  State IT admin can search for students from all Ed Orgs within the state
Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
# Search by First Name
Given I search in API for "mi-ha"
Then I should receive a return code of 200
 Then I should receive a collection with 4 elements
And I see the following search results at index 0:
 |Field              |Value                                           |
 |id                 |92a33cae13e838176dbea9ca8b8c354d7420eaa8_id     |
 |entityType         |student                                         |
 |name.lastSurname   |Tran                                            |
 |name.firstName     |Mi-Ha                                           |
 
 #Search by partial string
Given I search in API for "Jere"
#TO_DO: Update return code to 404 when the development is completed.
Then I should receive a return code of 200
And I see the following search results at index 0:
 |Field              |Value                                           |
 |id                 |44dcd2d31ccfecce30e206da107f4e3d0a302a91_id     |
 |entityType         |student                                         |
 |name.lastSurname   |Lin                                             |
 |name.firstName     |Jeremy                                          |
  
Scenario:  Teacher can search for students within their cohort/section/program only
Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
Given I search in API for "matt"
Then I should receive a return code of 200
And I see the following search results at index 0:
 |Field              |Value                                           |
 |id                 |067198fd6da91e1aa8d67e28e850f224d6851713_id     |
 |entityType         |student                                         |
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
And I see the following search results at index 0:
 |Field              |Value                                           |
 |id                 |2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id     |
 |entityType         |student                                         |
 |name.middleName    |Daniella                                        |
 |name.lastSurname   |Ortiz                                           |
 |name.firstName     |Carmen                                          |
 
Given I search in API for "matt"
#TO_DO: Update return code to 404 when the development is completed.
Then I should receive a return code of 200
And no search results are returned 
 
 Scenario: Staff More than 50 search results
 Given I am logged in using "ckoch" "ckoch1234" to realm "IL"
 Given I search in API for "matt"
 Then I should receive a return code of 200
 Then I should receive a collection with 50 elements
  #TODO: BUG: this should be 54
  And the header "TotalCount" equals 50
  #And the a next link exists with offset equal to 50 and limit equal to 50
  
Scenario:  Educator with more than 50 results
Given I am logged in using "manthony" "manthony1234" to realm "IL"
 Given I search in API for "matt"
 Then I should receive a return code of 200
 Then I should receive a collection with 50 elements
  #TODO: BUG: this should be 54
  And the header "TotalCount" equals 50
  #And the a next link exists with offset equal to 50 and limit equal to 50
  Given I search in API for "lin"
 Then I should receive a return code of 200
 Then I should receive a collection with 50 elements
  #TODO: BUG: this should be 54
  And the header "TotalCount" equals 50
  #And the a next link exists with offset equal to 50 and limit equal to 50

 
 Scenario: School Level searching for student not in school
 Given I am logged in using "mgonzales" "mgonzales1234" to realm "IL"
 Given I search in API for "Alton"
 Then I should receive a return code of 200
 Then I should receive a collection with 0 elements

