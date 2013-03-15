@RALLY_US4835
@rc
@sandbox
Feature: Delete tenant and drop tenant database

Background: Make a connection to Mongo
  Given I have a connection to Mongo
  And I am running in Sandbox mode

Scenario: Delete tenant from sli.tenant and drop tenant database
  When I get the database name
#  And I clean my tenant's landing zone
#  Then I will drop the whole database
# And I will clean my tenants recordHash documents from ingestion_batch_job db
# And I will drop the tenant document from the collection
  And I will delete the applications "Schlemiel,NotTheAppYoureLookingFor" from the collection