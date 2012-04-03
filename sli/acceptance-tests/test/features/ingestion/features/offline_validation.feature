Feature: Offline Tool Simple ValidationTest

Background: I have a landing zone route configured
Given I am using default offline tool package

Scenario: Run Offline Tool Validation for a Zip File
Given I post "Session1.zip" file as an input to offline validation tool
When "5" seconds have elapsed
Then I should see a log file in same directory
