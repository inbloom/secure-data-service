Feature: Test that default self rights give users access to entities that are included in self

Scenario: Aggregate Viewer is able to read their staffEdOrgAssociation
Given  I am valid SEA/LEA end user "jvasquez" with password "jvasquez1234" 
And I am authenticated on "IL"
And the role attribute equals "Aggregate Viewer"
And I make a call to get the staff ed org association for "jvasquez"
And I should receive a return code of 200
And I make a call to get the staff ed org association for "agillespie"
And I should receive a return code of 403