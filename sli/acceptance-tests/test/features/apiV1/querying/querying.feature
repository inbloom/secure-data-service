@RALLY_US210
Feature: Querying the API to receive subsets of results
 
Scenario Outline: Confirm ability to use all API query operators with different data type
  Given I am logged in using "jpratt" "jpratt1234" to realm "NY"
    And format "application/json"
   When parameter "limit" is "0"
    And parameter <field name> <operator> <operand>
    And I query <resource name> to demonstrate <test type>
   Then I should receive a return code of 200
    And I should receive a collection with <entities returned> elements
  Examples:
    | resource name    | field name              | operator | operand            | entities returned | test type |
    | "sections"       | "sequenceOfCourse"      | "<="     | "5"                | 12                | "integer" |
    | "sections"       | "sequenceOfCourse"      | ">"      | "5"                | 4                 | "integer" |
    | "sections"       | "sequenceOfCourse"      | "<"      | "5"                | 10                | "integer" |
    | "sections"       | "sequenceOfCourse"      | ">="     | "5"                | 6                 | "integer" |
    | "sections"       | "sequenceOfCourse"      | "!="     | "5"                | 14                | "integer" |
    | "sections"       | "sequenceOfCourse"      | "="      | "5"                | 2                 | "integer" |
    | "sections"       | "uniqueSectionCode"     | "<="     | "Chem305-Sec1"     | 5                 | "string"  |
    | "sections"       | "uniqueSectionCode"     | ">"      | "Chem305-Sec1"     | 11                | "string"  |
    | "sections"       | "uniqueSectionCode"     | "<"      | "Chem305-Sec1"     | 4                 | "string"  |
    | "sections"       | "uniqueSectionCode"     | ">="     | "Chem305-Sec1"     | 12                | "string"  |
    | "sections"       | "uniqueSectionCode"     | "!="     | "Chem305-Sec1"     | 15                | "string"  |
    | "sections"       | "uniqueSectionCode"     | "="      | "Chem305-Sec1"     | 1                 | "string"  |
    | "gradingPeriods" | "beginDate"             | "<="     | "2012-01-01"       | 2                 | "date"    |
    | "gradingPeriods" | "beginDate"             | ">"      | "2012-01-01"       | 0                 | "date"    |
    | "gradingPeriods" | "beginDate"             | "<"      | "2012-01-01"       | 1                 | "date"    |
    | "gradingPeriods" | "beginDate"             | ">="     | "2012-01-01"       | 1                 | "date"    |
    | "gradingPeriods" | "beginDate"             | "!="     | "2012-01-01"       | 1                 | "date"    |
    | "gradingPeriods" | "beginDate"             | "="      | "2012-01-01"       | 1                 | "date"    |
    | "students"       | "economicDisadvantaged" | "<="     | "true"             | 8                 | "boolean" |
    | "students"       | "economicDisadvantaged" | ">"      | "true"             | 0                 | "boolean" |
    | "students"       | "economicDisadvantaged" | "<"      | "true"             | 7                 | "boolean" |
    | "students"       | "economicDisadvantaged" | ">="     | "true"             | 1                 | "boolean" |
    | "students"       | "economicDisadvantaged" | "!="     | "true"             | 7                 | "boolean" |
    | "students"       | "economicDisadvantaged" | "="      | "true"             | 1                 | "boolean" |
    | "reportCards"    | "gpaCumulative"         | "<="     | "3.65"             | 1                 | "double"  |
    | "reportCards"    | "gpaCumulative"         | ">"      | "3.65"             | 1                 | "double"  |
    | "reportCards"    | "gpaCumulative"         | "<"      | "3.65"             | 0                 | "double"  |
    | "reportCards"    | "gpaCumulative"         | ">="     | "3.65"             | 2                 | "double"  |
    | "reportCards"    | "gpaCumulative"         | "!="     | "3.65"             | 1                 | "double"  |
    | "reportCards"    | "gpaCumulative"         | "="      | "3.65"             | 1                 | "double"  |
