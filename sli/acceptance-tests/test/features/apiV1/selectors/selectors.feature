@wip
@RALLY_US3289
Feature: As an SLI API, I want to be able to specify the network payload granularity.
  That means I am able to specify the data returned by providing a selector.

  Background: Logged in as an IT admin: Rick Rogers
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/json"

  Scenario: Applying selectors on base level fields
    Given selector "(*)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 200
    And in the response body I should see the following fields:
    | sex                           |
    | studentCharacteristics        |
    | economicDisadvantaged         |
    | hispanicLatinoEthnicity       |
    | disabilities                  |
    | cohortYears                   |
    | section504Disabilities        |
    | race                          |
    | programParticipations         |
    | languages                     |
    | studentUniqueStateId          |
    | name                          |
    | birthData                     |
    | otherName                     |
    | studentIndicators             |
    | homeLanguages                 |
    | learningStyles                |
    | limitedEnglishProficiency     |
    | studentIdentificationCode     |
    | address                       |
    | electronicMail                |
    | schoolFoodServicesEligibility |
    | telephone                     |
    | id                            |
    | entityType                    |

    Given selector "(name, sex, birthData)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 200
    And in the response body I should see the following fields only:
    | sex       |
    | name      |
    | birthData |

  Scenario: Applying selectors on nested fields
    Given selector "(name, sectionAssociations:(*))"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 200
    And in the response body I should see the following fields only:
    | name                |
    | sectionAssociations |
    And in "sectionAssociations" I should see the following fields:
    | sectionId |
    | studentId |
    | id        |

    Given selector "(name, sectionAssociations:(sectionId)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 200
    And in "sectionAssociations" I should see the following fields only:
    | sectionId |

  Scenario: Applying selector to exclude fields
    Given selector "(*, sex:false, cohortYears:false)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 200
    And in the response body I should see the following fields only:
    | studentCharacteristics        |
    | economicDisadvantaged         |
    | hispanicLatinoEthnicity       |
    | disabilities                  |
    | section504Disabilities        |
    | race                          |
    | programParticipations         |
    | languages                     |
    | studentUniqueStateId          |
    | name                          |
    | birthData                     |
    | otherName                     |
    | studentIndicators             |
    | homeLanguages                 |
    | learningStyles                |
    | limitedEnglishProficiency     |
    | studentIdentificationCode     |
    | address                       |
    | electronicMail                |
    | schoolFoodServicesEligibility |
    | telephone                     |
    | id                            |
    | entityType                    |

