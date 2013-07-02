Feature: Ingest odin data for use with bulk extract

Scenario: Generate and ingest data set for bulk extract
  Given I am using the odin working directory
    And I am using odin data store 

    When I generate the bulk extract data set in the generated directory
    And I zip generated data under filename OdinSampleDataSet.zip to the new OdinSampleDataSet directory
    And I copy generated data to the new OdinSampleDataSet directory
    Then I should see generated file <File>
| File  |
|ControlFile.ctl|
|InterchangeAssessmentMetadata.xml|
|InterchangeAttendance.xml|
|InterchangeEducationOrgCalendar.xml|
|InterchangeEducationOrganization.xml|
|InterchangeMasterSchedule.xml|
|InterchangeStaffAssociation.xml|
|InterchangeStudentAssessment.xml|
|InterchangeStudentCohort.xml|
|InterchangeStudentDiscipline.xml|
|InterchangeStudentEnrollment.xml|
|InterchangeStudentGrades.xml|
|InterchangeStudentParent.xml|
|InterchangeStudentProgram.xml|
|OdinSampleDataSet.zip|
|manifest.json|


Scenario: Post Odin Sample Data Set
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I am using odin data store 
  And I post "OdinSampleDataSet.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |
     | assessmentFamily                          |
     | assessmentPeriodDescriptor                |
     | attendance                                |
     | calendarDate                              |
     | cohort                                    |
     | competencyLevelDescriptor                 |
     | course                                    |
     | courseOffering                            |
     | courseSectionAssociation                  |
     | courseTranscript                          |
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
     | studentGradebookEntry                     |
     | studentParentAssociation                  |
     | studentProgramAssociation                 |
     | studentSchoolAssociation                  |
     | studentSectionAssociation                 |
     | teacher                                   |
     | teacherSchoolAssociation                  |
     | teacherSectionAssociation                 |
When zip file is scp to ingestion landing zone
  And a batch job for file "OdinSampleDataSet.zip" is completed in database
  And a batch job log has been created
  
Then I should see following map of entry counts in the corresponding collections:
  | collectionName                           |              count|
  | student                                  |                260|
  And I should not see an error log file created
  And I should not see a warning log file created
