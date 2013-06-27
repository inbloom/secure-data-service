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

Scenario: Denying access to past associations
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "cegray" with password "cegray1234"
  And format "application/json"
  And I am using api version "v1"
  Then I validate that I am denied access to restricted endpoints via API:
    | uri                                                     | rc  |
   #| Student with expired StudentSectionAssociation NOTE: Odin does not have any of these
   #| /v1/students/e40ee9041a7159c62867f63bf4da581ba9fc3dc7_id | 403 |
   #| Student with expired StudentProgramAssociation
    | /v1/students/89fa0cef4c8a3a0c50bcf132ee28fffdff4c90ef_id | 403 |
   #| Student with expired StudentCohortAssociation
    | /v1/students/f796e518237a69de9eac60b34093fad7fe7620b5_id | 403 |
   #| Student with no association to anything related
    | /v1/students/9a11978f7f0cbd6e1e1d7961edfe15a338e3c904_id | 403 |
