Feature:  View Student's contact information
As a teacher in a school district, I want to click on a student and be directed to their profile page that contains the student's contact info

Background:
Given I have an open web browser

@wip
Scenario: View a student  with 1 contact
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"
When I click on the Dashboard page
And I wait for "1" seconds
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "M. Jones - 8th Grade English - Sec 6"
And I wait for "2" seconds
And I click on student "Imelda Sydnee"
And I view its student profile
And the panel name is "Contact Information"
And there is "1" phone number listed
And the phone number is "708-432-9033"
And the phone number type is "Unlisted"
And phone number index "1" is in bold
And the email is "Imelda_Lindsey@yahoo.com"
And the email type is "other"
And the address is "1558 4th Street"
And the address type is "..."

@wip 
Scenario: View a student with more than 1 contact info
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"
#rank order of phone number to be displayed

@wip
Scenario:  PrimaryTelephoneNumberIndicator is recognzied
Given the server is in "test" mode	
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"

@wip
Scenario: End Date in address is earlier than today's date
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"

@wip
Scenario:  Begin Date in address is later than today's date
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"

@wip
Scenario:  Zero phone number
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"

@wip
Scenario: Zero email addresses
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"

@wip
Scenario: Max (10) phone numbers
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"	

@wip	
Scenario: Max (10) email addresses
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"

@wip
Scenario: Zero address
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"

@wip
Scenario:  Max (4) address
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"

@wip
Scenario: Include a building site number on its own line
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"
	
@wip
Scenario:  An address with countryCode other than US
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"

@wip
Scenario:  primaryTelephoneNumber is not present
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"

@wip
Scenario: Student with no contact info
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"
	
