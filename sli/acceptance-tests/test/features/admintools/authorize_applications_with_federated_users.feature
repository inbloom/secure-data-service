Feature: Authorize Applications with Federated Users

Background:
  Given I have an open browser

Scenario: Prepare Custom Roles (set up)
  # Reset to default state
  Given I am a valid tenant-level realm administrator
   When I navigate to the Custom Role Mapping page
   Then I should get to the correct Custom Role Mapping Page
   When I click on the Reset to Defaults button
   Then the page should be reset back to default

  # Create custom role
  When I add a new role group
  Then I should see the new role group

Scenario: Developer creates new application (set up data)
  Given I am a valid inBloom developer
    And I am managing my applications
    And I want to create a new application
    And I create new application "Boyne"
#  This step might need to be revisited
   Then application "Boyne" should be created

Scenario: SLC Operator accepts application registration request (set up data)
  Given I am a valid inBloom operator
    And I am managing my applications
    And I should see all applications and new application "Boyne"
   When the application "Boyne" is approved
   Then the 'Approve' button is disabled for the application

Scenario: Developer registers application (set up data)
  Given I am a valid inBloom developer
    And I am managing my applications
   Then I should see application "Boyne" as In Progress
   When I enable the education Organizations for the application
   Then "Boyne" is enabled for "208" education organizations

Scenario: NY Hosted User Authorizes App (set up data)
  Given I am a valid NY Hosted User
    And I am managing my application authorizations
   Then I should see application "Boyne" as Not Approved
   When I authorize the application for "New York State Education System"
   Then the application status should be "8 EdOrg(s)"

Scenario: Federated SEA Admin Approves application for SEA only (set up data)
  Given I am a valid tenant-level IT administrator
    And I navigate to the application authorization page
   Then I should see application "Boyne" as Not Approved
   When I authorize the application for "Illinois State Board of Education"
   Then the application status should be "1 EdOrg(s)"

Scenario: Linda Kim encounters Access Denied Message when attempting to access Application Authorization Tool using default Educator role
  Given I am a valid Educator
    And I go to application authorization page
   Then I should see the error message

Scenario: Create Application Authorizer Staff Education Organization Association (set up)
  When I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
   And a staffEducationOrgAssignmentAssociation is created for user "linda.kim" with role "Application Authorizer" for education organization "South Daybreak Elementary" in tenant "Midgar"
   And a staffEducationOrgAssignmentAssociation is created for user "linda.kim" with role "Educator" for education organization "Sunset Central High School" in tenant "Midgar"

Scenario: Linda Kim encounters Access Denied Message when attempting to access Application Authorization Tool using default Educator role
  Given I am a valid Educator
    And I navigate to the application authorization page
   Then I should see application "Boyne" as Not Approved
   When I authorize the application for "Illinois State Board of Education"
   Then the application status should be "1 EdOrg(s)"
   When I de-authorize the application for "Illinois State Board of Education"
   Then I should see application "Boyne" as Not Approved

Scenario: District admin allows access to sample app
  Given I am a valid district-level administrator
    And I am managing my application authorizations
   Then I see application "Sample"
    And I see application "SDK Sample App (CI)"

Scenario: Can access the Sample App
  Given I have an open web browser
   When I navigate to the sample app page
    And I select "Illinois Sunset School District 4526" from the dropdown and click go
   When I submit the credentials "manthony" "manthony1234" for the "Simple" login page
    And I am redirected to the sample app home page

