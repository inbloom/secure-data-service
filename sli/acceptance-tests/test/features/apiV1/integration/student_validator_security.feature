@student_validator
Feature: Verify integrety of Student Validation Logic
  I want to make damn sure that student validation logic is working as intended, and that others do not break it.

Scenario: Validators return proper return codes on multi-ID requests
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "carmen.ortiz" with password "carmen.ortiz1234"
  And format "application/json"
  When I make API calls for multiple IDs in accordance to the following table:
    | Path                             | GoodId                                                                                 | BadId                                                                                  |
    | /attendances/@ids                | 69a338d4c77f47dbb0edb12878c751bde7622505_id                                            | cd14890af69207e6d9433f0962107eb0c96a1748_id                                            |
    | /cohorts/@ids                    | 72c0402e82f3a67db019d7736d423cc4a214db87_id                                            | 4711a3d63401b22260d9ed17313b9fc301f02c6f_id                                            |
    | /courseTranscripts/@ids          | 883d1a1bcf9606cfc38774d205b46b5ad17657a8_id                                            | a7ab1449d7291f6409abac0cb24d5faa62a90074_id                                            |
    | /gradebookEntries/@ids           | 98c2fbb9fffbabb9c8bbbcc8de9d5f7db42dff5d_id4351ffe6bb167a40a87a839a83b7e91e0507f80b_id | 88bb1ad61323121c4939db296f4d444094ad5563_ida09195b53390343f89c72de0912696f48f8080b5_id |
    | /grades/@ids                     | 581786d185ce6b954c17be3c116a025410a28f74_id97c4517dc01cc832231a07805ce8ee0035c6904c_id | 2f38a01d3ce555cfcdc637aa02d3596de1e27574_idc7af9f9859103f140c0d3638751f0303995675e8_id |
    | /parents/@ids                    | 2d6638adf22232b9af30b03ce9e84e707f4cf501_id                                            | ac9d23542b310939801dec4d29cfddda7765353b_id                                            |
    | /reportCards/@ids                | 581786d185ce6b954c17be3c116a025410a28f74_iddbc9d23b4a2b3ae57d05c94c81ab4d579c68663a_id | 2f38a01d3ce555cfcdc637aa02d3596de1e27574_id541b51e428f2371374525eab0cc8743faf324ded_id |
#    | /staff/@ids                      | e19386ee6534460293d35cc7ab9b8547e145205a_id                                            | 6757c28005c30748f3bbda02882bf59bc81e0d71_id                                            |
    | /studentAcademicRecords/@ids     | 581786d185ce6b954c17be3c116a025410a28f74_iddac01c5702e0af232c4a34751c9c6f54aa966d4e_id | 2f38a01d3ce555cfcdc637aa02d3596de1e27574_id51a05e3fc152d6180c97370b1cdc4de367b1dce7_id |
    | /studentAssessments/@ids         | dac3e2b4f5bd1e7d02f22fb23b26fdb8ba94ab7f_id                                            | 2614b8f1d4c23aaa654e0250217aeceb21aa6a92_id                                            |
    | /studentCohortAssociations/@ids  | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id99f774601fb35844fa557ed82cfd97091d40c21f_id | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_idf460df29cd44fc5458e7cf650fa40002ec274865_id |
    | /studentCompetencies/@ids        | eec3a693a985d3d2ef25d159b583f689083e9429_id                                            | 22d650d7d2c2d479b28348856359732591e1c4bf_id                                            |
    | /studentGradebookEntries/@ids    | 0a36c36d52e68fcf72afef1ad6a4795fc13a75c7_id                                            | 54631882c95b478b13a723865be31ced851386f3_id                                            |
    | /studentParentAssociations/@ids  | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idc72d8118cb267b19c3716bf36e0d00a6f8649853_id | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_idf2f0a1ddc141a58169b3c586e78ae6ba8d44e8ee_id |
    | /studentProgramAssociations/@ids | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id7614372c4e0284e7e8ab0782ddffdc5bba9dbd7e_id | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id349189276a34caddc20d2fd6a2e5e6815710b896_id |
    | /students/@ids                   | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id                                            | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id                                            |
    | /studentSchoolAssociations/@ids  | 932111c8421d0eb277d8ce5238a0272ec56f9e38_id                                            | ea66a6ef0e3c2dd61701e9fa5bcf108a631a9bcb_id                                            |
    | /studentSectionAssociations/@ids | 98c2fbb9fffbabb9c8bbbcc8de9d5f7db42dff5d_id5da26ce302b6c25b2cd7d4cd3b73f1363f32a195_id | 88bb1ad61323121c4939db296f4d444094ad5563_id786e763a5ffa777305dc1a0cfa3f62dfb278f593_id |
#    | /teachers/@ids                   | fe472294f0e40fd428b1a67b9765360004562bab_id                                            | 6757c28005c30748f3bbda02882bf59bc81e0d71_id                                            |
#    | /teacherSchoolAssociations/@ids  | 185f8333b893edd803f880463a2a193d60715743_id                                            | 93a4133d17303788f99e3b229b9649d46de5f42e_id                                            |
#    | /teacherSectionAssociations/@ids | 527f07a98f7f05c56c17a07cbbeac7eb1fa1d4db_id1e0c3bfe230357bd09c3d1a19a29b17489eeea68_id | 6b687d24b9a2b10c664e2248bd8e689a482e47e2_idfe800a3044200c3b3ca6875b2449d581cc0521b7_id |
    | /yearlyAttendances/@ids          | 69a338d4c77f47dbb0edb12878c751bde7622505_id                                            | cd14890af69207e6d9433f0962107eb0c96a1748_id                                            |
  When I request the Good ID, I should be allowed
  When I request the Bad ID, I should be denied
  When I request both IDs, I should be denied
