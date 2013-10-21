@RALLY_US5904 @RALLY_US5908 @RALLY_US5907 @RALLY_US5909 @RALLY_US5905
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
      | studentProgramAssociation             |
    And I verify this "student" file should contain:
      | id                                          | condition            |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id | entityType = student |
    And I verify this "studentProgramAssociation" file should contain:
      | id                                                                                     | condition                              |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id6d3f9afe4b7a6fe871bf92aa46d6ee9cca56f6e5_id | entityType = studentProgramAssociation |


  Scenario: The extract for an edorg should not contain data for a former student or staff that's dated after the person has left
    And I request the latest bulk extract delta using the api
    And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "a13489364c2eb015c219172d561c62350f0453f3_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | student                               |
      | studentProgramAssociation             |
    And I verify this "student" file should contain:
      | id                                          | condition            |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id | entityType = student |
    And I verify this "studentProgramAssociation" file should not contain:
      | id                                                                                     |
      | 2474c3b2906eab72c1ee4b06a5c4ebf02d02aace_id58e9a8ae4486e96051b33876b20a8f2cac745408_id |


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

  #Scenario: Edge Cases for student and staff enrollment
#    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "a13489364c2eb015c219172d561c62350f0453f3_id"
#    And there is a metadata file in the extract
#    And I verify this "student" file should contain:
#      | id                                          | condition            |
#      | 067198fd6da91e1aa8d67e28e850f224d6851713_id | entityType = student |
  #This extract should contain content for anything that began on or before DCH's end date with the student
  #Even data from SCH that began on the student's final day with DCH should be included
#    And I verify this "studentProgramAssociation" file should contain:
#      | id                                                                                     | condition                              |
#      | 067198fd6da91e1aa8d67e28e850f224d6851713_ide34acabe3e308a140d76b7bd2da54011be117110_id | entityType = studentProgramAssociation |
#      | 067198fd6da91e1aa8d67e28e850f224d6851713_id56c2e2108230cfdd4fc0602921f4ee724ff8b1a2_id | entityType = studentProgramAssociation |
#      | 067198fd6da91e1aa8d67e28e850f224d6851713_ide2285301a2915907a047b8343f0522de2300031b_id | entityType = studentProgramAssociation |
#      | 067198fd6da91e1aa8d67e28e850f224d6851713_id16ec8cd2cdf977761aa6105868be5339c12e19bc_id | entityType = studentProgramAssociation |
  #This extract should not contain content for anything that began after DCH's end date with the student
  #Given proper data, everything from WSH shouldn't be included
#    And I verify this "studentProgramAssociation" file should not contain:
#      | id                                                                                     |
#      | 067198fd6da91e1aa8d67e28e850f224d6851713_id3401ad622b20c8502b936844cf68293b27c1957e_id |

