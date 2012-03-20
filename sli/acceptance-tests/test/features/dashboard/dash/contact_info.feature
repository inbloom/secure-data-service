Feature:  View Student's contact information
As a teacher in a school district, I want to click on a student and be directed to their profile page that contains the student's contact info

Background:
Given I have an open web browser
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

@wip
Scenario: View a student with 1 contact
Given I click on student "Imelda Sydnee"
And I view its student profile
And the panel name is "Contact Information"
And there is "1" phone number
And the list of phone number includes "708-432-9033"
And the phone number type is "Unlisted"  
And the phone number "708-432-9033" is in bold
And there is "1" email address
And the list of email is "Imelda_Lindsey@yahoo.com"
And the email type is "other"
And there is "1" address
And the address is "1558 4th Street"
And the address 2 is "APT 7776"
And the city is "Chicago"
And the county is "Cook"
And the state is "IL"
And the country is "US"
And the postal code is "60139-2152"
And the address type is "Home
And there is "1" email address
And the list of email address includes "Imelda_Lindsey@yahoo.com"

@wip
Scenario: View a student with more than 1 contacts 
Given I click on student "Brielle Klein"
And I view its student profile
And the panel name is "Contact Information"
And there is "2" phone number
#rank order of phone number to be displayed
And the list of phone number includes "630-667-8063"
And the list of phone number includes "773-445-2464"
And the phone number "773-445-2464" is in bold
And there is "3" email address
And the list of email address includes "Brielle.Klein@yahoo.com"
And the list of email address includes "B.Klein@gmail.com"
And the list of email address includes "Brielle_Klein@gmail.com"
And there is "2" address
And the list of address includes "1537 Steuben Street"
And the address 2 is "APT 330"
And the city is "New York"
And the county is "New York"
And the state is "NY"
And the country is "US"
And the address type is "Physical"
And the postal code is "10753-2389"
And the list of address includes "8124 Brighton 3 Place"
And the address 2 is empty
And the city is "Chicago"
And the county is "US"
And the state is "IL"
And the country is "Cook"
And the postal code is "60848-7633"
And the address type is "Home"

@wip
Scenario:  Student has no phone numbers and no email addresses
Given I click on student "Patricia Harper"
And I view its profile
And there is "0" phone number
And there is "0" email address

@wip
Scenario: Student has no addresses
Given I click on student "Astra Vincent"
And I view its profile
And there is "0" address

@wip
Scenario: Include a building site number on its own line
Given I click on student "Jeanette Graves"
And I view its profile
And there is "1" address
And the list of address includes "3297 35th Street"
And the building site number is displayed in its own line with value "186D"
	
@wip
Scenario:  An address with countryCode other than US
Given I click on student "Odysseus Merrill"
And I view its profile
And there is "2" address
And the list of address includes "7472 Ross Street"
And the country is "CA"
And there is a additional line that shows the country "Canada"

@wip
Scenario:  primaryTelephoneNumber is not present
Given I click on student "Deirdre Gentry"
And I view its profile
And there is "1" phone number
And the list of phone numbers include "224-167-3247"
And the phone number "224-167-3247" is in bold

@wip
Scenario: Max (10) phone numbers 
Given the server is in "test" mode	

@wip	
Scenario: Max (10) email addresses
Given the server is in "test" mode

@wip
Scenario:  Max (4) address
Given the server is in "test" mode

@wip
Scenario: End Date in address is earlier than today's date
Given the server is in "test" mode

@wip
Scenario:  Begin Date in address is later than today's date
Given the server is in "test" mode
	
