Feature: Test aggregation creation for performance level aggregations

Background: 
    Given I am connected to the sli database
    Given I am logged in using "demo" "demo1234"
    Given I have access to all aggregation definitions

Scenario: District Level 8th Grade EOG
    Given I am using the Smallville School District assessment scores 
    Given I have an aggregation definition for <District Level 8th Grade EOG> 
    When the aggregation is calculated 
    Then I should receive a district performance level aggregation for <Smallville District>
      And there should be 20 level one performers
      And there should be 32 level two performers
      And there should be 78 level three performers
      And there should be 15 level four performers
      
Scenario: School Level 8th Grade EOG
    Given I am using the Smallville School District assessment scores
    Given I have an aggregation definition for <School Level 8th Grade EOG>
    When the aggregation is calculated
    Then I should receive a school performance level aggregation for <Small Mouth Bass Middle School>
      And there should be 13 level one performers
      And there should be 17 level two performers
      And there should be 42 level three performers
      And there should be 10 level four performers
    Then I should receive a school performance level aggregation for <Don't Sweat the Small Stuff Middle School>
      And there should be 4 level one performers
      And there should be 10 level two performers
      And there should be 22 level three performers
      And there should be 3 level four performers
    Then I should receive a school performance level aggregation for <La Vie en Small French-Immersion K-8>
      And there should be 3 level one performers
      And there should be 5 level two performers
      And there should be 14 level three performers
      And there should be 2 level four performers
