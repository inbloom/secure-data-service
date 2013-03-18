@US5481
Feature: Admininstrating role-based access to bulking
    
    Scenario: Make sure it works
        Given I have an open web browser
        When I navigate to the Custom Role Mapping Page
        And I was redirected to the "Simple" IDP Login page
        When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
        Then I have navigated to my Custom Role Mapping Page
        Then I should not see right "BULK_EXTRACT" on any existing role        
        When I click on the Add Group button
        And I type the name "Balrogs" in the Group name textbox
        When I add the right "BULK_EXTRACT" to the group "Balrogs"   
        And I add the role "Balrog" to the group "Balrogs"
        And I hit the save button
        And the group "Balrogs" contains the "right" rights "BULK_EXTRACT"
        
        # Now test generation and use of the endpoint
        Given I used the long lived session token generator script to create a token for user "manthony" with role "Balrog" in tenant "Midgar"  for realm "IL-Sunset" that will expire in "300" seconds with client_id "AT1k3PdHzX" 
        Then I should see that my role is "Balrog"
        When I navigate to GET "/v1/sections"
        And I should receive a return code of 403
        When I navigate to GET "/bulk/extract"
        Then I get expected zip downloaded