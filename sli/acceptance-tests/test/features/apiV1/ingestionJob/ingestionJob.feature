Feature: As an SLI application, I want to be able to read ingestion job data
Any user with the INGESTION_LOG_VIEW privilege should be able to view them, 
and not otherwise.  Admins have this privilege by default.  

# note: to test succeeding with the correct privilege and failing without it, we are giving
# the privilege to the custom role "IT Administrator", testing for successs, then taking it
# away again and testing for failure.  The privilege is not a default admin privilege at this
# writing.  We do this with 2 IT Admin accounts; if we do it with one, the API's caching
# mechanism causes the test to fail.

Scenario: Change right for IT Admin to do a successful ingestion log API query
    And   I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
    When I navigate to GET "/ingestionJobs/"
    Then I should receive a return code of 200
    
Scenario: Attempt ingestion log data as a local admin, get denied because of insufficient privilege
    Given I change the custom role of "IT Administrator" in tenant "Midgar" to remove the "INGESTION_LOG_VIEW" right
    And   I am logged in using "rrogers" "rrogers1234" to realm "IL"
    When  I navigate to GET "/ingestionJobs/"
    Then  I should receive a return code of 403
    Given I change the custom role of "IT Administrator" in tenant "Midgar" to add the "INGESTION_LOG_VIEW" right
    
Scenario: Obtain ingestion log data as a state admin
    Given I am logged in using "iladmin" "iladmin1234" to realm "SLI"
    When I navigate to GET "/ingestionJobs/"
    Then I should receive a return code of 200

Scenario: Validate that the api returns the appropriate ingestionJobs
    Given the ingestion batch job collection has been reset
    And the ingestion batch job collection contains "6" records
    And I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
    And I navigate to GET "/ingestionJobs/"
    Then I should receive a response with "4" entities
    And I navigate to GET "/ingestionJobs/SmallSampleDataSet.zip-46f724c2-c248-44a7-b830-d3be40ccfa8f"
    Then I should have only one entity with id "SmallSampleDataSet.zip-46f724c2-c248-44a7-b830-d3be40ccfa8f"
    And I navigate to GET "/ingestionJobs/SmallSampleDataSet.zip-2d806bed-e9d5-43d0-9b61-01cfbef9369e"
    Then I should receive a return code of 204
