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
      |  attendance                            | 74 | a9605e685b48f1762236070db165cfe50961c058_id |
      |  calendarDate                          |  4 | 8b126c91f26c86d7204ebb670da8eed5dc958482_id |
      #|  competencyLevelDescriptor             |   |         |
      |  course                                |   95| c2af0428d5c803fb3e908fdea10ae624c2f5abe4_id |
      |  courseOffering                        |   95| 42d2d4e0616ed8a94894aa78be9f15e5cafa3eb7_id |
      |  courseTranscript                      |  196| f93d8f8678c4bcbebe1c8b118d12cda004025304_id |
      |  disciplineIncident                    | 2  | aa752ad920bcf00b341236b230eac76e5a80ef3b_id   |
      |  disciplineAction                      | 2  | 481fc191703cb530e38e8d6bfda6eab6bf86512d_id  |
      |  educationOrganization                 | 6 | 2fe47c8e78a65ee51a72628c170673c35c4bd85a_id |
      |  grade                                 | 4 | 0ae33a43b05f2be4b9c8af13788ae0b0448370ef_id2bccec3c9e510238c60c627d715fa3fc0d4d7dd3_id |
      |  gradebookEntry                        | 12 | 8a4b9bcd2ca43c5209fd7ab0e1f61dafd1a4f4a2_idd89bfce7c9788c7fc352c8556f491ffb6e324f88_id |
      |  gradingPeriod                         |  13 | a080020ce9742a50fdbea6703f0642a01bbe3a19_id |
      |  graduationPlan                        |  1 |  be94495521de212dcb844169eafd434061fbfff9_id |
      #|  learningObjective                     |   |         |
      #|  learningStandard                      |   |         |
      |  parent                                | 9 | 6bf57bc4b7ec518e78ee16a627b02da2a02a182b_id |
      #|  program                               |   |         |
      |  reportCard                            | 2 | 0ae33a43b05f2be4b9c8af13788ae0b0448370ef_id4a997dd12d3122fa839523965aab34e397fb79db_id |
      |  school                                | 4 | 2fe47c8e78a65ee51a72628c170673c35c4bd85a_id |
      |  session                               |  22 | ed9acb6591da6f3b0b3bc1264846fd185fbcd527_id |
      |  staff                                 | 10 | 63d4be8a233db1fd14676f1535fa21fe4c5dd466_id |
      |  staffCohortAssociation                | 2 | f2cbed0cf5ff1d7a43f77e6a5f0f75f174cc7065_id |
      |  staffEducationOrganizationAssociation | 11 | 346a3cc0939419b34283ec6cac2330e19a483f6f_id |
      |  staffProgramAssociation               | 6 | e0ccea007dca317537e52998c1a7d8267b110787_id |
      |  student                               | 78 |  |
      |  studentAcademicRecord                 | 117 | 0ae33a43b05f2be4b9c8af13788ae0b0448370ef_id5dc5dba7a0a4a5dbeefaf195726a5fbc7be98d70_id |
      |  studentAssessment                     | 203 | 2854a67ed0375d5b038caef2b4b39856d15b6d58_id |
      |  studentCohortAssociation              | 6 | 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_idc62d5fe5dcbcb399ec33abe02dccba23b8a16eb6_id |
      |  studentCompetency                     | 59  |  08aad024bc436c450c1dd457beadefe4de0ba3ae_id       |
      #|  studentCompetencyObjective            |   |         |
      |  studentDisciplineIncidentAssociation  |  4 | 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_idd41740c1b17aff2baea21a76ed0dc809af1ebf13_id |
      |  studentProgramAssociation             | 6 | 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_id2139ed90ad4b4a9e7d66f47a996fb343a07cde39_id |
      |  studentGradebookEntry                 | 312 | b444b2aabe4eed054c6f1258d44ae4d8721ae855_id |
      |  studentSchoolAssociation              | 168 | 849a010012a384e4ba33c6bb5014a4707e7072c9_id |
      |  studentSectionAssociation             | 294 | 42d78c37610b0110d8cf80d7afb4e556884550e1_ide371ef980b66ae592795248d685d60a16e73e726_id |
      |  studentParentAssociation              | 9 | 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_id5ba0c1c2a60e24e0b01a66d6d68892f16ba29b01_id |
      |  teacher                               | 3 | ac8ff205a6cbb248c761083ac692802a0709e4b1_id |
      |  teacherSchoolAssociation              | 3  | 7c6f6301ed520d8d0606ed525044143e900d456b_id |
      |  teacherSectionAssociation             | 11 | 8a4b9bcd2ca43c5209fd7ab0e1f61dafd1a4f4a2_id173e9047fcb1f753b83e69ccfce4c8f68844ffb4_id |
