Feature: Multi LEA Dataset for Bulk Extract

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post Small Sample Data Set
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "BulkExtractLeas.zip" file as the payload of the ingestion job
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
     | recordHash                                |
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
     | studentObjectiveAssessment                |
     | studentParentAssociation                  |
     | studentProgramAssociation                 |
     | studentSchoolAssociation                  |
     | studentSectionAssociation                 |
     | studentGradebookEntry                     |
     | courseTranscript                          |
     | teacher                                   |
     | teacherSchoolAssociation                  |
     | teacherSectionAssociation                 |
     | yearlyTranscript                          |
When zip file is scp to ingestion landing zone
  And a batch job for file "BulkExtractLeas.zip" is completed in database
  Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                 71|
     | assessmentFamily                         |                 66|
     | assessmentPeriodDescriptor               |                 28|
     | attendance                               |                172|
     | calendarDate                             |                950|
     | cohort                                   |                 12|
     | competencyLevelDescriptor                |                  4|
     | course                                   |                129|
     | courseOffering                           |                163|
     | courseSectionAssociation                 |                  0|
     | disciplineAction                         |                259|
     | disciplineIncident                       |                138|
     | educationOrganization                    |                 11|
     | educationOrganizationAssociation         |                  0|
     | educationOrganizationSchoolAssociation   |                  0|
     | grade                                    |                260|
     | gradebookEntry                           |               1239|
     | gradingPeriod                            |                 19|
     | graduationPlan                           |                  3|
     | learningObjective                        |                948|
     | learningStandard                         |               1499|
     | parent                                   |                109|
     | program                                  |                 64|
     | recordHash                               |              26711|
     | reportCard                               |                 99|
     | schoolSessionAssociation                 |                  0|
     | section                                  |                165|
     | sectionAssessmentAssociation             |                  0|
     | sectionSchoolAssociation                 |                  0|
     | session                                  |                 24|
     | sessionCourseAssociation                 |                  0|
     | staff                                    |                 70|
     | staffCohortAssociation                   |                 21|
     | staffEducationOrganizationAssociation    |                105|
     | staffProgramAssociation                  |                320|
     | student                                  |                128|
     | studentAcademicRecord                    |                214|
     | studentAssessment                        |                785|
     | studentCohortAssociation                 |                 90|
     | studentCompetency                        |               1339|
     | studentCompetencyObjective               |                  4|
     | studentDisciplineIncidentAssociation     |                261|
     | studentObjectiveAssessment               |                882|
     | studentParentAssociation                 |                109|
     | studentProgramAssociation                |                307|
     | studentSchoolAssociation                 |                265|
     | studentSectionAssociation                |                554|
     | studentGradebookEntry                    |               4948|
     | courseTranscript                         |                452|
     | teacherSchoolAssociation                 |                 25|
     | teacherSectionAssociation                |                 79|
    And I should not see an error log file created
	And I should not see a warning log file created
