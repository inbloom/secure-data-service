@RALLY_US4835
@rc
@sandbox
Feature: User provisions a landing zone, and preloads Small Sample Dataset

Background:
  Given I have an open web browser
  And I am running in Sandbox mode

  Scenario: Ingestion User Provisions LZ and Pre-loads Small Sample Dataset
	When I navigate to the Portal home page
	And I was redirected to the "Simple" IDP Login page
	When I submit the credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the "Simple" login page  
    Then I should be on Portal home page
    Then I should see Admin link
    And I click on Admin
    Then I should be on the admin page
    And under System Tools, I click on "Create Landing Zone"
    And I switch to the iframe
    When the developer selects to preload "Small Dataset"
    And I switch to the iframe
    Then I get the success message

#Verify the file ingested on the Landing Zone
    Given a landing zone
    And I check for the file "job*.log" every "30" seconds for "900" seconds
    Then the landing zone should contain a file with the message "Processed 4254 records"
    And the landing zone should contain a file with the message "All records processed successfully."
    And I should not see an error log file created
    And I should not see a warning log file created
    And I should see following map of entry counts in the corresponding collections:
         | collectionName                           |              count|
         | assessment                               |                 19|
         | attendance                               |                 75|
         | calendarDate                             |                556|
         | cohort                                   |                  3|
         | competencyLevelDescriptor                |                  0|
         | course                                   |                 95|
         | courseOffering                           |                 95|
         | courseSectionAssociation                 |                  0|
         | disciplineAction                         |                  2|
         | disciplineIncident                       |                  2|
         | educationOrganization                    |                  5|
         | educationOrganizationAssociation         |                  0|
         | educationOrganizationSchoolAssociation   |                  0|
         | grade                                    |                  4|
         | gradebookEntry                           |                 12|
         | gradingPeriod                            |                 17|
         | graduationPlan                           |                  0|
         | learningObjective                        |                198|
         | learningStandard                         |               1499|
         | parent                                   |                  9|
         | program                                  |                  2|
         | recordHash                               |               9479|
         | reportCard                               |                  2|
         | schoolSessionAssociation                 |                  0|
         | section                                  |                 97|
         | sectionAssessmentAssociation             |                  0|
         | sectionSchoolAssociation                 |                  0|
         | session                                  |                 22|
         | sessionCourseAssociation                 |                  0|
         | staff                                    |                 14|
         | staffCohortAssociation                   |                  3|
         | staffEducationOrganizationAssociation    |                 13|
         | staffProgramAssociation                  |                  7|
         | student                                  |                 78|
         | studentAcademicRecord                    |                117|
         | studentAssessment                        |                203|
         | studentCohortAssociation                 |                  6|
         | studentCompetency                        |                 59|
         | studentCompetencyObjective               |                  4|
         | studentDisciplineIncidentAssociation     |                  4|
         | studentParentAssociation                 |                  9|
         | studentProgramAssociation                |                  6|
         | studentSchoolAssociation                 |                167|
         | studentSectionAssociation                |                297|
         | studentGradebookEntry                    |                315|
         | courseTranscript                         |                196|
         | teacherSchoolAssociation                 |                  3|
         | teacherSectionAssociation                |                 11|
