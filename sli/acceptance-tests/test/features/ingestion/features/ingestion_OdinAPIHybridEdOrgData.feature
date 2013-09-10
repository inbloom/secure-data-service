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
  And I should not see an error log file created
  And I should not see a warning log file created

Scenario: Verify the sli verification script confirms everything ingested correctly
    Given the edfi manifest that was generated in the 'generated' directory
    And the tenant is 'Midgar'
    Then the sli-verify script completes successfully

Scenario: Verify specific staff document for IL Charter School Educator ingested correctly: Populated Database
  When I can find a "staff" with "body.teacherUniqueStateId" "chartereducator" in tenant db "Midgar"
    Then the "staff" entity "type" should be "teacher"

Scenario: Verify specific staff document for IL Charter School Admin ingested correctly: Populated Database
  When I can find a "staff" with "body.staffUniqueStateId" "charteradmin" in tenant db "Midgar"
    Then the "staff" entity "type" should be "staff"

Scenario: Verify specific staff document for IL Charter School Leader ingested correctly: Populated Database
  When I can find a "staff" with "body.staffUniqueStateId" "charterleader" in tenant db "Midgar"
    Then the "staff" entity "type" should be "staff"

Scenario: Verify specific staff document for IL Charter School Viewer ingested correctly: Populated Database
  When I can find a "staff" with "body.staffUniqueStateId" "charterviewer" in tenant db "Midgar"
    Then the "staff" entity "type" should be "staff"

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

Scenario: Verify the charter school ingested correctly: Populated Database
  When I can find a "educationOrganization" with "body.stateOrganizationId" "IL-CHARTER-SCHOOL" in tenant db "Midgar"
  Then the "educationOrganization" entity "type" should be "educationOrganization"
  And there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "IL-CHARTER-SCHOOL"
    | field                               | value                                       |
    | _id                                 | ea5ebdedeeb46a4395471d375a9c9e1a6c243aea_id |
    | body.parentEducationAgencyReference | 96179584b4d0dac4119989bc138d857d1cc9daa6_id,884daa27d806c2d725bc469b273d840493f84b4d_id |
    | body.organizationCategories         | Local Education Agency,School                                                           |
