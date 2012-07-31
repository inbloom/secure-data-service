Feature: Section Profile View

As a teacher in a school district, I want to navigate to a section, and be directed to the section profile page.

Background:
Given I have an open web browser
Given that dashboard has been authorized for all ed orgs

@RALLY_US2817
Scenario: View section's profile
When I navigate to the Dashboard home page
When I select "Illinois Daybreak School District 4529" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
Then I should be redirected to the Dashboard landing page
And I go to Teacher Profile
And the teacher name shown is "Linda Kim"
And I see the following current sections:
|Sections                     |
|7th Grade English - Sec. 1   |
|7th Grade English - Sec. 2   |
|7th Grade English - Sec. 3   |
|8th Grade English - Sec 4    |
|8th Grade English - Sec 5    |
|8th Grade English - Sec 6   |
|Mrs. Kim's Homeroom          |
|Spartans Softball Team       |
And I click to see section "8th Grade English - Sec 6"
Then I see a list of 25 students