@RALLY_US2281 @RALLY_US206
Feature: Complete onboarding workflow for sandbox and production

Background:
Given I have an open web browser
#And I have a "mock" SMTP/Email server configured
And I have a SMTP/Email server configured


@sandbox
Scenario: Developer is on-boarded in a sandbox environment
Given I go to the sandbox account registration page
And there is no registered account for "<USER_EMAIL>" in the SLI database
And there is no registered account for "<USER_EMAIL>" in LDAP
And the developer type in first name "<USER_FIRSTNAME>" and last name "<USER_LASTNAME>"
And the developer type in email "<USER_EMAIL>" and password "<USER_PASS>"
And the developer submits the account registration request
Then the developer is redirected to a page with terms and conditions
When the developer click "Accept"
Then the developer is directed to an acknowledgement page.
 And a verification email is sent to "<USER_EMAIL>"
When the developer click link in verification email in "sandbox"
Then an account entry is made in ldap with "Approved" status
And a "sandbox" approval email is sent to the "<USER_EMAIL>"
And the email has a "<URL_TO_PORTAL>"
#TODO: The portal for development is linked to RC which uses a different LDAP than dev. So we are breaking the flow and jump directly to the correct provisioning app.
#And the email has a "<URL_TO_PROVISIONING_APPLICATION>"
And a "<APPLICATION_DEVELOPER>" roles is a added for the user in ldap
When the user clicks on "<URL_TO_PROVISIONING_APPLICATION>"
Then the user has to authenticate against ldap using "<USER_EMAIL>" and "<USER_PASS>"

When the user selects the option to use the "<ED-ORG_SAMPLE_DS1>"
And clicks on "Provision"
#And an "<ED-ORG_SAMPLE_DS1>" is added in the application table for "<DASHBOARD_APP>"," <ADMIN_APP>", "<DATABROWSER_APP>"
And a request for a Landing zone is made with "<Tenant_ID>" and "<ED-ORG_SAMPLE_DS1>"
And a tenant entry with "<Tenant_ID>" and "<Landing_zone_directory>" is added to mongo
And the landing zone "<Landing_zone_directory>" is saved in Ldap
And the tenantId "<Tenant_ID>" is saved in Ldap

@sandbox
Scenario: Developer logs in after on-boarding on sandbox
Given the user has an approved sandbox account
When the user accesses the "<URL_TO_ADMIN_APP>"
Then the user has to authenticate against ldap using "<USER_EMAIL>" and "<USER_PASS>"
And the user is redirected to "<URL_TO_ADMIN_APP>"

#When the user is successfully authenticated
#Then the user can access "<DASHBOARD_APP>", "<DATABROWSER_APP>"

@sandbox
Scenario: Developer is able to register applications on sandbox
Given the user has an approved sandbox account
When the user clicks on "<URL_TO_APPLICATION_REGISTRATION>"
Then the user has to authenticate against ldap using "<USER_EMAIL>" and "<USER_PASS>"
And the user is redirected to "<URL_TO_APPLICATION_REGISTRATION>" after "5" seconds

@production
Scenario: District admin provisions LZ for an Ed-Org
Given the "<DISTRICT_ADMIN_USER>" has "<STATE_ED_ORG>" defined in LDAP by the operator
When the state super admin accesses the "<URL_TO_PROVISIONING_APPLICATION>"
Then the state super admin authenticates as "<DISTRICT_ADMIN_USER>" and "<DISTRICT_ADMIN_PASS>"
And clicks on "Provision"
And a request for a Landing zone is made with "<Tenant_ID>" and "<STATE_ED_ORG>"
And a tenant entry with "<Prod_Tenant_ID>" and "<Prod_Landing_zone_directory>" is added to mongo
And the landing zone "<Prod_Landing_zone_directory>" is saved in Ldap
And the tenantId "<Prod_Tenant_ID>" is saved in Ldap





