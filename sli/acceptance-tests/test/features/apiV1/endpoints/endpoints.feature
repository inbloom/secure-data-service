@RALLY4037

Feature: As a user of the SLC API I would like to see various endpoints exist.

    Scenario Outline: Check that the urls requested do actually exist
        Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
        When I navigate to <url> with <id>
        Then I should receive a valid return code
        Given I am logged in using "cgray" "cgray1234" to realm "IL"
        When I navigate to <url> with <id>
        Then I should receive a valid return code
    Examples:
        | url                                                                     | id                                     | 
        | "/schools/#{id}/courses"                                                | "67ce204b-9999-4a11-bfea-000000000005" |
        | "/schools/#{id}/courseOfferings"                                        | "67ce204b-9999-4a11-bfea-000000000005" |
        | "/schools/#{id}/sessions"                                               | "67ce204b-9999-4a11-bfea-000000000005" | 
        | "/schools/#{id}/sections/gradebookEntries"                              | "67ce204b-9999-4a11-bfea-000000000005" | 
        | "/schools/#{id}/sessions/gradingPeriods"                                | "67ce204b-9999-4a11-bfea-000000000005" |
        | "/educationOrganizations/#{id}/studentCompetencyObjectives"             | "67ce204b-9999-4a11-bfea-000000000005" |
        | "/staff/#{id}/disciplineIncidents/studentDisciplineIncidentAssociations"| "85585b27-5368-4f10-a331-3abcaf3a3f4c" |
        | "/staff/#{id}/disciplineIncidents"                                      | "85585b27-5368-4f10-a331-3abcaf3a3f4c" |
        | "/staff/#{id}/disciplineActions"                                        | "85585b27-5368-4f10-a331-3abcaf3a3f4c" |
        | "/sections/#{id}/gradebookEntries"                                      | "47b5adbf-6fd0-4f07-ba5e-39612da2e234" |

