@RALLY_5765
Feature: Use the APi to successfully get student data while having roles over many schools

  @wip
  Background: Setup for the tests
    Given I have an open web browser
    And the testing device app key has been created
# Setup students and staff correctly. The following dependencies as an example
#
# School-A
#----------
# Leader: Staff-A
# Educator: Staff-B
# Educator: Staff-C
# Student: Student-A (in Staff-B's section)
# Student: Student-A (in Staff-C's section)
# Student: Student-B (in Staff-C's section)
#
# School-B
#---------
# Leader: Staff-A
# Educator: Staff-B
# Student: Student-A (in Staff-B's section)
#
# School-C
#---------
# Leader: Staff-C
# Educator: Staff-B
# Student: Student-B (in Staff-B's section)
# Student: Student-C (in Staff-B's section)


  @wip
  Scenario Outline: Get a student's data using various staff-student combination
    When I navigate to the API authorization endpoint with my client ID
    Then I select "Illinois Daybreak School District 4529" from the dropdown and click go
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "<staff>" "<password>" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<student's URI>"
    Then I should receive a return code of 204

    Examples:
    | staff       | password                  | student's URI           |
    | <Staff-B>   | <Staff-B's password>      | <student-A URI>         |
    | <Staff-B>   | <Staff-B's password>      | <student-B URI>         |
#    | <Staff-C>   | <Staff-C's password>      | <student-A URI>         |
#    | <Staff-C>   | <Staff-C's password>      | <student-C URI>         |
#    | <Staff-C>   | <Staff-C's password>      | <student-B URI>         |
    | <Staff-A>   | <Staff-A's password>      | <student-B URI>         |
    | <Staff-A>   | <Staff-A's password>      | <student-A URI>         |