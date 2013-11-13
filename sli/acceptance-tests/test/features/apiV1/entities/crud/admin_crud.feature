@RALLY_US5865
Feature: As a federated user via an admin application, I should NOT be able to perform CRUD operations on certain admin resources

  Scenario Outline: API admin GET endpoints are inaccessible for federated users
    Given I am logged in using "rrogersAppAuth" "rrogers1234" to realm "IL"
    And format "application/vnd.slc+json"
    When I navigate to <Action> "/<Resource URI>"
    Then I should receive a return code of <Expected Status>

  Examples:
    | Resource URI     | Action | Expected Status |
    | customRoles      | POST   | 403             |
    | customRoles      | GET    | 403             |
    | adminDelegation  | GET    | 403             |
    | adminDelegation  | POST   | 403             |
    | provision        | POST   | 403             |
    | tenants          | GET    | 403             |
    | users            | GET    | 403             |
