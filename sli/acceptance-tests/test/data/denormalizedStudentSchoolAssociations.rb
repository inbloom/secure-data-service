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

def find_edOrg_lineage(edOrgFixture, edOrgId)
  lineage = []
  edOrgFile = File.new(edOrgFixture)
  while (edOrgLine = edOrgFile.gets)
    begin
      edOrg = JSON.parse(edOrgLine)
    rescue JSON::ParserError => e
      puts "Problem with education organization file --> line is #{edOrgLine}"
      next
    end
  
    if edOrg["_id"] == edOrgId
      lineage << edOrg["_id"]
      lineage << find_edOrg_lineage(edOrgFixture, edOrg["body"]["parentEducationAgencyReference"]) unless edOrg["body"]["parentEducationAgencyReference"].nil?
      break
    end
  end
  edOrgFile.close
  return lineage.flatten.uniq
end

studentFileString                          = "student_fixture.json"
studentSchoolAssociationString             = "studentSchoolAssociation_fixture.json"
educationOrganizationString                = "educationOrganization_fixture.json"
denormalizedStudentSchoolAssociationString = "student_denormalizedSchools_fixture.json"

if ARGV.length == 0
  puts "Using default arguments:"
  puts "\tstudent_json_file                  = #{studentFileString}"
  puts "\tstudentSchoolAssociation_json_file = #{studentSchoolAssociationString}"
  puts "\teducationOrganization_json_file    = #{educationOrganizationString}"
  puts "\toutput_json_file                   = #{denormalizedStudentSchoolAssociationString}"
elsif ARGV.length == 4
  studentFileString                          = ARGV[0]
  studentSchoolAssociationString             = ARGV[1]
  educationOrganizationString                = ARGV[2]
  denormalizedStudentSchoolAssociationString = ARGV[3]
else
  puts "Usage: #{$0} <student_json_file> <studentSchoolAssociation_json_file> <educationOrganization_json_file> <output_json_file>"
  puts "If no argument is given, the default is:"
  puts "\tstudent_json_file                  = #{studentFileString}"
  puts "\tstudentSchoolAssociation_json_file = #{studentSchoolAssociationString}"
  puts "\teducationOrganization_json_file    = #{educationOrganizationString}"
  puts "\toutput_json_file                   = #{denormalizedStudentSchoolAssociationString}"
  exit(1)
end

studentFile    = File.new(studentFileString)
newStudentFile = File.new(denormalizedStudentSchoolAssociationString, "w")

while (line = studentFile.gets)
  begin
    student = JSON.parse(line)
  rescue JSON::ParserError => e
    puts "Problem with student file --> line is #{line}"
    next
  end
  
  schools_array = [ ]
  studentSchoolAssociationFile = File.new(studentSchoolAssociationString)
  while (studentSchoolAssociationLine = studentSchoolAssociationFile.gets)
    studentSchoolAssociation = JSON.parse(studentSchoolAssociationLine)
    if student["_id"] == studentSchoolAssociation["body"]["studentId"]
      school = Hash.new
      school["_id"]              = studentSchoolAssociation["body"]["schoolId"]
      school["entryDate"]        = studentSchoolAssociation["body"]["entryDate"]        unless studentSchoolAssociation["body"]["entryDate"].nil?
      school["entryGradeLevel"]  = studentSchoolAssociation["body"]["entryGradeLevel"]  unless studentSchoolAssociation["body"]["entryGradeLevel"].nil?
      school["exitWithdrawDate"] = studentSchoolAssociation["body"]["exitWithdrawDate"] unless studentSchoolAssociation["body"]["exitWithdrawDate"].nil?
      school["edOrgs"]           = find_edOrg_lineage(educationOrganizationString, studentSchoolAssociation["body"]["schoolId"])
      schools_array << school
      puts "Lineage for ed org: #{school["_id"]} --> #{school["edOrgs"]}"
    end
  end
  
  if schools_array.length > 0
    puts "Student: #{student["_id"]} has #{schools_array.length} student-school associations"
    student["schools"] = schools_array
  end
  
  studentSchoolAssociationFile.close
  newStudentFile.puts student.to_json
end

studentFile.close
newStudentFile.close
