require "json"

# Usage: ruby superCohortConverter.rb <cohort json file> <studentCohortAssociation json file> <output json file>
# Example: ruby superCohortConverter.rb unified_data/uds_cohort.json unified_data/uds_studentCohortAssociation.json unified_data/out.json

if ARGV.length == 0
  # use default files
elsif ARGV.length == 5
  superDocFileString = ARGV[0]
  subDocFileString = ARGV[1]
  outputString = ARGV[2]
  lookupBy = ARGV[3]
  type = ARGV[4]
else
  puts "Usage: #{$0} <superdoc_json_file> <subdoc_json_file> <output_json_file> <lookupBy> <type>"
  exit(1)
end

superDocFile = File.new(superDocFileString)

output = File.new(outputString, "w")

from = 0.0
to = Time.now

while (line = superDocFile.gets)
  line_hash = JSON.parse(line)
  supderDocId = line_hash["_id"]
  subDocFile = File.new(subDocFileString)
  subdoc_array = [ ]
  while (subdocLine = subDocFile.gets)
    next if subdocLine.strip == ""
    subdocLine_hash = JSON.parse(subdocLine)
    if supderDocId == subdocLine_hash["body"][lookupBy]
      subdoc_array << subdocLine_hash
    end
    if subdoc_array.length > 0
      line_hash[type] = subdoc_array
    end
  end
  subDocFile.close
  output.puts line_hash.to_json
end
superDocFile.close
output.close
