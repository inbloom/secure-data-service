@wip
Feature: Display simple assessment contents

As a SEA/LEA user, I want to be able to select different views in my dashboard
application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "test" mode

Scenario: Displaying simple StateTest reading results for all students
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
	And I select <viewSelector> "IL_3-8_ELA"
	And the configuration file "assessmentFamily" is like "StateTest Reading for Grades 3-8"
	
    Then for each student I search for "Assessment" where "assessmentTitle" is "Grade 3 2010 StateTest Writing"
    And I see "studentAssessment.scoreResults.result" where "studentAssessment.scoreResult.assessmentReportingResultType" is "scaleScore" 
    And I see "studentAssessment.scoreResults.result" where "studentAssessment.scoreResult.assessmentReportingResultType" is "percentile"
    And I see "studentAssessment.scoreResults.result" where "studentAssessment.scoreResult.assessmentReportingResultType" is "other"  

Scenario: Displaying most recent StateTest writing results for all students
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
	And I select <viewSelector> "IL_3-8_ELA"
	And in the configuration file "assessmentFamilyHierarchy" is "StateTest Writing for Grades 3-8"
	And in the configuration file "rule" is "Most Recent"
    
    When I find the "Assessment" where the "assessmentTitle" is "Grade 3 2010 StateTest Writing" and the "assessmentFamilyHierarchy" is like "StateTest Writing for Grades 3-8*"
    Then for each student I find the "most recent" "studentAssessment"
    And I see "studentAssessment.scoreResults.result" where "studentAssessment.scoreResult.assessmentReportingResultType" is "scaleScore" 


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
    Then for each student I find the "highest" "studentAssessment.scoreResults.result"
    And I see "studentAssessment.scoreResults.result" where "studentAssessment.scoreResult.assessmentReportingResultType" is "scaleScore" 
    And I see "studentAssessment.scoreResults.result" where "studentAssessment.scoreResult.assessmentReportingResultType" is "percentile" 
 
# only implement in 3.4
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
    Then for each studentAssessment I find "studentObjectiveAssessment.objectiveAssessment.identificationCode = "SAT-Writing"
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
  And in the configuration file "assessmentFamilyHierarchy" is like "READ2"
  And in the configuration file "ReportingTypes" are "performance"
  
  When I find the "Assessment" where the "assessmentTitle" is "Grade K-3 MOY READ2" and the "assessmentFamilyHierarchy" is like "READ2*"
  When for each student I find the "studentAssessment"
   Then I see the "performanceLevelDescriptor.codeValue"
   And I see the "performanceLevelDescriptor.description"
   
 Scenario: Calculating most recent window for any a defined assessment
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
  Then I should only see one view named "IL_K-3"
  And in the configuration file "assessmentFamilyHierarchy" is like "READ2"
  And in the configuration file "ReportingTypes" are "performance level"
  
   When I find "most recent" "Assessment" where  the "assessmentFamilyHierarchy" is like "READ2*"
   Then I find "Assessment" where "assessmentTitle" is "Grade K-3 MOY READ2" 
   When for each student I find the "studentAssessment"
   Then I see the "performanceLevelDescriptor.codeValue"
   And I see the "performanceLevelDescriptor.description"