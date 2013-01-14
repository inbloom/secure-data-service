@RALLY_5096
Feature: Generate sample data using Odin data generator

Given I am using the odin working directory

Scenario: Generate jmeter api performance data set using Odin generate tool
    When I generate the jmeter api performance data set in the generated directory
      And I zip generated data under filename OdinSampleDataSet.zip to the new OdinSampleDataSet directory
      And I copy generated data to the new OdinSampleDataSet directory
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
