@RALLY_US3459

Feature: Tailored welcome email, user is able to log in

  Background:
    Given I have an open web browser

  @production
  Scenario Outline: When I set my password, I get a welcome email and I am able to log in
    Given I have a new account with <groups> in "production"
    When I set my password
    Then I get a welcome email of <template_type>
    And the email has a link to "portal"
    And I select "inBloom" from the dropdown and click go
    And I can log in with my username and password
  Examples:
    |groups                                |template_type                 |
    |SLC Operator                          |SLC Operator                  |
    |LEA Administrator                     |LEA or SEA only               |
    |SEA Administrator                     |LEA or SEA only               |
    |LEA Administrator, Realm Administrator|(LEA or SEA) and Realm Admin  |
    |LEA Administrator, Ingestion User     |(LEA or SEA) and Ingestion    |
    |SEA Administrator, Realm Administrator|(LEA or SEA) and Realm Admin  |
    |SEA Administrator, Ingestion User     |(LEA or SEA) and Ingestion    |
    |Ingestion User                        |Ingestion only                |
    |Realm Administrator                   |Realm Admin only              |

  @sandbox
  Scenario Outline: When I set my password on sandbox account, I get a welcome email and I am able to log in
    Given I have a new account with <groups> in "sandbox"
    When I set my password
    Then I get a welcome email of <template_type>
    And the email has a link to "portal"
    And I can log in with my username and password
  Examples:
    |groups                                                          |template_type                              |
    |Application Developer, Ingestion User                           |Application Developer and Sandbox Ingestion|
    |Sandbox Administrator, Application Developer                    |Sandbox Admin and Application Developer    |
    |Sandbox Administrator                                           |Sandbox Admin only                         |
    |Ingestion User                                                  |Sandbox Ingestion only                     |
    |Application Developer                                           |Application Developer only                 |
    |Sandbox Administrator, Ingestion User                           |Sandbox Admin and Sandbox Ingestion        |
    |Sandbox Administrator, Ingestion User, Application Developer    |Sandbox all rights                         |
    |Sandbox SLC Operator, Ingestion User                            |Sandbox Admin and Sandbox Ingestion        |
    |Sandbox SLC Operator, Ingestion User, Application Developer     |Sandbox all rights                         |
