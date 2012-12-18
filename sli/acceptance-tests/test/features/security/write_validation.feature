@RALLY_US4556
Feature: As an SEA/LEA end user I would like to be able to write only to data within my hierarchy through the API,
  so that, even though I can access data outside of it because I have association with particular student,
  I should not be writing into data that I don't own.

  Scenario Outline: Write operations requiring explicit associations on an entity as an IT Admin Teacher
    Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"
    And format "application/vnd.slc+json"
    Given entity URI <Entity Resource URI>
  # Bad Create
    Given an invalid entity json document for a <Entity Type>
    When the entities referenced or associated edorg is out of my context
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 403
  # Create
    Given a valid entity json document for a <Entity Type>
    When I navigate to POST "/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
  # Bad Update
    When I set the <Update Field> to <Updated Value>
    And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 403
  # Bad Delete
  #  When I navigate to DELETE "/<ENTITY URI>/<ID WITH NO WRITE CONTEXT>"
  #  Then I should receive a return code of 403

  Examples:
    | Entity Type                    | Entity Resource URI   | Update Field             | Updated Value             |
#    | "cohort"                       | "cohorts"             | "cohortDescription"      | "frisbee golf team"       |
    | "disciplineIncident"           | "disciplineIncidents" | "incidentTime"           | "01:02:15"                |
#    | "program"                      | "programs"            | "programSponsor"         | "State Education Agency"  |
    | "section"                      | "sections"            | "sequenceOfCourse"       | "2"                       |
#    | "staff"                        | "staff"               | "sex"                    | "Female"                  |
#    | "student"                      | "students"            | "sex"                    | "Female"                  |
#    | "teacher"                      | "teachers"            | "highlyQualifiedTeacher" | "false"                   |

# Session and course require multiple levels of associations, e.g. course -> courseOffering -> section -> teacherSectionAssoc
#| "session"                      | "sessions"            | | |  "totalInstructionalDays" | "43"                                         |
#| "course"                       | "courses"             | "courseOffering"                       | "section"         | "courseDescription"      | "Advanced Linguistic Studies" |
