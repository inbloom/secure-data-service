@wip
Feature: A bulk extract is triggered, retrieved through the api, and validated

@sandbox
Scenario: Trigger a bulk extract and retrieve the extract through the api
  When I insert the tenant for developer-email@slidev.org
  Given I trigger an extract for tenant "developer-email@slidev.org"
  And I am a valid 'service' user with an authorized long-lived token "14FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
  And in my list of rights I have BULK_EXTRACT
  When I make a call to the bulk extract end point "/bulk/extract/tenant"
  Then I should receive a return code of 403
  When I make a call to the bulk extract end point "/bulk/extract/1b223f577827204a1c7e9c851dba06bea6b031fe_id"
  When the return code is 200 I get expected tar downloaded
  Then I check the http response headers for tenant "developer-email@slidev.org"
  When I decrypt and save the extracted file
  And I verify that an extract tar file was created for the tenant "developer-email@slidev.org"
  And there is a metadata file in the extract
  And the extract contains a file for each of the following entities with the appropriate count and does not have certain ids:
    |  entityType                            | count |  id                                                                                    |
    |  attendance                            | 74    | a9605e685b48f1762236070db165cfe50961c058_id                                            |
    |  courseTranscript                      | 196   | f93d8f8678c4bcbebe1c8b118d12cda004025304_id                                            |
    |  disciplineIncident                    | 2     | aa752ad920bcf00b341236b230eac76e5a80ef3b_id                                            |
    |  disciplineAction                      | 2     | 481fc191703cb530e38e8d6bfda6eab6bf86512d_id                                            |
    |  grade                                 | 4     | 0ae33a43b05f2be4b9c8af13788ae0b0448370ef_id2bccec3c9e510238c60c627d715fa3fc0d4d7dd3_id |
    |  gradebookEntry                        | 12    | 8a4b9bcd2ca43c5209fd7ab0e1f61dafd1a4f4a2_idd89bfce7c9788c7fc352c8556f491ffb6e324f88_id |
    |  parent                                | 9     | 6bf57bc4b7ec518e78ee16a627b02da2a02a182b_id                                            |
    |  reportCard                            | 2     | 0ae33a43b05f2be4b9c8af13788ae0b0448370ef_id4a997dd12d3122fa839523965aab34e397fb79db_id |
    |  staff                                 | 11    | 63d4be8a233db1fd14676f1535fa21fe4c5dd466_id                                            |
    |  staffCohortAssociation                | 2     | f2cbed0cf5ff1d7a43f77e6a5f0f75f174cc7065_id                                            |
    |  staffEducationOrganizationAssociation | 12    | 346a3cc0939419b34283ec6cac2330e19a483f6f_id                                            |
    |  staffProgramAssociation               | 6     | e0ccea007dca317537e52998c1a7d8267b110787_id                                            |
    |  student                               | 78    |                                                                                        |
    |  studentAcademicRecord                 | 117   | 0ae33a43b05f2be4b9c8af13788ae0b0448370ef_id5dc5dba7a0a4a5dbeefaf195726a5fbc7be98d70_id |
    |  studentAssessment                     | 203   | 2854a67ed0375d5b038caef2b4b39856d15b6d58_id                                            |
    |  studentCohortAssociation              | 6     | 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_idc62d5fe5dcbcb399ec33abe02dccba23b8a16eb6_id |
    |  studentCompetency                     | 59    |  08aad024bc436c450c1dd457beadefe4de0ba3ae_id                                           |
    |  studentDisciplineIncidentAssociation  |  4    | 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_idd41740c1b17aff2baea21a76ed0dc809af1ebf13_id |
    |  studentProgramAssociation             | 6     | 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_id2139ed90ad4b4a9e7d66f47a996fb343a07cde39_id |
    |  studentGradebookEntry                 | 307   | b444b2aabe4eed054c6f1258d44ae4d8721ae855_id                                            |
    |  studentSchoolAssociation              | 168   | 849a010012a384e4ba33c6bb5014a4707e7072c9_id                                            |
    |  studentSectionAssociation             | 298   | 42d78c37610b0110d8cf80d7afb4e556884550e1_ide371ef980b66ae592795248d685d60a16e73e726_id |
    |  studentParentAssociation              | 9     | 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_id5ba0c1c2a60e24e0b01a66d6d68892f16ba29b01_id |
    |  teacher                               | 3     | ac8ff205a6cbb248c761083ac692802a0709e4b1_id                                            |
    |  teacherSchoolAssociation              | 3     | 7c6f6301ed520d8d0606ed525044143e900d456b_id                                            |
    |  teacherSectionAssociation             | 11    | 8a4b9bcd2ca43c5209fd7ab0e1f61dafd1a4f4a2_id173e9047fcb1f753b83e69ccfce4c8f68844ffb4_id |

@sandbox
Scenario: Retrieve the public extract through the api
  Given I am a valid 'service' user with an authorized long-lived token "14FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
  When I make a call to the bulk extract end point "/bulk/extract/public"
  When the return code is 200 I get expected tar downloaded
  Then I check the http response headers for tenant "developer-email@slidev.org"
  When I decrypt and save the extracted file
  And I verify that an extract tar file was created for the tenant "developer-email@slidev.org"
  And there is a metadata file in the extract
  And the extract contains a file for each of the following entities with the appropriate count and does not have certain ids:
    |  entityType                            | count |
    |  assessment                            |  71   |
    |  educationOrganization                 |  11   |
    |  graduationPlan                        |  3    |
    |  learningObjective                     |  948  |
    |  learningStandard                      |  1499 |
    |  program                               |  64   |
    |  course                                |  129  |
    |  courseOffering                        |  163  |
    |  session                               |  24   |
    |  gradingPeriod                         |  19   |
    |  school                                |  7    |
    |  section                               |  165  |
    |  cohort                                |  12   |
@sandbox
  Scenario: Retrieve the LEA/public delta extract through the api
    When I insert the tenant for developer-email@slidev.org
    Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    Given I am a valid 'service' user with an authorized long-lived token "14FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    When I set the header format to "application/json"
    Then I PATCH the "nameOfInstitution" field of the entity specified by endpoint "educationOrganizations/1b223f577827204a1c7e9c851dba06bea6b031fe_id" to '"ILDayBreak"'
    Then I PATCH the "educationalEnvironment" field of the entity specified by endpoint "sections/a963b9a6dd17202116a093d0c3e78698ffaa29ed_id" to '"Homebound"'
    Then I PATCH the "sex" field of the entity specified by endpoint "students/dbc254daaa4fb8ed275d725626c5e33b2c5818a8_id" to '"Male"'
    Then I PATCH the "serialNumber" field of the entity specified by endpoint "studentAssessments/fc13a0d4636449f552220f5f084cc173b3a7f4f2_id" to '"7"'

    Given I trigger a delta extract for tenant "developer-email@slidev.org"
    And I untar and decrypt the "inBloom" delta tarfile for tenant "developer-email@slidev.org" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
    Then I should see "4" bulk extract files
    And The "student" delta was extracted in the same format as the api
    And The "studentAssessment" delta was extracted in the same format as the api

    When I untar and decrypt the "inBloom" public delta tarfile for tenant "developer-email@slidev.org" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    Then The "educationOrganization" delta was extracted in the same format as the api
    And The "section" delta was extracted in the same format as the api

