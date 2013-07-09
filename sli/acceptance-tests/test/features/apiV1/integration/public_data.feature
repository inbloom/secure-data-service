Feature: Users can see public entities

   Background: Update one calendar date to reference a school (until Odin generates Calendar Dates for schools)
   Given I update the "calendarDate" with ID "7629c5951c8af6dac204cf636d5a81acb64fc6ef_id" field "body.educationOrganizationId" to "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id"

  Scenario Outline: User can see public entities via direct id calls
    Given I log in to realm "<REALM>" using simple-idp as "<TYPE>" "<USERNAME>" with password "<PASSWORD>"
    And format "application/json"
    And I am using api version "v1"
    Then I validate I have access to entities via the API access pattern "/v1/Entity/Id":
      | entity                                                                           | id                                          | notes                                                                     |
      | assessments                                                                      | b6430cc0f403986249675794c9ab49b5d978f682_id |                                                                           |
      | competencyLevelDescriptor                                                        | c91ae4718903d20289607c3c4335759e652ad569_id |                                                                           |
      | courses                                                                          | d875eac3c6117f5448437c192ac1ea7c3cc977dd_id |                                                                           |
      | courseOfferings                                                                  | d082f77ab409ff0ae64686248a252c08bf71e9a2_id |                                                                           |
      | educationOrganizations                                                           | 1b223f577827204a1c7e9c851dba06bea6b031fe_id |                                                                           |
      | graduationPlans                                                                  | 7f6e03f2a01f0f74258a1b0d8796be5eaf289f0a_id |                                                                           |
      | gradingPeriods                                                                   | 19b56717877893f8d13bcfe6cfc256811c60c8ff_id |                                                                           |
      | learningObjectives                                                               | 80cd1d34c504eb760b9cb1ad2a97f10df98c96a1_id |                                                                           |
      | learningStandards                                                                | c772fbb0f9b9210d1f2a1bfcd53018b205c46da6_id |                                                                           |
      | programs                                                                         | 36980b1432275aae32437bb367fb3b66c5efc90e_id |                                                                           |
      | schools                                                                          | 352e8570bd1116d11a72755b987902440045d346_id |                                                                           |
      | sections                                                                         | 8d9ad6c3b870e8775016fff99fbd9c74920de8d5_id |                                                                           |
      | sessions                                                                         | 3327329ef80b7419a48521818d65743234d6e5fb_id |                                                                           |
      | studentCompetencyObjectives                                                      | b7080a7f753939752b693bca21fe60375d15587e_id |                                                                           |
      | calendarDates                                                                    |                                             |                                                                           |
      | calendarDates                                                                    | e00dc4fb9d6be8372a549dea899fe1915a598c5c_id | associated to LEA IL-DAYBREAK 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
      | calendarDates                                                                    | dcaab0add72ad8d37de0dafa312b4d23d35ddb21_id | associated to LEA IL-HIGHWIND 99d527622dcb51c465c515c0636d17e085302d5e_id |
      | educationOrganizations/1b223f577827204a1c7e9c851dba06bea6b031fe_id/calendarDates |                                             |                                                                           |
      | schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/calendarDates                |                                             |                                                                           |
      #| educationOrganizations/1b223f577827204a1c7e9c851dba06bea6b031fe_id/calendarDates | e00dc4fb9d6be8372a549dea899fe1915a598c5c_id |                                                                           |
      #| schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/calendarDates                | 7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |                                                                           |
      #| gradingPeriods/19b56717877893f8d13bcfe6cfc256811c60c8ff_id/calendarDates         |                                             |                                                                           |
      #| gradingPeriods/19b56717877893f8d13bcfe6cfc256811c60c8ff_id/calendarDates         | 54b0182a783a58ca4cb7266773266a2040fcd799_id |                                                                           |

      Examples:
        | REALM                                   | TYPE              | USERNAME          | PASSWORD              |
        #| Illinois Daybreak School District 4529  | aggregate viewer  | msmith            | msmith1234            |
        | Illinois Daybreak School District 4529  | leader            | mgonzales         | mgonzales1234         |
        | Illinois Daybreak School District 4529  | educator          | linda.kim         | linda.kim1234         |
        | Illinois Daybreak School District 4529  | admin             | akopel            | akopel1234            |
        #| Illinois Daybreak Parents               | parent            | marsha.sollars    | marsha.sollars1234    |
        #| Illinois Daybreak Students              | student           | student.m.sollars | student.m.sollars1234 |

  @wip
  Scenario Outline: Verify Rewrites for entities for staff
    Given I log in to realm "<REALM>" using simple-idp as "<TYPE>" "<USERNAME>" with password "<PASSWORD>"
      And my contextual access is defined by the table:
        | Context                | Ids                                         |
        | educationOrganizations | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
        | schools                | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
      And format "application/json"
      When I navigate to the base level URI <Entity> I should see the rewrite in the format of <URI>:
        | Entity                       | URI                                                                            |
        | /calendarDates               | /educationOrganizations/@ids/calendarDates                                     |
      When I navigate to the URI <URI> I should see the rewrite in the format of <Rewrite>:
        | URI                                                                                                                           | Rewrite                                                    |
        | /educationOrganizations/1b223f577827204a1c7e9c851dba06bea6b031fe_id/calendarDates/e00dc4fb9d6be8372a549dea899fe1915a598c5c_id | /calendarDates/e00dc4fb9d6be8372a549dea899fe1915a598c5c_id |
        | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/calendarDates/7629c5951c8af6dac204cf636d5a81acb64fc6ef_id                | /calendarDates/7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |
        | /gradingPeriods/19b56717877893f8d13bcfe6cfc256811c60c8ff_id/calendarDates/54b0182a783a58ca4cb7266773266a2040fcd799_id         | /calendarDates/54b0182a783a58ca4cb7266773266a2040fcd799_id |

      Examples:
       | REALM                                   | TYPE              | USERNAME          | PASSWORD              |
       | Illinois Daybreak School District 4529  | aggregate viewer  | msmith            | msmith1234            |
       | Illinois Daybreak School District 4529  | leader            | mgonzales         | mgonzales1234         |
       | Illinois Daybreak School District 4529  | educator          | linda.kim         | linda.kim1234         |
       | Illinois Daybreak School District 4529  | admin             | akopel            | akopel1234            |

  @wip
  Scenario Outline: Verify Rewrites for entities for students and parents
    Given I log in to realm "<REALM>" using simple-idp as "<TYPE>" "<USERNAME>" with password "<PASSWORD>"
      And my contextual access is defined by the table:
        | Context                | Ids                                         |
        | educationOrganizations | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
        | schools                | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
      And format "application/json"
      When I navigate to the base level URI <Entity> I should see the rewrite in the format of <URI>:
        | Entity                       | URI                                                             |
        | /calendarDates               | /schools/@ids/calendarDates                                     |
      When I navigate to the URI <URI> I should see the rewrite in the format of <Rewrite>:
        | URI                                                                                                                           | Rewrite                                                    |
        | /educationOrganizations/1b223f577827204a1c7e9c851dba06bea6b031fe_id/calendarDates/e00dc4fb9d6be8372a549dea899fe1915a598c5c_id | /calendarDates/e00dc4fb9d6be8372a549dea899fe1915a598c5c_id |
        | /schools/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/calendarDates/7629c5951c8af6dac204cf636d5a81acb64fc6ef_id                | /calendarDates/7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |
        | /gradingPeriods/19b56717877893f8d13bcfe6cfc256811c60c8ff_id/calendarDates/54b0182a783a58ca4cb7266773266a2040fcd799_id         | /calendarDates/54b0182a783a58ca4cb7266773266a2040fcd799_id |

      Examples:
       | REALM                                   | TYPE              | USERNAME          | PASSWORD              |
       | Illinois Daybreak Parents               | parent            | marsha.sollars    | marsha.sollars1234    |
       | Illinois Daybreak Students              | student           | student.m.sollars | student.m.sollars1234 |

