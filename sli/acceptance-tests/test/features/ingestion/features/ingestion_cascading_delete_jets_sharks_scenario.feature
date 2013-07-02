@RALLY_US3033
@wip
Feature: Safe Deletion and Cascading Deletion 

Background: I have a landing zone route configured
Given I am using local data store

Scenario: delete "Sharks School", AssignmentSchoolReference is wiped out in the DisciplineAction, ResponsibilitySchoolReference still points to "Jets School"; delete "Jets School", DisciplineAction is deleted.
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "prep_cascading_deletion_school.zip" file as the payload of the ingestion job
  And the "Midgar" tenant db is empty
When zip file is scp to ingestion landing zone
  And a batch job for file "prep_cascading_deletion_school.zip" is completed in database
  Then I should see following map of entry counts in the corresponding collections:
     | collectionName                            |              count|
     | student                                   |					1|
     | staff                                     |                  1|
     | educationOrganization                     |					5|
     | disciplineAction                          |					1|
     | disciplineIncident                        |					1|
    And I check to find if record is in collection:
       | collectionName                        | expectedRecordCount | searchParameter                  | searchValue                                  | searchType   |
       | disciplineAction                      | 1                   | body.responsibilitySchoolId      | 0a7994b6ca3cb7bf46c9015373a9d29d003999db_id  | string       |
       | disciplineAction                      | 1                   | body.assignmentSchoolId          | 0d430e252dd56a6a3e9c855d336601d4eaa09842_id  | string       |    
       | disciplineAction                      | 1                   | body.studentId                   | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string       |         
       | disciplineAction                      | 1                   | body.staffId                     | b9bad00837eeea5bdbfe2475cf6011a3e4330c5c_id  | string       |         
       | disciplineAction                      | 1                   | body.disciplineIncidentId        | f96dbcb9c71d4b738c3f5d5200f199c45b00bf8c_id  | string       |          
    And I should see "Processed 9 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I post "cascading_deletion_sharks_school.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "cascading_deletion_sharks_school.zip" is completed in database
    Then I should see following map of entry counts in the corresponding collections:
     | collectionName                            |              count|
     | student                                   |					1|
     | staff                                     |                  1|
     | educationOrganization                     |					4|
     | disciplineAction                          |					1|
     | disciplineIncident                        |					1|  
    And I check to find if record is in collection:
     | collectionName                       | expectedRecordCount | searchParameter                   | searchValue                                  | searchType   |
     | disciplineAction                      | 1                   | body.responsibilitySchoolId      | 0a7994b6ca3cb7bf46c9015373a9d29d003999db_id  | string       |
     | disciplineAction                      | 0                   | body.assignmentSchoolId          | 0d430e252dd56a6a3e9c855d336601d4eaa09842_id  | string       |         
     | disciplineAction                      | 1                   | body.studentId                   | c6fcb4deb579ad0131c2664393d40b4319d8e215_id  | string       |         
     | disciplineAction                      | 1                   | body.staffId                     | b9bad00837eeea5bdbfe2475cf6011a3e4330c5c_id  | string       |         
     | disciplineAction                      | 1                   | body.disciplineIncidentId        | f96dbcb9c71d4b738c3f5d5200f199c45b00bf8c_id  | string       |         
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I post "cascading_deletion_jets_school.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "cascading_deletion_jets_school.zip" is completed in database
    Then I should see following map of entry counts in the corresponding collections:
     | collectionName                            |              count|
     | student                                   |					1|
     | staff                                     |                  1|
     | educationOrganization                     |					3|
     | disciplineAction                          |					0|
     | disciplineIncident                        |					1|         
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created


Scenario: delete  school, associated entities should be deleted, non-associated entities should not be deleted
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "prep_cascading_deletion.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
     | collectionName                     |
     | educationOrganization              |
     | attendance                         |
     | courseOffering                     |
     | disciplineAction                   |
     | disciplineIncident                 |
     | section                            |
     | studentSchoolAssociation           |
     | teacherSchoolAssociation           |
     | studentDisplineIncidentAssociation |
     | grade                              |
     | reportCard                         |
     | gradebookEntry                     |
     | studentGradeBookEntry              |
     | studentSectionAssociation          |
     | teacherSectionAssociation          |
     | studentCompetency                  |
     | student                            |
     | staff                              |
   When zip file is scp to ingestion landing zone
  And a batch job for file "prep_cascading_deletion.zip" is completed in database
    Then I should see following map of entry counts in the corresponding collections:
     | collectionName                     |              count|
     | student                            |					1|
     | educationOrganization              |					3|
     | attendance                         |					1|
     | courseOffering                     |					1|
     | disciplineAction                   |					1|
     | disciplineIncident                 |					1|
     | section                            |					1|
     | studentSchoolAssociation           |					1|
     | teacherSchoolAssociation           |					1|
     | studentDisciplineIncidentAssociation |				1|
     | grade                              |					1|
     | reportCard                         |					1|
     | gradebookEntry                     |					1|
     | studentGradebookEntry              |					1|
     | studentSectionAssociation          |					1|
     | teacherSectionAssociation          |					1|
     | studentCompetency                  |					1|
     | student                            |					1|
     | staff                              |					2|
   And I check to find if record is in collection:
       | collectionName                     | expectedRecordCount | searchParameter     | searchValue                                  | searchType           |
       | attendance                         | 1                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | courseOffering                     | 1                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | disciplineAction                   | 1                   | body.responsibilitySchoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | disciplineIncident                 | 1                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | section                            | 1                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | studentSchoolAssociation           | 1                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | teacherSchoolAssociation           | 1                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | studentDisciplineIncidentAssociation| 1                  | body.disciplineIncidentId       | f96dbcb9c71d4b738c3f5d5200f199c45b00bf8c_id | string     |
       | grade                              | 0                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | reportCard                         | 0                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | gradebookEntry                     | 1                   | body.sectionId	       | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string     |
       | studentGradebookEntry              | 1                   | body.sectionId       | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string     |
       | studentSectionAssociation          | 1                   | body.sectionId       | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string     |
       | teacherSectionAssociation          | 1                   | body.sectionId       | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string     |
       | studentCompetency                  | 1                   | body.studentSectionAssociationId       | 6df6309cd7609257f454ac8b7456e3943f4d6190_id11d22f998a39f5db6ccfa55264a3629637733195_id  | string     |
    And I should see "Processed 51 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I post "cascading_deletion_school_2.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "cascading_deletion_school_2.zip" is completed in database
    Then I should see following map of entry counts in the corresponding collections:
     | collectionName                     |              count|
     | student                            |					1|
     | educationOrganization              |					2|
     | attendance                         |					0|
     | courseOffering                     |					0|
     | disciplineAction                   |					0|
     | disciplineIncident                 |					0|
     | section                            |					0|
     | studentSchoolAssociation           |					0|
     | teacherSchoolAssociation           |					0|
     | studentDisciplineIncidentAssociation |				1|
     | grade                              |					0|
     | reportCard                         |					1|
     | gradebookEntry                     |					0|
     | studentGradebookEntry              |					0|
     | studentSectionAssociation          |					0|
     | teacherSectionAssociation          |					0|
     | studentCompetency                  |					0|
     | student                            |					1|
     | staff                              |					2|
     And I check to find if record is in collection:
       | collectionName                     | expectedRecordCount | searchParameter     | searchValue                                  | searchType           |
       | attendance                         | 0                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | courseOffering                     | 0                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | disciplineAction                   | 0                   | body.assignmentSchoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | disciplineIncident                 | 0                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | section                            | 0                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | studentSchoolAssociation           | 0                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | teacherSchoolAssociation           | 0                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       |studentDisciplineIncidentAssociation| 1                   | body.disciplineIncidentId       | f96dbcb9c71d4b738c3f5d5200f199c45b00bf8c_id | string     |
       | grade                              | 0                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | reportCard                         | 0                   | body.schoolId       | 1b69b7a3139078e4438dd930e554293b1030d319_id  | string     |
       | gradebookEntry                     | 0                   | body.sectionId	       | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string     |
       | studentGradebookEntry              | 0                   | body.sectionId       | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string     |
       | studentSectionAssociation          | 0                   | body.sectionId       | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string     |
       | teacherSectionAssociation          | 0                   | body.sectionId       | 6df6309cd7609257f454ac8b7456e3943f4d6190_id  | string     |
       | studentCompetency                  | 0                   | body.studentSectionAssociationId       | 6df6309cd7609257f454ac8b7456e3943f4d6190_id11d22f998a39f5db6ccfa55264a3629637733195_id  | string     |
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
