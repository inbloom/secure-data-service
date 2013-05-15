@RALLY_US
Feature: Custom Enumeration Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post a zip file containing custom enumeration data as a payload of the ingestion job: Clean Database
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "Enums.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName                        |
        | recordHash                            |
        | assessment                            |
        | assessmentPeriodDescriptor            |
        | assessmentFamily                      |
        | attendance                            |
        | calendarDate                          |
        | cohort                                |
        | competencyLevelDescriptor             |
        | course                                |
        | courseOffering                        |
        | disciplineAction                      |
        | disciplineIncident                    |
        | educationOrganization                 |
        | grade                                 |
        | gradebookEntry                        |
        | gradingPeriod                         |
        | graduationPlan                        |
        | learningObjective                     |
        | learningStandard                      |
        | parent                                |
        | program                               |
        | reportCard                            |
        | section                               |
        | session                               |
        | staff                                 |
        | staffCohortAssociation                |
        | staffEducationOrganizationAssociation |
        | staffProgramAssociation               |
        | student                               |
        | studentAcademicRecord                 |
        | studentAssessment                     |
        | studentCohortAssociation              |
        | studentCompetency                     |
        | studentCompetencyObjective            |
        | studentDisciplineIncidentAssociation  |
        | studentGradebookEntry                 |
        | studentParentAssociation              |
        | studentProgramAssociation             |
        | studentSchoolAssociation              |
        | studentSectionAssociation             |
        | courseTranscript                      |
        | teacherSchoolAssociation              |
        | teacherSectionAssociation             |
  And the following collections are empty in sli datastore:
        | collectionName                        |
        | securityEvent                         |
  When zip file is scp to ingestion landing zone
  And a batch job for file "Enums.zip" is completed in database

Then I should see following map of entry counts in the corresponding collections:
        | collectionName                           | count |
        | assessment                               | 1     |
        | assessmentFamily                         | 1     |
        | attendance                               | 1     |
        | educationOrganization                    | 1     |
        | parent                                   | 1     |
        | student                                  | 1     |
        | studentParentAssociation                 | 1     |
        | studentSchoolAssociation                 | 1     |
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | assessment                  | 1                   | body.attendanceEvent.event | Tardy         | string     |
       | attendance                  | 1                   | body.attendanceEvent.event | Tardy         | string     |
       | cohort                      | 1                   | body.cohortIdentifier      | ACC-TEST-COH-1             | string               |
       | cohort                      | 1                   | body.cohortIdentifier      | ACC-TEST-COH-2             | string               |
       | cohort                      | 1                   | body.cohortIdentifier      | ACC-TEST-COH-3             | string               |
       | course                      | 1                   | body.courseTitle     | 1st Grade Homeroom         | string               |
       | disciplineAction            | 1                   | body.disciplineDate      | 2011-03-04                 | string               |
       | disciplineAction            | 1                   | body.disciplineDate      | 2011-04-04                 | string               |
       | disciplineIncident          | 1                   | body.incidentIdentifier  | Disruption                 | string               |
       | disciplineIncident          | 1                   | body.incidentIdentifier  | Tardiness                  | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId      | IL                         | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId      | IL-DAYBREAK                | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId      | South Daybreak Elementary  | string               |
       | graduationPlan              | 3                   | body.educationOrganizationId                   | 1b223f577827204a1c7e9c851dba06bea6b031fe_id | string  |
       | graduationPlan              | 2                   | body.graduationPlanType                        | Minimum                                     | string  |
       | program                     | 1                   | body.programId      | ACC-TEST-PROG-1            | string               |
       | program                     | 1                   | body.programId      | ACC-TEST-PROG-2            | string               |
       | staff                       | 1                   | body.staffUniqueStateId        | cgray                      | string               |
       | staff                       | 1                   | body.staffUniqueStateId  | rbraverman                 | string               |
       | staff                       | 2                   | body.name.verification   | Drivers license            | string               |
       | staff                       | 2                   | body.race                | White                      | string               |
       | student                     | 1                   | body.studentUniqueStateId      | 100000000                  | string               |
       | student                     | 1                   | body.studentUniqueStateId      | 800000012                  | string               |
       | student                     | 1                   | body.studentUniqueStateId      | 800000025                  | string               |
       | student                     | 1                   | body.studentUniqueStateId      | 900000024                  | string               |
       | studentAssessment           | 10                  | studentAssessmentItem.body.assessmentItemResult              | Incorrect           | string |
       | studentAssessment           | 10                  | studentAssessmentItem.body.assessmentResponse                | False               | string |
       | studentAssessment           | 24                  | studentAssessmentItem.body.assessmentItemResult              | Correct             | string |
       | studentAssessment           | 24                  | studentAssessmentItem.body.assessmentResponse                | True                | string |
       | studentParentAssociation    | 2                   | body.contactRestrictions                                      | NO CONTACT ALLOWED  | string |
       | studentParentAssociation    | 3                   | body.contactPriority                                          | 1                   | integer|
    And I should see "Processed 10 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
#    And I should see "InterchangeStudentDiscipline.xml records considered for processing: 8" in the resulting batch job file
#    And I should see "InterchangeStudentDiscipline.xml records ingested successfully: 8" in the resulting batch job file
#    And I should see "InterchangeStudentDiscipline.xml records failed processing: 0" in the resulting batch job file
#    And I should see "InterchangeAssessmentMetadata-CommonCore.xml records considered for processing: 99" in the resulting batch job file
#    And I should see "InterchangeAssessmentMetadata-CommonCore.xml records ingested successfully: 99" in the resulting batch job file
#    And I should see "InterchangeAssessmentMetadata-CommonCore.xml records failed processing: 0" in the resulting batch job file

