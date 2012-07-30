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
Then I see a table with headings of "Full Name" and "Email" and "Role" and "EdOrg" and "Date Created" and "Actions"
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
#And the user "Role" is "<USER_ROLE>"
  
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
And I can select "Application Developer" from a choice between a "Sandbox Administrator", "Application Developer" and "Ingestion User" Role 
And I can also check "Ingestion User" Role 
#And I can also check "Application Developer" Role 

When I click "Save"
Then I am redirected to "Sandbox Account Management" page 
And a "Success! You have added a new user" message is displayed 
And I see a user with Full Name is "Sandbox AcceptanceTests" in the table
And I see "Sandbox AcceptanceTests" has "Application Developer" and "Ingestion User" role 

#And an email to verify user email address is sent

@sandbox @wip 
Scenario: As a Sandbox admin I am able to edit user accounts for my sandbox tenancy
When I access the User Management Page from the portal
Then I am redirected to "Sandbox Account Management" page which has a table of all accounts for my tenancy

When I hover over a row which is not my name
Then I am shown the option to "Edit" user

When I click on "Edit" link
Then I am redirected to "Add a User" form
And the title is "Edit a User"
And the Fullname is prefilled
And the Email is prefilled
And the EdOrg is selected
And the Role is selected

When I update the email address
And I update the Role
And I click "Save"
Then I am redirected to the "User Account Management" Page
And a "Success" message is displayed 
And the updated information is displayed in the table

When I click "Cancel"
Then I am redirected to the "User Account Management" Page
And no changes are shown in the table
  
