@wip
Feature: Display performance levels for assessment contents 

As a SEA/LEA user, I want to be able to select different views in my dashboard
application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "test" mode
  
Scenario: Calculating Highest ReportingResultType for any section of a defined assessment 
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
  Then I should only see one view named "IL_9-12"
  And the configuration file "rule" is "Highest"
  And the configuration file "AssessmentSections" is "SAT-Writing"
  And in the configuration file "ReportingTypes" are "scaleScore"  and "percentile"
  
  When I find the "Assessment" where the "assessmentTitle" is "SAT" and the "assessmentFamilyHierarchy" is like "SAT*"
    Then for each studentAssessmentAssociation I find "studentObjectiveAssessment.objectiveAssessment.identificationCode = "SAT-Writing"
    When for each student I find the "highest" "studentObjectiveAssessment.scoreResults.result"
    Then I see the "studentObjectiveAssessment.scoreResults.result"  where studentObjectiveAssessment.scoreResults.assessmentReportingResultType" is "scaleScore"	
	And I see the "studentObjectiveAssessment.scoreResults.result"  where studentObjectiveAssessment.scoreResults.assessmentReportingResultType" is "percentile"

Scenario: Display Performance Levels for an Assessment 
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
  Then I should only see one view named "IL_K-3"
  And in the configuration file "assessmentFamilyHierarchy" is like "DIBELS"
  And in the configuration file "ReportingTypes" are "performance"
  
  When I find the "Assessment" where the "assessmentTitle" is "Grade K-3 MOY DIBELS" and the "assessmentFamilyHierarchy" is like "DIBELS*"
  When for each student I find the "studentAssessmentAssociation"
   Then I see the "performanceLevelDescriptor.codeValue"
   And I see the "performanceLevelDescriptor.description"