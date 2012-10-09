@RALLY_US2281 @RALLY_US206
Feature: Complete onboarding workflow for sandbox and production

Background: 
Given I have an open web browser
#And I have a "mock" SMTP/Email server configured
And I have a SMTP/Email server configured


@sandbox
Scenario: Developer is on-boarded in a sandbox environment
Given I go to the sandbox account registration page
And there is no registered account for "<USER_EMAIL>" in the SLI database
And there is no registered account for "<USER_EMAIL>" in LDAP
And the developer type in first name "<USER_FIRSTNAME>" and last name "<USER_LASTNAME>"
And the developer type in email "<USER_EMAIL>" and password "<USER_PASS>" 
And the developer submits the account registration request
Then the developer is redirected to a page with terms and conditions
When the developer click "Accept" 
Then the developer is directed to an acknowledgement page. 
 And a verification email is sent to "<USER_EMAIL>"
When the developer click link in verification email in "sandbox"
Then an account entry is made in ldap with "Approved" status
And a "sandbox" approval email is sent to the "<USER_EMAIL>"
And the email has a "<URL_TO_PORTAL>"
#TODO: The portal for development is linked to RC which uses a different LDAP than dev. So we are breaking the flow and jump directly to the correct provisioning app.
#And the email has a "<URL_TO_PROVISIONING_APPLICATION>"
And a "<APPLICATION_DEVELOPER>" roles is a added for the user in ldap
When the user clicks on "<URL_TO_PROVISIONING_APPLICATION>"
Then the user has to authenticate against ldap using "<USER_EMAIL>" and "<USER_PASS>"

When the user selects the option to use the "<ED-ORG_SAMPLE_DS1>"
And clicks on "Provision" 
Then an "<ED-ORG_SAMPLE_DS1>" is saved to sandbox mongo
#And an "<ED-ORG_SAMPLE_DS1>" is added in the application table for "<DASHBOARD_APP>"," <ADMIN_APP>", "<DATABROWSER_APP>"
And a request for a Landing zone is made with "<Tenant_ID>" and "<ED-ORG_SAMPLE_DS1>"
And a tenant entry with "<Tenant_ID>" and "<Landing_zone_directory>" is added to mongo
And the landing zone "<Landing_zone_directory>" is saved in Ldap
And the tenantId "<Tenant_ID>" is saved in Ldap
And the sandbox db should have the following map of indexes in the corresponding collections:
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

@sandbox
Scenario: Developer logs in after on-boarding on sandbox
Given the user has an approved sandbox account
When the user accesses the "<URL_TO_ADMIN_APP>"
Then the user has to authenticate against ldap using "<USER_EMAIL>" and "<USER_PASS>"
And the user is redirected to "<URL_TO_ADMIN_APP>"

#When the user is successfully authenticated
#Then the user can access "<DASHBOARD_APP>", "<DATABROWSER_APP>"

@sandbox
Scenario: Developer is able to register applications on sandbox
Given the user has an approved sandbox account
When the user clicks on "<URL_TO_APPLICATION_REGISTRATION>"
Then the user has to authenticate against ldap using "<USER_EMAIL>" and "<USER_PASS>"
And the user is redirected to "<URL_TO_APPLICATION_REGISTRATION>"

@production
Scenario: Vendor registers on a production environment
Given I go to the production account registration page
And there is no registered account for "<USER_EMAIL>" in the SLI database
And there is no registered account for "<USER_EMAIL>" in LDAP
And the developer type in first name "<USER_FIRSTNAME>" and last name "<USER_LASTNAME>"
And the developer type in email "<USER_EMAIL>" and password "<USER_PASS>" 
And the developer submits the account registration request
Then the developer is redirected to a page with terms and conditions
When the developer click "Accept" 
Then the developer is directed to an acknowledgement page. 
And a verification email is sent to "<USER_EMAIL>"
When the developer click link in verification email in "production"
Then an account entry is made in ldap with "pending" status
When the SLC operator accesses the "<ACCOUNT_MANAGEMENT_APP>"
And the SLC operator authenticates as "<SLC_OPERATOR_USER>" and "<SLC_OPERATOR_PASS>"
And the SLC operator approves the vendor account for "<USER_EMAIL>"
Then a "production" approval email is sent to the "<USER_EMAIL>"
And the email has a "<URL_TO_PORTAL>"

@production
Scenario: District admin provisions LZ for an Ed-Org
Given the "<DISTRICT_ADMIN_USER>" has "<STATE_ED_ORG>" defined in LDAP by the operator
When the state super admin accesses the "<URL_TO_PROVISIONING_APPLICATION>"
Then the state super admin authenticates as "<DISTRICT_ADMIN_USER>" and "<DISTRICT_ADMIN_PASS>"
And clicks on "Provision" 
Then an "<STATE_ED_ORG>" is saved to mongo
And a request for a Landing zone is made with "<Tenant_ID>" and "<STATE_ED_ORG>"
And a tenant entry with "<Prod_Tenant_ID>" and "<Prod_Landing_zone_directory>" is added to mongo
And the landing zone "<Prod_Landing_zone_directory>" is saved in Ldap
And the tenantId "<Prod_Tenant_ID>" is saved in Ldap





