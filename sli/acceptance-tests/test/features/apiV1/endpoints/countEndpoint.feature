@RALLY4037

Feature: As a user of inBloom, I would like to verify that the count endpoints are functional

    Scenario Outline: Check that the urls requested do actually exist
        Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
        When I navigate to count <url> and get <total> in field <fieldName>
        Then I should receive a valid return code

    Examples:
        | url                                                                        | total | fieldName    |
        | "/count/educationOrganizations"                                            | 24    | "totalStaff" |
        | "/count/educationOrganizations/a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"       | 4     | "totalStaff" |
        | "/count/teacherAssociations/a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"          | 1     | "total"      |
        | "/count/teacherAssociations/a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb/teachers" | 1     | "total"      |
