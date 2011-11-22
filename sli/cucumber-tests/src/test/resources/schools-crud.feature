Feature: GET /schools
    This the SLI API to get school information.
    
    Background:
    	Given I am logged in using "jimi" "jimi"
    	And I accept "application/json"


    Scenario: The client creates a new school
        Given I go to "/schools"
        And I create:
        	| shortName | fullName | stateOrganizationId | schoolType | webSite | charterStatus | titleIPartASchoolDesignation | magnetSpecialProgramEmphasisSchool | administrativeFundingControl | operationalStatus |
        	| Canton | Plymouth-Canton High School | BOGUS-MI-ID-PCEP-001 | REGULAR | http://pcep.pccs.k12.mi.us/ | NOT_A_CHARTER_SCHOOL | NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL | NO_STUDENTS_PARTICIPATE | PUBLIC_SCHOOL | ACTIVE |
        	| Salem | Plymouth-Salem High School | BOGUS-MI-ID-PCEP-002 | REGULAR | http://pcep.pccs.k12.mi.us/ | NOT_A_CHARTER_SCHOOL | NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL | NO_STUDENTS_PARTICIPATE | PUBLIC_SCHOOL | ACTIVE |
        Then the status code is 204
        And the response is empty
 

    Scenario: The client requests all schools.
        Given I go to "/schools"
        Then the status code is 200
        And the response contains:
        	| shortName | fullName | stateOrganizationId | schoolType | webSite | charterStatus | titleIPartASchoolDesignation | magnetSpecialProgramEmphasisSchool | administrativeFundingControl | operationalStatus |
        	| Canton | Plymouth-Canton High School | BOGUS-MI-ID-PCEP-001 | REGULAR | http://pcep.pccs.k12.mi.us/ | NOT_A_CHARTER_SCHOOL | NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL | NO_STUDENTS_PARTICIPATE | PUBLIC_SCHOOL | ACTIVE |
        	| Salem | Plymouth-Salem High School | BOGUS-MI-ID-PCEP-002 | REGULAR | http://pcep.pccs.k12.mi.us/ | NOT_A_CHARTER_SCHOOL | NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL | NO_STUDENTS_PARTICIPATE | PUBLIC_SCHOOL | ACTIVE |
        	
     
     Scenario: The client updates an existing school
     	Given I go to "/schools"
     	And I find and update:
        	| shortName | fullName | stateOrganizationId | schoolType | webSite | charterStatus | titleIPartASchoolDesignation | magnetSpecialProgramEmphasisSchool | administrativeFundingControl | operationalStatus |
        	| Canton | Plymouth-Canton High School | UPDATED-ID1 | REGULAR | http://google.com | NOT_A_CHARTER_SCHOOL | NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL | NO_STUDENTS_PARTICIPATE | PUBLIC_SCHOOL | ACTIVE |
        	| Salem | Plymouth-Salem High School | UPDATED-ID1 | REGULAR | http://bing.com | NOT_A_CHARTER_SCHOOL | NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL | NO_STUDENTS_PARTICIPATE | PUBLIC_SCHOOL | ACTIVE |
        Then the status code is 204
        And I see the following updates:
        	| shortName | fullName | stateOrganizationId | schoolType | webSite | charterStatus | titleIPartASchoolDesignation | magnetSpecialProgramEmphasisSchool | administrativeFundingControl | operationalStatus |
        	| Canton | Plymouth-Canton High School | UPDATED-ID1 | REGULAR | http://google.com | NOT_A_CHARTER_SCHOOL | NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL | NO_STUDENTS_PARTICIPATE | PUBLIC_SCHOOL | ACTIVE |
        	| Salem | Plymouth-Salem High School | UPDATED-ID1 | REGULAR | http://bing.com | NOT_A_CHARTER_SCHOOL | NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL | NO_STUDENTS_PARTICIPATE | PUBLIC_SCHOOL | ACTIVE |


	Scenario: The client deletes an existing school
		Given I go to "/schools"
		And I find and delete:
        	| shortName | fullName | stateOrganizationId | schoolType | webSite | charterStatus | titleIPartASchoolDesignation | magnetSpecialProgramEmphasisSchool | administrativeFundingControl | operationalStatus |
        	| Canton | Plymouth-Canton High School | UPDATED-ID1 | REGULAR | http://google.com | NOT_A_CHARTER_SCHOOL | NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL | NO_STUDENTS_PARTICIPATE | PUBLIC_SCHOOL | ACTIVE |
        	| Salem | Plymouth-Salem High School | UPDATED-ID1 | REGULAR | http://bing.com | NOT_A_CHARTER_SCHOOL | NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL | NO_STUDENTS_PARTICIPATE | PUBLIC_SCHOOL | ACTIVE |
		Then the status code is 204
		And I cannot find the following:
        	| shortName | fullName | stateOrganizationId | schoolType | webSite | charterStatus | titleIPartASchoolDesignation | magnetSpecialProgramEmphasisSchool | administrativeFundingControl | operationalStatus |
        	| Canton | Plymouth-Canton High School | UPDATED-ID1 | REGULAR | http://google.com | NOT_A_CHARTER_SCHOOL | NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL | NO_STUDENTS_PARTICIPATE | PUBLIC_SCHOOL | ACTIVE |
        	| Salem | Plymouth-Salem High School | UPDATED-ID1 | REGULAR | http://bing.com | NOT_A_CHARTER_SCHOOL | NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL | NO_STUDENTS_PARTICIPATE | PUBLIC_SCHOOL | ACTIVE |
