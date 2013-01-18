@RALLY_US4214
Feature: As an SLI API, I want to be able to provide granular access to data.
  This means the user is able to request all the data within a school year range.

  Background: Use JSON format
    Given format "application/json"

  Scenario Outline: All data is returned within a specific school year range - staff
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And parameter "limit" is "0"
    When I navigate to GET "/v1/<Entity URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count Without Range>" entities
    Given parameter "schoolYears" is "2010-2011"
    When I navigate to GET "/v1/<Entity URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count With Range>" entities
    And I should only find "<Entity List>" in the list of entities returned
    Examples:
    # TODO: update the count with range and entity list
    | Entity URI                              | Count Without Range | Count With Range | Entity List |
    | attendances                             | 0                   | 99999            |             |
    | courseOfferings                         | 0                   | 99999            |             |
    | courseTranscripts                       | 0                   | 99999            |             |
    | disciplineActions                       | 2                   | 99999            |             |
    | disciplineIncidents                     | 0                   | 99999            |             |
    | gradebookEntries                        | 0                   | 99999            |             |
    | grades                                  | 0                   | 99999            |             |
    | gradingPeriods                          | 0                   | 99999            |             |
    | graduationPlans                         | 5                   | 99999            |             |
    | programs                                | 2                   | 99999            |             |
    | reportCards                             | 0                   | 99999            |             |
    | sections                                | 0                   | 99999            |             |
    | sessions                                | 0                   | 99999            |             |
    | staff                                   | 4                   | 99999            |             |
    | staffCohortAssociations                 | 2                   | 99999            |             |
    | staffEducationOrgAssignmentAssociations | 1                   | 99999            |             |
    | staffProgramAssociations                | 3                   | 99999            |             |
    | studentAcademicRecords                  | 0                   | 99999            |             |
    | studentAssessments                      | 0                   | 99999            |             |
    | studentCohortAssociations               | 6                   | 99999            |             |
    | studentCompetencies                     | 0                   | 99999            |             |
    | studentDisciplineIncidentAssociations   | 0                   | 99999            |             |
    | studentGradebookEntries                 | 0                   | 99999            |             |
    | studentProgramAssociations              | 10                  | 99999            |             |
    | studentSchoolAssociations               | 0                   | 99999            |             |
    | studentSectionAssociations              | 0                   | 99999            |             |
    | students                                | 0                   | 99999            |             |
    | teacherSchoolAssociations               | 0                   | 99999            |             |
    | teacherSectionAssociations              | 0                   | 99999            |             |
    | teachers                                | 0                   | 99999            |             |

  Scenario Outline: All data is returned within a specific school year range - teacher
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And parameter "limit" is "0"
    When I navigate to GET "/v1/<Entity URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count Without Range>" entities
    Given parameter "schoolYears" is "2010-2011"
    When I navigate to GET "/v1/<Entity URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count With Range>" entities
    And I should only find "<Entity List>" in the list of entities returned
    Examples:
    # TODO: update the count with range and entity list
    | Entity URI                              | Count Without Range | Count With Range | Entity List |
    | attendances                             | 29                  | 99999            |             |
    | courseOfferings                         | 39                  | 99999            |             |
    | courseTranscripts                       | 2                   | 99999            |             |
    | disciplineActions                       | 0                   | 99999            |             |
    | disciplineIncidents                     | 0                   | 99999            |             |
    | gradebookEntries                        | 3                   | 99999            |             |
    | grades                                  | 0                   | 99999            |             |
    | gradingPeriods                          | 2                   | 99999            |             |
    | graduationPlans                         | 5                   | 99999            |             |
    | programs                                | 0                   | 99999            |             |
    | reportCards                             | 1                   | 99999            |             |
    | sections                                | 4                   | 99999            |             |
    | sessions                                | 9                   | 99999            |             |
    | staff                                   | 1                   | 99999            |             |
    | staffCohortAssociations                 | 0                   | 99999            |             |
    | staffEducationOrgAssignmentAssociations | 1                   | 99999            |             |
    | staffProgramAssociations                | 0                   | 99999            |             |
    | studentAcademicRecords                  | 1                   | 99999            |             |
    | studentAssessments                      | 3                   | 99999            |             |
    | studentCohortAssociations               | 0                   | 99999            |             |
    | studentCompetencies                     | 0                   | 99999            |             |
    | studentDisciplineIncidentAssociations   | 0                   | 99999            |             |
    | studentGradebookEntries                 | 4                   | 99999            |             |
    | studentProgramAssociations              | 0                   | 99999            |             |
    | studentSchoolAssociations               | 67                  | 99999            |             |
    | studentSectionAssociations              | 31                  | 99999            |             |
    | students                                | 31                  | 99999            |             |
    | teacherSchoolAssociations               | 1                   | 99999            |             |
    | teacherSectionAssociations              | 4                   | 99999            |             |
    | teachers                                | 1                   | 99999            |             |

  Scenario Outline: Time-insensitive entities
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And parameter "schoolYears" is "2010-2011"
    And parameter "limit" is "0"
    When I navigate to GET "/v1/<Entity URI>"
    Then I should receive a return code of 400
    # TODO: update error message
    And the error message should say "NOOOOOOOOOOOOOOO!"
    Examples:
    | Entity URI                  |
    | assessments                 |
    | cohorts                     |
    | competencyLevelDescriptors  |
    | courses                     |
    | educationOrganizations      |
    | home                        |
    | learningObjectives          |
    | learningStandards           |
    | parents                     |
    | schools                     |
    | search                      |
    | studentCompetencyObjectives |
    | studentParentAssociations   |
    | system/session              |
    | system/support              |

  Scenario Outline: Different ways of overlapping the date range
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And a new "studentSchoolAssociation"
    When I navigate to POST "/studentSchoolAssociations"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    When I navigate to GET "/v1/studentSchoolAssociations/<NEW ID>"
    Then I should receive a return code of 200
    And I should receive a collection of "1" entities
    And the "entryDate" should be "2010-09-01"
    When I set the date "entryDate" to "<Entry Date>"
    And I set the date "exitWithdrawDate" to "<Exit Date>"
    And I navigate to PUT "/studentSchoolAssociations/<NEW ID>"
    Then I should receive a return code of 204
    Given parameter "schoolYears" is "2009-2011"
    And the session date range is "2009-09-06" to "2011-05-16"
    When I navigate to GET "/v1/studentSchoolAssociations/<NEW ID>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count>" entities
    And I delete the new "studentSchoolAssociation" for the next test scenario
    Examples:
    | Entry Date | Exit Date  | Count | # Note                                                |
    | 2001-01-01 | 2008-01-01 | 0     | # Starts and ends before range begins                 |
    | 2001-01-01 | 2009-01-01 | 0     | # Ends on the same day the range begins               |
    | 2001-01-01 | 2010-01-01 | 1     | # Starts before range begins, ends within range       |
    | 2001-01-01 | 2012-01-01 | 1     | # Starts before range begins, ends after range ends   |
    | 2001-01-01 | ?          | 1     | # Starts before range begins, ends unknown            |
    | 2009-12-01 | 2010-01-01 | 1     | # Completely within range                             |
    | 2009-06-01 | 2012-01-01 | 1     | # Starts after range begins, ends after range ends    |
    | 2009-06-01 | ?          | 1     | # Starts after range begins, ends unknown             |
    | 2011-05-16 | 2012-01-01 | 0     | # Starts on the same day the range ends               |
    | 2011-05-16 | ?          | 0     | # Starts on the same day the range ends, ends unknown |
    | 2012-01-01 | 2012-05-01 | 0     | # Starts and ends after range ends                    |
    | 2012-01-01 | ?          | 0     | # Starts after range ends, ends unknown               |

  Scenario Outline: Optional begin date - teacherSectionAssociation
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And a new "teacherSectionAssociation"
    When I navigate to POST "/studentSchoolAssociations"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    When I navigate to GET "/v1/teacherSectionAssociations/<NEW ID>"
    Then I should receive a return code of 200
    And I should receive a collection of "1" entities
    When I set the date "endDate" to "<Exit Date>"
    And I navigate to PUT "/teacherSectionAssociations/<NEW ID>"
    Then I should receive a return code of 204
    Given parameter "schoolYears" is "2009-2011"
    And the session date range is "2009-09-06" to "2011-05-16"
    When I navigate to GET "/v1/teacherSectionAssociations/<NEW ID>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count>" entities
    And I delete the new "teacherSectionAssociation" for the next test scenario
    Examples:
    | Exit Date  | Count | # Note                                                  |
    | 2001-01-01 | 0     | # Starts unknown, ends before range starts              |
    | 2009-09-06 | 0     | # Starts unknown, ends on the same day the range starts |
    | 2010-01-01 | 1     | # Starts unknown, ends within range                     |
    | 2012-01-01 | 1     | # Starts unknown, ends after range ends                 |
    | ?          | 0     | # Starts unknown, ends unknown                          |

  Scenario: Provide a really wide school year range to "get everything"
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And parameter "schoolYears" is "1900-2100"
    And parameter "limit" is "0"
    When I navigate to GET "/v1/studentSchoolAssociations"
    Then I should receive a return code of 200
    And I should receive a collection of "67" entities
    Given parameter "schoolYears" is "2010-2011"
    When I navigate to GET "/v1/studentSchoolAssociations"
    Then I should receive a return code of 200
    # TODO: update the count from above
    And I should receive a collection of "" entities

  Scenario Outline: Sad path - invalid date ranges
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And parameter "schoolYears" is "<Range>"
    When I navigate to GET "/v1/studentSectionAssociations"
    Then I should receive a return code of 400
    # TODO: update error message
    And the error message should say "NOOOOOOOOOOOOOOO!"
    Examples:
    | Range     | # Note                                |
    | 2011-2011 | # Same begin and end years            |
    | 2012-2009 | # Begin year is earlier than end year |
