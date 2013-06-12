@RALLY_US4305
@RALLY_US4306

Feature: As a student or staff I want to use apps that access the inBloom API

  Background: None


  Scenario: As a student, for my section, I want to get the most recent Math assessment
  # Log in via simple-idp and authenticate student credentials
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    When I navigate to GET "/v1/home"
    Then I should validate all the HATEOS links

  Scenario: I check the response body fields of specific student API endpoints
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    And I am accessing data about myself, "matt.sollars"
   When I verify the following response body fields in "/students/067198fd6da91e1aa8d67e28e850f224d6851713_id":
    | field                                                 | value                                       |
    | id                                                    | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | entityType                                            | student                                     |
    | studentUniqueStateId                                  | 800000025                                   |
    | sex                                                   | Female                                      |
    | oldEthnicity                                          | White, Not Of Hispanic Origin               |
    | profileThumbnail                                      | 800000025 thumb                             |
    | limitedEnglishProficiency                             | NotLimited                                  |
    | schoolFoodServicesEligibility                         | Full price                                  |
    | displacementStatus                                    | Status BBB                                  |
    | hispanicLatinoEthnicity                               | false                                       |
    #| studentCharacteristics.beginDate                      | 20013-04-20                                 |
    | name.firstName                                        | Matt                                        |
    | name.middleName                                       | Aida                                        |
    | name.lastSurname                                      | Sollars                                     |
    | studentIdentificationCode.0.identificationCode        | abcde                                       |
    | studentIdentificationCode.0.identificationSystem      | District                                    |
    | studentIdentificationCode.0.assigningOrganizationCode | School                                      |
    | address.0.streetNumberName                            | 707 Elm Street                              |
    | electronicMail.0.emailAddress                         | 4859@fakemail.com                           |
    | telephone.0.telephoneNumber                           | (115)555-5072                               |
    | homeLanguages.0.language                              | Cambodian (Khmer)                           |
    | studentIndicators.0.indicator                         | Indicator 1                                 |
    | otherName.0.lastSurname                               | Charles                                     |
    | languages.0.language                                  | Norwegian                                   |
    | programParticipations.0.program                       | Adult/Continuing Education                  |
    | section504Disabilities.0                              | Sensory Impairment                          |
    | cohortYears.0.schoolYear                              | 2011-2012                                   |
    | race.0                                                | Black - African American                    |
    | disabilities.0.disability                             | Deaf-Blindness                              |
    | studentCharacteristics.0.endDate                      | 2014-08-01                                  |
    | studentCharacteristics.0.designatedBy                 | Teacher                                     |
    | studentCharacteristics.0.characteristic               | Unaccompanied Youth                         |

  @student_public
  Scenario: Student cannot POST public entities
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
    When I POST and validate the following entities:
    | entity                         |  type                                  |  returnCode  |
    | newProgram                     |  program                               |  403         |


  @wip
  Scenario: Student cannot POST private entities
  Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    And I am using api version "v1"
   When I POST and validate the following entities:
    | entity                         |  type                                  |  returnCode  |
    | newDaybreakStudent             |  staffStudent                          |  403         |

  @wip
  Scenario: DIS IS CRAP
    When I navigate to GET "/students/<my student id>"
    Then the response body "id" should match my "student" "id"
    And the response field "entityType" should be "teacher"
    And the response field "name.lastSurname" should be "Gray"
    And I should receive a link named "self" with URI "/teachers/<teacher id>"
    And I should get and store the link named "getTeacherSectionAssociations"
    And I should get and store the link named "getSections"
    And I should get and store the link named "getTeacherSchoolAssociations"
    And I should get and store the link named "getSchools"
    And I should get and store the link named "getStaffEducationOrgAssignmentAssociations"
    And I should get and store the link named "getEducationOrganizations"

    When I navigate to GET "/sections/<teacher section>"
    Then I should have a list of 12 "section" entities

    When I make a GET request to URI "/sections/@id/studentSectionAssociations/students/studentAssessments"
    Then I should have a list of 42 "studentAssessment" entities
    And I should extract the "id" from the response body to a list and save to "studentAssessments"

    When I navigate to GET "/v1/studentAssessments"
    Then I should have a list of 50 "studentAssessment" entities
    And I store the studentAssessments

    When I navigate to GET "/studentAssessments/<student assessment>"
    Then I should extract the "studentAssessment" id from the "self" URI
    And the response field "entityType" should be "studentAssessment"
    And the response field "administrationLanguage.language" should be "English"
    And the response field "administrationEnvironment" should be "Classroom"
    And the response field "retestIndicator" should be "Primary Administration"
    And the response field "<SOA.scoreResults.result>" should be "77"
    And the response field "<SOA.OA.identificationCode>" should be "2013-Eleventh grade Assessment 2.OA-0"
    And I sort the studentAssessmentItems
    And the response field "<SAI.AI.identificationCode>" should be "2013-Eleventh grade Assessment 2#1"
    And I should extract the student reference from studentAssessment
    And I should extract the assessment reference from studentAssessment

  # /assessments/{id}
  # assessment: d12f6eb0f1a2bc260a738db6c61ea5515badc1cb_id
    When I navigate to GET "/assessments/<assessment>"
    Then I should extract the "assessment" id from the "self" URI
    And the response field "<AIC.identificationSystem>" should be "State"
    And the response field "<AIC.ID>" should be "<assessment 1>"
    And the response field "<OA.nomenclature>" should be "Nomenclature"
    And the response field "<OA.identificationCode>" should be "<objective assessment>"
    And the response field "<OA.percentOfAssessment>" should be the number "50"
    And the response field "<OA.APL.PLD.codeValue>" should be "<code value>"
    And the response field "<OA.APL.assessmentReportingMethod>" should be "<reporting method>"
    And the response field "<OA.AP.minimumScore>" should be the number "0"
    And the response field "<OA.AP.maximumScore>" should be the number "50"
  # Sub-Objective Assessments
    And the response field "<OA.OAS.nomenclature>" should be "Nomenclature"
    And the response field "<OA.OAS.identificationCode>" should be "<sub objective assessment>"
    And the response field "<OA.OAS.percentOfAssessment>" should be the number "50"
    And the response field "<OA.OAS.APL.PLD.codeValue>" should be "<code value>"
    And the response field "<OA.OAS.APL.assessmentReportingMethod>" should be "<reporting method>"
    And the response field "<OA.OAS.APL.minimumScore>" should be the number "0"
    And the response field "<OA.OAS.APL.maximumScore>" should be the number "50"
    And I should extract the learningObjectives from "<OA.learningObjectives>"
    And I should extract the learningObjectives from "<OA.OAS.learningObjectives>"
    And I make sure "<OA.learningObjectives>" match "<OA.OAS.learningObjectives>"
    And the response field "<OA.maxRawScore>" should be the number "50"
    And the response field "<OA.OAS.AI.identificationCode>" should be "<assessment item 1>"
    And the response field "<OA.OAS.AI.correctResponse>" should be "<correct response>"
    And the response field "<OA.OAS.AI.itemCategory>" should be "<item category>"
    And the response field "<OA.OAS.AI.maxRawScore>" should be the number "10"
  # Assessment Family Hierarchy
    And the response field "assessmentFamilyHierarchyName" should be "<assessment family hierarchy>"
  # Assessment Period Descriptor
    And the response field "assessmentPeriodDescriptor.description" should be "<assessment period descriptor>"
    And the response field "assessmentPeriodDescriptor.codeValue" should be "<APD.codeValue>"
    And the response field "entityType" should be "assessment"
    And the response field "gradeLevelAssessed" should be "Eleventh grade"
    And the response field "assessmentTitle" should be "<assessment 1>"
    And I extract all the "assessment" links

    When I follow the links for assessment
    Then I should validate the "objectiveAssessment.0.learningObjectives" from "assessment" links map to learningObjectives

    When I navigate to GET "/v1/search/assessments?q=Sixth"
    Then I should have a list of 4 "assessment" entities
    When I navigate to GET "/v1/search/assessments?assessmentTitle=2013-Sixth%20grade%20Assessment%202"
    Then I should have a list of 1 "assessment" entities
    And the offset response field "assessmentTitle" should be "2013-Sixth grade Assessment 2"
    And the offset response field "gradeLevelAssessed" should be "Sixth grade"
    And the offset response field "<AIC.ID>" should be "2013-Sixth grade Assessment 2"
    And the offset response field "<AIC.identificationSystem>" should be "State"
    And the offset response field "assessmentPeriodDescriptor.description" should be "Beginning of Year 2013-2014 for Sixth grade"
    And the offset response field "assessmentPeriodDescriptor.codeValue" should be "BOY-6-2013"
    And the offset response field "assessmentFamilyHierarchyName" should be "2013 Standard.2013 Sixth grade Standard"

    When I navigate to GET "/v1/search/assessments?q=sub"
    Then I should have a list of 50 "assessment" entities

    When I navigate to GET "/v1/search/assessments?q=2014-ninth%20grade%20assessment%201&limit=100"
    Then I should have a list of 52 "assessment" entities

    When I navigate to GET "/v1/search/assessments?assessmentTitle=2013-Sixth%20grade%20Assessment%201"
    Then I should have a list of 1 "assessment" entities
    And the offset response field "assessmentTitle" should be "2013-Sixth grade Assessment 1"
    And the offset response field "<search.assessment.ID>" should be "2013-Sixth grade Assessment 1"
    And the offset response field "<search.assessment.ID.system>" should be "State"
    And the offset response field "assessmentPeriodDescriptor.description" should be "Beginning of Year 2013-2014 for Sixth grade"
    And the offset response field "assessmentPeriodDescriptor.codeValue" should be "BOY-6-2013"
  #And the response field "<search.assessment.ID.system>" should be "State"
  # assessmentPeriodDescriptorId = ac743445484ab8745f3921fea80bad59bf484593_id
  #And the response field "<search.APD.id>" should be valid
  # assessmentFamilyReference = 3391fabed45ea970b84a47ae545ab165b4370cc4_id
  # And the offset response field "assessmentFamilyHierarchyName" should be "2014 Standard.2014 Ninth grade Standard"
  # assessmentFamilyHierarchyName = 2014 Standard.2014 Ninth grade Standard

    When I navigate to GET "/v1/assessments?assessmentPeriodDescriptor.description=Beginning%20of%20Year%202013-2014%20for%20Sixth%20grade"
    Then I should have a list of 2 "assessment" entities
    And the offset response field "assessmentTitle" should be "2013-Sixth grade Assessment 2"
    And the offset response field "gradeLevelAssessed" should be "Sixth grade"
    And the offset response field "assessmentFamilyHierarchyName" should be "2013 Standard.2013 Sixth grade Standard"
    And the offset response field "assessmentPeriodDescriptor.description" should be "Beginning of Year 2013-2014 for Sixth grade"
    And the offset response field "assessmentPeriodDescriptor.codeValue" should be "BOY-6-2013"

    When I navigate to GET "/v1/assessments?assessmentFamilyHierarchyName=2013%20Standard.2013%20Sixth%20grade%20Standard"
    Then I should have a list of 2 "assessment" entities
    And the offset response field "assessmentTitle" should be "2013-Sixth grade Assessment 2"
    And the offset response field "gradeLevelAssessed" should be "Sixth grade"
    And the offset response field "assessmentFamilyHierarchyName" should be "2013 Standard.2013 Sixth grade Standard"
    And the offset response field "<search.assessment.ID>" should be "2013-Sixth grade Assessment 2"
    And the offset response field "assessmentPeriodDescriptor.description" should be "Beginning of Year 2013-2014 for Sixth grade"
    And the offset response field "assessmentPeriodDescriptor.codeValue" should be "BOY-6-2013"
    And the offset response field "<OA.identificationCode>" should be "2013-Sixth grade Assessment 2.OA-0"
    And the offset response field "<OA.OAS.AI.identificationCode>" should be "2013-Sixth grade Assessment 2#1"


  Scenario Outline: Student has access to stuff
    Given I log in to realm "Illinois Daybreak Students" using simple-idp as student "student.m.sollars" with password "student.m.sollars1234"
    And format "application/json"
    When I navigate to GET "/v1/<Entity>/<Ids>"
    Then I should receive a return code of 200
  Examples:
    | Entity                    | Ids                                                                                                                                                                           |
    | students                  | 067198fd6da91e1aa8d67e28e850f224d6851713_id                                                                                                                                   |
    | parents                   | 5f8989384287747b1960d16edd95ff2bb318e3bd_id,7f5b783a051b72820eab5f8188c45ade72869f0f_id                                                                                       |
    | studentParentAssociations | 067198fd6da91e1aa8d67e28e850f224d6851713_idc43bbfa3df05d4fd2d78a9edfee8fd63fbcf495a_id,067198fd6da91e1aa8d67e28e850f224d6851713_ide2f8c24b3e1ab8ead6e134d661a464d0f90e4c8e_id |
