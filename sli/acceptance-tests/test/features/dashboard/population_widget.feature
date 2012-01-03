Feature: <US435>

Scenario: Selecting classes on LOS

Given I am logged in using "cgray" "cgray"
#	And I selected the "Dashboard" application

When I look in the school drop-down
Then I only see "Daybreak Central High"

When I select school "Daybreak Central High"
And I look in the course drop-down
Then I see these values in the drop-down: "Club/Team;Senior Homeroom;Writing about Government;Honors American Literature;American Literature"

When I select school "Daybreak Central High"
And I select course "American Literature"
Then I see these values in the drop-down: "Sec 142;Sec 143;Sec 144;Sec 145"

When I select school "Daybreak Central High"
And I select course "American Literature"
And I select section "Sec 144"
Then I see a list of 24 students

When I select school "Daybreak Central High"
And I select course "Writing about Government"
Then I see these values in the section drop-down: "Sec 923"

When I select school "Daybreak Central High"
And I select course "American Literature"
And I select section "Sec 145"
Then I see a list of 28 students
And the list includes: "Johnny Patel;Carmen Ortiz"
