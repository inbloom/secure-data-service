@US570
Feature: Admin Tool Declarative Administrative Permissions

As a SLI Operator/Administrator, I want to login to the SLI Default Roles Admin Page,
so I could get an information about the default roles in SLI and their permissions.

As a SLI Operator/Administrator, I want the SLI Default Roles Admin Page to be
protected so only I can get information about the default roles in SLI and their permissions.
 
Scenario: Valid SLI IDP user login to SLI Default Roles Admin Page
Given I have an open web browser 
And the following collections are empty in datastore:
        | collectionName              |
        | securityEvent               |

And I am not authenticated to SLI IDP
And I have tried to access the SLI Default Roles Admin Page
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
Then I am now authenticated to SLI IDP
And I should be redirected to the SLI Default Roles Admin Page
And I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | securityEvent               | 1     |

Scenario: Invalid SLI IDP user login to SLI Default Roles Admin Page
 
Given I have an open web browser
And the following collections are empty in datastore:
        | collectionName              |
        | securityEvent               |
And I am not authenticated to SLI IDP
And I have tried to access the SLI Default Roles Admin Page
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "InvalidJohnDoe" "badpass" for the "Simple" login page
Then I am informed that authentication has failed
And I do not have access to the SLI Default Roles Admin Page
And I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | securityEvent               | 1     |

Scenario: Go to SLI Default Roles Admin Page, with a role other than SLI IT Administrator when authenticated to SLI IDP
Given I have an open web browser
And the following collections are empty in datastore:
        | collectionName              |
        | securityEvent               |
And I am not authenticated to SLI IDP
And I have tried to access the SLI Default Roles Admin Page
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "leader" "leader1234" for the "Simple" login page
Then I should get a message that I am not authorized
And I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | securityEvent               | 1     |

 @wip
Scenario:  SLI Default Roles Admin Page logout
 
Given I am authenticated to SLI IDP
And I have navigated to the SLI Default Roles Admin Page
When I click on the Logout link
Then I am no longer authenticated to SLI IDP
And I am redirected to the SLI-IDP Login Page
