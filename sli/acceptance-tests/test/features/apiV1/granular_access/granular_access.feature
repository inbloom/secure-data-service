@RALLY_US4214
Feature: As an SLI API, I want to be able to provide granular access to data.
  This means the user is able to request all the data within a school year range.

  Background: Use JSON format
    Given format "application/json"

  @wip
  Scenario Outline: All data is returned within a specific school year range - staff
    Given I am logged in using "akopel" "akopel1234" to realm "IL"
    And parameter "limit" is "0"
    When I navigate to GET "/v1/<Entity URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count Without Range>" entities
    Given parameter "schoolYears" is "<School Years>"
    When I navigate to GET "/v1/<Entity URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count With Range>" entities
    And I should only find "<Entity List>" in the list of entities returned
    Examples:
    # 2009-2010: 2009-09-06 to 2010-05-16
    # 2010-2011: 2010-09-06 to 2011-05-16
    # 2011-2012: 2011-09-06 to 2012-06-31
    | Entity URI                              | School Years | Count Without Range | Count With Range | Entity List                             |
    | attendances                             | 2010-2011    | 0                   | 0                |                                         |
    | courseOfferings                         | 2010-2011    | 0                   | 0                |                                         |
    # | courseTranscripts                       | 2010-2011    | 0                   | 0                |                                         |
    | disciplineActions                       | 2010-2011    | 2                   | 0                |                                         |
    | disciplineIncidents                     | 2010-2011    | 0                   | 0                |                                         |
    | gradebookEntries                        | 2010-2011    | 0                   | 0                |                                         |
    | grades                                  | 2010-2011    | 0                   | 0                |                                         |
    | gradingPeriods                          | 2010-2011    | 0                   | 0                |                                         |
    | programs                                | 2009-2010    | 2                   | 1                | 9b8cafdc-8fd5-11e1-86ec-0021701f543f_id |
    # | reportCards                             | 2010-2011    | 0                   | 0                |                                         |
    # | sections                                | 2010-2011    | 0                   | 0                |                                         |
    | sessions                                | 2010-2011    | 0                   | 0                |                                         |
    | staffCohortAssociations                 | 2009-2010    | 2                   | 0                |                                         |
    | staffEducationOrgAssignmentAssociations | 2010-2011    | 1                   | 1                | c4d5d31b-001d-4573-b282-7e688a4676f9    |
    | staffProgramAssociations                | 2009-2010    | 3                   | 1                | 971638e0-03a8-43df-b4d3-a577fa5ff59c    |
    | studentAcademicRecords                  | 2010-2011    | 0                   | 0                |                                         |
    | studentAssessments                      | 2010-2011    | 0                   | 0                |                                         |
    | studentCohortAssociations               | 2009-2011    | 6                   | 5                | <LIST-SCA-RROGERS>                      |
    | studentCompetencies                     | 2010-2011    | 0                   | 0                |                                         |
    | studentDisciplineIncidentAssociations   | 2010-2011    | 0                   | 0                |                                         |
    | studentGradebookEntries                 | 2010-2011    | 0                   | 0                |                                         |
    | studentProgramAssociations              | 2009-2010    | 10                  | 0                |                                         |
    | studentSchoolAssociations               | 2010-2011    | 0                   | 0                |                                         |
    | studentSectionAssociations              | 2010-2011    | 0                   | 0                |                                         |
    | students                                | 2010-2011    | 0                   | 0                |                                         |
    | teacherSchoolAssociations               | 2010-2011    | 0                   | 0                |                                         |
    | teacherSectionAssociations              | 2010-2011    | 0                   | 0                |                                         |
    | teachers                                | 2010-2011    | 0                   | 0                |                                         |

  Scenario Outline: All data is returned within a specific school year range - teacher
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And parameter "limit" is "0"
    When I navigate to GET "/v1/<Entity URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count Without Range>" entities
    Given parameter "schoolYears" is "<School Years>"
    When I navigate to GET "/v1/<Entity URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count With Range>" entities
    And I should only find "<Entity List>" in the list of entities returned
    Examples:
    # 2009-2010: 2009-09-06 to 2010-05-16
    # 2010-2011: 2010-09-06 to 2011-05-16
    # 2011-2012: 2011-09-06 to 2012-06-31
    | Entity URI                              | School Years | Count Without Range | Count With Range | Entity List                                                                    |
    | attendances                             | 2010-2011    | 29                  | 1                | 530f0704-c240-4ed9-0a64-55c0308f91ee                                           |
    | courseOfferings                         | 2010-2011    | 39                  | 12               | <LIST-CO-LINDAKIM>                                                             |
#    | courseTranscripts                       | 2010-2011    | 2                   | 1                | f11a2a30-d4fd-4400-ae18-353c00d581a2                                           |
    | disciplineActions                       | 2010-2011    | 0                   | 0                |                                                                                |
    | disciplineIncidents                     | 2010-2011    | 0                   | 0                |                                                                                |
    | gradebookEntries                        | 2010-2011    | 3                   | 0                |                                                                                |
    | grades                                  | 2010-2011    | 0                   | 0                |                                                                                |
    | gradingPeriods                          | 2010-2011    | 2                   | 0                |                                                                                |
    | programs                                | 2010-2011    | 0                   | 0                |                                                                                |
#    | reportCards                             | 2009-2010    | 1                   | 0                |                                                                                |
#    | sections                                | 2010-2011    | 4                   | 2                | Science 7A - Sec 5f10,8th Grade English - Sec 6                                |
    | sessions                                | 2010-2011    | 9                   | 2                | <LIST-SESSION-LINDAKIM>                                                        |
    | staffCohortAssociations                 | 2010-2011    | 0                   | 0                |                                                                                |
    | staffEducationOrgAssignmentAssociations | 2010-2011    | 1                   | 1                | 2c6face89f0c2854667310b46808e21156ed73cc_id                                    |
    | staffProgramAssociations                | 2010-2011    | 0                   | 0                |                                                                                |
    | studentAcademicRecords                  | 2009-2010    | 1                   | 0                |                                                                                |
    | studentAssessments                      | 2009-2010    | 3                   | 0                |                                                                                |
    | studentCohortAssociations               | 2010-2011    | 0                   | 0                |                                                                                |
    | studentCompetencies                     | 2010-2011    | 0                   | 0                |                                                                                |
    | studentDisciplineIncidentAssociations   | 2010-2011    | 0                   | 0                |                                                                                |
    | studentGradebookEntries                 | 2010-2011    | 4                   | 0                |                                                                                |
    | studentProgramAssociations              | 2010-2011    | 0                   | 0                |                                                                                |
    | studentSchoolAssociations               | 2009-2010    | 67                  | 1                | 5738d251-dd0b-4734-9ea6-417ac9320a15_id                                        |
    | studentSectionAssociations              | 2010-2012    | 31                  | 1                | 706ee3be-0dae-4e98-9525-f564e05aa388_idbac890d6-b580-4d9d-a0d4-8bce4e8d351a_id |
    | students                                | 2010-2011    | 31                  | 0                |                                                                                |
    | teacherSchoolAssociations               | 2010-2011    | 1                   | 0                |                                                                                |
    | teacherSectionAssociations              | 2010-2011    | 4                   | 0                |                                                                                |
    | teachers                                | 2010-2011    | 1                   | 1                | linda.kim                                                                      |

  Scenario Outline: Time-insensitive entities
    Given I am logged in using "akopel" "akopel1234" to realm "IL"
    And parameter "schoolYears" is "2010-2011"
    And parameter "limit" is "0"
    When I navigate to GET "/v1/<Entity URI>"
    Then I should receive a return code of 400
    And the error message should say "Error Parsing the Query: Date range filtering not allowed"
    Examples:
    | Entity URI                  |
    | assessments                 |
    | cohorts                     |
    | competencyLevelDescriptor   |
    | courses                     |
    | educationOrganizations      |
    | graduationPlans             |
    | home                        |
    | learningObjectives          |
    | learningStandards           |
    | parents                     |
    | reportCards                 |
    | schools                     |
    | search                      |
    | staff                       |
    | studentCompetencyObjectives |
    | studentParentAssociations   |
    | system/session              |
    | system/support              |

  Scenario Outline: Different ways of overlapping the date range
    Given I am logged in using "akopel" "akopel1234" to realm "IL"
    And parameter "schoolYears" is "2009-2011"
    And the session date range is "2009-09-16" to "2011-05-16"
    When I navigate to GET "/v1/studentAssessments"
    Then I should receive a return code of 200
    And I should receive a collection of "2" entities
    Given a new "studentAssessment"
    When I set the date "administrationDate" to "<Entry Date>"
    And I set the date "administrationEndDate" to "<Exit Date>"
    And I navigate to POST "/v1/studentAssessments"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    When I navigate to GET "/v1/studentAssessments"
    Then I should receive a return code of 200
    And I should receive a collection of "<Count>" entities
    And I delete the new "studentAssessment" for the next test scenario
    Examples:
    | Entry Date | Exit Date  | Count | # Note                                                |
    | 2001-01-01 | 2008-01-01 | 2     | # Starts and ends before range begins                 |
    | 2001-01-01 | 2009-09-16 | 2     | # Ends on the same day the range begins               |
    | 2001-01-01 | 2010-01-01 | 3     | # Starts before range begins, ends within range       |
    | 2001-01-01 | 2012-01-01 | 3     | # Starts before range begins, ends after range ends   |
    | 2001-01-01 | ?          | 3     | # Starts before range begins, ends unknown            |
    | 2009-12-01 | 2010-01-01 | 3     | # Completely within range                             |
    | 2009-06-01 | 2012-01-01 | 3     | # Starts after range begins, ends after range ends    |
    | 2009-06-01 | ?          | 3     | # Starts after range begins, ends unknown             |
    | 2011-05-16 | 2012-01-01 | 2     | # Starts on the same day the range ends               |
    | 2011-05-16 | ?          | 2     | # Starts on the same day the range ends, ends unknown |
    | 2012-01-01 | 2012-05-01 | 2     | # Starts and ends after range ends                    |
    | 2012-01-01 | ?          | 2     | # Starts after range ends, ends unknown               |

  @wip
  Scenario Outline: Optional begin date - teacherSectionAssociation
    # Multiple hops to session
    Given I am logged in using "akopel" "akopel1234" to realm "IL"
    And parameter "schoolYears" is "2009-2011"
    And the session date range is "2009-09-06" to "2011-05-16"
    When I navigate to GET "/v1/teacherSectionAssociations"
    Then I should receive a return code of 200
    And I should receive a collection of "0" entities
    Given a new "teacherSectionAssociation"
    When I set the date "endDate" to "<Exit Date>"
    And I navigate to POST "/v1/teacherSectionAssociations"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    When I navigate to GET "/v1/teacherSectionAssociations"
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
    When I navigate to GET "/v1/sessions"
    Then I should receive a return code of 200
    And I should receive a collection of "9" entities
    Given parameter "schoolYears" is "2010-2011"
    When I navigate to GET "/v1/sessions"
    Then I should receive a return code of 200
    And I should receive a collection of "2" entities

  Scenario Outline: Sad path - invalid date ranges
    Given I am logged in using "akopel" "akopel1234" to realm "IL"
    And parameter "schoolYears" is "<Range>"
    When I navigate to GET "/v1/studentSectionAssociations"
    Then I should receive a return code of 400
    And the error message should say "Error Parsing the Query: Invalid date range"
    Examples:
    | Range     | # Note                                |
    | 2011-2011 | # Same begin and end years            |
    | 2012-2009 | # Begin year is earlier than end year |
@test
  Scenario: Sad path - Date filtering does not work on 2-part uris
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And parameter "schoolYears" is "2008-2009"
    When I navigate to GET "/v1/studentSectionAssociations/ceffbb26-1327-4313-9cfc-1c3afd38122e_id03143d0a-aacb-4e54-a945-ffb4061d00f5_id"
    Then I should receive a return code of 400
    And the error message should say "Error Parsing the Query: Date range filtering not allowed"
