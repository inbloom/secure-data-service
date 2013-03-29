Feature: A bulk extract is triggered, retrived through the api, and validated

Scenario: Trigger a bulk extract on ingested data
   Given I trigger a bulk extract

   And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
   And in my list of rights I have BULK_EXTRACT
   When I make bulk extract API call
   When the return code is 200 I get expected tar downloaded
   Then I check the http response headers

   When I save the extracted file
   And I verify that an extract tar file was created for the tenant "Midgar"
   And there is a metadata file in the extract
   And the extract contains a file for each of the following entities:
   |  entityType                            |
   |  assessment                            |
   |  attendance                            |
   |  calendarDate                          |
   |  cohort                                |
   |  competencyLevelDescriptor             |
   |  course                                |
   |  courseOffering                        |
   |  courseTranscript                      |
   |  disciplineIncident                    |
   |  disciplineAction                      |
   |  educationOrganization                 |
   #|  grade                                 |
   #|  gradebookEntry                        |
   |  gradingPeriod                         |
   |  graduationPlan                        |
   |  learningObjective                     |
   |  learningStandard                      |
   #|  objectiveAssessment                   |
   |  parent                                |
   |  program                               |
   #|  reportCard                            |
   |  school                                |
   |  section                               |
   |  session                               |
   #|  staff                                 |
   |  staffCohortAssociation                |
   |  staffEducationOrganizationAssociation |
   |  staffProgramAssociation               |
   |  student                               |
   #|  studentAcademicRecord                 |
   |  studentAssessment                     |
   #|  studentCohortAssociation              |
   |  studentCompetency                     |
   |  studentCompetencyObjective            |
   #|  studentDisciplineIncidentAssociation  |
   #|  studentObjectiveAssessment            |
   #|  studentProgramAssociation             |
   |  studentGradebookEntry                 |
   |  studentSchoolAssociation              |
   #|  studentSectionAssociation             |
   #|  studentParentAssociation              |
   #|  teacher                               |
   |  teacherSchoolAssociation              |
   #|  teacherSectionAssociation             |
