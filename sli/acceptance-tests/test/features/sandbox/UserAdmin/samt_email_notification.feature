@US_3455
Feature: As a new SAMT account holder I get an email when my account is created.

Background: 
Given I have an open web browser
And I have a SMTP/Email server configured

@sandbox
Scenario: An admin creates a sandbox account with a valid email, initial notification is sent

Given the testing user "peacefrog@slidev.org" does not already exists in LDAP
When I navigate to the sandbox user account management page
And I submit the credentials "sandboxadministrator" "sandboxadministrator1234" for the "Simple" login page
Then I am redirected to "Sandbox Account Management" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

And I enter Full Name "firstName lastName" and Email "peacefrog@slidev.org" into the required fields
And I can select "Application Developer" from a choice between "Sandbox Administrator, Application Developer, Ingestion User" Role 
And I can also check "Ingestion User" Role 

When I click button "Save"
#Then I am redirected to "Sandbox Account Management" page 
Then a "Success! You have added a new user" message is displayed
And a verify email notification is sent to user

@production
Scenario: An admin creates a production account with a valid email, initial notification is sent

Given the prod testing user "peacefrog@slidev.org" does not already exists in LDAP
When I navigate to the User Management Page 
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I am redirected to "Manage Administrator Accounts" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

And I enter Full Name "firstName lastName" and Email "peacefrog@slidev.org" into the required fields
And I can select "SEA Administrator" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can update the "Tenant" field to "IL1212"
And I can update the "EdOrg" field to "IL-DAYBREAK"

When I click button "Save"
#Then I am redirected to "Manage Administrator Accounts" page
Then a "Success" message is displayed 
And a verify email notification is sent to user

# This was manually tested in RC and it works as described. It is not possible to test this locally email server does not reject invalid email.
@wip
Scenario: An admin creates an account with a invalid email, initial notification is sent, exception caught
Given I have access to SAMT tool
And I create an account with a invalid email
Then a verify email notification is sent to the user	
And any exception is caught by the system
