Feature: Ingestion Encryption Testing

Background: I have a landing zone route configured
    Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone


Scenario: Ingested Student data should be encrypted
	Given I post "encryption.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
        | collectionName              |
        | student                     |   
	When zip file is scp to ingestion landing zone
		And "5" seconds have elapsed
	Then I should see "Processed 1 records." in the resulting batch job file
	 	And I should see following map of entry counts in the corresponding collections:
	        | collectionName              | count |
	        | student                     | 1    |
	    And I find a record in "student" with "metaData.externalId" equal to "530425896"
		# UNENCRYPTED FIELDS
		And the field "body.studentUniqueStateId" has value "530425896"
		And the field "body.cohortYears[0].schoolYear" has value "2010-2011"
		And the field "body.cohortYears[0].cohortYearType" has value "First grade"
		And the field "body.studentIndicators[0].indicatorName" has value "At risk"
		And the field "body.studentIndicators[0].indicator" has value "At risk"
		# ENCRYPTED FIELDS
		And the field "body.name.firstName" with value "Rhonda" is encrypted
		And the field "body.name.middleName" with value "Shannon" is encrypted
		And the field "body.name.lastSurname" with value "Delgado" is encrypted
		And the field "body.otherName[0].firstName" with value "Julie" is encrypted
		And the field "body.otherName[0].middleName" with value "Wren" is encrypted
		And the field "body.otherName[0].lastSurname" with value "Einstein" is encrypted
		And the field "body.otherName[0].otherNameType" with value "Nickname" is encrypted
		And the field "body.sex" with value "Female" is encrypted
		And the field "body.birthData.birthDate" with value "2006-07-02" is encrypted
		And the field "body.address[0].streetNumberName" with value "1234 Shaggy" is encrypted
		And the field "body.address[0].city" with value "Durham" is encrypted
		And the field "body.address[0].postalCode" with value "27701" is encrypted
		And the field "body.address[0].stateAbbreviation" with value "NC" is encrypted
		And the field "body.telephone[0].telephoneNumber" with value "919-555-8765" is encrypted
		And the field "body.electronicMail[0].emailAddress" with value "rsd@summer.nc.edu" is encrypted
		And the field "body.hispanicLatinoEthnicity" with value "false" is encrypted
		And the field "body.oldEthnicity" with value "Black, Not Of Hispanic Origin" is encrypted
		And the field "body.race[0]" with value "Black - African American" is encrypted
		And the field "body.economicDisadvantaged" with value "false" is encrypted
		And the field "body.schoolFoodServicesEligibility" with value "Reduced price" is encrypted
		And the field "body.studentCharacteristics[0].characteristic" with value "Parent in Military" is encrypted
		And the field "body.studentCharacteristics[0].beginDate" with value "2000-01-28" is encrypted
		And the field "body.limitedEnglishProficiency" with value "NotLimited" is encrypted
		And the field "body.languages[0]" with value "English" is encrypted
		And the field "body.homeLanguages[0]" with value "English" is encrypted
		And the field "body.disabilities[0]" with value "Other Health Impairment" is encrypted
		And the field "body.section504Disabilities[0]" with value "Medical Condition" is encrypted
		And the field "body.displacementStatus" with value "Slightly to the right" is encrypted
		And the field "body.learningStyles.visualLearning" with value "33" is encrypted
		And the field "body.learningStyles.auditoryLearning" with value "33" is encrypted
		And the field "body.learningStyles.tactileLearning" with value "33" is encrypted
		And the field "body.loginId" with value "rsd" is encrypted
		