require "json"

studentFile = File.new("student_fixture.json")

newSaaFile = File.new("studentAssessment_superDoc_fixture.json", "w")

while (line = studentFile.gets)
  begin
    line_hash = JSON.parse(line)
  rescue JSON::ParserError => e
    puts "Problem with student file :("
    puts "line is #{line}"
    next
  end
  studentId = line_hash["_id"]
#puts studentId
  saaFile = File.new("studentAssessment_fixture.json")
  saa_array = [ ]
  while (saaLine = saaFile.gets)
    saaLine_hash = JSON.parse(saaLine)

    #puts saaLine_hash["body"]["studentId"]
    if studentId == saaLine_hash["body"]["studentId"]
      #puts studentId
      saa_array << saaLine_hash
    end
    if saa_array.length > 0
      line_hash["studentAssessment"] = saa_array
      #puts line_hash
      #puts JSON.pretty_generate(line_hash)
    end
  end
  saaFile.close
  newSaaFile.puts line_hash.to_json
end

studentFile.close
newSaaFile.close