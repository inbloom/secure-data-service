Feature:
  As a federated user via an admin application
  I should NOT be able to perform CRUD operations on certain admin resources

  Background:
    Given I am logged in as a tenant-level IT Administrator
    And format "application/json"

  Scenario Outline: API admin GET endpoints are inaccessible for federated users
    When I navigate to <Action> "/<Resource URI>"
    Then the response status should be <Expected Status>

  Examples:
    | Resource URI     | Action | Expected Status |
    | customRoles      | POST   | 403             |
    | customRoles      | GET    | 403             |
    | adminDelegation  | GET    | 403             |
    | adminDelegation  | POST   | 403             |
    | provision        | POST   | 403             |
    | tenants          | GET    | 403             |
    | tenants          | POST   | 403             |
    | users            | GET    | 403             |
    | users            | POST   | 403             |
    | realm            | GET    | 403             |
    | realm            | POST   | 403             |

   Scenario Outline: API admin GET "application" and "applicationAuthorization" endpoints are inaccessible for federated users with proper rights
      When I navigate to <Action> "/<Resource URI>"
      Then the response status should be <Expected Status>

    Examples:
     | Resource URI     | Action | Expected Status |
     | apps             | GET    | 200             |
     | apps             | POST   | 403             |
     | applicationAuthorization| GET | 200         |
