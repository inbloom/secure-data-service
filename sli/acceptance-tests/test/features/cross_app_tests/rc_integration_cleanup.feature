@RALLY_US4835
@rc
Feature:  RC Integration CleanUp Tests

Background:
  Given I have an open web browser

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
  Then I see the realms for "IL-DAYBREAK"
  When I click the "Daybreak Test Realm" delete button and confirm deletion
  And I switch to the iframe
  And I should receive a notice that the realm was successfully "deleted"
  Then I see the realms for "IL-DAYBREAK"
  And I exit out of the iframe
  And I click on log out

Scenario: slcoperator deletes SEA,LEA
  When I navigate to the user account management page
  And I see the realm selector I authenticate to "inBloom"
  Then I am redirected to "Simple" login page
  When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
  Then I delete the user "RCTestSeaAdminFN RCTestSeaAdminLN"
  Then I delete the user "RCTestLeaAdminFN RCTestLeaAdminLN"
