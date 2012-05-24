@RALLY_US1889
Feature: StudentCohortAssociation Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "StudentDisciplineIncidentAssociation1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                        |
     | student                               |
	 | educationOrganization                 |
	 | staff                                 |
	 | staffEducationOrganizationAssociation |
     | disciplineIncident                    |
     | studentDisciplineIncidentAssociation  |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                          | count |
     | student                                 | 4     |
	 | educationOrganization                   | 3     |
	 | staff                                   | 1     |
	 | staffEducationOrganizationAssociation   | 1     |
     | disciplineIncident                      | 2     |
     | studentDisciplineIncidentAssociation    | 4     |
   And I check to find if record is in collection:
     | collectionName                          | expectedRecordCount | searchParameter                     | searchValue          | searchType           |
     | studentDisciplineIncidentAssociation    | 2                   | body.studentParticipationCode       | Perpetrator          | string               |
     | studentDisciplineIncidentAssociation    | 1                   | body.studentParticipationCode       | Witness              | string               |
     | studentDisciplineIncidentAssociation    | 1                   | body.studentParticipationCode       | Victim               | string               |
  And I should see "Processed 15 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "EdOrg1.xml records considered: 3" in the resulting batch job file
  And I should see "EdOrg1.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "EdOrg1.xml records failed: 0" in the resulting batch job file
  And I should see "Staff1.xml records considered: 2" in the resulting batch job file
  And I should see "Staff1.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "Staff1.xml records failed: 0" in the resulting batch job file
  And I should see "Student1.xml records considered: 4" in the resulting batch job file
  And I should see "Student1.xml records ingested successfully: 4" in the resulting batch job file
  And I should see "Student1.xml records failed: 0" in the resulting batch job file
  And I should see "Discipline1.xml records considered: 6" in the resulting batch job file
  And I should see "Discipline1.xml records ingested successfully: 6" in the resulting batch job file
  And I should see "Discipline1.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "StudentDisciplineIncidentAssociation2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                          | count |
     | student                                 | 4     |
	 | educationOrganization                   | 3     |
	 | staff                                   | 1     |
	 | staffEducationOrganizationAssociation   | 1     |
     | disciplineIncident                      | 2     |
     | studentDisciplineIncidentAssociation    | 4     |
   And I check to find if record is in collection:
     | collectionName                          | expectedRecordCount | searchParameter                     | searchValue          | searchType           |
     | studentDisciplineIncidentAssociation    | 1                   | body.studentParticipationCode       | Perpetrator          | string               |
     | studentDisciplineIncidentAssociation    | 1                   | body.studentParticipationCode       | Witness              | string               |
     | studentDisciplineIncidentAssociation    | 2                   | body.studentParticipationCode       | Victim               | string               |
  And I should see "Processed 2 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "Discipline2.xml records considered: 2" in the resulting batch job file
  And I should see "Discipline2.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "Discipline2.xml records failed: 0" in the resulting batch job file
