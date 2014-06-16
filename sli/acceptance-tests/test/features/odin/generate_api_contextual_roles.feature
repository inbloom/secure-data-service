Feature: Generate API Contextual Roles sample data using Odin data generator

  Scenario: Generate contextual roles data set using Odin generate tool
    When I generate the "contextual_roles" data set
    And I convert school "Daybreak Apocalypse" to a charter school in "InterchangeEducationOrganization.xml" with additional parent refs
      | ParentReference |
      | IL-DAYBREAK     |
      | District 31     |
    And I convert school "Daybreak Ragnarok" to a charter school in "InterchangeEducationOrganization.xml" with additional parent refs
      | ParentReference |
      | District 9      |
      | District 31     |
    And I convert edorg "Daybreak Center" to an Education Service Center in "InterchangeEducationOrganization.xml"
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
