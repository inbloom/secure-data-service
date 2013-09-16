@RALLY_US5862
Feature: As a user with Bulk Extract rights, I want to authorize a bulk extract app at my level of the ed org hierarchy or below that bulk extracts will be generated and made available to the appropriate users so that
they can pull the data that they need

  Background: Validate that the multi-parents entity is extracted correctly
	Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"

  Scenario: Validate that Parent, Section, Session, StaffCohortAssociation, StaffEdorgAssignment entity is extracted correctly with full bulk extract
    Given I clean the bulk extract file system and database
    And I post "SmallSampleDataSet.zip" file as the payload of the ingestion job
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
    And a batch job for file "SmallSampleDataSet.zip" is completed in database

    And in my list of rights I have BULK_EXTRACT
    Then I trigger a bulk extract
    When I retrieve the path to and decrypt the LEA "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And I verify this "parent" file should contain:
      | id                                          | condition                                                                |
      | 56f4f0ea60923de1474a45519790496b42228050_id | parentUniqueStateId = 9878863645                                         |
    And I verify this "student" file should contain:
      | id                                          | condition                                                                |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id | studentParentAssociation.body.parentId = 56f4f0ea60923de1474a45519790496b42228050_id                                         |
    And I verify this "section" file should contain:
      | id                                          | condition                                                                |
      | 9c3bfdbc08f25bde11130bb90608cf707571dfcb_id | studentSectionAssociation.body.studentId = 067198fd6da91e1aa8d67e28e850f224d6851713_id                                       |
    And I verify this "session" file should contain:
      | id                                          | condition                                                                |
      | 992dba93a065fdd6c736af98158905cfccd8c0e7_id | schoolId = 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id                   |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition                                                                |
      | 347086ba22c007b410946511deebbb3017f49aad_id | educationOrganizationReference = 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id                   |
    When I retrieve the path to and decrypt the LEA "1b223f577827204a1c7e9c851dba06bea6b031fe_id" public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And I verify this "parent" file should contain:
      | id                                          | condition                                                                |
      | 56f4f0ea60923de1474a45519790496b42228050_id | parentUniqueStateId = 9878863645                                         |
    And I verify this "student" file should contain:
      | id                                          | condition                                                                |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id | studentParentAssociation.body.parentId = 56f4f0ea60923de1474a45519790496b42228050_id                                         |
    And I verify this "section" file should contain:
      | id                                          | condition                                                                |
      | 9c3bfdbc08f25bde11130bb90608cf707571dfcb_id | studentSectionAssociation.body.studentId = 067198fd6da91e1aa8d67e28e850f224d6851713_id                                       |
    And I verify this "session" file should contain:
      | id                                          | condition                                                                |
      | 992dba93a065fdd6c736af98158905cfccd8c0e7_id | schoolId = 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id                   |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition                                                                |
      | 347086ba22c007b410946511deebbb3017f49aad_id | educationOrganizationReference = 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id                   |
    When I retrieve the path to and decrypt the LEA "884daa27d806c2d725bc469b273d840493f84b4d_id" public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And I verify this "parent" file should contain:
      | id                                          | condition                                                                |
      | 56f4f0ea60923de1474a45519790496b42228050_id | parentUniqueStateId = 9878863645                                         |
    And I verify this "student" file should contain:
      | id                                          | condition                                                                |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id | studentParentAssociation.body.parentId = 56f4f0ea60923de1474a45519790496b42228050_id                                         |
    And I verify this "section" file should contain:
      | id                                          | condition                                                                |
      | 9c3bfdbc08f25bde11130bb90608cf707571dfcb_id | studentSectionAssociation.body.studentId = 067198fd6da91e1aa8d67e28e850f224d6851713_id                                       |
    And I verify this "session" file should contain:
      | id                                          | condition                                                                |
      | 992dba93a065fdd6c736af98158905cfccd8c0e7_id | schoolId = 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id                   |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition                                                                |
      | 347086ba22c007b410946511deebbb3017f49aad_id | educationOrganizationReference = 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id                   |

  Scenario: Validate that Parent, Section, Session, StaffCohortAssociation, StaffEdorgAssignment entity is extracted correctly with delta bulk extract
    Given I clean the bulk extract file system and database
    And I post "SmallSampleDataSet.zip" file as the payload of the ingestion job
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
    And a batch job for file "SmallSampleDataSet.zip" is completed in database
    And in my list of rights I have BULK_EXTRACT
    Then I trigger a delta extract
    And I retrieve the path to and decrypt the the last delta bulk extract by app "<app id>" for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" in "Midgar"
    And I verify this "parent" file should contain:
      | id                                          | condition                                                                |
      | 56f4f0ea60923de1474a45519790496b42228050_id | parentUniqueStateId = 9878863645                                         |
    And I verify this "student" file should contain:
      | id                                          | condition                                                                |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id | studentParentAssociation.body.parentId = 56f4f0ea60923de1474a45519790496b42228050_id  |
    And I verify this "section" file should contain:
      | id                                          | condition                                                                |
      | 9c3bfdbc08f25bde11130bb90608cf707571dfcb_id | studentSectionAssociation.body.studentId = 067198fd6da91e1aa8d67e28e850f224d6851713_id                                       |
    And I verify this "session" file should contain:
      | id                                          | condition                                                                |
      | 992dba93a065fdd6c736af98158905cfccd8c0e7_id | schoolId = 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id                   |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition                                                                |
      | 347086ba22c007b410946511deebbb3017f49aad_id | educationOrganizationReference = 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id                   |
    And I retrieve the path to and decrypt the the last delta bulk extract by app "<app id>" for "1b223f577827204a1c7e9c851dba06bea6b031fe_id" in "Midgar"
    And I verify this "parent" file should contain:
      | id                                          | condition                                                                |
      | 56f4f0ea60923de1474a45519790496b42228050_id | parentUniqueStateId = 9878863645                                         |
    And I verify this "student" file should contain:
      | id                                          | condition                                                                |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id | studentParentAssociation.body.parentId = 56f4f0ea60923de1474a45519790496b42228050_id  |
    And I verify this "section" file should contain:
      | id                                          | condition                                                                |
      | 9c3bfdbc08f25bde11130bb90608cf707571dfcb_id | studentSectionAssociation.body.studentId = 067198fd6da91e1aa8d67e28e850f224d6851713_id                                       |
    And I verify this "session" file should contain:
      | id                                          | condition                                                                |
      | 992dba93a065fdd6c736af98158905cfccd8c0e7_id | schoolId = 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id                   |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition                                                                |
      | 347086ba22c007b410946511deebbb3017f49aad_id | educationOrganizationReference = 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id                   |
    And I retrieve the path to and decrypt the the last delta bulk extract by app "<app id>" for "884daa27d806c2d725bc469b273d840493f84b4d_id" in "Midgar"
    And I verify this "parent" file should contain:
      | id                                          | condition                                                                |
      | 56f4f0ea60923de1474a45519790496b42228050_id | parentUniqueStateId = 9878863645                                         |
    And I verify this "student" file should contain:
      | id                                          | condition                                                                |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id | studentParentAssociation.body.parentId = 56f4f0ea60923de1474a45519790496b42228050_id  |
    And I verify this "section" file should contain:
      | id                                          | condition                                                                |
      | 9c3bfdbc08f25bde11130bb90608cf707571dfcb_id | studentSectionAssociation.body.studentId = 067198fd6da91e1aa8d67e28e850f224d6851713_id                                       |
    And I verify this "session" file should contain:
      | id                                          | condition                                                                |
      | 992dba93a065fdd6c736af98158905cfccd8c0e7_id | schoolId = 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id                   |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition                                                                |
      | 347086ba22c007b410946511deebbb3017f49aad_id | educationOrganizationReference = 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id                   |