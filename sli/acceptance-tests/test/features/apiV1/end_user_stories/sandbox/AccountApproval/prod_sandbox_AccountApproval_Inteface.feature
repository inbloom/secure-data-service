Feature: SLC operator approves/disables production accounts or disables sandbox accounts using the account approval application

Background: 
Given I have an open web browser
And LDAP server has been setup and running
And I am authenticated to SLI IDP as user "operator" with pass "operator1234"

Scenario: As a slc operator I want to see a list of all the accounts and their correct status
Given there are accounts in requests pending in the system
When I hit the Admin Application Account Approval page
Then I see a table with headings of "Vendor" and "User Name" and "Last Update" and "Status" and "Action"
And on the next line there is vendor name in the "Vendor" column
And User Name in the "User Name" column
And last update date in the "Last Updated" column
And status in the "Status" column
And the "Action" column has 4 buttons "Approve", "Reject", "Disable", and "Enable"

Scenario: As a slc operator I approve pending production account request by clicking the approve button
Given there is a "pending" production account request for vendor "Macro Corp" 
When I hit the Admin Application Account Approval page
Then I see one account with name "Loraine Plyler"
And his account status is "pending"
When I click the "Approve" button
 And I am asked "Do you really want to approve this user account?"
 When I click on Ok
Then his account status changed to "approved"

Scenario: As a slc operator I reject pending production account request by clicking the reject button
Given there is a "pending" production account request for vendor "Macro Corp"
When I hit the Admin Application Account Approval page
Then I see one account with name "Loraine Plyler"
And his account status is "pending"
When I click the "Reject" button
And I am asked "Do you really want to reject this user account?"
 When I click on Ok
Then his account status changed to "rejected"

Scenario: As a slc operator I disable an approved production account by clicking the reject button
Given there is a "approved" production account request for vendor "Macro Corp"
When I hit the Admin Application Account Approval page
Then I see one account with name "Loraine Plyler"
And his account status is "approved"
When I click the "Disable" button
And I am asked "Do you really want to disable this user account?"
 When I click on Ok
Then his account status changed to "disabled"

@wip
Scenario: As a slc operator an sandbox accounts are automatically approved 
Given there is an pending sandbox account  for vendor "Macro Corp" 
When I hit the Admin Application Account Approval page
Then I see one account with name "Loraine Plyler 1"
And his account status is "Approved"

@wip
Scenario: As a slc operator I disable an approved sandbox account by clicking the reject button
Given there is an approved sandbox account  for vendor "Macro Corp" 
When I hit the Admin Application Account Approval page
Then I see one account with name "Loraine Plyler 1"
And his account status is "Approved"
When I click the "Disable" button
And I am asked "Do you really want to disable this user account?"
 When I click on Ok
Then his account status changed to "Disabled"