require "json"

# Usage: ruby superSectionConverter.rb <section json file> <studentSectionAssociation json file> <output json file>
# Example: ruby superSectionConverter.rb unified_data/uds_section.json unified_data/uds_studentSectionAssociation.json unified_data/out.json

sectionFileString = "section_fixture.json"
ssaFileString = "studentSectionAssociation_fixture.json"
newSsaFileString = "studentSectionAssociation_superDoc_fixture.json"

if ARGV.length == 0
  # use default files
elsif ARGV.length == 3
  sectionFileString = ARGV[0]
  ssaFileString = ARGV[1]
  newSsaFileString = ARGV[2]
else
  puts "Usage: #{$0} <section_json_file> <studentSectionAssociation_json_file> <output_json_file>"
  puts "If no argument is given, the default is:"
  puts "\tsection_json_file = #{sectionFileString}"
  puts "\tstudentSectionAssociation_json_file = #{ssaFileString}"
  puts "\toutput_json_file = #{newSsaFileString}"
  exit(1)
end


# the id of the sub document
EmbeddedDocField = "studentDisciplineIncidentAssociation"

# the field in the secondary entity that has to match the _id of the primary entity 
ResolveReferenceField = "studentId"

sectionFile = File.new(sectionFileString)
newSsaFile = File.new(newSsaFileString, "w")

from = 0.0
to = Time.now

while (line = sectionFile.gets)
  line_hash = JSON.parse(line)
  sectionId = line_hash["_id"]
  #puts sectionId
  ssaFile = File.new(ssaFileString)
  ssa_array = [ ]
  while (ssaLine = ssaFile.gets)
    next if ssaLine.strip == ""
    ssaLine_hash = JSON.parse(ssaLine)

#    ssaLine_hash["body"]["beginDate"] = Time.at(from + rand * (to.to_f - from.to_f)).strftime("%Y-%m-%d")
    #puts ssaLine_hash["body"]["sectionId"]
    if sectionId == ssaLine_hash["body"][ResolveReferenceField]
      #puts sectionId
      ssa_array << ssaLine_hash
    end
    if ssa_array.length > 0
      line_hash[EmbeddedDocField] = ssa_array
      #puts line_hash
      #puts JSON.pretty_generate(line_hash)
    end
  end
  ssaFile.close
  newSsaFile.puts line_hash.to_json
end



sectionFile.close
newSsaFile.close
