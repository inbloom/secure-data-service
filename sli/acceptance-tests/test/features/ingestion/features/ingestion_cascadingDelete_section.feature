@RALLY_US3033
Feature: Safe Deletion and Cascading Deletion 

Background: I have a landing zone route configured
Given I am using local data store
@wip
Scenario: Test insert dataset 
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "prep_cascading_deletion.zip" file as the payload of the ingestion job
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
     | disciplineAction                          |
     | disciplineIncident                        |
     | educationOrganization                     |
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
     | sectionSchoolAssociation                  |
     | session                                   |
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
When zip file is scp to ingestion landing zone
  And a batch job for file "prep_cascading_deletion.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                 2|
     | attendance                               |                 1|
     | calendarDate                             |                 1|
     | cohort                                   |                 1|
     | competencyLevelDescriptor                |                 1|
     | course                                   |                 1|
     | courseOffering                           |                 1|
     | disciplineAction                         |                 1|
     | disciplineIncident                       |                 1|
     | educationOrganization                    |                 3|
     | grade                                    |                 1|
     | gradebookEntry                           |                 1|
     | gradingPeriod                            |                 1|
     | graduationPlan                           |                 1|
     | learningObjective                        |                 1|
     | learningStandard                         |                 1|
     | parent                                   |                 2|
     | program                                  |                 1|
     | recordHash                               |                43|
     | reportCard                               |                 1|
     | section                                  |                 1|
     | session                                  |                 1|
     | staff                                    |                 2|
     | staffCohortAssociation                   |                 1|
     | staffEducationOrganizationAssociation    |                 1|
     | staffProgramAssociation                  |                 1|
     | student                                  |                 1|
     | studentAcademicRecord                    |                 1|
     | studentAssessment                        |                 1|
     | studentCohortAssociation                 |                 1|
     | studentCompetency                        |                 1|
     | studentCompetencyObjective               |                 1|
     | studentDisciplineIncidentAssociation     |                 1|
     | studentObjectiveAssessment               |                 1|
     | studentParentAssociation                 |                 2|
     | studentProgramAssociation                |                 1|
     | studentSchoolAssociation                 |                 1|
     | studentSectionAssociation                |                 1|
     | studentGradebookEntry                    |                 1|
     | courseTranscript                         |                 1|
     | teacherSchoolAssociation                 |                 1|
     | teacherSectionAssociation                |                 1|
    And I should see "Processed 49 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	

Scenario: delete "section", entities referencing "section" should be deleted or remove "section" from its reference list; entities referenced by "section" should not be deleted
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "prep_cascading_deletion_section.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | student                                   |
     | section                                   |
     | staff                                     |
     | attendance                                |
     | assessment                                |
     | gradebookEntry                            |
     | program                                   |
     | session                                   |
     | courseOffering                            |
     | course                                    |
     | educationOrganization                     |
     | studentSectionAssociation                 |    
     | teacherSectionAssociation                 |
When zip file is scp to ingestion landing zone
  And a batch job for file "prep_cascading_deletion_section.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                            |              count|
     | student                                   |					1|
     | section                                   |					1|
     | staff                                     |					1|
     | attendance                                |					1|
     | assessment                                |					1|
     | gradebookEntry                            |					1|
     | program                                   |					1|
     | session                                   |					1|
     | courseOffering                            |					1|
     | course                                    |					1|
     | educationOrganization                     |					3|
     | studentSectionAssociation                 | 					1|   
     | teacherSectionAssociation                 |					1|
   And I check to find if record is in collection:
       | collectionName                        | expectedRecordCount | searchParameter                  | searchValue                                  | searchType   |
       | studentSectionAssociation             | 1                   | body.sectionId                   | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string       |
       | teacherSectionAssociation             | 1                   | body.sectionId                   | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string       |    
       #| attendance                            | 1                   | body.sectionId                   | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string       |         
       #| assessment                            | 1                   | body.sectionId                   | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string       |         
       | gradebookEntry                        | 1                   | body.sectionId                   | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string       |           
    And I should see "Processed 18 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created 
    And I post "cascading_deletion_section.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "cascading_deletion_section.zip" is completed in database
    And a batch job log has been created
	Then I should see following map of entry counts in the corresponding collections:
	 | collectionName                            |              count|
     | student                                   |					1|
     | section                                   |					0|
     | staff                                     |					1|
    # | attendance                                |					0|
    # | assessment                                |					0|
     | gradebookEntry                            |					0|
     | program                                   |					1|
     | session                                   |					1|
     | courseOffering                            |					1|
     | course                                    |					1|
     | educationOrganization                     |					3|
     | studentSectionAssociation                 | 					0|   
     | teacherSectionAssociation                 |					0|
  And I check to find if record is in collection:
       | collectionName                        | expectedRecordCount | searchParameter                  | searchValue                                  | searchType   |
       | studentSectionAssociation             | 0                   | body.sectionId                   | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string       |
       | teacherSectionAssociation             | 0                   | body.sectionId                   | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string       |    
       #| attendance                            | 0                   | body.sectionId                   | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string       |         
       #| assessment                            | 0                   | body.sectionId                   | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string       |         
       | gradebookEntry                        | 0                   | body.sectionId                   | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string       |           
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created 