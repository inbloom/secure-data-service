@student
Feature: Student access to system

  Scenario Outline: User has access to public/global entities
    Given I am logged in using "carmen.ortiz" "carmen.ortiz1234" to realm "IL-Daybreak-Students"
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
    Given I am logged in using "carmen.ortiz" "carmen.ortiz1234" to realm "IL-Daybreak-Students"
    When I navigate to GET "/v1/<Entity>/<ids>"
    Then I should receive a return code of 403
  Examples:
    | Entity                                  | ids                                                                            |
    | disciplineIncidents                     | 0e26de79-222a-5e67-9201-5113ad50a03b                                           |
    | disciplineActions                       | db7f1d4b-9689-b2f4-9281-d88d65999423                                           |
    | studentDisciplineIncidentAssociations   | e0e99028-6360-4247-ae48-d3bb3ecb606a_id0e26de6c-225b-9f67-8e23-5113ad50a03b_id |

  Scenario Outline: Student cannot traverse to protected data through public entities
    Given I am logged in using "carmen.ortiz" "carmen.ortiz1234" to realm "IL-Daybreak-Students"
    When I navigate to GET "/v1/<Entity>/<ids>/<path>"
    Then I should receive a return code of 403
  Examples:
    | Entity                      | ids                                         | path                                            |
    | educationOrganizations      | d66fb6fd-691d-fde1-7f5f-efed78f7e1dc        | staffEducationOrgAssignmentAssociations         |
    | programs                    | 242b5d92-e69a-416e-b964-6ceb8756fd33_id     | studentProgramAssociations/students             |
    | courses                     | 43ee8275-de7b-4a02-8ecb-21d25a45db36        | courseTranscripts                               |
    | sessions                    | 8f391db1-dea5-4ace-becb-1f638f5a3f29        | studentAcademicRecords                          |
    | sections                    | b905a02e-30f7-43b7-94d5-58047985c00f        | studentSectionAssociations/students/attendances |
    | gradingPeriods              | b40a7eb5-dd74-4666-a5b9-5c3f4425f130        | reportCards                                     |
    | assessments                 | 25d9d83d11cfa02c687d4ca91e92969261a43d2d_id | studentAssessments                              |
    | learningObjectives          | dd9165f2-65fe-6d27-a8ec-bdc5f47757b7        | studentCompetencies                             |


  Scenario: Student cannot traverse to protected data through public entities security event
    Given I am logged in using "carmen.ortiz" "carmen.ortiz1234" to realm "IL-Daybreak-Students"
     And the sli securityEvent collection is empty
    When I navigate to GET "/v1/schools/888b2e17-0edb-4251-958b-8ac65093c9d3/studentSchoolAssociations/students"
    Then I should receive a return code of 403
     And I should see a count of "1" in the security event collection
     And I check to find if record is in sli db collection:
        | collectionName  | expectedRecordCount | searchParameter         | searchValue                              | searchType |
        | securityEvent   | 1                   | body.appId              | vavedRa9uB                               | string     |
        | securityEvent   | 1                   | body.className          | org.slc.sli.api.jersey.PostProcessFilter | string     |
        | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                              | string     |
        | securityEvent   | 1                   | body.targetEdOrgList    | THRACE                                   | string     |
    And "1" security event matching "Access Denied:url http.*/api/rest/v1.5/schools/888b2e17-0edb-4251-958b-8ac65093c9d3/studentSchoolAssociations/students is not accessible." should be in the sli db
    And "1" security event with field "body.actionUri" matching "http.*/api/rest/v1.5/schools/888b2e17-0edb-4251-958b-8ac65093c9d3/studentSchoolAssociations/students" should be in the sli db
     
  Scenario: Student cannot traverse to protected data through public entities security event checking
    Given I am logged in using "carmen.ortiz" "carmen.ortiz1234" to realm "IL-Daybreak-Students"
     And the sli securityEvent collection is empty
    When I navigate to GET "/v1/learningObjectives/dd9165f2-65fe-6d27-a8ec-bdc5f47757b7/studentCompetencies"
    Then I should receive a return code of 403
     And I should see a count of "1" in the security event collection
     And I check to find if record is in sli db collection:
        | collectionName  | expectedRecordCount | searchParameter         | searchValue                              | searchType |
        | securityEvent   | 1                   | body.appId              | vavedRa9uB                               | string     |
        | securityEvent   | 1                   | body.className          | org.slc.sli.api.security.pdp.UriMutator  | string     |
        | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                              | string     |
        | securityEvent   | 1                   | body.targetEdOrgList    | IL-DAYBREAK                              | string     |
     And "1" security event matching "Access Denied:url is not accessible to students or parents" should be in the sli db
     And "1" security event with field "body.actionUri" matching "http.*/api/rest/v1.5/learningObjectives/dd9165f2-65fe-6d27-a8ec-bdc5f47757b7/studentCompetencies" should be in the sli db
