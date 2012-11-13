@RALLY_2875
Feature: Aggregate highest ever assessments

Background: I have a landing zone route configured
Given I am using the general data store
And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
    Given I post "aggregationData/DataSet1.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName               |
        | assessment                   |
        | student                      |
        | studentSchoolAssociation     |
        | studentSectionAssociation    |
        | studentAssessment |
    When zip file is scp to ingestion landing zone
      And a batch job log has been created
    Then I should see following map of entry counts in the corresponding collections:
        | collectionName               | count |
        | student                      | 350   |
        | studentSchoolAssociation     | 350   |
        | studentSectionAssociation    | 350   |
        | studentAssessment | 3500  |

    When I run the highest ever aggregation job
    Then I see the expected number of <Collection> records for <Assessment> with score <Score> is <Count>:
        | Collection |       Assessment        | Score | Count |
        |  student   | Grade 7 2011 State Math |  32.0 |  110  |
    And for the student with ID <Id>, I see a highest ever score of <Score> for the <Assessment> assessment:
        | Id | Score |       Assessment        |
        |  0 |  32.0 | Grade 7 2011 State Math |
        |  1 |  27.0 | Grade 7 2011 State Math |
        |  2 |  30.0 | Grade 7 2011 State Math |
    When I run the proficiency aggregation job
    Then I see the expected number of <Collection> records with aggregates for <Assessment> is <Count>:
        |      Collection       |       Assessment        | Count |
        | educationOrganization | Grade 7 2011 State Math |   1   |
    And East Daybreak Junior High has cut point <Cut> count <Count> for assessment <Assessment>:
        | Cut | Count |       Assessment        |
        |  E  |  296  | Grade 7 2011 State Math |
        |  S  |   52  | Grade 7 2011 State Math |
        |  B  |    2  | Grade 7 2011 State Math |
