@RALLY_US2663
Feature: Sandbox usage analytics

Background:
  Given I have an open web browser

@wip
Scenario: As a SLC Operator I can determine how many tenants are using the sandbox environment,
the total mongo entity count and data size (in Kb) for all sandbox tenants, and mongo entity
count and data size (in Kb) for each sandbox tenant.
    Given I go to the "Tenant Usage Admin Tool"
        Then I was redirected to the "Simple" IDP Login page

    When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
        Then I see a list of "3" tenants
        And I see the total number of tenants as "3" at the bottom of the page
        And I see the total data size is greater than "0"
        And I see the total entity count is "597"
        And I see a row for tenantId <Tenant_id> with entity count <Count>, greater than size <Size>
    | Tenant_id      | Count | Size |
    | T1@slidev.org  |  15   | 0    |
    | T2@slidev.org  |  49   | 0    |
    | T3@slidev.org  | 533   | 0    |

@wip
Scenario: As a SLC Operator I can determine mongo entity count and data size (in Kb) for each collection for a single tenant
    Given I go to the "Tenant Usage Admin Tool"
        Then I was redirected to the "Simple" IDP Login page

    When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
        Then I see a list of "3" tenants

    When I click on the <Tenant_id> link
    | Tenant_id     | Collection_Count | Collection | CRecords | CSize | TCount | TSize |
    | T1@slidev.org |        4         |   grade    |      6   | 0     |    15  | 0     |
    | T2@slidev.org |        8         |   student  |     13   | 0     |    49  | 0     |
    | T3@slidev.org |        9         |   parent   |    146   | 0     |   533  | 0     |
        Then I see a list of <Collection_Count> collections
        And I see a row for collection <Collection> with entity count <CRecords>, greater than size <CSize>
        And I see the total for the tenant is count <TCount>, greater than size <TSize>

