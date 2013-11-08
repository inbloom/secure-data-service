Feature: Databrowser login via the shibboleth idp server

  Background:
    Given that databrowser has been authorized for all ed orgs

  Scenario:
    Given I have an open web browser
    And I navigated to the Data Browser Home URL
    And I was redirected to the Realm page
    And I choose realm "Shibboleth Daybreak" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the "Shibboleth" IDP Login page
    When I submit the credentials "wronguser" "wrongpassword" for the "Shibboleth" login page
    Then I should see a login failed message
    When I submit the credentials "jstevenson" "wrongpassword" for the "Shibboleth" login page
    Then I should see a login failed message
    When I submit the credentials "jstevenson" "jstevenson1234" for the "Shibboleth" login page