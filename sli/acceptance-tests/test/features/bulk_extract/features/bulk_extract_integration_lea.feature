  Feature: LEA Level Bulk Extract
  
  @LEA
  Scenario: Trigger a bulk extract on ingested data and retrieve the lea extract through the api
      Given I trigger a bulk extract

      And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
      And in my list of rights I have BULK_EXTRACT
      #When I make lea bulk extract API call for lea "BLOOP"
      #Then I get back a response code of "403"
      When I make lea bulk extract API call for lea "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
      When the return code is 200 I get expected tar downloaded
      Then I check the http response headers
      When I decrypt and save the extracted file
      And I verify that an extract tar file was created for the tenant "Midgar"
      And there is a metadata file in the extract
      And the extract contains a file for each of the following entities with the appropriate count and does not have certain ids:
      |  entityType                            | count |  id  | 
      #|  assessment                            |   |         |  
      |  attendance                            | 150 |         |
      |  cohort                                |   5|         |
      #|  competencyLevelDescriptor             |   |         |
      |  course                                |   95|         |
      |  courseOffering                        |   95|         |
      #|  courseTranscript                      |   |         |
      #|  disciplineIncident                    |   |         |
      #|  disciplineAction                      |   |         |
      |  educationOrganization                 | 5 |          |
      |  grade                                 | 8 |         |
      #|  gradebookEntry                        |   |         |
      |  gradingPeriod                         |  13 |         |
      #|  graduationPlan                        |   |         |
      #|  learningObjective                     |   |         |
      #|  learningStandard                      |   |         |
      |  parent                                | 18 |         |
      #|  program                               |   |         |
      |  reportCard                            | 4 |         |
      # |  school                                |   |        |
      |  section                               | 97  |         |
      |  session                               |  22 |         |
      |  staff                                 | 10 | 63d4be8a233db1fd14676f1535fa21fe4c5dd466_id |
      |  staffCohortAssociation                | 2 |         |
      |  staffEducationOrganizationAssociation | 10 | 346a3cc0939419b34283ec6cac2330e19a483f6f_id |
      |  staffProgramAssociation               | 6 |         |
      |  student                               | 156 |         |
      |  studentAcademicRecord                 | 234 |         |
      |  studentAssessment                     | 406 |         |
      |  studentCohortAssociation              | 12 |         |
      #|  studentCompetency                     |   |         |
      #|  studentCompetencyObjective            |   |         |
      |  studentDisciplineIncidentAssociation  |  8 |         |
      |  studentProgramAssociation             | 12 |         |
      #|  studentGradebookEntry                 |   |         |
      |  studentSchoolAssociation              | 334 |         |
      |  studentSectionAssociation             | 297  |         |
      |  studentParentAssociation              | 18 |         |
      |  teacher                               | 3 |         |
      |  teacherSchoolAssociation              | 3  |         |
      #|  teacherSectionAssociation             |   |         |
