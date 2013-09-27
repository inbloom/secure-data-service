Feature: A bulk extract is triggered for one student with multiple attendance in one day 

  Scenario: Validate that BEs are created for one student with multiple attendance in one day 
    When I ingest "AttendanceEventCategories.zip"
    Then I trigger a bulk extract    
    And I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "772a61c687ee7ecd8e6d9ad3369f7883409f803b_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    And I verify this "attendance" file should contain:
      | id                                          | condition                                                          |
      | f77d258165513ea45687ca9c096484819c413b18_id | schoolYearAttendance.attendanceEvent.event = In Attendance         |
      | f77d258165513ea45687ca9c096484819c413b18_id | schoolYearAttendance.attendanceEvent.event = Early departure       |

