@US5481
@US5519
Feature: Bulk Extraction Works
    
      @dk
        Scenario: Authorized long-lived session token can use bulk extract
        Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
        And in my list of rights I have BULK_EXTRACT
        When I make bulk extract API call
        When the return code is 404 I ensure there is no bulkExtractFiles entry for Midgar
        When the return code is 503 I ensure there is a bulkExtractFiles entry for Midgar
        When the return code is 200 I get expected tar downloaded
#        Then I check the http response headers
        Then the response is decrypted 
       
    Scenario: Un-Authorized user cannot use the endpoint
        Given I am logged in using "linda.kim" "balrogs" to realm "IL"
        When I make bulk extract API call
        Then I should receive a return code of 403   
