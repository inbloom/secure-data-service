@wip
@RALLY_US3459

Feature: Tailored welcome email, user is able to log in

  @production
  Scenario Outline: When I set my password, I get a welcome email and I am able to log in
    Given I have a new <account_type> account
    When I set my password
    Then I get an email of <template_type>
    When I click on the portal link in the email
    Then I am redirected to the portal
    When I enter my username and password
    Then I can log in the portal
  Examples:
    |account_type  |template_type  |
    |LEA Admin     |LEA or SEA type|
    |SEA Admin     |LEA or SEA type|
    |Ingestion User|Ingestion Admin|
    |Realm Admin   |Realm Admin    |

  @sandbox
  Scenario Outline: When I set my password on sandbox account, I get a welcome email and I am able to log in
    Given I have a new <account_type> account
    When I set my password
    Then I get an email of <template_type>
    When I click on the portal link in the email
    Then I am redirected to the portal
    When I enter my username and password
    Then I can log in the portal
  Examples:
    |account_type               |template_type        |
    |Sandbox Admin              |Admin Rights         |
    |Ingestion Admin            |Ingestion Rights     |
    |Application Developer      |Application Developer|
    |Admin and Ingestion        |MIXED 1              |
    |All Rights                 |MIXED 2              |
    |App Developer and Ingestion|MIXED 3              |
