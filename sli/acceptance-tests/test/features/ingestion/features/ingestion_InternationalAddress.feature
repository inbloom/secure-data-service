@RALLY_US3033
Feature: International Address

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Test Internation Address for Student, Parent, Staff, Teacher and EducationOrganization
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "AddressLine.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                            |
     | educationOrganization                     |
     | parent                                    |
     | staff                                     |
     | student                                   |
     | recordHash                                |
  When zip file is scp to ingestion landing zone
  And a batch job for file "AddressLine.zip" is completed in database
  Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | educationOrganization                    |                  3|
     | parent                                   |                  1|
     | recordHash                               |                 14|
     | staff                                    |                  3|
     | student                                  |                  1|
   And I check the number of records in collection:
       | collectionName              | expectedRecordCount | searchParameter                                         | searchContainer |
       | student                     | 2                   | body.address.addressType                                | body.address    |
       | student                     | 1                   | body.address.country                                    | body.address    |
       | student                     | 1                   | body.address.addressLine                                | body.address    |
       | parent                      | 0                   | body.address.addressType                                | body.address    |       
       | parent                      | 1                   | body.address.country                                    | body.address    |
       | parent                      | 1                   | body.address.addressLine                                | body.address    |
       | staff                       | 3                   | body.address.addressType                                | body.address    |      
       | staff                       | 3                   | body.address.country                                    | body.address    |
       | staff                       | 3                   | body.address.addressLine                                | body.address    |
       | educationOrganization       | 3                   | body.address.country                                    | body.address    |       
       | educationOrganization       | 1                   | body.address.addressType                                | body.address    |
       | educationOrganization       | 3                   | body.address.addressLine                                | body.address    |
  And I should not see an error log file created
  And I should not see a warning log file created