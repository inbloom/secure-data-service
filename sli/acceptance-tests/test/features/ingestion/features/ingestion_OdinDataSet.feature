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
     | student                                  |                 10|
    And I should not see an error log file created
	And I should not see a warning log file created

Scenario: Verify entities in education organization calendar were ingested correctly: Populated Database
    And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                          | searchValue                                   | searchType           |
     | session                     | 1                   | body.sessionName                         | 2001-2002 Year Round session: IL-DAYBREAK     | string               |
     | session                     | 1                   | body.sessionName                         | 2002-2003 Year Round session: IL-DAYBREAK     | string               |
     | session                     | 1                   | body.sessionName                         | 2003-2004 Year Round session: IL-DAYBREAK     | string               |
     | session                     | 2                   | body.schoolYear                          | 2001-2002                                     | string               |
     | session                     | 2                   | body.schoolYear                          | 2002-2003                                     | string               |
     | session                     | 2                   | body.schoolYear                          | 2003-2004                                     | string               |
     | session                     | 6                   | body.term                                | Year Round                                    | string               |
     | session                     | 6                   | body.totalInstructionalDays              | 180                                           | integer              |
     | gradingPeriod               | 6                   | body.gradingPeriodIdentity.gradingPeriod | End of Year                                   | string               |
     | gradingPeriod               | 2                   | body.gradingPeriodIdentity.schoolYear    | 2001-2002                                     | string               |
     | gradingPeriod               | 2                   | body.gradingPeriodIdentity.schoolYear    | 2002-2003                                     | string               |
     | gradingPeriod               | 2                   | body.gradingPeriodIdentity.schoolYear    | 2003-2004                                     | string               |
     | gradingPeriod               | 3                   | body.gradingPeriodIdentity.schoolId      | 880572db916fa468fbee53a68918227e104c10f5_id   | string               |
     | gradingPeriod               | 3                   | body.gradingPeriodIdentity.schoolId      | 1b223f577827204a1c7e9c851dba06bea6b031fe_id   | string               |
     | gradingPeriod               | 6                   | body.totalInstructionalDays              | 180                                           | integer              |

Scenario: Verify entities in student were ingested correctly: Populated Database
    And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                          | searchValue                                   | searchType           |
     | student                     | 10                  | type                                     | student                                       | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 1                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 2                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 3                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 4                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 5                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 6                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 7                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 8                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 9                                             | string               |
     | student                     | 1                   | body.studentUniqueStateId                | 10                                            | string               |
     | student                     | 10                  | schools.entryDate                        | 2001-08-27                                    | string               |
     | student                     | 3                   | schools.entryGradeLevel                  | Sixth grade                                   | string               |
     | student                     | 1                   | schools.entryGradeLevel                  | Kindergarten                                  | string               |
     | student                     | 3                   | schools.entryGradeLevel                  | Ninth grade                                   | string               |     
     | student                     | 6                   | schools.edOrgs                           | 352e8570bd1116d11a72755b987902440045d346_id   | string               |   
     | student                     | 5                   | schools.edOrgs                           | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id   | string               |   
     | student                     | 3                   | schools.edOrgs                           | a13489364c2eb015c219172d561c62350f0453f3_id   | string               |   
     | student                     | 10                  | schools.edOrgs                           | 1b223f577827204a1c7e9c851dba06bea6b031fe_id   | string               |   
     | student                     | 1                   | _id                                      | 9e54047cbfeeee26fed86b0667e98286a2b72791_id   | string               |   
     | studentParentAssociation    | 2                   | body.studentId                           | 9e54047cbfeeee26fed86b0667e98286a2b72791_id   | string               |   

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
    And the document references "student" "schools.entryGradeLevel" with "body.entryGradeLevel"

Scenario: Verify staffEducationOrganizationAssociation references ingested correctly: Populated Database
  When Examining the staffEducationOrganizationAssociation collection references
    Then the document references "educationOrganization" "_id" with "body.educationOrganizationReference"
     And the document references "staff" "_id" with "body.staffReference"

Scenario: Verify teacherSchoolAssociation references ingested correctly: Populated Database
  When Examining the teacherSchoolAssociation collection references
    Then the document references "educationOrganization" "_id" with "body.schoolId"
     And the document references "staff" "_id" with "body.teacherId"

Scenario: Verify entities in specific student document ingested correctly: Populated Database
  When I can find a "student" with "_id" "9e54047cbfeeee26fed86b0667e98286a2b72791_id" in tenant db "Midgar"
    Then the "student" entity "body.race" should be "White"
    And the "student" entity "body.limitedEnglishProficiency" should be "NotLimited"
    And the "student" entity "body.schoolFoodServicesEligibility" should be "Reduced price"  
    And the "student" entity "schools.entryGradeLevel" should be "Kindergarten"
    And the "student" entity "schools.entryGradeLevel" should be "First grade" 
    And the "student" entity "schools.entryGradeLevel" should be "Second grade" 

Scenario: Verify entities in student school association were ingested correctly
    And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                          | searchValue                                   | searchType           |
     | graduationPlan              | 1                   | _id                                      | 438cc6756e65d65da2eabb0968387ad25a3e0b93_id   | string               |
     | studentSchoolAssociation    | 5                   | body.graduationPlanId                    | 438cc6756e65d65da2eabb0968387ad25a3e0b93_id   | string               |

Scenario: Verify the sli verification script confirms everything ingested correctly
    Given the edfi manifest that was generated in the 'generated' directory
    And the tenant is 'Midgar'
    Then the sli-verify script completes successfully

Scenario: Verify the course optioinal fields is ingested correctly
    And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                          | searchValue                                     | searchType           |
     | course                      | 14                  | body.courseLevel                         |Honors                                           | string               |
     | course                      | 2                   | body.courseLevelCharacteristics          |Magnet                                           | string               |
     | course                      | 1                   | body.gradesOffered                       |First grade                                      | string               |
     | course                      | 3                   | body.subjectArea                         |Reading                                          | string               |
     | course                      | 1                   | body.courseDescription                   |this is a course for First grade                 | string               |
     | course                      | 9                   | body.courseGPAApplicability              |Not Applicable                                   | string               |
     | course                      | 12                  | body.courseDefinedBy                     |School                                           | string               |
     | course                      | 14                  | body.careerPathway                       |Science, Technology, Engineering and Mathematics | string               |
     | courseTranscript            | 12                  | body.gradeLevelWhenTaken                 |Seventh grade                                    | string               |
     | courseTranscript            | 26                  | body.finalLetterGradeEarned              |C+                                               | string               |
     | courseTranscript            | 75                  | body.courseRepeatCode                    |RepeatCounted                                    | string               |
     | courseTranscript            | 75                  | body.methodCreditEarned                  |Classroom credit                                 | string               |
     | courseTranscript            | 75                  | body.creditsAttempted.credit             |3                                                | integer              |
     | courseTranscript            | 7                   | body.finalNumericGradeEarned             |80                                               | integer              |
