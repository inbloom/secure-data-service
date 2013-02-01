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
studentCohortAssociationString             = "studentCohortAssociation_fixture.json"
denormalizedStudentCohortAssociationString = "student_denormalizedSchools_fixture.json"

if ARGV.length == 0
  puts "Using default arguments:"
  puts "\tstudent_json_file                  = #{studentFileString}"
  puts "\tstudentCohortAssociation_json_file = #{studentCohortAssociationString}"
  puts "\toutput_json_file                   = #{denormalizedStudentCohortAssociationString}"
elsif ARGV.length == 3
  studentFileString                          = ARGV[0]
  studentCohortAssociationString             = ARGV[1]
  denormalizedStudentCohortAssociationString = ARGV[2]
else
  puts "Usage: #{$0} <student_json_file> <studentCohortAssociation_json_file> <output_json_file>"
  puts "If no argument is given, the default is:"
  puts "\tstudent_json_file                  = #{studentFileString}"
  puts "\tstudentCohortAssociation_json_file = #{studentCohortAssociationString}"
  puts "\toutput_json_file                   = #{denormalizedStudentCohortAssociationString}"
  exit(1)
end

studentFile    = File.new(studentFileString)
newStudentFile = File.new(denormalizedStudentCohortAssociationString, "w")

while (line = studentFile.gets)
  begin
    student = JSON.parse(line)
  rescue JSON::ParserError => e
    puts "Problem with student file --> line is #{line}"
    next
  end
  
  cohorts_array = [ ]
  studentCohortAssociationFile = File.new(studentCohortAssociationString)
  while (studentCohortAssociationLine = studentCohortAssociationFile.gets)
    studentCohortAssociation = JSON.parse(studentCohortAssociationLine)
    if student["_id"] == studentCohortAssociation["body"]["studentId"]
      cohort = Hash.new
      cohort["_id"]              = studentCohortAssociation["body"]["cohortId"]
      cohort["beginDate"]        = studentCohortAssociation["body"]["beginDate"]        unless studentCohortAssociation["body"]["beginDate"].nil?
      cohort["endDate"] = studentCohortAssociation["body"]["endDate"] unless studentCohortAssociation["body"]["endDate"].nil?
      cohorts_array << cohort
    end
  end
  
  if cohorts_array.length > 0
    puts "Student: #{student["_id"]} has #{cohorts_array.length} student-cohort associations"
    student["cohort"] = cohorts_array
  end
  
  studentCohortAssociationFile.close
  newStudentFile.puts student.to_json
end

studentFile.close
newStudentFile.close
