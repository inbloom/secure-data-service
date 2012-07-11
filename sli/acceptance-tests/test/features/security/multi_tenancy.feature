@sandbox
Feature: Multi Tenancy Testing
As a system supporting Multi-tenancy, I want to make sure there is no way to cross tenant boundries

Scenario Outline: Get List of Authorized Apps

Given I am logged in using "linda.kim" "linda.kim1234" to realm <Realm>
When I make an API call to get my available apps
Then I see an app named <App Name>
Examples:
| Realm   | App Name       |
| "Zork"  | "Phzorked"     |
| "Chaos" | "Chaos Monkey" |

Scenario: Editing Data in one tenant not visible to other

Given I am logged in using "linda.kim" "linda.kim1234" to realm "Zork"
When I change my Linda Kim's yearsOfPriorTeachingExperience to "9"
Then My Linda Kim has "9" yearsOfPriorTeachingExperience
When I am logged in using "linda.kim" "linda.kim1234" to realm "Chaos"
Then My Linda Kim has "0" yearsOfPriorTeachingExperience