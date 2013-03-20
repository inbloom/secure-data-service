@RALLY_US4816
Feature: Odin Data Set Ingestion Correctness and Fidelity
Background: I have a landing zone route configured
Given I am using odin data store 

Scenario: Post Odin Sample Data Set
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "OdinSampleDataSet.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |
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
     | student                                  |               1000|
    And I should not see an error log file created
	And I should not see a warning log file created

Scenario: Verify specific staff document for Rebecca Braverman ingested correctly: Populated Database
  When I can find a "staff" with "body.teacherUniqueStateId" "rbraverman" in tenant db "Midgar"
    Then the "staff" entity "type" should be "teacher"

Scenario: Verify specific staff document for Charles Gray ingested correctly: Populated Database
  When I can find a "staff" with "body.teacherUniqueStateId" "cgray" in tenant db "Midgar"
    Then the "staff" entity "type" should be "teacher"

Scenario: Verify specific staff document for Linda Kim ingested correctly: Populated Database
  When I can find a "staff" with "body.teacherUniqueStateId" "linda.kim" in tenant db "Midgar"
    Then the "staff" entity "type" should be "teacher"
    
Scenario: Verify superdoc studentSchoolAssociation references ingested correctly: Populated Database
  When Examining the studentSchoolAssociation collection references
    Then the document references "educationOrganization" "_id" with "body.schoolId"
    And the document references "student" "_id" with "body.studentId"
    And the document references "student" "schools._id" with "body.schoolId"
    And the document references "student" "schools.entryDate" with "body.entryDate"
    #And the document references "student" "schools.entryGradeLevel" with "body.entryGradeLevel"

Scenario: Verify staffEducationOrganizationAssociation references ingested correctly: Populated Database
  When Examining the staffEducationOrganizationAssociation collection references
    Then the document references "educationOrganization" "_id" with "body.educationOrganizationReference"
     And the document references "staff" "_id" with "body.staffReference"

Scenario: Verify teacherSchoolAssociation references ingested correctly: Populated Database
  When Examining the teacherSchoolAssociation collection references
    Then the document references "educationOrganization" "_id" with "body.schoolId"
     And the document references "staff" "_id" with "body.teacherId"

Scenario: Verify the sli verification script confirms everything ingested correctly
    Given the edfi manifest that was generated in the 'generated' directory
    And the tenant is 'Midgar'
    #Then the sli-verify script completes successfully