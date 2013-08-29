@RALLY_US5470

Feature: As a student I want to use apps that access my associated Staff, Teachers, and Students via the inBloom API

Background: Kludge an association endDate to not expire until a more permanent solution is found
    Given I update the "staffCohortAssociation" with ID "56318bd0507551f3eff40aea265f3ce1e47ec3cc_id" field "body.endDate" to "2023-08-27"

@student_staff_endpoints
Scenario: Student has access to directly associated teachers, staff, and student entities via API
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1"
 When I validate I have access to entities via the API access pattern "/v1/Entity/Id":
    | entity                    | id                                          |
    | teachers                  | 8b6a31734ed43040f8a171d5d85e39176c543f22_id |
    | teachers                  | 4b07dba2b6868c0827315b99ea94fc74c0f7c902_id |
    | staff                     | 8b6a31734ed43040f8a171d5d85e39176c543f22_id |
    | staff                     | 00fb228365702c5fb03029216f3f057e174e3d6f_id |
    | staff                     | a94da453a895a6d9ea23f884c3d232323a4acaae_id |
    | staff                     | 0941af6d37f33cc9d690bd662894f851ee1bfd1e_id |
    | staff                     | aec76b8572750483be1e0d1e7b00d03e3b07220d_id |
    | staff                     | 143760f37839b2608d2c929ef26d30c900f6a434_id |
    | staff                     | b56a781295310ed319be5070ead4930590a82619_id |
    | staff                     | 1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id |
    | staff                     | c64062927d0a93b6cf92a55bf812ffb2e613683a_id |
    | staff                     | 4255c28503a1c96ed9a9127d1a21f992e636acd6_id |
    | staff                     | e27fc445699aa38246a09373e6aeaa96981ea921_id |
    | staff                     | 4b07dba2b6868c0827315b99ea94fc74c0f7c902_id |
    | staff                     | 58d1e760fcdc1612b900ecb8359a6d8b3e49a5ee_id |
    | staff                     | 6757c28005c30748f3bbda02882bf59bc81e0d71_id |
    # students in section       | eb8663fe6856b49684a778446a0a1ad33238a86d_id |
    | students                  | aea1153839c7923a4d70ca9f5859dbc0895d629f_id |
    | students                  | 38c2136429d32d2b69db9740e2e82c31c6b7bb7d_id |
    # students in section       | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id |
    | students                  | e1d57e8f1f7f50656cdc11265c4cdf4729223b78_id |
    | students                  | 080556ee7c69149d6143f309a758eec019ddfe11_id |
    # students in section       | e9b81633cba273dc9cc567d7f0f76a1c070c150d_id |
    | students                  | 45dcc24a13157514bb2cb7e43356813146b258b8_id |
    | students                  | 4ca25ffca7a2275de6c4ceb97eb4f390d2576bde_id |
    # students in section       | 2982f5d3840b0a46bf152c7b7243c0db8dda694f_id |
    | students                  | 153e8258f62c711861d4bbc51653ee5fcea8e8ac_id |
    | students                  | 18fff957f4618b2a492b4393153e8ef43858153f_id |


@student_cohort_endpoints
Scenario: Student has access to non-transitive associations through cohorts, programs, sections, schools
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1" 
  # Cohorts
  When I validate the current allowed association entities via API "/v1/cohorts/b4f9ddccc4c5c47a00541ee7c6d67fcb287316ce_id/staffCohortAssociations":
    | id                                          |
    | a17d936ca77e391ace5d14645a4b9b78f6dbd387_id |
    | 56318bd0507551f3eff40aea265f3ce1e47ec3cc_id |

  When I validate the allowed association entities via API "/v1/cohorts/b4f9ddccc4c5c47a00541ee7c6d67fcb287316ce_id/staffCohortAssociations/staff":
    | id                                          |
    | 0941af6d37f33cc9d690bd662894f851ee1bfd1e_id |
    | 4255c28503a1c96ed9a9127d1a21f992e636acd6_id |

  When I validate the current allowed association entities via API "/v1/cohorts/b4f9ddccc4c5c47a00541ee7c6d67fcb287316ce_id/studentCohortAssociations":
    | id                                                                                     |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_idc787af32ad98c7e7062619db99d233c32582d30a_id |
    | 080556ee7c69149d6143f309a758eec019ddfe11_idfd64bae1c7acb7605792ceba0cbbb83fe094aac7_id |
    | 326ca2d4fb11537d47479faa889526e86bee40ce_ida28607d8ab0b0b10bff12e90cba0fe9c13fdf566_id |
    | 18ebe03219dff01738c82645635997dbcb05205a_id18645899d59f4e3df0b0954dda091dfa6ea2525f_id |
    | 4d34e135d7af05424055c3798c8810d2330624f0_id4fef17fd717e91fb274353963f3a9a9619098141_id |
    | 126a8ceaaef2032ce0e8e3b1ea17ccd03cf869e2_ida9272a850a6868f9ccbeeb0a3c694e4e84a1b32d_id |
    | 7547f7c49431c8ea6a13e9381fd2dd3f22899278_id9853ba0a825be56d39dc9a81c035a1d8bb0686a3_id |
    | f24dad74523e919dcf96759b4f382df490bc9699_idf0262787bbbafd0dd085603491029197d8538e70_id |
    | 820642b49c9ca236d49c0384b98e9e6549047c34_id2e35b11bbbda60eb3bea9281e3bcca8f82d128a5_id |
    | f5d19abf71b888312e3807ae867a6c2c75196fbd_idac8dcc1402ede5702f5f5d817f78ba1fae7bcd3a_id |
  When I validate the allowed association entities via API "/v1/cohorts/b4f9ddccc4c5c47a00541ee7c6d67fcb287316ce_id/studentCohortAssociations/students":
    | id                                          |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | 080556ee7c69149d6143f309a758eec019ddfe11_id |
    | 326ca2d4fb11537d47479faa889526e86bee40ce_id |
    | 18ebe03219dff01738c82645635997dbcb05205a_id |
    | 4d34e135d7af05424055c3798c8810d2330624f0_id |
    | 126a8ceaaef2032ce0e8e3b1ea17ccd03cf869e2_id |
    | 7547f7c49431c8ea6a13e9381fd2dd3f22899278_id |
    | f24dad74523e919dcf96759b4f382df490bc9699_id |
    | 820642b49c9ca236d49c0384b98e9e6549047c34_id |
    | f5d19abf71b888312e3807ae867a6c2c75196fbd_id |
  # Programs
  When I validate the allowed association entities via API "/v1/programs/9cce6ea23864ee4870c8871e4c14ddecb6ab0fb0_id/staffProgramAssociations":
    | id                                          |
    | 7c71801b0e4d7fbca4993a8fa13e9490d3bc3d9c_id |
    | b519ce9ad9bd8588fb52997180888063a4ba9fce_id |
    | 75acfaf2376b53fe38f23840f14f2d937461faf6_id |
    | 8c163998fbc8ab004b7cf95e33fc5f6d14e87982_id |
    | 582d62a9f5f288e78defe672fc06228802d81f70_id |
    | 2cc6a6a3e5990518e6c196630873b6adc0736b86_id |
  When I validate the allowed association entities via API "/v1/programs/9cce6ea23864ee4870c8871e4c14ddecb6ab0fb0_id/staffProgramAssociations/staff":
    | id                                          |
    | 957463f7cd8d25526b35cbc215a78b752177bc13_id |
    | a909105eca7591d418b2697d72df27ca632e16f8_id |
    | 44faaf0a00179f2e1569e9bfb1adbda3d6397207_id |
    | 26e63d0460e9472f4fea94b962cd33a7e13d3c42_id |
    | 0d37e764a1d3a1a630502aa27421caef7bb66e71_id |
    | 63d4be8a233db1fd14676f1535fa21fe4c5dd466_id |
  When I validate there are "32" allowed association entities via API "/v1/programs/9cce6ea23864ee4870c8871e4c14ddecb6ab0fb0_id/studentProgramAssociations", some of them are:
    | id                                          |
    | a3a744519926ebf85bd1f669cd381ab3f02a8f83_id41e50d1c9c487ce16b391523118a1e6c7f97b4b6_id |
    | e4a3b2430710b17b9accb3b1f89a3082f3e60548_id1e7af41db1131632febcae69d8de62280e84da6b_id |
    | ca4ec31c7161966112d32034b76e3d9b80a02651_idd40773f3d054bcb1723c584d7ef3b411e877ccfd_id |
  When I validate there are "32" allowed association entities via API "/v1/programs/9cce6ea23864ee4870c8871e4c14ddecb6ab0fb0_id/studentProgramAssociations/students", some of them are:
    | id                                          |
    | a3a744519926ebf85bd1f669cd381ab3f02a8f83_id |
    | e4a3b2430710b17b9accb3b1f89a3082f3e60548_id |
    | ca4ec31c7161966112d32034b76e3d9b80a02651_id |
  # Sections 
  When I validate the allowed association entities via API "/v1/sections/eb8663fe6856b49684a778446a0a1ad33238a86d_id/gradebookEntries":
    | id                                                                                     |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id8d1a0d400d72873c2425529b91150e7c3f967db7_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id852c4a82822a7c66059123ae160d118660a62083_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id023e957aac164958c5188d2fac8b706808702956_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_idb0898361fdb3206069218ae1fca62162041a84e3_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_idd1ece9bb8efd6d1a9c056aaad89d1e8d11fe34e1_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id1fa3692f1a653c119c2be2d59e2e1fd6d15e5554_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id0adcb8715eca390ec3c3587e577e4708f23d39b0_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_ide2ca0904f73c91570c5ea058d7685c7f12e0d7b6_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id38beb6dd364803d45a72a6b9b7ce494479324a5e_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id2b3cf0a514c8c0a8c96960f9dbd364e8dd0152e3_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id4ff45056c082c8328dda2ec3c298fdb0d42f4c5c_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id5b148c5635918fbc1421c12e476505699c451d51_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_idc57ddf6c82bb684f1bcbd9ef1dbd0aef7c54c5b7_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_idde803259be0ce208572456c2315dd8007a0163be_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id1e0f700db933cda9dc1adf5f04d1204d2a9c2ddf_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_idb84a60d30c34987c884d686c7f3f1adba959b210_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id6874e70a7d53547c2e6c038a5c5d469b9d8802da_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id737be6e9f142cf208360d4b2f4173ac9373483b2_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_idf1d27a4c5ae5602985d718f15766a0ac92cb33d3_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_idbce001d4ef971b6ab2e390829dd49b81af30b3aa_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_idd3d7931bc74733979c18171f93073285816784c7_id |
  When I validate the current allowed association entities via API "/v1/sections/24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id/studentSectionAssociations":
    | id                                          |
    | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_idfbf78c465b21def53387e2aba9538b793a5944a7_id |
    | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id6069dcf4f63d35847e35c4da6f39f00ebbb5e612_id |
    | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id33ee33e252908a2e95eb8d0b4f85f96ffd4b0bae_id |
    | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id1ff6cfeb11316cc3374385a19fc50d13243eb39b_id |
    | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_idba94404b8e6a81d2664a19e4466113af75c6c8af_id |
    | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id74ada1a0d21fc43aaaf48c0915d33e74b3dc205f_id |
    | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_iddec4ba0e39b148b382ad107bd56fe83008f1e50d_id |
    | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_idb42a88ed8ef1d91b4fdbc24d99f2fe10eaa0758c_id |
    | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id78e7b442fdd45e90a678f9fb3cf27be14ab7e9f8_id |
    | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id65e75f7cb0804c8ab5d94e71c6005a193db2136e_id |
    | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id78469dfb63a222f45845f78e1a76542744696cfc_id |
  When I validate the allowed association entities via API "/v1/sections/24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id/studentSectionAssociations/students":
    | id                                          |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | aea1153839c7923a4d70ca9f5859dbc0895d629f_id |
    | e1d57e8f1f7f50656cdc11265c4cdf4729223b78_id |
    | 080556ee7c69149d6143f309a758eec019ddfe11_id |
    | 153e8258f62c711861d4bbc51653ee5fcea8e8ac_id |
    | 18fff957f4618b2a492b4393153e8ef43858153f_id |
    | 326ca2d4fb11537d47479faa889526e86bee40ce_id |
    | 38c2136429d32d2b69db9740e2e82c31c6b7bb7d_id |
    | 45dcc24a13157514bb2cb7e43356813146b258b8_id |
    | 4ca25ffca7a2275de6c4ceb97eb4f390d2576bde_id |
    | 4d34e135d7af05424055c3798c8810d2330624f0_id |
  When I validate the current allowed association entities via API "/v1/sections/e9b81633cba273dc9cc567d7f0f76a1c070c150d_id/teacherSectionAssociations":
    | id                                                                                     |
    | e9b81633cba273dc9cc567d7f0f76a1c070c150d_id2d275caf63e615e3d699f39cae4714084366024d_id |
  When I validate the allowed association entities via API "/v1/sections/e9b81633cba273dc9cc567d7f0f76a1c070c150d_id/teacherSectionAssociations/teachers":
    | id                                          |
    | 4b07dba2b6868c0827315b99ea94fc74c0f7c902_id |
  # Schools
  When I validate the allowed association entities via API "/v1/educationOrganizations/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/staffEducationOrgAssignmentAssociations/staff":
    | id                                          |
    | 8b6a31734ed43040f8a171d5d85e39176c543f22_id |
    | 00fb228365702c5fb03029216f3f057e174e3d6f_id |
    | a94da453a895a6d9ea23f884c3d232323a4acaae_id |
    | 0941af6d37f33cc9d690bd662894f851ee1bfd1e_id |
    | aec76b8572750483be1e0d1e7b00d03e3b07220d_id |
    | 143760f37839b2608d2c929ef26d30c900f6a434_id |
    | b56a781295310ed319be5070ead4930590a82619_id |
    | 1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id |
    | c64062927d0a93b6cf92a55bf812ffb2e613683a_id |
    | 4255c28503a1c96ed9a9127d1a21f992e636acd6_id |
    | e27fc445699aa38246a09373e6aeaa96981ea921_id |
    | 4b07dba2b6868c0827315b99ea94fc74c0f7c902_id |
    | 58d1e760fcdc1612b900ecb8359a6d8b3e49a5ee_id |
    | 6757c28005c30748f3bbda02882bf59bc81e0d71_id |

@student_staff_denied @student_expired_access
Scenario: Verify student access/deny through associations and expired entities
  #Given I log in to realm "Illinois Daybreak Students" using simple-idp as "studentLeader" "leader.m.sollars" with password "leader.m.sollars1234"
  Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "jstevenson" with password "jstevenson1234"
    And format "application/json"
    And I am using api version "v1"
    #POST custom expired entities to validate security policy for students
     And I POST and validate the following entities:
      | entityName                         | entityType                            | returnCode |
      | expiredTeacher                     | teacher                               | 201        |
      | expiredTeacherEdorgAssociation     | staffEducationOrganizationAssociation | 201        |
      | expiredTeacherSchoolAssociation    | teacherSchoolAssociation              | 201        |
      | expiredTeacherSectionAssociation   | teacherSectionAssociation             | 201        |
      | expiredStaff                       | staff                                 | 201        |
      | expiredStaffEdorgAssociation       | staffEducationOrganizationAssociation | 201        |
      | expiredStudent                     | student                               | 201        |
      | expiredStudentSchoolAssociation    | studentSchoolAssociation              | 201        |
      | expiredStudentSectionAssociation   | studentSectionAssociation             | 201        |
      | msollars.studentProgramAssociation | studentProgramAssociation             | 201        |
      | expiredStudentProgramAssociation   | studentProgramAssociation             | 201        |
      | expiredStudentCohortAssociation    | studentCohortAssociation              | 201        |

Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1"   
  When I validate that I am denied access to restricted endpoints via API:
    | uri                                                                                   | rc  |
   #| Teacher with expired association to student should return 403
    | /v1/teachers/2ff51e81ecbd9c4160a19be629d0ccb4cb529796_id                              | 403 |
   #| Teacher with no context to student should return                                      | 403 |
    | /v1/teachers/cf4646f47542eafa4e7dd9b3426e7889e157b114_id                              | 403 |
   #| Teacher that does not exist should return                                             | 404 |
    | /v1/teachers/this_teacher_dne                                                         | 404 |
   #| Staff with expired association to student should return                               | 403 |
    | /v1/staff/bfddb715a20bb2996b8769abfc1813d029bfdf29_id                                 | 403 |
   #| Staff with no context to student should return                                        | 403 |
    | /v1/staff/20b470cc8609718d36cd32d7d6258ef508529971_id                                 | 403 |
   #| Staff that does not exist should return                                               | 404 |
    | /v1/staff/this_staff_dne                                                              | 404 |
   #| Student with expired association to student should return                             | 403 |
    | /v1/students/b13887c5f555d6675d1f71de3b0fa6ad3b67f8aa_id                              | 403 |
   #| Student with no context to student should return                                      | 403 |
    | /v1/students/f9b25a057abd498c4a9ce367189d185f24b9681c_id                              | 403 |
   #| Student that does not exist should return                                             | 404 |
    | /v1/students/this_student_dne                                                         | 404 |
   #| Associations through expired programs should return                                          | 403 |
    | /v1/programs/b5d3344b30d0a3f8251aed87f307c387e069828e_id/staffProgramAssociations            | 403 |
    | /v1/programs/b5d3344b30d0a3f8251aed87f307c387e069828e_id/staffProgramAssociations/staff      | 403 |
    | /v1/programs/b5d3344b30d0a3f8251aed87f307c387e069828e_id/studentProgramAssociations          | 403 |
    | /v1/programs/b5d3344b30d0a3f8251aed87f307c387e069828e_id/studentProgramAssociations/students | 403 |
   #| Associations through programs that do not exist should return                                | 404 |
    | /v1/programs/this_program_dne                                                                | 404 |
   #| Associations through expired sections should be                                              | 403 |
    | /v1/sections/57277fceb3592d0c8f3faadcdd824690bc2b2586_id/gradebookEntries                    | 403 |
    | /v1/sections/57277fceb3592d0c8f3faadcdd824690bc2b2586_id/studentSectionAssociations          | 403 |
    | /v1/sections/0a96d039894bf5c9518584f11a646e53f1a9f4f6_id/studentSectionAssociations/students | 403 |
    | /v1/sections/9c5580ef4861ad2242e6ab444a52b359cb5fc516_id/teacherSectionAssociations          | 403 |
    | /v1/sections/8ae1caa952b3b22d9f58c26760aec903bed6d31b_id/teacherSectionAssociations/teachers | 403 |
   #| Associations through cohorts that do not exist should return                                 | 404 |
    | /v1/sections/this_section_dne                                                                | 404 |
  
  # Check that I only see CURRENT students in my current section
  When I navigate to GET "/v1/sections/eb8663fe6856b49684a778446a0a1ad33238a86d_id/studentSectionAssociations"
  Then I should be able to see <Fields> for the entity with ID <ID>:
    | ID                                                                                     | Fields      |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id9abdc5ad23afda9fca17a667c1af0f472000f2cb_id | AllSectAsoc |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id405cf08c5d34d2b30c4aeb47ebb4fe5634d28698_id | SecionIds   |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_idc255d211d6fd0048c69a400e860c6108aa3a94d6_id | SecionIds   |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_idc9a2ec8859473e2161b9472b0f927b25c6c8e356_id | SecionIds   |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id9c7fcb2f969a682fd280edab0668ea9cb64fe383_id | SecionIds   |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id7cd548ff4925dda74c79f32cf22ac59fc3e820d5_id | SecionIds   |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id97f35f9777d407f69b5ce69bb3b1c4866ee00265_id | SecionIds   |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_idd75b93563685713d03f62a7bcef6d27300494ccc_id | SecionIds   |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id07807a5d747cca0d4e96346b3f3994629335454d_id | SecionIds   |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_iddcf760efd488a4d7a921aa8a0a1274c25221a0d9_id | SecionIds   |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_idcfb32cf861dc89bab5a61b8a0a55c8f0f9820507_id | SecionIds   |
   #| Expired SSA ID should not be returned: 

  # Log in as Carmen Ortiz because she actually has expired associations to a cohort
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "carmen.ortiz" with password "carmen.ortiz1234"
  When I validate that I am denied access to restricted endpoints via API:
    | uri                                                                                        | rc  |
   #| Associations through expired cohorts should return                                         | 403 |
    | /v1/cohorts/72c0402e82f3a67db019d7736d423cc4a214db87_id/staffCohortAssociations            | 403 |
    | /v1/cohorts/72c0402e82f3a67db019d7736d423cc4a214db87_id/staffCohortAssociations/staff      | 403 |
    | /v1/cohorts/72c0402e82f3a67db019d7736d423cc4a214db87_id/studentCohortAssociations          | 403 |
    | /v1/cohorts/72c0402e82f3a67db019d7736d423cc4a214db87_id/studentCohortAssociations/students | 403 |
   #| Associations through cohorts that do not exist should return                               | 404 |
    | /v1/cohorts/this_cohort_dne                                                                | 404 |

