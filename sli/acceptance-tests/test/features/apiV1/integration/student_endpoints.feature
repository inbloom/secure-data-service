@RALLY_US4305
@RALLY_US4306

Feature: As a student or staff I want to use apps that access the inBloom API

  Background: None

@student_endpoints
Scenario: Student has access to entities via API endpoints
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1"
 When I validate I have access to entities via the API access pattern "/v1/Entity/Id":
    | entity                    | id                                          |
    | students                  | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | parents                   | 5f8989384287747b1960d16edd95ff2bb318e3bd_id |
    | parents                   | 7f5b783a051b72820eab5f8188c45ade72869f0f_id |
    | parents                   | 5f8989384287747b1960d16edd95ff2bb318e3bd_id,7f5b783a051b72820eab5f8188c45ade72869f0f_id |
    | studentParentAssociations | 067198fd6da91e1aa8d67e28e850f224d6851713_idc43bbfa3df05d4fd2d78a9edfee8fd63fbcf495a_id  |
    | studentParentAssociations | 067198fd6da91e1aa8d67e28e850f224d6851713_ide2f8c24b3e1ab8ead6e134d661a464d0f90e4c8e_id  |

@wip @student_endpoints
Scenario: Student has access to non-transitive associations
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1" When I validate I have access to associationentities via the API access pattern "/v1/Entity/Id/Association":
    | entity                    | id                                          |
    | students                  | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | parents                   | 5f8989384287747b1960d16edd95ff2bb318e3bd_id |
    | parents                   | 7f5b783a051b72820eab5f8188c45ade72869f0f_id |
    | parents                   | 5f8989384287747b1960d16edd95ff2bb318e3bd_id,7f5b783a051b72820eab5f8188c45ade72869f0f_id |
    | studentParentAssociations | 067198fd6da91e1aa8d67e28e850f224d6851713_idc43bbfa3df05d4fd2d78a9edfee8fd63fbcf495a_id  |
    | studentParentAssociations | 067198fd6da91e1aa8d67e28e850f224d6851713_ide2f8c24b3e1ab8ead6e134d661a464d0f90e4c8e_id  |
