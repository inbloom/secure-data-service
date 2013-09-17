@smoke
Feature: As a staff I want to demonstrate that all other staff I am associated with has secured data

Scenario: Teacher accesses another teacher in same school
	Given I am logged in using "rbraverman" "rbraverman1234" to realm "IL"
		And my role is Educator
	When I make an API call to access teachers
	Then I get a response
	And the response does not includes the protected fields


Scenario: Leader accesses teacher in same school and sees restricted fields
	Given I am logged in using "mgonzales" "mgonzales1234" to realm "IL"
		And my role is Leader
	When I make an API call to access teachers
	Then I get a response
	And the response includes the protected fields


Scenario: Teacher accesses Leader in same school
	Given I am logged in using "rbraverman" "rbraverman1234" to realm "IL"
		And my role is Educator
	When I make an API call to access staff
	Then I get a response
	And the response does not includes the protected fields

Scenario: Teacher accesses themselves and sees their restricted fields
	Given I am logged in using "rbraverman" "rbraverman1234" to realm "IL"
		And my role is Educator
	When I make an API call to access myself
	Then I get a response
	Then I should see my restricted information

#After F292, the return code should become 200
Scenario Outline: Staff can see other staff through programs/cohorts
    Given I am logged in using "sbantu" "Fall Gelb" to realm "IL"
    When I navigate to GET "/v1/staff/<StaffId>"
    Then I should receive a return code of <Code>
Examples:
| StaffId                              | Code |
| 85585b27-5368-4f10-a331-3abcaf3a3f4c | 403  |

@DE2657
Scenario: Staff can access staff via cohort despite studentAccess flag. Student Access flag works
    Given I am logged in using "rrogers" "заря" to realm "IL"
    When I update association "staffCohortAssociations" "8fef446f-fc63-15f9-8606-0b85086c07d5" to set studentAccessFlag to "false"
    When I navigate to GET "/v1/cohorts/b408635d-8fd5-11e1-86ec-0021701f543f_id/staffCohortAssociations/staff"
    Then I should receive a return code of 200
    And I should see a count of 2
    When I navigate to GET "/v1/cohorts/b408635d-8fd5-11e1-86ec-0021701f543f_id/studentCohortAssociations/students"
    Then I should receive a return code of 403
    When I update association "staffCohortAssociations" "8fef446f-fc63-15f9-8606-0b85086c07d5" to set studentAccessFlag to "true"
    When I navigate to GET "/v1/cohorts/b408635d-8fd5-11e1-86ec-0021701f543f_id/staffCohortAssociations/staff"
    Then I should receive a return code of 200
    And I should see a count of 2
    When I navigate to GET "/v1/cohorts/b408635d-8fd5-11e1-86ec-0021701f543f_id/studentCohortAssociations/students"
    Then I should receive a return code of 200
    And I should see a count of 2
    
@DE2657
Scenario: Staff can access staff via programs despite studentAccess flag. Student Access flag works
    Given I am logged in using "rrogers" "заря" to realm "IL"
    When I update association "staffProgramAssociations" "9bf906cc-8fd5-11e1-86ec-0021701f5432" to set studentAccessFlag to "false"
    When I navigate to GET "/v1/programs/9b8c3aab-8fd5-11e1-86ec-0021701f543f_id/staffProgramAssociations/staff"
    Then I should receive a return code of 200
    And I should see a count of 3
    When I navigate to GET "/v1/programs/9b8c3aab-8fd5-11e1-86ec-0021701f543f_id/studentProgramAssociations/students"
    Then I should receive a return code of 403
    When I update association "staffProgramAssociations" "9bf906cc-8fd5-11e1-86ec-0021701f5432" to set studentAccessFlag to "true"
    When I navigate to GET "/v1/programs/9b8c3aab-8fd5-11e1-86ec-0021701f543f_id/staffProgramAssociations/staff"
    Then I should receive a return code of 200
    And I should see a count of 3
    When I navigate to GET "/v1/programs/9b8c3aab-8fd5-11e1-86ec-0021701f543f_id/studentProgramAssociations/students"
    Then I should receive a return code of 200
    And I should see a count of 2
