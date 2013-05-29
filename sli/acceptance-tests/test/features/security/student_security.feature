@student
Feature: Student access to system

  Scenario Outline: User has access to public/global entities
    Given I am logged in using "carmen.ortiz" "carmen.ortiz1234" to realm "IL-Student"
    When I navigate to GET "/v1/<Entity>/<ids>"
    Then I should receive a return code of 200
  Examples:
    | Entity                      | ids                                                                                     |
    | schools                     | 888b2e17-0edb-4251-958b-8ac65093c9d3,67ce204b-9999-4a11-bfea-000000000009               |
    | educationOrganizations      | d66fb6fd-691d-fde1-7f5f-efed78f7e1dc,12b099d7-e8f4-4e5e-90a0-64ede16f3254               |
    | programs                    | 242b5d92-e69a-416e-b964-6ceb8756fd33_id,9b8c3aab-8fd5-11e1-86ec-0021701f543f_id         |
    | courses                     | 43ee8275-de7b-4a02-8ecb-21d25a45db36,92d6d5a0-852c-45f4-907a-912752831772               |
    | courseOfferings             | 9c5299eb-a958-4a60-a9e6-7c1232a40c72,9ce4f11b-ffd5-4349-8e9c-8304d8434192               |
    | sessions                    | 8f391db1-dea5-4ace-becb-1f638f5a3f29,5c59a6a1-9adb-4588-8e0d-313b5966b50a               |
    | sections                    | b905a02e-30f7-43b7-94d5-58047985c00f_id,a97f3e67-3f6c-4ddd-bbbf-14e6d756bd76_id         |
    | gradingPeriods              | b40a7eb5-dd74-4666-a5b9-5c3f4425f130,09f91102-1122-40fa-afc2-98ae32abc222               |
    | graduationPlans             | 023b1d9a-ed06-4f56-af97-gradplan1112,04e3a07a-a95f-4ac8-88f3-gradplan1115               |
    | assessments                 | 25d9d83d11cfa02c687d4ca91e92969261a43d2d_id,49da176bc1b8025d5a6c2855cebfec421a418541_id |
    | learningObjectives          | dd9165f2-65fe-6d27-a8ec-bdc5f47757b7,df9165f2-653e-df27-a86c-bfc5f4b7577d               |
    | learningStandards           | dd9165f2-65fe-7d27-a8ec-bdc5f77757f7,df9165f2-653e-8f27-a86c-bec5f4b75779               |
    | competencyLevelDescriptor   | df9165f2-65fe-de27-a82c-bfc5f4b75789,df916ff2-65fe-de24-a82c-bfc5f4b75789               |
    | studentCompetencyObjectives | 313db42ad65b911b0897d8240e26ca4b50bddb5e_id,313db42ad65b911b0897d8240e26ca4b50bddb5e_id |

  Scenario Outline: Student can never see these
    Given I am logged in using "carmen.ortiz" "carmen.ortiz1234" to realm "IL-Student"
    When I navigate to GET "/v1/<Entity>/<ids>"
    Then I should receive a return code of 403
  Examples:
    | Entity                                  | ids                                                                            |
    | disciplineIncidents                     | 0e26de79-222a-5e67-9201-5113ad50a03b                                           |
    | disciplineActions                       | db7f1d4b-9689-b2f4-9281-d88d65999423                                           |
    | staffCohortAssociations                 | 8fef446f-fc63-15f9-8606-0b85086c07d8                                           |
    | staffEducationOrgAssignmentAssociations | 05e3de47-9e41-c048-a572-3eb4c7ee9095                                           |
    | staffProgramAssociations                | 04223945-b773-425c-8173-af090a960603                                           |
    | studentDisciplineIncidentAssociations   | e0e99028-6360-4247-ae48-d3bb3ecb606a_id0e26de6c-225b-9f67-8e23-5113ad50a03b_id |
    | teacherSchoolAssociations               | 1a72521b-7bed-890a-d574-1d729a379528                                           |
    | teacherSectionAssociations              | 706ee3be-0dae-4e98-9525-f564e05aa388_id29d58f86-5fab-4926-a9e2-e4076fe27bb3_id |