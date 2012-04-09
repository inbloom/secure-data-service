Feature: As an SLI application, I want entity references displayed as usable links with exlicit link names
This means that when links are requested in a GET request, a link should be present that represents each reference field
and that the links are valid

Background: Nothing yet
    Given I am logged in using "demo" "demo1234" to realm "SLI"

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
        | "section"                               | "sections"                               | "programReference"               | "program"               | "programs"               | "getProgram"               | "getSections"                               | "58c9ef19-c172-4798-8e6e-c73e68ffb5a3" | ["e8d33606-d114-4ee4-878b-90ac7fc3df16"] | ["e8d33606-d114-4ee4-878b-90ac7fc3df16", "cb292c7d-3503-414a-92a2-dc76a1585d79"] |
        | "cohort"                                | "cohorts"                                | "programId"                      | "program"               | "programs"               | "getProgram"               | "getCohorts"                                | "a50121a2-c566-401b-99a5-71eb5cab5f4f" | ["e8d33606-d114-4ee4-878b-90ac7fc3df16"] | ["cb292c7d-3503-414a-92a2-dc76a1585d79"] |
        | "staffCohortAssociation"                | "staffCohortAssociations"                | "staffId"                        | "staff"                 | "staff"                  | "getStaff"                 | "getStaffCohortAssociations"                | "f4e62753-43e7-4cab-843b-c6c4a0d6c1c3" | ["f0e41d87-92d4-4850-9262-ed2f2723159b"] | ["ad878c6d-4eaf-4a8a-8284-8fb6570cea64"] |
        | "staffCohortAssociation"                | "staffCohortAssociations"                | "cohortId"                       | "cohort"                | "cohorts"                | "getCohort"                | "getStaffCohortAssociations"                | "e7a8cbfc-86c5-471f-a28d-dafe57b599a3" | ["7e9915ed-ea6f-4e6b-b8b0-aeae20a25826"] | ["a50121a2-c566-401b-99a5-71eb5cab5f4f"] |
        | "staffProgramAssociation"               | "staffProgramAssociations"               | "staffId"                        | "staff"                 | "staff"                  | "getStaff"                 | "getStaffProgramAssociations"               | "22f88217-4dc0-4113-a712-b6027c241606" | ["f0e41d87-92d4-4850-9262-ed2f2723159b"] | ["ad878c6d-4eaf-4a8a-8284-8fb6570cea64"] |
        | "staffProgramAssociation"               | "staffProgramAssociations"               | "programId"                      | "program"               | "programs"               | "getProgram"               | "getStaffProgramAssociations"               | "387f4818-b879-456a-acc7-5b4294c94549" | ["e8d33606-d114-4ee4-878b-90ac7fc3df16"] | ["cb292c7d-3503-414a-92a2-dc76a1585d79"] |