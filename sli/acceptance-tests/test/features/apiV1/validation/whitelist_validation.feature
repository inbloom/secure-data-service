@RALLY_US3001
@RALLY_US3066
Feature: White list input validation for API - core entities

  Background: Logged in as a super-user and using the small data set
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/json"

  Scenario Outline: Giant list of white list validations
    Given a valid document for <entity>
    When I navigate to POST "/v1/<entity uri>"
    Then I should receive a return code of 201
    When I change <key> to <invalid value>
    And I navigate to POST "/v1/<entity uri>"
    Then I should receive a return code of 400
    And I should see "pattern" in the error message

  Examples:
    | entity                 | entity uri              | key                          | invalid value           | comment                             |
    | staff                  | staff                   | address.postalCode           | ab123                   | non-digits                          |
    | staff                  | staff                   | address.postalCode           | 123                     | invalid zip length                  |
    | staff                  | staff                   | address.postalCode           | 123456                  | invalid zip length                  |
    | staff                  | staff                   | address.postalCode           | 12345-123               | invalid ext length                  |
    | staff                  | staff                   | address.postalCode           | 12345-12345             | invalid ext length                  |
    | staff                  | staff                   | address.countyFIPSCode       | abcde                   | non-digits                          |
    | staff                  | staff                   | address.countyFIPSCode       | 123                     | invalid code length                 |
    | staff                  | staff                   | address.countyFIPSCode       | 12345678                | invalid code length                 |
    | staff                  | staff                   | address.latitude             | 30.01.02                | too many '.'                        |
    | staff                  | staff                   | address.longitude            | 30.01.                  | ends with '.'                       |
    | staff                  | staff                   | electronicMail.emailAddress  | a@bunchofchar           | no '.'                              |
    | staff                  | staff                   | electronicMail.emailAddress  | a.bunch.of.char         | no '@'                              |
    | staff                  | staff                   | electronicMail.emailAddress  | test@abc.               | ends with '.'                       |
    | student                | students                | address.postalCode           | ab123                   | non-digits                          |
    | student                | students                | address.postalCode           | 123                     | invalid zip length                  |
    | student                | students                | address.postalCode           | 123456                  | invalid zip length                  |
    | student                | students                | address.postalCode           | 12345-123               | invalid ext length                  |
    | student                | students                | address.postalCode           | 12345-12345             | invalid ext length                  |
    | student                | students                | address.countyFIPSCode       | abcde                   | non-digits                          |
    | student                | students                | address.countyFIPSCode       | 123                     | invalid code length                 |
    | student                | students                | address.countyFIPSCode       | 12345678                | invalid code length                 |
    | student                | students                | address.latitude             | 30.01.02                | too many '.'                        |
    | student                | students                | address.longitude            | 30.01.                  | ends with '.'                       |
    | student                | students                | electronicMail.emailAddress  | a@bunchofchar           | no '.'                              |
    | student                | students                | electronicMail.emailAddress  | a.bunch.of.char         | no '@'                              |
    | student                | students                | electronicMail.emailAddress  | test@abc.               | ends with '.'                       |
    | student                | students                | profileThumbnail             | http://testabc          | no '.'                              |
    | student                | students                | profileThumbnail             | http://testabc.         | ends with '.'                       |
    | educationOrganization  | educationOrganizations  | address.postalCode           | ab123                   | non-digits                          |
    | educationOrganization  | educationOrganizations  | address.postalCode           | 123                     | invalid zip length                  |
    | educationOrganization  | educationOrganizations  | address.postalCode           | 123456                  | invalid zip length                  |
    | educationOrganization  | educationOrganizations  | address.postalCode           | 12345-123               | invalid ext length                  |
    | educationOrganization  | educationOrganizations  | address.postalCode           | 12345-12345             | invalid ext length                  |
    | educationOrganization  | educationOrganizations  | address.countyFIPSCode       | abcde                   | non-digits                          |
    | educationOrganization  | educationOrganizations  | address.countyFIPSCode       | 123                     | invalid code length                 |
    | educationOrganization  | educationOrganizations  | address.countyFIPSCode       | 12345678                | invalid code length                 |
    | educationOrganization  | educationOrganizations  | address.latitude             | 30.01.02                | too many '.'                        |
    | educationOrganization  | educationOrganizations  | address.longitude            | 30.01.                  | ends with '.'                       |
    | educationOrganization  | educationOrganizations  | webSite                      | http://www.test.        | ends with '.'                       |
    | educationOrganization  | educationOrganizations  | webSite                      | http://wwwtestcom       | no '.'                              |
    | educationOrganization  | educationOrganizations  | agencyHierarchyName          | a.bunch.of.char.        | ends with '.'                       |