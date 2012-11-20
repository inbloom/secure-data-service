@rc
Feature: Ingestion

  Background: Copy the data files to a local landing zone

    Given I am using local data store
    And I have a local configured landing zone for my tenant
    And I drop the file "SmallSampleDataSet.zip" into the landingzone
    And I check for the file "job*.log" every "30" seconds for "600" seconds
    Then the landing zone should contain a file with the message "Processed 4251 records"
    And the landing zone should contain a file with the message "All records processed successfully."
    And I should not see an error log file created
    And I should not see a warning log file created
