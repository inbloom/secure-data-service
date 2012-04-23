@wip
Feature: SLC operator approves/disables production accounts or disables sandbox accounts using the account approval application

Background: SLC Operator is logged into the Account Approval Tool

Scenario: As a slc operator I want to see a list of all the accounts and their correct status
Given there an accounts in requests pending in the system
When I login the the tool
Then I see a table with headings of "Vendor" and "User Name" and "Approval Date" and "Status" and "Action"
And on the next line there is vendor name in the "Vendor" column
And User Name in the "User Name" column
And the "Approval Date" is empty
And the "Status" is "Pending"
And the "Action" column has 3 button "Approve", "Reject" and "Disable"

Scenario: As a slc operator I approve pending production account request by clicking the approve button
Given there is a production account request for vendor "Macro Corp" 
And first name "Loraine" and last name "Plyler" 
And login name "Lplyer@macrocorp.com" pending in the account request queue
When I click the "Approve" button
And the account is shown as "Accepted" and status is updated in the database

Scenario: As a slc operator I reject pending production account request by clicking the reject button
Given there is a production account request for vendor "Macro Corp" 
And first name "Loraine" and last name "Plyler" 
And login name "Lplyer@macrocorp.com" pending in the account request queue
When I click the "Reject" button
And the account is shown as "Rejected" and status is updated in the database

Scenario: As a slc operator I disable an approved production account by clicking the reject button
Given there is an approved production account  for vendor "Macro Corp" 
And first name "Loraine" and last name "Plyler" 
And login name "Lplyer@macrocorp.com" 
When I click the "Disabled" button
And the account is shown as "Disabled" and the status is updated in the database

Scenario: As a slc operator an sandbox accounts are automatically approved 
Given there is an pending sandbox account  for vendor "Macro Corp" 
And first name "Loraine" and last name "Plyler" 
And login name "Lplyer@macrocorp.com"  
And the account is shown as "Accepted" and the status is updated in the database

Scenario: As a slc operator I disable an approved sandbox account by clicking the reject button
Given there is an approved sandbox account  for vendor "Macro Corp" 
And first name "Loraine" and last name "Plyler" 
And login name "Lplyer@macrocorp.com" 
When I click the "Disable" button 
And the account is shown as "Disabled" and the status is updated in the database