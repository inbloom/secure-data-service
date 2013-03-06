Feature: Program Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "CourseUpdate1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | course                      |
When zip file is scp to ingestion landing zone
  And a batch job for file "CourseUpdate1.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | course                      | 4     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | course                      | 1                   | body.courseCode.ID          | update field 1          | string               |
     | course                      | 1                   | body.courseTitle            | Original Title          | string               |
     | course                      | 0                   | body.courseTitle            | Updated Title           | string               |
     | course                      | 1                   | body.dateCourseAdopted      | 2000-10-01              | string               |
     | course                      | 0                   | body.dateCourseAdopted      | 2000-10-10              | string               |
     | course                      | 1                   | body.courseTitle            | Add More Course Code    | string               |
     | course                      | 1                   | body.courseCode.ID          | Course Code 1           | string               |
     | course                      | 0                   | body.courseCode.ID          | One More Course Code    | string               |
     | course                      | 1                   | body.courseTitle            | Remove Course Code      | string               |
     | course                      | 1                   | body.courseCode.ID          | Remove Course Code 1    | string               |
     | course                      | 1                   | body.courseCode.ID          | Remove Course Code 2    | string               |
     | course                      | 0                   | body.courseTitle            | New Course              | string               |
     | course                      | 1                   | body.courseTitle            | Different EdOrg Code    | string               |
     | course                      | 1                   | body.courseCode.ID          | Different EdOrg Code 1  | string               |
  And I should see "Processed 7 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "CourseUpdate1.xml records considered for processing: 7" in the resulting batch job file
  And I should see "CourseUpdate1.xml records ingested successfully: 7" in the resulting batch job file
  And I should see "CourseUpdate1.xml records failed processing: 0" in the resulting batch job file


Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "CourseUpdate2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job for file "CourseUpdate2.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | course                      | 6     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | course                      | 1                   | body.courseCode.ID          | update field 1          | string               |
     | course                      | 0                   | body.courseTitle            | Original Title          | string               |
     | course                      | 1                   | body.courseTitle            | Updated Title           | string               |
     | course                      | 0                   | body.dateCourseAdopted      | 2000-10-01              | string               |
     | course                      | 1                   | body.dateCourseAdopted      | 2000-10-10              | string               |
     | course                      | 1                   | body.courseTitle            | Add More Course Code    | string               |
     | course                      | 1                   | body.courseCode.ID          | Course Code 1           | string               |
     | course                      | 1                   | body.courseCode.ID          | One More Course Code    | string               |
     | course                      | 1                   | body.courseTitle            | Remove Course Code      | string               |
     | course                      | 1                   | body.courseCode.ID          | Remove Course Code 1    | string               |
     | course                      | 0                   | body.courseCode.ID          | Remove Course Code 2    | string               |
     | course                      | 1                   | body.courseTitle            | New Course              | string               |
     | course                      | 2                   | body.courseTitle            | Different EdOrg Code    | string               |
     | course                      | 2                   | body.courseCode.ID          | Different EdOrg Code 1  | string               |
  And I should see "Processed 8 records." in the resulting batch job file
  And I should not see an error log file created
    And I should see "CourseUpdate2.xml records considered for processing: 8" in the resulting batch job file
  And I should see "CourseUpdate2.xml records ingested successfully: 8" in the resulting batch job file
  And I should see "CourseUpdate2.xml records failed processing: 0" in the resulting batch job file
