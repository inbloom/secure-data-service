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
  When I navigate to GET "/v1/programs/9c320e961b9c6702c9013bf6d6a36a0701245c5c_id/studentProgramAssociations/students"
  Then I should be able to see <Fields> for the entity with ID <ID>:
    | ID                                          | Fields     |
    | 908404e876dd56458385667fa383509035cd4312_id | NameOnly   |
    | 2a83c3be5091796b40601b1215c62af421da6f8d_id | NameOnly   |
    | 4440ecc4484149919ad007ab8e0308a13a997912_id | NameOnly   |
    | 609640f6af263faad3a0cbee2cbe718fb71b9ab2_id | NameOnly   |
    | c25d10410b9d6d7955958911b94f40f761451dd1_id | NameOnly   |
    | f9d6f32e811b6b3f1a0482298f430d5168301391_id | NameOnly   |
    | 29c94cf2fb296f7b8e6300d6c24583468e80a5b0_id | NameOnly   |
    | b8ef8f6a6b421c2eaa5634d68f5227a199948df8_id | NameOnly   |
    | f9b49abe229f73946b2ef9f5cc708cf440761de2_id | NameOnly   |
    | 45dcc24a13157514bb2cb7e43356813146b258b8_id | NameOnly   |
    | aea1153839c7923a4d70ca9f5859dbc0895d629f_id | NameOnly   |
    | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id | AllStudent |
    | d2049def48bd80279dd3d774e776c1ad466497d1_id | NameOnly   |
    | 820642b49c9ca236d49c0384b98e9e6549047c34_id | NameOnly   |
    | 6d535e95cd376a40fa6348807b47867e37a4b0bd_id | NameOnly   |
    | 1851408f46551b2fcaae9e60786fe4339ef2bb3c_id | NameOnly   |
    | 14d0476047bc10afc09f4ab68a453a04306eb3c1_id | NameOnly   |
    | 37b544d7df759d3595c01770b9970788d5535a08_id | NameOnly   |
    | a417ac65cd0f660d7980d7452785e00e765d1b9b_id | NameOnly   |
    | b7c6e7f1311e8d70030509dc78a74e4e0bf1ff96_id | NameOnly   |
    | ab9e67df5e9030da4f23558b701af4d83729148f_id | NameOnly   |
    | fecf00a570a4471ee16ea3de648f0f849450ec2f_id | NameOnly   |
    | 126a8ceaaef2032ce0e8e3b1ea17ccd03cf869e2_id | NameOnly   |
    | 324313a6650ce8d03700271da0658e5c81b8628c_id | NameOnly   |
    | f9b25a057abd498c4a9ce367189d185f24b9681c_id | NameOnly   |
  When I navigate to GET "/v1/programs/9c320e961b9c6702c9013bf6d6a36a0701245c5c_id/studentProgramAssociations"
  Then I should be able to see <Fields> for the entity with ID <ID>:
    | ID                                                                                     | Fields       |
    | 908404e876dd56458385667fa383509035cd4312_id3ad6f62e61dba5df9c8e3a184c403215db59f08f_id | ProgramIds   |
    | 2a83c3be5091796b40601b1215c62af421da6f8d_idabd58e69b6e81b749eaec37e1149433b29e12ed6_id | ProgramIds   |
    | 4440ecc4484149919ad007ab8e0308a13a997912_id7f1ebb9be1ca0c6d9df5864823c7c39ce53da7d8_id | ProgramIds   |
    | 609640f6af263faad3a0cbee2cbe718fb71b9ab2_id1278b084606df5fc694c6b266a0b558975445ce2_id | ProgramIds   |
    | c25d10410b9d6d7955958911b94f40f761451dd1_id9677a596922063f3ce2db1d3841c508e256514f6_id | ProgramIds   |
    | f9d6f32e811b6b3f1a0482298f430d5168301391_id0bf5481248cc1c04ebbc6ed9c3186733b2efaa3e_id | ProgramIds   |
    | 29c94cf2fb296f7b8e6300d6c24583468e80a5b0_id848cb8a355b5d3520b3cf36a46cadd5024cc0169_id | ProgramIds   |
    | b8ef8f6a6b421c2eaa5634d68f5227a199948df8_idddfbf414a73c90da774885bb1b5d6c6ccbea4a74_id | ProgramIds   |
    | f9b49abe229f73946b2ef9f5cc708cf440761de2_idd58d7fd0a360ce0c929c490ef5af7487718c8655_id | ProgramIds   |
    | 45dcc24a13157514bb2cb7e43356813146b258b8_idf9e19950e00e6632bc0d12acfc8988760c8ad1a7_id | ProgramIds   |
    | aea1153839c7923a4d70ca9f5859dbc0895d629f_idb9c7de3692e1c97662f55eb3190cc8022d61a789_id | ProgramIds   |
    | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id9026d47eb0c7653fea6c19f7e8798c543bfc00e3_id | AllProgAssoc |
    | d2049def48bd80279dd3d774e776c1ad466497d1_idb042c8cce5bf59c63e59e88ff3c4e9061eebf101_id | ProgramIds   |
    | 820642b49c9ca236d49c0384b98e9e6549047c34_id6f95fb9f1801d362aa511c8f0369c0dda163fa06_id | ProgramIds   |
    | 6d535e95cd376a40fa6348807b47867e37a4b0bd_id269a65f20ecd157a5a2ac6e5c3f3e59625e2fa9e_id | ProgramIds   |
    | 1851408f46551b2fcaae9e60786fe4339ef2bb3c_id3398c6c9153948a5eff7430c8abf0cfd41f2bdce_id | ProgramIds   |
    | 14d0476047bc10afc09f4ab68a453a04306eb3c1_idd069a79ea3c8abf00f91f7f35e8c0f98e4cb3c0f_id | ProgramIds   |
    | 37b544d7df759d3595c01770b9970788d5535a08_id17f44cacffb8bebf24d513041fa887d594883480_id | ProgramIds   |
    | a417ac65cd0f660d7980d7452785e00e765d1b9b_id09e3a3ef46ac77aa66eaae4990dc3d8897430c6f_id | ProgramIds   |
    | b7c6e7f1311e8d70030509dc78a74e4e0bf1ff96_id09bfe39ba6110c46a1be6ffa9a4b6df5073879ab_id | ProgramIds   |
    | ab9e67df5e9030da4f23558b701af4d83729148f_id121adbd4ba650f73a71a4fdc1bcea9039f749c73_id | ProgramIds   |
    | fecf00a570a4471ee16ea3de648f0f849450ec2f_id1a0b30529cfa74891db5bfafd2dd04371512c6d1_id | ProgramIds   |
    | 126a8ceaaef2032ce0e8e3b1ea17ccd03cf869e2_id7d4d0a1b8e358afd5d738cb1481756124c13c966_id | ProgramIds   |
    | 324313a6650ce8d03700271da0658e5c81b8628c_id5767a918f0739a9d473d145fce43f7e7de828626_id | ProgramIds   |
    | f9b25a057abd498c4a9ce367189d185f24b9681c_id226ff670babacac799b300c388c1d939f833426c_id | ProgramIds   |
  When I navigate to GET "/v1/cohorts/4711a3d63401b22260d9ed17313b9fc301f02c6f_id/studentCohortAssociations/students"
  Then I should be able to see <Fields> for the entity with ID <ID>:
    | ID                                          | Fields     |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id | NameOnly   |
    | e1d57e8f1f7f50656cdc11265c4cdf4729223b78_id | NameOnly   |
    | 153e8258f62c711861d4bbc51653ee5fcea8e8ac_id | NameOnly   |
    | aea1153839c7923a4d70ca9f5859dbc0895d629f_id | NameOnly   |
    | 18fff957f4618b2a492b4393153e8ef43858153f_id | NameOnly   |
    | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id | AllStudent |
    | 080556ee7c69149d6143f309a758eec019ddfe11_id | NameOnly   |
    | 7b7fc7335ca0c8d8423d81740d1f1c0b9c7a3934_id | NameOnly   |
    | 18ebe03219dff01738c82645635997dbcb05205a_id | NameOnly   |
    | f24dad74523e919dcf96759b4f382df490bc9699_id | NameOnly   |
    | 392e181f62b546cdb4a76d79c7073521b5a0977a_id | NameOnly   |
    | ab9e67df5e9030da4f23558b701af4d83729148f_id | NameOnly   |
    | 31c4ff6bdc41c644772a5105386b6f7a215abfd2_id | NameOnly   |
  When I navigate to GET "/v1/cohorts/4711a3d63401b22260d9ed17313b9fc301f02c6f_id/studentCohortAssociations"
  Then I should be able to see <Fields> for the entity with ID <ID>:
    | ID                                                                                     | Fields        |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id35a7d3e403fa4702ee6db8cac8719eebf28a3e7e_id | CohortIds     |
    | e1d57e8f1f7f50656cdc11265c4cdf4729223b78_id8bae0d0699cd85818820a1cc54124e8cf069b8af_id | CohortIds     |
    | 153e8258f62c711861d4bbc51653ee5fcea8e8ac_idf87b49bd3af2880777f7d9502dbfafeddc89888e_id | CohortIds     |
    | aea1153839c7923a4d70ca9f5859dbc0895d629f_id8e7c71e4d4e154cec7b992a182d491a266837805_id | CohortIds     |
    | 18fff957f4618b2a492b4393153e8ef43858153f_id8e1565cdfc9f0cd9773ad1d5a4aa3f6dd4b06169_id | CohortIds     |
    | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_idf460df29cd44fc5458e7cf650fa40002ec274865_id | AllCohrtAssoc |
    | 080556ee7c69149d6143f309a758eec019ddfe11_idbce7df785ada5ed642d0c7f7e2b71579b5fce271_id | CohortIds     |
    | 7b7fc7335ca0c8d8423d81740d1f1c0b9c7a3934_id88d7d7842d656b655e089c9cbdd63f1d46de935f_id | CohortIds     |
    | 18ebe03219dff01738c82645635997dbcb05205a_id747f7fed6f8f5352e752479052380c62da2ff7a5_id | CohortIds     |
    | f24dad74523e919dcf96759b4f382df490bc9699_id033fd1615796b5cbe4d98a2d421a93348a476aa9_id | CohortIds     |
    | 392e181f62b546cdb4a76d79c7073521b5a0977a_id9f41329821da7d87d3fd71915f851b3c6c30be4b_id | CohortIds     |
    | ab9e67df5e9030da4f23558b701af4d83729148f_id8a8e059acdaa70e6666e0af14e51a586ef559529_id | CohortIds     |
    | 31c4ff6bdc41c644772a5105386b6f7a215abfd2_id22bb8c325d924f9047e01a34c2337774b786d75d_id | CohortIds     |


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
    When I verify the following response body fields in "/studentProgramAssociations/29c94cf2fb296f7b8e6300d6c24583468e80a5b0_id848cb8a355b5d3520b3cf36a46cadd5024cc0169_id":
      | field     | value                                       |
      | id        | 29c94cf2fb296f7b8e6300d6c24583468e80a5b0_id848cb8a355b5d3520b3cf36a46cadd5024cc0169_id |
      | studentId | 29c94cf2fb296f7b8e6300d6c24583468e80a5b0_id |
      | programId | 9c320e961b9c6702c9013bf6d6a36a0701245c5c_id |
    Then I verify the following response body fields do not exist in the response:
      | field                   |
      | beginDate               |
      | endDate                 |
      | educationOrganizationId |
      | services                |
      | reasonExited            |
    # Get other students StudentCohortAssociation
    When I verify the following response body fields in "/studentCohortAssociations/18ebe03219dff01738c82645635997dbcb05205a_id747f7fed6f8f5352e752479052380c62da2ff7a5_id":
      | field     | value                                       |
      | id        | 18ebe03219dff01738c82645635997dbcb05205a_id747f7fed6f8f5352e752479052380c62da2ff7a5_id |
      | studentId | 18ebe03219dff01738c82645635997dbcb05205a_id |
      | cohortId  | 4711a3d63401b22260d9ed17313b9fc301f02c6f_id |
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
    | /v1/students/89fa0cef4c8a3a0c50bcf132ee28fffdff4c90ef_id | 403 |
   #| Student with expired StudentCohortAssociation
    | /v1/students/f796e518237a69de9eac60b34093fad7fe7620b5_id | 403 |
   #| Student with no association to anything related
    | /v1/students/9a11978f7f0cbd6e1e1d7961edfe15a338e3c904_id | 403 |
