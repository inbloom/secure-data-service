Feature: Student Access Security Mega Test
I want to test all combinations and permutations of accessing student data

@smoke
Scenario Outline: Teacher attempt to access students through sections
  Given I am user <User> in IDP "SEC"
  When I make an API call to get the student <Student>
    Then I should receive a return code of <Read Code>
    And I see the response <Restricted Data> restricted data and <General Data> general data
    When I make an API call to update the student <Student>
    Then I should receive a return code of <Write Code>
  Examples:
| User     | Student        | Read Code | Restricted Data | General Data | Write Code |Comment |
| "teach1" | "student01"    | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through future end-date teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach2" | "student01"    | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through future end-date teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach3" | "student01"    | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through future end-date teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach4" | "student01"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach1" | "student02"    | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through future end-date teacherSectionAssoc and no end-date studentSectionAssoc |
| "teach2" | "student02"    | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through future end-date teacherSectionAssoc and no end-date studentSectionAssoc |
| "teach3" | "student02"    | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through future end-date teacherSectionAssoc and no end-date studentSectionAssoc |
| "teach4" | "student02"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date teacherSectionAssoc and no end-date studentSectionAssoc |
| "teach1" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through future end-date teacherSectionAssoc and past end-date studentSectionAssoc |
| "teach2" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through future end-date teacherSectionAssoc and past end-date studentSectionAssoc |
| "teach3" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through future end-date teacherSectionAssoc and past end-date studentSectionAssoc |
| "teach4" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date teacherSectionAssoc and past end-date studentSectionAssoc |
| "teach1" | "student04"    | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through no end-date teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach2" | "student04"    | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through no end-date teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach3" | "student04"    | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through no end-date teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach4" | "student04"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach1" | "student05"    | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through no end-date teacherSectionAssoc and no end-date studentSectionAssoc |
| "teach2" | "student05"    | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through no end-date teacherSectionAssoc and no end-date studentSectionAssoc |
| "teach3" | "student05"    | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through no end-date teacherSectionAssoc and no end-date studentSectionAssoc |
| "teach4" | "student05"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date teacherSectionAssoc and no end-date studentSectionAssoc |
| "teach1" | "student06"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no end-date teacherSectionAssoc and past end-date studentSectionAssoc |
| "teach2" | "student06"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no end-date teacherSectionAssoc and past end-date studentSectionAssoc |
| "teach3" | "student06"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no end-date teacherSectionAssoc and past end-date studentSectionAssoc |
| "teach4" | "student06"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date teacherSectionAssoc and past end-date studentSectionAssoc |
| "teach1" | "student07"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach2" | "student07"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach3" | "student07"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach4" | "student07"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach1" | "student08"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date teacherSectionAssoc and no end-date studentSectionAssoc |
| "teach2" | "student08"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date teacherSectionAssoc and no end-date studentSectionAssoc |
| "teach3" | "student08"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date teacherSectionAssoc and no end-date studentSectionAssoc |
| "teach4" | "student08"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date teacherSectionAssoc and no end-date studentSectionAssoc |
| "teach1" | "student09"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date teacherSectionAssoc and past end-date studentSectionAssoc |
| "teach2" | "student09"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date teacherSectionAssoc and past end-date studentSectionAssoc |
| "teach3" | "student09"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date teacherSectionAssoc and past end-date studentSectionAssoc |
| "teach4" | "student09"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date teacherSectionAssoc and past end-date studentSectionAssoc |
| "teach1" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach2" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach3" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no teacherSectionAssoc and future end-date studentSectionAssoc |
| "teach4" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no teacherSectionAssoc and future end-date studentSectionAssoc |

@smoke
Scenario Outline: Teacher views students through Cohort

  Given I am user <User> in IDP "SEC"
  When I make an API call to get the student <Student>
    Then I should receive a return code of <Read Code>
    And I see the response <Restricted Data> restricted data and <General Data> general data
    When I make an API call to update the student <Student>
    Then I should receive a return code of <Write Code>
  Examples:
| User     | Student        | Read Code | Restricted Data | General Data | Write Code |Comment |
| "teach1" | "student11"    | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through future end-date, record-access true staffCohortAssoc and future end-date studentCohortAssoc |
| "teach2" | "student11"    | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through future end-date, record-access true staffCohortAssoc and future end-date studentCohortAssoc |
| "teach3" | "student11"    | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through future end-date, record-access true staffCohortAssoc and future end-date studentCohortAssoc |
| "teach4" | "student11"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date, record-access true staffCohortAssoc and future end-date studentCohortAssoc |
| "teach1" | "student12"    | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through future end-date, record-access true staffCohortAssoc and no end-date studentCohortAssoc |
| "teach2" | "student12"    | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through future end-date, record-access true staffCohortAssoc and no end-date studentCohortAssoc |
| "teach3" | "student12"    | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through future end-date, record-access true staffCohortAssoc and no end-date studentCohortAssoc |
| "teach4" | "student12"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date, record-access true staffCohortAssoc and no end-date studentCohortAssoc |
| "teach1" | "student13"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through future end-date, record-access true staffCohortAssoc and past end-date studentCohortAssoc |
| "teach2" | "student13"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through future end-date, record-access true staffCohortAssoc and past end-date studentCohortAssoc |
| "teach3" | "student13"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through future end-date, record-access true staffCohortAssoc and past end-date studentCohortAssoc |
| "teach4" | "student13"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date, record-access true staffCohortAssoc and past end-date studentCohortAssoc |
| "teach1" | "student14"    | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through no end-date, record-access true staffCohortAssoc and future end-date studentCohortAssoc |
| "teach2" | "student14"    | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through no end-date, record-access true staffCohortAssoc and future end-date studentCohortAssoc |
| "teach3" | "student14"    | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through no end-date, record-access true staffCohortAssoc and future end-date studentCohortAssoc |
| "teach4" | "student14"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date, record-access true staffCohortAssoc and future end-date studentCohortAssoc |
| "teach1" | "student15"    | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through no end-date, record-access true staffCohortAssoc and no end-date studentCohortAssoc |
| "teach2" | "student15"    | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through no end-date, record-access true staffCohortAssoc and no end-date studentCohortAssoc |
| "teach3" | "student15"    | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through no end-date, record-access true staffCohortAssoc and no end-date studentCohortAssoc |
| "teach4" | "student15"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date, record-access true staffCohortAssoc and no end-date studentCohortAssoc |
| "teach1" | "student16"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no end-date, record-access true staffCohortAssoc and past end-date studentCohortAssoc |
| "teach2" | "student16"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no end-date, record-access true staffCohortAssoc and past end-date studentCohortAssoc |
| "teach3" | "student16"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no end-date, record-access true staffCohortAssoc and past end-date studentCohortAssoc |
| "teach4" | "student16"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date, record-access true staffCohortAssoc and past end-date studentCohortAssoc |
| "teach1" | "student17"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date, record-access true staffCohortAssoc and future end-date studentCohortAssoc |
| "teach2" | "student17"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date, record-access true staffCohortAssoc and future end-date studentCohortAssoc |
| "teach3" | "student17"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date, record-access true staffCohortAssoc and future end-date studentCohortAssoc |
| "teach4" | "student17"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date, record-access true staffCohortAssoc and future end-date studentCohortAssoc |
| "teach1" | "student18"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date, record-access true staffCohortAssoc and no end-date studentCohortAssoc |
| "teach2" | "student18"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date, record-access true staffCohortAssoc and no end-date studentCohortAssoc |
| "teach3" | "student18"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date, record-access true staffCohortAssoc and no end-date studentCohortAssoc |
| "teach4" | "student18"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date, record-access true staffCohortAssoc and no end-date studentCohortAssoc |
| "teach1" | "student19"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date, record-access true staffCohortAssoc and past end-date studentCohortAssoc |
| "teach2" | "student19"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date, record-access true staffCohortAssoc and past end-date studentCohortAssoc |
| "teach3" | "student19"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date, record-access true staffCohortAssoc and past end-date studentCohortAssoc |
| "teach4" | "student19"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date, record-access true staffCohortAssoc and past end-date studentCohortAssoc |
| "teach1" | "student20"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through future end-date, record-access false staffCohortAssoc and future end-date studentCohortAssoc |
| "teach2" | "student20"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through future end-date, record-access false staffCohortAssoc and future end-date studentCohortAssoc |
| "teach3" | "student20"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through future end-date, record-access false staffCohortAssoc and future end-date studentCohortAssoc |
| "teach4" | "student20"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date, record-access false staffCohortAssoc and future end-date studentCohortAssoc |
| "teach1" | "student21"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through future end-date, record-access false staffCohortAssoc and no end-date studentCohortAssoc |
| "teach2" | "student21"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through future end-date, record-access false staffCohortAssoc and no end-date studentCohortAssoc |
| "teach3" | "student21"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through future end-date, record-access false staffCohortAssoc and no end-date studentCohortAssoc |
| "teach4" | "student21"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date, record-access false staffCohortAssoc and no end-date studentCohortAssoc |
| "teach1" | "student22"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through future end-date, record-access false staffCohortAssoc and past end-date studentCohortAssoc |
| "teach2" | "student22"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through future end-date, record-access false staffCohortAssoc and past end-date studentCohortAssoc |
| "teach3" | "student22"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through future end-date, record-access false staffCohortAssoc and past end-date studentCohortAssoc |
| "teach4" | "student22"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date, record-access false staffCohortAssoc and past end-date studentCohortAssoc |
| "teach1" | "student23"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no end-date, record-access false staffCohortAssoc and future end-date studentCohortAssoc |
| "teach2" | "student23"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no end-date, record-access false staffCohortAssoc and future end-date studentCohortAssoc |
| "teach3" | "student23"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no end-date, record-access false staffCohortAssoc and future end-date studentCohortAssoc |
| "teach4" | "student23"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date, record-access false staffCohortAssoc and future end-date studentCohortAssoc |
| "teach1" | "student24"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no end-date, record-access false staffCohortAssoc and no end-date studentCohortAssoc |
| "teach2" | "student24"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no end-date, record-access false staffCohortAssoc and no end-date studentCohortAssoc |
| "teach3" | "student24"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no end-date, record-access false staffCohortAssoc and no end-date studentCohortAssoc |
| "teach4" | "student24"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date, record-access false staffCohortAssoc and no end-date studentCohortAssoc |
| "teach1" | "student25"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no end-date, record-access false staffCohortAssoc and past end-date studentCohortAssoc |
| "teach2" | "student25"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no end-date, record-access false staffCohortAssoc and past end-date studentCohortAssoc |
| "teach3" | "student25"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no end-date, record-access false staffCohortAssoc and past end-date studentCohortAssoc |
| "teach4" | "student25"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date, record-access false staffCohortAssoc and past end-date studentCohortAssoc |
| "teach1" | "student26"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date, record-access false staffCohortAssoc and future end-date studentCohortAssoc |
| "teach2" | "student26"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date, record-access false staffCohortAssoc and future end-date studentCohortAssoc |
| "teach3" | "student26"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date, record-access false staffCohortAssoc and future end-date studentCohortAssoc |
| "teach4" | "student26"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date, record-access false staffCohortAssoc and future end-date studentCohortAssoc |
| "teach1" | "student27"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date, record-access false staffCohortAssoc and no end-date studentCohortAssoc |
| "teach2" | "student27"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date, record-access false staffCohortAssoc and no end-date studentCohortAssoc |
| "teach3" | "student27"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date, record-access false staffCohortAssoc and no end-date studentCohortAssoc |
| "teach4" | "student27"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date, record-access false staffCohortAssoc and no end-date studentCohortAssoc |
| "teach1" | "student28"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date, record-access false staffCohortAssoc and past end-date studentCohortAssoc |
| "teach2" | "student28"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date, record-access false staffCohortAssoc and past end-date studentCohortAssoc |
| "teach3" | "student28"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date, record-access false staffCohortAssoc and past end-date studentCohortAssoc |
| "teach4" | "student28"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date, record-access false staffCohortAssoc and past end-date studentCohortAssoc |
| "teach1" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no staffCohortAssoc and future end-date studentCohortAssoc |
| "teach2" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no staffCohortAssoc and future end-date studentCohortAssoc |
| "teach3" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no staffCohortAssoc and future end-date studentCohortAssoc |
| "teach4" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no staffCohortAssoc and future end-date studentCohortAssoc |

@smoke
Scenario Outline: Teacher views students through program

  Given I am user <User> in IDP "SEC"
  When I make an API call to get the student <Student>
    Then I should receive a return code of <Read Code>
    And I see the response <Restricted Data> restricted data and <General Data> general data
    When I make an API call to update the student <Student>
    Then I should receive a return code of <Write Code>
  Examples:
| User     | Student        | Read Code | Restricted Data | General Data | Write Code |Comment |
| "teach1" | "student29"    | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through future end-date, record-access true staffProgramAssoc and future end-date studentProgramAssoc |
| "teach2" | "student29"    | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through future end-date, record-access true staffProgramAssoc and future end-date studentProgramAssoc |
| "teach3" | "student29"    | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through future end-date, record-access true staffProgramAssoc and future end-date studentProgramAssoc |
| "teach4" | "student29"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date, record-access true staffProgramAssoc and future end-date studentProgramAssoc |
| "teach1" | "student30"    | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through future end-date, record-access true staffProgramAssoc and no end-date studentProgramAssoc |
| "teach2" | "student30"    | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through future end-date, record-access true staffProgramAssoc and no end-date studentProgramAssoc |
| "teach3" | "student30"    | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through future end-date, record-access true staffProgramAssoc and no end-date studentProgramAssoc |
| "teach4" | "student30"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date, record-access true staffProgramAssoc and no end-date studentProgramAssoc |
| "teach1" | "student31"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through future end-date, record-access true staffProgramAssoc and past end-date studentProgramAssoc |
| "teach2" | "student31"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through future end-date, record-access true staffProgramAssoc and past end-date studentProgramAssoc |
| "teach3" | "student31"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through future end-date, record-access true staffProgramAssoc and past end-date studentProgramAssoc |
| "teach4" | "student31"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date, record-access true staffProgramAssoc and past end-date studentProgramAssoc |
| "teach1" | "student32"    | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through no end-date, record-access true staffProgramAssoc and future end-date studentProgramAssoc |
| "teach2" | "student32"    | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through no end-date, record-access true staffProgramAssoc and future end-date studentProgramAssoc |
| "teach3" | "student32"    | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through no end-date, record-access true staffProgramAssoc and future end-date studentProgramAssoc |
| "teach4" | "student32"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date, record-access true staffProgramAssoc and future end-date studentProgramAssoc |
| "teach1" | "student33"    | 200       | "excludes"      | "includes"   | 403        | teacher as Educator access student through no end-date, record-access true staffProgramAssoc and no end-date studentProgramAssoc |
| "teach2" | "student33"    | 200       | "includes"      | "includes"   | 403        | teacher as Leader   access student through no end-date, record-access true staffProgramAssoc and no end-date studentProgramAssoc |
| "teach3" | "student33"    | 200       | "includes"      | "includes"   | 204        | teacher as IT Admin access student through no end-date, record-access true staffProgramAssoc and no end-date studentProgramAssoc |
| "teach4" | "student33"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date, record-access true staffProgramAssoc and no end-date studentProgramAssoc |
| "teach1" | "student34"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no end-date, record-access true staffProgramAssoc and past end-date studentProgramAssoc |
| "teach2" | "student34"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no end-date, record-access true staffProgramAssoc and past end-date studentProgramAssoc |
| "teach3" | "student34"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no end-date, record-access true staffProgramAssoc and past end-date studentProgramAssoc |
| "teach4" | "student34"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date, record-access true staffProgramAssoc and past end-date studentProgramAssoc |
| "teach1" | "student35"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date, record-access true staffProgramAssoc and future end-date studentProgramAssoc |
| "teach2" | "student35"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date, record-access true staffProgramAssoc and future end-date studentProgramAssoc |
| "teach3" | "student35"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date, record-access true staffProgramAssoc and future end-date studentProgramAssoc |
| "teach4" | "student35"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date, record-access true staffProgramAssoc and future end-date studentProgramAssoc |
| "teach1" | "student36"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date, record-access true staffProgramAssoc and no end-date studentProgramAssoc |
| "teach2" | "student36"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date, record-access true staffProgramAssoc and no end-date studentProgramAssoc |
| "teach3" | "student36"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date, record-access true staffProgramAssoc and no end-date studentProgramAssoc |
| "teach4" | "student36"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date, record-access true staffProgramAssoc and no end-date studentProgramAssoc |
| "teach1" | "student37"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date, record-access true staffProgramAssoc and past end-date studentProgramAssoc |
| "teach2" | "student37"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date, record-access true staffProgramAssoc and past end-date studentProgramAssoc |
| "teach3" | "student37"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date, record-access true staffProgramAssoc and past end-date studentProgramAssoc |
| "teach4" | "student37"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date, record-access true staffProgramAssoc and past end-date studentProgramAssoc |
| "teach1" | "student38"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through future end-date, record-access false staffProgramAssoc and future end-date studentProgramAssoc |
| "teach2" | "student38"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through future end-date, record-access false staffProgramAssoc and future end-date studentProgramAssoc |
| "teach3" | "student38"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through future end-date, record-access false staffProgramAssoc and future end-date studentProgramAssoc |
| "teach4" | "student38"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date, record-access false staffProgramAssoc and future end-date studentProgramAssoc |
| "teach1" | "student39"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through future end-date, record-access false staffProgramAssoc and no end-date studentProgramAssoc |
| "teach2" | "student39"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through future end-date, record-access false staffProgramAssoc and no end-date studentProgramAssoc |
| "teach3" | "student39"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through future end-date, record-access false staffProgramAssoc and no end-date studentProgramAssoc |
| "teach4" | "student39"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date, record-access false staffProgramAssoc and no end-date studentProgramAssoc |
| "teach1" | "student40"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through future end-date, record-access false staffProgramAssoc and past end-date studentProgramAssoc |
| "teach2" | "student40"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through future end-date, record-access false staffProgramAssoc and past end-date studentProgramAssoc |
| "teach3" | "student40"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through future end-date, record-access false staffProgramAssoc and past end-date studentProgramAssoc |
| "teach4" | "student40"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through future end-date, record-access false staffProgramAssoc and past end-date studentProgramAssoc |
| "teach1" | "student41"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no end-date, record-access false staffProgramAssoc and future end-date studentProgramAssoc |
| "teach2" | "student41"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no end-date, record-access false staffProgramAssoc and future end-date studentProgramAssoc |
| "teach3" | "student41"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no end-date, record-access false staffProgramAssoc and future end-date studentProgramAssoc |
| "teach4" | "student41"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date, record-access false staffProgramAssoc and future end-date studentProgramAssoc |
| "teach1" | "student42"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no end-date, record-access false staffProgramAssoc and no end-date studentProgramAssoc |
| "teach2" | "student42"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no end-date, record-access false staffProgramAssoc and no end-date studentProgramAssoc |
| "teach3" | "student42"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no end-date, record-access false staffProgramAssoc and no end-date studentProgramAssoc |
| "teach4" | "student42"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date, record-access false staffProgramAssoc and no end-date studentProgramAssoc |
| "teach1" | "student43"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no end-date, record-access false staffProgramAssoc and past end-date studentProgramAssoc |
| "teach2" | "student43"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no end-date, record-access false staffProgramAssoc and past end-date studentProgramAssoc |
| "teach3" | "student43"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no end-date, record-access false staffProgramAssoc and past end-date studentProgramAssoc |
| "teach4" | "student43"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no end-date, record-access false staffProgramAssoc and past end-date studentProgramAssoc |
| "teach1" | "student44"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date, record-access false staffProgramAssoc and future end-date studentProgramAssoc |
| "teach2" | "student44"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date, record-access false staffProgramAssoc and future end-date studentProgramAssoc |
| "teach3" | "student44"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date, record-access false staffProgramAssoc and future end-date studentProgramAssoc |
| "teach4" | "student44"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date, record-access false staffProgramAssoc and future end-date studentProgramAssoc |
| "teach1" | "student45"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date, record-access false staffProgramAssoc and no end-date studentProgramAssoc |
| "teach2" | "student45"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date, record-access false staffProgramAssoc and no end-date studentProgramAssoc |
| "teach3" | "student45"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date, record-access false staffProgramAssoc and no end-date studentProgramAssoc |
| "teach4" | "student45"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date, record-access false staffProgramAssoc and no end-date studentProgramAssoc |
| "teach1" | "student46"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through past end-date, record-access false staffProgramAssoc and past end-date studentProgramAssoc |
| "teach2" | "student46"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through past end-date, record-access false staffProgramAssoc and past end-date studentProgramAssoc |
| "teach3" | "student46"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through past end-date, record-access false staffProgramAssoc and past end-date studentProgramAssoc |
| "teach4" | "student46"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through past end-date, record-access false staffProgramAssoc and past end-date studentProgramAssoc |
| "teach1" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Educator access student through no staffProgramAssoc and future end-date studentProgramAssoc |
| "teach2" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Leader   access student through no staffProgramAssoc and future end-date studentProgramAssoc |
| "teach3" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as IT Admin access student through no staffProgramAssoc and future end-date studentProgramAssoc |
| "teach4" | "student10"    | 403       | "excludes"      | "excludes"   | 403        | teacher as Agg View access student through no staffProgramAssoc and future end-date studentProgramAssoc |

@smoke
Scenario Outline: Staff attempts to access specific students as various roles

Given I am user <User> in IDP "SEC"
When I make an API call to get the student <Student>
  Then I should receive a return code of <Read Code>
    And I see the response <Restricted Data> restricted data and <General Data> general data
  When I make an API call to update the student <Student>
  Then I should receive a return code of <Write Code>
Examples:
| User      | Student        | Read Code | Restricted Data | General Data | Write Code |Comment |
| "staff1"  | "student01"    | 200       | "excludes"      | "includes"   | 403        | school-staff as Educator access student because the student is currently enrolled |
| "staff2"  | "student01"    | 200       | "includes"      | "includes"   | 403        | school-staff as Leader   access student because the student is currently enrolled |
| "staff3"  | "student01"    | 200       | "includes"      | "includes"   | 204        | school-staff as IT Admin access student because the student is currently enrolled |
| "staff4"  | "student01"    | 403       | "excludes"      | "excludes"   | 403        | school-staff as Agg View access student because the student is currently enrolled |
| "staff5"  | "student01"    | 403       | "excludes"      | "excludes"   | 403        | school-staff can't access student because the staff was terminated |
| "staff18" | "student01"    | 403       | "excludes"      | "excludes"   | 403        | school-staff can't access student because the staff was not associated |

| "staff1"  | "student02"    | 200       | "excludes"      | "includes"   | 403        | school-staff as Educator access student because the student is currently enrolled with no end-date |
| "staff2"  | "student02"    | 200       | "includes"      | "includes"   | 403        | school-staff as Leader   access student because the student is currently enrolled with no end-date |
| "staff3"  | "student02"    | 200       | "includes"      | "includes"   | 204        | school-staff as IT Admin access student because the student is currently enrolled with no end-date |
| "staff4"  | "student02"    | 403       | "excludes"      | "excludes"   | 403        | school-staff as Agg View access student because the student is currently enrolled with no end-date |
| "staff5"  | "student02"    | 403       | "excludes"      | "excludes"   | 403        | school-staff can't access student because the staff was terminated |
| "staff18" | "student02"    | 403       | "excludes"      | "excludes"   | 403        | school-staff can't access student because the staff was not associated |

| "staff1"  | "student02"    | 200       | "excludes"      | "includes"   | 403        | school-staff as Educator access student because the student is currently enrolled with no end-date |
| "staff2"  | "student02"    | 200       | "includes"      | "includes"   | 403        | school-staff as Leader   access student because the student is currently enrolled with no end-date |
| "staff3"  | "student02"    | 200       | "includes"      | "includes"   | 204        | school-staff as IT Admin access student because the student is currently enrolled with no end-date |
| "staff4"  | "student02"    | 403       | "excludes"      | "excludes"   | 403        | school-staff as Agg View access student because the student is currently enrolled with no end-date |
| "staff5"  | "student02"    | 403       | "excludes"      | "excludes"   | 403        | school-staff can't access student because the staff was terminated |
| "staff18" | "student02"    | 403       | "excludes"      | "excludes"   | 403        | school-staff can't access student because the staff was not associated |

| "staff6"  | "student01"    | 200       | "excludes"      | "includes"   | 403        | district-staff as Educator access student because the student is currently enrolled |
| "staff7"  | "student01"    | 200       | "includes"      | "includes"   | 403        | district-staff as Leader   access student because the student is currently enrolled |
| "staff8"  | "student01"    | 200       | "includes"      | "includes"   | 204        | district-staff as IT Admin access student because the student is currently enrolled |
| "staff9"  | "student01"    | 403       | "excludes"      | "excludes"   | 403        | district-staff as Agg View access student because the student is currently enrolled |
| "staff10" | "student01"    | 403       | "excludes"      | "excludes"   | 403        | district-staff can't access student because the staff was terminated |
| "staff17" | "student01"    | 403       | "excludes"      | "excludes"   | 403        | district-staff can't access student because the staff was not associated |

| "staff6"  | "student02"    | 200       | "excludes"      | "includes"   | 403        | district-staff as Educator access student because the student is currently enrolled with no end-date |
| "staff7"  | "student02"    | 200       | "includes"      | "includes"   | 403        | district-staff as Leader   access student because the student is currently enrolled with no end-date |
| "staff8"  | "student02"    | 200       | "includes"      | "includes"   | 204        | district-staff as IT Admin access student because the student is currently enrolled with no end-date |
| "staff9"  | "student02"    | 403       | "excludes"      | "excludes"   | 403        | district-staff as Agg View access student because the student is currently enrolled with no end-date |
| "staff10" | "student02"    | 403       | "excludes"      | "excludes"   | 403        | district-staff can't access student because the staff was terminated |
| "staff17" | "student02"    | 403       | "excludes"      | "excludes"   | 403        | district-staff can't access student because the staff was not associated |

| "staff6"  | "student01"    | 200       | "excludes"      | "includes"   | 403        | district-staff as Educator access student because the student is currently enrolled |
| "staff7"  | "student01"    | 200       | "includes"      | "includes"   | 403        | district-staff as Leader   access student because the student is currently enrolled |
| "staff8"  | "student01"    | 200       | "includes"      | "includes"   | 204        | district-staff as IT Admin access student because the student is currently enrolled |
| "staff9"  | "student01"    | 403       | "excludes"      | "excludes"   | 403        | district-staff as Agg View access student because the student is currently enrolled |
| "staff10" | "student01"    | 403       | "excludes"      | "excludes"   | 403        | district-staff can't access student because the staff was terminated |
| "staff17" | "student01"    | 403       | "excludes"      | "excludes"   | 403        | district-staff can't access student because the staff was not associated |

| "staff6"  | "student02"    | 200       | "excludes"      | "includes"   | 403        | district-staff as Educator access student because the student is currently enrolled with no end-date |
| "staff7"  | "student02"    | 200       | "includes"      | "includes"   | 403        | district-staff as Leader   access student because the student is currently enrolled with no end-date |
| "staff8"  | "student02"    | 200       | "includes"      | "includes"   | 204        | district-staff as IT Admin access student because the student is currently enrolled with no end-date |
| "staff9"  | "student02"    | 403       | "excludes"      | "excludes"   | 403        | district-staff as Agg View access student because the student is currently enrolled with no end-date |
| "staff10" | "student02"    | 403       | "excludes"      | "excludes"   | 403        | district-staff can't access student because the staff was terminated |
| "staff17" | "student02"    | 403       | "excludes"      | "excludes"   | 403        | district-staff can't access student because the staff was not associated |

| "staff6"  | "student03"    | 403       | "excludes"      | "excludes"   | 403        | district-staff as Educator can't access student because the student is not currently enrolled |
| "staff7"  | "student03"    | 403       | "excludes"      | "excludes"   | 403        | district-staff as Leader   can't access student because the student is not currently enrolled |
| "staff8"  | "student03"    | 403       | "excludes"      | "excludes"   | 403        | district-staff as IT Admin can't access student because the student is not currently enrolled |
| "staff9"  | "student03"    | 403       | "excludes"      | "excludes"   | 403        | district-staff as Agg View can't access student because the student is not currently enrolled |
| "staff10" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | district-staff can't access student because the staff was terminated |
| "staff17" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | district-staff can't access student because the staff was not associated |

| "staff11" | "student01"    | 200       | "excludes"      | "includes"   | 403        | state-staff as Educator access student because the student is currently enrolled |
| "staff12" | "student01"    | 200       | "includes"      | "includes"   | 403        | state-staff as Leader   access student because the student is currently enrolled |
| "staff13" | "student01"    | 200       | "includes"      | "includes"   | 204        | state-staff as IT Admin access student because the student is currently enrolled |
| "staff14" | "student01"    | 403       | "excludes"      | "excludes"   | 403        | state-staff as Agg View access student because the student is currently enrolled |
| "staff15" | "student01"    | 403       | "excludes"      | "excludes"   | 403        | state-staff can't access student because the staff was terminated |
| "staff16" | "student01"    | 403       | "excludes"      | "excludes"   | 403        | state-staff can't access student because the staff was not associated |

| "staff11" | "student02"    | 200       | "excludes"      | "includes"   | 403        | state-staff as Educator access student because the student is currently enrolled with no end-date |
| "staff12" | "student02"    | 200       | "includes"      | "includes"   | 403        | state-staff as Leader   access student because the student is currently enrolled with no end-date |
| "staff13" | "student02"    | 200       | "includes"      | "includes"   | 204        | state-staff as IT Admin access student because the student is currently enrolled with no end-date |
| "staff14" | "student02"    | 403       | "excludes"      | "excludes"   | 403        | state-staff as Agg View access student because the student is currently enrolled with no end-date |
| "staff15" | "student02"    | 403       | "excludes"      | "excludes"   | 403        | state-staff can't access student because the staff was terminated |
| "staff16" | "student02"    | 403       | "excludes"      | "excludes"   | 403        | state-staff can't access student because the staff was not associated |

| "staff11" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | state-staff as Educator can't access student because the student is not currently enrolled |
| "staff12" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | state-staff as Leader   can't access student because the student is not currently enrolled |
| "staff13" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | state-staff as IT Admin can't access student because the student is not currently enrolled |
| "staff14" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | state-staff as Agg View can't access student because the student is not currently enrolled |
| "staff15" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | state-staff can't access student because the staff was terminated |
| "staff16" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | state-staff can't access student because the staff was not associated |

| "staff1"  | "student47"    | 403       | "excludes"      | "excludes"   | 403        | school-staff can't see student in another school within tenant |
| "staff1"  | "student51"    | 403       | "excludes"      | "excludes"   | 403        | school-staff can't see student in another school outside district |
| "staff1"  | "student54"    | 404       | "excludes"      | "excludes"   | 403        | school-staff can't see student in another school outside tenant |
| "staff6"  | "student47"    | 200       | "excludes"      | "includes"   | 403        | district-staff can see student in another school within district |
| "staff6"  | "student51"    | 403       | "excludes"      | "excludes"   | 403        | district-staff can't see student in another school outside district |
| "staff6"  | "student54"    | 404       | "excludes"      | "excludes"   | 403        | district-staff can't see student in another school outside tenant |
| "staff11" | "student47"    | 200       | "excludes"      | "includes"   | 403        | state-staff can see student in school within state |
| "staff11" | "student51"    | 200       | "excludes"      | "includes"   | 403        | state-staff can see student in another school in other district |
| "staff11" | "student54"    | 404       | "excludes"      | "excludes"   | 403        | state-staff can't see student in another school outside tenant |

| "staff11" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | state-staff as Educator can't access student because the student is not currently enrolled |
| "staff12" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | state-staff as Leader   can't access student because the student is not currently enrolled |
| "staff13" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | state-staff as IT Admin can't access student because the student is not currently enrolled |
| "staff14" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | state-staff as Agg View can't access student because the student is not currently enrolled |
| "staff15" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | state-staff can't access student because the staff was terminated |
| "staff16" | "student03"    | 403       | "excludes"      | "excludes"   | 403        | state-staff can't access student because the staff was not associated |

| "staff1"  | "student47"    | 403       | "excludes"      | "excludes"   | 403        | school-staff can't see student in another school within tenant |
| "staff1"  | "student51"    | 403       | "excludes"      | "excludes"   | 403        | school-staff can't see student in another school outside district |
| "staff1"  | "student54"    | 404       | "excludes"      | "excludes"   | 403        | school-staff can't see student in another school outside tenant |
| "staff6"  | "student47"    | 200       | "excludes"      | "includes"   | 403        | district-staff can see student in another school within district |
| "staff6"  | "student51"    | 403       | "excludes"      | "excludes"   | 403        | district-staff can't see student in another school outside district |
| "staff6"  | "student54"    | 404       | "excludes"      | "excludes"   | 403        | district-staff can't see student in another school outside tenant |
| "staff11" | "student47"    | 200       | "excludes"      | "includes"   | 403        | state-staff can see student in school within state |
| "staff11" | "student51"    | 200       | "excludes"      | "includes"   | 403        | state-staff can see student in another school in other district |
| "staff11" | "student54"    | 404       | "excludes"      | "excludes"   | 403        | state-staff can't see student in another school outside tenant |

@smoke
Scenario Outline: Staff accessing lists of students at differing levels
  Given I am user <User> in IDP "SEC"
  When I make an API call to get my student list
  Then I should see a count of <Count>
Examples:
| User      | Count | Comment |
| "staff1"  | 45    | School-staff should see all students currently enrolled at the school. |
| "staff6"  | 48    | District-staff should see all students currently enrolled at the schools in their district. |
| "staff11" | 55    | State-staff should see all students currently enrolled at the schools in their state. |

Scenario Outline: Seeing data about student only if you can see the student
Given I am user <User> in IDP "SEC"
When I make an API call to get my student list
Then I should see a count of <Count>
When I make an API call to get the student <Student I can see>
Then I should receive a return code of 200
And I should be able to access data about the student <Student I can see>
When I make an API call to get the student <Student I cannot see>
Then I should receive a return code of 403
And I should not be able to access data about the student <Student I cannot see>
Examples:
| User        | Count | Student I can see | Student I cannot see |
| "teacher10" | 2     | "student57"       | "student59"          |
#| "teacher11" | 0     | "NONE"            | "student58"          |  COMMENT: Need to figure out how to gracefully handle this
| "teacher12" | 2     | "student58"       | "student59"          |
| "staff20"   | 7     | "student61"       | "student62"          |
| "staff21"   | 3     | "student62"       | "student61"          |
| "staff22"   | 7     | "student60"       | "student62"          |

Scenario Outline: Seeing data about section only if you can see the section
Given I am user <User> in IDP "SEC"
When I make an API call to get my section list
Then I should see a count of <Count>
When I make an API call to get the section <Section I can see>
Then I should receive a return code of 200
And I should be able to access data about the section <Section I can see>
When I make an API call to get the section <Section I cannot see>
Then I should receive a return code of 403
And I should not be able to access data about the section <Section I cannot see>
Examples:
| User        | Count | Section I can see | Section I cannot see |
| "teacher10" | 1     | "section4"        | "section5"           |
| "teacher11" | 1     | "section5"        | "section4"           |
| "teacher12" | 1     | "section4"        | "section5"           |
#| "staff20"   | 7     | "student61"       | "student62"          |
#| "staff21"   | 3     | "student62"       | "student61"          |
#| "staff22"   | 7     | "student60"       | "student62"          |

Scenario: Update data associations to change access for teachers and staff
Given I am user "staff13" in IDP "SEC"
When I move teacher12 to a new section
And I move student58 to a new section
And I move staff22 to a new school
And I move student61 to a new school
Then the stamper runs and completes

Scenario Outline: Teachers and Staff seeing new student data from changed associations
Given I am user <User> in IDP "SEC"
When I make an API call to get my student list
Then I should see a count of <Count>
When I make an API call to get the student <Student I can see>
Then I should receive a return code of 200
And I should be able to access data about the student <Student I can see>
When I make an API call to get the student <Student I cannot see>
Then I should receive a return code of 403
And I should not be able to access data about the student <Student I cannot see>
Examples:
| User        | Count | Student I can see | Student I cannot see |
| "teacher10" | 1     | "student57"       | "student58"          |
| "teacher11" | 1     | "student58"       | "student59"          |
| "teacher12" | 1     | "student58"       | "student57"          |
| "staff20"   | 6     | "student60"       | "student61"          |
| "staff21"   | 4     | "student61"       | "student60"          |
| "staff22"   | 4     | "student62"       | "student60"          |

Scenario Outline: Teachers and Staff seeing new section data from changed associations
Given I am user <User> in IDP "SEC"
When I make an API call to get my section list
Then I should see a count of <Count>
When I make an API call to get the section <Section I can see>
Then I should receive a return code of 200
And I should be able to access data about the section <Section I can see>
When I make an API call to get the section <Section I cannot see>
Then I should receive a return code of 403
And I should not be able to access data about the section <Section I cannot see>
Examples:
| User        | Count | Section I can see | Section I cannot see | Notes |
| "teacher10" | 1     | "section4"        | "section5"           | none |
| "teacher11" | 2     | "section5"        | "section6"           | Sec 5 now has student that used to be in Sec4 |
| "teacher12" | 2     | "section5"        | "section6"           | Sec 5 now has student that used to be in Sec4 |
#| "staff20"   | 7     | "student61"       | "student62"          |
#| "staff21"   | 3     | "student62"       | "student61"          |
#| "staff22"   | 7     | "student60"       | "student62"          |
