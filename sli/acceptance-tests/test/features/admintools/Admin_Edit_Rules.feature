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
Then I am redirected to "Manage Administrator Accounts" page 
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
Then I am redirected to "Manage Administrator Accounts" page 

#happy path
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
And I can update the "Email" field to "<NEW_EMAIL>"
And I can select "<NEW_ROLE>" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I click button "Update"
Then I am redirected to "Manage Administrator Accounts" page 
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
Then I am redirected to "Manage Administrator Accounts" page 
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
Then I am redirected to "Manage Administrator Accounts" page 

#happy path
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
And I can update the "Email" field to "<NEW_EMAIL>"
And I can select "<NEW_ROLE>" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I click button "Update"
Then I am redirected to "Manage Administrator Accounts" page 
And a "Success" message is displayed 
And the user has "Email" updated to "<NEW_EMAIL>" 
And the user has Roles as "<NEW_ROLE>"

    Examples:
    |USER_FULL_NAME              |USER_ROLE           |USER_EMAIL                          |USER_ADDITIONAL_ROLES   |NEW_ROLE          |NEW_EMAIL     |
    |Prod EditAdmin_hostname     |SEA Administrator   |hostname_prodtestuser@testwgen.net  |                        |Ingestion User    |random1@1.net |
    |Prod EditAdmin_hostname     |LEA Administrator   |hostname_prodtestuser@testwgen.net  |                        |Ingestion User    |random2@2.net |
    |Prod EditAdmin_hostname     |Ingestion User      |hostname_prodtestuser@testwgen.net  |                        |LEA Administrator |random3@3.net |
    |Prod EditAdmin_hostname     |Realm Administrator |hostname_prodtestuser@testwgen.net  |                        |LEA Administrator |random4@4.net |

Scenario Outline: As a SEA Admin I can not assign an ingestion user to a district without LEA
Given there is no users in edorg "IL-NIGHTFALL" 
Given There is a user with "<USER_FULL_NAME>", "<USER_ROLE>", "<USER_ADDITIONAL_ROLES>", and "<USER_EMAIL>" in LDAP Server
When I navigate to the User Management Page 
And I submit the credentials "<LOGIN>" "<PASSWORD>" for the "Simple" login page
Then I am redirected to "Manage Administrator Accounts" page 
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page

And I can change the EdOrg dropdown to "IL-NIGHTFALL"
And I click button "Update"
Then a "there is no LEA Administrator" message is displayed 

    Examples:
    |LOGIN       |PASSWORD        |USER_FULL_NAME              |USER_ROLE           |USER_EMAIL                          |USER_ADDITIONAL_ROLES   |NEW_ROLE            |NEW_EMAIL     |
    |iladmin     |iladmin1234     |Prod EditAdmin_hostname     |Ingestion User      |hostname_prodtestuser@testwgen.net  |                        |Ingestion User      |random1@1.net |
    |iladmin     |iladmin1234     |Prod EditAdmin_hostname     |Realm Administrator |hostname_prodtestuser@testwgen.net  |                        |Realm Administrator |random2@2.net |

    
Scenario Outline: As a SLC operator I can not assign an ingestion user to a district without LEA
Given there is no users in edorg "IL-NIGHTFALL" 
Given There is a user with "<USER_FULL_NAME>", "<USER_ROLE>", "<USER_ADDITIONAL_ROLES>", and "<USER_EMAIL>" in LDAP Server
When I navigate to the User Management Page 
And I submit the credentials "<LOGIN>" "<PASSWORD>" for the "Simple" login page
Then I am redirected to "Manage Administrator Accounts" page 
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page

And I can update the "EdOrg" field to "IL-NIGHTFALL"
And I click button "Update"
Then a "there is no LEA Administrator" message is displayed 

    Examples:
    |LOGIN       |PASSWORD        |USER_FULL_NAME              |USER_ROLE           |USER_EMAIL                          |USER_ADDITIONAL_ROLES   |NEW_ROLE            |NEW_EMAIL     |
    |slcoperator |slcoperator1234 |Prod EditAdmin_hostname     |Ingestion User      |hostname_prodtestuser@testwgen.net  |                        |Ingestion User      |random1@1.net |
    |slcoperator |slcoperator1234 |Prod EditAdmin_hostname     |Realm Administrator |hostname_prodtestuser@testwgen.net  |                        |Realm Administrator |random2@2.net |


Scenario:  SLC Operator can modify limited fields on their own account
Given there is a production "SLC Operator" with tenancy " " and in " "
Then I can navigate to the User Management Page with that production user
Then I am redirected to "Manage Administrator Accounts" page 
When I click the "edit" link for "SAMT Test_hostname"
Then I am redirected to "Update a User" page
And I do not see Role selection nor EdOrg dropdown menu 
Then I can update the "Full Name" field to "SAMT Changed"
And I can update the "Email" field to "samt_changed@testwgen.net"
And I click button "Update"
Then I am redirected to "Manage Administrator Accounts" page 
And a "Success" message is displayed 
And the user has "Email" updated to "samt_changed@testwgen.net" 

Scenario: As a SEA Admin I am able to edit any field for my account
Given there is a production "SEA Administrator" with tenancy "Midgar" and in "IL"
Then I can navigate to the User Management Page with that production user
Then I am redirected to "Manage Administrator Accounts" page 
When I click the "edit" link for "SAMT Test_hostname"
Then I am redirected to "Update a User" page
#all fields are editable except primary role
And I can delete text in "Full Name" field 
And I can delete text in "Email" field 
And I can change the EdOrg dropdown to "IL-DAYBREAK"
And I do not see an option to change my primary admin role
#And I can select "LEA Administrator" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can also check "Ingestion User" Role 

#unhappy path 
And I click button "Update"
And a "can't be blank" message is displayed 

Then I can update the "Full Name" field to "SAMT Changed"
Then I can update the "Email" field to "samt_email@random.net"

And I click button "Update"
Then I am redirected to "Manage Administrator Accounts" page 
And a "Success" message is displayed 



Scenario: As a LEA Admin I am able to edit any field for my account
Given there is a production "LEA Administrator" with tenancy "Midgar" and in "IL"
Then I can navigate to the User Management Page with that production user
Then I am redirected to "Manage Administrator Accounts" page 
When I click the "edit" link for "SAMT Test_hostname"
Then I am redirected to "Update a User" page
#all fields are editable except primary role
And I can delete text in "Full Name" field 
And I can delete text in "Email" field 
And EdOrg choice is limited to "IL" for me
And I do not see an option to change my primary admin role
#And I can select "LEA Administrator" from a choice between "SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can also check "Ingestion User" Role 

#unhappy path 
And I click button "Update"
And a "can't be blank" message is displayed 

Then I can update the "Full Name" field to "SAMT Changed"
Then I can update the "Email" field to "samt_email@random.net"

And I click button "Update"
Then I am redirected to "Manage Administrator Accounts" page 
And a "Success" message is displayed 


Scenario Outline: LEAs can create/modify other LEA admin, ingestion user and realm administrator in their edorg
Given There is a user with "<USER_FULL_NAME>", "<USER_ROLE>", "<USER_ADDITIONAL_ROLES>", and "<USER_EMAIL>" in "Midgar" "<EDORG>" LDAP Server
Given there is a production "LEA Administrator" with tenancy "Midgar" and in "<EDORG>"
Then I can navigate to the User Management Page with that production user
Then I am redirected to "Manage Administrator Accounts" page 
    
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
#And all fields are editable
Then I can update the "Full Name" field to "SAMT Changed"
And I can update the "Email" field to "<NEW_EMAIL>"
And I can select "LEA Administrator" from a choice between "LEA Administrator, Ingestion User, Realm Administrator" Role
And I can also check "Realm Administrator" Role 
And I can also check "Ingestion User" Role 
And EdOrg choice is limited to "<EDORG>" for me

#happy path
#deselect optional roles
And I can also check "Realm Administrator" Role  
And I can also check "Ingestion User" Role 
And I can select "<NEW_ROLE>" from a choice between "LEA Administrator, Ingestion User, Realm Administrator" Role
And I click button "Update"
Then I am redirected to "Manage Administrator Accounts" page 
And a "Success" message is displayed 
And the user has "Email" updated to "<NEW_EMAIL>" 
And the user has Roles as "<NEW_ROLE>"

    Examples:
    |USER_FULL_NAME              |USER_ROLE           |USER_EMAIL                          |USER_ADDITIONAL_ROLES   |NEW_ROLE            |NEW_EMAIL     |EDORG       |
    |Prod EditAdmin_hostname     |LEA Administrator   |hostname_prodtestuser@testwgen.net  |                        |LEA Administrator   |random3@3.net |IL-SUNSET   |
    |Prod EditAdmin_hostname     |Ingestion User      |hostname_prodtestuser@testwgen.net  |                        |Realm Administrator |random3@3.net |IL-SUNSET   |
    |Prod EditAdmin_hostname     |Realm Administrator |hostname_prodtestuser@testwgen.net  |                        |Ingestion User      |random4@4.net |IL-DAYBREAK |
    |Prod EditAdmin_hostname     |Realm Administrator |hostname_prodtestuser@testwgen.net  |                        |Ingestion User      |random4@4.net |IL          |
