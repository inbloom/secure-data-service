@RALLY_US209
@RALLY_US210
Feature: Sort and page API results

Scenario: Check default limit of 50
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
  Given format "application/json"
  When I navigate to GET "/v1/schools/8cc0a1ac-ccb5-dffc-1d74-32964722179b%2c92d6d5a0-852c-45f4-907a-912752831772%2ca189b6f2-cc17-4d66-8b0d-0478dcf0cdfb%2cec2e4218-6483-4e9c-8954-0aecccfd4731/courses"
  Then I should receive a collection with 50 elements

Scenario: Sorting a collection of student school association links by entryGradeLevel, ascending
	Given I am logged in using "jpratt" "jpratt1234" to realm "NY"
		And format "application/json"
		And parameter "sortBy" is "entryGradeLevel"
		And parameter "sortOrder" is "ascending"
	When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations"
	Then I should receive a collection
		And the link at index 0 should point to an entity with id "455a39aa-f950-4dca-a3df-9a87f990054b"
		And the link at index 1 should point to an entity with id "f05029c8-c4e8-427e-b2a4-cc89c74650a1"
		And the link at index 2 should point to an entity with id "8f423e3d-a206-4bbe-a3ab-aa37e95ba3e5"
		And the link at index 3 should point to an entity with id "69b49ecc-86f6-44b4-be99-43e9041dee2e"

Scenario: Sorting a collection of entities obtained via a hop using a (nested) field, descending
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    Given format "application/json"
    And parameter "sortBy" is "name.firstName"
    And parameter "sortOrder" is "descending"
    When I navigate to GET "/v1/educationOrganizations/<'Illinois State Ed-org' ID>/staffEducationOrgAssignmentAssociations/staff"
    Then I should receive a return code of 200
    And I should receive a collection
    And the link at index 0 should have "name.firstName" equal to "Rick"
    And the link at index 1 should have "name.firstName" equal to "Michael"
    And the link at index 2 should have "name.firstName" equal to "Mark"
    And the link at index 3 should have "name.firstName" equal to "Christopher"

Scenario: Sorting a collection of full student school association entities
	Given I am logged in using "jpratt" "jpratt1234" to realm "NY"
	Given format "application/json"
		And parameter "sortBy" is "entryGradeLevel"
		And parameter "sortOrder" is "descending"
	When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations"
	Then I should receive a collection
		And the link at index 0 should have "entryGradeLevel" equal to "Tenth grade"
		And the link at index 1 should have "entryGradeLevel" equal to "Sixth grade"
		And the link at index 2 should have "entryGradeLevel" equal to "Seventh grade"
		And the link at index 3 should have "entryGradeLevel" equal to "Eighth grade"

Scenario: Validate PII cannot be sorted against
  Given I am logged in using "jpratt" "jpratt1234" to realm "NY"
    Given format "application/json"
    And parameter "sortBy" is "name.firstName"
    And parameter "sortOrder" is "ascending"
    When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations/students"
    Then I should receive a return code of 400

Scenario: Sorting a collection of staff through a hop (from an ed-org)
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    Given format "application/json"
    And parameter "sortBy" is "name.firstName"
    And parameter "sortOrder" is "ascending"
    When I navigate to GET "/v1/educationOrganizations/<'Illinois State Ed-org' ID>/staffEducationOrgAssignmentAssociations/staff"
    Then I should receive a return code of 200
    And I should receive a collection
    And the link at index 0 should have "name.firstName" equal to "Christopher"
    And the link at index 1 should have "name.firstName" equal to "Mark"
    And the link at index 2 should have "name.firstName" equal to "Michael"
    And the link at index 3 should have "name.firstName" equal to "Rick"

Scenario: Paging request the first two results from an API request
  Given I am logged in using "jpratt" "jpratt1234" to realm "NY"
    Given format "application/json"
		And parameter "sortBy" is "entryGradeLevel"
		And parameter "sortOrder" is "ascending"
	When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations"
	Then I should receive a collection with 4 elements
 	Given parameter "offset" is "0"
		And parameter "limit" is "2"
	When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations"
	Then I should receive a collection with 2 elements
		And the link at index 0 should point to an entity with id "455a39aa-f950-4dca-a3df-9a87f990054b"
		And the link at index 1 should point to an entity with id "f05029c8-c4e8-427e-b2a4-cc89c74650a1"
		And the header "TotalCount" equals 4
		And the a next link exists with offset equal to 2 and limit equal to 2
		And the a previous link should not exist

Scenario: Paging request the first two results from an API request via a hop
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    Given format "application/json"
		And parameter "sortBy" is "name.firstName"
		And parameter "sortOrder" is "ascending"
	When I navigate to GET "/v1/educationOrganizations/<'Illinois State Ed-org' ID>/staffEducationOrgAssignmentAssociations/staff"
	Then I should receive a collection with 4 elements
 	Given parameter "offset" is "0"
		And parameter "limit" is "1"
	When I navigate to GET "/v1/educationOrganizations/<'Illinois State Ed-org' ID>/staffEducationOrgAssignmentAssociations/staff"
	Then I should receive a collection with 1 elements
		And the link at index 0 should point to an entity with id "b4c2a73f-336d-4c47-9b47-2d24871eef96"
		And the header "TotalCount" equals 4
		And the a next link exists with offset equal to 1 and limit equal to 1
		And the a previous link should not exist

Scenario: Request the last and middle page of results from a API request
  Given I am logged in using "jpratt" "jpratt1234" to realm "NY"
    Given format "application/json"
		And parameter "sortBy" is "entryGradeLevel"
		And parameter "sortOrder" is "ascending"
		And parameter "offset" is "2"
		And parameter "limit" is "2"
	When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations"
	Then I should receive a collection with 2 elements
		And the link at index 0 should point to an entity with id "8f423e3d-a206-4bbe-a3ab-aa37e95ba3e5"
		And the link at index 1 should point to an entity with id "69b49ecc-86f6-44b4-be99-43e9041dee2e"
		And the header "TotalCount" equals 4
		And the a previous link exists with offset equal to 0 and limit equal to 2
	Given parameter "offset" is "1"
		And parameter "limit" is "2"
	When I navigate to GET "/v1/schools/<'Dawn Elementary School' ID>/studentSchoolAssociations"
	Then I should receive a collection with 2 elements
			And the link at index 0 should point to an entity with id "f05029c8-c4e8-427e-b2a4-cc89c74650a1"
			And the link at index 1 should point to an entity with id "8f423e3d-a206-4bbe-a3ab-aa37e95ba3e5"
			And the header "TotalCount" equals 4
			And the a previous link exists with offset equal to 0 and limit equal to 2
			And the a next link exists with offset equal to 3 and limit equal to 2

@DE1873 @DE1906 @DE1910
Scenario: Paging tests from the context of a teacher
	Given I am logged in using "cgray" "cgray1234" to realm "IL"
    Given format "application/json"
    And parameter "sortBy" is "studentUniqueStateId"
    And parameter "sortOrder" is "ascending"
    And parameter "offset" is "20"
    And parameter "limit" is "5"
    When I navigate to GET "/v1/sections/<'Sec 145' ID>/studentSectionAssociations/students"
    Then I should receive a collection with 5 elements
    And the link at index 0 should point to an entity with id "154bc9b5-4214-4ff5-bda5-94cc99b2b724_id"
    And the link at index 1 should point to an entity with id "b8e346c8-025e-44ba-9ae1-f2fa4e832b08_id"
    And the link at index 2 should point to an entity with id "9f4019ca-dd53-4027-b11c-fc151268fafd_id"
    And the link at index 3 should point to an entity with id "6a859e2d-5664-47ea-bab0-78aee1edb6d9_id"
    And the link at index 4 should point to an entity with id "dd4068df-0bea-4280-bbac-fbc736eea54d_id"
    And the header "TotalCount" equals 25
    And the a previous link exists with offset equal to 15 and limit equal to 5
    Given parameter "offset" is "22"
    And parameter "limit" is "5"
    When I navigate to GET "/v1/sections/<'Sec 145' ID>/studentSectionAssociations/students"
    Then I should receive a collection with 3 elements
    And the link at index 0 should point to an entity with id "9f4019ca-dd53-4027-b11c-fc151268fafd_id"
    And the link at index 1 should point to an entity with id "6a859e2d-5664-47ea-bab0-78aee1edb6d9_id"
    And the link at index 2 should point to an entity with id "dd4068df-0bea-4280-bbac-fbc736eea54d_id"
    And the header "TotalCount" equals 25
    And the a previous link exists with offset equal to 17 and limit equal to 5

@DE1873 @DE1906 @DE1910
    Scenario: Paging's offset and limit don't do weird things
        Given I am logged in using "cgray" "cgray1234" to realm "IL"
        Given format "application/json"
        And all parameters are cleared
        When I navigate to GET "/v1/courses"
        And the header "TotalCount" equals 27
        Given parameter "offset" is "10"
        And parameter "limit" is "10"
        When I navigate to GET "/v1/courses"
        Then I should receive a collection with 10 unique elements
        And the header "TotalCount" equals 27
        Given parameter "offset" is "20"
        And parameter "limit" is "10"
        When I navigate to GET "/v1/courses"
        Then I should receive a collection with 7 unique elements
        Given parameter "offset" is "24"
        And parameter "limit" is "10"
        When I navigate to GET "/v1/courses"
        Then I should receive a collection with 3 non-unique elements
        And the header "TotalCount" equals 27
        Given parameter "offset" is "40"
        And parameter "limit" is "10"
        When I navigate to GET "/v1/courses"
        Then I should receive a return code of 200 


@DE1873 @DE1906 @DE1910
    Scenario: Paging's offset and limit don't do weird things
        Given I am logged in using "cgray" "cgray1234" to realm "IL"
        Given format "application/json"
        And all parameters are cleared
        When I navigate to GET "/v1/students"
        Given parameter "offset" is "0"
        And parameter "limit" is "0"
        Then I should receive a collection with 25 elements
        And the header "TotalCount" equals 25
        Given parameter "offset" is "10"
        And parameter "limit" is "10"
        When I navigate to GET "/v1/students"
        Then I should receive a collection with 10 unique elements
        And the header "TotalCount" equals 25
        Given parameter "offset" is "20"
        And parameter "limit" is "10"
        When I navigate to GET "/v1/students"
        Then I should receive a collection with 5 unique elements
        And the header "TotalCount" equals 25
        Given parameter "offset" is "30"
        And parameter "limit" is "10"
        When I navigate to GET "/v1/students"
        Then I should receive a return code of 200 
        Given parameter "offset" is "1"
        And parameter "limit" is "9"
        When I navigate to GET "/v1/students"
        Then I should receive a collection with 9 unique elements
        And the header "TotalCount" equals 25

@DE1580
Scenario: Confirm default limit of 50 entities and ability to override
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/json"
   When I navigate to GET "/v1/schools/ec2e4218-6483-4e9c-8954-0aecccfd4731/studentSchoolAssociations/students"
   Then I should receive a collection with 50 elements
    And the a next link exists with offset equal to 50 and limit equal to 50

   When parameter "limit" is "0"
    And I navigate to GET "/v1/schools/ec2e4218-6483-4e9c-8954-0aecccfd4731/studentSchoolAssociations/students"
   Then I should receive a collection with 63 elements
    And the a next link should not exist

Scenario: Confirm limit only affects final segment of URL
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/json"
   When parameter "limit" is "55"
    And I navigate to GET "/v1/schools/ec2e4218-6483-4e9c-8954-0aecccfd4731/studentSchoolAssociations/students/attendances"
   Then I should receive a collection with 55 elements
    
@DE1688
Scenario: Confirm negative limit is blocked by API
  Given I am logged in using "cgray" "rrogers1234" to realm "IL"
    And format "application/json"
   When parameter "limit" is "1"
    And I navigate to GET "/v1/schools"
   Then I should receive a return code of 200
   When parameter "limit" is "-1"
    And I navigate to GET "/v1/schools"
   Then I should receive a return code of 400
    
@DE1688
Scenario: Confirm negative offset is blocked by API
  Given I am logged in using "cgray" "rrogers1234" to realm "IL"
    And format "application/json"
   When parameter "offset" is "1"
    And parameter "limit" is "1"
    And I navigate to GET "/v1/schools"
   Then I should receive a return code of 200
   When parameter "offset" is "-1"
    And I navigate to GET "/v1/schools"
   Then I should receive a return code of 400

Scenario: Sorting Collections routed to Elastic Search
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/json"
    And parameter "sortBy" is "academicSubject"
    And parameter "sortOrder" is "ascending"
  When I navigate to GET "/v1/assessments"
  Then I should receive a return code of 200 
  Then I should receive a collection
   And the link at index 0 should have "academicSubject" equal to "English Language and Literature"
   And the link at index 1 should have "academicSubject" equal to "English Language and Literature"
   And the link at index 2 should have "academicSubject" equal to "English Language and Literature"
   And the link at index 3 should have "academicSubject" equal to "English Language and Literature"
   And the link at index 4 should have "academicSubject" equal to "Mathematics"
   And the link at index 12 should have "academicSubject" equal to "Reading"
  And the link at index 13 should have "academicSubject" equal to "Reading"
  And the link at index 14 should have "academicSubject" equal to "Reading"
  And the link at index 15 should have "academicSubject" equal to "Reading"
  And the link at index 16 should have "academicSubject" equal to "Reading"
 And parameter "sortBy" is "objective"
  And parameter "sortOrder" is "descending"
  When I navigate to GET "/v1/learningObjectives"
  Then I should receive a return code of 200 
  Then I should receive a collection
   And the link at index 0 should have "objective" equal to "Writing Test"
   And the link at index 1 should have "objective" equal to "Similarity, Right Triangle, and Trigonometry"
   And the link at index 2 should have "objective" equal to "Reading Test"
   And the link at index 3 should have "objective" equal to "Math Test"
   And the link at index 4 should have "objective" equal to "Geometry"
 And parameter "sortBy" is "subjectArea"
  And parameter "sortOrder" is "ascending"
  When I navigate to GET "/v1/learningStandards"
  Then I should receive a return code of 200 
  Then I should receive a collection
   And the link at index 0 should have "subjectArea" equal to "English Language and Literature"
   And the link at index 1 should have "subjectArea" equal to "English Language and Literature"
   And the link at index 2 should have "subjectArea" equal to "English Language and Literature"
   And the link at index 3 should have "subjectArea" equal to "Mathematics"
 And parameter "sortBy" is "codeValue"
  And parameter "sortOrder" is "ascending"
  When I navigate to GET "/v1/competencyLevelDescriptor"
  Then I should receive a return code of 200 
  Then I should receive a collection
   And the link at index 0 should have "codeValue" equal to "A"
   And the link at index 1 should have "codeValue" equal to "B"
  And parameter "sortBy" is "objective"
  And parameter "sortOrder" is "descending"
  When I navigate to GET "/v1/studentCompetencyObjectives"
  Then I should receive a return code of 200 
  Then I should receive a collection
   And the link at index 0 should have "objective" equal to "Learn to read"

Scenario: Paging collections routed to elastic search
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
  And format "application/json"
  Given format "application/json"
  And parameter "q" is "use"
  And parameter "offset" is "0"
  And parameter "limit" is "2"
  When I navigate to GET "/v1/search"
  Then I should receive a collection with 2 unique elements
  #This total count is due to an approximation algorithm for search paging
  And the header "TotalCount" equals 4
  And the a next link exists with offset equal to 2 and limit equal to 2
  Given parameter "offset" is "5"
  And parameter "limit" is "2"
  And parameter "q" is "use"
  When I navigate to GET "/v1/search"
  Then I should receive a collection with 1 unique elements
  And the header "TotalCount" equals 1
  And the a previous link exists with offset equal to 3 and limit equal to 2
  And the a next link should not exist
