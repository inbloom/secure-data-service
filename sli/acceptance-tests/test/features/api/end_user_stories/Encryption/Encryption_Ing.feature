@wip
Feature: Test Encrypted data

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone

Scenario: Ingest a xml file containing a student info, save, check data is encrypted
Given I post "Student.xml" file as the payload of the ingestion job
    And the following student collection is empty in datastor
	When zip file is scp to ingestion landing zone
    And "30" seconds have elapsed
	Then I should see student collection of entry count 1
    
    When I check to find if record readable in student collection
    Then name.firstName is encrypted
    And name.lastSurname is encrypted
    And otherName is encrypted
	And address. streetNumberName is encrypted
	And address.city is encrypted
	And address.stateAbbreviation is encrypted 
	And address.postalCode is encrypted
	And telephone.telephoneNumber is encrypted
	And 	electronicEmail.emailAddress is encrypted
	And hispanicLatinEthnicity is encrypted
	And oldEthnicity is encrypted
	And race is encrypted
	And economicDisadvantaged is encrypted
	And schoolFoodServicesEligibility is encrypted
	And studentCharacteristics.characteristic is encrypted
	And studentCharacteristics.beginDate is encrypted
	And limitedEnglishProficency is encrypted
	And homeLanguages is encrypted
	And languages is encrypted
	And disability.disability is encrypted
	And section504Disabilities is encrypted
	And displacementStatus is encrypted
	And programParticipation.program is encrypted
	And programParticipation.beginDate is encrypted
	And learningStyles.visualLearning is encrypted
	And loginId is encrypted
	And  sex is encrypted
	And birthData.birthDate is encrypted
	And birthData.stateOfBirthAbbreviation is encrypted
	