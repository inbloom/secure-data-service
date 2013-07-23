@RALLY_US5775 @RALLY_US5777
Feature: As a staff member API user with multiple roles over different edOrgs,
  I want to be able to perform all CRUD operations on all other entities.

  Background: Setup for the tests
    Given I import the odin setup application and realm data
    And the testing device app key has been created


# Single segment (/<ENTITY>) URI tests.

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
    | ENTITY TYPE                | ENTITY URI                  |
    | assessment                 | assessments                 |
    | competencyLevelDescriptor  | competencyLevelDescriptor   |
    | courseOffering             | courseOfferings             |
    | course                     | courses                     |
    | gradingPeriod              | gradingPeriods              |
    | graduationPlan             | graduationPlans             |
    | educationOrganization      | schools                     |
    | session                    | sessions                    |
    | studentCompetencyObjective | studentCompetencyObjectives |

  @wip
  Scenario Outline: Ensure GET can be performed on all edorg related entities with the proper rights
    And I log in as "jmacey"
    And parameter "limit" is "0"
    When I navigate to GET "/v1/<ENTITY URI>"
    Then I should receive a return code of 200

  Examples:
    | ENTITY URI                              |
    | staffEducationOrgAssignmentAssociations |
    | teacherSchoolAssociations               |
    | studentProgramAssocations               |
    | studentSchoolAssocations                |

  Scenario Outline: Ensure GET can NOT be performed on any public entities without READ_PUBLIC right
    Given I change the custom role of "Leader" to remove the "READ_PUBLIC" right
    Given I change the custom role of "Educator" to remove the "READ_PUBLIC" right
    And I log in as "jmacey"
    And parameter "limit" is "0"
    Given entity type "<ENTITY TYPE>"
    When I navigate to GET "/v1/<ENTITY URI>"
    Then I should receive a return code of 403

  Examples:
    | ENTITY TYPE                | ENTITY URI                  |
    | assessment                 | assessments                 |
    | competencyLevelDescriptor  | competencyLevelDescriptor   |
    | courseOffering             | courseOfferings             |
    | course                     | courses                     |
    | educationOrganization      | educationOrganizations      |
    | gradingPeriod              | gradingPeriods              |
    | graduationPlan             | graduationPlans             |
    | learningObjective          | learningObjectives          |
    | learningStandard           | learningStandards           |
    | program                    | programs                    |
    | educationOrganization      | schools                     |
    | section                    | sections                    |
    | session                    | sessions                    |
    | studentCompetencyObjective | studentCompetencyObjectives |

  @wip
  Scenario Outline: Ensure GET cannot be performed on all edorg related entities without the proper rights
    Given I change the custom role of "Leader" to remove the "READ_GENERAL" right
    And I change the custom role of "Leader" to remove the "READ_RESTRICTED" right
    And I change the custom role of "Educator" to remove the "READ_GENERAL" right
    And I change the custom role of "Educator" to remove the "READ_RESTRICTED" right
    And I log in as "jmacey"
    And parameter "limit" is "0"
    When I navigate to GET "/v1/<ENTITY URI>"
    Then I should receive a return code of 403

  Examples:
    | ENTITY URI                              |
    | staffEducationOrgAssignmentAssociations |
    | teacherSchoolAssociations               |
    | studentProgramAssocations               |
    | studentSchoolAssocations                |

  Scenario Outline: Ensure POST can be performed on all public entities with READ_PUBLIC and WRITE_PUBLIC rights
    Given I change the custom role of "Leader" to add the "READ_PUBLIC" right
    Given I change the custom role of "Leader" to add the "WRITE_PUBLIC" right
    Given I change the custom role of "Educator" to add the "READ_PUBLIC" right
    Given I change the custom role of "Educator" to add the "WRITE_PUBLIC" right
    And I log in as "jmacey"
    Given entity type "<ENTITY>"
    Given a valid formatted entity json document for a "<ENTITY TYPE>"
    When I navigate to POST "/v1/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    And I remove the new entity from "<ENTITY>"

  Examples:
    | ENTITY                     | ENTITY TYPE                | ENTITY URI                  |
    | assessment                 | assessment                 | assessments                 |
    | competencyLevelDescriptor  | competencyLevelDescriptor  | competencyLevelDescriptor   |
    | educationOrganization      | educationOrganization      | educationOrganizations      |
    | gradingPeriod              | gradingPeriod              | gradingPeriods              |
    | graduationPlan             | graduationPlan             | graduationPlans             |
    | learningObjective          | learningObjective          | learningObjectives          |
    | learningStandard           | learningStandard           | learningStandards           |
    | program                    | program                    | programs                    |
    | educationOrganization      | school                     | schools                     |
    | session                    | session                    | sessions                    |
    | studentCompetencyObjective | studentCompetencyObjective | studentCompetencyObjectives |

  @wip
  Scenario Outline: Ensure POST can be performed on edorg related entities with WRITE_GENERAL and WRITE_RESTRICTED rights
    Given I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I add a SEOA for "xbell" in "District 9" as a "Leader"
    And I log in as "xbell"
    Given a valid formatted entity json document for a "<ENTITY TYPE>"
    When I navigate to POST "/v1/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    And I remove the new entity from "<ENTITY>"

  Examples:
    | ENTITY                                | ENTITY TYPE                           | ENTITY URI                              |
    | staffEducationOrganizationAssociation | staffEducationOrganizationAssociation | staffEducationOrgAssignmentAssociations |
    | teacherSchoolAssociation              | teacherSchoolAssociation              | teacherSchoolAssociations               |
    | student.studentProgramAssociation     | studentProgramAssociation             | studentProgramAssociations              |
    | studentSchoolAssociation              | studentSchoolAssociation              | studentSchoolAssociations               |

  Scenario Outline: Ensure POST can NOT be performed on any public entities with READ_PUBLIC and WRITE_PUBLIC rights
    Given I change the custom role of "Leader" to remove the "READ_PUBLIC" right
    Given I change the custom role of "Leader" to remove the "WRITE_PUBLIC" right
    Given I change the custom role of "Educator" to remove the "READ_PUBLIC" right
    Given I change the custom role of "Educator" to remove the "WRITE_PUBLIC" right
    And I log in as "jmacey"
    Given entity type "<ENTITY>"
    Given a valid formatted entity json document for a "<ENTITY TYPE>"
    When I navigate to POST "/v1/<ENTITY URI>"
    Then I should receive a return code of 403

  Examples:
    | ENTITY                     | ENTITY TYPE                | ENTITY URI                  |
    | assessment                 | assessment                 | assessments                 |
    | competencyLevelDescriptor  | competencyLevelDescriptor  | competencyLevelDescriptor   |
    | courseOffering             | courseOffering             | courseOfferings             |
    | course                     | course                     | courses                     |
    | educationOrganization      | educationOrganization      | educationOrganizations      |
    | gradingPeriod              | gradingPeriod              | gradingPeriods              |
    | graduationPlan             | graduationPlan             | graduationPlans             |
    | learningObjective          | learningObjective          | learningObjectives          |
    | learningStandard           | learningStandard           | learningStandards           |
    | program                    | program                    | programs                    |
    | educationOrganization      | school                     | schools                     |
    | section                    | section                    | sections                    |
    | session                    | session                    | sessions                    |
    | studentCompetencyObjective | studentCompetencyObjective | studentCompetencyObjectives |

  @wip
  Scenario Outline: Ensure POST cannot be performed on edorg related entities without WRITE_GENERAL and WRITE_RESTRICTED rights
    Given I add a SEOA for "xbell" in "District 9" as a "Leader"
    And I log in as "xbell"
    Given a valid formatted entity json document for a "<ENTITY TYPE>"
    When I navigate to POST "/v1/<ENTITY URI>"
    Then I should receive a return code of 403

  Examples:
    | ENTITY TYPE                           | ENTITY URI                              |
    | staffEducationOrganizationAssociation | staffEducationOrgAssignmentAssociations |
    | teacherSchoolAssociation              | teacherSchoolAssociations               |
    | studentProgramAssocation              | studentProgramAssocations               |
    | studentSchoolAssocation               | studentSchoolAssocations                |

# Double segment (/<ENTITY>/{id}) URI tests.

  Scenario Outline: GETs on /entity/{id} for global entities
    Given I log in as "jmacey"
    And I get 10 random ids for "<ENTITY TYPE>" in "<ENTITY>"
    When I navigate to GET each id for "/v1/<ENTITY URI>"
    Then All the return codes should be 200

    Given I change the custom role of "Leader" to remove the "READ_PUBLIC" right
    And I change the custom role of "Educator" to remove the "READ_PUBLIC" right
    And I log in as "jmacey"
    When I navigate to GET each id for "/v1/<ENTITY URI>"
    Then All the return codes should be 403

  Examples:
    | ENTITY                     | ENTITY TYPE                | ENTITY URI                  |
    | assessment                 | assessment                 | assessments                 |
    | competencyLevelDescriptor  | competencyLevelDescriptor  | competencyLevelDescriptor   |
    | courseOffering             | courseOffering             | courseOfferings             |
    | course                     | course                     | courses                     |
    | educationOrganization      | educationOrganization      | educationOrganizations      |
    | gradingPeriod              | gradingPeriod              | gradingPeriods              |
    | graduationPlan             | graduationPlan             | graduationPlans             |
    | learningObjective          | learningObjective          | learningObjectives          |
    | learningStandard           | learningStandard           | learningStandards           |
    | program                    | program                    | programs                    |
    | educationOrganization      | school                     | schools                     |
    | section                    | section                    | sections                    |
    | session                    | session                    | sessions                    |
    | studentCompetencyObjective | studentCompetencyObjective | studentCompetencyObjectives |

    @wip
  Scenario Outline: GETs on /entity/{id} for edorg-related entities
    Given I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right
    And I log in as "msmith"
    And I get 10 random ids associated with the edorgs for "msmith" of "<ENTITY TYPE>" in "<ENTITY>"
    When I navigate to GET each id for "/v1/<ENTITY URI>"
    Then All the return codes should be 200

    Given I change the custom role of "Leader" to remove the "READ_GENERAL" right
    And I change the custom role of "Leader" to remove the "READ_RESTRICTED" right
    And I change the custom role of "Aggregate Viewer" to remove the "READ_GENERAL" right
    And I log in as "msmith"
    When I navigate to GET each id for "/v1/<ENTITY URI>"
    Then All the return codes should be 403

  Examples:
    | ENTITY                                | ENTITY TYPE                           | ENTITY URI                              |
    | staffEducationOrganizationAssociation | staffEducationOrganizationAssociation | staffEducationOrgAssignmentAssociations |
    | teacherSchoolAssociation              | teacherSchoolAssociation              | teacherSchoolAssociations               |
    | student                               | studentProgramAssociation             | studentProgramAssociations              |
    | studentSchoolAssociation              | studentSchoolAssociation              | studentSchoolAssociations               |

  Scenario Outline: PUTs, PATCHes, and DELETEs on /entity/{id}
    Given I change the custom role of "Aggregate Viewer" to add the "WRITE_PUBLIC" right
    Given I change the custom role of "Aggregate Viewer" to add the "WRITE_GENERAL" right
    Given I change the custom role of "Aggregate Viewer" to add the "WRITE_RESTRICTED" right
    And format "application/json"
    And a valid json document for entity "<ENTITY TYPE>"

    When I log in as "msmith"
    And I navigate to POST "/v1/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200

    When I log in as "jmacey"
    And I set the <MODIFY FIELD> to <PUT VALUE>
    And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 403

    When I log in as "msmith"
    And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204

    When I log in as "jmacey"
    And I change the field "<MODIFY FIELD>" to "<PATCH VALUE>"
    And I navigate to PATCH "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 403

    When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 403

    When I log in as "msmith"
    And I navigate to PATCH "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204

    When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204

    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 404

  Examples:
    | ENTITY TYPE                           | ENTITY URI                              | MODIFY FIELD       | PUT VALUE        | PATCH VALUE            |
    | assessment                            | assessments                             | assessmentCategory | Class test       | Other                  |
    | competencyLevelDescriptor             | competencyLevelDescriptor               | description        | Always Angry     | Skips school           |
    | educationOrganization                 | educationOrganizations                  | nameOfInstitution  | Dummy Agency     | Dummy Edorg            |
    | gradingPeriod                         | gradingPeriods                          | endDate            | 2013-01-01       | 2013-12-12             |
    | graduationPlan                        | graduationPlans                         | individualPlan     | true             | false                  |
    | learningObjective                     | learningObjectives                      | description        | My Description   | New Description        |
    | learningStandard                      | learningStandards                       | description        | My Description   | New Description        |
    | program                               | programs                                | programSponsor     | School           | State Education Agency |
    | school                                | schools                                 | nameOfInstitution  | Dummy Elementary | Dummy High             |
    | session                               | sessions                                | endDate            | 2013-01-01       | 2013-12-12             |
    | studentCompetencyObjective            | studentCompetencyObjectives             | description        | Basic Objective  | Advanced Objective     |
#    | staffEducationOrganizationAssociation | staffEducationOrgAssignmentAssociations | positionTitle      | Treasurer        | Principal              |
#    | teacherSchoolAssociation              | teacherSchoolAssociations               | academicSubjects   | [Reading]        | [Science]              |
#    | studentProgramAssociation             | studentProgramAssociations              | reasonExited       | Unknown reason   | Expulsion              |
#    | studentSchoolAssociation              | studentSchoolAssociations               | entryGradeLevel    | Eleventh grade   | Eighth grade           |

# Multi segment (/<ENTITY>/{id}/...) URI tests.

  Scenario: GETs on multiple (more than 2) part URIs of global entities
    When I log in as "jmacey"
    And I navigate to GET "/v1/educationOrganizations/99a4ec9d3ba372993b2860a798b550c77bb73a09_id/staffEducationOrgAssignmentAssociations/staff"
    Then I should receive a return code of 200