Feature: A bulk extract is triggered for the student entity

Background:
  Given I am using local data store

Scenario: Trigger a bulk extract on ingested data
   Given I trigger a bulk extract 
   When I retrieve the path to the extract file for the tenant "Midgar"
   And I verify that an extract zip file was created for the tenant "Midgar"
   And there is a metadata file in the extract
   And the extract contains a file for each collection