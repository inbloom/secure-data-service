Feature: Retrived through the api a generated delta bulk extract file, and validate the file

Scenario: Retrieve a generated bulk extract delta for today
   Given I have delta bulk extract files generated for today

      And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
      And in my list of rights I have BULK_EXTRACT
      When I make API call to retrieve today's delta file
      Then I should receive a return code of 200
      Then the return code is 200 I get expected tar downloaded

      When I save the extracted file
      And I verify this tar file is the same as the pre-generated delta file

      When I make API call to retrieve tomorrow's non existing delta files
      Then I should receive a return code of 404
