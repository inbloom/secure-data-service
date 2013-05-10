@RALLY_US5716

@wip
Feature: As an bulk extract user, I will be able to cleanup bulk extract file with the script

  Background: Prepare data and database
    Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And I have a fake bulk extract tar file for the following tenants and different dates:
     |  tenant     |    app   |          date              |    Edorg    |
     |  Midgar     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
     |  Midgar     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
     |  Midgar     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |
     |  Hyrule     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
     |  Hyrule     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
     |  Hyrule     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |
  @wip
  Scenario: I clean up all bulk extract file for the tenant
    When I execute cleanup script for tenant:'Midgar', edorg:'', date:'', path:''
    Then I should not see the following tenant bulk extract file:
      |  tenant     |    app   |          date              |    Edorg    |
      |  Midgar     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
      |  Midgar     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
      |  Midgar     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |
    And I should see the following tenant bulk extracg file:
      |  tenant     |    app   |          date              |    Edorg    |
      |  Hyrule     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
      |  Hyrule     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
      |  Hyrule     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |
  @wip
  Scenario: I clean up all bulk extract file for a Edorg in tenant
    When I execute cleanup script for tenant:'Midgar', edorg:'Daybreak', date:'', path:''
    Then I should not see the following tenant bulk extract file:
      |  tenant     |    app   |          date              |    Edorg    |
      |  Midgar     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
      |  Midgar     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |
    And I should see the following tenant bulk extracg file:
      |  tenant     |    app   |          date              |    Edorg    |
      |  Midgar     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
      |  Hyrule     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
      |  Hyrule     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
      |  Hyrule     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |
  @wip
  Scenario: I clean up all bulk extract file for a Edorg in tenant up to certain date
    When I execute cleanup script for tenant:'Midgar', edorg:'Daybreak', date:'2013-04-08T14:07:43.870Z', path:''
    Then I should not see the following tenant bulk extract file:
      |  tenant     |    app   |          date              |    Edorg    |
      |  Midgar     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
      |  Midgar     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |
    And I should see the following tenant bulk extracg file:
      |  tenant     |    app   |          date              |    Edorg    |
      |  Midgar     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
      |  Hyrule     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
      |  Hyrule     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
      |  Hyrule     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |
  @wip
  Scenario: I clean up all bulk extract file for a tenant up to certain date
    When I execute cleanup script for tenant:'Midgar', edorg:'', date:'2013-05-09T14:07:43.870Z', path:''
    Then I should not see the following tenant bulk extract file:
      |  tenant     |    app   |          date              |    Edorg    |
      |  Midgar     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
      |  Midgar     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
      |  Midgar     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |
    And I should see the following tenant bulk extracg file:
      |  tenant     |    app   |          date              |    Edorg    |
      |  Hyrule     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
      |  Hyrule     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
      |  Hyrule     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |

    @wip
  Scenario: I clean up a bulk extract file for a tenant
    When I execute cleanup script for tenant:'Midgar', edorg:'', date:'', path:"app-Midgar-Daybreak-2013-04-08T14:07:43.870Z"
    And I see "app-Midgar-Daybreak-2013-04-08T14:07:43.870Z" is in the output
    When I execute cleanup script for tenant:'Midgar', edorg:'', date:'', path:"app-Hyrule-Daybreak-2013-04-08T14:07:43.870Z"
    And I should see error message
    Then I should not see the following tenant bulk extract file:
      |  tenant     |    app   |          date              |    Edorg    |
      |  Midgar     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
    And I should see the following tenant bulk extracg file:
      |  tenant     |    app   |          date              |    Edorg    |
      |  Midgar     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
      |  Midgar     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |
      |  Hyrule     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
      |  Hyrule     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
      |  Hyrule     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |

  @wip
  Scenario: I clean up all bulk extract file with incorrect input
    When I execute cleanup script for tenant:'Midgar', edorg:'Daybreak', date:'2013-04-08T14:07:43.870Z', path:'app-Midgar-Daybreak-2013-04-08T14:07:43.870Z'
    Then I should see error message
    When I execute cleanup script for tenant:'', edorg:'Daybreak', date:'2013-04-08T14:07:43.870Z', path:'test-'
    Then I should see error message
    And I should see the following tenant bulk extracg file:
      |  tenant     |    app   |          date              |    Edorg    |
      |  Midgar     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
      |  Midgar     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
      |  Midgar     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |
      |  Hyrule     |    a1    | 2013-04-08T14:07:43.870Z   |    Daybreak |
      |  Hyrule     |    a1    | 2013-05-08T14:07:43.870Z   |    Sunset   |
      |  Hyrule     |    a2    | 2013-03-08T14:07:43.870Z   |    Daybreak |

  Scenario:
    Then I clean up the cleanup script test data