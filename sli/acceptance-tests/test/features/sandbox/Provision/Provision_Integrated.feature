@RALLY_US2281 @RALLY_US2844 @RALLY_US3251
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
And a request to provision a landing zone is made
And the directory structure for the landing zone is stored in ldap

@production
Scenario: As an ingestion admin I can use the provisioning tool to create a LZ for my district
Given there is an production Ingestion Admin account in ldap
And the account has a tenantId "<TENANTID>"
And the account has a edorg of "<PROD_EDORG>"
And there is no corresponding tenant in mongo
And there is no landing zone for the "<PROD_EDORG>" in mongo
When the Ingestion Admin go to the provisioning application web page
Then the Ingestion Admin is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the Ingestion Admin provision a Landing zone
Then the Ingestion Admin gets a success message
And a tenant with tenantId "<TENANTID>" created in Mongo
And a request to provision a landing zone is made
And the directory structure for the landing zone is stored in ldap
And the directory structure for the landing zone is stored for tenant in mongo


@production
Scenario: As an ingestion admin I can use the provisioning tool to get added to an existing LZ
Given there is an production Ingestion Admin account in ldap
And the account has a tenantId "<TENANTID>"
And the account has a edorg of "<PROD_EDORG>"
And there is already a tenant with tenantId "<TENANTID>" in mongo
And there is a landing zone for the "<PROD_EDORG>" in mongo
And there is no landing zone for the user in LDAP
When the Ingestion Admin go to the provisioning application web page
Then the Ingestion Admin is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the Ingestion Admin provision a Landing zone
And the directory structure for the landing zone is stored in ldap
And the Ingestion Admin gets a success message

@production
Scenario: As an ingestion admin I can use the provisioning tool to add a new district LZ
Given there is an production Ingestion Admin account in ldap
And the account has a tenantId "<TENANTID>"
And the account has a edorg of "<DISTRICT_EDORG>"
And there is already a tenant with tenantId "<TENANTID>" in mongo
And there is no landing zone for the "<DISTRICT_EDORG>" in mongo
And there is no landing zone for the user in LDAP
When the Ingestion Admin go to the provisioning application web page
Then the Ingestion Admin is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the Ingestion Admin provision a Landing zone
Then a request to provision a landing zone is made
And the directory structure for the landing zone is stored in ldap
And the directory structure for the landing zone is stored for tenant in mongo
And the Ingestion Admin gets a success message

@production
Scenario: As an ingestion admin I can use the provisioning tool to to have only one LZ as my home directory
Given there is an production Ingestion Admin account in ldap
And the account has a tenantId "<TENANTID>"
And the account has a edorg of "<PROD_EDORG>"
And there is already a tenant with tenantId "<TENANTID>" in mongo
And there is a landing zone for the "<PROD_EDORG>" in mongo
And there is a landing zone for the "<PROD_EDORG>" in LDAP
When the Ingestion Admin go to the provisioning application web page
Then the Ingestion Admin is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the Ingestion Admin provision a Landing zone
Then the user gets an error message

@sandbox
Scenario: As a developer I can use the provisioning tool to create a LZ for my sandbox tenancy
Given there is an sandbox account in ldap
And the account has a tenantId "<DEVELOPER_EMAIL>"
#And the account has a edorg of "<SANDBOX_EDORG>"
And there is no corresponding tenant in mongo
When the developer go to the provisioning application web page
Then the developer is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the developer provision a "sandbox" Landing zone with edorg is "<SANDBOX_EDORG>"
Then a tenant with tenantId "<DEVELOPER_EMAIL>" created in Mongo
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
And there is no landing zone for the "<SANDBOX_EDORG>" in mongo
When the developer go to the provisioning application web page
Then the developer is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the developer provision a "sandbox" Landing zone with edorg is "<SANDBOX_EDORG>"
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
And there is a landing zone for the "<SANDBOX_EDORG>" in mongo
And there is a landing zone for the "<SANDBOX_EDORG>" in LDAP
When the developer go to the provisioning application web page
Then the developer is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the developer provision a "sandbox" Landing zone with edorg is "<SANDBOX_EDORG>"
Then the directory structure for the landing zone is stored in ldap
Then the user gets an error message

@sandbox @auto-prov-ingest
Scenario: As a developer I can use the provisioning tool to pre-populate my tenant with a sample data set
Given there is an sandbox account in ldap
And the account has a tenantId "<DEVELOPER_EMAIL>"
And there is no corresponding tenant in mongo
When the developer go to the provisioning application web page
Then the developer is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
When the developer go to the provisioning application web page
When the developer selects to preload "Small Dataset"
Then a tenant with tenantId "<DEVELOPER_EMAIL>" created in Mongo
And a request to provision a landing zone is made
And the directory structure for the landing zone is stored in ldap
And the directory structure for the landing zone is stored for tenant in mongo
And the "small" data to preload is stored for the tenant in mongo
And the user gets a success message indicating preloading has been triggered
Then "20" seconds have elapsed
When the tenant with tenantId "<DEVELOPER_EMAIL>" is locked
When the developer go to the provisioning application web page
When the developer selects to preload "Small Dataset"
Then the user gets an error message
And the tenant with tenantId "<DEVELOPER_EMAIL>" is unlocked
Then "5" seconds have elapsed
And I go to my landing zone
Then a batch job log has been created
And I should not see an error log file created
And I should see "Processed 9729 records." in the resulting batch job file
Then "10" seconds have elapsed
And I clean the landing zone
And user's landing zone is still provisioned from the prior preloading of "Small Dataset"
When the developer go to the provisioning application web page
And the developer selects to preload "Small Dataset"
Then the "small" data to preload is stored for the tenant in mongo
And the user gets a success message indicating preloading has been triggered
Then "10" seconds have elapsed
And I go to my landing zone
Then a batch job log has been created
And I should not see an error log file created
And I should see "All records processed successfully." in the resulting batch job file
Then "10" seconds have elapsed
And I clean the landing zone

