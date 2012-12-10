Feature: Test version migration
    
    Background: Nothing yet
        
        Scenario: API requests for an educationOrganization get transformed using the test transformation
            Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
            And format "application/vnd.slc+json"  
            When I navigate to GET "/<EDORG URI>/<South Daybreak Elementary ID>"
            Then "newField" should be "migratedValue"
