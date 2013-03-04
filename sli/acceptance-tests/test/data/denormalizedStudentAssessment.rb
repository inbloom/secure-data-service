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

require "json"


studentFileString                          = "student_fixture.json"
studentAssessmentString                    = "studentAssessment_fixture.json"
denormalizedStudentAssessmentString        = "student_denormalizedStudentAssessment_fixture.json"

if ARGV.length == 0
  puts "Using default arguments:"
  puts "\tstudent_json_file                  = #{studentFileString}"
  puts "\tstudentAssessment_json_file        = #{studentAssessmentString}"
  puts "\toutput_json_file                   = #{denormalizedStudentAssessmentString}"
elsif ARGV.length == 3
  studentFileString                          = ARGV[0]
  studentAssessmentString                    = ARGV[1]
  denormalizedStudentAssessmentString        = ARGV[2]
else
  puts "Usage: #{$0} <student_json_file> <studentAssessment_json_file> <output_json_file>"
  puts "If no argument is given, the default is:"
  puts "\tstudent_json_file                  = #{studentFileString}"
  puts "\tstudentAssessment_json_file        = #{studentAssessmentString}"
  puts "\toutput_json_file                   = #{denormalizedStudentAssessmentString}"
  exit(1)
end

studentFile    = File.new(studentFileString)
newStudentFile = File.new(denormalizedStudentAssessmentString, "w")

while (line = studentFile.gets)
  begin
    student = JSON.parse(line)
  rescue JSON::ParserError => e
    puts "Problem with student file --> line is #{line}"
    next
  end
  
 studentAssessment_array = [ ]
  studentAssessmentFile = File.new(studentAssessmentString)
  while (studentAssessmentLine = studentAssessmentFile.gets)
    studentAssessment = JSON.parse(studentAssessmentLine)
    if student["_id"] == studentAssessment["body"]["studentId"]
      sa =  Hash.new
      sa["_id"]              = studentAssessment["_id"]
      sa["administrationDate"]        = studentAssessment["body"]["administrationDate"]        unless studentAssessment["body"]["administrationDate"].nil?
      studentAssessment_array << sa 
    end
  end
  
  if studentAssessment_array.length > 0
    puts "Student: #{student["_id"]} has #{studentAssessment_array.length} student-assessment associations"
    student["studentAssessment"] = studentAssessment_array
  end
  
  studentAssessmentFile.close
  newStudentFile.puts student.to_json
end

studentFile.close
newStudentFile.close
