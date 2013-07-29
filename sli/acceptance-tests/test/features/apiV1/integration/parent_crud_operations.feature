@RALLY_US4305
@RALLY_US4306
Feature: As a parent I want to use apps that access the inBloom API

  Background: None

  @parent_crud
  Scenario: Parent cannot Write to public entities with Access Denied security event asserts
    Given I log in to realm "Illinois Daybreak Parents" using simple-idp as "parent" "marsha.sollars" with password "marsha.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    And the sli securityEvent collection is empty
    #POST
    When I POST and validate the following entities:
      | entityName                    | entityType                 | returnCode |
      | newProgram                    | program                    | 403        |
    And I check to find if record is in sli db collection:
        | collectionName  | expectedRecordCount | searchParameter         | searchValue                                | searchType |
        | securityEvent   | 1                   | body.tenantId           | Midgar                                     | string     |
        | securityEvent   | 1                   | body.appId              | EGbI4LaLaL                                 | string     |
        | securityEvent   | 1                   | body.className          | org.slc.sli.api.jersey.PreProcessFilter    | string     |
        | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                                | string     |
        | securityEvent   | 1                   | body.targetEdOrgList    | IL-DAYBREAK                                | string     |
    And "1" security event with field "body.actionUri" matching "http.*/api/rest/v1.3/programs" should be in the sli db
    And "1" security event with field "body.logMessage" matching "Access Denied:url http.*/api/rest/v1.3/programs is not accessible." should be in the sli db

  @parent_crud
  Scenario: Parent cannot Write to public entities
    Given I log in to realm "Illinois Daybreak Parents" using simple-idp as "parent" "marsha.sollars" with password "marsha.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    #POST
    When I POST and validate the following entities:
      | entityName                    | entityType                 | returnCode |
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
    #PATCH
    Then I PATCH entities and check return code
      | Endpoint                    | Id                                          | Field                  | ReturnCode |
      | programs                    | 36980b1432275aae32437bb367fb3b66c5efc90e_id | programType            | 403        |
      | sections                    | 8d9ad6c3b870e8775016fff99fbd9c74920de8d5_id | repeatIdentifier       | 403        |
      | learningObjectives          | a39aa7089c0e0b8a271ed7caad97b8d319f7d236_id | academicSubject        | 403        |
      | learningStandards           | c772fbb0f9b9210d1f2a1bfcd53018b205c46da6_id | subjectArea            | 403        |
      | courseOfferings             | 4e22b4b0aac3310de7f4b789d5a31e5e2bd792ec_id | localCourseTitle       | 403        |
      | competencyLevelDescriptor   | c91ae4718903d20289607c3c4335759e652ad569_id | description            | 403        |
      | sessions                    | 3327329ef80b7419a48521818d65743234d6e5fb_id | sessionName            | 403        |
      | courses                     | d875eac3c6117f5448437c192ac1ea7c3cc977dd_id | courseDescription      | 403        |
      | studentCompetencyObjectives | b7080a7f753939752b693bca21fe60375d15587e_id | objective              | 403        |
      | educationOrganizations      | 1b223f577827204a1c7e9c851dba06bea6b031fe_id | shortNameOfInstitution | 403        |
      | gradingPeriods              | e71e876487c72d1c1c0e9f7fa413815706e7f422_id | endDate                | 403        |
      | assessments                 | 8e47092935b521fb6aba9fdec94a4f961f04cd45_id | identificationCode     | 403        |
    # We are using this more robust stepdef because certain entities require patching embedded non-string fields
    #When I PATCH and validate the following entities:
      #|  field               |  entityName                   |  value                                 |  returnCode  |
      #|  patchIndividualPlan |  patchEdOrg                   |  false                                 |  204         |
    #DELETE
    When I DELETE and validate the following entities:
      | entity                | id                                          | returnCode  |
     #| Dont exist in mongo   | Transitive endpoints should return a        | 403         |
      | assessment            | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | courseOffering        | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | course                | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | educationOrganization | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | gradingPeriod         | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | learningObjective     | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | learningStandard      | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | program               | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | schools               | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | section               | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | session               | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
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
      | educationOrganizations/id/schools   | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | schools/id/courseOfferings          | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | schools/id/courses                  | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | schools/id/sections                 | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | schools/id/sessions                 | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | schools/id/sessions/gradingPeriods  | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | sessions/id/courseOfferings         | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | sessions/id/courseOfferings/courses | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | sessions/id/sections                | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403         |
      | learningObjectives/id/childLearningObjectives         | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403 |
      | learningObjectives/id/learningStandards               | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403 |
      | learningObjectives/id/parentLearningObjectives        | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403 |
      | educationOrganizations/id/educationOrganizations      | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403 |
      | educationOrganizations/id/graduationPlans             | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403 |
      | educationOrganizations/id/studentCompetencyObjectives | doesnotexist177d00b06dee3fd928c1bfda4d49_id | 403 |
    When I DELETE and validate the following entities:    
      | entity                | id                                          | returnCode  |
     #| Do exist in mongo     | Transitive endpoints should return a        | 403         |
      | assessment            | 235e448a14cc25ac0ede32bf35e9a798bf2cbc1d_id | 403         |
      | courseOffering        | 4e22b4b0aac3310de7f4b789d5a31e5e2bd792ec_id | 403         |
      | course                | 7f3baa1a1f553809c6539671f08714aed6ec8b0c_id | 403         |
      | educationOrganization | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id | 403         |
      | gradingPeriod         | e71e876487c72d1c1c0e9f7fa413815706e7f422_id | 403         |
      | graduationPlan        | 7f6e03f2a01f0f74258a1b0d8796be5eaf289f0a_id | 403         |
      | learningObjective     | 735a9b42268fbe4a5be61124034be656249759dd_id | 403         |
      | learningStandard      | 7a9dc734146e8deff33b53a4e645e6b7cfd2c167_id | 403         |
      | program               | de7da21b8c7f020cc66a438d3cd13eb32ba41cb0_id | 403         |
      | school                | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id | 403         |
      | section               | 2982f5d3840b0a46bf152c7b7243c0db8dda694f_id | 403         |
      | session               | bfeaf9315f04797a41dbf1663d18ead6b6fb1309_id | 403         |
    When I DELETE and validate the following entities:
      | entity                              | id                                          | returnCode  |
     #| These entities dont exist in mongo  | Non-transitive endpoints should return a    | 403         |    
      | assessments/id/learningObjectives   | 735a9b42268fbe4a5be61124034be656249759dd_id | 403         |
      | assessments/id/learningStandards    | 7a9dc734146e8deff33b53a4e645e6b7cfd2c167_id | 403         |
      | courseOfferings/id/courses          | 7f3baa1a1f553809c6539671f08714aed6ec8b0c_id | 403         |
      | courseOfferings/id/sections         | 2982f5d3840b0a46bf152c7b7243c0db8dda694f_id | 403         |
      | courseOfferings/id/sessions         | bfeaf9315f04797a41dbf1663d18ead6b6fb1309_id | 403         |
      | courses/id/courseOfferings          | 514196bf10482bbfa307c023360692ef4c8f87db_id | 403         |
      | courses/id/courseOfferings/sessions | bfeaf9315f04797a41dbf1663d18ead6b6fb1309_id | 403         |
      | educationOrganizations/id/courses   | 7f3baa1a1f553809c6539671f08714aed6ec8b0c_id | 403         |
      | educationOrganizations/id/schools   | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id | 403         |
      | schools/id/courseOfferings          | 514196bf10482bbfa307c023360692ef4c8f87db_id | 403         |
      | schools/id/courses                  | 7f3baa1a1f553809c6539671f08714aed6ec8b0c_id | 403         |
      | schools/id/sections                 | 2982f5d3840b0a46bf152c7b7243c0db8dda694f_id | 403         |
      | schools/id/sessions                 | bfeaf9315f04797a41dbf1663d18ead6b6fb1309_id | 403         |
      | schools/id/sessions/gradingPeriods  | 21b8ac38bf886e78a879cfdb973a9352f64d07b9_id | 403         |
      | sessions/id/courseOfferings         | 514196bf10482bbfa307c023360692ef4c8f87db_id | 403         |
      | sessions/id/courseOfferings/courses | 7f3baa1a1f553809c6539671f08714aed6ec8b0c_id | 403         |
      | sessions/id/sections                | 2982f5d3840b0a46bf152c7b7243c0db8dda694f_id | 403         |
      | learningObjectives/id/childLearningObjectives         | 735a9b42268fbe4a5be61124034be656249759dd_id | 403 |
      | learningObjectives/id/learningStandards               | 7a9dc734146e8deff33b53a4e645e6b7cfd2c167_id | 403 |
      | learningObjectives/id/parentLearningObjectives        | 735a9b42268fbe4a5be61124034be656249759dd_id | 403 |
      | educationOrganizations/id/educationOrganizations      | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id | 403 |
      | educationOrganizations/id/graduationPlans             | 7f6e03f2a01f0f74258a1b0d8796be5eaf289f0a_id | 403 |
      | educationOrganizations/id/studentCompetencyObjectives | b7080a7f753939752b693bca21fe60375d15587e_id | 403 |
    When I DELETE and validate the following entities:
      | entity                     | id                                          | returnCode  |
      | attendance                 | fb7b5e0d6cebb8e9e35c8700270882f9c4adb49c_id | 403         |
      | cohort                     | 4711a3d63401b22260d9ed17313b9fc301f02c6f_id | 403         |
      | competencyLevelDescriptor  | c91ae4718903d20289607c3c4335759e652ad569_id | 403         |
      #| grade                      | 5aaf89278b7226f668f46509403d86a2b5968978_id9a437b8ade601e7ea50d146c9f91ec6745313c01_id | 403 |
      | gradebookEntry             | 2982f5d3840b0a46bf152c7b7243c0db8dda694f_ida861d8326b3144fe12262cae42b27b979d368dbe_id | 403 |
      | parent                     | 5f8989384287747b1960d16edd95ff2bb318e3bd_id | 403         |
      | parent                     | ea649b2220d73ec57eb2e92589c3049d44d102f7_id | 403         |
      | reportCard                 | 5aaf89278b7226f668f46509403d86a2b5968978_id2f2b79c129330d6f371439c9d63988abf8fffd05_id | 403 |
      | staff                      | e27fc445699aa38246a09373e6aeaa96981ea921_id | 403         |
      | staffCohortAssociation     | 3748e919e23549ec9205e917658f4b3486fd42b1_id | 403         |
      #| student                    | 067198fd6da91e1aa8d67e28e850f224d6851713_id | 403         |
      #| student                    | aea1153839c7923a4d70ca9f5859dbc0895d629f_id | 403         |
      | studentAcademicRecord      | 5aaf89278b7226f668f46509403d86a2b5968978_idf686ca38e2c6acd3eeb149ba351c6da21930e096_id | 403 |
      #| studentAssessment          | efaff117aa1656323dd225d70fc97501b393a7a6_id | 403         |
      | studentCohortAssociation   | 067198fd6da91e1aa8d67e28e850f224d6851713_idc787af32ad98c7e7062619db99d233c32582d30a_id | 403 |
      | studentCompetencyObjective | b7080a7f753939752b693bca21fe60375d15587e_id | 403         |
      | studentProgramAssociation  | 067198fd6da91e1aa8d67e28e850f224d6851713_id1564ad795bbfa929b2deb7b06386ad60f50c84fc_id | 403 |
      | studentSchoolAssociation   | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id | 403         |
      | studentSectionAssociation  | 2982f5d3840b0a46bf152c7b7243c0db8dda694f_id06f4aa0f6d84ae7ab290709fc348754cbd232cb5_id | 403 |
      | teacher                    | 8b6a31734ed43040f8a171d5d85e39176c543f22_id |  403        |
      | teacherSectionAssociation  | 2982f5d3840b0a46bf152c7b7243c0db8dda694f_idc3c6577c5e222d9575e6ebed2d280fe30fa1fd97_id | 403 |
      | teacherSchoolAssociation   | 079552a19e8f011d8c3d7ae4747148bbd9bc1e9a_id |  403        |
      | disciplineAction                     | abf2058b5a6fca25845a02df37d3ec1ab0b4c17d_id | 403         |
      | disciplineAction                     | 8c64b0b28cc48c9b12b303a36eeb6e9e067b0892_id | 403         |
      | disciplineIncident                   | 10829163dfd39c1830394a1ffde09a77c11ae065_id | 403         |
      | disciplineIncident                   | 84bea3a3c04d6be8935c8f057e8f3c080c0faf36_id | 403         |
      | studentDisciplineIncidentAssociation | 067198fd6da91e1aa8d67e28e850f224d6851713_idcc5123a629dee85e5f64ef188c1c33f2ffd6c210_id | 403 |
      | studentDisciplineIncidentAssociation | 908404e876dd56458385667fa383509035cd4312_id33a1c7ee086d4c488531652ab4a99cf0b6bd619d_id | 403 |


@parent_crud @clean_up_parent_posts
Scenario: POST new entities as a parent without, then with extended rights
Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
  And format "application/json"
  And I am using api version "v1"
  # As an IT Admin, POST cgray as a parent
  When I POST and validate the following entities:
    | entityName                              | entityType                | returnCode |
    | cgray.parent                            | parent                    | 201        |
    | cgray.studentParentAssociation.myClass  | studentParentAssociation  | 201        |
    | cgray.studentParentAssociation.notMyKid | studentParentAssociation  | 201        |
    | cgray.studentParentAssociation.mySchool | studentParentAssociation  | 201        |

# Asociate cgray to a student in a different LEA
Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
  When I POST and validate the following entities:
    | entityName                              | entityType                | returnCode |
    | cgray.studentParentAssociation.newLea   | studentParentAssociation  | 201        |
    | cgray.studentSchoolAssociation.myClass  | studentSchoolAssociation  | 201        |
    | cgray.studentSectionAssociation.myClass | studentSectionAssociation | 201        |

  # Make sure cgray can edit himself, but cannot edit any students yet since he has no studentParentAssociations
  And I log in to realm "Illinois Daybreak Parents" using simple-idp as "parent" "charles.gray" with password "charles.gray1234"
  When I PATCH and validate the following entities:
    | fieldName                 | entityType               | value   | returnCode | endpoint                                             |
    | cgray.name                | parent                   | Patched | 403        | parents/1fe86fe9c45680234f1caa3b494a1c4b42838954_id  |
    | cgray.name                | parent                   | Patched | 403        | parents/17075ee3f54bcf75fe37a75b098682b0644594d6_id  |
    | cgray.myClass.name        | student                  | Patched | 403        | students/fdd8ee3ee44133f489e47d2cae109e886b041382_id |
    | cgray.contactRestrictions | studentParentAssociation | Patched | 403        | studentParentAssociations/fdd8ee3ee44133f489e47d2cae109e886b041382_idec053d2e0752799cb0217578d003a1fe8f06b9a0_id |

  # Make sure cgray can see the student in a different LEA
  Then I validate I have access to entities via the API access pattern "/v1/Entity/Id":
    | entity                     | id                                          |
    | students                   | fdd8ee3ee44133f489e47d2cae109e886b041382_id |
    | students                   | 6b41180a6ba41031f50f3b50c97ef5f9387666c3_id |
    | students                   | f07bc57c18f13e8bb692660a7fab0ca92817598c_id |
    | sections                   | cee6195d1c5e2605bea2f3c34d264442c78638d2_id |
    | teachers                   | 67721e64bd47dc3a5845d643ef8c5f6635a9362a_id |
    | schools                    | f43e124e966084ce15bdba9b4e9befc92adf09ea_id |
    | staff                      | 0a6289889ce37aec0a94de535b2df8dfe3c136ee_id |
    | staff                      | 7a501f815f2224125033913c891d629e0d0f5fb9_id |
    | staffCohortAssociations    | f812f7507a1ab13b54a8ef390323342705bd9171_id |
    | staffProgramAssociations   | b519ce9ad9bd8588fb52997180888063a4ba9fce_id |
    | cohorts                    | cfa2241d0bbf48070ed492e653931db4bcd9bb43_id | 
    | cohorts                    | 271a8d3a18ae2d80599dc55a0abaaeb8527ff10f_id |
    | parents                    | 1fe86fe9c45680234f1caa3b494a1c4b42838954_id |
    | parents                    | cfa25e8916d80fd0e0c34507b3007ae0b45b938d_id |
    | parents                    | fe09eab2fe8d42454d03be52230d7fad92a84231_id |
    | programs                   | 1dbf54bff85c7f24df0a978f61dce57537ed4713_id |
    | programs                   | 44e7f0db5e6ad16796158a6961e1df9dd22da125_id |
    | educationOrganizations     | 99d527622dcb51c465c515c0636d17e085302d5e_id |
    | assessments                | f0ffa2e21cf1fc400527ac2ba63c20e4a620815c_id |
    | studentAssessments         | d4efea33872dc68985dbd1fc75c67681b8b13a60_id |
    | attendances                | 63033e548daa3c024ad12ae98466e96055909da5_id |
    | yearlyAttendances          | 63033e548daa3c024ad12ae98466e96055909da5_id |
    | studentGradebookEntries    | 93434cd0e970de1222e21e1b569e560bba82e203_id |
    | studentCompetencies        | 6668114ed5195e2a0baf50a42b9a81a6006a9ca2_id |
    | studentCompetencyObjectives| b7080a7f753939752b693bca21fe60375d15587e_id |
    | courseTranscripts          | 81ab106aa9e0b2658957cad5e9316e7e87e853a7_id |
    | studentSchoolAssociations  | 23125624f5f1dcfcf7e27eae8e7b44d91945ad2e_id |
    | teacherSchoolAssociations  | 5246bf9f52584eb497216d488a610959642219ed_id |
    | teacherSectionAssociations | cee6195d1c5e2605bea2f3c34d264442c78638d2_id2c4ea17ca750d3409c070bd638d6af7212160513_id |
    | grades                     | 00a80d4a19731ce76274a2b433d326a75e0040ba_id82ebb43f2947f56dcdb6cf61b1123d6df728a30f_id |
    | gradebookEntries           | d0bf8bb1e3418c8c7578a89403d6ffea5cb9c1a6_id58a523f121d74d87d45c9a26a686decdbce622ef_id |
    | reportCards                | 00a80d4a19731ce76274a2b433d326a75e0040ba_id719e8d9ca6168ad347b28985d80a61c518a67ae3_id |
    | studentAcademicRecords     | 00a80d4a19731ce76274a2b433d326a75e0040ba_id792ce89761e86b315ad10daadc0be38a0e44f010_id |
    | studentSectionAssociations | cee6195d1c5e2605bea2f3c34d264442c78638d2_idf073a2639d4d95f8fea3b0cfb96e17580416c819_id |
    | studentParentAssociations  | fdd8ee3ee44133f489e47d2cae109e886b041382_id146cb6be194273e519e711b0af37d42af7800c06_id |
    | studentParentAssociations  | fdd8ee3ee44133f489e47d2cae109e886b041382_idfbfd0e2202a671708374cb077c7ffd45e94046b7_id |
    | studentCohortAssociations  | fdd8ee3ee44133f489e47d2cae109e886b041382_id090a854fd293a2b17a2f7c54c2cc5f9b5f63e18b_id |
    | studentProgramAssociations | fdd8ee3ee44133f489e47d2cae109e886b041382_idfcd0570abc7cd5997bd21c74a1d1078cc7ee6a07_id |
    | staffEducationOrgAssignmentAssociations | f7371a2a35e09e879b04f8985bc77f23f99aa5e6_id |

# Now modify the Parent role to include the rights of an IT-Administrator
Given I get the rights for the "Parent" role in realm "deadbeef-1bad-4606-a936-094331bddeed"
  And I change the "Parent" role for realm "deadbeef-1bad-4606-a936-094331bddeed" to permit the following rights:
    | right            |
    | WRITE_PUBLIC     |
    | WRITE_RESTRICTED |
    | READ_GENERAL     |
    | AGGREGATE_READ   |
    | READ_PUBLIC      |
    | READ_RESTRICTED  |
    | WRITE_GENERAL    |

#POST entities as cgray with the parent role
Given I log in to realm "Illinois Daybreak Parents" using simple-idp as "parent" "charles.gray" with password "charles.gray1234"
  When I POST and validate the following entities:
    | entityName                              | entityType               | returnCode |
   #| cgray.parent                            | parent                   | 409        |
    | cgray.parent.notMe                      | parent                   | 403        |
    | newDaybreakStudent                      | student                  | 403        |
    | cgray.studentParentAssociation.myClass  | studentParentAssociation | 403        |
    | cgray.studentParentAssociation.notMyKid | studentParentAssociation | 403        |
    | cgray.studentParentAssociation.mySchool | studentParentAssociation | 403        |
    | cgray.studentParentAssociation.newLea   | studentParentAssociation | 403        |
    | cgray.studentAssessment                 | studentAssessment        | 403        |
    | cgray.studentGradebookEntry             | studentGradebookEntry    | 403        |
    | cgray.grade                             | grade                    | 403        |

  When I PATCH and validate the following entities:
    | fieldName                 | entityType               | value   | returnCode | endpoint                                             |
    | cgray.name                | parent                   | Patched | 204        | parents/1fe86fe9c45680234f1caa3b494a1c4b42838954_id  |
    | cgray.name                | parent                   | Patched | 403        | parents/17075ee3f54bcf75fe37a75b098682b0644594d6_id  |
    | cgray.myClass.name        | student                  | Patched | 204        | students/fdd8ee3ee44133f489e47d2cae109e886b041382_id |
    | cgray.myClass.name        | student                  | Patched | 403        | students/0324d50380119f1927eda4efcfd61061b23e3143_id |
    | cgray.contactRestrictions | studentParentAssociation | Patched | 403        | studentParentAssociations/fdd8ee3ee44133f489e47d2cae109e886b041382_idec053d2e0752799cb0217578d003a1fe8f06b9a0_id |
  
  When I PUT and validate the following entities:
    | field               | entityName               | value    | returnCode | endpoint                                                            |
    | name.middleName     | parent                   | Puttayed | 204        | parents/1fe86fe9c45680234f1caa3b494a1c4b42838954_id                 |
    | name.middleName     | parent                   | Puttayed | 403        | parents/17075ee3f54bcf75fe37a75b098682b0644594d6_id                 |
    | name.middleName     | student                  | Puttayed | 204        | students/fdd8ee3ee44133f489e47d2cae109e886b041382_id                |
    | name.middleName     | student                  | Puttayed | 403        | students/0324d50380119f1927eda4efcfd61061b23e3143_id                |
    | contactRestrictions | studentParentAssociation | Puttayed | 403        | studentParentAssociations/fdd8ee3ee44133f489e47d2cae109e886b041382_idec053d2e0752799cb0217578d003a1fe8f06b9a0_id |

  When I DELETE and validate the following entities:
    | entity                   | id                                           | returnCode |
    | parent                   | f9643b7abba04ae01586723abed0e38c63e4f975_id  | 403        |
    | student                  | fdd8ee3ee44133f489e47d2cae109e886b041382_id  | 403        |
    | studentParentAssociation | fdd8ee3ee44133f489e47d2cae109e886b041382_idec053d2e0752799cb0217578d003a1fe8f06b9a0_id  | 403        |

  And the sli securityEvent collection is empty
  When I PATCH and validate the following entities:
    | fieldName                 | entityType               | value   | returnCode | endpoint                                             |
    | cgray.name                | parent                   | Patched | 403        | parents/17075ee3f54bcf75fe37a75b098682b0644594d6_id  |
  And a security event matching "Access Denied:Cannot update parent not yourself" should be in the sli db
  And I check to find if record is in sli db collection:
    | collectionName  | expectedRecordCount | searchParameter         | searchValue                                        | searchType |
    | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                                        | string     |
    | securityEvent   | 1                   | body.targetEdOrgList    | South Highwind Elementary                          | string     |
    | securityEvent   | 1                   | body.tenantId           | Midgar                                             | string     |
    | securityEvent   | 1                   | body.appId              | EGbI4LaLaL                                         | string     |
    | securityEvent   | 1                   | body.logLevel           | TYPE_INFO                                          | string     |
    | securityEvent   | 1                   | body.className          | org.slc.sli.api.service.BasicService               | string     |
    | securityEvent   | 1                   | body.user               | cgray, Charles Gray                                | string     |
    | securityEvent   | 1                   | body.logMessage         | Access Denied:Cannot update parent not yourself    | string     |


  And the sli securityEvent collection is empty
  When I PATCH and validate the following entities:
    | fieldName                 | entityType               | value   | returnCode | endpoint                                             |
    | cgray.myClass.name        | student                  | Patched | 403        | students/0324d50380119f1927eda4efcfd61061b23e3143_id |
  And a security event matching "Access Denied:Cannot update student that are not your own" should be in the sli db
  And I check to find if record is in sli db collection:
    | collectionName  | expectedRecordCount | searchParameter               | searchValue                                               | searchType |
    | securityEvent   | 1                   | body.userEdOrg                | IL-DAYBREAK                                               | string     |
    | securityEvent   | 1                   | body.targetEdOrgList          | Daybreak Central High                                     | string     |
    | securityEvent   | 1                   | body.tenantId                 | Midgar                                                    | string     |
    | securityEvent   | 1                   | body.appId                    | EGbI4LaLaL                                                | string     |
    | securityEvent   | 1                   | body.logLevel                 | TYPE_INFO                                                 | string     |
    | securityEvent   | 1                   | body.className                | org.slc.sli.api.service.BasicService                      | string     |
    | securityEvent   | 1                   | body.user                     | cgray, Charles Gray                                       | string     |
    | securityEvent   | 1                   | body.logMessage               | Access Denied:Cannot update student that are not your own | string     |


  And I change the "Parent" role for realm "deadbeef-1bad-4606-a936-094331bddeed" back to its original rights