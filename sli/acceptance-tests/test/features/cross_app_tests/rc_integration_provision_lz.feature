@RALLY_US4835
@rc
Feature:  RC Integration Tests

Background:
Given I have an open web browser

Scenario: Ingestion User Provisions LZ (Currently, SEA)
When I navigate to the Portal home page
When I see the realm selector I authenticate to "inBloom"
#When I selected the realm "inBloom"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "<SECONDARY_EMAIL>" "<SECONDARY_EMAIL_PASS>" for the "Simple" login page
Then I should be on Portal home page
And under System Tools, I click on "Landing Zone"
When I click the Provision button
Then I get the success message