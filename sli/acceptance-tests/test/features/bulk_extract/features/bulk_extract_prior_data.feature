@RALLY_US5904
Feature: An edorg's extract file should contain student data from previous enrollments with other schools

  Scenario: Generate with expired data set using Odin generate tool
    Given I am using the odin working directory
    When I generate the with expired data set in the generated directory
    And I zip generated data under filename OdinSampleDataSet.zip to the new OdinSampleDataSet directory
    And I copy generated data to the new OdinSampleDataSet directory
    Then I should see generated file <File>
      | File  |
      |ControlFile.ctl|
      |InterchangeAssessmentMetadata.xml|
      |InterchangeAttendance.xml|
      |InterchangeEducationOrgCalendar.xml|
      |InterchangeEducationOrganization.xml|
      |InterchangeMasterSchedule.xml|
      |InterchangeStaffAssociation.xml|
      |InterchangeStudentAssessment.xml|
      |InterchangeStudentCohort.xml|
      |InterchangeStudentDiscipline.xml|
      |InterchangeStudentEnrollment.xml|
      |InterchangeStudentGrades.xml|
      |InterchangeStudentParent.xml|
      |InterchangeStudentProgram.xml|
      |OdinSampleDataSet.zip|

  Scenario: Setup the database and trigger an extract
    Given I clean the bulk extract file system and database
    And I am using odin data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And all collections are empty
    And I post "OdinSampleDataSet.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OdinSampleDataSet.zip" is completed in database

    Then I should see following map of entry counts in the corresponding collections:
      | collectionName                           |              count|
      | student                                  |                204|
    And I should not see an error log file created
    And I should not see a warning log file created

  Scenario: The extract for an edorg should contain data for a student from a previously enrolled school
    Given all edorgs in "Midgar" are authorized for "SDK Sample"
    And I trigger an extract for tenant "Midgar"
    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "897755cae2f689c2d565a35a48ea69d5dd3928d6_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | attendance                            |
      | cohort                                |
      | course                                |
      | courseOffering                        |
      | courseTranscript                      |
      | disciplineIncident                    |
      | disciplineAction                      |
      | educationOrganization                 |
      | grade                                 |
      | gradebookEntry                        |
#      | gradingPeriod                         |  #  Only in LEA extracts
      | graduationPlan                        |
      | parent                                |
      | reportCard                            |
      | school                                |
      | section                               |
#      | session                               |  #  Only in LEA extracts
      | staff                                 |
      | staffCohortAssociation                |
      | staffEducationOrganizationAssociation |
      | staffProgramAssociation               |
      | student                               |
      | studentAcademicRecord                 |
      | studentAssessment                     |
      | studentCohortAssociation              |
      | studentCompetency                     |
      | studentDisciplineIncidentAssociation  |
      | studentProgramAssociation             |
      | studentGradebookEntry                 |
      | studentParentAssociation              |
      | studentSchoolAssociation              |
      | studentSectionAssociation             |
      | teacher                               |
      | teacherSchoolAssociation              |
      | teacherSectionAssociation             |
    And I verify this "student" file should contain:
      | id                                          | condition            |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id | entityType = student |
    And I verify this "studentParentAssociation" file should contain:
      | id                                                                                     | condition |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id3382e80b35990e1ea89cdde30339fb0c4b79793d_id | entityType = studentParentAssociation |
    And I verify this "studentProgramAssociation" file should contain:
      | id                                                                                     | condition |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id22f33fc1c35430a40cbff324358dbcc57ac8d9cb_id | entityType = studentProgramAssociation |
    And I verify this "studentCohortAssociation" file should contain:
      | id                                                                                     | condition |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_ide24c9ebff7730244f0e92e4c0a6ef19b9e2a257a_id | entityType = studentCohortAssociation |
    And I verify this "studentDisciplineIncidentAssociation" file should contain:
      | id                                                                                     | condition |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id70db2f1e091ae90da64ee68ccaaf7ecdb960d6d3_id | entityType = studentDisciplineIncidentAssociation |

  Scenario: The extract for an edorg should not contain data for a former student that's dated after the student has left
    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "a13489364c2eb015c219172d561c62350f0453f3_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | attendance                            |
      | cohort                                |
      | course                                |
      | courseOffering                        |
      | courseTranscript                      |
      | disciplineIncident                    |
      | disciplineAction                      |
      | educationOrganization                 |
      | grade                                 |
      | gradebookEntry                        |
#      | gradingPeriod                         |  #  Only in LEA extracts
      | graduationPlan                        |
      | parent                                |
      | reportCard                            |
      | school                                |
      | section                               |
#      | session                               |  #  Only in LEA extracts
      | staff                                 |
      | staffCohortAssociation                |
      | staffEducationOrganizationAssociation |
      | staffProgramAssociation               |
      | student                               |
      | studentAcademicRecord                 |
      | studentAssessment                     |
      | studentCohortAssociation              |
      | studentCompetency                     |
      | studentDisciplineIncidentAssociation  |
      | studentProgramAssociation             |
      | studentGradebookEntry                 |
      | studentParentAssociation              |
      | studentSchoolAssociation              |
      | studentSectionAssociation             |
      | teacher                               |
      | teacherSchoolAssociation              |
      | teacherSectionAssociation             |
    And I verify this "student" file should contain:
      | id                                          | condition            |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id | entityType = student |
    And I verify this "studentParentAssociation" file should contain:
      | id                                                                                     | condition |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id3382e80b35990e1ea89cdde30339fb0c4b79793d_id | entityType = studentParentAssociation |
    And I verify this "studentProgramAssociation" file should not contain:
      | id                                                                                     |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idcbaa5d1f7d53bfa6fd10ccab7839c3f8a1d1eadd_id |
    And I verify this "studentCohortAssociation" file should not contain:
      | id                                                                                     |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idc61a12bdb9bbb3c686ba5e9a41cfcd51a99961be_id |
    And I verify this "studentDisciplineIncidentAssociation" file should not contain:
      | id                                                                                     |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idcca92762298c5d74995e5ddb90c0ae31221dfe71_id |

##########################################################################
#    TIMELINE OF ENROLLMENT OF STUDENT 1 FOR EDGE CASES
#  >>--------------------------------------------------------------------->
#    [-EDORG 1-]   [---EDORG 1---]
#                        [-----------EDORG 2-------------]
#                                [--EDORG 3---]
#                                 [--EDORG 4--]
#
# Student's end date in EDORG 1 is the same as the begin date of EDORG 3
# EDORG 4's begin date is one day after EDORG 3's.

  Scenario: Edge Cases for student enrollment
    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "a13489364c2eb015c219172d561c62350f0453f3_id"
    And there is a metadata file in the extract
    And I verify this "student" file should contain:
      | id                                          | condition            |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id | entityType = student |
    And I verify this "studentParentAssociation" file should contain:
      | id                                                                                     | condition |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_idc43bbfa3df05d4fd2d78a9edfee8fd63fbcf495a_id | entityType = studentParentAssociation |
  #This extract should contain content for anything that began on or before edorg 1's end date with the student
  #Even data from edorg 3 that began on the student's final day with edorg 1 should be included
    And I verify this "studentProgramAssociation" file should contain:
      | id                                                                                     | condition |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id00d49d7745ce3cda27c572722ff0b1a444994f0e_id | entityType = studentProgramAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id7dd356a77a6655418bca535fe5466d5b2121ced7_id | entityType = studentProgramAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id30d200e15de46701a0df6828239e5773681f84ef_id | entityType = studentProgramAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id769ed3b8b1d61f91c801bce0208f68c6430c8730_id | entityType = studentProgramAssociation |
    And I verify this "studentCohortAssociation" file should contain:
      | id                                                                                     | condition |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_idb12389748d8bccb346d465914b2d91d0005581fc_id | entityType = studentCohortAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id12b5335d92a7addbd9ae76b8d52955e7a032e25e_id | entityType = studentCohortAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id4f10bcb60aef1f6d9d9848f93c6673042689dc94_id | entityType = studentCohortAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id8d772bd9018bf3e181c719c51261cd8fafb71b76_id | entityType = studentCohortAssociation |
    And I verify this "studentDisciplineIncidentAssociation" file should contain:
      | id   | condition |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_ide9a308c1b89f083c73d3e9e80f319b0d2bb955e1_id | entityType = studentDisciplineIncidentAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_ida938c5c9dc67f25d2d31f7d04f61ea342d611154_id | entityType = studentDisciplineIncidentAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id466d1e3aad124f2f4c223e75c102f75a15d23eb9_id | entityType = studentDisciplineIncidentAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id6eb54f8a415281c5e7654d1538287011aafa1a9d_id | entityType = studentDisciplineIncidentAssociation |
  #This extract should not contain content for anything that began after edorg 1's end date with the student
  #Given proper data, everything from edorg 4 shouldn't be included
    And I verify this "studentProgramAssociation" file should not contain:
      | id                                                                                     |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id3ff9e2e6a970e85eca11b130a61b1532847c7e47_id |
    And I verify this "studentCohortAssociation" file should not contain:
      | id                                                                                     |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id767bb8420bf91d5f148b06630792394719d3545e_id |
    And I verify this "studentDisciplineIncidentAssociation" file should not contain:
      | id                                                                                     |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id21f97eb4744ac207d5e694ef08af730dca3da045_id |
