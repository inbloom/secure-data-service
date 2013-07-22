Feature: As an SLI application, I want to ensure security events are created for unauthorized access.


#Access  Denied Security Events
  Scenario Outline:
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/vnd.slc+json"
    And the sli securityEvent collection is empty
    #ContextValidator.java:240
    When I navigate to GET "/v1.3/disciplineIncidents/0e26de79-222a-5e67-9201-5113ad50a03b/studentDisciplineIncidentAssociations"
    Then I should receive a return code of 403
    And I should see a count of "1" in the security event collection
    And a security event matching "Cannot access endpoint." should be in the sli db
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                              | searchType |
      | securityEvent   | 1                   | body.userEdOrg          | IL-SUNSET                                | string     |
      | securityEvent   | 1                   | body.targetEdOrgList    | East Daybreak Junior High                | string     |
    And the sli securityEvent collection is empty
   #ContextValidator.java:302
    When I navigate to GET "/v1.3/teachers/04f708bc-928b-420d-a440-f1592a5d1073/disciplineIncidents"
    And I should see a count of "1" in the security event collection
    And a security event matching "Cannot access entities" should be in the sli db
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                              | searchType |
      | securityEvent   | 1                   | body.userEdOrg          | IL-SUNSET                                | string     |
      | securityEvent   | 1                   | body.targetEdOrgList    | Sunset Central High School               | string     |