@RALLY_US5984
@rc
@sandbox
Feature: Users can receive bulk extracts in sandbox mode

  Background:
    Given I have an open web browser
    And I am running in Sandbox mode

  Scenario: Enable bulk extract app and expand IT Administrators to include bulk extract right
    When I navigate to the Portal home page
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the "Simple" login page
    Then I should be on Portal home page
    And I should see Admin link
    When I click on Admin
    Then the portal should be on the admin page
    When under System Tools, I click on "Register Application"
    And I switch to the iframe
    Then I am redirected to the Application Registration Tool page
    When I clicked on the button Edit for the application "<Pre-installed Bulk Extract App Name>"
    And I expand all nodes
    And I enable all education organizations for this app
    And I click on the checkbox labeled "School"
    And I click on Save
    Then my new apps client ID is present
    And my new apps shared secret is present
    When I switch to the iframe
    Then I am redirected to the Application Registration Tool page
    And "<Pre-installed Bulk Extract App Name>" is enabled for "2" education organizations

    #Add Bulk Extract role to IT Admin
    And I exit out of the iframe
    And I click on Admin
    Then the portal should be on the admin page
    And under System Tools, I click on "Create Custom Roles"
    And I switch to the iframe
    And I edit the group "IT Administrator"
    When I add the right "BULK_EXTRACT" to the group "IT Administrator"
    And I hit the save button
    Then I am no longer in edit mode
    And I switch to the iframe
    And the group "IT Administrator" contains the "right" rights "Bulk IT Administrator"

  Scenario: Operator triggers a full extract for the sandbox tenant
    Given the sandbox extraction zone is empty
    And the operator triggers a bulk extract for the sandbox tenant
    Given the operator triggers a delta for the sandbox tenant

  Scenario: Ingest modifications for deltas
    Given a landing zone
    And I drop the file "StaffAppend.zip" into the landingzone
    When the most recent batch job for file "StaffAppend.zip" has completed successfully
    Then I should not see an error log file created
    And I should not see a warning log file created
    Given I drop the file "NewSimplePublicEntities.zip" into the landingzone
    When the most recent batch job for file "NewSimplePublicEntities.zip" has completed successfully
    Then I should not see an error log file created
    And I should not see a warning log file created

  Scenario: Operator triggers a delta extract for the sandbox tenant
    Given the operator triggers a delta for the sandbox tenant

  Scenario: Impersonate an IT Admin and retrieve extracts
    When I navigate to the Portal home page
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the "Simple" login page
    Then I should be on Portal home page
    When I click on log out
    Then I should be redirected to the impersonation page
    And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in
    And I want to select "rrogers" from the "SmallDatasetUsers" in automatic mode
    Given the pre-existing bulk extract testing app key has been created
    When I navigate to the API authorization endpoint with my client ID
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And there is no bulk extract files in the local directory

    #Edorg Full Extract
    When I get the id for the edorg "IL-DAYBREAK"
    And I request and download a "bulk" extract file for the edorg
    Then there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  attendance                            |
      |  courseTranscript                      |
      |  disciplineIncident                    |
      |  disciplineAction                      |
      |  grade                                 |
      |  gradebookEntry                        |
      |  parent                                |
      |  reportCard                            |
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

    #Edorg Delta Extract
    When there is no bulk extract files in the local directory
    And I make a call to the bulk extract end point "/v1.1/bulk/extract/list" using the certificate for app "<RC Server>"
    And I get back a response code of "200"
    And I store the URL for the latest delta for the edorg
    And I request and download a "delta" extract file for the edorg
    Then there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  staffEducationOrganizationAssociation |

  #Top Level Edorg Full Extract
    When I get the id for the edorg "STANDARD-SEA"
    And I request and download a "bulk" extract file for the edorg
    Then there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  staff                                 |
      |  staffCohortAssociation                |
      |  staffEducationOrganizationAssociation |
      |  staffProgramAssociation               |

  #Top Level Edorg Delta Extract
    When there is no bulk extract files in the local directory
    And I make a call to the bulk extract end point "/v1.1/bulk/extract/list" using the certificate for app "<RC Server>"
    And I get back a response code of "200"
    And I store the URL for the latest delta for the edorg
    And I request and download a "delta" extract file for the edorg
    Then there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  staffEducationOrganizationAssociation |

    #Public Full Extract
    When there is no bulk extract files in the local directory
    And I request and download a "public" extract file for the edorg
    Then there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  assessment                            |
      |  learningObjective                     |
      |  learningStandard                      |
      |  studentCompetencyObjective            |
      |  program                               |
      |  calendarDate                          |
      |  course                                |
      |  courseOffering                        |
      |  educationOrganization                 |
      |  gradingPeriod                         |
      |  session                               |
      |  school                                |
      |  cohort                                |
      |  section                               |

    #Public Delta Extract
    When there is no bulk extract files in the local directory
    And I make a call to the bulk extract end point "/v1.1/bulk/extract/list" using the certificate for app "<RC Server>"
    And I get back a response code of "200"
    And I store the URL for the latest delta for the Public
    And I request and download a "delta" extract file for the edorg
    Then there is a metadata file in the extract
    And the extract contains a file for each of the following entities:
      |  entityType                            |
      |  course                                |
      |  competencyLevelDescriptor             |
      |  studentCompetencyObjective            |
      |  learningObjective                     |
      |  learningStandard                      |
      |  program                               |
      |  calendarDate                          |
