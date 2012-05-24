@RALLY_US209
Feature: API calls to Learning Standards and Learning Objectives

Background: 
Given I have two math learning objectives and 1 english learning objective ingested in the SDS
Given I am logged in using "demo" "demo1234" to realm "SLI"
Given format "application/json"

Scenario: As an app developer I want to traverse a learning objective hierarchy and get to all learning standards
When I navigate to GET "/<LEARNINGOBJECTIVE URI>/<LEARNINGOBJECTIVE_WITH_STANDARD ID>"
Then I should receive a return code of 200
Then I should receive a "learningObjective" entity 
And "Objective" is "Similarity, Right Triangle, and Trigonometry"

When I navigate to GET "/<LEARNINGOBJECTIVE URI>/<LEARNINGOBJECTIVE_WITH_STANDARD ID>/<CHILD_LEARNINGOBJECTIVE>"
Then  I should receive a return code of 404

When I navigate to GET "/<LEARNINGOBJECTIVE URI>/<LEARNINGOBJECTIVE_WITH_STANDARD ID>/<PARENT_LEARNINGOBJECTIVE>"
Then  I should receive a return code of 200
And I should recieve a collection of 1 "learningObjective" entity
And  "Objective" is "Geometry"
And  "id" is "<PARENT_LEARNINGOBJECTIVE ID>"

When I navigate to GET "/<LEARNINGOBJECTIVE URI>/<LEARNINGOBJECTIVE_WITH_STANDARD ID>/<LEARNINGSTANDARD URI>"
Then  I should receive a return code of 200
And I should recieve a collection of 11 "learningStandard" entity
And I should find one entity with "description" is "Explain and use the relationship between the sine and cosine of complementary angles."
					 

	
	
	

  
