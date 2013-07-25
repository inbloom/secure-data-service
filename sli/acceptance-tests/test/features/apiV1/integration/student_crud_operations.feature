@RALLY_US4305
@RALLY_US4306

Feature: As a student or staff I want to use apps that access the inBloom API

  Background: None

  @student_crud
  Scenario: Student cannot Write to public entities
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
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


@student_crud @clean_up_student_posts
Scenario: POST to other student as a privileged student with extended rights
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "leader.m.sollars" with password "leader.m.sollars1234"
   And format "application/json"
   And I am using api version "v1"
   And the sli securityEvent collection is empty
  When I POST and validate the following entities:
    | entityName                     | entityType            | returnCode |
    | msollars.student               | student               | 403        |
  Then I should see a count of "1" in the security event collection
   And I check to find if record is in sli db collection:
    | collectionName  | expectedRecordCount | searchParameter         | searchValue                                      | searchType |
    | securityEvent   | 1                   | body.appId              | EGbI4LaLaL                                       | string     |
    | securityEvent   | 1                   | body.tenantId           | Midgar                                           | string     |
    | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                                      | string     |
    | securityEvent   | 1                   | body.targetEdOrgList    | IL-DAYBREAK                                      | string     |
    | securityEvent   | 1                   | body.logMessage         | Access Denied:Cannot update student not yourself | string     |
    | securityEvent   | 1                   | body.className          | org.slc.sli.api.service.BasicService             | string     |
   And "1" security event with field "body.actionUri" matching "http.*/api/rest/v1.3/students" should be in the sli db

@student_crud @clean_up_student_posts
Scenario: POST to other student assessment as a privileged student with extended rights
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "leader.m.sollars" with password "leader.m.sollars1234"
   And format "application/json"
   And I am using api version "v1"
   And the sli securityEvent collection is empty
  When I POST and validate the following entities:
    | entityName                     | entityType            | returnCode |
    | cgray.studentAssessment        | studentAssessment     | 403        |
  And I should see a count of "1" in the security event collection
  And I check to find if record is in sli db collection:
    | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                           | searchType |
    | securityEvent   | 1                   | body.appId              | EGbI4LaLaL                                                            | string     |
    | securityEvent   | 1                   | body.tenantId           | Midgar                                                                | string     |
    | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                                                           | string     |
    | securityEvent   | 1                   | body.targetEdOrgList    | Daybreak Central High                                                 | string     |
    | securityEvent   | 1                   | body.logMessage         | Access Denied:Cannot update student assessments that are not your own | string     |
    | securityEvent   | 1                   | body.className          | org.slc.sli.api.service.BasicService             | string     |
  And "1" security event with field "body.actionUri" matching "http.*/api/rest/v1.3/studentAssessments" should be in the sli db


@student_crud @clean_up_student_posts
Scenario: POST to other student entity as a privileged student with extended rights
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "leader.m.sollars" with password "leader.m.sollars1234"
   And format "application/json"
   And I am using api version "v1"
   And the sli securityEvent collection is empty
  When I POST and validate the following entities:
    | entityName         | entityType | returnCode |
    | cgray.grade        | grade      | 403        |
  And I should see a count of "1" in the security event collection
  And I check to find if record is in sli db collection:
    | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                           | searchType |
    | securityEvent   | 1                   | body.appId              | EGbI4LaLaL                                                            | string     |
    | securityEvent   | 1                   | body.tenantId           | Midgar                                                                | string     |
    | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                                                           | string     |
    | securityEvent   | 1                   | body.targetEdOrgList    | Daybreak Central High                                                 | string     |
    | securityEvent   | 1                   | body.logMessage         | Access Denied:Cannot update grade that are not your own               | string     |
    | securityEvent   | 1                   | body.className          | org.slc.sli.api.service.BasicService             | string     |
  And "1" security event with field "body.actionUri" matching "http.*/api/rest/v1.3/grades" should be in the sli db

@student_crud @clean_up_student_posts
Scenario: POST new entities as a privileged student with extended rights
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "leader.m.sollars" with password "leader.m.sollars1234"
   And format "application/json"
   And I am using api version "v1"
  When I POST and validate the following entities:
    | entityName                     | entityType            | returnCode |
    | msollars.studentAssessment     | studentAssessment     | 201        |
    | msollars.studentGradebookEntry | studentGradebookEntry | 201        |
    | msollars.grade                 | grade                 | 201        |
  When I PATCH and validate the following entities:
    | fieldName              | entityType            | value                       | returnCode | endpoint                                                                                      |
    | msollars.name          | student               | Patch                       | 204        | students/067198fd6da91e1aa8d67e28e850f224d6851713_id                                          |
    | diagnosticStatement    | grade                 | Student was patched derpy   | 204        | grades/f438cf61eda4d45d77f3d7624fc8d089aa95e5ea_id4542ee7a376b1c7813dcdc495368c875bc6b03ed_id |
    | gradeLevelWhenAssessed | studentAssessment     | Sixth grade                 | 204        | studentAssessments/f9643b7abba04ae01586723abed0e38c63e4f975_id                                |
    | diagnosticStatement    | studentGradebookEntry | "Student was patched good"  | 204        | studentGradebookEntries/7f714f03238d978398fbd4f8abbf9acb3e5775fe_id                           |
  When I PUT and validate the following entities:
    | field                  | entityName            | value                   | returnCode | endpoint                                                            |
    | name.firstName         | student               | MattPut                 | 204        | students/067198fd6da91e1aa8d67e28e850f224d6851713_id                |
    | diagnosticStatement    | grade                 | Student was put derpy   | 204        | grades/f438cf61eda4d45d77f3d7624fc8d089aa95e5ea_id4542ee7a376b1c7813dcdc495368c875bc6b03ed_id |
    | gradeLevelWhenAssessed | studentAssessment     | Seventh grade           | 204        | studentAssessments/f9643b7abba04ae01586723abed0e38c63e4f975_id      |
    | diagnosticStatement    | studentGradebookEntry | Student was put good    | 204        | studentGradebookEntries/7f714f03238d978398fbd4f8abbf9acb3e5775fe_id |
  When I DELETE and validate the following entities:
    | entity                | id                                           | returnCode |
    | studentAssessment     | f9643b7abba04ae01586723abed0e38c63e4f975_id  | 204        |
    | studentGradebookEntry | 7f714f03238d978398fbd4f8abbf9acb3e5775fe_id  | 204        |
    | grade                 | f438cf61eda4d45d77f3d7624fc8d089aa95e5ea_id4542ee7a376b1c7813dcdc495368c875bc6b03ed_id | 204        |
   #| student               | 067198fd6da91e1aa8d67e28e850f224d6851713_id  | 204        |
  #This step is just to put Matt Sollars back to normal
  When I PUT and validate the following entities:
    | field          | entityName | value | returnCode | endpoint                                             |
    | name.firstName | student    | Matt  | 204        | students/067198fd6da91e1aa8d67e28e850f224d6851713_id |
  When I PATCH and validate the following entities:
    | fieldName     | entityType | value      | returnCode | endpoint                                             |
    | msollars.name | student    | Aida       | 204        | students/067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | studentLunch  | student    | Full price | 204        | students/067198fd6da91e1aa8d67e28e850f224d6851713_id |

