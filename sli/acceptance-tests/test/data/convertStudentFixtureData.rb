require "json"

studentFileString = "student_fixture.json"
newStudentFileString = "student_converted_fixture.json"

if ARGV.length == 0
  # use default files
elsif ARGV.length == 2
  studentFileString = ARGV[0]
  newStudentFileString = ARGV[1]
else
  puts "Usage: #{$0} <student_json_file> <output_json_file>"
  puts "If no argument is given, the default is:"
  puts "\student_json_file = #{studentFileString}"
  puts "\toutput_json_file = #{newStudentFileString}"
  exit(1)
end

studentFile = File.new(studentFileString)
newStudentFile = File.new(newStudentFileString, "w")

while (line = studentFile.gets)
  begin
    student = JSON.parse(line)
  rescue JSON::ParserError => e
    puts "Problem with student file :("
    puts "line is #{line}"
    next
  end
  studentId   = student["_id"]
  assessments = student["studentAssessmentAssociation"]
  schools     = student["studentSchoolAssociation"]
  unless assessments.nil?
    puts "converting student assessment associations for student: #{studentId}"
    assessments.each do |assessment|
       assessment["_id"] = studentId + assessment["_id"]
       assessment["body"]["studentId"] = studentId
    end
  end
  unless schools.nil?
    puts "converting student school associations for student: #{studentId}"
    schools.each do |association|
      association["body"]["studentId"] = studentId
    end
  end
  newStudentFile.puts student.to_json
end

studentFile.close
newStudentFile.close