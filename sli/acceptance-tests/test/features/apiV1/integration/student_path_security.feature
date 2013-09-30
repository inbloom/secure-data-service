@student_path
Feature: Path Based Security for Student Authentication
  I want to verify that URI paths that don't make sense for students to access are denied or rewritten

Scenario: Check un-versioned URIs work for student
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "cegray" with password "cegray1234"
  When I make requests to both the versioned and un-versioned URI "/system/session/check"
  Then both responses should be identical in return code and body
  When I make requests to both the versioned and un-versioned URI "/system/session/debug"
  Then both responses should be identical in return code and body
  When I navigate to GET "/v1/system/support/email"
  Then I should receive a return code of 200
  When I navigate to GET "/userapps"
  Then I should receive a return code of 200
  When I navigate to GET "/system/session/logout"

  Given the sli securityEvent collection is empty
  Then any future API request should result in a 401 response code
   And I should see a count of "3" in the security event collection
   And I check to find if record is in sli db collection:
       | collectionName  | expectedRecordCount | searchParameter         | searchValue                       | searchType |
       | securityEvent   | 3                   | body.appId              | UNKNOWN                           | string     |
       | securityEvent   | 2                   | body.className          | org.slc.sli.api.util.SecurityUtil | string     |
       | securityEvent   | 2                   | body.logMessage         | Access Denied: Unauthorized       | string     |
      # tenantId, userEdOrg and targetEdOrgList not yet set
      And "1" security event with field "body.actionUri" matching "http.*/api/rest/v1..*/students" should be in the sli db
      And "1" security event with field "body.actionUri" matching "http.*/api/rest/v1..*/assessments" should be in the sli db

Scenario: Verify Rewrites for entities for Students
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "cegray" with password "cegray1234"
  # TODO get this info from the db - note must check expiry
  And my contextual access is defined by the table:
    | Context                | Ids                                         |
    | educationOrganizations | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
    | schools                | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
    | sections               | 88bb1ad61323121c4939db296f4d444094ad5563_id,e963c47cc117e076735bcfed4a3bbac748a820e5_id,2143ef735a74796f1bbc2462e10ff9ba36de9919_id,9226b1f7fcf2e3b14d12f59c69e1a9d693f51247_id,5acb9a7f01a06c393cf60416ddbc0f8238b8c7d0_id,248f27c61d78ee9fed5a7a77edcb172d2dff2324_id,e1af27afcaba9691bdb1cbc1baa30fe75b8c300c_id,82d6d79602c102c8223cb381ba43c3678efa9f2c_id |
    | students               | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id |
    | studentAcademicRecords | 29aeeb86490afdf1f9685216582d4410a0b9c380_id3e6ae27c427e4c3213eae1f77c0263a001312b33_id,2f38a01d3ce555cfcdc637aa02d3596de1e27574_id51a05e3fc152d6180c97370b1cdc4de367b1dce7_id |
    | studentSectionAssociations | 248f27c61d78ee9fed5a7a77edcb172d2dff2324_id2adcbbde16d2ae5b34d8818c2aab718aa3de9415_id,5acb9a7f01a06c393cf60416ddbc0f8238b8c7d0_id4834473e3acf9705b0caf734594922c91147a360_id,2143ef735a74796f1bbc2462e10ff9ba36de9919_idf43bed4dea431b12e2f4a75ed35252354567253b_id,88bb1ad61323121c4939db296f4d444094ad5563_id786e763a5ffa777305dc1a0cfa3f62dfb278f593_id,82d6d79602c102c8223cb381ba43c3678efa9f2c_id5e4eccb4dd848815aede7e71d83f5f43ce4a4732_id,e1af27afcaba9691bdb1cbc1baa30fe75b8c300c_id749b53e759c6cb0d13782c3189fb40a6bef0a64b_id,9226b1f7fcf2e3b14d12f59c69e1a9d693f51247_id4cb16d3e1882ba13516ad727d35ee83a85e63c44_id,e963c47cc117e076735bcfed4a3bbac748a820e5_idc80d29e01100af98af4e4aefd4ab80c6c4e45793_id |
    # TODO change Odin to have a non-expired cohort
    | cohorts                |  |
    | programs               | 904888b5ed47e46e2be7f3536a581e044fb18835_id,4bec276e96f0d52f4b99db2baa1b7b68487c0de7_id,af14c09f7795c63716c823c190e686e742b2c962_id,de7da21b8c7f020cc66a438d3cd13eb32ba41cb0_id,d5678b60124db54df4ae42565d5161b0a87e5691_id |
  And format "application/json"
  When I navigate to the base level URI <Entity> I should see the rewrite in the format of <URI>:
    | Entity                       | URI                                                                            |
#    | /assessments                 | /students/@ids/studentAssessments/assessments                                  |
#    | /attendances                 | /students/@ids/attendances                                                     |
#    | /cohorts                     | /students/@ids/studentCohortAssociations/cohorts                               |
    | /competencyLevelDescriptor   | /search/competencyLevelDescriptor                                              |
    | /courseOfferings             | /schools/@ids/courseOfferings                                                  |
    | /courses                     | /schools/@ids/courses                                                          |
    | /courseTranscripts           | /studentAcademicRecords/@ids/courseTranscripts                                 |
    | /educationOrganizations      | /schools/@ids                                                                  |
    | /gradebookEntries            | /sections/@ids/gradebookEntries                                                |
    | /grades                      | /studentSectionAssociations/@ids/grades                                        |
    | /gradingPeriods              | /schools/@ids/sessions/gradingPeriods                                          |
    | /graduationPlans             | /schools/@ids/graduationPlans                                                  |
    | /learningObjectives          | /search/learningObjectives                                                     |
    | /learningStandards           | /search/learningStandards                                                      |
    | /parents                     | /students/@ids/studentParentAssociations/parents                               |
    | /programs                    | /students/@ids/studentProgramAssociations/programs                             |
    | /reportCards                 | /students/@ids/reportCards                                                     |
    | /schools                     | /schools/@ids                                                                  |
    | /sections                    | /students/@ids/studentSectionAssociations/sections                             |
    | /sessions                    | /schools/@ids/sessions                                                         |
    | /staff                       | /educationOrganizations/@ids/staffEducationOrgAssignmentAssociations/staff     |
    # TODO update Odin to have a non-expired cohort associations - depends on studentCohortAssociation which is expired
    # Expired    | /staffCohortAssociations     | /cohorts/@ids/staffCohortAssociations                                          |
    | /staffEducationOrgAssignmentAssociations | /educationOrganizations/@ids/staffEducationOrgAssignmentAssociations           |
    | /staffProgramAssociations    | /programs/@ids/staffProgramAssociations                                        |
    | /studentAcademicRecords      | /students/@ids/studentAcademicRecords                                          |
    | /studentAssessments          | /students/@ids/studentAssessments                                              |
    # TODO update Odin to have a non-expired cohort association
    # Expired    | /studentCohortAssociations   | /students/@ids/studentCohortAssociations                                       |
    | /studentCompetencies         | /studentSectionAssociations/@ids/studentCompetencies                           |
    | /studentCompetencyObjectives | /educationOrganizations/@ids/studentCompetencyObjectives                       |
    | /studentGradebookEntries     | /students/@ids/studentGradebookEntries                                         |
    | /studentParentAssociations   | /students/@ids/studentParentAssociations                                       |
    | /studentProgramAssociations  | /students/@ids/studentProgramAssociations                                      |
    | /students                    | /sections/@ids/studentSectionAssociations/students                             |
    | /studentSchoolAssociations   | /students/@ids/studentSchoolAssociations                                       |
    | /studentSectionAssociations  | /students/@ids/studentSectionAssociations                                      |
    | /teachers                    | /sections/@ids/teacherSectionAssociations/teachers                             |
    | /teacherSchoolAssociations   | /schools/@ids/teacherSchoolAssociations                                        |
    | /teacherSectionAssociations  | /sections/@ids/teacherSectionAssociations                                      |
    | /yearlyAttendances           | /students/@ids/yearlyAttendances                                               |
  When I navigate to the URI <URI> I should see the rewrite in the format of <Rewrite>:
    | URI                          | Rewrite                                                                            |
    | /studentSectionAssociations/9226b1f7fcf2e3b14d12f59c69e1a9d693f51247_id4cb16d3e1882ba13516ad727d35ee83a85e63c44_id/sections                                                                                                        | /sections/9226b1f7fcf2e3b14d12f59c69e1a9d693f51247_id     |
    | /studentSectionAssociations/9226b1f7fcf2e3b14d12f59c69e1a9d693f51247_id4cb16d3e1882ba13516ad727d35ee83a85e63c44_id/students                                                                                                        | /sections/9226b1f7fcf2e3b14d12f59c69e1a9d693f51247_id/studentSectionAssociations/students     |


  Scenario: Verify Blacklist for Student URI paths
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "cegray" with password "cegray1234"
    And format "application/json"
    When I navigate to the the URI <Path> I should be denied:
      | Path                                                                                                                                              |
      | /assessments/573536622c1e9be19918624f781865febc011f89_id/studentAssessments                                                                       |
      | /courses/2baafeea20b19d4034d99e37059241a124545a16_id/courseTranscripts                                                                            |
      | /courseTranscripts/766251c46d92647f8ffd93e2de93ee4989b97439_id/students                                                                           |
      | /disciplineActions                                                                                                                                |
      | /disciplineIncidents                                                                                                                              |
      | /disciplineIncidents/9196736538ed47a9a565201eeda04fda31d9d671_id/studentDisciplineIncidentAssociations                                            |
      | /disciplineIncidents/9196736538ed47a9a565201eeda04fda31d9d671_id/studentDisciplineIncidentAssociations/students                                   |
      | /educationOrganizations/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/cohorts                                                                       |
      | /gradebookEntries/ac4aede7e0113d1c003f3da487fc079e124f129d_id26c51914b1974dfd8104dad40ee78f4c10324851_id/studentGradebookEntries                  |
      | /gradingPeriods/21b8ac38bf886e78a879cfdb973a9352f64d07b9_id/grades                                                                                |
      | /gradingPeriods/21b8ac38bf886e78a879cfdb973a9352f64d07b9_id/reportCards                                                                           |
      | /learningObjectives/53f942931e3c494e7d91ec26ddf52b4e02d5cdee_id/studentCompetencies                                                               |
      | /parents/ac9d23542b310939801dec4d29cfddda7765353b_id/studentParentAssociations                                                                    |
      | /parents/ac9d23542b310939801dec4d29cfddda7765353b_id/studentParentAssociations/students                                                           |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/sections/gradebookEntries                                                                    |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/sections/studentSectionAssociations                                                          |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/sections/studentSectionAssociations/grades                                                   |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/sections/studentSectionAssociations/studentCompetencies                                      |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/studentSchoolAssociations                                                                    |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/studentSchoolAssociations/students                                                           |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/studentSchoolAssociations/students/attendances                                               |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/studentSchoolAssociations/students/courseTranscripts                                         |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/studentSchoolAssociations/students/reportCards                                               |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/studentSchoolAssociations/students/studentAcademicRecords                                    |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/studentSchoolAssociations/students/studentAcademicRecords/courseTranscripts                  |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/studentSchoolAssociations/students/studentAssessments                                        |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/studentSchoolAssociations/students/studentGradebookEntries                                   |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/studentSchoolAssociations/students/studentParentAssociations                                 |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/studentSchoolAssociations/students/studentParentAssociations/parents                         |
      | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/teacherSchoolAssociations/teachers/teacherSectionAssociations                                |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentGradebookEntries                                                                     |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/grades                                                           |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/studentCompetencies                                              |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/students/attendances                                             |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/students/courseTranscripts                                       |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/students/reportCards                                             |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/students/studentAcademicRecords                                  |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/students/studentAcademicRecords/courseTranscripts                |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/students/studentAssessments                                      |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/students/studentGradebookEntries                                 |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/students/studentParentAssociations                               |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/students/studentParentAssociations/parents                       |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/students/studentSchoolAssociations                               |
      | /sections/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentSectionAssociations/students/yearlyAttendances                                       |
      | /sessions/02ffe06e27e313e46e852c1a457ecb25af2cd950_id/studentAcademicRecords                                                                      |
      | /staff/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/disciplineActions                                                                              |
      | /staff/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/disciplineIncidents                                                                            |
      | /staff/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/disciplineIncidents/studentDisciplineIncidentAssociations                                      |
      | /staff/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/staffCohortAssociations                                                                        |
      | /staff/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/staffCohortAssociations/cohorts                                                                |
      | /staff/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/staffCohortAssociations/cohorts/studentCohortAssociations                                      |
      | /staff/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/staffEducationOrgAssignmentAssociations                                                        |
      | /staff/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/staffEducationOrgAssignmentAssociations/educationOrganizations                                 |
      | /staff/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/staffEducationOrgAssignmentAssociations/schools                                                |
      | /staff/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/staffProgramAssociations                                                                       |
      | /staff/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/staffProgramAssociations/programs                                                              |
      | /staff/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/staffProgramAssociations/programs/studentProgramAssociations                                   |
      | /studentDisciplineIncidentAssociations                                                                                                            |
      | /studentDisciplineIncidentAssociations/92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id6727fea36fffe778b5489861df0e1114b912f583_id/disciplineIncidents |
      | /studentDisciplineIncidentAssociations/92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id6727fea36fffe778b5489861df0e1114b912f583_id/students            |
      | /students/92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id/studentDisciplineIncidentAssociations                                                       |
      | /students/92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id/studentDisciplineIncidentAssociations/disciplineIncidents                                   |
      | /teachers/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/disciplineActions                                                                           |
      | /teachers/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/disciplineIncidents                                                                         |
      | /teachers/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/disciplineIncidents/studentDisciplineIncidentAssociations                                   |
      | /teachers/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/staffCohortAssociations                                                                     |
      | /teachers/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/staffCohortAssociations/cohorts                                                             |
      | /teachers/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/teacherSchoolAssociations                                                                   |
      | /teachers/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/teacherSchoolAssociations/schools                                                           |
      | /teachers/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/teacherSectionAssociations                                                                  |
      | /teachers/1b8c3849be3ac8c3b1a7442aab1b00d1dcfa299c_id/teacherSectionAssociations/sections                                                         |
