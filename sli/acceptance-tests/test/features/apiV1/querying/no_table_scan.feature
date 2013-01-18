Feature: As an SLI application, I want to hit every resource to ensure that we are not causing table scans.

  Scenario Outline:
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
      And format "application/vnd.slc+json"
      And parameter "<parameter>" is "<value>"
      When I navigate to GET for each resource available
      Then I should not encounter any table scans
    Examples:
      | parameter                | value             |
      | offset                   | 0                 |
      | limit                    | 100               |
      | includeFields            | id                |
      | excludeFields            | links             |
      | sortBy                   | id                |
      | sortOrder                | ascending         |
      | views                    | assessments       |
      | includeCustom            | true              |
      | includeCalculatedValues  | true              |
      | includeAggregates        | true              |
      # Non-sensical
      | offset                   | true              |
      | limit                    | false             |
      | includeFields            | -1                |
      | excludeFields            | -1                |
      | sortBy                   | -1                |
      | sortOrder                | -1                |
      | views                    | -1                |
      | includeCustom            | -1                |
      | includeCalculatedValues  | -1                |
      | includeAggregates        | -1                |
      # Renable this last step when /resource?x=y gets rewritten for Staff
      | foo                      | bar               |

  # Teacher context
  Scenario Outline:
    Given I am logged in using "cgrayadmin" "cgrayadmin1234" to realm "IL"
      And format "application/vnd.slc+json"
      And parameter "<parameter>" is "<value>"
      When I navigate to GET for each resource available
      Then I should not encounter any table scans
    Examples:
      | parameter                | value             |
      | offset                   | 0                 |
      | limit                    | 100               |
      | includeFields            | foobar            |
      | excludeFields            | barfoo            |
      | sortBy                   | foo               |
      | sortOrder                | ascending         |
      | views                    | foobar            |
      | includeCustom            | true              |
      | includeCalculatedValues  | true              |
      | includeAggregates        | true              |
      # Non-sensical
      | offset                   | true              |
      | limit                    | false             |
      | includeFields            | -1                |
      | excludeFields            | -1                |
      | sortBy                   | -1                |
      | sortOrder                | -1                |
      | views                    | -1                |
      | includeCustom            | -1                |
      | includeCalculatedValues  | -1                |
      | includeAggregates        | -1                |
      # Renable this last step when /resource?x=y gets rewritten for Teachers
      | foo                      | bar               |
