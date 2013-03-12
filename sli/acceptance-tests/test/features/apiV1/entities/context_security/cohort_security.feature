@RALLY_US1804
@security
@RALLY_US209
Feature: Security for Cohort CRUD
  As a product owner, I want to validate that my cohort entity is properly secured up to current SLI standards

  Scenario: Authorized user tries to hit the cohort URL directly
    Given I am user "sbantu" in IDP "IL"
    When I make an API call to get the cohort "ACC-TEST-COH-2"
    Then I receive a JSON response

  Scenario Outline: Unauthorized authenticated user tries to hit the cohort URL directly
    Given I am user <User> in IDP "IL"
    When I make an API call to get the cohort "ACC-TEST-COH-2"
    Then I get a message that I am not authorized
	Examples:
	|User    |
	|"llogan"|
	|"cgray" |

Scenario Outline: Teacher views students through valid Cohort Association

  Given I am user "manthony" in IDP "IL"
  And the sli securityEvent collection is empty
  When I make an API call to get the student <Student>
  Then I should receive a return code of <Code>
    And I check to find if record is in sli db collection:
       | collectionName      | expectedRecordCount  | searchParameter       | searchValue  |
       | securityEvent       | <expectedRecordCount>| body.userEdOrg        | <userEdOrg>  |
       | securityEvent       | <expectedRecordCount>| body.targetEdOrgList  | <targetEdOrg>|
       | securityEvent       | <expectedRecordCount>| body.logMessage       | <logMessage> |

  Examples:
  | Student        | Code | Comment                                                     | userEdOrg     | targetEdOrg                | expectedRecordCount  | logMessage                                                                        |
  |"Todd Angulo"   |  200 | #current cohort, current student                            | *             | *                          | 0                    | *                                                                                 |
  |"Agnes Trinh"   |  403 | #old cohort, current student                                | IL-SUNSET     | UNKNOWN                    | 1                    | Access Denied:Access to 22bf5f8f-5e6b-4749-9e1a-2efda072d506_id is not authorized |
  |"Stella Rego"   |  403 | #current cohort, current student, student record flag false | IL-SUNSET     | IL-SUNSET                  | 1                    | Access Denied:Cannot access entities                                              |
  |"Glenda Koch"   |  403 | #Current cohort, old student                                | IL-SUNSET     | IL-SUNSET                  | 1                    | Access Denied:Cannot access entities                                              |
  |"Johnny Tallent"|  403 | #current cohort, old student                                | IL-SUNSET     | IL-SUNSET                  | 1                    | Access Denied:Cannot access entities                                              |
  |"Thelma Frasier"|  200 | #current cohort, current student                            | *             | *                          | 0                    | *                                                                                 |
