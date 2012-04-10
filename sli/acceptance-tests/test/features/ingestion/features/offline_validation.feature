Feature: Offline Tool Simple ValidationTest

Background: I have a landing zone route configured
Given I am using default offline tool package
	And I am using local input file directory

Scenario: Run Offline Tool Validation for a Zip File
Given I post "Session1.zip" file as an input to offline validation tool
When I run offline validation command on input file
	And  "5" seconds have elapsed
Then I should see a log file in same directory
Then I should see "processing is complete" in the resulting log file

Scenario: Run Offline Tool Validation for a Ctl File
Given I post "Session2/Session2.ctl" control file as an input to offline validation tool
When I run offline validation command on input file
  And "10" seconds have elapsed
Then I should see a log file in same directory
Then I should see "processing is complete" in the resulting log file

Scenario: Run Offline Tool Validation for DemoData
Given I post "DemoData.zip" file as an input to offline validation tool
When I run offline validation command on input file
  And "10" seconds have elapsed
Then I should see a log file in same directory
Then I should see "DemoData.zip] processing is complete" in the resulting log file
