@RALLY_US4835
@rc
@sandbox
Feature:  RC Integration Tests

Background:
Given I have an open web browser

@ci
Scenario: Operator triggers a bulk extract
   Given the extraction zone is empty
   When the operator triggers a bulk extract for tenant "<SANDBOX_TENANT>"

Scenario: App developer Registers, Approves, and Enables a new Installed app and Full window web app. Educators and IT Administrators can see the apps.
#Installed App
When I navigate to the Portal home page
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Register Application"
And I switch to the iframe
Then I am redirected to the Application Registration Tool page
And I have clicked to the button New
And I am redirected to a new application page
When I entered the name "NotTheAppYoureLookingFor" into the field titled "Name"
And I entered the name "Best.  Description.  Ever." into the field titled "Description"
And I entered the name "0.0" into the field titled "Version"
And I make my app an installed app
And I click on the button Submit
And I switch to the iframe
Then I am redirected to the Application Registration Tool page
And the application "NotTheAppYoureLookingFor" is listed in the table on the top
And the client ID and shared secret fields are present
And I clicked on the button Edit for the application "NotTheAppYoureLookingFor"
Then I can see the on-boarded states
When I select a state
Then I see all of the Districts
Then I check the Districts
When I click on Save
And my new apps client ID is present
And my new apps shared secret is present
Then the "NotTheAppYoureLookingFor" is enabled for Districts

#Add Bulk Extract role to IT Admin
And I exit out of the iframe
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Create Custom Roles"
And I switch to the iframe
And I edit the group "IT Administrator"
When I add the right "BULK_EXTRACT" to the group "IT Administrator"
And I hit the save button
Then I am no longer in edit mode
And I switch to the iframe
And the group "IT Administrator" contains the "right" rights "Bulk IT Administrator"

#Full Window App
And I exit out of the iframe
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Register Application"
And I switch to the iframe
Then I am redirected to the Application Registration Tool page
And I have clicked to the button New
And I am redirected to a new application page
When I entered the name "Schlemiel" into the field titled "Name"
And I entered the name "Yes, I totally made Schlemiel the painter's algorithm for SLI'" into the field titled "Description"
And I entered the name "1.0" into the field titled "Version"
And I entered the name "http://www.google.com" into the field titled "Application_URL"
And I entered the name "http://wwww.google.com" into the field titled "Redirect_URI"
And I select the app display method to "Full Window App" 
And I click on the button Submit
Then I am redirected to the Application Registration Tool page
And the application "Schlemiel" is listed in the table on the top
And the client ID and shared secret fields are present
And I clicked on the button Edit for the application "Schlemiel"
Then I can see the on-boarded states
When I select a state
Then I see all of the Districts
Then I check the Districts
When I click on Save
Then the "Schlemiel" is enabled for Districts
And I exit out of the iframe
And I click on log out
Then I should be redirected to the impersonation page
And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in

#Educator can see non-installed Apps
And I want to select "cgray" from the "SmallDatasetUsers" in automatic mode
Then I should be on Portal home page
And under My Applications, I see the following apps: "inBloom Dashboards;Schlemiel"
And under My Applications, I click on "Schlemiel"
Then my current url is "http://www.google.com/"	

@ci
Scenario: App makes an api call to retrieve a bulk extract
#Bulk Extract 
#Get a session to trigger a bulk extract
Given the testing device app key has been created
When I navigate to the API authorization endpoint with my client ID
And I was redirected to the "Simple" IDP Login page
When I submit the developer credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the impersonation login page
Then I should be redirected to the impersonation page
And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in
And I want to select "jstevenson" from the "SmallDatasetUsers" in automatic mode
Then I should receive a json response containing my authorization code
When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
Then I should receive a json response containing my authorization token

#Get bulk extract tar file
Then I request and download a bulk extract file
And there is a metadata file in the extract
And the extract contains a file for each of the following entities:
   |  entityType                            |
   |  assessment                            |
   |  assessmentFamily                      |
   |  assessmentPeriodDescriptor            |
   |  attendance                            |
   |  cohort                                |
   |  course                                |
   |  courseOffering                        |
   |  disciplineIncident                    |
   |  disciplineAction                      |
   |  educationOrganization                 |
   |  grade                                 |
   |  gradebookEntry                        |
   |  gradingPeriod                         |
   |  learningObjective                     |
   |  learningStandard                      |
   |  parent                                |
   |  program                               |
   |  reportCard                            |
   |  school                                |
   |  section                               |
   |  session                               |
   |  staff                                 |
   |  staffCohortAssociation                |
   |  staffEducationOrganizationAssociation |
   |  staffProgramAssociation               |
   |  student                               |
   |  studentAcademicRecord                 |
   |  studentAssessment                     |
   |  studentCohortAssociation              |
   |  studentCompetency                     |
   |  studentCompetencyObjective            |
   |  studentDisciplineIncidentAssociation  |
   |  studentObjectiveAssessment            |
   |  studentProgramAssociation             |
   |  studentGradebookEntry                 |
   |  studentSchoolAssociation              |
   |  studentSectionAssociation             |
   |  studentParentAssociation              |
   |  teacher                               |
   |  teacherSchoolAssociation              |
