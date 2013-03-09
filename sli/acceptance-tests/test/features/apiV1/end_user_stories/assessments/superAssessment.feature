@RALLY_US
@RALLY_US
Feature: As a teacher or staff I want to investigate my student assessments

Background: None

Scenario Outline: As a teacher, for my section, I want to get the most recent Math assessment
   
Given I am a valid teacher <Username> with password <Password>
  And the testing device app key has been created
  And I have an open web browser
  And I import the odin-local-setup application and realm data
  
  When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "cgray" "cgray1234" for the "Simple" login page
      Then I should receive a json response containing my authorization code
  
  When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
      And I should be able to use the token to make valid API calls
      
  Given format "application/json"
    When I navigate to GET "/v1/home"
      Then I should get and store the link named "self"
      And I should extract the "teachers" id from the "self" URI
  
  When I navigate to GET "/teachers/<teacher id>"
    Then the response body "id" should match my teacher "id"
      And the response field "entityType" should be "teacher"
      And the response field "name.lastSurname" should be "Gray"
      And I should receive a link named "self" with URI "/teachers/<teacher id>"
      And I should get and store the link named "getTeacherSectionAssociations"
      And I should get and store the link named "getSections"
      And I should get and store the link named "getTeacherSchoolAssociations"
      And I should get and store the link named "getSchools"
      And I should get and store the link named "getStaffEducationOrgAssignmentAssociations"
      And I should get and store the link named "getEducationOrganizations" 

  When I follow the HATEOS link named "<getTeacherSectionAssociations>" 
    Then I should extract the "sectionId" from the response body to a list

  When I navigate to GET "/sections/<teacher section>"
    Then I should have a list of 12 "section" entities

  # Temporarily comment this out due to bug: 
  When I make a GET request to URI "/sections/@id/studentSectionAssociations/students/studentAssessments"
    Then I should have a list of 50 "studentAssessment" entities
    And I should extract the "id" from the response body to a list and save to "studentAssessments"

  When I navigate to GET "/v1/studentAssessments"
    Then I should have a list of 50 "studentAssessment" entities
    And I store the studentAssessments
    

  When I navigate to GET "/studentAssessments/<student assessment>"
    Then I should extract the "studentAssessment" id from the "self" URI
    And the response field "entityType" should be "studentAssessment"
    And the response field "administrationLanguage" should be "English"
    And the response field "administrationEnvironment" should be "Classroom"
    And the response field "retestIndicator" should be "Primary Administration"
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
    
Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "cgray"         | "cgray1234"         | "Educator"         |

