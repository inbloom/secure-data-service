@RALLY_US180
@RALLY_US1703
@RALLY_US1968
Feature: Offline Tool Simple ValidationTest

Background: I have a landing zone route configured
Given I am using default offline tool package
	And I am using local input file directory

Scenario: Run Offline Tool Validation for a Zip File
Given I post "Session1.zip" file as an input to offline validation tool
When I run offline validation command on input file
	And  "5" seconds have elapsed
Then I should see a log file in same directory
Then I should see "VALIDATION_0006" in the resulting log file

Scenario: Run Offline Tool Validation for a Ctl File
Given I post "Session2/Session2.ctl" control file as an input to offline validation tool
When I run offline validation command on input file
  And "10" seconds have elapsed
Then I should see a log file in same directory
Then I should see "VALIDATION_0009" in the resulting log file

Scenario: Run Offline Tool Validation for DemoData
Given I post "DemoData.zip" file as an input to offline validation tool
When I run offline validation command on input file
  And "10" seconds have elapsed
Then I should see a log file in same directory
Then I should see "VALIDATION_0006" in the resulting log file


Scenario: Run Offline Tool Validation against Ed-Fi XSD
Given I post "XsdValidation.zip" file as an input to offline validation tool
When I run offline validation command on input file
 And "5" seconds have elapsed
Then I should see a log file in same directory

Then I should see "BASE_0027" in the resulting log file
Then I should see "line-56,column-28" in the resulting log file
Then I should see "InterchangeStudent.xml:" in the resulting log file
Then I should see "cvc-complex-type.2.4.a: Invalid content was found starting with element 'EconomicDisadvantaged'. One of '{" in the resulting log file

Then I should see "BASE_0027" in the resulting log file
Then I should see "line-87,column-60" in the resulting log file
Then I should see "InterchangeStudent.xml:" in the resulting log file
Then I should see "cvc-type.3.1.3: The value '' of element 'LimitedEnglishProficiency' is not valid." in the resulting log file
