@US5826
Feature: As an SLI application, I want to ensure security events are created for unauthorized access

  Background: None

  Scenario : Confirm that Access Denied Exceptions are handled and SecurityEvents logged
    When I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And format "application/vnd.slc+json"
    And the sli securityEvent collection is empty
    And I navigate to GET "/v1.3/disciplineIncidents/0e26de79-222a-5e67-9201-5113ad50a03b/studentDisciplineIncidentAssociations"
    Then I should receive a return code of 403
    And I should see a count of "1" in the security event collection
    And a security event matching "Cannot access endpoint." should be in the sli db
    And I check to find if record is in sli db collection:
       | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                      | searchType |
       | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                                                      | string     |
       | securityEvent   | 1                   | body.targetEdOrgList    | East Daybreak Junior High                                        | string     |
       | securityEvent   | 1                   | body.tenantId           | Midgar                                                           | string     |
       | securityEvent   | 1                   | body.appId              | ke9Dgpo3uI                                                       | string     |
       | securityEvent   | 1                   | body.credential         | 4cf7a5d4-37a1-ca19-8b13-b5f95131ac85                             | string     |
       | securityEvent   | 1                   | body.logLevel           | TYPE_INFO                                                        | string     |
       | securityEvent   | 1                   | body.className          | org.slc.sli.api.security.context.ContextValidator                | string     |
       | securityEvent   | 1                   | body.user               | linda.kim, Linda Kim                                             | string     |
       | securityEvent   | 1                   | body.logMessage         | Access Denied:Cannot access endpoint.                            | string     |


    When the sli securityEvent collection is empty
    And I navigate to GET "/v1.3/teachers/04f708bc-928b-420d-a440-f1592a5d1073/disciplineIncidents"
    Then I should receive a return code of 403
    Then I should see a count of "1" in the security event collection
    And a security event matching "Cannot access entities" should be in the sli db
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                                       | searchType |
      | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                                       | string     |
      | securityEvent   | 1                   | body.targetEdOrgList    | Sunset Central High School                        | string     |
      | securityEvent   | 1                   | body.tenantId           | Midgar                                            | string     |
      | securityEvent   | 1                   | body.appId              | ke9Dgpo3uI                                        | string     |
      | securityEvent   | 1                   | body.credential         | 4cf7a5d4-37a1-ca19-8b13-b5f95131ac85              | string     |
      | securityEvent   | 1                   | body.logLevel           | TYPE_INFO                                         | string     |
      | securityEvent   | 1                   | body.className          | org.slc.sli.api.security.context.ContextValidator | string     |
      | securityEvent   | 1                   | body.user               | linda.kim, Linda Kim                              | string     |
      | securityEvent   | 1                   | body.logMessage         | Access Denied:Cannot access entities              | string     |


    When I am logged in using "carmen.ortiz" "carmen.ortiz1234" to realm "IL-Daybreak-Students"
    And the sli securityEvent collection is empty
    And I navigate to GET "/v1.3/disciplineActions"
    Then I should receive a return code of 403
    And I should see a count of "1" in the security event collection
    And a security event matching "Students do not have access to discipline actions." should be in the sli db
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                      | searchType |
      | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                                                      | string     |
      | securityEvent   | 1                   | body.targetEdOrgList    | IL-DAYBREAK                                                      | string     |
      | securityEvent   | 1                   | body.tenantId           | Midgar                                                           | string     |
      | securityEvent   | 1                   | body.appId              | vavedRa9uB                                                       | string     |
      | securityEvent   | 1                   | body.credential         | 1AC2D1F8-D6B4-4174-884B-214A48E0007B                             | string     |
      | securityEvent   | 1                   | body.logLevel           | TYPE_INFO                                                        | string     |
      | securityEvent   | 1                   | body.className          | org.slc.sli.api.security.pdp.UriMutator                          | string     |
      | securityEvent   | 1                   | body.user               | 900000016, Carmen Ortiz                                          | string     |
      | securityEvent   | 1                   | body.logMessage         | Access Denied:Students do not have access to discipline actions. | string     |


    When the sli securityEvent collection is empty
    And I navigate to GET "/v1.3/disciplineIncidents"
    Then I should receive a return code of 403
    And I should see a count of "1" in the security event collection
    And a security event matching "Students do not have access to discipline incidents." should be in the sli db
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter               | searchValue                                                        | searchType |
      | securityEvent   | 1                   | body.userEdOrg                | IL-DAYBREAK                                                        | string     |
      | securityEvent   | 1                   | body.targetEdOrgList          | IL-DAYBREAK                                                        | string     |
      | securityEvent   | 1                   | body.tenantId                 | Midgar                                                             | string     |
      | securityEvent   | 1                   | body.appId                    | vavedRa9uB                                                         | string     |
      | securityEvent   | 1                   | body.credential               | 1AC2D1F8-D6B4-4174-884B-214A48E0007B                               | string     |
      | securityEvent   | 1                   | body.logLevel                 | TYPE_INFO                                                          | string     |
      | securityEvent   | 1                   | body.className                | org.slc.sli.api.security.pdp.UriMutator                            | string     |
      | securityEvent   | 1                   | body.user                     | 900000016, Carmen Ortiz                                            | string     |
      | securityEvent   | 1                   | body.logMessage               | Access Denied:Students do not have access to discipline incidents. | string     |


    When the sli securityEvent collection is empty
    And I navigate to GET "/v1.3/studentDisciplineIncidentAssociations"
    Then I should receive a return code of 403
    And I should see a count of "1" in the security event collection
    And a security event matching "Students do not have access to discipline incident associations." should be in the sli db
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter               | searchValue                                                                    | searchType |
      | securityEvent   | 1                   | body.userEdOrg                | IL-DAYBREAK                                                                    | string     |
      | securityEvent   | 1                   | body.targetEdOrgList          | IL-DAYBREAK                                                                    | string     |
      | securityEvent   | 1                   | body.tenantId                 | Midgar                                                                         | string     |
      | securityEvent   | 1                   | body.appId                    | vavedRa9uB                                                                     | string     |
      | securityEvent   | 1                   | body.credential               | 1AC2D1F8-D6B4-4174-884B-214A48E0007B                                           | string     |
      | securityEvent   | 1                   | body.logLevel                 | TYPE_INFO                                                                      | string     |
      | securityEvent   | 1                   | body.className                | org.slc.sli.api.security.pdp.UriMutator                                        | string     |
      | securityEvent   | 1                   | body.user                     | 900000016, Carmen Ortiz                                                        | string     |
      | securityEvent   | 1                   | body.logMessage               | Access Denied:Students do not have access to discipline incident associations. | string     |


    When I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    When the sli securityEvent collection is empty
    And I navigate to GET "/v1.3/students/6a98d5d3-d508-4b9c-aec2-59fce7e16825_id/attendances"
    Then I should receive a return code of 403
    And I should see a count of "1" in the security event collection
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter               | searchValue                                         | searchType |
      | securityEvent   | 1                   | body.userEdOrg                | IL-DAYBREAK                                         | string     |
      | securityEvent   | 1                   | body.targetEdOrgList          | 152901002                                           | string     |
      | securityEvent   | 1                   | body.tenantId                 | Midgar                                              | string     |
      | securityEvent   | 1                   | body.appId                    | ke9Dgpo3uI                                          | string     |
      | securityEvent   | 1                   | body.credential               | 4cf7a5d4-37a1-ca19-8b13-b5f95131ac85                | string     |
      | securityEvent   | 1                   | body.logLevel                 | TYPE_INFO                                           | string     |
      | securityEvent   | 1                   | body.className                | org.slc.sli.api.security.roles.RightAccessValidator | string     |
      | securityEvent   | 1                   | body.user                     | linda.kim, Linda Kim                                | string     |
      | securityEvent   | 1                   | body.logMessage               | Access Denied:Insufficient Privileges               | string     |
