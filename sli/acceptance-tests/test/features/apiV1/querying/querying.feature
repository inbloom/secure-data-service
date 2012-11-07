@RALLY_US210
Feature: Querying the API to receive subsets of results
 
Scenario Outline: Confirm ability to use all API query operators with different data type
  Given I am logged in using <username> <password> to realm "NY"
    And format "application/json;charset=utf-8"
   When parameter "limit" is "0"
    And parameter <field name> <operator> <operand>
    And I query <resource name> of <school id> to demonstrate <test type>
   Then I should receive a return code of 200
    And I should receive a collection with <entities returned> elements
  Examples:
    | username               | password                   | resource name                        | school id                              | field name              | operator | operand            | entities returned | test type |
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "sequenceOfCourse"      | "<="     | "5"                | 4                 | "integer" |
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "sequenceOfCourse"      | ">"      | "1"                | 2                 | "integer" |
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "sequenceOfCourse"      | "<"      | "5"                | 2                 | "integer" |
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "sequenceOfCourse"      | ">="     | "1"                | 4                 | "integer" |
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "sequenceOfCourse"      | "!="     | "5"                | 2                 | "integer" |
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "sequenceOfCourse"      | "="      | "5"                | 2                 | "integer" |
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "uniqueSectionCode"     | "<="     | "Chem305-Sec2"     | 2                 | "string"  |
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "uniqueSectionCode"     | ">"      | "Chem305-Sec2"     | 2                 | "string"  |
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "uniqueSectionCode"     | "<"      | "Chem305-Sec2"     | 1                 | "string"  |
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "uniqueSectionCode"     | ">="     | "Chem305-Sec2"     | 3                 | "string"  |
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "uniqueSectionCode"     | "!="     | "Chem305-Sec2"     | 3                 | "string"  |
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "uniqueSectionCode"     | "="      | "Chem305-Sec2"     | 1                 | "string"  |
    | "jpratt"               | "jpratt1234"               | "gradingPeriods"                     | ""                                     | "beginDate"             | "<="     | "2012-01-01"       | 2                 | "date"    |
    | "jpratt"               | "jpratt1234"               | "gradingPeriods"                     | ""                                     | "beginDate"             | ">"      | "2012-01-01"       | 0                 | "date"    |
    | "jpratt"               | "jpratt1234"               | "gradingPeriods"                     | ""                                     | "beginDate"             | ">="     | "2012-01-01"       | 1                 | "date"    |
    | "jpratt"               | "jpratt1234"               | "gradingPeriods"                     | ""                                     | "beginDate"             | "!="     | "2012-01-01"       | 1                 | "date"    |
    | "jpratt"               | "jpratt1234"               | "gradingPeriods"                     | ""                                     | "beginDate"             | "="      | "2011-08-01"       | 1                 | "date"    |
    | "jpratt"               | "jpratt1234"               | "studentSchoolAssociations/students" | "46c2e439-f800-4aaf-901c-8cf3299658cc" | "economicDisadvantaged" | "<="     | "true"             | 4                 | "boolean" |
    | "jpratt"               | "jpratt1234"               | "studentSchoolAssociations/students" | "46c2e439-f800-4aaf-901c-8cf3299658cc" | "economicDisadvantaged" | ">"      | "true"             | 0                 | "boolean" |
    | "jpratt"               | "jpratt1234"               | "studentSchoolAssociations/students" | "46c2e439-f800-4aaf-901c-8cf3299658cc" | "economicDisadvantaged" | "<"      | "true"             | 4                 | "boolean" |
    | "jpratt"               | "jpratt1234"               | "studentSchoolAssociations/students" | "46c2e439-f800-4aaf-901c-8cf3299658cc" | "economicDisadvantaged" | ">="     | "true"             | 0                 | "boolean" |
    | "jpratt"               | "jpratt1234"               | "studentSchoolAssociations/students" | "46c2e439-f800-4aaf-901c-8cf3299658cc" | "economicDisadvantaged" | "!="     | "true"             | 4                 | "boolean" |
    | "jpratt"               | "jpratt1234"               | "studentSchoolAssociations/students" | "46c2e439-f800-4aaf-901c-8cf3299658cc" | "economicDisadvantaged" | "="      | "true"             | 0                 | "boolean" |
    | "jpratt"               | "jpratt1234"               | "reportCards"                        | ""                                     | "gpaCumulative"         | "<="     | "3.65"             | 1                 | "double"  |
    | "jpratt"               | "jpratt1234"               | "reportCards"                        | ""                                     | "gpaCumulative"         | ">"      | "3.65"             | 1                 | "double"  |
    | "jpratt"               | "jpratt1234"               | "reportCards"                        | ""                                     | "gpaCumulative"         | "<"      | "3.65"             | 0                 | "double"  |
    | "jpratt"               | "jpratt1234"               | "reportCards"                        | ""                                     | "gpaCumulative"         | ">="     | "3.65"             | 2                 | "double"  |
    | "jpratt"               | "jpratt1234"               | "reportCards"                        | ""                                     | "gpaCumulative"         | "!="     | "3.65"             | 1                 | "double"  |
    | "jpratt"               | "jpratt1234"               | "reportCards"                        | ""                                     | "gpaCumulative"         | "="      | "3.65"             | 1                 | "double"  |
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "sequenceOfCourse"      | "<="     | "5"                | 2                 | "integer" |
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "sequenceOfCourse"      | ">"      | "0"                | 2                 | "integer" |
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "sequenceOfCourse"      | "<"      | "5"                | 2                 | "integer" |
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "sequenceOfCourse"      | ">="     | "1"                | 2                 | "integer" |
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "sequenceOfCourse"      | "!="     | "5"                | 2                 | "integer" |
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "sequenceOfCourse"      | "="      | "5"                | 0                 | "integer" |
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "uniqueSectionCode"     | "<="     | "PDMS-Geometry"    | 1                 | "string"  |
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "uniqueSectionCode"     | ">"      | "PDMS-Geometry"    | 1                 | "string"  |
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "uniqueSectionCode"     | "<"      | "PDMS-Geometry"    | 0                 | "string"  |
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "uniqueSectionCode"     | ">="     | "PDMS-Geometry"    | 2                 | "string"  |
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "uniqueSectionCode"     | "!="     | "PDMS-Geometry"    | 1                 | "string"  |
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "uniqueSectionCode"     | "="      | "PDMS-Geometry"    | 1                 | "string"  |

Scenario Outline: Test that include fields only affect body fields (type remains)
  Given I am logged in using <username> <password> to realm "IL"
    And format "application/json"
   When I navigate to GET "/v1/students/0fb8e0b4-8f84-48a4-b3f0-9ba7b0513dba?includeFields=name,sex"
   Then the "name" should be "Lashawn" "Lawrence" "Aldama"
    And the "sex" should be "Male"
    And the "entityType" should be "student"
    Examples:
      | username           | password              |
      | "rrogers"          | "rrogers1234"         |
      | "linda.kim"        | "linda.kim1234"       |

@subdoc
Scenario Outline: Query subdoc
  Given I am logged in using <username> <password> to realm "IL"
  And format "application/json;charset=utf-8"
  And parameter "sortBy" is "<sort by>"
  And parameter "sortOrder" is "<sort order>"
  And I navigate to GET <resource name>
  And I should see a sorted list sorted by "<sort by>" in "<sort order>" order
  And parameter "offset" is "<offset>"
  And parameter "limit" is "<limit>"
  And I navigate to GET <resource name>
  And I should see a sorted list with "<offset>" offset and "<limit>" limit sorted by "<sort by>"
  Examples:
    | username       | password         | resource name                                                                  | sort by   | sort order | offset | limit |
    | "jstevenson"   | "jstevenson1234" | "/v1/sections/1d345e41-f1c7-41b2-9cc4-9898c82faeda/studentSectionAssociations" | studentId | descending | 10     | 10    |
    | "jstevenson"   | "jstevenson1234" | "/v1/sections/1d345e41-f1c7-41b2-9cc4-9898c82faeda/studentSectionAssociations" | studentId | ascending  | 0      | 20    |
    | "linda.kim"    | "linda.kim1234"  | "/v1/sections/ceffbb26-1327-4313-9cfc-1c3afd38122e/studentSectionAssociations" | studentId | descending | 10     | 10    |
    | "linda.kim"    | "linda.kim1234"  | "/v1/sections/ceffbb26-1327-4313-9cfc-1c3afd38122e/studentSectionAssociations" | studentId | ascending  | 0      | 20    |
