@RALLY_US5594

Feature: As an API user, I want to be able to make bulk extract requests to with different versions.

Scenario: Validate backwards compatibility 
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And I set up a fake tar file on the file system and in Mongo
    And in my list of rights I have BULK_EXTRACT
    When I make API call to retrieve sampled bulk extract file headers with version ""
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers
    When I make API call to bulk extract file headers with version ""
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers


Scenario: Valicate request with only major version
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And I set up a fake tar file on the file system and in Mongo
    And in my list of rights I have BULK_EXTRACT
    When I make API call to retrieve sampled bulk extract file headers with version "v1"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers
    When I make API call to bulk extract file headers with version "v1"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers

Scenario: Validate requests with minor versions
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And I set up a fake tar file on the file system and in Mongo
    And in my list of rights I have BULK_EXTRACT
    When I make API call to bulk extract file headers with version "v1.1"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers
    When I make API call to bulk extract file headers with version "v1.2"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers
    When I make API call to retrieve sampled bulk extract file headers with version "v1.1"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers
    When I make API call to retrieve sampled bulk extract file headers with version "v1.2"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers

Scenario: Validate requests with invalid versions
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And I set up a fake tar file on the file system and in Mongo
    And in my list of rights I have BULK_EXTRACT
    When I make API call to bulk extract file headers with version "v1.3"
    Then I get back a response code of "404"
    When I make API call to bulk extract file headers with version "v2.0"
    Then I get back a response code of "404"
    When I make API call to bulk extract file headers with version "1.3"
    Then I get back a response code of "404"
    When I make API call to bulk extract file headers with version "abc"
    Then I get back a response code of "404"
    When I make API call to retrieve sampled bulk extract file headers with version "v1.3"
    Then I get back a response code of "404"
    When I make API call to retrieve sampled bulk extract file headers with version "v2.0"
    Then I get back a response code of "404"
    When I make API call to retrieve sampled bulk extract file headers with version "1.3"
    Then I get back a response code of "404"
    When I make API call to retrieve sampled bulk extract file headers with version "abc"
    Then I get back a response code of "404"
