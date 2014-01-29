Feature: We can run tests
  As an inBloom developer
  In order to make other tests
  I have to generate this Odin data

  Scenario: Generate the api security testing data set using the Odin generate tool
    Given I generate the "api_security_testing" data set
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