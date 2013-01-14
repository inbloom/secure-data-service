@RALLY_US5074
Feature: Generate 10k1 student sample data using Odin data generator

Given I am using the odin working directory

Scenario: Generate a 10k1 student data set using Odin generate tool
  When I generate the 10001 student data set in the generated directory
  And I zip generated data under filename Odin10k1SampleDataSet.zip to the new Odin10k1SampleDataSet directory
  And I copy generated data to the new Odin10k1SampleDataSet directory
  Then I should see generated file <File>
| File  |
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
