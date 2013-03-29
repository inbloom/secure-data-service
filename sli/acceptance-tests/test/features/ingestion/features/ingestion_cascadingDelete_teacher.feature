@RALLY_US3033
Feature: Safe Deletion and Cascading Deletion 

Background: I have a landing zone route configured
Given I am using local data store

Scenario: delete  teacher, associated entities should be deleted, non-associated entities should not be deleted
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "prep_cascading_deletion_teacher.zip" file as the payload of the ingestion job
  And the "Midgar" tenant db is empty
When zip file is scp to ingestion landing zone
  And a batch job for file "prep_cascading_deletion_teacher.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                            |              count|
     | teacherSchoolAssociation                  |					1|
     | teacherSectionAssociation                 |					1|
     | educationOrganization                     |					3|
     | program                                   |					1|
     | section                                   |					1|
     | staff                                     |					1|
    And I check to find if record is in collection:
       | collectionName                        | expectedRecordCount | searchParameter     | searchValue                                  | searchType           |
       | teacherSchoolAssociation              | 1                   | body.teacherId      | b49545f9d443dfbf93358851c903a9923f6af4dd_id  | string     |
       | teacherSectionAssociation             | 1                   | body.teacherId      | b49545f9d443dfbf93358851c903a9923f6af4dd_id  | string     |
    And I should see "Processed 8 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I post "cascading_deletion_teacher.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "cascading_deletion_teacher.zip" is completed in database
    And a batch job log has been created
	Then I should see following map of entry counts in the corresponding collections:
     | collectionName                            |              count|
   #  | teacherSchoolAssociation                  |					0|
   #  | teacherSectionAssociation                 |					0|
     | educationOrganization                     |					3|
     | program                                   |					1|
     | section                                   |					1|
     | staff                                     |					0|
    And I check to find if record is in collection:
       | collectionName                        | expectedRecordCount | searchParameter     | searchValue                                  | searchType           |
   #    | teacherSchoolAssociation              | 0                   | body.teacherId      | b49545f9d443dfbf93358851c903a9923f6af4dd_id  | string     |
   #    | teacherSectionAssociation             | 0                   | body.teacherId      | b49545f9d443dfbf93358851c903a9923f6af4dd_id  | string     |            
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created