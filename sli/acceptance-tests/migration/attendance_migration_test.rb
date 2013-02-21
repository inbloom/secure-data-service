#!/usr/bin/env ruby
=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

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
