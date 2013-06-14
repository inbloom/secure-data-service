@RALLY_US4305
@RALLY_US4306

Feature: As a student or staff I want to use apps that access the inBloom API

  Background: None

  @student_public
  Scenario: Student cannot POST public entities
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    When I POST and validate the following entities:
      | entity                        | type                       | returnCode |
      | newProgram                    | program                    | 403        |
      | newSection                    | section                    | 403        |
      | newLearningObjective          | learningObjective          | 403        |
      | newLearningStandard           | learningStandard           | 403        |
      | newCourseOffering             | courseOffering             | 403        |
      | newCompetencyLevelDescriptor  | competencyLevelDescriptor  | 403        |
      | newSession                    | session                    | 403        |
      | newSEACourse                  | course                     | 403        |
      | newStudentCompetencyObjective | studentCompetencyObjective | 403        |
      | newEducationOrganization      | educationOrganization      | 403        |
      | newGradingPeriod              | gradingPeriod              | 403        |
      | newAssessment                 | assessment                 | 403        |

  @student_patch
  Scenario: Student cannot PATCH public entities
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    Then I PATCH entities and check return code
      | Endpoint                    | Id                                          | Field                  | ReturnCode |
      | programs                    | 36980b1432275aae32437bb367fb3b66c5efc90e_id | programType            | 403        |
      | sections                    | 8d9ad6c3b870e8775016fff99fbd9c74920de8d5_id | repeatIdentifier       | 403        |
      | learningObjectives          | a39aa7089c0e0b8a271ed7caad97b8d319f7d236_id | academicSubject        | 403        |
      | learningStandards           | c772fbb0f9b9210d1f2a1bfcd53018b205c46da6_id | subjectArea            | 403        |
      | courseOfferings             | 7e2dc97f5868cf7fe5ec8a279facd9574b29af6a_id | localCourseTitle       | 403        |
      | competencyLevelDescriptor   | c91ae4718903d20289607c3c4335759e652ad569_id | description            | 403        |
      | sessions                    | 3327329ef80b7419a48521818d65743234d6e5fb_id | sessionName            | 403        |
      | courses                     | d875eac3c6117f5448437c192ac1ea7c3cc977dd_id | courseDescription      | 403        |
      | studentCompetencyObjectives | b7080a7f753939752b693bca21fe60375d15587e_id | objective              | 403        |
      | educationOrganizations      | 1b223f577827204a1c7e9c851dba06bea6b031fe_id | shortNameOfInstitution | 403        |
      | gradingPeriods              | 5db742ef357941df75afdfcdf78b12191d5898ef_id | endDate                | 403        |
      | assessments                 | 8e47092935b521fb6aba9fdec94a4f961f04cd45_id | identificationCode     | 403        |

  @wip
  Scenario: Student cannot POST private entities
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    When I POST and validate the following entities:
      | entity             | type         | returnCode |
      | newDaybreakStudent | staffStudent | 403        |

  @student_delete
  Scenario: Student cannot DELETE public entities
   Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    When I DELETE and validate the following entities:
    | entity                | id                                          | returnCode  |
   #| Dont exist in mongo   | Transitive endpoints should return a        | 404         |
    | assessment            | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 404         |
    | courseOffering        | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 404         |
    | course                | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 404         |
    | educationOrganization | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 404         |
    | gradingPeriod         | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 404         |
    | learningObjective     | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 404         |
    | learningStandard      | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 404         |
    | program               | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 404         |
    | schools               | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 404         |
    | section               | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 404         |
    | session               | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 404         |
    When I DELETE and validate the following entities:
    | entity                              | id                                          | returnCode  |
   #| These entities dont exist in mongo  | Non-transitive endpoints should return a    | 403         |    
    | assessments/id/learningObjectives   | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | assessments/id/learningStandards    | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | courseOfferings/id/courses          | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | courseOfferings/id/sections         | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | courseOfferings/id/sessions         | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | courses/id/courseOfferings          | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | courses/id/courseOfferings/sessions | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | educationOrganizations/id/courses   | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | educationOrganizations/id/educationOrganizations  | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403 |
    | educationOrganizations/id/graduationPlans         | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403 |
    | educationOrganizations/id/studentCompetencyObjectives | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403 |
    | educationOrganizations/id/schools   | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | learningObjectives/id/childLearningObjectives     | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403 |
    | learningObjectives/id/learningStandards           | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403 |
    | learningObjectives/id/parentLearningObjectives    | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403 |
    | schools/id/courseOfferings          | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | schools/id/courses                  | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | schools/id/sections                 | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | schools/id/sessions                 | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | schools/id/sessions/gradingPeriods  | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | sessions/id/courseOfferings         | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | sessions/id/courseOfferings/courses | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    | sessions/id/sections                | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
    When I DELETE and validate the following entities:    
    | These entities do exist in mongo    | I should get a return code of               | 403         |
    | entity                | id                                          | returnCode  |
   #| Dont exist in mongo   | Transitive endpoints should return a        | 404         |
    | assessment            | 235e448a14cc25ac0ede32bf35e9a798bf2cbc1d_id | 404         |
    | courseOffering        | 514196bf10482bbfa307c023360692ef4c8f87db_id | 404         |
    | course                | 7f3baa1a1f553809c6539671f08714aed6ec8b0c_id | 404         |
    | educationOrganization | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id | 404         |
    | gradingPeriod         | 21b8ac38bf886e78a879cfdb973a9352f64d07b9_id | 404         |
    | learningObjective     | a23b3b5546c0abdff108da9fd23b1018ef7d6c20_id | 404         |
    | learningStandard      | 7a9dc734146e8deff33b53a4e645e6b7cfd2c167_id | 404         |
    | program               | de7da21b8c7f020cc66a438d3cd13eb32ba41cb0_id | 404         |
    | schools               | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id | 404         |
    | section               | d4254efaa82daacfce951763bcd5e9e2352ac073_id | 404         |
    | session               | bfeaf9315f04797a41dbf1663d18ead6b6fb1309_id | 404         |

  @wip @student_delete
  Scenario: Student cannot DELETE private entities
   When I DELETE and validate the following entities:
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    | entity                     | id                                          | returnCode  |
    #| attendance                 | 95b973e29368712e2090fcad34d90fffb20aa9c4_id | 204         |
    #| cohort                     | cb99a7df36fadf8885b62003c442add9504b3cbd_id | 204         |
    #| competencyLevelDescriptor  | ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id | 204         |
    #| disciplineActions          | ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id | 204         |
    #| disciplineIncidents        | ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id | 204         |
    #| competencyLevelDescriptor  | ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id | 204         |
    #| competencyLevelDescriptor  | ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id | 204         |
    #| competencyLevelDescriptor  | ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id | 204         |
    #| grade                      | 1417cec726dc51d43172568a9c332ee1712d73d4_idcd83575df61656c7d8aebb690ae0bb3ff129a857_id | 204 |
    | gradebookEntry             | d4254efaa82daacfce951763bcd5e9e2352ac073_id53d7969bbd7fa2eab718eea517f04ce3b514607e_id | 204 |
    #| parent                     |                                             | 403         |
    #| reportCard                 | 1417cec726dc51d43172568a9c332ee1712d73d4_id77bc827b90835ef0df42154428ac3153f0ddc746_id | 204 |
    #| staff                      | e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id | 204         |
    #| staffCohortAssociation     | 5e7d5f12cefbcb749069f2e5db63c1003df3c917_id | 204         |
    | student                    | 067198fd6da91e1aa8d67e28e850f224d6851713_id | 403         |
    | student                    | aea1153839c7923a4d70ca9f5859dbc0895d629f_id | 403         |
    | studentAcademicRecord      | 1417cec726dc51d43172568a9c332ee1712d73d4_idb2b773084845209865762830ceb1721ebb1101ef_id | 204 |
    | studentAssessment          | 97918c8339c1e56bbef6cb4ce0c1164e9fec1c92_id | 204         |
    | studentCohortAssociation   | 9bf3036428c40861238fdc820568fde53e658d88_idfa64547520fbfcbc8646a7a0bb3a52f76e4f4d21_id | 204 |
    | studentCompetencyObjective | ef680988e7c411cdb5438ded373512cd59cbfa7b_id | 204         |
    | studentProgramAssociation  | 067198fd6da91e1aa8d67e28e850f224d6851713_id598e72efb94e91340f04feff220b0896efbc40f8_id | 204 |
    | studentSchoolAssociation   | cbfe3a47491fdff0432d5d4abca339735da9461d_id | 204         |
    | studentSectionAssociation  | 4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id | 204 |
    | teacher                    | 2472b775b1607b66941d9fb6177863f144c5ceae_id |  204        |
    | teacherSchoolAssociation   | 7a2d5a958cfda9905812c3a9f38c07ac4e8899b0_id |  204        |

