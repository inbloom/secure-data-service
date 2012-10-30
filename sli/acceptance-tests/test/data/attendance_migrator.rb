#!/usr/bin/env ruby

require 'json'

attendance_file = File.new 'attendance_fixture.json'
student_attendance_file = File.new('studentAttendance_fixture.json', 'w')

while (line = JSON.parse(attendance_file.gets))
  student_id = line['body']['studentId']
  school_id = line['body']['schoolId']
  school_year_key = 'schoolYearAttendance'

  school_year_attendance = line['body'][school_year_key]

  if (school_year_attendance.length > 1)
    $stderr.puts 'warning! school year attendance is longer than 1. this will result in duplicate ids'
  end

  base_doc = line.clone
  base_doc['body'].delete(school_year_key)

  school_year_attendance.each do |school_year_doc|
    school_year = school_year_doc['schoolYear']
    base_doc['body']['schoolYear'] = school_year

    attendance_events = school_year_doc['attendanceEvent']
    base_doc['body']['attendanceEvent'] = attendance_events

    student_attendance_file.puts base_doc.to_json
  end
end

attendance_file.close
student_attendance_file.close

