@wip
@RALLY_US3409
Feature: Admin users of SAMT can only update certain fields of existing accounts via the UI

Background: 
Given I have an open web browser
And LDAP and email server has been setup and running
And I already have a SLC Operator account

Scenario Outline: As a SLC Operator I am able to edit any field
Given There is a user with "<USER_FULL_NAME>", "<USER_ROLE>", "<USER_ADDITIONAL_ROLES>", and "<USER_EMAIL>" in LDAP Server
When I navigate to the User Management Page 
And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I am redirected to "Admin Account Management" page 
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
#And all fields are editable
And I can delete text in "Full Name" field 
And I can delete text in "Email" field 
And I can delete text in "Tenant" field 
And I can delete text in "EdOrg" field 
And I can select "SEA Administrator" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can also check "Ingestion User" Role 

#unhappy path -- also shows I can modify all fields
And I click button "Update"
And a "can't be blank" message is displayed 
When I click "Cancel" link
Then I am redirected to "Admin Account Management" page 

#happy path
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
And I can update the "Email" field to "<NEW_EMAIL>"
And I can select "<NEW_ROLE>" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I click button "Update"
Then I am redirected to "Admin Account Management" page 
And a "Success" message is displayed 
And the user has "Email" updated to "<NEW_EMAIL>" 
And the user has Roles as "<NEW_ROLE>"

    Examples:
    |USER_FULL_NAME              |USER_ROLE           |USER_EMAIL                          |USER_ADDITIONAL_ROLES   |NEW_ROLE          |NEW_EMAIL     |
    |Prod EditAdmin_hostname     |SEA Administrator   |hostname_prodtestuser@testwgen.net  |                        |Ingestion User    |random1@1.net |
    |Prod EditAdmin_hostname     |LEA Administrator   |hostname_prodtestuser@testwgen.net  |                        |Ingestion User    |random2@2.net |
    |Prod EditAdmin_hostname     |Ingestion User      |hostname_prodtestuser@testwgen.net  |                        |LEA Administrator |random3@3.net |
    |Prod EditAdmin_hostname     |Realm Administrator |hostname_prodtestuser@testwgen.net  |                        |LEA Administrator |random4@4.net |


Scenario Outline: As a SEA Admin I am able to edit any field for accounts in my tenancy
Given There is a user with "<USER_FULL_NAME>", "<USER_ROLE>", "<USER_ADDITIONAL_ROLES>", and "<USER_EMAIL>" in LDAP Server
When I navigate to the User Management Page 
And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
Then I am redirected to "Admin Account Management" page 
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
#And all fields are editable
And I can delete text in "Full Name" field 
And I can delete text in "Email" field 
And I can change the EdOrg dropdown to "IL-DAYBREAK"
And I can select "LEA Administrator" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can also check "Ingestion User" Role 

#unhappy path -- also shows I can modify all fields
And I click button "Update"
And a "can't be blank" message is displayed 
When I click "Cancel" link
Then I am redirected to "Admin Account Management" page 

#happy path
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
And I can update the "Email" field to "<NEW_EMAIL>"
And I can select "<NEW_ROLE>" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I click button "Update"
Then I am redirected to "Admin Account Management" page 
And a "Success" message is displayed 
And the user has "Email" updated to "<NEW_EMAIL>" 
And the user has Roles as "<NEW_ROLE>"

    Examples:
    |USER_FULL_NAME              |USER_ROLE           |USER_EMAIL                          |USER_ADDITIONAL_ROLES   |NEW_ROLE          |NEW_EMAIL     |
    |Prod EditAdmin_hostname     |SEA Administrator   |hostname_prodtestuser@testwgen.net  |                        |Ingestion User    |random1@1.net |
    |Prod EditAdmin_hostname     |LEA Administrator   |hostname_prodtestuser@testwgen.net  |                        |Ingestion User    |random2@2.net |
    |Prod EditAdmin_hostname     |Ingestion User      |hostname_prodtestuser@testwgen.net  |                        |LEA Administrator |random3@3.net |
    |Prod EditAdmin_hostname     |Realm Administrator |hostname_prodtestuser@testwgen.net  |                        |LEA Administrator |random4@4.net |


Scenario: As a SEA Admin I am able to edit any field for my account
Given there is a production "SEA Administrator" with tenancy "Midgar" and in "IL"
Then I can navigate to the User Management Page with that production user
Then I am redirected to "Admin Account Management" page 
When I click the "edit" link for "SAMT Test"
Then I am redirected to "Update a User" page
#And all fields are editable
And I can delete text in "Full Name" field 
And I can delete text in "Email" field 
And I can change the EdOrg dropdown to "IL-DAYBREAK"
And I can select "LEA Administrator" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can also check "Ingestion User" Role 

#unhappy path 
And I click button "Update"
And a "can't be blank" message is displayed 

Then I can update the "Full Name" field to "SAMT Changed"
Then I can update the "Email" field to "samt_email@random.net"

And I click button "Update"
Then I am redirected to "Admin Account Management" page 
And a "Success" message is displayed 

Scenario Outline: LEAs can't see LEA from their edorg
Given there is a production "LEA Administrator" with tenancy "Midgar" and in "<EDORG>"
Then I can navigate to the User Management Page with that production user
Then I am redirected to "Admin Account Management" page 

And I do not see any other LEA admin from "<EDORG>" besides me
And any other LEA admin belongs to "<BELOW_EDORG>"

    Examples:
    |EDORG       |BELOW_EDORG                |
    |IL          |IL-SUNSET, IL-DAYBREAK     |
    |IL-SUNSET   |                           |
    |IL-DAYBREAK |                           |


Scenario Outline: LEAs can create/modify ingestion user and realm administrator in their edorg
Given There is a user with "<USER_FULL_NAME>", "<USER_ROLE>", "<USER_ADDITIONAL_ROLES>", and "<USER_EMAIL>" in LDAP Server
When I navigate to the User Management Page 
And I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I am redirected to "Admin Account Management" page 
    
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
#And all fields are editable
Then I can update the "Full Name" field to "SAMT Changed"
And I can update the "Email" field to "<NEW_EMAIL>"
And I can change the EdOrg dropdown to "IL-SUNSET"
And I can select "LEA Administrator" from a choice between "LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can also check "Ingestion User" Role 

#unhappy path -- can't create LEA with empty edorg (edorg disabled as this is creating a peer LEA)
And I click button "Update"
And a "There was an error processing your request" message is displayed 

#happy path
#deselect optional roles
And I can also check "Realm Administrator" Role  
And I can also check "Ingestion User" Role 
And I can select "<NEW_ROLE>" from a choice between "LEA Administrator, Ingestion User, Realm Administrator" Role
And I click button "Update"
Then I am redirected to "Admin Account Management" page 
And a "Success" message is displayed 
And the user has "Email" updated to "<NEW_EMAIL>" 
And the user has Roles as "<NEW_ROLE>"

    Examples:
    |USER_FULL_NAME              |USER_ROLE           |USER_EMAIL                          |USER_ADDITIONAL_ROLES   |NEW_ROLE            |NEW_EMAIL     |
    |Prod EditAdmin_hostname     |Ingestion User      |hostname_prodtestuser@testwgen.net  |                        |Realm Administrator |random3@3.net |
    |Prod EditAdmin_hostname     |Realm Administrator |hostname_prodtestuser@testwgen.net  |                        |Ingestion User      |random4@4.net |


Scenario:  LEA can modify limited fields on their own account
Given there is a production "LEA Administrator" with tenancy "Midgar" and in "IL"
Then I can navigate to the User Management Page with that production user
Then I am redirected to "Admin Account Management" page 
When I click the "edit" link for "SAMT Test"
Then I am redirected to "Update a User" page
And I do not see Role selection nor EdOrg dropdown menu 
Then I can update the "Full Name" field to "SAMT Changed"
And I can update the "Email" field to "samt_changed@testwgen.net"
And I click button "Update"
Then I am redirected to "Admin Account Management" page 
And a "Success" message is displayed 
And the user has "Email" updated to "samt_changed@testwgen.net" 

Scenario Outline: LEA can modify all fields for LEAs below my edorg
Given There is a user with "<USER_FULL_NAME>", "<USER_ROLE>", "<USER_ADDITIONAL_ROLES>", and "<USER_EMAIL>" in LDAP Server
Given there is a production "LEA Administrator" with tenancy "Midgar" and in "IL"
Then I can navigate to the User Management Page with that production user
Then I am redirected to "Admin Account Management" page 

#happy path
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
#I can edit all fields
And I can delete text in "Full Name" field 
And I can delete text in "Email" field 
And I can change the EdOrg dropdown to "IL-DAYBREAK"
And I can select "LEA Administrator" from a choice between "LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can also check "Ingestion User" Role 

And I can update the "Full Name" field to "SAMT LEAtest"
And I can update the "Email" field to "<NEW_EMAIL>"
#deselect the roles
And I can also check "Realm Administrator" Role 
And I can also check "Ingestion User" Role 
And I can select "<NEW_ROLE>" from a choice between "LEA Administrator, Ingestion User, Realm Administrator" Role
And I click button "Update"
Then I am redirected to "Admin Account Management" page 
And a "Success" message is displayed 
And the user has "Email" updated to "<NEW_EMAIL>" 
And the user has Roles as "<NEW_ROLE>"

    Examples:
    |USER_FULL_NAME              |USER_ROLE           |USER_EMAIL                          |USER_ADDITIONAL_ROLES   |NEW_ROLE          |NEW_EMAIL     |
    |Prod EditAdmin_hostname     |LEA Administrator   |hostname_prodtestuser@testwgen.net  |                        |Ingestion User    |random2@2.net |
