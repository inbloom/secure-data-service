Feature: Admin Tools session cookie must be encrypted

  Background:
    Given I have an open web browser

  Scenario: Check that Admin Tools session cookie is encrypted
    When I navigate to the User Management Page
    And I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
    Then the decoded cookie "ADMIN_SESSION_ID" is encrypted and should not contain "slcoperator"
