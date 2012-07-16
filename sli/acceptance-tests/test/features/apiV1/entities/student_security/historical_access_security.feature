Feature:Historical Access Security Test
I want to test all combinations and permutations of accessing student data
	
Scenario Outline: Seeing data about student only if you can see the student
Given I am user <User> in IDP "SEC"
When I make an API call to get my student list
Then I should see a count of <Count>
When I make an API call to get the student <Student I can see>
Then I should receive a return code of 200
And I should receive a code of 200 when accessing data about that student
When I make an API call to get the student <Student I cannot see>
Then I should receive a return code of 403
And I should receive a code of 403 when accessing data about that student
Examples:
| User        | Count | Student I can see | Student I cannot see |
| "teacher10" | 2     | "student57"       | "student59"          |
#| "teacher11" | 0     | "NONE"            | "student58"          |  COMMENT: Need to figure out how to gracefully handle this
| "teacher12" | 2     | "student58"       | "student59"          |
| "staff20"   | 7     | "student61"       | "student62"          |
| "staff21"   | 3     | "student62"       | "student61"          |
| "staff22"   | 7     | "student60"       | "student62"          |

Scenario: Update data associations to change access for teachers and staff
Given I am user "staff13" in IDP "SEC"
When I move teacher12 to a new section
And I move student58 to a new section
And I move staff22 to a new school
And I move student61 to a new school
Then the stamper runs and completes

Scenario Outline: Teachers and Staff seeing new data from changed associations
Given I am user <User> in IDP "SEC"
When I make an API call to get my student list
Then I should see a count of <Count>
When I make an API call to get the student <Student I can see>
Then I should receive a return code of 200
And I should receive a code of 200 when accessing data about that student
When I make an API call to get the student <Student I cannot see>
Then I should receive a return code of 403
And I should receive a code of 403 when accessing data about that student
Examples:
| User        | Count | Student I can see | Student I cannot see |
| "teacher10" | 1     | "student57"       | "student58"          |
| "teacher11" | 1     | "student58"       | "student59"          |
| "teacher12" | 1     | "student58"       | "student57"          |
| "staff20"   | 1     | "student60"       | "student61"          |
| "staff21"   | 2     | "student61"       | "student60"          |
| "staff22"   | 2     | "student62"       | "student60"          |
