Feature: Users can access public entities

  Scenario: Update calendar dates to reference schools (until Odin generates Calendar Dates for schools)
#    Given I update the "calendarDate" with ID "7629c5951c8af6dac204cf636d5a81acb64fc6ef_id" field "body.educationOrganizationId" to "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id"
#    Given I update the "calendarDate" with ID "6f93d0a3e53c2d9c3409646eaab94155fe079e87_id" field "body.educationOrganizationId" to "352e8570bd1116d11a72755b987902440045d346_id"

    Scenario: Class Period Creation
    Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
     And format "application/json"
     And I am using api version "v1"
    When I POST and validate the following entities:
       | entityName      | entityType   | returnCode |
       | newClassPeriod  | classPeriod  | 201        |
       | newClassPeriod2 | classPeriod  | 201        |
    Then I verify the following response body fields in "/classPeriods/42921d6ca01bcee753d5bc81e2f3e1592ed05492_id":
       | field                   | value                                       |
       | id                      | 42921d6ca01bcee753d5bc81e2f3e1592ed05492_id |
       | entityType              | classPeriod                                 |
       | classPeriodName         | Fifth Period                                |
       | educationOrganizationId | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
    Then I verify the following response body fields in "/classPeriods/a78690d5d75f709066534ab6dbf4a69a0f69989f_id":
       | field                   | value                                       |
       | id                      | a78690d5d75f709066534ab6dbf4a69a0f69989f_id |
       | entityType              | classPeriod                                 |
       | classPeriodName         | Sixth Period                                |
       | educationOrganizationId | 352e8570bd1116d11a72755b987902440045d346_id |

  Scenario: Bell Schedule Creates
    Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
     And format "application/json"
     And I am using api version "v1"
    When I POST and validate the following entities:
       | entityName       | entityType   | returnCode |
       | newBellSchedule  | bellSchedule | 201        |
       | newBellSchedule2 | bellSchedule | 201        |
    Then I verify the following response body fields in "/bellSchedules/ab9e672f9bc3e9cc988763041a4435743d8a8e34_id":
       | field                                | value                                       |
       | id                                   | ab9e672f9bc3e9cc988763041a4435743d8a8e34_id |
       | entityType                           | bellSchedule                                |
       | bellScheduleName                     | English 42                                  |
       | educationOrganizationId              | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
       | meetingTime.classPeriodId            | 42921d6ca01bcee753d5bc81e2f3e1592ed05492_id |
       | meetingTime.alternateDayName         | Beige                                       |
       | meetingTime.startTime                | 09:00:00.000                                |
       | meetingTime.endTime                  | 09:55:00.000                                |
       | meetingTime.officialAttendancePeriod | true                                        |
       | gradeLevels.0                        | First grade                                 |
       | gradeLevels.1                        | Second grade                                |
       | calendarDateReference                | 7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |
    Then I verify the following response body fields in "/bellSchedules/6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id":
       | field                                | value                                       |
       | id                                   | 6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id |
       | entityType                           | bellSchedule                                |
       | bellScheduleName                     | Maths 17                                    |
       | educationOrganizationId              | 352e8570bd1116d11a72755b987902440045d346_id |
       | meetingTime.classPeriodId            | a78690d5d75f709066534ab6dbf4a69a0f69989f_id |
       | meetingTime.startTime                | 13:00:00.000                                |
       | meetingTime.endTime                  | 13:55:00.000                                |
       | gradeLevels.0                        | Third grade                                 |
       | gradeLevels.1                        | Fourth grade                                |
       | calendarDateReference                | 6f93d0a3e53c2d9c3409646eaab94155fe079e87_id |

@wip
  Scenario Outline: User access of public entities via direct id calls
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
      | gradingPeriods/19b56717877893f8d13bcfe6cfc256811c60c8ff_id/calendarDates         |                                             |                                                                           |
      | classPeriods                                                                     |                                             |                                                                           |
      | classPeriods                                                                     |42921d6ca01bcee753d5bc81e2f3e1592ed05492_id  |                                                                           |
      | classPeriods                                                                     |a78690d5d75f709066534ab6dbf4a69a0f69989f_id  |                                                                           |
      | educationOrganizations/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/classPeriods  |                                             |                                                                           |
      | educationOrganizations/352e8570bd1116d11a72755b987902440045d346_id/classPeriods  |                                             |                                                                           |
      | bellSchedules                                                                    |                                             |                                                                           |
      | bellSchedules                                                                    | ab9e672f9bc3e9cc988763041a4435743d8a8e34_id |                                                                           |
      | bellSchedules                                                                    | 6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id |                                                                           |
      | educationOrganizations/772a61c687ee7ecd8e6d9ad3369f7883409f803b_id/bellSchedules |                                             |                                                                           |
      | educationOrganizations/352e8570bd1116d11a72755b987902440045d346_id/bellSchedules |                                             |                                                                           |


    Then I validate that I am denied access to certain endpoints via API:
       | uri                        | rc  | description                |
       | /v1/calendarDates/1        | 404 | Non-existent calendar date |
       | /v1/classPeriods/1         | 404 | Non-existent class period  |
       | /v1/bellSchedules/1        | 404 | Non-existent bell schedule |

    Then I verify the following response body fields in "/calendarDates/7629c5951c8af6dac204cf636d5a81acb64fc6ef_id":
       | field                                                 | value                                       |
       | id                                                    | 7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |
       | entityType                                            | calendarDate                                |
       | calendarEvent                                         | Instructional day                           |
       | date                                                  | 2012-09-18                                  |
#       | educationOrganizationId                               | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |

    Then I verify the following response body fields in "/calendarDates/6f93d0a3e53c2d9c3409646eaab94155fe079e87_id":
       | field                                                 | value                                       |
       | id                                                    | 6f93d0a3e53c2d9c3409646eaab94155fe079e87_id |
       | entityType                                            | calendarDate                                |
       | calendarEvent                                         | Instructional day                           |
       | date                                                  | 2014-01-21                                  |
#       | educationOrganizationId                               | 352e8570bd1116d11a72755b987902440045d346_id |

    Then I verify the following response body fields in "/classPeriods/42921d6ca01bcee753d5bc81e2f3e1592ed05492_id":
       | field                   | value                                       |
       | id                      | 42921d6ca01bcee753d5bc81e2f3e1592ed05492_id |
       | entityType              | classPeriod                                 |
       | classPeriodName         | Fifth Period                                |
       | educationOrganizationId | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
    Then I verify the following response body fields in "/classPeriods/a78690d5d75f709066534ab6dbf4a69a0f69989f_id":
       | field                   | value                                       |
       | id                      | a78690d5d75f709066534ab6dbf4a69a0f69989f_id |
       | entityType              | classPeriod                                 |
       | classPeriodName         | Sixth Period                                |
       | educationOrganizationId | 352e8570bd1116d11a72755b987902440045d346_id |
    Then I verify the following response body fields in "/bellSchedules/ab9e672f9bc3e9cc988763041a4435743d8a8e34_id":
       | field                                | value                                       |
       | id                                   | ab9e672f9bc3e9cc988763041a4435743d8a8e34_id |
       | entityType                           | bellSchedule                                |
       | bellScheduleName                     | English 42                                  |
       | educationOrganizationId              | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
       | meetingTime.classPeriodId            | 42921d6ca01bcee753d5bc81e2f3e1592ed05492_id |
       | meetingTime.alternateDayName         | Beige                                       |
       | meetingTime.startTime                | 09:00:00.000                                |
       | meetingTime.endTime                  | 09:55:00.000                                |
       | meetingTime.officialAttendancePeriod | true                                        |
       | gradeLevels.0                        | First grade                                 |
       | gradeLevels.1                        | Second grade                                |
       | calendarDateReference                | 7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |
    Then I verify the following response body fields in "/bellSchedules/6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id":
       | field                                | value                                       |
       | id                                   | 6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id |
       | entityType                           | bellSchedule                                |
       | bellScheduleName                     | Maths 17                                    |
       | educationOrganizationId              | 352e8570bd1116d11a72755b987902440045d346_id |
       | meetingTime.classPeriodId            | a78690d5d75f709066534ab6dbf4a69a0f69989f_id |
       | meetingTime.startTime                | 13:00:00.000                                |
       | meetingTime.endTime                  | 13:55:00.000                                |
       | gradeLevels.0                        | Third grade                                 |
       | gradeLevels.1                        | Fourth grade                                |
       | calendarDateReference                | 6f93d0a3e53c2d9c3409646eaab94155fe079e87_id |

    #Verify base endpoint only contains calendarDates for the directly associated edOrgs
    When I navigate to GET "/v1/calendarDates"
    Then I should receive a return code of 200
     And I should receive a collection with <CALDATECOUNT> elements

    #Verify base endpoint only contains classPeriods for the directly associated edOrgs
    When I navigate to GET "/v1/classPeriods"
    Then I should receive a return code of 200
    And I should receive a collection with <CLASSPERIODCOUNT> elements

     #Verify base endpoint only contains bellSchedules for the directly associated edOrgs
     When I navigate to GET "/v1/bellSchedules"
     Then I should receive a return code of 200
     And I should receive a collection with <BELLSCHEDCOUNT> elements

    #Verify rewrites
    When I navigate to the base level URI <Entity> I should see the rewrite in the format of <URI>:
       | Entity                       | URI                                           |
       | /calendarDates               | /educationOrganizations/<EDORG>/calendarDates |
       | /classPeriods                | /educationOrganizations/<EDORG>/classPeriods  |
       | /bellSchedules               | /educationOrganizations/<EDORG>/bellSchedules |


    Examples: User Credentials and Expected Counts
       | REALM                                   | TYPE              | USERNAME          | PASSWORD              | CALDATECOUNT | CLASSPERIODCOUNT | BELLSCHEDCOUNT | EDORG             |
       | Illinois Daybreak School District 4529  | aggregate viewer  | msmith            | msmith1234            | 1            | 11               | 1              | 352e8570bd1116d11a72755b987902440045d346_id |
       | Illinois Daybreak School District 4529  | leader            | mgonzales         | mgonzales1234         | 1            | 11               | 1              | 352e8570bd1116d11a72755b987902440045d346_id |
       | Illinois Daybreak School District 4529  | educator          | linda.kim         | linda.kim1234         | 1            | 11               | 1              | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
       | Illinois Daybreak School District 4529  | admin             | akopel            | akopel1234            | 1            | 11               | 1              | 352e8570bd1116d11a72755b987902440045d346_id |
       | Illinois Daybreak Parents               | parent            | marsha.sollars    | marsha.sollars1234    | 1            | 11               | 1              | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
       | Illinois Daybreak Students              | student           | student.m.sollars | student.m.sollars1234 | 1            | 11               | 1              | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |

@wip
  Scenario: Public Entities Write Commands as a IT Admin (user with WRITE PUBLIC)
    Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "akopel" with password "akopel1234"
     And format "application/json"
     And I am using api version "v1"
    When I POST and validate the following entities:
       | entityName      | entityType   | returnCode |
       | newCalendarDate | calendarDate | 201        |
    Then I verify the following response body fields in "/calendarDates/611ce67cc258ae2d06bc3199ee678df0fb6cecab_id":
       | field                   | value                                       |
       | id                      | 611ce67cc258ae2d06bc3199ee678df0fb6cecab_id |
       | entityType              | calendarDate                                |
       | calendarEvent           | Instructional day                           |
       | date                    | 2015-04-02                                  |
       | educationOrganizationId | 352e8570bd1116d11a72755b987902440045d346_id |
    When I PATCH and validate the following entities:
       |  fieldName     |  entityType   | value   |  returnCode  | endpoint                                                  |
       |  calendarEvent |  calendarDate | Holiday |  204         | calendarDates/611ce67cc258ae2d06bc3199ee678df0fb6cecab_id |
    Then I verify the following response body fields in "/calendarDates/611ce67cc258ae2d06bc3199ee678df0fb6cecab_id":
       | field                   | value                                       |
       | id                      | 611ce67cc258ae2d06bc3199ee678df0fb6cecab_id |
       | entityType              | calendarDate                                |
       | calendarEvent           | Holiday                                     |
       | date                    | 2015-04-02                                  |
       | educationOrganizationId | 352e8570bd1116d11a72755b987902440045d346_id |
    When I PUT and validate the following entities:
       |  field         | entityName      |  value            | returnCode | endpoint                                                  |
       |  calendarEvent | newCalendarDate | Instructional day | 204        | calendarDates/611ce67cc258ae2d06bc3199ee678df0fb6cecab_id |
    Then I verify the following response body fields in "/calendarDates/611ce67cc258ae2d06bc3199ee678df0fb6cecab_id":
       | field                   | value                                       |
       | id                      | 611ce67cc258ae2d06bc3199ee678df0fb6cecab_id |
       | entityType              | calendarDate                                |
       | calendarEvent           | Instructional day                           |
       | date                    | 2015-04-02                                  |
       | educationOrganizationId | 352e8570bd1116d11a72755b987902440045d346_id |
    When I DELETE and validate the following entities:
       | entity       | id                                           | returnCode |
       | calendarDate | 611ce67cc258ae2d06bc3199ee678df0fb6cecab_id  | 204        |
     And I navigate to GET "/v1/calendarDates/611ce67cc258ae2d06bc3199ee678df0fb6cecab_id"
    Then I should receive a return code of 404

   #Out of Context Public Entities Writes as a IT Admin (user with WRITE PUBLIC)
    When I POST and validate the following entities:
       | entityName       | entityType   | returnCode |
       | newCalendarDate2 | calendarDate | 403        |
       | newClassPeriod   | classPeriod  | 403        |
       | newBellSchedule  | bellSchedule | 403        |
    When I PATCH and validate the following entities:
       |  fieldName      |  entityType    | value            |  returnCode  | endpoint                                                  |
       |  calendarEvent  |  calendarDate  | Holiday          |  403         | calendarDates/7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |
       | classPeriodName |  classPeriod   | First Period     |  405         | classPeriods/a78690d5d75f709066534ab6dbf4a69a0f69989f_id  |
       | gradeLevels     |  bellSchedule  | patchGradeLevels |  403         | bellSchedules/ab9e672f9bc3e9cc988763041a4435743d8a8e34_id  |
    Then I verify the following response body fields in "/calendarDates/7629c5951c8af6dac204cf636d5a81acb64fc6ef_id":
       | field                   | value                                       |
       | id                      | 7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |
       | entityType              | calendarDate                                |
       | calendarEvent           | Instructional day                           |
       | date                    | 2012-09-18                                  |
       | educationOrganizationId | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
    Then I verify the following response body fields in "/classPeriods/42921d6ca01bcee753d5bc81e2f3e1592ed05492_id":
       | field                   | value                                       |
       | id                      | 42921d6ca01bcee753d5bc81e2f3e1592ed05492_id |
       | entityType              | classPeriod                                 |
       | classPeriodName         | Fifth Period                                |
       | educationOrganizationId | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
    Then I verify the following response body fields in "/bellSchedules/ab9e672f9bc3e9cc988763041a4435743d8a8e34_id":
       | field                                | value                                       |
       | id                                   | ab9e672f9bc3e9cc988763041a4435743d8a8e34_id |
       | entityType                           | bellSchedule                                |
       | bellScheduleName                     | English 42                                  |
       | educationOrganizationId              | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
       | meetingTime.classPeriodId            | 42921d6ca01bcee753d5bc81e2f3e1592ed05492_id |
       | meetingTime.alternateDayName         | Beige                                       |
       | meetingTime.startTime                | 09:00:00.000                                |
       | meetingTime.endTime                  | 09:55:00.000                                |
       | meetingTime.officialAttendancePeriod | true                                        |
       | gradeLevels.0                        | First grade                                 |
       | gradeLevels.1                        | Second grade                                |
       | calendarDateReference                | 7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |
    When I PUT and validate the following entities:
       |  field          | entityName       |  value            | returnCode | endpoint                                                  |
       |  calendarEvent  | newCalendarDate2 | Holiday           | 403        | calendarDates/7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |
       | classPeriodName | newClassPeriod   | First Period      | 405        | classPeriods/42921d6ca01bcee753d5bc81e2f3e1592ed05492_id  |
       | gradeLevels     |  bellSchedule    | patchGradeLevels  | 403        | bellSchedules/ab9e672f9bc3e9cc988763041a4435743d8a8e34_id  |
    Then I verify the following response body fields in "/calendarDates/7629c5951c8af6dac204cf636d5a81acb64fc6ef_id":
       | field                   | value                                       |
       | id                      | 7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |
       | entityType              | calendarDate                                |
       | calendarEvent           | Instructional day                           |
       | date                    | 2012-09-18                                  |
       | educationOrganizationId | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
    Then I verify the following response body fields in "/classPeriods/42921d6ca01bcee753d5bc81e2f3e1592ed05492_id":
       | field                   | value                                       |
       | id                      | 42921d6ca01bcee753d5bc81e2f3e1592ed05492_id |
       | entityType              | classPeriod                                 |
       | classPeriodName         | Fifth Period                                |
       | educationOrganizationId | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
    Then I verify the following response body fields in "/bellSchedules/ab9e672f9bc3e9cc988763041a4435743d8a8e34_id":
       | field                                | value                                       |
       | id                                   | ab9e672f9bc3e9cc988763041a4435743d8a8e34_id |
       | entityType                           | bellSchedule                                |
       | bellScheduleName                     | English 42                                  |
       | educationOrganizationId              | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
       | meetingTime.classPeriodId            | 42921d6ca01bcee753d5bc81e2f3e1592ed05492_id |
       | meetingTime.alternateDayName         | Beige                                       |
       | meetingTime.startTime                | 09:00:00.000                                |
       | meetingTime.endTime                  | 09:55:00.000                                |
       | meetingTime.officialAttendancePeriod | true                                        |
       | gradeLevels.0                        | First grade                                 |
       | gradeLevels.1                        | Second grade                                |
       | calendarDateReference                | 7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |
    When I DELETE and validate the following entities:
       | entity       | id                                           | returnCode |
       | calendarDate | 7629c5951c8af6dac204cf636d5a81acb64fc6ef_id  | 403        |
       | classPeriod  | 42921d6ca01bcee753d5bc81e2f3e1592ed05492_id  | 403        |
       | bellSchedule | ab9e672f9bc3e9cc988763041a4435743d8a8e34_id  | 403        |
    Then I verify the following response body fields in "/calendarDates/7629c5951c8af6dac204cf636d5a81acb64fc6ef_id":
       | field                   | value                                       |
       | id                      | 7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |
       | entityType              | calendarDate                                |
       | calendarEvent           | Instructional day                           |
       | date                    | 2012-09-18                                  |
       | educationOrganizationId | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
    Then I verify the following response body fields in "/classPeriods/42921d6ca01bcee753d5bc81e2f3e1592ed05492_id":
       | field                   | value                                       |
       | id                      | 42921d6ca01bcee753d5bc81e2f3e1592ed05492_id |
       | entityType              | classPeriod                                 |
       | classPeriodName         | Fifth Period                                |
       | educationOrganizationId | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
    Then I verify the following response body fields in "/bellSchedules/ab9e672f9bc3e9cc988763041a4435743d8a8e34_id":
       | field                                | value                                       |
       | id                                   | ab9e672f9bc3e9cc988763041a4435743d8a8e34_id |
       | entityType                           | bellSchedule                                |
       | bellScheduleName                     | English 42                                  |
       | educationOrganizationId              | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id |
       | meetingTime.classPeriodId            | 42921d6ca01bcee753d5bc81e2f3e1592ed05492_id |
       | meetingTime.alternateDayName         | Beige                                       |
       | meetingTime.startTime                | 09:00:00.000                                |
       | meetingTime.endTime                  | 09:55:00.000                                |
       | meetingTime.officialAttendancePeriod | true                                        |
       | gradeLevels.0                        | First grade                                 |
       | gradeLevels.1                        | Second grade                                |
       | calendarDateReference                | 7629c5951c8af6dac204cf636d5a81acb64fc6ef_id |

  Scenario: Bell Schedule Delete - Part 1
    Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
     And format "application/json"
     And I am using api version "v1"
    When I DELETE and validate the following entities:
       | entity       | id                                           | returnCode |
       | bellSchedule | ab9e672f9bc3e9cc988763041a4435743d8a8e34_id  | 204        |
       | classPeriod  | 42921d6ca01bcee753d5bc81e2f3e1592ed05492_id  | 204        |
    When I navigate to GET "/v1/bellSchedules/ab9e672f9bc3e9cc988763041a4435743d8a8e34_id"
    Then I should receive a return code of 404
    When I navigate to GET "/v1/classPeriods/42921d6ca01bcee753d5bc81e2f3e1592ed05492_id"
    Then I should receive a return code of 404

  Scenario Outline: Public Entities Write Commands as non IT Admins (users without WRITE PUBLIC)
     Given I log in to realm "<REALM>" using simple-idp as "<TYPE>" "<USERNAME>" with password "<PASSWORD>"
      And format "application/json"
      And I am using api version "v1"
     When I POST and validate the following entities:
        | entityName           | entityType   | returnCode |
        | newCalendarDate      | calendarDate | 403        |
        | newClassPeriod       | classPeriod  | 403        |
        | newBellSchedule      | bellSchedule | 403        |
     When I navigate to GET "/v1/calendarDates/611ce67cc258ae2d06bc3199ee678df0fb6cecab_id"
     Then I should receive a return code of 404
     When I navigate to GET "/v1/classPeriods/42921d6ca01bcee753d5bc81e2f3e1592ed05492_id"
     Then I should receive a return code of 404
     When I navigate to GET "/v1/bellSchedules/ab9e672f9bc3e9cc988763041a4435743d8a8e34_id"
     Then I should receive a return code of 404

     When I PATCH and validate the following entities:
        |  fieldName      |  entityType   | value            |  returnCode  | endpoint                                                  |
        | calendarEvent   |  calendarDate | Holiday          |  403         | calendarDates/6f93d0a3e53c2d9c3409646eaab94155fe079e87_id |
        | classPeriodName |  classPeriod  | First Period     |  405         | classPeriods/a78690d5d75f709066534ab6dbf4a69a0f69989f_id  |
        | patchGradeLevels|  bellSchedule | Fifth Grade      |  403         | bellSchedules/6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id |

     Then I verify the following response body fields in "/calendarDates/6f93d0a3e53c2d9c3409646eaab94155fe079e87_id":
        | field                   | value                                       |
        | id                      | 6f93d0a3e53c2d9c3409646eaab94155fe079e87_id |
        | entityType              | calendarDate                                |
        | calendarEvent           | Instructional day                           |
        | date                    | 2014-01-21                                  |
#        | educationOrganizationId | 352e8570bd1116d11a72755b987902440045d346_id |
     Then I verify the following response body fields in "/classPeriods/a78690d5d75f709066534ab6dbf4a69a0f69989f_id":
        | field                   | value                                       |
        | id                      | a78690d5d75f709066534ab6dbf4a69a0f69989f_id |
        | entityType              | classPeriod                                 |
        | classPeriodName         | Sixth Period                                |
#        | educationOrganizationId | 352e8570bd1116d11a72755b987902440045d346_id |
     Then I verify the following response body fields in "/bellSchedules/6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id":
        | field                                | value                                       |
        | id                                   | 6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id |
        | entityType                           | bellSchedule                                |
        | bellScheduleName                     | Maths 17                                    |
#        | educationOrganizationId              | 352e8570bd1116d11a72755b987902440045d346_id |
        | meetingTime.classPeriodId            | a78690d5d75f709066534ab6dbf4a69a0f69989f_id |
        | meetingTime.startTime                | 13:00:00.000                                |
        | meetingTime.endTime                  | 13:55:00.000                                |
        | gradeLevels.0                        | Third grade                                 |
        | gradeLevels.1                        | Fourth grade                                |
        | calendarDateReference                | 6f93d0a3e53c2d9c3409646eaab94155fe079e87_id |

     When I PUT and validate the following entities:
        | field            | entityName      |  value            | returnCode | endpoint                                                  |
        | calendarEvent    | newCalendarDate | Holiday           | 403        | calendarDates/6f93d0a3e53c2d9c3409646eaab94155fe079e87_id |
        | classPeriodName  | newClassPeriod  | First Period      | 405        | classPeriods/a78690d5d75f709066534ab6dbf4a69a0f69989f_id  |
        | patchGradeLevels | newBellSchedule | Fifth Grade       | 403        | bellSchedules/6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id |

     Then I verify the following response body fields in "/calendarDates/6f93d0a3e53c2d9c3409646eaab94155fe079e87_id":
        | field                   | value                                       |
        | id                      | 6f93d0a3e53c2d9c3409646eaab94155fe079e87_id |
        | entityType              | calendarDate                                |
        | calendarEvent           | Instructional day                           |
        | date                    | 2014-01-21                                  |
#        | educationOrganizationId | 352e8570bd1116d11a72755b987902440045d346_id |
     Then I verify the following response body fields in "/classPeriods/a78690d5d75f709066534ab6dbf4a69a0f69989f_id":
        | field                   | value                                       |
        | id                      | a78690d5d75f709066534ab6dbf4a69a0f69989f_id |
        | entityType              | classPeriod                                 |
        | classPeriodName         | Sixth Period                                |
#        | educationOrganizationId | 352e8570bd1116d11a72755b987902440045d346_id |
     Then I verify the following response body fields in "/bellSchedules/6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id":
        | field                                | value                                       |
        | id                                   | 6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id |
        | entityType                           | bellSchedule                                |
        | bellScheduleName                     | Maths 17                                    |
#        | educationOrganizationId              | 352e8570bd1116d11a72755b987902440045d346_id |
        | meetingTime.classPeriodId            | a78690d5d75f709066534ab6dbf4a69a0f69989f_id |
        | meetingTime.startTime                | 13:00:00.000                                |
        | meetingTime.endTime                  | 13:55:00.000                                |
        | gradeLevels.0                        | Third grade                                 |
        | gradeLevels.1                        | Fourth grade                                |
        | calendarDateReference                | 6f93d0a3e53c2d9c3409646eaab94155fe079e87_id |

     When I DELETE and validate the following entities:
        | entity       | id                                           | returnCode |
        | calendarDate | 6f93d0a3e53c2d9c3409646eaab94155fe079e87_id  | 403        |
        | classPeriod  | a78690d5d75f709066534ab6dbf4a69a0f69989f_id  | 403        |
        | bellSchedule | 6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id  | 403        |

     Then I verify the following response body fields in "/calendarDates/6f93d0a3e53c2d9c3409646eaab94155fe079e87_id":
        | field                   | value                                       |
        | id                      | 6f93d0a3e53c2d9c3409646eaab94155fe079e87_id |
        | entityType              | calendarDate                                |
        | calendarEvent           | Instructional day                           |
        | date                    | 2014-01-21                                  |
#        | educationOrganizationId | 352e8570bd1116d11a72755b987902440045d346_id |
     Then I verify the following response body fields in "/classPeriods/a78690d5d75f709066534ab6dbf4a69a0f69989f_id":
        | field                   | value                                       |
        | id                      | a78690d5d75f709066534ab6dbf4a69a0f69989f_id |
        | entityType              | classPeriod                                 |
        | classPeriodName         | Sixth Period                                |
#        | educationOrganizationId | 352e8570bd1116d11a72755b987902440045d346_id |
     Then I verify the following response body fields in "/bellSchedules/6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id":
        | field                                | value                                       |
        | id                                   | 6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id |
        | entityType                           | bellSchedule                                |
        | bellScheduleName                     | Maths 17                                    |
#        | educationOrganizationId              | 352e8570bd1116d11a72755b987902440045d346_id |
        | meetingTime.classPeriodId            | a78690d5d75f709066534ab6dbf4a69a0f69989f_id |
        | meetingTime.startTime                | 13:00:00.000                                |
        | meetingTime.endTime                  | 13:55:00.000                                |
        | gradeLevels.0                        | Third grade                                 |
        | gradeLevels.1                        | Fourth grade                                |
        | calendarDateReference                | 6f93d0a3e53c2d9c3409646eaab94155fe079e87_id |

     Examples: User Credentials
         | REALM                                   | TYPE              | USERNAME          | PASSWORD              |
         | Illinois Daybreak School District 4529  | aggregate viewer  | msmith            | msmith1234            |
         | Illinois Daybreak School District 4529  | leader            | mgonzales         | mgonzales1234         |
         | Illinois Daybreak School District 4529  | educator          | linda.kim         | linda.kim1234         |
         | Illinois Daybreak Parents               | parent            | marsha.sollars    | marsha.sollars1234    |
         | Illinois Daybreak Students              | student           | student.m.sollars | student.m.sollars1234 |

  Scenario: Deletes - Part 2
    Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
     And format "application/json"
     And I am using api version "v1"
    When I DELETE and validate the following entities:
       | entity       | id                                           | returnCode |
       | bellSchedule | 6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id  | 204        |
       | classPeriod  | a78690d5d75f709066534ab6dbf4a69a0f69989f_id  | 204        |
    When I navigate to GET "/v1/bellSchedules/6e9a1c5a88fc1e007d4bd5e1640f721a3cdc7022_id"
    Then I should receive a return code of 404
     And I am using api version "v1"
    When I navigate to GET "/v1/classPeriods/a78690d5d75f709066534ab6dbf4a69a0f69989f_id"
    Then I should receive a return code of 404