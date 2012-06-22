@RALLY_US2663
Feature: Sandbox usage analytics

Background:
  Given I have an open web browser

Scenario Outline: As a SLC Operator I can determine how many tenants are using the sandbox enviornment
    Given I go to the "Tenant Usage Admin Tool"
        Then I was redirected to the "Simple" IDP Login page

    When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
        Then I am displayed a list of "3" tenants
        And there is a row with tenantId <Tenant_id>
        And I am displayed a column for mongo usage and number of records
        And I am displayed the total number of tenants as "3" at the bottom of the page

    Examples:
    | Tenant_id |
    |    "T1"   |
    |    "T2"   |
    |    "T3"   |

Scenario Outline: As a SLC Operator I can determine mongo entity count and data size (in Kb) for each sandbox tenant
    Given I go to the "Tenant Usage Admin Tool"
        Then I was redirected to the "Simple" IDP Login page

    When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
        Then I am displayed a list of "3" tenants
        And there is a row with tenantId <Tenant_id>
        And the row for <Tenant_id> displays entity count <Count>
        And the row for <Tenant_id> displays size <Size>

    Examples:
    | Tenant_id | Count |   Size   |
    |    "T1"   |  "14" |  "39.06" |
    |    "T2"   |  "49" | "136.24" |
    |    "T3"   | "532" | "525.31" |

Scenario: As a SLC Operator I can determine the total mongo entity count and data size (in Kb) for all sandbox tenants
    Given I go to the "Tenant Usage Admin Tool"
        Then I was redirected to the "Simple" IDP Login page

    When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
        Then I am displayed a list of "3" tenants
        And I am displayed the total data size as "700.61"
        And I am displayed the total entity count as "595"

Scenario Outline: As a SLC Operator I can determine mongo entity count and data size (in Kb) for each collection for a single tenant
    Given I go to the "Tenant Usage Admin Tool"
        Then I was redirected to the "Simple" IDP Login page

    When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
        Then I am displayed a list of "3" tenants
        And there is a row with tenantId <Tenant_id>

    When I click on the <Tenant_id> link
        Then I am displayed a list of <Collection_Count> collections
        And next to <Collection> is the entity count <CRecords>
        And next to <Collection> is size <CSize>
        And I am displayed the total data size as <TSize>
        And I am displayed the total entity count as <TCount>

    Examples:
    | Tenant_id | Collection_Count | Collection | CRecords |  CSize  |  TSize   | TCount |
    |    "T1"   |       "4"        |  "grade"   |     "5"  |  "9.72" |  "39.06" |   "14" |
    |    "T2"   |       "8"        |  "student" |    "13"  | "36.49" | "136.24" |   "49" |
    |    "T3"   |       "9"        |  "parent"  |   "146"  | "88.32" | "525.31" |  "532" |

