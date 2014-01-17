@DS917
Feature:
  As an admin for an edorg that is assigned an educator role I want to be allowed to continue to modify data for edorg
  I am an admin for but not be allowed to modify data for the edorgs I am an educator

Background: Setup for the tests
Given an authorized app key has been created

Scenario: Assign a new staff edorg association
Given I associate "cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4" to edorg "ec2e4218-6483-4e9c-8954-0aecccfd4731" with the role "Educator"
  And I log in as "akopel"
When I PATCH the edorg "ec2e4218-6483-4e9c-8954-0aecccfd4731"
Then I should receive a 403 error from the API