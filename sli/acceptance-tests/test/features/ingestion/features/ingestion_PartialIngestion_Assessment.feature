@RALLY_US3696
Feature: Partial Ingestion of Assessment records

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post partial Assessment dataset on an empty database
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "AssessmentPartial.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |

When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  2|
    And I should see "Processed 2 records." in the resulting batch job file
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter                  | searchValue        | searchType         |
       | assessment                  | 2                   | body.assessmentPeriodDescriptor  | nil                | nil                |

 Scenario: Post full assessment dataset on an empty database followed by partial assessment dataset
 Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "AssessmentFull.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | assessment                                |

When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  2|
    And I should see "Processed 2 records." in the resulting batch job file
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter                  | searchValue                | searchType           |
       | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue  | READ2-BOY-2011                       | string               |
       | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue  | READ2-MOY-2011                       | string               |

    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "AssessmentPartial.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
    And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | assessment                               |                  4|
    And I should see "Processed 2 records." in the resulting batch job file
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter                  | searchValue                | searchType           |
       | assessment                  | 2                   | body.assessmentPeriodDescriptor.codeValue  | READ2-BOY-2011                       | string               |
       | assessment                  | 2                   | body.assessmentPeriodDescriptor.codeValue  | READ2-MOY-2011                       | string               |
