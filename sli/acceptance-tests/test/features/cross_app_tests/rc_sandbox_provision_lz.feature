@RALLY_US4835
@rc
@sandbox
Feature: User provisions a landing zone, and preloads Small Sample Dataset

Background:
  Given I have an open web browser

  Scenario: Ingestion User Provisions LZ and Pre-loads Small Sample Dataset
	When I navigate to the Portal home page
    Then I will be redirected to realm selector web page
    When I click on the "Admin" realm in "Sandbox"
	And I was redirected to the "Simple" IDP Login page
	When I submit the credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the "Simple" login page  
    Then I should be on Portal home page
    Then I should see Admin link
    And I click on Admin
    Then I should be on the admin page
    And under System Tools, I click on "Provision Landing Zone"
    And I switch to the iframe
    When the developer selects to preload "Small Dataset"
    And I switch to the iframe
    Then I get the success message