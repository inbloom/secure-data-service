Feature: A bulk extract is triggered for the student entity

Scenario: Verify a student was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "student" extract file exists
   And a the correct number of "student" was extracted from the database
   And a "student" was extracted with all the correct fields
   And I used the long lived session token generator script to create a token for user "jstevenson" with role "IT Administrator" in tenant "Midgar"  for realm "IL-Daybreak" that will expire in "300" seconds with client_id "K2e7Dwhq5J"
   Then a "student" was extracted in the same format as the api