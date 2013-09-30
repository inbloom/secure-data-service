@RALLY_US5904
@wip
Feature: An edorg's extract file should contain student data from previous enrollments with other schools

  Scenario: Setup the database and trigger an extract
    Given I clean the bulk extract file system and database
    And I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And all collections are empty
    And I successfully ingest "<New Dataset>"
  #TODO: Substitute with actual name of dataset
  #Previous Step can be replaced with odin generation and ingest steps, if desired
    And all edorgs in "Midgar" are authorized for "SDK Sample"
    And I trigger a bulk extract

  Scenario: The extract for an edorg should contain data for a student from a previously enrolled school
  #TODO: Substitute with actual id of edorg
    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "<edorg id>"
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
      | gradingPeriod                         |
      | graduationPlan                        |
      | parent                                |
      | reportCard                            |
      | school                                |
      | section                               |
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
    #TODO: Substitute with actual ids
      | id   | condition            |
      | <id> | entityType = student |
    And I verify this "studentParentAssociation" file should contain:
    #TODO: Substitute with actual ids
      | id   | condition                             |
      | <id> | entityType = studentParentAssociation |
    And I verify this "studentProgramAssociation" file should contain:
    #TODO: Substitute with actual ids
      | id   | condition                              |
      | <id> | entityType = studentProgramAssociation |
    And I verify this "studentCohortAssociation" file should contain:
    #TODO: Substitute with actual ids
      | id   | condition                             |
      | <id> | entityType = studentCohortAssociation |
    And I verify this "studentDisciplineIncidentAssociation" file should contain:
    #TODO: Substitute with actual ids
      | id   | condition                                         |
      | <id> | entityType = studentDisciplineIncidentAssociation |

  Scenario: The extract for an edorg should not contain data for a former student that's dated after the student has left
  #TODO: Substitute with actual id of edorg
    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "<edorg 2 id>"
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
      | gradingPeriod                         |
      | graduationPlan                        |
      | parent                                |
      | reportCard                            |
      | school                                |
      | section                               |
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
    #TODO: Substitute with actual ids
      | id   | condition            |
      | <id> | entityType = student |
    And I verify this "studentParentAssociation" file should contain:
    #TODO: Substitute with actual ids
      | id   | condition                             |
      | <id> | entityType = studentParentAssociation |
    And I verify this "studentProgramAssociation" file should not contain:
    #TODO: Substitute with actual ids
      | id   |
      | <id> |
    And I verify this "studentCohortAssociation" file should not contain:
    #TODO: Substitute with actual ids
      | id   |
      | <id> |
    And I verify this "studentDisciplineIncidentAssociation" file should not contain:
    #TODO: Substitute with actual ids
      | id   |
      | <id> |

##########################################################################
#    TIMELINE OF ENROLLMENT OF STUDENT 1 FOR EDGE CASES
#  >>--------------------------------------------------------------------->
#    [---EDORG 1--]
#            [-----------EDORG 2-----]
#                 [--EDORG 3---]
#                  [--EDORG 4--]
#
# Student's end date in EDORG 1 is the same as the begin date of EDORG 3
# EDORG 4's begin date is one day after EDORG 3's.

  Scenario: Edge Cases for student enrollment
  #TODO: Substitute with actual id of edorg
    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "<edorg 1 id>"
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
      | gradingPeriod                         |
      | graduationPlan                        |
      | parent                                |
      | reportCard                            |
      | school                                |
      | section                               |
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
    #TODO: Substitute with actual ids
      | id   | condition            |
      | <id> | entityType = student |
    And I verify this "studentParentAssociation" file should contain:
    #TODO: Substitute with actual ids
      | id   | condition                             |
      | <id> | entityType = studentParentAssociation |
  #This extract should contain content for anything that began on or before edorg 1's end date with the student
  #Even data from edorg 3 that began on the student's final day with edorg 1 should be included
    And I verify this "studentProgramAssociation" file should contain:
    #TODO: Substitute with actual ids
      | id   | condition                              |
      | <id> | entityType = studentProgramAssociation |
      | <id> | entityType = studentProgramAssociation |
      | <id> | entityType = studentProgramAssociation |
    And I verify this "studentCohortAssociation" file should contain:
    #TODO: Substitute with actual ids
      | id   | condition                             |
      | <id> | entityType = studentCohortAssociation |
      | <id> | entityType = studentCohortAssociation |
      | <id> | entityType = studentCohortAssociation |
    And I verify this "studentDisciplineIncidentAssociation" file should contain:
    #TODO: Substitute with actual ids
      | id   | condition                                         |
      | <id> | entityType = studentDisciplineIncidentAssociation |
      | <id> | entityType = studentDisciplineIncidentAssociation |
      | <id> | entityType = studentDisciplineIncidentAssociation |
  #This extract should not contain content for anything that began after edorg 1's end date with the student
  #Given proper data, everything from edorg 4 shouldn't be included
    And I verify this "studentProgramAssociation" file should not contain:
    #TODO: Substitute with actual ids
      | id   |
      | <id> |
    And I verify this "studentCohortAssociation" file should not contain:
    #TODO: Substitute with actual ids
      | id   |
      | <id> |
    And I verify this "studentDisciplineIncidentAssociation" file should not contain:
    #TODO: Substitute with actual ids
      | id   |
      | <id> |