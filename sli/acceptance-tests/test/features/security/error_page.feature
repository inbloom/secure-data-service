 @RALLY_US5156
Feature: I want to verify that 403 error pages are handled correctly 

Scenario: Make bad saml assertion with application/json content type 
  Given I am using "application/json" as my content type
  And the sli securityEvent collection is empty
  And I make a post request to sso/post
  Then I get back a json-formatted 403 error page
   And "1" security event matching "^SAML message received" should be in the sli db
   And I should see a count of "2" in the security event collection
   And I check to find if record is in sli db collection:
    | collectionName  | expectedRecordCount | searchParameter         | searchValue                                               | searchType |
    | securityEvent   | 1                   | body.appId              | UNKNOWN                                                   | string     |
    | securityEvent   | 2                   | body.className          | org.slc.sli.api.resources.security.SamlFederationResource | string     |
    | securityEvent   | 1                   | body.logMessage         | Access Denied:Authorization could not be verified.        | string     |
   And "2" security event with field "body.actionUri" matching "http.*/api/rest/saml/sso/post" should be in the sli db

Scenario: Make bad saml assertion with text/html content type 
  Given I am using "text/html" as my content type
   And the sli securityEvent collection is empty
   And I make a post request to sso/post
  Then I get back a html-formatted 403 error page
   And a security event matching "^SAML message received" should be in the sli db
   And I should see a count of "2" in the security event collection
   And I check to find if record is in sli db collection:
    | collectionName  | expectedRecordCount | searchParameter         | searchValue                                               | searchType |
    | securityEvent   | 1                   | body.appId              | UNKNOWN                                                   | string     |
    | securityEvent   | 2                   | body.className          | org.slc.sli.api.resources.security.SamlFederationResource | string     |
    | securityEvent   | 1                   | body.logMessage         | Access Denied:Authorization could not be verified.        | string     |
   And "2" security event with field "body.actionUri" matching "http.*/api/rest/saml/sso/post" should be in the sli db
