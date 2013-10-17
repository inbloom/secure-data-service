@RALLY_US5904 @RALLY_US5908 @RALLY_US5907 @RALLY_US5909 @RALLY_US5905
Feature: An edorg's extract file should contain student and staff data from previous enrollments with other schools

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
    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "897755cae2f689c2d565a35a48ea69d5dd3928d6_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | attendance                            |
      | courseTranscript                      |
      | disciplineIncident                    |
      | disciplineAction                      |
      | grade                                 |
      | gradebookEntry                        |
      | parent                                |
      | reportCard                            |
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
      | e1ddd4b5c0c531a734135ecd461b33cab842c18c_idfce1fd8e96cffd8c1dbf505a6862acfcf914b01b_id | entityType = grade |
    And I verify this "reportCard" file should contain:
      | id                                                                                     | condition               |
      | e325f180753f2f170b2826a26112f1be229cdf63_ida74c24bab9a9ef60755b46422a8d480239498581_id | entityType = reportCard |
      | e1ddd4b5c0c531a734135ecd461b33cab842c18c_id9c0b53684b9d64742c653621239bdd92c6bc4288_id | entityType = reportCard |
    And I verify this "studentAcademicRecord" file should contain:
      | id                                                                                     | condition                          |
      | e325f180753f2f170b2826a26112f1be229cdf63_ide536c2b89ee393a7767b597601b581fd9bbfe4e0_id | entityType = studentAcademicRecord |
      | e1ddd4b5c0c531a734135ecd461b33cab842c18c_id17e81b9ff5c5c728ec22ec3f40e975eea03570a6_id | entityType = studentAcademicRecord |
    And I verify this "attendance" file should contain:
      | id                                          | condition               |
      | 5b2d2eeec0b545269d634aaa760ed6f61f8c5021_id | entityType = attendance |
      | 49e65583c8c5f3e97db81807d5beeb028433053d_id | entityType = attendance |
    And I verify this "courseTranscript" file should contain:
      | id                                          | condition                     |
      | 9d80fafba1ac36587a60002bc83df1ebe13c7c36_id | entityType = courseTranscript |
      | f0e15138c37352a57aab8d70feb6a0cad6c59741_id | entityType = courseTranscript |
    And I verify this "studentGradebookEntry" file should contain:
      | id                                          | condition                          |
      | fc1b5f1ae73a9b0808fb9c6dbc63ba68fe2da206_id | entityType = studentGradebookEntry |
      | c07d6623ddf954dc943949f9200231a760755f8a_id | entityType = studentGradebookEntry |
    And I verify this "gradebookEntry" file should contain:
      | id                                                                                     | condition                   |
      | e0b0e46a34acdf56356183cf22d9c506e4285527_id3082031a18c680ef1cac4dba1c9b8de482654d7a_id | entityType = gradebookEntry |
    And I verify this "studentCompetency" file should contain:
      | id                                          | condition                      |
      | 91d9aa5d5da9dd0e2ae46791a6cc6882aec9a59a_id | entityType = studentCompetency |
    And I verify this "studentSectionAssociation" file should contain:
      | id                                                                                     | condition                              |
      | 49e048fa9d77126a719d5719cfc08c36170981b1_idd5df60e5ffe544f23eb3167542fc582215e6a7a2_id | entityType = studentSectionAssociation |
    And I verify this "staff" file should contain:
      | id                                          | condition          |
      | 589a7e8634d7d284a2ec9fd76d7e1ee64a0f63b5_id | entityType = staff |
    And I verify this "teacher" file should contain:
      | id                                          | condition            |
      | 589a7e8634d7d284a2ec9fd76d7e1ee64a0f63b5_id | entityType = teacher |
    #And I verify this "staffEducationOrganizationAssociation" file should contain:
      #| id                                          | condition                                          |
      #| d0de313091b8d4c249ff1ed47cae0121079f284c_id | entityType = staffEducationOrganizationAssociation |
      #| bb98c67830fa46b204c8a4903e3c0b4525390e4c_id | entityType = staffEducationOrganizationAssociation |

  Scenario: The extract for an edorg should not contain data for a former student or staff that's dated after the person has left
    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "a13489364c2eb015c219172d561c62350f0453f3_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | attendance                            |
      | courseTranscript                      |
      | disciplineIncident                    |
      | disciplineAction                      |
      | grade                                 |
      | gradebookEntry                        |
      | parent                                |
      | reportCard                            |
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
    And I verify this "attendance" file should not contain:
      | id                                          |
      | 49e65583c8c5f3e97db81807d5beeb028433053d_id |
    And I verify this "courseTranscript" file should not contain:
      | id                                          |
      | 0bc385d7a20aa1a9df92627cd841d545d3052b3b_id |
    And I verify this "studentGradebookEntry" file should not contain:
      | id                                          |
      | 28e83915fb2ed72fd074efdcb24f6b1a778a5f57_id |
    And I verify this "gradebookEntry" file should not contain:
      | id                                                                                     |
      | f6b0227d1704b24efb2dae4a21e2d530fb384cca_idde48b510ed6ec71962f2483e1ad33511308db485_id |
    And I verify this "studentCompetency" file should not contain:
      | id                                          |
      | 3c2a56c1531ee76299aec831d2f41dc5bc6ec987_id |
    And I verify this "studentSectionAssociation" file should not contain:
      | id                                                                                     |
      | c44eb520d29bad5d60237f6addc22f769b3448aa_idaf30e6685a85c716c26d5e559bde27017f57f304_id |
    And I verify this "staff" file should contain:
      | id                                          | condition          |
      | 589a7e8634d7d284a2ec9fd76d7e1ee64a0f63b5_id | entityType = staff |
    And I verify this "teacher" file should contain:
      | id                                          | condition            |
      | 589a7e8634d7d284a2ec9fd76d7e1ee64a0f63b5_id | entityType = teacher |
    #And I verify this "staffEducationOrganizationAssociation" file should not contain:
      #| id                                          |
      #| bb98c67830fa46b204c8a4903e3c0b4525390e4c_id |

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
#    TIMELINE OF EMPLOYMENT OF CHARLES GRAY FOR POSITIVE/NEGATIVE CASES
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
    And I verify this "attendance" file should contain:
      | id                                          | condition               |
      | a4e76009f29433910dabda3f5d79b9fd4be3f8a9_id | entityType = attendance |
      | 8ccecad63cb003d3e93d1ce4808204d33ad8859f_id | entityType = attendance |
      | 28e5438c90b728ff4599f83c1fa36fdbabc0dbff_id | entityType = attendance |
    And I verify this "courseTranscript" file should contain:
      | id                                          | condition                     |
      | cb154b7f3fdb1ed9a62a5343c6d4d78addc8d444_id | entityType = courseTranscript |
      | c2c71979a917b74578950b6f976c4314acc9969f_id | entityType = courseTranscript |
      | 5a214a3e596887dffeaf44fdabd4535f33a96646_id | entityType = courseTranscript |
    And I verify this "studentGradebookEntry" file should contain:
      | id                                          | condition                          |
      | 4c301bcd87feb866f5e55301880ab03f7651b3ab_id | entityType = studentGradebookEntry |
      | f707337e00e682b21a9b1dfcd4963164672e2300_id | entityType = studentGradebookEntry |
      | cf29b3421366846cb720ad2aaf0aa77bc6320f10_id | entityType = studentGradebookEntry |
    And I verify this "gradebookEntry" file should contain:
      | id                                                                                     | condition                   |
      | 6e9504c3061a61384cbd9591a52893f07c6af242_id70c4a7aee25bcd0e9c07f370c2987970db065402_id | entityType = gradebookEntry |
      | 0d8ae7beaec1d6ceb44b5e7dae3fa5aa75267c1f_id70b1a2d9af0abfbfd9583a0090adbf9ddb76d644_id | entityType = gradebookEntry |
      | 2bf98e6bef0cfa93c8f824179f3d4d76d6f8eb07_id4e6bd3cc6e9f1f7fe7b7671ddba4f03ce56595da_id | entityType = gradebookEntry |
    And I verify this "studentCompetency" file should contain:
      | id                                          | condition                      |
      | c761c5f2fcc53bb90940e3cd26501a75d0106acc_id | entityType = studentCompetency |
      | 20119d985f13ca5b223a8521972bf3fcac7a8dad_id | entityType = studentCompetency |
      | fd7c6a6862dc7b4257234f477d601300ef4c3fc1_id | entityType = studentCompetency |
    And I verify this "studentSectionAssociation" file should contain:
      | id                                                                                     | condition                              |
      | 6e9504c3061a61384cbd9591a52893f07c6af242_id56a60187f236b69252b085c0ca55c9a1cb8081ab_id | entityType = studentSectionAssociation |
      | 8887531ea737afbd49da86f201e95d1f6fc45571_id50a30c780c85361faec9ac20013d347a619958fc_id | entityType = studentSectionAssociation |
      | 5c593810111e06cd8b5a4e7f315b5b49c16c35b1_id26f3811034fc7d29bdc8ac5250ab1a9fe3ce97d7_id | entityType = studentSectionAssociation |
    And I verify this "staff" file should contain:
      | id                                          | condition          |
      | b49545f9d443dfbf93358851c903a9923f6af4dd_id | entityType = staff |
    And I verify this "teacher" file should contain:
      | id                                          | condition            |
      | b49545f9d443dfbf93358851c903a9923f6af4dd_id | entityType = teacher |
    #And I verify this "staffEducationOrganizationAssociation" file should contain:
      #| id                                          | condition                                          |
      #| 202b88ed9039b0d2c366a94dcba2ab4434257102_id | entityType = staffEducationOrganizationAssociation |
      #| 8c897da11f6d0a0dbb038118dfebade4197d72eb_id | entityType = staffEducationOrganizationAssociation |
      #| e8b5d82e4aa2f0061f4d27797f6a0b4750582c83_id | entityType = staffEducationOrganizationAssociation |
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
    And I verify this "attendance" file should not contain:
      | id                                          |
      | d1c52a1dac17a9a5cde037ca618ccac8b4e368ee_id |
    And I verify this "courseTranscript" file should not contain:
      | id                                          |
      | b848986b74335a114ebee017c4f70659f96850db_id |
    And I verify this "studentGradebookEntry" file should not contain:
      | id                                          |
      | 8c14cc6999e43fd6b90d41a30cbb245cb212b508_id |
    And I verify this "gradebookEntry" file should not contain:
      | id                                                                                     |
      | 7df01fe133b2605d0007dd1fecf9c8f8bc6afbee_id591ed4c7b19326e3ffa2c680b4a469ff413d65f4_id |
    And I verify this "studentCompetency" file should not contain:
      | id                                          |
      | ee9b1b72d1ca9692ff56bb2221a9f136c860d050_id |
    And I verify this "studentSectionAssociation" file should not contain:
      | id                                                                                     |
      | 7df01fe133b2605d0007dd1fecf9c8f8bc6afbee_id07bec3af9633c4bdde1a240e8b003abac7e4fc47_id |
      | 96ad14342c72b986ff6fe42556edb552abd239ca_idf146ce1a9ecb6f7852c9b48f36fee2d0f1bfcd0c_id |
    #And I verify this "staffEducationOrganizationAssociation" file should not contain:
      #| id                                          |
      #| a6c6892f64c60e7fe6a7bb044a0f5131fd99e7f0_id |

