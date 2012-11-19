@rc
Feature: Ingestion

Background:
SFTP into landing zone to drop the Small Data Set

Given I am using local data store
# put the small sample dataset into the landing zone
Given I am using default landing zone
Given I use the landingzone user name "<Email_IMAP_Registration_User_Email>" and password "<Email_IMAP_Registration_Pass>" on landingzone server "<Email_IMAP_Registration_LZ>" on port "<Email_IMAP_Registration_LZ_Port>"
And I drop the file "SmallSampleDataSet.zip" into the landingzone
# Should take about 4 minutes
And I check for the file "job*.log" every "30" seconds for "600" seconds
Then the landing zone should contain a file with the message "Processed 4251 records"
And the landing zone should contain a file with the message "All records processed successfully."
