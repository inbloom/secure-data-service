Feature: As an SLI application, I want to be able to perform CRUD operations on edOrgs involving multiple parents

  Background: No extra setup required
    Given format "application/vnd.slc+json"

  #Why does this have to be an integration test? Because changes to StaffEducationOrganizationAssociation are reflected only after re-login.
  Scenario: CRUD operations involving multiple parents
    Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And format "application/vnd.slc+json"

    And I create a LEA named "LEA1" with parent IL
    And I create a LEA named "LEA2" with parent IL
    And I create a School named "School1" with parents "LEA1,LEA2"
    And I authorize all apps to access "School1"

    And I create a Student named "Student1"
    And I create a StudentSchoolAssociation between "Student1" and "School1"
    And I create a Cohort "Cohort1" for "School1" and associate "Student1" and "mgonzales" with it

    And I create a Student named "Student2"
    And I create a StudentSchoolAssociation between "Student2" and "School1"
    And I create a Cohort "Cohort2" for "School1" and associate "Student2" and "msmith" with it

    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "Leader" "mgonzales" with password "mgonzales1234"
    And I try to get "Student1" and get a response code "403"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I create a StaffEducationOrganizationAssociation  between "mgonzales" and "LEA1"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "Leader" "mgonzales" with password "mgonzales1234"
    And I try to get "Student1" and get a response code "200"
    #And I try to update "Student1" name to "New Student 1 Name"

    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "Leader" "msmith" with password "msmith1234"
    And I try to get "Student2" and get a response code "403"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I create a StaffEducationOrganizationAssociation  between "msmith" and "LEA2"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "Leader" "msmith" with password "msmith1234"
    And I try to get "Student2" and get a response code "200"
    #And I try to update "Student2" name to "New Student 2 Name"







