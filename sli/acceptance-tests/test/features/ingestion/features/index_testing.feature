Feature: Ingestion Index Test

Scenario: Create Indexes
Given the following collections are empty in datastore:
     | collectionName                             |
     | assessment                                 |
     | attendance                                 |
     | course                                     |
     | educationOrganization                      |
     | gradebookEntry                             |
     | parent                                     |
     | school                                     |
     | section                                    |
     | section                                    |
     | session                                    |
     | staff                                      |
     | staffEducationOrganizationAssociation      |
     | student                                    |
     | studentAssessmentAssociation               |
     | studentParentAssociation                   |
     | studentSchoolAssociation                   |
     | studentSchoolAssociation                   |
     | studentSchoolAssociation                   |
     | studentSectionAssociation                  |
     | studentSectionAssociation                  |
     | studentSectionAssociation                  |
     | studentGradebookEntry                      |
     | studentTranscriptAssociation               |
     | teacher                                    |
     | teacherSectionAssociation                  |

Then I should see following map of indexes in the corresponding collections:
     | collectionName                             | index                                                                |
     | assessment                                 | metaData.externalId_1                            |
     | attendance                                 | metaData.externalId_1                            |
     | course                                     | metaData.externalId_1                            |
     | educationOrganization                      | metaData.externalId_1                            |
     | gradebookEntry                             | metaData.externalId_1                            |
     | parent                                     | metaData.externalId_1                            |
     | school                                     | metaData.externalId_1                            |
     | section                                    | metaData.externalId_1                            |
     | section                                    | body.schoolId_1_metaData.externalId_1            |
     | section                                    | body.courseId_1_metaData.externalId_1            |
     | session                                    | metaData.externalId_1                            |
     | staff                                      | metaData.externalId_1                            |
     | staffEducationOrganizationAssociation      | metaData.externalId_1                            |
     | student                                    | metaData.externalId_1                            |
     | studentAssessmentAssociation               | metaData.externalId_1                            |
     | studentParentAssociation                   | body.parentId_1_body.studentId_1_metaData.externalId_1               |
     | studentSchoolAssociation                   | body.schoolId_1_metaData.externalId_1            |
     | studentSchoolAssociation                   | body.studentId_1_metaData.externalId_1           |
     | studentSchoolAssociation                   | metaData.externalId_1                            |
     | studentSectionAssociation                  | body.sectionId_1_metaData.externalId_1           |
     | studentSectionAssociation                  | body.studentId_1_body.sectionId_1                |
     | studentSectionAssociation                  | body.studentId_1_metaData.externalId_1           |
     | studentGradebookEntry                      | metaData.externalId_1                            |
     | studentTranscriptAssociation               | body.studentId_1_body.courseId_1                 |
     | teacher                                    | metaData.externalId_1                            |
     | teacherSectionAssociation                  | body.teacherId_1_body.sectionId_1                |