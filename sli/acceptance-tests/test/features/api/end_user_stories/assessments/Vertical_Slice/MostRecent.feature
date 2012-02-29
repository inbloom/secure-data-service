@wip
Feature: Display simple assessment contents

As a SEA/LEA user, I want to be able to select different views in my dashboard
application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "test" mode

Scenario: Displaying simple ISAT reading results for all students
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
	And I select <viewSelector> "IL_3-8_ELA"
	And the configuration file "assessmentFamily" is like "ISAT Reading for Grades 3-8"
	
    Then for each student I search for "Assessment" where "assessmentTitle" is "Grade 3 2010 ISAT Writing"
    And I see "studentAssessmentAssociation.scoreResults.result" where "studentAssessmentAssociation.scoreResult.assessmentReportingResultType" is "scaleScore" 
    And I see "studentAssessmentAssociation.scoreResults.result" where "studentAssessmentAssociation.scoreResult.assessmentReportingResultType" is "percentile"
    And I see "studentAssessmentAssociation.scoreResults.result" where "studentAssessmentAssociation.scoreResult.assessmentReportingResultType" is "other"  

Scenario: Displaying most recent ISAT writing results for all students
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
	And I select <viewSelector> "IL_3-8_ELA"
	And in the configuration file "assessmentFamilyHierarchy" is "ISAT Writing for Grades 3-8"
	And in the configuration file "rule" is "Most Recent"
    
    When I find the "Assessment" where the "assessmentTitle" is "Grade 3 2010 ISAT Writing" and the "assessmentFamilyHierarchy" is like "ISAT Writing for Grades 3-8*"
    Then for each student I find the "most recent" "studentAssessmentAssociation"
    And I see "studentAssessmentAssociation.scoreResults.result" where "studentAssessmentAssociation.scoreResult.assessmentReportingResultType" is "scaleScore" 