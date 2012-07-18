@RALLY_US3054
Feature: Ingestion HealthCheck Test

As a SLI operator, I want to verify if ingestion is responsive.

@wip
Scenario: Valid user HealthCheck
  When I navigate to the Ingestion Service HealthCheck page and submit login credentials "admin" "admin"
  Then I am informed that "* Ingestion: OK"
