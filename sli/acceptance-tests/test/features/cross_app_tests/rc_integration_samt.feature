@RALLY_US4835
@rc
Feature:  RC Integration SAMT Tests

  Background:
    Given I have an open web browser

  Scenario: SLC Operator logs into SAMT and creates SEA Administrator for tenant "RCTestTenant". SEA Administrator then completes the user creation flow.
    When I navigate to the Portal home page
    When I see the realm selector I authenticate to "Shared Learning Collaborative"
    #When I selected the realm "Shared Learning Collaborative"
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
    Then I should be on Portal home page
    Then I should see Admin link
    And I click on Admin
    Then I should be on the admin page
    And under System Tools, I click on "Administrative Account Management"

    And I switch to the iframe
    Then I delete the user "RCTestSeaAdminFN RCTestSeaAdminLN" if exists
    And I switch to the iframe
    Then I click on the "Add User" button
    And I switch to the iframe
    And I am redirected to the "Add a User" page
    And I can directly update the "Full Name" field to "RCTestSeaAdminFN RCTestSeaAdminLN"
    And I can directly update the "Email" field to "<PRIMARY_EMAIL>"

    And I can select "SEA Administrator" from a choice of "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
    And I can also check "Ingestion User" Role
    And I can update the "Tenant" field to "<TENANT>"
    And I can update the "EdOrg" field to "STANDARD-SEA"

    When I click button "Save"
    Then I am redirected to the "Admin Account Management" page
    And the "Success" message is displayed
    And the newly created user has "Tenant" updated to "<TENANT>"
    And the newly created user has "EdOrg" updated to "STANDARD-SEA"
    
    And I switch to the iframe
    Then I set my password to "<PRIMARY_EMAIL_PASS>"

# This is used for local testing to bypass portal.
#@local
#  Scenario: SLC Operator logs into SAMT and creates SEA Administrator for tenant "<TENANT>". SEA Administrator then completes the user creation flow.
#  When I navigate to the user account management page
#  Then I am redirected to "Simple" login page
#  When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
#
#  Then I delete the user "RCTestSeaAdminFN RCTestSeaAdminLN" if exists
#
#  Then I click on the "Add User" button
#  And I am redirected to the "Add a User" page
#  And I can directly update the "Full Name" field to "RCTestSeaAdminFN RCTestSeaAdminLN"
#  And I can directly update the "Email" field to "<PRIMARY_EMAIL>"
#
#  And I can select "SEA Administrator" from a choice of "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
#  And I can also check "Ingestion User" Role
#  And I can update the "Tenant" field to "<TENANT>"
#  And I can update the "EdOrg" field to "RCTestEdOrg"
#
#  When I click button "Save"
#  Then I am redirected to the "Admin Account Management" page
#  And the "Success" message is displayed
#  And the newly created user has "Tenant" updated to "<TENANT>"
#  And the newly created user has "EdOrg" updated to "RCTestEdOrg"
#
#  Then I set my password to "test1234"