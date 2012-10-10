require "json"

sectionFile = File.new("section_fixture.json")

newSsaFile = File.new("studentSectionAssociation_superDoc_fixture.json", "w")

from = 0.0
to = Time.now

while (line = sectionFile.gets)
  line_hash = JSON.parse(line)
  sectionId = line_hash["_id"]
#puts sectionId
  ssaFile = File.new("studentSectionAssociation_fixture.json")
  ssa_array = [ ]
  while (ssaLine = ssaFile.gets)
    ssaLine_hash = JSON.parse(ssaLine)

    ssaLine_hash["body"]["beginDate"] = Time.at(from + rand * (to.to_f - from.to_f))
    #puts ssaLine_hash["body"]["sectionId"]
    if sectionId == ssaLine_hash["body"]["sectionId"]
      #puts sectionId
      ssa_array << ssaLine_hash
    end
    if ssa_array.length > 0
      line_hash["studentSectionAssociation"] = ssa_array
      #puts line_hash
      puts JSON.pretty_generate(line_hash)
    end
  end
  ssaFile.close
  newSsaFile.puts line_hash.to_json
end



sectionFile.close
newSsaFile.close
