Feature: A bulk extract is triggered, retrived through the api, and validated

Scenario: Trigger a bulk extract on ingested data and retrieve the extract through the api
   Given I trigger a bulk extract

   And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
   And in my list of rights I have BULK_EXTRACT
   When I make a call to the bulk extract end point "/bulk/extract/tenant"
   When the return code is 200 I get expected tar downloaded
   Then I check the http response headers
   When I decrypt and save the extracted file
   And I verify that an extract tar file was created for the tenant "Midgar"
   And there is a metadata file in the extract
   And the extract contains a file for each of the following entities:
   |  entityType                            |
   |  assessment                            |              
   |  attendance                            |
   |  cohort                                |
   |  competencyLevelDescriptor             |
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
   |  learningObjective                     |
   |  learningStandard                      |
   |  parent                                |
   |  program                               |
   |  reportCard                            |
   |  school                                |
   |  section                               |
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
   |  studentCompetencyObjective            |
   |  studentDisciplineIncidentAssociation  |
   |  studentProgramAssociation             |
   |  studentGradebookEntry                 |
   |  studentSchoolAssociation              |
   |  studentSectionAssociation             |
   |  studentParentAssociation              |
   |  teacher                               |
   |  teacherSchoolAssociation              |
   |  teacherSectionAssociation             |
   
   Scenario: Trigger a bulk extract on ingested data and retrieve the lea extract through the api
      Given I trigger a bulk extract

      And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
      And in my list of rights I have BULK_EXTRACT
      #When I make lea bulk extract API call for lea "BLOOP"
      #Then I get back a response code of "403"
      When I make lea bulk extract API call for lea "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
      When the return code is 200 I get expected tar downloaded
      Then I check the http response headers
      When I decrypt and save the extracted file
      And I verify that an extract tar file was created for the tenant "Midgar"
      And there is a metadata file in the extract
      And the extract contains a file for each of the following entities with the appropriate count and does not have certain ids:
      |  entityType                            | count |  id  | 
      #|  assessment                            |   |         |  
      |  attendance                            | 150 |         |
      |  cohort                                |   5|         |
      #|  competencyLevelDescriptor             |   |         |
      #|  course                                |   |         |
      #|  courseOffering                        |   |         |
      #|  courseTranscript                      |   |         |
      #|  disciplineIncident                    |   |         |
      #|  disciplineAction                      |   |         |
      |  educationOrganization                 | 5 |          |
      |  grade                                 | 8 |         |
      #|  gradebookEntry                        |   |         |
      #|  gradingPeriod                         |   |         |
      #|  graduationPlan                        |   |         |
      #|  learningObjective                     |   |         |
      #|  learningStandard                      |   |         |
      |  parent                                | 18 |         |
      #|  program                               |   |         |
      |  reportCard                            | 4 |         |
      # |  school                                |   |        |
      #|  section                               |   |         |
      #|  session                               |   |         |
      |  staff                                 | 10 | 63d4be8a233db1fd14676f1535fa21fe4c5dd466_id |
      #|  staffCohortAssociation                | 7 |         |
      |  staffEducationOrganizationAssociation | 10 | 346a3cc0939419b34283ec6cac2330e19a483f6f_id |
      # |  staffProgramAssociation               | 7 |         |
      |  student                               | 156 |         |
      |  studentAcademicRecord                 | 234 |         |
      |  studentAssessment                     | 406 |         |
      |  studentCohortAssociation              | 12 |         |
      #|  studentCompetency                     |   |         |
      #|  studentCompetencyObjective            |   |         |
      |  studentDisciplineIncidentAssociation  |  8 |         |
      |  studentProgramAssociation             | 12 |         |
      #|  studentGradebookEntry                 |   |         |
      |  studentSchoolAssociation              | 334 |         |
      #|  studentSectionAssociation             |   |         |
      |  studentParentAssociation              | 18 |         |
      |  teacher                               | 3 |         |
      |  teacherSchoolAssociation              | 3  |         |
      #|  teacherSectionAssociation             |   |         |
                                                    
    Scenario: Un-Authorized user cannot use the endpoint
        Given I am logged in using "linda.kim" "balrogs" to realm "IL"
        When I make a call to the bulk extract end point "/bulk/extract/tenant"
        Then I should receive a return code of 403   


Scenario: Validate the Last-Modified header is in a valid http date format
    #Retrieve file information
    When I retrieve the path to and decrypt the extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    When I know the file-length of the extract file

    #Make a head call to retrieve last-modified information
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call retrieve the header for the bulk extract end point "/bulk/extract/tenant"
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request

    #Make a bulk extract request with correct If-Unmodified-Since time based on last-modified
    When the If-Unmodified-Since header field is set to "BEFORE"
    And I make a custom bulk extract API call
    Then I get back a response code of "412"
