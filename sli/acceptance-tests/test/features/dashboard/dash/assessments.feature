Feature: Display simple assessment contents

As a SEA/LEA user, I want to be able to select different views in my dashboard
application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "live" mode

 @RALLY_US200
Scenario: Displaying simple StateTest reading and writing results for all students
    When I navigate to the Dashboard home page
    When I select "Illinois Sunset School District 4526" and click go
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
    When I select <edOrg> "Daybreak School District 4529"
      And I select <school> "East Daybreak Junior High"
      And I select <course> "8th Grade English"
      And I select <section> "8th Grade English - Sec 6"
    And I select view "Middle School ELA View"

	#The test now runs against SDS data
	 #Highest Ever reading
     And the scale score for assessment "StateTest Reading" for student "Matt Sollars" is "199"
     #Most recent writing
     And the scale score for assessment "StateTest Writing" for student "Matt Sollars" is "1"
