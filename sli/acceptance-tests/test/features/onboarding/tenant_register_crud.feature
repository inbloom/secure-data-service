Feature: Tenant Registration Entity
As an administrator for SLI, I want to create a tenant entity so that it listen for files on landing zone

Scenario: CRUD operations on Tenants

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    When I navigate to GET "/tenants?tenantId=<New Tenant ID>"
    Then I should receive a return code of 200
    And I should receive a UUID
    And I should receive the data for the specified tenant entry
    When I navigate to PUT "/tenants/<New Tenant UUID>"
    Then I should receive a return code of 204
    When I navigate to DELETE "/tenants/<New Tenant UUID>"
    Then I should receive a return code of 204
    And I should no longer be able to get that tenant's data

Scenario: Deny creation when specifying invalid fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    When I navigate to GET "/tenants?tenantId=<New Tenant ID>"
    Then I should receive a return code of 200
    And I should receive a UUID
    And I PUT a tenant specifying an invalid field
    Then I should receive a return code of 400

Scenario Outline: Deny access when logging in as invalid user

    Given I am logged in using "baduser" "baduser1234" to realm "SLI"
    Given I am logged in using <User> <Password> to realm <Realm>
    When I navigate to GET "/tenants/<Testing Tenant>"
    Then I should receive a return code of 403
    Examples:
    | User       | Password       | Realm |
    | "baduser"  | "baduser1234"  | "SLI" |
    | "badadmin" | "badadmin1234" | "IL"  |

Scenario: Deny creation when specifying wrong size fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I POST a provision request with "tenantId" set to "123456789012345678901234567890123456789012345678A"
    Then I should receive a return code of 400
    When I POST a provision request with "tenantId" set to ""
    Then I should receive a return code of 400
    When I POST a provision request with "stateOrganizationId" set to "123456789012345678901234567890123456789012345678901234567890A"
    Then I should receive a return code of 400
    When I POST a provision request with "stateOrganizationId" set to ""
    Then I should receive a return code of 400


@wip
Scenario: Deny creation when missing userName
    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I POST a basic tenant with no userName
    Then I should receive a return code of 400

Scenario Outline: Deny creation when missing individual fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I POST a provision request with missing <Property>
    Then I should receive a return code of 400
    Examples:
    | Property      |
    | "tenantId"    |
    | "stateOrganizationId" |






Scenario Outline: Deny update when missing individual fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    When I navigate to GET "/tenants?tenantId=<New Tenant ID>"
    Then I should receive a return code of 200
    And I should receive a UUID
    When I PUT a basic tenant with missing <Property>
    Then I should receive a return code of 400
    Examples:
    | Property      |
    | "tenantId"    |
    | "landingZone" |

Scenario Outline: Deny creation when missing individual landingZone fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    When I navigate to GET "/tenants?tenantId=<New Tenant ID>"
    Then I should receive a return code of 200
    And I should receive a UUID
    When I PUT a basic tenant with missing landingZone <Property>
    Then I should receive a return code of 400
    Examples:
    | Property                |
    | "ingestionServer"       |
    | "educationOrganization" |
    | "path"                  |
    | "ingestionServer"       |

Scenario Outline: Deny creation when specifying individual wrong size fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    When I navigate to GET "/tenants?tenantId=<New Tenant ID>"
    Then I should receive a return code of 200
    And I should receive a UUID
    When I PUT a basic tenant with <Property> set to <Value>
    Then I should receive a return code of 400
    Examples:
    | Property | Value |
    | "tenantId" | "123456789012345678901234567890123456789012345678A" |
    | "tenantId" | "" |

Scenario Outline: Deny creation when specifying individual wrong size fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    When I navigate to GET "/tenants?tenantId=<New Tenant ID>"
    Then I should receive a return code of 200
    And I should receive a UUID
    When I PUT a basic tenant with userName <Value>
    Then I should receive a return code of 400
    Examples:
    | Value |
    | "123456789012345678901234567890123456789012345678A" |
    | "" |
    
Scenario Outline: Deny creation when specifying individual landingZone wrong size fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    When I navigate to GET "/tenants?tenantId=<New Tenant ID>"
    Then I should receive a return code of 200
    And I should receive a UUID
    When I PUT a basic tenant with landingZone <Property> set to <Value>
    Then I should receive a return code of 400
    Examples:
    | Property                | Value |
    | "ingestionServer"       | "123456789012345678901234567890123456789012345678A" |
    | "ingestionServer"       | "" |
    | "educationOrganization" | "123456789012345678901234567890123456789012345678901234567890A" |
    | "educationOrganization" | "" |
    | "path"                  | "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456A" |
    | "path"                  | "" |
    | "desc"                  | "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456A" |


    
    
