@RALLY_US3174
Feature: Ensure API performance meets defined thresholds

Background: Load database with large dataset
  Given I copy the database "apiperf" to database "sli"

Scenario Outline: Check API performance for a staff
  Given I want to execute each call "5" times
    And I want to log in as "rrogers" to realm "IL"
  When I execute a series of GETs on "<resource>"
  Then the "<AVERAGE TIME STAT>" should be less than <average time> ms
    And the "<90TH PERCENTILE STAT>" should be less than <90th percentile> ms
   Examples:
    | resource                | average time | 90th percentile |
    | /system/session/check   | 100          | 120             |
    | /educationOrganizations | 100          | 120             |
