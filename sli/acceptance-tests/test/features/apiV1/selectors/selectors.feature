@RALLY_US3289
Feature: As an SLI API, selectors should not work for 1.0

  Background: Logged in as an IT admin: Rick Rogers
    Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
    And format "application/json"

  Scenario: Applying pre-defined selectors
    Given selector "(*)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 400

  Scenario: Applying selectors on base level fields
    Given selector "(name,sex,birthData)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
    Then I should receive a return code of 400

  Scenario: Applying selectors on 1, 3 and 4 part URIs
    Given selector "(name)"
    When I navigate to GET "/v1/students"
    Then I should receive a return code of 400
    Given selector "(section)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>/studentSectionAssociations"
    Then I should receive a return code of 400
    Given selector "(sequenceOfCourse)"
    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>/studentSectionAssociations/sections"
    Then I should receive a return code of 400

#Feature: As an SLI API, I want to be able to specify the network payload granularity.
#  That means I am able to specify the data returned by providing a selector.
#
#  Background: Logged in as an IT admin: Rick Rogers
#    Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
#    And format "application/json"
#
#  Scenario: Applying pre-defined selectors
#    Given selector "(*)"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 200
#    And in the response body I should see the following fields only:
#    | address                               |
#    | assessments                           |
#    | attendances                           |
#    | birthData                             |
#    | cohortYears                           |
#    | cohorts                               |
#    | courseTranscripts                     |
#    | disabilities                          |
#    | disciplineActions                     |
#    | disciplineIncidents                   |
#    | displacementStatus                    |
#    | economicDisadvantaged                 |
#    | electronicMail                        |
#    | entityType                            |
#    | hispanicLatinoEthnicity               |
#    | homeLanguages                         |
#    | id                                    |
#    | languages                             |
#    | learningStyles                        |
#    | limitedEnglishProficiency             |
#    | links                                 |
#    | name                                  |
#    | otherName                             |
#    | parents                               |
#    | programParticipations                 |
#    | programs                              |
#    | race                                  |
#    | reportCards                           |
#    | schoolFoodServicesEligibility         |
#    | schools                               |
#    | section504Disabilities                |
#    | sections                              |
#    | sex                                   |
#    | studentAcademicRecords                |
#    | studentAssessments                    |
#    | studentCharacteristics                |
#    | studentCohortAssociations             |
#    | studentDisciplineIncidentAssociations |
#    | studentGradebookEntries               |
#    | studentIdentificationCode             |
#    | studentIndicators                     |
#    | studentParentAssociations             |
#    | studentProgramAssociations            |
#    | studentSchoolAssociations             |
#    | studentSectionAssociations            |
#    | studentUniqueStateId                  |
#    | telephone                             |
#    Given selector "(.)"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 200
#    And in the response body I should see the following fields only:
#    | address                       |
#    | birthData                     |
#    | cohortYears                   |
#    | disabilities                  |
#    | displacementStatus            |
#    | economicDisadvantaged         |
#    | electronicMail                |
#    | entityType                    |
#    | hispanicLatinoEthnicity       |
#    | homeLanguages                 |
#    | id                            |
#    | languages                     |
#    | learningStyles                |
#    | limitedEnglishProficiency     |
#    | links                         |
#    | name                          |
#    | otherName                     |
#    | programParticipations         |
#    | race                          |
#    | schoolFoodServicesEligibility |
#    | section504Disabilities        |
#    | sex                           |
#    | studentCharacteristics        |
#    | studentIdentificationCode     |
#    | studentIndicators             |
#    | studentUniqueStateId          |
#    | telephone                     |
#
#  Scenario: Applying selectors on base level fields
#    Given selector "(name,sex,birthData)"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 200
#    And in the response body I should see the following fields only:
#    | birthData  |
#    | entityType |
#    | id         |
#    | links      |
#    | name       |
#    | sex        |
#
#  Scenario: Applying selectors on nested fields
#    Given selector "(name,studentSectionAssociations:(*))"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 200
#    And in the response body I should see the following fields only:
#    | entityType                 |
#    | id                         |
#    | links                      |
#    | name                       |
#    | studentSectionAssociations |
#    And in "studentSectionAssociations" I should see the following fields only:
#    | studentGradebookEntries |
#    | entityType              |
#    | grades                  |
#    | id                      |
#    | sectionId               |
#    | sections                |
#    | studentCompetencies     |
#    | studentId               |
#    | students                |
#    | beginDate               |
#    Given selector "(name,studentSectionAssociations:(section:(*)))"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 200
#    And in the response body I should see the following fields only:
#    | entityType                 |
#    | id                         |
#    | links                      |
#    | name                       |
#    | studentSectionAssociations |
#    And in "studentSectionAssociations" I should see the following fields only:
#    | id         |
#    | entityType |
#    | sections   |
#    | studentId  |
#    And in "studentSectionAssociations=>sections" I should see the following fields only:
#    | assessments                |
#    | availableCredit            |
#    | courseOfferingId           |
#    | courseOfferings            |
#    | educationalEnvironment     |
#    | entityType                 |
#    | gradebookEntries           |
#    | id                         |
#    | mediumOfInstruction        |
#    | populationServed           |
#    | programReference           |
#    | programs                   |
#    | schoolId                   |
#    | schools                    |
#    | sequenceOfCourse           |
#    | sessionId                  |
#    | sessions                   |
#    | studentGradebookEntries    |
#    | studentSectionAssociations |
#    | students                   |
#    | teacherSectionAssociations |
#    | teachers                   |
#    | uniqueSectionCode          |
#
#  Scenario: Applying selectors on 1, 3 and 4 part URIs
#    Given selector "(name)"
#    When I navigate to GET "/v1/students"
#    Then I should receive a return code of 200
#    And in the response body for all entities I should see the following fields only:
#    | entityType |
#    | id         |
#    | links      |
#    | name       |
#    Given selector "(section)"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>/studentSectionAssociations"
#    Then I should receive a return code of 200
#    And in the response body for all entities I should see the following fields only:
#    | id         |
#    | entityType |
#    | links      |
#    | sections   |
#    Given selector "(sequenceOfCourse)"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>/studentSectionAssociations/sections"
#    Then I should receive a return code of 200
#    And in the response body for all entities I should see the following fields only:
#    | entityType                |
#    | id                        |
#    | links                     |
#    | sequenceOfCourse          |
#
#  Scenario: Applying selector to exclude fields
#    Given selector "(.,sex:false,cohortYears:false)"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 200
#    And in the response body I should see the following fields only:
#    | address                       |
#    | birthData                     |
#    | disabilities                  |
#    | displacementStatus            |
#    | economicDisadvantaged         |
#    | electronicMail                |
#    | entityType                    |
#    | hispanicLatinoEthnicity       |
#    | homeLanguages                 |
#    | id                            |
#    | languages                     |
#    | learningStyles                |
#    | limitedEnglishProficiency     |
#    | links                         |
#    | name                          |
#    | otherName                     |
#    | programParticipations         |
#    | race                          |
#    | schoolFoodServicesEligibility |
#    | section504Disabilities        |
#    | studentCharacteristics        |
#    | studentIdentificationCode     |
#    | studentIndicators             |
#    | studentUniqueStateId          |
#    | telephone                     |
#
#  Scenario: Applying empty selector
#    Given selector "()"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 200
#    And in the response body I should see the following fields only:
#    | id         |
#    | entityType |
#    | links      |
#    Given selector "(studentSectionAssociations:())"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 200
#    And in "studentSectionAssociations" I should see the following fields only:
#    | id         |
#    | entityType |
#    | studentId  |
#
#  Scenario: Sad path - include and exclude a field at the same time
#    Given selector "(name,sex:true,sex:false)"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 200
#    And in the response body I should see the following fields only:
#    | entityType                    |
#    | id                            |
#    | links                         |
#    | name                          |
#    Given selector "(name,sex:false,sex:true)"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 200
#    And in the response body I should see the following fields only:
#    | entityType                    |
#    | id                            |
#    | links                         |
#    | name                          |
#    | sex                           |
#
#  Scenario: Sad path - '$' as selector field
#    Given selector "($)"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 400
#    And I should be informed that the selector is invalid
#    Given selector "(name,studentSectionAssociations:($))"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 400
#    And I should be informed that the selector is invalid
#
#  Scenario: Sad path - ID as selector field
#    Given selector "(studentSectionAssociations:(sectionId))"
#    When I navigate to GET "/v1/students/<MARVIN MILLER STUDENT ID>"
#    Then I should receive a return code of 400
#    And I should be informed that the selector is invalid
#
#  Scenario: Sad path - Selector embeds too many documents
#    Given selector "(students:(*, sections:(*, students:(*))), teachers:(*, sections:(*, students:(*))), studentSchoolAssociations:(*), teacherSchoolAssociations:(*))"
#    When I navigate to GET "/v1/schools/8cc0a1ac-ccb5-dffc-1d74-32964722179b,92d6d5a0-852c-45f4-907a-912752831772,a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb,ec2e4218-6483-4e9c-8954-0aecccfd4731"
#    Then I should receive a return code of 413