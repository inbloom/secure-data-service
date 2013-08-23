@RALLY_US5221
Feature: Generate API hybrid edOrg data using Odin data generator

Given I am using the odin working directory

Scenario: Generate an API data set with hybrid edOrgs for API testing using Odin generate tool
  When I generate the "hybrid_edorgs" data set in the "generated" directory
  When I convert schools to charter schools in "InterchangeEducationOrganization.xml"
#  And I copy generated data to the new OdinSampleDataSet directory
#  And I zip generated data under filename OdinSampleDataSet.zip to the new OdinSampleDataSet directory
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
  When I zip generated data under filename "OdinSampleDataSet.zip"
