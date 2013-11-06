@RALLY_US3033
Feature: Data Set for App Enable and Authorize Testing

For use in generating fixture data only

Scenario: Post App Auths Data Set
  Given I should see following map of entry counts in the corresponding collections:
      | collectionName                           |              count|
      | educationOrganization                    |                 41|
  When I ingest "AppEnableAndAuthorizeDataSet.zip"
  #189 edorgs ingested
  Then I should see following map of entry counts in the corresponding collections:
      | collectionName                           |              count|
      | educationOrganization                    |                230|

  #replace all b64ee2bcc92805cdd8ada6b7d8f9c643c9459831_id with b1bd3db6-d020-4651-b1b8-a8dba688d9e1


