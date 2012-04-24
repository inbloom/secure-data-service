Feature: As an SLI application, I want to be able to support XML.
  This means the API should support POST, PUT and GET with XML format.

Background: Nothing yet
  Given I am logged in using "demo" "demo1234" to realm "SLI"
  And format "application/xml"

Scenario Outline: Getting response from GET - Read
  When I navigate to GET "/v1/<ENTITY URI>/<ENTITY ID>"
  Then I should receive a return code of 200
  And I should receive an XML document
  And I should see "entityType" is <ENTITY TYPE>

Examples:
  | ENTITY URI                  | ENTITY TYPE                | ENTITY ID                               |
  | assessments                 | assessment                 | 6c572483-fe75-421c-9588-d82f1f5f3af5    |
  | schools                     | school                     | eb3b8c35-f582-df23-e406-6947249a19f2    |
  | students                    | student                    | 714c1304-8a04-4e23-b043-4ad80eb60992    |
  | studentSectionAssociations  | studentSectionAssociation  | 4efb4b14-bc49-f388-0000-0000c9355702    |
  | courseOfferings   			| sessionCourseAssociation   | 9ff65bb1-ef8b-4588-83af-d58f39c1bf68    |

Scenario Outline: Getting response from GET - Read all
  Given parameter "limit" is "0"
  When I navigate to GET "/v1/<ENTITY URI>"
  Then I should receive a return code of 200
  And I should receive an XML document
  And I should receive <ENTITY COUNT> entities
  And I should see each entity's "entityType" is <ENTITY TYPE>

Examples:
  | ENTITY URI                  | ENTITY TYPE                | ENTITY COUNT    |
  | assessments                 | assessment                 | 16              |
  | schools                     | school                     | 32              |
  | students                    | student                    | 223             |
  | studentSectionAssociations  | studentSectionAssociation  | 309             |
  | courseOfferings   			| sessionCourseAssociation   | 4               |
