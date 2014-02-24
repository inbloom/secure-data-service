Feature:
  As an federated administrator at the district-level
  In order to prevent modification of certain administrative functions
  I only want to be allowed to manage application authorizations

Scenario: Federated Users cannot access non-application authorization pages
  Given I am a valid federated district-level administrator
    And I have an open browser
    And I am managing my application authorizations
  Then I am not authorized to access the following pages:
     | custom_roles        |
     | users               |
     | realm_management    |
     | admin_delegations   |
     | landing_zone        |
     | change_passwords    |
     | account_managements |
     | lea                 |
     | changePassword      |