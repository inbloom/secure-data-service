@RALLY_US2281 @RALLY_US2844
Feature: Developer/Vendor can create a High Level Ed-Org and link it to the Landing Zone

Background:
Given I have an open web browser
And LDAP server has been setup and running


@production
Scenario: As a Vendor/Developer I use a defined High Level Ed-Org to Provision my Landing Zone
Given there is an production Ingestion Admin account in ldap
And the account has a tenantId "<TENANTID>"
And the account has a edorg of "<PROD_EDORG>"
When the developer go to the provisioning application web page
And the developer is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
And I provision a Landing zone
Then the user gets a success message
And an ed-org is created in Mongo with the "stateOrganizationId" is "<EDORG_NAME>"
And a request to provision a landing zone is made
And the directory structure for the landing zone is stored in ldap

@production
Scenario: As an ingestion admin I can use the provisioning tool to create a LZ for my district
Given there is an production Ingestion Admin account in ldap
And the account has a tenantId "<TENANTID>"
And the account has a edorg of "<PROD_EDORG>"
And there is no corresponding tenant in mongo
And there is no corresponding ed-org in mongo
And there is no landing zone for the "<PROD_EDORG>" in mongo
When the Ingestion Admin go to the provisioning application web page
Then the Ingestion Admin is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the Ingestion Admin provision a Landing zone
Then the Ingestion Admin gets a success message
And a tenantId "<TENANTID>" created in Mongo
And an ed-org is created in Mongo with the "stateOrganizationId" is "<PROD_EDORG>"
And a request to provision a landing zone is made
And the directory structure for the landing zone is stored in ldap
And the directory structure for the landing zone is stored for tenant in mongo


@wip
@production
Scenario: As an ingestion admin I can use the provisioning tool to get added to an existing LZ
Given there is an production Ingestion Admin account in ldap
And the account has a  <tenantId>
And the account has a edorg of <EDORG_NAME>
And there is already a tenant in mongo
And there is already ed-org in mongo
And there is a landing zone for the <EDORG_NAME> in mongo
And there is no landing zone for the user in LDAP
When the Ingestion Admin go to the provisioning application web page
Then the Ingestion Admin is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the Ingestion Admin provision a Landing zone
And the directory structure for the landing zone is stored in ldap for <Ingestion_Admin>
And the Ingestion Admin gets a success message

@wip
@production
Scenario: As an ingestion admin I can use the provisioning tool to a add a new district LZ
Given there is an production Ingestion Admin account in ldap
And the account has a  <tenantId>
And the account has a edorg of <DISTRICT_EDORG_NAME>
And there is already a tenant in mongo
And there is already edorg in mongo
And there is no landing zone for the <DISTRICT_EDORG_NAME> in mongo
And there is no landing zone for the <Ingestion_Admin> in LDAP
When the Ingestion Admin go to the provisioning application web page
Then the Ingestion Admin is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the Ingestion Admin provision a Landing zone
Then a request to provision a landing zone is made
And the directory structure for the landing zone is stored in ldap for <Ingestion_Admin>
And the directory structure for the landing zone is stored for tenant in mongo
And the Ingestion Admin gets a success message

@wip
@production
Scenario: As an ingestion admin I can use the provisioning tool to to have only one LZ as my home directory
Given there is an production Ingestion Admin account in ldap
And the account has a  <tenantId>
And the account has a <EDORG_NAME>
And there is already a tenant in mongo
And there is already ed-org in mongo
And there is a landing zone for the ed-org in mongo
And there is a landing zone for the <Ingestion_Admin> in LDAP
When the Ingestion Admin go to the provisioning application web page
Then the Ingestion Admin is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the Ingestion Admin provision a Landing zone
Then the Ingestion Admin is gets an already provisioned message

@sandbox
Scenario: As a developer I can use the provisioning tool to create a LZ for my sandbox tenancy
Given there is an sandbox account in ldap
And the account has a tenantId "<DEVELOPER_EMAIL>"
#And the account has a edorg of "<SANDBOX_EDORG>"
And there is no corresponding tenant in mongo
And there is no corresponding ed-org in mongo
When the developer go to the provisioning application web page
Then the developer is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the developer provision a "sandbox" Landing zone with edorg is "<SANDBOX_EDORG>"
Then a tenantId "<DEVELOPER_EMAIL>" created in Mongo
And an ed-org is created in Mongo with the "stateOrganizationId" is "<SANDBOX_EDORG>"
And a request to provision a landing zone is made
And the directory structure for the landing zone is stored in ldap
And the directory structure for the landing zone is stored for tenant in mongo
And the user gets a success message

@sandbox
Scenario: As a developer I can use the provisioning tool to create more than one LZ for my sandbox tenancy
Given there is an sandbox account in ldap
And the account has a tenantId "<DEVELOPER_EMAIL>"
And the account has a edorg of "<SANDBOX_EDORG_2>"
And there is already a tenant with tenantId "<DEVELOPER_EMAIL>" in mongo
And there is already a edorg with stateOrganizationId "<SANDBOX_EDORG_2>" in mongo
And there is no landing zone for the "<SANDBOX_EDORG>" in mongo
When the developer go to the provisioning application web page
Then the developer is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the developer provision a "sandbox" Landing zone with edorg is "<SANDBOX_EDORG>"
Then an ed-org is created in Mongo with the "stateOrganizationId" is "<SANDBOX_EDORG>"
And a request to provision a landing zone is made
And the directory structure for the landing zone is stored in ldap
And the directory structure for the landing zone is stored for tenant in mongo
And the user gets a success message

@sandbox
Scenario: As an developer I can use the provisioning tool to switch between LZ
Given there is an sandbox account in ldap
And the account has a tenantId "<DEVELOPER_EMAIL>"
And the account has a edorg of "<SANDBOX_EDORG>"
And there is already a tenant with tenantId "<DEVELOPER_EMAIL>" in mongo
And there is already a edorg with stateOrganizationId "<SANDBOX_EDORG>" in mongo
And there is a landing zone for the "<SANDBOX_EDORG>" in mongo
And there is a landing zone for the "<SANDBOX_EDORG>" in LDAP
When the developer go to the provisioning application web page
Then the developer is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the developer provision a "sandbox" Landing zone with edorg is "<SANDBOX_EDORG>"
Then the directory structure for the landing zone is stored in ldap
Then the user gets an already provisioned message
