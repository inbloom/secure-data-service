require "json"

studentFileString = "student_fixture.json"
studentSchoolAssociationString = "studentSchoolAssociation_fixture.json"
newStudentSchoolAssociationString = "studentSchoolAssociation_superDoc_fixture.json"

if ARGV.length == 0
  # use default files
elsif ARGV.length == 3
  studentFileString = ARGV[0]
  studentSchoolAssociationString = ARGV[1]
  newStudentSchoolAssociationString = ARGV[2]
else
  puts "Usage: #{$0} <student_json_file> <studentSchoolAssociation_json_file> <output_json_file>"
  puts "If no argument is given, the default is:"
  puts "\student_json_file = #{studentFileString}"
  puts "\studentSchoolAssociation_json_file = #{studentSchoolAssociationString}"
  puts "\toutput_json_file = #{newStudentSchoolAssociationString}"
  exit(1)
end

studentFile = File.new(studentFileString)
newStudentSchoolAssociationFile = File.new(newStudentSchoolAssociationString, "w")

while (line = studentFile.gets)
  begin
    student = JSON.parse(line)
  rescue JSON::ParserError => e
    puts "Problem with student file :("
    puts "line is #{line}"
    next
  end
  studentId = student["_id"]
  deterministicId = studentId + "_id"
  
  associations_array = [ ]
  studentSchoolAssociationFile = File.new(studentSchoolAssociationString)
  while (studentSchoolAssociationLine = studentSchoolAssociationFile.gets)
    studentSchoolAssociation = JSON.parse(studentSchoolAssociationLine)
    if studentId == studentSchoolAssociation["body"]["studentId"]
      studentSchoolAssociation["_id"] = deterministicId + studentSchoolAssociation["_id"]
      associations_array << studentSchoolAssociation
    end
  end
  if associations_array.length > 0
    puts "Student: #{studentId} has #{associations_array.length} student-school associations"
    student["studentSchoolAssociation"] = associations_array
  end
  
  student["_id"] = deterministicId
  studentSchoolAssociationFile.close
  newStudentSchoolAssociationFile.puts student.to_json
end

studentFile.close
newStudentSchoolAssociationFile.close