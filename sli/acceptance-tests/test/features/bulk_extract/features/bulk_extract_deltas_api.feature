Feature: Retrived through the api a generated delta bulk extract file, and validate the file

Background: I have a landing zone route configured

Scenario: Initialize security trust store for Bulk Extract application and LEAs
  Given The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "<clientId>"
  And The X509 cert "cert" has been installed in the trust store and aliased

#Scenario: Retrieve a generated bulk extract delta for today
#  Given I have delta bulk extract files generated for today

#    And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
#    And in my list of rights I have BULK_EXTRACT
#    When I make API call to retrieve today's delta file
#    Then I should receive a return code of 200
#    Then the return code is 200 I get expected tar downloaded

#    When I save the extracted file
#    And I verify this tar file is the same as the pre-generated delta file

#    When I make API call to retrieve tomorrow's non existing delta files
#    Then I should receive a return code of 404

Scenario: Generate and ingest data set for bulk extract
  Given I am using the odin working directory
    And I am using odin data store 

    When I generate the api data set in the generated directory
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
|manifest.json|


Scenario: Post Odin Sample Data Set
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I am using odin data store 
  And I post "OdinSampleDataSet.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |
     | assessmentFamily                          |
     | assessmentPeriodDescriptor                |
     | attendance                                |
     | calendarDate                              |
     | cohort                                    |
     | competencyLevelDescriptor                 |
     | course                                    |
     | courseOffering                            |
     | courseSectionAssociation                  |
     | courseTranscript                          |
     | disciplineAction                          |
     | disciplineIncident                        |
     | educationOrganization                     |
     | educationOrganizationAssociation          |
     | educationOrganizationSchoolAssociation    |
     | grade                                     |
     | gradebookEntry                            |
     | gradingPeriod                             |
     | graduationPlan                            |
     | learningObjective                         |
     | learningStandard                          |
     | parent                                    |
     | program                                   |
     | reportCard                                |
     | school                                    |
     | schoolSessionAssociation                  |
     | section                                   |
     | sectionAssessmentAssociation              |
     | sectionSchoolAssociation                  |
     | session                                   |
     | sessionCourseAssociation                  |
     | staff                                     |
     | staffCohortAssociation                    |
     | staffEducationOrganizationAssociation     |
     | staffProgramAssociation                   |
     | student                                   |
     | studentAcademicRecord                     |
     | studentAssessment                         |
     | studentCohortAssociation                  |
     | studentCompetency                         |
     | studentCompetencyObjective                |
     | studentDisciplineIncidentAssociation      |
     | studentGradebookEntry                     |
     | studentParentAssociation                  |
     | studentProgramAssociation                 |
     | studentSchoolAssociation                  |
     | studentSectionAssociation                 |
     | teacher                                   |
     | teacherSchoolAssociation                  |
     | teacherSectionAssociation                 |
When zip file is scp to ingestion landing zone
  And a batch job for file "OdinSampleDataSet.zip" is completed in database
  And a batch job log has been created
  
Then I should see following map of entry counts in the corresponding collections:
  | collectionName                           |              count|
  | student                                  |                100|
  And I should not see an error log file created
  And I should not see a warning log file created


Scenario: Generate a bulk extract day 0 delta    
  When inBloom generates a bulk extract delta file
   #And I log in to the inBloom dashboards as "rrogers" and get a token
   And I request the latest bulk extract delta
   And I untar and decrypt the tarfile with cert "<cert>"
    Then The bulk extract tarfile should be empty


Scenario: Ingest education organization and perform delta   
  Given I am using local data store
    And I post "deltas_new_edorg.zip" file as the payload of the ingestion job

  When zip file is scp to ingestion landing zone
   And a batch job for file "<deltaFile>" is completed in database
   And a batch job log has been created 
    Then I should not see an error log file created
     And I should not see a warning log file created

#  When inBloom generates a bulk extract delta file
#   And I log in to the inBloom dashboards as "<user>" and get a token
#   And I request the latest bulk extract delta
#   And I untar and decrypt the tarfile with cert "<cert>"
#    Then I should see "1" entities of type "educationOrganization" in the bulk extract deltas tarfile
#     And a "educationOrganization" was extracted in the same format as the api
#     And each extracted "educationOrganization" delta matches the mongo entry
#  Examples:
#  | deltaFile                 | user       | role             | realm       | tenant | cert                                 |
#  | deltas_new_edorg.zip      | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
#  | deltas_update_edorg.zip   | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |
#  | deltas_move_edorg.zip     | jstevenson | IT Administrator | IL-Daybreak | Midgar | 19cca28d-7357-4044-8df9-caad4b1c8ee4 |


Scenario: Log in to the API and authenticate with the Bulk Extract ClientID
  When I navigate to the API authorization endpoint with my client ID
   And I was redirected to the "Simple" IDP Login page
   And I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
  
  When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
     And I should be able to use the token to make valid API calls
      

Scenario: Create a new education organization through the API and perform delta
#Given format "application/json"
#  When I "POST" a "create" entity of type "educationOrganization"
#    Then I should receive a return code of 200
#  
#  When inBloom generates a bulk extract delta file
#   And I log in to the inBloom dashboards as "jstevenson" and get a token
#   And I request the latest bulk extract delta
#   And I untar and decrypt the tarfile with cert "<cert>"

#  Then I should see "1" entities of type "educationOrganization" in the bulk extract deltas tarfile
#   And a "educationOrganization" was extracted in the same format as the api
#   And each extracted "educationOrganization" delta matches the mongo entry

Scenario: Update an existing education organization through the API and perform delta
#Given format "application/json"
#  When I "POST" a "update" entity of type "educationOrganization"
#    Then I should receive a return code of 200

#  When inBloom generates a bulk extract delta file
#   And I log in to the inBloom dashboards as "jstevenson" and get a token
#   And I request the latest bulk extract delta
#   And I untar and decrypt the tarfile with cert "<cert>"

#  Then I should see "1" entities of type "educationOrganization" in the bulk extract deltas tarfile
#   And a "educationOrganization" was extracted in the same format as the api
#   And each extracted "educationOrganization" delta matches the mongo entry

Scenario: Update an existing education organization with an invalid API call
#Given format "application/json"
#  When I "PUT" a "invalid" entity of type "educationOrganization"
#    Then I should receive a return code of 404

#  When inBloom generates a bulk extract delta file
#   And I log in to the inBloom dashboards as "jstevenson" and get a token
#   And I request the latest bulk extract delta
#   And I untar and decrypt the tarfile with cert "<cert>"

#  Then I should see "0" entities of type "educationOrganization" in the bulk extract deltas tarfile
#   And a "educationOrganization" was extracted in the same format as the api
#   And each extracted "educationOrganization" delta matches the mongo entry