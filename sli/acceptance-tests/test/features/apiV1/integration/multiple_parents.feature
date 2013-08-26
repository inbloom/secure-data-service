Feature: As an SLI application, I want to be able to perform CRUD operations on edOrgs involving multiple parents

  Background: No extra setup required
    Given format "application/vnd.slc+json"

  #LEA1(mgonzales)     LEA2(msmith)
  # |                        |
  # |                        |
  #  ________________________
  #               |
  #            School1 ______Cohort1-____Student1
  #                  |            |
  #                  |            |______mgonzales
  #                  |
  #                  |_______Cohort2_____Student2
  #                               |
  #                               |______msmith

  #Why does this have to be an integration test? Because changes to StaffEducationOrganizationAssociation are reflected only after re-login and re-login requires SimpleIDP.
  Scenario: CRUD operations involving multiple parents
    Given I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And format "application/vnd.slc+json"

    And I create a LEA named "LEA1" with parent IL
    And I create a LEA named "LEA2" with parent IL
    And I create a School named "School1" with parents "LEA1,LEA2"
    And I authorize all apps to access "School1"

    # School1 has HATEOS links for LEA1, LEA2 and LEA1/2 has HATEOS link for School1
    And I check that "School1" references "LEA1, LEA2" as parents
    And I check that "LEA1" has "School1" as a feederEducationOrganization
    And I check that "LEA2" has "School1" as a feederEducationOrganization

    #creating student, cohorts, staffEdOrgAssociations, studentSchoolAssociations
    And I create a Student named "Student1"
    And I create a StudentSchoolAssociation between "Student1" and "School1"
    And I create a Cohort "Cohort1" for "School1" and associate "Student1" and "mgonzales" with it

    And I create a Student named "Student2"
    And I create a StudentSchoolAssociation between "Student2" and "School1"
    And I create a Cohort "Cohort2" for "School1" and associate "Student2" and "msmith" with it

    #mgonzales from LEA1 can access Student1 from School1
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "Leader" "mgonzales" with password "mgonzales1234"
    And I try to get "Student1" and get a response code "403"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I create a StaffEducationOrganizationAssociation  between "mgonzales" and "LEA1"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "Leader" "mgonzales" with password "mgonzales1234"
    And I try to get "Student1" and get a response code "200"
    #And I try to update "Student1" name to "New Student 1 Name". mgonzales does not have RIGHTS to modify.

    #msmith from LEA2 can access Student2 from School1
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "Leader" "msmith" with password "msmith1234"
    And I try to get "Student2" and get a response code "403"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I create a StaffEducationOrganizationAssociation  between "msmith" and "LEA2"
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "Leader" "msmith" with password "msmith1234"
    And I try to get "Student2" and get a response code "200"
    #And I try to update "Student2" name to "New Student 2 Name". msmith does not have RIGHTS to modify

    #Parents can be deleted
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
    And I remove "LEA1" as parent of "School1"
    And I check that "School1" references "LEA2" as parents
    And I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "Leader" "mgonzales" with password "mgonzales1234"
    And I try to get "Student1" and get a response code "403"







