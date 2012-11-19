@rc
Feature: Ingestion

  Background: Copy the data files to a local landing zone

    Given I am using local data store
    And I have a local configured landing zone for my tenant
  # put the small sample dataset into the landing zone
    And I drop the file "SmallSampleDataSet.zip" into the landingzone
  # Should take about 4 minutes
    And I check for the file "job*.log" every "30" seconds for "600" seconds
    Then the landing zone should contain a file with the message "Processed 4251 records"
    And the landing zone should contain a file with the message "All records processed successfully."
