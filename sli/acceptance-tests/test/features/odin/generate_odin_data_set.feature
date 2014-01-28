Feature: Generate sample data sets based on scenarios using Odin data generator
  As an inBloom developer
  In order to test various data-driven scenarios
  I want to be able to generate sample data sets of ingestable XML files

Scenario: Generate a data set using the Odin generate tool
  Given I generate a typical data set
  Then I should see the generated files:
    |ControlFile.ctl|
    |InterchangeAssessmentMetadata.xml|
    |InterchangeAttendance.xml|
    |InterchangeEducationOrgCalendar.xml|
    |InterchangeEducationOrganization.xml|
    |InterchangeMasterSchedule.xml|
    |InterchangeStaffAssociation.xml|
    |InterchangeStudentAssessment.xml|
    |InterchangeStudentCohort.xml|
    |InterchangeStudentDiscipline.xml|
    |InterchangeStudentEnrollment.xml|
    |InterchangeStudentGrades.xml|
    |InterchangeStudentParent.xml|
    |InterchangeStudentProgram.xml|
    |OdinSampleDataSet.zip|
    |manifest.json|