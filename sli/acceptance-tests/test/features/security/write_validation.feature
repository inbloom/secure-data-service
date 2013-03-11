@RALLY_US4556
Feature: As an SEA/LEA end user I would like to be able to write only to data within my hierarchy through the API,
  so that, even though I can access data outside of it because I have association with particular student,
  I should not be writing into data that I don't own.

  Scenario Outline: Write operations requiring explicit associations on an entity as an IT Admin Teacher
    Given I am logged in using "cgrayadmin" "cgray1234" to realm "IL"
    And format "application/vnd.slc+json"
    And entity URI <Entity Resource URI>
  # Bad Create
    #When the sli securityEvent collection is empty
    And a valid entity json document for a <Entity Type>
    When the entities referenced or associated edorg is out of my context
    When I post the entity
    Then I should receive a return code of 403
    #And a security event matching "^Access Denied" should be in the sli db
    #And I check to find if record is in sli db collection:
     #| collectionName      | expectedRecordCount | searchParameter       | searchValue                           |
     #| securityEvent       | 1                   | body.userEdOrg        | IL-SUNSET                             |
     #| securityEvent       | 1                   | body.targetEdOrgList  | IL                                    |
  # Create
    Given a valid entity json document for a <Entity Type>
    When the entities referenced or associated edorg is in my context
    When I post the entity
    Then I should receive a return code of 201
    And I should receive a new entity URI
  # Bad Update
    #When the sli securityEvent collection is empty
    And I try to update the previously created entity with an invalid reference
    Then I should receive a return code of 403
    #And a security event matching "^Access Denied" should be in the sli db
    #And I check to find if record is in sli db collection:
     #| collectionName      | expectedRecordCount | searchParameter       | searchValue                           |
     #| securityEvent       | 1                   | body.userEdOrg        | IL-SUNSET                             |
     #| securityEvent       | 1                   | body.targetEdOrgList  | IL                                    |
  # Bad Delete
    #When the sli securityEvent collection is empty
    And I try to delete an entity that is out of my write context
    Then I should receive a return code of 403
    #And a security event matching "^Access Denied" should be in the sli db
    #And I check to find if record is in sli db collection:
     #| collectionName      | expectedRecordCount | searchParameter       | searchValue                           |
     #| securityEvent       | 1                   | body.userEdOrg        | IL-SUNSET                             |
     #| securityEvent       | 1                   | body.targetEdOrgList  | IL                                    |

  Examples:
    | Entity Type                    | Entity Resource URI          |
    | "attendance"                   | "attendances"                |
    | "cohort"                       | "cohorts"                    |
    | "course"                       | "courses"                    |
    | "courseOffering"               | "courseOfferings"            |
    | "courseTranscript"             | "courseTranscripts"          |
    | "disciplineAction"             | "disciplineActions"          |
    | "disciplineIncident"           | "disciplineIncidents"        |
    | "gradebookEntry"               | "gradebookEntries"           |
    | "graduationPlan"               | "graduationPlans"            |
    | "section"                      | "sections"                   |
    | "session"                      | "sessions"                   |
    | "studentGradebookEntry"        | "studentGradebookEntries"    |
    | "studentProgramAssociation"    | "studentProgramAssociations" |
    | "studentSectionAssociation"    | "studentSectionAssociations" |
    | "studentSchoolAssociation"     | "studentSchoolAssociations"  |
