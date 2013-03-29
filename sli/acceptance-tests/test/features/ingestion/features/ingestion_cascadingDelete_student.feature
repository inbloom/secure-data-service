@RALLY_US3033
Feature: Safe Deletion and Cascading Deletion 

Background: I have a landing zone route configured
Given I am using local data store

Scenario: delete basic student, associated entities should be deleted, non-associated entities should not be deleted
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "prep_cascading_deletion_student.zip" file as the payload of the ingestion job
  And the "Midgar" tenant db is empty
When zip file is scp to ingestion landing zone
  And a batch job for file "prep_cascading_deletion_student.zip" is completed in database
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
     | studentCohortAssociation                  |                  1|  
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
       | studentCohortAssociation              | 1                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |            
    And I should see "Processed 51 records." in the resulting batch job file
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
     | studentCohortAssociation                  |                  0|   
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
       | studentCohortAssociation              | 0                   | body.studentId      | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string     |               
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	#And I should not see "c6fcb4deb579ad0131c2664393d40b4319d8e215_id" in the "Midgar" database