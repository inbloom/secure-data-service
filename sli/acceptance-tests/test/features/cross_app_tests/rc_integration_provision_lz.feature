@rc
Feature:  RC Integration Tests

Background:
Given I have an open web browser

Scenario: Ingestion User Provisions LZ (Currently, SEA)
When I navigate to the Portal home page
When I selected the realm "Shared Learning Collaborative"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "testuser0.wgen@gmail.com" "test1234" for the "Simple" login page  
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Provision Landing Zone"
And I switch to the iframe
When I click the Provision button
And I switch to the iframe
Then I get the success message