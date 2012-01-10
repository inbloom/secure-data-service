Feature: <US634> Sample Ingestion Gherkin Test

Background: I have a landing zone route configured
Given I have landing zone configured to "C:/Software/gitrepo/SLI/sli/ingestion/src/test/resources/fileLevelTestData/validXML/"
	And I connect to "sli" database

Scenario: Post a zip file as a payload of the ingestion job
Given I post "validXML.zip" as the payload of the ingestion job
	And there are no students in DS
When zip file is scp to ingestion landing zone at "C:/Software/sampleLandingZone/"
	And "1" seconds have elapsed
Then I should see a file called "100students.zip.status" at "C:/Software/sampleLandingZone/"
	And I should see "100 records ingested" in "100students.zip.status" file


    