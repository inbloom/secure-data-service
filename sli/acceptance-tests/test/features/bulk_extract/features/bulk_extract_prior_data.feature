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

##########################################################################
#    TIMELINE OF ENROLLMENT OF CARMEN ORTIZ FOR POSITIVE/NEGATIVE CASES
#  >>--------------------------------------------------------------------->
#    [2011-08-26 -EDORG 1- 2012-05-22]
#                                      [2012-08-26 --EDORG 2--- 2018-05-22]
#
#  a13489364c2eb015c219172d561c62350f0453f3_id - EDORG 1
#  897755cae2f689c2d565a35a48ea69d5dd3928d6_id - EDORG 2

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
      | gradingPeriod                         |
      | graduationPlan                        |
      | parent                                |
      | reportCard                            |
      | school                                |
      | session                               |
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
      | id                                                                                     | condition                             |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id3382e80b35990e1ea89cdde30339fb0c4b79793d_id | entityType = studentParentAssociation |
    And I verify this "studentProgramAssociation" file should contain:
      | id                                                                                     | condition                              |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id6d3f9afe4b7a6fe871bf92aa46d6ee9cca56f6e5_id | entityType = studentProgramAssociation |
    And I verify this "studentCohortAssociation" file should contain:
      | id                                                                                     | condition                             |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idb8cb9f9619c552284b43208290b8e2455137eeed_id | entityType = studentCohortAssociation |
    And I verify this "studentDisciplineIncidentAssociation" file should contain:
      | id                                                                                     | condition                                         |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id92709ce1b529f9825bd4ab623f292c12c083df8e_id | entityType = studentDisciplineIncidentAssociation |
    And I verify this "disciplineAction" file should contain:
      | id                                          | condition                     |
      | c3210dcba5a65f44d37a88528989e39cdbcc6e09_id | entityType = disciplineAction |
    And I verify this "studentAssessment" file should contain:
      | id                                          | condition                      |
      | abf6b39f8c841a247c3e4731a821ea8b86f1c5d1_id | entityType = studentAssessment |
    And I verify this "studentSchoolAssociation" file should contain:
      | id                                          | condition                             |
      | 89c3228f05f5d88d785b4788babbf12c02c9f3f4_id | entityType = studentSchoolAssociation |
    And I verify this "parent" file should contain:
      | id                                          | condition           |
      | 2d6638adf22232b9af30b03ce9e84e707f4cf501_id | entityType = parent |
    And I verify this "disciplineIncident" file should contain:
      | id                                          | condition                       |
      | 5c2d1d70eed68e801d551631eb82636fc9e9a6dc_id | entityType = disciplineIncident |
    And I verify this "grade" file should contain:
      | id                                                                                     | condition          |
      | e325f180753f2f170b2826a26112f1be229cdf63_idd17b5f2c25d83632142b68f96eae69c7c73ccdf4_id | entityType = grade |
    And I verify this "reportCard" file should contain:
      | id                                                                                     | condition               |
      | e325f180753f2f170b2826a26112f1be229cdf63_ida74c24bab9a9ef60755b46422a8d480239498581_id | entityType = reportCard |
    And I verify this "studentAcademicRecord" file should contain:
      | id                                                                                     | condition                          |
      | e325f180753f2f170b2826a26112f1be229cdf63_ide536c2b89ee393a7767b597601b581fd9bbfe4e0_id | entityType = studentAcademicRecord |
    #And I verify this "attendance" file should contain:
      #| id                                          | condition               |
      #| 5b2d2eeec0b545269d634aaa760ed6f61f8c5021_id | entityType = attendance |

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
      | gradingPeriod                         |
      | graduationPlan                        |
      | parent                                |
      | reportCard                            |
      | school                                |
      | session                               |
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
      | id                                                                                     | condition                             |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id3382e80b35990e1ea89cdde30339fb0c4b79793d_id | entityType = studentParentAssociation |
    And I verify this "studentProgramAssociation" file should not contain:
      | id                                                                                     |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id58e9a8ae4486e96051b33876b20a8f2cac745408_id |
    And I verify this "studentCohortAssociation" file should not contain:
      | id                                                                                     |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_idcee230069953f0b915305f33ff9f061bfc832509_id |
    And I verify this "studentDisciplineIncidentAssociation" file should not contain:
      | id                                                                                     |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id2b0fbf2af85b9e850e533ded46d26d77aeaa2e75_id |
    And I verify this "disciplineAction" file should not contain:
      | id                                          |
      | e3d4fe2fdb6af50c87446e9050b74e0d7903f5cb_id |
    And I verify this "studentAssessment" file should not contain:
      | id                                          |
      | b2542b105c09130bc7d3f81b471d1f0f0e481fd8_id |
    And I verify this "studentSchoolAssociation" file should not contain:
      | id                                          |
      | c5a10351b0957620192a7b1c0e3e6fd686173579_id |
    And I verify this "parent" file should contain:
      | id                                          | condition           |
      | 2d6638adf22232b9af30b03ce9e84e707f4cf501_id | entityType = parent |
    And I verify this "disciplineIncident" file should not contain:
      | id                                          |
      | ad0101e8b3efe4d35317175167c9fee11d746b58_id |
    And I verify this "grade" file should not contain:
      | id                                                                                     |
      | e1ddd4b5c0c531a734135ecd461b33cab842c18c_idc215fa1253b26479fea38c153679913544bf3ad0_id |
    And I verify this "reportCard" file should not contain:
      | id                                                                                     |
      | e1ddd4b5c0c531a734135ecd461b33cab842c18c_idfa02e1c8575067c8b43bfaee7da6108ffb4da31d_id |
    And I verify this "studentAcademicRecord" file should not contain:
      | id                                                                                     |
      | e1ddd4b5c0c531a734135ecd461b33cab842c18c_id074f8af9afa35d4bb10ea7cd17794174563c7509_id |
    #And I verify this "attendance" file should not contain:
      #| id                                          |
      #| 49e65583c8c5f3e97db81807d5beeb028433053d_id |

##########################################################################
#    TIMELINE OF ENROLLMENT OF MATT SOLLARS FOR EDGE CASES
#  >>--------------------------------------------------------------------->
#    [2009-01-01 -EDORG 1- 2009-12-25]   [2010-08-26 -EDORG 1- 2011-09-05]
#                                                    [2010-09-01 -----------EDORG 2----------------------- 2014-05-22]
#                                                                        [2011-09-05 --EDORG 3--- 2013-05-22]
#                                                                         [2011-09-06 --EDORG 4--- 2013-05-23]
#
# Student's end date in EDORG 1 is the same as the begin date of EDORG 3
# EDORG 4's begin date is one day after EDORG 3's.
#  a13489364c2eb015c219172d561c62350f0453f3_id - EDORG 1
#  f63789e8d9f3c8aa4d42bdbec83ca64cc1d2ee16_id - EDORG 2
#  897755cae2f689c2d565a35a48ea69d5dd3928d6_id - EDORG 3
#  b78524194f38795a5c2e422cb7fc8becece062d0_id - EDORG 4

  Scenario: Edge Cases for student enrollment
    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "a13489364c2eb015c219172d561c62350f0453f3_id"
    And there is a metadata file in the extract
    And I verify this "student" file should contain:
      | id                                          | condition            |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id | entityType = student |
    And I verify this "studentParentAssociation" file should contain:
      | id                                                                                     | condition                             |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_idc43bbfa3df05d4fd2d78a9edfee8fd63fbcf495a_id | entityType = studentParentAssociation |
    And I verify this "parent" file should contain:
      | id                                          | condition           |
      | 5f8989384287747b1960d16edd95ff2bb318e3bd_id | entityType = parent |
      | 7f5b783a051b72820eab5f8188c45ade72869f0f_id | entityType = parent |
  #This extract should contain content for anything that began on or before edorg 1's end date with the student
  #Even data from edorg 3 that began on the student's final day with edorg 1 should be included
    And I verify this "studentProgramAssociation" file should contain:
      | id                                                                                     | condition                              |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_ide34acabe3e308a140d76b7bd2da54011be117110_id | entityType = studentProgramAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id56c2e2108230cfdd4fc0602921f4ee724ff8b1a2_id | entityType = studentProgramAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_ide2285301a2915907a047b8343f0522de2300031b_id | entityType = studentProgramAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id16ec8cd2cdf977761aa6105868be5339c12e19bc_id | entityType = studentProgramAssociation |
    And I verify this "studentCohortAssociation" file should contain:
      | id                                                                                     | condition                             |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id388413bdbb0059dd85a0451fe1c6ea8c5475d4d1_id | entityType = studentCohortAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id293b5f08004c4385b121091e2cd72a1a33e39392_id | entityType = studentCohortAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_idfe9b9e96676d530866cf5b742ea265d76f0d8a24_id | entityType = studentCohortAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id7de288576a0f32b99789d8f3a6cb773200794aa8_id | entityType = studentCohortAssociation |
    And I verify this "studentDisciplineIncidentAssociation" file should contain:
      | id                                                                                     | condition                                         |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_idd178f903e8fc7f13da40eff90fe04289f8d60180_id | entityType = studentDisciplineIncidentAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id7c81a5b5c57d1eacf611875aa87c44e57e2d4422_id | entityType = studentDisciplineIncidentAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id7776723a42cad712a6771a01aec0d7bb4b4c4ec9_id | entityType = studentDisciplineIncidentAssociation |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id126ec69e8441ecd01db21b4a68b74026e7cfb1b9_id | entityType = studentDisciplineIncidentAssociation |
    And I verify this "disciplineIncident" file should contain:
      | id                                          | condition                       |
      | 86048cb8a09146bf0c241aff921e1d68664961d2_id | entityType = disciplineIncident |
      | 02510a4ee38ab5b6b2b24270c89ab57e3f21e84c_id | entityType = disciplineIncident |
      | ccc1eb03dc0b67c556608ad0d6f1542d7f0e81ac_id | entityType = disciplineIncident |
    And I verify this "disciplineAction" file should contain:
      | id                                          | condition                     |
      | 8487d1a242024f633a945d953483b3fe58ced932_id | entityType = disciplineAction |
      | b7ae083ff970dc7d053db375cf228b4d055e1f10_id | entityType = disciplineAction |
      | 3704e7d33ede429ffaff697f6df37d95749fdfe8_id | entityType = disciplineAction |
      | 707dbc3c10c188bf5351a52a291009fe8f014075_id | entityType = disciplineAction |
    And I verify this "studentSchoolAssociation" file should contain:
      | id                                          | condition                             |
      | 85021515aa5e6b0133d6830aa5a01856a78ecad3_id | entityType = studentSchoolAssociation |
      | 1c560036515238f702e031799673dbb6994d1eaf_id | entityType = studentSchoolAssociation |
      | 728e9428a82847723ed9eab66fa04003827228ee_id | entityType = studentSchoolAssociation |
      | e0c566a0bd2c94298c117c6220ddf0c2465c0945_id | entityType = studentSchoolAssociation |
    And I verify this "studentAssessment" file should contain:
      | id                                          | condition                      |
      | 5154384148dbad6bc84a4b20b1c312e99fb3c004_id | entityType = studentAssessment |
      | 4c7a3fb655b56351f244c3d87bad76fd07b8478f_id | entityType = studentAssessment |
      | 02cbe22e355aea8e59f976247bae5389c491176d_id | entityType = studentAssessment |
    And I verify this "grade" file should contain:
      | id                                                                                     | condition          |
      | 429dc90b61707fa474005db798cec3f46807fa69_idab729c89eb9aa10765955b0da2f6c9bd4e1a2bb6_id | entityType = grade |
      | 429dc90b61707fa474005db798cec3f46807fa69_id6e4d4b52f5caa38f1ae6063fca428908f2c1575d_id | entityType = grade |
      | 429dc90b61707fa474005db798cec3f46807fa69_id3b746ce9c454a4ef2bb8b29c9672bbf1e75a704c_id | entityType = grade |
    And I verify this "reportCard" file should contain:
      | id                                                                                     | condition               |
      | 429dc90b61707fa474005db798cec3f46807fa69_id42fd81249cbc0c15bb99024e300b4d6f801d9a0f_id | entityType = reportCard |
    And I verify this "studentAcademicRecord" file should contain:
      | id                                                                                     | condition                          |
      | 429dc90b61707fa474005db798cec3f46807fa69_id1a62a16dd757629cf502eeeaa9fd4494a0fff115_id | entityType = studentAcademicRecord |
    #And I verify this "attendance" file should contain:
      #| id                                          | condition               |
      #| a4e76009f29433910dabda3f5d79b9fd4be3f8a9_id | entityType = attendance |
      #| 8ccecad63cb003d3e93d1ce4808204d33ad8859f_id | entityType = attendance |
      #| 28e5438c90b728ff4599f83c1fa36fdbabc0dbff_id | entityType = attendance |
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
    And I verify this "studentSchoolAssociation" file should not contain:
      | id                                          |
      | 6beed2cdd5386603d5b0b0e34eb1f87d091e9eb7_id |
    And I verify this "studentAssessment" file should not contain:
      | id                                          |
      | 6697f6850c06b1458bebff42b9ea9b5fdb444e95_id |
    And I verify this "disciplineAction" file should not contain:
      | id                                          |
      | 50bbbe6516a35098047a3b81634cf718ed58ffc4_id |
    And I verify this "disciplineIncident" file should not contain:
      | id                                          |
      | bbd04e4949e29924c6520123c832209dcac9b8c0_id |
    And I verify this "grade" file should not contain:
      | id                                                                                     |
      | b28bc3be4667c80070094a24e1f7bc3a9b9a2893_id0d1ec4954d2640bf3ae2eb758f6c8c86d820dbb1_id |
    And I verify this "reportCard" file should not contain:
      | id                                                                                     |
      | b28bc3be4667c80070094a24e1f7bc3a9b9a2893_id5d7185b665b99461cb92d18f09635d1212eda10b_id |
    And I verify this "studentAcademicRecord" file should not contain:
      | id                                                                                     |
      | b28bc3be4667c80070094a24e1f7bc3a9b9a2893_id9f850bb17e294c429148d0b353f9e0db6c17338c_id |
    #And I verify this "attendance" file should not contain:
      #| id                                          |
      #| d1c52a1dac17a9a5cde037ca618ccac8b4e368ee_id |

