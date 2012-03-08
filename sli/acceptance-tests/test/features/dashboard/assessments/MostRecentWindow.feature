Feature: Display Most recent window score for assessment contents

As a SEA/LEA user, I want to be able to select different views in my dashboard
application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "test" mode


Scenario: Calculating most recent window for any a defined assessment
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
  Then I should only see one view named "IL_K-3"
  And in the configuration file "assessmentFamilyHierarchy" is like "DIBELS"
  And in the configuration file "ReportingTypes" are "performance level"
  
   When I find "most recent" "Assessment" where  the "assessmentFamilyHierarchy" is like "DIBELS*"
   Then I find "Assessment" where "assessmentTitle" is "Grade K-3 MOY DIBELS" 
   When for each student I find the "studentAssessmentAssociation"
   Then I see the "performanceLevelDescriptor.codeValue"
   And I see the "performanceLevelDescriptor.description"