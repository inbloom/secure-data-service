Feature: As an SLI application, I want to be able to use entities to reference other entities
    This means I want to be able to CRUD entities with fields that are entity references
    I want these references to be displayed to me as links
    
Background: Logged in as a super-user and using the small data set
  Given I am logged in using "demo" "demo1234"
  Given I have access to all sections, schools, sessions and courses
  Given format "application/json"

Scenario: Confirm posting data with an invalid reference causes a validation error
    Given the "uniqueSectionCode" is "SpanishB09"
      And the "sequenceOfCourse" is "1"
      And the "educationalEnvironment" is "Off-school center"
      And the "mediumOfInstruction" is "Independent study"
      And the "populationServed" is "Regular Students"
      And the "schoolId" is "<'INVALID' ID>"
      And the "sessionId" is "<'INVALID' ID>"
      And the "courseId" is "<'INVALID' ID>"
     When I navigate to POST "/sections/"
     Then I should receive a return code of 400
     
Scenario: Confirm posting data with all valid references is accepted and then displayed as links
    Given the "uniqueSectionCode" is "SpanishB09"
      And the "sequenceOfCourse" is "1"
      And the "educationalEnvironment" is "Off-school center"
      And the "mediumOfInstruction" is "Independent study"
      And the "populationServed" is "Regular Students"
      And the "schoolId" is "<'APPLE ELEMENTARY (SCHOOL)' ID>"
      And the "sessionId" is "<'FALL 2011 (SESSION)' ID>"
      And the "courseId" is "<'FRENCH 1 (COURSE)' ID>"
     When I navigate to POST "/sections/"
     Then I should receive a return code of 201
      And I should receive an ID for the newly created section 
     When I navigate to GET "/sections/<'NEWLY CREATED SECTION' ID>"
     Then I should receive a return code of 200
      And the "schoolId" should be "<'APPLE ELEMENTARY (SCHOOL)' ID>"
      And the "sessionId" should be "<'FALL 2011 (SESSION)' ID>"
      And the "courseId" should be "<'FRENCH 1 (COURSE)' ID>"
      And I should receive a link named "courseId" with URI "/courses/<'FRENCH 1 (COURSE)' ID>"
      And I should receive a link named "schoolId" with URI "/schools/<'APPLE ELEMENTARY (SCHOOL)' ID>"
      And I should receive a link named "sessionId" with URI "/sessions/<'FALL 2011 (SESSION)' ID>"
     
Scenario: Confirm putting data with an invalid reference causes a validation error
    Given format "application/json"
     When I navigate to GET "/sections/<'BIOLOGY F09J (SECTION)' ID>"
     Then I should receive a return code of 200   
     When I set "courseId" to "<'INVALID' ID>"
      And I navigate to PUT "/sections/<'BIOLOGY F09J (SECTION)' ID>"
     Then I should receive a return code of 400

Scenario: Confirm putting data with a valid reference is accepted and then displayed as links
    Given format "application/json"
     When I navigate to GET "/sections/<'BIOLOGY F09J (SECTION)' ID>"
     Then I should receive a return code of 200   
     When I set "courseId" to "<'RUSSIAN 1 (COURSE)' ID>"
      And I navigate to PUT "/sections/<'BIOLOGY F09J (SECTION)' ID>"
     Then I should receive a return code of 204
     When I navigate to GET "/sections/<'BIOLOGY F09J (SECTION)' ID>"
     Then I should receive a return code of 200   
      And the "courseId" should be "<'RUSSIAN 1 (COURSE)' ID>"
      And I should receive a link named "courseId" with URI "/courses/<'RUSSIAN 1 (COURSE)' ID>"
