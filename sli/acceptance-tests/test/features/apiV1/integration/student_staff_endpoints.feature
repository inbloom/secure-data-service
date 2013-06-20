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
    | staff                     | 0000000000000000000000000000000000000000_id |
    | staff                     | 0000000000000000000000000000000000000000_id |
    | staff                     | 0000000000000000000000000000000000000000_id |
    | staff                     | 0000000000000000000000000000000000000000_id |
    | staff                     | 0000000000000000000000000000000000000000_id |
    | staff                     | 0000000000000000000000000000000000000000_id |
    | staff                     | 0000000000000000000000000000000000000000_id |
    | staff                     | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |
    | students                  | 0000000000000000000000000000000000000000_id |


@wip @student_cohort_endpoints
Scenario: Student has access to non-transitive associations through cohorts, programs, sections, schools
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1" 
  # 
  When I validate the allowed association entities via API "/cohorts/b4f9ddccc4c5c47a00541ee7c6d67fcb287316ce_id/staffCohortAssociations":
    | id                                                                                     |
    | 57277fceb3592d0c8f3faadcdd824690bc2b2586_id6e94350a7db678fd3f8fddb521a2a117728c832a_id |

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

@wip @student_section_endpoints
Scenario: Student has access to non-transitive associations through sections
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1"   
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
  When I validate the allowed association entities via API "/schools/{id}/staffEducationOrgAssignmentAssociations/staff":


@wip @student_staff_denied
Scenario: Student has access to non-transitive associations through sections
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1"   
  When I validate that I am denied access to restricted endpoints via API:
    | uri                                                                                   | rc  |
   #| Teacher with expired association to student should be                                 | 403 |
    | /v1/teachers/e27fc445699aa38246a09373e6aeaa96981ea921_id                              | 403 |
    | /v1/teachers/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id                              | 403 |
   #| Teacher with no context to student should be                                          | 403 |
    | /v1/teachers/cf4646f47542eafa4e7dd9b3426e7889e157b114_id                              | 403 |
   #| Teacher that does not exist should be                                                 | 404 |
    | /v1/teachers/dude_aliens                                                              | 404 |
   #| Staff with expired association to student should be                                   | 403 |
    | /v1/staff/{id}                                                                        | 403 |
   #| Staff with no context to student should be                                            | 403 |
    | /v1/staff/{id}                                                                        | 403 |
   #| Staff that does not exist should be                                                   | 404 |
    | /v1/staff/{id}                                                                        | 404 |
   #| Student with expired association to student should be                                 | 403 |
    | /v1/students/{id}                                                                     | 403 |
   #| Student with no context to student should be                                          | 403 |
    | /v1/students/{id}                                                                     | 403 |
   #| Student that does not exist should be                                                 | 404 |
    | /v1/students/{id}                                                                     | 404 |
   #| Associations through expired cohorts should be                                        | 403 |
    | /v1/cohorts/{id}/staffCohortAssociations                                              | 403 |
    | /v1/cohorts/{id}/staffCohortAssociations/staff                                        | 403 |
    | /v1/cohorts/{id}/studentCohortAssociations                                            | 403 |
    | /v1/cohorts/{id}/studentCohortAssociations/students                                   | 403 |
   #| Associations through expired programs should be                                       | 403 |
    | /v1/programs/b5d3344b30d0a3f8251aed87f307c387e069828e_id/staffProgramAssociations            | 403 |
    | /v1/programs/b5d3344b30d0a3f8251aed87f307c387e069828e_id/staffProgramAssociations/staff      | 403 |
    | /v1/programs/b5d3344b30d0a3f8251aed87f307c387e069828e_id/studentProgramAssociations          | 403 |
    | /v1/programs/b5d3344b30d0a3f8251aed87f307c387e069828e_id/studentProgramAssociations/students | 403 |
   #| Associations through expired sections should be                                       | 403 |
    | /v1/sections/{id}/gradebookEntries                                                    | 403 |
    | /v1/sections/{id}/studentSectionAssociations                                          | 403 |
    | /v1/sections/{id}/studentSectionAssociations/students                                 | 403 |
    | /v1/sections/{id}/teacherSectionAssociations                                          | 403 |
    | /v1/sections/{id}/teacherSectionAssociations/teachers                                 | 403 |

