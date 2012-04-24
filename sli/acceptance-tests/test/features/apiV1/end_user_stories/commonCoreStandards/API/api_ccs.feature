@wip
Feature: API calls to Learning Standards and Learning Objectives

Background: I have two math learning objectives and 1 english learning objective ingested in the SDS

Scenario: As an app developer I want to traverse a learning objective hierarchy and get to all learning standards
Given a valid association json document for a "<ASSOCIATION TYPE>" with "Objective" "Similarity, Right Triangle, and Trigonometry" and ID <ObjectiveWithStandard>
When I navigate to GET "/<ENTITY URI>"
Then I should receive a return code of 200

When I navigate to GET "/<ENTITY URI>/<ObjectiveWithStandard>/childLearningObjectives"
Then  I should receive a return code of 200
And I should recieve no references to  "<ASSOCIATION TYPE>" 

When I navigate to GET "/<ENTITY URI>"/<ObjectiveWithStandard>/parentLearningObjective"
Then  I should receive a return code of 200
And I should recieve a references to 1 "<ASSOCIATION TYPE>" 
And when I resolve one reference I get an "<ASSOCIATION TYPE>" with "Objective"of "Geometry"
And ID <ParentLearningObjectiveID>

When I navigate GET "/<ENTITY URI>/<ObjectiveWithStandards>/learningStandards"
Then  I should receive a return code of 200
And I should recieve a references to 11 "<ASSOCIATION TYPE LEARNING STANDARDS>" 
And when I resolve all references I find one reference of "<ASSOCIATION TYPE LEARNING STANDARDS>"  type with "Description"of "Explain and use the relationship between the sine and cosine of complementary angles."
					 

	
	
	

  
