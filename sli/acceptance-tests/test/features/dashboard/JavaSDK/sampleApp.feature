@wip
Feature: List Students in SampleApp

As a SEA/LEA user, I want to be able to see list of students.

Background:
    Given I have an open web browser
    Given the sampleApp is deployed on sampleApp server
	

Scenario: Teacher sees list of students
		When I navigate to the sampleApp home page
		Then I should be redirected to the Realm page
		When I select "Illinois Realm" and click go
		When I login as "cgray" "cgray1234"
		Then I should be redirected to List of Students
		And the table should include header "Student"
		And I should see student sampeStudent" in the list


