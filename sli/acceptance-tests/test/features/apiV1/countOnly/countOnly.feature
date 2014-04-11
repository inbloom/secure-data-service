Feature:
  As an API user
  In order to simply query for the number of entities in a collection resource
  I would like to get counts from API calls without getting their results

  Scenario Outline: Check that correct counts come back from various queries
        Given I have a session as a tenant-level IT Administrator
        When I navigate to a countOnly <url> with <id>
        Then I should get back just a count
    Examples:
        | url                                                                              | id                                        |
        | "/v1/calendarDates"                                                              | ""                                        |
        | "/v1/educationOrganizations/#{id}/staffEducationOrgAssignmentAssociations/staff" | "b1bd3db6-d020-4651-b1b8-a8dba688d9e1"    |
