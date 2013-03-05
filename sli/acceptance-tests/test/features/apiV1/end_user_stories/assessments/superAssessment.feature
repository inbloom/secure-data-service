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
    Then I should have a list of 10 "section" entities

  # Temporarily comment this out due to bug: 
  #When I make a GET request to URI "/sections/@id/studentSectionAssociations/students/studentAssessments"
    #Then I should have a list of 50 "studentAssessment" entities
    #And I should extract the "id" from the response body to a list and save to "studentAssessments"

  When I navigate to GET "/v1/studentAssessments"
    Then I should have a list of 50 "studentAssessment" entities
    And I store the studentAssessments

  When I navigate to GET "/studentAssessments/<student assessment>"
    And the response field "entityType" should be "studentAssessment"
    And the response field "administrationLanguage" should be "English"
    And the response field "administrationEnvironment" should be "Classroom"
    And the response field "retestIndicator" should be "Primary Administration"



  When I navigate to GET "/students"

    #/teachers/{id}/teacherSectionAssociations/sections << get sections
    #/teachers/{id}/teacherSchoolAssociations/schools << get schools

    #/sections/{id}/studentSectionAssociations/students/studentAssessments  << get _id, assessmentId, {studentObjectiveAssessment >> objectiveAssessment >> assessmentId, learningObjective, subObjectiveAssessment, learningObjectives, nomenclature }, {studentAssessmentItems >> assessmentItem >> {identificationCode, assessmentId, learningStandards, maxRawScore (10), itemCategory ("True-False")}, studentId, , administrationLanguage


    #UC2: As a teacher, for my section, I want to get the most recent state math assessment (APD)
    # get sections

    


    #as a teacher, for my section/{id}, get the most recent math assessment
    #/sections/{id}/studentSectionAssociations/students/studentAssessments  << teachers
    #use state assessment, maps to linda kim better
    #want most recent assessment, sort by period descriptor > boy, moy, eoy (know what your current date is)
    #get student's results on that assessment

    #UC1: As a teacher, for my sections, I want to get the most recent results for a state math assessment (AD)
    #     student assessment more than once
    # teacher data struct:
    #  teacher = { id => "id", 
    #              schools  => [school1, ..., schoolN]
    #              sections => [section1, .., sectionN]
    #              students => [list_of_students],
    #              assessments => { assessment1 => "_id",
    #                               assessmentN => "_id"},
    #              studentAssessments => { "_id" => "_id",
    #                                      "entityType" => "studentAssessment"
    #                                      "assessmentId" => "_id",
    #                                      "studentId" => "_id",
    #                                      "administrationLanguage" => "English",
    #                                      "administrationEnvironment" => "Classroom",
    #                                      "retestIndicator" => "Primary Administration",
    #                                      {"studentObjectiveAssessment" => 
    #                                        { "objectiveAssessment" =>
    #                                          { "assessmentId" => "_id", 
    #                                            "subObjectiveAssessment" => ["2014-Eleventh grade Assessment 1.OA-0 Sub"],
    #                                            "nomenclature" => "Nomenclature"
    #                                            "identificationCode" => "2014-Eleventh grade Assessment 1.OA-0" 
    #                                            "assessmentItemRefs" => []
    #                                            "learningObjectives" => ["_id1", "_id2", "_idN"], 
    #                                            "maxRawScore" => 50
    #                                          }
    #                                        }
    #                                      }
    #                                    },
    #              links => {self => "some_uri", 
    #                        getTeacherSectionAssociations => "some_uri", 
    #                        getSections => "some_uri"}}

Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "cgray"         | "cgray1234"         | "Educator"         |
| "linda.kim"     | "linda.kim1234"     | "Educator"         |

