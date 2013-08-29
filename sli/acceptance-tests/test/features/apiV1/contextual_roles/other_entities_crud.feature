@RALLY_US5775 @RALLY_US5777 @RALLY_US5778 @RALLY_US5789
Feature: As a staff member API user with multiple roles over different edOrgs,
  I want to be able to perform all CRUD operations on all other entities.

  Background: Setup for the tests
    Given I import the odin setup application and realm data
    And the testing device app key has been created


# Single segment (/<ENTITY>) URI tests.

  Scenario Outline: Ensure GET can be performed on all public entities with READ_PUBLIC right
    Given I change the custom role of "Leader" to add the "READ_PUBLIC" right
    Given I change the custom role of "Educator" to add the "READ_PUBLIC" right
    And I log in as "jmacey"
    And parameter "limit" is "0"
    Given entity type "<ENTITY TYPE>"
    When I navigate to GET "/v1/<ENTITY URI>"
    Then I should receive a return code of 200
    And I should see all global entities

  Examples:
    | ENTITY TYPE                | ENTITY URI                  |
    | assessment                 | assessments                 |
    | competencyLevelDescriptor  | competencyLevelDescriptor   |
    | courseOffering             | courseOfferings             |
    | course                     | courses                     |
    | gradingPeriod              | gradingPeriods              |
    | graduationPlan             | graduationPlans             |
    | educationOrganization      | schools                     |
    | session                    | sessions                    |
    | studentCompetencyObjective | studentCompetencyObjectives |

  Scenario Outline: Ensure GET can be performed on all edorg, student, and staff related entities with the proper rights
    And I log in as "jmacey"
    And parameter "limit" is "0"
    When I navigate to GET "/v1/<ENTITY URI>"
    Then I should receive a return code of 200

  Examples:
    | ENTITY URI                              |
    | staffEducationOrgAssignmentAssociations |
    | teacherSchoolAssociations               |
    | studentProgramAssociations              |
    | studentSchoolAssociations               |
    | cohorts                                 |
    | disciplineIncidents                     |
    | disciplineActions                       |
    | gradebookEntries                        |
    | attendances                             |
    | courseTranscripts                       |
    | studentAcademicRecords                  |
    | studentAssessments                      |
    | studentCohortAssociations               |
    | studentDisciplineIncidentAssociations   |
    | studentParentAssociations               |
    | studentSectionAssociations              |
    | reportCards                             |
    | staffCohortAssociations                 |
    | staffProgramAssociations                |
    | teacherSectionAssociations              |
    | grades                                  |
    | studentCompetencies                     |
    | studentGradebookEntries                 |
    | parents                                 |
    | staff                                   |
    | teachers                                |

  Scenario: Ensure GET can be performed on self entities with the proper rights
    And I log in as "msmith"
    And parameter "limit" is "0"
    When I navigate to GET "<msmith URI>/staffEducationOrgAssignmentAssociations"
    Then I should receive a return code of 200
    And I should receive a collection of "2" entities

  Scenario Outline: Ensure GET can NOT be performed on any public entities without READ_PUBLIC right
    Given I change the custom role of "Leader" to remove the "READ_PUBLIC" right
    Given I change the custom role of "Educator" to remove the "READ_PUBLIC" right
    And I log in as "jmacey"
    And parameter "limit" is "0"
    Given entity type "<ENTITY TYPE>"
    When I navigate to GET "/v1/<ENTITY URI>"
    Then I should receive a return code of 403

  Examples:
    | ENTITY TYPE                | ENTITY URI                  |
    | assessment                 | assessments                 |
    | competencyLevelDescriptor  | competencyLevelDescriptor   |
    | courseOffering             | courseOfferings             |
    | course                     | courses                     |
    | educationOrganization      | educationOrganizations      |
    | gradingPeriod              | gradingPeriods              |
    | graduationPlan             | graduationPlans             |
    | learningObjective          | learningObjectives          |
    | learningStandard           | learningStandards           |
    | program                    | programs                    |
    | educationOrganization      | schools                     |
    | section                    | sections                    |
    | session                    | sessions                    |
    | studentCompetencyObjective | studentCompetencyObjectives |
    | calendarDate               | calendarDates               |

  Scenario Outline: Ensure GET cannot be performed on all edorg, staff, and student related entities without the proper rights
    Given I change the custom role of "Leader" to remove the "READ_GENERAL" right
    And I change the custom role of "Leader" to remove the "READ_RESTRICTED" right
    And I change the custom role of "Educator" to remove the "READ_GENERAL" right
    And I change the custom role of "Educator" to remove the "READ_RESTRICTED" right
    And I log in as "jmacey"
    And parameter "limit" is "0"
    When I navigate to GET "/v1/<ENTITY URI>"
    Then I should receive a return code of 403

  Examples:
    | ENTITY URI                              |
    | staffEducationOrgAssignmentAssociations |
    | teacherSchoolAssociations               |
    | studentProgramAssociations              |
    | studentSchoolAssociations               |
    | cohorts                                 |
    | disciplineIncidents                     |
    | disciplineActions                       |
    | gradebookEntries                        |
    | attendances                             |
    | courseTranscripts                       |
    | studentAcademicRecords                  |
    | studentAssessments                      |
    | studentCohortAssociations               |
    | studentDisciplineIncidentAssociations   |
    | studentParentAssociations               |
    | studentSectionAssociations              |
    | reportCards                             |
    | staffCohortAssociations                 |
    | staffProgramAssociations                |
    | teacherSectionAssociations              |
    | grades                                  |
    | studentCompetencies                     |
    | studentGradebookEntries                 |
    | parents                                 |
    | staff                                   |
    | teachers                                |

 Scenario: GET lists of staff for a user with various contexts; verify URI is mutated correctly.
    Given the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | matt.sollars    | jmacey               | East Daybreak High    | yes                   |

   When I log in as "jmacey"
   And I navigate to GET "/v1/studentSchoolAssociations"
   Then I should receive a return code of 200
   And the header "X-ExecutedPath" contains "schools/<IDs>/studentSchoolAssociations"
   And the header "X-ExecutedPath" contains "<East Daybreak High>"
   And the header "X-ExecutedPath" contains "<District 9>"

   Given I change the custom role of "Educator" to remove the "TEACHER_CONTEXT" right
   When I log in as "jmacey"
   And I navigate to GET "/v1/studentSchoolAssociations"
   Then I should receive a return code of 200
   And the header "X-ExecutedPath" contains "schools/<IDs>/studentSchoolAssociations"
   And the header "X-ExecutedPath" contains "<East Daybreak High>"
   And the header "X-ExecutedPath" contains "<District 9>"

   Given I change the custom role of "Leader" to add the "TEACHER_CONTEXT" right
   And I change the custom role of "Educator" to add the "TEACHER_CONTEXT" right
   When I log in as "jmacey"
   And I navigate to GET "/v1/studentSchoolAssociations"
   Then I should receive a return code of 200
   And the header "X-ExecutedPath" contains "schools/<IDs>/studentSchoolAssociations"
   And the header "X-ExecutedPath" contains "<East Daybreak High>"
   And the header "X-ExecutedPath" contains "<District 9>"

   Given I change the custom role of "Leader" to remove the "STAFF_CONTEXT" right
   When I log in as "jmacey"
   And I navigate to GET "/v1/studentSchoolAssociations"
   Then I should receive a return code of 200
   And the header "X-ExecutedPath" contains "sections/<IDs>/studentSectionAssociations/students/studentSchoolAssociations"
   And the header "X-ExecutedPath" contains "<JMaceys Section>"

  Scenario Outline: Ensure POST can be performed on all public entities with READ_PUBLIC and WRITE_PUBLIC rights
    Given I change the custom role of "Leader" to add the "READ_PUBLIC" right
    Given I change the custom role of "Leader" to add the "WRITE_PUBLIC" right
    Given I change the custom role of "Educator" to add the "READ_PUBLIC" right
    Given I change the custom role of "Educator" to add the "WRITE_PUBLIC" right
    And I log in as "jmacey"
    Given entity type "<ENTITY>"
    Given a valid formatted entity json document for a "<ENTITY TYPE>"
    When I navigate to POST "/v1/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    And I remove the new entity from "<ENTITY>"

  Examples:
    | ENTITY                     | ENTITY TYPE                | ENTITY URI                  |
    | assessment                 | assessment                 | assessments                 |
    | competencyLevelDescriptor  | competencyLevelDescriptor  | competencyLevelDescriptor   |
    | educationOrganization      | educationOrganization      | educationOrganizations      |
    | gradingPeriod              | gradingPeriod              | gradingPeriods              |
    | graduationPlan             | graduationPlan             | graduationPlans             |
    | learningObjective          | learningObjective          | learningObjectives          |
    | learningStandard           | learningStandard           | learningStandards           |
    | program                    | program                    | programs                    |
    | educationOrganization      | school                     | schools                     |
    | section                    | section                    | sections                    |
    | session                    | session                    | sessions                    |
    | studentCompetencyObjective | studentCompetencyObjective | studentCompetencyObjectives |
    | calendarDate               | calendarDate               | calendarDates               |

  Scenario Outline: Ensure POST can be performed on edorg and student related entities with WRITE_GENERAL and WRITE_RESTRICTED rights
    Given I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I add a SEOA for "xbell" in "District 9" as a "Leader"
    And I log in as "xbell"
    Given a valid formatted entity json document for a "<ENTITY TYPE>"
    When I navigate to POST "/v1/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    And I remove the new entity from "<ENTITY>"

  Examples:
    | ENTITY                                       | ENTITY TYPE                           | ENTITY URI                              |
    | staffEducationOrganizationAssociation        | staffEducationOrganizationAssociation | staffEducationOrgAssignmentAssociations |
    | teacherSchoolAssociation                     | teacherSchoolAssociation              | teacherSchoolAssociations               |
    | student.studentProgramAssociation            | studentProgramAssociation             | studentProgramAssociations              |
    | studentSchoolAssociation                     | studentSchoolAssociation              | studentSchoolAssociations               |
    | cohort                                       | cohort                                | cohorts                                 |
    | disciplineIncident                           | disciplineIncident                    | disciplineIncidents                     |
    | disciplineAction                             | disciplineAction                      | disciplineActions                       |
    | section.gradebookEntry                       | gradebookEntry                        | gradebookEntries                        |
    | attendance                                   | attendance                            | attendances                             |
    | studentAssessment                            | studentAssessment                     | studentAssessments                      |
    | student.studentCohortAssociation             | studentCohortAssociation              | studentCohortAssociations               |
    | student.studentDisciplineIncidentAssociation | studentDisciplineIncidentAssociation  | studentDisciplineIncidentAssociations   |
    | student.studentParentAssociation             | studentParentAssociation              | studentParentAssociations               |
    | section.studentSectionAssociation            | studentSectionAssociation             | studentSectionAssociations              |
    | staffCohortAssociation                       | staffCohortAssociation                | staffCohortAssociations                 |
    | staffProgramAssociation                      | staffProgramAssociation               | staffProgramAssociations                |
    | section.teacherSectionAssociation            | teacherSectionAssociation             | teacherSectionAssociations              |
    | studentCompetency                            | studentCompetency                     | studentCompetencies                     |
    | parent                                       | parent                                | parents                                 |
    | staff                                        | staff                                 | staff                                  |
    | staff                                        | teacher                               | teachers                               |

  Scenario Outline: Ensure POST can NOT be performed on any public entities without READ_PUBLIC and WRITE_PUBLIC rights
    Given I change the custom role of "Leader" to remove the "READ_PUBLIC" right
    Given I change the custom role of "Leader" to remove the "WRITE_PUBLIC" right
    Given I change the custom role of "Educator" to remove the "READ_PUBLIC" right
    Given I change the custom role of "Educator" to remove the "WRITE_PUBLIC" right
    And I log in as "jmacey"
    Given entity type "<ENTITY>"
    Given a valid formatted entity json document for a "<ENTITY TYPE>"
    When I navigate to POST "/v1/<ENTITY URI>"
    Then I should receive a return code of 403

  Examples:
    | ENTITY                     | ENTITY TYPE                | ENTITY URI                  |
    | assessment                 | assessment                 | assessments                 |
    | competencyLevelDescriptor  | competencyLevelDescriptor  | competencyLevelDescriptor   |
    | courseOffering             | courseOffering             | courseOfferings             |
    | course                     | course                     | courses                     |
    | educationOrganization      | educationOrganization      | educationOrganizations      |
    | gradingPeriod              | gradingPeriod              | gradingPeriods              |
    | graduationPlan             | graduationPlan             | graduationPlans             |
    | learningObjective          | learningObjective          | learningObjectives          |
    | learningStandard           | learningStandard           | learningStandards           |
    | program                    | program                    | programs                    |
    | educationOrganization      | school                     | schools                     |
    | section                    | section                    | sections                    |
    | session                    | session                    | sessions                    |
    | studentCompetencyObjective | studentCompetencyObjective | studentCompetencyObjectives |
    | calendarDate               | calendarDate               | calendarDates               |

  Scenario Outline: Ensure POST cannot be performed on edorg or student related entities without WRITE_GENERAL and WRITE_RESTRICTED rights
    Given I add a SEOA for "xbell" in "District 9" as a "Leader"
    And I log in as "xbell"
    Given a valid formatted entity json document for a "<ENTITY TYPE>"
    When I navigate to POST "/v1/<ENTITY URI>"
    Then I should receive a return code of 403

  Examples:
    | ENTITY TYPE                           | ENTITY URI                              |
    | staffEducationOrganizationAssociation | staffEducationOrgAssignmentAssociations |
    | teacherSchoolAssociation              | teacherSchoolAssociations               |
    | studentProgramAssociation             | studentProgramAssociations              |
    | studentSchoolAssociation              | studentSchoolAssociations               |
    | cohort                                | cohorts                                 |
    | disciplineIncident                    | disciplineIncidents                     |
    | disciplineAction                      | disciplineActions                       |
    | gradebookEntry                        | gradebookEntries                        |
    | attendance                            | attendances                             |
    | studentAssessment                     | studentAssessments                      |
    | studentCohortAssociation              | studentCohortAssociations               |
    | studentDisciplineIncidentAssociation  | studentDisciplineIncidentAssociations   |
    | studentParentAssociation              | studentParentAssociations               |
    | studentSectionAssociation             | studentSectionAssociations              |
    | staffCohortAssociation                | staffCohortAssociations                 |
    | staffProgramAssociation               | staffProgramAssociations                |
    | teacherSectionAssociation             | teacherSectionAssociations              |
    | studentCompetency                     | studentCompetencies                     |
    | parent                                | parents                                 |
    | staff                                 | staff                                   |
    | teacher                               | teachers                                |

# Double segment (/<ENTITY>/{id}) URI tests.

  Scenario Outline: GETs on /entity/{id} for global entities
    Given I log in as "jmacey"
    And I get 10 random ids for "<ENTITY TYPE>" in "<ENTITY>"
    When I navigate to GET each id for "/v1/<ENTITY URI>"
    Then All the return codes should be 200

    Given I change the custom role of "Leader" to remove the "READ_PUBLIC" right
    And I change the custom role of "Educator" to remove the "READ_PUBLIC" right
    And I log in as "jmacey"
    When I navigate to GET each id for "/v1/<ENTITY URI>"
    Then All the return codes should be 403

  Examples:
    | ENTITY                     | ENTITY TYPE                | ENTITY URI                  |
    | assessment                 | assessment                 | assessments                 |
    | competencyLevelDescriptor  | competencyLevelDescriptor  | competencyLevelDescriptor   |
    | courseOffering             | courseOffering             | courseOfferings             |
    | course                     | course                     | courses                     |
    | educationOrganization      | educationOrganization      | educationOrganizations      |
    | gradingPeriod              | gradingPeriod              | gradingPeriods              |
    | graduationPlan             | graduationPlan             | graduationPlans             |
    | learningObjective          | learningObjective          | learningObjectives          |
    | learningStandard           | learningStandard           | learningStandards           |
    | program                    | program                    | programs                    |
    | educationOrganization      | school                     | schools                     |
    | section                    | section                    | sections                    |
    | session                    | session                    | sessions                    |
    | studentCompetencyObjective | studentCompetencyObjective | studentCompetencyObjectives |
    | calendarDate               | calendarDate               | calendarDates               |

  Scenario Outline: GETs on /entity/{id} for edorg-related entities
    Given I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right
    And I log in as "msmith"
    And I get 10 random ids associated with the edorgs for "msmith" of "<ENTITY TYPE>" in "<ENTITY>"
    When I navigate to GET each id for "/v1/<ENTITY URI>"
    Then All the return codes should be 200

    Given I change the custom role of "Leader" to remove the "READ_GENERAL" right
    And I change the custom role of "Leader" to remove the "READ_RESTRICTED" right
    And I change the custom role of "Aggregate Viewer" to remove the "READ_GENERAL" right
    And I log in as "msmith"
    When I navigate to GET each id for "/v1/<ENTITY URI>"
    Then All the return codes should be 403

  Examples:
    | ENTITY                   | ENTITY TYPE                          | ENTITY URI                            |
    | teacherSchoolAssociation | teacherSchoolAssociation             | teacherSchoolAssociations             |
    | student                  | studentProgramAssociation            | studentProgramAssociations            |
    | studentSchoolAssociation | studentSchoolAssociation             | studentSchoolAssociations             |
    | cohort                   | cohort                               | cohorts                               |
    | disciplineIncident       | disciplineIncident                   | disciplineIncidents                   |
    | disciplineAction         | disciplineAction                     | disciplineActions                     |
    | section                  | gradebookEntry                       | gradebookEntries                      |
    | attendance               | attendance                           | attendances                           |
    | yearlyTranscript         | studentAcademicRecord                | studentAcademicRecords                |
    | studentAssessment        | studentAssessment                    | studentAssessments                    |
    | student                  | studentCohortAssociation             | studentCohortAssociations             |
    | student                  | studentDisciplineIncidentAssociation | studentDisciplineIncidentAssociations |
    | student                  | studentParentAssociation             | studentParentAssociations             |
    | section                  | studentSectionAssociation            | studentSectionAssociations            |
    | yearlyTranscript         | reportCard                           | reportCards                           |
    | yearlyTranscript         | grade                                | grades                                |
    | studentCompetency        | studentCompetency                    | studentCompetencies                   |
    | studentGradebookEntry    | studentGradebookEntry                | studentGradebookEntries               |

  Scenario Outline: GETs on /entity/{id} for staff related entities
    Given I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right
    And I log in as "msmith"
    And I get 10 random ids of "<ENTITY TYPE>" in "<ENTITY>" associated with the staff of "msmith"
    When I navigate to GET each id for "/v1/<ENTITY URI>"
    Then All the return codes should be 200

    Given I change the custom role of "Leader" to remove the "READ_GENERAL" right
    And I change the custom role of "Leader" to remove the "READ_RESTRICTED" right
    And I change the custom role of "Aggregate Viewer" to remove the "READ_GENERAL" right
    And I log in as "msmith"
    When I navigate to GET each id for "/v1/<ENTITY URI>"
    Then All the return codes should be 403

  Examples:
    | ENTITY                   | ENTITY TYPE                          | ENTITY URI                            |
    | staffCohortAssociation   | staffCohortAssociation               | staffCohortAssociations               |
    | staffProgramAssociation  | staffProgramAssociation              | staffProgramAssociations              |
    | section                  | teacherSectionAssociation            | teacherSectionAssociations            |
    | staff                    | staff                                | staff                                 |
    | staff                    | teacher                              | teachers                              |

  Scenario: GETs on /parents/{id}
    Given I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right
    And I log in as "msmith"
    And I get 10 random ids for parents associated with the students of "msmith"
    When I navigate to GET each id for "/v1/parents"
    Then All the return codes should be 200

    Given I change the custom role of "Leader" to remove the "READ_GENERAL" right
    And I change the custom role of "Leader" to remove the "READ_RESTRICTED" right
    And I change the custom role of "Aggregate Viewer" to remove the "READ_GENERAL" right
    And I log in as "msmith"
    When I navigate to GET each id for "/v1/parents"
    Then All the return codes should be 403

  Scenario Outline: PUTs, PATCHes, and DELETEs on /entity/{id}
    Given I change the custom role of "Aggregate Viewer" to add the "WRITE_PUBLIC" right
    Given I change the custom role of "Aggregate Viewer" to add the "WRITE_GENERAL" right
    Given I change the custom role of "Aggregate Viewer" to add the "WRITE_RESTRICTED" right
    And format "application/json"
    And a valid json document for entity "<ENTITY TYPE>"

    When I log in as "msmith"
    And I navigate to POST "/v1/<ENTITY URI>"
    Then I should receive a return code of 201
    And I should receive a new entity URI
    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200

    When I log in as "jmacey"
    And I set the <MODIFY FIELD> to <PUT VALUE>
    And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 403

    When I log in as "msmith"
    And I navigate to PUT "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204

    When I log in as "jmacey"
    And I change the field "<MODIFY FIELD>" to "<PATCH VALUE>"
    And I navigate to PATCH "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 403

    When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 403

    When I log in as "msmith"
    And I navigate to PATCH "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204

    When I navigate to DELETE "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 204

    When I navigate to GET "/<ENTITY URI>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 404

  Examples:
    | ENTITY TYPE                           | ENTITY URI                              | MODIFY FIELD             | PUT VALUE                               | PATCH VALUE                             |
    | assessment                            | assessments                             | assessmentCategory       | Class test                              | Other                                   |
    | competencyLevelDescriptor             | competencyLevelDescriptor               | description              | Always Angry                            | Skips school                            |
    | educationOrganization                 | educationOrganizations                  | nameOfInstitution        | Dummy Agency                            | Dummy Edorg                             |
    | gradingPeriod                         | gradingPeriods                          | endDate                  | 2013-01-01                              | 2013-12-12                              |
    | graduationPlan                        | graduationPlans                         | individualPlan           | true                                    | false                                   |
    | learningObjective                     | learningObjectives                      | description              | My Description                          | New Description                         |
    | learningStandard                      | learningStandards                       | description              | My Description                          | New Description                         |
    | program                               | programs                                | programSponsor           | School                                  | State Education Agency                  |
    | school                                | schools                                 | nameOfInstitution        | Dummy Elementary                        | Dummy High                              |
    | section                               | sections                                | mediumOfInstruction      | Televised                               | Internship                              |
    | session                               | sessions                                | endDate                  | 2013-01-01                              | 2013-12-12                              |
    | studentCompetencyObjective            | studentCompetencyObjectives             | description              | Basic Objective                         | Advanced Objective                      |
    | staffEducationOrganizationAssociation | staffEducationOrgAssignmentAssociations | positionTitle            | Treasurer                               | Principal                               |
    | teacherSchoolAssociation              | teacherSchoolAssociations               | academicSubjects         | [Reading]                               | [Science]                               |
    | studentProgramAssociation             | studentProgramAssociations              | reasonExited             | Unknown reason                          | Expulsion                               |
    | studentSchoolAssociation              | studentSchoolAssociations               | entryGradeLevel          | Eleventh grade                          | Eighth grade                            |
    | cohort                                | cohorts                                 | cohortDescription        | Field Trip!                             | Wooo.. Field Trip                       |
    | disciplineIncident                    | disciplineIncidents                     | incidentLocation         | Off School                              | School bus                              |
    | disciplineAction                      | disciplineActions                       | disciplineDate           | 2013-02-01                              | 2013-03-01                              |
    | gradebookEntry                        | gradebookEntries                        | description              | Quiz entry                              | First quiz entry                        |
    | attendance                            | attendances                             | attendanceEvent          | [{'event':'Tardy','date':'2011-12-13'}] | [{'event':'Tardy','date':'2011-12-14'}] |
    | studentAssessment                     | studentAssessments                      | gradeLevelWhenAssessed   | Ninth grade                             | Adult Education                         |
    | studentCohortAssociation              | studentCohortAssociations               | endDate                  | 2013-12-31                              | 2012-05-08                              |
    | studentDisciplineIncidentAssociation  | studentDisciplineIncidentAssociations   | studentParticipationCode | Victim                                  | Reporter                                |
    | studentParentAssociation              | studentParentAssociations               | relation                 | Brother, natural/adoptive               | Father-in-law                           |
    | studentSectionAssociation             | studentSectionAssociations              | endDate                  | 2013-06-09                              | 2012-08-15                              |
    | staffCohortAssociation                | staffCohortAssociations                 | studentRecordAccess      | true                                    | false                                   |
    | staffProgramAssociation               | staffProgramAssociations                | endDate                  | 2013-01-01                              | 2012-12-12                              |
    | teacherSectionAssociation             | teacherSectionAssociations              | classroomPosition        | Support Teacher                         | Substitute Teacher                      |
    | calendarDate                          | calendarDates                           | calendarEvent            | Make-up day                             | Student late arrival/early dismissal    |
    | studentCompetency                     | studentCompetencies                     | diagnosticStatement      | Needs improvement                       | Very unsatisfactory                     |
    | parent                                | parents                                 | loginId                  | new-login                               | even-newer-login                        |
    | staff                                 | staff                                   | loginId                  | newer-staff-login                       | even-newer-staff-login                  |
    | teacher                               | teachers                                | loginId                  | newer-teacher-login                     | even-newer-teacher-login                |

# Multi segment (/<ENTITY>/{id}/...) URI tests.

  Scenario: GETs on multiple (more than 2) part URIs of global entities
    When I log in as "jmacey"
    And I navigate to GET "<District 9 URI>/staffEducationOrgAssignmentAssociations/staff"
    Then I should receive a return code of 200
    Given there is a course in the edOrg "<Daybreak Bayside High>"
    When I navigate to GET "<Daybreak Bayside High URI>/courses"
    Then I should receive a return code of 200
    When I navigate to GET "<Daybreak Bayside High URI>/staffEducationOrgAssignmentAssociations"
    Then I should receive a return code of 403

  Scenario Outline: Can view  for non-subdoc historical data of a student from a different edorg, but can't write to it
    Given I add "<Entity>" for "lashawn.taite" in "Daybreak Bayside High" that's already expired
    When I log in as "<User>"

    When I navigate to GET "/<Entity>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200

    Given format "application/json"
    When I change the field "<Field>" to "<New Value>"
    And I navigate to PATCH "/<Entity>/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 403

  Examples:
    | User      | Entity                      | Field                   | New Value                               |
    | tcuyper   | studentSchoolAssociations   | exitWithdrawType        | Exited                                  |
    | tcuyper   | attendances                 | attendanceEvent         | [{'event':'Tardy','date':'2011-12-13'}] |
    | tcuyper   | disciplineActions           | disciplineDate          | 2012-01-01                              |
    | tcuyper   | courseTranscripts           | finalLetterGradeEarned  | F                                       |


  Scenario Outline: Can view  for subdoc historical data of a student from a different edorg, but can't write to it
    Given I add subdoc "<subDoc>" for "lashawn.taite" and "<Reference>" in "Daybreak Bayside High" that's already expired
    When I log in as "<User>"
    And I navigate to GET "<lashawn.taite URI>/<subDoc>s"
    Then I should receive a return code of 200
    And the response should have the newly created entity

    When I navigate to GET "/<subDoc>s/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 200

    Given format "application/json"
    When I change the field "<Field>" to "<New Value>"
    And I navigate to PATCH "/<subDoc>s/<NEWLY CREATED ENTITY ID>"
    Then I should receive a return code of 403


  Examples:
    |  User      |           subDoc                         |    Reference             |    Field                   |                 New Value                       |
    |  tcuyper   |     reportCard                           |    yearlyTranscript      |    gradingPeriodId         |                  Reporter                       |
    |  tcuyper   |   studentProgramAssociation              |   program                |   reasonExited             |                   blabla                        |
    |  tcuyper   |   studentSectionAssociation              |   section                |   reasonExited             |                   blabla                        |


