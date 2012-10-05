@RALLY_US2170
Feature: Tenant Registration Entity
As an administrator for SLI, I want to create a tenant entity so that it listen for files on landing zone

Scenario: CRUD operations on Tenants

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    When I navigate to GET "/tenants?tenantId=Midgar"
    Then I should receive a return code of 200
    And I should receive a UUID
    And I should receive the data for the specified tenant entry
    When I navigate to PUT "/tenants/<New Tenant UUID>"
    Then I should receive a return code of 204
    When I navigate to DELETE "/tenants/<New Tenant UUID>"
    Then I should receive a return code of 204
    And I should no longer be able to get that tenant's data

Scenario: Deny creation when specifying invalid fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    And I should see following map of indexes in the corresponding collections:
     | collectionName                             | index                                                                |
     | assessment                                 | metaData.tenantId_1__id_1                                            |
     | assessment                                 | metaData.tenantId_1_metaData.edOrgs_1                                |
     | attendance                                 | metaData.tenantId_1__id_1                            				 |
     | attendance                                 | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | attendance                                 | metaData.tenantId_1_body.studentId_1                            	 |
     | attendance                                 | metaData.tenantId_1_body.studentId_1                            	 |
     | cohort                                     | metaData.tenantId_1__id_1                                			 |
     | cohort                                     | metaData.tenantId_1_metaData.edOrgs_1                                |
     | cohort                                     | metaData.tenantId_1_body.educationOrgId_1                            |
     | course                                     | metaData.tenantId_1__id_1                                			 |
     | course                                     | metaData.tenantId_1_metaData.edOrgs_1                                |
     | course                                     | metaData.tenantId_1_body.schoolId_1                                  |
     | courseOffering                             | metaData.tenantId_1__id_1                             			     |
     | courseOffering                             | metaData.tenantId_1_metaData.edOrgs_1                                |
     | courseOffering                             | metaData.tenantId_1_body.courseId_1                                  |
     | courseOffering                             | metaData.tenantId_1_body.schoolId_1                                  |
     | courseOffering                             | metaData.tenantId_1_body.sessionId_1                                 |
     | disciplineAction                      	  | metaData.tenantId_1__id_1                          				     |
     | disciplineAction                      	  | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | disciplineAction                      	  | metaData.tenantId_1_body.assignmentSchoolId_1                      	 |
     | disciplineIncident                         | metaData.tenantId_1__id_1                            				 |
     | disciplineIncident                         | metaData.tenantId_1_metaData.edOrgs_1                                |
     | disciplineIncident                         | metaData.tenantId_1_body.schoolId_1                            		 |
     | disciplineIncident                         | metaData.tenantId_1_body.staffId_1                             		 |
     | educationOrganization                      | metaData.tenantId_1__id_1                           				 |
     | educationOrganization                      | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | educationOrganization                      | metaData.tenantId_1_body.parentEducationAgencyReference_1            |
     | grade                                      | metaData.tenantId_1__id_1                                            |
     | grade                                      | metaData.tenantId_1_metaData.edOrgs_1                                |
     | grade                                      | metaData.tenantId_1_body.gradingPeriodId_1                           |
     | gradebookEntry                             | metaData.tenantId_1__id_1                                            |
     | gradebookEntry                             | metaData.tenantId_1_metaData.edOrgs_1                           	 |
     | gradebookEntry                             | metaData.tenantId_1_body.sectionId_1                 	             |
     | gradingPeriod                              | metaData.tenantId_1__id_1                                            |
     | gradingPeriod                              | metaData.tenantId_1_metaData.edOrgs_1                                |
     | learningObjective                          | metaData.tenantId_1_metaData.edOrgs_1                                |
     | learningObjective                          | metaData.tenantId_1_body.parentLearningObjective_1                   |
     | learningStandard                           | metaData.tenantId_1__id_1                                            |
     | learningStandard                           | metaData.tenantId_1_metaData.edOrgs_1                                |
     | parent                                     | metaData.tenantId_1__id_1                                            |
     | parent                                     | metaData.tenantId_1_metaData.edOrgs_1                        	     |
     | parent                                     | metaData.tenantId_1_body.parentUniqueStateId_1                       |
     | program                                    | metaData.tenantId_1__id_1                                            |
     | program                                    | metaData.tenantId_1_metaData.edOrgs_1                                |
     | program                                    | metaData.tenantId_1_body.programId_1                                 |
     | reportCard                                 | metaData.tenantId_1__id_1                                            |
     | reportCard                                 | metaData.tenantId_1_metaData.edOrgs_1                                |
     | reportCard                                 | metaData.tenantId_1_body.studentId_1                                 |
     | section                                    | metaData.tenantId_1__id_1                                            |
     | section                                    | metaData.tenantId_1_metaData.edOrgs_1                                |
     | section                                    | metaData.tenantId_1_body.courseOfferingId_1                          |
     | section                                    | metaData.tenantId_1_body.schoolId_1                                  |
     | section                                    | metaData.tenantId_1_body.sessionId_1                                 |
     | session                                    | metaData.tenantId_1__id_1                                            |
     | session                                    | metaData.tenantId_1_metaData.edOrgs_1                                |
     | session                                    | metaData.tenantId_1_body.schoolId_1                                  |
     | staff                                      | metaData.tenantId_1__id_1                                            |
     | staff                                      | metaData.tenantId_1_metaData.edOrgs_1                                |
     | staff                                      | metaData.tenantId_1_body.staffUniqueStateId_1                        |
     | staffCohortAssociation                     | metaData.tenantId_1__id_1                                            |
     | staffCohortAssociation                     | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | staffEducationOrganizationAssociation      | metaData.tenantId_1__id_1                                            |
     | staffEducationOrganizationAssociation      | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | staffEducationOrganizationAssociation      | metaData.tenantId_1_body.staffReference_1                          	 |
     | staffProgramAssociation                    | metaData.tenantId_1__id_1                                            |
     | staffProgramAssociation                    | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | student                                    | metaData.tenantId_1__id_1                                            |
     | student                                    | metaData.tenantId_1_metaData.edOrgs_1                                |
     | student                                    | metaData.tenantId_1_body.studentUniqueStateId_1                      |
     | studentAcademicRecord                      | metaData.tenantId_1__id_1                                            |
     | studentAcademicRecord                      | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | studentAcademicRecord                      | metaData.tenantId_1_body.sessionId_1                            	 |
     | studentAcademicRecord                      | metaData.tenantId_1_body.studentId_1                            	 |
     | studentAssessmentAssociation               | metaData.tenantId_1__id_1                                            |
     | studentAssessmentAssociation               | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | studentAssessmentAssociation               | metaData.tenantId_1_body.assessmentId_1                            	 |
     | studentAssessmentAssociation               | metaData.tenantId_1_body.studentId_1                            	 |
     | studentCohortAssociation                   | metaData.tenantId_1__id_1                                            |
     | studentCohortAssociation                   | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | studentCohortAssociation                   | metaData.tenantId_1_body.cohortId_1                              	 |
     | studentCohortAssociation                   | metaData.tenantId_1_body.studentId_1                            	 |
     | studentCompetency                          | metaData.tenantId_1__id_1                                            |
     | studentCompetency                          | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | studentCompetency                          | metaData.tenantId_1_body.learningObjectiveId_1                    	 |
     | studentCompetency                          | metaData.tenantId_1_body.studentSectionAssociationId_1             	 |
     | studentDisciplineIncidentAssociation       | metaData.tenantId_1__id_1                                            |
     | studentDisciplineIncidentAssociation       | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | studentDisciplineIncidentAssociation       | metaData.tenantId_1_body.disciplineIncidentId_1                            	 |
     | studentDisciplineIncidentAssociation       | metaData.tenantId_1_body.studentId_1                            	 |
     | studentGradebookEntry                      | metaData.tenantId_1__id_1                                            |
     | studentGradebookEntry                      | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | studentGradebookEntry                      | metaData.tenantId_1_body.gradebookEntryId_1                        	 |
     | studentGradebookEntry                      | metaData.tenantId_1_body.sectionId_1                            	 |
     | studentGradebookEntry                      | metaData.tenantId_1_body.studentId_1                            	 |
     | studentParentAssociation                   | metaData.tenantId_1__id_1                                            |
     | studentParentAssociation                   | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | studentParentAssociation                   | metaData.tenantId_1_body.parentId_1                             	 |
     | studentParentAssociation                   | metaData.tenantId_1_body.studentId_1                            	 |
     | studentProgramAssociation                  | metaData.tenantId_1__id_1                                            |
     | studentProgramAssociation                  | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | studentProgramAssociation                  | metaData.tenantId_1_body.educationOrganizationId_1                 	 |
     | studentProgramAssociation                  | metaData.tenantId_1_body.programId_1                            	 |
     | studentProgramAssociation                  | metaData.tenantId_1_body.studentId_1                            	 |
     | studentSchoolAssociation                   | metaData.tenantId_1__id_1                                            |
     | studentSchoolAssociation                   | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | studentSchoolAssociation                   | metaData.tenantId_1_body.schoolId_1                            	     |
     | studentSchoolAssociation                   | metaData.tenantId_1_body.studentId_1                            	 |
     | studentSectionAssociation                  | metaData.tenantId_1__id_1                                            |
     | studentSectionAssociation                  | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | studentSectionAssociation                  | metaData.tenantId_1_body.sectionId_1                            	 |
     | studentSectionAssociation                  | metaData.tenantId_1_body.studentId_1                            	 |
     | studentTranscriptAssociation               | metaData.tenantId_1__id_1                                            |
     | studentTranscriptAssociation               | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | studentTranscriptAssociation               | metaData.tenantId_1_body.courseId_1                            	     |
     | studentTranscriptAssociation               | metaData.tenantId_1_body.studentAcademicRecordId_1                	 |
     | studentTranscriptAssociation               | metaData.tenantId_1_body.studentId_1                            	 |
     | teacherSchoolAssociation                   | metaData.tenantId_1__id_1                                            |
     | teacherSchoolAssociation                   | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | teacherSchoolAssociation                   | metaData.tenantId_1_body.schoolId_1                              	 |
     | teacherSchoolAssociation                   | metaData.tenantId_1_body.teacherId_1                            	 |
     | teacherSectionAssociation                  | metaData.tenantId_1__id_1                                            |
     | teacherSectionAssociation                  | metaData.tenantId_1_metaData.edOrgs_1                            	 |
     | teacherSectionAssociation                  | metaData.tenantId_1_body.sectionId_1                            	 |
     | teacherSectionAssociation                  | metaData.tenantId_1_body.teacherId_1                            	 |
     
    When I navigate to GET "/tenants?tenantId=Midgar"
    Then I should receive a return code of 200
    And I should receive a UUID
    And I PUT a tenant specifying an invalid field
    Then I should receive a return code of 400

Scenario Outline: Deny access when logging in as invalid user

    Given I am logged in using "baduser" "baduser1234" to realm "SLI"
    Given I am logged in using <User> <Password> to realm <Realm>
    When I navigate to GET "/tenants/<Testing Tenant>"
    Then I should receive a return code of 403
    Examples:
    | User       | Password       | Realm |
    | "baduser"  | "baduser1234"  | "SLI" |
    | "badadmin" | "badadmin1234" | "IL"  |

@wip
Scenario: Deny creation when missing userName
    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I POST a basic tenant with no userName
    Then I should receive a return code of 400

Scenario Outline: Deny creation when missing individual fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I POST a provision request with missing <Property>
    Then I should receive a return code of 400
    Examples:
    | Property      |
    | "stateOrganizationId" |


Scenario Outline: Deny update when missing individual fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    When I navigate to GET "/tenants?tenantId=Midgar"
    Then I should receive a return code of 200
    And I should receive a UUID
    When I PUT a basic tenant with missing <Property>
    Then I should receive a return code of 400
    Examples:
    | Property      |
    | "landingZone" |

Scenario Outline: Deny creation when missing individual landingZone fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    When I navigate to GET "/tenants?tenantId=Midgar"
    Then I should receive a return code of 200
    And I should receive a UUID
    When I PUT a basic tenant with missing landingZone <Property>
    Then I should receive a return code of 400
    Examples:
    | Property                |
    | "ingestionServer"       |
    | "educationOrganization" |
    | "path"                  |
    | "ingestionServer"       |


Scenario Outline: Deny creation when specifying individual wrong size fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    When I navigate to GET "/tenants?tenantId=Midgar"
    Then I should receive a return code of 200
    And I should receive a UUID
    When I PUT a basic tenant with userName <Value>
    Then I should receive a return code of 400
    Examples:
    | Value |
    | "123456789012345678901234567890123456789012345678A" |
    | "" |
    
Scenario Outline: Deny creation when specifying individual landingZone wrong size fields

    Given I am logged in using "ingestionuser" "ingestionuser1234" to realm "SLI"
    When I provision a new landing zone
    Then I should receive a return code of 201
    When I navigate to GET "/tenants?tenantId=Midgar"
    Then I should receive a return code of 200
    And I should receive a UUID
    When I PUT a basic tenant with landingZone <Property> set to <Value>
    Then I should receive a return code of 400
    Examples:
    | Property                | Value |
    | "ingestionServer"       | "123456789012345678901234567890123456789012345678A" |
    | "ingestionServer"       | "" |
    | "educationOrganization" | "123456789012345678901234567890123456789012345678901234567890A" |
    | "educationOrganization" | "" |
    | "path"                  | "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456A" |
    | "path"                  | "" |
    | "desc"                  | "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456A" |


    
    
