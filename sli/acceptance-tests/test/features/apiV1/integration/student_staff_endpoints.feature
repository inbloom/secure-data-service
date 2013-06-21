@RALLY_US5470

Feature: As a student I want to use apps that access my associated Staff, Teachers, and Students via the inBloom API

Background: None

@wip @student_staff_endpoints
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



@wip @student_cohort_endpoints
Scenario: Student has access to non-transitive associations through cohorts, programs, sections, schools
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1" 
  # Cohorts
  When I validate the allowed association entities via API "/cohorts/b4f9ddccc4c5c47a00541ee7c6d67fcb287316ce_id/staffCohortAssociations":
    | id                                                                                     |
    | 57277fceb3592d0c8f3faadcdd824690bc2b2586_id6e94350a7db678fd3f8fddb521a2a117728c832a_id |
   And I validate the association is not expired
   
  When I validate the allowed association entities via API "/cohorts/b4f9ddccc4c5c47a00541ee7c6d67fcb287316ce_id/staffCohortAssociations/staff":
    | id                                                                                     |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id6f14f627d36449fd4ac6d98198c621f7eee82bc5_id |

  When I validate the allowed association entities via API "/cohorts/b4f9ddccc4c5c47a00541ee7c6d67fcb287316ce_id/studentCohortAssociations":
    | id                                                                                     |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id35a7d3e403fa4702ee6db8cac8719eebf28a3e7e_id |
  When I validate the allowed association entities via API "/cohorts/b4f9ddccc4c5c47a00541ee7c6d67fcb287316ce_id/studentCohortAssociations/students":
    | id                                          |
    | ec8b76883033432dc83b97e71fbc5bf881b4ccbb_id |
  # Programs
  When I validate the allowed association entities via API "/programs/612bcb0398256023d2415b04b7706d56175c10bc_id/staffProgramAssociations":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/programs/612bcb0398256023d2415b04b7706d56175c10bc_id/staffProgramAssociations/staff":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/programs/612bcb0398256023d2415b04b7706d56175c10bc_id/studentProgramAssociations":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/programs/612bcb0398256023d2415b04b7706d56175c10bc_id/studentProgramAssociations/students":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  # Sections 
  When I validate the allowed association entities via API "/sections/eb8663fe6856b49684a778446a0a1ad33238a86d_id/gradebookEntries":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/sections/24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id/studentSectionAssociations":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/sections/24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id/studentSectionAssociations/students":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/sections/e9b81633cba273dc9cc567d7f0f76a1c070c150d_id/teacherSectionAssociations":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/sections/e9b81633cba273dc9cc567d7f0f76a1c070c150d_id/teacherSectionAssociations/teachers":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  # Schools
  When I validate the allowed association entities via API "/schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/staffEducationOrgAssignmentAssociations/staff":


@wip @student_staff_denied
Scenario: Student has access to non-transitive associations through sections
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1"   
  When I validate that I am denied access to restricted endpoints via API:
    | uri                                                                                   | rc  |
   #| Teacher with expired association to student should return                             | 403 |
    | /v1/teachers/e27fc445699aa38246a09373e6aeaa96981ea921_id                              | 403 |
    | /v1/teachers/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id                              | 403 |
   #| Teacher with no context to student should return                                      | 403 |
    | /v1/teachers/cf4646f47542eafa4e7dd9b3426e7889e157b114_id                              | 403 |
   #| Teacher that does not exist should return                                             | 404 |
    | /v1/teachers/this_teacher_dne                                                         | 404 |
   #| Staff with expired association to student should return                               | 403 |
    | /v1/staff/{id}                                                                        | 403 |
   #| Staff with no context to student should return                                        | 403 |
    | /v1/staff/{id}                                                                        | 403 |
   #| Staff that does not exist should return                                               | 404 |
    | /v1/staff/this_staff_dne                                                              | 404 |
   #| Student with expired association to student should return                             | 403 |
    | /v1/students/{id}                                                                     | 403 |
   #| Student with no context to student should return                                      | 403 |
    | /v1/students/{id}                                                                     | 403 |
   #| Student that does not exist should return                                             | 404 |
    | /v1/students/{id}                                                                     | 404 |
   #| Associations through expired cohorts should return                                    | 403 |
    | /v1/cohorts/{id}/staffCohortAssociations                                              | 403 |
    | /v1/cohorts/{id}/staffCohortAssociations/staff                                        | 403 |
    | /v1/cohorts/{id}/studentCohortAssociations                                            | 403 |
    | /v1/cohorts/{id}/studentCohortAssociations/students                                   | 403 |
   #| Associations through cohorts that do not exist should return                          | 404 |
    | /v1/cohorts/this_cohort_dne                                                           | 404 |
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

