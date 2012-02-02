Feature: Test aggregation creation for performance level aggregations

Background: 
    Given I am connected to the sli database
    Given I am logged in using "demo" "demo1234"
    Given I have access to all aggregation definitions
    Given the aggregation table is clear
    Given format "application/json"

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
  
Scenario Outline: Teacher level math section aggregation
    Given I am using the Smallville School District assessment scores
    Given I have an aggregation definition for <Teacher Level Math Scores>
    When the aggregation is calculated
    Then I should receive a teacher performance level aggregation for <<teacher>> 
      And there should be <level1> level one performers
      And there should be <level2> level two performers
      And there should be <level3> level three performers
      And there should be <level4> level four performers
    Examples:
    | teacher | level1 | level2 | level3 | level4 |
    | 8888031	|   4	   |   4	  |    5	 |    4	  |
    | 8888032	|   0	   |   1	  |    9   |    6   |
    | 8888033	|   0	   |   0	  |   17	 |    0   |
    | 8888034	|   0	   |   5	  |   11	 |    0   |
    | 8888035	|   9	   |   7	  |    0   |    0   |
    | 8888036	|   0	   |   0	  |    4	 |    3   |
    | 8888037	|   0	   |   0	  |    8	 |    0   |
    | 8888038	|   0	   |   0	  |    8	 |    0   |
    | 8888039	|   0	   |   6	  |    2	 |    0   |
    | 8888040	|   4	   |   4	  |    0	 |    0   |
    | 8888049	|   3	   |   5	  |   14	 |    2   |
