@RALLY_US5865

Feature: Other Urls not accessible by federated users in admin tools
As an Federated User, I want to test some urls which are not accessible

Scenario: Federated Users cannot access non-application authorization pages
	Given I have an open web browser
	When I hit the Admin Application Authorization Tool
	And I select "Illinois Daybreak School District 4529" from the dropdown and click go
	And I was redirected to the "Simple" IDP Login page
	When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
	When I try to authenticate on the "custom_roles" Tool
	Then I get message that I am not authorized   	
	When I try to authenticate on the "users" Tool
	Then I get message that I am not authorized   
	When I try to authenticate on the "realm_management" Tool
	Then I get message that I am not authorized   
	When I try to authenticate on the "admin_delegations" Tool
	Then I get message that I am not authorized   
	When I try to authenticate on the "landing_zone" Tool
	Then I get message that I am not authorized   
	When I try to authenticate on the "change_passwords" Tool
	Then I get message that I am not authorized   		
	#When I try to authenticate on the "forgot_passwords" Tool
	#Then I get message that I am not authorized   
	When I try to authenticate on the "account_managements" Tool
	Then I get message that I am not authorized   
	When I try to authenticate on the "lea" Tool
	Then I get message that I am not authorized   
	When I try to authenticate on the "changePassword" Tool
	Then I get message that I am not authorized   
	#When I try to authenticate on the "forgotPassword" Tool
	#Then I get message that I am not authorized   
	
	
	
	
	


    
    
	