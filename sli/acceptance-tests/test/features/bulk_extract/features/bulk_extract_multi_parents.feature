@RALLY_US5859
Feature: As an API user, I want to be able to get a list of links available to the user.

  Background: Validate that the multi-parents entity is extracted correctly
	Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"

  Scenario: Validate that the multi-parents School entity is extracted correctly with full bulk extract
    Given I clean the bulk extract file system and database
    And I post "SmallSampleDataSet-Charter.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
      | collectionName                            |
      | assessment                                |
      | assessmentFamily                          |
      | assessmentPeriodDescriptor                |
      | attendance                                |
      | calendarDate                              |
      | cohort                                    |
      | competencyLevelDescriptor                 |
      | course                                    |
      | courseOffering                            |
      | courseSectionAssociation                  |
      | courseTranscript                          |
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
      | studentAssessment                         |
      | studentCohortAssociation                  |
      | studentCompetency                         |
      | studentCompetencyObjective                |
      | studentDisciplineIncidentAssociation      |
      | studentGradebookEntry                     |
      | studentParentAssociation                  |
      | studentProgramAssociation                 |
      | studentSchoolAssociation                  |
      | studentSectionAssociation                 |
      | teacher                                   |
      | teacherSchoolAssociation                  |
      | teacherSectionAssociation                 |
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
      |attendance                              |
      |grade                                   |
      |section                                 |
      |studentCohortAssociation                |
      |studentSectionAssociation               |
      |course                                  |
      |gradebookEntry                          |
      |session                                 |
      |studentCompetency                       |
      |teacher                                 |
      |courseOffering                          |
      |gradingPeriod                           |
      |staff                                   |
      |studentDisciplineIncidentAssociation    |
      |teacherSchoolAssociation                |
      |courseTranscript                        |
      |staffEducationOrganizationAssociation   |
      |studentGradebookEntry                   |
      |teacherSectionAssociation               |
      |disciplineAction                        |
      |parent                                  |
      |student                                 |
      |studentParentAssociation                |
      |disciplineIncident                      |
      |reportCard                              |
      |studentAcademicRecord                   |
      |studentProgramAssociation               |
      |educationOrganization                   |
      |school                                  |
      |studentAssessment                       |
      |studentSchoolAssociation                |
    And I verify this "educationOrganization" file should contain:
      | id                                          | condition                                                                |
      | a13489364c2eb015c219172d561c62350f0453f3_id | stateOrganizationId = Daybreak Central High                              |
    And I verify this "studentSchoolAssociation" file should contain:
      | id                                          | condition                                                                |
      | 067e45f799cfbf79d12f494baa13551de2221373_id | studentId = 366e15c0213a81f653cdcf524606edeed3f80f99_id                  |
      | 067e45f799cfbf79d12f494baa13551de2221373_id | schoolId = a13489364c2eb015c219172d561c62350f0453f3_id                   |
    When I retrieve the path to and decrypt the LEA "<IL-DAYBREAK>" public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |attendance|
      |calendarDate|
      |cohort|
      |course|
      |courseOffering|
      |courseTranscript|
      |disciplineIncident|
      |educationOrganization|
      |grade|
      |gradebookEntry|
      |gradingPeriod|
      |parent|
      |reportCard|
      |school|
      |section|
      |session|
      |staff|
      |staffEducationOrganizationAssociation|
      |student|
      |studentAcademicRecord|
      |studentAssessment|
      |studentCohortAssociation|
      |studentDisciplineIncidentAssociation|
      |studentGradebookEntry|
      |studentParentAssociation|
      |studentProgramAssociation|
      |studentSchoolAssociation|
      |studentSectionAssociation|
      |teacher|
      |teacherSchoolAssociation|
      |teacherSectionAssociation|
    And I verify this "educationOrganization" file should contain:
      | id                                          | condition                                                                |
      | a13489364c2eb015c219172d561c62350f0453f3_id | stateOrganizationId = Daybreak Central High                              |
    And I verify this "studentSchoolAssociation" file should contain:
      | id                                          | condition                                                                |
      | 067e45f799cfbf79d12f494baa13551de2221373_id | studentId = 366e15c0213a81f653cdcf524606edeed3f80f99_id                  |
      | 067e45f799cfbf79d12f494baa13551de2221373_id | schoolId = a13489364c2eb015c219172d561c62350f0453f3_id                   |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition                                                                |
      | f5a80ce4a2c20608824e17ae41de0a61d3c42704_id | educationOrganizationReference = a13489364c2eb015c219172d561c62350f0453f3_id   |
      | f5a80ce4a2c20608824e17ae41de0a61d3c42704_id | staffReference = b49545f9d443dfbf93358851c903a9923f6af4dd_id                   |

  Scenario: Validate that the multi-parents school entity is extracted correctly with delta bulk extract
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
      |  entityType                            |
      |attendance                              |
      |grade                                   |
      |section                                 |
      |studentCohortAssociation                |
      |studentSectionAssociation               |
      |course                                  |
      |gradebookEntry                          |
      |session                                 |
      |studentCompetency                       |
      |teacher                                 |
      |courseOffering                          |
      |gradingPeriod                           |
      |staff                                   |
      |studentDisciplineIncidentAssociation    |
      |teacherSchoolAssociation                |
      |courseTranscript                        |
      |staffEducationOrganizationAssociation   |
      |studentGradebookEntry                   |
      |teacherSectionAssociation               |
      |disciplineAction                        |
      |parent                                  |
      |student                                 |
      |studentParentAssociation                |
      |disciplineIncident                      |
      |reportCard                              |
      |studentAcademicRecord                   |
      |studentProgramAssociation               |
      |educationOrganization                   |
      |school                                  |
      |studentAssessment                       |
      |studentSchoolAssociation                |
    And I verify this "educationOrganization" file should contain:
      | id                                          | condition                                                                |
      | a13489364c2eb015c219172d561c62350f0453f3_id | stateOrganizationId = Daybreak Central High                              |
    And I verify this "studentSchoolAssociation" file should contain:
      | id                                          | condition                                                                |
      | 067e45f799cfbf79d12f494baa13551de2221373_id | studentId = 366e15c0213a81f653cdcf524606edeed3f80f99_id                  |
      | 067e45f799cfbf79d12f494baa13551de2221373_id | schoolId = a13489364c2eb015c219172d561c62350f0453f3_id                   |
    When I verify the last delta bulk extract by app "<app id>" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
      |  entityType                            |
      |attendance                              |
      |calendarDate                            |
      |cohort                                  |
      |course                                  |
      |courseOffering                          |
      |courseTranscript                        |
      |educationOrganization                   |
      |grade                                   |
      |gradebookEntry                          |
      |gradingPeriod                           |
      |parent                                  |
      |reportCard                              |
      |school                                  |
      |section                                 |
      |session                                 |
      |staff                                   |
      |staffEducationOrganizationAssociation   |
      |student                                 |
      |studentAcademicRecord                   |
      |studentAssessment                       |
      |studentCohortAssociation                |
      |studentCompetency                       |
      |studentGradebookEntry                   |
      |studentParentAssociation                |
      |studentProgramAssociation               |
      |studentSchoolAssociation                |
      |studentSectionAssociation               |
      |teacher                                 |
      |teacherSchoolAssociation                |
      |teacherSectionAssociation               |
    #And I verify this "educationOrganization" file should contain:
    #  | id                                          | condition                                                                |
    #  | a13489364c2eb015c219172d561c62350f0453f3_id | stateOrganizationId = Daybreak Central High                              |
    #And I verify this "studentSchoolAssociation" file should contain:
     # | id                                          | condition                                                                |
     # | 067e45f799cfbf79d12f494baa13551de2221373_id | studentId = 366e15c0213a81f653cdcf524606edeed3f80f99_id                  |
    #  | 067e45f799cfbf79d12f494baa13551de2221373_id | schoolId = a13489364c2eb015c219172d561c62350f0453f3_id                   |
    #And I verify this "staffEducationOrganizationAssociation" file should contain:
    #  | id                                          | condition                                                                |
    #  | f5a80ce4a2c20608824e17ae41de0a61d3c42704_id | educationOrganizationReference = a13489364c2eb015c219172d561c62350f0453f3_id   |
    #  | f5a80ce4a2c20608824e17ae41de0a61d3c42704_id | staffReference = b49545f9d443dfbf93358851c903a9923f6af4dd_id                   |
