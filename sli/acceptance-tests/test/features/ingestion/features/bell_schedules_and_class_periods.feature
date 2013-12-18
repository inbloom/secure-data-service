Feature: Ingestion SuperDoc Deletion

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