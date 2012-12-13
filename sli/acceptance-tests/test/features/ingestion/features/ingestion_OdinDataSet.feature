@RALLY_US4816
Feature: Odin Data Set Ingestion Correctness and Fidelity
Background: I have a landing zone route configured
Given I am using odin data store

Scenario: Post Odin Sample Data Set
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And the tenant database for "Midgar" does not exist
  And I post "OdinSampleDataSet.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |
     | attendance                                |
     | calendarDate                              |
     | cohort                                    |
     | competencyLevelDescriptor                 |
     | course                                    |
     | courseOffering                            |
     | courseSectionAssociation                  |
     | disciplineAction                          |
     | disciplineIncident                        |
     | educationOrganization                     |
     | educationOrganizationAssociation          |
     | educationOrganizationSchoolAssociation    |
     | grade                                     |
     | gradebookEntry                            |
     | gradingPeriod                             |
     | graduationPlan                            |
     | learningObjective                         |
     | learningStandard                          |
     | parent                                    |
     | program                                   |
     | reportCard                                |
     | school                                    |
     | schoolSessionAssociation                  |
     | section                                   |
     | sectionAssessmentAssociation              |
     | sectionSchoolAssociation                  |
     | session                                   |
     | sessionCourseAssociation                  |
     | staff                                     |
     | staffCohortAssociation                    |
     | staffEducationOrganizationAssociation     |
     | staffProgramAssociation                   |
     | student                                   |
     | studentAcademicRecord                     |
     | studentAssessment                         |
     | studentCohortAssociation                  |
     | studentCompetency                         |
     | studentCompetencyObjective                |
     | studentDisciplineIncidentAssociation      |
     | studentParentAssociation                  |
     | studentProgramAssociation                 |
     | studentSchoolAssociation                  |
     | studentSectionAssociation                 |
     | studentGradebookEntry                     |
     | courseTranscript                          |
     | teacher                                   |
     | teacherSchoolAssociation                  |
     | teacherSectionAssociation                 |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                 78|
     | attendance                               |                  0|
     | calendarDate                             |               1161|
     | cohort                                   |                  0|
     | competencyLevelDescriptor                |                  0|
     | course                                   |                 34|
     | courseOffering                           |                102|
     | courseSectionAssociation                 |                  0|
     | disciplineAction                         |                  0|
     | disciplineIncident                       |                  0|
     | educationOrganization                    |                  6|
     | educationOrganizationAssociation         |                  0|
     | educationOrganizationSchoolAssociation   |                  0|
     | grade                                    |                  0|
     | gradebookEntry                           |                  0|
     | gradingPeriod                            |                  6|
     | graduationPlan                           |                  3|
     | learningObjective                        |                  0|
     | learningStandard                         |                  0|
     | parent                                   |                 20|
     | program                                  |                  0|
     | reportCard                               |                  0|
     | schoolSessionAssociation                 |                  0|
     | section                                  |                 75|
     | sectionAssessmentAssociation             |                  0|
     | sectionSchoolAssociation                 |                  0|
     | session                                  |                  6|
     | sessionCourseAssociation                 |                  0|
     | staff                                    |                 70|
     | staffCohortAssociation                   |                  0|
     | staffEducationOrganizationAssociation    |                168|
     | staffProgramAssociation                  |                  0|
     | student                                  |                 10|
     | studentAcademicRecord                    |                  0|
     | studentAssessment                        |                180|
     | studentCohortAssociation                 |                  0|
     | studentCompetency                        |                  0|
     | studentCompetencyObjective               |                  0|
     | studentDisciplineIncidentAssociation     |                  0|
     | studentParentAssociation                 |                 20|
     | studentProgramAssociation                |                  0|
     | studentSchoolAssociation                 |                 30|
     | studentSectionAssociation                |                 75|
     | studentGradebookEntry                    |                  0|
     | courseTranscript                         |                  0|
     | teacherSchoolAssociation                 |                 21|
     | teacherSectionAssociation                |                 75|
    And I should see "Processed 2106 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created

Scenario: Verify entities in education organization calendar were ingested correctly: Populated Database
    And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                          | searchValue                                   | searchType           |
     | session                     | 1                   | body.sessionName                         | 2001-2002 Year Round session: IL-DAYBREAK     | string               |
     | session                     | 1                   | body.sessionName                         | 2002-2003 Year Round session: IL-DAYBREAK     | string               |
     | session                     | 1                   | body.sessionName                         | 2003-2004 Year Round session: IL-DAYBREAK     | string               |
     | session                     | 2                   | body.schoolYear                          | 2001-2002                                     | string               |
     | session                     | 2                   | body.schoolYear                          | 2002-2003                                     | string               |
     | session                     | 2                   | body.schoolYear                          | 2003-2004                                     | string               |
     | session                     | 6                   | body.term                                | Year Round                                    | string               |
     | session                     | 6                   | body.totalInstructionalDays              | 180                                           | integer              |
     | gradingPeriod               | 6                   | body.gradingPeriodIdentity.gradingPeriod | End of Year                                   | string               |
     | gradingPeriod               | 2                   | body.gradingPeriodIdentity.schoolYear    | 2001-2002                                     | string               |
     | gradingPeriod               | 2                   | body.gradingPeriodIdentity.schoolYear    | 2002-2003                                     | string               |
     | gradingPeriod               | 2                   | body.gradingPeriodIdentity.schoolYear    | 2003-2004                                     | string               |
     | gradingPeriod               | 3                   | body.gradingPeriodIdentity.schoolId      | 71fdd5177721d3f95ad0f1f580ad55d7aa6a922e_id   | string               |
     | gradingPeriod               | 3                   | body.gradingPeriodIdentity.schoolId      | 1b223f577827204a1c7e9c851dba06bea6b031fe_id   | string               |
     | gradingPeriod               | 6                   | body.totalInstructionalDays              | 180                                           | integer              |

Scenario: Verify entities in student were ingested correctly: Populated Database
    And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                          | searchValue                                   | searchType           |
     | student                     | 10                  | type                                     | student                                       | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 1                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 2                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 3                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 4                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 5                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 6                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 7                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 8                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 9                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 10                                            | string               |
     | student                     | 10                  | schools.entryDate                        | 2001-09-01                                    | string               |
     | student                     | 3                   | schools.entryGradeLevel                  | Sixth grade                                   | string               |
     | student                     | 1                   | schools.entryGradeLevel                  | Kindergarten                                  | string               |
     | student                     | 3                   | schools.entryGradeLevel                  | Ninth grade                                   | string               |     
     | student                     | 6                   | schools.edOrgs                           | 352e8570bd1116d11a72755b987902440045d346_id   | string               |   
     | student                     | 5                   | schools.edOrgs                           | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id   | string               |   
     | student                     | 3                   | schools.edOrgs                           | a13489364c2eb015c219172d561c62350f0453f3_id   | string               |   
     | student                     | 10                  | schools.edOrgs                           | 1b223f577827204a1c7e9c851dba06bea6b031fe_id   | string               |   
     | student                     | 1                   | _id                                      | 9e54047cbfeeee26fed86b0667e98286a2b72791_id   | string               |   
     | studentParentAssociation    | 2                   | body.studentId                           | 9e54047cbfeeee26fed86b0667e98286a2b72791_id   | string               |   

Scenario: Verify entities in specific student document ingested correctly: Populated Database
  When I can find a student with _id 9e54047cbfeeee26fed86b0667e98286a2b72791_id in tenant db "Midgar"
    Then the student entity body.race should be "White"
    And the student entity body.limitedEnglishProficiency should be "NotLimited"
    And the student entity body.schoolFoodServicesEligibility should be "Reduced price"  
    And the student entity schools.entryGradeLevel should be "Kindergarten"
    And the student entity schools.entryGradeLevel should be "First grade" 
    And the student entity schools.entryGradeLevel should be "Second grade" 

Scenario: Verify entities in student school association were ingested correctly
    And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                          | searchValue                                   | searchType           |
     | graduationPlan              | 1                   | _id                                      | 438cc6756e65d65da2eabb0968387ad25a3e0b93_id   | string               |
     | studentSchoolAssociation    | 3                   | body.graduationPlanId                    | 438cc6756e65d65da2eabb0968387ad25a3e0b93_id   | string               |
