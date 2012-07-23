@wip
@RALLY_3350 @RALLY_3274
Feature: As an admin I can access user accounts management tools and manage user accounts

  Background: Given I have an open web browser

 @wip
  @sandbox
  Scenario Outline:  As a sandbox admin I am able to read all admin accounts in my tenancy on sandbox
   Given I navigate to the sandbox user account management page
   Then I will be redirected to "Simple" login page
   Given I submit the credentials "<USER>" "<PASSWORD>" for the "Simple" login page
   When I hit the sandbox user account management app list all users page
   Then I see a table with headings of "Full Name" and "Email" and "Tenant" and "EdOrg" and "Date Created" and "Actions"
   Then I see a user with "Full Name" is "<USER_FULL_NAME>" in the table
   Then the user "Email" is "<USER_EMAIL>"
   Then the user "Role" is "<USER_ROLE>"
   Then the user "Tenant" is "<USER_TENANT>"
   Then the user "EdOrg" is "<USER_EDORG>"
   

  Examples:
    |USER                 |PASSWORD                 |USER_FULL_NAME       |USER_EMAIL                     |USER_ROLE              |USER_TENANT                      |USER_EDORG        |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Admin        |sandboxadministrator@slidev.org|Sandbox Administrator  |sandboxadministrator@slidev.org  |                  | 
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Developer    |sandboxdeveloper@slidev.org    |Application Developer  |sandboxadministrator@slidev.org  |                  |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox IngestionUser|sandboxingestionuser@slidev.org|Ingestion User         |sandboxadministrator@slidev.org  |                  |
    
  @wip
  @sandbox
  Scenario Outline:  As a sandbox application developer/ingestion user I am not able to access user account management app
   Given I navigate to the sandbox user account management page
   Then I will be redirected to "Simple" login page
   Given I submit the credentials "<USER>" "<PASSWORD>" for the "Simple" login page
   Then I will get an error message that "I do not have access to this resource"
   

  Examples:
    |USER                 |PASSWORD                 |
    |sandboxdeveloper     |sandboxdeveloper1234     | 
    |sandboxingestionuser |sandboxingestionuser1234 |
 
 @wip   
 @sandbox
  Scenario Outline:  As a sandbox admin I am able to delete admin accounts in my tenancy on sandbox
  Given I navigate to the sandbox user account management page
   Then I will be redirected to "Simple" login page
   Given I submit the credentials "<USER>" "<PASSWORD>" for the "Simple" login page
   When I hit the sandbox user account management app list all users page
   Then I see a user with "Full Name" is "<USER_FULL_NAME>" in the table
   And the user "Role" is "<USER_ROLE>"
   When I click on delete button
   Then I should receive a warning message to confirm deletion
   When I click on confirm deletion
   Then I should receive a message that "user has been deleted successfully"
   Then I can not see a user with "Full Name" is "USER_FULL_NAME" in the table
   
    Examples:
    |USER                 |PASSWORD                 |USER_FULL_NAME                |USER_ROLE                      |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Admin_hostname        |Sandbox Administrator          |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Developer_hostname    |Application Developer          |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox IngestionUser_hostname|Ingestion User                 |
    
  