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
       | collectionName  | expectedRecordCount | searchParameter         | searchValue                              | searchType |
       | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                              | string     |
       | securityEvent   | 1                   | body.targetEdOrgList    | East Daybreak Junior High                | string     |


    When the sli securityEvent collection is empty
    And I navigate to GET "/v1.3/teachers/04f708bc-928b-420d-a440-f1592a5d1073/disciplineIncidents"
    Then I should receive a return code of 403
    Then I should see a count of "1" in the security event collection
    And a security event matching "Cannot access entities" should be in the sli db
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                              | searchType |
      | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                              | string     |
      | securityEvent   | 1                   | body.targetEdOrgList    | Sunset Central High School               | string     |


    When I am logged in using "carmen.ortiz" "carmen.ortiz1234" to realm "IL-Daybreak-Students"
    And the sli securityEvent collection is empty
    And I navigate to GET "/v1.3/disciplineActions"
    Then I should receive a return code of 403
    And I should see a count of "1" in the security event collection
    And a security event matching "Students do not have access to discipline actions." should be in the sli db
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                              | searchType |
      | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                              | string     |
      | securityEvent   | 1                   | body.targetEdOrgList    | IL-DAYBREAK                              | string     |


    When the sli securityEvent collection is empty
    And I navigate to GET "/v1.3/disciplineIncidents"
    Then I should receive a return code of 403
    And I should see a count of "1" in the security event collection
    And a security event matching "Students do not have access to discipline incidents." should be in the sli db
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                              | searchType |
      | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                              | string     |
      | securityEvent   | 1                   | body.targetEdOrgList    | IL-DAYBREAK                              | string     |


    When the sli securityEvent collection is empty
    And I navigate to GET "/v1.3/studentDisciplineIncidentAssociations"
    Then I should receive a return code of 403
    And I should see a count of "1" in the security event collection
    And a security event matching "Students do not have access to discipline incident associations." should be in the sli db
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                              | searchType |
      | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                              | string     |
      | securityEvent   | 1                   | body.targetEdOrgList    | IL-DAYBREAK                              | string     |


    When I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    When the sli securityEvent collection is empty
    And I navigate to GET "/v1.3/students/6a98d5d3-d508-4b9c-aec2-59fce7e16825_id/attendances"
    Then I should receive a return code of 403
    And I should see a count of "1" in the security event collection
    And a security event matching "Access Denied:Access to 6a98d5d3-d508-4b9c-aec2-59fce7e16825_id is not authorized" should be in the sli db
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                              | searchType |
      | securityEvent   | 1                   | body.userEdOrg          | IL-DAYBREAK                              | string     |
      | securityEvent   | 1                   | body.targetEdOrgList    | 152901002                                | string     |
