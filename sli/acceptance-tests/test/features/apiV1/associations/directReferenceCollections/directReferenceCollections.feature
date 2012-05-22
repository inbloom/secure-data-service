Feature: As an SLI application, I want entity references displayed as usable links with exlicit link names
This means that when links are requested in a GET request, a link should be present that represents each reference field
and that the links are valid

Background: Nothing yet
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"

Scenario Outline: Control the presence of links by specifying an accept type format
   Given format <format>
     And I request <return links>
     And I navigate to GET "/<URI FOR ENTITY THAT CAN RETURN LINKS>/<ID OF ENTITY THAT CAN RETURN LINKS>"
    Then I should receive a return code of 200
     And the response should contain links if I requested them
    Examples:
        | format                     | return links |
        | "application/json"         | "links"      |
        | "application/vnd.slc+json" | "links"      |
#       | "application/xml"          |              |

Scenario Outline: Confirm all known reference fields generate two valid links that are implemented and update-able
   Given format "application/vnd.slc+json"
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
     And I should receive a link named <source link name> with URI "/<URI OF ENTITIES THAT REFER TO TARGET>"
    When I navigate to GET "/<URI OF ENTITIES THAT REFER TO TARGET>"
    Then I should receive a return code of 200
     And each entity's "entityType" should be <source entity type>
    When I navigate to GET "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
     And I set the list <reference field> to "<INVALID REFERENCE>"
     And I navigate to PUT "/<REFERRING COLLECTION URI>/<REFERRING ENTITY ID>"
    Then I should receive a return code of 400
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
        | source entity type                      | source expose name                       | reference field                  | target entity type      | target expose name       | target link name           | source link name                            | testing ID                             | reference value                          | new valid value                                                                  |
        | "reportCard"                            | "reportCards"                            | "studentCompetencyId"            | "studentCompetency"     | "studentCompetencies"    | "getStudentCompetency"     | "getReportCards"                            | "cf0ca1c6-a9db-4180-bf23-8276c4e2624c" | ["b57643e4-9acf-11e1-89a7-68a86d21d918"] | ["b57643e4-9acf-11e1-89a7-68a86d21d918", "3a2ea9f8-9acf-11e1-add5-68a86d21d918"] |
        | "section"                               | "sections"                               | "programReference"               | "program"               | "programs"               | "getProgram"               | "getSections"                               | "706ee3be-0dae-4e98-9525-f564e05aa388" | ["9b8cafdc-8fd5-11e1-86ec-0021701f543f"] | ["9b8cafdc-8fd5-11e1-86ec-0021701f543f", "cb292c7d-3503-414a-92a2-dc76a1585d79"] |
        | "cohort"                                | "cohorts"                                | "programId"                      | "program"               | "programs"               | "getProgram"               | "getCohorts"                                | "b40926af-8fd5-11e1-86ec-0021701f543f" | ["9b8c3aab-8fd5-11e1-86ec-0021701f543f"] | ["9b8cafdc-8fd5-11e1-86ec-0021701f543f"] |
