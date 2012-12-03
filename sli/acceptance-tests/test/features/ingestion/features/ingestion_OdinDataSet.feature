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
     | studentAssessment              |
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
     | assessment                               |                  0|
     | attendance                               |                  0|
     | calendarDate                             |                579|
     | cohort                                   |                  0|
     | competencyLevelDescriptor                |                  0|
     | course                                   |                 34|
     | courseOffering                           |                102|
     | courseSectionAssociation                 |                  0|
     | disciplineAction                         |                  0|
     | disciplineIncident                       |                  0|
     | educationOrganization                    |                  5|
     | educationOrganizationAssociation         |                  0|
     | educationOrganizationSchoolAssociation   |                  0|
     | grade                                    |                  0|
     | gradebookEntry                           |                  0|
     | gradingPeriod                            |                  3|
     | graduationPlan                           |                  0|
     | learningObjective                        |                  0|
     | learningStandard                         |                  0|
     | parent                                   |                  0|
     | program                                  |                  0|
     | reportCard                               |                  0|
     | schoolSessionAssociation                 |                  0|
     | section                                  |                  0|
     | sectionAssessmentAssociation             |                  0|
     | sectionSchoolAssociation                 |                  0|
     | session                                  |                  3|
     | sessionCourseAssociation                 |                  0|
     | staff                                    |                  0|
     | staffCohortAssociation                   |                  0|
     | staffEducationOrganizationAssociation    |                  0|
     | staffProgramAssociation                  |                  0|
     | student                                  |                 10|
     | studentAcademicRecord                    |                  0|
     | studentAssessment                        |                  0|
     | studentCohortAssociation                 |                  0|
     | studentCompetency                        |                  0|
     | studentCompetencyObjective               |                  0|
     | studentDisciplineIncidentAssociation     |                  0|
     | studentParentAssociation                 |                  0|
     | studentProgramAssociation                |                  0|
     | studentSchoolAssociation                 |                 10|
     | studentSectionAssociation                |                  0|
     | studentGradebookEntry                    |                  0|
     | courseTranscript                         |                  0|
     | teacherSchoolAssociation                 |                  0|
     | teacherSectionAssociation                |                  0|
    And I should see "Processed 746 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created

Scenario: Verify entities in education organization calendar were ingested correctly: Populated Database
    And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                          | searchValue                                   | searchType           |
     | session                     | 1                   | body.sessionName                         | 2001-2002 Year Round session: locl-0000000004 | string               |
     | session                     | 1                   | body.sessionName                         | 2002-2003 Year Round session: locl-0000000004 | string               |
     | session                     | 1                   | body.sessionName                         | 2003-2004 Year Round session: locl-0000000004 | string               |
     | session                     | 1                   | body.schoolYear                          | 2001-2002                                     | string               |
     | session                     | 1                   | body.schoolYear                          | 2002-2003                                     | string               |
     | session                     | 1                   | body.schoolYear                          | 2003-2004                                     | string               |
     | session                     | 3                   | body.term                                | Year Round                                    | string               |
     | session                     | 1                   | body.beginDate                           | 2001-09-07                                    | string               |
     | session                     | 1                   | body.beginDate                           | 2002-09-09                                    | string               |
     | session                     | 1                   | body.beginDate                           | 2003-09-05                                    | string               |
     | session                     | 1                   | body.endDate                             | 2002-06-04                                    | string               |
     | session                     | 1                   | body.endDate                             | 2003-06-04                                    | string               |
     | session                     | 1                   | body.endDate                             | 2004-06-01                                    | string               |
     | session                     | 3                   | body.totalInstructionalDays              | 180                                           | integer              |
     | gradingPeriod               | 3                   | body.gradingPeriodIdentity.gradingPeriod | End of Year                                   | string               |
     | gradingPeriod               | 1                   | body.gradingPeriodIdentity.schoolYear    | 2001-2002                                     | string               |
     | gradingPeriod               | 1                   | body.gradingPeriodIdentity.schoolYear    | 2002-2003                                     | string               |
     | gradingPeriod               | 1                   | body.gradingPeriodIdentity.schoolYear    | 2003-2004                                     | string               |
     | gradingPeriod               | 3                   | body.gradingPeriodIdentity.schoolId      | 42aaf7313c83453e0977f82dee426e6a51dd99a4_id   | string               |
     | gradingPeriod               | 1                   | body.beginDate                           | 2001-09-07                                    | string               |
     | gradingPeriod               | 1                   | body.beginDate                           | 2002-09-09                                    | string               |
     | gradingPeriod               | 1                   | body.beginDate                           | 2003-09-05                                    | string               |
     | gradingPeriod               | 1                   | body.endDate                             | 2002-06-04                                    | string               |
     | gradingPeriod               | 1                   | body.endDate                             | 2003-06-04                                    | string               |
     | gradingPeriod               | 1                   | body.endDate                             | 2004-06-01                                    | string               |
     | gradingPeriod               | 3                   | body.totalInstructionalDays              | 180                                           | integer              |
     | calendarDate                | 0                   | body.date                                | 2001-09-06                                    | string               |
     | calendarDate                | 1                   | body.date                                | 2001-09-07                                    | string               |
     | calendarDate                | 1                   | body.date                                | 2002-06-04                                    | string               |
     | calendarDate                | 0                   | body.date                                | 2002-06-05                                    | string               |
     | calendarDate                | 0                   | body.date                                | 2002-09-08                                    | string               |
     | calendarDate                | 1                   | body.date                                | 2002-09-09                                    | string               |
     | calendarDate                | 1                   | body.date                                | 2003-06-04                                    | string               |
     | calendarDate                | 0                   | body.date                                | 2003-06-05                                    | string               |
     | calendarDate                | 0                   | body.date                                | 2003-09-04                                    | string               |
     | calendarDate                | 1                   | body.date                                | 2003-09-05                                    | string               |
     | calendarDate                | 1                   | body.date                                | 2004-06-01                                    | string               |
     | calendarDate                | 0                   | body.date                                | 2004-06-02                                    | string               |
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
     | student                     | 6                   | schools.entryGradeLevel                  | Kindergarten                                  | string               |
     | student                     | 1                   | schools.entryGradeLevel                  | Ninth grade                                   | string               |
     | student                     | 10                  | schools.edOrgs                           | []                                            | array                |
     
 