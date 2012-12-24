

Feature: As an SLI application, I want to be able to visit all links presented to me


Background:
    Given format "application/vnd.slc+json"

Scenario: Starting from a teacher/home, crawl all links
	Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    When I start crawling at "/v1/home" 
    Then I should be able to visit all available links

Scenario: Starting from a staff/home, crawl all links
	Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    When I start crawling at "/v1/home" 
    Then I should be able to visit all available links
