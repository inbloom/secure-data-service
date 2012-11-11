Feature:  Simple Student Search

Background:
Given I have an open web browser
Given that dashboard has been authorized for all ed orgs

@integration @RALLY_US197
Scenario: Search by First name
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
When I enter "Matt" into the "firstName" search box
And I click the search button
When I look in the school drop-down
Then I see these values in the drop-down: "East Daybreak Junior High"
And I select school "East Daybreak Junior High" 
And I search by clicking on the go button
Then "1" results are returned in the page
#Then I should be informed that "1" results are returned
#Then "1" results are returned in the page
And the title of the page is "SLC - Search"
And the search results has the following entries:
  |Student      |Grade    |School                     |
  |Matt Sollars |8        |East Daybreak Junior High  |

And I enter "matt" into the "firstName" search box
And I click the search button
And I select school "East Daybreak Junior High" 
And I search by clicking on the go button
Then "1" results are returned in the page
And the search results has the following entries:
 |Student      |Grade    |School                     |
  |Matt Sollars |8        |East Daybreak Junior High  |

@integration @RALLY_US197
Scenario: Search by Last name
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
When I enter "Sollars" into the "lastName" search box
And I click the search button
When I look in the school drop-down
Then I see these values in the drop-down: "East Daybreak Junior High"
And I select school "East Daybreak Junior High" 
And I search by clicking on the go button
#And I send the enter key
#Then I should be informed that "1" results are returned
Then "1" results are returned in the page
And the search results has the following entries:
  |Student      |Grade    |School                     |
  |Matt Sollars |8        |East Daybreak Junior High  |
When I enter "sollars" into the "lastName" search box
And I click the search button
And I select school "East Daybreak Junior High" 
And I search by clicking on the go button
Then I should be informed that "1" results are returned
Then "1" results are returned in the page
And the search results has the following entries:
  |Student      |Grade    |School                     |
  |Matt Sollars |8        |East Daybreak Junior High  |
And I click on student "Matt Sollars"
And I view its student profile
And I click on the browser back button
Then I should be informed that "1" results are returned
And "1" results are returned in the page
And the search results has the following entries:
  |Student      |Grade    |School                     |
  |Matt Sollars |8        |East Daybreak Junior High  |

@integration @RALLY_US197
Scenario:  Search by non-existing student
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
When I enter "Tran" into the "lastName" search box
And I click the search button
And I select school "East Daybreak Junior High" 
And I search by clicking on the go button
Then I should be informed that "0" results are returned
Then "0" results are returned in the page

@integration @RALLY_US197
Scenario:  Empty Search
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
When I enter nothing into either field of student search
And I click the search button
And I select school "East Daybreak Junior High" 
And I search by clicking on the go button
Then I should be informed that "0" results are returned
Then "0" results are returned in the page
And I enter "Gerardo" into the "lastName" search box
And I click the search button
And I select school "East Daybreak Junior High" 
And I search by clicking on the go button
Then I should be informed that "0" results are returned
Then "0" results are returned in the page

@integration @RALLY_US197
Scenario:  Search with more than 1 results
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
When I enter "Gerardo" into the "firstName" search box
And I click the search button
And I select school "East Daybreak Junior High" 
And I search by clicking on the go button
Then I should be informed that "3" results are returned
Then "3" results are returned in the page
And the search results has the following entries:
  |Student            |Grade    |School                     |
  |Gerardo Giaquinto  |8        |East Daybreak Junior High  |
  |Gerardo Rounsaville|8        |East Daybreak Junior High  |
  |Gerardo Saltazor   |8        |East Daybreak Junior High  |
And I click on student "Gerardo Giaquinto"
And I view its student profile
And I click on the browser back button
Then I should be informed that "3" results are returned
And "3" results are returned in the page
And the search results has the following entries:
  |Student            |Grade    |School                     |
  |Gerardo Giaquinto  |8        |East Daybreak Junior High  |
  |Gerardo Rounsaville|8        |East Daybreak Junior High  |
  |Gerardo Saltazor   |8        |East Daybreak Junior High  |
When I enter "Gerardo" into the "firstName" search box
And I enter "Rounsaville" into the "lastName" search box
And I click the search button
And I select school "East Daybreak Junior High" 
And I search by clicking on the go button
Then I should be informed that "1" results are returned
Then "1" results are returned in the page
And the search results has the following entries:
  |Student            |Grade    |School                     |
  |Gerardo Rounsaville|8        |East Daybreak Junior High  |

@integration @RALLY_US197
Scenario:  Search pagination
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "manthony" "manthony1234" for the "Simple" login page
When I enter "Matt" into the "firstName" search box
And I click the search button
And I select school "Sunset Central High School" 
And I search by clicking on the go button
Then I should be informed that "53" results are returned
And "50" results are returned in the page
And I click on the next page
And "3" results are returned in the page
And I select page size of "100"
And "53" results are returned in the page
And the search results include:
  |Student            |Grade    |School                     |
  |Matt Lin           |11       |Sunset Central High School |
When I enter "Lin" into the "lastName" search box
And I click the search button
And I select school "Sunset Central High School" 
And I search by clicking on the go button
Then I should be informed that "51" results are returned
And "50" results are returned in the page
And I click on the next page
And "1" results are returned in the page
And I click on the previous page
And "50" results are returned in the page
And I select page size of "100"
And "51" results are returned in the page
And the search results include:
  |Student            |Grade    |School                     |
  |Jeremy Lin         |11       |Sunset Central High School |

