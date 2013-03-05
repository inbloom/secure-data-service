@RALLY_US4112
Feature: Ingestion Delta Test

Background: I have a landing zone route configured
Given I am using local data store
 And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"

Scenario: Job report should report deltas when SDS is ingested twice
    Given I post "StoriedDataSet_IL_Daybreak.zip" file as the payload of the ingestion job
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
    And zip file is scp to ingestion landing zone
    And a batch job for file "StoriedDataSet_IL_Daybreak.zip" is completed in database
    And a batch job log has been created
    And zip file is scp to ingestion landing zone with name "Reingest-StoriedDataSet_IL_Daybreak.zip"
    And a batch job for file "Reingest-StoriedDataSet_IL_Daybreak.zip" is completed in database
    And I should see "InterchangeStudent.xml student 78 deltas!" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml course 95 deltas!" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml school 3 deltas!" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml program 2 deltas!" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml competencyLevelDescriptor 6 deltas!" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml localEducationAgency 2 deltas!" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml stateEducationAgency 1 deltas!" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml calendarDate 556 deltas!" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml session 22 deltas!" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml gradingPeriod 17 deltas!" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml courseOffering 95 deltas!" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml section 97 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentProgram.xml studentProgramAssociation 6 deltas!" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml teacherSchoolAssociation 3 deltas!" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml staff 11 deltas!" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml teacherSectionAssociation 11 deltas!" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml staffEducationOrgAssignmentAssociation 13 deltas!" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml teacher 3 deltas!" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml staffProgramAssociation 7 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml studentSchoolAssociation 167 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml graduationPlan 4 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml studentSectionAssociation 325 deltas!" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CCS-English.xml learningStandard 954 deltas!" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CCS-Math.xml learningStandard 509 deltas!" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CommonCore.xml learningStandard 36 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml courseTranscript 196 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml studentAcademicRecord 117 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml studentCompetencyObjective 4 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml reportCard 2 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml studentCompetency 60 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml grade 4 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml studentGradebookEntry 315 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml gradebookEntry 12 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml parent 9 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml studentParentAssociation 9 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentCohort.xml cohort 3 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentCohort.xml studentCohortAssociation 6 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentCohort.xml staffCohortAssociation 3 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml disciplineAction 2 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml studentDisciplineIncidentAssociation 4 deltas!" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml disciplineIncident 2 deltas!" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CCS-English.xml learningObjective 70 deltas!" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CCS-Math.xml learningObjective 65 deltas!" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CommonCore.xml learningObjective 63 deltas!" in the resulting batch job file
    And I should see "InterchangeAttendance.xml attendanceEvent 5550 deltas!" in the resulting batch job file
    And I should not see a warning log file created
    And I post "StoriedDataSet_IL_Daybreak_Deltas.zip" file as the payload of the ingestion job
    And zip file is scp to ingestion landing zone with name "StoriedDataSet_IL_Daybreak_Deltas.zip"
    And a batch job for file "StoriedDataSet_IL_Daybreak_Deltas.zip" is completed in database
    And I should see "InterchangeAttendance.xml attendanceEvent 2 deltas!" in the resulting batch job file
    And I should not see a warning log file created
    And I check to find if record is in collection:
        | collectionName              | expectedRecordCount | searchParameter                                 | searchValue       |
        | attendance                  | 1                   | body.attendanceEvent.reason| test for 100000000|
        | attendance                  | 1                   | body.attendanceEvent.date  | 2011-12-11        |


Scenario: Job report should not report deltas when SDS is ingested twice for different tenantId
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "Hyrule.zip" file as the payload of the ingestion job for "Midgar-Daybreak"
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
When zip file is scp to ingestion landing zone for "Midgar-Daybreak"
And a batch job for file "Hyrule.zip" is completed in database
And I should not see a warning log file created
And I should see following map of entry counts in the corresponding collections:
  | collectionName                           |              count|
  | recordHash                               |                112|
And I am using preconfigured Ingestion Landing Zone for "Hyrule-NYC"
And I post "Hyrule.zip" file as the payload of the ingestion job
And zip file is scp to ingestion landing zone with name "Reingest-Hyrule.zip"
And a batch job for file "Reingest-Hyrule.zip" is completed in database
And I should not see a warning log file created
And I should see following map of entry counts in the corresponding collections:
  | collectionName                           |              count|
  | recordHash                               |                112|
