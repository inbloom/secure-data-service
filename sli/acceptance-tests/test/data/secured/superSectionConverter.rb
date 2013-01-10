require "json"

sectionFile = File.new("section.json")

newSsaFile = File.new("studentSectionAssociation_superDoc_fixture.json", "w")
while (line = sectionFile.gets)
line_hash = JSON.parse(line)
sectionId = line_hash["_id"]
#puts sectionId
ssaFile = File.new("studentSectionAssociation.json")
ssa_array = [ ]
while (ssaLine = ssaFile.gets)
ssaLine_hash = JSON.parse(ssaLine)

#puts ssaLine_hash["body"]["sectionId"]
if sectionId == ssaLine_hash["body"]["sectionId"]
#puts sectionId
ssa_array << ssaLine_hash
end
if ssa_array.length > 0
line_hash["studentSectionAssociation"] = ssa_array
puts line_hash
end
end
ssaFile.close
newSsaFile.puts line_hash.to_json
end



sectionFile.close
newSsaFile.close
