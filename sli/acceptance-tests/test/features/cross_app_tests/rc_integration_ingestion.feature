@RALLY_US4835
@rc
Feature: Ingestion

    Background: SFTP into landing zone to drop the Small Data Set


        Scenario: Ingest Charter School Dataset
            Given a landing zone
            Given I am using odin data store
            And I post "OdinSampleDataSet.zip" file as the payload of the ingestion job
            When zip file is scp to ingestion landing zone
            And a batch job for file "OdinSampleDataSet.zip" is completed in database
            And the landing zone should contain a file with the message "All records processed successfully."
            And I should not see an error log file created
            And I should not see a warning log file created

        Scenario: Ingest Small Sample Dataset for End to End Testing

            Given a landing zone
            And I drop the file "SmallSampleDataSet.zip" into the landingzone
            And I check for the file "job*.log" every "30" seconds for "600" seconds
            Then the "SmallSampleDataSet.zip" should be ingested with the correct number of records
            And the landing zone should contain a file with the message "All records processed successfully."
            And I should not see an error log file created
            And I should not see a warning log file created
