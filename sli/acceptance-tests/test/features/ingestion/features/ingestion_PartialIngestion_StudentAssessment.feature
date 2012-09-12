@RALLY_US3696
Feature: Partial Ingestion of StudentAttendence records

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post StudentAttendance records without required parent records
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "SSDS_StudentAssessment.zip" file as the payload of the ingestion job
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
     | studentAssessmentAssociation              |
     | studentCohortAssociation                  |
     | studentCompetency                         |
     | studentCompetencyObjective                |
     | studentDisciplineIncidentAssociation      |
     | studentParentAssociation                  |
     | studentProgramAssociation                 |
     | studentSchoolAssociation                  |
     | studentSectionAssociation                 |
     | studentGradebookEntry                     |
     | studentTranscriptAssociation              |
     | teacher                                   |
     | teacherSchoolAssociation                  |
     | teacherSectionAssociation                 |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  0|
     | attendance                               |                  0|
     | calendarDate                             |                  0|
     | cohort                                   |                  0|
     | competencyLevelDescriptor                |                  0|
     | course                                   |                  0|
     | courseOffering                           |                  0|
     | courseSectionAssociation                 |                  0|
     | disciplineAction                         |                  0|
     | disciplineIncident                       |                  0|
     | educationOrganization                    |                  0|
     | educationOrganizationAssociation         |                  0|
     | educationOrganizationSchoolAssociation   |                  0|
     | grade                                    |                  0|
     | gradebookEntry                           |                  0|
     | gradingPeriod                            |                  0|
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
     | session                                  |                  0|
     | sessionCourseAssociation                 |                  0|
     | staff                                    |                  0|
     | staffCohortAssociation                   |                  0|
     | staffEducationOrganizationAssociation    |                  0|
     | staffProgramAssociation                  |                  0|
     | student                                  |                  0|
     | studentAcademicRecord                    |                  0|
     | studentAssessmentAssociation             |                  0|
     | studentCohortAssociation                 |                  0|
     | studentCompetency                        |                  0|
     | studentCompetencyObjective               |                  0|
     | studentDisciplineIncidentAssociation     |                  0|
     | studentParentAssociation                 |                  0|
     | studentProgramAssociation                |                  0|
     | studentSchoolAssociation                 |                  0|
     | studentSectionAssociation                |                  0|
     | studentGradebookEntry                    |                  0|
     | studentTranscriptAssociation             |                  0|
     | teacherSchoolAssociation                 |                  0|
     | teacherSectionAssociation                |                  0|
    And I should see "Not all records were processed completely due to errors." in the resulting batch job file
    And I should see "Processed 203 records." in the resulting batch job file
  
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "SSDS_No_StudentAssessment.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
    And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                 19|
     | attendance                               |                 75|
     | calendarDate                             |                556|
     | cohort                                   |                  3|
     | competencyLevelDescriptor                |                  0|
     | course                                   |                 95|
     | courseOffering                           |                 95|
     | courseSectionAssociation                 |                  0|
     | disciplineAction                         |                  2|
     | disciplineIncident                       |                  2|
     | educationOrganization                    |                  5|
     | educationOrganizationAssociation         |                  0|
     | educationOrganizationSchoolAssociation   |                  0|
     | grade                                    |                  4|
     | gradebookEntry                           |                 12|
     | gradingPeriod                            |                 17|
     | graduationPlan                           |                  0|
     | learningObjective                        |                135|
     | learningStandard                         |               1463|
     | parent                                   |                  9|
     | program                                  |                  2|
     | reportCard                               |                  2|
     | schoolSessionAssociation                 |                  0|
     | section                                  |                 97|
     | sectionAssessmentAssociation             |                  0|
     | sectionSchoolAssociation                 |                  0|
     | session                                  |                 22|
     | sessionCourseAssociation                 |                  0|
     | staff                                    |                 14|
     | staffCohortAssociation                   |                  3|
     | staffEducationOrganizationAssociation    |                 10|
     | staffProgramAssociation                  |                  3|
     | student                                  |                 78|
     | studentAcademicRecord                    |                117|
     | studentAssessmentAssociation             |                  0|
     | studentCohortAssociation                 |                  6|
     | studentCompetency                        |                 59|
     | studentCompetencyObjective               |                  4|
     | studentDisciplineIncidentAssociation     |                  4|
     | studentParentAssociation                 |                  9|
     | studentProgramAssociation                |                  6|
     | studentSchoolAssociation                 |                167|
     | studentSectionAssociation                |                297|
     | studentGradebookEntry                    |                315|
     | studentTranscriptAssociation             |                196|
     | teacherSchoolAssociation                 |                  3|
     | teacherSectionAssociation                |                 11|
    And I should see "Processed 3945 records." in the resulting batch job file
    And I should not see an error log file created

    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "SSDS_StudentAssessment.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                 19|
     | attendance                               |                 75|
     | calendarDate                             |                556|
     | cohort                                   |                  3|
     | competencyLevelDescriptor                |                  0|
     | course                                   |                 95|
     | courseOffering                           |                 95|
     | courseSectionAssociation                 |                  0|
     | disciplineAction                         |                  2|
     | disciplineIncident                       |                  2|
     | educationOrganization                    |                  5|
     | educationOrganizationAssociation         |                  0|
     | educationOrganizationSchoolAssociation   |                  0|
     | grade                                    |                  4|
     | gradebookEntry                           |                 12|
     | gradingPeriod                            |                 17|
     | graduationPlan                           |                  0|
     | learningObjective                        |                135|
     | learningStandard                         |               1463|
     | parent                                   |                  9|
     | program                                  |                  2|
     | reportCard                               |                  2|
     | schoolSessionAssociation                 |                  0|
     | section                                  |                 97|
     | sectionAssessmentAssociation             |                  0|
     | sectionSchoolAssociation                 |                  0|
     | session                                  |                 22|
     | sessionCourseAssociation                 |                  0|
     | staff                                    |                 14|
     | staffCohortAssociation                   |                  3|
     | staffEducationOrganizationAssociation    |                 10|
     | staffProgramAssociation                  |                  3|
     | student                                  |                 78|
     | studentAcademicRecord                    |                117|
     | studentAssessmentAssociation             |                178|
     | studentCohortAssociation                 |                  6|
     | studentCompetency                        |                 59|
     | studentCompetencyObjective               |                  4|
     | studentDisciplineIncidentAssociation     |                  4|
     | studentParentAssociation                 |                  9|
     | studentProgramAssociation                |                  6|
     | studentSchoolAssociation                 |                167|
     | studentSectionAssociation                |                297|
     | studentGradebookEntry                    |                315|
     | studentTranscriptAssociation             |                196|
     | teacherSchoolAssociation                 |                  3|
     | teacherSectionAssociation                |                 11|
    And I should see "Not all records were processed completely due to errors." in the resulting batch job file
    And I should see "Processed 203 records." in the resulting batch job file

