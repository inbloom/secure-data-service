@RALLY_DE87
Feature: Course Offering Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone


Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "CourseOffering1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | educationOrganization       |
     | courseOffering              |
     | session                     |
     | course                      |
     | calendarDate                |
     | gradingPeriod               |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | educationOrganization       | 1     |
     | courseOffering              | 2     |
     | session                     | 1     |
     | course                      | 1     |
     | gradingPeriod               | 1     |
     | calendarDate                | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | courseOffering              | 1                   | body.localCourseCode        | ACC-TEST-COURSE-CODE-1  | string               |
     | courseOffering              | 1                   | body.localCourseCode        | ACC-TEST-COURSE-CODE-2  | string               |
     | courseOffering              | 2                   | body.courseId               | Geometry-12             | string               |
  And I should see "Processed 7 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "CourseOffering1.xml records considered: 2" in the resulting batch job file
  And I should see "CourseOffering1.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "CourseOffering1.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml records considered: 2" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeEducationOrgCalendar.xml records considered: 3" in the resulting batch job file
  And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file

@wip
Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "CourseOffering2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | educationOrganization       | 1     |
     | courseOffering              | 2     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue               | searchType           |
     | courseOffering              | 1                   | body.localCourseTitle       | ACC-TEST-COURSE-TITLE-2A  | string               |
     | courseOffering              | 0                   | body.localCourseTitle       | ACC-TEST-COURSE-TITLE-2   | string               |
  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "CourseOffering2.xml records considered: 1" in the resulting batch job file
  And I should see "CourseOffering2.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "CourseOffering2.xml records failed: 0" in the resulting batch job file
