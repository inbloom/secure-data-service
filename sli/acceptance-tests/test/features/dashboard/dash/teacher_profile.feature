Feature: Teacher Profile View

As a teacher in a school district, I want to navigate to a list of teacher, and be directed to the teacher profile page.

Background:
Given I have an open web browser
Given that dashboard has been authorized for all ed orgs

@RALLY_US2817 @integration
Scenario: View teacher's profile
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
Then I should be redirected to the Dashboard landing page
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
And I click on the go button
 When I click on "Teacher" Tab
 And I see the following teachers:
 |Teachers                    |
 |Rebecca Braverman           |
 And I click on teacher "Rebecca Braverman"
And I look at Teacher Profile
And the teacher name shown is "Rebecca Braverman"
And I see the following current sections:
|Sections                       |
|Gym Class - 4                  |
|Mrs. Braverman's Homeroom #25  |
|Mrs. Braverman's Homeroom #33  |
|Mrs. Braverman's Homeroom #38  |
|Mrs. Braverman's Homeroom #9   |
And I click to see section "Mrs. Braverman's Homeroom #38"
Then I see a list of 25 students