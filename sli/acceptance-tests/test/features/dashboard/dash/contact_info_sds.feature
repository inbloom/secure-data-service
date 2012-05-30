Feature:  Student Contact Info (SDS)
As a teacher in a school district, I want to click on a student and be directed to their profile page that contains the student's contact info

Background:
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go

@integration  @RALLY_US198  @RALLY_US147
Scenario: Student has 1 email, 1 address, 1 phone
When I login as "rbraverman" "rbraverman1234"
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
When I select course "1st Grade Homeroom"
When I select section "Mrs. Braverman's Homeroom #38"
Given I click on student "Dalia Pingel"
And I view its student profile
And I look at the panel "Contact Information"
And I look at "Student" Contact Info
And there are "1" phone numbers
And the list of phone number includes "708-432-9033"
And the phone number "708-432-9033" is of type "Unlisted"  
And there are "1" email addresses
And the list of email address includes "Dalia_Pingel@yahoo.com"
And the email "Dalia_Pingel@yahoo.com" is of type "Other"
And there are "1" addresses
And the list of address includes
"""
1558 4th Street, APT 7776
Chicago, IL 60139-2152
"""
And the address "1558 4th Street" is of type "Home"

@integration @RALLY_US198  @RALLY_US147
Scenario: Student has 2 phone numbers, 3 emails, 2 addresses
When I login as "rbraverman" "rbraverman1234"
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
When I select course "1st Grade Homeroom"
When I select section "Mrs. Braverman's Homeroom #38"
Given I click on student "Felipe Rafalski"
And I view its student profile
And I look at the panel "Contact Information"
And there are "2" phone numbers
And the list of phone number includes "630-667-8063"
And the list of phone number includes "773-445-2464"
And the order of the phone numbers is "773-445-2464;630-667-8063"
And there are "3" email addresses
And the list of email address includes "felipe.rafalski@yahoo.com"
And the list of email address includes "f.rafalski@gmail.com"
And the list of email address includes "Felipe_Rafalski@gmail.com"
And the email "felipe.rafalski@yahoo.com" is of type "Home/Personal"
And the email "f.rafalski@gmail.com" is of type "School E-mail"
And the email "Felipe_Rafalski@gmail.com" is of type "Other"
And the order of the email addresses is "felipe.rafalski@yahoo.com;f.rafalski@gmail.com;Felipe_Rafalski@gmail.com"
And the list of address includes 
"""
8124 Brighton 3 Place
Chicago, IL 60848-7633
"""
And the list of address includes 
"""
1537 Steuben Street, APT 330
New York, NY 10753-2389
"""
And the address "8124 Brighton 3 Place" is of type "Home"
And the address "1537 Steuben Street" is of type "Physical"

@integration @RALLY_US198  @RALLY_US147
Scenario:  Student has no email and no address
When I login as "linda.kim" "linda.kim1234"
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
Given I click on student "Mayme Borc"
And I view its student profile
And I look at the panel "Contact Information"
And there are "0" email addresses
And there are "0" addresses

@integration @RALLY_US198  @RALLY_US147
Scenario:  Student has no phone numbers
When I login as "linda.kim" "linda.kim1234"
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
Given I click on student "Malcolm Costillo"
And I view its student profile
And I look at the panel "Contact Information"
And there are "0" phone numbers

@integration @RALLY_US198  @RALLY_US147
Scenario: Address has buildingSiteNumber and not in US
When I login as "rbraverman" "rbraverman1234"
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
When I select course "1st Grade Homeroom"
When I select section "Mrs. Braverman's Homeroom #38"
Given I click on student "Bennie Cimmino"
And I view its student profile
And I look at the panel "Contact Information"
And there are "2" addresses
And the list of address includes 
"""
3655 Brighton 2nd Lane, APT 4221
351
Chicago, IL 60908-1028
"""
And the list of address includes 
"""
9857 Kingsborough 2nd Walk
Toronto, WA A1B 2C3
CA
"""

@integration @RALLY_US198  @RALLY_US147
Scenario:  primaryTelephoneNumber is not present
When I login as "linda.kim" "linda.kim1234"
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
Given I click on student "Mayme Borc"
And I view its student profile
And I look at the panel "Contact Information"
And there are "1" phone numbers
And the list of phone number includes "708-929-8507"

@integration @RALLY_US198  @RALLY_US147
Scenario:  primaryTelephoneNumber is not the Home Number
When I login as "linda.kim" "linda.kim1234"
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
Given I click on student "Lyn Consla"
And I view its student profile
And I look at the panel "Contact Information"
And there are "6" phone numbers
And the order of the phone numbers is "219-828-9469;630-196-0500;779-503-8057;219-470-5216;630-971-1951;262-007-3326"

@integration @RALLY_US198  @RALLY_US147
Scenario:  Check address sorting order
When I login as "rbraverman" "rbraverman1234"
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
When I select course "1st Grade Homeroom"
When I select section "Mrs. Braverman's Homeroom #38"
Given I click on student "Dara Nemecek"
And I view its student profile
And I look at the panel "Contact Information"
And there are "2" addresses 
And the list of address includes 
"""
4256 Wyckoff Street
New York, NY 10473-1436
"""
And the list of address includes
"""
4606 Albemarle Road, APT 5760
Chicago, IL 60457-1957
"""
And the order of the addressess is "4256 Wyckoff Street;4606 Albemarle Road"

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
Scenario: Begin Date in address is later than today's date
Given the server is in "test" mode
	
