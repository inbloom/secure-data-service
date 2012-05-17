@wip
Feature: Complete onboarding workflow for sandbox and prodution

Background: None

@sandbox
Scenario: Developer is on-boarded in a sandbox enviornment
Given the developer first name "Loraine" and last name "Plyler"  submits an account registration request
Then the developer gets an email to verify email address "Lplyer@macrocorp.com"
When the developer verifies the email
Then an account entry is made is ldap with "Approved" status
And an email is sent to the developer
And the email has a <URL_TO_PROVISIONING_APPLICATION>
And the email has a <URL_TO_APPLICATION_REGISTRATION>
And a <SUPER_ADMIN> roles is a added for the user in ldap
When the user clicks on <URL_TO_PROVISIONING_APPLICATION>
Then the user has to authenticate against ldap using "Lplyer@macrocorp.com" and <PSWD>
And the user is redirected to <URL_TO_PROVISIONING_APPLICATION>

When the user selects the option to use the <ED-ORG_SAMPLE_DS1>
And clicks on "Provision"
Then an <ED-ORG_SAMPLE_DS1> is saved to mongo
And a <ED-ORG_SAMPLE_DS1> is added in the application table for <DASHBOARD_APP>, <PORTAL_APP>, <DATABROWSER_APP>
And a request for a Landing zone is made with <Tenant_ID> and <ED-ORG_SAMPLE_DS1>
And tenant entry with <Tenant_ID> and <Landing_zone_directory> is added to mongo
And the <Landing_zone_directory> is saved in Ldap
And the <Tenant_ID> is saved in ldap

@sandbox
Scenario: Developer logs in after on-boarding on sandbox
Given the user has an approved account
When the user accesses the <PORTAL_APP>
Then the user is authenticated using the <LDAP_CREDENTIALS> and <OTHER> realm

When the user is successfully authenticated
Then the user can access <DASHBOARD_APP>, <DATABROWSER_APP>

@sandbox
Scenario: Developer is able to register applications on sandbox
Given the user has an approved account
When the user clicks on  <URL_TO_APPLICATION_REGISTRATION>
Then the user is authenticated using <LDAP_CREDENTIALS>
And the user can access <APPLICATION_REGISTRATION_APP>

@production
Scenario: Vendor registers on a production enviornment
Given the vendor "Macro Corp"  submits an account registration request
Then the vendor gets an email to verify email address "Lplyer@macrocorp.com"
When the vendor verifies the email
Then an account entry is made is ldap with "Submitted" status

When <SLC_OPERATOR> accesses the <ADMIN_PORTAL>
Then <SLC_OPERATOR> authenticates against <SLI_realm>
And the <SLC_OPERATOR> is able access <ACCOUNT_MANAGEMENT_APP>

When <SLC_OPERATOR> accepts the vendor account
Then an email is sent to the developer
And the email has a <URL_TO_APPLICATION_REGISTRATION>

@production
Scenario: State Super admin provisions LZ for an Ed-Org
When the <STATE_SUPER_ADMIN> accesses the <ADMIN_PORTAL>
Then the <STATE_SUPER_ADMIN> authenticates
When the user enters <TEST_ED_ORG>
And clicks on "Provision"
Then an <STATE_ED_ORG> is saved to mongo
And a request for a Landing zone is made with <Tenant_ID> and <STATE_ED_ORG>
And tenant entry with <Tenant_ID> and <Landing_zone_directory> is added to mongo
And the <Landing_zone_directory> is saved in Ldap
And the <Tenant_ID> is saved in ldap






