Feature: <US435>

Scenario: Selecting classes on LOS

Given I am logged in using "CGray" "cgray"

When I look in the school drop-down
Then I only see Daybreak Central High

When I select Daybreak Central High
And I look in the course drop-down
Then I see these values in the drop-down: "Club/Team; Senior Homeroom; Writing about Government; 	Honors U.S. Literature; U.S. Literature"

When I select Daybreak Central High
And I select course U.S. Literature
Then I see these values in the class drop-down: "Sec 142; Sec 143; Sec 144; Sec 145"

When I select Daybreak Central High
And I select course U.S. Literature
And I select Sec 145
Then I see a list of 28 students
And the list includes: Johnny Patel and Carmen Ortiz



When I select Daybreak Central High
And I select course Writing about Government
Then I see these values in the class drop-down: Sec 923


When I select Daybreak Central High
And I select course U.S. Literature
And I select Sec 923
Then I see a list of 24 students