@integration
@RALLY_US215
  Feature: User being denied access to sample app

Scenario: Can access the Sample App
  Given I have an open web browser
  When I navigate to the sample app page
  And I was redirected to the realmchooser page
  And I selected the realm "Illinois Sunset School District 4526"
  When I submit the credentials "manthony" "manthony1234" for the "OpenAM" login page
  And I am redirected to the sample app home page

Scenario: District admin denies access to sample app
  Given I am an authenticated District Super Administrator for "Sunset School District"
  And I am logged into the Application Authorization Tool
  And I see an application "SDK Sample App (CI)" in the table
  And in Status it says "Approved"
  And I click on the "Deny" button next to it
  And I am asked 'Do you really want deny access to this application of the district's data'
  When I click on Ok
  Then the application is denied to use data of "Sunset School District"
  And I see an application "Sample" in the table
  And in Status it says "Approved"
  And I click on the "Deny" button next to it
  And I am asked 'Do you really want deny access to this application of the district's data'
  When I click on Ok
  Then the application is denied to use data of "Sunset School District"


Scenario: Can no longer access the sample app
  Given I have an open web browser
  When I navigate to the sample app page
  And I was redirected to the realmchooser page
  And I selected the realm "Illinois Sunset School District 4526"
  When I submit the credentials "manthony" "manthony1234" for the "OpenAM" login page
  Then I am denied access to the sample app home page

Scenario: District admin allows access to sample app
  Given I am an authenticated District Super Administrator for "Sunset School District"
  And I am logged into the Application Authorization Tool
  And I see an application "Sample" in the table
  And in Status it says "Not Approved"
  And I click on the "Approve" button next to it
  And I am asked 'Do you really want this application to access the district's data'
  When I click on Ok
  Then the application is authorized to use data of "Sunset School District"
  And I see an application "SDK Sample App (CI)" in the table
  And in Status it says "Not Approved"
  And I click on the "Approve" button next to it
  And I am asked 'Do you really want this application to access the district's data'
  When I click on Ok
  Then the application is authorized to use data of "Sunset School District"

Scenario: Can access the Sample App
  Given I have an open web browser
  When I navigate to the sample app page
  And I was redirected to the realmchooser page
  And I selected the realm "Illinois Sunset School District 4526"
  When I submit the credentials "manthony" "manthony1234" for the "Simple" login page
  And I am redirected to the sample app home page
