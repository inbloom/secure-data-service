@RALLY_US5779

Feature: Use the APi to successfully post student data while having roles over many schools

  Background: Setup for the tests
    Given the testing device app key has been created
    And I import the odin setup application and realm data

  Scenario: User with Write Access in first school and no Write Access in a second school
    Given I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_PUBLIC" right
    And I change the custom role of "Educator" to remove the "WRITE_GENERAL" right
    When I log in as "rbelding"
    Then the following student section associations in Midgar are set correctly
    | student         | teacher              | edorg                 | enrolledInAnySection? |
    | jack.jackson    | rbelding             | East Daybreak High    | no                    |
    | carmen.ortiz    | rbelding             | Daybreak Central High | yes                   |

    #User can write to student in edorg where he has Write Access
    Given format "application/json"
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    When I change the result field "sex" to "Male"
    And I navigate to PUT "<jack.jackson URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And "sex" should be "Male"
    When I change the result field "sex" to "Female"
    And I navigate to PUT "<jack.jackson URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And "sex" should be "Female"

    #User cannot write to student in edorg where he has no Write Access
    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    When I change the result field "sex" to "Male"
    And I navigate to PUT "<carmen.ortiz URI>"
    Then I should receive a return code of 403

  Scenario: User with Write Restricted in first school and Write General in a second school
    Given I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_PUBLIC" right
    And I change the custom role of "Educator" to add the "WRITE_GENERAL" right
    And I change the custom role of "Educator" to add the "WRITE_PUBLIC" right
    When I log in as "rbelding"
    Then the following student section associations in Midgar are set correctly
    | student         | teacher              | edorg                 | enrolledInAnySection? |
    | jack.jackson    | rbelding             | East Daybreak High    | no                    |
    | carmen.ortiz    | rbelding             | Daybreak Central High | yes                   |

    #User can write to restricted fields in student in edorg where he has Restricted Write Access
    Given format "application/json"
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    When I change the result field "economicDisadvantaged" to "true"
    And I navigate to PUT "<jack.jackson URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And "economicDisadvantaged" should be "true"
    When I change the result field "economicDisadvantaged" to "false"
    And I navigate to PUT "<jack.jackson URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And "economicDisadvantaged" should be "false"
    
    #User cannot write to restricted fields in student in edorg where he has only General Write Access
    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    When I change the result field "economicDisadvantaged" to "false"
    And I navigate to PUT "<carmen.ortiz URI>"
    Then I should receive a return code of 403    
        
    #User can write to general fields in student in edorg where he has only General Write Access
    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    When I change the result field "sex" to "Male"
    And I navigate to PUT "<carmen.ortiz URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And "sex" should be "Male"
    When I change the result field "sex" to "Female"
    And I navigate to PUT "<carmen.ortiz URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And "sex" should be "Female"

  Scenario: User with Write Access in LEA and no Write Access in a school
    Given I change the custom role of "Aggregate Viewer" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Aggregate Viewer" to add the "WRITE_GENERAL" right
    And I change the custom role of "Aggregate Viewer" to add the "WRITE_PUBLIC" right
    And I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right
    And I change the custom role of "Aggregate Viewer" to add the "READ_RESTRICTED" right
    And I change the custom role of "Leader" to remove the "WRITE_GENERAL" right

    When I log in as "msmith"

    #User can write to student in an edorg in the heirarchy where he has Write Access
    Given format "application/json"
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    When I change the result field "sex" to "Male"
    And I navigate to PUT "<jack.jackson URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And "sex" should be "Male"
    When I change the result field "sex" to "Female"
    And I navigate to PUT "<jack.jackson URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And "sex" should be "Female"

    #User can write to student in a different edorg in the heirarchy where he has Write Access
    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    When I change the result field "sex" to "Male"
    And I navigate to PUT "<carmen.ortiz URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And "sex" should be "Male"
    When I change the result field "sex" to "Female"
    And I navigate to PUT "<carmen.ortiz URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And "sex" should be "Female"

  Scenario: User with no Write Access in LEA and Write Access in a school
    Given I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_PUBLIC" right
    And I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right
    And I change the custom role of "Aggregate Viewer" to add the "READ_RESTRICTED" right
    When I log in as "msmith"

    #User can write to student in an edorg where he has Write Access
    Given format "application/json"
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    When I change the result field "sex" to "Male"
    And I navigate to PUT "<jack.jackson URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And "sex" should be "Male"
    When I change the result field "sex" to "Female"
    And I navigate to PUT "<jack.jackson URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And "sex" should be "Female"

    #User cannot write to student in edorg in lea heirarchy where he has no Write Access
    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    When I change the result field "sex" to "Male"
    And I navigate to PUT "<carmen.ortiz URI>"
    Then I should receive a return code of 403


  Scenario: User with General Write Access in LEA and Restricted Write Access in a school
    And I change the custom role of "Aggregate Viewer" to add the "WRITE_GENERAL" right
    And I change the custom role of "Aggregate Viewer" to add the "WRITE_PUBLIC" right
    And I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right
    And I change the custom role of "Aggregate Viewer" to add the "READ_RESTRICTED" right
    And I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_PUBLIC" right

    When I log in as "msmith"

    #User can write to restricted fields in student in edorg where he has Restricted Write Access
    Given format "application/json"
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    When I change the result field "economicDisadvantaged" to "true"
    And I navigate to PUT "<jack.jackson URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And "economicDisadvantaged" should be "true"
    When I change the result field "economicDisadvantaged" to "false"
    And I navigate to PUT "<jack.jackson URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And "economicDisadvantaged" should be "false"
    
    #User cannot write to restricted fields in student in edorg where he has only General Write Access
    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    When I change the result field "economicDisadvantaged" to "false"
    And I navigate to PUT "<carmen.ortiz URI>"
    Then I should receive a return code of 403    
        
    #User can write to general fields in student in edorg where he has only General Write Access
    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    When I change the result field "sex" to "Male"
    And I navigate to PUT "<carmen.ortiz URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And "sex" should be "Male"
    When I change the result field "sex" to "Female"
    And I navigate to PUT "<carmen.ortiz URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And "sex" should be "Female"

  Scenario: User can write to an entity they created
    Given I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_PUBLIC" right

    When I log in as "msmith"

    Given format "application/json"
    Given I create a student entity with restricted data
    When I navigate to POST "/v1/students"
    Then I should receive a return code of 201
    When I navigate to GET the new entity
    Then I should receive a return code of 200
    When I change the result field "economicDisadvantaged" to "false"
    And I navigate to PUT the new entity
    Then I should receive a return code of 204
