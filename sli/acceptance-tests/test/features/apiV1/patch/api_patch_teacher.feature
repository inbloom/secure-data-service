Feature: As an SLI application, I want to be able to do partial updates.
  That means the API should support PATCH method for HTTP.

Background: Logged in as Teacher Charles Gray
  Given I am logged in using "cgrayadmin" "cgrayadmin1234" to realm "IL"
  And format "application/json"

  Scenario: Partial update using PATCH
    When I navigate to GET "/students/<ROBIN MCWAIN STUDENT ID>"
    Then I should receive a return code of 200
    And "sex" should be "Female"
    And "limitedEnglishProficiency" should be "NotLimited"
    When I change the field "sex" to "Male"
    And I change the field "limitedEnglishProficiency" to "Limited"
    And I navigate to PATCH "/students/<ROBIN MCWAIN STUDENT ID>"
    Then I should receive a return code of 204
    When I navigate to GET "/students/<ROBIN MCWAIN STUDENT ID>"
    And "sex" should be "Male"
    And "limitedEnglishProficiency" should be "Limited"
    
    Scenario: Partial update using PATCH for sub doc
    When I navigate to GET "/studentSectionAssociations/<STUDENT SECTION ASSOCIATION ID 3>"
    Then I should receive a return code of 200
    And "beginDate" should be "1998-08-14"
    And "studentId" should be "0c2756fd-6a30-4010-af79-488d6ef2735a"
    When I change the field "beginDate" to "2012-01-01"
    And I navigate to PATCH "/studentSectionAssociations/<STUDENT SECTION ASSOCIATION ID 3>"
    Then I should receive a return code of 204
    When I navigate to GET "/studentSectionAssociations/<STUDENT SECTION ASSOCIATION ID 3>"
    And "beginDate" should be "2012-01-01"
    
    Scenario: Sad path - some fields in the partial update are invalid for sub doc
    When I navigate to GET "/studentSectionAssociations/<STUDENT SECTION ASSOCIATION ID 2>"
    Then I should receive a return code of 200
    And "beginDate" should be "1980-10-09"
    And "studentId" should be "11e51fc3-2e4a-4ef0-bfe7-c8c29d1a798b"
    When I change the field "beginDate" to "2012-01-01---"
    And I navigate to PATCH "/studentSectionAssociations/<STUDENT SECTION ASSOCIATION ID 2>"
    Then I should receive a return code of 400
    When I navigate to GET "/studentSectionAssociations/<STUDENT SECTION ASSOCIATION ID 2>"
    And "beginDate" should be "1980-10-09"

  Scenario: Partial update on Array
    When I navigate to GET "/students/<ROBIN MCWAIN STUDENT ID>"
    Then I should receive a return code of 200
    And "race" should be "[]"
    And I change the field "race" to "[Asian,White]"
    And I navigate to PATCH "/students/<ROBIN MCWAIN STUDENT ID>"
    Then I should receive a return code of 204
    When I navigate to GET "/students/<ROBIN MCWAIN STUDENT ID>"
    And "race" should be "[Asian,White]"
    When I change the field "race" to "[Black - African American]"
    And I navigate to PATCH "/students/<ROBIN MCWAIN STUDENT ID>"
    Then I should receive a return code of 204
    When I navigate to GET "/students/<ROBIN MCWAIN STUDENT ID>"
    Then "race" should be "[Black - African American]"

  Scenario: Sad path - some fields in the partial update are invalid
    When I navigate to GET "/students/<ROBIN MCWAIN STUDENT ID>"
    Then I should receive a return code of 200
    And "sex" should be "Male"
    And "economicDisadvantaged" should be "false"
    When I change the field "sex" to "Neutral"
    And I change the field "economicDisadvantaged" to "true"
    When I navigate to PATCH "/students/<ROBIN MCWAIN STUDENT ID>"
    Then I should receive a return code of 400
    When I navigate to GET "/students/<ROBIN MCWAIN STUDENT ID>"
    Then I should receive a return code of 200
    And "sex" should be "Male"
    And "economicDisadvantaged" should be "false"

  Scenario: Sad path - partial update on invalid reference
    When I change the field "sex" to "Male"
    And I navigate to PATCH "/students/<INVALID ID>"
    Then I should receive a return code of 404

