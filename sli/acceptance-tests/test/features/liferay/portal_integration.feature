@integration
Feature:  Portal Integration Tests

Background:
Given that dashboard has been authorized for all ed orgs
Given I have an open web browser
When I navigate to the Portal home page

Scenario: Admin Logs into dashboard from portal
When I select "Illinois Daybreak School District 4529" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under Application Configuration, I click on "inBloom Dashboards"
Then I am authorized to the Configuration Area

Scenario: Educator Logs into dashboard from portal
When I select "Illinois Daybreak School District 4529" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page    
Then I should be on Portal home page
And under My Applications, I see the following apps: "inBloom Dashboards"
And under My Applications, I click on "inBloom Dashboards" 
Then I should be redirected to the Dashboard landing page
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
And I click on the go button
And I view the School Profile
And I click on subject "English Language and Literature"
And I click on course "8th Grade English"
And I click on section "8th Grade English - Sec 6"
Then I see a list of 28 students
