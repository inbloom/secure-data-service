@student_students
Feature: Students accessing fellow students
  As a student I want to access students in my current sections, programs, cohorts to lookup their names

Scenario: Accessing Students via Multi-part URIs
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "cegray" with password "cegray1234"
  And format "application/json"
  And I am using api version "v1"
  When I navigate to GET "/v1/sections/88bb1ad61323121c4939db296f4d444094ad5563_id/studentSectionAssociations/students"
  Then I should be able to see <Fields> for the entity with ID <ID>:
    | ID                                          | Fields     |
    | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id | AllStudent |
    | 7b7fc7335ca0c8d8423d81740d1f1c0b9c7a3934_id | NameOnly   |
    | 18ebe03219dff01738c82645635997dbcb05205a_id | NameOnly   |
    | f24dad74523e919dcf96759b4f382df490bc9699_id | NameOnly   |
    | 0854e41747bbd84635eb0d327d8cfab283a3f7ae_id | NameOnly   |
    | 820642b49c9ca236d49c0384b98e9e6549047c34_id | NameOnly   |
    | 0dc21b2e5c92329271442eb1389993aa3d92d233_id | NameOnly   |
    | 392e181f62b546cdb4a76d79c7073521b5a0977a_id | NameOnly   |
    | 6d535e95cd376a40fa6348807b47867e37a4b0bd_id | NameOnly   |
    | 3265f13cb2255a82dd8f53804e01803311d812f0_id | NameOnly   |
    | 60b37f09d3b991222cd8a92c2609b30566385f97_id | NameOnly   |
  When I navigate to GET "/v1/sections/88bb1ad61323121c4939db296f4d444094ad5563_id/studentSectionAssociations"
  Then I should be able to see <Fields> for the entity with ID <ID>:
    | ID                                                                                     | Fields      |
    | 88bb1ad61323121c4939db296f4d444094ad5563_id786e763a5ffa777305dc1a0cfa3f62dfb278f593_id | AllSectAsoc |
    | 88bb1ad61323121c4939db296f4d444094ad5563_idc3f9d7359140da694a574c12007157e7a0f1b4d0_id | SecionIds   |
    | 88bb1ad61323121c4939db296f4d444094ad5563_idbc7a150fce67b483037bec6b43aeb4ea7a39736a_id | SecionIds   |
    | 88bb1ad61323121c4939db296f4d444094ad5563_id2efb5b279886e8eb7aace17481f977b7469e5e7b_id | SecionIds   |
    | 88bb1ad61323121c4939db296f4d444094ad5563_idc6fbe625227ce5b73ea16f7b5a4971f23b46f453_id | SecionIds   |
    | 88bb1ad61323121c4939db296f4d444094ad5563_id7848f3971a0ee8afd286c40b5e3c9a411ee8708c_id | SecionIds   |
    | 88bb1ad61323121c4939db296f4d444094ad5563_id17fc83e9b3985a6465301e11cd44c44ed84f138a_id | SecionIds   |
    | 88bb1ad61323121c4939db296f4d444094ad5563_id30846f09eb0d8c48ec5f1a2d320bfb8e060e975f_id | SecionIds   |
    | 88bb1ad61323121c4939db296f4d444094ad5563_id9afe585994ee60d2678d314cb74617f21aa6e7d5_id | SecionIds   |
    | 88bb1ad61323121c4939db296f4d444094ad5563_id5628bec9f40fd6f6655f817fba9236934b439227_id | SecionIds   |
    | 88bb1ad61323121c4939db296f4d444094ad5563_id88338d5fbd840995817282d81357f97dab86c886_id | SecionIds   |
  When I navigate to GET "/v1/programs/d5678b60124db54df4ae42565d5161b0a87e5691_id/studentProgramAssociations/students"
  Then I should be able to see <Fields> for the entity with ID <ID>:
    | ID                                          | Fields     |
    | 45dcc24a13157514bb2cb7e43356813146b258b8_id | NameOnly   |
    | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id | AllStudent |
    | 1d19aac952e77145ee45df1259ff8cf90ed92c7d_id | NameOnly   |
  When I navigate to GET "/v1/programs/d5678b60124db54df4ae42565d5161b0a87e5691_id/studentProgramAssociations"
  Then I should be able to see <Fields> for the entity with ID <ID>:
    | ID                                                                                     | Fields       |
    | 1d19aac952e77145ee45df1259ff8cf90ed92c7d_id12a57e4e94534389ca74e59dbbe485db87d22b4c_id | ProgramIds   |
    | 45dcc24a13157514bb2cb7e43356813146b258b8_id1b95aa6868b656e6cafb51e71fb6e364c3e0f9b7_id | ProgramIds   |
    | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id2e4e887f8b1a41f4ac99cfe21d1fd9bd16c52545_id | AllProgAssoc |

Scenario: Accessing Students via Multi-part URIs - part 2 since Celeste Gray has no valid cohort associations anymore
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "miha.tran" with password "miha.tran1234"
  And format "application/json"
  And I am using api version "v1"
  When I navigate to GET "/v1/cohorts/a0131cb5caf2fa21c5743014a2e7ef31f76d74a5_id/studentCohortAssociations/students"
  Then I should be able to see <Fields> for the entity with ID <ID>:
    | ID                                          | Fields     |
    | 92a33cae13e838176dbea9ca8b8c354d7420eaa8_id | AllStudent |
    | fb494e4d89e436d93a594c88e79c264bb5d42552_id | NameOnly   |
    | b108661120c77a442459a950d413bff2ea7a4aef_id | NameOnly   |
    | 9a6021688a00a3227e05ec870213a5f6d7b49584_id | NameOnly   |
    | e2a606e245de7b697e2426c07a4887ec31395ad7_id | NameOnly   |
    | fecf00a570a4471ee16ea3de648f0f849450ec2f_id | NameOnly   |
    | fb317151468e61386c567808649d9c0d24123880_id | NameOnly   |
    | 1e7bec17a23ae79f3b03a80949d35696214d40ec_id | NameOnly   |
    | 6f0b47f1a33d6fb235222a229533678bf71afedb_id | NameOnly   |
    | 43de135c4a6c11f904dfddae1bcf7dd117e0315b_id | NameOnly   |
    | 670b0b68d6de978a6302538ac855e8bbfa657fb0_id | NameOnly   |
    | a4d90e3162518c142a353f96b3aedaf2ab72d06d_id | NameOnly   |
    | e0a28626322863ce312f8d56a96aa565fda0280d_id | NameOnly   |
    | 92a33cae13e838176dbea9ca8b8c354d7420eaa8_id | NameOnly   |
    | 0725234ef8bc910a7c08fce22d20e05837957089_id | NameOnly   |
  When I navigate to GET "/v1/cohorts/a0131cb5caf2fa21c5743014a2e7ef31f76d74a5_id/studentCohortAssociations"
  Then I should be able to see <Fields> for the entity with ID <ID>:
    | ID                                                                                     | Fields        |
    | 92a33cae13e838176dbea9ca8b8c354d7420eaa8_ide6616a5c6908954307a9792896ad7083c288056d_id | AllCohrtAssoc |
    | 670b0b68d6de978a6302538ac855e8bbfa657fb0_id3a0707da8b329f81daf369c186f85f180187317d_id | CohortIds     |
    | 1e7bec17a23ae79f3b03a80949d35696214d40ec_id38eb95b895dcd869df7e41a67285f55a19dcdced_id | CohortIds     |
    | 6f0b47f1a33d6fb235222a229533678bf71afedb_id44dc8e362214ad09dd272de1d45187af4f72104d_id | CohortIds     |
    | 43de135c4a6c11f904dfddae1bcf7dd117e0315b_id26a0d41cc8492d376fd6d6ec02cad4255ccdd0ea_id | CohortIds     |
    | 0725234ef8bc910a7c08fce22d20e05837957089_ide08a34ee8e1bb652583bfa6d5ba251e30178c301_id | CohortIds     |
    | fb494e4d89e436d93a594c88e79c264bb5d42552_id4a65b28156b4411e21e3d98719f8950335a99026_id | CohortIds     |
    | 9a6021688a00a3227e05ec870213a5f6d7b49584_idf3272baf46e42605f7190d6565ad6ed1dc2da7f2_id | CohortIds     |
    | e2a606e245de7b697e2426c07a4887ec31395ad7_id4bf2938fed93dde6bcab1a3b9db7ac3c96ca2ca8_id | CohortIds     |
    | fecf00a570a4471ee16ea3de648f0f849450ec2f_ida79c35c4456caf7ef3b4107ddd4eba648c23fed3_id | CohortIds     |
    | b108661120c77a442459a950d413bff2ea7a4aef_id902aa5d387763f8b9ed942926aa34b43f822e903_id | CohortIds     |
    | a4d90e3162518c142a353f96b3aedaf2ab72d06d_id781deecb81cf3f5dcfeca145de41779cb42d570a_id | CohortIds     |
    | fb317151468e61386c567808649d9c0d24123880_id794265e4c85ba4c1d0b8aec6c02a6157b8199557_id | CohortIds     |
    | e0a28626322863ce312f8d56a96aa565fda0280d_id40175c18b2fee807d0ed3603a1e1c076a1f0a0af_id | CohortIds     |


Scenario: Accessing other students directly
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "cegray" with password "cegray1234"
  And format "application/json"
  And I am using api version "v1"
  # Get student in current Section
  When I verify the following response body fields in "/students/820642b49c9ca236d49c0384b98e9e6549047c34_id":
    | field            | value                                       |
    | id               | 820642b49c9ca236d49c0384b98e9e6549047c34_id |
    | name.firstName   | Myron                                       |
    | name.middleName  | Nicolas                                     |
    | name.lastSurname | Gilbert                                     |
  Then I verify the following response body fields do not exist in the response:
    | field                         |
    | studentUniqueStateId          |
    | studentIdentificationCode     |
    | otherName                     |
    | sex                           |
    | birthData                     |
    | address                       |
    | telephone                     |
    | electronicMail                |
    | profileThumbnail              |
    | hispanicLatinoEthnicity       |
    | oldEthnicity                  |
    | race                          |
    | economicDisadvantaged         |
    | schoolFoodServicesEligibility |
    | studentCharacteristics        |
    | limitedEnglishProficiency     |
    | languages                     |
    | homeLanguages                 |
    | disabilities                  |
    | section504Disabilities        |
    | displacementStatus            |
    | programParticipations         |
    | learningStyles                |
    | cohortYears                   |
    | studentIndicators             |
    | loginId                       |
    | gradeLevel                    |
    | schoolId                      |
  # Get student in current Cohort
  When I verify the following response body fields in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id":
    | field            | value                                       |
    | id               | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | name.firstName   | Matt                                        |
    | name.middleName  | Aida                                        |
    | name.lastSurname | Sollars                                     |
  Then I verify the following response body fields do not exist in the response:
    | field                         |
    | studentUniqueStateId          |
    | studentIdentificationCode     |
    | otherName                     |
    | sex                           |
    | birthData                     |
    | address                       |
    | telephone                     |
    | electronicMail                |
    | profileThumbnail              |
    | hispanicLatinoEthnicity       |
    | oldEthnicity                  |
    | race                          |
    | economicDisadvantaged         |
    | schoolFoodServicesEligibility |
    | studentCharacteristics        |
    | limitedEnglishProficiency     |
    | languages                     |
    | homeLanguages                 |
    | disabilities                  |
    | section504Disabilities        |
    | displacementStatus            |
    | programParticipations         |
    | learningStyles                |
    | cohortYears                   |
    | studentIndicators             |
    | loginId                       |
    | gradeLevel                    |
    | schoolId                      |
  # Get student in current Program
  When I verify the following response body fields in "/students/b7c6e7f1311e8d70030509dc78a74e4e0bf1ff96_id":
    | field            | value                                       |
    | id               | b7c6e7f1311e8d70030509dc78a74e4e0bf1ff96_id |
    | name.firstName   | Lucile                                      |
    | name.middleName  | Sandy                                       |
    | name.lastSurname | Underhill                                   |
  Then I verify the following response body fields do not exist in the response:
    | field                         |
    | studentUniqueStateId          |
    | studentIdentificationCode     |
    | otherName                     |
    | sex                           |
    | birthData                     |
    | address                       |
    | telephone                     |
    | electronicMail                |
    | profileThumbnail              |
    | hispanicLatinoEthnicity       |
    | oldEthnicity                  |
    | race                          |
    | economicDisadvantaged         |
    | schoolFoodServicesEligibility |
    | studentCharacteristics        |
    | limitedEnglishProficiency     |
    | languages                     |
    | homeLanguages                 |
    | disabilities                  |
    | section504Disabilities        |
    | displacementStatus            |
    | programParticipations         |
    | learningStyles                |
    | cohortYears                   |
    | studentIndicators             |
    | loginId                       |
    | gradeLevel                    |
    | schoolId                      |
    # Get other students StudentSectionAssociation
    When I verify the following response body fields in "/studentSectionAssociations/88bb1ad61323121c4939db296f4d444094ad5563_id7848f3971a0ee8afd286c40b5e3c9a411ee8708c_id":
      | field     | value                                       |
      | id        | 88bb1ad61323121c4939db296f4d444094ad5563_id7848f3971a0ee8afd286c40b5e3c9a411ee8708c_id |
      | studentId | 820642b49c9ca236d49c0384b98e9e6549047c34_id |
      | sectionId | 88bb1ad61323121c4939db296f4d444094ad5563_id |
    Then I verify the following response body fields do not exist in the response:
      | field             |
      | beginDate         |
      | endDate           |
      | homeroomIndicator |
      | repeatIdentifier  |
    # Get other students StudentProgramAssociation
    When I verify the following response body fields in "/studentProgramAssociations/45dcc24a13157514bb2cb7e43356813146b258b8_id1b95aa6868b656e6cafb51e71fb6e364c3e0f9b7_id":
      | field     | value                                       |
      | id        | 45dcc24a13157514bb2cb7e43356813146b258b8_id1b95aa6868b656e6cafb51e71fb6e364c3e0f9b7_id |
      | studentId | 45dcc24a13157514bb2cb7e43356813146b258b8_id |
      | programId | d5678b60124db54df4ae42565d5161b0a87e5691_id |
    Then I verify the following response body fields do not exist in the response:
      | field                   |
      | beginDate               |
      | endDate                 |
      | educationOrganizationId |
      | services                |
      | reasonExited            |

  Scenario: Accessing other students directly -- part 2 cohort
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "miha.tran" with password "miha.tran1234"
    And format "application/json"
    And I am using api version "v1"
    # Get other students StudentCohortAssociation
    When I verify the following response body fields in "/studentCohortAssociations/670b0b68d6de978a6302538ac855e8bbfa657fb0_id3a0707da8b329f81daf369c186f85f180187317d_id":
      | field     | value                                       |
      | id        | 670b0b68d6de978a6302538ac855e8bbfa657fb0_id3a0707da8b329f81daf369c186f85f180187317d_id |
      | studentId | 670b0b68d6de978a6302538ac855e8bbfa657fb0_id |
      | cohortId  | a0131cb5caf2fa21c5743014a2e7ef31f76d74a5_id |
    Then I verify the following response body fields do not exist in the response:
      | field     |
      | beginDate |
      | endDate   |

  Scenario: Denying access to past associations
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "cegray" with password "cegray1234"
  And format "application/json"
  And I am using api version "v1"
  Then I validate that I am denied access to restricted endpoints via API:
    | uri                                                     | rc  |
   #| Student with expired StudentSectionAssociation
   #| /v1/students/b13887c5f555d6675d1f71de3b0fa6ad3b67f8aa_id | 403 |
   #| Student with expired StudentProgramAssociation
    | /v1/students/e1d57e8f1f7f50656cdc11265c4cdf4729223b78_id | 403 |
   #| Student with expired StudentCohortAssociation
    | /v1/students/f796e518237a69de9eac60b34093fad7fe7620b5_id | 403 |
   #| Student with no association to anything related
    | /v1/students/9a11978f7f0cbd6e1e1d7961edfe15a338e3c904_id | 403 |
