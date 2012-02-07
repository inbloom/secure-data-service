Feature: As an SLI application, I want to be able to traverse from education organizations down to low levels
    As a client application using SLI
    I want to be able to start at a very high level and be able to access relevant teachers
    I want to be able to start at a very high level and be able to access relevant students

Background: 
    Given I am logged in using "demo" "demo1234"
    Given I have access to all data
    Given format "application/json"
    
Scenario: Traverse from parent education organization through child education organization, school, session, course, section to a teacher
    When I navigate to GET "/educationOrganizations/<'KANSAS STATE (EDORG)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getEducationOrganizationAssociations" with URI "/educationOrganization-associations/<'KANSAS STATE (EDORG)' ID>"
    When I navigate to GET "/educationOrganization-associations/<'KANSAS STATE (EDORG)' ID>"
    Then I should receive a return code of 200
     And I should receive a collection link named "self" with URI "/educationOrganization-associations/<'KANSAS STATE-SMALLVILLE DISTRICT (EDORG-EDORG)' ID>"
    When I navigate to GET "/educationOrganization-associations/<'KANSAS STATE-SMALLVILLE DISTRICT (EDORG-EDORG)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getEducationOrganizationChild" with URI "/educationOrganizations/<'SMALLVILLE DISTRICT (EDORG)' ID>"
    When I navigate to GET "/educationOrganizations/<'SMALLVILLE DISTRICT (EDORG)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getSchoolsAssigned" with URI "/educationOrganization-school-associations/<'SMALLVILLE DISTRICT (EDORG)' ID>"
    When I navigate to GET "/educationOrganization-school-associations/<'SMALLVILLE DISTRICT (EDORG)' ID>"
    Then I should receive a return code of 200
     And I should receive a collection link named "self" with URI "/educationOrganization-school-associations/<'SMALLVILLE DISTRICT-DEREK SMALLS HIGH SCHOOL (EDORG-SCHOOL)' ID>"
    When I navigate to GET "/educationOrganization-school-associations/<'SMALLVILLE DISTRICT-DEREK SMALLS HIGH SCHOOL (EDORG-SCHOOL)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getSchool" with URI "/schools/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>"
    When I navigate to GET "/schools/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getSchoolSessionAssociations" with URI "/school-session-associations/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>"
    When I navigate to GET "/school-session-associations/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>"
    Then I should receive a return code of 200
     And I should receive a collection link named "self" with URI "/school-session-associations/<'SPRING 2011 @ DEREK SMALLS HIGH SCHOOL (SCHOOL-SESSION)' ID>"
    When I navigate to GET "/school-session-associations/<'SPRING 2011 @ DEREK SMALLS HIGH SCHOOL (SCHOOL-SESSION)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getSession" with URI "/sessions/<'SPRING 2011 (SESSION)' ID>"
    When I navigate to GET "/sessions/<'SPRING 2011 (SESSION)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getSessionCourseAssociations" with URI "/session-course-associations/<'SPRING 2011 (SESSION)' ID>"
    When I navigate to GET "/session-course-associations/<'SPRING 2011 (SESSION)' ID>"
    Then I should receive a return code of 200
     And I should receive a collection link named "self" with URI "/session-course-associations/<'MATH 1 DURING SPRING 2011 (SESSION-COURSE)' ID>"
    When I navigate to GET "/session-course-associations/<'MATH 1 DURING SPRING 2011 (SESSION-COURSE)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getCourse" with URI "/courses/<'MATH 1 (COURSE)' ID>"
    When I navigate to GET "/courses/<'MATH 1 (COURSE)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getCourseSectionAssociations" with URI "/course-section-associations/<'MATH 1 (COURSE)' ID>"
    When I navigate to GET "/course-section-associations/<'MATH 1 (COURSE)' ID>"
    Then I should receive a return code of 200
     And I should receive a collection link named "self" with URI "/course-section-associations/<'MATH 1 SECTION 1 (COURSE-SECTION)' ID>"
    When I navigate to GET "/course-section-associations/<'MATH 1 SECTION 1 (COURSE-SECTION)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getSection" with URI "/sections/<'MATH 1 (SECTION)' ID>"
    When I navigate to GET "/sections/<'MATH 1 (SECTION)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'MATH 1 (SECTION)' ID>"
    When I navigate to GET "/teacher-section-associations/<'MATH 1 (SECTION)' ID>"
    Then I should receive a return code of 200
     And I should receive a collection link named "self" with URI "/teacher-section-associations/<'TEACHER OF SECTION 1 (TEACHER-SECTION)' ID>"
    When I navigate to GET "/teacher-section-associations/<'TEACHER OF SECTION 1 (TEACHER-SECTION)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getTeacher" with URI "/teachers/<'OTIS OBAMA (TEACHER)' ID>"
    When I navigate to GET "/teachers/<'OTIS OBAMA (TEACHER)' ID>"
    Then I should receive a return code of 200
     And the "name" should be "Otis" "" "Obama"
     And "staffUniqueStateId" should be "8888039"
    
Scenario: Traverse from parent education organization through child education organization, school, session, course, section to a student
    When I navigate to GET "/educationOrganizations/<'KANSAS STATE (EDORG)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getEducationOrganizationAssociations" with URI "/educationOrganization-associations/<'KANSAS STATE (EDORG)' ID>"
    When I navigate to GET "/educationOrganization-associations/<'KANSAS STATE (EDORG)' ID>"
    Then I should receive a return code of 200
     And I should receive a collection link named "self" with URI "/educationOrganization-associations/<'KANSAS STATE-SMALLVILLE DISTRICT (EDORG-EDORG)' ID>"
    When I navigate to GET "/educationOrganization-associations/<'KANSAS STATE-SMALLVILLE DISTRICT (EDORG-EDORG)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getEducationOrganizationChild" with URI "/educationOrganizations/<'SMALLVILLE DISTRICT (EDORG)' ID>"
    When I navigate to GET "/educationOrganizations/<'SMALLVILLE DISTRICT (EDORG)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getSchoolsAssigned" with URI "/educationOrganization-school-associations/<'SMALLVILLE DISTRICT (EDORG)' ID>"
    When I navigate to GET "/educationOrganization-school-associations/<'SMALLVILLE DISTRICT (EDORG)' ID>"
    Then I should receive a return code of 200
     And I should receive a collection link named "self" with URI "/educationOrganization-school-associations/<'SMALLVILLE DISTRICT-DEREK SMALLS HIGH SCHOOL (EDORG-SCHOOL)' ID>"
    When I navigate to GET "/educationOrganization-school-associations/<'SMALLVILLE DISTRICT-DEREK SMALLS HIGH SCHOOL (EDORG-SCHOOL)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getSchool" with URI "/schools/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>"
    When I navigate to GET "/schools/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getSchoolSessionAssociations" with URI "/school-session-associations/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>"
    When I navigate to GET "/school-session-associations/<'DEREK SMALLS HIGH SCHOOL (SCHOOL)' ID>"
    Then I should receive a return code of 200
     And I should receive a collection link named "self" with URI "/school-session-associations/<'SPRING 2011 @ DEREK SMALLS HIGH SCHOOL (SCHOOL-SESSION)' ID>"
    When I navigate to GET "/school-session-associations/<'SPRING 2011 @ DEREK SMALLS HIGH SCHOOL (SCHOOL-SESSION)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getSession" with URI "/sessions/<'SPRING 2011 (SESSION)' ID>"
    When I navigate to GET "/sessions/<'SPRING 2011 (SESSION)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getSessionCourseAssociations" with URI "/session-course-associations/<'SPRING 2011 (SESSION)' ID>"
    When I navigate to GET "/session-course-associations/<'SPRING 2011 (SESSION)' ID>"
    Then I should receive a return code of 200
     And I should receive a collection link named "self" with URI "/session-course-associations/<'MATH 1 DURING SPRING 2011 (SESSION-COURSE)' ID>"
    When I navigate to GET "/session-course-associations/<'MATH 1 DURING SPRING 2011 (SESSION-COURSE)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getCourse" with URI "/courses/<'MATH 1 (COURSE)' ID>"
    When I navigate to GET "/courses/<'MATH 1 (COURSE)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getCourseSectionAssociations" with URI "/course-section-associations/<'MATH 1 (COURSE)' ID>"
    When I navigate to GET "/course-section-associations/<'MATH 1 (COURSE)' ID>"
    Then I should receive a return code of 200
     And I should receive a collection link named "self" with URI "/course-section-associations/<'MATH 1 SECTION 1 (COURSE-SECTION)' ID>"
    When I navigate to GET "/course-section-associations/<'MATH 1 SECTION 1 (COURSE-SECTION)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getSection" with URI "/sections/<'MATH 1 (SECTION)' ID>"
    When I navigate to GET "/sections/<'MATH 1 (SECTION)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getStudentSectionAssociations" with URI "/student-section-associations/<'MATH 1 (SECTION)' ID>"
    When I navigate to GET "/student-section-associations/<'MATH 1 (SECTION)' ID>"
    Then I should receive a return code of 200
     And I should receive a collection link named "self" with URI "/student-section-associations/<'SCOTTY PIERSON IN MATH 1 (STUDENT-SECTION)' ID>"
    When I navigate to GET "/student-section-associations/<'SCOTTY PIERSON IN MATH 1 (STUDENT-SECTION)' ID>"
    Then I should receive a return code of 200
     And I should receive a link named "getStudent" with URI "/students/<'SCOTTY PIERSON (STUDENT)' ID>"
    When I navigate to GET "/students/<'SCOTTY PIERSON (STUDENT)' ID>"
    Then I should receive a return code of 200
     And the "name" should be "Scotty" "" "Pierson"
     And "economicDisadvantaged" should be "true"
     And "schoolFoodServicesEligibility" should be "Free"
     And a "studentCharacteristics" "characteristic" should be "Homeless"
     And "limitedEnglishProficiency" should be "Limited"
     And a "disabilities" "disability" should be "Emotional Disturbance"
     And "section504Disabilities" should be "Motor Impairment"
     And "displacementStatus" should be "Hurricane Kevin"
     And a "programParticipations" should be "Extended Day/Child Care Services"
     
    
    