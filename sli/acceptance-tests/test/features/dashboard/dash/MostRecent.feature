Feature: Display simple assessment contents

As a SEA/LEA user, I want to be able to select different views in my dashboard
application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "live" mode

Scenario: Displaying simple ISAT reading results for all students
    When I navigate to the Dashboard home page
    When I select "Illinois Sunset School District 4526" and click go
    When I login as "linda.kim" "linda.kim1234"
      When I select <edOrg> "Daybreak School District 4529"
        And I select <school> "East Daybreak Junior High"
        And I select <course> "8th Grade English"
        And I select <section> "8th Grade English - Sec 6"
      And I select view "Middle School ELA View"
	
	Then I should see a table heading "ISAT Reading"
	And I should see a field "SS" in this table
	And I should see  "Matt Sollars" in student field
	And I should see his/her ISAT Reading Scale Score is "195"
	

Scenario: Displaying most recent ISAT writing results for all students
    When I navigate to the Dashboard home page
    When I select "Illinois Sunset School District 4526" and click go
    When I login as "linda.kim" "linda.kim1234"
      When I select <edOrg> "Daybreak School District 4529"
        And I select <school> "East Daybreak Junior High"
        And I select <course> "8th Grade English"
        And I select <section> "8th Grade English - Sec 6"
      And I select view "Middle School ELA View"
    
    Then I should see a table heading "ISAT Writing (most recent)"
	And I should see a field "SS" in this table
	And I should see  "Matt Sollars" in student field
	And I should see his/her ISAT Writing Scale Score is "1"