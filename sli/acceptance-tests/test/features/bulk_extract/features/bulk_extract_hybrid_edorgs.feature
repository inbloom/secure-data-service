@RALLY_US5859
Feature: As an API user, I want to be able to get a list of links available to the user.

  Background: Validate that the charter School entity is extracted correctly
	Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"

  Scenario: Validate that the charter School entity is extracted correctly with full bulk extract
    Given I clean the bulk extract file system and database
    And I post "SmallSampleDataSet-Charter.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SmallSampleDataSet-Charter.zip" is completed in database
    And I check to find if record is in collection:
      | collectionName              			   | expectedRecordCount | searchParameter     | searchValue                                 | searchType           |
      | educationOrganization                      | 1                   | body.charterStatus  | School Charter                              | string               |
    And in my list of rights I have BULK_EXTRACT
    Then I trigger a bulk extract
    When I retrieve the path to and decrypt the LEA "<IL-DAYBREAK school>" public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  teacherSchoolAssociation              |
      |  studentProgramAssociation             |
      |  reportCard                            |
      |  studentSectionAssociation             |
      |  studentAssessment                     |
      |  course                                |
      |  studentSchoolAssociation              |
      |  staff                                 |
      |  student                               |
      |  disciplineAction                      |
      |  grade                                 |
      |  courseTranscript                      |
      |  studentParentAssociation              |
      |  educationOrganization                 |
      |  studentAcademicRecord                 |
      |  teacherSectionAssociation             |
      |  attendance                            |
      |  gradingPeriod                         |
      |  studentGradebookEntry                 |
      |  parent                                |
      |  studentCohortAssociation              |
      |  section                               |
      |  school                                |
      |  staffEducationOrganizationAssociation |
      |  session                               |
      |  studentCompetency                     |
      |  courseOffering                        |
      |  teacher                               |
      |  gradebookEntry                        |
    And I verify this "educationOrganization" file should contain:
      | id                                          | condition                                                                |
      | 352e8570bd1116d11a72755b987902440045d346_id | charterStatus = School Charter                                           |
      | 352e8570bd1116d11a72755b987902440045d346_id | titleIPartASchoolDesignation = Not designated as a Title I Part A school |
      | 352e8570bd1116d11a72755b987902440045d346_id | administrativeFundingControl = Public School                             |
    And I verify this "studentSchoolAssociation" file should contain:
      | id                                          | condition                                                                |
      | c4e8f7ef1215970305cf9d2fb828c3762afd02a1_id | studentId = b73841a9b1cfacce71dbc7645b12f18ce16c5fca_id                  |
      | c4e8f7ef1215970305cf9d2fb828c3762afd02a1_id | schoolId = 352e8570bd1116d11a72755b987902440045d346_id                   |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition                                                                |
      | 78ded3395d49f8099bf2aa75ade2f7ca181fbae1_id | educationOrganizationReference = 352e8570bd1116d11a72755b987902440045d346_id   |
      | 78ded3395d49f8099bf2aa75ade2f7ca181fbae1_id | staffReference = 8107c5ce31cec58d4ac0b647e91b786b03091f02_id                   |

    When I retrieve the path to and decrypt the LEA "<IL-DAYBREAK>" public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And I verify this "educationOrganization" file should contain:
      | id                                          | condition                                                                |
      | 1b223f577827204a1c7e9c851dba06bea6b031fe_id | nameOfInstitution = Daybreak School District 4529                        |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition                                                                |
      | 1d4648a8bf29459821c50dc57d89823a438f11ca_id | staffReference = 938653e083fa189a5f52360e4db0e207878c3f2e_id                   |
      | 1d4648a8bf29459821c50dc57d89823a438f11ca_id | educationOrganizationReference = 1b223f577827204a1c7e9c851dba06bea6b031fe_id   |


  Scenario: Validate that the charter School entity is extracted correctly with delta bulk extract
    Given I clean the bulk extract file system and database
    And I post "SmallSampleDataSet-Charter.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SmallSampleDataSet-Charter.zip" is completed in database
    And I check to find if record is in collection:
      | collectionName              			   | expectedRecordCount | searchParameter     | searchValue                                 | searchType           |
      | educationOrganization                      | 1                   | body.charterStatus  | School Charter                              | string               |
    And in my list of rights I have BULK_EXTRACT
    Then I trigger a delta extract

    When I verify the last delta bulk extract by app "<app id>" for "<IL-DAYBREAK school>" in "Midgar" contains a file for each of the following entities:
      |  entityType                                |
      |attendance                                  |
      |gradebookEntry                              |
      |section                                     |
      |studentAssessment                           |
      |studentSchoolAssociation                    |
      |course                                      |
      |gradingPeriod                               |
      |session                                     |
      |studentCohortAssociation                    |
      |studentSectionAssociation                   |
      |courseOffering                              |
      |staff                                       |
      |studentCompetency                           |
      |teacher                                     |
      |disciplineAction                            |
      |parent                                      |
      |staffEducationOrganizationAssociation       |
      |studentGradebookEntry                       |
      |teacherSchoolAssociation                    |
      |educationOrganization                       |
      |reportCard                                  |
      |student                                     |
      |studentParentAssociation                    |
      |teacherSectionAssociation                   |
      |grade                                       |
      |school                                      |
      |studentAcademicRecord                       |
      |studentProgramAssociation                   |
    And I verify this "educationOrganization" file should contain:
      | id                                          | condition                                                                |
      | 352e8570bd1116d11a72755b987902440045d346_id | charterStatus = School Charter                                           |
      | 352e8570bd1116d11a72755b987902440045d346_id | titleIPartASchoolDesignation = Not designated as a Title I Part A school |
      | 352e8570bd1116d11a72755b987902440045d346_id | administrativeFundingControl = Public School                             |
    And I verify this "studentSchoolAssociation" file should contain:
      | id                                          | condition                                                                |
      | c4e8f7ef1215970305cf9d2fb828c3762afd02a1_id | studentId = b73841a9b1cfacce71dbc7645b12f18ce16c5fca_id                  |
      | c4e8f7ef1215970305cf9d2fb828c3762afd02a1_id | schoolId = 352e8570bd1116d11a72755b987902440045d346_id                   |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition                                                                |
      | 78ded3395d49f8099bf2aa75ade2f7ca181fbae1_id | educationOrganizationReference = 352e8570bd1116d11a72755b987902440045d346_id   |
      | 78ded3395d49f8099bf2aa75ade2f7ca181fbae1_id | staffReference = 8107c5ce31cec58d4ac0b647e91b786b03091f02_id                   |

    When I verify the last delta bulk extract by app "<app id>" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
      |  entityType                                |
      |attendance                                  |
      |disciplineIncident                          |
      |reportCard                                  |
      |studentAcademicRecord                       |
      |studentProgramAssociation                   |
      |calendarDate                                |
      |educationOrganization                       |
      |school                                      |
      |studentAssessment                           |
      |studentSchoolAssociation                    |
      |cohort                                      |
      |grade                                       |
      |section                                     |
      |studentCohortAssociation                    |
      |studentSectionAssociation                   |
      |course                                      |
      |gradebookEntry                              |
      |session                                     |
      |studentCompetency                           |
      |teacher                                     |
      |courseOffering                              |
      |gradingPeriod                               |
      |staff                                       |
      |studentDisciplineIncidentAssociation        |
      |teacherSchoolAssociation                    |
      |courseTranscript                            |
      |staffEducationOrganizationAssociation       |
      |studentGradebookEntry                       |
      |teacherSectionAssociation                   |
      |disciplineAction                            |
      |parent                                      |
      |student                                     |
      |studentParentAssociation                    |
    And I verify this "educationOrganization" file should contain:
      | id                                          | condition                                                                |
      | 1b223f577827204a1c7e9c851dba06bea6b031fe_id | nameOfInstitution = Daybreak School District 4529                        |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition                                                                |
      | 1d4648a8bf29459821c50dc57d89823a438f11ca_id | staffReference = 938653e083fa189a5f52360e4db0e207878c3f2e_id                   |
      | 1d4648a8bf29459821c50dc57d89823a438f11ca_id | educationOrganizationReference = 1b223f577827204a1c7e9c851dba06bea6b031fe_id   |