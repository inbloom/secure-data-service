Feature: Teacher-School Association Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone
    And I connect to "sli" database

Scenario: Post a zip file containing a XML of teacher-school associations as a payload of the ingestion job
Given I post "validTeachersXML3.zip" as the payload of the ingestion job
	And the payload contains entities of type "teacher"
	And there are none of this type of entity in the DS
	And zip file is scp to ingestion landing zone
	And "3" seconds have elapsed
	And I post "validSchoolsXml2.zip" as the payload of the ingestion job
	And the payload contains entities of type "school"
	And there are none of this type of entity in the DS
	And zip file is scp to ingestion landing zone
	And "3" seconds have elapsed
When I post "validTeacherSchoolAssociationXML2.zip" as the payload of the ingestion job
    And the payload contains entities of type "teacherSchoolAssociation"
    And there are none of this type of entity in the DS
When zip file is scp to ingestion landing zone
    And "3" seconds have elapsed
Then I should see "2" entries in the corresponding collection
    And I should see "Processed 2 records." in the resulting batch job file

Scenario: Post a zip file containing a CSV of teacher-school associations as a payload of the ingestion job
Given I post "validTeachersCSV100.zip" as the payload of the ingestion job
	And the payload contains entities of type "teacher"
	And there are none of this type of entity in the DS
	And zip file is scp to ingestion landing zone
	And "3" seconds have elapsed
	And I post "validSchoolsCSV100.zip" as the payload of the ingestion job
	And the payload contains entities of type "school"
	And there are none of this type of entity in the DS
	And zip file is scp to ingestion landing zone
	And "3" seconds have elapsed
When I post "validTeacherSchoolAssociationCSV100.zip" as the payload of the ingestion job
    And the payload contains entities of type "teacherSchoolAssociation"
    And there are none of this type of entity in the DS
When zip file is scp to ingestion landing zone
    And "3" seconds have elapsed
Then I should see "100" entries in the corresponding collection
    And I should see "Processed 100 records." in the resulting batch job file
