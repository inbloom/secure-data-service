@RALLY_US5660
@RALLY_US5589
@RALLY_US5753
@RALLY_US5781
@RALLY_US5977
Feature: As an bulk extract user, I want to be able to get the state public entities


Scenario: As a valid user unsuccessful attempt to get public data extract using BEEP when extract has not been triggered
    Given The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "<clientId>"
    And The X509 cert "cert" has been installed in the trust store and aliased
    And the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I make a call to the bulk extract end point "/bulk/extract/public"
    Then I get back a response code of "404"

Scenario: As an bulk extract user, I want to be able to get the state public entities
    Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    Then I remove the edorg with id "IL-Test" from the "Midgar" database
    Then I trigger a bulk extract
    Then I should see "1" bulk extract public data file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    When I retrieve the path to and decrypt the public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  course                                |
      |  courseOffering                        |
      |  educationOrganization                 |
      |  graduationPlan                        |
      |  session                               |
      |  assessment                            |
      |  learningObjective                     |
      |  learningStandard                      |
      |  competencyLevelDescriptor             |
      |  studentCompetencyObjective            |
      |  program                               |
      |  gradingPeriod                         |
      |  calendarDate                          |
      |  school                                |
      |  cohort                                |
      |  section                               |
      |  bellSchedule                          |
      |  classPeriod                           |
  And I verify this "classPeriod" file should contain:
    | id                                          | condition                                                                |
    | eea084077b72e08e47c59b6dcbc002e672b3bba2_id | entityType = classPeriod                                                 |
  And I verify this "bellSchedule" file should contain:
    | id                                          | condition                                                                |
    | e570a3f708b3d28d8b10dff8b5603b038f7b21a0_id | entityType = bellSchedule                                                |


  Scenario Outline: Extract should have all public tenant data for certain entities
    When I retrieve the path to and decrypt the public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
   And a the correct number of "<entity>" was extracted from the database
   And a "<entity>" was extracted with all the correct fields

   Examples:
      | entity                                 |
      |  assessment                            |
      |  learningObjective                     |
      |  learningStandard                      |
      |  competencyLevelDescriptor             |
      |  studentCompetencyObjective            |
      |  program                               |
      |  calendarDate                          |
      |  course                                |
      |  courseOffering                        |
      |  educationOrganization                 |
      |  graduationPlan                        |
      |  session                               |
      |  school                                |
      |  section                               |
      |  cohort                                |
      |  bellSchedule                          |
      |  classPeriod                           |

Scenario: As a valid user get public data extract using BEEP
    Given in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I make a call to the bulk extract end point "/bulk/extract/public"
    And the return code is 200 I get expected tar downloaded
    And I decrypt and save the extracted file
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  course                                |
      |  courseOffering                        |
      |  educationOrganization                 |
      |  graduationPlan                        |
      |  session                               |
      |  assessment                            |
      |  learningObjective                     |
      |  learningStandard                      |
      |  competencyLevelDescriptor             |
      |  studentCompetencyObjective            |
      |  program                               |
      |  gradingPeriod                         |
      |  calendarDate                          |
      |  school                                |
      |  cohort                                |
      |  section                               |
      |  bellSchedule                          |
      |  classPeriod                           |

Scenario Outline: Extract received through the API should have all the valid tenant public data
    When I know where the extracted tar is for tenant "Midgar"
   And a the correct number of "<entity>" was extracted from the database
   And a "<entity>" was extracted with all the correct fields
   Examples:
      | entity                                 |
      |  assessment                            |
	  |  calendarDate                          |
      |  learningObjective                     |
      |  learningStandard                      |
      |  competencyLevelDescriptor             |
      |  studentCompetencyObjective            |
      |  program                               |
      |  course                                |
      |  courseOffering                        |
      |  educationOrganization                 |
      |  graduationPlan                        |
      |  session                               |
      |  calendarDate                          |
      |  school                                |
      |  cohort                                |
      |  section                               |
      |  bellSchedule                          |
      |  classPeriod                           |


  Scenario: As a valid user get public data delta extract using BEEP
  Given in my list of rights I have BULK_EXTRACT
  When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  When I make a call to the bulk extract end point "/bulk/extract/public/delta/2013-04-30T17:22:26.391Z"
  Then I get back a response code of "404"

Scenario: Invalid user tries to access public data
    Given I am a valid 'service' user with an authorized long-lived token "438e472e-a888-46d1-8087-0195f4e37089"
    And in my list of rights I have BULK_EXTRACT
    When I make a call to the bulk extract end point "/bulk/extract/public"
    When I get back a response code of "403"


Scenario: public data delta extract using BEEP with invalid users
  Given in my list of rights I have BULK_EXTRACT
  When I log into "SDK Sample" with a token of "linda.kim", a "Teacher" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  When I make a call to the bulk extract end point "/bulk/extract/public/delta/2013-04-30T17:22:26.391Z"
  Then I get back a response code of "403"

  Scenario: A public data extract can be retrieved from the api by a user with the BULK_EXTRACT right when the app is approved only for the top level edorg
    Given in my list of rights I have BULK_EXTRACT
    And the bulk extract files in the database are scrubbed
    And The bulk extract app hasn't been approved for "Midgar-DAYBREAK" with client id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And The bulk extract app has been approved for "IL" with client id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I trigger a bulk extract
    And I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And I make a call to the bulk extract end point "/bulk/extract/public"
    Then the return code is 200 I get expected tar downloaded
    And I decrypt and save the extracted file
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  course                                |
      |  courseOffering                        |
      |  educationOrganization                 |
      |  graduationPlan                        |
      |  session                               |
      |  assessment                            |
      |  learningObjective                     |
      |  learningStandard                      |
      |  competencyLevelDescriptor             |
      |  studentCompetencyObjective            |
      |  program                               |
      |  gradingPeriod                         |
      |  calendarDate                          |
      |  school                                |
      |  cohort                                |
      |  section                               |
      |  bellSchedule                          |
      |  classPeriod                           |

  Scenario: A public data extract can be retrieved from the api by a user with the BULK_EXTRACT right when the app isn't approved for the top level edorg
    Given in my list of rights I have BULK_EXTRACT
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And The bulk extract app hasn't been approved for "IL" with client id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I trigger a bulk extract
    And I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And I make a call to the bulk extract end point "/bulk/extract/public"
    Then the return code is 200 I get expected tar downloaded
    And I decrypt and save the extracted file
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  course                                |
      |  courseOffering                        |
      |  educationOrganization                 |
      |  graduationPlan                        |
      |  session                               |
      |  assessment                            |
      |  learningObjective                     |
      |  learningStandard                      |
      |  competencyLevelDescriptor             |
      |  studentCompetencyObjective            |
      |  program                               |
      |  gradingPeriod                         |
      |  calendarDate                          |
      |  school                                |
      |  cohort                                |
      |  section                               |
      |  bellSchedule                          |
      |  classPeriod                           |

  Scenario: Where the public entity has no edOrg reference, verify the entity is still extracted for the public extract
    Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And none of the following entities reference any edorg in the tenant "Midgar":
      | entity                                 | path                                   |
      |  graduationPlan                        | body.educationOrganizationId           |
    Then I trigger a bulk extract
    Then I should see "1" bulk extract public data file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    When I retrieve the path to and decrypt the public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  assessment                            |
      |  learningObjective                     |
      |  learningStandard                      |
      |  graduationPlan                        |
      |  competencyLevelDescriptor             |
      |  studentCompetencyObjective            |
      |  program                               |
      |  educationOrganization                 |
      |  school                                |
      |  course                                |
      |  courseOffering                        |
      |  session                               |
      |  gradingPeriod                         |
      |  calendarDate                          |
      |  cohort                                |
      |  section                               |
      |  bellSchedule                          |
      |  classPeriod                           |

  Scenario: As an bulk extract user, I want to be able to get correct state public entities after updating and deleting some entities
    Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    Then I remove the edorg with id "IL-Test" from the "Midgar" database
    Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "BellScheduleAndClassPeriodUpdate_bulkExtract.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BellScheduleAndClassPeriodUpdate_bulkExtract.zip" is completed in database
    And a batch job log has been created
    Then I trigger a bulk extract
    Then I should see "1" bulk extract public data file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    When I retrieve the path to and decrypt the public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  course                                |
      |  courseOffering                        |
      |  educationOrganization                 |
      |  graduationPlan                        |
      |  session                               |
      |  assessment                            |
      |  learningObjective                     |
      |  learningStandard                      |
      |  competencyLevelDescriptor             |
      |  studentCompetencyObjective            |
      |  program                               |
      |  gradingPeriod                         |
      |  calendarDate                          |
      |  school                                |
      |  cohort                                |
      |  section                               |
      |  bellSchedule                          |
      |  classPeriod                           |
    And I verify this "classPeriod" file should contain:
      | id                                          | condition                                                                |
      | eea084077b72e08e47c59b6dcbc002e672b3bba2_id | entityType = classPeriod                                                 |
    And I verify this "bellSchedule" file should contain:
      | id                                          | condition                                                                |
      | e570a3f708b3d28d8b10dff8b5603b038f7b21a0_id | gradeLevels = ["First grade", "High School"]                             |

    Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "BellScheduleAndClassPeriodDeletes_bulkExtract.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BellScheduleAndClassPeriodDeletes_bulkExtract.zip" is completed in database
    And a batch job log has been created
    Then I trigger a bulk extract
    Then I should see "1" bulk extract public data file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    When I retrieve the path to and decrypt the public data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  course                                |
      |  courseOffering                        |
      |  educationOrganization                 |
      |  graduationPlan                        |
      |  session                               |
      |  assessment                            |
      |  learningObjective                     |
      |  learningStandard                      |
      |  competencyLevelDescriptor             |
      |  studentCompetencyObjective            |
      |  program                               |
      |  gradingPeriod                         |
      |  calendarDate                          |
      |  school                                |
      |  cohort                                |
      |  section                               |
      |  classPeriod                           |

Scenario: Clean up the public data in the database
    Given all collections are empty

