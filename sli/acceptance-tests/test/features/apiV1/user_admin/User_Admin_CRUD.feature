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
    Given I am logged in using "<USER>" "<PASSWORD>" to realm "<REALM>"
    And I navigate to GET "/users"
    Then I should receive a return code of <CODE>
    And I have a uid "<UID>" and role "<ADMIN_ROLE>"
    And I should receive a list of size "<Number>" of "<WANTED_ADMIN_ROLE>"
#    And each account has "fullName", "uid", "email", "createTime" and "modifyTime"
    And one of the accounts has "<Full_Name>", "<User_ID>", "<Email_Address>"
  Examples:
    |USER       |PASSWORD       |UID         |ADMIN_ROLE             |REALM      |WANTED_ADMIN_ROLE           |CODE|Number   |Full_Name       |User_ID                          |Email_Address               |
    |operator   |operator1234   |slcoperator |SLC Operator           |SLI        |SLC Operator                |200 |1 or more|Bill Operator   |slcoperator-email@slidev.org     |slcoperator-email@slidev.org|
    |operator   |operator1234   |slcoperator |SLC Operator           |SLI        |SEA Administrator           |200 |1 or more|NY Admin        |nyadmin                          |                            |
    |operator   |operator1234   |slcoperator |SLC Operator           |SLI        |LEA Administrator           |200 |1 or more|Daybreak Admin  |daybreakadmin                    |daybreakadmin@slidev.org    |
    |operator   |operator1234   |slcoperator |SLC Operator           |SLI        |Realm Administrator         |200 |1 or more|Mal Admin       |mreynolds                        |mreynolds@slidev.org        |
    |operator   |operator1234   |slcoperator |SLC Operator           |SLI        |Ingestion Administrator     |200 |0        |                |                                 |                            |
    |iladmin    |iladmin1234    |iladmin     |SEA Administrator      |SLI        |SLC Operator                |200 |0        |                |                                 |                            |
    |iladmin    |iladmin1234    |iladmin     |SEA Administrator      |SLI        |SEA Administrator           |200 |1 or more|IL Admin        |iladmin                          |                            |
    |iladmin    |iladmin1234    |iladmin     |SEA Administrator      |SLI        |LEA Administrator           |200 |1 or more|Daybreak Admin  |daybreakadmin                    |daybreakadmin@slidev.org    |
    |iladmin    |iladmin1234    |iladmin     |SEA Administrator      |SLI        |Realm Administrator         |200 |1 or more|Sunset RealmAdmin|sunsetrealadmin                 |sunsetrealmadmin@slidev.org |
    |iladmin    |iladmin1234    |iladmin     |SEA Administrator      |SLI        |Ingestion Administrator     |200 |0        |                |                                 |                            |
    |sunsetadmin|sunsetadmin1234|sunsetadmin |LEA Administrator      |SLI        |SLC Operator                |200 |0        |                |                                 |                            |
    |sunsetadmin|sunsetadmin1234|sunsetadmin |LEA Administrator      |SLI        |SEA Administrator           |200 |0        |                |                                 |                            |
    |sunsetadmin|sunsetadmin1234|sunsetadmin |LEA Administrator      |SLI        |LEA Administrator           |200 |1 or more|Sunset Admin    |sunsetdmin                       |                            |
    |sunsetadmin|sunsetadmin1234|sunsetadmin |LEA Administrator      |SLI        |Realm Administrator         |200 |1 or more|Sunset RealmAdmin|sunsetrealmadmin                |sunsetrealmadmin@slidev.org |
    |sunsetadmin|sunsetadmin1234|sunsetadmin |LEA Administrator      |SLI        |Ingestion User              |200 |1 or more|Sunset IngestionUser|sunsetingestionuser          |sunsetingestionuser@slidev.org|
    |sunsetrealmadmin|sunsetrealmadmin1234 |            |           |SLI        |                            |403 |         |                |                                  |                            |
    |ingestionuser   |ingestionuser1234    |            |           |SLI        |                            |403 |         |                |                                  |                            |

  @wip
  @sandbox
  Scenario Outline:  As a admin I am able to read all admin accounts in my tenancy on sandbox
    Given I have a <ADMIN_ROLE>  with <ALLOWED_RIGHTS> on <TENANT_ID>
    When I am authenticated on <ADMIN_REALM>
    And I navigate to GET <ADMIN_ACCOUT_URI>
    Then I should receive a return code of <CODE>
    And then I should receive a list <Number> of <WANTED_ADMIN_ROLE>
    And each account has <Full_Name>, <User_name>, <Email_Address>, "Date_Created" and "Date_Updated"

  Examples:
    |ADMIN_ROLE             |ADMIN_REALM                  |WANTED_ADMIN_ROLE           |ALLOWED_RIGHTS          |CODE|Number|Full_Name          |User_name  |Email_Address         |
    |SLC_Operator           |Shared Learning Collaborative|SLC_Operator                |DEFAULT                 |200 |1     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |
    |SLC_Operator           |Shared Learning Collaborative|Admin_App_Developer         |DEFAULT                 |200 |1     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|
    |SLC_Operator           |Shared Learning Collaborative|Application_Developer       |DEFAULT                 |200 |1     |App_Developer      |AppDev     |App_Dev@test.com      |
    |Application_Developer  |Shared Learning Collaborative|SLC_Operator                |ADMIN_ACCOUNT_MANAGEMENT|401 |0     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |
    |Application_Developer  |Shared Learning Collaborative|Admin_App_Developer         |ADMIN_ACCOUNT_MANAGEMENT|200 |1     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|
    |Application_Developer  |Shared Learning Collaborative|Application_Developer       |ADMIN_ACCOUNT_MANAGEMENT|200 |1     |App_Developer      |AppDev     |App_Dev@test.com      |
    |Application_Developer  |Shared Learning Collaborative|SLC_Operator                |DEFAULT                 |401 |0     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |
    |Application_Developer  |Shared Learning Collaborative|Admin_App_Developer         |DEFAULT                 |401 |0     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|
    |Application_Developer  |Shared Learning Collaborative|Application_Developer       |DEFAULT                 |200 |1     |App_Developer      |AppDev     |App_Dev@test.com      |

  @wip
  @production
  Scenario Outline:  As a admin I am able to create/update admin accounts in my tenancy
    Given I have a <ADMIN_ROLE>
    When I am authenticated on <ADMIN_REALM>
    And I navigate to <ACTION> <CREATE_ADMIN_ROLE>
    And "Full_Name" is <Full_Name>
    And "Username" is <User_Name>
    And "Email" is <Email_Address>
    And "Role" is <CREATE_ADMIN_ROLE>
    And "Additional Role" is <Additional_Role>
    And "Tenant" is <Tenant>
    And "Edorg" is <Ed_Org>
    Then I should receive a return code of <CODE>
    When I navigate to GET <CREATE_ADMIN_ROLE>
    Then I should receive a return code of <READ_CODE>
    And then I should receive a list <NUMBER> of <WANTED_ADMIN_ROLE>
    And each account has <Full_Name>, <User_name>, <Email_Address>, "Date_Created" and "Date_Updated"

  Examples:
    |ADMIN_ROLE             |ADMIN_REALM                  |CREATE_ADMIN_ROLE           |ACTION    |CODE|READ_CODE|NUMBER|Full_Name     |User_Name  |Email_Address        |Addtional_Role |Tenant|Ed_Org     |
    |SLC_Operator           |Shared Learning Collaborative|SLC_Operator                |POST      |200 |200      |1     |SLC_Operator_2|SLCOP2     |SLC_Operator@test.com|               |      |           |
    |SLC_Operator           |Shared Learning Collaborative|SEA_Super_Administrator     |POST      |200 |200      |1     |SEA_Admin     |SeaAdmin   |SeaAdmin@test.com    |               |      |           |
    |SLC_Operator           |Shared Learning Collaborative|LEA_Super_Administrator     |POST      |200 |200      |1     |LEA_Admin     |LeaAdmin   |LeaAdmin@test.com    |               |      |           |
    |SLC_Operator           |Shared Learning Collaborative|Realm_Administrator         |POST      |200 |200      |1     |Realm_Admin   |RealmAdmin |RealmAdmin@test.com  |               |      |           |
    |SLC_Operator           |Shared Learning Collaborative|Ingestion_Administrator     |POST      |200 |200      |1     |Ingest_Admin  |IngestAdmin|IngestAdmin@test.com |               |      |           |
    |SEA_Super_Administrator|IL                           |SLC_Operator                |POST      |401 |401      |0     |SLC_Operator_2|SLCOP2     |SLC_Operator@test.com|               |      |           |
    |SEA_Super_Administrator|IL                           |SEA_Super_Administrator     |POST      |200 |200      |1     |SEA_Admin     |SeaAdmin   |SeaAdmin@test.com    |               |IL    |IL         |
    |SEA_Super_Administrator|IL                           |LEA_Super_Administrator     |POST      |200 |200      |1     |LEA_Admin     |LeaAdmin   |LeaAdmin@test.com    |Ingestion_Admin|IL    |IL-DAYBREAK|
    |SEA_Super_Administrator|IL                           |Realm_Administrator         |POST      |200 |200      |1     |Realm_Admin   |RealmAdmin |RealmAdmin@test.com  |Realm_Admin    |IL    |IL-DAYBREAK|
    |SEA_Super_Administrator|IL                           |Ingestion_Administrator     |POST      |200 |200      |1     |Ingest_Admin  |IngestAdmin|IngestAdmin@test.com |               |      |           |
    |LEA_Super_Administrator|IL-SUNSET                    |SLC_Operator                |POST      |401 |401      |0     |SLC_Operator_2|SLCOP2     |SLC_Operator@test.com|               |      |           |
    |LEA_Super_Administrator|IL-SUNSET                    |SEA_Super_Administrator     |POST      |401 |401      |0     |SEA_Admin     |SeaAdmin   |SeaAdmin@test.com    |               |IL    |IL         |
    |LEA_Super_Administrator|IL-SUNSET                    |LEA_Super_Administrator     |POST      |200 |200      |1     |LEA_Admin     |LeaAdmin   |LeaAdmin@test.com    |Ingestion_Admin|IL    |IL-DAYBREAK|
    |LEA_Super_Administrator|IL-SUNSET                    |Realm_Administrator         |POST      |200 |200      |1     |Realm_Admin   |RealmAdmin |RealmAdmin@test.com  |Realm_Admin    |IL    |IL-DAYBREAK|
    |LEA_Super_Administrator|IL-SUNSET                    |Ingestion_Administrator     |POST      |200 |200      |1     |Ingest_Admin  |IngestAdmin|IngestAdmin@test.com |Ingestion_Admin|IL    |IL-DAYBREAK|
    |Realm_Administrator    |IL-SUNSET                    |SLC_Operator                |POST      |401 |401      |0     |SLC_Operator_2|SLCOP2     |SLC_Operator@test.com|               |      |           |
    |Realm_Administrator    |IL-SUNSET                    |SEA_Super_Administrator     |POST      |401 |401      |0     |SEA_Admin     |SeaAdmin   |SeaAdmin@test.com    |               |IL    |IL         |
    |Realm_Administrator    |IL-SUNSET                    |LEA_Super_Administrator     |POST      |401 |401      |0     |LEA_Admin     |LeaAdmin   |LeaAdmin@test.com    |Ingestion_Admin|IL    |IL-DAYBREAK|
    |Realm_Administrator    |IL-SUNSET                    |Realm_Administrator         |POST      |401 |401      |0     |Realm_Admin   |RealmAdmin |RealmAdmin@test.com  |Realm_Admin    |IL    |IL-DAYBREAK|
    |Realm_Administrator    |IL-SUNSET                    |Ingestion_Administrator     |POST      |401 |401      |0     |Ingest_Admin  |IngestAdmin|IngestAdmin@test.com |Ingestion_Admin|IL    |IL-DAYBREAK|
    |Ingestion_Administrator|IL-SUNSET                    |SLC_Operator                |POST      |401 |401      |0     |SLC_Operator_2|SLCOP2     |SLC_Operator@test.com|               |      |           |
    |Ingestion_Administrator|IL-SUNSET                    |SEA_Super_Administrator     |POST      |401 |401      |0     |SEA_Admin     |SeaAdmin   |SeaAdmin@test.com    |               |IL    |IL         |
    |Ingestion_Administrator|IL-SUNSET                    |LEA_Super_Administrator     |POST      |401 |401      |0     |LEA_Admin     |LeaAdmin   |LeaAdmin@test.com    |Ingestion_Admin|IL    |IL-DAYBREAK|
    |Ingestion_Administrator|IL-SUNSET                    |Realm_Administrator         |POST      |401 |401      |0     |Realm_Admin   |RealmAdmin |RealmAdmin@test.com  |Realm_Admin    |IL    |IL-DAYBREAK|
    |Ingestion_Administrator|IL-SUNSET                    |Ingestion_Administrator     |POST      |401 |401      |0     |Ingest_Admin  |IngestAdmin|IngestAdmin@test.com |Ingestion_Admin|IL    |IL-DAYBREAK|
    |SLC_Operator           |Shared Learning Collaborative|SLC_Operator                |PUT       |200 |200      |1     |SLC_Operator_2|SLCOP2     |SLC_Operator@test.com|               |      |           |
    |SLC_Operator           |Shared Learning Collaborative|SEA_Super_Administrator     |PUT       |200 |200      |1     |SEA_Admin     |SeaAdmin   |SeaAdmin@test.com    |               |IL    |IL         |
    |SLC_Operator           |Shared Learning Collaborative|LEA_Super_Administrator     |PUT       |200 |200      |1     |LEA_Admin     |LeaAdmin   |LeaAdmin@test.com    |Ingestion_Admin|IL    |IL-DAYBREAK|
    |SLC_Operator           |Shared Learning Collaborative|Realm_Administrator         |PUT       |200 |200      |1     |Realm_Admin   |RealmAdmin |RealmAdmin@test.com  |Realm_Admin    |IL    |IL-DAYBREAK|
    |SLC_Operator           |Shared Learning Collaborative|Ingestion_Administrator     |PUT       |200 |200      |1     |Ingest_Admin  |IngestAdmin|IngestAdmin@test.com |Ingestion_Admin|IL    |IL-DAYBREAK|
    |SEA_Super_Administrator|IL                           |SLC_Operator                |PUT       |401 |401      |0     |SLC_Operator_2|SLCOP2     |SLC_Operator@test.com|               |      |           |
    |SEA_Super_Administrator|IL                           |SEA_Super_Administrator     |PUT       |200 |200      |1     |SEA_Admin     |SeaAdmin   |SeaAdmin@test.com    |               |IL    |IL         |
    |SEA_Super_Administrator|IL                           |LEA_Super_Administrator     |PUT       |200 |200      |1     |LEA_Admin     |LeaAdmin   |LeaAdmin@test.com    |Ingestion_Admin|IL    |IL-DAYBREAK|
    |SEA_Super_Administrator|IL                           |Realm_Administrator         |PUT       |200 |200      |1     |Realm_Admin   |RealmAdmin |RealmAdmin@test.com  |Realm_Admin    |IL    |IL-DAYBREAK|
    |SEA_Super_Administrator|IL                           |Ingestion_Administrator     |PUT       |200 |200      |1     |Ingest_Admin  |IngestAdmin|IngestAdmin@test.com |Ingestion_Admin|IL    |IL-DAYBREAK|
    |LEA_Super_Administrator|IL-SUNSET                    |SLC_Operator                |PUT       |401 |401      |0     |SLC_Operator_2|SLCOP2     |SLC_Operator@test.com|               |      |           |
    |LEA_Super_Administrator|IL-SUNSET                    |SEA_Super_Administrator     |PUT       |401 |401      |0     |SEA_Admin     |SeaAdmin   |SeaAdmin@test.com    |               |IL    |IL         |
    |LEA_Super_Administrator|IL-SUNSET                    |LEA_Super_Administrator     |PUT       |200 |200      |1     |LEA_Admin     |LeaAdmin   |LeaAdmin@test.com    |Ingestion_Admin|IL    |IL-DAYBREAK|
    |LEA_Super_Administrator|IL-SUNSET                    |Realm_Administrator         |PUT       |200 |200      |1     |Realm_Admin   |RealmAdmin |RealmAdmin@test.com  |Realm_Admin    |IL    |IL-DAYBREAK|
    |LEA_Super_Administrator|IL-SUNSET                    |Ingestion_Administrator     |PUT       |200 |200      |1     |Ingest_Admin  |IngestAdmin|IngestAdmin@test.com |Ingestion_Admin|IL    |IL-DAYBREAK|
    |Realm_Administrator    |IL-SUNSET                    |SLC_Operator                |PUT       |401 |401      |0     |SLC_Operator_2|SLCOP2     |SLC_Operator@test.com|               |      |           |
    |Realm_Administrator    |IL-SUNSET                    |SEA_Super_Administrator     |PUT       |401 |401      |0     |SEA_Admin     |SeaAdmin   |SeaAdmin@test.com    |               |IL    |IL         |
    |Realm_Administrator    |IL-SUNSET                    |LEA_Super_Administrator     |PUT       |401 |401      |0     |LEA_Admin     |LeaAdmin   |LeaAdmin@test.com    |Ingestion_Admin|IL    |IL-DAYBREAK|
    |Realm_Administrator    |IL-SUNSET                    |Realm_Administrator         |PUT       |401 |401      |0     |Realm_Admin   |RealmAdmin |RealmAdmin@test.com  |Realm_Admin    |IL    |IL-DAYBREAK|
    |Realm_Administrator    |IL-SUNSET                    |Ingestion_Administrator     |PUT       |401 |401      |0     |Ingest_Admin  |IngestAdmin|IngestAdmin@test.com |Ingestion_Admin|IL    |IL-DAYBREAK|
    |Ingestion_Administrator|IL-SUNSET                    |SLC_Operator                |PUT       |401 |401      |0     |SLC_Operator_2|SLCOP2     |SLC_Operator@test.com|               |      |           |
    |Ingestion_Administrator|IL-SUNSET                    |SEA_Super_Administrator     |PUT       |401 |401      |0     |SEA_Admin     |SeaAdmin   |SeaAdmin@test.com    |               |IL    |IL         |
    |Ingestion_Administrator|IL-SUNSET                    |LEA_Super_Administrator     |PUT       |401 |401      |0     |LEA_Admin     |LeaAdmin   |LeaAdmin@test.com    |Ingestion_Admin|IL    |IL-DAYBREAK|
    |Ingestion_Administrator|IL-SUNSET                    |Realm_Administrator         |PUT       |401 |401      |0     |Realm_Admin   |RealmAdmin |RealmAdmin@test.com  |Realm_Admin    |IL    |IL-DAYBREAK|
    |Ingestion_Administrator|IL-SUNSET                    |Ingestion_Administrator     |PUT       |401 |401      |0     |Ingest_Admin  |IngestAdmin|IngestAdmin@test.com |Ingestion_Admin|IL    |IL-DAYBREAK|

  @wip
  @sandbox
  Scenario Outline:  As a admin I am able to create/update admin accounts in my tenancy on sandbox
    Given I have a <ADMIN_ROLE> with <ALLOWED_RIGHTS> on <Tenant>
    When I am authenticated on <ADMIN_REALM>
    And I navigate to <ACTION> <CREATE_ADMIN_ROLE>
    And "Full_Name" is <Full_Name>
    And "Username" is <User_Name>
    And "Email" is <Email_Address>
    And "Role" is <CREATE_ADMIN_ROLE>
    And "Additional Role" is <Additional_Role>
    And "Tenant" is <Tenant>
    Then I should receive a return code of <CODE>
    When I navigate to GET /<WANTED_ADMIN_ROLE>
    Then I should receive a return code of <READ_CODE>
    And then I should receive a list <NUMBER> of <WANTED_ADMIN_ROLE>

  Examples:
    |ADMIN_ROLE             |ADMIN_REALM                  |WANTED_ROLE                 |ALLOWED_RIGHTS       |ACTION    |CODE|READ_CODE|NUMBER|Full_Name          |User_Name  |Email_Address         |Tenant                |
    |SLC_Operator           |Shared Learning Collaborative|SLC_Operator                |DEFAULT              |POST      |200 |200      |1     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |                      |
    |SLC_Operator           |Shared Learning Collaborative|Admin_App_Developer         |DEFAULT              |POST      |200 |200      |1     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|Admin_App_Dev@test.com|
    |SLC_Operator           |Shared Learning Collaborative|Application_Developer       |DEFAULT              |POST      |200 |200      |1     |App_Developer      |AppDev     |App_Dev@test.com      |Admin_App_Dev@test.com|
    |Application_Developer  |Shared Learning Collaborative|SLC_Operator                |SB_ACCOUNT_MANAGEMENT|POST      |401 |401      |0     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |                      |
    |Application_Developer  |Shared Learning Collaborative|Admin_App_Developer         |SB_ACCOUNT_MANAGEMENT|POST      |200 |200      |1     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|Admin_App_Dev@test.com|
    |Application_Developer  |Shared Learning Collaborative|Application_Developer       |SB_ACCOUNT_MANAGEMENT|POST      |200 |200      |1     |App_Developer      |AppDev     |App_Dev@test.com      |Admin_App_Dev@test.com|
    |Application_Developer  |Shared Learning Collaborative|SLC_Operator                |DEFAULT              |POST      |401 |401      |0     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |                      |
    |Application_Developer  |Shared Learning Collaborative|Admin_App_Developer         |DEFAULT              |POST      |401 |401      |0     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|Admin_App_Dev@test.com|
    |Application_Developer  |Shared Learning Collaborative|Application_Developer       |DEFAULT              |POST      |401 |401      |1     |App_Developer      |AppDev     |App_Dev@test.com      |Admin_App_Dev@test.com|
    |SLC_Operator           |Shared Learning Collaborative|SLC_Operator                |DEFAULT              |PUT       |401 |401      |1     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |                      |
    |SLC_Operator           |Shared Learning Collaborative|Admin_App_Developer         |DEFAULT              |PUT       |200 |200      |1     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|Admin_App_Dev@test.com|
    |SLC_Operator           |Shared Learning Collaborative|Application_Developer       |DEFAULT              |PUT       |200 |200      |1     |App_Developer      |AppDev     |App_Dev@test.com      |Admin_App_Dev@test.com|
    |Application_Developer  |Shared Learning Collaborative|SLC_Operator                |SB_ACCOUNT_MANAGEMENT|PUT       |401 |401      |0     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |                      |
    |Application_Developer  |Shared Learning Collaborative|Admin_App_Developer         |SB_ACCOUNT_MANAGEMENT|PUT       |200 |200      |1     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|Admin_App_Dev@test.com|
    |Application_Developer  |Shared Learning Collaborative|Application_Developer       |SB_ACCOUNT_MANAGEMENT|PUT       |200 |200      |1     |App_Developer      |AppDev     |App_Dev@test.com      |Admin_App_Dev@test.com|
    |Application_Developer  |Shared Learning Collaborative|SLC_Operator                |DEFAULT              |PUT       |401 |401      |0     |SLC_Operator_2     |SLCOP2     |SLC_Operator@test.com |                      |
    |Application_Developer  |Shared Learning Collaborative|Admin_App_Developer         |DEFAULT              |PUT       |401 |401      |0     |Admin_App_Developer|AdminAppDev|Admin_App_Dev@test.com|Admin_App_Dev@test.com|
    |Application_Developer  |Shared Learning Collaborative|Application_Developer       |DEFAULT              |PUT       |401 |401      |1     |App_Developer      |AppDev     |App_Dev@test.com      |Admin_App_Dev@test.com|

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

