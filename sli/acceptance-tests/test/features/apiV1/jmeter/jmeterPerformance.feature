@RALLY_US4959
@RALLY_US4966
Feature: API Jmeter performance tests

Scenario: Run Jmeter tests
  Given I run each of the Jmeter tests:
    | testName            |
    | login               |
    | list-attendance	  |
    | list-sections	      |
    | list-grades         |
    | list-students       |
    | single-student      |
    | update-gradebooks   |
    | update-attendance   |
    | student-dashboard   |
    | dashboard-single-student.jmx   |
  #Then no performance regressions should be found
