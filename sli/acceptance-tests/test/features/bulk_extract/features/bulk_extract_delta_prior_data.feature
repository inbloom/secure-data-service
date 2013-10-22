@RALLY_US5911
Feature: An edorg's extract file should contain student and staff data from previous enrollments with other schools

  Scenario: Setup the database and trigger an extract
    Given I clean the bulk extract file system and database
    And I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I successfully ingest "PriorDataSetDeltas.zip"
    And all edorgs in "Midgar" are authorized for "SDK Sample"
    And I trigger a delta extract

##########################################################################
#    TIMELINE OF ENROLLMENT OF CARMEN ORTIZ FOR POSITIVE/NEGATIVE CASES
#  >>--------------------------------------------------------------------->
#    [2011-08-26 -DCH- 2012-05-22]
#                                      [2012-08-26 -- SCH--- 2018-05-22]
#
#
#    TIMELINE OF EMPLOYMENT OF EMILY SHEA FOR POSITIVE/NEGATIVE CASES
#  >>--------------------------------------------------------------------------------------------->
#    [2010-08-26 -DCH- 2011-05-22]
#                                      [2013-09-03 -- SCH--- 2014-05-29]
#
#
#  a13489364c2eb015c219172d561c62350f0453f3_id - Daybreak Central High (DCH)
#  897755cae2f689c2d565a35a48ea69d5dd3928d6_id - Sunset Central High (SCH)

  Scenario: The extract for an edorg should contain data for a student or staff from a previously enrolled school
    And I request the latest bulk extract delta using the api
    And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "897755cae2f689c2d565a35a48ea69d5dd3928d6_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | student                               |
      | studentCohortAssociation              |
      | studentDisciplineIncidentAssociation  |
      | studentParentAssociation              |
      | studentProgramAssociation             |
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
    And I verify this "courseTranscript" file should contain:
      | id                                          | condition                     |
      | 9d80fafba1ac36587a60002bc83df1ebe13c7c36_id | entityType = courseTranscript |
      | f0e15138c37352a57aab8d70feb6a0cad6c59741_id | entityType = courseTranscript |
    And I verify this "gradebookEntry" file should contain:
      | id                                                                                     | condition                   |
      | e0b0e46a34acdf56356183cf22d9c506e4285527_id3082031a18c680ef1cac4dba1c9b8de482654d7a_id | entityType = gradebookEntry |

  Scenario: The extract for an edorg should not contain data for a former student or staff that's dated after the person has left
    And I request the latest bulk extract delta using the api
    And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "a13489364c2eb015c219172d561c62350f0453f3_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | student                               |
      | studentCohortAssociation              |
      | studentDisciplineIncidentAssociation  |
      | studentParentAssociation              |
      | studentProgramAssociation             |
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
    And I verify this "courseTranscript" file should not contain:
      | id                                          |
      | 0bc385d7a20aa1a9df92627cd841d545d3052b3b_id |
    And I verify this "gradebookEntry" file should not contain:
      | id                                                                                     |
      | f6b0227d1704b24efb2dae4a21e2d530fb384cca_idde48b510ed6ec71962f2483e1ad33511308db485_id |

##########################################################################
#    TIMELINE OF ENROLLMENT OF MATT SOLLARS FOR EDGE CASES
#  >>--------------------------------------------------------------------->
#    [2009-01-01 -DCH- 2009-12-25]   [2010-08-26 -DCH- 2011-09-05]
#                                                    [2010-09-01 -----------ESH----------------------- 2014-05-22]
#                                                                [2011-09-05 --SCH--- 2013-05-22]
#                                                                 [2011-09-06 --WSH-- 2013-05-23]
#
# Student's end date in DCH is the same as the begin date of SCH
# WSH's begin date is one day after SCH's.
#
#    TIMELINE OF EMPLOYMENT OF CHARLES GRAY FOR EDGE CASES
#  >>--------------------------------------------------------------------------------------------->
#    [2007-05-06 -DCH- 2008-07-16] [2009-08-26 -DCH- 2011-07-22]
#                                             [2010-08-26 ------------ ESH --------- 2013-05-22]
#                                                              [2011-07-22 --- WSH--- 2013-05-23]
#                                                               [2011-07-23 ----- SCH---------- 2018-05-22]
#
# Staff's end date in DCH is the same as the begin date of WSH
# SCH's begin date is one day after WSH's.
#
#
#  a13489364c2eb015c219172d561c62350f0453f3_id - Daybreak Central High (DCH)
#  f63789e8d9f3c8aa4d42bdbec83ca64cc1d2ee16_id - East Side High (ESH)
#  897755cae2f689c2d565a35a48ea69d5dd3928d6_id - Sunset Central High (SCH)
#  b78524194f38795a5c2e422cb7fc8becece062d0_id - West Side High (WSH)

  Scenario: Edge Cases for student and staff enrollment
    And I request the latest bulk extract delta using the api
    And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "a13489364c2eb015c219172d561c62350f0453f3_id"
    And there is a metadata file in the extract
    And I verify this "student" file should contain:
      | id                                          | condition            |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id | entityType = student |
    And I verify this "studentParentAssociation" file should contain:
      | id                                                                                     | condition                             |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_idc43bbfa3df05d4fd2d78a9edfee8fd63fbcf495a_id | entityType = studentParentAssociation |
  #This extract should contain content for anything that began on or before DCH's end date with the student
  #Even data from SCH that began on the student's final day with DCH should be included
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
    And I verify this "courseTranscript" file should contain:
      | id                                          | condition                     |
      | cb154b7f3fdb1ed9a62a5343c6d4d78addc8d444_id | entityType = courseTranscript |
      | c2c71979a917b74578950b6f976c4314acc9969f_id | entityType = courseTranscript |
      | 5a214a3e596887dffeaf44fdabd4535f33a96646_id | entityType = courseTranscript |
    And I verify this "gradebookEntry" file should contain:
      | id                                                                                     | condition                   |
      | 6e9504c3061a61384cbd9591a52893f07c6af242_id70c4a7aee25bcd0e9c07f370c2987970db065402_id | entityType = gradebookEntry |
      | 0d8ae7beaec1d6ceb44b5e7dae3fa5aa75267c1f_id70b1a2d9af0abfbfd9583a0090adbf9ddb76d644_id | entityType = gradebookEntry |
      | 2bf98e6bef0cfa93c8f824179f3d4d76d6f8eb07_id4e6bd3cc6e9f1f7fe7b7671ddba4f03ce56595da_id | entityType = gradebookEntry |

  #This extract should not contain content for anything that began after DCH's end date with the student
  #Given proper data, everything from WSH shouldn't be included
    And I verify this "studentProgramAssociation" file should not contain:
      | id                                                                                     |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id3401ad622b20c8502b936844cf68293b27c1957e_id |
    And I verify this "studentCohortAssociation" file should not contain:
      | id                                                                                     |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_idb675acc4cb309496b14c25e7c3d74d07b60d68ae_id |
    And I verify this "studentDisciplineIncidentAssociation" file should not contain:
      | id                                                                                     |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id488486ca968826efacf5c1941c04e3ab30b83dc9_id |
    And I verify this "courseTranscript" file should not contain:
      | id                                          |
      | b848986b74335a114ebee017c4f70659f96850db_id |
    And I verify this "gradebookEntry" file should not contain:
      | id                                                                                     |
      | 7df01fe133b2605d0007dd1fecf9c8f8bc6afbee_id591ed4c7b19326e3ffa2c680b4a469ff413d65f4_id |



