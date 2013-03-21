Feature: Search Indexer reindexes accordingly when we update/delete/create Assessment,
  Assessment Period Descriptor, and AssessmentFamily

Scenario: Search Indexer should reindex when I update the Assessment Period Descriptor

  Given I am a valid teacher "rrogers" with password "rrogers1234"
  And the testing device app key has been created
  And I have an open web browser
  And I import the odin-local-setup application and realm data

  When I navigate to the API authorization endpoint with my client ID
  And I was redirected to the "Simple" IDP Login page
  And I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  Then I should receive a json response containing my authorization code

  When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
  Then I should receive a json response containing my authorization token
  And I should be able to use the token to make valid API calls

  Given format "application/json"
  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I should receive a collection with 2 elements
  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Hello%20World"
  Then I should receive a return code of 200
  Then I should receive a collection with 0 elements
  Then I update the "assessmentPeriodDescriptor" with ID "7bcb5c6cb1b13bf11406676f4397d6dec4659561_id" field "body.description" to "Hello World"
  When I send an update event to the search indexer for collection "assessmentPeriodDescriptor" and ID "7bcb5c6cb1b13bf11406676f4397d6dec4659561_id"
  Then I will EVENTUALLY GET "/v1/assessments?assessmentPeriodDescriptor.description=Hello%20World" with 2 elements
  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Hello%20World"
  Then I should receive a return code of 200
  Then I should receive a collection with 2 elements
  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I should receive a collection with 0 elements
  Then I update the "assessmentPeriodDescriptor" with ID "7bcb5c6cb1b13bf11406676f4397d6dec4659561_id" field "body.description" to "Beginning of Year 2013-2014 for First grade"
  When I send an update event to the search indexer for collection "assessmentPeriodDescriptor" and ID "7bcb5c6cb1b13bf11406676f4397d6dec4659561_id"
  Then I will EVENTUALLY GET "/v1/assessments?assessmentPeriodDescriptor.description=Hello%20World" with 0 elements
  Then I will EVENTUALLY GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade" with 2 elements
  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Hello%20World"
  Then I should receive a return code of 200
  Then I should receive a collection with 0 elements
  When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20First%20grade"
  Then I should receive a return code of 200
  Then I should receive a collection with 2 elements