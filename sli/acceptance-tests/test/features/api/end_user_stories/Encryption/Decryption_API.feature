@wip
Feature: Read decrypted data

Background: None

Scenario:  As a IT admin I should be able read decrypted student entity
     Given  I am a valid SEA/LEA end user "administrator" with password "administrator1234"
    And I have a Role attribute returned from the "SEA/LEA IDP"
    And the role attribute equals "IT Administrator"
    And I am authenticated on "SEA/LEA IDP"

    Given format "application/json"
	When I navigate to GET "/students/<'Keisha Ho' ID>"
	Then I should find Student with <'Keisha Ho' ID>
	And name.firstName is "Keisha"
    And name.lastSurname is "Ho"
    And otherName is "Keish"
	And address. streetNumberName is "4 Privet Drive"
	And address.city is "Waco"
	And address.stateAbbreviation is "TX"
	And address.postalCode is "22222"
	And telephone.telephoneNumber is "111-111-1111"
	And 	electronicEmail.emailAddress is "KHO@mymail.com"
	And hispanicLatinEthnicity is "false"
	And oldEthnicity is "Asian Or Pacific Islander"
	And race is "Asian"
	And economicDisadvantaged is "false"
	And schoolFoodServicesEligibility is "Full price"
	And studentCharacteristics.characteristic is "Immigrant"
	And studentCharacteristics.beginDate is "2003-01-01"
	And limitedEnglishProficency is "NotLimited"
	And homeLanguages is "Vietnamese"
	And languages is "English"
	And disability.disability is "Other Health Impairment"
	And section504Disabilities is "Other"
	And displacementStatus is "false"
	And programParticipation.program is "Regular Education"
	And programParticipation.beginDate is "2005-01-01"
	And learningStyles.visualLearning is "45%"
	And loginId is "KHO1212"
	And birthData.birthDate is "1994-08-02"
	And birthData.stateOfBirthAbbreviation is "TX"
	And  sex is "Female"
