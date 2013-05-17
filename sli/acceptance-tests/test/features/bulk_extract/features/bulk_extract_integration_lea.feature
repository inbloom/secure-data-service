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
      |  attendance                            | 148 | a9605e685b48f1762236070db165cfe50961c058_id |
      |  cohort                                |   4 | f25bd34b5500f039c4fcbcdb1c1eccfc9b878bb5_id |
      #|  competencyLevelDescriptor             |   |         |
      |  course                                |   95| c2af0428d5c803fb3e908fdea10ae624c2f5abe4_id |
      |  courseOffering                        |   95| 42d2d4e0616ed8a94894aa78be9f15e5cafa3eb7_id |
      #|  courseTranscript                      |   |         |
      #|  disciplineIncident                    |   |         |
      #|  disciplineAction                      |   |         |
      |  educationOrganization                 | 4 | 2fe47c8e78a65ee51a72628c170673c35c4bd85a_id |
      |  grade                                 | 8 |         |
      |  gradebookEntry                        | 12 |         |
      |  gradingPeriod                         |  13 |         |
      #|  graduationPlan                        |   |         |
      #|  learningObjective                     |   |         |
      #|  learningStandard                      |   |         |
      |  parent                                | 18 |         |
      #|  program                               |   |         |
      |  reportCard                            | 4 |         |
      |  school                                | 3 | 2fe47c8e78a65ee51a72628c170673c35c4bd85a_id |
      |  section                               | 97  |         |
      |  session                               |  22 |         |
      |  staff                                 | 10 | 63d4be8a233db1fd14676f1535fa21fe4c5dd466_id |
      |  staffCohortAssociation                | 2 |         |
      |  staffEducationOrganizationAssociation | 10 | 346a3cc0939419b34283ec6cac2330e19a483f6f_id |
      |  staffProgramAssociation               | 6 |         |
      |  student                               | 154 | 7fb7741b5aea5d17bb01d5775c5df55524604868_id |
      |  studentAcademicRecord                 | 230 |         |
      |  studentAssessment                     | 400 |         |
      |  studentCohortAssociation              | 12 |         |
      #|  studentCompetency                     |   |         |
      #|  studentCompetencyObjective            |   |         |
      |  studentDisciplineIncidentAssociation  |  8 |         |
      |  studentProgramAssociation             | 12 |         |
      #|  studentGradebookEntry                 |   |         |
      |  studentSchoolAssociation              | 332 |         |
      |  studentSectionAssociation             | 297  |         |
      |  studentParentAssociation              | 18 |         |
      |  teacher                               | 3 |         |
      |  teacherSchoolAssociation              | 3  |         |
      |  teacherSectionAssociation             | 11 |         |
