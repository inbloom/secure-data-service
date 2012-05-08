Feature: Tenant Registration Entity
As an administrator for SLI, I want to create a tenant entity so that it listen for files on landing zone

Scenario: CRUD operations on Tenants

    Given I am logged in using "developer" "developer1234" to realm "SLI"
    When I POST a new tenant
    Then I should receive a return code of 201
    And I should receive an ID for the newly created tenant
    When I rePOST the new tenant
    Then I should receive a return code of 201
    And I should receive the new tenant id
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
