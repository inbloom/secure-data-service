@DS917
Feature:
  As an user that has WRITE_PUBLIC access to an edorg that is assigned an educator role I want to be allowed to continue to modify data for edorg
  I am an admin for but not be allowed to modify data for the edorgs I am an educator

Background:
   Given I associate "cdc2fe5a-5e5d-4b10-8caa-8f3be735a7d4" to edorg "ec2e4218-6483-4e9c-8954-0aecccfd4731" with the role "Educator"

  Scenario: Disallow modifications to edorgs for which I have only READ_PUBLIC access
    Given I am logged in as a local-level IT Administrator
    When I PATCH the edorg "ec2e4218-6483-4e9c-8954-0aecccfd4731"
    Then I should receive a 403 Denied

  Scenario: Disallow the ability to overwrite data for edorgs for which I have only READ_PUBLIC access
    Given I am logged in as a local-level IT Administrator
    When I PUT the edorg "ec2e4218-6483-4e9c-8954-0aecccfd4731"
    Then I should receive a 403 Denied

  Scenario: Disallow deletion of an edorg for which I have only READ_PUBLIC access
    Given I am logged in as a local-level IT Administrator
    When I DELETE the edorg "ec2e4218-6483-4e9c-8954-0aecccfd4731"
    Then I should receive a 403 Denied

  Scenario: Continue to allow read access to newly assigned edorg
    Given I am logged in as a local-level IT Administrator
    When I GET the edorg "ec2e4218-6483-4e9c-8954-0aecccfd4731"
    Then I should receive a 200 OK

#  Scenario: Allow Administrators to modify EdOrgs below them
#    Given I am logged in as a district-level IT Administrator
#    When I PATCH the edorg "ec2e4218-6483-4e9c-8954-0aecccfd4731"
#    Then the response status should be 204 No Content

#No PUT PATCH DELETE
# Edit PUBLIC Data below me as an LEA Admin
