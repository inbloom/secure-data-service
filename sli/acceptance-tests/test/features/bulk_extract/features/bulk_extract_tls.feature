@US5603
Feature: Client Certificate Validation for Bulk Extract
I want to validate that the X509 Certificate passed to the API Bulk Extract call is the same one we have on record,
before giving all the data in SLI to a vendor

Scenario: Validate only associated Cert for a given app is allowed
  Given I trigger a bulk extract
  And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
  When I make a call to the bulk extract end point "/bulk/extract/public" using the certificate for app "pavedz00ua"
  Then I get back a response code of "403"
  When I make a call to the bulk extract end point "/bulk/extract/public" using the certificate for app "vavedra9ub"
  Then I get back a response code of "200"
