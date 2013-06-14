@student_path
Feature: Path Based Security for Student Authentication
  I want to verify that URI paths that don't make sense for students to access are denied or rewritten

Scenario: Check un-versioned URIs work for student
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "cegray" with password "cegray1234"
  When I make requests to both the versioned and un-versioned URI "/system/session/check"
  Then both responses should be identical in return code and body
  When I make requests to both the versioned and un-versioned URI "/system/session/debug"
  Then both responses should be identical in return code and body
  When I navigate to GET "/system/session/logout"
  Then any future API request should result in a 401 response code

  @wip
Scenario: Verify Rewrites for Base Level entities for Students
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "cegray" with password "cegray1234"
  And my contextual access is defined by the table:
    | Context                | Ids                                         |
    | educationOrganizations | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
    | schools                | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
    | sections               | fb23953d3b55349847fe558e4909a265fab3b6a0_id,ac4aede7e0113d1c003f3da487fc079e124f129d_id,02ffe06e27e313e46e852c1a457ecb25af2cd950_id,6b687d24b9a2b10c664e2248bd8e689a482e47e2_id |
    | students               | 92164cd19ebdbe17cfdcd0e1d177877cdc9a40ef_id |
  And format "application/json"
  When I navigate to the base level URI <Entity> I should see the rewrite in the format of <URI>:
    | Entity                       | URI                                                                            |
    | /assessments                 | /search/assessments                                                            |
    | /attendances                 | /students/@ids/attendances                                                     |
    | /cohorts                     | /students/@ids/studentCohortAssociations/cohorts                               |
    | /competencyLevelDescriptor   | /search/competencyLevelDescriptor                                              |
    | /courseOfferings             | /schools/@ids/courseOfferings                                                  |
    | /courses                     | /schools/@ids/courseOfferings/courses                                          |
    | /courseTranscripts           | /students/@ids/studentAcademicRecords/courseTranscripts                        |
    | /educationOrganizations      | /schools/@ids                                                                  |
    | /gradebookEntries            | /sections/@ids/gradebookEntries                                                |
    | /grades                      | /students/@ids/studentSectionAssociations/grades                               |
    | /gradingPeriods              | /schools/@ids/sessions/gradingPeriods                                          |
    | /graduationPlans             | /schools/@ids/graduationPlans                                                  |
    | /learningObjectives          | /search/learningObjectives                                                     |
    | /learningStandards           | /search/learningStandards                                                      |
    | /parents                     | /students/@ids/studentParentAssociations/parents                               |
    | /programs                    | /students/@ids/studentProgramAssociations/programs                             |
    | /reportCards                 | /students/@ids/reportCards                                                     |
    | /schools                     | /schools/@ids                                                                  |
    | /sections                    | /sections/@ids                                                                 |
    | /sessions                    | /schools/@ids/sessions                                                         |
    | /staff                       | /educationOrganizations/@ids/staffEducationOrgAssignmentAssociations           |
    | /studentAcademicRecords      | /students/@ids/studentAcademicRecords                                          |
    | /studentAssessments          | /students/@ids/studentAssessments                                              |
    | /studentCohortAssociations   | /students/@ids/studentCohortAssociations                                       |
    | /studentCompetencies         | /students/@ids/studentSectionAssociations/studentCompetencies                  |
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

Scenario: Verify Blacklist for Student URI paths
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "cegray" with password "cegray1234"
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
    | /educationOrganizations/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/sessions                                                                      |
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
