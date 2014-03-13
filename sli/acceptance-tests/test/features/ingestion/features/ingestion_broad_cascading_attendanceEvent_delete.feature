@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete AttendanceEvent with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |_id                                       |94de66549a6b58f96463ff0d59b34817aa1fead6_id                                           |
        |body.attendanceEvent.date                 |2001-09-13                                                                            |

    And I check the number of elements in array of collection:
    | collectionName   |field  |value                                       |expectedRecordCount|searchContainer       |
    | attendance       |_id    |94de66549a6b58f96463ff0d59b34817aa1fead6_id | 22                | body.attendanceEvent |

    And I save the collection counts in "Midgar" tenant
    And I post "SafeAttendanceEventDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeAttendanceEventDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "attendance" to get "0" records 
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | attendance                                |         0|
        | attendanceEvent                           |        -1|
        | recordHash                                |        -1|     

    And I check the number of elements in array of collection:
    | collectionName   |field  |value                                       |expectedRecordCount|searchContainer       |
    | attendance       |_id    |94de66549a6b58f96463ff0d59b34817aa1fead6_id | 21                | body.attendanceEvent |     

Scenario: Delete AttendanceEvent by ref with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |_id                                       |94de66549a6b58f96463ff0d59b34817aa1fead6_id                                           |
        |body.attendanceEvent.date                 |2001-09-13                                                                            |

    And I check the number of elements in array of collection:
    | collectionName   |field  |value                                       |expectedRecordCount|searchContainer       |
    | attendance       |_id    |94de66549a6b58f96463ff0d59b34817aa1fead6_id | 22                | body.attendanceEvent |

    And I save the collection counts in "Midgar" tenant
    And I post "SafeAttendanceEventRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeAttendanceEventRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "attendance" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | attendance                                |         0|
        | attendanceEvent                           |        -1|
        | recordHash                                |        -1|

    And I check the number of elements in array of collection:
    | collectionName   |field  |value                                       |expectedRecordCount|searchContainer       |
    | attendance       |_id    |94de66549a6b58f96463ff0d59b34817aa1fead6_id | 21                | body.attendanceEvent |

Scenario: Delete Orphan AttendanceEvent with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |_id                                       |69356617a72b0d74b14d4fe256d411821a04b6ba_id                                           |
        |body.attendanceEvent.date                 |2011-12-25                                                                            |
    And I check the number of elements in array of collection:
        | collectionName   |field  |value                                       |expectedRecordCount|searchContainer      |
        | attendance       |_id    |69356617a72b0d74b14d4fe256d411821a04b6ba_id | 1                | body.attendanceEvent |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanAttendanceEventDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanAttendanceEventDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "attendance" to get "0" records 
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | attendance                                |        -1|
        | attendanceEvent                           |        -1|         
        | recordHash                                |        -1| 
    And a query on attendance of for studentId "69356617a72b0d74b14d4fe256d411821a04b6ba_id", schoolYear "2011-2012" and date "2011-12-25" on the "Midgar" tenant has a count of "0"

Scenario: Delete Orphan AttendanceEvent by ref (no section ref ingested) with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |_id                                       |71ca8f7bf0738fdd72ff09858365ef87b4bbb178_id                                           |
        |body.attendanceEvent.date                 |2001-12-25                                                                            |
    And I check the number of elements in array of collection:
        | collectionName   |field  |value                                       |expectedRecordCount|searchContainer      |
        | attendance       |_id    |71ca8f7bf0738fdd72ff09858365ef87b4bbb178_id | 1                | body.attendanceEvent |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanAttendanceEventRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanAttendanceEventRefDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "attendance" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | attendance                                |        -1|
        | attendanceEvent                           |        -1|
        | recordHash                                |        -1|
    And a query on attendance of for studentId "71ca8f7bf0738fdd72ff09858365ef87b4bbb178_id", schoolYear "2001-2002" and date "2001-12-25" on the "Midgar" tenant has a count of "0"

Scenario: Force delete AttendanceEvent with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |_id                                       |94de66549a6b58f96463ff0d59b34817aa1fead6_id                                           |
        |body.attendanceEvent.date                 |2001-09-13                                                                            |

    And I check the number of elements in array of collection:
    | collectionName   |field  |value                                       |expectedRecordCount|searchContainer       |
    | attendance       |_id    |94de66549a6b58f96463ff0d59b34817aa1fead6_id | 22                | body.attendanceEvent |

    And I save the collection counts in "Midgar" tenant
    And I post "ForceAttendanceEventDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceAttendanceEventDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "attendance" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | attendance                                |         0|
        | attendanceEvent                           |        -1|
        | recordHash                                |        -1|

    And I check the number of elements in array of collection:
    | collectionName   |field  |value                                       |expectedRecordCount|searchContainer       |
    | attendance       |_id    |94de66549a6b58f96463ff0d59b34817aa1fead6_id | 21                | body.attendanceEvent |

Scenario: Force delete AttendanceEvent by ref with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |_id                                       |94de66549a6b58f96463ff0d59b34817aa1fead6_id                                           |
        |body.attendanceEvent.date                 |2001-09-13                                                                            |

    And I check the number of elements in array of collection:
    | collectionName   |field  |value                                       |expectedRecordCount|searchContainer       |
    | attendance       |_id    |94de66549a6b58f96463ff0d59b34817aa1fead6_id | 22                | body.attendanceEvent |

    And I save the collection counts in "Midgar" tenant
    And I post "ForceAttendanceEventRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceAttendanceEventRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "attendance" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | attendance                                |         0|
        | attendanceEvent                           |        -1|
        | recordHash                                |        -1|

    And I check the number of elements in array of collection:
    | collectionName   |field  |value                                       |expectedRecordCount|searchContainer       |
    | attendance       |_id    |94de66549a6b58f96463ff0d59b34817aa1fead6_id | 21                | body.attendanceEvent |