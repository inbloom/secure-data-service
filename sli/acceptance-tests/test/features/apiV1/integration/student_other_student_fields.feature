@student_students @wip
Feature: Students accessing fellow students
  As a student I want to access students in my current sections, programs, cohorts to lookup their names

Scenario: Accessing Students via Multi-part URIs
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "cegray" with password "cegray1234"
  And format "application/json"
  And I am using api version "v1"
  When I navigate to GET "/v1/sections/067198fd6da91e1aa8d67e28e850f224d6851713_id/studentSectionAssociations/students"
  Then I should be able to see <Fields> for the entity with ID <ID>:
    |ID|Fields|
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id | NameOnly |
    | 067198fd6da91e1aa8d67e28e850f224d6851713_id | All      |

Scenario: Accessing other students directly
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "cegray" with password "cegray1234"
  And format "application/json"
  And I am using api version "v1"
  # Get student in current Section
  When I verify the following response body fields in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id":
    | field            | value                                       |
    | id               | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | name.firstName   | Matt                                        |
    | name.middleName  | Aida                                        |
    | name.lastSurname | Sollars                                     |
  Then I verify the following response body fields do not exist in the response:
    | field                         |
    | studentUniqueStateId          |
    | studentIdentificationCode     |
    | otherName                     |
    | sex                           |
    | birthData                     |
    | address                       |
    | telephone                     |
    | electronicMail                |
    | profileThumbnail              |
    | hispanicLatinoEthnicity       |
    | oldEthnicity                  |
    | race                          |
    | economicDisadvantaged         |
    | schoolFoodServicesEligibility |
    | studentCharacteristics        |
    | limitedEnglishProficiency     |
    | languages                     |
    | homeLanguages                 |
    | disabilities                  |
    | section504Disabilities        |
    | displacementStatus            |
    | programParticipations         |
    | learningStyles                |
    | cohortYears                   |
    | studentIndicators             |
    | loginId                       |
    | gradeLevel                    |
    | schoolId                      |
# Get student in current Cohort
  When I verify the following response body fields in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id":
    | field            | value                                       |
    | id               | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | name.firstName   | Matt                                        |
    | name.middleName  | Aida                                        |
    | name.lastSurname | Sollars                                     |
  Then I verify the following response body fields do not exist in the response:
    | field                         |
    | studentUniqueStateId          |
    | studentIdentificationCode     |
    | otherName                     |
    | sex                           |
    | birthData                     |
    | address                       |
    | telephone                     |
    | electronicMail                |
    | profileThumbnail              |
    | hispanicLatinoEthnicity       |
    | oldEthnicity                  |
    | race                          |
    | economicDisadvantaged         |
    | schoolFoodServicesEligibility |
    | studentCharacteristics        |
    | limitedEnglishProficiency     |
    | languages                     |
    | homeLanguages                 |
    | disabilities                  |
    | section504Disabilities        |
    | displacementStatus            |
    | programParticipations         |
    | learningStyles                |
    | cohortYears                   |
    | studentIndicators             |
    | loginId                       |
    | gradeLevel                    |
    | schoolId                      |
# Get student in current Program
  When I verify the following response body fields in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id":
    | field            | value                                       |
    | id               | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | name.firstName   | Matt                                        |
    | name.middleName  | Aida                                        |
    | name.lastSurname | Sollars                                     |
  Then I verify the following response body fields do not exist in the response:
    | field                         |
    | studentUniqueStateId          |
    | studentIdentificationCode     |
    | otherName                     |
    | sex                           |
    | birthData                     |
    | address                       |
    | telephone                     |
    | electronicMail                |
    | profileThumbnail              |
    | hispanicLatinoEthnicity       |
    | oldEthnicity                  |
    | race                          |
    | economicDisadvantaged         |
    | schoolFoodServicesEligibility |
    | studentCharacteristics        |
    | limitedEnglishProficiency     |
    | languages                     |
    | homeLanguages                 |
    | disabilities                  |
    | section504Disabilities        |
    | displacementStatus            |
    | programParticipations         |
    | learningStyles                |
    | cohortYears                   |
    | studentIndicators             |
    | loginId                       |
    | gradeLevel                    |
    | schoolId                      |

  Scenario: Denying access to past associations
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as "student" "cegray" with password "cegray1234"
  And format "application/json"
  And I am using api version "v1"
  Then I validate that I am denied access to restricted endpoints via API:
    | uri                                                     | rc  |
   #| Student with expired StudentSectionAssociation
    | /v1/student/e40ee9041a7159c62867f63bf4da581ba9fc3dc7_id | 403 |
   #| Student with expired StudentProgramAssociation
    | /v1/student/e1b4e5e0e8c1d7b84d7a8eb72958ad0a53e7ef77_id | 403 |
   #| Student with expired StudentCohortAssociation
    | /v1/student/d7fa74360c0fa06259c37fb67b07b039211c72de_id | 403 |
   #| Student with no association to anything related
    | /v1/student/fa47e994944a53bc0b23a7f16fc5843149937b94_id | 403 |
