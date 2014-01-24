
Feature: As a user of the SLC API I would like to get counts from API calls without getting their results

    Scenario Outline: Check that correct counts come back from various queries
        Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
        When I navigate to a countOnly <url> with <id>
        Then I should get back just a count of <count>
    Examples:
        | url                                                                              | id                                        | count |
        | "/v1/staff/#{id}/staffProgramAssociations/programs"                              | "85585b27-5368-4f10-a331-3abcaf3a3f4c"    |    2  |
        | "/v1/calendarDates"                                                              | ""                                        |    4  |
        | "/v1/staff/#{id}/staffProgramAssociations"                                       | "85585b27-5368-4f10-a331-3abcaf3a3f4c"    |    3  |
        | "/v1/staff/#{id}/staffEducationOrgAssignmentAssociations"                        | "85585b27-5368-4f10-a331-3abcaf3a3f4c"    |    2  |
        | "/v1/educationOrganizations/#{id}/staffEducationOrgAssignmentAssociations/staff" | "b1bd3db6-d020-4651-b1b8-a8dba688d9e1"    |    5  |
        | "/v1/schools?parentEducationAgencyReference=b1bd3db6-d020-4651-b1b8-a8dba688d9e1"| ""                                        |   31  |
        | "/v1/educationOrganizations?parentEducationAgencyReference=b1bd3db6-d020-4651-b1b8-a8dba688d9e1" | ""                        |   37  |
        | "/v1/educationOrganizations/#{id}/staffEducationOrgAssignmentAssociations/staff" | "b1bd3db6-d020-4651-b1b8-a8dba688d9e1"    |    5  |
