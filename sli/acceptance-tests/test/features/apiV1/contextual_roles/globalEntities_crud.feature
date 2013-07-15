@smoke @RALLY_US5775
Feature: As a staff member API user with multiple roles over different edOrgs,
         I want to be able to perform all CRUD operations on all global entities.

  Background: Setup for the tests
    Given I import the odin setup application and realm data
    And the testing device app key has been created

  @tagPublicEntities
  Scenario Outline: Ensure GET can be performed on all public entities with READ_PUBLIC right
    Given I change the custom role of "Leader" to add the "READ_PUBLIC" right
    Given I change the custom role of "Educator" to add the "READ_PUBLIC" right
    And I log in as "jmacey"
    And parameter "limit" is "0"
    Given entity type "<ENTITY TYPE>"
    When I navigate to GET "/v1/<ENTITY URI>"
    Then I should receive a return code of 200
    And I should see all global entities

  Examples:
      |  ENTITY TYPE                           |  ENTITY URI                            |
      |  assessment                            |  assessments                           |
      |  competencyLevelDescriptor             |  competencyLevelDescriptor             |
      |  courseOffering                        |  courseOfferings                       |
      |  course                                |  courses                               |
      |  educationOrganization                 |  educationOrganizations                |
      |  gradingPeriod                         |  gradingPeriods                        |
      |  graduationPlan                        |  graduationPlans                       |
      |  learningObjective                     |  learningObjectives                    |
      |  learningStandard                      |  learningStandards                     |
      |  program                               |  programs                              |
      |  educationOrganization                 |  schools                               |
      |  section                               |  sections                              |
      |  session                               |  sessions                              |
      |  studentCompetencyObjective            |  studentCompetencyObjectives           |

