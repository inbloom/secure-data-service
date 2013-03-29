@RALLY_US3033
Feature: Safe Deletion and Cascading Deletion 

Background: I have a landing zone route configured
Given I am using local data store

Scenario: delete "section", entities referencing "section" should be deleted or remove "section" from its reference list; entities referenced by "section" should not be deleted
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "prep_cascading_deletion_section.zip" file as the payload of the ingestion job
  And the "Midgar" tenant db is empty
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
	And I should not see "6df6309cd7609257f454ac8b7456e3943f4d6190_id" in the "Midgar" database