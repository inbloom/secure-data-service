@RALLY_US4835
@rc
Feature:  RC Portal Startup Scripts
As someone who needs to test the portal, I wish I didn't have to hit every page of the portal for it to compile its own assests after it's already been deployed.
Note: If the portal was re-written in Rails, this would not be a problem and you could delete this test 

Scenario: Hit every portal page to compile its assests

    Given I have an open web browser
    When I hit the Portal home page
    When I see the realm selector I authenticate to "inBloom"
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
    Then the Portal home page should be compiled
    When I hit the Portal Admin page
    Then the Portal Admin page should be compiled
