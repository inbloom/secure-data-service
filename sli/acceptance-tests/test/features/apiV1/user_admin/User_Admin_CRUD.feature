@RALLY_3076 @RALLY_3070
Feature: As an admin I can create admin accounts for tenancies I administer

  Background: none
	
	@production
	Scenario Outline: SLC Operator, SEA Administrator, LEA Administrator, Sandbox SLC Operator, and Sandbox Administrator have access the Super Administrator Management Tool
	Given I have logged in to realm "<REALM>" using "<USER>" "<PASSWORD>"
	And I have a role "<ADMIN_ROLE>"
	Then I <SHOULD> see "<APP_NAME>" on my list of allowed apps
	Examples:
		|USER       	|PASSWORD       		|ADMIN_ROLE             |REALM      |SHOULD   	|APP_NAME |
		|operator   	|operator1234   		|SLC Operator           |SLI        | should  	|Manage Administrator Accounts|
		|iladmin    	|iladmin1234    		|SEA Administrator      |SLI        | should  	|Manage Administrator Accounts|
		|sunsetadmin	|sunsetadmin1234		|LEA Administrator      |SLI        | should  	|Manage Administrator Accounts|
		|sandboxoperator|sandboxoperator1234	|Sandbox SLC Operator	|SLI		| should not|Manage Administrator Accounts|
		|sandboxadministrator|sandboxadministrator1234|Sandbox Administrator|SLI	| should not|Manage Administrator Accounts|
		|ingestionuser  |ingestionuser1234		|Ingestion User			|SLI		| should not|Manage Administrator Accounts|
		|sunsetrealmadmin|sunsetrealmadmin1234	|Realm Administrator	|SLI		| should not|Manage Administrator Accounts|
		|sandboxdeveloper|sandboxdeveloper1234	|Application Developer	|SLI		| should not|Manage Administrator Accounts|
		|operator   	|operator1234   		|SLC Operator           |SLI        | should not|Manage Developer Accounts|
		|iladmin    	|iladmin1234    		|SEA Administrator      |SLI        | should not|Manage Developer Accounts|
		|sunsetadmin	|sunsetadmin1234		|LEA Administrator      |SLI        | should not|Manage Developer Accounts|
		|sandboxoperator|sandboxoperator1234	|Sandbox SLC Operator	|SLI		| should |Manage Developer Accounts|
		|sandboxadministrator|sandboxadministrator1234|Sandbox Administrator|SLI	| should	|Manage Developer Accounts|
		|ingestionuser  |ingestionuser1234		|Ingestion User			|SLI		| should not|Manage Developer Accounts|
		|sunsetrealmadmin|sunsetrealmadmin1234	|Realm Administrator	|SLI		| should not|Manage Developer Accounts|
		|sandboxdeveloper|sandboxdeveloper1234	|Application Developer	|SLI		| should not|Manage Developer Accounts|
		
	
  @production
  Scenario Outline:  As an admin, I can only see users I'm allowed to see
  Given I have logged in to realm "<REALM>" using "<USER>" "<PASSWORD>"
  And I have a role "<ADMIN_ROLE>"
  When I navigate to GET "/users"
  Then I <SHOULD> see "<USER_ID>"
  Examples:
	|USER		|PASSWORD		|ADMIN_ROLE			|REALM		|SHOULD			|USER_ID			|
	# |operator   |operator1234	|SLC Operator		|SLI		|should			|operator			|
	|operator   |operator1234	|SLC Operator		|SLI		|should			|iladmin			|
	|operator   |operator1234	|SLC Operator		|SLI		|should			|sunsetadmin		|
	|operator   |operator1234	|SLC Operator		|SLI		|should			|sunsetingestionuser|
	|operator   |operator1234	|SLC Operator		|SLI		|should			|sunsetrealmadmin	|
	|iladmin	|iladmin1234	|SEA Administrator	|SLI		|should not		|operator			|
	|iladmin	|iladmin1234	|SEA Administrator	|SLI		|should			|iladmin			|
	|iladmin	|iladmin1234	|SEA Administrator	|SLI		|should			|sunsetadmin		|
	|iladmin	|iladmin1234	|SEA Administrator	|SLI		|should			|sunsetingestionuser|
	|iladmin	|iladmin1234	|SEA Administrator	|SLI		|should			|sunsetrealmadmin	|
	|sunsetadmin|sunsetadmin1234|LEA Administrator	|SLI		|should not		|operator			|
	|sunsetadmin|sunsetadmin1234|LEA Administrator	|SLI		|should not		|iladmin			|
	|sunsetadmin|sunsetadmin1234|LEA Administrator	|SLI		|should			|sunsetadmin		|
	|sunsetadmin|sunsetadmin1234|LEA Administrator	|SLI		|should			|sunsetingestionuser|
	|sunsetadmin|sunsetadmin1234|LEA Administrator	|SLI		|should			|sunsetrealmadmin	|

  @production
  Scenario Outline:  As an administrator I can read all admin accounts in my tenancy if I am a SLC operator or a SEA.  If I am LEA, I can read myself and realm/ingestion users. 
    
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
    |operator          |operator1234        |SLC Operator           |SLI      |Ingestion User      |PUT      |204 |200      |1 or more|Ingestion User7 |Ingestion_User7 |Ingestion_User@test.com|    |Midgar| IL-SUNSET|
    |operator          |operator1234        |SLC Operator           |SLI      |SLC Operator        |POST      |201 |200      |1 or more|SLC Operator2 |SLC_Operator2     |SLC_Operator@test.com|               |      |           |
    |operator          |operator1234        |SLC Operator           |SLI      |SEA Administrator   |POST      |201 |200      |1 or more|SEA Administrator2 |SEA_Administrator2 |SEA_Administrator@test.com|    |Midgar| IL|
    |operator          |operator1234        |SLC Operator           |SLI      |LEA Administrator   |POST      |201 |200      |1 or more|LEA Administrator2 |LEA_Administrator2 |LEA_Administrator@test.com|    |Midgar| IL-SUNSET|
    |operator          |operator1234        |SLC Operator           |SLI      |Realm Administrator |POST      |201 |200      |1 or more|Realm Administrator2 |Realm_Administrator2 |Realm_Administrator@test.com|    |Midgar| IL-SUNSET|
    |operator          |operator1234        |SLC Operator           |SLI      |Ingestion User      |POST      |201 |200      |1 or more|Ingestion User2 |Ingestion_User2 |Ingestion_User@test.com|    |Midgar| IL-SUNSET|
	|operator          |operator1234        |SLC Operator           |SLI      |LEA Administrator   |POST      |201 |200      |1 or more|LEA Administrator19 	|LEA_Administrator19 	|LEA_Administrator@test.com| |Midgar|IL |
    |operator          |operator1234        |SLC Operator           |SLI      |LEA Administrator   |POST      |201 |200      |1 or more|LEA Administrator17  |LEA_Administrator17  	|LEA_Administrator@test.com|    	|Hyrule| IL-DAYBREAK |
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |SLC Operator        |POST      |403 |200      |0         |SLC Operator3 |SLC_Operator3     |SLC_Operator@test.com|               |      |           |
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |SEA Administrator   |POST      |201 |200      |1 or more|SEA Administrator3 |SEA_Administrator3 |SEA_Administrator@test.com|    |Midgar| IL|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |LEA Administrator   |POST      |201 |200      |1 or more|LEA Administrator3 |LEA_Administrator3 |LEA_Administrator@test.com|    |Midgar| IL-SUNSET|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |Realm Administrator |POST      |201 |200      |1 or more|Realm Administrator3 |Realm_Administrator3 |Realm_Administrator@test.com|    |Midgar| IL-SUNSET|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |Ingestion User      |POST      |201 |200      |1 or more|Ingestion User3 |Ingestion_User3 |Ingestion_User@test.com|    |Midgar| IL-SUNSET|
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |SLC Operator        |POST      |403 |200      |0        |SLC Operator4   |SLC_Operator4   |SLC_Operator@test.com|               |      |           | 
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |SEA Administrator   |POST      |403 |200      |0        |SEA Administrator4 |SEA_Administrator4 |SEA_Administrator@test.com|    |Midgar| IL|
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
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |SLC Operator        |PUT      |400 |200      |0         |SLC Operator8 |SLC_Operator8     |SLC_Operator@test.com|               |      |           |
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |SEA Administrator   |PUT      |204 |200      |1 or more|SEA Administrator8 |SEA_Administrator8 |SEA_Administrator@test.com|    |Midgar| IL|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |LEA Administrator   |PUT      |204 |200      |1 or more|LEA Administrator8 |LEA_Administrator8 |LEA_Administrator@test.com|    |Midgar| IL-SUNSET|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |Realm Administrator |PUT      |204 |200      |1 or more|Realm Administrator8 |Realm_Administrator8 |Realm_Administrator@test.com|    |Midgar| IL-SUNSET|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |Ingestion User      |PUT      |204 |200      |1 or more|Ingestion User8 |Ingestion_User8 |Ingestion_User@test.com|    |Midgar| IL-SUNSET|
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |SLC Operator        |PUT      |400 |200      |0        |SLC Operator9   |SLC_Operator9   |SLC_Operator@test.com|               |      |           | 
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |SEA Administrator   |PUT      |400 |200      |0        |SEA Administrator9 |SEA_Administrator9 |SEA_Administrator@test.com|    |Midgar| IL|
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
    |operator          |operator1234        |SLC Operator           |SLI      |SLC Operator        |POST     |201 |200      |1 or more|Homer J Simpson |homerSimpson      |homersimpson@test.com|               |      |           |
    |operator          |operator1234        |SLC Operator           |SLI      |SLC Operator        |POST     |201 |200      |1 or more|Homer           |homerThePoet      |homerpoet@test.com|               |      |           |
	|sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |LEA Administrator   |POST     |201 |200      |1 or more|LEA Administrator18  |LEA_Administrator18  |LEA_Administrator@test.com|Realm Administrator |Midgar| IL-SUNSET  |


@production 
Scenario Outline:  As a admin I am able to create/update admin accounts in my tenancy : Unhappy Path - tenant and ed-org validation
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
  And an account with "<Full_Name>", "<User_Name>", "<Email_Address>" should not exist

  Examples:
    |USER              |PASSWORD            |ADMIN_ROLE             |REALM    |CREATE_ADMIN_ROLE   |ACTION    |CODE|READ_CODE |Full_Name     |User_Name  |Email_Address        |Additional_Role |Tenant|Ed_Org     |
# operator with tenant, ed-org
	|operator          |operator1234        |SLC Operator           |SLI      |SLC Operator        |POST      |400 |200       |SLC Operator13 		|SLC_Operator13     	|SLC_Operator@test.com|    	|Midgar| |
	|operator          |operator1234        |SLC Operator           |SLI      |SLC Operator        |POST      |400 |200       |SLC Operator14 		|SLC_Operator14     	|SLC_Operator@test.com|     | |	    IL-SUNSET   |
# sea, lea with no tenant
	|operator          |operator1234        |SLC Operator           |SLI      |SEA Administrator   |POST      |400 |200       |SEA Administrator12 	|SEA_Administrator12 	|SEA_Administrator@test.com||Midgar| |
	|operator          |operator1234        |SLC Operator           |SLI      |LEA Administrator   |POST      |400 |200       |LEA Administrator13 	|LEA_Administrator13 	|LEA_Administrator@test.com| | | IL-SUNSET |
# lea with no ed-org
	|operator          |operator1234        |SLC Operator           |SLI      |LEA Administrator   |POST      |400 |200       |LEA Administrator14 	|LEA_Administrator14 	|LEA_Administrator@test.com| |Midgar| |
# lea in state level ed-org
	|iladmin           |iladmin1234         |SEA Administrator      |SLI      |LEA Administrator   |POST      |400 |200       |LEA Administrator20 	|LEA_Administrator20 	|LEA_Administrator@test.com| |Midgar|IL |
# sea creates a sea, lea outside their tenant
	|iladmin           |iladmin1234         |SEA Administrator      |SLI      |SEA Administrator   |POST      |400 |200       |SEA Administrator14	|SEA_Administrator14 	|SEA_Administrator@test.com| |Hyrule|  |
	|iladmin           |iladmin1234         |SEA Administrator      |SLI      |LEA Administrator   |POST      |400 |200       |LEA Administrator16	|LEA_Administrator16 	|LEA_Administrator@test.com| |Hyrule|  |
# lea creates a lea in a different part of the ed-org hierarchy
	|sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI      |LEA Administrator   |POST      |400 |200       |LEA Administrator15  |LEA_Administrator15  |LEA_Administrator@test.com|Realm Administrator |Midgar| IL-DAYBREAK|
# realm admin, ingestion_user must have tenant & ed-org for user with production roles
	|iladmin           |iladmin1234         |SEA Administrator      |SLI      |Realm Administrator |POST      |400 |200       |Realm Administrator3 |Realm_Administrator3 |Realm_Administrator@test.com|    |Midgar| |
	|iladmin           |iladmin1234         |SEA Administrator      |SLI      |Realm Administrator |POST      |400 |200       |Realm Administrator3 |Realm_Administrator3 |Realm_Administrator@test.com|    | | IL-SUNSET|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |Ingestion User      |POST      |400 |200       |Ingestion User3      |Ingestion_User3      |Ingestion_User@test.com|        |Midgar| |
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |Ingestion User      |POST      |400 |200       |Ingestion User3      |Ingestion_User3      |Ingestion_User@test.com|        | | IL-SUNSET|
# app_dev, ingestion_user must have tenant for user with sandbox roles
	|sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI      |Application Developer|POST     |400 |200       |Application Developer4 |Application_Developer4|applicationdeveloper@slidev.org|            | |   |
	|sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI      |Ingestion User       |POST     |400 |200       |Ingestion User4 |Ingestion_User4|ingestionuser@slidev.org|            | |   |
    |operator          |operator1234        |SLC Operator           |SLI      |SLC Operator        |POST      |400 |200      |              |No_Name    |noname@test.com      |               |      |           |
    |operator          |operator1234        |SLC Operator           |SLI      |SLC Operator        |POST      |400 |200      |No User_Name  |           |nousername@test.com  |               |      |           |
    |operator          |operator1234        |SLC Operator           |SLI      |SLC Operator        |POST      |400 |200      |No Mail       |No_Mail    |                     |               |      |           |
# can not create ingestion_user or realm admin when there is no LEA in the edorg
	|iladmin           |iladmin1234         |SEA Administrator      |SLI      |Realm Administrator |POST      |400 |200       |Realm Administrator4 |Realm_Administrator4 |Realm_Administrator@test.com|    |Midgar | IL-NIGHTFALL|
    |iladmin           |iladmin1234         |SEA Administrator      |SLI      |Ingestion User      |POST      |400 |200       |Ingestion User5      |Ingestion_User5      |Ingestion_User@test.com|        |Midgar| IL-NIGHTFALL|

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
  |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI      |Sandbox Administrator|POST      |201 |200     |1 or more|Sandbox Administrator2 |Sandbox_Administrator2|sandboxadministrator@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI      |Application Developer|POST      |201 |200     |1 or more|Application Developer2 |Application_Developer2|applicationdeveloper@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI      |Ingestion User       |POST      |201 |200     |1 or more|Ingestion User2 |Ingestion_User2|ingestionuser@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator|SLI|Sandbox SLC Operator |POST      |403 |200     |0        |Sandbox SLCOperator3 |Sandbox_SLC_Operator3|sandbox_SLC_Operator@test.com|                |      |           | 
  |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator|SLI|Sandbox Administrator|POST      |201 |200     |1 or more|Sandbox Administrator3 |Sandbox_Administrator3|sandboxadministrator@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator|SLI|Application Developer|POST      |201 |200     |1 or more|Application Developer3 |Application_Developer3|applicationdeveloper@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator|SLI|Ingestion User       |POST      |201 |200     |1 or more|Ingestion User3 |Ingestion_User3|ingestionuser@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxdeveloper |sandboxdeveloper1234 |Application Developer  |SLI      |Sandbox SLC Operator |POST      |403 |403     |0        |Sandbox SLCOperator4 |Sandbox_SLC_Operator4|sandbox_SLC_Operator@test.com|                |      |           |
  |sandboxdeveloper |sandboxdeveloper1234 |Application Developer  |SLI      |Sandbox Administrator|POST      |403 |403     |0         |Sandbox Administrator4 |Sandbox_Administrator4|sandboxadministrator@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxdeveloper |sandboxdeveloper1234 |Application Developer  |SLI      |Application Developer|POST      |403 |403     |0         |Application Developer4 |Application_Developer4|applicationdeveloper@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxdeveloper |sandboxdeveloper1234 |Application Developer  |SLI      |Ingestion User       |POST      |403 |403     |0         |Ingestion User4 |Ingestion_User4|ingestionuser@slidev.org|            |sandboxadministrator@slidev.org|   |      
  |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI      |Sandbox SLC Operator|PUT      |204 |200      |1 or more|Sandbox SLCOperator2 |Sandbox_SLC_Operator2|sandbox_SLC_Operator@test.com|                |      |           |
  |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI      |Sandbox Administrator|PUT      |204 |200     |1 or more|Sandbox Administrator2 |Sandbox_Administrator2|sandboxadministrator@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI      |Application Developer|PUT      |204 |200     |1 or more|Application Developer2 |Application_Developer2|applicationdeveloper@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxoperator   |sandboxoperator1234 |Sandbox SLC Operator   |SLI      |Ingestion User       |PUT      |204 |200     |1 or more|Ingestion User2 |Ingestion_User2|ingestionuser@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator|SLI|Sandbox SLC Operator |PUT      |400 |200     |0        |Sandbox SLCOperator3 |Sandbox_SLC_Operator3|sandbox_SLC_Operator@test.com|                |      |           | 
  |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator|SLI|Sandbox Administrator|PUT      |204 |200     |1 or more|Sandbox Administrator3 |Sandbox_Administrator3|sandboxadministrator@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator|SLI|Application Developer|PUT      |204 |200     |1 or more|Application Developer3 |Application_Developer3|applicationdeveloper@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator|SLI|Ingestion User       |PUT      |204 |200     |1 or more|Ingestion User3 |Ingestion_User3|ingestionuser@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxdeveloper |sandboxdeveloper1234 |Application Developer  |SLI      |Sandbox SLC Operator |PUT      |403 |403     |0        |Sandbox SLCOperator4 |Sandbox_SLC_Operator4|sandbox_SLC_Operator@test.com|                |      |           |
  |sandboxdeveloper |sandboxdeveloper1234 |Application Developer  |SLI      |Sandbox Administrator|PUT      |403 |403     |0         |Sandbox Administrator4 |Sandbox_Administrator4|sandboxadministrator@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxdeveloper |sandboxdeveloper1234 |Application Developer  |SLI      |Application Developer|PUT      |403 |403     |0         |Application Developer4 |Application_Developer4|applicationdeveloper@slidev.org|            |sandboxadministrator@slidev.org|   |
  |sandboxdeveloper |sandboxdeveloper1234 |Application Developer  |SLI      |Ingestion User       |PUT      |403 |403     |0         |Ingestion User4 |Ingestion_User4|ingestionuser@slidev.org|            |sandboxadministrator@slidev.org|   |      

  @production 
  Scenario Outline:  As a admin I am able to delete admin accounts in my tenancy
    Given I have logged in to realm "<ADMIN_REALM>" using "<USER>" "<PASSWORD>"
    And I have a role "<ADMIN_ROLE>"
    And I have a tenant "<TENANT>" and edorg "<ED_ORG>"
    When I navigate to DELETE  "<WANTED_ADMIN_ROLE>" in environment "production"
    Then I should receive a return code "<CODE>"

  Examples:
    |USER              |PASSWORD            |ADMIN_ROLE             |ADMIN_REALM                  |WANTED_ADMIN_ROLE           |CODE|TENANT|ED_ORG     |
    |operator          |operator1234        |SLC Operator           |SLI                          |SLC Operator                |204 |      |           |
    |operator          |operator1234        |SLC Operator           |SLI                          |SEA Administrator           |204 |test  |test       |
    |operator          |operator1234        |SLC Operator           |SLI                          |LEA Administrator           |204 |Midgar|IL-SUNSET  |
    |operator          |operator1234        |SLC Operator           |SLI                          |Realm Administrator         |204 |Midgar|IL-SUNSET  |
    |operator          |operator1234        |SLC Operator           |SLI                          |Ingestion User              |204 |Midgar|IL-SUNSET  |
    |iladmin           |iladmin1234         |SEA Administrator      |SLI                          |SLC Operator                |403 |      |           |
    |iladmin           |iladmin1234         |SEA Administrator      |SLI                          |SEA Administrator           |204 |Midgar|IL-SUNSET  |
    |iladmin           |iladmin1234         |SEA Administrator      |SLI                          |LEA Administrator           |204 |Midgar|IL-SUNSET  |
    |iladmin           |iladmin1234         |SEA Administrator      |SLI                          |Realm Administrator         |204 |Midgar|IL-SUNSET  |
    |iladmin           |iladmin1234         |SEA Administrator      |SLI                          |Ingestion User              |204 |Midgar|IL-SUNSET  |
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI                          |SLC Operator                |403 |      |           |
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI                          |SEA Administrator           |403 |Midgar|IL-SUNSET  |
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI                          |LEA Administrator           |204 |Midgar|IL-SUNSET  |
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI                          |Realm Administrator         |204 |Midgar|IL-SUNSET  |
    |sunsetadmin       |sunsetadmin1234     |LEA Administrator      |SLI                          |Ingestion User              |204 |Midgar|IL-SUNSET  |
    |sunsetrealmadmin  |sunsetrealmadmin1234|Realm Administrator    |SLI                          |SLC Operator                |403 |      |           |
    |sunsetrealmadmin  |sunsetrealmadmin1234|Realm Administrator    |SLI                          |SEA Administrator           |403 |Midgar|IL-SUNSET  |
    |sunsetrealmadmin  |sunsetrealmadmin1234|Realm Administrator    |SLI                          |LEA Administrator           |403 |Midgar|IL-SUNSET  |
    |sunsetrealmadmin  |sunsetrealmadmin1234|Realm Administrator    |SLI                          |Realm Administrator         |403 |Midgar|IL-SUNSET  |
    |sunsetrealmadmin  |sunsetrealmadmin1234|Realm Administrator    |SLI                          |Ingestion User              |403 |Midgar|IL-SUNSET  |
    |ingestionuser     |ingestionuser1234   |Ingestion User         |SLI                          |SLC Operator                |403 |      |           |
    |ingestionuser     |ingestionuser1234   |Ingestion User         |SLI                          |SEA Administrator           |403 |Midgar|IL-SUNSET  |
    |ingestionuser     |ingestionuser1234   |Ingestion User         |SLI                          |LEA Administrator           |403 |Midgar|IL-SUNSET  |
    |ingestionuser     |ingestionuser1234   |Ingestion User         |SLI                          |Realm Administrator         |403 |Midgar|IL-SUNSET  |
    |ingestionuser     |ingestionuser1234   |Ingestion User         |SLI                          |Ingestion User              |403 |Midgar|IL-SUNSET  |


  #sandbox
  Scenario Outline:  As a admin on sandbox I am able to delete admin accounts in my tenancy
    Given I have logged in to realm "<ADMIN_REALM>" using "<USER>" "<PASSWORD>"
    And I have a role "<ADMIN_ROLE>"
    And I have a tenant "<TENANT>" and edorg "<ED_ORG>"
    When I navigate to DELETE  "<WANTED_ADMIN_ROLE>" in environment "sandbox"
    Then I should receive a return code "<CODE>"

  Examples:
    |USER                 |PASSWORD                 |ADMIN_ROLE             |ADMIN_REALM                  |WANTED_ADMIN_ROLE           |CODE|TENANT|ED_ORG     |
    |sandboxoperator      |sandboxoperator1234      |Sandbox SLC Operator   |SLI                          |Sandbox SLC Operator        |204 |      |           |
    |sandboxoperator      |sandboxoperator1234      |Sandbox SLC Operator   |SLI                          |Sandbox Administrator       |204 |Midgar|IL-SUNSET  |
    |sandboxoperator      |sandboxoperator1234      |Sandbox SLC Operator   |SLI                          |Ingestion User              |204 |Midgar|IL-SUNSET  |
    |sandboxoperator      |sandboxoperator1234      |Sandbox SLC Operator   |SLI                          |Application Developer       |204 |Midgar|IL-SUNSET  |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator  |SLI                          |Sandbox SLC Operator        |403 |      |           |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator  |SLI                          |Sandbox Administrator       |204 |sandboxadministrator@slidev.org|IL-SUNSET  |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator  |SLI                          |Ingestion User              |204 |sandboxadministrator@slidev.org|IL-SUNSET  |
    |sandboxadministrator |sandboxadministrator1234 |Sandbox Administrator  |SLI                          |Application Developer       |204 |sandboxadministrator@slidev.org|IL-SUNSET  |
    |sandboxdeveloper     |sandboxdeveloper1234     |Application Developer  |SLI                          |Sandbox SLC Operator        |403 |      |           |
    |sandboxdeveloper     |sandboxdeveloper1234     |Application Developer  |SLI                          |Sandbox Administrator       |403 |Midgar|IL-SUNSET  |
    |sandboxdeveloper     |sandboxdeveloper1234     |Application Developer  |SLI                          |Application Developer       |403 |Midgar|IL-SUNSET  |
    |sandboxdeveloper     |sandboxdeveloper1234     |Application Developer  |SLI                          |Ingestion User              |403 |Midgar|IL-SUNSET  |
    |sunsetrealmadmin     |sunsetrealmadmin1234     |Realm Administrator    |SLI                          |Sandbox SLC Operator        |403 |      |           |
    |sunsetrealmadmin     |sunsetrealmadmin1234     |Realm Administrator    |SLI                          |Sandbox Administrator       |403 |Midgar|IL-SUNSET  |
    |sunsetrealmadmin     |sunsetrealmadmin1234     |Realm Administrator    |SLI                          |Application Developer       |403 |Midgar|IL-SUNSET  |
    |sunsetrealmadmin     |sunsetrealmadmin1234     |Realm Administrator    |SLI                          |Ingestion User              |403 |Midgar|IL-SUNSET  |
    |ingestionuser        |ingestionuser1234        |Ingestion User         |SLI                          |Sandbox SLC Operator        |403 |      |           |
    |ingestionuser        |ingestionuser1234        |Ingestion User         |SLI                          |Sandbox Administrator       |403 |Midgar|IL-SUNSET  |
    |ingestionuser        |ingestionuser1234        |Ingestion User         |SLI                          |Application Developer       |403 |Midgar|IL-SUNSET  |
    |ingestionuser        |ingestionuser1234        |Ingestion User         |SLI                          |Ingestion User              |403 |Midgar|IL-SUNSET  |


  @production
  Scenario: Unhappy path:  LEA cannot see SEA who has same edorg
  Given I have logged in to realm "SLI" using "iladmin" "iladmin1234"
  And I create a new "SEA Administrator, Ingestion User" "il2admin" with tenant "Midgar" and edorg "IL-SUNSET"
  When I have logged in to realm "SLI" using "operator" "operator1234"
  Then I should see user "il2admin"
  When I have logged in to realm "SLI" using "iladmin" "iladmin1234"
  Then I should see user "il2admin"
  When I have logged in to realm "SLI" using "sunsetadmin" "sunsetadmin1234"
  Then I should not see user "il2admin"
  Then I have logged in to realm "SLI" using "operator" "operator1234"
  And I delete the test user "il2admin"

  @production
  Scenario: Unhappy path:  LEA cannot update SEA with an incomplete group list
  Given I have logged in to realm "SLI" using "iladmin" "iladmin1234"
  And I create a new "SEA Administrator, Ingestion User" "il2admin" with tenant "Midgar" and edorg "IL-SUNSET"
  When I have logged in to realm "SLI" using "sunsetadmin" "sunsetadmin1234"
  And I try to update this new user as "Realm Administrator"
  Then I should receive a return code "403"
  Then I have logged in to realm "SLI" using "operator" "operator1234"
  And I delete the test user "il2admin"

  Scenario Outline:  I can not change home directory for users I have permission with
    Given I have logged in to realm "SLI" using "<USER>" "<PASS>"
    And I create a new "Ingestion User" "il2admin" with tenant "<TENANT>" and edorg "<EDORG>"
    And I verify this new user has home directory "/dev/null"
    Then I try to change his home directory to "/"
    And It will not change
 

  Examples:
    |USER                  |PASS                     |TENANT                          |EDORG         |
    |operator              |operator1234             |Midgar                          |IL-SUNSET     |
    |sunsetadmin           |sunsetadmin1234          |Midgar                          |IL-SUNSET     |
    |iladmin               |iladmin1234              |Midgar                          |IL-SUNSET     |
    |sandboxadministrator  |sandboxadministrator1234 |sandboxadministrator@slidev.org |STANDARD-SEA  |

