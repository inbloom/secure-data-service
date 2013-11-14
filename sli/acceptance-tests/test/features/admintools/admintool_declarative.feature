@US2212
Feature: Admin Tool Declarative Administrative Permissions

As a SLI Operator/Administrator, I want to login to the default Admin Page,
so I could get an information about the default roles in SLI and their permissions.

As a SLI Operator/Administrator, I want the default Admin Page to be
protected so only I can get information about the default roles in SLI and their permissions.
 
Scenario: Valid SLI IDP user login to default Admin Page
Given I have an open web browser 
And the following collections are empty in sli datastore:
        | collectionName              |
        | securityEvent               |

And I am not authenticated to SLI IDP
And I have tried to access the default Admin Page
And I select "inBloom" from the dropdown and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "slcoperator-email@slidev.org" "slcoperator-email1234" for the "Simple" login page
Then I am now authenticated to SLI IDP
And I should be redirected to the default Admin Page
And I should see following map of entry counts in the corresponding sli db collections:
        | collectionName              | count |
        | securityEvent               | 2     |

Scenario: Invalid SLI IDP user login to default Admin Page
 
Given I have an open web browser
And the following collections are empty in sli datastore:
        | collectionName              |
        | securityEvent               |
And I am not authenticated to SLI IDP
And I have tried to access the default Admin Page
And I select "inBloom" from the dropdown and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "InvalidJohnDoe" "badpass" for the "Simple" login page
Then I am informed that authentication has failed
And I do not have access to the default Admin Page
And I should see following map of entry counts in the corresponding sli db collections:
        | collectionName              | count |
        | securityEvent               | 1     |

@DE2728
Scenario: Go to default Admin Page, with an invalid user who does not have any roles
# hijacked this test to test an user with no roles defined in LDAP since we removed
# leader group from our LDAP
Given I have an open web browser
And the following collections are empty in sli datastore:
        | collectionName              |
        | securityEvent               |
And I am not authenticated to SLI IDP
And I have tried to access the default Admin Page
And I select "inBloom" from the dropdown and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "leader" "leader1234" for the "Simple" login page
Then I am informed that authentication has failed
And I will get an error message that "User account is in invalid mode"
And I do not have access to the default Admin Page
And I should see following map of entry counts in the corresponding sli db collections:
        | collectionName              | count |
        | securityEvent               | 1     |

