@RALLY_US3289
Feature: As an SLI API, I want to be able to specify the network payload granularity.
  That means I am able to specify the data returned by providing a selector.

  Background: Logged in as an IT admin: Rick Rogers
    Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
    And format "application/json"

  Scenario: Applying pre-defined selectors
    Given selector "(*)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 200
    And in the response body I should see the following fields only:
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
    Given selector "(.)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 200
    And in the response body I should see the following fields only:
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
    | studentSchoolAssociations     |
    | studentSectionAssociations    |

  Scenario: Applying selectors on base level fields
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
    And in "studentSectionAssociations" I should see the following fields only:
    | sectionId  |
    | studentId  |
    | id         |
    | entityType |
    Given selector "(name,sectionAssociations:(section:(*)))"
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
    And in "studentSectionAssociations" I should see the following fields only:
    | id         |
    | entityType |
    | sections   |
    | studentId  |
    And in "studentSectionAssociations=>sections" I should see the following fields only:
    | availableCredit        |
    | courseOfferingId       |
    | educationalEnvironment |
    | mediumOfInstruction    |
    | populationServed       |
    | programReference       |
    | schoolId               |
    | sequenceOfCourse       |
    | sessionId              |
    | uniqueSectionCode      |
    | id                     |
    | entityType             |

  Scenario: Applying selectors on 1, 3 and 4 part URIs
    Given selector "(name)"
    When I navigate to GET "/v1/students"
    Then I should receive a return code of 200
    And in the response body for all entities I should see the following fields only:
    | name                       |
    | id                         |
    | entityType                 |
    | links                      |
    | schoolId                   |
    | gradeLevel                 |
    Given selector "(section)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>/studentSectionAssociations"
    Then I should receive a return code of 200
    And in the response body for all entities I should see the following fields only:
    | id                         |
    | entityType                 |
    | links                      |
    | sections                   |
    Given selector "(sequenceOfCourse)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>/studentSectionAssociations/sections"
    Then I should receive a return code of 200
    And in the response body for all entities I should see the following fields only:
    | name                       |
    | id                         |
    | entityType                 |
    | links                      |
    | sequenceOfCourse           |

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

  Scenario: Sad path - '$' as selector field
    Given selector "($)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 400
    And I should be informed that the selector is invalid
    Given selector "(name,sectionAssociations:($))"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 400
    And I should be informed that the selector is invalid

  Scenario: Sad path - ID as selector field
    Given selector "(sectionAssociations:(sectionId))"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 400
    And I should be informed that the selector is invalid

  Scenario: Sad path - empty selector
    Given selector "()"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 200
    Given selector "(name,sectionAssociation:())"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 200

