@RALLY_DE955
Feature: As an SLI application, I want to return the right order of entities.
  That means when I do a GET on comma-separated entities, the order of the entities should be preserved.

  Background: Logged in a Linda Kim
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And format "application/json"

  Scenario Outline: Doing a GET request on 3 comma-separated, all valid student IDs
    Given the order of students I want is <STUDENT IDs ORDER>
    When I navigate to GET "/v1/students/<STUDENT IDs LIST>"
    Then I should receive a return code of 200
    And the response at position 0 should include the information <STRING 1>
    And the response at position 1 should include the information <STRING 2>
    And the response at position 2 should include the information <STRING 3>

  Examples:
    | STUDENT IDs ORDER                                         | STRING 1           | STRING 2           | STRING 3           |
    | <MATT SOLLARS ID>,<CARMEN ORTIZ ID>,<MARVIN MILLER ID>    | <MATT SOLLARS ID>  | <CARMEN ORTIZ ID>  | <MARVIN MILLER ID> |
    | <MATT SOLLARS ID>,<MARVIN MILLER ID>,<CARMEN ORTIZ ID>    | <MATT SOLLARS ID>  | <MARVIN MILLER ID> | <CARMEN ORTIZ ID>  |
    | <CARMEN ORTIZ ID>,<MATT SOLLARS ID>,<MARVIN MILLER ID>    | <CARMEN ORTIZ ID>  | <MATT SOLLARS ID>  | <MARVIN MILLER ID> |
    | <CARMEN ORTIZ ID>,<MARVIN MILLER ID>,<MATT SOLLARS ID>    | <CARMEN ORTIZ ID>  | <MARVIN MILLER ID> | <MATT SOLLARS ID>  |
    | <MARVIN MILLER ID>,<CARMEN ORTIZ ID>,<MATT SOLLARS ID>    | <MARVIN MILLER ID> | <CARMEN ORTIZ ID>  | <MATT SOLLARS ID>  |
    | <MARVIN MILLER ID>,<MATT SOLLARS ID>,<CARMEN ORTIZ ID>    | <MARVIN MILLER ID> | <MATT SOLLARS ID>  | <CARMEN ORTIZ ID>  |

  Scenario Outline: Doing a GET request on 3 comma-separated, some invalid student IDs
    Given the order of students I want is <STUDENT IDs ORDER>
    When I navigate to GET "/v1/students/<STUDENT IDs LIST>"
    Then I should receive a return code of 200
    And the response at position 0 should include the information <STRING 1>
    And the response at position 1 should include the information <STRING 2>
    And the response at position 2 should include the information <STRING 3>

  Examples:
    | STUDENT IDs ORDER                                         | STRING 1             | STRING 2             | STRING 3             |
    | <INVALID ID>,<CHEROKEE STUART ID>,<MARVIN MILLER ID>      | Entity not found     | Access DENIED        | <MARVIN MILLER ID>   |
    | <INVALID ID>,<MARVIN MILLER ID>,<CHEROKEE STUART ID>      | Entity not found     | <MARVIN MILLER ID>   | Access DENIED        |
    | <CHEROKEE STUART ID>,<INVALID ID>,<MARVIN MILLER ID>      | Access DENIED        | Entity not found     | <MARVIN MILLER ID>   |
    | <CHEROKEE STUART ID>,<MARVIN MILLER ID>,<INVALID ID>      | Access DENIED        | <MARVIN MILLER ID>   | Entity not found     |
    | <MARVIN MILLER ID>,<CHEROKEE STUART ID>,<INVALID ID>      | <MARVIN MILLER ID>   | Access DENIED        | Entity not found     |
    | <MARVIN MILLER ID>,<INVALID ID>,<CHEROKEE STUART ID>      | <MARVIN MILLER ID>   | Entity not found     | Access DENIED        |