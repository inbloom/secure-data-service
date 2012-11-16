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
    | "jpratt"               | "jpratt1234"               | "sections"                           | "c9929e15-f907-4473-a948-6f9aa302647d" | "uniqueSectionCode"     | "=~"     | "Chem305"          | 2                 | "string"  |
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
    | "jpratt"               | "jpratt1234"               | "reportCards"                        | "46c2e439-f800-4aaf-901c-8cf3299658cc,9d970849-0116-499d-b8f3-2255aeb69552" | "gpaCumulative"         | "<="     | "3.65"             | 1                 | "double"  |
    | "jpratt"               | "jpratt1234"               | "reportCards"                        | "46c2e439-f800-4aaf-901c-8cf3299658cc,9d970849-0116-499d-b8f3-2255aeb69552" | "gpaCumulative"         | ">"      | "3.65"             | 1                 | "double"  |
    | "jpratt"               | "jpratt1234"               | "reportCards"                        | "46c2e439-f800-4aaf-901c-8cf3299658cc,9d970849-0116-499d-b8f3-2255aeb69552" | "gpaCumulative"         | "<"      | "3.65"             | 0                 | "double"  |
    | "jpratt"               | "jpratt1234"               | "reportCards"                        | "46c2e439-f800-4aaf-901c-8cf3299658cc,9d970849-0116-499d-b8f3-2255aeb69552" | "gpaCumulative"         | ">="     | "3.65"             | 2                 | "double"  |
    | "jpratt"               | "jpratt1234"               | "reportCards"                        | "46c2e439-f800-4aaf-901c-8cf3299658cc,9d970849-0116-499d-b8f3-2255aeb69552" | "gpaCumulative"         | "!="     | "3.65"             | 1                 | "double"  |
    | "jpratt"               | "jpratt1234"               | "reportCards"                        | "46c2e439-f800-4aaf-901c-8cf3299658cc,9d970849-0116-499d-b8f3-2255aeb69552" | "gpaCumulative"         | "="      | "3.65"             | 1                 | "double"  |
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
    | "johndoe"              | "johndoe1234"              | "sections"                           | "eb3b8c35-f582-df23-e406-6947249a19f2" | "uniqueSectionCode"     | "=~"     | "Trig"             | 1                 | "string"  |


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
    | "jstevenson"   | "jstevenson1234" | "/v1/sections/1d345e41-f1c7-41b2-9cc4-9898c82faeda_id/studentSectionAssociations" | studentId | descending | 10     | 10    |
    | "jstevenson"   | "jstevenson1234" | "/v1/sections/1d345e41-f1c7-41b2-9cc4-9898c82faeda_id/studentSectionAssociations" | studentId | ascending  | 0      | 20    |
    | "linda.kim"    | "linda.kim1234"  | "/v1/sections/ceffbb26-1327-4313-9cfc-1c3afd38122e_id/studentSectionAssociations" | studentId | descending | 10     | 10    |
    | "linda.kim"    | "linda.kim1234"  | "/v1/sections/ceffbb26-1327-4313-9cfc-1c3afd38122e_id/studentSectionAssociations" | studentId | ascending  | 0      | 20    |


  Scenario Outline: Include fields
    Given I am logged in using <username> <password> to realm "IL"
    And format "application/json;charset=utf-8"
    And parameter "includeFields" is "stateOrganizationId"
    When I navigate to GET "/v1/schools/6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
    Then in the response body I should see the following fields only:
      | id                  |
      | links               |
      | entityType          |
      | stateOrganizationId | 
    Examples:
      | username       | password         |
      | "jstevenson"   | "jstevenson1234" |
      | "linda.kim"    | "linda.kim1234"  |


  Scenario Outline: Exclude fields
    Given I am logged in using <username> <password> to realm "IL"
    And format "application/json;charset=utf-8"
    And parameter "excludeFields" is "stateOrganizationId"
    When I navigate to GET "/v1/schools/6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
    Then in the response body I should not see field "stateOrganizationId"
    Examples:
      | username       | password         |
      | "jstevenson"   | "jstevenson1234" |
      | "linda.kim"    | "linda.kim1234"  |


  Scenario Outline: Include & Exclude fields combination
    Given I am logged in using <username> <password> to realm "IL"
    And format "application/json;charset=utf-8"
    And parameter "excludeFields" is "links"
    And parameter "includeFields" is "stateOrganizationId"
    When I navigate to GET "/v1/schools/6756e2b9-aba1-4336-80b8-4a5dde3c63fe"
    Then in the response body I should see the following fields only:
      | id                  |
      | entityType          |
      | stateOrganizationId | 
    Examples:
      | username       | password         |
      | "jstevenson"   | "jstevenson1234" |
      | "linda.kim"    | "linda.kim1234"  |
      
    @RALLY_DE2088
  Scenario Outline: Learning Objective tests
    Given I am logged in using "<username>" "<password>" to realm "IL"
    And format "application/json;charset=utf-8"
    When I navigate to GET "<resource name>"
    Then I should receive a return code of 200
    And "entityType" should be "<Entity Type>"
    And "id" should be "<VALID VALUE>"
  Examples:
    | username   | password       |  resource name                                                                       | Entity Type       | VALID VALUE                          |
    | cgrayadmin | cgrayadmin1234 | /v1/learningObjectives/dd9165f2-65be-6d27-a8ac-bdc5f46757b6/childLearningObjectives  | learningObjective | dd9165f2-65fe-6d27-a8ec-bdc5f47757b7 |
    | cgrayadmin | cgrayadmin1234 | /v1/learningObjectives/dd9165f2-65fe-6d27-a8ec-bdc5f47757b7/parentLearningObjectives | learningObjective | dd9165f2-65be-6d27-a8ac-bdc5f46757b6 |

      
Scenario Outline: Confirm that API blocks regex against PII data:
  Given I am logged in using <username> <password> to realm "IL"
  And format "application/json;charset=utf-8"
  When I navigate to GET "/v1/students?name.firstName=~Billy"
  Then I should receive a return code of 400
  Examples:
      | username       | password         |
      | "jstevenson"   | "jstevenson1234" |
      | "linda.kim"    | "linda.kim1234"  |
    
Scenario Outline: Confirm that API blocks regex against no-context endpoints:
  Given I am logged in using <username> <password> to realm "IL"
  And format "application/json;charset=utf-8"
  When parameter "foo" is "bar"
  And I navigate to GET <endpoint>
  Then I should receive a return code of 400
  Examples:
      | username       | password         | endpoint                             |
      | "jstevenson"   | "jstevenson1234" | "/v1/assessments"                    |
      | "jstevenson"   | "jstevenson1234" | "/v1/competencyLevelDescriptor"      |
      | "jstevenson"   | "jstevenson1234" | "/v1/competencyLevelDescriptorTypes" |
      | "jstevenson"   | "jstevenson1234" | "/v1/learningObjectives"             |
      | "jstevenson"   | "jstevenson1234" | "/v1/learningStandards"              | 
      | "linda.kim"    | "linda.kim1234"  | "/v1/assessments"                    |
      | "linda.kim"    | "linda.kim1234"  | "/v1/competencyLevelDescriptor"      |
      | "linda.kim"    | "linda.kim1234"  | "/v1/competencyLevelDescriptorTypes" |
      | "linda.kim"    | "linda.kim1234"  | "/v1/learningObjectives"             |
      | "linda.kim"    | "linda.kim1234"  | "/v1/learningStandards"              |
    

Scenario Outline: Confirm that API inserts context against some endpoints:
  Given I am logged in using <username> <password> to realm "IL"
  And format "application/json;charset=utf-8"
  When parameter "foo" is "bar"
  And I navigate to GET <endpoint>
  Then I should receive a return code of 200
  And the executed path should not equal the requested <endpoint>
  Examples:
      | username       | password         | endpoint                             |
      | "jstevenson"   | "jstevenson1234" | "/v1/studentCompetencyObjectives"    |
      | "linda.kim"    | "linda.kim1234"  | "/v1/studentCompetencyObjectives"    |
    

@wip
Scenario Outline: Confirm that entities that block queries dont block 2+ part URIs from querying
  Given I am logged in using <username> <password> to realm "IL"
  And format "application/json;charset=utf-8"
  When parameter "foo" is "bar"
  And I navigate to GET <endpoint>
  Then I should receive a return code of 404
  Examples:
      | username       | password         | endpoint                             |
      | "jstevenson"   | "jstevenson1234" | "/v1/assessments/29f044bd-1449-4fb7-8e9a-5e2cf9ad252a"                      |
      | "jstevenson"   | "jstevenson1234" | "/v1/competencyLevelDescriptor/3a7b0473fd3fdeb24254c08d1250087a2144c642_id"      |
      | "jstevenson"   | "jstevenson1234" | "/v1/competencyLevelDescriptorTypes" |
      | "jstevenson"   | "jstevenson1234" | "/v1/learningObjectives"             |
      | "jstevenson"   | "jstevenson1234" | "/v1/learningStandards"              | 
      | "linda.kim"    | "linda.kim1234"  | "/v1/assessments/29f044bd-1449-4fb7-8e9a-5e2cf9ad252a"                    |
      | "linda.kim"    | "linda.kim1234"  | "/v1/competencyLevelDescriptor/3a7b0473fd3fdeb24254c08d1250087a2144c642_id"      |
      | "linda.kim"    | "linda.kim1234"  | "/v1/competencyLevelDescriptorTypes" |
      | "linda.kim"    | "linda.kim1234"  | "/v1/learningObjectives"             |
      | "linda.kim"    | "linda.kim1234"  | "/v1/learningStandards"              |
    
