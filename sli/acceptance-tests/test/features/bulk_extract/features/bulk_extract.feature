Feature: A bulk extract is triggered

@smoke
Scenario: Trigger a bulk extract on ingested data
   Given I trigger a bulk extract
   When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
   And I verify that an extract tar file was created for the tenant "Midgar" 
   And there is a metadata file in the extract
   And the extract contains a file for each of the following entities:
   |  entityType                            |
   |  attendance                            |
   |  cohort                                |
   |  course                                |
   |  courseOffering                        |
   |  courseTranscript                      |
   |  disciplineIncident                    |
   |  disciplineAction                      |
   |  educationOrganization                 |
   |  grade                                 |
   |  gradebookEntry                        |
   |  gradingPeriod                         |
   |  graduationPlan                        |
   |  parent                                |
   |  reportCard                            |
   |  school                                |
   |  section                               |
   |  session                               |
   |  staff                                 |   
   |  staffCohortAssociation                |
   |  staffEducationOrganizationAssociation |
   |  staffProgramAssociation               |
   |  student                               |
   |  studentAcademicRecord                 |
   |  studentAssessment                     |
   |  studentCohortAssociation              |
   |  studentCompetency                     |
   |  studentDisciplineIncidentAssociation  |   
   |  studentProgramAssociation             |
   |  studentGradebookEntry                 |
   |  studentParentAssociation              |
   |  studentSchoolAssociation              |
   |  studentSectionAssociation             |
   |  teacher                               |
   |  teacherSchoolAssociation              |
   |  teacherSectionAssociation             |

 @smoke
  Scenario: Trigger a bulk extract on ingested data
    Given I trigger a bulk extract
    When I fetch the path to and decrypt the SEA public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "b64ee2bcc92805cdd8ada6b7d8f9c643c9459831_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  assessment                            |
      |  competencyLevelDescriptor             |
      |  graduationPlan                        |
      |  learningStandard                      |
      |  program                               |
      |  calendarDate                          |
      |  educationOrganization                 |
      |  learningObjective                     |
      |  studentCompetencyObjective            |