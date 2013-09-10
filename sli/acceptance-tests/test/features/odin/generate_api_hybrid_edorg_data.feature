@RALLY_US5221
Feature: Generate API hybrid edOrg data using Odin data generator

Given I am using the odin working directory
@rc
Scenario: Generate an API data set with hybrid edOrgs for API testing using Odin generate tool
  When I generate the "hybrid_edorgs" data set in the "generated" directory
  When I convert school "IL-CHARTER-SCHOOL" to a charter school in "InterchangeEducationOrganization.xml" with additional parent refs
  |  ParentReference |
  | STANDARD-SEA     |

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
