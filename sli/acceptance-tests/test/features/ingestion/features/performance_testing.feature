Feature: Ingestion Performance Test

Background: I have a landing zone route configured
Given I am using destination-local data store
    And I am using preconfigured Ingestion Landing Zone

@smoke
Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job
Given I want to ingest locally provided data "PerformanceData.zip" file as the payload of the ingestion job
When local zip file is moved to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count     |
        | student                     | 78        |
        | studentSchoolAssociation    | 162       |
        | course                      | 89        |
        | educationOrganization       | 3         |
        | school                      | 4         |
        | section                     | 90        |
        | studentSectionAssociation   | 290       |
        | teacher                     | 3         |
        | staff                       | 2         |
        | staffEducationOrganizationAssociation | 2|
        | teacherSchoolAssociation    | 4         |
        | teacherSectionAssociation   | 4         |
        | session                     | 22        |
        | assessment                  | 4         |
        | studentAssessmentAssociation| 116       |
        | studentTranscriptAssociation| 132       |
        | parent                      | 9         |
        | studentParentAssociation    | 9         |
        | gradebookEntry              | 12        |
        | studentSectionGradebookEntry| 78        |
        | attendance                  | 13650     |
    And I should see "Processed 15073 records." in the resulting batch job file