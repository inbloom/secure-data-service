Feature: API Jmeter performance tests

Scenario: Run Jmeter tests
  Given I run each of the Jmeter tests:
    | testName            |
    | dashboard           |
  Then I only check "See LOS;See Student Profile" for performance regression
  #Then no performance regressions should be found