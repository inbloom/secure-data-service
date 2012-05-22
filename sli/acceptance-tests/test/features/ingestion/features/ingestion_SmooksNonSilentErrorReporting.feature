Feature: Ingestion Smooks Non Silent Error Reporting Test

Background: I have a landing zone route configured
    Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Non silent errors are reported for the ingested zip file- Clean database
    Given I post "smooksNonSilent.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
    When zip file is scp to ingestion landing zone
      And I am willing to wait upto 30 seconds for ingestion to complete
      And a batch job log has been created
    Then I should see following map of entry counts in the corresponding collections:
       | collectionName              | count |
       | student                     | 1     |
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "/InterchangeStudentParent(0)/Student(0)/DummyElement(0): The dummyAttribute attribute was not processed" in the resulting warning log file for "InterchangeStudent.xml"
    And I should see "/InterchangeStudentParent(0)/Student(0)/DummyElement(0) : The element was not processed" in the resulting warning log file for "InterchangeStudent.xml"
    And I should see "/InterchangeStudentParent(0)/Student(0)/DummyElement(0)/EmailAddress(0) : The element was not processed" in the resulting warning log file for "InterchangeStudent.xml"

