@RALLY_US4439
Feature: Reversion support for record level deltas

#  As an ingestion user, I want to be able to revert changes made to entity data and not have the update that reverts the data get dropped due to a prior identical revision existing with a stale record hash

Background: I have a landing zone route configured
Given I am using local data store
And I am using preconfigured Ingestion Landing Zone

Scenario: Ingest original data, update, then revert
Given the following collections are empty in datastore:
     | collectionName              |
     | educationOrganization       |
  And I post "TinyDataSet.zip" file as the payload of the ingestion job
  And zip file is scp to ingestion landing zone
  And I am willing to wait upto 60 seconds for ingestion to complete
  And a batch job log has been created
Then I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue                                 | searchType           |
     | educationOrganization       | 1                   | body.nameOfInstitution      | Illinois State Board of Education           | string               |
When I post "TinyDataSetUpdated.zip" file as the payload of the ingestion job
  And zip file is scp to ingestion landing zone
  And I am willing to wait upto 60 seconds for ingestion to complete
  And a batch job log has been created
Then I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue                                 | searchType           |
     | educationOrganization       | 1                   | body.nameOfInstitution      | Illinois State Board of Da Bears Education  | string               |
When I post "TinyDataSet.zip" file as the payload of the ingestion job
  And zip file is scp to ingestion landing zone
  And I am willing to wait upto 60 seconds for ingestion to complete
  And a batch job log has been created
  And "5" seconds have elapsed
Then I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue                                 | searchType           |
     | educationOrganization       | 1                   | body.nameOfInstitution      | Illinois State Board of Education           | string               |

