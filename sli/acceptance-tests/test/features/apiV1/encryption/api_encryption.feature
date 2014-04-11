Feature: Test database encryption.
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a student.

Background: Logged in as a super-user and using the small data set
  Given I have a session as a tenant-level IT Administrator
    And I want to use format "application/json"

  Scenario: Student data created via the API should be encrypted - Staff IT Admin
   Given no record exists in "student" with a "body.studentUniqueStateId" of "530425896"
        And a valid entity json document for a "student"
    When I navigate to POST "/<STUDENT URI>"
    Then I should receive a return code of 201
        And I should receive an ID for the newly created entity
    When I have a valid school association for the student
        And I navigate to POST "/<STUDENT SCHOOL ASSOCIATION URI>"
        And I navigate to GET "/<STUDENT URI>/<NEWLY CREATED ENTITY ID>"
    When I find a mongo record in "student" with "studentUniqueStateId" equal to "530425896"
    Then the following fields should not be encrypted:
      | hispanicLatinoEthnicity                   |
      | oldEthnicity                              |
      | race[0]                                   |
      | economicDisadvantaged                     |
      | schoolFoodServicesEligibility             |
      | studentCharacteristics[0].characteristic  |
      | studentCharacteristics[0].beginDate       |
      | limitedEnglishProficiency                 |
      | languages[0].language                     |
      | homeLanguages[0].language                 |
      | disabilities[0].disability                |
      | section504Disabilities[0]                 |
      | displacementStatus                        |
      | learningStyles.visualLearning             |
      | learningStyles.auditoryLearning           |
      | learningStyles.tactileLearning            |
      | cohortYears[0].schoolYear                 |
      | cohortYears[0].cohortYearType             |
      | studentIndicators[0].indicatorName        |
      | studentIndicators[0].indicator            |
    But the following fields should be encrypted:
      | name.firstName                            |
      | name.middleName                           |
      | name.lastSurname                          |
      | otherName[0].firstName                    |
      | otherName[0].middleName                   |
      | otherName[0].lastSurname                  |
      | otherName[0].otherNameType                |
      | sex                                       |
      | birthData.birthDate                       |
      | address[0].streetNumberName               |
      | address[0].city                           |
      | address[0].postalCode                     |
      | address[0].stateAbbreviation              |
      | telephone[0].telephoneNumber              |
      | electronicMail[0].emailAddress            |
      | loginId                                   |

Scenario: Sorting on PII across a hop should fail
    Given format "application/json"
        And parameter "sortBy" is "name.firstName"
        And parameter "sortOrder" is "descending"
    When I navigate to GET "/<SCHOOL URI>/<South Daybreak Elementary ID>/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT URI>"
    Then I should receive a return code of 400
    When I navigate to GET "/<SCHOOL URI>/<South Daybreak Elementary ID>/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT URI>"
    Then I should receive a return code of 400
    Given parameter "sortBy" is "studentUniqueStateId"
    When I navigate to GET "/<SCHOOL URI>/<South Daybreak Elementary ID>/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT URI>"
    Then I should receive a return code of 200

Scenario: Can query PII fields by exact matching
    Given format "application/json"
        And student Rhonda Delagio exists
	And Rhonda Delagio is associated with "<English Sec 6>"
        And parameter "name.firstName" is "Rhonda"
        And parameter "limit" is "0"
    When I navigate to GET "/<SECTION URI>/<English Sec 6>/<STUDENT SECTION ASSOCIATION URI>/<STUDENT URI>"
    Then I should receive a return code of 200
        And all students should have "name.firstName" equal to "Rhonda"
    Given parameter "name.firstName" is not "Rhonda"
    When I navigate to GET "/<SECTION URI>/<English Sec 6>/<STUDENT SECTION ASSOCIATION URI>/<STUDENT URI>"
    Then I should receive a return code of 200
        And no student should have "name.firstName" equal to "Rhonda"

Scenario: Can not query PII fields by non-exact matching
    Given format "application/json"
        And student Rhonda Delagio exists
	And Rhonda Delagio is associated with "<English Sec 7>"
        And parameter "name.firstName" less than "Rhonda"
    When I navigate to GET "/<SCHOOL URI>/<South Daybreak Elementary ID>/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT URI>"
    Then I should receive a return code of 400
    Given parameter "name.firstName" greater than "Rhonda"
    When I navigate to GET "/<SCHOOL URI>/<South Daybreak Elementary ID>/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT URI>"
    Then I should receive a return code of 400
    Given parameter "name.firstName" greater than or equal to "Rhonda"
    When I navigate to GET "/<SCHOOL URI>/<South Daybreak Elementary ID>/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT URI>"
    Then I should receive a return code of 400
    Given parameter "name.firstName" less than or equal to "Rhonda"
    When I navigate to GET "/<SCHOOL URI>/<South Daybreak Elementary ID>/<STUDENT SCHOOL ASSOCIATION URI>/<STUDENT URI>"
    Then I should receive a return code of 400
