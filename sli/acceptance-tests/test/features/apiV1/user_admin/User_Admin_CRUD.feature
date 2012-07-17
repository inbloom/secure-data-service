@RALLY_3076 @RALLY_3070
Feature: As an admin I can create admin accounts for tenancies I administer

  Background: none

  @wip
  @production
  Scenario Outline: As a admin I am able to read all Tenants
    Given I have a <ADMIN_ROLE>
    When I am authenticated on <ADMIN_REALM>
    And I navigate to GET <Tenant_URI>
    Then I should receive a return code of <CODE>
    And I should receive a list of tenants and <Tenant> is in the list
  Examples:
    |ADMIN_ROLE             |ADMIN_REALM                  |CODE|Tenant         |
    |SLC_Operator           |Shared Learning Collaborative|200 |IL             |
    |SEA_Super_Administrator|IL                           |200 |IL             |
    |LEA_Super_Administrator|IL-DAYBREAK                  |200 |IL             |
    |Realm_Administrator    |IL-DAYBREAK                  |401 |               |
    |Ingestion_Administrator|IL-DAYBREAK                  |401 |               |

  @wip
  @sandbox
  Scenario Outline: As a admin I am able to read all Tenants on sandbox
    Given I have a <ADMIN_ROLE> with <ALLOWED_RIGHTS> on <TENANT_ID>
    When I am authenticated on <ADMIN_REALM>
    And I navigate to GET <Tenants_URI>
    Then I should receive a return code of <CODE>
    And I should receive a list of tenants and <Tenant> is in the list
  Examples:
    |ADMIN_ROLE             |ADMIN_REALM                  |ALLOWED_RIGHTS       |CODE|Tenant           |
    |SLC_Operator           |Shared Learning Collaborative|DEFAULT              |200 |testuser@test.com|
    |Application_Developer  |IL                           |SB_ACCOUNT_MANAGEMENT|401 |                 |
    |Application_Developer  |IL-DAYBREAK                  |DEFAULT              |401 |                 |

  @wip
  Scenario Outline: As a admin I am able to read all Ed-Orgs in a tenancy
    Given I have a "<ADMIN_ROLE>"
    When I am authenticated on "<ADMIN_REALM>"
    And I navigate to GET "/users"
    And the "Tenant" is "<Tenant>"
    Then I should receive a return code of <CODE>
    And I should receive a list of Ed-Org and "<EdOrg>" is in the list
  Examples:
    |ADMIN_ROLE             |ADMIN_REALM                  |CODE|Tenant         |EdOrg         |
    |SLC_Operator           |Shared Learning Collaborative|200 |IL             |              |
    |SEA_Super_Administrator|IL                           |200 |IL             |IL            |
    |LEA_Super_Administrator|IL-DAYBREAK                  |200 |IL             |IL-DAYBREAK   |
    |Realm_Administrator    |IL-DAYBREAK                  |401 |IL             |IL-DAYBREAK   |
    |Ingestion_Administrator|IL-DAYBREAK                  |401 |IL             |IL-DAYBREAK   |

  @production
  Scenario Outline:  As an administrator I can read all admin accounts in my tenancy
    
    Given I have logged in to realm "<REALM>" using "<USER>" "<PASSWORD>"
    And I have a role "<ADMIN_ROLE>"
    And I navigate to GET "/users"
    Then I should receive a return code of <CODE>
    And I should receive a list of size "<Number>" of "<WANTED_ADMIN_ROLE>"
#    And each account has "fullName", "uid", "email", "createTime" and "modifyTime"
    And one of the accounts has "<Full_Name>", "<User_ID>", "<Email_Address>"
  Examples:
    |USER       |PASSWORD       |ADMIN_ROLE             |REALM      |WANTED_ADMIN_ROLE           |CODE|Number   |Full_Name       |User_ID                          |Email_Address               |
    |operator   |operator1234   |SLC Operator           |SLI        |SLC Operator                |200 |1 or more|Bill Operator   |slcoperator-email@slidev.org     |slcoperator-email@slidev.org|
    |operator   |operator1234   |SLC Operator           |SLI        |SEA Administrator           |200 |1 or more|NY Admin        |nyadmin                          |                            |
    |operator   |operator1234   |SLC Operator           |SLI        |LEA Administrator           |200 |1 or more|Daybreak Admin  |daybreakadmin                    |daybreakadmin@slidev.org    |
    |operator   |operator1234   |SLC Operator           |SLI        |Realm Administrator         |200 |1 or more|Mal Admin       |mreynolds                        |mreynolds@slidev.org        |
    |operator   |operator1234   |SLC Operator           |SLI        |Ingestion User              |200 |1 or more|Sunset IngestionUser|sunsetingestionuser          |sunsetingestionuser@slidev.org|
    |iladmin    |iladmin1234    |SEA Administrator      |SLI        |SLC Operator                |200 |0        |                |                                 |                            |
    |iladmin    |iladmin1234    |SEA Administrator      |SLI        |SEA Administrator           |200 |1 or more|IL Admin        |iladmin                          |                            |
    |iladmin    |iladmin1234    |SEA Administrator      |SLI        |LEA Administrator           |200 |1 or more|Daybreak Admin  |daybreakadmin                    |daybreakadmin@slidev.org    |
    |iladmin    |iladmin1234    |SEA Administrator      |SLI        |Realm Administrator         |200 |1 or more|Sunset RealmAdmin|sunsetrealmadmin                |sunsetrealmadmin@slidev.org |
    |iladmin    |iladmin1234    |SEA Administrator      |SLI        |Ingestion User              |200 |1 or more|Sunset IngestionUser|sunsetingestionuser          |sunsetingestionuser@slidev.org|
    |sunsetadmin|sunsetadmin1234|LEA Administrator      |SLI        |SLC Operator                |200 |0        |                |                                 |                            |
    |sunsetadmin|sunsetadmin1234|LEA Administrator      |SLI        |SEA Administrator           |200 |0        |                |                                 |                            |
    |sunsetadmin|sunsetadmin1234|LEA Administrator      |SLI        |LEA Administrator           |200 |1 or more|Sunset Admin    |sunsetadmin                      |                            |
    |sunsetadmin|sunsetadmin1234|LEA Administrator      |SLI        |Realm Administrator         |200 |1 or more|Sunset RealmAdmin|sunsetrealmadmin                |sunsetrealmadmin@slidev.org |
    |sunsetadmin|sunsetadmin1234|LEA Administrator      |SLI        |Ingestion User              |200 |1 or more|Sunset IngestionUser|sunsetingestionuser          |sunsetingestionuser@slidev.org|
    |sunsetrealmadmin|sunsetrealmadmin1234 |Realm Administrator     |SLI        |                |403 |         |                |                                  |                            |
    |ingestionuser   |ingestionuser1234    |Ingestion User          |SLI        |                |403 |         |                |                                  |                            |
  
  #sandbox
  Scenario Outline:  As a admin I am able to read all admin accounts in my tenancy on sandbox
   Given I have logged in to realm "<REALM>" using "<USER>" "<PASSWORD>"
    And I have a role "<ADMIN_ROLE>"
    And I navigate to GET "/users"
    Then I should receive a return code of <CODE>
    And I should receive a list of size "<Number>" of "<WANTED_ADMIN_ROLE>"
#    And each account has "fullName", "uid", "email", "createTime" and "modifyTime"
    And one of the accounts has "<Full_Name>", "<User_ID>", "<Email_Address>"

  Examples:
    |USER              |PASSWORD            |ADMIN_ROLE             |REALM      |WANTED_ADMIN_ROLE       |CODE|Number   |Full_Name        |User_ID                       |Email_Address                 |
    |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI        |Sandbox SLC Operator    |200 |1 or more|Sandbox Operator |sandboxslcoperator            |sandboxslcoperator@slidev.org |
    |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI        |Sandbox Administrator   |200 |1 or more|Sandbox Admin    |sandboxadministrator          |sandboxadministrator@slidev.org|
    |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI        |Application Developer   |200 |1 or more|Sandbox Developer|sandboxdeveloper              |sandboxdeveloper@slidev.org    |
    |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI        |Ingestion User          |200 |1 or more|Sandbox IngestionUser|sandboxingestionuser      |sandboxingestionuser@slidev.org|
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator|SLI     |Sandbox SLC Operator  |200 |0        |              |                              |                               |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator|SLI     |Sandbox Administrator |200 |1 or more|Sandbox Admin |sandboxadministrator          |sandboxadministrator@slidev.org|
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator|SLI     |Application Developer |200 |1 or more|Sandbox Developer|sandboxdeveloper           |sandboxdeveloper@slidev.org    |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator|SLI     |Ingestion User        |200 |1 or more|Sandbox IngestionUser|sandboxingestionuser   |sandboxingestionuser@slidev.org|
    |sandboxdeveloper |sandboxdeveloper1234 |Application Developer  |SLI         |                        |403 |         |                |                             |                               |
    |ingestionuser    |ingestionuser1234    |Ingestion User          |SLI        |                        |403 |        |                |                              |                              |
    
  @production
  Scenario Outline:  As a admin I am able to create/update admin accounts in my tenancy
    Given I have logged in to realm "<REALM>" using "<USER>" "<PASSWORD>"
    And I have a role "<ADMIN_ROLE>"
    And the new/update user has
    And "fullName" is "<Full_Name>"
    And "uid" is "<User_Name>"
    And "email" is "<Email_Address>"
    And "role" is "<CREATE_ADMIN_ROLE>"
    And "additional_role" is "<Additional_Role>"
    And "tenant" is "<Tenant>"
    And "edorg" is "<Ed_Org>"
    And the format is "application/json"
    And I navigate to "<ACTION>" "/users"
    Then I should receive a return code of <CODE>
    When I navigate to GET "/users"
    Then I should receive a return code of <READ_CODE>
    And I should receive a list of size "<Number>" of "<CREATE_ADMIN_ROLE>"
    And one of the accounts has "<Full_Name>", "<User_Name>", "<Email_Address>"

  Examples:
    |USER              |PASSWORD            |ADMIN_ROLE             |REALM    |CREATE_ADMIN_ROLE   |ACTION    |CODE|READ_CODE|Number   |Full_Name     |User_Name  |Email_Address        |Additional_Role |Tenant|Ed_Org     |
    |operator          |operator1234        |SLC Operator           |SLI      |SLC Operator        |POST      |201 |200      |1 or more|SLC Operator2 |SLC_Operator2     |SLC_Operator@test.com|               |      |           |
    |operator          |operator1234        |SLC Operator           |SLI      |SEA Administrator   |POST      |201 |200      |1 or more|SEA Administrator2 |SEA_Administrator2 |SEA_Administrator@test.com|    |Midgar| IL|
    |operator          |operator1234        |SLC Operator           |SLI      |LEA Administrator   |POST      |201 |200      |1 or more|LEA Administrator2 |LEA_Administrator2 |LEA_Administrator@test.com|    |Midgar| IL-SUNSET|
    |operator          |operator1234        |SLC Operator           |SLI      |Realm Administrator |POST      |201 |200      |1 or more|Realm Administrator2 |Realm_Administrator2 |Realm_Administrator@test.com|    |Midgar| IL-SUNSET|
    |operator          |operator1234        |SLC Operator           |SLI      |Ingestion User      |POST      |201 |200      |1 or more|Ingestion User2 |Ingestion_User2 |Ingestion_User@test.com|    |Midgar| IL-SUNSET|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |SLC Operator        |POST      |403 |200      |0         |SLC Operator3 |SLC_Operator3     |SLC_Operator@test.com|               |      |           |
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |SEA Administrator   |POST      |201 |200      |1 or more|SEA Administrator3 |SEA_Administrator3 |SEA_Administrator@test.com|    |Midgar| IL|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |LEA Administrator   |POST      |201 |200      |1 or more|LEA Administrator3 |LEA_Administrator3 |LEA_Administrator@test.com|    |Midgar| IL-SUNSET|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |Realm Administrator |POST      |201 |200      |1 or more|Realm Administrator3 |Realm_Administrator3 |Realm_Administrator@test.com|    |Midgar| IL-SUNSET|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |Ingestion User      |POST      |201 |200      |1 or more|Ingestion User3 |Ingestion_User3 |Ingestion_User@test.com|    |Midgar| IL-SUNSET|
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |SLC Operator        |POST      |403 |200      |0        |SLC Operator4   |SLC_Operator4   |SLC_Operator@test.com|               |      |           | 
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |SEA Administrator   |POST      |403 |200      |0        |SEA Administrator4 |SEA_Administrator4 |SEA_Administrator@test.com|    |Midgar| IL|
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |LEA Administrator   |POST      |201 |200      |1 or more|LEA Administrator4 |LEA_Administrator4 |LEA_Administrator@test.com|Realm Administrator   |Midgar| IL-SUNSET|
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |Realm Administrator |POST      |201 |200      |1 or more|Realm Administrator4 |Realm_Administrator4 |Realm_Administrator@test.com|Ingestion User    |Midgar| IL-SUNSET|
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |Ingestion User      |POST      |201 |200      |1 or more|Ingestion User4 |Ingestion_User4 |Ingestion_User@test.com|    |Midgar| IL-SUNSET|
    |sunsetrealmadmin  |sunsetrealmadmin1234 |Realm Administrator   |SLI      |SLC Operator        |POST      |403 |403      |0        |SLC Operator5 |SLC_Operator5     |SLC_Operator@test.com|               |      |           |
    |sunsetrealmadmin  |sunsetrealmadmin1234 |Realm Administrator   |SLI      |SEA Administrator   |POST      |403 |403      |0        |SEA Administrator5 |SEA_Administrator5 |SEA_Administrator@test.com|    |Midgar| IL|
    |sunsetrealmadmin  |sunsetrealmadmin1234 |Realm Administrator   |SLI      |LEA Administrator   |POST      |403 |403      |0        |LEA Administrator5 |LEA_Administrator5 |LEA_Administrator@test.com|    |Midgar| IL|
    |sunsetrealmadmin  |sunsetrealmadmin1234 |Realm Administrator   |SLI      |Realm Administrator |POST      |403 |403      |0        |Realm Administrator5 |Realm_Administrator5 |Realm_Administrator@test.com|    |Midgar| IL-SUNSET|
    |sunsetrealmadmin  |sunsetrealmadmin1234 |Realm Administrator   |SLI      |Ingestion User      |POST      |403 |403      |0        |Ingestion User5 |Ingestion_User5 |Ingestion_User@test.com|    |Midgar| IL-SUNSET|
    |ingestionuser     |ingestionuser1234    |Ingestion User        |SLI      |SLC Operator        |POST      |403 |403      |0        |SLC Operator6 |SLC_Operator6     |SLC_Operator@test.com|               |      |           |
    |ingestionuser     |ingestionuser1234    |Ingestion User        |SLI      |SEA Administrator   |POST      |403 |403      |0        |SEA Administrator6 |SEA_Administrator6 |SEA_Administrator@test.com|    |Midgar| IL|
    |ingestionuser     |ingestionuser1234    |Ingestion User        |SLI      |LEA Administrator   |POST      |403 |403      |0        |LEA Administrator6 |LEA_Administrator6 |LEA_Administrator@test.com|    |Midgar| IL|
    |ingestionuser     |ingestionuser1234    |Ingestion User        |SLI      |Realm Administrator |POST      |403 |403      |0        |Realm Administrator6 |Realm_Administrator6 |Realm_Administrator@test.com|    |Midgar| IL-SUNSET|
    |ingestionuser     |ingestionuser1234    |Ingestion User        |SLI      |Ingestion User      |POST      |403 |403      |0        |Ingestion User6 |Ingestion_User6 |Ingestion_User@test.com|    |Midgar| IL-SUNSET| 
    |operator          |operator1234        |SLC Operator           |SLI      |SLC Operator        |PUT      |204 |200      |1 or more|SLC Operator7 |SLC_Operator7     |SLC_Operator@test.com|               |      |           |
    |operator          |operator1234        |SLC Operator           |SLI      |SEA Administrator   |PUT      |204 |200      |1 or more|SEA Administrator7 |SEA_Administrator7 |SEA_Administrator@test.com|    |Midgar| IL|
    |operator          |operator1234        |SLC Operator           |SLI      |LEA Administrator   |PUT      |204 |200      |1 or more|LEA Administrator7 |LEA_Administrator7 |LEA_Administrator@test.com|    |Midgar| IL-SUNSET|
    |operator          |operator1234        |SLC Operator           |SLI      |Realm Administrator |PUT      |204 |200      |1 or more|Realm Administrator7 |Realm_Administrator7 |Realm_Administrator@test.com|    |Midgar| IL-SUNSET|
    |operator          |operator1234        |SLC Operator           |SLI      |Ingestion User      |PUT      |204 |200      |1 or more|Ingestion User7 |Ingestion_User7 |Ingestion_User@test.com|    |Midgar| IL-SUNSET|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |SLC Operator        |PUT      |403 |200      |0         |SLC Operator8 |SLC_Operator8     |SLC_Operator@test.com|               |      |           |
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |SEA Administrator   |PUT      |204 |200      |1 or more|SEA Administrator8 |SEA_Administrator8 |SEA_Administrator@test.com|    |Midgar| IL|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |LEA Administrator   |PUT      |204 |200      |1 or more|LEA Administrator8 |LEA_Administrator8 |LEA_Administrator@test.com|    |Midgar| IL-SUNSET|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |Realm Administrator |PUT      |204 |200      |1 or more|Realm Administrator8 |Realm_Administrator8 |Realm_Administrator@test.com|    |Midgar| IL-SUNSET|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |Ingestion User      |PUT      |204 |200      |1 or more|Ingestion User8 |Ingestion_User8 |Ingestion_User@test.com|    |Midgar| IL-SUNSET|
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |SLC Operator        |PUT      |403 |200      |0        |SLC Operator9   |SLC_Operator9   |SLC_Operator@test.com|               |      |           | 
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |SEA Administrator   |PUT      |403 |200      |0        |SEA Administrator9 |SEA_Administrator9 |SEA_Administrator@test.com|    |Midgar| IL|
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |LEA Administrator   |PUT      |204 |200      |1 or more|LEA Administrator9 |LEA_Administrator9 |LEA_Administrator@test.com|Realm Administrator   |Midgar| IL-SUNSET|
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |Realm Administrator |PUT      |204 |200      |1 or more|Realm Administrator9 |Realm_Administrator9 |Realm_Administrator@test.com|Ingestion User    |Midgar| IL-SUNSET|
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |Ingestion User      |PUT      |204 |200      |1 or more|Ingestion User9 |Ingestion_User9 |Ingestion_User@test.com|    |Midgar| IL-SUNSET|
    |sunsetrealmadmin  |sunsetrealmadmin1234 |Realm Administrator   |SLI      |SLC Operator        |PUT      |403 |403      |0        |SLC Operator10 |SLC_Operator10     |SLC_Operator@test.com|               |      |           |
    |sunsetrealmadmin  |sunsetrealmadmin1234 |Realm Administrator   |SLI      |SEA Administrator   |PUT      |403 |403      |0        |SEA Administrator10 |SEA_Administrator10 |SEA_Administrator@test.com|    |Midgar| IL|
    |sunsetrealmadmin  |sunsetrealmadmin1234 |Realm Administrator   |SLI      |LEA Administrator   |PUT      |403 |403      |0        |LEA Administrator10 |LEA_Administrator10 |LEA_Administrator@test.com|    |Midgar| IL|
    |sunsetrealmadmin  |sunsetrealmadmin1234 |Realm Administrator   |SLI      |Realm Administrator |PUT      |403 |403      |0        |Realm Administrator10 |Realm_Administrator10 |Realm_Administrator@test.com|    |Midgar| IL-SUNSET|
    |sunsetrealmadmin  |sunsetrealmadmin1234 |Realm Administrator   |SLI      |Ingestion User      |PUT      |403 |403      |0        |Ingestion User10 |Ingestion_User10 |Ingestion_User@test.com|    |Midgar| IL-SUNSET|
    |ingestionuser     |ingestionuser1234    |Ingestion User        |SLI      |SLC Operator        |PUT      |403 |403      |0        |SLC Operator11 |SLC_Operator11     |SLC_Operator@test.com|               |      |           |
    |ingestionuser     |ingestionuser1234    |Ingestion User        |SLI      |SEA Administrator   |PUT      |403 |403      |0        |SEA Administrator11 |SEA_Administrator11 |SEA_Administrator@test.com|    |Midgar| IL|
    |ingestionuser     |ingestionuser1234    |Ingestion User        |SLI      |LEA Administrator   |PUT      |403 |403      |0        |LEA Administrator11 |LEA_Administrator11 |LEA_Administrator@test.com|    |Midgar| IL|
    |ingestionuser     |ingestionuser1234    |Ingestion User        |SLI      |Realm Administrator |PUT      |403 |403      |0        |Realm Administrator11 |Realm_Administrator11 |Realm_Administrator@test.com|    |Midgar| IL-SUNSET|
    |ingestionuser     |ingestionuser1234    |Ingestion User        |SLI      |Ingestion User      |PUT      |403 |403      |0        |Ingestion User11 |Ingestion_User11 |Ingestion_User@test.com|    |Midgar| IL-SUNSET| 


  #sandbox
  Scenario Outline:  As a admin I am able to create/update admin accounts in my tenancy on sandbox
    Given I have logged in to realm "<REALM>" using "<USER>" "<PASSWORD>"
    And I have a role "<ADMIN_ROLE>"
    And the new/update user has
    And "fullName" is "<Full_Name>"
    And "uid" is "<User_Name>"
    And "email" is "<Email_Address>"
    And "role" is "<CREATE_ADMIN_ROLE>"
    And "additional_role" is "<Additional_Role>"
    And "tenant" is "<Tenant>"
    And "edorg" is "<Ed_Org>"
    And the format is "application/json"
    And I navigate to "<ACTION>" "/users"
    Then I should receive a return code of <CODE>
    When I navigate to GET "/users"
    Then I should receive a return code of <READ_CODE>
    And I should receive a list of size "<Number>" of "<CREATE_ADMIN_ROLE>"
    And one of the accounts has "<Full_Name>", "<User_Name>", "<Email_Address>"

  Examples:
  |USER              |PASSWORD            |ADMIN_ROLE             |REALM    |CREATE_ADMIN_ROLE   |ACTION    |CODE|READ_CODE|Number   |Full_Name            |User_Name            |Email_Address                |Additional_Role |Tenant|Ed_Org     |
  |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI      |Sandbox SLC Operator|POST      |201 |200      |1 or more|Sandbox SLCOperator2 |Sandbox_SLC_Operator2|sandbox_SLC_Operator@test.com|                |      |           |
 # |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI      |Sandbox Administrator|POST      |201 |200     |1 or more|Sandbox Administrator2 |Sandbox_Administrator2|sandboxadministrator@slidev.org|            |sandboxadministrator@slidev.org|   |
 
  
#    |ADMIN_ROLE             |ADMIN_REALM                  |WANTED_ROLE                 |ALLOWED_RIGHTS       |ACTION    |CODE|READ_CODE|NUMBER|Full_Name          |User_Name  |Email_Address         |Tenant                |
#    |SLC_Operator           |Shared Learning Collaborative|SLC_Operator                |DEFAULT              |POST      |200 |200      |1     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |                      |
#    |SLC_Operator           |Shared Learning Collaborative|Admin_App_Developer         |DEFAULT              |POST      |200 |200      |1     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|Admin_App_Dev@test.com|
#    |SLC_Operator           |Shared Learning Collaborative|Application_Developer       |DEFAULT              |POST      |200 |200      |1     |App_Developer      |AppDev     |App_Dev@test.com      |Admin_App_Dev@test.com|
#    |Application_Developer  |Shared Learning Collaborative|SLC_Operator                |SB_ACCOUNT_MANAGEMENT|POST      |401 |401      |0     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |                      |
#    |Application_Developer  |Shared Learning Collaborative|Admin_App_Developer         |SB_ACCOUNT_MANAGEMENT|POST      |200 |200      |1     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|Admin_App_Dev@test.com|
#    |Application_Developer  |Shared Learning Collaborative|Application_Developer       |SB_ACCOUNT_MANAGEMENT|POST      |200 |200      |1     |App_Developer      |AppDev     |App_Dev@test.com      |Admin_App_Dev@test.com|
#    |Application_Developer  |Shared Learning Collaborative|SLC_Operator                |DEFAULT              |POST      |401 |401      |0     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |                      |
#    |Application_Developer  |Shared Learning Collaborative|Admin_App_Developer         |DEFAULT              |POST      |401 |401      |0     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|Admin_App_Dev@test.com|
#    |Application_Developer  |Shared Learning Collaborative|Application_Developer       |DEFAULT              |POST      |401 |401      |1     |App_Developer      |AppDev     |App_Dev@test.com      |Admin_App_Dev@test.com|
#    |SLC_Operator           |Shared Learning Collaborative|SLC_Operator                |DEFAULT              |PUT       |401 |401      |1     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |                      |
#    |SLC_Operator           |Shared Learning Collaborative|Admin_App_Developer         |DEFAULT              |PUT       |200 |200      |1     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|Admin_App_Dev@test.com|
#    |SLC_Operator           |Shared Learning Collaborative|Application_Developer       |DEFAULT              |PUT       |200 |200      |1     |App_Developer      |AppDev     |App_Dev@test.com      |Admin_App_Dev@test.com|
#    |Application_Developer  |Shared Learning Collaborative|SLC_Operator                |SB_ACCOUNT_MANAGEMENT|PUT       |401 |401      |0     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |                      |
#    |Application_Developer  |Shared Learning Collaborative|Admin_App_Developer         |SB_ACCOUNT_MANAGEMENT|PUT       |200 |200      |1     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|Admin_App_Dev@test.com|
#    |Application_Developer  |Shared Learning Collaborative|Application_Developer       |SB_ACCOUNT_MANAGEMENT|PUT       |200 |200      |1     |App_Developer      |AppDev     |App_Dev@test.com      |Admin_App_Dev@test.com|
#    |Application_Developer  |Shared Learning Collaborative|SLC_Operator                |DEFAULT              |PUT       |401 |401      |0     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |                      |
#    |Application_Developer  |Shared Learning Collaborative|Admin_App_Developer         |DEFAULT              |PUT       |401 |401      |0     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|Admin_App_Dev@test.com|
#    |Application_Developer  |Shared Learning Collaborative|Application_Developer       |DEFAULT              |PUT       |401 |401      |1     |App_Developer      |AppDev     |App_Dev@test.com      |Admin_App_Dev@test.com|

  @wip
  @production
  Scenario Outline:  As a admin I am able to delete admin accounts in my tenancy if there will be 1 left
    Given I have a <ADMIN_ROLE>
    When I am authenticated on <ADMIN_REALM>
    And I navigate to DELETE  <WANTED_ADMIN_ROLE_URI>
    And there are 2 admins with <WANTED_ADMIN_ROLE>
    Then I should receive a return code of <CODE>
    When I navigate to DELETE  <WANTED_ADMIN_ROLE_URI>
    And there is only one admin with <WANTED_ADMIN_ROLE>
    Then I should receive a return code of 400

  Examples:
    |ADMIN_ROLE             |ADMIN_REALM                  |WANTED_ADMIN_ROLE           |CODE|
    |SLC_Operator           |Shared Learning Collaborative|SLC_Operator                |200 |
    |SLC_Operator           |Shared Learning Collaborative|SEA_Super_Administrator     |200 |
    |SLC_Operator           |Shared Learning Collaborative|LEA_Super_Administrator     |200 |
    |SLC_Operator           |Shared Learning Collaborative|Realm_Administrator         |200 |
    |SLC_Operator           |Shared Learning Collaborative|Ingestion_Administrator     |200 |
    |SEA_Super_Administrator|IL                           |SLC_Operator                |401 |
    |SEA_Super_Administrator|IL                           |SEA_Super_Administrator     |200 |
    |SEA_Super_Administrator|IL                           |LEA_Super_Administrator     |200 |
    |SEA_Super_Administrator|IL                           |Realm_Administrator         |200 |
    |SEA_Super_Administrator|IL                           |Ingestion_Administrator     |200 |
    |LEA_Super_Administrator|IL-SUNSET                    |SLC_Operator                |401 |
    |LEA_Super_Administrator|IL-SUNSET                    |SEA_Super_Administrator     |401 |
    |LEA_Super_Administrator|IL-SUNSET                    |LEA_Super_Administrator     |200 |
    |LEA_Super_Administrator|IL-SUNSET                    |Realm_Administrator         |200 |
    |LEA_Super_Administrator|IL-SUNSET                    |Ingestion_Administrator     |200 |
    |Realm_Administrator    |IL-SUNSET                    |SLC_Operator                |401 |
    |Realm_Administrator    |IL-SUNSET                    |SEA_Super_Administrator     |401 |
    |Realm_Administrator    |IL-SUNSET                    |LEA_Super_Administrator     |401 |
    |Realm_Administrator    |IL-SUNSET                    |Realm_Administrator         |401 |
    |Realm_Administrator    |IL-SUNSET                    |Ingestion_Administrator     |401 |
    |Ingestion_Administrator|IL-SUNSET                    |SLC_Operator                |401 |
    |Ingestion_Administrator|IL-SUNSET                    |SEA_Super_Administrator     |401 |
    |Ingestion_Administrator|IL-SUNSET                    |LEA_Super_Administrator     |401 |
    |Ingestion_Administrator|IL-SUNSET                    |Realm_Administrator         |401 |
    |Ingestion_Administrator|IL-SUNSET                    |Ingestion_Administrator     |401 |

  @wip
  @sandbox
  Scenario Outline:  As a admin on sandbox I am able to delete admin accounts in my tenancy if there will be 1 left
    Given I have a <ADMIN_ROLE> with <ALLOWED_RIGHTS> on <TENANT_ID>
    When I am authenticated on <ADMIN_REALM>
    And I navigate to DELETE  <WANTED_ADMIN_ROLE_URI>
    And there are 2 admins with <WANTED_ADMIN_ROLE>
    Then I should receive a return code of <CODE>
    When I navigate to DELETE  <WANTED_ADMIN_ROLE_URI
    And there is only one admin with <WANTED_ADMIN_ROLE>
    Then I should receive a return code of 400

  Examples:
    |ADMIN_ROLE             |ADMIN_REALM                  |ALLOWED_RIGHTS       |WANTED_ADMIN_ROLE           |CODE|
    |SLC_Operator           |Shared Learning Collaborative|DEFAULT              |SLC_Operator                |200 |
    |SLC_Operator           |Shared Learning Collaborative|DEFAULT              |SEA_Super_Administrator     |200 |
    |SLC_Operator           |Shared Learning Collaborative|DEFAULT              |LEA_Super_Administrator     |200 |
    |SLC_Operator           |Shared Learning Collaborative|DEFAULT              |Realm_Administrator         |200 |
    |SLC_Operator           |Shared Learning Collaborative|DEFAULT              |Ingestion_Administrator     |200 |
    |Application_Developer  |Shared Learning Collaborative|SB_ACCOUNT_MANAGEMENT|SLC_Operator                |401 |
    |Application_Developer  |Shared Learning Collaborative|SB_ACCOUNT_MANAGEMENT|Admin_App_Developer         |200 |
    |Application_Developer  |Shared Learning Collaborative|SB_ACCOUNT_MANAGEMENT|Application_Developer       |200 |
    |Application_Developer  |Shared Learning Collaborative|SB_ACCOUNT_MANAGEMENT|Realm_Administrator         |200 |
    |Application_Developer  |Shared Learning Collaborative|SB_ACCOUNT_MANAGEMENT|Ingestion_Administrator     |200 |
    |Application_Developer  |Shared Learning Collaborative|DEFAULT              |SLC_Operator                |401 |
    |Application_Developer  |Shared Learning Collaborative|DEFAULT              |Admin_App_Developer         |401 |
    |Application_Developer  |Shared Learning Collaborative|DEFAULT              |Application_Developer       |401 |
    |Application_Developer  |Shared Learning Collaborative|DEFAULT              |Realm_Administrator         |401 |
    |Application_Developer  |Shared Learning Collaborative|DEFAULT              |Ingestion_Administrator     |401 |
    |Realm_Administrator    |Shared Learning Collaborative|DEFAULT              |SLC_Operator                |401 |
    |Realm_Administrator    |Shared Learning Collaborative|DEFAULT              |Admin_App_Developer         |401 |
    |Realm_Administrator    |Shared Learning Collaborative|DEFAULT              |Application_Developer       |401 |
    |Realm_Administrator    |Shared Learning Collaborative|DEFAULT              |Realm_Administrator         |401 |
    |Realm_Administrator    |Shared Learning Collaborative|DEFAULT              |Ingestion_Administrator     |401 |
    |Ingestion_Administrator|Shared Learning Collaborative|DEFAULT              |SLC_Operator                |401 |
    |Ingestion_Administrator|Shared Learning Collaborative|DEFAULT              |Admin_App_Developer         |401 |
    |Ingestion_Administrator|Shared Learning Collaborative|DEFAULT              |Application_Developer       |401 |
    |Ingestion_Administrator|Shared Learning Collaborative|DEFAULT              |Realm_Administrator         |401 |
    |Ingestion_Administrator|Shared Learning Collaborative|DEFAULT              |Ingestion_Administrator     |401 |

