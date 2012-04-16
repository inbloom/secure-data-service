Feature: Session Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "studentAssessment_Valid.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName               |
     | studentAssessmentAssociation |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName               | count    |
     | student                      | 100      |
     | assessment                   | 10       |
     | studentAssessmentAssociation | 651      |
  And I should see "Processed 1110 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudent.xml records considered: 100" in the resulting batch job file
  And I should see "InterchangeStudent.xml records ingested successfully: 100" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata.xml records considered: 10" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata.xml records ingested successfully: 10" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeStudentAssessment.xml records considered: 1000" in the resulting batch job file
  And I should see "InterchangeStudentAssessment.xml records ingested successfully: 1000" in the resulting batch job file
  And I should see "InterchangeStudentAssessment.xml records failed: 0" in the resulting batch job file

@wip
Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "studentAssessment_MissingIDRef.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | studentAssessment           |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count    |
     | studentAssessment           | 0        |
  And I should see an error log file created
  And I should see "Error resolving references in XML file studentAssessment_MissingIDRef.xml: Unresolved reference, id=" in the resulting batch job file
  And I should see "STU-1" in the resulting batch job file

@wip
Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "studentAssessment_MalformedXML.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | studentAssessment           |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count    |
     | studentAssessment           | 0        |
  Then I should see an error log file created
  And I should see "Error parsing XML file studentAssessment_MalformedXML.xml: The element type " in the resulting batch job file
  And I should see "StudentReference" in the resulting batch job file
  And I should see "must be terminated by the matching end-tag " in the resulting batch job file
  And I should see "</StudentReference>" in the resulting batch job file

