Feature:  Sandbox DevChecklist Tests

Background:
Given I have an open web browser
When I navigate to the Portal home page

Scenario:  Developer Checklist
When I selected the realm "Shared Learning Collaborative"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "<DEVELOPER_EMAIL>" "<DEVELOPER_EMAIL_PASS>" for the "Simple" login page  
Then I should be on Portal home page
And I should see "Developer Home"
And I should see Developer Checklist
#TODO do this check for new developers
#And Nothing is checked off in Developer Checklist
#Provision a LZ
And I should see a check in "Provision a Landing Zone" 
And I should see a check in "Upload Data"
And I click on Don't show this again 
And I see an Apply button