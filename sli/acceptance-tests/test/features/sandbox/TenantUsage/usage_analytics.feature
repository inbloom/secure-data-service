@RALLY_US2663
Feature: Sandbox usage analytics

Background:
  Given I have an open web browser

Scenario: As a SLC Operator I can determine how many tenants are using the sandbox environment,
the total mongo entity count and data size (in Kb) for all sandbox tenants, and mongo entity
count and data size (in Kb) for each sandbox tenant.
    Given I go to the "Tenant Usage Admin Tool"
        Then I was redirected to the "Simple" IDP Login page

    When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
        Then I see a list of "3" tenants
        And I see the total number of tenants as "3" at the bottom of the page
        And I see the total data size is approximately "600"
        And I see the total entity count is "597"
        And I see a row for tenantId <Tenant_id> with entity count <Count>, approximate size <Size>
    | Tenant_id | Count |  Size |
    |    T1   |  15 |  20 |
    |    T2   |  49 | 100 |
    |    T3   | 533 | 500 |


Scenario: As a SLC Operator I can determine mongo entity count and data size (in Kb) for each collection for a single tenant
    Given I go to the "Tenant Usage Admin Tool"
        Then I was redirected to the "Simple" IDP Login page

    When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
        Then I see a list of "3" tenants

    When I click on the <Tenant_id> link
    | Tenant_id | Collection_Count | Collection | CRecords | CSize | TCount | TSize |
    |     T1    |        4         |   grade    |      6   |   7   |    15  |   30  |
    |     T2    |        8         |   student  |     13   |  30   |    49  |  120  |
    |     T3    |        9         |   parent   |    146   |  80   |   533  |  400  |
        Then I see a list of <Collection_Count> collections
        And I see a row for collection <Collection> with entity count <CRecords>, approximate size <CSize>
        And I see the total for the tenant is count <TCount>, approximate size <TSize>

