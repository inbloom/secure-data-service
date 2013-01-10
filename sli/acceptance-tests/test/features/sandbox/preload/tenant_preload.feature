@RALLY_DE1685
Feature: Tenant Preload Resource Test
As an administrator for SLI, I want to access preload resource to create preload info in tenant entity

@sandbox
Scenario Outline: create preload info on Tenant
    Given There is a Tenant with tenantId "sandboxadministrator@slidev.org" in mongo
    And There is a EdOrg with stateOrganizationId "STANDARD-SEA" in mongo
    And the tenant has a landing zone path for this edorg
    Given I am logged in using <User> <Password> to realm <Realm>
    When I navigate to POST "/tenants/tenant_UUID/preload"
    Then I should receive a return code of <Code>
   
Examples:
    | User                    | Password                | Realm | Code       |
    | "sandboxingestionuser"  | "ingestionuser1234"     | "SLI" |  201       |
    | "sandboxdeveloper"      | "sandboxdeveloper1234"  | "SLI" |  403       |
    | "iladmin"               | "iladmin1234"           | "SLI" |  403       |
    | "sunsetadmin"           | "sunsetadmin1234"       | "SLI" |  403       |
    | "operator"              | "operator1234"          | "SLI" |  403       |
