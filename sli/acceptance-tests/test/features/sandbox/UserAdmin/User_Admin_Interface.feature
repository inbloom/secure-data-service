@RALLY_3350 @RALLY_3274
Feature: Sandbox Account Management Interface

Background: 
Given I have an open web browser
And LDAP server has been setup and running

@sandbox 
Scenario Outline:  As a sandbox admin I am able to read all admin accounts in my tenancy on sandbox
When I navigate to the sandbox user account management page
Then I will be redirected to "Simple" login page
When I submit the credentials "<USER>" "<PASSWORD>" for the "Simple" login page
And I hit the sandbox user account management app list all users page
Then I see a table with headings of "Full Name" and "Email" and "Roles" and "EdOrg" and "Date Created" and "Actions"
Then I see a user with Full Name is "<USER_FULL_NAME>" in the table
Then the user "email" is "<USER_EMAIL>"
#Then the user "role" is "<USER_ROLE>"
#Then the user "edorg" is "<USER_EDORG>"
   

  Examples:
    |USER                 |PASSWORD                 |USER_FULL_NAME       |USER_EMAIL                     |USER_ROLE              |USER_TENANT                      |USER_EDORG        |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Admin        |sandboxadministrator@slidev.org|Sandbox Administrator  |sandboxadministrator@slidev.org  |                  | 
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Developer    |sandboxdeveloper@slidev.org    |Application Developer  |sandboxadministrator@slidev.org  |                  |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox IngestionUser|sandboxingestionuser@slidev.org|Ingestion User         |sandboxadministrator@slidev.org  |                  |
    
@sandbox
Scenario Outline:  As a sandbox application developer/ingestion user I am not able to access user account management app
When I navigate to the sandbox user account management page
Then I will be redirected to "Simple" login page
When I submit the credentials "<USER>" "<PASSWORD>" for the "Simple" login page
Then I will get an error message that "you don't have access to this page"
   

  Examples:
    |USER                 |PASSWORD                 |
    |sandboxdeveloper     |sandboxdeveloper1234     | 
    |sandboxingestionuser |sandboxingestionuser1234 |


@sandbox
Scenario Outline:  As a sandbox admin I am able to delete admin accounts in my tenancy on sandbox
Given There is a sandbox user with "<USER_FULL_NAME>" and "<USER_ROLE>" in LDAP Server
When I navigate to the sandbox user account management page
Then I will be redirected to "Simple" login page
When I submit the credentials "<USER>" "<PASSWORD>" for the "Simple" login page
And I hit the sandbox user account management app list all users page
Then I see a user with Full Name is "<USER_FULL_NAME>" in the table
And the user "role" is "<USER_ROLE>"
  
When I click on "delete" icon 
Then I am asked to confirm the delete action
  
When I confirm the delete action
Then that user is removed from LDAP
And the user entry is removed from the table
   
    Examples:
    |USER                 |PASSWORD                 |USER_FULL_NAME                |USER_ROLE                      |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Admin_hostname        |Sandbox Administrator          |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Developer_hostname    |Application Developer          |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox IngestionUser_hostname|Ingestion User                 |

@sandbox    
Scenario Outline: As a Sandbox admin I can not delete my account
Given I navigate to the sandbox user account management page
Then I will be redirected to "Simple" login page
When I submit the credentials "<USER>" "<PASSWORD>" for the "Simple" login page
And I hit the sandbox user account management app list all users page
Then I see my Full Name is "<USER_FULL_NAME>" in the table
And the "delete" button is disabled

 Examples:
    |USER                 |PASSWORD                 |USER_FULL_NAME                |USER_ROLE                      |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Admin                 |Sandbox Administrator          |


@sandbox 
Scenario: As a Sandbox admin I can quit to create user accounts
When I navigate to the sandbox user account management page
And I submit the credentials "sandboxadministrator" "sandboxadministrator1234" for the "Simple" login page
Then I am redirected to "Sandbox Account Management" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

When I click "Cancel" link
Then I am redirected to "Sandbox Account Management" page 

@sandbox    
Scenario: As a Sandbox admin I am able to create user accounts for my sandbox tenancy
Given the testing user does not already exists in LDAP
When I navigate to the sandbox user account management page
And I submit the credentials "sandboxadministrator" "sandboxadministrator1234" for the "Simple" login page
Then I am redirected to "Sandbox Account Management" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

And I have entered Full Name and Email into the required fields
And I can select "Application Developer" from a choice between "Sandbox Administrator, Application Developer, Ingestion User" Role 
And I can also check "Ingestion User" Role 

When I click button "Save"
Then I am redirected to "Sandbox Account Management" page 
And a "Success! You have added a new user" message is displayed 
And I see a user with Full Name is "Sandbox AcceptanceTests" in the table
And I see "Sandbox AcceptanceTests" has "Application Developer" and "Ingestion User" role 

#Unhappy path: I can't add the same user again
Then I click on "Add User" button
And I am redirected to "Add a User" page

And I have entered Full Name and Email into the required fields
And I can select "Application Developer" from a choice between "Sandbox Administrator, Application Developer, Ingestion User" Role 
And I can also check "Ingestion User" Role 

When I click button "Save"
And a "An account with this email already exists" message is displayed 

#And an email to verify user email address is sent

@sandbox
Scenario: Unhappy path: As a sandbox admin I can't add an user if I do not fill out all information
Given the testing user does not already exists in LDAP
When I navigate to the sandbox user account management page
And I submit the credentials "sandboxadministrator" "sandboxadministrator1234" for the "Simple" login page
Then I am redirected to "Sandbox Account Management" page 
Then I click on "Add User" button
And I am redirected to "Add a User" page

When I click button "Save"
And a "There was an error processing your request. Please fix any highlighted fields below." message is displayed 

@sandbox 
Scenario Outline: As a Sandbox admin I can cancel editing user accounts and no changes will be made
Given There is a sandbox user with "<USER_FULL_NAME>", "<USER_ROLE>", "<USER_ADDITIONAL_ROLES>", and "<USER_EMAIL>" in LDAP Server
When I navigate to the sandbox user account management page
And I submit the credentials "sandboxadministrator" "sandboxadministrator1234" for the "Simple" login page
Then I am redirected to "Sandbox Account Management" page 
#
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
And the "Email" field is prefilled with "<USER_EMAIL>"
#Those values will be localized by the acceptance tests 
And I can update the "Email" field to "<NEW_EMAIL>"

When I click "Cancel" link
Then I am redirected to "Sandbox Account Management" page 
And the user still has "Email" as "<USER_EMAIL>" 
 
    Examples:
    |USER_FULL_NAME              |USER_ROLE               |USER_EMAIL                  |USER_ADDITIONAL_ROLES   |NEW_EMAIL      |
    |Sandbox EditAdmin_hostname  |Sandbox Administrator   |hostname_testuser@wgen.net  |Ingestion User          |random@1.net   |

@sandbox 
Scenario Outline: As a Sandbox admin I am able to edit user accounts for my sandbox tenancy
Given There is a sandbox user with "<USER_FULL_NAME>", "<USER_ROLE>", "<USER_ADDITIONAL_ROLES>", and "<USER_EMAIL>" in LDAP Server
When I navigate to the sandbox user account management page
And I submit the credentials "sandboxadministrator" "sandboxadministrator1234" for the "Simple" login page
Then I am redirected to "Sandbox Account Management" page 
#
When I click the "edit" link for "<USER_FULL_NAME>"
Then I am redirected to "Update a User" page
And the "Full Name" field is prefilled with "<USER_FULL_NAME>"
And the "Email" field is prefilled with "<USER_EMAIL>"
##And the EdOrg is selected
And the Role combobox is populated with "<USER_ROLE>" 
And the Role checkbox is checked with "<USER_ADDITIONAL_ROLES>" 
#Those values will be localized by the acceptance tests 

#unhappy path: update with empty field
Then I can delete text in "Full Name" field 
Then I can delete text in "Email" field 
And I click button "Update"
And a "can't be blank" message is displayed 

Then I can update the "Full Name" field to "<NEW_NAME>"
And I can update the "Email" field to "<NEW_EMAIL>"
And I can change the Role from the dropdown to "Ingestion User"
And I can add additional Role "Application Developer"
And I click button "Update"
Then I am redirected to "Sandbox Account Management" page 
And a "Success! You have updated the user" message is displayed 
And the user has "Full Name" updated to "<NEW_NAME>" 
And the user has "Email" updated to "<NEW_EMAIL>" 
And the user now has roles "Ingestion User" and "Application Developer"

#clean up
Then I click on "delete" icon 
Then I am asked to confirm the delete action
And I confirm the delete action

    Examples:
    |USER_FULL_NAME              |USER_ROLE               |USER_EMAIL                  |USER_ADDITIONAL_ROLES   |NEW_NAME       |NEW_EMAIL      |
    |Sandbox EditAdmin_hostname  |Sandbox Administrator   |hostname_testuser@wgen.net  |Ingestion User          |Some Random    |random@1.net   |
