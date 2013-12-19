Feature: Ingestion of Class Periods and Bell Schedules

Scenario: Ingestion of Bell Schedules and Class Periods

#initial ingestion
Given the "Midgar" tenant db is empty
Then I ingest "BellSchedulesAndClassPeriods.zip"
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |            count |
     | bellSchedule                             |                1 |
     | calendarDate                             |                1 |
     | classPeriod                              |                1 |
     | educationOrganization                    |                1 |
     | section                                  |                1 |
     | recordHash                               |                5 |
Then there exist "1" "bellSchedule" records like below in "Midgar" tenant. And I save this query as "bellSchedule"
    |field       													  |value                                               |
    |_id         													  |e63be44d3016df23718ee8aba4382eb9e8dfa2d4_id         |
    |type										                      |bellSchedule                                        |
    |body.gradeLevels.0         									  |First grade                                         |
    |body.gradeLevels.1         									  |Second grade                                        |
    |body.meetingTime.startTime     								  |09:00:00                                            |
    |body.meetingTime.endTime        								  |09:55:00                                            |
    |body.meetingTime.alternateDayName        						  |Maroon                                              |
    |body.meetingTime.classPeriodId          						  |0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id         |
    |body.meetingTime.officialAttendancePeriod          			  |true                                                |
    |body.bellScheduleName          								  |Some Bell Schedule Name                             |
    |body.calendarDateReference    							          |83df3b52534ead7445a26da2a74c5f077d059753_id         |
    |body.educationOrganizationId                                     |2897f482a59f833370562b33e2f7478c3fb25aed_id         |
Then there exist "1" "classPeriod" records like below in "Midgar" tenant. And I save this query as "classPeriod"
    |field       													  |value                                               |
    |_id         													  |0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id         |
    |type										                      |classPeriod                                         |
    |body.classPeriodName                                             |Some Class Period Name                              |
    |body.educationOrganizationId                                     |2897f482a59f833370562b33e2f7478c3fb25aed_id         |
Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
    |field       													  |value                                               |
    |_id         													  |004f6c78a56037e462cee1691ab6251a2bb69222_id         |
    |body.educationalEnvironment                                      |Classroom                                           |
	|body.sessionId                                                   |825e239fdaae87b8f8317659edb39c29e354ee6d_id         |
	|body.courseOfferingId                                            |57a0006dda47488da9b42543185a0675935af99c_id         |
	|body.classPeriodId                                               |0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id         |
	|body.populationServed                                            |Regular Students                                    |
	|body.mediumOfInstruction                                         |Face-to-face instruction                            |
	|body.uniqueSectionCode                                           |12345                                               |
	|body.programReference.0                                          |2e88f28a4eae4d3ee3e54a4b49552b92b043e8f2_id         |
	|body.schoolId                                                    |2897f482a59f833370562b33e2f7478c3fb25aed_id         |
	|body.availableCredit.creditType                                  |Carnegie unit                                       |

#duplicate ingestion
When I post "BellSchedulesAndClassPeriods.zip" file as the payload of the ingestion job
And zip file is scp to ingestion landing zone with name "BellSchedulesAndClassPeriods2.zip"
Then a batch job for file "BellSchedulesAndClassPeriods2.zip" is completed in database
And I should not see an error log file created
And I should not see a warning log file created
And I should see "InterchangeEducationOrganization.xml classPeriod 1 deltas!" in the resulting batch job file
And I should see "InterchangeEducationOrganization.xml educationOrganization 1 deltas!" in the resulting batch job file
And I should see "InterchangeEducationOrgCalendar.xml calendarDate 1 deltas!" in the resulting batch job file
And I should see "InterchangeMasterSchedule.xml bellSchedule 1 deltas!" in the resulting batch job file
And I should see "InterchangeMasterSchedule.xml section 1 deltas!" in the resulting batch job file
And I re-execute saved query "bellSchedule" to get "1" records
And I re-execute saved query "classPeriod" to get "1" records
And I re-execute saved query "section" to get "1" records

#updates through ingestion
Then I ingest "BellScheduleUpdates.zip"
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |            count |
     | bellSchedule                             |                2 |
     | calendarDate                             |                1 |
     | classPeriod                              |                1 |
     | educationOrganization                    |                1 |
     | section                                  |                1 |
     | recordHash                               |                6 |
Then there exist "1" "bellSchedule" records like below in "Midgar" tenant. And I save this query as "bellSchedule"
    |field       													  |value                                               |
    |_id         													  |e63be44d3016df23718ee8aba4382eb9e8dfa2d4_id         |
    |type										                      |bellSchedule                                        |
    |body.gradeLevels.0         									  |First grade                                         |
    |body.meetingTime.startTime     								  |13:00:00                                            |
    |body.meetingTime.endTime        								  |13:55:00                                            |
    |body.meetingTime.classPeriodId          						  |0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id         |
    |body.bellScheduleName          								  |Some Bell Schedule Name                             |
    |body.calendarDateReference    							          |83df3b52534ead7445a26da2a74c5f077d059753_id         |
    |body.educationOrganizationId                                     |2897f482a59f833370562b33e2f7478c3fb25aed_id         |
Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
    |field       													  |value                                               |
    |_id         													  |004f6c78a56037e462cee1691ab6251a2bb69222_id         |
    |body.educationalEnvironment                                      |Classroom                                           |
	|body.sessionId                                                   |825e239fdaae87b8f8317659edb39c29e354ee6d_id         |
	|body.courseOfferingId                                            |57a0006dda47488da9b42543185a0675935af99c_id         |
	|body.classPeriodId                                               |7b22bec65680ef796bb2b3a7dc15a823ec5ea9a0_id         |
	|body.populationServed                                            |Regular Students                                    |
	|body.mediumOfInstruction                                         |Face-to-face instruction                            |
	|body.uniqueSectionCode                                           |12345                                               |
	|body.programReference.0                                          |2e88f28a4eae4d3ee3e54a4b49552b92b043e8f2_id         |
	|body.schoolId                                                    |2897f482a59f833370562b33e2f7478c3fb25aed_id         |
	|body.availableCredit.creditType                                  |Carnegie unit                                       |

#verify class period affects DID resolution
Then there exist "1" "bellSchedule" records like below in "Midgar" tenant. And I save this query as "bellSchedule2"
    |field       													  |value                                               |
    |_id         													  |e3cee19da46a9e6974f7531f78f0f6a621885dbb_id         |
    |type										                      |bellSchedule                                        |
    |body.gradeLevels.0         									  |First grade                                         |
    |body.gradeLevels.1         									  |Second grade                                        |
    |body.meetingTime.startTime     								  |09:00:00                                            |
    |body.meetingTime.endTime        								  |09:55:00                                            |
    |body.meetingTime.alternateDayName        						  |Maroon                                              |
    |body.meetingTime.classPeriodId          						  |df9aa6b223c3b733f0327ece23fa0a2a8e01bd2f_id         |
    |body.meetingTime.officialAttendancePeriod          			  |true                                                |
    |body.bellScheduleName          								  |Some Bell Schedule Name                             |
    |body.calendarDateReference    							          |83df3b52534ead7445a26da2a74c5f077d059753_id         |
    |body.educationOrganizationId                                     |2897f482a59f833370562b33e2f7478c3fb25aed_id         |

#remove classPeriod from Section
Then I ingest "RemoveClassPeriodFromSection.zip"
 And I re-execute saved query "section" to get "0" records
 Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
    |field       													  |value                                               |
    |_id         													  |004f6c78a56037e462cee1691ab6251a2bb69222_id         |
    |body.educationalEnvironment                                      |Classroom                                           |
	|body.sessionId                                                   |825e239fdaae87b8f8317659edb39c29e354ee6d_id         |
	|body.courseOfferingId                                            |57a0006dda47488da9b42543185a0675935af99c_id         |
	|body.populationServed                                            |Regular Students                                    |
	|body.mediumOfInstruction                                         |Face-to-face instruction                            |
	|body.uniqueSectionCode                                           |12345                                               |
	|body.programReference.0                                          |2e88f28a4eae4d3ee3e54a4b49552b92b043e8f2_id         |
	|body.schoolId                                                    |2897f482a59f833370562b33e2f7478c3fb25aed_id         |
	|body.availableCredit.creditType                                  |Carnegie unit                                       |


#update back to original
When I post "BellSchedulesAndClassPeriods.zip" file as the payload of the ingestion job
And zip file is scp to ingestion landing zone with name "BellSchedulesAndClassPeriods3.zip"
Then a batch job for file "BellSchedulesAndClassPeriods3.zip" is completed in database
And I should not see an error log file created
And I should not see a warning log file created
And I should see "InterchangeEducationOrganization.xml classPeriod 1 deltas!" in the resulting batch job file
And I should see "InterchangeEducationOrganization.xml educationOrganization 1 deltas!" in the resulting batch job file
And I should see "InterchangeEducationOrgCalendar.xml calendarDate 1 deltas!" in the resulting batch job file
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |            count |
     | bellSchedule                             |                2 |
     | calendarDate                             |                1 |
     | classPeriod                              |                1 |
     | educationOrganization                    |                1 |
     | section                                  |                1 |
     | recordHash                               |                6 |
Then there exist "1" "bellSchedule" records like below in "Midgar" tenant. And I save this query as "bellSchedule"
    |field       													  |value                                               |
    |_id         													  |e63be44d3016df23718ee8aba4382eb9e8dfa2d4_id         |
    |type										                      |bellSchedule                                        |
    |body.gradeLevels.0         									  |First grade                                         |
    |body.gradeLevels.1         									  |Second grade                                        |
    |body.meetingTime.startTime     								  |09:00:00                                            |
    |body.meetingTime.endTime        								  |09:55:00                                            |
    |body.meetingTime.alternateDayName        						  |Maroon                                              |
    |body.meetingTime.classPeriodId          						  |0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id         |
    |body.meetingTime.officialAttendancePeriod          			  |true                                                |
    |body.bellScheduleName          								  |Some Bell Schedule Name                             |
    |body.calendarDateReference    							          |83df3b52534ead7445a26da2a74c5f077d059753_id         |
    |body.educationOrganizationId                                     |2897f482a59f833370562b33e2f7478c3fb25aed_id         |
And I re-execute saved query "bellSchedule2" to get "1" records

#safe deletes of other entities
Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
    | field                                            |value                                                |
    | _id                                              | 2897f482a59f833370562b33e2f7478c3fb25aed_id         |
Then there exist "1" "calendarDate" records like below in "Midgar" tenant. And I save this query as "calendarDate"
    | field                                            |value                                                |
    | _id                                              | 83df3b52534ead7445a26da2a74c5f077d059753_id         |
And I save the collection counts in "Midgar" tenant
And I post "SafeDeleteOfEntitiesReferredByBellSchedule.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
And a batch job for file "SafeDeleteOfEntitiesReferredByBellSchedule.zip" is completed in database
And I should see "InterchangeEducationOrganization.xml records deleted successfully: 0" in the resulting batch job file
And I should see "InterchangeEducationOrganization.xml records failed processing: 1" in the resulting batch job file
And I should see "InterchangeEducationOrgCalendar.xml records deleted successfully: 0" in the resulting batch job file
And I should see "InterchangeEducationOrgCalendar.xml records failed processing: 1" in the resulting batch job file
And I should see "Not all records were processed completely due to errors." in the resulting batch job file
And I should see "Processed 2 records." in the resulting batch job file
And I should see "CORE_0066" in the resulting error log file for "InterchangeEducationOrganization.xml"
And I should see "Child reference of entity type bellSchedule, ID e3cee19da46a9e6974f7531f78f0f6a621885dbb_id, exists at depth 1 for entity type educationOrganization, ID 2897f482a59f833370562b33e2f7478c3fb25aed_id" in the resulting error log file for "InterchangeEducationOrganization.xml"
And I should see "Child reference of entity type bellSchedule, ID e63be44d3016df23718ee8aba4382eb9e8dfa2d4_id, exists at depth 1 for entity type educationOrganization, ID 2897f482a59f833370562b33e2f7478c3fb25aed_id" in the resulting error log file for "InterchangeEducationOrganization.xml"
And I should see "Child reference of entity type classPeriod, ID 0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id, exists at depth 1 for entity type educationOrganization, ID 2897f482a59f833370562b33e2f7478c3fb25aed_id" in the resulting error log file for "InterchangeEducationOrganization.xml"
And I should see "CORE_0066" in the resulting error log file for "InterchangeEducationOrgCalendar.xml"
And I should see "Child reference of entity type bellSchedule, ID e63be44d3016df23718ee8aba4382eb9e8dfa2d4_id, exists at depth 1 for entity type calendarDate, ID 83df3b52534ead7445a26da2a74c5f077d059753_id" in the resulting error log file for "InterchangeEducationOrgCalendar.xml"
And I should see "Child reference of entity type bellSchedule, ID e3cee19da46a9e6974f7531f78f0f6a621885dbb_id, exists at depth 1 for entity type calendarDate, ID 83df3b52534ead7445a26da2a74c5f077d059753_id" in the resulting error log file for "InterchangeEducationOrgCalendar.xml"
And I should not see a warning log file created
And I re-execute saved query "educationOrganization" to get "1" records
And I re-execute saved query "calendarDate" to get "1" records
And I see that collections counts have changed as follows in tenant "Midgar"
    |collection                        | delta    |
    | calendarDate                     |         0|
    | educationOrganization            |         0|
    | recordHash                       |         0|

#force deletes of other entities
Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
    | field                                            |value                                                |
    | _id                                              | 2897f482a59f833370562b33e2f7478c3fb25aed_id         |
Then there exist "1" "calendarDate" records like below in "Midgar" tenant. And I save this query as "calendarDate"
    | field                                            |value                                                |
    | _id                                              | 83df3b52534ead7445a26da2a74c5f077d059753_id         |
And I save the collection counts in "Midgar" tenant
And the landing zone for tenant "Midgar" edOrg "Daybreak" is reinitialized
And I post "ForceDeleteOfEntitiesReferredByBellSchedule.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
And a batch job for file "ForceDeleteOfEntitiesReferredByBellSchedule.zip" is completed in database
And I should see "InterchangeEducationOrganization.xml records considered for processing: 1" in the resulting batch job file
And I should see "InterchangeEducationOrganization.xml records deleted successfully: 1" in the resulting batch job file
And I should see "InterchangeEducationOrgCalendar.xml records considered for processing: 1" in the resulting batch job file
And I should see "InterchangeEducationOrgCalendar.xml records deleted successfully: 1" in the resulting batch job file
And I should see "All records processed successfully" in the resulting batch job file
And I should see "Processed 2 records." in the resulting batch job file
And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrganization.xml"
And I should see "Child reference of entity type bellSchedule, ID e3cee19da46a9e6974f7531f78f0f6a621885dbb_id, exists at depth 1 for entity type educationOrganization, ID 2897f482a59f833370562b33e2f7478c3fb25aed_id" in the resulting warning log file for "InterchangeEducationOrganization.xml"
And I should see "Child reference of entity type bellSchedule, ID e63be44d3016df23718ee8aba4382eb9e8dfa2d4_id, exists at depth 1 for entity type educationOrganization, ID 2897f482a59f833370562b33e2f7478c3fb25aed_id" in the resulting warning log file for "InterchangeEducationOrganization.xml"
And I should see "Child reference of entity type classPeriod, ID 0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id, exists at depth 1 for entity type educationOrganization, ID 2897f482a59f833370562b33e2f7478c3fb25aed_id" in the resulting warning log file for "InterchangeEducationOrganization.xml"
And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrgCalendar.xml"
And I should see "Child reference of entity type bellSchedule, ID e63be44d3016df23718ee8aba4382eb9e8dfa2d4_id, exists at depth 1 for entity type calendarDate, ID 83df3b52534ead7445a26da2a74c5f077d059753_id" in the resulting warning log file for "InterchangeEducationOrgCalendar.xml"
And I should see "Child reference of entity type bellSchedule, ID e3cee19da46a9e6974f7531f78f0f6a621885dbb_id, exists at depth 1 for entity type calendarDate, ID 83df3b52534ead7445a26da2a74c5f077d059753_id" in the resulting warning log file for "InterchangeEducationOrgCalendar.xml"
And I should not see an error log file created
And I re-execute saved query "educationOrganization" to get "0" records
And I re-execute saved query "calendarDate" to get "0" records
And I see that collections counts have changed as follows in tenant "Midgar"
    |collection                        | delta    |
    | calendarDate                     |       -1 |
    | educationOrganization            |       -1 |
    | recordHash                       |       -2 |

#reset back
And the landing zone for tenant "Midgar" edOrg "Daybreak" is reinitialized
When I post "BellSchedulesAndClassPeriods.zip" file as the payload of the ingestion job
And zip file is scp to ingestion landing zone with name "BellSchedulesAndClassPeriods4.zip"
Then a batch job for file "BellSchedulesAndClassPeriods4.zip" is completed in database
And I should not see an error log file created
And I should not see a warning log file created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |            count |
     | bellSchedule                             |                2 |
     | calendarDate                             |                1 |
     | classPeriod                              |                1 |
     | educationOrganization                    |                1 |
     | section                                  |                1 |
     | recordHash                               |                6 |
And I re-execute saved query "bellSchedule" to get "1" records
And I re-execute saved query "bellSchedule2" to get "1" records

#deletes
And I save the collection counts in "Midgar" tenant
Then I ingest "BellScheduleDeletes.zip"
And I re-execute saved query "bellSchedule" to get "0" records
And I re-execute saved query "bellSchedule2" to get "0" records
And I see that collections counts have changed as follows in tenant "Midgar"
    |collection                        | delta    |
    | bellSchedule                     |       -2 |
    | recordHash                       |       -2 |

#reset back
And the landing zone for tenant "Midgar" edOrg "Daybreak" is reinitialized
When I post "BellSchedulesAndClassPeriods.zip" file as the payload of the ingestion job
And zip file is scp to ingestion landing zone with name "BellSchedulesAndClassPeriods5.zip"
Then a batch job for file "BellSchedulesAndClassPeriods5.zip" is completed in database
And I should not see an error log file created
And I should not see a warning log file created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |            count |
     | bellSchedule                             |                1 |
     | calendarDate                             |                1 |
     | classPeriod                              |                1 |
     | educationOrganization                    |                1 |
     | section                                  |                1 |
     | recordHash                               |                5 |
And I re-execute saved query "bellSchedule" to get "1" records

#safe delete of class period by full body
Then there exist "1" "classPeriod" records like below in "Midgar" tenant. And I save this query as "classPeriod"
    |field       													  |value                                               |
    |_id         													  |0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id         |
    |type										                      |classPeriod                                         |
    |body.classPeriodName                                             |Some Class Period Name                              |
    |body.educationOrganizationId                                     |2897f482a59f833370562b33e2f7478c3fb25aed_id         |
And I save the collection counts in "Midgar" tenant
And I post "ClassPeriodSafeDeleteByFullBody.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
And a batch job for file "ClassPeriodSafeDeleteByFullBody.zip" is completed in database
And I should see "InterchangeEducationOrganization.xml records deleted successfully: 0" in the resulting batch job file
And I should see "InterchangeEducationOrganization.xml records failed processing: 1" in the resulting batch job file
And I should see "Not all records were processed completely due to errors." in the resulting batch job file
And I should see "Processed 1 records." in the resulting batch job file
And I should see "CORE_0066" in the resulting error log file for "InterchangeEducationOrganization.xml"
And I should see "Child reference of entity type bellSchedule, ID e63be44d3016df23718ee8aba4382eb9e8dfa2d4_id, exists at depth 1 for entity type classPeriod, ID 0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id" in the resulting error log file for "InterchangeEducationOrganization.xml"
And I should see "Child reference of entity type section, ID 004f6c78a56037e462cee1691ab6251a2bb69222_id, exists at depth 1 for entity type classPeriod, ID 0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id" in the resulting error log file for "InterchangeEducationOrganization.xml"
And I should not see a warning log file created
And I re-execute saved query "classPeriod" to get "1" records
And I see that collections counts have changed as follows in tenant "Midgar"
    |collection                        | delta    |
    | classPeriod                      |        0 |
    | recordHash                       |        0 |

#force delete of class period by reference
Then there exist "1" "classPeriod" records like below in "Midgar" tenant. And I save this query as "classPeriod"
    |field       													  |value                                               |
    |_id         													  |0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id         |
    |type										                      |classPeriod                                         |
    |body.classPeriodName                                             |Some Class Period Name                              |
    |body.educationOrganizationId                                     |2897f482a59f833370562b33e2f7478c3fb25aed_id         |
And I save the collection counts in "Midgar" tenant
And the landing zone for tenant "Midgar" edOrg "Daybreak" is reinitialized
And I post "ClassPeriodForceDeleteByReference.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
And a batch job for file "ClassPeriodForceDeleteByReference.zip" is completed in database
And I should see "InterchangeEducationOrganization.xml records considered for processing: 1" in the resulting batch job file
And I should see "InterchangeEducationOrganization.xml records deleted successfully: 1" in the resulting batch job file
And I should see "All records processed successfully" in the resulting batch job file
And I should see "Processed 1 records." in the resulting batch job file
And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrganization.xml"
And I should see "Child reference of entity type bellSchedule, ID e63be44d3016df23718ee8aba4382eb9e8dfa2d4_id, exists at depth 1 for entity type classPeriod, ID 0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id" in the resulting warning log file for "InterchangeEducationOrganization.xml"
And I should see "Child reference of entity type section, ID 004f6c78a56037e462cee1691ab6251a2bb69222_id, exists at depth 1 for entity type classPeriod, ID 0c7523f4f74e6e5de117b6af88115cf98b5b1e2c_id" in the resulting warning log file for "InterchangeEducationOrganization.xml"
And I should not see an error log file created
And I re-execute saved query "classPeriod" to get "0" records
And I see that collections counts have changed as follows in tenant "Midgar"
    |collection                        | delta    |
    | classPeriod                      |       -1 |
    | recordHash                       |       -1 |
    
#orphan delete of class period
Then I ingest "ClassPeriodsOrphan.zip"
Then there exist "1" "classPeriod" records like below in "Midgar" tenant. And I save this query as "classPeriod"
    |field       													  |value                                               |
    |_id         													  |9d3e57cdb4e23b4693e26b9603f611b44af8e6a7_id         |
    |type										                      |classPeriod                                         |
    |body.classPeriodName                                             |Megatron Class Period Name                          |
    |body.educationOrganizationId                                     |2fe47c8e78a65ee51a72628c170673c35c4bd85a_id         |
And I save the collection counts in "Midgar" tenant
And the landing zone for tenant "Midgar" edOrg "Daybreak" is reinitialized
And I post "ClassPeriodsOrphanDelete.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
And a batch job for file "ClassPeriodsOrphanDelete.zip" is completed in database
And I should see "InterchangeEducationOrganization.xml records considered for processing: 1" in the resulting batch job file
And I should see "InterchangeEducationOrganization.xml records deleted successfully: 1" in the resulting batch job file
And I should see "All records processed successfully" in the resulting batch job file
And I should see "Processed 1 records." in the resulting batch job file
And I should not see an error log file created
And I should not see a warning log file created
And I re-execute saved query "classPeriod" to get "0" records
And I see that collections counts have changed as follows in tenant "Midgar"
    |collection                        | delta    |
    | classPeriod                      |       -1 |
    | recordHash                       |       -1 |

 
 
 
    
