@wip
@RALLY_US5959
Feature: Databrowser login via an artifact binding idp server and encrypted POST idp server

  Background:
    Given that databrowser has been authorized for all ed orgs
    Given I have an open web browser
    And I navigated to the Data Browser Home URL
    And I was redirected to the Realm page

  Scenario Outline: Successfully login as an IT Administrator
    And I select "<Realm>" from the dropdown and click go
    And I was redirected to the "Shibboleth" IDP Login page
    When I submit the credentials "wronguser" "wrongpassword" for the "Shibboleth" login page
    Then I should see a login failed message
    When I submit the credentials "jstevenson" "wrongpassword" for the "Shibboleth" login page
    Then I should see a login failed message
    When I submit the credentials "jstevenson" "jstevenson1234" for the "Shibboleth" login page
    Then I should be redirected to the Data Browser home page
    And I should see my available links labeled
    And I should navigate to "/entities/system/session/debug"
    And I should see "James Stevenson" on the page
    And I should see "IT Administrator" on the page
  Examples:
    | Realm                                 |
    | Artifact Daybreak                     |
    | Encrypted Assertion Artifact Daybreak |
    | Encrypted POST Daybreak               |

  Scenario Outline: Unable to use databrowser as an Educator
    And I select "<Realm>" from the dropdown and click go
    And I was redirected to the "Shibboleth" IDP Login page
    When I submit the credentials "cgray" "cgray1234" for the "Shibboleth" login page
    Then I get message that I am not authorized
  Examples:
    | Realm                                 |
    | Artifact Daybreak                     |
    | Encrypted Assertion Artifact Daybreak |
    | Encrypted POST Daybreak               |

  Scenario Outline: Unable to use databrowser as a user not in the database
    And I select "<Realm>" from the dropdown and click go
    And I was redirected to the "Shibboleth" IDP Login page
    When I submit the credentials "tcuyper" "tcuyper1234" for the "Shibboleth" login page
    Then I get an error message that says "Invalid user."
  Examples:
    | Realm                                 |
    | Artifact Daybreak                     |
    | Encrypted Assertion Artifact Daybreak |
    | Encrypted POST Daybreak               |

  Scenario Outline: Unable to use databrowser as a user with no roles
    And I select "<Realm>" from the dropdown and click go
    And I was redirected to the "Shibboleth" IDP Login page
    When I submit the credentials "ckoch" "ckoch1234" for the "Shibboleth" login page
    Then I get an error message that says "Invalid user. No roles specified for user."
  Examples:
    | Realm                                 |
    | Artifact Daybreak                     |
    | Encrypted Assertion Artifact Daybreak |
    | Encrypted POST Daybreak               |
