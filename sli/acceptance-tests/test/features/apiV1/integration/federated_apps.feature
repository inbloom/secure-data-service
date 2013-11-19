Feature: As an SLI application, I want to be able to perform CRUD operations on application resource

  Background: No extra setup required
    Given format "application/vnd.slc+json"

#Why does this have to be an integration test? Because changes to StaffEducationOrganizationAssociation are reflected only after re-login and re-login requires SimpleIDP.
  #jstevenson can access applications for his schools
  Scenario: CRUD operations involving developers, slcoperators, and federated users with APP_AUTHORIZE rights
    Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And format "application/vnd.slc+json"

    And I create a LEA named "FLEA1"
    And I create a School named "FSchool1" with parents "FLEA1"
    And I create a School named "FSchool2" with parents "FLEA1"
    And I login as developer "developer" and create application named "US5865 Application 1"
    And I login as slcoperator and approve application named "US5865 Application 1"

    #jstevenson cannot access application because (1.) Developer has not enabled app for FSchool1 (2.) jstevenson does not have context with FSchool1
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to get app "US5865 Application 1" and get a response code "403"

    #jstevenson cannot access application because (2.) jstevenson does not have context with FSchool1
    And I login as developer "developer" and enable application named "US5865 Application 1" for "FSchool1"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to get app "US5865 Application 1" and get a response code "403"

    #jstevenson CAN access application because (1.) Developer has enabled app for FSchool1 (2.) jstevenson has IT Administrator/APP_AUTHORIZE context with FSchool1
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I add a "IT Administrator" StaffEducationOrganizationAssociation  between "jstevenson" and "FSchool1"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "Leader" "jstevenson" with password "jstevenson1234"
    And I try to get app "US5865 Application 1" and get a response code "200"

    #developer disables application for FSchool1 and jstevenson loses access to application
    And I login as developer "developer" and disable application named "US5865 Application 1" for "FSchool1"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to get app "US5865 Application 1" and get a response code "403"

  #developer enables application for FSchool1 and jstevenson gains access to application
    And I login as developer "developer" and enable application named "US5865 Application 1" for "FSchool1"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to get app "US5865 Application 1" and get a response code "200"

    #IT Adminstrator modifies jstevensons  StaffEducationOrganizationAssociation with FSchool1 and jstevenson loses access
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I remove a "IT Administrator" StaffEducationOrganizationAssociation  between "jstevenson" and "FSchool1"

    #jstevenson cannot access application even if he has Educator StaffEducationOrganizationAssociation with FSchool2 and application is enabled for FSchool2
    And I login as developer "developer" and disable application named "US5865 Application 1" for "FSchool1"
    And I login as developer "developer" and enable application named "US5865 Application 1" for "FSchool2"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to get app "US5865 Application 1" and get a response code "403"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I add a "Educator" StaffEducationOrganizationAssociation  between "jstevenson" and "FSchool2"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to get app "US5865 Application 1" and get a response code "403"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I add a "IT Administrator" StaffEducationOrganizationAssociation  between "jstevenson" and "FSchool2"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to get app "US5865 Application 1" and get a response code "200"


  #jstevenson can access applications for all schools that are under his LEA
  Scenario: CRUD operations involving developers, slcoperators, and federated users with APP_AUTHORIZE rights
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I create a LEA named "ZFLEA1"
    And I create a School named "ZFSchool1" with parents "ZFLEA1"
    And I create a School named "ZFSchool2" with parents "ZFLEA1"
    And I login as developer "developer" and create application named "US5865 Application Z1"
    And I login as developer "developer" and create application named "US5865 Application Z2"
    And I login as slcoperator and approve application named "US5865 Application Z1"
    And I login as slcoperator and approve application named "US5865 Application Z2"

    #app is enabled for schools and admin has APP_AUTHORIZE for lea
    And I login as developer "developer" and enable application named "US5865 Application Z1" for "ZFSchool1"
    And I login as developer "developer" and enable application named "US5865 Application Z2" for "ZFSchool2"

    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to get app "US5865 Application Z1" and get a response code "403"
    And I try to get app "US5865 Application Z2" and get a response code "403"

    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I add a "IT Administrator" StaffEducationOrganizationAssociation  between "jstevenson" and "ZFLEA1"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to get app "US5865 Application Z1" and get a response code "200"
    And I try to get app "US5865 Application Z2" and get a response code "200"

    #app is enabled for lea and admin has APP_AUTHORIZE for lea
    And I login as developer "developer" and disable application named "US5865 Application Z1" for "ZFSchool1"
    And I login as developer "developer" and disable application named "US5865 Application Z2" for "ZFSchool2"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to get app "US5865 Application Z1" and get a response code "403"
    And I try to get app "US5865 Application Z2" and get a response code "403"

    And I login as developer "developer" and enable application named "US5865 Application Z1" for "ZFLEA1"
    And I login as developer "developer" and enable application named "US5865 Application Z2" for "ZFLEA1"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to get app "US5865 Application Z1" and get a response code "200"
    And I try to get app "US5865 Application Z2" and get a response code "200"


  #jstevenson can access applicationAuthorizations for his schools and leas
  Scenario: CRUD operations involving developers, slcoperators, and federated users with APP_AUTHORIZE rights
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I create a LEA named "L1"
    And I create a School named "S1" with parents "L1"
    And I create a School named "S2" with parents "L1"
    And I login as developer "developer" and create application named "A1"
    And I login as slcoperator and approve application named "A1"
    And I login as developer "developer" and enable application named "A1" for "S1"
    And I login as developer "developer" and enable application named "A1" for "S2"

    #jstevenson tries to authorize app for a school that he is an IT administrator and succeeds
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I add a "IT Administrator" StaffEducationOrganizationAssociation  between "jstevenson" and "S1"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to read the authorization for "A1" and get a response code of "200"
    And I try to authorize "A1" to access "S1" and get a response code of "204"
    And I try to deAuthorize "A1" to access "S1" and get a response code of "204"

    #jstevenson tries to authorize app for a school that he is NOT an IT administrator and fails.
    # S2 is filtered out and not authorized
    And I try to authorize "A1" to access "S2" and get a response code of "204"

#jstevenson can access applications that are authorized for all edOrgs
  Scenario: CRUD operations involving developers, slcoperators, and federated users with APP_AUTHORIZE rights
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I create a LEA named "V1"
    And I create a School named "H1" with parents "V1"
    And I create a School named "H2" with parents "V1"
    And I login as developer "developer" and create application named "D1"
    And I login as slcoperator and approve application named "D1"

    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to get app "D1" and get a response code "403"
    #And I try to read the authorization for "D1" and get a response code of "403"

    And I login as developer "developer" and enable application named "D1" for all edOrgs
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to read the authorization for "D1" and get a response code of "200"
    And I try to authorize "D1" to access "H1" and get a response code of "204"
    #we look at the AA collection and see that H1 IS NOT in the list

    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I add a "IT Administrator" StaffEducationOrganizationAssociation  between "jstevenson" and "H1"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to read the authorization for "D1" and get a response code of "200"
    And I try to authorize "D1" to access "H1" and get a response code of "204"
    #we look at the AA collection and see that H1 IS in the list

    #jstevenson has multiple contexts
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I add a "Educator" StaffEducationOrganizationAssociation  between "jstevenson" and "H1"
    And I add a "Teacher" StaffEducationOrganizationAssociation  between "jstevenson" and "H1"
    And I add a "Teacher" StaffEducationOrganizationAssociation  between "jstevenson" and "V1"
    And I add a "Educator" StaffEducationOrganizationAssociation  between "jstevenson" and "V1"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And I try to read the authorization for "D1" and get a response code of "200"
    And I try to authorize "D1" to access "H1" and get a response code of "204"

    And I login as developer "developer" and disable application named "D1" for all edOrgs
    And I try to get app "D1" and get a response code "403"

    And I login as developer "developer" and enable application named "D1" for all edOrgs
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I remove a "IT Administrator" StaffEducationOrganizationAssociation  between "jstevenson" and "H1"
    And I login as developer "developer" and disable application named "D1" for all edOrgs
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I try to get app "D1" and get a response code "403"
    And I try to read the authorization for "D1" and get a response code of "403"