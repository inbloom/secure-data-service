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

@wip	
Scenario: delete basic student, associated entities should be deleted, non-associated entities should not be deleted
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "prep_cascading_deletion.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | student                                   |
     | studentParentAssociation                  |
     | studentAssessment                         |
     | studentAcademicRecord                     |
     | studentProgramAssociation                 |
     | attendance                                |
     | studentSchoolAssociation                  |
     | studentSectionAssociation                 |
     | parent                                    |
     | reportCard                                |
     | session                                   |
     | educationOrganization                     |
     | program                                   |
     | section                                   |
     | graduationPlan                            |
     | course                                    |
     | courseTranscript                          |
     | grade                                     |
     | studentGradebookEntry                     |
     | gradebookEntry                            |
     | assessment                                |
     | disciplineIncident                        |
     | disciplineAction                          |
     | studentDisciplineIncidentAssociation      |
     | studentCompetency                         |
When zip file is scp to ingestion landing zone
  And a batch job for file "prep_cascading_deletion.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                            |              count|
     | student                                   |					1|
     | studentParentAssociation                  |					2|
     | studentAssessment                         |					1|
     | studentAcademicRecord                     |					1|
     | studentProgramAssociation                 |					1|
     | attendance                                |					1|
     | studentSchoolAssociation                  |					1|
     | studentSectionAssociation                 |					1|
     | parent                                    |					2|
     | reportCard                                |					1|
     | session                                   |					1|
     | educationOrganization                     |					3|
     | program                                   |					1|
     | section                                   |					1|
     | graduationPlan                            |					1|
     | course                                    |					1|
     | courseTranscript                          |					1|
     | grade                                     |                  1|
     | studentGradebookEntry                     |					1|
     | gradebookEntry                            |					1|
     | assessment                                |					2|
     | disciplineIncident                        |					1|
     | disciplineAction                          |					1|
     | studentDisciplineIncidentAssociation      |					1|
     | studentCompetency                         |                  1|
    And I check to find if record is in collection:
       | collectionName                        | expectedRecordCount | searchParameter     | searchValue                                  | searchType           |
       | studentSchoolAssociation              | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentAssessment                     | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentGradebookEntry                 | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentParentAssociation              | 2                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentAcademicRecord                 | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentProgramAssociation             | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentSectionAssociation             | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentGradebookEntry                 | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | disciplineAction                      | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentDisciplineIncidentAssociation  | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | attendance                            | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | reportCard                            | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |   
       | studentCompetency                     | 1                   | body.studentSectionAssociationId      | 6df6309cd7609257f454ac8b7456e3943f4d6190_id11d22f998a39f5db6ccfa55264a3629637733195_id  | string     |   
       | grade                                 | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |      
    And I should see "Processed 49 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I post "cascading_deletion_student.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "cascading_deletion_student.zip" is completed in database
    And a batch job log has been created
	Then I should see following map of entry counts in the corresponding collections:
     | collectionName                            |              count|
     | student                                   |					0|
     | studentParentAssociation                  |					0|
     | studentAssessment                         |					0|
     | studentAcademicRecord                     |					0|
     | studentProgramAssociation                 |					0|
     | attendance                                |					0|
     | studentSchoolAssociation                  |					0|
     | studentSectionAssociation                 |					0|
     | parent                                    |					2|
     | reportCard                                |					0|
     | session                                   |					1|
     | educationOrganization                     |					3|
     | program                                   |					1|
     | section                                   |					1|
     | graduationPlan                            |					1|
     | course                                    |					1|
     | courseTranscript                          |					0|
     | grade                                     |                  0|
     | studentGradebookEntry                     |					0|
     | gradebookEntry                            |					1|
     | assessment                                |					2|
     | disciplineIncident                        |					1|
     | disciplineAction                          |					0|
     | studentDisciplineIncidentAssociation      |					0|
     | studentCompetency                         |                  0|
    And I check to find if record is in collection:
       | collectionName                        | expectedRecordCount | searchParameter     | searchValue                                  | searchType |
       | studentSchoolAssociation              | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentAssessment                     | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentGradebookEntry                 | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentParentAssociation              | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentAcademicRecord                 | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentProgramAssociation             | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentSectionAssociation             | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentGradebookEntry                 | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | disciplineAction                      | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | studentDisciplineIncidentAssociation  | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | attendance                            | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |
       | reportCard                            | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |   
       | studentCompetency                     | 0                   | body.studentSectionAssociationId      | 6df6309cd7609257f454ac8b7456e3943f4d6190_id11d22f998a39f5db6ccfa55264a3629637733195_id  | string     |   
       | grade                                 | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |      
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
@wip	
Scenario: delete "Jets School", AssignmentSchoolReference is wiped out in the DisciplineAction, ResponsibilitySchoolReference still points to "Sharks School"; delete "Sharks School", DisciplineAction is deleted.
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "prep_cascading_deletion_school.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | student                                   |
     | staff                                     |
     | educationOrganization                     |
     | disciplineAction                          |
     | disciplineIncident                        |
When zip file is scp to ingestion landing zone
  And a batch job for file "prep_cascading_deletion_school.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                            |              count|
     | student                                   |					1|
     | staff                                     |                  1|
     | educationOrganization                     |					5|
     | disciplineAction                          |					1|
     | disciplineIncident                        |					1|
    And I check to find if record is in collection:
       | collectionName                        | expectedRecordCount | searchParameter                  | searchValue                                  | searchType   |
       | disciplineAction                      | 1                   | body.responsibilitySchoolId      | 0d430e252dd56a6a3e9c855d336601d4eaa09842_id  | string       |
       | disciplineAction                      | 1                   | body.assignmentSchoolId          | 0a7994b6ca3cb7bf46c9015373a9d29d003999db_id  | string       |    
       | disciplineAction                      | 1                   | body.studentId                   | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string       |         
       | disciplineAction                      | 1                   | body.staffId                     | b9bad00837eeea5bdbfe2475cf6011a3e4330c5c_id  | string       |         
       | disciplineAction                      | 1                   | body.disciplineIncidentId        | f96dbcb9c71d4b738c3f5d5200f199c45b00bf8c_id  | string       |          
    And I should see "Processed 9 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I post "cascading_deletion_jets_school.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "cascading_deletion_jets_school.zip" is completed in database
    And a batch job log has been created
	Then I should see following map of entry counts in the corresponding collections:
     | collectionName                            |              count|
     | student                                   |					1|
     | staff                                     |                  1|
     | educationOrganization                     |					4|
     | disciplineAction                          |					1|
     | disciplineIncident                        |					1|  
    And I check to find if record is in collection:
     | collectionName                       | expectedRecordCount | searchParameter                   | searchValue                                  | searchType   |
     | disciplineAction                      | 0                   | body.responsibilitySchoolId      | 0d430e252dd56a6a3e9c855d336601d4eaa09842_id  | string       |
     | disciplineAction                      | 1                   | body.assignmentSchoolId          | 0a7994b6ca3cb7bf46c9015373a9d29d003999db_id  | string       |         
     | disciplineAction                      | 1                   | body.studentId                   | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string       |         
     | disciplineAction                      | 1                   | body.staffId                     | b9bad00837eeea5bdbfe2475cf6011a3e4330c5c_id  | string       |         
     | disciplineAction                      | 1                   | body.disciplineIncidentId        | f96dbcb9c71d4b738c3f5d5200f199c45b00bf8c_id  | string       |         
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I post "cascading_deletion_sharks_school.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "cascading_deletion_sharks_school.zip" is completed in database
    And a batch job log has been created
	Then I should see following map of entry counts in the corresponding collections:
     | collectionName                            |              count|
     | student                                   |					1|
     | staff                                     |                  1|
     | educationOrganization                     |					3|
     | disciplineAction                          |					0|
     | disciplineIncident                        |					1|  
    And I check to find if record is in collection:
     | collectionName                        | expectedRecordCount | searchParameter                  | searchValue                                  | searchType   |
     | disciplineAction                      | 0                   | body.responsibilitySchoolId      | 0d430e252dd56a6a3e9c855d336601d4eaa09842_id  | string       |
     | disciplineAction                      | 0                   | body.assignmentSchoolId          | 0a7994b6ca3cb7bf46c9015373a9d29d003999db_id  | string       |        
     | disciplineAction                      | 0                   | body.studentId                   | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string       |         
     | disciplineAction                      | 0                   | body.staffId                     | b9bad00837eeea5bdbfe2475cf6011a3e4330c5c_id  | string       |         
     | disciplineAction                      | 0                   | body.disciplineIncidentId        | f96dbcb9c71d4b738c3f5d5200f199c45b00bf8c_id  | string       |         
    And I should see "Processed 1 records." in the resulting batch job file
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
       #| attendance                            | 1                   | body.sectionId                   | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string       |         
       #| assessment                            | 1                   | body.sectionId                   | b9bad00837eeea5bdbfe2475cf6011a3e4330c5c_id  | string       |         
       #| gradebookEntry                        | 1                   | body.sectionId                   | f96dbcb9c71d4b738c3f5d5200f199c45b00bf8c_id  | string       |           
    And I should see "Processed 17 records." in the resulting batch job file
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
     | attendance                                |					0|
     | assessment                                |					0|
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
       #| attendance                            | 0                   | body.sectionId                   | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string       |         
       #| assessment                            | 0                   | body.sectionId                   | b9bad00837eeea5bdbfe2475cf6011a3e4330c5c_id  | string       |         
       #| gradebookEntry                        | 0                   | body.sectionId                   | f96dbcb9c71d4b738c3f5d5200f199c45b00bf8c_id  | string       |           
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created 