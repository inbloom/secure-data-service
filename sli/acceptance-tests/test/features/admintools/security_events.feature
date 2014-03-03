@security_event @no_ingestion_hooks

Feature:
  As an inBloom operator
  I want security events (access and denials) to be logged
  So that I can monitor the security of my inBloom instance

Background:
  Given I have an open browser

Scenario: Non-SLI-hosted valid user tries to access the Application Authorization Tool
  Given I am a valid non-SLI hosted user with no roles
   When I attempt to manage application authorizations
   Then I should see the error header for 403 Forbidden
    And I should see an error message indicating that "No valid role mappings exist"
    And I should see security events for:
      | Successful login             |
      | No valid role mappings exist |

Scenario: Valid SLI IDP user login to default Admin Page
  Given I am a valid SLC Operator
   When I attempt to go to the default administration page
   Then I should be on the default administration page
   Then I should see security events for:
      | Successful login         |
      | logged successfully into |

Scenario: Invalid SLI IDP user attempts to access the default admin page
  Given I am an unknown user
   When I attempt to go to the default administration page
   Then I should not be on the default administration page
    And I should see security events for:
      | Failed login |

Scenario: Valid SLI user who does not have any roles
  Given I am a valid SLI hosted user with no roles
   When I attempt to go to the default administration page
   Then I should not be on the default administration page
    But I should see an error message indicating that "User account is in invalid mode"
    And I should see security events for:
      | Failed login |

