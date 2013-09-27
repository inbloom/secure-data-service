@RALLY_US4305
@RALLY_US4306

Feature: As a student or staff I want to use apps that access the inBloom API

  Background: None

@student_endpoints @shortcut
Scenario: Student has access to entities via API endpoints
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1"
 Then I validate I have access to entities via the API access pattern "/v1/Entity/Id":
    | entity                                   | id                                          |
    | students                                 | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | parents                                  | 5f8989384287747b1960d16edd95ff2bb318e3bd_id |
    | parents                                  | 7f5b783a051b72820eab5f8188c45ade72869f0f_id |
    | parents                                  | 5f8989384287747b1960d16edd95ff2bb318e3bd_id,7f5b783a051b72820eab5f8188c45ade72869f0f_id |
    | studentParentAssociations                | 067198fd6da91e1aa8d67e28e850f224d6851713_idc43bbfa3df05d4fd2d78a9edfee8fd63fbcf495a_id  |
    | studentParentAssociations                | 067198fd6da91e1aa8d67e28e850f224d6851713_ide2f8c24b3e1ab8ead6e134d661a464d0f90e4c8e_id  |
    | staff                                    | 63d4be8a233db1fd14676f1535fa21fe4c5dd466_id |
    | staff                                    | 4255c28503a1c96ed9a9127d1a21f992e636acd6_id |
    | staff                                    | 143760f37839b2608d2c929ef26d30c900f6a434_id |
    | staff                                    | 8b6a31734ed43040f8a171d5d85e39176c543f22_id |
    | staffCohortAssociations                  | ee3d3035994b88d465553f38a265fff4597946d2_id |
    | staffProgramAssociations                 | 8c163998fbc8ab004b7cf95e33fc5f6d14e87982_id |
    | staffEducationOrgAssignmentAssociations  | b1877c4d0d8e081e87f8af9a933c6d46aea5d9ad_id |
    | teacherSectionAssociations               | eb8663fe6856b49684a778446a0a1ad33238a86d_idc2e898df96ee2cb40e24e1986a2d1cbdf053184f_id |
    | teacherSchoolAssociations                | 8495e720e4f1261f3845aeb1f499ec40359669a5_id |
    | calendarDates                           | e00dc4fb9d6be8372a549dea899fe1915a598c5c_id |
    | calendarDates                           | b6f70d30b51a569610b8f882c8fa3a2d2eefbee1_id |

  Then I validate that I am denied access to restricted endpoints via API:
    | uri                                                                                                                   | rc           |
    | /v1/staff/e40ee9041a7159c62867f63bf4da581ba9fc3dc7_id                                                                 | 403          |
    | /v1/staffCohortAssociations/e1b4e5e0e8c1d7b84d7a8eb72958ad0a53e7ef77_id                                               | 403          |
    # student association has expired
    | /v1/staffProgramAssociations/d7fa74360c0fa06259c37fb67b07b039211c72de_id                                              | 403          |
    | /v1/teacherSectionAssociations/57277fceb3592d0c8f3faadcdd824690bc2b2586_id98f3f6d2ee97b4e0bc1aca33f88911393446c017_id | 403          |
    # staff association has expired
    | /v1/staffProgramAssociations/fa47e994944a53bc0b23a7f16fc5843149937b94_id                                              | 403          |
    | /v1/staffEducationOrgAssignmentAssociations/a9c8dfab5f003151f52b825be529bead1ae50564_id                               | 403          |

@student_endpoints
Scenario: Student has access to non-transitive associations
Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  And I am using api version "v1" 
  When I validate the allowed association entities via API "/v1/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentSectionAssociations":
    | id                                                                                     |
    | 57277fceb3592d0c8f3faadcdd824690bc2b2586_id6e94350a7db678fd3f8fddb521a2a117728c832a_id |
    | eb8663fe6856b49684a778446a0a1ad33238a86d_id9abdc5ad23afda9fca17a667c1af0f472000f2cb_id |
    | 24cdeb47d5ccfee1536dd8f6a8951baea76b82f3_id33ee33e252908a2e95eb8d0b4f85f96ffd4b0bae_id |
    | 0a96d039894bf5c9518584f11a646e53f1a9f4f6_idad60894d8bea24c6903eb973285fb9f5f39dfa18_id |
    | e9b81633cba273dc9cc567d7f0f76a1c070c150d_id84ac5a840b7d61ed3071e5e44e14f9b40192654f_id |
    | 9c5580ef4861ad2242e6ab444a52b359cb5fc516_id24ef2cc415667d2fd9d22047f25691f0aa671569_id |
    | 8ae1caa952b3b22d9f58c26760aec903bed6d31b_id874418051ec05e918f88b69ea4729e5db94702dd_id |
    | 2982f5d3840b0a46bf152c7b7243c0db8dda694f_id06f4aa0f6d84ae7ab290709fc348754cbd232cb5_id |
  When I validate the allowed association entities via API "/v1/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentProgramAssociations":
    | id                                                                                     |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id6f14f627d36449fd4ac6d98198c621f7eee82bc5_id |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_ide16cd0618778dd1f72935f6ecb54519db428a97f_id |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_idbee47dcc9085b8a8193dbaa7cc2d39730d19b059_id |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_idef2480c472f48a31dfa1980d4a25e3fe2466d1d9_id |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id6417d8a91387ee974e410e05a47457413aa82700_id |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id15841d8ee9f6c3f2e60b9087255e5fab536ebd37_id |
  When I validate the allowed association entities via API "/v1/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentCohortAssociations":
    | id                                                                                     |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id3ae975b387f2af8761a354abb13dacd04a6b748b_id |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id35a7d3e403fa4702ee6db8cac8719eebf28a3e7e_id |
  When I validate the allowed association entities via API "/v1/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentSchoolAssociations":
    | id                                          |
    | 139f77e73ae5f1970c5d884d4d2b90367361d1f6_id |
    | ec8b76883033432dc83b97e71fbc5bf881b4ccbb_id |

  Scenario: I check the response to uris with query parameters
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    And I am accessing data about myself, "matt.sollars"
    Then I verify the following response body fields in "/schools?parentEducationAgencyReference=1b223f577827204a1c7e9c851dba06bea6b031fe_id&sortBy=stateOrganizationId":
      | field                                                 | value                                       |
      | 0.stateOrganizationId                                 | Daybreak Central High                       |
      | 1.stateOrganizationId                                 | East Daybreak Junior High                   |
      | 2.stateOrganizationId                                 | South Daybreak Elementary                   |
    Then I verify the following response body fields in "/schools?parentEducationAgencyReference=1b223f577827204a1c7e9c851dba06bea6b031fe_id&sortBy=stateOrganizationId&limit=1&offset=1":
      | field                                                 | value                                       |
      | 0.stateOrganizationId                                 | East Daybreak Junior High                   |
    Then I verify the following response body fields in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentSectionAssociations?beginDate=2013-09-09&sortBy=beginDate":
      | field                                                 | value                                       |
      | 0.beginDate                                           | 2013-09-09                                  |
