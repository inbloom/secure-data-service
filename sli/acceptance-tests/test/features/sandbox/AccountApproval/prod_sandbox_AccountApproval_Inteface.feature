
Feature: SLC operator approves/disables production accounts or disables sandbox accounts using the account approval application

Background: 
Given I have an open web browser
And LDAP server has been setup and running
And I navigate to the account management page
And I submit the credentials "slcoperator-email@slidev.org" "slcoperator-email1234" for the "Simple" login page

@production
Scenario: As a slc operator I want to see a list of all the accounts and their correct status
Given there are accounts in requests pending in the system
When I hit the Admin Application Account Approval page
Then I see a table with headings of "Vendor" and "Username" and "Last Update" and "Status" and "Actions"
And on the next line there is vendor name in the "Vendor" column
And User Name in the "User Name" column
And last update date in the "Last Updated" column
And status in the "Status" column
And the "Action" column has 4 buttons "Approve", "Reject", "Disable", and "Enable"

@production
Scenario: As a slc operator I approve pending production account request by clicking the approve button
Given there is a "pending" production account request for vendor "Macro Corp" 
When I hit the Admin Application Account Approval page
Then I see one account with name "Loraine Plyler"
And his account status is "pending"
When I click the "Approve" button
 And I am asked "Do you really want to approve this user account?"
 When I click on Ok
Then his account status changed to "approved"

@production
Scenario: As a slc operator I reject pending production account request by clicking the reject button
Given there is a "pending" production account request for vendor "Macro Corp"
When I hit the Admin Application Account Approval page
Then I see one account with name "Loraine Plyler"
And his account status is "pending"
When I click the "Reject" button
And I am asked "Do you really want to reject this user account?"
 When I click on Ok
Then his account status changed to "rejected"

@production
Scenario: As a slc operator I disable an approved production account by clicking the reject button
Given there is a "approved" production account request for vendor "Macro Corp"
When I hit the Admin Application Account Approval page
Then I see one account with name "Loraine Plyler"
And his account status is "approved"
When I click the "Disable" button
And I am asked "Do you really want to disable this user account?"
 When I click on Ok
Then his account status changed to "disabled"

@sandbox
Scenario: As a slc operator an sandbox accounts are automatically approved 
Given there is an approved sandbox account  for vendor "Macro Corp" 
When I hit the Admin Application Account Approval page
Then I see one account with name "Loraine Plyler"
And his account status is "approved"

@sandbox
Scenario: As a slc operator I disable an approved sandbox account by clicking the reject button
Given there is an approved sandbox account  for vendor "Macro Corp" 
When I hit the Admin Application Account Approval page
Then I see one account with name "Loraine Plyler"
And his account status is "approved"
When I click the "Disable" button
And I am asked "Do you really want to disable this user account?"
 When I click on Ok
Then his account status changed to "disabled"
