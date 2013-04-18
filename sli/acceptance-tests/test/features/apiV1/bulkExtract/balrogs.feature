@US5481
@US5519
Feature: Bulk Extraction Works

        @fakeTar
        Scenario: Authorized long-lived session token can use bulk extract
        Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
        And I set up a fake tar file on the file system and in Mongo
        And in my list of rights I have BULK_EXTRACT
        When I make API call to retrieve sampled bulk extract file
        When the return code is 200
        Then I check the http response headers
        When I make bulk extract API call
        When the return code is 200 I get expected tar downloaded
#        Then I check the http response headers
        Then the response is decrypted 
        And I see that the response matches what I put in the fake tar file
        
    @fakeTar
    Scenario: Authorized long-lived session token can head bulk extract
        Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
        And in my list of rights I have BULK_EXTRACT
        And I set up a fake tar file on the file system and in Mongo
        When I make API call to retrieve sampled bulk extract file headers
        When the return code is 200
        When I make bulk extract API head call
        When the return code is 200

    Scenario: Un-Authorized user cannot use the endpoint
        Given I am logged in using "linda.kim" "balrogs" to realm "IL"
        When I make bulk extract API call
        Then I should receive a return code of 403   
