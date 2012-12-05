@RALLY_3071
Feature: Super Admin Interface

	
Background: 
Given I have an open web browser
And LDAP server has been setup and running
And I already have a SLC Operator account


Scenario: As a SLC Operator I can cancel adding an account 
When I navigate to the User Management Page 
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I am redirected to "Manage Administrator Accounts" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

When I click "Cancel" link
Then I am redirected to "Manage Administrator Accounts" page 


Scenario: As a SLC Operator I can add an account 
Given the prod testing user does not already exists in LDAP
When I navigate to the User Management Page 
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I am redirected to "Manage Administrator Accounts" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

And I can update the "Full Name" field to "Superadmin AcceptanceTest"
And I can update the "Email" field to "prodtestuser@testwgen.net"

And I can select "SEA Administrator" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can update the "Tenant" field to "IL1212"
And I can update the "EdOrg" field to "IL-DAYBREAK"

When I click button "Save"
Then I am redirected to "Manage Administrator Accounts" page
And a "Success" message is displayed 

And the new user has "Tenant" updated to "IL1212" 
And the new user has "EdOrg" updated to "IL-DAYBREAK" 
And the user now has roles "SEA Administrator" and "Realm Administrator"

#And an email to verify user email address is sent

Scenario: As a SLC Operator I can't add tenant to an SLC Operator account
When I navigate to the User Management Page 
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I am redirected to "Manage Administrator Accounts" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

Then I can select "SLC Operator" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And the "Tenant" textbox is disabled 


Scenario: As a SEA Admin I can only create certain roles
Given I have a valid account as a SEA Administrator
Given the prod testing user does not already exists in LDAP
When I navigate to the User Management Page 
And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
Then I am redirected to "Manage Administrator Accounts" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page
And There is no textbox for "Tenant"

Then I can update the "Full Name" field to "Superadmin AcceptanceTest"
And I can update the "Email" field to "prodtestuser@testwgen.net"
And I can select "SEA Administrator" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can also check "Ingestion User" Role 

Then I can select "LEA Administrator" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can change the EdOrg dropdown to "IL-DAYBREAK"

When I click button "Save"
Then I am redirected to "Manage Administrator Accounts" page 
And a "Success" message is displayed 

And the new user has "EdOrg" updated to "IL-DAYBREAK" 
And the new user has Roles as "LEA Administrator, Realm Administrator, Ingestion User"
And the new user has the same "Tenant" field as "IL Admin" has
#And an email to verify user email address is sent

Scenario: As a SLC Operator I can not create ingestion user or realm admin in a district without LEAs
Given the prod testing user does not already exists in LDAP
Given there is no users in edorg "IL-NIGHTFALL" 
When I navigate to the User Management Page 
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I am redirected to "Manage Administrator Accounts" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

And I can update the "Full Name" field to "Superadmin AcceptanceTest"
And I can update the "Email" field to "prodtestuser@testwgen.net"

And I can select "Ingestion User" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can update the "Tenant" field to "Midgar"
And I can update the "EdOrg" field to "IL-NIGHTFALL"
When I click button "Save"
Then a "there is no LEA Administrator" message is displayed 

Then I can select "Realm Administrator" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
When I click button "Save"
Then a "there is no LEA Administrator" message is displayed 

#Can't save this LEA, otherwise other people's test will fail

#Then I can select "LEA Administrator" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
#When I click button "Save"
#Then I am redirected to "Manage Administrator Accounts" page 
#And a "Success" message is displayed 

##should be able to create Realm Administrator now after a LEA is created
#Then I click on "Add User" button
#And I am redirected to "Add a User" page
#Then I can update the "Full Name" field to "Superadmin AcceptanceTest"
#And I can update the "Email" field to "prodtestuser2@testwgen.net"
#Then I can select "Realm Administrator" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
#And I can update the "Tenant" field to "Midgar"
#And I can update the "EdOrg" field to "IL-NIGHTFALL"
#When I click button "Save"
#And a "Success" message is displayed 

Scenario: As a SEA Admin I can not create ingestion user or realm admin in a district without LEAs
Given the prod testing user does not already exists in LDAP
Given there is no users in edorg "IL-NIGHTFALL" 
When I navigate to the User Management Page 
And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
Then I am redirected to "Manage Administrator Accounts" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page
Then I can update the "Full Name" field to "Superadmin AcceptanceTest"
And I can update the "Email" field to "prodtestuser@testwgen.net"
Then I can select "Ingestion User" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can change the EdOrg dropdown to "IL-NIGHTFALL"
When I click button "Save"
And a "there is no LEA Administrator" message is displayed 
Then I can select "Realm Administrator" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
When I click button "Save"
And a "there is no LEA Administrator" message is displayed 

#Can't save this LEA, otherwise other people's test will fail

#Then I can select "LEA Administrator" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
#When I click button "Save"
#Then I am redirected to "Manage Administrator Accounts" page 
#And a "Success" message is displayed 
##should be able to create Ingestion user now after a LEA is created
#Then I click on "Add User" button
#And I am redirected to "Add a User" page
#Then I can update the "Full Name" field to "Superadmin AcceptanceTest"
#And I can update the "Email" field to "prodtestuser2@testwgen.net"
#Then I can select "Ingestion User" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
#And I can change the EdOrg dropdown to "IL-NIGHTFALL"
#When I click button "Save"
#And a "Success" message is displayed 

Scenario: As a LEA Admin I can only create certain roles
Given I have a valid account as a LEA Administrator
Given the prod testing user does not already exists in LDAP
When I navigate to the User Management Page 
And I submit the credentials "daybreaknorealmadmin" "daybreaknorealmadmin1234" for the "Simple" login page
Then I am redirected to "Manage Administrator Accounts" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

Then I can update the "Full Name" field to "Superadmin AcceptanceTest"
And I can update the "Email" field to "prodtestuser@testwgen.net"
Then I can select "Ingestion User" from a choice between "LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can also check "Ingestion User" Role 
And I can change the EdOrg dropdown to "IL-DAYBREAK"

When I click button "Save"
Then I am redirected to "Manage Administrator Accounts" page 
And a "Success" message is displayed 
And the new user has Roles as "Realm Administrator, Ingestion User"
And the new user has the same "Tenant" field as "DaybreakNoRealmAdmin Test" has
#And an email to verify user email address is sent

Scenario Outline: As a SLC Operator I can cancel editing an account
Given There is a user with "<USER_FULL_NAME>", "<USER_ROLE>", "<USER_ADDITIONAL_ROLES>", and "<USER_EMAIL>" in LDAP Server
When I navigate to the User Management Page 
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I am redirected to "Manage Administrator Accounts" page 

When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
And the "Full Name" field is prefilled with "<USER_FULL_NAME>"
And the "Email" field is prefilled with "<USER_EMAIL>"
And the "EdOrg" field is prefilled
	
When I click "Cancel" link
Then I am redirected to "Manage Administrator Accounts" page 

    Examples:
    |USER_FULL_NAME              |USER_ROLE           |USER_EMAIL                      |USER_ADDITIONAL_ROLES   |NEW_NAME       |NEW_EMAIL      |
    |Prod EditAdmin_hostname     |SEA Administrator   |hostname_prodtestuser@testwgen.net  |Realm Administrator     |Some Random    |random@1.net   |


Scenario Outline: As a SLC Operator I can edit an account
Given There is a user with "<USER_FULL_NAME>", "<USER_ROLE>", "<USER_ADDITIONAL_ROLES>", and "<USER_EMAIL>" in LDAP Server
When I navigate to the User Management Page 
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I am redirected to "Manage Administrator Accounts" page 

When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
And the "Full Name" field is prefilled with "<USER_FULL_NAME>"
And the "Email" field is prefilled with "<USER_EMAIL>"
And the "EdOrg" field is prefilled
And the "Tenant" field is prefilled
And the Role combobox is populated with "<USER_ROLE>" 
And the Role checkbox is checked with "<USER_ADDITIONAL_ROLES>" 

#unhappy path: update with empty field
Then I can delete text in "Full Name" field 
Then I can delete text in "Email" field 
And I click button "Update"
And a "can't be blank" message is displayed 

Then I can update the "Full Name" field to "<NEW_NAME>"
And I can update the "Email" field to "<NEW_EMAIL>"
And I can also check "Ingestion User" Role 

#And I can change the Role from the dropdown to "LEA Administrator"
And I click button "Update"
Then I am redirected to "Manage Administrator Accounts" page 
And a "Success" message is displayed 

And the user has "Full Name" updated to "<NEW_NAME>" 
And the user has "Email" updated to "<NEW_EMAIL>" 
And the user has Roles as "SEA Administrator, Realm Administrator, Ingestion User"

    Examples:
    |USER_FULL_NAME              |USER_ROLE           |USER_EMAIL                      |USER_ADDITIONAL_ROLES   |NEW_NAME       |NEW_EMAIL      |
    |Prod EditAdmin_hostname     |SEA Administrator   |hostname_prodtestuser@testwgen.net  |Realm Administrator     |Some Random    |random@1.net   |

Scenario Outline: As a SLC Operator I can delete an account
Given There is a user with "<USER_FULL_NAME>", "<USER_ROLE>", "<USER_ADDITIONAL_ROLES>", and "<USER_EMAIL>" in LDAP Server
When I navigate to the User Management Page 
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I see a user with Full Name is "<USER_FULL_NAME>" in the table
And the user "role" is "<USER_ROLE>"
When I click on "delete" icon 
Then I am asked to confirm the delete action
  
When I confirm the delete action
Then that user is removed from LDAP

    Examples:
    |USER_FULL_NAME              |USER_ROLE           |USER_EMAIL                      |USER_ADDITIONAL_ROLES   |NEW_NAME       |NEW_EMAIL      |
    |Prod EditAdmin_hostname     |SEA Administrator   |hostname_prodtestuser@testwgen.net  |                        |Some Random    |random@1.net   |


Scenario Outline: As an admin I can not delete my account
When I navigate to the User Management Page 
Then I will be redirected to "Simple" login page
When I submit the credentials "<USER>" "<PASSWORD>" for the "Simple" login page
Then I see my Full Name is "<USER_FULL_NAME>" in the table
And the "delete" button is disabled

 Examples:
    |USER                 |PASSWORD                 |USER_FULL_NAME                |USER_ROLE           |
    |slcoperator          |slcoperator1234          |SLC Operator                  |SLC Operator        |
