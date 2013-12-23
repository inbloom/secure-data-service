@RALLY_US5795
Feature: As a staff member API user with multiple roles over different edOrgs,
  I want to be able to perform all CRUD operations on all bell Schedules.

  Background: Setup for the tests
    Given I import the odin setup application and realm data
    And the testing device app key has been created

  Scenario: Ensure GET can be performed on all public entities with READ_PUBLIC right
    Given I change the custom role of "Leader" to add the "READ_PUBLIC" right
    And I change the custom role of "Leader" to add the "WRITE_PUBLIC" right

    Given I change the custom role of "Educator" to add the "READ_PUBLIC" right
    And I change the custom role of "Educator" to add the "WRITE_PUBLIC" right

    And I change the custom role of "IT Administrator" to add the "READ_PUBLIC" right
    And I change the custom role of "IT Administrator" to add the "WRITE_PUBLIC" right

    And I delete all classPeriods
    And I delete all bellSchedules


  #  IL-DAYBREAK  Neither jmacey nor linda kim should be able to delete ILD classPeriods or bellSchedules
  #  |
  #  |______District 9(jmacey  LEA System Administrator, Leader)
  #             |
  #             |____East Daybreak High (jmacey Educator/Teacher)   Only jmacey should be able to delete ILD-EDH classPeriods or bellSchedules
  #             |
  #             |____Daybreak Central High (linda.kim Educator/Teacher) linda.kim and jmacey should both be able to delete ILD-DCH classPeriods and bellSchedules
  #

    And I log in as "jstevenson"
    And I create a "ILD Physics 1" classPeriod for "IL-DAYBREAK"
    And I create a "ILD-EDH Chemistry 1 JM" classPeriod for "East Daybreak High"
    And I create a "ILD-EDH Chemistry 1 LK" classPeriod for "East Daybreak High"
    And I create a "ILD-DCH Biology 1 JM" classPeriod for "Daybreak Central High"
    And I create a "ILD-DCH Biology 1 LK" classPeriod for "Daybreak Central High"

    And I create a "BS ILD Physics 1" bellSchedule for "ILD Physics 1"
    And I create a "BS ILD-EDH Chemistry 1 JM" bellSchedule for "ILD-EDH Chemistry 1 JM"
    And I create a "BS ILD-EDH Chemistry 1 LK" bellSchedule for "ILD-EDH Chemistry 1 LK"
    And I create a "BS ILD-DCH Biology 1 JM" bellSchedule for "ILD-DCH Biology 1 JM"
    And I create a "BS ILD-DCH Biology 1 LK" bellSchedule for "ILD-DCH Biology 1 LK"

    #jmacey can delete ILD-EDH, ILD-DCH but not ILD
    And I log in as "jmacey"
    And I try to delete bellSchedule "BS ILD Physics 1" and get "403"
    And I try to delete bellSchedule "BS ILD-EDH Chemistry 1 JM" and get "204"
    And I try to delete bellSchedule "BS ILD-DCH Biology 1 JM" and get "204"

    #linda.kim can delete ILD-DCH and nothing else
    And I log in as "linda.kim"
    And I try to delete bellSchedule "BS ILD Physics 1" and get "403"
    And I try to delete bellSchedule "BS ILD-EDH Chemistry 1 LK" and get "403"
    And I try to delete bellSchedule "BS ILD-DCH Biology 1 LK" and get "204"

    And I log in as "jmacey"
    And I try to delete classPeriod "ILD Physics 1" and get "403"
    #TODO Add step to check SecurityEvent
    And I try to delete classPeriod "ILD-EDH Chemistry 1 JM" and get "204"
    And I try to delete classPeriod "ILD-DCH Biology 1 JM" and get "204"

    And I log in as "linda.kim"
    And I try to delete classPeriod "ILD Physics 1" and get "403"
    #TODO Add step to check SecurityEvent
    And I try to delete classPeriod "ILD-EDH Chemistry 1 LK" and get "403"
    And I try to delete classPeriod "ILD-DCH Biology 1 LK" and get "204"

    #By default, Leader/Educator does not have WRITE_PUBLIC
    And I change the custom role of "Leader" to remove the "WRITE_PUBLIC" right
    And I change the custom role of "Educator" to remove the "WRITE_PUBLIC" right




