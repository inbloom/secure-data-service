
Feature: After Small Sample DS is ingested and BE is run, I want to ensure that extracts are created for all edOrgs(Not only LEA/SEA).

  Background: Validate that BE are created to all edOrgs
	Given I am using local data store

  Scenario: Validate that BEs are created for all edOrgs
    When I ingest "AppendEdOrg.zip"
    Then I trigger a bulk extract

    And I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract

    And I check that the student extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records
    And I check that the attendance extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records
    And I check that the "course" extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records
    And I check that the "courseOffering" extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records
    And I check that the gradingPeriod extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records
    And I check that the student extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records
    And I check that the attendance extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records
    And I check that the parent extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records
    And I check that the section extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records
    And I check that the session extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records
    And I check that the staffEdorgAssignment extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records
    And I check that the teacherSchoolAssociation extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records

    And I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "a13489364c2eb015c219172d561c62350f0453f3_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And I check that the "disciplineIncident" extract for "a13489364c2eb015c219172d561c62350f0453f3_id" has the correct number of records
    And I check that the teacherSchoolAssociation extract for "a13489364c2eb015c219172d561c62350f0453f3_id" has the correct number of records
    And I check that the studentGradebookEntry extract for "a13489364c2eb015c219172d561c62350f0453f3_id" has the correct number of records
    #And I check that the studentSchoolAssociation extract for "a13489364c2eb015c219172d561c62350f0453f3_id" has the correct number of records


    And I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "352e8570bd1116d11a72755b987902440045d346_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And I check that the disciplineAction extract for "352e8570bd1116d11a72755b987902440045d346_id" has the correct number of records
    And I check that the staffCohortAssociation extract for "352e8570bd1116d11a72755b987902440045d346_id" has the correct number of records
    And I check that the teacherSchoolAssociation extract for "352e8570bd1116d11a72755b987902440045d346_id" has the correct number of records
    And I check that the studentGradebookEntry extract for "352e8570bd1116d11a72755b987902440045d346_id" has the correct number of records
    And I check that the staffProgramAssociation extract for "352e8570bd1116d11a72755b987902440045d346_id" has the correct number of records
    And I check that the calendarDate extract for "352e8570bd1116d11a72755b987902440045d346_id" has the correct number of records
    And I check that the "cohort" extract for "352e8570bd1116d11a72755b987902440045d346_id" has the correct number of records
    And I check that the "educationOrganization" extract for "352e8570bd1116d11a72755b987902440045d346_id" has "1" records

    #And I check that the staffCohortAssociation extract for "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id" has the correct number of records





