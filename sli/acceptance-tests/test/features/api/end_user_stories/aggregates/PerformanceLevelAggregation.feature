Feature: Test aggregation creation for performance level aggregations

Background: 
    Given I am connected to the sli database
    Given I am logged in using "demo" "demo1234"
    Given I have access to all aggregation definitions

Scenario: District Level 8th Grade EOG
    Given I am using the Smallville School District assessment scores 
    Given I have an aggregation definition for <District Level 8th Grade EOG> 
    When the aggregation is calculated 
    Then I should receive a performance level aggregation
      And there should be 20 level one performers
      And there should be 32 level two performers
      And there should be 78 level three performers
      And there should be 15 level four performers