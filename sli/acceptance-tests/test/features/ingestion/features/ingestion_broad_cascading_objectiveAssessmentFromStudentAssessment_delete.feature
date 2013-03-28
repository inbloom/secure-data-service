@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Objective Assessment From Student Assessment with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "BroadSetOfTypes.zip" file as the payload of the ingestion job
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
       | recordHash                                |
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
       | studentObjectiveAssessment                |
       | studentParentAssociation                  |
       | studentProgramAssociation                 |
       | studentSchoolAssociation                  |
       | studentSectionAssociation                 |
       | studentGradebookEntry                     |
       | courseTranscript                          |
       | teacher                                   |
       | teacherSchoolAssociation                  |
       | teacherSectionAssociation                 |
       | yearlyTranscript                          |
    When zip file is scp to ingestion landing zone
    Then a batch job for file "BroadSetOfTypes.zip" is completed in database
    And a batch job log has been created
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should see child entities of entityType "objectiveAssessment" with id "58346902a070426a109f451129eeeb1268daed21_idd705e26a138eb9e608e23b4c82fd6257633b7244_id" in the "Midgar" database	
    And I post "BroadObjectiveAssessmentFromStudentAssessmentDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadObjectiveAssessmentFromStudentAssessmentDelete.zip" is completed in database
    And a batch job log has been created
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "58346902a070426a109f451129eeeb1268daed21_idd705e26a138eb9e608e23b4c82fd6257633b7244_id" in the "Midgar" database
    And I should not see any entity mandatorily referring to "58346902a070426a109f451129eeeb1268daed21_idd705e26a138eb9e608e23b4c82fd6257633b7244_id" in the "Midgar" database
	And I should see entities optionally referring to "58346902a070426a109f451129eeeb1268daed21_idd705e26a138eb9e608e23b4c82fd6257633b7244_id" be updated in the "Midgar" database
	