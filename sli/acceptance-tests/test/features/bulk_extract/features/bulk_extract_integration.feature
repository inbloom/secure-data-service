Feature: A bulk extract is triggered, retrieved through the api, and validated

Scenario: Trigger a bulk extract on ingested data and retrieve the extract through the api
   Given I trigger a bulk extract
   And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
   And in my list of rights I have BULK_EXTRACT
  When I make a call to the bulk extract end point "/bulk/extract/tenant"
  Then I should receive a return code of 403
  When I make a call to the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
   When the return code is 200 I get expected tar downloaded
   Then I check the http response headers
   When I decrypt and save the extracted file
   And I verify that an extract tar file was created for the tenant "Midgar"
   And there is a metadata file in the extract
   And the extract contains a file for each of the following entities:
   |  entityType                            |
   |  attendance                            |
   |  course                                |
   |  courseOffering                        |
   |  courseTranscript                      |
   |  disciplineIncident                    |
   |  disciplineAction                      |
   |  educationOrganization                 |
   |  grade                                 |
   |  gradebookEntry                        |
   |  gradingPeriod                         |
   |  graduationPlan                        |
   |  parent                                |
   |  reportCard                            |
   |  school                                |
   |  session                               |
   |  staff                                 |
   |  staffCohortAssociation                |
   |  staffEducationOrganizationAssociation |
   |  staffProgramAssociation               |
   |  student                               |
   |  studentAcademicRecord                 |
   |  studentAssessment                     |
   |  studentCohortAssociation              |
   |  studentCompetency                     |
   |  studentDisciplineIncidentAssociation  |
   |  studentProgramAssociation             |
   |  studentGradebookEntry                 |
   |  studentSchoolAssociation              |
   |  studentSectionAssociation             |
   |  studentParentAssociation              |
   |  teacher                               |
   |  teacherSchoolAssociation              |
   |  teacherSectionAssociation             |
  When I make a call to the bulk extract end point "/bulk/extract/SEA_IL_ID"
  When the return code is 200 I get expected tar downloaded
  Then I check the http response headers
  When I decrypt and save the extracted file
  And I verify that an extract tar file was created for the tenant "Midgar"
  And there is a metadata file in the extract
  And the extract contains a file for each of the following entities:
    |  entityType                            |
    |  assessment                            |
    |  calendarDate                          |
    |  competencyLevelDescriptor             |
    |  educationOrganization                 |
    |  graduationPlan                        |
    |  learningObjective                     |
    |  learningStandard                      |
    |  program                               |
    |  studentCompetencyObjective            |
    |  course                                |
    |  courseOffering                        |
    |  session                               |
    |  gradingPeriod                         |
    |  school                                |
    |  section                               |
    |  cohort                                |


Scenario: Un-Authorized user cannot use the endpoint
        Given I am logged in using "linda.kim" "balrogs" to realm "IL"
        When I make a call to the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
        Then I should receive a return code of 403


Scenario: Validate the Last-Modified header is in a valid http date format
    #Retrieve file information
    #When I retrieve the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
    When I know the file-length of the extract file

    #Make a head call to retrieve last-modified information
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call retrieve the header for the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request

    #Make a bulk extract request with correct If-Unmodified-Since time based on last-modified
    When the If-Unmodified-Since header field is set to "BEFORE"
    And I make a custom bulk extract API call
    Then I get back a response code of "412"

Scenario: Verify the cleanup script correctly deletes the files and database entries
  When I execute cleanup script for tenant:"Midgar", edorg:"", date:"", path:""
  Then I should not see an extract for tenant "Midgar"

Scenario: Leave an extract for following tests
  Given I trigger a bulk extract
