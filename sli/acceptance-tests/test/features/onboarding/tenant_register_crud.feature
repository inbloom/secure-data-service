Feature: Tenant Registration Entity
As an administrator for SLI, I want to create a tenant entity so that it listen for files on landing zone

Scenario: CRUD operations on Tenants

    Given I am logged in using "developer" "developer1234" to realm "SLI"
    When I POST a new tenant
    Then I should receive a return code of 201
    And I should receive an ID for the newly created tenant
    When I rePOST the new tenant
    Then I should receive a return code of 201
    And I should receive the same tenant id
    When I navigate to GET "/tenants/<New Tenant ID>"
    Then I should receive a return code of 200
    And I should receive the data for the specified tenant entry
    When I navigate to PUT "/tenants/<New Tenant ID>"
    Then I should receive a return code of 204
    When I navigate to DELETE "/tenants/<New Tenant ID>"
    Then I should receive a return code of 204
    And I should no longer be able to get that tenant's data

Scenario: Deny creation when specifying invalid fields

    Given I am logged in using "developer" "developer1234" to realm "SLI"
    When I POST a tenant specifying an invalid field
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

Scenario: Deny creation when specifying individual wrong size fields

    Given I am logged in using "developer" "developer1234" to realm "SLI"
    When I POST a basic tenant with "tenantId" set to "123456789012345678901234567890123456789012345678A"
    Then I should receive a return code of 400
    When I POST a basic tenant with "tenantId" set to ""
    Then I should receive a return code of 400
    When I POST a basic tenant with userName "123456789012345678901234567890123456789012345678A"
    Then I should receive a return code of 400
    When I POST a basic tenant with userName ""
    Then I should receive a return code of 400

Scenario Outline: Deny creation when specifying individual landingZone wrong size fields

    Given I am logged in using "developer" "developer1234" to realm "SLI"
    When I POST a basic tenant with landingZone <Property> set to <Value>
    Then I should receive a return code of 400
    Examples:
    | Property                | Value |
    | "educationOrganization" | "123456789012345678901234567890123456789012345678901234567890A" |
    | "educationOrganization" | "" |
    | "desc"                  | "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456A" |

@wip
Scenario: Deny creation when missing userName
    Given I am logged in using "developer" "developer1234" to realm "SLI"
    When I POST a basic tenant with no userName
    Then I should receive a return code of 400

Scenario Outline: Deny creation when missing individual fields

    Given I am logged in using "developer" "developer1234" to realm "SLI"
    When I POST a basic tenant with missing <Property>
    Then I should receive a return code of 400
    Examples:
    | Property      |
    | "tenantId"    |
    | "landingZone" |

Scenario Outline: Deny creation when missing individual landingZone fields

    Given I am logged in using "developer" "developer1234" to realm "SLI"
    When I POST a basic tenant with missing landingZone <Property>
    Then I should receive a return code of 400
    Examples:
    | Property                |
    | "educationOrganization" |
