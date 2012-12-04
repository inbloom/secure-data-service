@RALLY_2875
Feature: Generate sample data using Odin data generator

Given I am using the odin working directory

Scenario: Generate a small 10 student data set using Odin generate tool
    When I generate the 10 student data set in the generated directory
      And I zip generated data under filename OdinSampleDataSet.zip to the new OdinSampleDataSet directory
      And I copy generated data to the new OdinSampleDataSet directory
      
    Then I should see 8 xml interchange files
      And I should see InterchangeEducationOrgCalendar.xml has been generated
      And I should see InterchangeEducationOrganization.xml has been generated
      And I should see InterchangeMasterSchedule.xml has been generated
      And I should see InterchangeStudentEnrollment.xml has been generated
      And I should see InterchangeStudentParent.xml has been generated
      And I should see ControlFile.ctl has been generated
      And I should see OdinSampleDataSet.zip has been generated
      

