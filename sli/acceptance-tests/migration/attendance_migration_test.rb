#!/usr/bin/env ruby

require 'json'
require 'test/unit'
require './attendance_migration'


class TestAttendanceMigration < Test::Unit::TestCase

  def test_migrate
    actual = Migration.migrate simple_input
    assert_equal(actual, simple_output)
  end

  private
    def test_input
      input = <<-EOS
        { "_id":"4beb72d4-0f76-4071-92b4-61982dba7a7b", "body":{ "studentId":"0c2756fd-6a30-4010-af79-488d6ef2735a_id", "schoolId":"eb3b8c35-f582-df23-e406-6947249a19f2", "schoolYearAttendance":[ { "schoolYear":"2011-2012", "attendanceEvent":[ { "date":"2011-09-06", "event":"In Attendance" } ], "schoolYear":"2010-2011", "attendanceEvent":[ { "date":"2011-09-06", "event":"In Attendance" } ] } ] }, "metaData":{ "created":{ "$date":1330955261574 }, "updated":{ "$date":1330955261574 } }, "type":"attendance" }
      EOS
      JSON.parse(input)
    end
    
    def exepected_output
      output = <<-EOS
        [{ "_id":"4beb72d4-0f76-4071-92b4-61982dba7a7b", "body":{ "studentId":"0c2756fd-6a30-4010-af79-488d6ef2735a_id", "schoolId":"eb3b8c35-f582-df23-e406-6947249a19f2", "schoolYear":"2011-2012", "attendanceEvent":[ { "date":"2011-09-06", "event":"In Attendance" } ] }, "metaData":{ "created":{ "$date":1330955261574 }, "updated":{ "$date":1330955261574 } }, "type":"attendance" }, { "_id":"4beb72d4-0f76-4071-92b4-61982dba7a7b", "body":{ "studentId":"0c2756fd-6a30-4010-af79-488d6ef2735a_id", "schoolId":"eb3b8c35-f582-df23-e406-6947249a19f2", "schoolYear":"2011-2012", "attendanceEvent":[ { "date":"2011-09-06", "event":"In Attendance" } ] }, "metaData":{ "created":{ "$date":1330955261574 }, "updated":{ "$date":1330955261574 } }, "type":"attendance" } ]
      EOS
      JSON.parse(output)
    end

    def simple_input
      input = <<-EOS
        { "_id":"4beb72d4-0f76-4071-92b4-61982dba7a7b", "body":{ "studentId":"0c2756fd-6a30-4010-af79-488d6ef2735a_id", "schoolId":"eb3b8c35-f582-df23-e406-6947249a19f2", "schoolYearAttendance":[ { "schoolYear":"2011-2012", "attendanceEvent":[ { "date":"2011-09-06", "event":"In Attendance" } ] } ] }, "metaData":{ "created":{ "$date":1330955261574 }, "updated":{ } }, "type":"attendance" }
      EOS
      JSON.parse(input)
    end
    
    def simple_output
      output = <<-EOS
        { "_id":"4beb72d4-0f76-4071-92b4-61982dba7a7b", "body":{ "studentId":"0c2756fd-6a30-4010-af79-488d6ef2735a_id", "schoolId":"eb3b8c35-f582-df23-e406-6947249a19f2", "schoolYear":"2011-2012", "attendanceEvent":[ { "date":"2011-09-06", "event":"In Attendance" } ] }, "metaData":{ "created":{ "$date":1330955261574 }, "updated":{ } }, "type":"attendance" }
      EOS
      JSON.parse(output)
    end
end
