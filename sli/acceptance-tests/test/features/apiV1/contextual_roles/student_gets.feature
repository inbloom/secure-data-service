@RALLY_US5765
@wip
Feature: Use the APi to successfully get student data while having roles over many schools

  Background: Setup for the tests
    Given the testing device app key has been created
    And I import the odin setup application and realm data
    And I have an open web browser

  @wip
  Scenario Outline: Get a student's data using various staff-student combination
    Given the only SEOA for "rbraverman" is as a "Educator" in "District 9"
    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | linda.kim            | Daybreak Central High | yes                   |
      | carmen.ortiz    | rbraverman           | Daybreak Central High | yes                   |
      | bert.jakeman    | linda.kim            | Daybreak Central High | yes                   |
      | lashawn.taite   | linda.kim            | Daybreak Central High | no                    |
      | carmen.ortiz    | linda.kim            | Daybreak Bayside High | yes                   |
      | nate.dedrick    | linda.kim            | Daybreak Bayside High | yes                   |
      | mu.mcneill      | linda.kim            | Daybreak Bayside High | yes                   |
      | matt.sollars    | rbraverman           | East Daybreak High    | yes                   |
    And "lashawn.taite" is not associated with any program that belongs to "linda.kim"
    And "lashawn.taite" is not associated with any cohort that belongs to "linda.kim"

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "<staff>" "<password>" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<student's URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response <should see restricted?> have restricted student data

    Examples:
    | staff       | password                  | student's URI           | should see restricted? |
    | sbantu      | sbantu1234                | <lashawn.taite URI>     | should                 |
    | sbantu      | sbantu1234                | <matt.sollars URI>      | should                 |
    | rbraverman  | rbraverman1234            | <matt.sollars URI>      | should not             |
    | rbraverman  | rbraverman1234            | <carmen.ortiz URI>      | should not             |
    | jstevenson  | jstevenson1234            | <matt.sollars URI>      | should                 |
    | jstevenson  | jstevenson1234            | <carmen.ortiz URI>      | should                 |
    | jstevenson  | jstevenson1234            | <lashawn.taite URI>     | should                 |
    #| jstevenson  | jstevenson1234            | <yishai.sokoll URI>     | should                 |
    | jstevenson  | jstevenson1234            | <bert.jakeman URI>      | should                 |
    | jstevenson  | jstevenson1234            | <nate.dedrick URI>      | should                 |
    | jstevenson  | jstevenson1234            | <mu.mcneill URI>        | should                 |
    | linda.kim   | linda.kim1234             | <carmen.ortiz URI>      | should not             |
    | linda.kim   | linda.kim1234             | <bert.jakeman URI>      | should not             |
    | linda.kim   | linda.kim1234             | <nate.dedrick URI>      | should not             |
    | linda.kim   | linda.kim1234             | <mu.mcneill URI>        | should not             |

  @wip
  Scenario: Staff with multiple roles in edOrg hierarchy
    Given the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | matt.sollars    | jmacey               | East Daybreak High    | yes                   |
      | jack.jackson    | jmacey               | East Daybreak High    | no                    |
      | lashawn.taite   | jmacey               | East Daybreak High    | no                    |
    And "jack.jackson" is not associated with any program that belongs to "jmacey"
    And "jack.jackson" is not associated with any cohort that belongs to "jmacey"
    And "lashawn.taite" is not associated with any program that belongs to "jmacey"
    And "lashawn.taite" is not associated with any cohort that belongs to "jmacey"

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "jmacey" "jmacey1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data

  #Commenting out since we do not support both staff and teacher context for a user(teacher)
  #TODO:Uncomment out all steps when US5787 is done
    #When I navigate to GET "<carmen.ortiz URI>"
    #Then I should receive a return code of 200
    #And the response should have general student data
    #And the response should have restricted student data
    #When I navigate to GET "<lashawn.taite URI>"
    #Then I should receive a return code of 200
    #And the response should have general student data
    #And the response should have restricted student data
    #When I navigate to GET "<bert.jakeman URI>"
    #Then I should receive a return code of 200
    #And the response should have general student data
    #And the response should have restricted student data
    #When I navigate to GET "<jack.jackson URI>"
    #Then I should receive a return code of 200
    #And the response should have general student data
    #And the response should have restricted student data

    Given format "application/json"
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403

    Given I remove the SEOA with role "Leader" for staff "jmacey" in "District 9"

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "jmacey" "jmacey1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data


    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403
    Given format "application/json"
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403
    Given format "application/json"
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 403

    Given I expire all section associations that "matt.sollars" has with "jmacey"
    And "matt.sollars" is not associated with any program that belongs to "jmacey"
    And "matt.sollars" is not associated with any cohort that belongs to "jmacey"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 403

  @wip
  Scenario: Staff with multiple roles in edOrg hierarchy, rights are unionized
    Given the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | matt.sollars    | jmacey               | East Daybreak High    | yes                   |
      | jack.jackson    | jmacey               | East Daybreak High    | no                    |
      | lashawn.taite   | jmacey               | East Daybreak High    | no                    |
    And "jack.jackson" is not associated with any program that belongs to "jmacey"
    And "jack.jackson" is not associated with any cohort that belongs to "jmacey"
    And "lashawn.taite" is not associated with any program that belongs to "jmacey"
    And "lashawn.taite" is not associated with any cohort that belongs to "jmacey"
    And I change the custom role of "Leader" to remove the "READ_GENERAL" right

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "jmacey" "jmacey1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data
    #When I navigate to GET "<carmen.ortiz URI>"
    #Then I should receive a return code of 200
    #And the response should not have general student data
    #And the response should have restricted student data
    #When I navigate to GET "<lashawn.taite URI>"
    #Then I should receive a return code of 200
    #And the response should not have general student data
    #And the response should have restricted student data
    #When I navigate to GET "<bert.jakeman URI>"
    #Then I should receive a return code of 200
    #And the response should not have general student data
    #And the response should have restricted student data
    #When I navigate to GET "<jack.jackson URI>"
    #Then I should receive a return code of 200
    #And the response should not have general student data
    #And the response should have restricted student data

  @wip
  Scenario: Student belongs to different schools
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "rbelding" "rbelding1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | rbelding             | Daybreak Central High | yes                   |
      | lashawn.taite   | rbelding             | Daybreak Central High | no                    |
      | bert.jakeman    | rbelding             | Daybreak Central High | no                    |
    And "lashawn.taite" is not associated with any program that belongs to "rbelding"
    And "lashawn.taite" is not associated with any cohort that belongs to "rbelding"
    And "bert.jakeman" is not associated with any program that belongs to "rbelding"
    And "bert.jakeman" is not associated with any cohort that belongs to "rbelding"

    Given format "application/json"
    #When I navigate to GET "<matt.sollars URI>"
    #Then I should receive a return code of 200
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403

    #TODO:lashawn.taite should return 200 when US5787 is done
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 403
    #And the response should have general student data
    #And the response should have restricted student data

    Given I change the type of "rbelding" to "staff"
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "rbelding" "rbelding1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code

    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
    #TODO:bert.jakeman should return 403 when US5787 is done
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data

    Given I remove the teacherSectionAssociation for "rbelding"

    Given format "application/json"
    #TODO:lashawn.taite and matt.sollars should return 200 when US5787 is done
    #When I navigate to GET "<matt.sollars URI>"
    #Then I should receive a return code of 200
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    #TODO:carmen.ortiz should return 403 when US5787 is done
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    When I navigate to GET "<mu.mcneill URI>"
    Then I should receive a return code of 403

  @wip
  Scenario: Aggregate Viewer can not access student data
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "msmith" "msmith1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have restricted student data
    And the response should have general student data
    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403
    Given format "application/json"
    When I navigate to GET "<mu.mcneill URI>"
    Then I should receive a return code of 403

    Given I change all SEOAs of "msmith" to the edorg "East Daybreak High"
    And I change the custom role of "Aggregate Viewer" to add the "READ_RESTRICTED" right
    And I change the custom role of "Leader" to remove the "READ_RESTRICTED" right

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "msmith" "msmith1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403

    Given I remove the SEOA with role "Leader" for staff "msmith" in "East Daybreak High"
    And I change the custom role of "Aggregate Viewer" to remove the "READ_RESTRICTED" right

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "msmith" "msmith1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 403

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403

    Given format "application/json"
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 403

  @wip
  Scenario: Student belongs to schools in different LEAs
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "mgonzales" "mgonzales1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 200

    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403

  @wip
  Scenario: User gets data based on roles in SEOA, even if user has more roles defined in IDP
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "xbell" "xbell1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403

  @wip
  Scenario: User gets additional data of new role if a seoa is added to match additional role defined in IDP

    Given I add a SEOA for "xbell" in "District 9" as a "Leader"
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "xbell" "xbell1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls
    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have restricted student data
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And the response should have restricted student data
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403


  @wip
  Scenario: Teacher can only access students associated with her/him.
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | linda.kim            | Daybreak Central High | yes                   |
      | bert.jakeman    | linda.kim            | Daybreak Central High | yes                   |
      | lashawn.taite   | linda.kim            | Daybreak Central High | no                    |
      | carmen.ortiz    | linda.kim            | Daybreak Bayside High | no                    |
      | nate.dedrick    | linda.kim            | Daybreak Bayside High | no                    |
      | mu.mcneill      | linda.kim            | Daybreak Bayside High | yes                   |
    And "lashawn.taite" is not associated with any program that belongs to "linda.kim"
    And "lashawn.taite" is not associated with any cohort that belongs to "linda.kim"
    And "nate.dedrick" is not associated with any program that belongs to "linda.kim"
    And "nate.dedrick" is not associated with any cohort that belongs to "linda.kim"

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<mu.mcneill URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 403
    Given format "application/json"
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403

    Given I remove the SEOA with role "Educator" for staff "linda.kim" in "Daybreak Bayside High"

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<mu.mcneill URI>"
    Then I should receive a return code of 403

    Given I remove the school association with student "bert.jakeman" in tenant "Midgar"
    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403

    Given I remove all SEOAs for "linda.kim" in tenant "Midgar"
    Given I add a SEOA for "linda.kim" in "IL-DAYBREAK" as a "Educator"

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<mu.mcneill URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

  @wip
  Scenario: Educators can only access students associated with them
    Given the only SEOA for "rbraverman" is as a "Educator" in "District 9"
    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | rbraverman           | Daybreak Central High | yes                   |
      | matt.sollars    | rbraverman           | East Daybreak High    | no                    |
    And "matt.sollars" is not associated with any program that belongs to "rbraverman"
    And "matt.sollars" is not associated with any cohort that belongs to "rbraverman"

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "rbraverman" "rbraverman1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 403
    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given I expire all section associations that "carmen.ortiz" has with "rbraverman"
    And "carmen.ortiz" is not associated with any program that belongs to "rbraverman"
    And "carmen.ortiz" is not associated with any cohort that belongs to "rbraverman"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403
