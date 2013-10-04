@RALLY_US5904
Feature: An edorg's extract file should contain student data from previous enrollments with other schools

  Scenario: Setup the database and trigger an extract
    Given I clean the bulk extract file system and database
    And I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And all collections are empty
    And I successfully ingest "PriorDataSet.zip"
    And all edorgs in "Midgar" are authorized for "SDK Sample"
    And I trigger an extract for tenant "Midgar"

  Scenario: The extract for an edorg should contain data for a student from a previously enrolled school
    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "897755cae2f689c2d565a35a48ea69d5dd3928d6_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | attendance                            |
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
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id6d3f9afe4b7a6fe871bf92aa46d6ee9cca56f6e5_id | entityType = studentProgramAssociation |
    And I verify this "studentCohortAssociation" file should contain:
      | id                                                                                     | condition |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idb8cb9f9619c552284b43208290b8e2455137eeed_id | entityType = studentCohortAssociation |
    And I verify this "studentDisciplineIncidentAssociation" file should contain:
      | id                                                                                     | condition |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id92709ce1b529f9825bd4ab623f292c12c083df8e_id | entityType = studentDisciplineIncidentAssociation |
    And I verify this "disciplineAction" file should contain:
      | id                                                                                     | condition |
      | c3210dcba5a65f44d37a88528989e39cdbcc6e09_id | entityType = disciplineAction |
    And I verify this "studentAssessment" file should contain:
      | id                                                                                     | condition |
      | abf6b39f8c841a247c3e4731a821ea8b86f1c5d1_id | entityType = studentAssessment |
    And I verify this "studentSchoolAssociation" file should contain:
      | id                                                                                     | condition |
      | 89c3228f05f5d88d785b4788babbf12c02c9f3f4_id | entityType = studentSchoolAssociation |
    And I verify this "parent" file should contain:
      | id                                                                                     | condition |
      | 2d6638adf22232b9af30b03ce9e84e707f4cf501_id | entityType = parent |

  Scenario: The extract for an edorg should not contain data for a former student that's dated after the student has left
    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "a13489364c2eb015c219172d561c62350f0453f3_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | attendance                            |
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
      | id                                                                                     | condition |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id58e9a8ae4486e96051b33876b20a8f2cac745408_id |           |
    And I verify this "studentCohortAssociation" file should not contain:
      | id                                                                                     | condition |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idcee230069953f0b915305f33ff9f061bfc832509_id |           |
    And I verify this "studentDisciplineIncidentAssociation" file should not contain:
      | id                                                                                     | condition |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id2b0fbf2af85b9e850e533ded46d26d77aeaa2e75_id |           |


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
      | 067198fd6da91e1aa8d67e28e850f224d6851713_ide34acabe3e308a140d76b7bd2da54011be117110_id | entityType = studentProgramAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id56c2e2108230cfdd4fc0602921f4ee724ff8b1a2_id | entityType = studentProgramAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_ide2285301a2915907a047b8343f0522de2300031b_id | entityType = studentProgramAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id16ec8cd2cdf977761aa6105868be5339c12e19bc_id | entityType = studentProgramAssociation |
    And I verify this "studentCohortAssociation" file should contain:
      | id                                                                                     | condition |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id388413bdbb0059dd85a0451fe1c6ea8c5475d4d1_id | entityType = studentCohortAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id293b5f08004c4385b121091e2cd72a1a33e39392_id | entityType = studentCohortAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_idfe9b9e96676d530866cf5b742ea265d76f0d8a24_id | entityType = studentCohortAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id7de288576a0f32b99789d8f3a6cb773200794aa8_id | entityType = studentCohortAssociation |
    And I verify this "studentDisciplineIncidentAssociation" file should contain:
      | id   | condition |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_idd178f903e8fc7f13da40eff90fe04289f8d60180_id | entityType = studentDisciplineIncidentAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id7c81a5b5c57d1eacf611875aa87c44e57e2d4422_id | entityType = studentDisciplineIncidentAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id7776723a42cad712a6771a01aec0d7bb4b4c4ec9_id | entityType = studentDisciplineIncidentAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id126ec69e8441ecd01db21b4a68b74026e7cfb1b9_id | entityType = studentDisciplineIncidentAssociation |
  #This extract should not contain content for anything that began after edorg 1's end date with the student
  #Given proper data, everything from edorg 4 shouldn't be included
    And I verify this "studentProgramAssociation" file should not contain:
      | id                                                                                     |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id3401ad622b20c8502b936844cf68293b27c1957e_id |
    And I verify this "studentCohortAssociation" file should not contain:
      | id                                                                                     |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_idb675acc4cb309496b14c25e7c3d74d07b60d68ae_id |
    And I verify this "studentDisciplineIncidentAssociation" file should not contain:
      | id                                                                                     |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id488486ca968826efacf5c1941c04e3ab30b83dc9_id |
