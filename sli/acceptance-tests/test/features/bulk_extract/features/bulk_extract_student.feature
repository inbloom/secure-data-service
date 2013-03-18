Scenario: Verify a student was correctly extracted
   When I retrieve the path to the extract file for the tenant "Midgar"
   And a "student" extract file exists
   And a the correct number of "student" was extracted from the database
   And a "student" was extracted with all the correct fields
   And a "student" was extracted in the same format as the api