@RALLY_US2281 @wip
Feature: Complete onboarding workflow for sandbox and prodution

Background: 
Given I have an open web browser
#And I have a "mock" SMTP/Email server configured
And I have a SMTP/Email server configured


@sandbox
Scenario: Developer is on-boarded in a sandbox enviornment
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
When the developer click link in verification email
Then an account entry is made in ldap with "Approved" status
And an approval email is sent to the "<USER_EMAIL>"
And the email has a "<URL_TO_PROVISIONING_APPLICATION>"
And the email has a "<URL_TO_APPLICATION_REGISTRATION>"
And a "<APPLICATION_DEVELOPER>" roles is a added for the user in ldap
When the user clicks on "<URL_TO_PROVISIONING_APPLICATION>"
Then the user has to authenticate against ldap using "<USER_EMAIL>" and "<USER_PASS>"
And the user is redirected to "<URL_TO_PROVISIONING_APPLICATION>"

When the user selects the option to use the "<ED-ORG_SAMPLE_DS1>"
And clicks on "Provision" 
Then an "<ED-ORG_SAMPLE_DS1>" is saved to mongo
And an "<ED-ORG_SAMPLE_DS1>" is added in the application table for "<DASHBOARD_APP>"," <ADMIN_APP>", "<DATABROWSER_APP>"
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
And the user is redirected to "<URL_TO_APPLICATION_REGISTRATION>"

@production
Scenario: Vendor registers on a production environment
Given I go to the production account registration page
And there is no registered account for "<USER_EMAIL>" in the SLI database
And there is no registered account for "<USER_EMAIL>" in LDAP
And the developer type in first name "<USER_FIRSTNAME>" and last name "<USER_LASTNAME>"
And the developer type in email "<USER_EMAIL>" and password "<USER_PASS>" 
And the developer submits the account registration request
Then the developer is redirected to a page with terms and conditions
When the developer click "Accept" 
Then the developer is directed to an acknowledgement page. 
And a verification email is sent to "<USER_EMAIL>"
When the developer click link in verification email
Then an account entry is made in ldap with "pending" status
# And there is no registered account for "devldapuser@slidev.org" in the SLI database 
# And there is no registered account for "devldapuser@slidev.org" in LDAP
# Given the vendor "Macro Corp"  submits an account registration request
# Then I am redirected to a page with terms and conditions
# And when I click "Accept" 
# Then I am directed to an acknowledgement page. 
#  And an email verification link for "devldapuser@slidev.org" is generated
# When the vendor verifies the email
# Then an account entry is made in ldap with "pending" status

When the SLC operator accesses the "<ACCOUNT_MANAGEMENT_APP>"
And the SLC operator authenticates as "<SLC_OPERATOR_USER>" and "<SLC_OPERATOR_PASS>"
And the SLC operator approves the vendor account for "<USER_EMAIL>"
Then an approval email is sent to the "<USER_EMAIL>"
And the email has a "<URL_TO_APPLICATION_REGISTRATION>"

@production
Scenario: District admin provisions LZ for an Ed-Org
When the state super admin accesses the "<URL_TO_PROVISIONING_APPLICATION>"
Then the state super admin authenticates as "<DISTRICT_ADMIN_USER>" and "<DISTRICT_ADMIN_PASS>"
When  the state super admin set the custom high-level ed-org to "<STATE_ED_ORG>"
And clicks on "Provision" 
Then an "<STATE_ED_ORG>" is saved to mongo
And a request for a Landing zone is made with "<Tenant_ID>" and "<STATE_ED_ORG>"
And a tenant entry with "<Prod_Tenant_ID>" and "<Prod_Landing_zone_directory>" is added to mongo
And the landing zone "<Prod_Landing_zone_directory>" is saved in Ldap
And the tenantId "<Prod_Tenant_ID>" is saved in Ldap





