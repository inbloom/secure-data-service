@parent_public
Feature: Parent can see public entities

  Scenario: Parent can see public entities via direct id calls
    Given I log in to realm "Illinois Daybreak Parents" using simple-idp as "parent" "marsha.sollars" with password "marsha.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    Then I validate I have access to entities via the API access pattern "/v1/Entity/Id":
      | entity                      | id                                          | notes                                                                                               |
      | assessments                 | b6430cc0f403986249675794c9ab49b5d978f682_id |                                                                                                     |
      | competencyLevelDescriptor   | c91ae4718903d20289607c3c4335759e652ad569_id |                                                                                                     |
      | courses                     | d875eac3c6117f5448437c192ac1ea7c3cc977dd_id |                                                                                                     |
      | courseOfferings             | d082f77ab409ff0ae64686248a252c08bf71e9a2_id |                                                                                                     |
      | educationOrganizations      | 1b223f577827204a1c7e9c851dba06bea6b031fe_id |                                                                                                     |
      | graduationPlans             | 7f6e03f2a01f0f74258a1b0d8796be5eaf289f0a_id |                                                                                                     |
      | gradingPeriods              | 19b56717877893f8d13bcfe6cfc256811c60c8ff_id |                                                                                                     |
      | learningObjectives          | 80cd1d34c504eb760b9cb1ad2a97f10df98c96a1_id |                                                                                                     |
      | learningStandards           | c772fbb0f9b9210d1f2a1bfcd53018b205c46da6_id |                                                                                                     |
      | programs                    | 36980b1432275aae32437bb367fb3b66c5efc90e_id |                                                                                                     |
      | schools                     | 352e8570bd1116d11a72755b987902440045d346_id |                                                                                                     |
      | sections                    | 8d9ad6c3b870e8775016fff99fbd9c74920de8d5_id |                                                                                                     |
      | sessions                    | 3327329ef80b7419a48521818d65743234d6e5fb_id |                                                                                                     |
      | studentCompetencyObjectives | b7080a7f753939752b693bca21fe60375d15587e_id |                                                                                                     |
      | calendarDates               | e00dc4fb9d6be8372a549dea899fe1915a598c5c_id | directly associated to LEA IL-DAYBREAK 1b223f577827204a1c7e9c851dba06bea6b031fe_id                  |
      | calendarDates               | b6f70d30b51a569610b8f882c8fa3a2d2eefbee1_id | directly associated to LEA 11 02bdd6bf0fd5f761e6fc316ca6c763d4bb96c055_id                           |

