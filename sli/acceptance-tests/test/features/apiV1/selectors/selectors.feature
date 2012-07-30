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
    | displacementStatus            |
    | id                            |
    | entityType                    |
    | links                         |
    | schoolId                      |
    | gradeLevel                    |

    Given selector "(name,sex,birthData)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 200
    And in the response body I should see the following fields only:
    | sex        |
    | name       |
    | birthData  |
    | id         |
    | entityType |
    | links      |
    | schoolId   |
    | gradeLevel |

  Scenario: Applying selectors on nested fields
    Given selector "(name,sectionAssociations:(*))"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 200
    And in the response body I should see the following fields only:
    | name                       |
    | studentSectionAssociations |
    | id                         |
    | entityType                 |
    | links                      |
    | schoolId                   |
    | gradeLevel                 |
    And in "studentSectionAssociations" I should see the following fields:
    | sectionId  |
    | studentId  |
    | id         |
    | entityType |

    Given selector "(name,sectionAssociations:(sectionId)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 200
    And in "studentSectionAssociations" I should see the following fields only:
    | sectionId  |
    | id         |
    | entityType |

  Scenario: Applying selector to exclude fields
    Given selector "(*,sex:false,cohortYears:false)"
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
    | displacementStatus            |
    | id                            |
    | entityType                    |
    | links                         |
    | schoolId                      |
    | gradeLevel                    |

