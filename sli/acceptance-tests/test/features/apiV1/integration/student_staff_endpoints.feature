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
    | teachers                  | 0000000000000000000000000000000000000000_id |
    | teachers                  | 0000000000000000000000000000000000000000_id |
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
Scenario: Student has access to non-transitive associations through cohorts
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1" 
  When I validate the allowed association entities via API "/cohorts/{id}/staffCohortAssociations":
    | id                                                                                     |
    | 57277fceb3592d0c8f3faadcdd824690bc2b2586_id6e94350a7db678fd3f8fddb521a2a117728c832a_id |

  When I validate the allowed association entities via API "/cohorts/{id}/staffCohortAssociations/staff":
    | id                                                                                     |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id6f14f627d36449fd4ac6d98198c621f7eee82bc5_id |

  When I validate the allowed association entities via API "/cohorts/{id}/studentCohortAssociations":
    | id                                                                                     |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id35a7d3e403fa4702ee6db8cac8719eebf28a3e7e_id |
  When I validate the allowed association entities via API "/cohorts/{id}/studentCohortAssociations/students":
    | id                                          |
    | ec8b76883033432dc83b97e71fbc5bf881b4ccbb_id |

@wip @student_program_endpoints
Scenario: Student has access to non-transitive associations through programs
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1" 
  When I validate the allowed association entities via API "/programs/{id}/staffProgramAssociations":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/programs/{id}/staffProgramAssociations/staff":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/programs/{id}/studentProgramAssociations":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/programs/{id}/studentProgramAssociations/students":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |

@wip @student_section_endpoints
Scenario: Student has access to non-transitive associations through sections
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1"   
  When I validate the allowed association entities via API "/sections/{id}/gradebookEntries":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/sections/{id}/studentSectionAssociations":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/sections/{id}/studentSectionAssociations/students":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/sections/{id}/teacherSectionAssociations":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
  When I validate the allowed association entities via API "/sections/{id}/teacherSectionAssociations/teachers":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |

@wip @student_staff_denied
Scenario: Student has access to non-transitive associations through sections
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1"   
  When I validate that I am denied access to restricted endpoints via API:
    | uri                                                                                   | rc  |
   #| Teacher with expired association to student should be                                 | 403 |
    | /v1/teachers/{id}                                                                     | 403 |
   #| Teacher with no context to student should be                                          | 403 |
    | /v1/teachers/{id}                                                                     | 403 |
   #| Teacher that does not exist should be                                                 | 404 |
    | /v1/teachers/{id}                                                                     | 404 |
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
    | /v1/programs/{id}/staffProgramAssociations                                            | 403 |
    | /v1/programs/{id}/staffProgramAssociations/staff                                      | 403 |
    | /v1/programs/{id}/studentProgramAssociations                                          | 403 |
    | /v1/programs/{id}/studentProgramAssociations/students                                 | 403 |
   #| Associations through expired sections should be                                       | 403 |
    | /v1/sections/{id}/gradebookEntries                                                    | 403 |
    | /v1/sections/{id}/studentSectionAssociations                                          | 403 |
    | /v1/sections/{id}/studentSectionAssociations/students                                 | 403 |
    | /v1/sections/{id}/teacherSectionAssociations                                          | 403 |
    | /v1/sections/{id}/teacherSectionAssociations/teachers                                 | 403 |

