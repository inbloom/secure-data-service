@wip
Feature: Display Higest score for assessment contents

As a SEA/LEA user, I want to be able to select different views in my dashboard
application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "test" mode
   
Scenario: Calculating Highest ReportingResultType for any a defined assessment 
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
  Then I should only see one view named "IL_9-12"
  And in the configuration file "rule" is "Highest"
  And in the configuration file "ReportingTypes" are "scaleScore"  and "percentile"
  
  When I find the "Assessment" where the "assessmentTitle" is "SAT" and the "assessmentFamilyHierarchy" is like "SAT*"
    Then for each student I find the "highest" "studentAssessmentAssociation.scoreResults.result"
    And I see "studentAssessmentAssociation.scoreResults.result" where "studentAssessmentAssociation.scoreResult.assessmentReportingResultType" is "scaleScore" 
    And I see "studentAssessmentAssociation.scoreResults.result" where "studentAssessmentAssociation.scoreResult.assessmentReportingResultType" is "percentile" 