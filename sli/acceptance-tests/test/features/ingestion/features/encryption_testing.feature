@RALLY_US1207
Feature: Ingestion Encryption Testing

Background: I have a landing zone route configured
    Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Ingested Student data should be encrypted: Clean Database
	Given I post "encryption.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
        | collectionName              |
        | student                     |  
        | parent                      | 
	When zip file is scp to ingestion landing zone
	And I am willing to wait upto 60 seconds for ingestion to complete
	And a batch job for file "encryption.zip" is completed in database
	And a batch job log has been created
	And I should not see a warning log file created
	And I should not see an error log file created
	Then I should see "Processed 2 records." in the resulting batch job file
	 	And I should see following map of entry counts in the corresponding collections:
	        | collectionName              | count |
	        | student                     | 1     |
	        | parent                      | 1     |
	    And I find a record in "student" with "body.studentUniqueStateId" equal to "530425896"
		# UNENCRYPTED FIELDS
		And the field "body.studentUniqueStateId" has value "530425896"
		And the field "body.hispanicLatinoEthnicity" has value "false"
		And the field "body.oldEthnicity" has value "Black, Not Of Hispanic Origin"
		And the field "body.race[0]" has value "Black - African American"
		And the field "body.economicDisadvantaged" has value "false"
		And the field "body.schoolFoodServicesEligibility" has value "Reduced price"
		And the field "body.studentCharacteristics[0].characteristic" has value "Parent in Military"
		And the field "body.studentCharacteristics[0].beginDate" has value "2000-10-01"
		And the field "body.limitedEnglishProficiency" has value "NotLimited"
		And the field "body.languages[0]" has value "English"
		And the field "body.homeLanguages[0]" has value "English"
		And the field "body.disabilities[0].disability" has value "Other Health Impairment"
		And the field "body.section504Disabilities[0]" has value "Medical Condition"
		And the field "body.displacementStatus" has value "Slightly to the right"
		And the field "body.learningStyles.visualLearning" has value "33"
		And the field "body.learningStyles.auditoryLearning" has value "33"
		And the field "body.learningStyles.tactileLearning" has value "33"
		And the field "body.cohortYears[0].schoolYear" has value "2010-2011"
		And the field "body.cohortYears[0].cohortYearType" has value "First grade"
		And the field "body.studentIndicators[0].indicatorName" has value "At risk"
		And the field "body.studentIndicators[0].indicator" has value "At risk"
		And the field "body.studentIdentificationCode[0].identificationSystem" has value "SSN"
		And the field "body.studentIdentificationCode[0].assigningOrganizationCode" has value "Federal"

		# ENCRYPTED FIELDS
		And the field "body.studentIdentificationCode[0].identificationCode" with value "555-55-5555" is encrypted
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
		And the field "body.loginId" with value "rsd" is encrypted

		And I find a record in "parent" with "body.parentUniqueStateId" equal to "9870036500"
        #UNENCRYPTED FIELDS
		And the field "body.loginId" has value "rle"
        # ENCRYPTED FIELDS
		And the field "body.name.firstName" with value "Richard" is encrypted
		And the field "body.name.middleName" with value "Lee" is encrypted
		And the field "body.name.lastSurname" with value "Le" is encrypted
		And the field "body.sex" with value "Male" is encrypted
		And the field "body.address[0].streetNumberName" with value "23 Rue Lecourbe" is encrypted
		And the field "body.address[0].city" with value "Paris" is encrypted
		And the field "body.address[0].postalCode" with value "75229" is encrypted
		And the field "body.address[0].stateAbbreviation" with value "IL" is encrypted
		And the field "body.telephone[0].telephoneNumber" with value "+33 (1) 45.23.35.48.12" is encrypted
		And the field "body.electronicMail[0].emailAddress" with value "rle@summer.nc.edu" is encrypted

Scenario: Ingested Student data should be encrypted: Populated Database
	Given I post "encryption.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
         | collectionName              |
	    | recordHash                  |
	When zip file is scp to ingestion landing zone
	And I am willing to wait upto 30 seconds for ingestion to complete
	And a batch job for file "encryption.zip" is completed in database
	And a batch job log has been created
	Then I should see "Processed 2 records." in the resulting batch job file
	And I should not see a warning log file created
	And I should not see an error log file created
	 	And I should see following map of entry counts in the corresponding collections:
	        | collectionName              | count |
	        | student                     | 1     |
	        | parent                      | 1     |
	    And I find a record in "student" with "body.studentUniqueStateId" equal to "530425896"
		# UNENCRYPTED FIELDS
		And the field "body.studentUniqueStateId" has value "530425896"
		And the field "body.hispanicLatinoEthnicity" has value "false"
		And the field "body.oldEthnicity" has value "Black, Not Of Hispanic Origin"
		And the field "body.race[0]" has value "Black - African American"
		And the field "body.economicDisadvantaged" has value "false"
		And the field "body.schoolFoodServicesEligibility" has value "Reduced price"
		And the field "body.studentCharacteristics[0].characteristic" has value "Parent in Military"
		And the field "body.studentCharacteristics[0].beginDate" has value "2000-10-01"
		And the field "body.limitedEnglishProficiency" has value "NotLimited"
		And the field "body.languages[0]" has value "English"
		And the field "body.homeLanguages[0]" has value "English"
		And the field "body.disabilities[0].disability" has value "Other Health Impairment"
		And the field "body.section504Disabilities[0]" has value "Medical Condition"
		And the field "body.displacementStatus" has value "Slightly to the right"
		And the field "body.learningStyles.visualLearning" has value "33"
		And the field "body.learningStyles.auditoryLearning" has value "33"
		And the field "body.learningStyles.tactileLearning" has value "33"
		And the field "body.cohortYears[0].schoolYear" has value "2010-2011"
		And the field "body.cohortYears[0].cohortYearType" has value "First grade"
		And the field "body.studentIndicators[0].indicatorName" has value "At risk"
		And the field "body.studentIndicators[0].indicator" has value "At risk"
		And the field "body.studentIdentificationCode[0].identificationSystem" has value "SSN"
		And the field "body.studentIdentificationCode[0].assigningOrganizationCode" has value "Federal"
		# ENCRYPTED FIELDS
		And the field "body.studentIdentificationCode[0].identificationCode" with value "555-55-5555" is encrypted
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
		And the field "body.loginId" with value "rsd" is encrypted

		And I find a record in "parent" with "body.parentUniqueStateId" equal to "9870036500"
        # UNENCRYPTED FIELDS
		And the field "body.loginId" has value "rle"
        # ENCRYPTED FIELDS
		And the field "body.name.firstName" with value "Richard" is encrypted
		And the field "body.name.middleName" with value "Lee" is encrypted
		And the field "body.name.lastSurname" with value "Le" is encrypted
		And the field "body.sex" with value "Male" is encrypted
		And the field "body.address[0].streetNumberName" with value "23 Rue Lecourbe" is encrypted
		And the field "body.address[0].city" with value "Paris" is encrypted
		And the field "body.address[0].postalCode" with value "75229" is encrypted
		And the field "body.address[0].stateAbbreviation" with value "IL" is encrypted
		And the field "body.telephone[0].telephoneNumber" with value "+33 (1) 45.23.35.48.12" is encrypted
		And the field "body.electronicMail[0].emailAddress" with value "rle@summer.nc.edu" is encrypted
		
