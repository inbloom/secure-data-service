@student_validator
Feature: Verify integrety of Student Validation Logic
  I want to make damn sure that student validation logic is working as intended, and that others do not break it.

Scenario: Validators return proper return codes on multi-ID requests for Carmen Ortiz
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "carmen.ortiz" with password "carmen.ortiz1234"
  And format "application/json"
  When I make API calls for multiple IDs in accordance to the following table:
    | Path                             | GoodId                                                                                 | BadId                                                                                  |
    | /studentSectionAssociations/@ids | 98c2fbb9fffbabb9c8bbbcc8de9d5f7db42dff5d_id5da26ce302b6c25b2cd7d4cd3b73f1363f32a195_id | 08e02a71b7ff453134a35cbd4374dac31041ced1_id57a288ab3f4399bcefdd1b2f8668e1545c0678cc_id |
    | /attendances/@ids                | 69a338d4c77f47dbb0edb12878c751bde7622505_id                                            | cd14890af69207e6d9433f0962107eb0c96a1748_id                                            |
    | /cohorts/@ids                    | 72c0402e82f3a67db019d7736d423cc4a214db87_id                                            | 4711a3d63401b22260d9ed17313b9fc301f02c6f_id                                            |
    | /courseTranscripts/@ids          | 883d1a1bcf9606cfc38774d205b46b5ad17657a8_id                                            | a7ab1449d7291f6409abac0cb24d5faa62a90074_id                                            |
    | /gradebookEntries/@ids           | 1be05ac2613d16696756ca4205f169c5624df927_idd062287c536b92e20ae081acf661f30f7661e682_id | 88bb1ad61323121c4939db296f4d444094ad5563_ida09195b53390343f89c72de0912696f48f8080b5_id |
    | /grades/@ids                     | 581786d185ce6b954c17be3c116a025410a28f74_id97c4517dc01cc832231a07805ce8ee0035c6904c_id | 2f38a01d3ce555cfcdc637aa02d3596de1e27574_idc7af9f9859103f140c0d3638751f0303995675e8_id |
    | /parents/@ids                    | 2d6638adf22232b9af30b03ce9e84e707f4cf501_id                                            | ac9d23542b310939801dec4d29cfddda7765353b_id                                            |
    | /reportCards/@ids                | 581786d185ce6b954c17be3c116a025410a28f74_iddbc9d23b4a2b3ae57d05c94c81ab4d579c68663a_id | 2f38a01d3ce555cfcdc637aa02d3596de1e27574_id541b51e428f2371374525eab0cc8743faf324ded_id |
    | /staff/@ids                      | 2cb881fd1ff848815dc394103b3abbc1b768469e_id                                            | 6757c28005c30748f3bbda02882bf59bc81e0d71_id                                            |
    | /staffEducationOrgAssignmentAssociations/@ids |  f7371a2a35e09e879b04f8985bc77f23f99aa5e6_id                              | 70ebd066ba83fc4fbe7396a52f539ee6d87e0b26_id                                            |
    | /staffProgramAssociations/@ids   | da4c6adb39570a6b19c30a741ca24584f023fc4a_id                                            | 920b076c32a2c93ecc0321cf37ea61be72a9f7a4_id                                            |
    | /studentAcademicRecords/@ids     | 581786d185ce6b954c17be3c116a025410a28f74_iddac01c5702e0af232c4a34751c9c6f54aa966d4e_id | 2f38a01d3ce555cfcdc637aa02d3596de1e27574_id51a05e3fc152d6180c97370b1cdc4de367b1dce7_id |
    | /studentAssessments/@ids         | dac3e2b4f5bd1e7d02f22fb23b26fdb8ba94ab7f_id                                            | 2614b8f1d4c23aaa654e0250217aeceb21aa6a92_id                                            |
    | /studentCohortAssociations/@ids  | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id99f774601fb35844fa557ed82cfd97091d40c21f_id | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id044d34cf5f46b5b7d7b5c54c9bfed3fa9e7279e6_id |
    | /studentCompetencies/@ids        | eec3a693a985d3d2ef25d159b583f689083e9429_id                                            | 22d650d7d2c2d479b28348856359732591e1c4bf_id                                            |
    | /studentGradebookEntries/@ids    | 0a36c36d52e68fcf72afef1ad6a4795fc13a75c7_id                                            | 54631882c95b478b13a723865be31ced851386f3_id                                            |
    | /studentParentAssociations/@ids  | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idc72d8118cb267b19c3716bf36e0d00a6f8649853_id | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_idf2f0a1ddc141a58169b3c586e78ae6ba8d44e8ee_id |
    | /studentProgramAssociations/@ids | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id7614372c4e0284e7e8ab0782ddffdc5bba9dbd7e_id | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id349189276a34caddc20d2fd6a2e5e6815710b896_id |
    | /students/@ids                   | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                            | 695f276632ce13007c67689d74dc1c83537f30f6_id                                            |
    | /studentSchoolAssociations/@ids  | 5473d560c39002a09d650c618768b60adc6aadab_id                                            | ea66a6ef0e3c2dd61701e9fa5bcf108a631a9bcb_id                                            |
    | /teachers/@ids                   | fe472294f0e40fd428b1a67b9765360004562bab_id                                            | 6757c28005c30748f3bbda02882bf59bc81e0d71_id                                            |
    | /teacherSchoolAssociations/@ids  | fd64eb0e7ddcef2a651b2d0ad0bfbbc85c62a4ce_id | 8495e720e4f1261f3845aeb1f499ec40359669a5_id |
    | /teacherSectionAssociations/@ids | ecb5131a66ffff44c3169acbdb9f1242e8384b13_id36678eb42429bf2cea4817c45f7bff5bb841c0a4_id | e9b81633cba273dc9cc567d7f0f76a1c070c150d_id2d275caf63e615e3d699f39cae4714084366024d_id |
    | /yearlyAttendances/@ids          | 69a338d4c77f47dbb0edb12878c751bde7622505_id                                            | cd14890af69207e6d9433f0962107eb0c96a1748_id                                            |
#  multi-part url
     | /educationOrganizations/@ids/staffEducationOrgAssignmentAssociations | a13489364c2eb015c219172d561c62350f0453f3_id                              | 352e8570bd1116d11a72755b987902440045d346_id                                                           |
     | /educationOrganizations/@ids/staffEducationOrgAssignmentAssociations/staff | a13489364c2eb015c219172d561c62350f0453f3_id                              | 352e8570bd1116d11a72755b987902440045d346_id                                                      |
     | /schools/@ids/teacherSchoolAssociations  | a13489364c2eb015c219172d561c62350f0453f3_id                              | 352e8570bd1116d11a72755b987902440045d346_id                                                           |
     | /schools/@ids/teacherSchoolAssociations/teachers | a13489364c2eb015c219172d561c62350f0453f3_id                              | 352e8570bd1116d11a72755b987902440045d346_id                                                           |
     | /sections/@ids/gradebookEntries          | ecb5131a66ffff44c3169acbdb9f1242e8384b13_id                                                          | 8d9ad6c3b870e8775016fff99fbd9c74920de8d5_id                                                            |
     | /sections/@ids/studentSectionAssociations/students | ecb5131a66ffff44c3169acbdb9f1242e8384b13_id                                                | 8d9ad6c3b870e8775016fff99fbd9c74920de8d5_id                                                            |
     | /sections/@ids/teacherSectionAssociations/teachers | ecb5131a66ffff44c3169acbdb9f1242e8384b13_id                                                | 8d9ad6c3b870e8775016fff99fbd9c74920de8d5_id                                                            |
     | /sections/@ids/studentSectionAssociations | ecb5131a66ffff44c3169acbdb9f1242e8384b13_id                                                | 8d9ad6c3b870e8775016fff99fbd9c74920de8d5_id                                                            |
     | /sections/@ids/teacherSectionAssociations | ecb5131a66ffff44c3169acbdb9f1242e8384b13_id                                                | 8d9ad6c3b870e8775016fff99fbd9c74920de8d5_id                                                            |
     | /students/@ids/studentAssessments/assessments | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/attendances               | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/studentCohortAssociations/cohorts | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/courseTranscripts         | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/studentParentAssociations/parents | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/studentProgramAssociations/programs | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/reportCards               | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/studentAcademicRecords    | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/studentAssessments        | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/studentCohortAssociations | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/studentGradebookEntries   | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/studentParentAssociations | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/studentProgramAssociations | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/studentSchoolAssociations | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/studentSectionAssociations | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /studentSectionAssociations/@ids/grades  | ecb5131a66ffff44c3169acbdb9f1242e8384b13_ida0403a489062fb836e0640fa2f5e3d4cf6a31d1a_id               | ecb5131a66ffff44c3169acbdb9f1242e8384b13_id3e905f4bb054ed1440cb81c4bbeeb0a4d1d25dfc_id               |
     | /studentSectionAssociations/@ids/studentCompetencies | ecb5131a66ffff44c3169acbdb9f1242e8384b13_ida0403a489062fb836e0640fa2f5e3d4cf6a31d1a_id               | ecb5131a66ffff44c3169acbdb9f1242e8384b13_id3e905f4bb054ed1440cb81c4bbeeb0a4d1d25dfc_id               |
     | /students/@ids/yearlyAttendances         | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /programs/@ids/staffProgramAssociations  | b5d3344b30d0a3f8251aed87f307c387e069828e_id                                                               | 36980b1432275aae32437bb367fb3b66c5efc90e_id                                       |
     | /programs/@ids/staffProgramAssociations/staff | b5d3344b30d0a3f8251aed87f307c387e069828e_id                                                               | 36980b1432275aae32437bb367fb3b66c5efc90e_id                                       |
     | /programs/@ids/studentProgramAssociations | b5d3344b30d0a3f8251aed87f307c387e069828e_id                                                               | 36980b1432275aae32437bb367fb3b66c5efc90e_id                                       |
     | /programs/@ids/studentProgramAssociations/students |  b5d3344b30d0a3f8251aed87f307c387e069828e_id                                                               | 36980b1432275aae32437bb367fb3b66c5efc90e_id                                       |
     | /staffEducationOrgAssignmentAssociations/@ids/educationOrganizations | f7371a2a35e09e879b04f8985bc77f23f99aa5e6_id                              | 70ebd066ba83fc4fbe7396a52f539ee6d87e0b26_id                                            |
     | /staffEducationOrgAssignmentAssociations/@ids/staff | f7371a2a35e09e879b04f8985bc77f23f99aa5e6_id                              | 70ebd066ba83fc4fbe7396a52f539ee6d87e0b26_id                                            |
     | /staffProgramAssociations/@ids/programs  | da4c6adb39570a6b19c30a741ca24584f023fc4a_id                                            | 920b076c32a2c93ecc0321cf37ea61be72a9f7a4_id                                            |
     | /staffProgramAssociations/@ids/staff     | da4c6adb39570a6b19c30a741ca24584f023fc4a_id                                            | 920b076c32a2c93ecc0321cf37ea61be72a9f7a4_id                                            |
     | /studentAcademicRecords/@ids/courseTranscripts | e1ddd4b5c0c531a734135ecd461b33cab842c18c_id074f8af9afa35d4bb10ea7cd17794174563c7509_id               | 2f38a01d3ce555cfcdc637aa02d3596de1e27574_id51a05e3fc152d6180c97370b1cdc4de367b1dce7_id               |
     | /studentAssessments/@ids/assessments     | ae538b129c770f205c3bd3e35d0efb4b9e6f6551_id                                                          | 36d55966f9410fef4e35390eb5d93e32b9f05449_id                                                          |
     | /studentAssessments/@ids/students        | ae538b129c770f205c3bd3e35d0efb4b9e6f6551_id                                                          | 36d55966f9410fef4e35390eb5d93e32b9f05449_id                                                          |
     | /studentCohortAssociations/@ids/cohorts  | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id99f774601fb35844fa557ed82cfd97091d40c21f_id               | 4d34e135d7af05424055c3798c8810d2330624f0_idda378859e69dd733400737ceb82a8096a653a760_id               |
     | /studentCohortAssociations/@ids/students | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id99f774601fb35844fa557ed82cfd97091d40c21f_id               | 4d34e135d7af05424055c3798c8810d2330624f0_idda378859e69dd733400737ceb82a8096a653a760_id               |
     | /studentCompetencies/@ids/reportCards    | d447b1690a4eec7339ce5d509f1da2c7315aafc3_id                                                          | fc408d8b2c345ee30ba5ee68f85da32f9cacdeb8_id                                                          |
     | /studentParentAssociations/@ids/parents  | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idc72d8118cb267b19c3716bf36e0d00a6f8649853_id               | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_idf2f0a1ddc141a58169b3c586e78ae6ba8d44e8ee_id               |
     | /studentParentAssociations/@ids/students | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idc72d8118cb267b19c3716bf36e0d00a6f8649853_id               | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_idf2f0a1ddc141a58169b3c586e78ae6ba8d44e8ee_id               |
     | /studentProgramAssociations/@ids/programs | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idd03d44b47418aaa645c62e0cfe892eeb049e73b4_id               | 4d34e135d7af05424055c3798c8810d2330624f0_id494237474c4986c098c84b1fc02ec645ff834e27_id               |
     | /studentProgramAssociations/@ids/students | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id4ac491e55d7f84870828b2b10dba2be1efb3a7f2_id               | 4d34e135d7af05424055c3798c8810d2330624f0_id494237474c4986c098c84b1fc02ec645ff834e27_id               |
     # studentProgram for other current student
     | /studentProgramAssociations/@ids/students | 1d0e46ac239d91ec943e3c43c17edb9d343a9347_id121aebac562e8edf74439c0bb1edac559e701782_id               | 4d34e135d7af05424055c3798c8810d2330624f0_id494237474c4986c098c84b1fc02ec645ff834e27_id               |
     | /students/@ids/studentSchoolAssociations/schools | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /students/@ids/studentSectionAssociations/sections | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     | /studentSchoolAssociations/@ids/schools  | 5473d560c39002a09d650c618768b60adc6aadab_id                                                          | 90edded75ff09a5fad3371df3be9289ca2ae718a_id                                                          |
     | /studentSchoolAssociations/@ids/students | 932111c8421d0eb277d8ce5238a0272ec56f9e38_id                                                          | 90edded75ff09a5fad3371df3be9289ca2ae718a_id                                                          |
     | /teacherSchoolAssociations/@ids/schools  | fd64eb0e7ddcef2a651b2d0ad0bfbbc85c62a4ce_id | 8495e720e4f1261f3845aeb1f499ec40359669a5_id |
     | /teacherSchoolAssociations/@ids/teachers | fd64eb0e7ddcef2a651b2d0ad0bfbbc85c62a4ce_id | 8495e720e4f1261f3845aeb1f499ec40359669a5_id |
     | /teacherSectionAssociations/@ids/sections | ecb5131a66ffff44c3169acbdb9f1242e8384b13_id36678eb42429bf2cea4817c45f7bff5bb841c0a4_id | e9b81633cba273dc9cc567d7f0f76a1c070c150d_id2d275caf63e615e3d699f39cae4714084366024d_id |
     | /teacherSectionAssociations/@ids/teachers | ecb5131a66ffff44c3169acbdb9f1242e8384b13_id36678eb42429bf2cea4817c45f7bff5bb841c0a4_id | e9b81633cba273dc9cc567d7f0f76a1c070c150d_id2d275caf63e615e3d699f39cae4714084366024d_id |
     | /studentSectionAssociations/@ids/students | 6967fe8c89198c9f2a64f2df1e60cd7677e62c31_idc75f6630245b4942ae6870a5b6466b51e322ea8a_id               | 828b5629e48f924f2c090b5fc92a08307a4e5d85_id9176177b445474ddcc0de79c118a499849192cbf_id               |
     # data doesn't contain valid courseTranscript / courses asssociations
     #| /students/@ids/courseTranscripts/courses | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                                          | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                                          |
     #| /courseTranscripts/@ids/courses          | 66855042298036c199ee39628625be212d682049_id                               | 000c61fc14cdf8a2def80d7aa81a0e425cb76925_id |
  When I request the Good ID, I should be allowed
  When I request the Bad ID, I should be denied
  When I request both IDs, I should be denied

Scenario: Validators return proper return codes on multi-ID requests for Matt Sollars
  # Carman doesn't have a valid cohort, must test with Matt
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "student.m.sollars" with password "student.m.sollars1234"
  And format "application/json"
  When I make API calls for multiple IDs in accordance to the following table:
      | Path                                        | GoodId                                      | BadId                                        |
      | /staffCohortAssociations/@ids               | ee3d3035994b88d465553f38a265fff4597946d2_id | 2878f8c43530ccb0d5ad94ffaadad6e31953daf6_id  |
      | /staffCohortAssociations/@ids/cohorts       | ee3d3035994b88d465553f38a265fff4597946d2_id | 2878f8c43530ccb0d5ad94ffaadad6e31953daf6_id  |
      | /staffCohortAssociations/@ids/staff         | ee3d3035994b88d465553f38a265fff4597946d2_id | 2878f8c43530ccb0d5ad94ffaadad6e31953daf6_id  |
      | /cohorts/@ids/staffCohortAssociations       | 1c1a4ac5bebc9b67806805935edce7d6e1f269ea_id | b7c645daf74600587514032c5315588290d01b06_id  |
      | /cohorts/@ids/staffCohortAssociations/staff | 1c1a4ac5bebc9b67806805935edce7d6e1f269ea_id | b7c645daf74600587514032c5315588290d01b06_id  |
      | /cohorts/@ids/studentCohortAssociations     | 1c1a4ac5bebc9b67806805935edce7d6e1f269ea_id | b7c645daf74600587514032c5315588290d01b06_id  |
      | /cohorts/@ids/studentCohortAssociations/students | 1c1a4ac5bebc9b67806805935edce7d6e1f269ea_id | b7c645daf74600587514032c5315588290d01b06_id  |
