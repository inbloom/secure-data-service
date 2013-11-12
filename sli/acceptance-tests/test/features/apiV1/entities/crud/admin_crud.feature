@RALLY_US5865
Feature: As a federated user via an admin application, I should NOT be able to perform CRUD operations on certain admin resources

  Scenario Outline: API admin endpoints are inaccessible for federated users
    Given I am logged in using "rrogersAppAuth" "rrogers1234" to realm "IL"
    And format "application/vnd.slc+json"
    When I navigate to GET "/<Resource URI>"
    Then I should receive a return code of <Expected Status>

  Examples:
    | Resource URI     | Expected Status |
    | adminDelegation  | 406             |
    | provision        | 405             |
    | tenants          | 406             |
    | users            | 406             |
