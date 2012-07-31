@RALLY_3071
Feature: Super Admin Interface

	
Background: 
Given I have an open web browser
And LDAP server has been setup and running
And I already have a SLC Operator account


Scenario: As a SLC Operator I can cancel adding an account 
When I navigate to the User Management Page 
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I am redirected to "Admin Account Management" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

When I click "Cancel" link
Then I am redirected to "Admin Account Management" page 


Scenario: As a SLC Operator I can add an account 
Given the prod testing user does not already exists in LDAP
When I navigate to the User Management Page 
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I am redirected to "Admin Account Management" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

And I can update the "Full Name" field to "Superadmin AcceptanceTest"
And I can update the "Email" field to "prodtestuser@testwgen.net"

And I can select "SEA Administrator" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can update the "Tenant" field to "IL1212"
And I can update the "Tenant" field to "IL1212"
And I can update the "EdOrg" field to "IL-DAYBREAK"

When I click button "Save"
Then I am redirected to "Admin Account Management" page
And a "Success" message is displayed 

And the new user has "Tenant" updated to "IL1212" 
And the new user has "EdOrg" updated to "IL-DAYBREAK" 
And the user now has roles "SEA Administrator" and "Realm Administrator"

#And an email to verify user email address is sent

Scenario: As a SLC Operator I can't add tenant to an SLC Operator account
When I navigate to the User Management Page 
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I am redirected to "Admin Account Management" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

Then I can select "SLC Administrator" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And the "Tenant" textbox is disabled 


Scenario: As a SEA Admin I can only create certain roles
Given I have a valid account as a SEA Administrator
Given the prod testing user does not already exists in LDAP
When I navigate to the User Management Page 
And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
Then I am redirected to "Admin Account Management" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page
And There is no textbox for "Tenant"

Then I can update the "Full Name" field to "Superadmin AcceptanceTest"
And I can update the "Email" field to "prodtestuser@testwgen.net"
And I can select "SEA Administrator" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I do not see "SLC Operator" Role
And I can also check "Realm Administrator" Role 
And I can also check "Ingestion User" Role 

Then I can select "LEA Administrator" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can change the EdOrg dropdown to "IL-DAYBREAK"

When I click button "Save"
Then I am redirected to "Admin Account Management" page 
And a "Success" message is displayed 

And the user has Roles as "LEA Administrator, Realm Administrator, Ingestion User"
#And the new user has the same "Tenant" field as "IL Admin" has
#And a new unique account has been created with the same tenant_id and "Ed-Org" as "IL-DAYBREAK" and "Role" as "LEA Administrator" and "Realm  Adminstrator" and "Ingestion User"
#And an email to verify user email address is sent
#
#
#Scenario: As a LEA Admin I can only create certain roles
#Given I have a valid account as a LEA Administrator
#When I go to "Add a User" form 
#Then the only options in the "Role" drop-down are "LEA Administrator", "Ingestion User", "Realm Administrator"
#And  I can select either or both optional roles of "Ingestion User"  "Realm Administrator"
#
#When I select role "Ingestion User"
#And  I select both "Realm Administrator" and "Ingestion User"
#And I enter an EdOrg as "IL-DAYBREAK"
#And I click "Save"
#Then I am redirected to the "User Account Management" Page
#And a "Success" message is displayed 
#And a new unique account has been created with the same tenant_id and "Ed-Org" as "IL-DAYBREAK" and "Role" as "SEA Administrator" and "Realm  Adminstrator" and "Ingestion User"
#And an email to verify user email address is sent
#
#	
#Scenario: As a SLC Operator I can edit an account
#When I access the User Management Page from the portal
#Then I am redirected to "User Account Management" page which has a table of all accounts for my tenancy
#
#When I hover over a row which is not my name
#Then I am shown the option to "Edit" user
#
#When I click on "Edit" link
#Then I am redirected to "Add a User" form
#And the title is "Edit a User"
#And the Fullname is prefilled
#And the Email is prefilled
#And the EdOrg is prefilled
#And the tenant is prefilled
#And the Role is selected
#
#When I update the email address
#And I update the Role to "LEA Administrator"
#And I click "Save"
#Then I am redirected to the "User Account Management" Page
#And a "Success" message is displayed 
#And the updated information is displayed in the table
#
#When I click "Cancel"
#Then I am redirected to the "User Account Management" Page
#And no changes are shown in the table
#
#Scenario: As a SLC Operator I can delete an account
#When I access the User Management Page from the portal
#Then I am redirected to "User Account Management" page which has a table of all accounts for my tenancy
#
#When I hover over a row which is not my name
#Then I am shown the option to "Delete" user
#
#When I click on "Delete" icon
#Then I am asked to confirm the delete action
#
#When I confirm the delete action
#Then that user is removed from LDAP
#And the user entry is removed from the table
#
#
#Scenario: As a Admin I cannot delete an account
#When I access the User Management Page from the portal
#Then I am redirected to "User Account Management" page which has a table of all accounts for my tenancy
#
#When I hover over a row which has my name
#Then I am not shown the option to "Delete"
#
#
#Scenario: As a LEA Administrator I can edit limited fields on my  account
#Given I have a account as a "LEA Administrator"
#When I access the User Management Page from the portal 
#Then I am redirected to "User Account Management" page which has a table of all accounts for my tenancy
#
#When I hover over a row has my name
#Then I am shown the option to "Edit" user
#
#When I click on "Edit" link
#Then I am redirected to "Add a User" form
#And the title is "Edit a User"
#And I can update email address
#And I can update the Fullname 
#And I cannot update any other field 
#
#When I click "Save"
#Then I am redirected to the "User Account Management" Page
#And a "Success" message is displayed 
#And the updated information is displayed in the table
#
#When I click "Cancel"
#Then I am redirected to the "User Account Management" Page
#And no changes are shown in the table
#
