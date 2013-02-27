@RALLY_US4835
@rc
Feature:  RC Integration CleanUp Tests

Background:
  Given I have an open web browser
  And I have a connection to Mongo

Scenario:  LEA deletes realm
  When I navigate to the Portal home page
  When I selected the realm "inBloom"
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "<SECONDARY_EMAIL>" "<SECONDARY_EMAIL_PASS>" for the "Simple" login page
  Then I should be on Portal home page
  Then I should see Admin link
  And I click on Admin
  Then I should be on the admin page
  And under System Tools, I click on "Manage Realm"
  And I switch to the iframe
  Then I see the realms for "Daybreak School District 4529 (IL-DAYBREAK)"
  When I click the "Daybreak Test Realm" delete button and confirm deletion
  And I switch to the iframe
  Then I see the realms for "Daybreak School District 4529 (IL-DAYBREAK)"
  And I exit out of the iframe
  And I click on log out

  Scenario: SEA purge tenant data
    When I drop a control file to purge tenant data as "<SEA ADMIN>" with password "<SEA ADMIN PASSWORD>" to "<SERVER>"
    Then my tenant database should be cleared
    And the landing zone should contain a file with the message "All records processed successfully."
    And the landing zone should contain a file with the message "Processed 0 records."
    And the landing zone should contain a file with the message "Purge process completed successfully."
    And I should not see an error log file created
    And I should not see a warning log file created

Scenario: slcoperator deletes SEA,LEA
  When I navigate to the user account management page
  And I see the realm selector I authenticate to "inBloom"
  Then I am redirected to "Simple" login page
  When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
  Then I delete the user "RCTestSeaAdminFN RCTestSeaAdminLN"
  Then I delete the user "RCTestLeaAdminFN RCTestLeaAdminLN"
