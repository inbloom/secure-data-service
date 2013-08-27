@RALLY_US5859
Feature: As a teacher or staff of a hybrid edOrg I want to validate I can access data in it

Background: None

Scenario: As a teacher, for my section, I want to get a list of students

# Log in via simple-idp and authenticate teacher credentials   
  Given I log in to realm "IL Charter School" using simple-idp as "teacher" "chartereducator" with password "chartereducator1234"
    And format "application/json"
    
  When I navigate to GET "/v1/home"
    Then I should get and store the "teacher" link named "self"
     And I should extract the "teacher" id from the "self" URI
  
  When I navigate to GET "/teachers/<teacher id>"
    Then the response body "id" should match my "teacher" "id"
      And the response field "entityType" should be "teacher"
      And I should receive a link named "self" with URI "/teachers/<teacher id>"
      And I should get and store the "teacher" link named "getTeacherSectionAssociations"
      And I should get and store the "teacher" link named "getSections"
      And I should get and store the "teacher" link named "getTeacherSchoolAssociations"
      And I should get and store the "teacher" link named "getSchools"
      And I should get and store the "teacher" link named "getStaffEducationOrgAssignmentAssociations"
      And I should get and store the "teacher" link named "getEducationOrganizations" 

  When I validate the "teacher" HATEOS link for "getTeacherSectionAssociations"
    Then I should extract the "sectionId" from the response body to a list

  When I make a GET request to URI "/sections/@id/studentSectionAssociations/students"
    Then I should have a list of 1 "student" entities
