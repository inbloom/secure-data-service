
@RALLY_US209
@RALLY_US1961
Feature: As an SLI application, I want entity references displayed as usable links with exlicit link names
  This means that when links are requested in a GET request, a link should be present that represents each reference field
  and that the links are valid

  Background: Nothing yet
    Given I am logged in using "cgrayadmin" "cgrayadmin1234" to realm "IL"

  Scenario Outline: Control the presence of links by specifying an accept type format
    Given format <format>
    And I request <return links>
    And I navigate to GET "/<URI FOR ENTITY THAT CAN RETURN LINKS>/<ID OF ENTITY THAT CAN RETURN LINKS>"
    Then I should receive a return code of 200
    And the response should contain links if I requested them
    Examples:
      | format                                    | return links |
      | "application/json;charset=utf-8"         | "links"      |
      | "application/vnd.slc+json;charset=utf-8" | "links"      |

  Scenario Outline: Confirm all known reference fields generate two valid links that are implemented and update-able
    Given format "application/vnd.slc+json;charset=utf-8"
    And referring collection <source entity type> exposed as <source expose name>
    And referring field <reference field> with value <reference value>
    And referred collection <target entity type> exposed as <target expose name>
    And the link from references to referred entity is <target link name>
    And the link from referred entity to referring entities is <source link name>
    And the ID to use for testing is <testing ID>
    And the ID to use for testing a valid update is <new valid value>
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a link named <target link name> with URI "/<URI OF REFERENCED ENTITY>/<REFERRED ENTITY ID>"
    When I navigate to GET "/<URI OF REFERENCED ENTITY>/<REFERRED ENTITY ID>"
    Then I should receive a return code of 200
    And "entityType" should be <target entity type>
    And I should receive a link named <source link name>
    When I navigate to GET the link named <source link name>
    Then I should receive a return code of 200
    And each entity's "entityType" should be <source entity type>
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 200
    When I set the list <reference field> to "<INVALID REFERENCE>"
    And I navigate to PUT "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 403
    When I set the list <reference field> to <new valid value>
    And I navigate to PUT "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 204
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then list <reference field> should be <new valid value>
    And I should receive a link named <target link name> for each value in list "<NEW VALID VALUE>" with URI prefix "/<URI OF REFERENCED ENTITY>"
    When I navigate to GET the first value in list "<NEW VALID VALUE>" with URI prefix "/<URI OF REFERENCED ENTITY>"
    Then "id" should be the first value in list "<NEW VALID VALUE>"
    And "entityType" should be <target entity type>
    Examples:
      | source entity type   | source expose name  | reference field          | target entity type    | target expose name       | target link name           | source link name     | testing ID                             | reference value                          | new valid value                          |
      | "reportCard"         | "reportCards"       | "studentCompetencyId"    | "studentCompetency"   | "studentCompetencies"    | "getStudentCompetency"     | "getReportCards"     | "648302f251c5a9b24753ddc45975720c939d7d73_id8770da5b-dca5-4ced-7919-201211120001" | ["b57643e4-9acf-11e1-7919-201211130002"] | ["b57643e4-9acf-11e1-7919-201211130003"] |
      | "session"            | "sessions"          | "gradingPeriodReference" | "gradingPeriod"       | "gradingPeriods"         | "getGradingPeriod"         | "getSessions"        | "4d796afd-b1ac-4f16-9e0d-6db47d445b55" | ["b40a7eb5-dd74-4666-a5b9-5c3f4425f130"] | ["ef72b883-90fa-40fa-afc2-4cb1ae17623b"] |
