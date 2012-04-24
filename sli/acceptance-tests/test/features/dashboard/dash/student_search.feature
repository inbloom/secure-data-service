Feature:  Simple Student Search 

Background:
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go

@integration
Scenario: Search by First name
When I login as "linda.kim" "linda.kim1234"
When I enter "Matt" into the "First Name" search box
And I click the search button
Then "1" results are returned
And the search results include:
  |Student      |Grade    |School                     |
  |Matt Sollars |8        |East Daybreak Junior High  |
And I enter "matt" into the "First Name" search box
And I click the search button
Then "1" results are returned
And the search results include:
  |Student      |Grade    |School                     |
  |Matt Sollars |8        |East Daybreak Junior High  |
And I enter "MATT" into the "First name" search box
And I click the search button
Then "1" results are returned
And the search results include:
  |Student      |Grade    |School                     |
  |Matt Sollars |8        |East Daybreak Junior High  |

@integration
Scenario: Search by Last name
When I login as "linda.kim" "linda.kim1234"
When I enter "Sollars" into the "Last Name" search box
And I click the search button
Then "1" results are returned
And the search results include:
  |Student      |Grade    |School                     |
  |Matt Sollars |8        |East Daybreak Junior High  |
When I enter "SOLLARS" into the "Last Name" search box
And I click the search button
Then "1" results are returned
And the search results include:
  |Student      |Grade    |School                     |
  |Matt Sollars |8        |East Daybreak Junior High  |
When I enter "sollars" into the "Last Name" search box
And I click the search button
Then "1" results are returned
And the search results include:
  |Student      |Grade    |School                     |
  |Matt Sollars |8        |East Daybreak Junior High  |
#And I click on student "Matt Sollars"
#And I view its student profile
And I click on the browser back button
And "1" results are returned
And the search results include:
  |Student      |Grade    |School                     |
  |Matt Sollars |8        |East Daybreak Junior High  |
  
@integration
Scenario:  Search by non-existing student
When I login as "linda.kim" "linda.kim1234"
When I enter "Tran" into the "Last Name" search box
And I click the search button
Then "0" results are returned

@integration
Scenario:  Empty Search
When I login as "linda.kim" "linda.kim1234"
When I enter nothing into either field of student search
And I click the search button
Then "0" results are returned
And I enter "Gerardo" into the "Last Name" search box
And I click the search button
Then "0" results are returned

@integration
Scenario:  Search with more than 1 results
When I login as "linda.kim" "linda.kim1234"
When I enter "Gerardo" into the "First Name" search box
And I click the search button
Then "3" results are returned
And the search results include:
  |Student            |Grade    |School                     |
  |Gerardo Giaquinto  |8        |East Daybreak Junior High  |
  |Gerardo Rounsaville|8        |East Daybreak Junior High  |
  |Gerardo Saltazor   |8        |East Daybreak Junior High  |
And I click on student "Gerardo Giaquinto"
And I view its student profile
And I click on the browser back button
And "3" results are returned
And the search results include:
  |Student            |Grade    |School                     |
  |Gerardo Giaquinto  |8        |East Daybreak Junior High  |
  |Gerardo Rounsaville|8        |East Daybreak Junior High  |
  |Gerardo Saltazor   |8        |East Daybreak Junior High  |
When I enter "Gerardo" into the "First Name" search box
And I enter "Rounsaville" into the "Last Name" search box
And I click the search button
Then "1" results are returned
And the search results include:
  |Student            |Grade    |School                     |
  |Gerardo Rounsaville|8        |East Daybreak Junior High  |

