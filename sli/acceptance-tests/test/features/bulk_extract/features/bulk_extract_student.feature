Feature: A bulk extract is triggered for the student entity

@smoke
Scenario: Verify a student was correctly extracted
   When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
   And a "student" extract file exists
   And a the correct number of "student" was extracted from the database
   And a "student" was extracted with all the correct fields
   And I log into "inBloom Dashboards" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then a "student" was extracted in the same format as the api