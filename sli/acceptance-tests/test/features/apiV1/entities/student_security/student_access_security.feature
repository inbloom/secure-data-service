@wip
Feature: Student Access Security Mega Test
I want to test all combinations and permiations of accessing student data

Scenario Outline: Teacher views students through valid Section

  Given I am user <User> in IDP "AA"
  When I make an API call to get the student <Student>
    Then I should receive a return code of <Read Code>
    And the response <Restricted Data> restricted data and <General Data> general data
    When I make an API call to update the student <Student>
    Then I should receive a return code of <Write Code>
  Examples:
| User     | Student        | Read Code | Restricted Data | General Data | Write Code |Comment |
| "teach1" | "student1"     | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through future end-date section and future end-date enrollment |
| "teach2" | "student1"     | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through future end-date section and future end-date enrollment |
| "teach3" | "student1"     | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through future end-date section and future end-date enrollment |
| "teach4" | "student1"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date section and future end-date enrollment |
| "teach1" | "student2"     | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through future end-date section and no end-date enrollment |
| "teach2" | "student2"     | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through future end-date section and no end-date enrollment |
| "teach3" | "student2"     | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through future end-date section and no end-date enrollment |
| "teach4" | "student2"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date section and no end-date enrollment |
| "teach1" | "student3"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through future end-date section and past end-date enrollment |
| "teach2" | "student3"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through future end-date section and past end-date enrollment |
| "teach3" | "student3"     | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through future end-date section and past end-date enrollment |
| "teach4" | "student3"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date section and past end-date enrollment |
| "teach1" | "student4"     | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through no end-date section and future end-date enrollment |
| "teach2" | "student4"     | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through no end-date section and future end-date enrollment |
| "teach3" | "student4"     | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through no end-date section and future end-date enrollment |
| "teach4" | "student4"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date section and future end-date enrollment |
| "teach1" | "student5"     | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through no end-date section and no end-date enrollment |
| "teach2" | "student5"     | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through no end-date section and no end-date enrollment |
| "teach3" | "student5"     | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through no end-date section and no end-date enrollment |
| "teach4" | "student5"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date section and no end-date enrollment |
| "teach1" | "student6"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no end-date section and past end-date enrollment |
| "teach2" | "student6"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no end-date section and past end-date enrollment |
| "teach3" | "student6"     | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no end-date section and past end-date enrollment |
| "teach4" | "student6"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date section and past end-date enrollment |
| "teach1" | "student7"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date section and future end-date enrollment |
| "teach2" | "student7"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date section and future end-date enrollment |
| "teach3" | "student7"     | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date section and future end-date enrollment |
| "teach4" | "student7"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date section and future end-date enrollment |
| "teach1" | "student8"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date section and no end-date enrollment |
| "teach2" | "student8"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date section and no end-date enrollment |
| "teach3" | "student8"     | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date section and no end-date enrollment |
| "teach4" | "student8"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date section and no end-date enrollment |
| "teach1" | "student9"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date section and past end-date enrollment |
| "teach2" | "student9"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date section and past end-date enrollment |
| "teach3" | "student9"     | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date section and past end-date enrollment |
| "teach4" | "student9"     | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date section and past end-date enrollment |
| "teach1" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no section assoc and future end-date enrollment |
| "teach2" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no section assoc and future end-date enrollment |
| "teach3" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no section assoc and future end-date enrollment |
| "teach4" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no section assoc and future end-date enrollment |