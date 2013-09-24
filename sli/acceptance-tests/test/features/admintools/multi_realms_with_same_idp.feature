@RALLY_US5897
Feature: Multiple realms with the same IDP should be able to authenticate users.


Scenario: Multiple realms with the same IDP authenticates
  Given I have an open web browser
   When I hit the realm editing URL
  And I was redirected to the "Simple" IDP Login page
  And I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
    When I see the realms for "Sunset School District 4526 (IL-SUNSET)"
    And I click the "Illinois Sunset School District 4526" edit button
    Then I should see that I am on the "Illinois Sunset School District 4526" edit page
    Then I should enter "http://local.slidev.org:8082/simple-idp?realm=IL-Daybreak" into Redirect Endpoint
    Then I should enter "http://local.slidev.org:8082/simple-idp?realm=IL-Daybreak" into IDP URL
    And I should click the "Save" button
    Then I should be redirected back to the realm listing page
    And I should receive a notice that the realm was successfully "updated"
    And I see the realms for "Sunset School District 4526 (IL-SUNSET)"

  Scenario: User is able to login Sunset
    Given I have an open web browser
    Given I log in to realm "Illinois Sunset School District 4526" using simple-idp as "staff" "rrogers" with password "rrogers1234"

Scenario: User is able to login Daybreak
  Given I have an open web browser
  Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "staff" "rrogers" with password "rrogers1234"